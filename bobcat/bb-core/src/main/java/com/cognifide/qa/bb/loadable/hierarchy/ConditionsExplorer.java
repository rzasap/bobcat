/*
 * Copyright 2016 Cognifide Ltd..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.qa.bb.loadable.hierarchy;

import com.cognifide.qa.bb.loadable.context.ClassFieldContext;
import com.cognifide.qa.bb.loadable.annotation.LoadableComponent;
import com.cognifide.qa.bb.loadable.context.LoadableComponentContext;
import com.cognifide.qa.bb.loadable.hierarchy.util.LoadableComponentsUtil;
import com.cognifide.qa.bb.qualifier.PageObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.qa.bb.utils.AopUtil;

/**
 *
 * This runs after a test class is initialized and explores the hierarchy of {@link PageObject} elements which
 * is needed to control hierarchical evaluation of {@link PageObject} and {@link WebElement} fields annotated
 * with {@link LoadableComponent} annotation.
 */
@Singleton
public class ConditionsExplorer {

  private static final Logger LOG = LoggerFactory.getLogger(ConditionsExplorer.class);

  private final ConditionHierarchyNode treeRootNode = new ConditionHierarchyNode(null);

  /**
   * Discovers the hierarchy of Loadable Conditions form provided class up to the root class which is usually
   * the test class which have run the test.
   *
   * @param directClassFieldContext context of the class field that have called the {@link WebElement} method.
   * @param subjectStack stack of subjects which taken part in invocation of WebElement's method
   * @return Stack of hierarchical conditions from {@link LoadableComponent} annotated fields from the test
   * class down to the "clazz" parameter with "directClassFieldContext" from that class.
   */
  public ConditionStack discoverLoadableContextHierarchy(ClassFieldContext directClassFieldContext,
                                                         LinkedList<Object> subjectStack) {
    Stack<LoadableComponentContext> stack = new Stack<>();
    if (directClassFieldContext != null) {
      directClassFieldContext.toLoadableContextList().stream().
        forEach((context) -> {
          stack.add(context);
        });
    }
    while (!subjectStack.isEmpty()) {

      ConditionHierarchyNode node = findNode(treeRootNode, subjectStack.pollLast());
      if (node != null) {
        stack.addAll(node.getLoadableFieldContext().toLoadableContextList());
      }
    }

    return new ConditionStack(stack);
  }

  /**
   * Builds entire {@link PageObject} field hierarchy starting from the @param injectee parameter.
   */
  public void registerLoadableContextHierarchyTree(Object injectee) {
    Class clazz = injectee.getClass();
    Class normalizedClass = AopUtil.getBaseClassForAopObject(clazz);
    treeRootNode.setLoadableFieldContext(new ClassFieldContext(normalizedClass, Collections.emptyList()));
    processLoadableContextForClass(normalizedClass, treeRootNode, injectee);
  }

  private void processLoadableContextForClass(Class clazz, ConditionHierarchyNode parent, Object injectee) {
    List<Field> declaredFields = Arrays.asList(clazz.getDeclaredFields());
    List<Field> applicableFields = declaredFields.stream()
      .filter(f -> (f.isAnnotationPresent(Inject.class))
        || (f.isAnnotationPresent(FindBy.class) && !f.getType().equals(WebElement.class)))
      .filter(f -> f.getType().isAnnotationPresent(PageObject.class))
      .collect(Collectors.toList());

    applicableFields.stream().
      forEach((field) -> {
        field.setAccessible(true);
        Object subjectInstance = null;
        try {
          subjectInstance = field.get(injectee);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
          LOG.error(ex.getMessage(), ex);
        }
        ConditionHierarchyNode node = addChild(parent, new ClassFieldContext(subjectInstance,
            LoadableComponentsUtil.getConditionsFormField(field)));
        processLoadableContextForClass(field.getType(), node, subjectInstance);
      });
  }

  private ConditionHierarchyNode addChild(ConditionHierarchyNode parent,
    ClassFieldContext loadableContext) {
    ConditionHierarchyNode node = new ConditionHierarchyNode(parent);
    node.setLoadableFieldContext(loadableContext);
    parent.getChildren().add(node);
    return node;
  }

  private ConditionHierarchyNode findNode(ConditionHierarchyNode parent, Object subject) {
    ClassFieldContext loadableFiledContext = parent.getLoadableFieldContext();

    if (loadableFiledContext.getSubject().equals(subject)) {
      return parent;
    } else {
      for (ConditionHierarchyNode node : parent.getChildren()) {
        ConditionHierarchyNode result = findNode(node, subject);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }
}

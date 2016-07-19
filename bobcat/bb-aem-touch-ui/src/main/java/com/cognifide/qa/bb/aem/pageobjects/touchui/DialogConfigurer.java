package com.cognifide.qa.bb.aem.pageobjects.touchui;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.cognifide.qa.bb.utils.PageObjectInjector;
import com.cognifide.qa.bb.aem.data.componentconfigs.FieldType;
import com.cognifide.qa.bb.aem.data.componentconfigs.FieldTypeRegistry;
import com.cognifide.qa.bb.aem.pageobjects.touchui.dialogfields.DialogField;
import com.google.inject.Inject;

public class DialogConfigurer {

  public static final By FIELD_LOCATOR = By.cssSelector(".coral-Form-fieldwrapper");

  public static final By LABEL_SELECTOR = By
      .cssSelector("label.coral-Form-fieldlabel, label.coral-Form-field");

  @Inject
  private FieldTypeRegistry fieldTypeRegistry;

  @Inject
  private PageObjectInjector pageObjectInjector;

  /**
   * Finds the dialog field of given type within a WebElement based on the provided label. If label is not
   * present, returns the first field from the tab.
   *
   * @param parentElement parent element from which DialogField will be retrieved
   * @param label         of the requested field
   * @param type          of the requested field
   * @return DialogField of the given type based on the provided info
   */
  public DialogField getDialogField(WebElement parentElement, String label, FieldType type) {
    List<WebElement> fields = parentElement.findElements(FIELD_LOCATOR);

    if (fields.isEmpty()) {
      throw new IllegalStateException("There are no fields in the tab");
    }

    WebElement scope = StringUtils.isEmpty(label) ? fields.get(0) : fields.stream() //
        .filter(field -> containsIgnoreCase(getFieldLabel(field), label)) //
        .findFirst() //
            //				.orElse(fields.get(0));
        .orElseThrow(() -> new IllegalStateException("Dialog field not found"));

    return getFieldObject(scope, type);
  }

  /**
   * Returns the label of given field.
   * Label may not be present in the field, thus a workaround using list is introduced here.
   *
   * @param field WebElement corresponding to the given field
   * @return label of the field or {@code StringUtils.Empty} when there is none
   */
  private String getFieldLabel(WebElement field) {
    List<WebElement> labelField = field.findElements(LABEL_SELECTOR);
    return labelField.isEmpty() ? StringUtils.EMPTY : labelField.get(0).getText();
  }

  private DialogField getFieldObject(WebElement scope, FieldType type) {
    Class clazz = fieldTypeRegistry.getClass(type);
    return (DialogField) pageObjectInjector.inject(clazz, scope);
  }
}
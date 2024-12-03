/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.support;

import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.HtmlUtils;

public class BindStatus {
    private final RequestContext requestContext;
    private final String path;
    private final boolean htmlEscape;
    @Nullable
    private final String expression;
    @Nullable
    private final Errors errors;
    private final String[] errorCodes;
    @Nullable
    private String[] errorMessages;
    @Nullable
    private List<? extends ObjectError> objectErrors;
    @Nullable
    private Object value;
    @Nullable
    private Class<?> valueType;
    @Nullable
    private Object actualValue;
    @Nullable
    private PropertyEditor editor;
    @Nullable
    private BindingResult bindingResult;

    public BindStatus(RequestContext requestContext, String path, boolean htmlEscape) throws IllegalStateException {
        String beanName;
        this.requestContext = requestContext;
        this.path = path;
        this.htmlEscape = htmlEscape;
        int dotPos = path.indexOf(46);
        if (dotPos == -1) {
            beanName = path;
            this.expression = null;
        } else {
            beanName = path.substring(0, dotPos);
            this.expression = path.substring(dotPos + 1);
        }
        this.errors = requestContext.getErrors(beanName, false);
        if (this.errors != null) {
            if (this.expression != null) {
                if ("*".equals(this.expression)) {
                    this.objectErrors = this.errors.getAllErrors();
                } else if (this.expression.endsWith("*")) {
                    this.objectErrors = this.errors.getFieldErrors(this.expression);
                } else {
                    this.objectErrors = this.errors.getFieldErrors(this.expression);
                    this.value = this.errors.getFieldValue(this.expression);
                    this.valueType = this.errors.getFieldType(this.expression);
                    if (this.errors instanceof BindingResult) {
                        this.bindingResult = (BindingResult)this.errors;
                        this.actualValue = this.bindingResult.getRawFieldValue(this.expression);
                        this.editor = this.bindingResult.findEditor(this.expression, null);
                    } else {
                        this.actualValue = this.value;
                    }
                }
            } else {
                this.objectErrors = this.errors.getGlobalErrors();
            }
            this.errorCodes = BindStatus.initErrorCodes(this.objectErrors);
        } else {
            Object target = requestContext.getModelObject(beanName);
            if (target == null) {
                throw new IllegalStateException("Neither BindingResult nor plain target object for bean name '" + beanName + "' available as request attribute");
            }
            if (this.expression != null && !"*".equals(this.expression) && !this.expression.endsWith("*")) {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(target);
                this.value = bw.getPropertyValue(this.expression);
                this.valueType = bw.getPropertyType(this.expression);
                this.actualValue = this.value;
            }
            this.errorCodes = new String[0];
            this.errorMessages = new String[0];
        }
        if (htmlEscape && this.value instanceof String) {
            this.value = HtmlUtils.htmlEscape((String)this.value);
        }
    }

    private static String[] initErrorCodes(List<? extends ObjectError> objectErrors) {
        String[] errorCodes = new String[objectErrors.size()];
        for (int i2 = 0; i2 < objectErrors.size(); ++i2) {
            ObjectError error = objectErrors.get(i2);
            errorCodes[i2] = error.getCode();
        }
        return errorCodes;
    }

    public String getPath() {
        return this.path;
    }

    @Nullable
    public String getExpression() {
        return this.expression;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    @Nullable
    public Class<?> getValueType() {
        return this.valueType;
    }

    @Nullable
    public Object getActualValue() {
        return this.actualValue;
    }

    public String getDisplayValue() {
        if (this.value instanceof String) {
            return (String)this.value;
        }
        if (this.value != null) {
            return this.htmlEscape ? HtmlUtils.htmlEscape(this.value.toString()) : this.value.toString();
        }
        return "";
    }

    public boolean isError() {
        return this.errorCodes.length > 0;
    }

    public String[] getErrorCodes() {
        return this.errorCodes;
    }

    public String getErrorCode() {
        return this.errorCodes.length > 0 ? this.errorCodes[0] : "";
    }

    public String[] getErrorMessages() {
        return this.initErrorMessages();
    }

    public String getErrorMessage() {
        String[] errorMessages = this.initErrorMessages();
        return errorMessages.length > 0 ? errorMessages[0] : "";
    }

    public String getErrorMessagesAsString(String delimiter) {
        return StringUtils.arrayToDelimitedString(this.initErrorMessages(), delimiter);
    }

    private String[] initErrorMessages() throws NoSuchMessageException {
        if (this.errorMessages == null) {
            if (this.objectErrors != null) {
                this.errorMessages = new String[this.objectErrors.size()];
                for (int i2 = 0; i2 < this.objectErrors.size(); ++i2) {
                    ObjectError error = this.objectErrors.get(i2);
                    this.errorMessages[i2] = this.requestContext.getMessage(error, this.htmlEscape);
                }
            } else {
                this.errorMessages = new String[0];
            }
        }
        return this.errorMessages;
    }

    @Nullable
    public Errors getErrors() {
        return this.errors;
    }

    @Nullable
    public PropertyEditor getEditor() {
        return this.editor;
    }

    @Nullable
    public PropertyEditor findEditor(Class<?> valueClass) {
        return this.bindingResult != null ? this.bindingResult.findEditor(this.expression, valueClass) : null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("BindStatus: ");
        sb.append("expression=[").append(this.expression).append("]; ");
        sb.append("value=[").append(this.value).append(']');
        if (!ObjectUtils.isEmpty(this.errorCodes)) {
            sb.append("; errorCodes=").append(Arrays.asList(this.errorCodes));
        }
        return sb.toString();
    }
}


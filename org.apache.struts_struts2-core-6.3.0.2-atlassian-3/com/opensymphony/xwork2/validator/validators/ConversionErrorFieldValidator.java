/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.RepopulateConversionErrorFieldValidatorSupport;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ConversionErrorFieldValidator
extends RepopulateConversionErrorFieldValidatorSupport {
    @Override
    public void doValidate(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        String fullFieldName = this.getValidatorContext().getFullFieldName(fieldName);
        ActionContext context = ActionContext.getContext();
        Map<String, ConversionData> conversionErrors = context.getConversionErrors();
        if (conversionErrors.containsKey(fullFieldName)) {
            if (StringUtils.isBlank((CharSequence)this.defaultMessage)) {
                this.defaultMessage = XWorkConverter.getConversionErrorMessage(fullFieldName, conversionErrors.get(fullFieldName).getToClass(), context.getValueStack());
            }
            this.addFieldError(fieldName, object);
        }
    }
}


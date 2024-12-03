/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RepopulateConversionErrorFieldValidatorSupport
extends FieldValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(RepopulateConversionErrorFieldValidatorSupport.class);
    private boolean repopulateField = false;

    public boolean isRepopulateField() {
        return this.repopulateField;
    }

    public void setRepopulateField(boolean repopulateField) {
        this.repopulateField = repopulateField;
    }

    @Override
    public void validate(Object object) throws ValidationException {
        this.doValidate(object);
        if (this.repopulateField) {
            this.repopulateField(object);
        }
    }

    public void repopulateField(Object object) throws ValidationException {
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        Map<String, ConversionData> conversionErrors = ActionContext.getContext().getConversionErrors();
        String fieldName = this.getFieldName();
        String fullFieldName = this.getValidatorContext().getFullFieldName(fieldName);
        if (conversionErrors.containsKey(fullFieldName)) {
            Object value = conversionErrors.get(fullFieldName).getValue();
            final LinkedHashMap<String, String> fakeParams = new LinkedHashMap<String, String>();
            boolean doExprOverride = false;
            if (value instanceof String[]) {
                String[] tmpValue = (String[])value;
                if (tmpValue.length > 0) {
                    doExprOverride = true;
                    fakeParams.put(fullFieldName, this.escape(tmpValue[0]));
                } else {
                    LOG.warn("value is an empty array of String or with first element in it as null [{}], will not repopulate conversion error", value);
                }
            } else if (value instanceof String) {
                String tmpValue = (String)value;
                doExprOverride = true;
                fakeParams.put(fullFieldName, this.escape(tmpValue));
            } else {
                LOG.warn("conversion error value is not a String or array of String but instead is [{}], will not repopulate conversion error", value);
            }
            if (doExprOverride) {
                invocation.addPreResultListener(new PreResultListener(){

                    @Override
                    public void beforeResult(ActionInvocation invocation, String resultCode) {
                        ValueStack stack = ActionContext.getContext().getValueStack();
                        stack.setExprOverrides(fakeParams);
                    }
                });
            }
        }
    }

    protected String escape(String value) {
        return "\"" + StringEscapeUtils.escapeJava((String)value) + "\"";
    }

    protected abstract void doValidate(Object var1) throws ValidationException;
}


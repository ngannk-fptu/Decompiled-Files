/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.validator.ValidationException
 *  com.opensymphony.xwork2.validator.validators.ValidatorSupport
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.misc;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;
import org.apache.commons.lang3.StringUtils;

public class AlternateRequiredStringValidator
extends ValidatorSupport {
    private String fieldName1;
    private String fieldName2;

    public void validate(Object object) throws ValidationException {
        Object value1 = this.getFieldValue(this.getFieldName1(), object);
        Object value2 = this.getFieldValue(this.getFieldName2(), object);
        if (this.checkString(value1) && this.checkString(value2) || !this.checkString(value1) && !this.checkString(value2)) {
            this.addActionError(object);
        }
    }

    private boolean checkString(Object value) {
        return !(value instanceof String) || !StringUtils.isNotEmpty((CharSequence)((String)value));
    }

    public String getFieldName1() {
        return this.fieldName1;
    }

    public void setFieldName1(String fieldName1) {
        this.fieldName1 = fieldName1;
    }

    public String getFieldName2() {
        return this.fieldName2;
    }

    public void setFieldName2(String fieldName2) {
        this.fieldName2 = fieldName2;
    }
}


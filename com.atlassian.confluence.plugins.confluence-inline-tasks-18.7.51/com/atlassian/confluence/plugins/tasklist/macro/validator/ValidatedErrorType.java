/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.tasklist.macro.validator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ValidatedErrorType {
    private String fieldNameCode;
    private String messageCode;
    private String[] params;

    public ValidatedErrorType(String fieldNameCode, String messageCode) {
        this(fieldNameCode, messageCode, new String[0]);
    }

    public ValidatedErrorType(String fieldNameCode, String messageCode, String[] params) {
        this.fieldNameCode = fieldNameCode;
        this.messageCode = messageCode;
        this.params = params;
    }

    public String getFieldNameCode() {
        return this.fieldNameCode;
    }

    public void setFieldNameCode(String fieldNameCode) {
        this.fieldNameCode = fieldNameCode;
    }

    public String getMessageCode() {
        return this.messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String[] getParams() {
        return this.params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}


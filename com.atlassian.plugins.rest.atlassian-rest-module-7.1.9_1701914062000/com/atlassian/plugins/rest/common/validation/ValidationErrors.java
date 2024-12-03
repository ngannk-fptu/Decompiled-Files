/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.rest.common.validation;

import com.atlassian.plugins.rest.common.validation.ValidationError;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType
public class ValidationErrors {
    private List<ValidationError> errors = new ArrayList<ValidationError>();

    public List<ValidationError> getErrors() {
        return this.errors;
    }

    public void addError(ValidationError error) {
        this.errors.add(error);
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}


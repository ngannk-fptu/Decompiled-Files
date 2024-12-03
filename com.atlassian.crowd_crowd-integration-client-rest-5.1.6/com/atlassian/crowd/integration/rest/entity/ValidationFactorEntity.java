/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.ValidationFactor
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.model.authentication.ValidationFactor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="validation-factor")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ValidationFactorEntity {
    @XmlElement(name="name")
    private final String name;
    @XmlElement(name="value")
    private final String value;

    private ValidationFactorEntity() {
        this.name = null;
        this.value = null;
    }

    public ValidationFactorEntity(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public static ValidationFactorEntity newInstance(ValidationFactor validationFactor) {
        return new ValidationFactorEntity(validationFactor.getName(), validationFactor.getValue());
    }
}


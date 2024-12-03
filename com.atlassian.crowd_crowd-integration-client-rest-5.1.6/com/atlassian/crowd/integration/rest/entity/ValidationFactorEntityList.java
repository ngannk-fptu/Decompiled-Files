/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.ValidationFactor
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.ValidationFactorEntity;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="validation-factors")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ValidationFactorEntityList {
    @XmlElements(value={@XmlElement(name="validation-factor", type=ValidationFactorEntity.class)})
    private final List<ValidationFactorEntity> validationFactors;

    private ValidationFactorEntityList() {
        this.validationFactors = new ArrayList<ValidationFactorEntity>();
    }

    public ValidationFactorEntityList(List<ValidationFactorEntity> validationFactors) {
        this.validationFactors = validationFactors;
    }

    public List<ValidationFactorEntity> getValidationFactors() {
        return this.validationFactors;
    }

    public static ValidationFactorEntityList newInstance(List<ValidationFactor> validationFactors) {
        ArrayList<ValidationFactorEntity> validationFactorEntities = new ArrayList<ValidationFactorEntity>();
        for (ValidationFactor vf : validationFactors) {
            validationFactorEntities.add(ValidationFactorEntity.newInstance(vf));
        }
        return new ValidationFactorEntityList(validationFactorEntities);
    }
}


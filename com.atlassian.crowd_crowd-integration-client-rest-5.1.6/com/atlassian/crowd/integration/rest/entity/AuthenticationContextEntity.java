/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.UserAuthenticationContext
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.ValidationFactorEntity;
import com.atlassian.crowd.integration.rest.entity.ValidationFactorEntityList;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="authentication-context")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class AuthenticationContextEntity {
    @XmlElement(name="username")
    private final String username;
    @XmlElement(name="password")
    private final String password;
    @XmlElement(name="validation-factors")
    private final ValidationFactorEntityList validationFactors;

    private AuthenticationContextEntity() {
        this.username = null;
        this.password = null;
        this.validationFactors = null;
    }

    public AuthenticationContextEntity(String name, String password, ValidationFactorEntityList validationFactors) {
        this.username = name;
        this.password = password;
        this.validationFactors = validationFactors;
    }

    public String getUserName() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public List<ValidationFactorEntity> getValidationFactors() {
        if (this.validationFactors != null) {
            return this.validationFactors.getValidationFactors();
        }
        return Collections.emptyList();
    }

    public static AuthenticationContextEntity newInstance(UserAuthenticationContext uac) {
        ValidationFactorEntityList validationFactorEntityList = ValidationFactorEntityList.newInstance(Arrays.asList(uac.getValidationFactors()));
        if (uac.getCredential() != null) {
            return new AuthenticationContextEntity(uac.getName(), uac.getCredential().getCredential(), validationFactorEntityList);
        }
        return new AuthenticationContextEntity(uac.getName(), null, validationFactorEntityList);
    }
}


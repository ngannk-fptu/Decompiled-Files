/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.LoginOption;
import java.util.Objects;

public class IdpLoginOption
extends LoginOption {
    private final long id;
    private final String buttonText;
    private final String loginLink;

    public IdpLoginOption(long id, String buttonText, String loginLink) {
        super(LoginOption.Type.IDP);
        this.id = id;
        this.buttonText = buttonText;
        this.loginLink = loginLink;
    }

    public IdpLoginOption(IdpConfig idpConfig, String loginLinkBaseUrl) {
        this(idpConfig.getId(), idpConfig.getButtonText(), loginLinkBaseUrl + "/" + idpConfig.getId());
    }

    public long getId() {
        return this.id;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public String getLoginLink() {
        return this.loginLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        IdpLoginOption that = (IdpLoginOption)o;
        return this.id == that.id && Objects.equals(this.buttonText, that.buttonText) && Objects.equals(this.loginLink, that.loginLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.id, this.buttonText, this.loginLink);
    }
}


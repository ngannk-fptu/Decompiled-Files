/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import com.atlassian.plugins.authentication.api.config.IdpLoginOption;
import com.atlassian.plugins.authentication.api.config.LoginOption;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoginOptionEntity {
    @JsonProperty(value="type")
    private LoginOption.Type type;
    @JsonProperty(value="id")
    private long id;
    @JsonProperty(value="button-text")
    private String buttonText;
    @JsonProperty(value="login-link")
    private String loginLink;

    public LoginOptionEntity() {
    }

    public LoginOptionEntity(LoginOption loginOption) {
        this.type = loginOption.getType();
        if (loginOption.getType() == LoginOption.Type.IDP) {
            IdpLoginOption idpLoginOption = (IdpLoginOption)loginOption;
            this.id = idpLoginOption.getId();
            this.buttonText = idpLoginOption.getButtonText();
            this.loginLink = idpLoginOption.getLoginLink();
        }
    }

    public LoginOption.Type getType() {
        return this.type;
    }

    public void setType(LoginOption.Type type) {
        this.type = type;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getLoginLink() {
        return this.loginLink;
    }

    public void setLoginLink(String loginLink) {
        this.loginLink = loginLink;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LoginOptionEntity that = (LoginOptionEntity)o;
        return this.id == that.id && this.type == that.type && Objects.equals(this.buttonText, that.buttonText) && Objects.equals(this.loginLink, that.loginLink);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.id, this.buttonText, this.loginLink});
    }

    public String toString() {
        return "LoginOptionEntity{type=" + (Object)((Object)this.type) + ", id=" + this.id + ", buttonText='" + this.buttonText + '\'' + ", loginLink='" + this.loginLink + '\'' + '}';
    }

    public static interface Fields {
        public static final String TYPE = "type";
        public static final String LAST_UPDATED = "last-updated";

        public static interface IdpSpecific {
            public static final String ID = "id";
            public static final String BUTTON_TEXT = "button-text";
            public static final String LOGIN_LINK = "login-link";
        }
    }
}


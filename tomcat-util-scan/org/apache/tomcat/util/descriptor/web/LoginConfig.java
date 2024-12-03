/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UDecoder
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.XmlEncodingBase;

public class LoginConfig
extends XmlEncodingBase
implements Serializable {
    private static final long serialVersionUID = 2L;
    private String authMethod = null;
    private String errorPage = null;
    private String loginPage = null;
    private String realmName = null;

    public LoginConfig() {
    }

    public LoginConfig(String authMethod, String realmName, String loginPage, String errorPage) {
        this.setAuthMethod(authMethod);
        this.setRealmName(realmName);
        this.setLoginPage(loginPage);
        this.setErrorPage(errorPage);
    }

    public String getAuthMethod() {
        return this.authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public String getErrorPage() {
        return this.errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = UDecoder.URLDecode((String)errorPage, (Charset)this.getCharset());
    }

    public String getLoginPage() {
        return this.loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = UDecoder.URLDecode((String)loginPage, (Charset)this.getCharset());
    }

    public String getRealmName() {
        return this.realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("LoginConfig[");
        sb.append("authMethod=");
        sb.append(this.authMethod);
        if (this.realmName != null) {
            sb.append(", realmName=");
            sb.append(this.realmName);
        }
        if (this.loginPage != null) {
            sb.append(", loginPage=");
            sb.append(this.loginPage);
        }
        if (this.errorPage != null) {
            sb.append(", errorPage=");
            sb.append(this.errorPage);
        }
        sb.append(']');
        return sb.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.authMethod == null ? 0 : this.authMethod.hashCode());
        result = 31 * result + (this.errorPage == null ? 0 : this.errorPage.hashCode());
        result = 31 * result + (this.loginPage == null ? 0 : this.loginPage.hashCode());
        result = 31 * result + (this.realmName == null ? 0 : this.realmName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LoginConfig)) {
            return false;
        }
        LoginConfig other = (LoginConfig)obj;
        if (this.authMethod == null ? other.authMethod != null : !this.authMethod.equals(other.authMethod)) {
            return false;
        }
        if (this.errorPage == null ? other.errorPage != null : !this.errorPage.equals(other.errorPage)) {
            return false;
        }
        if (this.loginPage == null ? other.loginPage != null : !this.loginPage.equals(other.loginPage)) {
            return false;
        }
        return !(this.realmName == null ? other.realmName != null : !this.realmName.equals(other.realmName));
    }
}


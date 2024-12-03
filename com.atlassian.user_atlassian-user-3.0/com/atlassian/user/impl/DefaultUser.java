/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultEntity;

public class DefaultUser
extends DefaultEntity
implements User {
    protected String fullName;
    protected String email;
    protected String password;

    public DefaultUser() {
    }

    public DefaultUser(String name) {
        super(name);
    }

    public DefaultUser(String name, String fullName, String email) {
        super(name);
        this.fullName = fullName;
        this.email = email;
    }

    public DefaultUser(User user) {
        this(user.getName(), user.getFullName(), user.getEmail());
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String passw) {
        this.password = passw;
    }

    public String toString() {
        return this.name;
    }

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
        DefaultUser that = (DefaultUser)o;
        if (this.email != null ? !this.email.equals(that.email) : that.email != null) {
            return false;
        }
        if (this.fullName != null ? !this.fullName.equals(that.fullName) : that.fullName != null) {
            return false;
        }
        return !(this.password != null ? !this.password.equals(that.password) : that.password != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.fullName != null ? this.fullName.hashCode() : 0);
        result = 29 * result + (this.email != null ? this.email.hashCode() : 0);
        result = 29 * result + (this.password != null ? this.password.hashCode() : 0);
        return result;
    }
}


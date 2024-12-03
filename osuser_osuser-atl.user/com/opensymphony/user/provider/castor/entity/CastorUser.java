/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.castor.entity;

import com.opensymphony.user.provider.castor.entity.BaseCastorEntity;
import com.opensymphony.user.provider.ejb.util.Base64;
import com.opensymphony.user.provider.ejb.util.PasswordDigester;
import java.util.ArrayList;

public class CastorUser
extends BaseCastorEntity {
    private ArrayList groups = null;
    private String email = null;
    private String fullName = null;
    private String passwordHash = null;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setGroups(ArrayList groups) {
        if (groups != null) {
            this.groups = new ArrayList(groups);
        }
    }

    public ArrayList getGroups() {
        return this.groups;
    }

    public void setPassword(String password) {
        this.setPasswordHash(this.createHash(password));
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public boolean authenticate(String password) {
        if (password == null || this.getPasswordHash() == null || password.length() == 0) {
            return false;
        }
        return this.compareHash(this.getPasswordHash(), password);
    }

    private boolean compareHash(String hashedValue, String unhashedValue) {
        return hashedValue.equals(this.createHash(unhashedValue));
    }

    private String createHash(String original) {
        byte[] digested = PasswordDigester.digest(original.getBytes());
        byte[] encoded = Base64.encode(digested);
        return new String(encoded);
    }
}


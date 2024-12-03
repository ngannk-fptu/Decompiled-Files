/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.Entity
 *  com.opensymphony.user.ImmutableException
 *  com.opensymphony.user.User
 *  org.apache.log4j.Category
 */
package com.atlassian.user.impl.osuser;

import com.atlassian.user.User;
import com.atlassian.user.impl.osuser.OSUEntity;
import com.opensymphony.user.Entity;
import com.opensymphony.user.ImmutableException;
import org.apache.log4j.Category;

public class OSUUser
extends OSUEntity
implements User {
    private static final Category log = Category.getInstance(OSUUser.class);
    protected com.opensymphony.user.User osuser;

    public OSUUser(com.opensymphony.user.User osuser) {
        super((Entity)osuser);
        this.osuser = osuser;
    }

    public String getFullName() {
        try {
            return this.osuser.getFullName();
        }
        catch (NullPointerException e) {
            log.debug((Object)("No email address found for user with name [" + this.getName() + "]"));
            return null;
        }
    }

    public String getEmail() {
        try {
            return this.osuser.getEmail();
        }
        catch (NullPointerException e) {
            log.debug((Object)("No email address found for user with name [" + this.getName() + "]"));
            return null;
        }
    }

    public String toString() {
        StringBuffer sf = new StringBuffer();
        sf = sf.append("user: [").append(this.getName()).append("]\n");
        sf = sf.append("email: [").append(this.getEmail()).append("]\n");
        sf = sf.append("fullName: [").append(this.getFullName()).append("]\n");
        return sf.toString();
    }

    public void setEmail(String email) {
        this.osuser.setEmail(email);
    }

    public void setFullName(String fullName) {
        this.osuser.setFullName(fullName);
    }

    public void setPassword(String password) {
        try {
            this.osuser.setPassword(password);
        }
        catch (ImmutableException e) {
            log.error((Object)"Could not alter password: ", (Throwable)e);
            throw new IllegalStateException("Could not alter password: " + (Object)((Object)e));
        }
    }
}


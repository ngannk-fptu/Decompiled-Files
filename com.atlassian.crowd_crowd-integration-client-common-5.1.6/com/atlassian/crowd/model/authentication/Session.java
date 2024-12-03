/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.authentication;

import java.security.Principal;
import java.util.Date;

public interface Session {
    public String getToken();

    public Date getCreatedDate();

    public Date getExpiryDate();

    public Principal getUser();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user;

import com.atlassian.user.Entity;
import java.security.Principal;

public interface User
extends Entity,
Principal {
    public String getFullName();

    public String getEmail();
}


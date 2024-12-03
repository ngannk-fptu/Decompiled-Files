/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.model.DirectoryEntity;
import java.security.Principal;

public interface User
extends Principal,
DirectoryEntity,
com.atlassian.crowd.embedded.api.User {
    public String getFirstName();

    public String getLastName();

    public String getExternalId();
}


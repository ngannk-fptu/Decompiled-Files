/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.Entity
 *  com.opensymphony.user.Group
 */
package com.atlassian.user.impl.osuser;

import com.atlassian.user.Group;
import com.atlassian.user.impl.osuser.OSUEntity;
import com.opensymphony.user.Entity;

public class OSUGroup
extends OSUEntity
implements Group {
    com.opensymphony.user.Group osgroup;

    public OSUGroup(com.opensymphony.user.Group osgroup) {
        super((Entity)osgroup);
        this.osgroup = osgroup;
    }
}


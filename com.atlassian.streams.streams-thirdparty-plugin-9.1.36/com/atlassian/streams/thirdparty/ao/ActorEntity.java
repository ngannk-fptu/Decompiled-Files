/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 */
package com.atlassian.streams.thirdparty.ao;

import java.net.URI;
import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface ActorEntity
extends Entity {
    public String getUsername();

    public void setUsername(String var1);

    public String getFullName();

    public void setFullName(String var1);

    public URI getProfilePageUri();

    public void setProfilePageUri(URI var1);

    public URI getProfilePictureUri();

    public void setProfilePictureUri(URI var1);
}


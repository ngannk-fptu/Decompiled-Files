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
public interface MediaLinkEntity
extends Entity {
    public Integer getDuration();

    public void setDuration(Integer var1);

    public Integer getHeight();

    public void setHeight(Integer var1);

    public URI getUrl();

    public void setUrl(URI var1);

    public Integer getWidth();

    public void setWidth(Integer var1);
}


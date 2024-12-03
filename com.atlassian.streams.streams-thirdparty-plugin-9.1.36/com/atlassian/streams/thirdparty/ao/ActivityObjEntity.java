/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Polymorphic
 */
package com.atlassian.streams.thirdparty.ao;

import com.atlassian.streams.thirdparty.ao.MediaLinkEntity;
import java.net.URI;
import net.java.ao.Entity;
import net.java.ao.Polymorphic;

@Polymorphic
public interface ActivityObjEntity
extends Entity {
    public URI getObjectId();

    public void setObjectId(URI var1);

    public String getContent();

    public void setContent(String var1);

    public String getDisplayName();

    public void setDisplayName(String var1);

    public MediaLinkEntity getImage();

    public void setImage(MediaLinkEntity var1);

    public URI getObjectType();

    public void setObjectType(URI var1);

    public String getSummary();

    public void setSummary(String var1);

    public URI getUrl();

    public void setUrl(URI var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tapestry.Tapestry
 */
package com.opensymphony.module.sitemesh.tapestry;

import com.opensymphony.module.sitemesh.tapestry.SiteMeshBase;
import org.apache.tapestry.Tapestry;

public abstract class Title
extends SiteMeshBase {
    public abstract String getDefault();

    public String getTitle() {
        String title = this.getSiteMeshPage().getTitle();
        return Tapestry.isBlank((String)title) ? this.getDefault() : title;
    }
}


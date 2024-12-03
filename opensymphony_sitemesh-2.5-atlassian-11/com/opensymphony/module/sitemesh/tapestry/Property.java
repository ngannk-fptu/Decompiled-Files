/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tapestry.Tapestry
 */
package com.opensymphony.module.sitemesh.tapestry;

import com.opensymphony.module.sitemesh.tapestry.SiteMeshBase;
import org.apache.tapestry.Tapestry;

public abstract class Property
extends SiteMeshBase {
    public abstract String getProperty();

    public abstract String getDefault();

    public String getValue() {
        String propertyName = this.getProperty();
        String propertyValue = this.getSiteMeshPage().getProperty(propertyName);
        if (Tapestry.isBlank((String)propertyValue)) {
            propertyValue = this.getDefault();
        }
        return propertyValue;
    }
}


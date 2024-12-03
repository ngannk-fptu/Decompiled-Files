/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tapestry.BaseComponent
 */
package com.opensymphony.module.sitemesh.tapestry;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.tapestry.Util;
import org.apache.tapestry.BaseComponent;

public class SiteMeshBase
extends BaseComponent {
    public Page getSiteMeshPage() {
        return Util.getPage(this.getPage().getRequestCycle());
    }
}


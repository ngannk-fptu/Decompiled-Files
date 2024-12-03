/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.taglib.decorator.PropertyTag;

public class ExtractPropertyTag
extends PropertyTag {
    public int doEndTag() {
        Page page = this.getPage();
        page.addProperty("_sitemesh.removefrompage." + this.getProperty(), "true");
        return super.doEndTag();
    }
}


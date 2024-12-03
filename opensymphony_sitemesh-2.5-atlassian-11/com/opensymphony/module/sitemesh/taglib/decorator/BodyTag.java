/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.taglib.AbstractTag;

public class BodyTag
extends AbstractTag {
    public final int doEndTag() {
        try {
            this.getPage().writeBody(this.getOut());
        }
        catch (Exception e) {
            BodyTag.trace(e);
        }
        return 6;
    }
}


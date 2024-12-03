/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagData
 *  javax.servlet.jsp.tagext.TagExtraInfo
 *  javax.servlet.jsp.tagext.VariableInfo
 */
package com.opensymphony.module.sitemesh.taglib.decorator;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class UsePageTEI
extends TagExtraInfo {
    protected String getType() {
        return "com.opensymphony.module.sitemesh.Page";
    }

    public VariableInfo[] getVariableInfo(TagData data) {
        String id = data.getAttributeString("id");
        return new VariableInfo[]{new VariableInfo(id, this.getType(), true, 2)};
    }
}


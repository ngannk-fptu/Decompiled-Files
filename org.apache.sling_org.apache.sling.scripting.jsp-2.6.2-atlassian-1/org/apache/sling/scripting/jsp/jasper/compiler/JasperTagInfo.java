/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.TagAttributeInfo
 *  javax.servlet.jsp.tagext.TagExtraInfo
 *  javax.servlet.jsp.tagext.TagInfo
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  javax.servlet.jsp.tagext.TagVariableInfo
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;

class JasperTagInfo
extends TagInfo {
    private String dynamicAttrsMapName;

    public JasperTagInfo(String tagName, String tagClassName, String bodyContent, String infoString, TagLibraryInfo taglib, TagExtraInfo tagExtraInfo, TagAttributeInfo[] attributeInfo, String displayName, String smallIcon, String largeIcon, TagVariableInfo[] tvi, String mapName) {
        super(tagName, tagClassName, bodyContent, infoString, taglib, tagExtraInfo, attributeInfo, displayName, smallIcon, largeIcon, tvi);
        this.dynamicAttrsMapName = mapName;
    }

    public String getDynamicAttributesMapName() {
        return this.dynamicAttrsMapName;
    }

    public boolean hasDynamicAttributes() {
        return this.dynamicAttrsMapName != null;
    }
}


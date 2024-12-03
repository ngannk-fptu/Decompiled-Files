/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class CustomStylesheetAction
extends ConfluenceActionSupport {
    private String spaceKey;
    private String style;
    private StylesheetManager stylesheetManager;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.style = this.stylesheetManager.getSpaceStylesheet(this.spaceKey);
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStylesheetManager(StylesheetManager stylesheetManager) {
        this.stylesheetManager = stylesheetManager;
    }
}


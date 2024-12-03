/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.themes.ColorSchemeBean;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.util.ColourUtils;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;

public class ColorsStylesheetAction
extends ConfluenceActionSupport {
    private ColorSchemeBean colorScheme;
    private ColourSchemeManager colourSchemeManager;
    private ColourUtils colourUtils = new ColourUtils();
    private String spaceKey;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.colorScheme = new ColorSchemeBean(this.getActiveColourScheme(this.spaceKey));
        return "success";
    }

    private ColourScheme getActiveColourScheme(String spaceKey) {
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            return this.colourSchemeManager.getSpaceColourScheme(spaceKey);
        }
        return this.colourSchemeManager.getGlobalColourScheme();
    }

    public ColorSchemeBean getColourScheme() {
        return this.colorScheme;
    }

    public ColorSchemeBean getColorScheme() {
        return this.colorScheme;
    }

    public void setColourSchemeManager(ColourSchemeManager colourSchemeManager) {
        this.colourSchemeManager = colourSchemeManager;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public ColourUtils getColourUtils() {
        return this.colourUtils;
    }
}


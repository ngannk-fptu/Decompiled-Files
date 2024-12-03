/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class ColourPickerAction
extends ConfluenceActionSupport {
    private static final String[] hex = new String[]{"00", "33", "66", "99", "cc", "ff"};
    private static final String[] bigHex = new String[]{"000000", "111111", "222222", "333333", "444444", "555555", "666666", "777777", "888888", "999999", "aaaaaa", "bbbbbb", "cccccc", "dddddd", "eeeeee", "ffffff"};
    private String colourKey;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public String getColourKey() {
        return this.colourKey;
    }

    public void setColourKey(String colourKey) {
        this.colourKey = colourKey;
    }

    public String[] getHex() {
        return hex;
    }

    public String[] getBigHex() {
        return bigHex;
    }

    public int getTableWidth() {
        return hex.length * hex.length;
    }
}


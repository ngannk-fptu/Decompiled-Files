/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class AdvancedSearchTipsAction
extends ConfluenceActionSupport {
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }
}


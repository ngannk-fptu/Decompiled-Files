/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class OpenSearchDescriptorAction
extends ConfluenceActionSupport {
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (!this.settingsManager.getGlobalSettings().isEnableOpenSearch()) {
            return "notfound";
        }
        return "success";
    }
}


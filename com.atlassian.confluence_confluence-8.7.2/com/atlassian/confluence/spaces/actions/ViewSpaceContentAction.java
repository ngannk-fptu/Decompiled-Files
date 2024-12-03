/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class ViewSpaceContentAction
extends AbstractSpaceAction {
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BasePermissionCondition;

public class HasViewProfilePermission
extends BasePermissionCondition {
    @Override
    protected Object getPermissionTarget(WebInterfaceContext context) {
        return context.getPersonalInformation();
    }
}


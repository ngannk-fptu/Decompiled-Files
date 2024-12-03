/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.api.core.InjectParam
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.oauth2.provider.rest.resource.filter;

import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.api.core.InjectParam;
import javax.ws.rs.ext.Provider;

@Provider
public class SysadminOnlyResourceFilter
extends com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter {
    public SysadminOnlyResourceFilter(@InjectParam UserManager userManager) {
        super(userManager);
    }
}


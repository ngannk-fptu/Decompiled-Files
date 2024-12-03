/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.config.SecurityConfig;
import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public abstract class SimpleAbstractRoleMapper
implements RoleMapper {
    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
    }

    @Override
    public boolean canLogin(Principal user, HttpServletRequest request) {
        return user != null;
    }
}


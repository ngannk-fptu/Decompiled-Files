/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph;

import com.atlassian.seraph.Initable;
import java.io.Serializable;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public interface SecurityService
extends Serializable,
Initable {
    public void destroy();

    public Set<String> getRequiredRoles(HttpServletRequest var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface ConfluenceActionResolver {
    public Optional<String> getActionConfigClassName(HttpServletRequest var1);
}


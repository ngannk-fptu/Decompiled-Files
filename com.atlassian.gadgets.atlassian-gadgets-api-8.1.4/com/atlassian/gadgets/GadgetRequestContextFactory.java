/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.GadgetRequestContext;
import javax.servlet.http.HttpServletRequest;

public interface GadgetRequestContextFactory {
    public GadgetRequestContext get(HttpServletRequest var1);

    public GadgetRequestContext.Builder getBuilder(HttpServletRequest var1);
}


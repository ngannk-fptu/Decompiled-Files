/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

public interface RequestToViewNameTranslator {
    @Nullable
    public String getViewName(HttpServletRequest var1) throws Exception;
}


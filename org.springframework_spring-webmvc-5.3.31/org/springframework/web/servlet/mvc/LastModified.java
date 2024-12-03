/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

@Deprecated
public interface LastModified {
    public long getLastModified(HttpServletRequest var1);
}


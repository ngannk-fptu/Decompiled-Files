/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.FlashMap;

public interface FlashMapManager {
    @Nullable
    public FlashMap retrieveAndUpdate(HttpServletRequest var1, HttpServletResponse var2);

    public void saveOutputFlashMap(FlashMap var1, HttpServletRequest var2, HttpServletResponse var3);
}


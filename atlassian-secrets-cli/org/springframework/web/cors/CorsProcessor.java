/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.cors;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;

public interface CorsProcessor {
    public boolean processRequest(@Nullable CorsConfiguration var1, HttpServletRequest var2, HttpServletResponse var3) throws IOException;
}


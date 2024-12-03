/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.cors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;

public interface CorsConfigurationSource {
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest var1);
}


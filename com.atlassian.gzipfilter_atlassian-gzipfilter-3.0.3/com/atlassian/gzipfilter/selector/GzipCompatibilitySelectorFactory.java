/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.gzipfilter.selector;

import com.atlassian.gzipfilter.selector.GzipCompatibilitySelector;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public interface GzipCompatibilitySelectorFactory {
    public GzipCompatibilitySelector getSelector(FilterConfig var1, HttpServletRequest var2);
}


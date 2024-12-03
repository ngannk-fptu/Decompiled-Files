/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package org.springframework.web.bind;

import javax.servlet.ServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;

public class ServletRequestParameterPropertyValues
extends MutablePropertyValues {
    public static final String DEFAULT_PREFIX_SEPARATOR = "_";

    public ServletRequestParameterPropertyValues(ServletRequest request) {
        this(request, null, null);
    }

    public ServletRequestParameterPropertyValues(ServletRequest request, @Nullable String prefix) {
        this(request, prefix, DEFAULT_PREFIX_SEPARATOR);
    }

    public ServletRequestParameterPropertyValues(ServletRequest request, @Nullable String prefix, @Nullable String prefixSeparator) {
        super(WebUtils.getParametersStartingWith(request, prefix != null ? prefix + prefixSeparator : null));
    }
}


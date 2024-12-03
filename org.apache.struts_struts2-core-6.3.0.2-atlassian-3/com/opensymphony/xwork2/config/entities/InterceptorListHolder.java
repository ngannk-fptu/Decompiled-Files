/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import java.util.List;

public interface InterceptorListHolder {
    public InterceptorListHolder addInterceptor(InterceptorMapping var1);

    public InterceptorListHolder addInterceptors(List<InterceptorMapping> var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.support;

import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.util.UriComponentsBuilder;

public interface UriComponentsContributor {
    public boolean supportsParameter(MethodParameter var1);

    public void contributeMethodArgument(MethodParameter var1, Object var2, UriComponentsBuilder var3, Map<String, Object> var4, ConversionService var5);
}


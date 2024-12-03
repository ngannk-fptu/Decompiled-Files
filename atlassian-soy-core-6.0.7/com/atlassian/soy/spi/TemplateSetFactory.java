/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.spi;

import java.net.URL;
import java.util.Set;

public interface TemplateSetFactory {
    public void clear();

    public Set<URL> get(String var1);
}


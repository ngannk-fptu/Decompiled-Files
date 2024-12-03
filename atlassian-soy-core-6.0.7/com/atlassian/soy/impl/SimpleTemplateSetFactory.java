/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.soy.impl;

import com.atlassian.soy.impl.AbstractTemplateSetFactory;
import com.google.common.collect.ImmutableSet;
import java.net.URL;
import java.util.Set;

public class SimpleTemplateSetFactory
extends AbstractTemplateSetFactory {
    private final Set<URL> urls;

    public SimpleTemplateSetFactory(Set<URL> urls) {
        this.urls = urls;
    }

    public SimpleTemplateSetFactory(URL ... urls) {
        this((Set<URL>)ImmutableSet.copyOf((Object[])urls));
    }

    @Override
    public Set<URL> get(String completeModuleKey) {
        return this.urls;
    }
}


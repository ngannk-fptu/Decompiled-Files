/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.synd.Converter;
import com.rometools.rome.io.impl.PluginManager;
import java.util.List;

public class Converters
extends PluginManager<Converter> {
    public static final String CONVERTERS_KEY = "Converter.classes";

    public Converters() {
        super(CONVERTERS_KEY);
    }

    public Converter getConverter(String feedType) {
        return (Converter)this.getPlugin(feedType);
    }

    @Override
    protected String getKey(Converter obj) {
        return obj.getType();
    }

    public List<String> getSupportedFeedTypes() {
        return this.getKeys();
    }
}


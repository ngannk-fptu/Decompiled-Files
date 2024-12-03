/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.io.WireFeedParser;
import com.rometools.rome.io.impl.PluginManager;
import java.util.List;
import org.jdom2.Document;

public class FeedParsers
extends PluginManager<WireFeedParser> {
    public static final String FEED_PARSERS_KEY = "WireFeedParser.classes";

    public FeedParsers() {
        super(FEED_PARSERS_KEY);
    }

    public List<String> getSupportedFeedTypes() {
        return this.getKeys();
    }

    public WireFeedParser getParserFor(Document document) {
        List parsers = this.getPlugins();
        for (WireFeedParser parser : parsers) {
            if (!parser.isMyType(document)) continue;
            return parser;
        }
        return null;
    }

    @Override
    protected String getKey(WireFeedParser obj) {
        return obj.getType();
    }
}


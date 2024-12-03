/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.feed.GadgetFeedReader
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.directory.internal.GadgetHttpCache;
import com.atlassian.gadgets.directory.internal.impl.GadgetFeedReaderImpl;
import com.atlassian.gadgets.feed.GadgetFeedReader;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="gadgetFeedReaderFactory")
@ExportAsService(value={GadgetFeedReaderFactory.class})
public class GadgetFeedReaderFactoryImpl
implements GadgetFeedReaderFactory {
    private final GadgetHttpCache http;

    @Autowired
    public GadgetFeedReaderFactoryImpl(@ComponentImport ApplicationProperties applicationProperties) {
        this.http = new GadgetHttpCache(applicationProperties);
    }

    public GadgetFeedReader getFeedReader(URI feedUri) {
        return new GadgetFeedReaderImpl(feedUri, this.http);
    }
}


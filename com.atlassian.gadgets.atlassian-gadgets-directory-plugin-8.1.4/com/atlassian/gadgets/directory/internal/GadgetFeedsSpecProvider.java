/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetSpecProvider
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.gadgets.feed.GadgetFeedReader
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.gadgets.util.TransactionRunner
 *  com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.gadgets.directory.internal.GadgetFeedReaderHelper;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.gadgets.feed.GadgetFeedReader;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.gadgets.util.TransactionRunner;
import com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class GadgetFeedsSpecProvider
implements GadgetSpecProvider {
    private static final Logger log = LoggerFactory.getLogger(GadgetFeedsSpecProvider.class);
    private final GadgetFeedReaderFactory readerFactory;
    private final SubscribedGadgetFeedStore store;
    private final TransactionRunner transactionRunner;

    @Autowired
    public GadgetFeedsSpecProvider(GadgetFeedReaderFactory readerFactory, @ComponentImport SubscribedGadgetFeedStore store, @ClasspathComponent TransactionRunner transactionRunner) {
        this.readerFactory = readerFactory;
        this.store = (SubscribedGadgetFeedStore)Preconditions.checkNotNull((Object)store, (Object)"store");
        this.transactionRunner = (TransactionRunner)Preconditions.checkNotNull((Object)transactionRunner, (Object)"transactionRunner");
    }

    public boolean contains(URI gadgetSpecUri) {
        return Iterables.any(this.getFeedReaders(), GadgetFeedsSpecProvider.feedContains(gadgetSpecUri));
    }

    public Iterable<URI> entries() {
        return Iterables.concat((Iterable)Iterables.transform(this.getFeedReaders(), GadgetFeedReaderHelper.toEntries()));
    }

    private Iterable<SubscribedGadgetFeed> getFeeds() {
        return (Iterable)this.transactionRunner.execute((Callable)new Callable<Iterable<SubscribedGadgetFeed>>(){

            @Override
            public Iterable<SubscribedGadgetFeed> call() {
                return GadgetFeedsSpecProvider.this.store.getAllFeeds();
            }
        });
    }

    private Iterable<GadgetFeedReader> getFeedReaders() {
        ArrayList<GadgetFeedReader> readers = new ArrayList<GadgetFeedReader>();
        for (SubscribedGadgetFeed feed : this.getFeeds()) {
            try {
                GadgetFeedReader reader = this.readerFactory.getFeedReader(feed.getUri());
                readers.add(reader);
            }
            catch (RuntimeException e) {
                if (log.isDebugEnabled()) {
                    log.warn("Unable to parse feed from: " + feed.getUri().toString(), (Throwable)e);
                    continue;
                }
                log.warn("Unable to parse feed from: " + feed.getUri().toString());
            }
        }
        return Iterables.unmodifiableIterable(readers);
    }

    private static Predicate<GadgetFeedReader> feedContains(URI gadgetSpecUri) {
        return GadgetFeedReaderHelper.containsSpecUri(gadgetSpecUri);
    }
}


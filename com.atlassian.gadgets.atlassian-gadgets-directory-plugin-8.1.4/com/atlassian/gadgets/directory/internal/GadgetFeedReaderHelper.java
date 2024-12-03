/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.feed.GadgetFeedHostConnectionException
 *  com.atlassian.gadgets.feed.GadgetFeedParsingException
 *  com.atlassian.gadgets.feed.GadgetFeedReader
 *  com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.feed.GadgetFeedHostConnectionException;
import com.atlassian.gadgets.feed.GadgetFeedParsingException;
import com.atlassian.gadgets.feed.GadgetFeedReader;
import com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class GadgetFeedReaderHelper {
    GadgetFeedReaderHelper() {
    }

    static Predicate<GadgetFeedReader> containsSpecUri(URI gadgetSpecUri) {
        return new GadgetFeedReaderContainsPredicate(gadgetSpecUri);
    }

    static Function<GadgetFeedReader, Iterable<URI>> toEntries() {
        return new GadgetFeedReaderToEntriesFunction();
    }

    private static final class GadgetFeedReaderToEntriesFunction
    implements Function<GadgetFeedReader, Iterable<URI>> {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private GadgetFeedReaderToEntriesFunction() {
        }

        public Iterable<URI> apply(GadgetFeedReader reader) {
            try {
                return reader.entries();
            }
            catch (NonAtomGadgetSpecFeedException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri().toASCIIString() + "' is not an Atom feed", (Throwable)e);
                } else {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri().toASCIIString() + "' is not an Atom feed");
                }
                return ImmutableSet.of();
            }
            catch (GadgetFeedParsingException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri() + "' could not be parsed as an Atom feed", (Throwable)e);
                } else {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri() + "' could not be parsed as an Atom feed");
                }
                return ImmutableSet.of();
            }
            catch (GadgetFeedHostConnectionException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.warn("Cannot connect to gadegt spec feed at '" + e.getFeedUri() + "'", (Throwable)e);
                } else {
                    this.logger.warn("Cannot connect to gadegt spec feed at '" + e.getFeedUri() + "'");
                }
                return ImmutableSet.of();
            }
            catch (RuntimeException e) {
                this.logger.debug("Unable to get the contents of the GadgetFeedReader", (Throwable)e);
                return ImmutableSet.of();
            }
        }
    }

    private static final class GadgetFeedReaderContainsPredicate
    implements Predicate<GadgetFeedReader> {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private final URI gadgetSpecUri;

        private GadgetFeedReaderContainsPredicate(URI gadgetSpecUri) {
            this.gadgetSpecUri = gadgetSpecUri;
        }

        public boolean apply(GadgetFeedReader reader) {
            try {
                return reader.contains(this.gadgetSpecUri);
            }
            catch (NonAtomGadgetSpecFeedException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri().toASCIIString() + "' is not an Atom feed", (Throwable)e);
                } else {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri().toASCIIString() + "' is not an Atom feed");
                }
                return false;
            }
            catch (GadgetFeedParsingException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri() + "' could not be parsed as an Atom feed", (Throwable)e);
                } else {
                    this.logger.warn("Gadget spec feed at '" + e.getFeedUri() + "' could not be parsed as an Atom feed");
                }
                return false;
            }
            catch (GadgetFeedHostConnectionException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.warn("Cannot connect to gadegt spec feed at '" + e.getFeedUri() + "'", (Throwable)e);
                } else {
                    this.logger.warn("Cannot connect to gadegt spec feed at '" + e.getFeedUri() + "'");
                }
                return false;
            }
            catch (RuntimeException e) {
                this.logger.debug("Unable to determine if GadgetFeedReader contains the URI '" + this.gadgetSpecUri, (Throwable)e);
                return false;
            }
        }
    }
}


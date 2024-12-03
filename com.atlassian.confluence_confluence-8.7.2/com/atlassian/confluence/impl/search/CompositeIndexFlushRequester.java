/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.search.IndexFlushRequester;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeIndexFlushRequester
implements IndexFlushRequester {
    private static final Logger log = LoggerFactory.getLogger(CompositeIndexFlushRequester.class);
    private final Collection<IndexFlushRequester> flushRequesters;

    public CompositeIndexFlushRequester(Collection<IndexFlushRequester> flushRequesters) {
        this.flushRequesters = new ArrayList<IndexFlushRequester>(Objects.requireNonNull(flushRequesters));
    }

    @Override
    public void requestFlush() {
        this.runForAllChildren(IndexFlushRequester::requestFlush);
    }

    @Override
    public void resume() {
        this.runForAllChildren(IndexFlushRequester::resume);
    }

    @Override
    public IndexFlushRequester.Resumer pause() {
        List<IndexFlushRequester.Resumer> resumers = this.runForAllChildren(IndexFlushRequester::pause);
        return new CompositeResumer(resumers);
    }

    public void runForAllChildren(Consumer<IndexFlushRequester> operation) {
        this.runForAllChildren((IndexFlushRequester r) -> {
            operation.accept((IndexFlushRequester)r);
            return null;
        });
    }

    public <T> List<T> runForAllChildren(Function<IndexFlushRequester, T> operation) {
        boolean failed = false;
        ArrayList<T> results = new ArrayList<T>();
        for (IndexFlushRequester flushRequester : this.flushRequesters) {
            try {
                T result = operation.apply(flushRequester);
                results.add(result);
            }
            catch (RuntimeException ex) {
                log.error("Failed to perform IndexFlushRequester action", (Throwable)ex);
                failed = true;
            }
        }
        if (failed) {
            throw new RuntimeException("Failed to perform action for some IndexFlushRequesters");
        }
        return results;
    }

    private static class CompositeResumer
    implements IndexFlushRequester.Resumer {
        private final Collection<IndexFlushRequester.Resumer> children;

        public CompositeResumer(Collection<IndexFlushRequester.Resumer> children) {
            this.children = children;
        }

        @Override
        public void close() {
            boolean failed = false;
            for (IndexFlushRequester.Resumer child : this.children) {
                try {
                    child.close();
                }
                catch (RuntimeException ex) {
                    log.error("Failed to close Resumer", (Throwable)ex);
                    failed = true;
                }
            }
            if (failed) {
                throw new RuntimeException("Failed to close some Resumers");
            }
        }
    }
}


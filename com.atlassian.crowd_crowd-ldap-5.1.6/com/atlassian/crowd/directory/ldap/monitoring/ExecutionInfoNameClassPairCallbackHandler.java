/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.core.NameClassPairCallbackHandler
 */
package com.atlassian.crowd.directory.ldap.monitoring;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.NameClassPairCallbackHandler;

public class ExecutionInfoNameClassPairCallbackHandler<T extends NameClassPairCallbackHandler>
implements NameClassPairCallbackHandler {
    private static final Logger log = LoggerFactory.getLogger(ExecutionInfoNameClassPairCallbackHandler.class);
    private final T delegate;
    private int resultCount = 0;

    public ExecutionInfoNameClassPairCallbackHandler(T delegate) {
        this.delegate = delegate;
    }

    public void handleNameClassPair(NameClassPair nameClassPair) throws NamingException {
        if (log.isTraceEnabled() && nameClassPair instanceof SearchResult) {
            log.trace("Search result {}, with attributes {} ", (Object)nameClassPair.getName(), (Object)((SearchResult)nameClassPair).getAttributes());
        }
        ++this.resultCount;
        this.delegate.handleNameClassPair(nameClassPair);
    }

    public void logResultCount() {
        log.debug("The operation returned {} results", (Object)this.resultCount);
    }

    public T getDelegate() {
        return this.delegate;
    }
}


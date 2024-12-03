/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthor
 *  com.atlassian.crowd.audit.AuditLogContextCallback
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogContextCallback;
import com.atlassian.crowd.audit.AuditLogContextInternal;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogContextInternalImpl
implements AuditLogContextInternal {
    private static final Logger log = LoggerFactory.getLogger(AuditLogContextInternalImpl.class);
    private final ThreadLocal<AuditLogAuthor> threadLocalAuthor;
    private final ThreadLocal<AuditLogEventSource> threadLocalSource;

    public AuditLogContextInternalImpl() {
        this(new ThreadLocal<AuditLogAuthor>(), new ThreadLocal<AuditLogEventSource>());
    }

    @VisibleForTesting
    AuditLogContextInternalImpl(ThreadLocal<AuditLogAuthor> threadLocalAuthor, ThreadLocal<AuditLogEventSource> threadLocalSource) {
        this.threadLocalAuthor = threadLocalAuthor;
        this.threadLocalSource = threadLocalSource;
    }

    @Override
    public Optional<AuditLogAuthor> getAuthor() {
        return Optional.ofNullable(this.threadLocalAuthor.get());
    }

    @Override
    public Optional<AuditLogEventSource> getSource() {
        return Optional.ofNullable(this.threadLocalSource.get());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T withAuditLogAuthor(AuditLogAuthor author, AuditLogContextCallback<T> callback) throws Exception {
        try {
            log.debug("Setting custom author context for author {}", (Object)author);
            this.threadLocalAuthor.set(author);
            Object object = callback.execute();
            return (T)object;
        }
        finally {
            this.threadLocalAuthor.remove();
            log.debug("Cleared custom author context for author {}", (Object)author);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T withAuditLogSource(AuditLogEventSource source, AuditLogContextCallback<T> callback) throws Exception {
        try {
            log.debug("Setting custom source context for source {}", (Object)source);
            this.threadLocalSource.set(source);
            Object object = callback.execute();
            return (T)object;
        }
        finally {
            this.threadLocalSource.remove();
            log.debug("Cleared custom source context for source {}", (Object)source);
        }
    }
}


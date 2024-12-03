/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.logging;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.RepositoryService;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;
import org.apache.jackrabbit.spi.commons.logging.LogWriterProvider;
import org.apache.jackrabbit.spi.commons.logging.Slf4jLogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLogWriterProvider
implements LogWriterProvider {
    @Override
    public LogWriter getLogWriter(RepositoryService service) {
        return Slf4jLogWriterProvider.getLogWriterInternal(service);
    }

    @Override
    public LogWriter getLogWriter(NameFactory nameFactory) {
        return Slf4jLogWriterProvider.getLogWriterInternal(nameFactory);
    }

    @Override
    public LogWriter getLogWriter(PathFactory pathFactory) {
        return Slf4jLogWriterProvider.getLogWriterInternal(pathFactory);
    }

    @Override
    public LogWriter getLogWriter(IdFactory idFactory) {
        return Slf4jLogWriterProvider.getLogWriterInternal(idFactory);
    }

    @Override
    public LogWriter getLogWriter(QValueFactory valueFactory) {
        return Slf4jLogWriterProvider.getLogWriterInternal(valueFactory);
    }

    @Override
    public LogWriter getLogWriter(SessionInfo sessionInfo) {
        return Slf4jLogWriterProvider.getLogWriterInternal(sessionInfo);
    }

    @Override
    public LogWriter getLogWriter(Batch batch) {
        return Slf4jLogWriterProvider.getLogWriterInternal(batch);
    }

    private static LogWriter getLogWriterInternal(Object object) {
        Logger log = LoggerFactory.getLogger(object.getClass());
        return log.isDebugEnabled() ? new Slf4jLogWriter(log) : null;
    }
}


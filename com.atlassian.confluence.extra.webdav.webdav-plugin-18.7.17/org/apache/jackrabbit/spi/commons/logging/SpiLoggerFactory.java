/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.RepositoryService;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.commons.logging.BatchLogger;
import org.apache.jackrabbit.spi.commons.logging.IdFactoryLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;
import org.apache.jackrabbit.spi.commons.logging.LogWriterProvider;
import org.apache.jackrabbit.spi.commons.logging.NameFactoryLogger;
import org.apache.jackrabbit.spi.commons.logging.PathFactoryLogger;
import org.apache.jackrabbit.spi.commons.logging.QValueFactoryLogger;
import org.apache.jackrabbit.spi.commons.logging.RepositoryServiceLogger;
import org.apache.jackrabbit.spi.commons.logging.SessionInfoLogger;
import org.apache.jackrabbit.spi.commons.logging.Slf4jLogWriterProvider;

public final class SpiLoggerFactory {
    private SpiLoggerFactory() {
    }

    public static RepositoryService create(RepositoryService service) {
        return SpiLoggerFactory.create(service, (LogWriterProvider)new Slf4jLogWriterProvider());
    }

    public static RepositoryService create(RepositoryService service, LogWriterProvider logWriterProvider) {
        if (service == null) {
            throw new IllegalArgumentException("Service must not be null");
        }
        if (logWriterProvider == null) {
            throw new IllegalArgumentException("LogWriterProvider must not be null");
        }
        LogWriter logWriter = logWriterProvider.getLogWriter(service);
        if (logWriter == null) {
            return service;
        }
        return new ServiceLogger(service, logWriterProvider, logWriter);
    }

    public static NameFactory create(NameFactory nameFactory, LogWriterProvider logWriterProvider) {
        if (nameFactory == null) {
            throw new IllegalArgumentException("NameFactory must not be null");
        }
        if (logWriterProvider == null) {
            throw new IllegalArgumentException("LogWriterProvider must not be null");
        }
        LogWriter logWriter = logWriterProvider.getLogWriter(nameFactory);
        if (logWriter == null) {
            return nameFactory;
        }
        return new NameFactoryLogger(nameFactory, logWriter);
    }

    public static PathFactory create(PathFactory pathFactory, LogWriterProvider logWriterProvider) {
        if (pathFactory == null) {
            throw new IllegalArgumentException("PathFactory must not be null");
        }
        if (logWriterProvider == null) {
            throw new IllegalArgumentException("LogWriterProvider must not be null");
        }
        LogWriter logWriter = logWriterProvider.getLogWriter(pathFactory);
        if (logWriter == null) {
            return pathFactory;
        }
        return new PathFactoryLogger(pathFactory, logWriter);
    }

    public static IdFactory create(IdFactory idFactory, LogWriterProvider logWriterProvider) {
        if (idFactory == null) {
            throw new IllegalArgumentException("IdFactory must not be null");
        }
        if (logWriterProvider == null) {
            throw new IllegalArgumentException("LogWriterProvider must not be null");
        }
        LogWriter logWriter = logWriterProvider.getLogWriter(idFactory);
        if (logWriter == null) {
            return idFactory;
        }
        return new IdFactoryLogger(idFactory, logWriter);
    }

    public static QValueFactory create(QValueFactory qValueFactory, LogWriterProvider logWriterProvider) {
        if (qValueFactory == null) {
            throw new IllegalArgumentException("QValueFactory must not be null");
        }
        if (logWriterProvider == null) {
            throw new IllegalArgumentException("LogWriterProvider must not be null");
        }
        LogWriter logWriter = logWriterProvider.getLogWriter(qValueFactory);
        if (logWriter == null) {
            return qValueFactory;
        }
        return new QValueFactoryLogger(qValueFactory, logWriter);
    }

    public static SessionInfo create(SessionInfo sessionInfo, LogWriterProvider logWriterProvider) {
        if (sessionInfo == null) {
            throw new IllegalArgumentException("SessionInfo must not be null");
        }
        if (logWriterProvider == null) {
            throw new IllegalArgumentException("LogWriterProvider must not be null");
        }
        LogWriter logWriter = logWriterProvider.getLogWriter(sessionInfo);
        if (logWriter == null) {
            return sessionInfo;
        }
        return new SessionInfoLogger(sessionInfo, logWriter);
    }

    public static Batch create(Batch batch, LogWriterProvider logWriterProvider) {
        if (batch == null) {
            throw new IllegalArgumentException("Batch must not be null");
        }
        if (logWriterProvider == null) {
            throw new IllegalArgumentException("LogWriterProvider must not be null");
        }
        LogWriter logWriter = logWriterProvider.getLogWriter(batch);
        if (logWriter == null) {
            return batch;
        }
        return new BatchLogger(batch, logWriter);
    }

    private static class ServiceLogger
    extends RepositoryServiceLogger {
        private final LogWriterProvider logWriterProvider;

        public ServiceLogger(RepositoryService service, LogWriterProvider logWriterProvider, LogWriter logWriter) {
            super(service, logWriter);
            this.logWriterProvider = logWriterProvider;
        }

        @Override
        public NameFactory getNameFactory() throws RepositoryException {
            NameFactory result = super.getNameFactory();
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }

        @Override
        public PathFactory getPathFactory() throws RepositoryException {
            PathFactory result = super.getPathFactory();
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }

        @Override
        public IdFactory getIdFactory() throws RepositoryException {
            IdFactory result = super.getIdFactory();
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }

        @Override
        public QValueFactory getQValueFactory() throws RepositoryException {
            QValueFactory result = super.getQValueFactory();
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }

        @Override
        public SessionInfo obtain(Credentials credentials, String workspaceName) throws RepositoryException {
            SessionInfo result = super.obtain(credentials, workspaceName);
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }

        @Override
        public SessionInfo obtain(SessionInfo sessionInfo, String workspaceName) throws RepositoryException {
            SessionInfo result = super.obtain(sessionInfo, workspaceName);
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }

        @Override
        public SessionInfo impersonate(SessionInfo sessionInfo, Credentials credentials) throws RepositoryException {
            SessionInfo result = super.impersonate(sessionInfo, credentials);
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }

        @Override
        public Batch createBatch(SessionInfo sessionInfo, ItemId itemId) throws RepositoryException {
            Batch result = super.createBatch(sessionInfo, itemId);
            return result == null ? null : SpiLoggerFactory.create(result, this.logWriterProvider);
        }
    }
}


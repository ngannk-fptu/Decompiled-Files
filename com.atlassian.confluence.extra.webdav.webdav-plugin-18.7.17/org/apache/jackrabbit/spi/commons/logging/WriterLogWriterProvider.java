/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import java.io.Writer;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.RepositoryService;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;
import org.apache.jackrabbit.spi.commons.logging.LogWriterProvider;
import org.apache.jackrabbit.spi.commons.logging.WriterLogWriter;

public class WriterLogWriterProvider
implements LogWriterProvider {
    private final Writer log;

    public WriterLogWriterProvider(Writer log) {
        this.log = log;
    }

    @Override
    public LogWriter getLogWriter(RepositoryService service) {
        return WriterLogWriterProvider.getLogWriterInternal(this.log, service);
    }

    @Override
    public LogWriter getLogWriter(NameFactory nameFactory) {
        return WriterLogWriterProvider.getLogWriterInternal(this.log, nameFactory);
    }

    @Override
    public LogWriter getLogWriter(PathFactory pathFactory) {
        return WriterLogWriterProvider.getLogWriterInternal(this.log, pathFactory);
    }

    @Override
    public LogWriter getLogWriter(IdFactory idFactory) {
        return WriterLogWriterProvider.getLogWriterInternal(this.log, idFactory);
    }

    @Override
    public LogWriter getLogWriter(QValueFactory valueFactory) {
        return WriterLogWriterProvider.getLogWriterInternal(this.log, valueFactory);
    }

    @Override
    public LogWriter getLogWriter(SessionInfo sessionInfo) {
        return WriterLogWriterProvider.getLogWriterInternal(this.log, sessionInfo);
    }

    @Override
    public LogWriter getLogWriter(Batch batch) {
        return WriterLogWriterProvider.getLogWriterInternal(this.log, batch);
    }

    private static LogWriter getLogWriterInternal(Writer log, Object object) {
        return new WriterLogWriter(log, object.getClass().getSimpleName());
    }
}


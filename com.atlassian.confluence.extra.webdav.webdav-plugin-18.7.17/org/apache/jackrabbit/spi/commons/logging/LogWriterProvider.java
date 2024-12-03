/*
 * Decompiled with CFR 0.152.
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

public interface LogWriterProvider {
    public LogWriter getLogWriter(RepositoryService var1);

    public LogWriter getLogWriter(NameFactory var1);

    public LogWriter getLogWriter(PathFactory var1);

    public LogWriter getLogWriter(IdFactory var1);

    public LogWriter getLogWriter(QValueFactory var1);

    public LogWriter getLogWriter(SessionInfo var1);

    public LogWriter getLogWriter(Batch var1);
}


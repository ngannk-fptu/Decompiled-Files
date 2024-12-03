/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 */
package com.atlassian.logging.log4j.juli;

import com.atlassian.logging.log4j.juli.JuliToLog4jMapper;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.apache.logging.log4j.LogManager;

public class JuliToLog4jHandler
extends Handler {
    private JuliToLog4jMapper mapper = new JuliToLog4jMapper();

    public void setMapper(JuliToLog4jMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord record) {
        JuliToLog4jMapper.MappedLogRecord mapped = this.mapper.map(record);
        LogManager.getLogger((String)mapped.getLoggerName()).log(mapped.getLevel(), mapped.getMessage(), mapped.getThrowable());
    }
}


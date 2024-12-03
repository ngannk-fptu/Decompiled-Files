/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Setting;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

abstract class ConnectionSettingsBase<T extends Throwable> {
    private final Log log = LogFactory.getLog(ConnectionSettingsBase.class);
    private final StringManager sm = StringManager.getManager(ConnectionSettingsBase.class);
    private final String connectionId;
    static final int MAX_WINDOW_SIZE = Integer.MAX_VALUE;
    static final int MIN_MAX_FRAME_SIZE = 16384;
    static final int MAX_MAX_FRAME_SIZE = 0xFFFFFF;
    static final long UNLIMITED = 0x100000000L;
    static final int MAX_HEADER_TABLE_SIZE = 65536;
    static final int DEFAULT_HEADER_TABLE_SIZE = 4096;
    static final boolean DEFAULT_ENABLE_PUSH = true;
    static final long DEFAULT_MAX_CONCURRENT_STREAMS = 0x100000000L;
    static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
    static final int DEFAULT_MAX_FRAME_SIZE = 16384;
    static final long DEFAULT_MAX_HEADER_LIST_SIZE = 32768L;
    static final long DEFAULT_NO_RFC7540_PRIORITIES = 1L;
    Map<Setting, Long> current = new ConcurrentHashMap<Setting, Long>();
    Map<Setting, Long> pending = new ConcurrentHashMap<Setting, Long>();

    ConnectionSettingsBase(String connectionId) {
        this.connectionId = connectionId;
        this.current.put(Setting.HEADER_TABLE_SIZE, 4096L);
        this.current.put(Setting.ENABLE_PUSH, 1L);
        this.current.put(Setting.MAX_CONCURRENT_STREAMS, 0x100000000L);
        this.current.put(Setting.INITIAL_WINDOW_SIZE, 65535L);
        this.current.put(Setting.MAX_FRAME_SIZE, 16384L);
        this.current.put(Setting.MAX_HEADER_LIST_SIZE, 32768L);
        this.current.put(Setting.NO_RFC7540_PRIORITIES, 1L);
    }

    final void set(Setting setting, long value) throws T {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.sm.getString("connectionSettings.debug", new Object[]{this.connectionId, this.getEndpointName(), setting, Long.toString(value)}));
        }
        switch (setting) {
            case HEADER_TABLE_SIZE: {
                this.validateHeaderTableSize(value);
                break;
            }
            case ENABLE_PUSH: {
                this.validateEnablePush(value);
                break;
            }
            case MAX_CONCURRENT_STREAMS: {
                break;
            }
            case INITIAL_WINDOW_SIZE: {
                this.validateInitialWindowSize(value);
                break;
            }
            case MAX_FRAME_SIZE: {
                this.validateMaxFrameSize(value);
                break;
            }
            case MAX_HEADER_LIST_SIZE: {
                break;
            }
            case NO_RFC7540_PRIORITIES: {
                this.validateNoRfc7540Priorities(value);
                break;
            }
            case UNKNOWN: {
                return;
            }
        }
        this.set(setting, (Long)value);
    }

    synchronized void set(Setting setting, Long value) {
        this.current.put(setting, value);
    }

    final int getHeaderTableSize() {
        return this.getMinInt(Setting.HEADER_TABLE_SIZE);
    }

    final boolean getEnablePush() {
        long result = this.getMin(Setting.ENABLE_PUSH);
        return result != 0L;
    }

    final long getMaxConcurrentStreams() {
        return this.getMax(Setting.MAX_CONCURRENT_STREAMS);
    }

    final int getInitialWindowSize() {
        return this.getMaxInt(Setting.INITIAL_WINDOW_SIZE);
    }

    final int getMaxFrameSize() {
        return this.getMaxInt(Setting.MAX_FRAME_SIZE);
    }

    final long getMaxHeaderListSize() {
        return this.getMax(Setting.MAX_HEADER_LIST_SIZE);
    }

    private synchronized long getMin(Setting setting) {
        Long pendingValue = this.pending.get((Object)setting);
        long currentValue = this.current.get((Object)setting);
        if (pendingValue == null) {
            return currentValue;
        }
        return Long.min(pendingValue, currentValue);
    }

    private synchronized int getMinInt(Setting setting) {
        long result = this.getMin(setting);
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }

    private synchronized long getMax(Setting setting) {
        Long pendingValue = this.pending.get((Object)setting);
        long currentValue = this.current.get((Object)setting);
        if (pendingValue == null) {
            return currentValue;
        }
        return Long.max(pendingValue, currentValue);
    }

    private synchronized int getMaxInt(Setting setting) {
        long result = this.getMax(setting);
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }

    private void validateHeaderTableSize(long headerTableSize) throws T {
        if (headerTableSize > 65536L) {
            String msg = this.sm.getString("connectionSettings.headerTableSizeLimit", new Object[]{this.connectionId, Long.toString(headerTableSize)});
            this.throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }

    private void validateEnablePush(long enablePush) throws T {
        if (enablePush > 1L) {
            String msg = this.sm.getString("connectionSettings.enablePushInvalid", new Object[]{this.connectionId, Long.toString(enablePush)});
            this.throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }

    private void validateInitialWindowSize(long initialWindowSize) throws T {
        if (initialWindowSize > Integer.MAX_VALUE) {
            String msg = this.sm.getString("connectionSettings.windowSizeTooBig", new Object[]{this.connectionId, Long.toString(initialWindowSize), Long.toString(Integer.MAX_VALUE)});
            this.throwException(msg, Http2Error.FLOW_CONTROL_ERROR);
        }
    }

    private void validateMaxFrameSize(long maxFrameSize) throws T {
        if (maxFrameSize < 16384L || maxFrameSize > 0xFFFFFFL) {
            String msg = this.sm.getString("connectionSettings.maxFrameSizeInvalid", new Object[]{this.connectionId, Long.toString(maxFrameSize), Integer.toString(16384), Integer.toString(0xFFFFFF)});
            this.throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }

    private void validateNoRfc7540Priorities(long noRfc7540Priorities) throws T {
        if (noRfc7540Priorities < 0L || noRfc7540Priorities > 1L) {
            String msg = this.sm.getString("connectionSettings.noRfc7540PrioritiesInvalid", new Object[]{this.connectionId, Long.toString(noRfc7540Priorities)});
            this.throwException(msg, Http2Error.PROTOCOL_ERROR);
        }
    }

    abstract void throwException(String var1, Http2Error var2) throws T;

    abstract String getEndpointName();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.FrameType;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Http2Exception;
import org.apache.coyote.http2.StreamException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

class StreamStateMachine {
    private static final Log log = LogFactory.getLog(StreamStateMachine.class);
    private static final StringManager sm = StringManager.getManager(StreamStateMachine.class);
    private final String connectionId;
    private final String streamId;
    private State state;

    StreamStateMachine(String connectionId, String streamId) {
        this.connectionId = connectionId;
        this.streamId = streamId;
        this.stateChange(null, State.IDLE);
    }

    final synchronized void sentPushPromise() {
        this.stateChange(State.IDLE, State.RESERVED_LOCAL);
    }

    final synchronized void sentHeaders() {
        this.stateChange(State.RESERVED_LOCAL, State.HALF_CLOSED_REMOTE);
    }

    final synchronized void receivedStartOfHeaders() {
        this.stateChange(State.IDLE, State.OPEN);
        this.stateChange(State.RESERVED_REMOTE, State.HALF_CLOSED_LOCAL);
    }

    final synchronized void sentEndOfStream() {
        this.stateChange(State.OPEN, State.HALF_CLOSED_LOCAL);
        this.stateChange(State.HALF_CLOSED_REMOTE, State.CLOSED_TX);
    }

    final synchronized void receivedEndOfStream() {
        this.stateChange(State.OPEN, State.HALF_CLOSED_REMOTE);
        this.stateChange(State.HALF_CLOSED_LOCAL, State.CLOSED_RX);
    }

    public synchronized void sendReset() {
        if (this.state == State.IDLE) {
            throw new IllegalStateException(sm.getString("streamStateMachine.debug.change", new Object[]{this.connectionId, this.streamId, this.state}));
        }
        if (this.state.canReset()) {
            this.stateChange(this.state, State.CLOSED_RST_TX);
        }
    }

    final synchronized void receivedReset() {
        this.stateChange(this.state, State.CLOSED_RST_RX);
    }

    private void stateChange(State oldState, State newState) {
        if (this.state == oldState) {
            this.state = newState;
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("streamStateMachine.debug.change", new Object[]{this.connectionId, this.streamId, oldState, newState}));
            }
        }
    }

    final synchronized void checkFrameType(FrameType frameType) throws Http2Exception {
        if (!this.isFrameTypePermitted(frameType)) {
            if (this.state.connectionErrorForInvalidFrame) {
                throw new ConnectionException(sm.getString("streamStateMachine.invalidFrame", new Object[]{this.connectionId, this.streamId, this.state, frameType}), this.state.errorCodeForInvalidFrame);
            }
            throw new StreamException(sm.getString("streamStateMachine.invalidFrame", new Object[]{this.connectionId, this.streamId, this.state, frameType}), this.state.errorCodeForInvalidFrame, Integer.parseInt(this.streamId));
        }
    }

    final synchronized boolean isFrameTypePermitted(FrameType frameType) {
        return this.state.isFrameTypePermitted(frameType);
    }

    final synchronized boolean isActive() {
        return this.state.isActive();
    }

    final synchronized boolean canRead() {
        return this.state.canRead();
    }

    final synchronized boolean canWrite() {
        return this.state.canWrite();
    }

    final synchronized boolean isClosedFinal() {
        return this.state == State.CLOSED_FINAL;
    }

    final synchronized void closeIfIdle() {
        this.stateChange(State.IDLE, State.CLOSED_FINAL);
    }

    final synchronized String getCurrentStateName() {
        return this.state.name();
    }

    private static enum State {
        IDLE(false, false, false, true, Http2Error.PROTOCOL_ERROR, FrameType.HEADERS, FrameType.PRIORITY),
        OPEN(true, true, true, true, Http2Error.PROTOCOL_ERROR, FrameType.DATA, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST, FrameType.PUSH_PROMISE, FrameType.WINDOW_UPDATE),
        RESERVED_LOCAL(false, false, true, true, Http2Error.PROTOCOL_ERROR, FrameType.PRIORITY, FrameType.RST, FrameType.WINDOW_UPDATE),
        RESERVED_REMOTE(false, true, true, true, Http2Error.PROTOCOL_ERROR, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST),
        HALF_CLOSED_LOCAL(true, false, true, true, Http2Error.PROTOCOL_ERROR, FrameType.DATA, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST, FrameType.PUSH_PROMISE, FrameType.WINDOW_UPDATE),
        HALF_CLOSED_REMOTE(false, true, true, true, Http2Error.STREAM_CLOSED, FrameType.PRIORITY, FrameType.RST, FrameType.WINDOW_UPDATE),
        CLOSED_RX(false, false, false, true, Http2Error.STREAM_CLOSED, FrameType.PRIORITY),
        CLOSED_TX(false, false, false, true, Http2Error.STREAM_CLOSED, FrameType.PRIORITY, FrameType.RST, FrameType.WINDOW_UPDATE),
        CLOSED_RST_RX(false, false, false, false, Http2Error.STREAM_CLOSED, FrameType.PRIORITY),
        CLOSED_RST_TX(false, false, false, false, Http2Error.STREAM_CLOSED, FrameType.DATA, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST, FrameType.PUSH_PROMISE, FrameType.WINDOW_UPDATE),
        CLOSED_FINAL(false, false, false, true, Http2Error.PROTOCOL_ERROR, FrameType.PRIORITY);

        private final boolean canRead;
        private final boolean canWrite;
        private final boolean canReset;
        private final boolean connectionErrorForInvalidFrame;
        private final Http2Error errorCodeForInvalidFrame;
        private final Set<FrameType> frameTypesPermitted;

        private State(boolean canRead, boolean canWrite, boolean canReset, boolean connectionErrorForInvalidFrame, Http2Error errorCode, FrameType ... frameTypes) {
            this.canRead = canRead;
            this.canWrite = canWrite;
            this.canReset = canReset;
            this.connectionErrorForInvalidFrame = connectionErrorForInvalidFrame;
            this.errorCodeForInvalidFrame = errorCode;
            this.frameTypesPermitted = new HashSet<FrameType>(Arrays.asList(frameTypes));
        }

        public boolean isActive() {
            return this.canWrite || this.canRead;
        }

        public boolean canRead() {
            return this.canRead;
        }

        public boolean canWrite() {
            return this.canWrite;
        }

        public boolean canReset() {
            return this.canReset;
        }

        public boolean isFrameTypePermitted(FrameType frameType) {
            return this.frameTypesPermitted.contains((Object)frameType);
        }
    }
}


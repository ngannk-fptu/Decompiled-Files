/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import org.apache.tika.pipes.emitter.EmitData;

public class PipesResult {
    public static final PipesResult CLIENT_UNAVAILABLE_WITHIN_MS = new PipesResult(STATUS.CLIENT_UNAVAILABLE_WITHIN_MS);
    public static final PipesResult TIMEOUT = new PipesResult(STATUS.TIMEOUT);
    public static final PipesResult OOM = new PipesResult(STATUS.OOM);
    public static final PipesResult UNSPECIFIED_CRASH = new PipesResult(STATUS.UNSPECIFIED_CRASH);
    public static final PipesResult EMIT_SUCCESS = new PipesResult(STATUS.EMIT_SUCCESS);
    public static final PipesResult INTERRUPTED_EXCEPTION = new PipesResult(STATUS.INTERRUPTED_EXCEPTION);
    public static final PipesResult EMPTY_OUTPUT = new PipesResult(STATUS.EMPTY_OUTPUT);
    private final STATUS status;
    private final EmitData emitData;
    private final String message;

    private PipesResult(STATUS status, EmitData emitData, String message) {
        this.status = status;
        this.emitData = emitData;
        this.message = message;
    }

    public PipesResult(STATUS status) {
        this(status, null, null);
    }

    public PipesResult(STATUS status, String message) {
        this(status, null, message);
    }

    public PipesResult(EmitData emitData) {
        this(STATUS.PARSE_SUCCESS, emitData, null);
    }

    public PipesResult(EmitData emitData, String message) {
        this(STATUS.PARSE_SUCCESS_WITH_EXCEPTION, emitData, message);
    }

    public STATUS getStatus() {
        return this.status;
    }

    public EmitData getEmitData() {
        return this.emitData;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "PipesResult{status=" + (Object)((Object)this.status) + ", emitData=" + this.emitData + ", message='" + this.message + '\'' + '}';
    }

    public static enum STATUS {
        CLIENT_UNAVAILABLE_WITHIN_MS,
        FETCHER_INITIALIZATION_EXCEPTION,
        FETCH_EXCEPTION,
        EMPTY_OUTPUT,
        PARSE_EXCEPTION_NO_EMIT,
        PARSE_EXCEPTION_EMIT,
        PARSE_SUCCESS,
        PARSE_SUCCESS_WITH_EXCEPTION,
        OOM,
        TIMEOUT,
        UNSPECIFIED_CRASH,
        NO_EMITTER_FOUND,
        EMIT_SUCCESS,
        EMIT_SUCCESS_PARSE_EXCEPTION,
        EMIT_EXCEPTION,
        INTERRUPTED_EXCEPTION,
        NO_FETCHER_FOUND;

    }
}


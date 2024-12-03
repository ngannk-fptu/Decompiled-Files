/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.io.Serializable;
import java.util.Objects;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.HandlerConfig;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.fetcher.FetchKey;

public class FetchEmitTuple
implements Serializable {
    public static final ON_PARSE_EXCEPTION DEFAULT_ON_PARSE_EXCEPTION = ON_PARSE_EXCEPTION.EMIT;
    private final String id;
    private final FetchKey fetchKey;
    private EmitKey emitKey;
    private final Metadata metadata;
    private final ON_PARSE_EXCEPTION onParseException;
    private HandlerConfig handlerConfig;

    public FetchEmitTuple(String id, FetchKey fetchKey, EmitKey emitKey) {
        this(id, fetchKey, emitKey, new Metadata(), HandlerConfig.DEFAULT_HANDLER_CONFIG, DEFAULT_ON_PARSE_EXCEPTION);
    }

    public FetchEmitTuple(String id, FetchKey fetchKey, EmitKey emitKey, ON_PARSE_EXCEPTION onParseException) {
        this(id, fetchKey, emitKey, new Metadata(), HandlerConfig.DEFAULT_HANDLER_CONFIG, onParseException);
    }

    public FetchEmitTuple(String id, FetchKey fetchKey, EmitKey emitKey, Metadata metadata) {
        this(id, fetchKey, emitKey, metadata, HandlerConfig.DEFAULT_HANDLER_CONFIG, DEFAULT_ON_PARSE_EXCEPTION);
    }

    public FetchEmitTuple(String id, FetchKey fetchKey, EmitKey emitKey, Metadata metadata, HandlerConfig handlerConfig, ON_PARSE_EXCEPTION onParseException) {
        this.id = id;
        this.fetchKey = fetchKey;
        this.emitKey = emitKey;
        this.metadata = metadata;
        this.handlerConfig = handlerConfig;
        this.onParseException = onParseException;
    }

    public String getId() {
        return this.id;
    }

    public FetchKey getFetchKey() {
        return this.fetchKey;
    }

    public EmitKey getEmitKey() {
        return this.emitKey;
    }

    public Metadata getMetadata() {
        return this.metadata;
    }

    public ON_PARSE_EXCEPTION getOnParseException() {
        return this.onParseException;
    }

    public void setEmitKey(EmitKey emitKey) {
        this.emitKey = emitKey;
    }

    public void setHandlerConfig(HandlerConfig handlerConfig) {
        this.handlerConfig = handlerConfig;
    }

    public HandlerConfig getHandlerConfig() {
        return this.handlerConfig == null ? HandlerConfig.DEFAULT_HANDLER_CONFIG : this.handlerConfig;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FetchEmitTuple that = (FetchEmitTuple)o;
        if (!Objects.equals(this.id, that.id)) {
            return false;
        }
        if (!Objects.equals(this.fetchKey, that.fetchKey)) {
            return false;
        }
        if (!Objects.equals(this.emitKey, that.emitKey)) {
            return false;
        }
        if (!Objects.equals(this.metadata, that.metadata)) {
            return false;
        }
        if (this.onParseException != that.onParseException) {
            return false;
        }
        return Objects.equals(this.handlerConfig, that.handlerConfig);
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (this.fetchKey != null ? this.fetchKey.hashCode() : 0);
        result = 31 * result + (this.emitKey != null ? this.emitKey.hashCode() : 0);
        result = 31 * result + (this.metadata != null ? this.metadata.hashCode() : 0);
        result = 31 * result + (this.onParseException != null ? this.onParseException.hashCode() : 0);
        result = 31 * result + (this.handlerConfig != null ? this.handlerConfig.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "FetchEmitTuple{id='" + this.id + '\'' + ", fetchKey=" + this.fetchKey + ", emitKey=" + this.emitKey + ", metadata=" + this.metadata + ", onParseException=" + (Object)((Object)this.onParseException) + ", handlerConfig=" + this.handlerConfig + '}';
    }

    public static enum ON_PARSE_EXCEPTION {
        SKIP,
        EMIT;

    }
}


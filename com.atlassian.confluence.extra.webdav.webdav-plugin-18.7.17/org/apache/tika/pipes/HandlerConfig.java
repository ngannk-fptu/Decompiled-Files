/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import org.apache.tika.sax.BasicContentHandlerFactory;

public class HandlerConfig
implements Serializable {
    private static final long serialVersionUID = -3861669115439125268L;
    public static final HandlerConfig DEFAULT_HANDLER_CONFIG = new HandlerConfig(BasicContentHandlerFactory.HANDLER_TYPE.TEXT, PARSE_MODE.RMETA, -1, -1, true);
    private BasicContentHandlerFactory.HANDLER_TYPE type = BasicContentHandlerFactory.HANDLER_TYPE.TEXT;
    int writeLimit = -1;
    int maxEmbeddedResources = -1;
    boolean throwOnWriteLimitReached = true;
    PARSE_MODE parseMode = PARSE_MODE.RMETA;

    public HandlerConfig(BasicContentHandlerFactory.HANDLER_TYPE type, PARSE_MODE parseMode, int writeLimit, int maxEmbeddedResources, boolean throwOnWriteLimitReached) {
        this.type = type;
        this.parseMode = parseMode;
        this.writeLimit = writeLimit;
        this.maxEmbeddedResources = maxEmbeddedResources;
        this.throwOnWriteLimitReached = throwOnWriteLimitReached;
    }

    public BasicContentHandlerFactory.HANDLER_TYPE getType() {
        return this.type;
    }

    public int getWriteLimit() {
        return this.writeLimit;
    }

    public int getMaxEmbeddedResources() {
        return this.maxEmbeddedResources;
    }

    public PARSE_MODE getParseMode() {
        return this.parseMode;
    }

    public boolean isThrowOnWriteLimitReached() {
        return this.throwOnWriteLimitReached;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HandlerConfig that = (HandlerConfig)o;
        return this.writeLimit == that.writeLimit && this.maxEmbeddedResources == that.maxEmbeddedResources && this.throwOnWriteLimitReached == that.throwOnWriteLimitReached && this.type == that.type && this.parseMode == that.parseMode;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.writeLimit, this.maxEmbeddedResources, this.throwOnWriteLimitReached, this.parseMode});
    }

    public String toString() {
        return "HandlerConfig{type=" + (Object)((Object)this.type) + ", writeLimit=" + this.writeLimit + ", maxEmbeddedResources=" + this.maxEmbeddedResources + ", throwOnWriteLimitReached=" + this.throwOnWriteLimitReached + ", parseMode=" + (Object)((Object)this.parseMode) + '}';
    }

    public static enum PARSE_MODE {
        RMETA,
        CONCATENATE;


        public static PARSE_MODE parseMode(String modeString) {
            for (PARSE_MODE m : PARSE_MODE.values()) {
                if (!m.name().equalsIgnoreCase(modeString)) continue;
                return m;
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (PARSE_MODE m : PARSE_MODE.values()) {
                if (i++ > 0) {
                    sb.append(", ");
                }
                sb.append(m.name().toLowerCase(Locale.US));
            }
            throw new IllegalArgumentException("mode must be one of: (" + sb + "). I regret I do not understand: " + modeString);
        }
    }
}


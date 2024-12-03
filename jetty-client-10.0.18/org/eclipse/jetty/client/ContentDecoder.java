/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import org.eclipse.jetty.client.HttpExchange;

public interface ContentDecoder {
    default public void beforeDecoding(HttpExchange exchange) {
    }

    public ByteBuffer decode(ByteBuffer var1);

    default public void release(ByteBuffer decoded) {
    }

    default public void afterDecoding(HttpExchange exchange) {
    }

    public static abstract class Factory {
        private final String encoding;

        protected Factory(String encoding) {
            this.encoding = encoding;
        }

        public String getEncoding() {
            return this.encoding;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Factory)) {
                return false;
            }
            Factory that = (Factory)obj;
            return this.encoding.equals(that.encoding);
        }

        public int hashCode() {
            return this.encoding.hashCode();
        }

        public abstract ContentDecoder newContentDecoder();
    }
}


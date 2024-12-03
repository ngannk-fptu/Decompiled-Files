/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw;

import java.util.Collections;
import java.util.function.Supplier;

public interface EmbeddedExtractor {
    default public Iterable<EmbeddedPart> getEmbeddings() {
        return Collections.emptyList();
    }

    public static class EmbeddedPart {
        private String name;
        private Supplier<byte[]> data;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Supplier<byte[]> getData() {
            return this.data;
        }

        public void setData(Supplier<byte[]> data) {
            this.data = data;
        }
    }
}


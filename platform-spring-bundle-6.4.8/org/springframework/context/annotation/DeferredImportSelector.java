/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

public interface DeferredImportSelector
extends ImportSelector {
    @Nullable
    default public Class<? extends Group> getImportGroup() {
        return null;
    }

    public static interface Group {
        public void process(AnnotationMetadata var1, DeferredImportSelector var2);

        public Iterable<Entry> selectImports();

        public static class Entry {
            private final AnnotationMetadata metadata;
            private final String importClassName;

            public Entry(AnnotationMetadata metadata, String importClassName) {
                this.metadata = metadata;
                this.importClassName = importClassName;
            }

            public AnnotationMetadata getMetadata() {
                return this.metadata;
            }

            public String getImportClassName() {
                return this.importClassName;
            }

            public boolean equals(@Nullable Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || this.getClass() != other.getClass()) {
                    return false;
                }
                Entry entry = (Entry)other;
                return this.metadata.equals(entry.metadata) && this.importClassName.equals(entry.importClassName);
            }

            public int hashCode() {
                return this.metadata.hashCode() * 31 + this.importClassName.hashCode();
            }

            public String toString() {
                return this.importClassName;
            }
        }
    }
}


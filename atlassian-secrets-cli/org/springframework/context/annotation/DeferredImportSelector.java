/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.Objects;
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

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                Entry entry = (Entry)o;
                return Objects.equals(this.metadata, entry.metadata) && Objects.equals(this.importClassName, entry.importClassName);
            }

            public int hashCode() {
                return Objects.hash(this.metadata, this.importClassName);
            }
        }
    }
}


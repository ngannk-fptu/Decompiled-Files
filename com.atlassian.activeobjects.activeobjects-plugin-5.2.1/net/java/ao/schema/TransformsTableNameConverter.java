/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.util.List;
import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.schema.CanonicalClassNameTableNameConverter;

abstract class TransformsTableNameConverter
extends CanonicalClassNameTableNameConverter {
    private List<Transform> transforms;
    private CanonicalClassNameTableNameConverter delegateTableNameConverter;

    TransformsTableNameConverter(List<Transform> transforms, CanonicalClassNameTableNameConverter delegateTableNameConverter) {
        this.transforms = Objects.requireNonNull(transforms, "transforms can't be null");
        this.delegateTableNameConverter = Objects.requireNonNull(delegateTableNameConverter, "delegateTableNameConverter can't be null");
    }

    @Override
    protected final String getName(String entityClassCanonicalName) {
        return this.delegateTableNameConverter.getName(this.transform(entityClassCanonicalName));
    }

    private String transform(String entityClassCanonicalName) {
        for (Transform transform : this.transforms) {
            if (!transform.accept(entityClassCanonicalName)) continue;
            return transform.apply(entityClassCanonicalName);
        }
        return entityClassCanonicalName;
    }

    static final class ClassNameTableNameConverter
    extends CanonicalClassNameTableNameConverter {
        ClassNameTableNameConverter() {
        }

        @Override
        protected String getName(String entityClassCanonicalName) {
            return Common.convertSimpleClassName(entityClassCanonicalName);
        }
    }

    static interface Transform {
        public boolean accept(String var1);

        public String apply(String var1);
    }
}


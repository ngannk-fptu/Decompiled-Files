/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.util.List;
import net.java.ao.Common;
import net.java.ao.schema.AbstractFieldNameConverter;
import net.java.ao.schema.FieldNameResolver;

public final class CamelCaseFieldNameConverter
extends AbstractFieldNameConverter {
    public CamelCaseFieldNameConverter() {
    }

    public CamelCaseFieldNameConverter(List<FieldNameResolver> fieldNameResolvers) {
        super(fieldNameResolvers);
    }

    @Override
    public String convertName(String name) {
        return Common.convertDowncaseName(name);
    }
}


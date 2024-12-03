/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import net.java.ao.Common;
import net.java.ao.schema.CanonicalClassNameTableNameConverter;

public final class CamelCaseTableNameConverter
extends CanonicalClassNameTableNameConverter {
    @Override
    protected String getName(String entityClassCanonicalName) {
        return Common.convertDowncaseName(Common.convertSimpleClassName(entityClassCanonicalName));
    }
}


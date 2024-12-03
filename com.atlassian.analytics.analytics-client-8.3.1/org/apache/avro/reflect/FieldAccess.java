/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.lang.reflect.Field;
import org.apache.avro.reflect.FieldAccessor;

abstract class FieldAccess {
    FieldAccess() {
    }

    protected abstract FieldAccessor getAccessor(Field var1);
}


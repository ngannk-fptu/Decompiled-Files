/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import net.sf.ehcache.writer.writebehind.OperationConverter;
import net.sf.ehcache.writer.writebehind.operations.KeyBasedOperation;

public final class CastingOperationConverter
implements OperationConverter<KeyBasedOperation> {
    private static final CastingOperationConverter INSTANCE = new CastingOperationConverter();

    private CastingOperationConverter() {
    }

    public static CastingOperationConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public KeyBasedOperation convert(Object source) {
        return (KeyBasedOperation)source;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import java.util.List;
import net.sf.ehcache.writer.writebehind.OperationConverter;

public interface OperationsFilter<T> {
    public void filter(List var1, OperationConverter<T> var2);
}


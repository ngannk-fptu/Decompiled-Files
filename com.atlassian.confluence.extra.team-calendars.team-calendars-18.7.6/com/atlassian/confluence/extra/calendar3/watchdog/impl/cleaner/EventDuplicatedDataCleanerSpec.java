/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner;

import java.util.Collection;

public interface EventDuplicatedDataCleanerSpec<T> {
    public Collection<T> getDuplicatedDTOs(int var1, int var2);

    public long getDuplicatedCountPerDTO(T var1);

    public long deleteDuplicatedRow(int var1, T var2);
}


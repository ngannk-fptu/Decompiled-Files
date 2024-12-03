/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.model;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2={"Lcom/addonengine/addons/analytics/service/model/CountType;", "", "(Ljava/lang/String;I)V", "TOTAL", "UNIQUE", "analytics"})
public final class CountType
extends Enum<CountType> {
    public static final /* enum */ CountType TOTAL = new CountType();
    public static final /* enum */ CountType UNIQUE = new CountType();
    private static final /* synthetic */ CountType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static CountType[] values() {
        return (CountType[])$VALUES.clone();
    }

    public static CountType valueOf(String value) {
        return Enum.valueOf(CountType.class, value);
    }

    @NotNull
    public static EnumEntries<CountType> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = countTypeArray = new CountType[]{CountType.TOTAL, CountType.UNIQUE};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


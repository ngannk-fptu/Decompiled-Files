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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/model/SpaceSortField;", "", "(Ljava/lang/String;I)V", "SPACE_NAME", "VIEWED_COUNT", "CREATED_COUNT", "UPDATED_COUNT", "VIEWED_USERS", "VIEWED_LAST_DATE", "analytics"})
public final class SpaceSortField
extends Enum<SpaceSortField> {
    public static final /* enum */ SpaceSortField SPACE_NAME = new SpaceSortField();
    public static final /* enum */ SpaceSortField VIEWED_COUNT = new SpaceSortField();
    public static final /* enum */ SpaceSortField CREATED_COUNT = new SpaceSortField();
    public static final /* enum */ SpaceSortField UPDATED_COUNT = new SpaceSortField();
    public static final /* enum */ SpaceSortField VIEWED_USERS = new SpaceSortField();
    public static final /* enum */ SpaceSortField VIEWED_LAST_DATE = new SpaceSortField();
    private static final /* synthetic */ SpaceSortField[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static SpaceSortField[] values() {
        return (SpaceSortField[])$VALUES.clone();
    }

    public static SpaceSortField valueOf(String value) {
        return Enum.valueOf(SpaceSortField.class, value);
    }

    @NotNull
    public static EnumEntries<SpaceSortField> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = spaceSortFieldArray = new SpaceSortField[]{SpaceSortField.SPACE_NAME, SpaceSortField.VIEWED_COUNT, SpaceSortField.CREATED_COUNT, SpaceSortField.UPDATED_COUNT, SpaceSortField.VIEWED_USERS, SpaceSortField.VIEWED_LAST_DATE};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


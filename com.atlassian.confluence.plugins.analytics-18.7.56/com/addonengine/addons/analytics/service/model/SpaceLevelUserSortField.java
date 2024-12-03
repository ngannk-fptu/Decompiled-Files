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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserSortField;", "", "(Ljava/lang/String;I)V", "VIEWED_COUNT", "CREATED_COUNT", "UPDATED_COUNT", "COMMENTS_COUNT", "CONTRIBUTOR_SCORE", "analytics"})
public final class SpaceLevelUserSortField
extends Enum<SpaceLevelUserSortField> {
    public static final /* enum */ SpaceLevelUserSortField VIEWED_COUNT = new SpaceLevelUserSortField();
    public static final /* enum */ SpaceLevelUserSortField CREATED_COUNT = new SpaceLevelUserSortField();
    public static final /* enum */ SpaceLevelUserSortField UPDATED_COUNT = new SpaceLevelUserSortField();
    public static final /* enum */ SpaceLevelUserSortField COMMENTS_COUNT = new SpaceLevelUserSortField();
    public static final /* enum */ SpaceLevelUserSortField CONTRIBUTOR_SCORE = new SpaceLevelUserSortField();
    private static final /* synthetic */ SpaceLevelUserSortField[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static SpaceLevelUserSortField[] values() {
        return (SpaceLevelUserSortField[])$VALUES.clone();
    }

    public static SpaceLevelUserSortField valueOf(String value) {
        return Enum.valueOf(SpaceLevelUserSortField.class, value);
    }

    @NotNull
    public static EnumEntries<SpaceLevelUserSortField> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = spaceLevelUserSortFieldArray = new SpaceLevelUserSortField[]{SpaceLevelUserSortField.VIEWED_COUNT, SpaceLevelUserSortField.CREATED_COUNT, SpaceLevelUserSortField.UPDATED_COUNT, SpaceLevelUserSortField.COMMENTS_COUNT, SpaceLevelUserSortField.CONTRIBUTOR_SCORE};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


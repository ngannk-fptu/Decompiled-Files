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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2={"Lcom/addonengine/addons/analytics/service/model/ContentSortField;", "", "(Ljava/lang/String;I)V", "CONTENT_NAME", "CREATED_DATE", "MODIFIED_LAST_DATE", "VIEWED_LAST_DATE", "COMMENTS_COUNT", "VIEWED_USERS", "VIEWED_COUNT", "analytics"})
public final class ContentSortField
extends Enum<ContentSortField> {
    public static final /* enum */ ContentSortField CONTENT_NAME = new ContentSortField();
    public static final /* enum */ ContentSortField CREATED_DATE = new ContentSortField();
    public static final /* enum */ ContentSortField MODIFIED_LAST_DATE = new ContentSortField();
    public static final /* enum */ ContentSortField VIEWED_LAST_DATE = new ContentSortField();
    public static final /* enum */ ContentSortField COMMENTS_COUNT = new ContentSortField();
    public static final /* enum */ ContentSortField VIEWED_USERS = new ContentSortField();
    public static final /* enum */ ContentSortField VIEWED_COUNT = new ContentSortField();
    private static final /* synthetic */ ContentSortField[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static ContentSortField[] values() {
        return (ContentSortField[])$VALUES.clone();
    }

    public static ContentSortField valueOf(String value) {
        return Enum.valueOf(ContentSortField.class, value);
    }

    @NotNull
    public static EnumEntries<ContentSortField> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = contentSortFieldArray = new ContentSortField[]{ContentSortField.CONTENT_NAME, ContentSortField.CREATED_DATE, ContentSortField.MODIFIED_LAST_DATE, ContentSortField.VIEWED_LAST_DATE, ContentSortField.COMMENTS_COUNT, ContentSortField.VIEWED_USERS, ContentSortField.VIEWED_COUNT};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/model/ContentType;", "", "contentTableValue", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getContentTableValue", "()Ljava/lang/String;", "PAGE", "BLOG", "analytics"})
public final class ContentType
extends Enum<ContentType> {
    @NotNull
    private final String contentTableValue;
    public static final /* enum */ ContentType PAGE = new ContentType("PAGE");
    public static final /* enum */ ContentType BLOG = new ContentType("BLOGPOST");
    private static final /* synthetic */ ContentType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private ContentType(String contentTableValue) {
        this.contentTableValue = contentTableValue;
    }

    @NotNull
    public final String getContentTableValue() {
        return this.contentTableValue;
    }

    public static ContentType[] values() {
        return (ContentType[])$VALUES.clone();
    }

    public static ContentType valueOf(String value) {
        return Enum.valueOf(ContentType.class, value);
    }

    @NotNull
    public static EnumEntries<ContentType> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = contentTypeArray = new ContentType[]{ContentType.PAGE, ContentType.BLOG};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


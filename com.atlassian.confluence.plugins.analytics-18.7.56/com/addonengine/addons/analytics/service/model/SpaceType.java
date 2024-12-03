/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceType
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.model;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/model/SpaceType;", "", "confluenceType", "Lcom/atlassian/confluence/spaces/SpaceType;", "(Ljava/lang/String;ILcom/atlassian/confluence/spaces/SpaceType;)V", "getConfluenceType", "()Lcom/atlassian/confluence/spaces/SpaceType;", "GLOBAL", "PERSONAL", "analytics"})
public final class SpaceType
extends Enum<SpaceType> {
    @NotNull
    private final com.atlassian.confluence.spaces.SpaceType confluenceType;
    public static final /* enum */ SpaceType GLOBAL;
    public static final /* enum */ SpaceType PERSONAL;
    private static final /* synthetic */ SpaceType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private SpaceType(com.atlassian.confluence.spaces.SpaceType confluenceType) {
        this.confluenceType = confluenceType;
    }

    @NotNull
    public final com.atlassian.confluence.spaces.SpaceType getConfluenceType() {
        return this.confluenceType;
    }

    public static SpaceType[] values() {
        return (SpaceType[])$VALUES.clone();
    }

    public static SpaceType valueOf(String value) {
        return Enum.valueOf(SpaceType.class, value);
    }

    @NotNull
    public static EnumEntries<SpaceType> getEntries() {
        return $ENTRIES;
    }

    static {
        com.atlassian.confluence.spaces.SpaceType spaceType = com.atlassian.confluence.spaces.SpaceType.GLOBAL;
        Intrinsics.checkNotNullExpressionValue((Object)spaceType, (String)"GLOBAL");
        GLOBAL = new SpaceType(spaceType);
        com.atlassian.confluence.spaces.SpaceType spaceType2 = com.atlassian.confluence.spaces.SpaceType.PERSONAL;
        Intrinsics.checkNotNullExpressionValue((Object)spaceType2, (String)"PERSONAL");
        PERSONAL = new SpaceType(spaceType2);
        $VALUES = spaceTypeArray = new SpaceType[]{SpaceType.GLOBAL, SpaceType.PERSONAL};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


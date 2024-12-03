/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.confluence.model;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "", "(Ljava/lang/String;I)V", "AUTHENTICATED", "ANONYMOUS", "ANONYMISED", "UNKNOWN", "analytics"})
public final class UserType
extends Enum<UserType> {
    public static final /* enum */ UserType AUTHENTICATED = new UserType();
    public static final /* enum */ UserType ANONYMOUS = new UserType();
    public static final /* enum */ UserType ANONYMISED = new UserType();
    public static final /* enum */ UserType UNKNOWN = new UserType();
    private static final /* synthetic */ UserType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static UserType[] values() {
        return (UserType[])$VALUES.clone();
    }

    public static UserType valueOf(String value) {
        return Enum.valueOf(UserType.class, value);
    }

    @NotNull
    public static EnumEntries<UserType> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = userTypeArray = new UserType[]{UserType.AUTHENTICATED, UserType.ANONYMOUS, UserType.ANONYMISED, UserType.UNKNOWN};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


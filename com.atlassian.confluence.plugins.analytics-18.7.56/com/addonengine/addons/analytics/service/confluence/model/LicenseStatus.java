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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b\u00a8\u0006\f"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/model/LicenseStatus;", "", "(Ljava/lang/String;I)V", "VALID", "UNLICENSED", "EXPIRED", "TYPE_MISMATCH", "USER_MISMATCH", "EDITION_MISMATCH", "ROLE_EXCEEDED", "ROLE_UNDEFINED", "VERSION_MISMATCH", "analytics"})
public final class LicenseStatus
extends Enum<LicenseStatus> {
    public static final /* enum */ LicenseStatus VALID = new LicenseStatus();
    public static final /* enum */ LicenseStatus UNLICENSED = new LicenseStatus();
    public static final /* enum */ LicenseStatus EXPIRED = new LicenseStatus();
    public static final /* enum */ LicenseStatus TYPE_MISMATCH = new LicenseStatus();
    public static final /* enum */ LicenseStatus USER_MISMATCH = new LicenseStatus();
    public static final /* enum */ LicenseStatus EDITION_MISMATCH = new LicenseStatus();
    public static final /* enum */ LicenseStatus ROLE_EXCEEDED = new LicenseStatus();
    public static final /* enum */ LicenseStatus ROLE_UNDEFINED = new LicenseStatus();
    public static final /* enum */ LicenseStatus VERSION_MISMATCH = new LicenseStatus();
    private static final /* synthetic */ LicenseStatus[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static LicenseStatus[] values() {
        return (LicenseStatus[])$VALUES.clone();
    }

    public static LicenseStatus valueOf(String value) {
        return Enum.valueOf(LicenseStatus.class, value);
    }

    @NotNull
    public static EnumEntries<LicenseStatus> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = licenseStatusArray = new LicenseStatus[]{LicenseStatus.VALID, LicenseStatus.UNLICENSED, LicenseStatus.EXPIRED, LicenseStatus.TYPE_MISMATCH, LicenseStatus.USER_MISMATCH, LicenseStatus.EDITION_MISMATCH, LicenseStatus.ROLE_EXCEEDED, LicenseStatus.ROLE_UNDEFINED, LicenseStatus.VERSION_MISMATCH};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


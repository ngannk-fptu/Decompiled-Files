/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.sql.DatePart
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.model;

import com.querydsl.sql.DatePart;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/service/model/PeriodOption;", "", "datePart", "Lcom/querydsl/sql/DatePart;", "(Ljava/lang/String;ILcom/querydsl/sql/DatePart;)V", "getDatePart", "()Lcom/querydsl/sql/DatePart;", "HOUR", "DAY", "WEEK", "MONTH", "analytics"})
public final class PeriodOption
extends Enum<PeriodOption> {
    @NotNull
    private final DatePart datePart;
    public static final /* enum */ PeriodOption HOUR = new PeriodOption(DatePart.hour);
    public static final /* enum */ PeriodOption DAY = new PeriodOption(DatePart.day);
    public static final /* enum */ PeriodOption WEEK = new PeriodOption(DatePart.week);
    public static final /* enum */ PeriodOption MONTH = new PeriodOption(DatePart.month);
    private static final /* synthetic */ PeriodOption[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private PeriodOption(DatePart datePart) {
        this.datePart = datePart;
    }

    @NotNull
    public final DatePart getDatePart() {
        return this.datePart;
    }

    public static PeriodOption[] values() {
        return (PeriodOption[])$VALUES.clone();
    }

    public static PeriodOption valueOf(String value) {
        return Enum.valueOf(PeriodOption.class, value);
    }

    @NotNull
    public static EnumEntries<PeriodOption> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = periodOptionArray = new PeriodOption[]{PeriodOption.HOUR, PeriodOption.DAY, PeriodOption.WEEK, PeriodOption.MONTH};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


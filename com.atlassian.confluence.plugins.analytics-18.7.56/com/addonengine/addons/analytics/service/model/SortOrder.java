/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.Order
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.model;

import com.querydsl.core.types.Order;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/model/SortOrder;", "", "dslOrder", "Lcom/querydsl/core/types/Order;", "(Ljava/lang/String;ILcom/querydsl/core/types/Order;)V", "getDslOrder", "()Lcom/querydsl/core/types/Order;", "ASC", "DESC", "analytics"})
public final class SortOrder
extends Enum<SortOrder> {
    @NotNull
    private final Order dslOrder;
    public static final /* enum */ SortOrder ASC = new SortOrder(Order.ASC);
    public static final /* enum */ SortOrder DESC = new SortOrder(Order.DESC);
    private static final /* synthetic */ SortOrder[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private SortOrder(Order dslOrder) {
        this.dslOrder = dslOrder;
    }

    @NotNull
    public final Order getDslOrder() {
        return this.dslOrder;
    }

    public static SortOrder[] values() {
        return (SortOrder[])$VALUES.clone();
    }

    public static SortOrder valueOf(String value) {
        return Enum.valueOf(SortOrder.class, value);
    }

    @NotNull
    public static EnumEntries<SortOrder> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = sortOrderArray = new SortOrder[]{SortOrder.ASC, SortOrder.DESC};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}


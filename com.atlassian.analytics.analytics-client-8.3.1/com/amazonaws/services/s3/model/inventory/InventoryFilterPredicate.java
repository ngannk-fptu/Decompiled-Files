/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.inventory;

import com.amazonaws.services.s3.model.inventory.InventoryPredicateVisitor;
import java.io.Serializable;

public abstract class InventoryFilterPredicate
implements Serializable {
    public abstract void accept(InventoryPredicateVisitor var1);
}


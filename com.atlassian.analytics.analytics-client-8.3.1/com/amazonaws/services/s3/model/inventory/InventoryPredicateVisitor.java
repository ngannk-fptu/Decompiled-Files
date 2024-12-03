/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.inventory;

import com.amazonaws.services.s3.model.inventory.InventoryPrefixPredicate;

public interface InventoryPredicateVisitor {
    public void visit(InventoryPrefixPredicate var1);
}


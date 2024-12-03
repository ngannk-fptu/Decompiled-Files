/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.zip;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class NestedProgressTracker<O, I> {
    public void process(List<O> outerItems) throws InterruptedException {
        if (outerItems.isEmpty()) {
            return;
        }
        int outerProgressShare = 100 / outerItems.size();
        for (int i = 0; i < outerItems.size(); ++i) {
            O outerItem = outerItems.get(i);
            int outerProgress = 100 * i / outerItems.size();
            this.recordOuterProgress(outerProgress, outerItem);
            Collection<I> innerItems = this.getInnerItems(outerItem);
            if (innerItems.isEmpty()) continue;
            int innerItemsProcessed = 0;
            int innerProgressShare = outerProgressShare / innerItems.size();
            for (I innerItem : innerItems) {
                int innerProgress = innerProgressShare * innerItemsProcessed++;
                this.processInnerItem(outerItem, innerItem, outerProgress + innerProgress);
            }
        }
    }

    @Nonnull
    protected abstract Collection<I> getInnerItems(O var1);

    protected abstract void recordOuterProgress(int var1, O var2);

    protected abstract void processInnerItem(O var1, I var2, int var3) throws InterruptedException;
}


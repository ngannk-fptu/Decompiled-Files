/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.predicate;

import com.atlassian.analytics.client.pipeline.predicate.CanHandleEventPredicate;

public class AllEventsPredicate
implements CanHandleEventPredicate {
    @Override
    public boolean canHandleEvent(Object o) {
        return true;
    }
}


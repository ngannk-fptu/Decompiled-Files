/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.criterion.AggregateProjection;

public class AvgProjection
extends AggregateProjection {
    public AvgProjection(String propertyName) {
        super("avg", propertyName);
    }
}


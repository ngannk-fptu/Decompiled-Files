/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.criterion.AbstractEmptinessExpression;
import org.hibernate.criterion.Criterion;

public class NotEmptyExpression
extends AbstractEmptinessExpression
implements Criterion {
    protected NotEmptyExpression(String propertyName) {
        super(propertyName);
    }

    @Override
    protected boolean excludeEmpty() {
        return true;
    }
}


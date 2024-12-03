/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;

public class Conjunction
extends Junction {
    public Conjunction() {
        super(Junction.Nature.AND);
    }

    protected Conjunction(Criterion ... criterion) {
        super(Junction.Nature.AND, criterion);
    }
}


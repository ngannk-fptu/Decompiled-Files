/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;

public class Disjunction
extends Junction {
    protected Disjunction() {
        super(Junction.Nature.OR);
    }

    protected Disjunction(Criterion[] conditions) {
        super(Junction.Nature.OR, conditions);
    }
}


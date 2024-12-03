/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql.ordering.antlr;

import org.hibernate.sql.ordering.antlr.NodeSupport;

public class OrderingSpecification
extends NodeSupport {
    private boolean resolved;
    private Ordering ordering;

    public Ordering getOrdering() {
        if (!this.resolved) {
            this.ordering = OrderingSpecification.resolve(this.getText());
            this.resolved = true;
        }
        return this.ordering;
    }

    private static Ordering resolve(String text) {
        if (Ordering.ASCENDING.name.equals(text)) {
            return Ordering.ASCENDING;
        }
        if (Ordering.DESCENDING.name.equals(text)) {
            return Ordering.DESCENDING;
        }
        throw new IllegalStateException("Unknown ordering [" + text + "]");
    }

    @Override
    public String getRenderableText() {
        return this.getOrdering().name;
    }

    public static class Ordering {
        public static final Ordering ASCENDING = new Ordering("asc");
        public static final Ordering DESCENDING = new Ordering("desc");
        private final String name;

        private Ordering(String name) {
            this.name = name;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.sql.ordering.antlr;

import antlr.collections.AST;
import org.hibernate.sql.ordering.antlr.CollationSpecification;
import org.hibernate.sql.ordering.antlr.NodeSupport;
import org.hibernate.sql.ordering.antlr.OrderingSpecification;
import org.hibernate.sql.ordering.antlr.SortKey;

public class SortSpecification
extends NodeSupport {
    public SortKey getSortKey() {
        return (SortKey)this.getFirstChild();
    }

    public CollationSpecification getCollation() {
        AST possible = this.getSortKey().getNextSibling();
        return possible != null && 13 == possible.getType() ? (CollationSpecification)possible : null;
    }

    public OrderingSpecification getOrdering() {
        AST possible = this.getSortKey().getNextSibling();
        if (possible == null) {
            return null;
        }
        if (13 == possible.getType()) {
            possible = possible.getNextSibling();
        }
        return possible != null && 6 == possible.getType() ? (OrderingSpecification)possible : null;
    }
}


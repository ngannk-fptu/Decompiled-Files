/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 */
package org.hibernate.sql.ordering.antlr;

import antlr.ASTFactory;
import org.hibernate.sql.ordering.antlr.CollationSpecification;
import org.hibernate.sql.ordering.antlr.NodeSupport;
import org.hibernate.sql.ordering.antlr.OrderByFragment;
import org.hibernate.sql.ordering.antlr.OrderByTemplateTokenTypes;
import org.hibernate.sql.ordering.antlr.OrderingSpecification;
import org.hibernate.sql.ordering.antlr.SortKey;
import org.hibernate.sql.ordering.antlr.SortSpecification;

public class Factory
extends ASTFactory
implements OrderByTemplateTokenTypes {
    public Class getASTNodeType(int i) {
        switch (i) {
            case 4: {
                return OrderByFragment.class;
            }
            case 5: {
                return SortSpecification.class;
            }
            case 6: {
                return OrderingSpecification.class;
            }
            case 13: {
                return CollationSpecification.class;
            }
            case 8: {
                return SortKey.class;
            }
        }
        return NodeSupport.class;
    }
}


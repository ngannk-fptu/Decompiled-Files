/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 *  org.jboss.logging.Logger
 */
package org.hibernate.sql.ordering.antlr;

import antlr.collections.AST;
import org.hibernate.NullPrecedence;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.sql.ordering.antlr.GeneratedOrderByFragmentRenderer;
import org.hibernate.sql.ordering.antlr.Node;
import org.jboss.logging.Logger;

public class OrderByFragmentRenderer
extends GeneratedOrderByFragmentRenderer {
    private static final Logger LOG = Logger.getLogger((String)OrderByFragmentRenderer.class.getName());
    private final SessionFactoryImplementor sessionFactory;
    private int traceDepth = 0;

    public OrderByFragmentRenderer(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void out(AST ast) {
        this.out(((Node)ast).getRenderableText());
    }

    public void traceIn(String ruleName, AST tree) {
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = StringHelper.repeat('-', this.traceDepth++ * 2) + "-> ";
        String traceText = ruleName + " (" + this.buildTraceNodeName(tree) + ")";
        LOG.trace((Object)(prefix + traceText));
    }

    private String buildTraceNodeName(AST tree) {
        return tree == null ? "???" : tree.getText() + " [" + TokenPrinters.ORDERBY_FRAGMENT_PRINTER.getTokenTypeName(tree.getType()) + "]";
    }

    public void traceOut(String ruleName, AST tree) {
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = "<-" + StringHelper.repeat('-', --this.traceDepth * 2) + " ";
        LOG.trace((Object)(prefix + ruleName));
    }

    @Override
    protected String renderOrderByElement(String expression, String collation, String order, String nulls) {
        NullPrecedence nullPrecedence = NullPrecedence.parse(nulls, this.sessionFactory.getSessionFactoryOptions().getDefaultNullPrecedence());
        return this.sessionFactory.getDialect().renderOrderByElement(expression, collation, order, nullPrecedence);
    }
}


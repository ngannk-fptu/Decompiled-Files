/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.TokenStream
 *  org.jboss.logging.Logger
 */
package org.hibernate.sql.ordering.antlr;

import antlr.TokenStream;
import java.io.StringReader;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.sql.ordering.antlr.GeneratedOrderByLexer;
import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
import org.hibernate.sql.ordering.antlr.OrderByFragmentParser;
import org.hibernate.sql.ordering.antlr.OrderByFragmentRenderer;
import org.hibernate.sql.ordering.antlr.OrderByTranslation;
import org.hibernate.sql.ordering.antlr.TranslationContext;
import org.jboss.logging.Logger;

public class OrderByFragmentTranslator {
    private static final Logger LOG = Logger.getLogger((String)OrderByFragmentTranslator.class.getName());

    public static OrderByTranslation translate(TranslationContext context, String fragment) {
        LOG.tracef("Beginning parsing of order-by fragment : %s", (Object)fragment);
        GeneratedOrderByLexer lexer = new GeneratedOrderByLexer(new StringReader(fragment));
        OrderByFragmentParser parser = new OrderByFragmentParser((TokenStream)lexer, context);
        try {
            parser.orderByFragment();
        }
        catch (HibernateException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new HibernateException("Unable to parse order-by fragment", t);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)TokenPrinters.ORDERBY_FRAGMENT_PRINTER.showAsString(parser.getAST(), "--- {order-by fragment} ---"));
        }
        OrderByFragmentRenderer renderer = new OrderByFragmentRenderer(context.getSessionFactory());
        try {
            renderer.orderByFragment(parser.getAST());
        }
        catch (HibernateException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new HibernateException("Unable to render parsed order-by fragment", t);
        }
        return new StandardOrderByTranslationImpl(renderer.getRenderedFragment(), parser.getColumnReferences());
    }

    public static class StandardOrderByTranslationImpl
    implements OrderByTranslation {
        private final String sqlTemplate;
        private final Set<String> columnReferences;

        public StandardOrderByTranslationImpl(String sqlTemplate, Set<String> columnReferences) {
            this.sqlTemplate = sqlTemplate;
            this.columnReferences = columnReferences;
        }

        @Override
        public String injectAliases(OrderByAliasResolver aliasResolver) {
            String sql = this.sqlTemplate;
            for (String columnReference : this.columnReferences) {
                String replacementToken = "{" + columnReference + "}";
                sql = sql.replace(replacementToken, aliasResolver.resolveTableAlias(columnReference) + '.' + columnReference);
            }
            return sql;
        }
    }
}


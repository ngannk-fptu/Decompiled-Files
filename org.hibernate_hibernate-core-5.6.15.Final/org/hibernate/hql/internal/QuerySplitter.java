/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.classic.ParserHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;

public final class QuerySplitter {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(QuerySplitter.class);
    private static final Set<String> BEFORE_CLASS_TOKENS = new HashSet<String>();
    private static final Set<String> NOT_AFTER_CLASS_TOKENS = new HashSet<String>();

    private QuerySplitter() {
    }

    public static String[] concreteQueries(String query, SessionFactoryImplementor factory) throws MappingException {
        String[] tokens = StringHelper.split(" \n\r\f\t(),", query, true);
        if (tokens.length == 0) {
            return new String[]{query};
        }
        ArrayList<String> placeholders = new ArrayList<String>();
        ArrayList<String[]> replacements = new ArrayList<String[]>();
        StringBuilder templateQuery = new StringBuilder(40);
        int start = QuerySplitter.getStartingPositionFor(tokens, templateQuery);
        int count = 0;
        String last = tokens[start - 1].toLowerCase(Locale.ROOT);
        boolean inQuote = false;
        for (int i = start; i < tokens.length; ++i) {
            String importedClassName;
            String token = tokens[i];
            if (ParserHelper.isWhitespace(token)) {
                templateQuery.append(token);
                continue;
            }
            if (QuerySplitter.isQuoteCharacter(token)) {
                inQuote = !inQuote;
                templateQuery.append(token);
                continue;
            }
            if (QuerySplitter.isTokenStartWithAQuoteCharacter(token)) {
                if (!QuerySplitter.isTokenEndWithAQuoteCharacter(token)) {
                    inQuote = true;
                }
                templateQuery.append(token);
                continue;
            }
            if (QuerySplitter.isTokenEndWithAQuoteCharacter(token)) {
                inQuote = false;
                templateQuery.append(token);
                continue;
            }
            if (inQuote) {
                templateQuery.append(token);
                continue;
            }
            String next = QuerySplitter.nextNonWhite(tokens, i).toLowerCase(Locale.ROOT);
            boolean process = QuerySplitter.isJavaIdentifier(token) && QuerySplitter.isPossiblyClassName(last, next);
            last = token.toLowerCase(Locale.ROOT);
            if (process && (importedClassName = QuerySplitter.getImportedClass(token, factory)) != null) {
                String[] implementors = factory.getImplementors(importedClassName);
                token = "$clazz" + count++ + "$";
                if (implementors != null) {
                    placeholders.add(token);
                    replacements.add(implementors);
                }
            }
            templateQuery.append(token);
        }
        String[] results = StringHelper.multiply(templateQuery.toString(), placeholders.iterator(), replacements.iterator());
        if (results.length == 0) {
            LOG.noPersistentClassesFound(query);
        }
        return results;
    }

    private static boolean isQuoteCharacter(String token) {
        return "'".equals(token) || "\"".equals(token);
    }

    private static boolean isTokenStartWithAQuoteCharacter(String token) {
        return token.startsWith("'") || token.startsWith("\"");
    }

    private static boolean isTokenEndWithAQuoteCharacter(String token) {
        return token.endsWith("'") || token.endsWith("\"");
    }

    private static String nextNonWhite(String[] tokens, int start) {
        for (int i = start + 1; i < tokens.length; ++i) {
            if (ParserHelper.isWhitespace(tokens[i])) continue;
            return tokens[i];
        }
        return tokens[tokens.length - 1];
    }

    private static int getStartingPositionFor(String[] tokens, StringBuilder templateQuery) {
        templateQuery.append(tokens[0]);
        if (!"select".equals(tokens[0].toLowerCase(Locale.ROOT))) {
            return 1;
        }
        for (int i = 1; i < tokens.length; ++i) {
            if ("from".equals(tokens[i].toLowerCase(Locale.ROOT))) {
                return i;
            }
            templateQuery.append(tokens[i]);
        }
        return tokens.length;
    }

    private static boolean isPossiblyClassName(String last, String next) {
        return "class".equals(last) || BEFORE_CLASS_TOKENS.contains(last) && !NOT_AFTER_CLASS_TOKENS.contains(next);
    }

    private static boolean isJavaIdentifier(String token) {
        return Character.isJavaIdentifierStart(token.charAt(0));
    }

    public static String getImportedClass(String name, SessionFactoryImplementor factory) {
        return factory.getMetamodel().getImportedClassName(name);
    }

    static {
        BEFORE_CLASS_TOKENS.add("from");
        BEFORE_CLASS_TOKENS.add("delete");
        BEFORE_CLASS_TOKENS.add("update");
        BEFORE_CLASS_TOKENS.add(",");
        BEFORE_CLASS_TOKENS.add("join");
        NOT_AFTER_CLASS_TOKENS.add("in");
        NOT_AFTER_CLASS_TOKENS.add("from");
        NOT_AFTER_CLASS_TOKENS.add(")");
    }
}


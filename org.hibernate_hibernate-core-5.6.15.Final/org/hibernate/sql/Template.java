/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.sql.ordering.antlr.ColumnMapper;
import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
import org.hibernate.sql.ordering.antlr.OrderByFragmentTranslator;
import org.hibernate.sql.ordering.antlr.OrderByTranslation;
import org.hibernate.sql.ordering.antlr.SqlValueReference;
import org.hibernate.sql.ordering.antlr.TranslationContext;

public final class Template {
    private static final Set<String> KEYWORDS = new HashSet<String>();
    private static final Set<String> BEFORE_TABLE_KEYWORDS = new HashSet<String>();
    private static final Set<String> FUNCTION_KEYWORDS = new HashSet<String>();
    public static final String TEMPLATE = "$PlaceHolder$";
    public static final OrderByAliasResolver LEGACY_ORDER_BY_ALIAS_RESOLVER;

    private Template() {
    }

    public static String renderTransformerReadFragment(String fragment, String ... columnNames) {
        for (String columnName : columnNames) {
            fragment = fragment.replace(columnName, "$PlaceHolder$." + columnName);
        }
        return fragment;
    }

    public static String renderWhereStringTemplate(String sqlWhereString, Dialect dialect, SQLFunctionRegistry functionRegistry) {
        return Template.renderWhereStringTemplate(sqlWhereString, TEMPLATE, dialect, functionRegistry);
    }

    @Deprecated
    public static String renderWhereStringTemplate(String sqlWhereString, String placeholder, Dialect dialect) {
        return Template.renderWhereStringTemplate(sqlWhereString, placeholder, dialect, new SQLFunctionRegistry(dialect, Collections.emptyMap()));
    }

    public static String renderWhereStringTemplate(String sqlWhereString, String placeholder, Dialect dialect, SQLFunctionRegistry functionRegistry) {
        String nextToken;
        String symbols = "=><!+-*/()',|&`" + " \n\r\f\t" + dialect.openQuote() + dialect.closeQuote();
        StringTokenizer tokens = new StringTokenizer(sqlWhereString, symbols, true);
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        boolean quotedIdentifier = false;
        boolean beforeTable = false;
        boolean inFromClause = false;
        boolean afterFromTable = false;
        boolean hasMore = tokens.hasMoreTokens();
        String string = nextToken = hasMore ? tokens.nextToken() : null;
        while (hasMore) {
            boolean quotedOrWhitespace;
            String token = nextToken;
            String lcToken = token.toLowerCase(Locale.ROOT);
            hasMore = tokens.hasMoreTokens();
            nextToken = hasMore ? tokens.nextToken() : null;
            boolean isQuoteCharacter = false;
            if (!quotedIdentifier && "'".equals(token)) {
                quoted = !quoted;
                isQuoteCharacter = true;
            }
            if (!quoted) {
                boolean isOpenQuote;
                if ("`".equals(token)) {
                    isOpenQuote = !quotedIdentifier;
                    lcToken = isOpenQuote ? Character.toString(dialect.openQuote()) : Character.toString(dialect.closeQuote());
                    token = lcToken;
                    quotedIdentifier = isOpenQuote;
                    isQuoteCharacter = true;
                } else if (!quotedIdentifier && dialect.openQuote() == token.charAt(0)) {
                    isOpenQuote = true;
                    quotedIdentifier = true;
                    isQuoteCharacter = true;
                } else if (quotedIdentifier && dialect.closeQuote() == token.charAt(0)) {
                    quotedIdentifier = false;
                    isQuoteCharacter = true;
                    isOpenQuote = false;
                } else {
                    isOpenQuote = false;
                }
                if (isOpenQuote) {
                    result.append(placeholder).append('.');
                }
            }
            if ("extract".equals(lcToken) && "(".equals(nextToken)) {
                String field = Template.extractUntil(tokens, "from");
                String source = Template.renderWhereStringTemplate(Template.extractUntil(tokens, ")"), placeholder, dialect, functionRegistry);
                result.append("extract(").append(field).append(" from ").append(source).append(')');
                hasMore = tokens.hasMoreTokens();
                nextToken = hasMore ? tokens.nextToken() : null;
                continue;
            }
            if ("trim".equals(lcToken) && "(".equals(nextToken)) {
                ArrayList<String> operands = new ArrayList<String>();
                StringBuilder builder = new StringBuilder();
                boolean hasMoreOperands = true;
                String operandToken = tokens.nextToken();
                boolean quotedOperand = false;
                while (hasMoreOperands) {
                    boolean isQuote = "'".equals(operandToken);
                    if (isQuote) {
                        boolean bl = quotedOperand = !quotedOperand;
                        if (!quotedOperand) {
                            operands.add(builder.append('\'').toString());
                            builder.setLength(0);
                        } else {
                            builder.append('\'');
                        }
                    } else if (quotedOperand) {
                        builder.append(operandToken);
                    } else if (operandToken.length() != 1 || !Character.isWhitespace(operandToken.charAt(0))) {
                        operands.add(operandToken);
                    }
                    operandToken = tokens.nextToken();
                    hasMoreOperands = tokens.hasMoreTokens() && !")".equals(operandToken);
                }
                TrimOperands trimOperands = new TrimOperands(operands);
                result.append("trim(");
                if (trimOperands.trimSpec != null) {
                    result.append(trimOperands.trimSpec).append(' ');
                }
                if (trimOperands.trimChar != null) {
                    if (trimOperands.trimChar.startsWith("'") && trimOperands.trimChar.endsWith("'")) {
                        result.append(trimOperands.trimChar);
                    } else {
                        result.append(Template.renderWhereStringTemplate(trimOperands.trimSpec, placeholder, dialect, functionRegistry));
                    }
                    result.append(' ');
                }
                if (trimOperands.from != null) {
                    result.append(trimOperands.from).append(' ');
                } else if (trimOperands.trimSpec != null || trimOperands.trimChar != null) {
                    result.append("from ");
                }
                result.append(Template.renderWhereStringTemplate(trimOperands.trimSource, placeholder, dialect, functionRegistry)).append(')');
                hasMore = tokens.hasMoreTokens();
                nextToken = hasMore ? tokens.nextToken() : null;
                continue;
            }
            boolean bl = quotedOrWhitespace = quoted || quotedIdentifier || isQuoteCharacter || Character.isWhitespace(token.charAt(0));
            if (quotedOrWhitespace) {
                result.append(token);
            } else if (beforeTable) {
                result.append(token);
                beforeTable = false;
                afterFromTable = true;
            } else if (afterFromTable) {
                if (!"as".equals(lcToken)) {
                    afterFromTable = false;
                }
                result.append(token);
            } else if (Template.isNamedParameter(token)) {
                result.append(token);
            } else if (Template.isIdentifier(token) && !Template.isFunctionOrKeyword(lcToken, nextToken, dialect, functionRegistry)) {
                result.append(placeholder).append('.').append(dialect.quote(token));
            } else {
                if (BEFORE_TABLE_KEYWORDS.contains(lcToken)) {
                    beforeTable = true;
                    inFromClause = true;
                } else if (inFromClause && ",".equals(lcToken)) {
                    beforeTable = true;
                }
                if (Template.isBoolean(token)) {
                    token = dialect.toBooleanValueString(Boolean.parseBoolean(token));
                }
                result.append(token);
            }
            if (!inFromClause || !KEYWORDS.contains(lcToken) || BEFORE_TABLE_KEYWORDS.contains(lcToken)) continue;
            inFromClause = false;
        }
        return result.toString();
    }

    private static String extractUntil(StringTokenizer tokens, String delimiter) {
        StringBuilder valueBuilder = new StringBuilder();
        String token = tokens.nextToken();
        while (!delimiter.equalsIgnoreCase(token)) {
            valueBuilder.append(token);
            token = tokens.nextToken();
        }
        return valueBuilder.toString().trim();
    }

    @Deprecated
    public static String renderOrderByStringTemplate(String orderByFragment, Dialect dialect, SQLFunctionRegistry functionRegistry) {
        return Template.renderOrderByStringTemplate(orderByFragment, NoOpColumnMapper.INSTANCE, null, dialect, functionRegistry);
    }

    public static String renderOrderByStringTemplate(String orderByFragment, ColumnMapper columnMapper, SessionFactoryImplementor sessionFactory, Dialect dialect, SQLFunctionRegistry functionRegistry) {
        return Template.translateOrderBy(orderByFragment, columnMapper, sessionFactory, dialect, functionRegistry).injectAliases(LEGACY_ORDER_BY_ALIAS_RESOLVER);
    }

    public static OrderByTranslation translateOrderBy(String orderByFragment, final ColumnMapper columnMapper, final SessionFactoryImplementor sessionFactory, final Dialect dialect, final SQLFunctionRegistry functionRegistry) {
        TranslationContext context = new TranslationContext(){

            @Override
            public SessionFactoryImplementor getSessionFactory() {
                return sessionFactory;
            }

            @Override
            public Dialect getDialect() {
                return dialect;
            }

            @Override
            public SQLFunctionRegistry getSqlFunctionRegistry() {
                return functionRegistry;
            }

            @Override
            public ColumnMapper getColumnMapper() {
                return columnMapper;
            }
        };
        return OrderByFragmentTranslator.translate(context, orderByFragment);
    }

    private static boolean isNamedParameter(String token) {
        return token.startsWith(":");
    }

    private static boolean isFunctionOrKeyword(String lcToken, String nextToken, Dialect dialect, SQLFunctionRegistry functionRegistry) {
        return "(".equals(nextToken) || KEYWORDS.contains(lcToken) || Template.isType(lcToken, dialect) || Template.isFunction(lcToken, nextToken, functionRegistry) || dialect.getKeywords().contains(lcToken) || FUNCTION_KEYWORDS.contains(lcToken);
    }

    private static boolean isType(String lcToken, Dialect dialect) {
        return dialect.isTypeNameRegistered(lcToken);
    }

    private static boolean isFunction(String lcToken, String nextToken, SQLFunctionRegistry functionRegistry) {
        if ("(".equals(nextToken)) {
            return true;
        }
        SQLFunction function = functionRegistry.findSQLFunction(lcToken);
        if (function == null) {
            return false;
        }
        return !function.hasParenthesesIfNoArguments();
    }

    private static boolean isIdentifier(String token) {
        if (Template.isBoolean(token)) {
            return false;
        }
        return token.charAt(0) == '`' || Character.isLetter(token.charAt(0)) && token.indexOf(46) < 0;
    }

    private static boolean isBoolean(String token) {
        return "true".equals(token) || "false".equals(token);
    }

    static {
        KEYWORDS.add("and");
        KEYWORDS.add("or");
        KEYWORDS.add("not");
        KEYWORDS.add("like");
        KEYWORDS.add("escape");
        KEYWORDS.add("is");
        KEYWORDS.add("in");
        KEYWORDS.add("between");
        KEYWORDS.add("null");
        KEYWORDS.add("select");
        KEYWORDS.add("distinct");
        KEYWORDS.add("from");
        KEYWORDS.add("join");
        KEYWORDS.add("inner");
        KEYWORDS.add("outer");
        KEYWORDS.add("left");
        KEYWORDS.add("right");
        KEYWORDS.add("on");
        KEYWORDS.add("where");
        KEYWORDS.add("having");
        KEYWORDS.add("group");
        KEYWORDS.add("order");
        KEYWORDS.add("by");
        KEYWORDS.add("desc");
        KEYWORDS.add("asc");
        KEYWORDS.add("limit");
        KEYWORDS.add("any");
        KEYWORDS.add("some");
        KEYWORDS.add("exists");
        KEYWORDS.add("all");
        KEYWORDS.add("union");
        KEYWORDS.add("minus");
        BEFORE_TABLE_KEYWORDS.add("from");
        BEFORE_TABLE_KEYWORDS.add("join");
        FUNCTION_KEYWORDS.add("as");
        FUNCTION_KEYWORDS.add("leading");
        FUNCTION_KEYWORDS.add("trailing");
        FUNCTION_KEYWORDS.add("from");
        FUNCTION_KEYWORDS.add("case");
        FUNCTION_KEYWORDS.add("when");
        FUNCTION_KEYWORDS.add("then");
        FUNCTION_KEYWORDS.add("else");
        FUNCTION_KEYWORDS.add("end");
        LEGACY_ORDER_BY_ALIAS_RESOLVER = new OrderByAliasResolver(){

            @Override
            public String resolveTableAlias(String columnReference) {
                return Template.TEMPLATE;
            }
        };
    }

    public static class NoOpColumnMapper
    implements ColumnMapper {
        public static final NoOpColumnMapper INSTANCE = new NoOpColumnMapper();

        @Override
        public SqlValueReference[] map(String reference) {
            return null;
        }
    }

    private static class TrimOperands {
        private final String trimSpec;
        private final String trimChar;
        private final String from;
        private final String trimSource;

        private TrimOperands(List<String> operands) {
            int size = operands.size();
            if (size == 1) {
                this.trimSpec = null;
                this.trimChar = null;
                this.from = null;
                this.trimSource = operands.get(0);
            } else if (size == 4) {
                this.trimSpec = operands.get(0);
                this.trimChar = operands.get(1);
                this.from = operands.get(2);
                this.trimSource = operands.get(3);
            } else {
                if (size < 1 || size > 4) {
                    throw new HibernateException("Unexpected number of trim function operands : " + size);
                }
                this.trimSource = operands.get(size - 1);
                if (!"from".equals(operands.get(size - 2))) {
                    throw new HibernateException("Expecting FROM, found : " + operands.get(size - 2));
                }
                this.from = operands.get(size - 2);
                if ("leading".equalsIgnoreCase(operands.get(0)) || "trailing".equalsIgnoreCase(operands.get(0)) || "both".equalsIgnoreCase(operands.get(0))) {
                    this.trimSpec = operands.get(0);
                    this.trimChar = null;
                } else {
                    this.trimSpec = null;
                    this.trimChar = size - 2 == 0 ? null : operands.get(0);
                }
            }
        }
    }
}


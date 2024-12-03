/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class QueryUtils {
    public static final String COUNT_QUERY_STRING = "select count(%s) from %s x";
    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x";
    private static final String IDENTIFIER = "[._$[\\P{Z}&&\\P{Cc}&&\\P{Cf}&&\\P{Punct}]]+";
    static final String COLON_NO_DOUBLE_COLON = "(?<![:\\\\]):";
    static final String IDENTIFIER_GROUP = String.format("(%s)", "[._$[\\P{Z}&&\\P{Cc}&&\\P{Cf}&&\\P{Punct}]]+");
    private static final String COUNT_REPLACEMENT_TEMPLATE = "select count(%s) $5$6$7";
    private static final String SIMPLE_COUNT_VALUE = "$2";
    private static final String COMPLEX_COUNT_VALUE = "$3 $6";
    private static final String COMPLEX_COUNT_LAST_VALUE = "$6";
    private static final String ORDER_BY_PART = "(?iu)\\s+order\\s+by\\s+.*";
    private static final Pattern ALIAS_MATCH;
    private static final Pattern COUNT_MATCH;
    private static final Pattern PROJECTION_CLAUSE;
    private static final String JOIN;
    private static final Pattern JOIN_PATTERN;
    private static final Pattern ORDER_BY;
    private static final Pattern CONSTRUCTOR_EXPRESSION;
    private static final int QUERY_JOIN_ALIAS_GROUP_INDEX = 3;
    private static final int VARIABLE_NAME_GROUP_INDEX = 4;
    private static final int COMPLEX_COUNT_FIRST_INDEX = 3;
    private static final Pattern FUNCTION_PATTERN;
    private static final Pattern FIELD_ALIAS_PATTERN;
    private static final String QUERY_MUST_NOT_BE_NULL_OR_EMPTY = "Query must not be null or empty!";

    private QueryUtils() {
    }

    public static String applySorting(String query, Sort sort, @Nullable String alias) {
        Assert.hasText((String)query, (String)QUERY_MUST_NOT_BE_NULL_OR_EMPTY);
        if (sort.isUnsorted()) {
            return query;
        }
        StringBuilder builder = new StringBuilder(query);
        if (!ORDER_BY.matcher(query).matches()) {
            builder.append(" order by ");
        } else {
            builder.append(", ");
        }
        Set<String> joinAliases = QueryUtils.getOuterJoinAliases(query);
        Set<String> selectionAliases = QueryUtils.getFunctionAliases(query);
        selectionAliases.addAll(QueryUtils.getFieldAliases(query));
        for (Sort.Order order : sort) {
            builder.append(QueryUtils.getOrderClause(joinAliases, selectionAliases, alias, order)).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    private static String getOrderClause(Set<String> joinAliases, Set<String> selectionAlias, @Nullable String alias, Sort.Order order) {
        String property = order.getProperty();
        if (selectionAlias.contains(property)) {
            return String.format("%s %s", property, QueryUtils.toAoDirection(order));
        }
        boolean qualifyReference = !property.contains("(");
        for (String joinAlias : joinAliases) {
            if (!property.startsWith(joinAlias.concat("."))) continue;
            qualifyReference = false;
            break;
        }
        String reference = qualifyReference && StringUtils.hasText((String)alias) ? String.format("%s.%s", alias, property) : property;
        String wrapped = order.isIgnoreCase() ? String.format("lower(%s)", reference) : reference;
        return String.format("%s %s", wrapped, QueryUtils.toAoDirection(order));
    }

    static Set<String> getOuterJoinAliases(String query) {
        HashSet<String> result = new HashSet<String>();
        Matcher matcher = JOIN_PATTERN.matcher(query);
        while (matcher.find()) {
            String alias = matcher.group(3);
            if (!StringUtils.hasText((String)alias)) continue;
            result.add(alias);
        }
        return result;
    }

    private static Set<String> getFieldAliases(String query) {
        HashSet<String> result = new HashSet<String>();
        Matcher matcher = FIELD_ALIAS_PATTERN.matcher(query);
        while (matcher.find()) {
            String alias = matcher.group(1);
            if (!StringUtils.hasText((String)alias)) continue;
            result.add(alias);
        }
        return result;
    }

    static Set<String> getFunctionAliases(String query) {
        HashSet<String> result = new HashSet<String>();
        Matcher matcher = FUNCTION_PATTERN.matcher(query);
        while (matcher.find()) {
            String alias = matcher.group(1);
            if (!StringUtils.hasText((String)alias)) continue;
            result.add(alias);
        }
        return result;
    }

    private static String toAoDirection(Sort.Order order) {
        return order.getDirection().name().toLowerCase(Locale.US);
    }

    @Nullable
    @Deprecated
    public static String detectAlias(String query) {
        Matcher matcher = ALIAS_MATCH.matcher(query);
        return matcher.find() ? matcher.group(2) : null;
    }

    @Deprecated
    public static String createCountQueryFor(String originalQuery, @Nullable String countProjection) {
        String countQuery;
        Assert.hasText((String)originalQuery, (String)"OriginalQuery must not be null or empty!");
        Matcher matcher = COUNT_MATCH.matcher(originalQuery);
        if (countProjection == null) {
            String variable = matcher.matches() ? matcher.group(4) : null;
            boolean useVariable = StringUtils.hasText((String)variable) && !variable.startsWith(" new") && !variable.startsWith("count(") && !variable.contains(",");
            String complexCountValue = matcher.matches() && StringUtils.hasText((String)matcher.group(3)) ? COMPLEX_COUNT_VALUE : COMPLEX_COUNT_LAST_VALUE;
            String replacement = useVariable ? SIMPLE_COUNT_VALUE : complexCountValue;
            countQuery = matcher.replaceFirst(String.format(COUNT_REPLACEMENT_TEMPLATE, replacement));
        } else {
            countQuery = matcher.replaceFirst(String.format(COUNT_REPLACEMENT_TEMPLATE, countProjection));
        }
        return countQuery.replaceFirst(ORDER_BY_PART, "");
    }

    public static boolean hasConstructorExpression(String query) {
        Assert.hasText((String)query, (String)QUERY_MUST_NOT_BE_NULL_OR_EMPTY);
        return CONSTRUCTOR_EXPRESSION.matcher(query).find();
    }

    public static String getProjection(String query) {
        Assert.hasText((String)query, (String)QUERY_MUST_NOT_BE_NULL_OR_EMPTY);
        Matcher matcher = PROJECTION_CLAUSE.matcher(query);
        String projection = matcher.find() ? matcher.group(1) : "";
        return projection.trim();
    }

    static {
        PROJECTION_CLAUSE = Pattern.compile("select\\s+(?:distinct\\s+)?(.+)\\s+from", 2);
        JOIN = "join\\s+(fetch\\s+)?[._$[\\P{Z}&&\\P{Cc}&&\\P{Cf}&&\\P{Punct}]]+\\s+(as\\s+)?" + IDENTIFIER_GROUP;
        JOIN_PATTERN = Pattern.compile(JOIN, 2);
        ORDER_BY = Pattern.compile(".*order\\s+by\\s+.*", 2);
        StringBuilder builder = new StringBuilder();
        builder.append("(?<=from)");
        builder.append("(?:\\s)+");
        builder.append(IDENTIFIER_GROUP);
        builder.append("(?:\\sas)*");
        builder.append("(?:\\s)+");
        builder.append("(?!(?:where|group\\s*by|order\\s*by))(\\w+)");
        ALIAS_MATCH = Pattern.compile(builder.toString(), 2);
        builder = new StringBuilder();
        builder.append("(select\\s+((distinct)?((?s).+?)?)\\s+)?(from\\s+");
        builder.append(IDENTIFIER);
        builder.append("(?:\\s+as)?\\s+)");
        builder.append(IDENTIFIER_GROUP);
        builder.append("(.*)");
        COUNT_MATCH = Pattern.compile(builder.toString(), 2);
        builder = new StringBuilder();
        builder.append("select");
        builder.append("\\s+");
        builder.append("(.*\\s+)?");
        builder.append("new");
        builder.append("\\s+");
        builder.append(IDENTIFIER);
        builder.append("\\s*");
        builder.append("\\(");
        builder.append(".*");
        builder.append("\\)");
        CONSTRUCTOR_EXPRESSION = Pattern.compile(builder.toString(), 34);
        builder = new StringBuilder();
        builder.append("\\w+\\s*\\([\\w\\.,\\s'=]+\\)");
        builder.append("\\s+[as|AS]+\\s+(([\\w\\.]+))");
        FUNCTION_PATTERN = Pattern.compile(builder.toString());
        builder = new StringBuilder();
        builder.append("\\s+");
        builder.append("[^\\s\\(\\)]+");
        builder.append("\\s+[as|AS]+\\s+(([\\w\\.]+))");
        FIELD_ALIAS_PATTERN = Pattern.compile(builder.toString());
    }
}


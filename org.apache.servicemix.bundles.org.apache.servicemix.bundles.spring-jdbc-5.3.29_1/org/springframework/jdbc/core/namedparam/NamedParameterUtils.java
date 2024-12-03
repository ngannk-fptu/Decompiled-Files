/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core.namedparam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class NamedParameterUtils {
    private static final String[] START_SKIP = new String[]{"'", "\"", "--", "/*"};
    private static final String[] STOP_SKIP = new String[]{"'", "\"", "\n", "*/"};
    private static final String PARAMETER_SEPARATORS = "\"':&,;()|=+-*%/\\<>^";
    private static final boolean[] separatorIndex = new boolean[128];

    public static ParsedSql parseSqlStatement(String sql) {
        Assert.notNull((Object)sql, (String)"SQL must not be null");
        HashSet<String> namedParameters = new HashSet<String>();
        StringBuilder sqlToUse = new StringBuilder(sql);
        ArrayList<ParameterHolder> parameterList = new ArrayList<ParameterHolder>();
        char[] statement = sql.toCharArray();
        int namedParameterCount = 0;
        int unnamedParameterCount = 0;
        int totalParameterCount = 0;
        int escapes = 0;
        int i = 0;
        while (i < statement.length) {
            int j;
            int skipToPosition = i;
            while (i < statement.length && i != (skipToPosition = NamedParameterUtils.skipCommentsAndQuotes(statement, i))) {
                i = skipToPosition;
            }
            if (i >= statement.length) break;
            char c = statement[i];
            if (c == ':' || c == '&') {
                if (c == ':' && j < statement.length && statement[j] == ':') {
                    i += 2;
                    continue;
                }
                String parameter = null;
                if (c == ':' && j < statement.length && statement[j] == '{') {
                    while (statement[j] != '}') {
                        if (++j >= statement.length) {
                            throw new InvalidDataAccessApiUsageException("Non-terminated named parameter declaration at position " + i + " in statement: " + sql);
                        }
                        if (statement[j] != ':' && statement[j] != '{') continue;
                        throw new InvalidDataAccessApiUsageException("Parameter name contains invalid character '" + statement[j] + "' at position " + i + " in statement: " + sql);
                    }
                    if (j - i > 2) {
                        parameter = sql.substring(i + 2, j);
                        namedParameterCount = NamedParameterUtils.addNewNamedParameter(namedParameters, namedParameterCount, parameter);
                        totalParameterCount = NamedParameterUtils.addNamedParameter(parameterList, totalParameterCount, escapes, i, j + 1, parameter);
                    }
                    ++j;
                } else {
                    for (j = i + 1; j < statement.length && !NamedParameterUtils.isParameterSeparator(statement[j]); ++j) {
                    }
                    if (j - i > 1) {
                        parameter = sql.substring(i + 1, j);
                        namedParameterCount = NamedParameterUtils.addNewNamedParameter(namedParameters, namedParameterCount, parameter);
                        totalParameterCount = NamedParameterUtils.addNamedParameter(parameterList, totalParameterCount, escapes, i, j, parameter);
                    }
                }
                i = j - 1;
            } else {
                if (c == '\\' && (j = i + 1) < statement.length && statement[j] == ':') {
                    sqlToUse.deleteCharAt(i - escapes);
                    ++escapes;
                    i += 2;
                    continue;
                }
                if (c == '?') {
                    j = i + 1;
                    if (j < statement.length && (statement[j] == '?' || statement[j] == '|' || statement[j] == '&')) {
                        i += 2;
                        continue;
                    }
                    ++unnamedParameterCount;
                    ++totalParameterCount;
                }
            }
            ++i;
        }
        ParsedSql parsedSql = new ParsedSql(sqlToUse.toString());
        for (ParameterHolder ph : parameterList) {
            parsedSql.addNamedParameter(ph.getParameterName(), ph.getStartIndex(), ph.getEndIndex());
        }
        parsedSql.setNamedParameterCount(namedParameterCount);
        parsedSql.setUnnamedParameterCount(unnamedParameterCount);
        parsedSql.setTotalParameterCount(totalParameterCount);
        return parsedSql;
    }

    private static int addNamedParameter(List<ParameterHolder> parameterList, int totalParameterCount, int escapes, int i, int j, String parameter) {
        parameterList.add(new ParameterHolder(parameter, i - escapes, j - escapes));
        return ++totalParameterCount;
    }

    private static int addNewNamedParameter(Set<String> namedParameters, int namedParameterCount, String parameter) {
        if (!namedParameters.contains(parameter)) {
            namedParameters.add(parameter);
            ++namedParameterCount;
        }
        return namedParameterCount;
    }

    private static int skipCommentsAndQuotes(char[] statement, int position) {
        for (int i = 0; i < START_SKIP.length; ++i) {
            if (statement[position] != START_SKIP[i].charAt(0)) continue;
            boolean match = true;
            for (int j = 1; j < START_SKIP[i].length(); ++j) {
                if (statement[position + j] == START_SKIP[i].charAt(j)) continue;
                match = false;
                break;
            }
            if (!match) continue;
            int offset = START_SKIP[i].length();
            for (int m = position + offset; m < statement.length; ++m) {
                if (statement[m] != STOP_SKIP[i].charAt(0)) continue;
                boolean endMatch = true;
                int endPos = m;
                for (int n = 1; n < STOP_SKIP[i].length(); ++n) {
                    if (m + n >= statement.length) {
                        return statement.length;
                    }
                    if (statement[m + n] != STOP_SKIP[i].charAt(n)) {
                        endMatch = false;
                        break;
                    }
                    endPos = m + n;
                }
                if (!endMatch) continue;
                return endPos + 1;
            }
            return statement.length;
        }
        return position;
    }

    public static String substituteNamedParameters(ParsedSql parsedSql, @Nullable SqlParameterSource paramSource) {
        String originalSql = parsedSql.getOriginalSql();
        List<String> paramNames = parsedSql.getParameterNames();
        if (paramNames.isEmpty()) {
            return originalSql;
        }
        StringBuilder actualSql = new StringBuilder(originalSql.length());
        int lastIndex = 0;
        for (int i = 0; i < paramNames.size(); ++i) {
            String paramName = paramNames.get(i);
            int[] indexes = parsedSql.getParameterIndexes(i);
            int startIndex = indexes[0];
            int endIndex = indexes[1];
            actualSql.append(originalSql, lastIndex, startIndex);
            if (paramSource != null && paramSource.hasValue(paramName)) {
                Object value = paramSource.getValue(paramName);
                if (value instanceof SqlParameterValue) {
                    value = ((SqlParameterValue)value).getValue();
                }
                if (value instanceof Iterable) {
                    Iterator entryIter = ((Iterable)value).iterator();
                    int k = 0;
                    while (entryIter.hasNext()) {
                        if (k > 0) {
                            actualSql.append(", ");
                        }
                        ++k;
                        Object entryItem = entryIter.next();
                        if (entryItem instanceof Object[]) {
                            Object[] expressionList = (Object[])entryItem;
                            actualSql.append('(');
                            for (int m = 0; m < expressionList.length; ++m) {
                                if (m > 0) {
                                    actualSql.append(", ");
                                }
                                actualSql.append('?');
                            }
                            actualSql.append(')');
                            continue;
                        }
                        actualSql.append('?');
                    }
                } else {
                    actualSql.append('?');
                }
            } else {
                actualSql.append('?');
            }
            lastIndex = endIndex;
        }
        actualSql.append(originalSql, lastIndex, originalSql.length());
        return actualSql.toString();
    }

    public static Object[] buildValueArray(ParsedSql parsedSql, SqlParameterSource paramSource, @Nullable List<SqlParameter> declaredParams) {
        Object[] paramArray = new Object[parsedSql.getTotalParameterCount()];
        if (parsedSql.getNamedParameterCount() > 0 && parsedSql.getUnnamedParameterCount() > 0) {
            throw new InvalidDataAccessApiUsageException("Not allowed to mix named and traditional ? placeholders. You have " + parsedSql.getNamedParameterCount() + " named parameter(s) and " + parsedSql.getUnnamedParameterCount() + " traditional placeholder(s) in statement: " + parsedSql.getOriginalSql());
        }
        List<String> paramNames = parsedSql.getParameterNames();
        for (int i = 0; i < paramNames.size(); ++i) {
            String paramName = paramNames.get(i);
            try {
                SqlParameter param = NamedParameterUtils.findParameter(declaredParams, paramName, i);
                Object paramValue = paramSource.getValue(paramName);
                if (paramValue instanceof SqlParameterValue) {
                    paramArray[i] = paramValue;
                    continue;
                }
                paramArray[i] = param != null ? new SqlParameterValue(param, paramValue) : SqlParameterSourceUtils.getTypedValue(paramSource, paramName);
                continue;
            }
            catch (IllegalArgumentException ex) {
                throw new InvalidDataAccessApiUsageException("No value supplied for the SQL parameter '" + paramName + "': " + ex.getMessage());
            }
        }
        return paramArray;
    }

    @Nullable
    private static SqlParameter findParameter(@Nullable List<SqlParameter> declaredParams, String paramName, int paramIndex) {
        if (declaredParams != null) {
            SqlParameter declaredParam;
            for (SqlParameter declaredParam2 : declaredParams) {
                if (!paramName.equals(declaredParam2.getName())) continue;
                return declaredParam2;
            }
            if (paramIndex < declaredParams.size() && (declaredParam = declaredParams.get(paramIndex)).getName() == null) {
                return declaredParam;
            }
        }
        return null;
    }

    private static boolean isParameterSeparator(char c) {
        return c < '\u0080' && separatorIndex[c] || Character.isWhitespace(c);
    }

    public static int[] buildSqlTypeArray(ParsedSql parsedSql, SqlParameterSource paramSource) {
        int[] sqlTypes = new int[parsedSql.getTotalParameterCount()];
        List<String> paramNames = parsedSql.getParameterNames();
        for (int i = 0; i < paramNames.size(); ++i) {
            String paramName = paramNames.get(i);
            sqlTypes[i] = paramSource.getSqlType(paramName);
        }
        return sqlTypes;
    }

    public static List<SqlParameter> buildSqlParameterList(ParsedSql parsedSql, SqlParameterSource paramSource) {
        List<String> paramNames = parsedSql.getParameterNames();
        ArrayList<SqlParameter> params = new ArrayList<SqlParameter>(paramNames.size());
        for (String paramName : paramNames) {
            params.add(new SqlParameter(paramName, paramSource.getSqlType(paramName), paramSource.getTypeName(paramName)));
        }
        return params;
    }

    public static String parseSqlStatementIntoString(String sql) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        return NamedParameterUtils.substituteNamedParameters(parsedSql, null);
    }

    public static String substituteNamedParameters(String sql, SqlParameterSource paramSource) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        return NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
    }

    public static Object[] buildValueArray(String sql, Map<String, ?> paramMap) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        return NamedParameterUtils.buildValueArray(parsedSql, new MapSqlParameterSource(paramMap), null);
    }

    static {
        for (char c : PARAMETER_SEPARATORS.toCharArray()) {
            NamedParameterUtils.separatorIndex[c] = true;
        }
    }

    private static class ParameterHolder {
        private final String parameterName;
        private final int startIndex;
        private final int endIndex;

        public ParameterHolder(String parameterName, int startIndex, int endIndex) {
            this.parameterName = parameterName;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getParameterName() {
            return this.parameterName;
        }

        public int getStartIndex() {
            return this.startIndex;
        }

        public int getEndIndex() {
            return this.endIndex;
        }
    }
}


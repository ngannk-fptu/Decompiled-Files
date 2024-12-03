/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.internal.util.StringHelper;

public class SQLServer2005LimitHandler
extends AbstractLimitHandler {
    private static final String SELECT = "select";
    private static final String FROM = "from";
    private static final String DISTINCT = "distinct";
    private static final String ORDER_BY = "order by";
    private static final String SELECT_DISTINCT = "select distinct";
    private static final String SELECT_DISTINCT_SPACE = "select distinct ";
    private static final String SELECT_SPACE = "select ";
    private static final Pattern SELECT_DISTINCT_PATTERN = SQLServer2005LimitHandler.buildShallowIndexPattern("select distinct ", true);
    private static final Pattern SELECT_PATTERN = SQLServer2005LimitHandler.buildShallowIndexPattern("select(.*)", true);
    private static final Pattern FROM_PATTERN = SQLServer2005LimitHandler.buildShallowIndexPattern("from", true);
    private static final Pattern DISTINCT_PATTERN = SQLServer2005LimitHandler.buildShallowIndexPattern("distinct", true);
    private static final Pattern ORDER_BY_PATTERN = SQLServer2005LimitHandler.buildShallowIndexPattern("order by", true);
    private static final Pattern COMMA_PATTERN = SQLServer2005LimitHandler.buildShallowIndexPattern(",", false);
    private static final Pattern ALIAS_PATTERN = Pattern.compile("(?![^\\[]*(\\]))\\S+\\s*(\\s(?i)as\\s)\\s*(\\S+)*\\s*$|(?![^\\[]*(\\]))\\s+(\\S+)$");
    private static final String SPACE_NEWLINE_LINEFEED = "[\\s\\t\\n\\r]*";
    private static final Pattern WITH_CTE = Pattern.compile("(^[\\s\\t\\n\\r]*WITH[\\s\\t\\n\\r]*)", 2);
    private static final Pattern WITH_EXPRESSION_NAME = Pattern.compile("(^[\\s\\t\\n\\r]*[a-zA-Z0-9]*[\\s\\t\\n\\r]*)", 2);
    private static final Pattern WITH_COLUMN_NAMES_START = Pattern.compile("(^[\\s\\t\\n\\r]*\\()", 2);
    private static final Pattern WITH_COLUMN_NAMES_END = Pattern.compile("(\\))", 2);
    private static final Pattern WITH_AS = Pattern.compile("(^[\\s\\t\\n\\r]*AS[\\s\\t\\n\\r]*)", 2);
    private static final Pattern WITH_COMMA = Pattern.compile("(^[\\s\\t\\n\\r]*,)", 2);
    private boolean topAdded;
    private boolean isCTE;

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }

    @Override
    public boolean supportsVariableLimit() {
        return true;
    }

    @Override
    public int convertToFirstRowValue(int zeroBasedFirstResult) {
        return zeroBasedFirstResult + 1;
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        StringBuilder sb = new StringBuilder(sql);
        if (sb.charAt(sb.length() - 1) == ';') {
            sb.setLength(sb.length() - 1);
        }
        int offset = this.getStatementIndex(sb);
        if (!LimitHelper.hasFirstRow(selection)) {
            this.addTopExpression(sb, offset);
        } else {
            String selectClause = this.fillAliasInSelectClause(sb, offset);
            if (SQLServer2005LimitHandler.shallowIndexOfPattern(sb, ORDER_BY_PATTERN, offset) > 0) {
                this.addTopExpression(sb, offset);
            }
            this.encloseWithOuterQuery(sb, offset);
            sb.insert(offset, !this.isCTE ? "with query as (" : ", query as (");
            sb.append(") select ").append(selectClause).append(" from query ");
            sb.append("where __row__ >= ? and __row__ < ?");
        }
        return sb.toString();
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        if (this.topAdded) {
            statement.setInt(index, this.getMaxOrLimit(selection) - 1);
            return 1;
        }
        return 0;
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        return LimitHelper.hasFirstRow(selection) ? super.bindLimitParametersAtEndOfQuery(selection, statement, index) : 0;
    }

    protected String fillAliasInSelectClause(StringBuilder sb, int offset) {
        String alias;
        String expression;
        String separator = System.lineSeparator();
        LinkedList<String> aliases = new LinkedList<String>();
        int startPos = this.getSelectColumnsStartPosition(sb, offset);
        int endPos = SQLServer2005LimitHandler.shallowIndexOfPattern(sb, FROM_PATTERN, startPos);
        int nextComa = startPos;
        int prevComa = startPos;
        int unique = 0;
        boolean selectsMultipleColumns = false;
        while (nextComa != -1) {
            prevComa = nextComa;
            if ((nextComa = SQLServer2005LimitHandler.shallowIndexOfPattern(sb, COMMA_PATTERN, nextComa)) > endPos) break;
            if (nextComa == -1) continue;
            expression = sb.substring(prevComa, nextComa);
            if (this.selectsMultipleColumns(expression)) {
                selectsMultipleColumns = true;
            } else {
                alias = this.getAlias(expression);
                if (alias == null) {
                    alias = StringHelper.generateAlias("page", unique);
                    sb.insert(nextComa, " as " + alias);
                    int aliasExprLength = (" as " + alias).length();
                    ++unique;
                    nextComa += aliasExprLength;
                    endPos += aliasExprLength;
                }
                aliases.add(alias);
            }
            ++nextComa;
        }
        if (this.selectsMultipleColumns(expression = sb.substring(prevComa, endPos = SQLServer2005LimitHandler.shallowIndexOfPattern(sb, FROM_PATTERN, startPos)))) {
            selectsMultipleColumns = true;
        } else {
            alias = this.getAlias(expression);
            if (alias == null) {
                alias = StringHelper.generateAlias("page", unique);
                boolean endWithSeparator = sb.substring(endPos - separator.length()).startsWith(separator);
                sb.insert(endPos - (endWithSeparator ? 2 : 1), " as " + alias);
            }
            aliases.add(alias);
        }
        return selectsMultipleColumns ? "*" : String.join((CharSequence)", ", aliases);
    }

    private int getSelectColumnsStartPosition(StringBuilder sb, int offset) {
        int startPos = this.getSelectStartPosition(sb, offset);
        String sql = sb.toString().substring(startPos).toLowerCase();
        if (sql.startsWith(SELECT_DISTINCT_SPACE)) {
            return startPos + SELECT_DISTINCT_SPACE.length();
        }
        if (sql.startsWith(SELECT_SPACE)) {
            return startPos + SELECT_SPACE.length();
        }
        return startPos;
    }

    private int getSelectStartPosition(StringBuilder sb, int offset) {
        return SQLServer2005LimitHandler.shallowIndexOfPattern(sb, SELECT_PATTERN, offset);
    }

    private boolean selectsMultipleColumns(String expression) {
        String lastExpr = expression.trim().replaceFirst("(?i)(.)*\\s", "").trim();
        return "*".equals(lastExpr) || lastExpr.endsWith(".*");
    }

    private String getAlias(String expression) {
        expression = expression.replaceFirst("(\\((.)*\\))", "").trim();
        Matcher matcher = ALIAS_PATTERN.matcher(expression);
        String alias = null;
        if (matcher.find() && matcher.groupCount() > 1 && (alias = matcher.group(3)) == null) {
            alias = matcher.group(0);
        }
        return alias != null ? alias.trim() : null;
    }

    protected void encloseWithOuterQuery(StringBuilder sql, int offset) {
        sql.insert(offset, "select inner_query.*, row_number() over (order by current_timestamp) as __row__ from ( ");
        sql.append(" ) inner_query ");
    }

    protected void addTopExpression(StringBuilder sql, int offset) {
        int selectDistinctPos;
        int selectPos = SQLServer2005LimitHandler.shallowIndexOfPattern(sql, SELECT_PATTERN, offset);
        if (selectPos == (selectDistinctPos = SQLServer2005LimitHandler.shallowIndexOfPattern(sql, SELECT_DISTINCT_PATTERN, offset))) {
            sql.insert(selectDistinctPos + SELECT_DISTINCT.length(), " top(?)");
        } else {
            sql.insert(selectPos + SELECT.length(), " top(?)");
        }
        this.topAdded = true;
    }

    private static int shallowIndexOfPattern(StringBuilder sb, Pattern pattern, int fromIndex) {
        int index;
        block3: {
            Matcher matcher;
            List<IgnoreRange> ignoreRangeList;
            block2: {
                index = -1;
                String matchString = sb.toString();
                if (matchString.length() < fromIndex || fromIndex < 0) {
                    return -1;
                }
                ignoreRangeList = SQLServer2005LimitHandler.generateIgnoreRanges(matchString);
                matcher = pattern.matcher(matchString);
                matcher.region(fromIndex, matchString.length());
                if (!ignoreRangeList.isEmpty()) break block2;
                if (!matcher.find() || matcher.groupCount() <= 0) break block3;
                index = matcher.start();
                break block3;
            }
            while (matcher.find() && matcher.groupCount() > 0) {
                int position = matcher.start();
                if (SQLServer2005LimitHandler.isPositionIgnorable(ignoreRangeList, position)) continue;
                index = position;
                break;
            }
        }
        return index;
    }

    private static Pattern buildShallowIndexPattern(String pattern, boolean wordBoundary) {
        return Pattern.compile("(" + (wordBoundary ? "\\b" : "") + pattern + (wordBoundary ? "\\b" : "") + ")(?![^\\(|\\[]*(\\)|\\]))", 2);
    }

    private static List<IgnoreRange> generateIgnoreRanges(String sql) {
        ArrayList<IgnoreRange> ignoreRangeList = new ArrayList<IgnoreRange>();
        int depth = 0;
        int start = -1;
        boolean insideAStringValue = false;
        for (int i = 0; i < sql.length(); ++i) {
            char ch = sql.charAt(i);
            if (ch == '\'') {
                insideAStringValue = !insideAStringValue;
                continue;
            }
            if (ch == '(' && !insideAStringValue) {
                if (++depth != 1) continue;
                start = i;
                continue;
            }
            if (ch != ')' || insideAStringValue) continue;
            if (depth > 0) {
                if (depth == 1) {
                    ignoreRangeList.add(new IgnoreRange(start, i));
                    start = -1;
                }
                --depth;
                continue;
            }
            throw new IllegalStateException("Found an unmatched ')' at position " + i + ": " + sql);
        }
        if (depth != 0) {
            throw new IllegalStateException("Unmatched parenthesis in rendered SQL (" + depth + " depth): " + sql);
        }
        return ignoreRangeList;
    }

    private static boolean isPositionIgnorable(List<IgnoreRange> ignoreRangeList, int position) {
        for (IgnoreRange ignoreRange : ignoreRangeList) {
            if (!ignoreRange.isWithinRange(position)) continue;
            return true;
        }
        return false;
    }

    private int getStatementIndex(StringBuilder sql) {
        Matcher matcher = WITH_CTE.matcher(sql.toString());
        if (matcher.find() && matcher.groupCount() > 0) {
            this.isCTE = true;
            return this.locateQueryInCTEStatement(sql, matcher.end());
        }
        return 0;
    }

    private int locateQueryInCTEStatement(StringBuilder sql, int offset) {
        Matcher matcher;
        while ((matcher = WITH_EXPRESSION_NAME.matcher(sql.substring(offset))).find() && matcher.groupCount() > 0) {
            if ((matcher = WITH_COLUMN_NAMES_START.matcher(sql.substring(offset += matcher.end()))).find() && matcher.groupCount() > 0) {
                if ((matcher = WITH_COLUMN_NAMES_END.matcher(sql.substring(offset += matcher.end()))).find() && matcher.groupCount() > 0) {
                    offset += matcher.end();
                    if ((matcher = WITH_COMMA.matcher(sql.substring(offset += this.advanceOverCTEInnerQuery(sql, offset)))).find() && matcher.groupCount() > 0) {
                        offset += matcher.end();
                        continue;
                    }
                    return offset;
                }
                throw new IllegalArgumentException(String.format(Locale.ROOT, "Failed to parse CTE expression columns at offset %d, SQL [%s]", offset, sql.toString()));
            }
            matcher = WITH_AS.matcher(sql.substring(offset));
            if (matcher.find() && matcher.groupCount() > 0) {
                offset += matcher.end();
                if ((matcher = WITH_COMMA.matcher(sql.substring(offset += this.advanceOverCTEInnerQuery(sql, offset)))).find() && matcher.groupCount() > 0) {
                    offset += matcher.end();
                    continue;
                }
                return offset;
            }
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Failed to locate AS keyword in CTE query at offset %d, SQL [%s]", offset, sql.toString()));
        }
        throw new IllegalArgumentException(String.format(Locale.ROOT, "Failed to locate CTE expression name at offset %d, SQL [%s]", offset, sql.toString()));
    }

    private int advanceOverCTEInnerQuery(StringBuilder sql, int offset) {
        int index;
        int brackets = 0;
        boolean inString = false;
        for (index = offset; index < sql.length(); ++index) {
            if (sql.charAt(index) == '\'' && !inString) {
                inString = true;
                continue;
            }
            if (sql.charAt(index) == '\'' && inString) {
                inString = false;
                continue;
            }
            if (sql.charAt(index) == '(' && !inString) {
                ++brackets;
                continue;
            }
            if (sql.charAt(index) == ')' && !inString && --brackets == 0) break;
        }
        if (brackets > 0) {
            throw new IllegalArgumentException("Failed to parse the CTE query inner query because closing ')' was not found.");
        }
        return index - offset + 1;
    }

    static class IgnoreRange {
        private int start;
        private int end;

        IgnoreRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean isWithinRange(int position) {
            return position >= this.start && position <= this.end;
        }
    }
}


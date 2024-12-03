/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  org.apache.commons.lang3.Validate
 */
package net.java.ao.sql;

import com.google.common.base.Function;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.java.ao.Common;
import org.apache.commons.lang3.Validate;

public final class SqlUtils {
    public static final Pattern WHERE_CLAUSE = Pattern.compile("(\\w+)(?=\\s*((=|!=|>|<|<>|<=|>=|(?<!(NOT\\s{1,10}))LIKE|(?<!(NOT\\s{1,10}))like|(?<!(NOT\\s{1,10}))BETWEEN|(?<!(NOT\\s{1,10}))between|IS|is|(?<!((IS|AND)\\s{1,10}))NOT|(?<!(NOT\\s{1,10}))IN|(?<!(is\\s{1,10}))not|(?<!(not\\s{1,10}))in)(\\s|\\()))");
    public static final Pattern ON_CLAUSE = Pattern.compile("(?:(\\w+)\\.)?(?:(\\w+)\\.)?(\\w+)(\\s*=\\s*)(?:(\\w+)\\.)?(?:(\\w+)\\.)?(\\w+)");
    public static final Pattern GROUP_BY_CLAUSE = Pattern.compile("(?:(\\w+)\\.)?(\\w+)");
    public static final Pattern HAVING_CLAUSE = Pattern.compile("(?:(\\w+)\\()?(?:(\\w+)\\.)?(\\w+)\\)?(?=\\s*(?:(?:=|!=|>|<|<>|<=|>=|(?<!(?:NOT\\s{1,10}))LIKE|(?<!(?:NOT\\s{1,10}))like|(?<!(?:NOT\\s{1,10}))BETWEEN|(?<!(?:NOT\\s{1,10}))between|IS|is|(?<!(?:(?:IS|AND)\\s{1,10}))NOT|(?<!(?:NOT\\s{1,10}))IN|(?<!(?:is\\s{1,10}))not|(?<!(?:not\\s{1,10}))in)(?:\\s|\\()))");

    private SqlUtils() {
    }

    public static String processWhereClause(String where, Function<String, String> processor) {
        Matcher matcher = WHERE_CLAUSE.matcher(where);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, (String)processor.apply((Object)matcher.group()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String processOnClause(String on, Function<String, String> processor) {
        Matcher matcher = ON_CLAUSE.matcher(on);
        Validate.validState((boolean)matcher.matches());
        StringBuilder sb = new StringBuilder();
        if (matcher.group(1) != null) {
            sb.append(matcher.group(1)).append(".");
            if (matcher.group(2) != null) {
                sb.append((String)processor.apply((Object)matcher.group(2))).append(".");
            }
        } else if (matcher.group(2) != null) {
            sb.append(matcher.group(2)).append(".");
        }
        sb.append((String)processor.apply((Object)matcher.group(3)));
        sb.append(matcher.group(4));
        if (matcher.group(5) != null) {
            sb.append(matcher.group(5)).append(".");
            if (matcher.group(6) != null) {
                sb.append((String)processor.apply((Object)matcher.group(6))).append(".");
            }
        } else if (matcher.group(6) != null) {
            sb.append(matcher.group(6)).append(".");
        }
        sb.append((String)processor.apply((Object)matcher.group(7)));
        return sb.toString();
    }

    public static String processGroupByClause(String groupBy, Function<String, String> columnNameProcessor, Function<String, String> tableNameProcessor) {
        Matcher matcher = GROUP_BY_CLAUSE.matcher(groupBy);
        StringBuffer sql = new StringBuffer();
        while (matcher.find()) {
            StringBuilder repl = new StringBuilder();
            if (matcher.group(1) != null) {
                repl.append((String)tableNameProcessor.apply((Object)"$1"));
                repl.append(".");
            }
            repl.append((String)columnNameProcessor.apply((Object)"$2"));
            matcher.appendReplacement(sql, repl.toString());
        }
        matcher.appendTail(sql);
        return sql.toString();
    }

    public static String processHavingClause(String having, Function<String, String> columnNameProcessor, Function<String, String> tableNameProcessor) {
        Matcher matcher = HAVING_CLAUSE.matcher(having);
        StringBuffer sql = new StringBuffer();
        while (matcher.find()) {
            StringBuilder repl = new StringBuilder();
            if (matcher.group(1) != null) {
                repl.append(matcher.group(1));
                repl.append("(");
            }
            if (matcher.group(2) != null) {
                repl.append((String)tableNameProcessor.apply((Object)"$2"));
                repl.append(".");
            }
            repl.append((String)columnNameProcessor.apply((Object)"$3"));
            if (matcher.group(1) != null) {
                repl.append(")");
            }
            matcher.appendReplacement(sql, repl.toString());
        }
        matcher.appendTail(sql);
        return sql.toString();
    }

    @Deprecated
    public static void closeQuietly(Connection connection) {
        Common.closeQuietly(connection);
    }

    @Deprecated
    public static void closeQuietly(Statement statement) {
        Common.closeQuietly(statement);
    }

    @Deprecated
    public static void closeQuietly(ResultSet resultSet) {
        Common.closeQuietly(resultSet);
    }

    @Deprecated
    public static void closeQuietly(Statement st, Connection c) {
        SqlUtils.closeQuietly(st);
        SqlUtils.closeQuietly(c);
    }

    @Deprecated
    public static void closeQuietly(ResultSet rs, Statement st, Connection c) {
        SqlUtils.closeQuietly(rs);
        SqlUtils.closeQuietly(st, c);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.HashSet;
import java.util.Iterator;
import org.hibernate.dialect.Dialect;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.QueryJoinFragment;

public class QuerySelect {
    private Dialect dialect;
    private JoinFragment joins;
    private StringBuilder select = new StringBuilder();
    private StringBuilder where = new StringBuilder();
    private StringBuilder groupBy = new StringBuilder();
    private StringBuilder orderBy = new StringBuilder();
    private StringBuilder having = new StringBuilder();
    private String comment;
    private boolean distinct;
    private static final HashSet<String> DONT_SPACE_TOKENS = new HashSet();

    public QuerySelect(Dialect dialect) {
        this.dialect = dialect;
        this.joins = new QueryJoinFragment(dialect, false);
    }

    public JoinFragment getJoinFragment() {
        return this.joins;
    }

    public void addSelectFragmentString(String fragment) {
        if (fragment.length() > 0 && fragment.charAt(0) == ',') {
            fragment = fragment.substring(1);
        }
        if ((fragment = fragment.trim()).length() > 0) {
            if (this.select.length() > 0) {
                this.select.append(", ");
            }
            this.select.append(fragment);
        }
    }

    public void addSelectColumn(String columnName, String alias) {
        this.addSelectFragmentString(columnName + ' ' + alias);
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public void setWhereTokens(Iterator tokens) {
        QuerySelect.appendTokens(this.where, tokens);
    }

    public void prependWhereConditions(String conditions) {
        if (this.where.length() > 0) {
            this.where.insert(0, conditions + " and ");
        } else {
            this.where.append(conditions);
        }
    }

    public void setGroupByTokens(Iterator tokens) {
        QuerySelect.appendTokens(this.groupBy, tokens);
    }

    public void setOrderByTokens(Iterator tokens) {
        QuerySelect.appendTokens(this.orderBy, tokens);
    }

    public void setHavingTokens(Iterator tokens) {
        QuerySelect.appendTokens(this.having, tokens);
    }

    public void addOrderBy(String orderByString) {
        if (this.orderBy.length() > 0) {
            this.orderBy.append(", ");
        }
        this.orderBy.append(orderByString);
    }

    public String toQueryString() {
        boolean hasWhereConditions;
        String from;
        StringBuilder buf = new StringBuilder(50);
        if (this.comment != null) {
            buf.append("/* ").append(Dialect.escapeComment(this.comment)).append(" */ ");
        }
        buf.append("select ");
        if (this.distinct) {
            buf.append("distinct ");
        }
        if ((from = this.joins.toFromFragmentString()).startsWith(",")) {
            from = from.substring(1);
        } else if (from.startsWith(" inner join")) {
            from = from.substring(11);
        }
        buf.append(this.select.toString()).append(" from").append(from);
        String outerJoinsAfterWhere = this.joins.toWhereFragmentString().trim();
        String whereConditions = this.where.toString().trim();
        boolean hasOuterJoinsAfterWhere = outerJoinsAfterWhere.length() > 0;
        boolean bl = hasWhereConditions = whereConditions.length() > 0;
        if (hasOuterJoinsAfterWhere || hasWhereConditions) {
            buf.append(" where ");
            if (hasOuterJoinsAfterWhere) {
                buf.append(outerJoinsAfterWhere.substring(4));
            }
            if (hasWhereConditions) {
                if (hasOuterJoinsAfterWhere) {
                    buf.append(" and (");
                }
                buf.append(whereConditions);
                if (hasOuterJoinsAfterWhere) {
                    buf.append(")");
                }
            }
        }
        if (this.groupBy.length() > 0) {
            buf.append(" group by ").append(this.groupBy.toString());
        }
        if (this.having.length() > 0) {
            buf.append(" having ").append(this.having.toString());
        }
        if (this.orderBy.length() > 0) {
            buf.append(" order by ").append(this.orderBy.toString());
        }
        return this.dialect.transformSelectString(buf.toString());
    }

    private static void appendTokens(StringBuilder buf, Iterator iter) {
        boolean lastSpaceable = true;
        boolean lastQuoted = false;
        while (iter.hasNext()) {
            String token = (String)iter.next();
            boolean spaceable = !DONT_SPACE_TOKENS.contains(token);
            boolean quoted = token.startsWith("'");
            if (spaceable && lastSpaceable && (!quoted || !lastQuoted)) {
                buf.append(' ');
            }
            lastSpaceable = spaceable;
            buf.append(token);
            lastQuoted = token.endsWith("'");
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public QuerySelect copy() {
        QuerySelect copy = new QuerySelect(this.dialect);
        copy.joins = this.joins.copy();
        copy.select.append(this.select.toString());
        copy.where.append(this.where.toString());
        copy.groupBy.append(this.groupBy.toString());
        copy.orderBy.append(this.orderBy.toString());
        copy.having.append(this.having.toString());
        copy.comment = this.comment;
        copy.distinct = this.distinct;
        return copy;
    }

    static {
        DONT_SPACE_TOKENS.add(".");
        DONT_SPACE_TOKENS.add("+");
        DONT_SPACE_TOKENS.add("-");
        DONT_SPACE_TOKENS.add("/");
        DONT_SPACE_TOKENS.add("*");
        DONT_SPACE_TOKENS.add("<");
        DONT_SPACE_TOKENS.add(">");
        DONT_SPACE_TOKENS.add("=");
        DONT_SPACE_TOKENS.add("#");
        DONT_SPACE_TOKENS.add("~");
        DONT_SPACE_TOKENS.add("|");
        DONT_SPACE_TOKENS.add("&");
        DONT_SPACE_TOKENS.add("<=");
        DONT_SPACE_TOKENS.add(">=");
        DONT_SPACE_TOKENS.add("=>");
        DONT_SPACE_TOKENS.add("=<");
        DONT_SPACE_TOKENS.add("!=");
        DONT_SPACE_TOKENS.add("<>");
        DONT_SPACE_TOKENS.add("!#");
        DONT_SPACE_TOKENS.add("!~");
        DONT_SPACE_TOKENS.add("!<");
        DONT_SPACE_TOKENS.add("!>");
        DONT_SPACE_TOKENS.add("(");
        DONT_SPACE_TOKENS.add(")");
    }
}


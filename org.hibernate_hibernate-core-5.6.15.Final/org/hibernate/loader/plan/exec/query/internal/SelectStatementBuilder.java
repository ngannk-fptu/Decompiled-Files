/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.query.internal;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;

public class SelectStatementBuilder {
    public final Dialect dialect;
    private StringBuilder selectClause = new StringBuilder();
    private StringBuilder fromClause = new StringBuilder();
    private String outerJoinsAfterFrom;
    private StringBuilder whereClause;
    private String outerJoinsAfterWhere;
    private StringBuilder orderByClause;
    private String comment;
    private LockOptions lockOptions = new LockOptions();
    private int guesstimatedBufferSize = 20;

    public SelectStatementBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public void appendSelectClauseFragment(String selection) {
        if (this.selectClause.length() > 0) {
            this.selectClause.append(", ");
            this.guesstimatedBufferSize += 2;
        }
        this.selectClause.append(selection);
        this.guesstimatedBufferSize += selection.length();
    }

    public void appendFromClauseFragment(String fragment) {
        if (this.fromClause.length() > 0) {
            this.fromClause.append(", ");
            this.guesstimatedBufferSize += 2;
        }
        this.fromClause.append(fragment);
        this.guesstimatedBufferSize += fragment.length();
    }

    public void appendFromClauseFragment(String tableName, String alias) {
        this.appendFromClauseFragment(tableName + ' ' + alias);
    }

    public void appendRestrictions(String restrictions) {
        String cleaned = this.cleanRestrictions(restrictions);
        if (StringHelper.isEmpty(cleaned)) {
            return;
        }
        this.guesstimatedBufferSize += cleaned.length();
        if (this.whereClause == null) {
            this.whereClause = new StringBuilder(cleaned);
        } else {
            this.whereClause.append(" and ").append(cleaned);
            this.guesstimatedBufferSize += 5;
        }
    }

    private String cleanRestrictions(String restrictions) {
        if ((restrictions = restrictions.trim()).startsWith("and ")) {
            restrictions = restrictions.substring(4);
        }
        if (restrictions.endsWith(" and")) {
            restrictions = restrictions.substring(0, restrictions.length() - 4);
        }
        return restrictions;
    }

    public void setOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
        String cleanRestrictions;
        this.outerJoinsAfterFrom = outerJoinsAfterFrom;
        this.outerJoinsAfterWhere = cleanRestrictions = this.cleanRestrictions(outerJoinsAfterWhere);
        this.guesstimatedBufferSize += outerJoinsAfterFrom.length() + cleanRestrictions.length();
    }

    public void appendOrderByFragment(String ordering) {
        if (this.orderByClause == null) {
            this.orderByClause = new StringBuilder();
        } else {
            this.orderByClause.append(", ");
            this.guesstimatedBufferSize += 2;
        }
        this.orderByClause.append(ordering);
    }

    public void setComment(String comment) {
        this.comment = comment;
        this.guesstimatedBufferSize += comment.length();
    }

    public void setLockMode(LockMode lockMode) {
        this.lockOptions.setLockMode(lockMode);
    }

    public void setLockOptions(LockOptions lockOptions) {
        LockOptions.copy(lockOptions, this.lockOptions);
    }

    public String toStatementString() {
        StringBuilder buf = new StringBuilder(this.guesstimatedBufferSize);
        if (StringHelper.isNotEmpty(this.comment)) {
            buf.append("/* ").append(Dialect.escapeComment(this.comment)).append(" */ ");
        }
        buf.append("select ").append((CharSequence)this.selectClause).append(" from ").append((CharSequence)this.fromClause);
        if (StringHelper.isNotEmpty(this.outerJoinsAfterFrom)) {
            buf.append(this.outerJoinsAfterFrom);
        }
        if (this.isNotEmpty(this.whereClause) || this.isNotEmpty(this.outerJoinsAfterWhere)) {
            buf.append(" where ");
            if (StringHelper.isNotEmpty(this.outerJoinsAfterWhere)) {
                buf.append(this.outerJoinsAfterWhere);
                if (this.isNotEmpty(this.whereClause)) {
                    buf.append(" and ");
                }
            }
            if (this.isNotEmpty(this.whereClause)) {
                buf.append((CharSequence)this.whereClause);
            }
        }
        if (this.orderByClause != null) {
            buf.append(" order by ").append((CharSequence)this.orderByClause);
        }
        if (this.lockOptions.getLockMode() != LockMode.NONE) {
            buf = new StringBuilder(this.dialect.applyLocksToSql(buf.toString(), this.lockOptions, null));
        }
        return this.dialect.transformSelectString(buf.toString());
    }

    private boolean isNotEmpty(String string) {
        return StringHelper.isNotEmpty(string);
    }

    private boolean isNotEmpty(StringBuilder builder) {
        return builder != null && builder.length() > 0;
    }
}


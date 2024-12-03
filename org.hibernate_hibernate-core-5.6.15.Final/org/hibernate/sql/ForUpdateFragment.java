/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;

public class ForUpdateFragment {
    private final StringBuilder aliases = new StringBuilder();
    private boolean isNowaitEnabled;
    private boolean isSkipLockedEnabled;
    private final Dialect dialect;
    private LockMode lockMode;
    private LockOptions lockOptions;

    public ForUpdateFragment(Dialect dialect) {
        this.dialect = dialect;
    }

    public ForUpdateFragment(Dialect dialect, LockOptions lockOptions, Map<String, String[]> keyColumnNames) throws QueryException {
        this(dialect);
        LockMode lockMode;
        LockMode upgradeType = null;
        Iterator<Map.Entry<String, LockMode>> iter = lockOptions.getAliasLockIterator();
        this.lockOptions = lockOptions;
        if (!iter.hasNext() && LockMode.READ.lessThan(lockMode = lockOptions.getLockMode())) {
            upgradeType = lockMode;
            this.lockMode = lockMode;
        }
        while (iter.hasNext()) {
            Map.Entry<String, LockMode> me = iter.next();
            LockMode lockMode2 = me.getValue();
            if (!LockMode.READ.lessThan(lockMode2)) continue;
            String tableAlias = me.getKey();
            if (dialect.forUpdateOfColumns()) {
                String[] keyColumns = keyColumnNames.get(tableAlias);
                if (keyColumns == null) {
                    throw new IllegalArgumentException("alias not found: " + tableAlias);
                }
                for (String keyColumn : keyColumns = StringHelper.qualify(tableAlias, keyColumns)) {
                    this.addTableAlias(keyColumn);
                }
            } else {
                this.addTableAlias(tableAlias);
            }
            if (upgradeType != null && lockMode2 != upgradeType) {
                throw new QueryException("mixed LockModes");
            }
            upgradeType = lockMode2;
        }
        if (upgradeType == LockMode.UPGRADE_NOWAIT || lockOptions.getTimeOut() == 0) {
            this.setNowaitEnabled(true);
        }
        if (upgradeType == LockMode.UPGRADE_SKIPLOCKED || lockOptions.getTimeOut() == -2) {
            this.setSkipLockedEnabled(true);
        }
    }

    public ForUpdateFragment addTableAlias(String alias) {
        if (this.aliases.length() > 0) {
            this.aliases.append(", ");
        }
        this.aliases.append(alias);
        return this;
    }

    public String toFragmentString() {
        if (this.lockOptions != null) {
            if (this.aliases.length() == 0) {
                return this.dialect.getForUpdateString(this.lockOptions);
            }
            return this.dialect.getForUpdateString(this.aliases.toString(), this.lockOptions);
        }
        if (this.aliases.length() == 0) {
            if (this.lockMode != null) {
                return this.dialect.getForUpdateString(this.lockMode);
            }
            return "";
        }
        if (this.isNowaitEnabled) {
            return this.dialect.getForUpdateNowaitString(this.aliases.toString());
        }
        if (this.isSkipLockedEnabled) {
            return this.dialect.getForUpdateSkipLockedString(this.aliases.toString());
        }
        return this.dialect.getForUpdateString(this.aliases.toString());
    }

    public ForUpdateFragment setNowaitEnabled(boolean nowait) {
        this.isNowaitEnabled = nowait;
        return this;
    }

    public ForUpdateFragment setSkipLockedEnabled(boolean skipLocked) {
        this.isSkipLockedEnabled = skipLocked;
        return this;
    }
}


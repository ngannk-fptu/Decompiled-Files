/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi.sql;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;

public abstract class NativeSQLQueryNonScalarReturn
implements NativeSQLQueryReturn,
Serializable {
    private final String alias;
    private final LockMode lockMode;
    private final Map<String, String[]> propertyResults = new HashMap<String, String[]>();
    private final int hashCode;

    protected NativeSQLQueryNonScalarReturn(String alias, Map<String, String[]> propertyResults, LockMode lockMode) {
        this.alias = alias;
        if (alias == null) {
            throw new HibernateException("alias must be specified");
        }
        this.lockMode = lockMode;
        if (propertyResults != null) {
            this.propertyResults.putAll(propertyResults);
        }
        this.hashCode = this.determineHashCode();
    }

    private int determineHashCode() {
        int result = this.alias != null ? this.alias.hashCode() : 0;
        result = 31 * result + this.getClass().getName().hashCode();
        result = 31 * result + (this.lockMode != null ? this.lockMode.hashCode() : 0);
        result = 31 * result + this.propertyResults.hashCode();
        return result;
    }

    public String getAlias() {
        return this.alias;
    }

    public LockMode getLockMode() {
        return this.lockMode;
    }

    public Map<String, String[]> getPropertyResultsMap() {
        return Collections.unmodifiableMap(this.propertyResults);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NativeSQLQueryNonScalarReturn that = (NativeSQLQueryNonScalarReturn)o;
        if (this.alias != null ? !this.alias.equals(that.alias) : that.alias != null) {
            return false;
        }
        if (this.lockMode != null ? !this.lockMode.equals((Object)that.lockMode) : that.lockMode != null) {
            return false;
        }
        return this.propertyResults.equals(that.propertyResults);
    }

    @Override
    public void traceLog(NativeSQLQueryReturn.TraceLogger logger2) {
        if (NativeSQLQueryRootReturn.class.isInstance(this)) {
            logger2.writeLine("Entity(...)");
        } else if (NativeSQLQueryCollectionReturn.class.isInstance(this)) {
            logger2.writeLine("Collection(...)");
        } else if (NativeSQLQueryJoinReturn.class.isInstance(this)) {
            logger2.writeLine("Join(...)");
        } else {
            logger2.writeLine(this.getClass().getName() + "(...)");
        }
    }
}


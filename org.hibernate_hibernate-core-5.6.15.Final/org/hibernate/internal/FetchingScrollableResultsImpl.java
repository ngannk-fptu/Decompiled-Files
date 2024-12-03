/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.internal.AbstractScrollableResults;
import org.hibernate.loader.Loader;
import org.hibernate.type.Type;

public class FetchingScrollableResultsImpl
extends AbstractScrollableResults {
    private Object[] currentRow;
    private int currentPosition;
    private Integer maxPosition;

    public FetchingScrollableResultsImpl(ResultSet rs, PreparedStatement ps, SharedSessionContractImplementor sess, Loader loader, QueryParameters queryParameters, Type[] types, HolderInstantiator holderInstantiator) {
        super(rs, ps, sess, loader, queryParameters, types, holderInstantiator);
    }

    @Override
    protected Object[] getCurrentRow() {
        return this.currentRow;
    }

    @Override
    public boolean next() {
        boolean afterLast;
        if (this.maxPosition != null && this.maxPosition <= this.currentPosition) {
            this.currentRow = null;
            this.currentPosition = this.maxPosition + 1;
            return false;
        }
        if (this.isResultSetEmpty()) {
            this.currentRow = null;
            this.currentPosition = 0;
            return false;
        }
        Object row = this.getLoader().loadSequentialRowsForward(this.getResultSet(), this.getSession(), this.getQueryParameters(), true);
        try {
            afterLast = this.getResultSet().isAfterLast();
        }
        catch (SQLException e) {
            throw this.getSession().getFactory().getSQLExceptionHelper().convert(e, "exception calling isAfterLast()");
        }
        ++this.currentPosition;
        this.currentRow = new Object[]{row};
        if (afterLast && this.maxPosition == null) {
            this.maxPosition = this.currentPosition;
        }
        this.afterScrollOperation();
        return true;
    }

    @Override
    public boolean previous() {
        if (this.currentPosition <= 1) {
            this.currentPosition = 0;
            this.currentRow = null;
            return false;
        }
        Object loadResult = this.getLoader().loadSequentialRowsReverse(this.getResultSet(), this.getSession(), this.getQueryParameters(), false, this.maxPosition != null && this.currentPosition > this.maxPosition);
        this.currentRow = new Object[]{loadResult};
        --this.currentPosition;
        this.afterScrollOperation();
        return true;
    }

    @Override
    public boolean scroll(int positions) {
        boolean more = false;
        if (positions > 0) {
            for (int i = 0; i < positions && (more = this.next()); ++i) {
            }
        } else if (positions < 0) {
            for (int i = 0; i < 0 - positions && (more = this.previous()); ++i) {
            }
        } else {
            throw new HibernateException("scroll(0) not valid");
        }
        this.afterScrollOperation();
        return more;
    }

    @Override
    public boolean last() {
        boolean more = false;
        if (this.maxPosition != null) {
            if (this.currentPosition > this.maxPosition) {
                more = this.previous();
            }
            for (int i = this.currentPosition; i < this.maxPosition; ++i) {
                more = this.next();
            }
        } else {
            try {
                if (this.isResultSetEmpty() || this.getResultSet().isAfterLast()) {
                    return false;
                }
                while (!this.getResultSet().isAfterLast()) {
                    more = this.next();
                }
            }
            catch (SQLException e) {
                throw this.getSession().getFactory().getSQLExceptionHelper().convert(e, "exception calling isAfterLast()");
            }
        }
        this.afterScrollOperation();
        return more;
    }

    @Override
    public boolean first() {
        this.beforeFirst();
        boolean more = this.next();
        this.afterScrollOperation();
        return more;
    }

    @Override
    public void beforeFirst() {
        try {
            this.getResultSet().beforeFirst();
        }
        catch (SQLException e) {
            throw this.getSession().getFactory().getSQLExceptionHelper().convert(e, "exception calling beforeFirst()");
        }
        this.currentRow = null;
        this.currentPosition = 0;
    }

    @Override
    public void afterLast() {
        this.last();
        this.next();
        this.afterScrollOperation();
    }

    @Override
    public boolean isFirst() {
        return this.currentPosition == 1;
    }

    @Override
    public boolean isLast() {
        return this.maxPosition != null && this.currentPosition == this.maxPosition;
    }

    @Override
    public int getRowNumber() {
        return this.currentPosition;
    }

    @Override
    public boolean setRowNumber(int rowNumber) {
        if (rowNumber == 1) {
            return this.first();
        }
        if (rowNumber == -1) {
            return this.last();
        }
        if (this.maxPosition != null && rowNumber == this.maxPosition) {
            return this.last();
        }
        return this.scroll(rowNumber - this.currentPosition);
    }

    private boolean isResultSetEmpty() {
        try {
            return this.currentPosition == 0 && !this.getResultSet().isBeforeFirst() && !this.getResultSet().isAfterLast();
        }
        catch (SQLException e) {
            throw this.getSession().getFactory().getSQLExceptionHelper().convert(e, "Could not determine if resultset is empty due to exception calling isBeforeFirst or isAfterLast()");
        }
    }
}


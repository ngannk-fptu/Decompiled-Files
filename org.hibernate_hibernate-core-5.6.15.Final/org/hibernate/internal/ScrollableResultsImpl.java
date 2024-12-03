/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.internal.AbstractScrollableResults;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.Loader;
import org.hibernate.type.Type;

public class ScrollableResultsImpl
extends AbstractScrollableResults
implements ScrollableResults {
    private Object[] currentRow;

    public ScrollableResultsImpl(ResultSet rs, PreparedStatement ps, SharedSessionContractImplementor sess, Loader loader, QueryParameters queryParameters, Type[] types, HolderInstantiator holderInstantiator) {
        super(rs, ps, sess, loader, queryParameters, types, holderInstantiator);
    }

    @Override
    protected Object[] getCurrentRow() {
        return this.currentRow;
    }

    @Override
    public boolean scroll(int i) {
        try {
            boolean result = this.getResultSet().relative(i);
            this.prepareCurrentRow(result);
            return result;
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "could not advance using scroll()");
        }
    }

    protected JDBCException convert(SQLException sqle, String message) {
        return this.getSession().getFactory().getSQLExceptionHelper().convert(sqle, message);
    }

    @Override
    public boolean first() {
        try {
            boolean result = this.getResultSet().first();
            this.prepareCurrentRow(result);
            return result;
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "could not advance using first()");
        }
    }

    @Override
    public boolean last() {
        try {
            boolean result = this.getResultSet().last();
            this.prepareCurrentRow(result);
            return result;
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "could not advance using last()");
        }
    }

    @Override
    public boolean next() {
        try {
            boolean result = this.getResultSet().next();
            this.prepareCurrentRow(result);
            return result;
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "could not advance using next()");
        }
    }

    @Override
    public boolean previous() {
        try {
            boolean result = this.getResultSet().previous();
            this.prepareCurrentRow(result);
            return result;
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "could not advance using previous()");
        }
    }

    @Override
    public void afterLast() {
        try {
            this.getResultSet().afterLast();
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "exception calling afterLast()");
        }
    }

    @Override
    public void beforeFirst() {
        try {
            this.getResultSet().beforeFirst();
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "exception calling beforeFirst()");
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return this.getResultSet().isFirst();
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "exception calling isFirst()");
        }
    }

    @Override
    public boolean isLast() {
        try {
            return this.getResultSet().isLast();
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "exception calling isLast()");
        }
    }

    @Override
    public int getRowNumber() throws HibernateException {
        try {
            return this.getResultSet().getRow() - 1;
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "exception calling getRow()");
        }
    }

    @Override
    public boolean setRowNumber(int rowNumber) throws HibernateException {
        if (rowNumber >= 0) {
            ++rowNumber;
        }
        try {
            boolean result = this.getResultSet().absolute(rowNumber);
            this.prepareCurrentRow(result);
            return result;
        }
        catch (SQLException sqle) {
            throw this.convert(sqle, "could not advance using absolute()");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void prepareCurrentRow(boolean underlyingScrollSuccessful) {
        if (!underlyingScrollSuccessful) {
            this.currentRow = null;
            return;
        }
        PersistenceContext persistenceContext = this.getSession().getPersistenceContextInternal();
        persistenceContext.beforeLoad();
        try {
            Object result = this.getLoader().loadSingleRow(this.getResultSet(), this.getSession(), this.getQueryParameters(), true);
            this.currentRow = result != null && result.getClass().isArray() ? ArrayHelper.toObjectArray(result) : new Object[]{result};
            if (this.getHolderInstantiator() != null) {
                this.currentRow = new Object[]{this.getHolderInstantiator().instantiate(this.currentRow)};
            }
        }
        finally {
            persistenceContext.afterLoad();
        }
        this.afterScrollOperation();
    }
}


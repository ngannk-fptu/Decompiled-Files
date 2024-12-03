/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.Loader;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public abstract class AbstractScrollableResults
implements ScrollableResultsImplementor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractScrollableResults.class);
    private final ResultSet resultSet;
    private final PreparedStatement ps;
    private final SharedSessionContractImplementor session;
    private final Loader loader;
    private final QueryParameters queryParameters;
    private final Type[] types;
    private HolderInstantiator holderInstantiator;
    private boolean closed;

    protected AbstractScrollableResults(ResultSet rs, PreparedStatement ps, SharedSessionContractImplementor sess, Loader loader, QueryParameters queryParameters, Type[] types, HolderInstantiator holderInstantiator) {
        this.resultSet = rs;
        this.ps = ps;
        this.session = sess;
        this.loader = loader;
        this.queryParameters = queryParameters;
        this.types = types;
        this.holderInstantiator = holderInstantiator != null && holderInstantiator.isRequired() ? holderInstantiator : null;
    }

    protected abstract Object[] getCurrentRow();

    protected ResultSet getResultSet() {
        return this.resultSet;
    }

    protected PreparedStatement getPs() {
        return this.ps;
    }

    protected SharedSessionContractImplementor getSession() {
        return this.session;
    }

    protected Loader getLoader() {
        return this.loader;
    }

    protected QueryParameters getQueryParameters() {
        return this.queryParameters;
    }

    protected Type[] getTypes() {
        return this.types;
    }

    protected HolderInstantiator getHolderInstantiator() {
        return this.holderInstantiator;
    }

    @Override
    public final void close() {
        block3: {
            if (this.closed) {
                return;
            }
            JdbcCoordinator jdbcCoordinator = this.session.getJdbcCoordinator();
            jdbcCoordinator.getResourceRegistry().release(this.ps);
            jdbcCoordinator.afterStatementExecution();
            try {
                this.session.getPersistenceContextInternal().getLoadContexts().cleanup(this.resultSet);
            }
            catch (Throwable ignore) {
                if (!LOG.isTraceEnabled()) break block3;
                LOG.tracev("Exception trying to cleanup load context : {0}", ignore.getMessage());
            }
        }
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public int getNumberOfTypes() {
        return this.types.length;
    }

    @Override
    public final Object[] get() throws HibernateException {
        if (this.closed) {
            throw new IllegalStateException("ScrollableResults is closed");
        }
        return this.getCurrentRow();
    }

    @Override
    public final Object get(int col) throws HibernateException {
        if (this.closed) {
            throw new IllegalStateException("ScrollableResults is closed");
        }
        return this.getCurrentRow()[col];
    }

    protected final Object getFinal(int col, Type returnType) throws HibernateException {
        if (this.closed) {
            throw new IllegalStateException("ScrollableResults is closed");
        }
        if (this.holderInstantiator != null) {
            throw new HibernateException("query specifies a holder class");
        }
        if (returnType.getReturnedClass() == this.types[col].getReturnedClass()) {
            return this.get(col);
        }
        return this.throwInvalidColumnTypeException(col, this.types[col], returnType);
    }

    protected final Object getNonFinal(int col, Type returnType) throws HibernateException {
        if (this.closed) {
            throw new IllegalStateException("ScrollableResults is closed");
        }
        if (this.holderInstantiator != null) {
            throw new HibernateException("query specifies a holder class");
        }
        if (returnType.getReturnedClass().isAssignableFrom(this.types[col].getReturnedClass())) {
            return this.get(col);
        }
        return this.throwInvalidColumnTypeException(col, this.types[col], returnType);
    }

    @Override
    public final BigDecimal getBigDecimal(int col) throws HibernateException {
        return (BigDecimal)this.getFinal(col, StandardBasicTypes.BIG_DECIMAL);
    }

    @Override
    public final BigInteger getBigInteger(int col) throws HibernateException {
        return (BigInteger)this.getFinal(col, StandardBasicTypes.BIG_INTEGER);
    }

    @Override
    public final byte[] getBinary(int col) throws HibernateException {
        return (byte[])this.getFinal(col, StandardBasicTypes.BINARY);
    }

    @Override
    public final String getText(int col) throws HibernateException {
        return (String)this.getFinal(col, StandardBasicTypes.TEXT);
    }

    @Override
    public final Blob getBlob(int col) throws HibernateException {
        return (Blob)this.getNonFinal(col, StandardBasicTypes.BLOB);
    }

    @Override
    public final Clob getClob(int col) throws HibernateException {
        return (Clob)this.getNonFinal(col, StandardBasicTypes.CLOB);
    }

    @Override
    public final Boolean getBoolean(int col) throws HibernateException {
        return (Boolean)this.getFinal(col, StandardBasicTypes.BOOLEAN);
    }

    @Override
    public final Byte getByte(int col) throws HibernateException {
        return (Byte)this.getFinal(col, StandardBasicTypes.BYTE);
    }

    @Override
    public final Character getCharacter(int col) throws HibernateException {
        return (Character)this.getFinal(col, StandardBasicTypes.CHARACTER);
    }

    @Override
    public final Date getDate(int col) throws HibernateException {
        return (Date)this.getNonFinal(col, StandardBasicTypes.TIMESTAMP);
    }

    @Override
    public final Calendar getCalendar(int col) throws HibernateException {
        return (Calendar)this.getNonFinal(col, StandardBasicTypes.CALENDAR);
    }

    @Override
    public final Double getDouble(int col) throws HibernateException {
        return (Double)this.getFinal(col, StandardBasicTypes.DOUBLE);
    }

    @Override
    public final Float getFloat(int col) throws HibernateException {
        return (Float)this.getFinal(col, StandardBasicTypes.FLOAT);
    }

    @Override
    public final Integer getInteger(int col) throws HibernateException {
        return (Integer)this.getFinal(col, StandardBasicTypes.INTEGER);
    }

    @Override
    public final Long getLong(int col) throws HibernateException {
        return (Long)this.getFinal(col, StandardBasicTypes.LONG);
    }

    @Override
    public final Short getShort(int col) throws HibernateException {
        return (Short)this.getFinal(col, StandardBasicTypes.SHORT);
    }

    @Override
    public final String getString(int col) throws HibernateException {
        return (String)this.getFinal(col, StandardBasicTypes.STRING);
    }

    @Override
    public final Locale getLocale(int col) throws HibernateException {
        return (Locale)this.getFinal(col, StandardBasicTypes.LOCALE);
    }

    @Override
    public final TimeZone getTimeZone(int col) throws HibernateException {
        return (TimeZone)this.getNonFinal(col, StandardBasicTypes.TIMEZONE);
    }

    @Override
    public final Type getType(int i) {
        return this.types[i];
    }

    private Object throwInvalidColumnTypeException(int i, Type type, Type returnType) throws HibernateException {
        throw new HibernateException("incompatible column types: " + type.getName() + ", " + returnType.getName());
    }

    protected void afterScrollOperation() {
        this.session.afterScrollOperation();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 *  javax.persistence.TemporalType
 *  org.jboss.logging.Logger
 */
package org.hibernate.query.procedure.internal;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Calendar;
import javax.persistence.ParameterMode;
import javax.persistence.TemporalType;
import org.hibernate.engine.jdbc.cursor.spi.RefCursorSupport;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.procedure.ParameterBind;
import org.hibernate.procedure.ParameterMisuseException;
import org.hibernate.procedure.ParameterRegistration;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.procedure.spi.ParameterStrategy;
import org.hibernate.query.internal.QueryParameterImpl;
import org.hibernate.query.procedure.spi.ProcedureParameterImplementor;
import org.hibernate.type.CalendarDateType;
import org.hibernate.type.CalendarTimeType;
import org.hibernate.type.CalendarType;
import org.hibernate.type.ProcedureParameterExtractionAware;
import org.hibernate.type.ProcedureParameterNamedBinder;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class ProcedureParameterImpl<T>
extends QueryParameterImpl<T>
implements ProcedureParameterImplementor<T>,
ParameterRegistration<T> {
    private static final Logger log = Logger.getLogger(ProcedureParameterImpl.class);
    private final ProcedureCallImpl procedureCall;
    private final String name;
    private final Integer position;
    private final ParameterMode mode;
    private final Class<T> javaType;
    private int[] sqlTypes;
    private boolean passNullsEnabled;
    private int startIndex;

    public ProcedureParameterImpl(ProcedureCallImpl procedureCall, String name, ParameterMode mode, Class<T> javaType, Type hibernateType, boolean initialPassNullsSetting) {
        super(hibernateType);
        this.procedureCall = procedureCall;
        this.name = name;
        this.position = null;
        this.mode = mode;
        this.javaType = javaType;
        this.passNullsEnabled = initialPassNullsSetting;
        this.setHibernateType(hibernateType);
    }

    public ProcedureParameterImpl(ProcedureCallImpl procedureCall, Integer position, ParameterMode mode, Class<T> javaType, Type hibernateType, boolean initialPassNullsSetting) {
        super(hibernateType);
        this.procedureCall = procedureCall;
        this.name = null;
        this.position = position;
        this.mode = mode;
        this.javaType = javaType;
        this.passNullsEnabled = initialPassNullsSetting;
        this.setHibernateType(hibernateType);
    }

    @Override
    public ParameterMode getMode() {
        return this.mode;
    }

    @Override
    public boolean isPassNullsEnabled() {
        return this.passNullsEnabled;
    }

    @Override
    public void enablePassingNulls(boolean enabled) {
        this.passNullsEnabled = enabled;
    }

    @Override
    public int[] getSourceLocations() {
        return new int[0];
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getPosition() {
        return this.position;
    }

    @Override
    public void setHibernateType(Type expectedType) {
        super.setHibernateType(expectedType);
        if (this.mode == ParameterMode.REF_CURSOR) {
            this.sqlTypes = new int[]{2012};
        } else {
            if (expectedType == null) {
                throw new IllegalArgumentException("Type cannot be null");
            }
            this.sqlTypes = expectedType.sqlTypes(this.procedureCall.getSession().getFactory());
        }
    }

    @Override
    public Class<T> getParameterType() {
        return this.javaType;
    }

    @Override
    public ParameterBind<T> getBind() {
        return (ParameterBind)this.procedureCall.getQueryParameterBindings().getBinding(this);
    }

    @Override
    public void bindValue(Object value) {
        this.getBind().setBindValue(value);
    }

    @Override
    public void bindValue(Object value, TemporalType explicitTemporalType) {
        this.getBind().setBindValue(value, explicitTemporalType);
    }

    @Override
    public void prepare(CallableStatement statement, int startIndex) throws SQLException {
        Type typeToUse = this.getHibernateType();
        int[] sqlTypesToUse = this.sqlTypes;
        ParameterBind<T> bind = this.getBind();
        if (bind != null && bind.getExplicitTemporalType() != null && Calendar.class.isInstance(bind.getValue())) {
            switch (bind.getExplicitTemporalType()) {
                case TIMESTAMP: {
                    typeToUse = CalendarType.INSTANCE;
                    sqlTypesToUse = typeToUse.sqlTypes(this.procedureCall.getSession().getFactory());
                    break;
                }
                case DATE: {
                    typeToUse = CalendarDateType.INSTANCE;
                    sqlTypesToUse = typeToUse.sqlTypes(this.procedureCall.getSession().getFactory());
                    break;
                }
                case TIME: {
                    typeToUse = CalendarTimeType.INSTANCE;
                    sqlTypesToUse = typeToUse.sqlTypes(this.procedureCall.getSession().getFactory());
                }
            }
        }
        this.startIndex = startIndex;
        if (this.mode == ParameterMode.IN || this.mode == ParameterMode.INOUT || this.mode == ParameterMode.OUT) {
            if (this.mode == ParameterMode.INOUT || this.mode == ParameterMode.OUT) {
                if (sqlTypesToUse.length > 1) {
                    boolean canHandleMultiParamExtraction;
                    boolean bl = canHandleMultiParamExtraction = ProcedureParameterExtractionAware.class.isInstance(typeToUse) && ((ProcedureParameterExtractionAware)((Object)typeToUse)).canDoExtraction();
                    if (!canHandleMultiParamExtraction) {
                        throw new UnsupportedOperationException("Type [" + typeToUse + "] does support multi-parameter value extraction");
                    }
                }
                if (sqlTypesToUse.length == 1 && this.procedureCall.getParameterStrategy() == ParameterStrategy.NAMED && this.canDoNameParameterBinding(typeToUse)) {
                    statement.registerOutParameter(this.getName(), sqlTypesToUse[0]);
                } else {
                    for (int i = 0; i < sqlTypesToUse.length; ++i) {
                        statement.registerOutParameter(startIndex + i, sqlTypesToUse[i]);
                    }
                }
            }
            if (this.mode == ParameterMode.INOUT || this.mode == ParameterMode.IN) {
                if (bind == null || bind.getValue() == null) {
                    if (this.isPassNullsEnabled()) {
                        log.debugf("Stored procedure [%s] IN/INOUT parameter [%s] not bound and `passNulls` was set to true; binding NULL", (Object)this.procedureCall.getProcedureName(), (Object)this);
                        if (this.procedureCall.getParameterStrategy() == ParameterStrategy.NAMED && this.canDoNameParameterBinding(typeToUse)) {
                            ((ProcedureParameterNamedBinder)((Object)typeToUse)).nullSafeSet(statement, null, this.getName(), this.procedureCall.getSession());
                        } else {
                            typeToUse.nullSafeSet(statement, null, startIndex, this.procedureCall.getSession());
                        }
                    } else {
                        log.debugf("Stored procedure [%s] IN/INOUT parameter [%s] not bound and `passNulls` was set to false; assuming procedure defines default value", (Object)this.procedureCall.getProcedureName(), (Object)this);
                    }
                } else if (this.procedureCall.getParameterStrategy() == ParameterStrategy.NAMED && this.canDoNameParameterBinding(typeToUse)) {
                    ((ProcedureParameterNamedBinder)((Object)typeToUse)).nullSafeSet(statement, bind.getValue(), this.getName(), this.procedureCall.getSession());
                } else {
                    typeToUse.nullSafeSet(statement, bind.getValue(), startIndex, this.procedureCall.getSession());
                }
            }
        } else if (this.procedureCall.getParameterStrategy() == ParameterStrategy.NAMED) {
            this.procedureCall.getSession().getFactory().getServiceRegistry().getService(RefCursorSupport.class).registerRefCursorParameter(statement, this.getName());
        } else {
            this.procedureCall.getSession().getFactory().getServiceRegistry().getService(RefCursorSupport.class).registerRefCursorParameter(statement, startIndex);
        }
    }

    private boolean canDoNameParameterBinding(Type hibernateType) {
        ExtractedDatabaseMetaData databaseMetaData = this.procedureCall.getSession().getJdbcCoordinator().getJdbcSessionOwner().getJdbcSessionContext().getServiceRegistry().getService(JdbcEnvironment.class).getExtractedDatabaseMetaData();
        return databaseMetaData.supportsNamedParameters() && ProcedureParameterNamedBinder.class.isInstance(hibernateType) && ((ProcedureParameterNamedBinder)((Object)hibernateType)).canDoSetting();
    }

    @Override
    public int[] getSqlTypes() {
        if (this.mode == ParameterMode.REF_CURSOR) {
            throw new IllegalStateException("REF_CURSOR parameters do not have a SQL/JDBC type");
        }
        return this.determineHibernateType().sqlTypes(this.procedureCall.getSession().getFactory());
    }

    private Type determineHibernateType() {
        ParameterBind<T> bind = this.getBind();
        Type bindType = bind.getBindType();
        if (bindType != null) {
            return bindType;
        }
        Type paramType = this.getHibernateType();
        if (paramType != null) {
            return paramType;
        }
        if (bind.getValue() != null) {
            return this.procedureCall.getSession().getFactory().getTypeResolver().heuristicType(bind.getValue().getClass().getName());
        }
        throw new IllegalStateException("Unable to determine SQL type(s) - Hibernate Type not known");
    }

    @Override
    public T extract(CallableStatement statement) {
        if (this.mode == ParameterMode.IN) {
            throw new ParameterMisuseException("IN parameter not valid for output extraction");
        }
        try {
            boolean useNamed;
            if (this.mode == ParameterMode.REF_CURSOR) {
                if (this.procedureCall.getParameterStrategy() == ParameterStrategy.NAMED) {
                    return (T)statement.getObject(this.name);
                }
                return (T)statement.getObject(this.startIndex);
            }
            Type hibernateType = this.determineHibernateType();
            int[] sqlTypes = hibernateType.sqlTypes(this.procedureCall.getSession().getFactory());
            boolean bl = useNamed = sqlTypes.length == 1 && this.procedureCall.getParameterStrategy() == ParameterStrategy.NAMED && this.canDoNameParameterBinding(hibernateType);
            if (ProcedureParameterExtractionAware.class.isInstance(hibernateType)) {
                if (useNamed) {
                    return ((ProcedureParameterExtractionAware)((Object)hibernateType)).extract(statement, new String[]{this.getName()}, this.procedureCall.getSession());
                }
                return ((ProcedureParameterExtractionAware)((Object)hibernateType)).extract(statement, this.startIndex, this.procedureCall.getSession());
            }
            if (useNamed) {
                return (T)statement.getObject(this.name);
            }
            return (T)statement.getObject(this.startIndex);
        }
        catch (SQLException e) {
            throw this.procedureCall.getSession().getFactory().getSQLExceptionHelper().convert(e, "Unable to extract OUT/INOUT parameter value");
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.NoResultException
 *  javax.persistence.NonUniqueResultException
 *  javax.persistence.Parameter
 *  javax.persistence.ParameterMode
 *  javax.persistence.TemporalType
 *  org.jboss.logging.Logger
 */
package org.hibernate.procedure.internal;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.TemporalType;
import org.hibernate.HibernateException;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.procedure.NoSuchParameterException;
import org.hibernate.procedure.ParameterRegistration;
import org.hibernate.procedure.ParameterStrategyException;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.procedure.internal.ProcedureCallMementoImpl;
import org.hibernate.procedure.internal.ProcedureOutputsImpl;
import org.hibernate.procedure.internal.Util;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.procedure.spi.ParameterStrategy;
import org.hibernate.procedure.spi.ProcedureCallImplementor;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.procedure.internal.ProcedureParamBindings;
import org.hibernate.query.procedure.internal.ProcedureParameterImpl;
import org.hibernate.query.procedure.internal.ProcedureParameterMetadata;
import org.hibernate.query.procedure.spi.ProcedureParameterImplementor;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.result.NoMoreReturnsException;
import org.hibernate.result.Output;
import org.hibernate.result.ResultSetOutput;
import org.hibernate.result.UpdateCountOutput;
import org.hibernate.result.spi.ResultContext;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class ProcedureCallImpl<R>
extends AbstractProducedQuery<R>
implements ProcedureCallImplementor<R>,
ResultContext {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)ProcedureCallImpl.class.getName());
    private static final NativeSQLQueryReturn[] NO_RETURNS = new NativeSQLQueryReturn[0];
    private final String procedureName;
    private final NativeSQLQueryReturn[] queryReturns;
    private final boolean globalParameterPassNullsSetting;
    private final ProcedureParameterMetadata parameterMetadata;
    private final ProcedureParamBindings paramBindings;
    private Set<String> synchronizedQuerySpaces;
    private ProcedureOutputsImpl outputs;
    private ProcedureOutputs procedureResult;

    public ProcedureCallImpl(SharedSessionContractImplementor session, String procedureName) {
        super(session, null);
        this.procedureName = procedureName;
        this.globalParameterPassNullsSetting = session.getFactory().getSessionFactoryOptions().isProcedureParameterNullPassingEnabled();
        this.queryReturns = NO_RETURNS;
        this.parameterMetadata = new ProcedureParameterMetadata(this);
        this.paramBindings = new ProcedureParamBindings(this.parameterMetadata, this);
    }

    public ProcedureCallImpl(final SharedSessionContractImplementor session, String procedureName, Class ... resultClasses) {
        super(session, null);
        this.procedureName = procedureName;
        this.globalParameterPassNullsSetting = session.getFactory().getSessionFactoryOptions().isProcedureParameterNullPassingEnabled();
        final ArrayList collectedQueryReturns = new ArrayList();
        final HashSet<String> collectedQuerySpaces = new HashSet<String>();
        Util.resolveResultClasses(new Util.ResultClassesResolutionContext(){

            @Override
            public SessionFactoryImplementor getSessionFactory() {
                return session.getFactory();
            }

            @Override
            public void addQueryReturns(NativeSQLQueryReturn ... queryReturns) {
                Collections.addAll(collectedQueryReturns, queryReturns);
            }

            @Override
            public void addQuerySpaces(String ... spaces) {
                Collections.addAll(collectedQuerySpaces, spaces);
            }
        }, resultClasses);
        this.queryReturns = collectedQueryReturns.toArray(new NativeSQLQueryReturn[collectedQueryReturns.size()]);
        this.synchronizedQuerySpaces = collectedQuerySpaces;
        this.parameterMetadata = new ProcedureParameterMetadata(this);
        this.paramBindings = new ProcedureParamBindings(this.parameterMetadata, this);
    }

    public ProcedureCallImpl(final SharedSessionContractImplementor session, String procedureName, String ... resultSetMappings) {
        super(session, null);
        this.procedureName = procedureName;
        this.globalParameterPassNullsSetting = session.getFactory().getSessionFactoryOptions().isProcedureParameterNullPassingEnabled();
        final ArrayList collectedQueryReturns = new ArrayList();
        final HashSet<String> collectedQuerySpaces = new HashSet<String>();
        Util.resolveResultSetMappings(new Util.ResultSetMappingResolutionContext(){

            @Override
            public SessionFactoryImplementor getSessionFactory() {
                return session.getFactory();
            }

            @Override
            public ResultSetMappingDefinition findResultSetMapping(String name) {
                return session.getFactory().getNamedQueryRepository().getResultSetMappingDefinition(name);
            }

            @Override
            public void addQueryReturns(NativeSQLQueryReturn ... queryReturns) {
                Collections.addAll(collectedQueryReturns, queryReturns);
            }

            @Override
            public void addQuerySpaces(String ... spaces) {
                Collections.addAll(collectedQuerySpaces, spaces);
            }
        }, resultSetMappings);
        this.queryReturns = collectedQueryReturns.toArray(new NativeSQLQueryReturn[collectedQueryReturns.size()]);
        this.synchronizedQuerySpaces = collectedQuerySpaces;
        this.parameterMetadata = new ProcedureParameterMetadata(this);
        this.paramBindings = new ProcedureParamBindings(this.parameterMetadata, this);
    }

    ProcedureCallImpl(SharedSessionContractImplementor session, ProcedureCallMementoImpl memento) {
        super(session, null);
        this.procedureName = memento.getProcedureName();
        this.globalParameterPassNullsSetting = session.getFactory().getSessionFactoryOptions().isProcedureParameterNullPassingEnabled();
        this.queryReturns = memento.getQueryReturns();
        this.synchronizedQuerySpaces = Util.copy(memento.getSynchronizedQuerySpaces());
        this.parameterMetadata = new ProcedureParameterMetadata(this);
        this.paramBindings = new ProcedureParamBindings(this.parameterMetadata, this);
        for (ProcedureCallMementoImpl.ParameterMemento parameterMemento : memento.getParameterDeclarations()) {
            ProcedureParameterImpl registration = StringHelper.isNotEmpty(parameterMemento.getName()) ? new ProcedureParameterImpl(this, parameterMemento.getName(), parameterMemento.getMode(), parameterMemento.getType(), parameterMemento.getHibernateType(), parameterMemento.isPassNullsEnabled()) : new ProcedureParameterImpl(this, parameterMemento.getPosition(), parameterMemento.getMode(), parameterMemento.getType(), parameterMemento.getHibernateType(), parameterMemento.isPassNullsEnabled());
            this.getParameterMetadata().registerParameter(registration);
        }
        for (Map.Entry entry : memento.getHintsMap().entrySet()) {
            this.setHint((String)entry.getKey(), entry.getValue());
        }
    }

    @Override
    public ProcedureParameterMetadata getParameterMetadata() {
        return this.parameterMetadata;
    }

    @Override
    public QueryParameterBindings getQueryParameterBindings() {
        return this.paramBindings;
    }

    @Override
    public SharedSessionContractImplementor getSession() {
        return this.getProducer();
    }

    public ParameterStrategy getParameterStrategy() {
        return this.getParameterMetadata().getParameterStrategy();
    }

    @Override
    public String getProcedureName() {
        return this.procedureName;
    }

    @Override
    public String getSql() {
        return this.getProcedureName();
    }

    @Override
    public NativeSQLQueryReturn[] getQueryReturns() {
        return this.queryReturns;
    }

    @Override
    public <T> ParameterRegistration<T> registerParameter(int position, Class<T> type, ParameterMode mode) {
        ProcedureParameterImpl<T> procedureParameter = new ProcedureParameterImpl<T>(this, position, mode, type, this.getSession().getFactory().getTypeResolver().heuristicType(type.getName()), this.globalParameterPassNullsSetting);
        this.registerParameter(procedureParameter);
        return procedureParameter;
    }

    @Override
    public ProcedureCall registerParameter0(int position, Class type, ParameterMode mode) {
        this.registerParameter(position, type, mode);
        return this;
    }

    private void registerParameter(ProcedureParameterImplementor parameter) {
        this.getParameterMetadata().registerParameter(parameter);
    }

    @Override
    public ParameterRegistrationImplementor getParameterRegistration(int position) {
        return this.getParameterMetadata().getQueryParameter(position);
    }

    @Override
    public <T> ParameterRegistration<T> registerParameter(String name, Class<T> type, ParameterMode mode) {
        ProcedureParameterImpl<T> parameter = new ProcedureParameterImpl<T>(this, name, mode, type, this.getSession().getFactory().getTypeResolver().heuristicType(type.getName()), this.globalParameterPassNullsSetting);
        this.registerParameter(parameter);
        return parameter;
    }

    @Override
    public ProcedureCall registerParameter0(String name, Class type, ParameterMode mode) {
        this.registerParameter(name, type, mode);
        return this;
    }

    @Override
    public ParameterRegistrationImplementor getParameterRegistration(String name) {
        return this.getParameterMetadata().getQueryParameter(name);
    }

    public List getRegisteredParameters() {
        return new ArrayList(this.getParameterMetadata().collectAllParameters());
    }

    @Override
    public ProcedureOutputs getOutputs() {
        if (this.outputs == null) {
            this.outputs = this.buildOutputs();
        }
        return this.outputs;
    }

    private ProcedureOutputsImpl buildOutputs() {
        String call = this.getProducer().getJdbcServices().getJdbcEnvironment().getDialect().getCallableStatementSupport().renderCallableStatement(this.procedureName, this.getParameterMetadata(), this.paramBindings, this.getProducer());
        LOG.debugf("Preparing procedure call : %s", call);
        final CallableStatement statement = (CallableStatement)this.getSession().getJdbcCoordinator().getStatementPreparer().prepareStatement(call, true);
        this.getParameterMetadata().visitRegistrations(new Consumer<QueryParameter>(){
            int i = 1;

            @Override
            public void accept(QueryParameter queryParameter) {
                try {
                    ParameterRegistrationImplementor registration = (ParameterRegistrationImplementor)queryParameter;
                    registration.prepare(statement, this.i);
                    this.i = registration.getMode() == ParameterMode.REF_CURSOR ? ++this.i : (this.i += registration.getSqlTypes().length);
                }
                catch (SQLException e) {
                    throw ProcedureCallImpl.this.getSession().getJdbcServices().getSqlExceptionHelper().convert(e, "Error preparing registered callable parameter", ProcedureCallImpl.this.getProcedureName());
                }
            }
        });
        return new ProcedureOutputsImpl(this, statement);
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String[] getReturnAliases() {
        throw new UnsupportedOperationException("Procedure/function calls do not support returning aliases");
    }

    @Override
    public Type[] getReturnTypes() {
        throw new UnsupportedOperationException("Procedure/function calls do not support returning 'return types'");
    }

    @Override
    public ProcedureCallImplementor<R> setEntity(int position, Object val) {
        return null;
    }

    @Override
    public ProcedureCallImplementor<R> setEntity(String name, Object val) {
        return null;
    }

    protected Set<String> synchronizedQuerySpaces() {
        if (this.synchronizedQuerySpaces == null) {
            this.synchronizedQuerySpaces = new HashSet<String>();
        }
        return this.synchronizedQuerySpaces;
    }

    @Override
    public Set<String> getSynchronizedQuerySpaces() {
        if (this.synchronizedQuerySpaces == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(this.synchronizedQuerySpaces);
    }

    @Override
    public ProcedureCallImplementor<R> addSynchronizedQuerySpace(String querySpace) {
        this.synchronizedQuerySpaces().add(querySpace);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> addSynchronizedEntityName(String entityName) {
        this.addSynchronizedQuerySpaces(this.getSession().getFactory().getMetamodel().entityPersister(entityName));
        return this;
    }

    protected void addSynchronizedQuerySpaces(EntityPersister persister) {
        this.synchronizedQuerySpaces().addAll(Arrays.asList((String[])persister.getQuerySpaces()));
    }

    @Override
    public ProcedureCallImplementor<R> addSynchronizedEntityClass(Class entityClass) {
        this.addSynchronizedQuerySpaces(this.getSession().getFactory().getMetamodel().entityPersister(entityClass.getName()));
        return this;
    }

    @Override
    protected boolean isNativeQuery() {
        return false;
    }

    @Override
    public QueryParameters getQueryParameters() {
        QueryParameters qp = super.getQueryParameters();
        qp.setAutoDiscoverScalarTypes(true);
        qp.setCallable(true);
        return qp;
    }

    public ParameterRegistrationImplementor[] collectRefCursorParameters() {
        ArrayList refCursorParams = new ArrayList();
        this.getParameterMetadata().visitRegistrations(queryParameter -> {
            ParameterRegistrationImplementor registration = (ParameterRegistrationImplementor)queryParameter;
            if (registration.getMode() == ParameterMode.REF_CURSOR) {
                refCursorParams.add(registration);
            }
        });
        return refCursorParams.toArray(new ParameterRegistrationImplementor[refCursorParams.size()]);
    }

    @Override
    public ProcedureCallMemento extractMemento(Map<String, Object> hints) {
        return new ProcedureCallMementoImpl(this.procedureName, Util.copy(this.queryReturns), this.getParameterMetadata().getParameterStrategy(), ProcedureCallImpl.toParameterMementos(this.getParameterMetadata()), Util.copy(this.synchronizedQuerySpaces), Util.copy(hints));
    }

    @Override
    public ProcedureCallMemento extractMemento() {
        return new ProcedureCallMementoImpl(this.procedureName, Util.copy(this.queryReturns), this.getParameterMetadata().getParameterStrategy(), ProcedureCallImpl.toParameterMementos(this.getParameterMetadata()), Util.copy(this.synchronizedQuerySpaces), Util.copy(this.getHints()));
    }

    private static List<ProcedureCallMementoImpl.ParameterMemento> toParameterMementos(ProcedureParameterMetadata parameterMetadata) {
        if (parameterMetadata.getParameterStrategy() == ParameterStrategy.UNKNOWN) {
            return Collections.emptyList();
        }
        ArrayList<ProcedureCallMementoImpl.ParameterMemento> copy = new ArrayList<ProcedureCallMementoImpl.ParameterMemento>();
        parameterMetadata.visitRegistrations(queryParameter -> {
            ParameterRegistrationImplementor registration = (ParameterRegistrationImplementor)queryParameter;
            copy.add(ProcedureCallMementoImpl.ParameterMemento.fromRegistration(registration));
        });
        return copy;
    }

    @Override
    public ProcedureCallImplementor<R> registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
        this.getProducer().checkOpen(true);
        try {
            this.registerParameter(position, type, mode);
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he);
        }
        catch (RuntimeException e) {
            this.getProducer().markForRollbackOnly();
            throw e;
        }
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
        this.getProducer().checkOpen(true);
        try {
            this.registerParameter(parameterName, type, mode);
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he);
        }
        catch (RuntimeException e) {
            this.getProducer().markForRollbackOnly();
            throw e;
        }
        return this;
    }

    public boolean execute() {
        try {
            Output rtn = this.outputs().getCurrent();
            return rtn != null && ResultSetOutput.class.isInstance(rtn);
        }
        catch (NoMoreReturnsException e) {
            return false;
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he);
        }
        catch (RuntimeException e) {
            this.getProducer().markForRollbackOnly();
            throw e;
        }
    }

    protected ProcedureOutputs outputs() {
        if (this.procedureResult == null) {
            this.procedureResult = this.getOutputs();
        }
        return this.procedureResult;
    }

    @Override
    public int executeUpdate() {
        this.getProducer().checkTransactionNeededForUpdateOperation("javax.persistence.Query.executeUpdate requires active transaction");
        try {
            this.execute();
            int n = this.getUpdateCount();
            return n;
        }
        finally {
            this.outputs().release();
        }
    }

    public Object getOutputParameterValue(int position) {
        try {
            return this.outputs().getOutputParameterValue(position);
        }
        catch (ParameterStrategyException e) {
            throw new IllegalArgumentException("Invalid mix of named and positional parameters", (Throwable)((Object)e));
        }
        catch (NoSuchParameterException e) {
            throw new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e));
        }
    }

    public Object getOutputParameterValue(String parameterName) {
        try {
            return this.outputs().getOutputParameterValue(parameterName);
        }
        catch (ParameterStrategyException e) {
            throw new IllegalArgumentException("Invalid mix of named and positional parameters", (Throwable)((Object)e));
        }
        catch (NoSuchParameterException e) {
            throw new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e));
        }
    }

    public boolean hasMoreResults() {
        return this.outputs().goToNext() && ResultSetOutput.class.isInstance(this.outputs().getCurrent());
    }

    public int getUpdateCount() {
        try {
            Output rtn = this.outputs().getCurrent();
            if (rtn == null) {
                return -1;
            }
            if (UpdateCountOutput.class.isInstance(rtn)) {
                return ((UpdateCountOutput)rtn).getUpdateCount();
            }
            return -1;
        }
        catch (NoMoreReturnsException e) {
            return -1;
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he);
        }
        catch (RuntimeException e) {
            this.getProducer().markForRollbackOnly();
            throw e;
        }
    }

    @Override
    public List<R> getResultList() {
        if (this.getMaxResults() == 0) {
            return Collections.EMPTY_LIST;
        }
        try {
            Output rtn = this.outputs().getCurrent();
            if (!ResultSetOutput.class.isInstance(rtn)) {
                throw new IllegalStateException("Current CallableStatement ou was not a ResultSet, but getResultList was called");
            }
            return ((ResultSetOutput)rtn).getResultList();
        }
        catch (NoMoreReturnsException e) {
            return null;
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he);
        }
        catch (RuntimeException e) {
            this.getProducer().markForRollbackOnly();
            throw e;
        }
    }

    @Override
    public R getSingleResult() {
        List<R> resultList = this.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            throw new NoResultException(String.format("Call to stored procedure [%s] returned no results", this.getProcedureName()));
        }
        if (resultList.size() > 1) {
            throw new NonUniqueResultException(String.format("Call to stored procedure [%s] returned multiple results", this.getProcedureName()));
        }
        return resultList.get(0);
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        if (cls.isInstance(this)) {
            return (T)this;
        }
        if (cls.isInstance(this.outputs)) {
            return (T)this.outputs();
        }
        return super.unwrap(cls);
    }

    @Override
    public ProcedureCallImplementor<R> setLockMode(LockModeType lockMode) {
        throw new IllegalStateException("javax.persistence.Query.setLockMode not valid on javax.persistence.StoredProcedureQuery");
    }

    @Override
    public LockModeType getLockMode() {
        throw new IllegalStateException("javax.persistence.Query.getHibernateFlushMode not valid on javax.persistence.StoredProcedureQuery");
    }

    @Override
    public ProcedureCallImplementor<R> setHint(String hintName, Object value) {
        super.setHint(hintName, value);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setFlushMode(FlushModeType flushModeType) {
        super.setFlushMode(flushModeType);
        return this;
    }

    @Override
    public <P> ProcedureCallImplementor<R> setParameter(QueryParameter<P> parameter, P value) {
        this.paramBindings.getBinding(this.getParameterMetadata().resolve(parameter)).setBindValue(value);
        return this;
    }

    @Override
    public <P> ProcedureCallImplementor<R> setParameter(Parameter<P> parameter, P value) {
        this.paramBindings.getBinding(this.getParameterMetadata().resolve((Parameter)parameter)).setBindValue(value);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(String name, Object value) {
        QueryParameterBinding<Object> binding = this.paramBindings.getBinding(this.getParameterMetadata().getQueryParameter(name));
        if (value instanceof TypedParameterValue) {
            binding.setBindValue(((TypedParameterValue)value).getValue(), ((TypedParameterValue)value).getType());
        } else {
            binding.setBindValue(value);
        }
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(int position, Object value) {
        QueryParameterBinding<Object> binding = this.paramBindings.getBinding(this.getParameterMetadata().getQueryParameter(position));
        if (value instanceof TypedParameterValue) {
            binding.setBindValue(((TypedParameterValue)value).getValue(), ((TypedParameterValue)value).getType());
        } else {
            binding.setBindValue(value);
        }
        return this;
    }

    @Override
    public <P> ProcedureCallImplementor<R> setParameter(QueryParameter<P> parameter, P value, Type type) {
        QueryParameterBinding<P> binding = this.paramBindings.getBinding(parameter);
        binding.setBindValue(value, type);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(String name, Object value, Type type) {
        QueryParameterBinding<Object> binding = this.paramBindings.getBinding(this.getParameterMetadata().getQueryParameter(name));
        binding.setBindValue(value, type);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(int position, Object value, Type type) {
        QueryParameterBinding<Object> binding = this.paramBindings.getBinding(this.getParameterMetadata().getQueryParameter(position));
        binding.setBindValue(value, type);
        return this;
    }

    @Override
    public <P> ProcedureCallImplementor<R> setParameter(QueryParameter<P> parameter, P value, TemporalType temporalType) {
        QueryParameterBinding<P> binding = this.paramBindings.getBinding(parameter);
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(String name, Object value, TemporalType temporalType) {
        QueryParameterBinding<Object> binding = this.paramBindings.getBinding(this.getParameterMetadata().getQueryParameter(name));
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(int position, Object value, TemporalType temporalType) {
        QueryParameterBinding<Object> binding = this.paramBindings.getBinding(this.getParameterMetadata().getQueryParameter(position));
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(Parameter parameter, Calendar value, TemporalType temporalType) {
        QueryParameterBinding<Calendar> binding = this.paramBindings.getBinding(this.getParameterMetadata().resolve(parameter));
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(Parameter parameter, Date value, TemporalType temporalType) {
        QueryParameterBinding<Date> binding = this.paramBindings.getBinding(this.getParameterMetadata().resolve(parameter));
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(String name, Calendar value, TemporalType temporalType) {
        QueryParameterBinding<Calendar> binding = this.paramBindings.getBinding(name);
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(String name, Date value, TemporalType temporalType) {
        QueryParameterBinding<Date> binding = this.paramBindings.getBinding(name);
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(int position, Calendar value, TemporalType temporalType) {
        QueryParameterBinding<Calendar> binding = this.paramBindings.getBinding(position);
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public ProcedureCallImplementor<R> setParameter(int position, Date value, TemporalType temporalType) {
        QueryParameterBinding<Date> binding = this.paramBindings.getBinding(position);
        binding.setBindValue(value, temporalType);
        return this;
    }

    @Override
    public Stream getResultStream() {
        return this.getResultList().stream();
    }
}


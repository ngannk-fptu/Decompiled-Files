/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 */
package org.hibernate.query.internal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.persistence.Parameter;
import org.hibernate.HibernateException;
import org.hibernate.Incubating;
import org.hibernate.QueryException;
import org.hibernate.QueryParameterException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.query.spi.NamedParameterDescriptor;
import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.MathHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.internal.QueryParameterBindingImpl;
import org.hibernate.query.internal.QueryParameterListBindingImpl;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.spi.QueryParameterListBinding;
import org.hibernate.type.SerializableType;
import org.hibernate.type.Type;

@Incubating
public class QueryParameterBindingsImpl
implements QueryParameterBindings {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(QueryParameterBindingsImpl.class);
    private final SessionFactoryImplementor sessionFactory;
    private final ParameterMetadata parameterMetadata;
    private final boolean queryParametersValidationEnabled;
    private final int ordinalParamValueOffset;
    private final int jdbcStyleOrdinalCountBase;
    private Map<QueryParameter, QueryParameterBinding> parameterBindingMap;
    private Map<QueryParameter, QueryParameterListBinding> parameterListBindingMap;
    private Set<QueryParameter> parametersConvertedToListBindings;
    private Set<QueryParameter> syntheticParametersFromListBindings;
    private static final Object[] EMPTY_VALUES = new Object[0];

    public static QueryParameterBindingsImpl from(ParameterMetadata parameterMetadata, SessionFactoryImplementor sessionFactory, boolean queryParametersValidationEnabled) {
        if (parameterMetadata == null) {
            throw new QueryParameterException("Query parameter metadata cannot be null");
        }
        return new QueryParameterBindingsImpl(sessionFactory, parameterMetadata, queryParametersValidationEnabled);
    }

    private QueryParameterBindingsImpl(SessionFactoryImplementor sessionFactory, ParameterMetadata parameterMetadata, boolean queryParametersValidationEnabled) {
        this.sessionFactory = sessionFactory;
        this.parameterMetadata = parameterMetadata;
        this.queryParametersValidationEnabled = queryParametersValidationEnabled;
        this.parameterBindingMap = CollectionHelper.concurrentMap(parameterMetadata.getParameterCount());
        int n = this.jdbcStyleOrdinalCountBase = sessionFactory.getSessionFactoryOptions().jdbcStyleParamsZeroBased() ? 0 : 1;
        if (parameterMetadata.hasPositionalParameters()) {
            int smallestOrdinalParamLabel = Integer.MAX_VALUE;
            for (QueryParameter queryParameter : parameterMetadata.getPositionalParameters()) {
                if (queryParameter.getPosition() == null) {
                    throw new HibernateException("Non-ordinal parameter ended up in ordinal param list");
                }
                if (queryParameter.getPosition() >= smallestOrdinalParamLabel) continue;
                smallestOrdinalParamLabel = queryParameter.getPosition();
            }
            this.ordinalParamValueOffset = smallestOrdinalParamLabel;
        } else {
            this.ordinalParamValueOffset = 0;
        }
    }

    protected QueryParameterBinding makeBinding(QueryParameter queryParameter) {
        assert (!this.parameterBindingMap.containsKey(queryParameter));
        if (!this.parameterMetadata.containsReference(queryParameter)) {
            throw new IllegalArgumentException("Cannot create binding for parameter reference [" + queryParameter + "] - reference is not a parameter of this query");
        }
        QueryParameterBinding binding = this.makeBinding(queryParameter.getHibernateType());
        this.parameterBindingMap.put(queryParameter, binding);
        return binding;
    }

    protected QueryParameterBinding makeBinding(Type bindType) {
        return new QueryParameterBindingImpl(bindType, this.sessionFactory, this.shouldValidateBindingValue());
    }

    protected <T> QueryParameterListBinding<T> makeListBinding(QueryParameter<T> param) {
        if (this.parametersConvertedToListBindings == null) {
            this.parametersConvertedToListBindings = new HashSet<QueryParameter>();
        }
        this.parametersConvertedToListBindings.add(param);
        if (this.parameterListBindingMap == null) {
            this.parameterListBindingMap = new HashMap<QueryParameter, QueryParameterListBinding>();
        }
        return this.parameterListBindingMap.computeIfAbsent(param, p -> new QueryParameterListBindingImpl(param.getHibernateType(), this.shouldValidateBindingValue()));
    }

    @Override
    public boolean isBound(QueryParameter parameter) {
        QueryParameterBinding binding = this.getBinding(parameter);
        return binding.isBound();
    }

    @Override
    public <T> QueryParameterBinding<T> getBinding(QueryParameter<T> parameter) {
        QueryParameterBinding binding = this.parameterBindingMap.get(parameter);
        if (binding == null) {
            if (!this.parameterMetadata.containsReference(parameter)) {
                throw new IllegalArgumentException("Could not resolve QueryParameter reference [" + parameter + "] to QueryParameterBinding");
            }
            binding = this.makeBinding(parameter);
        }
        return binding;
    }

    public QueryParameterBinding getBinding(int position) {
        return this.getBinding(this.parameterMetadata.getQueryParameter(position));
    }

    public QueryParameterBinding getBinding(String name) {
        return this.getBinding(this.parameterMetadata.getQueryParameter(name));
    }

    @Override
    public void verifyParametersBound(boolean reserveFirstParameter) {
        for (QueryParameter<?> parameter : this.parameterMetadata.collectAllParameters()) {
            if (this.parameterBindingMap.containsKey(parameter) || this.parameterListBindingMap != null && this.parameterListBindingMap.containsKey(parameter) || this.parametersConvertedToListBindings != null && this.parametersConvertedToListBindings.contains(parameter)) continue;
            if (parameter.getName() != null) {
                throw new QueryException("Named parameter not bound : " + parameter.getName());
            }
            throw new QueryException("Ordinal parameter not bound : " + parameter.getPosition());
        }
    }

    @Deprecated
    public Collection<Type> collectBindTypes() {
        return this.parameterBindingMap.values().stream().map(QueryParameterBinding::getBindType).collect(Collectors.toList());
    }

    @Deprecated
    public Collection<Object> collectBindValues() {
        return this.parameterBindingMap.values().stream().map(QueryParameterBinding::getBindValue).collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public Type[] collectPositionalBindTypes() {
        return ArrayHelper.EMPTY_TYPE_ARRAY;
    }

    private TreeMap<QueryParameter, QueryParameterBinding> getSortedPositionalParamBindingMap() {
        TreeMap<QueryParameter, QueryParameterBinding> map = new TreeMap<QueryParameter, QueryParameterBinding>(Comparator.comparing(Parameter::getPosition));
        for (Map.Entry<QueryParameter, QueryParameterBinding> entry : this.parameterBindingMap.entrySet()) {
            if (entry.getKey().getPosition() == null) continue;
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @Override
    @Deprecated
    public Object[] collectPositionalBindValues() {
        return EMPTY_VALUES;
    }

    @Override
    public boolean isMultiValuedBinding(QueryParameter parameter) {
        if (this.parameterListBindingMap == null) {
            return false;
        }
        return this.parameterListBindingMap.containsKey(parameter);
    }

    @Override
    @Deprecated
    public Map<String, TypedValue> collectNamedParameterBindings() {
        HashMap<String, TypedValue> collectedBindings = new HashMap<String, TypedValue>();
        for (Map.Entry<QueryParameter, QueryParameterBinding> entry : this.parameterBindingMap.entrySet()) {
            String key = entry.getKey().getPosition() != null ? Integer.toString(entry.getKey().getPosition()) : entry.getKey().getName();
            SerializableType<Serializable> bindType = entry.getValue().getBindType();
            if (bindType == null) {
                log.debugf("Binding for parameter [%s] did not define type", key);
                bindType = SerializableType.INSTANCE;
            }
            collectedBindings.put(key, new TypedValue(bindType, entry.getValue().getBindValue()));
        }
        return collectedBindings;
    }

    @Override
    @Deprecated
    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(QueryParameter<T> queryParameter) {
        if (this.parameterListBindingMap == null) {
            this.parameterListBindingMap = new HashMap<QueryParameter, QueryParameterListBinding>();
        }
        return this.transformQueryParameterBindingToQueryParameterListBinding(queryParameter);
    }

    @Deprecated
    private QueryParameterListBinding locateQueryParameterListBinding(QueryParameter queryParameter) {
        QueryParameter resolved;
        QueryParameterListBinding binding;
        if (this.parameterListBindingMap == null) {
            this.parameterListBindingMap = new HashMap<QueryParameter, QueryParameterListBinding>();
        }
        if ((binding = this.parameterListBindingMap.get(queryParameter)) == null && (resolved = this.resolveParameter(queryParameter)) != queryParameter) {
            binding = this.parameterListBindingMap.get(resolved);
        }
        if (binding == null) {
            throw new IllegalArgumentException("Could not locate parameter list binding");
        }
        return binding;
    }

    private QueryParameter resolveParameter(QueryParameter queryParameter) {
        if (queryParameter.getName() != null) {
            return this.parameterMetadata.getQueryParameter(queryParameter.getName());
        }
        return this.parameterMetadata.getQueryParameter(queryParameter.getPosition());
    }

    @Deprecated
    private <T> QueryParameterListBinding<T> transformQueryParameterBindingToQueryParameterListBinding(QueryParameter<T> queryParameter) {
        log.debugf("Converting QueryParameterBinding to QueryParameterListBinding for given QueryParameter : %s", queryParameter);
        this.getAndRemoveBinding(queryParameter);
        return this.makeListBinding(queryParameter);
    }

    private boolean shouldValidateBindingValue() {
        return this.sessionFactory.getSessionFactoryOptions().isJpaBootstrap() && this.queryParametersValidationEnabled;
    }

    @Deprecated
    private <T> QueryParameterBinding<T> getAndRemoveBinding(QueryParameter<T> parameter) {
        QueryParameterBinding binding = this.parameterBindingMap.remove(parameter);
        if (binding == null && (parameter = parameter.getName() != null ? this.parameterMetadata.getQueryParameter(parameter.getName()) : this.parameterMetadata.getQueryParameter(parameter.getPosition())) == null) {
            throw new HibernateException("Unable to resolve QueryParameter");
        }
        binding = this.parameterBindingMap.remove(parameter);
        return binding;
    }

    @Override
    @Deprecated
    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(String name) {
        QueryParameter<T> queryParameter = this.resolveQueryParameter(name);
        return this.getQueryParameterListBinding(queryParameter);
    }

    @Deprecated
    private <T> QueryParameter<T> resolveQueryParameter(String name) {
        QueryParameter param = this.parameterMetadata.getQueryParameter(name);
        if (param == null) {
            throw new IllegalArgumentException("Unable to resolve given parameter name [" + name + "] to QueryParameter reference");
        }
        return param;
    }

    @Override
    @Deprecated
    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(int name) {
        QueryParameter<T> queryParameter = this.resolveQueryParameter(name);
        return this.getQueryParameterListBinding(queryParameter);
    }

    @Deprecated
    private <T> QueryParameter<T> resolveQueryParameter(int name) {
        QueryParameter param = this.parameterMetadata.getQueryParameter(name);
        if (param == null) {
            throw new IllegalArgumentException("Unable to resolve given parameter name [" + name + "] to QueryParameter reference");
        }
        return param;
    }

    @Override
    @Deprecated
    public String expandListValuedParameters(String queryString, SharedSessionContractImplementor session) {
        if (queryString == null) {
            return null;
        }
        if (this.syntheticParametersFromListBindings != null) {
            this.parameterBindingMap.keySet().removeAll(this.syntheticParametersFromListBindings);
            this.syntheticParametersFromListBindings.clear();
        }
        if (this.parameterListBindingMap == null || this.parameterListBindingMap.isEmpty()) {
            return queryString;
        }
        Dialect dialect = session.getFactory().getServiceRegistry().getService(JdbcServices.class).getJdbcEnvironment().getDialect();
        int inExprLimit = dialect.getInExpressionCountLimit();
        int maxOrdinalPosition = this.getMaxOrdinalPosition();
        for (Map.Entry<QueryParameter, QueryParameterListBinding> entry : this.parameterListBindingMap.entrySet()) {
            boolean isEnclosedInParens;
            String sourceToken;
            int loc;
            boolean inClauseParameterPaddingEnabled;
            int bindValueCount;
            QueryParameter sourceParam = entry.getKey();
            Collection bindValues = entry.getValue().getBindValues();
            int bindValueMaxCount = bindValueCount = bindValues.size();
            boolean bl = inClauseParameterPaddingEnabled = session.getFactory().getSessionFactoryOptions().inClauseParameterPaddingEnabled() && bindValueCount > 2;
            if (inClauseParameterPaddingEnabled) {
                int bindValuePaddingCount = MathHelper.ceilingPowerOfTwo(bindValueCount);
                if (inExprLimit > 0 && bindValuePaddingCount > inExprLimit) {
                    bindValuePaddingCount = inExprLimit;
                }
                if (bindValueCount < bindValuePaddingCount) {
                    bindValueMaxCount = bindValuePaddingCount;
                }
            }
            if (inExprLimit > 0 && bindValueCount > inExprLimit) {
                log.tooManyInExpressions(dialect.getClass().getName(), inExprLimit, sourceParam.getName(), bindValueCount);
            }
            if ((loc = StringHelper.indexOfIdentifierWord(queryString, sourceToken = sourceParam instanceof NamedParameterDescriptor ? ":" + ((NamedParameterDescriptor)NamedParameterDescriptor.class.cast(sourceParam)).getName() : "?" + ((OrdinalParameterDescriptor)OrdinalParameterDescriptor.class.cast(sourceParam)).getPosition())) < 0) continue;
            String beforePlaceholder = queryString.substring(0, loc);
            String afterPlaceholder = queryString.substring(loc + sourceToken.length());
            boolean bl2 = isEnclosedInParens = StringHelper.getLastNonWhitespaceCharacter(beforePlaceholder) == '(' && StringHelper.getFirstNonWhitespaceCharacter(afterPlaceholder) == ')';
            if (bindValues.size() == 1 && isEnclosedInParens) {
                QueryParameterBinding syntheticBinding = this.makeBinding(entry.getValue().getBindType());
                syntheticBinding.setBindValue(bindValues.iterator().next());
                this.parameterBindingMap.put(sourceParam, syntheticBinding);
                continue;
            }
            StringBuilder expansionList = new StringBuilder();
            Iterator bindValueIterator = entry.getValue().getBindValues().iterator();
            Object bindValue = null;
            for (int i = 0; i < bindValueMaxCount; ++i) {
                QueryParameter syntheticParam;
                if (i < bindValueCount) {
                    bindValue = bindValueIterator.next();
                }
                if (i > 0) {
                    expansionList.append(", ");
                }
                if (sourceParam instanceof NamedParameterDescriptor) {
                    String syntheticName = ((NamedParameterDescriptor)NamedParameterDescriptor.class.cast(sourceParam)).getName() + '_' + i;
                    expansionList.append(":").append(syntheticName);
                    syntheticParam = new NamedParameterDescriptor(syntheticName, sourceParam.getHibernateType(), sourceParam.getSourceLocations());
                } else {
                    if (i == 0) {
                        syntheticParam = sourceParam;
                    } else {
                        int syntheticPosition = ++maxOrdinalPosition;
                        syntheticParam = new OrdinalParameterDescriptor(syntheticPosition, syntheticPosition - this.jdbcStyleOrdinalCountBase, sourceParam.getHibernateType(), sourceParam.getSourceLocations());
                    }
                    expansionList.append("?").append(syntheticParam.getPosition());
                }
                this.registerSyntheticParamFromListBindings(syntheticParam);
                QueryParameterBinding syntheticBinding = this.makeBinding(entry.getValue().getBindType());
                syntheticBinding.setBindValue(bindValue);
                this.parameterBindingMap.put(syntheticParam, syntheticBinding);
            }
            String expansionListAsString = expansionList.toString();
            if (!dialect.supportsEmptyInList() && expansionListAsString.isEmpty()) {
                expansionListAsString = "null";
            }
            queryString = StringHelper.replace(beforePlaceholder, afterPlaceholder, sourceToken, expansionListAsString, true, true);
        }
        return queryString;
    }

    private void registerSyntheticParamFromListBindings(QueryParameter<?> syntheticParam) {
        if (this.syntheticParametersFromListBindings == null) {
            this.syntheticParametersFromListBindings = new HashSet<QueryParameter>();
        }
        this.syntheticParametersFromListBindings.add(syntheticParam);
    }

    private int getMaxOrdinalPosition() {
        int maxOrdinalPosition = 0;
        for (QueryParameter queryParameter : this.parameterBindingMap.keySet()) {
            if (!(queryParameter instanceof OrdinalParameterDescriptor)) continue;
            maxOrdinalPosition = Math.max(maxOrdinalPosition, queryParameter.getPosition());
        }
        for (QueryParameter queryParameter : this.parameterListBindingMap.keySet()) {
            if (!(queryParameter instanceof OrdinalParameterDescriptor)) continue;
            maxOrdinalPosition = Math.max(maxOrdinalPosition, queryParameter.getPosition());
        }
        return maxOrdinalPosition;
    }
}


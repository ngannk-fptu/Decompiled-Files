/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.hql.internal.NameGenerator;
import org.hibernate.hql.internal.classic.NamedParameterInformationImpl;
import org.hibernate.hql.internal.classic.ParserHelper;
import org.hibernate.hql.internal.classic.PositionalParameterInformationImpl;
import org.hibernate.hql.internal.classic.PreprocessingParser;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.NamedParameterInformation;
import org.hibernate.hql.spi.ParameterTranslations;
import org.hibernate.hql.spi.PositionalParameterInformation;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.IteratorImpl;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.BasicLoader;
import org.hibernate.loader.Loader;
import org.hibernate.loader.internal.AliasConstantsHelper;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.param.CollectionFilterKeyParameterSpecification;
import org.hibernate.param.ParameterBinder;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;
import org.hibernate.sql.QuerySelect;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.AssociationType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class QueryTranslatorImpl
extends BasicLoader
implements FilterTranslator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(QueryTranslatorImpl.class);
    private static final String[] NO_RETURN_ALIASES = new String[0];
    private final String queryIdentifier;
    private final String queryString;
    private final Map typeMap = new LinkedHashMap();
    private final Map collections = new LinkedHashMap();
    private List returnedTypes = new ArrayList();
    private final List fromTypes = new ArrayList();
    private final List scalarTypes = new ArrayList();
    private final Map aliasNames = new HashMap();
    private final Map oneToOneOwnerNames = new HashMap();
    private final Map uniqueKeyOwnerReferences = new HashMap();
    private final Map decoratedPropertyMappings = new HashMap();
    private final Map<String, NamedParameterInformationImpl> namedParameters = new HashMap<String, NamedParameterInformationImpl>();
    private final Map<Integer, PositionalParameterInformationImpl> ordinalParameters = new HashMap<Integer, PositionalParameterInformationImpl>();
    private final List<ParameterBinder> paramValueBinders = new ArrayList<ParameterBinder>();
    private final List scalarSelectTokens = new ArrayList();
    private final List whereTokens = new ArrayList();
    private final List havingTokens = new ArrayList();
    private final Map joins = new LinkedHashMap();
    private final List orderByTokens = new ArrayList();
    private final List groupByTokens = new ArrayList();
    private final Set<Serializable> querySpaces = new HashSet<Serializable>();
    private final Set entitiesToFetch = new HashSet();
    private final Map pathAliases = new HashMap();
    private final Map pathJoins = new HashMap();
    private Queryable[] persisters;
    private int[] owners;
    private EntityType[] ownerAssociationTypes;
    private String[] names;
    private boolean[] includeInSelect;
    private int selectLength;
    private Type[] returnTypes;
    private Type[] actualReturnTypes;
    private String[][] scalarColumnNames;
    private Map tokenReplacements;
    private int nameCount;
    private int parameterCount;
    private boolean distinct;
    private boolean compiled;
    private String sqlString;
    private Class holderClass;
    private Constructor holderConstructor;
    private boolean hasScalars;
    private boolean shallowQuery;
    private QueryTranslatorImpl superQuery;
    private QueryableCollection collectionPersister;
    private int collectionOwnerColumn = -1;
    private String collectionOwnerName;
    private String fetchName;
    private String[] suffixes;
    private Map enabledFilters;
    private OrdinalParameterStyle ordinalParameterStyle;
    private int legacyPositionalParameterCount = 0;

    public QueryTranslatorImpl(String queryIdentifier, String queryString, Map enabledFilters, SessionFactoryImplementor factory) {
        super(factory);
        this.queryIdentifier = queryIdentifier;
        this.queryString = queryString;
        this.enabledFilters = enabledFilters;
    }

    public QueryTranslatorImpl(String queryString, Map enabledFilters, SessionFactoryImplementor factory) {
        this(queryString, queryString, enabledFilters, factory);
    }

    void compile(QueryTranslatorImpl superquery) throws QueryException, MappingException {
        this.tokenReplacements = superquery.tokenReplacements;
        this.superQuery = superquery;
        this.shallowQuery = true;
        this.enabledFilters = superquery.getEnabledFilters();
        this.compile();
    }

    @Override
    public synchronized void compile(Map replacements, boolean scalar) throws QueryException, MappingException {
        if (!this.compiled) {
            this.tokenReplacements = replacements;
            this.shallowQuery = scalar;
            this.compile();
        }
    }

    @Override
    public synchronized void compile(String collectionRole, Map replacements, boolean scalar) throws QueryException, MappingException {
        if (!this.isCompiled()) {
            this.addFromAssociation("this", collectionRole);
            this.paramValueBinders.add(new CollectionFilterKeyParameterSpecification(collectionRole, this.getFactory().getMetamodel().collectionPersister(collectionRole).getKeyType()));
            this.compile(replacements, scalar);
        }
    }

    private void compile() throws QueryException, MappingException {
        LOG.trace("Compiling query");
        try {
            ParserHelper.parse(new PreprocessingParser(this.tokenReplacements), this.queryString, " \n\r\f\t,()=<>&|+-=/*'^![]#~\\", this);
            this.renderSQL();
        }
        catch (QueryException qe) {
            if (qe.getQueryString() == null) {
                throw qe.wrapWithQueryString(this.queryString);
            }
            throw qe;
        }
        catch (MappingException me) {
            throw me;
        }
        catch (Exception e) {
            LOG.debug("Unexpected query compilation problem", e);
            throw new QueryException("Incorrect query syntax", this.queryString, e);
        }
        this.postInstantiate();
        this.compiled = true;
    }

    @Override
    public String getSQLString() {
        return this.sqlString;
    }

    @Override
    public List<String> collectSqlStrings() {
        return Collections.singletonList(this.sqlString);
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    protected Loadable[] getEntityPersisters() {
        return this.persisters;
    }

    @Override
    public Type[] getReturnTypes() {
        return this.actualReturnTypes;
    }

    @Override
    public String[] getReturnAliases() {
        return NO_RETURN_ALIASES;
    }

    @Override
    public String[][] getColumnNames() {
        return this.scalarColumnNames;
    }

    private static void logQuery(String hql, String sql) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("HQL: %s", hql);
            LOG.debugf("SQL: %s", sql);
        }
    }

    void setAliasName(String alias, String name) {
        this.aliasNames.put(alias, name);
    }

    public String getAliasName(String alias) {
        String name = (String)this.aliasNames.get(alias);
        if (name == null) {
            name = this.superQuery != null ? this.superQuery.getAliasName(alias) : alias;
        }
        return name;
    }

    String unalias(String path) {
        String alias = StringHelper.root(path);
        String name = this.getAliasName(alias);
        if (name != null) {
            return name + path.substring(alias.length());
        }
        return path;
    }

    void addEntityToFetch(String name, String oneToOneOwnerName, AssociationType ownerAssociationType) {
        this.addEntityToFetch(name);
        if (oneToOneOwnerName != null) {
            this.oneToOneOwnerNames.put(name, oneToOneOwnerName);
        }
        if (ownerAssociationType != null) {
            this.uniqueKeyOwnerReferences.put(name, ownerAssociationType);
        }
    }

    private void addEntityToFetch(String name) {
        this.entitiesToFetch.add(name);
    }

    private int nextCount() {
        int n;
        if (this.superQuery == null) {
            int n2 = this.nameCount;
            n = n2;
            this.nameCount = n2 + 1;
        } else {
            int n3 = this.superQuery.nameCount;
            n = n3;
            this.superQuery.nameCount = n3 + 1;
        }
        return n;
    }

    String createNameFor(String type) {
        return StringHelper.generateAlias(type, this.nextCount());
    }

    String createNameForCollection(String role) {
        return StringHelper.generateAlias(role, this.nextCount());
    }

    private String getType(String name) {
        String type = (String)this.typeMap.get(name);
        if (type == null && this.superQuery != null) {
            type = this.superQuery.getType(name);
        }
        return type;
    }

    private String getRole(String name) {
        String role = (String)this.collections.get(name);
        if (role == null && this.superQuery != null) {
            role = this.superQuery.getRole(name);
        }
        return role;
    }

    boolean isName(String name) {
        return this.aliasNames.containsKey(name) || this.typeMap.containsKey(name) || this.collections.containsKey(name) || this.superQuery != null && this.superQuery.isName(name);
    }

    PropertyMapping getPropertyMapping(String name) throws QueryException {
        PropertyMapping decorator = this.getDecoratedPropertyMapping(name);
        if (decorator != null) {
            return decorator;
        }
        String type = this.getType(name);
        if (type == null) {
            String role = this.getRole(name);
            if (role == null) {
                throw new QueryException("alias not found: " + name);
            }
            return this.getCollectionPersister(role);
        }
        Queryable persister = this.getEntityPersister(type);
        if (persister == null) {
            throw new QueryException("persistent class not found: " + type);
        }
        return persister;
    }

    private PropertyMapping getDecoratedPropertyMapping(String name) {
        return (PropertyMapping)this.decoratedPropertyMappings.get(name);
    }

    void decoratePropertyMapping(String name, PropertyMapping mapping) {
        this.decoratedPropertyMappings.put(name, mapping);
    }

    private Queryable getEntityPersisterForName(String name) throws QueryException {
        String type = this.getType(name);
        Queryable persister = this.getEntityPersister(type);
        if (persister == null) {
            throw new QueryException("persistent class not found: " + type);
        }
        return persister;
    }

    Queryable getEntityPersisterUsingImports(String className) {
        String importedClassName = this.getFactory().getMetamodel().getImportedClassName(className);
        if (importedClassName == null) {
            return null;
        }
        try {
            return (Queryable)this.getFactory().getMetamodel().entityPersister(importedClassName);
        }
        catch (MappingException me) {
            return null;
        }
    }

    Queryable getEntityPersister(String entityName) throws QueryException {
        try {
            return (Queryable)this.getFactory().getMetamodel().entityPersister(entityName);
        }
        catch (Exception e) {
            throw new QueryException("persistent class not found: " + entityName);
        }
    }

    QueryableCollection getCollectionPersister(String role) throws QueryException {
        try {
            return (QueryableCollection)this.getFactory().getMetamodel().collectionPersister(role);
        }
        catch (ClassCastException cce) {
            throw new QueryException("collection role is not queryable: " + role);
        }
        catch (Exception e) {
            throw new QueryException("collection role not found: " + role);
        }
    }

    void addType(String name, String type) {
        this.typeMap.put(name, type);
    }

    void addCollection(String name, String role) {
        this.collections.put(name, role);
    }

    void addFrom(String name, String type, JoinSequence joinSequence) throws QueryException {
        this.addType(name, type);
        this.addFrom(name, joinSequence);
    }

    void addFromCollection(String name, String collectionRole, JoinSequence joinSequence) throws QueryException {
        this.addCollection(name, collectionRole);
        this.addJoin(name, joinSequence);
    }

    void addFrom(String name, JoinSequence joinSequence) throws QueryException {
        this.fromTypes.add(name);
        this.addJoin(name, joinSequence);
    }

    void addFromClass(String name, Queryable classPersister) throws QueryException {
        JoinSequence joinSequence = new JoinSequence(this.getFactory()).setRoot(classPersister, name);
        this.addFrom(name, classPersister.getEntityName(), joinSequence);
    }

    void addSelectClass(String name) {
        this.returnedTypes.add(name);
    }

    void addSelectScalar(Type type) {
        this.scalarTypes.add(type);
    }

    void appendWhereToken(String token) {
        this.whereTokens.add(token);
    }

    void appendHavingToken(String token) {
        this.havingTokens.add(token);
    }

    void appendOrderByToken(String token) {
        this.orderByTokens.add(token);
    }

    void appendGroupByToken(String token) {
        this.groupByTokens.add(token);
    }

    void appendScalarSelectToken(String token) {
        this.scalarSelectTokens.add(token);
    }

    void appendScalarSelectTokens(String[] tokens) {
        this.scalarSelectTokens.add(tokens);
    }

    void addFromJoinOnly(String name, JoinSequence joinSequence) throws QueryException {
        this.addJoin(name, joinSequence.getFromPart());
    }

    void addJoin(String name, JoinSequence joinSequence) throws QueryException {
        if (!this.joins.containsKey(name)) {
            this.joins.put(name, joinSequence);
        }
    }

    void addNamedParameter(String name) {
        if (this.superQuery != null) {
            this.superQuery.addNamedParameter(name);
        }
        int loc = this.parameterCount++;
        NamedParameterInformationImpl info = this.namedParameters.computeIfAbsent(name, k -> new NamedParameterInformationImpl(name));
        this.paramValueBinders.add(info);
        info.addSourceLocation(loc);
    }

    void addLegacyPositionalParameter() {
        if (this.superQuery != null) {
            this.superQuery.addLegacyPositionalParameter();
        }
        if (this.ordinalParameterStyle == null) {
            this.ordinalParameterStyle = OrdinalParameterStyle.LEGACY;
        } else if (this.ordinalParameterStyle != OrdinalParameterStyle.LEGACY) {
            throw new QueryException("Cannot mix legacy and labeled positional parameters");
        }
        int label = this.legacyPositionalParameterCount++;
        PositionalParameterInformationImpl paramInfo = new PositionalParameterInformationImpl(label);
        this.ordinalParameters.put(label, paramInfo);
        this.paramValueBinders.add(paramInfo);
        int loc = this.parameterCount++;
        paramInfo.addSourceLocation(loc);
    }

    void addOrdinalParameter(int label) {
        if (this.superQuery != null) {
            this.superQuery.addOrdinalParameter(label);
        }
        if (this.ordinalParameterStyle == null) {
            this.ordinalParameterStyle = OrdinalParameterStyle.LABELED;
        } else if (this.ordinalParameterStyle != OrdinalParameterStyle.LABELED) {
            throw new QueryException("Cannot mix legacy and labeled positional parameters");
        }
        int loc = this.parameterCount++;
        PositionalParameterInformationImpl info = this.ordinalParameters.computeIfAbsent(label, k -> new PositionalParameterInformationImpl(label));
        this.paramValueBinders.add(info);
        info.addSourceLocation(loc);
    }

    @Override
    protected int bindParameterValues(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        int span = 0;
        for (ParameterBinder binder : this.paramValueBinders) {
            span += binder.bind(statement, queryParameters, session, startIndex + span);
        }
        return span;
    }

    @Override
    public int[] getNamedParameterLocs(String name) throws QueryException {
        NamedParameterInformationImpl o = this.namedParameters.get(name);
        if (o == null) {
            throw new QueryException("Named parameter does not appear in Query: " + name, this.queryString);
        }
        if (o instanceof Integer) {
            return new int[]{(Integer)((Object)o)};
        }
        return ArrayHelper.toIntArray((ArrayList)((Object)o));
    }

    private void renderSQL() throws QueryException, MappingException {
        PropertyMapping p;
        int rtsize;
        if (this.returnedTypes.size() == 0 && this.scalarTypes.size() == 0) {
            this.returnedTypes = this.fromTypes;
            rtsize = this.returnedTypes.size();
        } else {
            rtsize = this.returnedTypes.size();
            this.returnedTypes.addAll(this.entitiesToFetch);
        }
        int size = this.returnedTypes.size();
        this.persisters = new Queryable[size];
        this.names = new String[size];
        this.owners = new int[size];
        this.ownerAssociationTypes = new EntityType[size];
        this.suffixes = new String[size];
        this.includeInSelect = new boolean[size];
        for (int i = 0; i < size; ++i) {
            String oneToOneOwner;
            String name = (String)this.returnedTypes.get(i);
            this.persisters[i] = this.getEntityPersisterForName(name);
            this.suffixes[i] = size == 1 ? "" : AliasConstantsHelper.get(i);
            this.names[i] = name;
            boolean bl = this.includeInSelect[i] = !this.entitiesToFetch.contains(name);
            if (this.includeInSelect[i]) {
                ++this.selectLength;
            }
            if (name.equals(this.collectionOwnerName)) {
                this.collectionOwnerColumn = i;
            }
            this.owners[i] = (oneToOneOwner = (String)this.oneToOneOwnerNames.get(name)) == null ? -1 : this.returnedTypes.indexOf(oneToOneOwner);
            this.ownerAssociationTypes[i] = (EntityType)this.uniqueKeyOwnerReferences.get(name);
        }
        if (ArrayHelper.isAllNegative(this.owners)) {
            this.owners = null;
        }
        String scalarSelect = this.renderScalarSelect();
        int scalarSize = this.scalarTypes.size();
        this.hasScalars = this.scalarTypes.size() != rtsize;
        this.returnTypes = new Type[scalarSize];
        for (int i = 0; i < scalarSize; ++i) {
            this.returnTypes[i] = (Type)this.scalarTypes.get(i);
        }
        QuerySelect sql = new QuerySelect(this.getFactory().getDialect());
        sql.setDistinct(this.distinct);
        if (!this.shallowQuery) {
            this.renderIdentifierSelect(sql);
            this.renderPropertiesSelect(sql);
        }
        if (this.collectionPersister != null) {
            sql.addSelectFragmentString(this.collectionPersister.selectFragment(this.fetchName, "__"));
        }
        if (this.hasScalars || this.shallowQuery) {
            sql.addSelectFragmentString(scalarSelect);
        }
        this.mergeJoins(sql.getJoinFragment());
        sql.setWhereTokens(this.whereTokens.iterator());
        sql.setGroupByTokens(this.groupByTokens.iterator());
        sql.setHavingTokens(this.havingTokens.iterator());
        sql.setOrderByTokens(this.orderByTokens.iterator());
        if (this.collectionPersister != null && this.collectionPersister.hasOrdering()) {
            sql.addOrderBy(this.collectionPersister.getSQLOrderByString(this.fetchName));
        }
        this.scalarColumnNames = NameGenerator.generateColumnNames(this.returnTypes, this.getFactory());
        Iterator<Object> iter = this.collections.values().iterator();
        while (iter.hasNext()) {
            p = this.getCollectionPersister((String)iter.next());
            this.addQuerySpaces(p.getCollectionSpaces());
        }
        iter = this.typeMap.keySet().iterator();
        while (iter.hasNext()) {
            p = this.getEntityPersisterForName((String)iter.next());
            this.addQuerySpaces(p.getQuerySpaces());
        }
        this.sqlString = sql.toQueryString();
        if (this.holderClass != null) {
            this.holderConstructor = ReflectHelper.getConstructor(this.holderClass, this.returnTypes);
        }
        if (this.hasScalars) {
            this.actualReturnTypes = this.returnTypes;
        } else {
            this.actualReturnTypes = new Type[this.selectLength];
            int j = 0;
            for (int i = 0; i < this.persisters.length; ++i) {
                if (!this.includeInSelect[i]) continue;
                this.actualReturnTypes[j++] = this.getFactory().getTypeResolver().getTypeFactory().manyToOne(this.persisters[i].getEntityName(), this.shallowQuery);
            }
        }
    }

    private void renderIdentifierSelect(QuerySelect sql) {
        int size = this.returnedTypes.size();
        for (int k = 0; k < size; ++k) {
            String name = (String)this.returnedTypes.get(k);
            String suffix = size == 1 ? "" : AliasConstantsHelper.get(k);
            sql.addSelectFragmentString(this.persisters[k].identifierSelectFragment(name, suffix));
        }
    }

    private void renderPropertiesSelect(QuerySelect sql) {
        int size = this.returnedTypes.size();
        for (int k = 0; k < size; ++k) {
            String suffix = size == 1 ? "" : AliasConstantsHelper.get(k);
            String name = (String)this.returnedTypes.get(k);
            sql.addSelectFragmentString(this.persisters[k].propertySelectFragment(name, suffix, false));
        }
    }

    private String renderScalarSelect() {
        boolean isSubselect = this.superQuery != null;
        StringBuilder buf = new StringBuilder(20);
        if (this.scalarTypes.size() == 0) {
            int size = this.returnedTypes.size();
            for (int k = 0; k < size; ++k) {
                this.scalarTypes.add(this.getFactory().getTypeResolver().getTypeFactory().manyToOne(this.persisters[k].getEntityName(), this.shallowQuery));
                String[] idColumnNames = this.persisters[k].getIdentifierColumnNames();
                for (int i = 0; i < idColumnNames.length; ++i) {
                    buf.append(this.returnedTypes.get(k)).append('.').append(idColumnNames[i]);
                    if (!isSubselect) {
                        buf.append(" as ").append(NameGenerator.scalarName(k, i));
                    }
                    if (i == idColumnNames.length - 1 && k == size - 1) continue;
                    buf.append(", ");
                }
            }
        } else {
            Iterator iter = this.scalarSelectTokens.iterator();
            int c = 0;
            boolean nolast = false;
            int parenCount = 0;
            while (iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof String) {
                    String token = (String)next;
                    if ("(".equals(token)) {
                        ++parenCount;
                    } else if (")".equals(token)) {
                        --parenCount;
                    }
                    String lc = token.toLowerCase(Locale.ROOT);
                    if (lc.equals(", ")) {
                        if (nolast) {
                            nolast = false;
                        } else if (!isSubselect && parenCount == 0) {
                            int x = c++;
                            buf.append(" as ").append(NameGenerator.scalarName(x, 0));
                        }
                    }
                    buf.append(token);
                    if (!lc.equals("distinct") && !lc.equals("all")) continue;
                    buf.append(' ');
                    continue;
                }
                nolast = true;
                String[] tokens = (String[])next;
                for (int i = 0; i < tokens.length; ++i) {
                    buf.append(tokens[i]);
                    if (!isSubselect) {
                        buf.append(" as ").append(NameGenerator.scalarName(c, i));
                    }
                    if (i == tokens.length - 1) continue;
                    buf.append(", ");
                }
                ++c;
            }
            if (!isSubselect && !nolast) {
                int x = c++;
                buf.append(" as ").append(NameGenerator.scalarName(x, 0));
            }
        }
        return buf.toString();
    }

    private void mergeJoins(JoinFragment ojf) throws MappingException, QueryException {
        for (Map.Entry me : this.joins.entrySet()) {
            String name = (String)me.getKey();
            JoinSequence join = (JoinSequence)me.getValue();
            join.setSelector(new JoinSequence.Selector(){

                @Override
                public boolean includeSubclasses(String alias) {
                    return QueryTranslatorImpl.this.returnedTypes.contains(alias) && !QueryTranslatorImpl.this.isShallowQuery();
                }
            });
            if (this.typeMap.containsKey(name)) {
                ojf.addFragment(join.toJoinFragment(this.enabledFilters, true));
                continue;
            }
            if (!this.collections.containsKey(name)) continue;
            ojf.addFragment(join.toJoinFragment(this.enabledFilters, true));
        }
    }

    @Override
    public final Set<Serializable> getQuerySpaces() {
        return this.querySpaces;
    }

    boolean isShallowQuery() {
        return this.shallowQuery;
    }

    void addQuerySpaces(Serializable[] spaces) {
        Collections.addAll(this.querySpaces, spaces);
        if (this.superQuery != null) {
            this.superQuery.addQuerySpaces(spaces);
        }
    }

    void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    boolean isSubquery() {
        return this.superQuery != null;
    }

    @Override
    public CollectionPersister[] getCollectionPersisters() {
        CollectionPersister[] collectionPersisterArray;
        if (this.collectionPersister == null) {
            collectionPersisterArray = null;
        } else {
            CollectionPersister[] collectionPersisterArray2 = new CollectionPersister[1];
            collectionPersisterArray = collectionPersisterArray2;
            collectionPersisterArray2[0] = this.collectionPersister;
        }
        return collectionPersisterArray;
    }

    @Override
    protected String[] getCollectionSuffixes() {
        String[] stringArray;
        if (this.collectionPersister == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = "__";
        }
        return stringArray;
    }

    void setCollectionToFetch(String role, String name, String ownerName, String entityName) throws QueryException {
        this.fetchName = name;
        this.collectionPersister = this.getCollectionPersister(role);
        this.collectionOwnerName = ownerName;
        if (this.collectionPersister.getElementType().isEntityType()) {
            this.addEntityToFetch(entityName);
        }
    }

    @Override
    protected String[] getSuffixes() {
        return this.suffixes;
    }

    @Override
    protected String[] getAliases() {
        return this.names;
    }

    private void addFromAssociation(String elementName, String collectionRole) throws QueryException {
        QueryableCollection persister = this.getCollectionPersister(collectionRole);
        Type collectionElementType = persister.getElementType();
        if (!collectionElementType.isEntityType()) {
            throw new QueryException("collection of values in filter: " + elementName);
        }
        String[] keyColumnNames = persister.getKeyColumnNames();
        JoinSequence join = new JoinSequence(this.getFactory());
        String collectionName = persister.isOneToMany() ? elementName : this.createNameForCollection(collectionRole);
        join.setRoot(persister, collectionName);
        if (!persister.isOneToMany()) {
            this.addCollection(collectionName, collectionRole);
            try {
                join.addJoin((AssociationType)persister.getElementType(), elementName, JoinType.INNER_JOIN, persister.getElementColumnNames(collectionName));
            }
            catch (MappingException me) {
                throw new QueryException((Exception)((Object)me));
            }
        }
        join.addCondition(collectionName, keyColumnNames, " = ?");
        EntityType elemType = (EntityType)collectionElementType;
        this.addFrom(elementName, elemType.getAssociatedEntityName(), join);
    }

    String getPathAlias(String path) {
        return (String)this.pathAliases.get(path);
    }

    JoinSequence getPathJoin(String path) {
        return (JoinSequence)this.pathJoins.get(path);
    }

    void addPathAliasAndJoin(String path, String alias, JoinSequence joinSequence) {
        this.pathAliases.put(path, alias);
        this.pathJoins.put(path, joinSequence);
    }

    @Override
    public List list(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
        return this.list(session, queryParameters, this.getQuerySpaces(), this.actualReturnTypes);
    }

    @Override
    public Iterator iterate(QueryParameters queryParameters, EventSource session) throws HibernateException {
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        long startTime = 0L;
        if (stats) {
            startTime = System.nanoTime();
        }
        try {
            ArrayList<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
            Loader.SqlStatementWrapper wrapper = this.executeQueryStatement(queryParameters, false, afterLoadActions, session);
            ResultSet rs = wrapper.getResultSet();
            PreparedStatement st = (PreparedStatement)wrapper.getStatement();
            HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(this.holderConstructor, queryParameters.getResultTransformer());
            IteratorImpl result = new IteratorImpl(rs, st, session, queryParameters.isReadOnly(session), this.returnTypes, this.getColumnNames(), hi);
            if (stats) {
                long endTime = System.nanoTime();
                long milliseconds = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
                statistics.queryExecuted("HQL: " + this.queryString, 0, milliseconds);
            }
            return result;
        }
        catch (SQLException sqle) {
            throw this.getFactory().getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not execute query using iterate", this.getSQLString());
        }
    }

    @Override
    public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        throw new UnsupportedOperationException("Not supported!  Use the AST translator...");
    }

    @Override
    protected boolean[] includeInResultRow() {
        boolean[] isResultReturned = this.includeInSelect;
        if (this.hasScalars) {
            isResultReturned = new boolean[this.returnedTypes.size()];
            Arrays.fill(isResultReturned, true);
        }
        return isResultReturned;
    }

    @Override
    protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
        return HolderInstantiator.resolveClassicResultTransformer(this.holderConstructor, resultTransformer);
    }

    @Override
    protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] resultRow = this.getResultRow(row, rs, session);
        return this.holderClass == null && resultRow.length == 1 ? resultRow[0] : resultRow;
    }

    @Override
    protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] resultRow;
        if (this.hasScalars) {
            String[][] scalarColumns = this.getColumnNames();
            int queryCols = this.returnTypes.length;
            resultRow = new Object[queryCols];
            for (int i = 0; i < queryCols; ++i) {
                resultRow[i] = this.returnTypes[i].nullSafeGet(rs, scalarColumns[i], session, null);
            }
        } else {
            resultRow = this.toResultRow(row);
        }
        return resultRow;
    }

    @Override
    protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
        if (this.holderClass != null) {
            for (int i = 0; i < results.size(); ++i) {
                Object[] row = (Object[])results.get(i);
                try {
                    results.set(i, this.holderConstructor.newInstance(row));
                    continue;
                }
                catch (Exception e) {
                    throw new QueryException("could not instantiate: " + this.holderClass, e);
                }
            }
        }
        return results;
    }

    private Object[] toResultRow(Object[] row) {
        if (this.selectLength == row.length) {
            return row;
        }
        Object[] result = new Object[this.selectLength];
        int j = 0;
        for (int i = 0; i < row.length; ++i) {
            if (!this.includeInSelect[i]) continue;
            result[j++] = row[i];
        }
        return result;
    }

    void setHolderClass(Class clazz) {
        this.holderClass = clazz;
    }

    @Override
    protected LockMode[] getLockModes(LockOptions lockOptions) {
        HashMap<String, LockMode> nameLockOptions = new HashMap<String, LockMode>();
        if (lockOptions == null) {
            lockOptions = LockOptions.NONE;
        }
        if (lockOptions.getAliasLockCount() > 0) {
            Iterator<Map.Entry<String, LockMode>> iter = lockOptions.getAliasLockIterator();
            while (iter.hasNext()) {
                Map.Entry<String, LockMode> me = iter.next();
                nameLockOptions.put(this.getAliasName(me.getKey()), me.getValue());
            }
        }
        LockMode[] lockModesArray = new LockMode[this.names.length];
        for (int i = 0; i < this.names.length; ++i) {
            LockMode lm = (LockMode)((Object)nameLockOptions.get(this.names[i]));
            if (lm == null) {
                lm = lockOptions.getLockMode();
            }
            lockModesArray[i] = lm;
        }
        return lockModesArray;
    }

    @Override
    protected String applyLocks(String sql, QueryParameters parameters, Dialect dialect, List<AfterLoadAction> afterLoadActions) throws QueryException {
        LockOptions lockOptions = parameters.getLockOptions();
        if (lockOptions == null || lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0) {
            return sql;
        }
        LockOptions locks = new LockOptions();
        locks.setLockMode(lockOptions.getLockMode());
        locks.setTimeOut(lockOptions.getTimeOut());
        locks.setScope(lockOptions.getScope());
        Iterator<Map.Entry<String, LockMode>> iter = lockOptions.getAliasLockIterator();
        while (iter.hasNext()) {
            Map.Entry<String, LockMode> me = iter.next();
            locks.setAliasSpecificLockMode(this.getAliasName(me.getKey()), me.getValue());
        }
        HashMap<String, String[]> keyColumnNames = null;
        if (dialect.forUpdateOfColumns()) {
            keyColumnNames = new HashMap<String, String[]>();
            for (int i = 0; i < this.names.length; ++i) {
                keyColumnNames.put(this.names[i], this.persisters[i].getIdentifierColumnNames());
            }
        }
        String result = dialect.applyLocksToSql(sql, locks, keyColumnNames);
        QueryTranslatorImpl.logQuery(this.queryString, result);
        return result;
    }

    @Override
    protected boolean upgradeLocks() {
        return true;
    }

    @Override
    protected int[] getCollectionOwners() {
        return new int[]{this.collectionOwnerColumn};
    }

    protected boolean isCompiled() {
        return this.compiled;
    }

    @Override
    public String toString() {
        return this.queryString;
    }

    @Override
    protected int[] getOwners() {
        return this.owners;
    }

    @Override
    protected EntityType[] getOwnerAssociationTypes() {
        return this.ownerAssociationTypes;
    }

    public Class getHolderClass() {
        return this.holderClass;
    }

    @Override
    public Map getEnabledFilters() {
        return this.enabledFilters;
    }

    @Override
    public ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(this.holderConstructor, queryParameters.getResultTransformer());
        return this.scroll(queryParameters, this.returnTypes, hi, session);
    }

    @Override
    public String getQueryIdentifier() {
        return this.queryIdentifier;
    }

    @Override
    protected boolean isSubselectLoadingEnabled() {
        return this.hasSubselectLoadableCollections();
    }

    @Override
    public List<String> getPrimaryFromClauseTables() {
        throw new UnsupportedOperationException("The classic mode does not support UPDATE statements via createQuery!");
    }

    @Override
    public void validateScrollability() throws HibernateException {
        if (this.getCollectionPersisters() != null) {
            throw new HibernateException("Cannot scroll queries which initialize collections");
        }
    }

    @Override
    public boolean containsCollectionFetches() {
        return false;
    }

    @Override
    public boolean isManipulationStatement() {
        return false;
    }

    @Override
    public Class getDynamicInstantiationResultType() {
        return this.holderClass;
    }

    @Override
    public ParameterTranslations getParameterTranslations() {
        return new ParameterTranslations(){

            public Map getNamedParameterInformationMap() {
                return QueryTranslatorImpl.this.namedParameters;
            }

            public Map getPositionalParameterInformationMap() {
                return QueryTranslatorImpl.this.ordinalParameters;
            }

            @Override
            public PositionalParameterInformation getPositionalParameterInformation(int position) {
                return (PositionalParameterInformation)QueryTranslatorImpl.this.ordinalParameters.get(position);
            }

            @Override
            public NamedParameterInformation getNamedParameterInformation(String name) {
                return (NamedParameterInformation)QueryTranslatorImpl.this.namedParameters.get(name);
            }
        };
    }

    private static enum OrdinalParameterStyle {
        LABELED,
        LEGACY;

    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.criteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.EnhancedProjection;
import org.hibernate.criterion.Projection;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.criteria.ComponentCollectionCriteriaInfoProvider;
import org.hibernate.loader.criteria.CriteriaInfoProvider;
import org.hibernate.loader.criteria.EntityCriteriaInfoProvider;
import org.hibernate.loader.criteria.ScalarCollectionCriteriaInfoProvider;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.JoinType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.Type;

public class CriteriaQueryTranslator
implements CriteriaQuery {
    public static final String ROOT_SQL_ALIAS = "this_";
    private CriteriaQuery outerQueryTranslator;
    private final CriteriaImpl rootCriteria;
    private final String rootEntityName;
    private final String rootSQLAlias;
    private final Map<Criteria, CriteriaInfoProvider> criteriaInfoMap = new LinkedHashMap<Criteria, CriteriaInfoProvider>();
    private final Map<String, CriteriaInfoProvider> nameCriteriaInfoMap = new LinkedHashMap<String, CriteriaInfoProvider>();
    private final Map<Criteria, String> criteriaSQLAliasMap = new HashMap<Criteria, String>();
    private final Map<String, Criteria> aliasCriteriaMap = new HashMap<String, Criteria>();
    private final Map<String, Criteria> associationPathCriteriaMap = new LinkedHashMap<String, Criteria>();
    private final Map<String, JoinType> associationPathJoinTypesMap = new LinkedHashMap<String, JoinType>();
    private final Map<String, Criterion> withClauseMap = new HashMap<String, Criterion>();
    private Set<String> associations;
    private final SessionFactoryImplementor sessionFactory;
    private final SessionFactoryHelper helper;

    public CriteriaQueryTranslator(SessionFactoryImplementor factory, CriteriaImpl criteria, String rootEntityName, String rootSQLAlias, CriteriaQuery outerQuery) throws HibernateException {
        this(factory, criteria, rootEntityName, rootSQLAlias);
        this.outerQueryTranslator = outerQuery;
    }

    public CriteriaQueryTranslator(SessionFactoryImplementor factory, CriteriaImpl criteria, String rootEntityName, String rootSQLAlias) throws HibernateException {
        this.rootCriteria = criteria;
        this.rootEntityName = rootEntityName;
        this.sessionFactory = factory;
        this.rootSQLAlias = rootSQLAlias;
        this.helper = new SessionFactoryHelper(factory);
        this.createAliasCriteriaMap();
        this.createAssociationPathCriteriaMap();
        this.createCriteriaEntityNameMap();
        this.createCriteriaSQLAliasMap();
    }

    public void setAssociations(Set<String> associations) {
        this.associations = associations;
    }

    @Override
    public String generateSQLAlias() {
        int aliasCount = 0;
        return StringHelper.generateAlias("this", aliasCount) + '_';
    }

    public String getRootSQLALias() {
        return this.rootSQLAlias;
    }

    private Criteria getAliasedCriteria(String alias) {
        return this.aliasCriteriaMap.get(alias);
    }

    public boolean isJoin(String path) {
        return this.associationPathCriteriaMap.containsKey(path);
    }

    public JoinType getJoinType(String path) {
        JoinType result = this.associationPathJoinTypesMap.get(path);
        return result == null ? JoinType.INNER_JOIN : result;
    }

    public Criteria getCriteria(String path) {
        return this.associationPathCriteriaMap.get(path);
    }

    public Set<Serializable> getQuerySpaces() {
        HashSet<Serializable> result = new HashSet<Serializable>();
        for (CriteriaInfoProvider criteriaInfoProvider : this.criteriaInfoMap.values()) {
            result.addAll(Arrays.asList(criteriaInfoProvider.getSpaces()));
        }
        for (Map.Entry entry : this.associationPathCriteriaMap.entrySet()) {
            String path = (String)entry.getKey();
            CriteriaImpl.Subcriteria crit = (CriteriaImpl.Subcriteria)entry.getValue();
            int index = path.lastIndexOf(46);
            if (index > 0) {
                path = path.substring(index + 1, path.length());
            }
            CriteriaInfoProvider info = this.criteriaInfoMap.get(crit.getParent());
            CollectionPersister persister = this.getFactory().getMetamodel().collectionPersisters().get(info.getName() + "." + path);
            if (persister == null) continue;
            result.addAll(Arrays.asList(persister.getCollectionSpaces()));
        }
        return result;
    }

    private void createAliasCriteriaMap() {
        this.aliasCriteriaMap.put(this.rootCriteria.getAlias(), this.rootCriteria);
        Iterator<CriteriaImpl.Subcriteria> iter = this.rootCriteria.iterateSubcriteria();
        while (iter.hasNext()) {
            Criteria old;
            Criteria subcriteria = iter.next();
            if (subcriteria.getAlias() == null || (old = this.aliasCriteriaMap.put(subcriteria.getAlias(), subcriteria)) == null) continue;
            throw new QueryException("duplicate alias: " + subcriteria.getAlias());
        }
    }

    private void createAssociationPathCriteriaMap() {
        Iterator<CriteriaImpl.Subcriteria> iter = this.rootCriteria.iterateSubcriteria();
        while (iter.hasNext()) {
            CriteriaImpl.Subcriteria crit = iter.next();
            String wholeAssociationPath = this.getWholeAssociationPath(crit);
            Object old = this.associationPathCriteriaMap.put(wholeAssociationPath, crit);
            if (old != null) {
                throw new QueryException("duplicate association path: " + wholeAssociationPath);
            }
            JoinType joinType = crit.getJoinType();
            old = this.associationPathJoinTypesMap.put(wholeAssociationPath, joinType);
            if (old != null) {
                throw new QueryException("duplicate association path: " + wholeAssociationPath);
            }
            if (crit.getWithClause() == null) continue;
            this.withClauseMap.put(wholeAssociationPath, crit.getWithClause());
        }
    }

    private String getWholeAssociationPath(CriteriaImpl.Subcriteria subcriteria) {
        String testAlias;
        String path = subcriteria.getPath();
        Criteria parent = null;
        if (path.indexOf(46) > 0 && !(testAlias = StringHelper.root(path)).equals(subcriteria.getAlias())) {
            parent = this.aliasCriteriaMap.get(testAlias);
        }
        if (parent == null) {
            parent = subcriteria.getParent();
        } else {
            path = StringHelper.unroot(path);
        }
        if (parent.equals(this.rootCriteria)) {
            return path;
        }
        return this.getWholeAssociationPath((CriteriaImpl.Subcriteria)parent) + '.' + path;
    }

    private void createCriteriaEntityNameMap() {
        EntityCriteriaInfoProvider rootProvider = new EntityCriteriaInfoProvider((Queryable)this.sessionFactory.getEntityPersister(this.rootEntityName));
        this.criteriaInfoMap.put(this.rootCriteria, rootProvider);
        this.nameCriteriaInfoMap.put(rootProvider.getName(), rootProvider);
        for (Map.Entry<String, Criteria> entry : this.associationPathCriteriaMap.entrySet()) {
            String key = entry.getKey();
            Criteria value = entry.getValue();
            CriteriaInfoProvider info = this.getPathInfo(key);
            this.criteriaInfoMap.put(value, info);
            this.nameCriteriaInfoMap.put(info.getName(), info);
        }
    }

    private CriteriaInfoProvider getPathInfo(String path) {
        StringTokenizer tokens = new StringTokenizer(path, ".");
        String componentPath = "";
        CriteriaInfoProvider provider = this.nameCriteriaInfoMap.get(this.rootEntityName);
        while (tokens.hasMoreTokens()) {
            Type type = provider.getType(componentPath = componentPath + tokens.nextToken());
            if (type.isAssociationType()) {
                Type elementType;
                AssociationType atype = (AssociationType)type;
                CollectionType ctype = type.isCollectionType() ? (CollectionType)type : null;
                Type type2 = elementType = ctype != null ? ctype.getElementType(this.sessionFactory) : null;
                provider = ctype != null && elementType.isComponentType() ? new ComponentCollectionCriteriaInfoProvider(this.helper.getCollectionPersister(ctype.getRole())) : (ctype != null && !elementType.isEntityType() ? new ScalarCollectionCriteriaInfoProvider(this.helper, ctype.getRole()) : new EntityCriteriaInfoProvider((Queryable)this.sessionFactory.getEntityPersister(atype.getAssociatedEntityName(this.sessionFactory))));
                componentPath = "";
                continue;
            }
            if (type.isComponentType()) {
                if (!tokens.hasMoreTokens()) {
                    throw new QueryException("Criteria objects cannot be created directly on components.  Create a criteria on owning entity and use a dotted property to access component property: " + path);
                }
                componentPath = componentPath + '.';
                continue;
            }
            throw new QueryException("not an association: " + componentPath);
        }
        return provider;
    }

    public int getSQLAliasCount() {
        return this.criteriaSQLAliasMap.size();
    }

    private void createCriteriaSQLAliasMap() {
        int i = 0;
        for (Map.Entry<Criteria, CriteriaInfoProvider> entry : this.criteriaInfoMap.entrySet()) {
            Criteria crit = entry.getKey();
            CriteriaInfoProvider value = entry.getValue();
            String alias = crit.getAlias();
            if (alias == null) {
                alias = value.getName();
            }
            this.criteriaSQLAliasMap.put(crit, StringHelper.generateAlias(alias, i++));
        }
        this.criteriaSQLAliasMap.put(this.rootCriteria, this.rootSQLAlias);
    }

    public CriteriaImpl getRootCriteria() {
        return this.rootCriteria;
    }

    public QueryParameters getQueryParameters() {
        TypedValue[] tv;
        RowSelection selection = new RowSelection();
        selection.setFirstRow(this.rootCriteria.getFirstResult());
        selection.setMaxRows(this.rootCriteria.getMaxResults());
        selection.setTimeout(this.rootCriteria.getTimeout());
        selection.setFetchSize(this.rootCriteria.getFetchSize());
        LockOptions lockOptions = new LockOptions();
        Map<String, LockMode> lockModeMap = this.rootCriteria.getLockModes();
        for (Map.Entry<String, LockMode> entry : lockModeMap.entrySet()) {
            String key = entry.getKey();
            LockMode value = entry.getValue();
            Criteria subcriteria = this.getAliasedCriteria(key);
            lockOptions.setAliasSpecificLockMode(this.getSQLAlias(subcriteria), value);
        }
        ArrayList<Object> values = new ArrayList<Object>();
        ArrayList<Type> types = new ArrayList<Type>();
        Iterator<CriteriaImpl.Subcriteria> subcriteriaIterator = this.rootCriteria.iterateSubcriteria();
        while (subcriteriaIterator.hasNext()) {
            CriteriaImpl.Subcriteria subcriteria = subcriteriaIterator.next();
            LockMode lm = subcriteria.getLockMode();
            if (lm != null) {
                lockOptions.setAliasSpecificLockMode(this.getSQLAlias(subcriteria), lm);
            }
            if (subcriteria.getWithClause() == null) continue;
            for (TypedValue aTv : tv = subcriteria.getWithClause().getTypedValues(subcriteria, this)) {
                values.add(aTv.getValue());
                types.add(aTv.getType());
            }
        }
        Iterator<CriteriaImpl.CriterionEntry> iter = this.rootCriteria.iterateExpressionEntries();
        while (iter.hasNext()) {
            CriteriaImpl.CriterionEntry ce = iter.next();
            for (TypedValue aTv : tv = ce.getCriterion().getTypedValues(ce.getCriteria(), this)) {
                values.add(aTv.getValue());
                types.add(aTv.getType());
            }
        }
        Object[] valueArray = values.toArray();
        Type[] typeArray = ArrayHelper.toTypeArray(types);
        return new QueryParameters(typeArray, valueArray, lockOptions, selection, this.rootCriteria.isReadOnlyInitialized(), this.rootCriteria.isReadOnlyInitialized() && this.rootCriteria.isReadOnly(), this.rootCriteria.getCacheable(), this.rootCriteria.getCacheRegion(), this.rootCriteria.getComment(), this.rootCriteria.getQueryHints(), this.rootCriteria.isLookupByNaturalKey(), this.rootCriteria.getResultTransformer());
    }

    public boolean hasProjection() {
        return this.rootCriteria.getProjection() != null;
    }

    public String getGroupBy() {
        if (this.rootCriteria.getProjection().isGrouped()) {
            return this.rootCriteria.getProjection().toGroupSqlString(this.rootCriteria.getProjectionCriteria(), this);
        }
        return "";
    }

    public String getSelect() {
        return this.rootCriteria.getProjection().toSqlString(this.rootCriteria.getProjectionCriteria(), 0, this);
    }

    Type getResultType(Criteria criteria) {
        return this.getFactory().getTypeResolver().getTypeFactory().manyToOne(this.getEntityName(criteria));
    }

    public Type[] getProjectedTypes() {
        return this.rootCriteria.getProjection().getTypes(this.rootCriteria, this);
    }

    public String[] getProjectedColumnAliases() {
        return this.rootCriteria.getProjection() instanceof EnhancedProjection ? ((EnhancedProjection)this.rootCriteria.getProjection()).getColumnAliases(0, this.rootCriteria, this) : this.rootCriteria.getProjection().getColumnAliases(0);
    }

    public String[] getProjectedAliases() {
        return this.rootCriteria.getProjection().getAliases();
    }

    public String getWhereCondition() {
        StringBuilder condition = new StringBuilder(30);
        Iterator<CriteriaImpl.CriterionEntry> criterionIterator = this.rootCriteria.iterateExpressionEntries();
        while (criterionIterator.hasNext()) {
            CriteriaImpl.CriterionEntry entry = criterionIterator.next();
            String sqlString = entry.getCriterion().toSqlString(entry.getCriteria(), this);
            condition.append(sqlString);
            if (!criterionIterator.hasNext()) continue;
            condition.append(" and ");
        }
        return condition.toString();
    }

    public String getOrderBy() {
        StringBuilder orderBy = new StringBuilder(30);
        Iterator<CriteriaImpl.OrderEntry> criterionIterator = this.rootCriteria.iterateOrderings();
        while (criterionIterator.hasNext()) {
            CriteriaImpl.OrderEntry oe = criterionIterator.next();
            orderBy.append(oe.getOrder().toSqlString(oe.getCriteria(), this));
            if (!criterionIterator.hasNext()) continue;
            orderBy.append(", ");
        }
        return orderBy.toString();
    }

    @Override
    public SessionFactoryImplementor getFactory() {
        return this.sessionFactory;
    }

    @Override
    public String getSQLAlias(Criteria criteria) {
        return this.criteriaSQLAliasMap.get(criteria);
    }

    @Override
    public String getEntityName(Criteria criteria) {
        CriteriaInfoProvider infoProvider = this.criteriaInfoMap.get(criteria);
        return infoProvider != null ? infoProvider.getName() : null;
    }

    @Override
    public String getColumn(Criteria criteria, String propertyName) {
        String[] cols = this.getColumns(propertyName, criteria);
        if (cols.length != 1) {
            throw new QueryException("property does not map to a single column: " + propertyName);
        }
        return cols[0];
    }

    @Override
    public String[] getColumnsUsingProjection(Criteria subcriteria, String propertyName) throws HibernateException {
        Projection projection = this.rootCriteria.getProjection();
        String[] projectionColumns = null;
        if (projection != null) {
            String[] stringArray = projectionColumns = projection instanceof EnhancedProjection ? ((EnhancedProjection)projection).getColumnAliases(propertyName, 0, this.rootCriteria, this) : projection.getColumnAliases(propertyName, 0);
        }
        if (projectionColumns == null) {
            try {
                return this.getColumns(propertyName, subcriteria);
            }
            catch (HibernateException he) {
                if (this.outerQueryTranslator != null) {
                    return this.outerQueryTranslator.getColumnsUsingProjection(subcriteria, propertyName);
                }
                throw he;
            }
        }
        return projectionColumns;
    }

    @Override
    public String[] getIdentifierColumns(Criteria criteria) {
        String[] idcols = ((Loadable)((Object)this.getPropertyMapping(this.getEntityName(criteria)))).getIdentifierColumnNames();
        return StringHelper.qualify(this.getSQLAlias(criteria), idcols);
    }

    @Override
    public Type getIdentifierType(Criteria criteria) {
        return ((Loadable)((Object)this.getPropertyMapping(this.getEntityName(criteria)))).getIdentifierType();
    }

    @Override
    public TypedValue getTypedIdentifierValue(Criteria criteria, Object value) {
        Loadable loadable = (Loadable)((Object)this.getPropertyMapping(this.getEntityName(criteria)));
        return new TypedValue(loadable.getIdentifierType(), value);
    }

    @Override
    public Type getForeignKeyType(Criteria criteria, String associationPropertyName) {
        Type propertyType = ((Loadable)((Object)this.getPropertyMapping(this.getEntityName(criteria)))).getPropertyType(associationPropertyName);
        if (!(propertyType instanceof ManyToOneType)) {
            throw new QueryException("Argument to fk() function must be the fk owner of a to-one association, but found " + propertyType);
        }
        return ((ManyToOneType)propertyType).getIdentifierOrUniqueKeyType(this.getFactory());
    }

    @Override
    public String[] getForeignKeyColumns(Criteria criteria, String associationPropertyName) {
        PropertyMapping propertyMapping = this.getPropertyMapping(this.getEntityName(criteria));
        assert (propertyMapping instanceof EntityPersister);
        Type propertyType = ((EntityPersister)((Object)propertyMapping)).getPropertyType(associationPropertyName);
        if (!(propertyType instanceof ManyToOneType)) {
            throw new QueryException("Argument to fk() function must be the fk owner of a to-one association, but found " + propertyType);
        }
        return propertyMapping.toColumns(this.getSQLAlias(criteria, associationPropertyName), associationPropertyName);
    }

    @Override
    public TypedValue getForeignKeyTypeValue(Criteria criteria, String associationPropertyName, Object value) {
        return new TypedValue(this.getForeignKeyType(criteria, associationPropertyName), value);
    }

    @Override
    public String[] getColumns(String propertyName, Criteria subcriteria) throws HibernateException {
        try {
            return this.getPropertyMapping(this.getEntityName(subcriteria, propertyName)).toColumns(this.getSQLAlias(subcriteria, propertyName), this.getPropertyName(propertyName));
        }
        catch (QueryException qe) {
            if (propertyName.indexOf(46) > 0) {
                String propertyRootName = StringHelper.root(propertyName);
                CriteriaInfoProvider pathInfo = this.getPathInfo(propertyRootName);
                PropertyMapping propertyMapping = pathInfo.getPropertyMapping();
                if (propertyMapping instanceof EntityPersister) {
                    String name = propertyName.substring(propertyRootName.length() + 1);
                    if (((EntityPersister)((Object)propertyMapping)).getIdentifierPropertyName().equals(name)) {
                        Criteria associationPathCriteria = this.associationPathCriteriaMap.get(propertyRootName);
                        if (associationPathCriteria == null) {
                            Criteria criteria = this.addInnerJoin(subcriteria, propertyRootName, pathInfo);
                            return propertyMapping.toColumns(this.getSQLAlias(criteria, name), name);
                        }
                        return propertyMapping.toColumns(this.getSQLAlias(associationPathCriteria, name), name);
                    }
                }
                throw qe;
            }
            throw qe;
        }
    }

    private Criteria addInnerJoin(Criteria subcriteria, String root, CriteriaInfoProvider pathInfo) {
        Criteria criteria = subcriteria.createCriteria(root, root, JoinType.INNER_JOIN);
        this.aliasCriteriaMap.put(root, criteria);
        this.associationPathCriteriaMap.put(root, criteria);
        this.associationPathJoinTypesMap.put(root, JoinType.INNER_JOIN);
        this.criteriaInfoMap.put(criteria, pathInfo);
        this.nameCriteriaInfoMap.put(pathInfo.getName(), pathInfo);
        this.criteriaSQLAliasMap.put(criteria, StringHelper.generateAlias(root, this.criteriaSQLAliasMap.size()));
        return criteria;
    }

    @Override
    public String[] findColumns(String propertyName, Criteria subcriteria) throws HibernateException {
        try {
            return this.getColumns(propertyName, subcriteria);
        }
        catch (HibernateException he) {
            if (this.outerQueryTranslator != null) {
                return this.outerQueryTranslator.findColumns(propertyName, subcriteria);
            }
            throw he;
        }
    }

    @Override
    public Type getTypeUsingProjection(Criteria subcriteria, String propertyName) throws HibernateException {
        Type[] projectionTypes;
        Projection projection = this.rootCriteria.getProjection();
        Type[] typeArray = projectionTypes = projection == null ? null : projection.getTypes(propertyName, subcriteria, this);
        if (projectionTypes == null) {
            try {
                return this.getType(subcriteria, propertyName);
            }
            catch (HibernateException he) {
                if (this.outerQueryTranslator != null) {
                    return this.outerQueryTranslator.getType(subcriteria, propertyName);
                }
                throw he;
            }
        }
        if (projectionTypes.length != 1) {
            throw new QueryException("not a single-length projection: " + propertyName);
        }
        return projectionTypes[0];
    }

    @Override
    public Type getType(Criteria subcriteria, String propertyName) throws HibernateException {
        try {
            return this.getPropertyMapping(this.getEntityName(subcriteria, propertyName)).toType(this.getPropertyName(propertyName));
        }
        catch (QueryException qe) {
            if (propertyName.indexOf(46) > 0) {
                PropertyMapping propertyMapping;
                String propertyRootName = StringHelper.root(propertyName);
                String name = propertyName.substring(propertyRootName.length() + 1);
                Criteria associationPathCriteria = this.associationPathCriteriaMap.get(propertyRootName);
                if (associationPathCriteria != null && (propertyMapping = this.getPropertyMapping(this.getEntityName(associationPathCriteria, propertyRootName))) instanceof EntityPersister && ((EntityPersister)((Object)propertyMapping)).getIdentifierPropertyName().equals(name)) {
                    return propertyMapping.toType(this.getPropertyName(name));
                }
                throw qe;
            }
            throw qe;
        }
    }

    @Override
    public TypedValue getTypedValue(Criteria subcriteria, String propertyName, Object value) throws HibernateException {
        Class entityClass;
        Queryable q;
        if (value instanceof Class && (q = SessionFactoryHelper.findQueryableUsingImports(this.sessionFactory, (entityClass = (Class)value).getName())) != null) {
            Type type = q.getDiscriminatorType();
            String stringValue = q.getDiscriminatorSQLValue();
            if (stringValue != null && stringValue.length() > 2 && stringValue.startsWith("'") && stringValue.endsWith("'")) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            if (!(type instanceof StringRepresentableType)) {
                throw new QueryException("Unsupported discriminator type " + type);
            }
            StringRepresentableType nullableType = (StringRepresentableType)((Object)type);
            value = nullableType.fromStringValue(stringValue);
            return new TypedValue(type, value);
        }
        return new TypedValue(this.getTypeUsingProjection(subcriteria, propertyName), value);
    }

    private PropertyMapping getPropertyMapping(String entityName) throws MappingException {
        CriteriaInfoProvider info = this.nameCriteriaInfoMap.get(entityName);
        if (info == null) {
            throw new HibernateException("Unknown entity: " + entityName);
        }
        return info.getPropertyMapping();
    }

    @Override
    public String getEntityName(Criteria subcriteria, String propertyName) {
        String root;
        Criteria crit;
        if (propertyName.indexOf(46) > 0 && (crit = this.getAliasedCriteria(root = StringHelper.root(propertyName))) != null) {
            return this.getEntityName(crit);
        }
        return this.getEntityName(subcriteria);
    }

    @Override
    public String getSQLAlias(Criteria criteria, String propertyName) {
        String root;
        Criteria subcriteria;
        if (propertyName.indexOf(46) > 0 && (subcriteria = this.getAliasedCriteria(root = StringHelper.root(propertyName))) != null) {
            return this.getSQLAlias(subcriteria);
        }
        return this.getSQLAlias(criteria);
    }

    @Override
    public String getPropertyName(String propertyName) {
        String root;
        Criteria criteria;
        if (propertyName.indexOf(46) > 0 && (criteria = this.getAliasedCriteria(root = StringHelper.root(propertyName))) != null) {
            return propertyName.substring(root.length() + 1);
        }
        return propertyName;
    }

    public String getWithClause(String path) {
        Criterion criterion = this.withClauseMap.get(path);
        return criterion == null ? null : criterion.toSqlString(this.getCriteria(path), this);
    }

    public boolean hasRestriction(String path) {
        CriteriaImpl.Subcriteria subcriteria = (CriteriaImpl.Subcriteria)this.getCriteria(path);
        return subcriteria != null && subcriteria.hasRestriction();
    }
}


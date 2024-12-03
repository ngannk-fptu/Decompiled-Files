/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Defaults
 */
package net.java.ao;

import com.google.common.base.Defaults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.java.ao.Common;
import net.java.ao.DatabaseProvider;
import net.java.ao.EntityManager;
import net.java.ao.ImplementationWrapper;
import net.java.ao.ManyToMany;
import net.java.ao.MethodImplWrapper;
import net.java.ao.OneToMany;
import net.java.ao.OneToOne;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.FieldInfo;
import net.java.ao.sql.SqlUtils;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;

public class EntityProxy<T extends RawEntity<K>, K>
implements InvocationHandler {
    private final K key;
    private final EntityInfo<T, K> entityInfo;
    private final EntityManager manager;
    private final Map<String, Object> values = new HashMap<String, Object>();
    private final Set<String> dirty = new HashSet<String>();
    private final Lock lockValuesDirty = new ReentrantLock();
    private ImplementationWrapper<T> implementation;
    private List<PropertyChangeListener> listeners;

    EntityProxy(EntityManager manager, EntityInfo<T, K> entityInfo, K key) {
        this.key = key;
        this.entityInfo = entityInfo;
        this.manager = manager;
        this.listeners = new LinkedList<PropertyChangeListener>();
    }

    private FieldNameConverter getFieldNameConverter() {
        return this.manager.getNameConverters().getFieldNameConverter();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean isManyToMany;
        String callingClassName;
        Class<?> declaringClass;
        MethodImplWrapper methodImpl;
        String methodName = method.getName();
        if (methodName.equals("getEntityProxy")) {
            return this;
        }
        if (methodName.equals("getEntityType")) {
            return this.getType();
        }
        if (this.implementation == null) {
            this.implementation = new ImplementationWrapper();
            this.implementation.init((RawEntity)proxy);
        }
        if (!((methodImpl = this.implementation.getMethod(methodName, method.getParameterTypes())) == null || Object.class.equals(declaringClass = methodImpl.getMethod().getDeclaringClass()) || (callingClassName = Common.getCallingClassName(1)) != null && callingClassName.equals(declaringClass.getName()))) {
            return methodImpl.getMethod().invoke(methodImpl.getInstance(), args);
        }
        if (this.entityInfo.hasAccessor(method) && this.entityInfo.getField(method).equals(this.entityInfo.getPrimaryKey())) {
            return this.getKey();
        }
        if (methodName.equals("save")) {
            this.save((RawEntity)proxy);
            return Void.TYPE;
        }
        if (methodName.equals("getEntityManager")) {
            return this.manager;
        }
        if (methodName.equals("addPropertyChangeListener")) {
            this.addPropertyChangeListener((PropertyChangeListener)args[0]);
            return null;
        }
        if (methodName.equals("removePropertyChangeListener")) {
            this.removePropertyChangeListener((PropertyChangeListener)args[0]);
            return null;
        }
        if (methodName.equals("hashCode")) {
            return this.hashCodeImpl();
        }
        if (methodName.equals("equals")) {
            return this.equalsImpl((RawEntity)proxy, args[0]);
        }
        if (methodName.equals("toString")) {
            return this.toStringImpl();
        }
        if (methodName.equals("init")) {
            return null;
        }
        FieldInfo fieldInfo = this.entityInfo.getField(method);
        if (fieldInfo != null && !fieldInfo.isNullable() && args != null && args.length > 0 && args[0] == null) {
            throw new IllegalArgumentException("Field '" + this.getFieldNameConverter().getName(method) + "' does not accept null values");
        }
        OneToOne oneToOneAnnotation = method.getAnnotation(OneToOne.class);
        boolean isOneToOne = oneToOneAnnotation != null && RawEntity.class.isAssignableFrom(method.getReturnType());
        OneToMany oneToManyAnnotation = method.getAnnotation(OneToMany.class);
        boolean isOneToMany = oneToManyAnnotation != null && method.getReturnType().isArray() && RawEntity.class.isAssignableFrom(method.getReturnType().getComponentType());
        ManyToMany manyToManyAnnotation = method.getAnnotation(ManyToMany.class);
        boolean bl = isManyToMany = manyToManyAnnotation != null && method.getReturnType().isArray() && RawEntity.class.isAssignableFrom(method.getReturnType().getComponentType());
        if (isOneToOne || isOneToMany || isManyToMany) {
            Object ret;
            this.lockValuesDirty.lock();
            try {
                if (this.values.containsKey(methodName)) {
                    ret = this.values.get(methodName);
                } else if (isOneToOne) {
                    ret = oneToOneAnnotation.reverse().isEmpty() ? this.legacyFetchOneToOne((RawEntity)proxy, method, oneToOneAnnotation) : this.fetchOneToOne(method, oneToOneAnnotation);
                    this.values.put(methodName, ret);
                } else if (isOneToMany) {
                    ret = oneToManyAnnotation.reverse().isEmpty() ? this.legacyFetchOneToMany((RawEntity)proxy, method, oneToManyAnnotation) : this.fetchOneToMany(method, oneToManyAnnotation);
                    this.values.put(methodName, ret);
                } else if (isManyToMany) {
                    ret = manyToManyAnnotation.reverse().isEmpty() || manyToManyAnnotation.through().isEmpty() ? this.legacyFetchManyToMany((RawEntity)proxy, method, manyToManyAnnotation) : this.fetchManyToMany(method, manyToManyAnnotation);
                    this.values.put(methodName, ret);
                } else {
                    ret = null;
                }
            }
            finally {
                this.lockValuesDirty.unlock();
            }
            return ret;
        }
        if (fieldInfo != null) {
            if (method.equals(fieldInfo.getAccessor())) {
                return this.invokeGetter(fieldInfo);
            }
            if (method.equals(fieldInfo.getMutator())) {
                this.invokeSetter(this.getFieldNameConverter().getName(method), args[0], fieldInfo.getPolymorphicName());
                return Void.TYPE;
            }
        }
        throw new IllegalArgumentException("Cannot handle method. It is not a valid getter or setter and does not have an implementation supplied. Signature: " + method.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RawEntity[] fetchManyToMany(Method method, ManyToMany annotation) throws SQLException, NoSuchMethodException {
        Class<?> remoteType = method.getReturnType().getComponentType();
        Class<RawEntity<?>> throughType = annotation.value();
        String whereClause = Common.where(annotation, this.getFieldNameConverter());
        Preload preloadAnnotation = remoteType.getAnnotation(Preload.class);
        Method reverseMethod = throughType.getMethod(annotation.reverse(), new Class[0]);
        Method throughMethod = throughType.getMethod(annotation.through(), new Class[0]);
        String reversePolymorphicTypeFieldName = Common.getAttributeTypeFromMethod(reverseMethod).isAnnotationPresent(Polymorphic.class) ? this.getFieldNameConverter().getPolyTypeName(reverseMethod) : null;
        String remotePolymorphicTypeFieldName = Common.getAttributeTypeFromMethod(throughMethod).isAnnotationPresent(Polymorphic.class) ? this.getFieldNameConverter().getPolyTypeName(throughMethod) : null;
        String returnField = this.getFieldNameConverter().getName(throughMethod);
        LinkedHashSet<String> selectFields = new LinkedHashSet<String>();
        DatabaseProvider provider = this.manager.getProvider();
        StringBuilder sql = new StringBuilder("SELECT t.").append(provider.processID(returnField));
        if (remotePolymorphicTypeFieldName != null) {
            sql.append(", t.").append(provider.processID(remotePolymorphicTypeFieldName));
        } else {
            if (preloadAnnotation != null) {
                selectFields.addAll(Common.preloadValue(preloadAnnotation, this.getFieldNameConverter()));
            } else {
                selectFields.addAll((Collection<String>)Common.getValueFieldsNames(this.manager.resolveEntityInfo(remoteType), this.getFieldNameConverter()));
            }
            if (selectFields.contains("*")) {
                sql.append(", r.*");
            } else {
                for (String field : selectFields) {
                    sql.append(", r.").append(this.manager.getProvider().processID(field));
                }
            }
        }
        String throughTable = provider.withSchema(this.getTableNameConverter().getName(throughType));
        sql.append(" FROM ").append(throughTable).append(" t ");
        String remotePrimaryKeyField = Common.getPrimaryKeyField(remoteType, this.getFieldNameConverter());
        if (!selectFields.isEmpty()) {
            String remoteTable = provider.withSchema(this.getTableNameConverter().getName(remoteType));
            sql.append(" INNER JOIN ").append(remoteTable).append(" r ON t.").append(provider.processID(returnField)).append(" = r.").append(provider.processID(remotePrimaryKeyField));
        }
        String reverseField = provider.processID(this.getFieldNameConverter().getName(reverseMethod));
        sql.append(" WHERE ");
        if (!selectFields.isEmpty()) {
            sql.append("t.");
        }
        sql.append(reverseField).append(" = ?");
        if (reversePolymorphicTypeFieldName != null) {
            sql.append(" AND ");
            if (!selectFields.isEmpty()) {
                sql.append("t.");
            }
            sql.append(provider.processID(reversePolymorphicTypeFieldName)).append(" = ?");
        }
        if (!whereClause.trim().equals("")) {
            sql.append(" AND (").append(provider.processWhereClause(whereClause)).append(")");
        }
        ArrayList back = new ArrayList();
        try (Connection conn = provider.getConnection();
             PreparedStatement stmt = provider.preparedStatement(conn, sql);){
            TypeInfo<K> dbType = this.getTypeManager().getType(EntityProxy.getClass(this.key));
            dbType.getLogicalType().putToDatabase(this.manager, stmt, 1, this.key, dbType.getJdbcWriteType());
            if (reversePolymorphicTypeFieldName != null) {
                stmt.setString(2, this.manager.getPolymorphicTypeMapper().convert(this.entityInfo.getEntityType()));
            }
            TypeInfo primaryKeyType = Common.getPrimaryKeyType(provider.getTypeManager(), remoteType);
            try (ResultSet res = stmt.executeQuery();){
                while (res.next()) {
                    EntityInfo entityInfo = this.manager.resolveEntityInfo(remotePolymorphicTypeFieldName == null ? remoteType : this.manager.getPolymorphicTypeMapper().invert(remoteType, res.getString(remotePolymorphicTypeFieldName)));
                    if (selectFields.remove("*")) {
                        selectFields.addAll(entityInfo.getFieldNames());
                    }
                    Object returnValueEntity = this.manager.peer(entityInfo, primaryKeyType.getLogicalType().pullFromDatabase(this.manager, res, throughType, returnField));
                    EntityProxy proxy = this.manager.getProxyForEntity(returnValueEntity);
                    proxy.lockValuesDirty.lock();
                    try {
                        for (String field : selectFields) {
                            proxy.values.put(field, res.getObject(field));
                        }
                    }
                    finally {
                        proxy.lockValuesDirty.unlock();
                    }
                    back.add(returnValueEntity);
                }
            }
        }
        return back.toArray((RawEntity[])Array.newInstance(remoteType, back.size()));
    }

    /*
     * Exception decompiling
     */
    private RawEntity[] fetchOneToMany(Method method, OneToMany annotation) throws SQLException, NoSuchMethodException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private String getPolymorphicTypeFieldName(Method remoteMethod) {
        Class<T> attributeType = Common.getAttributeTypeFromMethod(remoteMethod);
        return attributeType != null && attributeType.isAssignableFrom(this.getType()) && attributeType.isAnnotationPresent(Polymorphic.class) ? this.getFieldNameConverter().getPolyTypeName(remoteMethod) : null;
    }

    /*
     * Exception decompiling
     */
    private RawEntity fetchOneToOne(Method method, OneToOne annotation) throws SQLException, NoSuchMethodException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Deprecated
    private RawEntity[] legacyFetchManyToMany(RawEntity<K> proxy, Method method, ManyToMany manyToManyAnnotation) throws SQLException {
        Class<? extends RawEntity<?>> throughType = manyToManyAnnotation.value();
        Class<?> type = method.getReturnType().getComponentType();
        return this.retrieveRelations(proxy, null, Common.getMappingFields(this.getFieldNameConverter(), throughType, type), throughType, type, Common.where(manyToManyAnnotation, this.getFieldNameConverter()), Common.getPolymorphicFieldNames(this.getFieldNameConverter(), throughType, this.entityInfo.getEntityType()), Common.getPolymorphicFieldNames(this.getFieldNameConverter(), throughType, type));
    }

    @Deprecated
    private RawEntity[] legacyFetchOneToMany(RawEntity<K> proxy, Method method, OneToMany oneToManyAnnotation) throws SQLException {
        Class<?> type = method.getReturnType().getComponentType();
        return this.retrieveRelations(proxy, new String[0], new String[]{Common.getPrimaryKeyField(type, this.getFieldNameConverter())}, type, Common.where(oneToManyAnnotation, this.getFieldNameConverter()), Common.getPolymorphicFieldNames(this.getFieldNameConverter(), type, this.entityInfo.getEntityType()));
    }

    @Deprecated
    private RawEntity legacyFetchOneToOne(RawEntity<K> proxy, Method method, OneToOne oneToOneAnnotation) throws SQLException {
        Class<?> type = method.getReturnType();
        RawEntity[] back = this.retrieveRelations(proxy, new String[0], new String[]{Common.getPrimaryKeyField(type, this.getFieldNameConverter())}, type, Common.where(oneToOneAnnotation, this.getFieldNameConverter()), Common.getPolymorphicFieldNames(this.getFieldNameConverter(), type, this.entityInfo.getEntityType()));
        return back.length == 0 ? null : back[0];
    }

    private TableNameConverter getTableNameConverter() {
        return this.manager.getNameConverters().getTableNameConverter();
    }

    public K getKey() {
        return this.key;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save(RawEntity entity) throws SQLException {
        this.lockValuesDirty.lock();
        try {
            if (this.dirty.isEmpty()) {
                return;
            }
            String table = this.entityInfo.getName();
            DatabaseProvider provider = this.manager.getProvider();
            TypeManager typeManager = provider.getTypeManager();
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = provider.getConnection();
                StringBuilder sql = new StringBuilder("UPDATE " + provider.withSchema(table) + " SET ");
                String paramList = this.dirty.stream().map(this::prepareParam).collect(Collectors.joining(", "));
                sql.append(paramList);
                sql.append(" WHERE ").append(provider.processID(this.entityInfo.getPrimaryKey().getName())).append(" = ?");
                stmt = provider.preparedStatement(conn, sql);
                LinkedList<PropertyChangeEvent> events = new LinkedList<PropertyChangeEvent>();
                int index = 1;
                for (String name : this.dirty) {
                    if (!this.values.containsKey(name)) continue;
                    Object value = this.values.get(name);
                    FieldInfo fieldInfo = this.entityInfo.getField(name);
                    events.add(new PropertyChangeEvent(entity, name, null, value));
                    if (value == null) {
                        this.manager.getProvider().putNull(stmt, index++);
                        continue;
                    }
                    Class<Object> javaType = value.getClass();
                    if (value instanceof RawEntity) {
                        javaType = ((RawEntity)value).getEntityType();
                    }
                    TypeInfo<?> dbType = typeManager.getType(javaType);
                    dbType.getLogicalType().validate(value);
                    dbType.getLogicalType().putToDatabase(this.manager, stmt, index++, value, dbType.getJdbcWriteType());
                    if (fieldInfo.isStorable()) continue;
                    this.values.remove(name);
                }
                TypeInfo<K> pkType = this.entityInfo.getPrimaryKey().getTypeInfo();
                pkType.getLogicalType().putToDatabase(this.manager, stmt, index, this.key, pkType.getJdbcWriteType());
                stmt.executeUpdate();
                this.dirty.clear();
                for (PropertyChangeListener l : this.listeners) {
                    for (PropertyChangeEvent evt : events) {
                        l.propertyChange(evt);
                    }
                }
            }
            catch (Throwable throwable) {
                SqlUtils.closeQuietly(stmt);
                SqlUtils.closeQuietly(conn);
                throw throwable;
            }
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(conn);
        }
        finally {
            this.lockValuesDirty.unlock();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.remove(listener);
    }

    public int hashCodeImpl() {
        return (this.key.hashCode() + this.entityInfo.hashCode()) % 65536;
    }

    public boolean equalsImpl(RawEntity<K> proxy, Object obj) {
        if (proxy == obj) {
            return true;
        }
        if (obj instanceof RawEntity) {
            RawEntity entity = (RawEntity)obj;
            String ourTableName = this.getTableNameConverter().getName(proxy.getEntityType());
            String theirTableName = this.getTableNameConverter().getName(entity.getEntityType());
            return Common.getPrimaryKeyValue(entity).equals(this.key) && theirTableName.equals(ourTableName);
        }
        return false;
    }

    public String toStringImpl() {
        return this.entityInfo.getName() + " {" + this.entityInfo.getPrimaryKey().getName() + " = " + this.key.toString() + "}";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EntityProxy) {
            EntityProxy proxy = (EntityProxy)obj;
            if (proxy.entityInfo.equals(this.entityInfo) && proxy.key.equals(this.key)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.hashCodeImpl();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void updateValues(Map<String, Object> updatedValues) {
        this.lockValuesDirty.lock();
        try {
            for (Map.Entry<String, Object> updatedValue : updatedValues.entrySet()) {
                this.values.put(updatedValue.getKey(), updatedValue.getValue());
            }
        }
        finally {
            this.lockValuesDirty.unlock();
        }
    }

    Class<T> getType() {
        return this.entityInfo.getEntityType();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <V> V invokeGetter(FieldInfo<V> fieldInfo) throws Throwable {
        Class<V> type = fieldInfo.getJavaType();
        String name = fieldInfo.getName();
        boolean isStorable = fieldInfo.isStorable();
        this.lockValuesDirty.lock();
        try {
            if (this.values.containsKey(name) && isStorable) {
                EntityInfo remoteEntityInfo;
                Object value = this.values.get(name);
                if (this.instanceOf(value, type)) {
                    Object object = this.handleNullReturn(value, type);
                    return (V)object;
                }
                if (this.isBigDecimal(value, type)) {
                    Object object = this.handleBigDecimal(value, type);
                    return (V)object;
                }
                if (RawEntity.class.isAssignableFrom(type) && this.instanceOf(value, (remoteEntityInfo = this.manager.resolveEntityInfo(type)).getPrimaryKey().getJavaType())) {
                    value = this.manager.peer(remoteEntityInfo, value);
                    this.values.put(name, value);
                    Object object = this.handleNullReturn(value, type);
                    return (V)object;
                }
            }
            V back = this.pullFromDatabase(fieldInfo);
            if (isStorable) {
                this.values.put(name, back);
            }
            V v = this.handleNullReturn(back, type);
            return v;
        }
        finally {
            this.lockValuesDirty.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <V> V pullFromDatabase(FieldInfo<V> fieldInfo) throws SQLException {
        Class<V> type = fieldInfo.getJavaType();
        String name = fieldInfo.getName();
        DatabaseProvider provider = this.manager.getProvider();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        V back = null;
        try {
            conn = provider.getConnection();
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(provider.processID(name));
            String polyName = fieldInfo.getPolymorphicName();
            if (polyName != null) {
                sql.append(',').append(provider.processID(polyName));
            }
            sql.append(" FROM ").append(provider.withSchema(this.entityInfo.getName())).append(" WHERE ");
            sql.append(provider.processID(this.entityInfo.getPrimaryKey().getName())).append(" = ?");
            stmt = provider.preparedStatement(conn, sql);
            TypeInfo<K> pkType = this.entityInfo.getPrimaryKey().getTypeInfo();
            pkType.getLogicalType().putToDatabase(this.manager, stmt, 1, this.getKey(), pkType.getJdbcWriteType());
            res = stmt.executeQuery();
            if (res.next()) {
                back = this.convertValue(res, provider.shorten(name), provider.shorten(polyName), type);
            }
        }
        catch (Throwable throwable) {
            SqlUtils.closeQuietly(res, stmt, conn);
            throw throwable;
        }
        SqlUtils.closeQuietly(res, stmt, conn);
        return back;
    }

    private <V> V handleNullReturn(V back, Class<V> type) {
        return (V)(back != null ? back : Defaults.defaultValue(type));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void invokeSetter(String name, Object value, String polyName) throws Throwable {
        this.lockValuesDirty.lock();
        try {
            this.values.put(name, value);
            this.dirty.add(name);
            if (polyName != null) {
                String strValue = null;
                if (value != null) {
                    strValue = this.manager.getPolymorphicTypeMapper().convert(((RawEntity)value).getEntityType());
                }
                this.values.put(polyName, strValue);
                this.dirty.add(polyName);
            }
        }
        finally {
            this.lockValuesDirty.unlock();
        }
    }

    @Deprecated
    private <V extends RawEntity<K>> V[] retrieveRelations(RawEntity<K> entity, String[] inMapFields, String[] outMapFields, Class<V> type, String where, String[] thisPolyNames) throws SQLException {
        return this.retrieveRelations(entity, inMapFields, outMapFields, type, type, where, thisPolyNames, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    private <V extends RawEntity<K>> V[] retrieveRelations(RawEntity<K> entity, String[] inMapFields, String[] outMapFields, Class<? extends RawEntity<?>> type, Class<V> finalType, String where, String[] thisPolyNames, String[] thatPolyNames) throws SQLException {
        if (inMapFields == null || inMapFields.length == 0) {
            inMapFields = Common.getMappingFields(this.getFieldNameConverter(), type, this.entityInfo.getEntityType());
        }
        ArrayList<V> back = new ArrayList<V>();
        ArrayList<String> resPolyNames = new ArrayList<String>(thatPolyNames == null ? 0 : thatPolyNames.length);
        String table = this.getTableNameConverter().getName(type);
        boolean oneToMany = type.equals(finalType);
        Preload preloadAnnotation = finalType.getAnnotation(Preload.class);
        DatabaseProvider provider = this.manager.getProvider();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            int index;
            String throughField;
            String returnField;
            conn = provider.getConnection();
            StringBuilder sql = new StringBuilder();
            int numParams = 0;
            LinkedHashSet<String> selectFields = new LinkedHashSet<String>();
            if (oneToMany && inMapFields.length == 1 && outMapFields.length == 1 && (thatPolyNames == null || thatPolyNames.length == 0)) {
                sql.append("SELECT ");
                selectFields.add(outMapFields[0]);
                if (preloadAnnotation != null) {
                    selectFields.addAll(Common.preloadValue(preloadAnnotation, this.getFieldNameConverter()));
                } else {
                    selectFields.addAll((Collection<String>)Common.getValueFieldsNames(this.manager.resolveEntityInfo(finalType), this.getFieldNameConverter()));
                }
                if (selectFields.contains("*")) {
                    sql.append("*");
                } else {
                    for (String field : selectFields) {
                        sql.append(provider.processID(field)).append(',');
                    }
                    sql.setLength(sql.length() - 1);
                }
                sql.append(" FROM ").append(provider.withSchema(table));
                sql.append(" WHERE ").append(provider.processID(inMapFields[0])).append(" = ?");
                if (!where.trim().equals("")) {
                    sql.append(" AND (").append(this.manager.getProvider().processWhereClause(where)).append(")");
                }
                if (thisPolyNames != null) {
                    for (String name : thisPolyNames) {
                        sql.append(" AND ").append(provider.processID(name)).append(" = ?");
                    }
                }
                ++numParams;
                returnField = outMapFields[0];
            } else if (!(oneToMany || inMapFields.length != 1 || outMapFields.length != 1 || thatPolyNames != null && thatPolyNames.length != 0)) {
                String[] finalTable = this.getTableNameConverter().getName(finalType);
                String finalTableAlias = "f";
                String tableAlias = "t";
                returnField = this.manager.getProvider().shorten((String)finalTable + "__aointernal__id");
                throughField = this.manager.getProvider().shorten(table + "__aointernal__id");
                sql.append("SELECT ");
                String finalPKField = Common.getPrimaryKeyField(finalType, this.getFieldNameConverter());
                selectFields.add(finalPKField);
                if (preloadAnnotation != null) {
                    selectFields.addAll(Common.preloadValue(preloadAnnotation, this.getFieldNameConverter()));
                } else {
                    selectFields.addAll((Collection<String>)Common.getValueFieldsNames(this.manager.resolveEntityInfo(finalType), this.getFieldNameConverter()));
                }
                if (selectFields.contains("*")) {
                    selectFields.remove("*");
                    selectFields.addAll((Collection<String>)Common.getValueFieldsNames(this.manager.resolveEntityInfo(finalType), this.getFieldNameConverter()));
                }
                sql.append("f").append('.').append(provider.processID(finalPKField));
                sql.append(" AS ").append(provider.quote(returnField)).append(',');
                selectFields.remove(finalPKField);
                sql.append("t").append('.').append(provider.processID(Common.getPrimaryKeyField(type, this.getFieldNameConverter())));
                sql.append(" AS ").append(provider.quote(throughField)).append(',');
                for (String field : selectFields) {
                    sql.append("f").append('.').append(provider.processID(field)).append(',');
                }
                sql.setLength(sql.length() - 1);
                sql.append(" FROM ").append(provider.withSchema(table)).append(" ").append("t").append(" INNER JOIN ");
                sql.append(provider.withSchema((String)finalTable)).append(" ").append("f").append(" ON ");
                sql.append("t").append('.').append(provider.processID(outMapFields[0]));
                sql.append(" = ").append("f").append('.').append(provider.processID(finalPKField));
                sql.append(" WHERE ").append("t").append('.').append(provider.processID(inMapFields[0])).append(" = ?");
                if (!where.trim().equals("")) {
                    sql.append(" AND (").append(this.manager.getProvider().processWhereClause(where)).append(")");
                }
                if (thisPolyNames != null) {
                    for (String name : thisPolyNames) {
                        sql.append(" AND ").append(provider.processID(name)).append(" = ?");
                    }
                }
                ++numParams;
            } else if (inMapFields.length == 1 && outMapFields.length == 1) {
                sql.append("SELECT ").append(provider.processID(outMapFields[0]));
                selectFields.add(outMapFields[0]);
                if (!oneToMany) {
                    throughField = Common.getPrimaryKeyField(type, this.getFieldNameConverter());
                    sql.append(',').append(provider.processID(throughField));
                    selectFields.add(throughField);
                }
                if (thatPolyNames != null) {
                    for (String name : thatPolyNames) {
                        resPolyNames.add(name);
                        sql.append(',').append(provider.processID(name));
                        selectFields.add(name);
                    }
                }
                sql.append(" FROM ").append(provider.withSchema(table));
                sql.append(" WHERE ").append(provider.processID(inMapFields[0])).append(" = ?");
                if (!where.trim().equals("")) {
                    sql.append(" AND (").append(this.manager.getProvider().processWhereClause(where)).append(")");
                }
                if (thisPolyNames != null) {
                    for (String name : thisPolyNames) {
                        sql.append(" AND ").append(provider.processID(name)).append(" = ?");
                    }
                }
                ++numParams;
                returnField = outMapFields[0];
            } else {
                sql.append("SELECT DISTINCT a.outMap AS outMap");
                selectFields.add("outMap");
                if (thatPolyNames != null) {
                    for (String name : thatPolyNames) {
                        resPolyNames.add(name);
                        sql.append(',').append("a.").append(provider.processID(name)).append(" AS ").append(provider.processID(name));
                        selectFields.add(name);
                    }
                }
                sql.append(" FROM (");
                returnField = "outMap";
                for (String outMap : outMapFields) {
                    for (String inMap : inMapFields) {
                        sql.append("SELECT ");
                        sql.append(provider.processID(outMap));
                        sql.append(" AS outMap,");
                        sql.append(provider.processID(inMap));
                        sql.append(" AS inMap");
                        if (thatPolyNames != null) {
                            for (String name : thatPolyNames) {
                                sql.append(',').append(provider.processID(name));
                            }
                        }
                        if (thisPolyNames != null) {
                            for (String name : thisPolyNames) {
                                sql.append(',').append(provider.processID(name));
                            }
                        }
                        sql.append(" FROM ").append(provider.withSchema(table));
                        sql.append(" WHERE ");
                        sql.append(provider.processID(inMap)).append(" = ?");
                        if (!where.trim().equals("")) {
                            sql.append(" AND (").append(this.manager.getProvider().processWhereClause(where)).append(")");
                        }
                        sql.append(" UNION ");
                        ++numParams;
                    }
                }
                sql.setLength(sql.length() - " UNION ".length());
                sql.append(") a");
                if (thatPolyNames != null) {
                    if (thatPolyNames.length > 0) {
                        sql.append(" WHERE (");
                    }
                    for (String name : thatPolyNames) {
                        sql.append("a.").append(provider.processID(name)).append(" = ?").append(" OR ");
                    }
                    if (thatPolyNames.length > 0) {
                        sql.setLength(sql.length() - " OR ".length());
                        sql.append(')');
                    }
                }
                if (thisPolyNames != null) {
                    if (thisPolyNames.length > 0) {
                        if (thatPolyNames == null) {
                            sql.append(" WHERE (");
                        } else {
                            sql.append(" AND (");
                        }
                    }
                    for (String name : thisPolyNames) {
                        sql.append("a.").append(provider.processID(name)).append(" = ?").append(" OR ");
                    }
                    if (thisPolyNames.length > 0) {
                        sql.setLength(sql.length() - " OR ".length());
                        sql.append(')');
                    }
                }
            }
            stmt = provider.preparedStatement(conn, sql);
            TypeInfo<K> dbType = this.getTypeManager().getType(EntityProxy.getClass(this.key));
            for (index = 0; index < numParams; ++index) {
                dbType.getLogicalType().putToDatabase(this.manager, stmt, index + 1, this.key, dbType.getJdbcWriteType());
            }
            int newLength = numParams + (thisPolyNames == null ? 0 : thisPolyNames.length);
            String typeValue = this.manager.getPolymorphicTypeMapper().convert(this.entityInfo.getEntityType());
            while (index < newLength) {
                stmt.setString(index + 1, typeValue);
                ++index;
            }
            dbType = Common.getPrimaryKeyType(provider.getTypeManager(), finalType);
            res = stmt.executeQuery();
            while (res.next()) {
                RawEntity<?> returnValue = dbType.getLogicalType().pullFromDatabase(this.manager, res, type, returnField);
                Class<Object> backType = finalType;
                for (String polyName : resPolyNames) {
                    typeValue = res.getString(polyName);
                    if (typeValue == null) continue;
                    backType = this.manager.getPolymorphicTypeMapper().invert(finalType, typeValue);
                    break;
                }
                if (backType.equals(this.entityInfo.getEntityType()) && returnValue.equals(this.key)) continue;
                V returnValueEntity = this.manager.peer(this.manager.resolveEntityInfo(backType), returnValue);
                EntityProxy proxy = this.manager.getProxyForEntity(returnValueEntity);
                if (selectFields.contains("*")) {
                    selectFields.remove("*");
                    selectFields.addAll((Collection<String>)Common.getValueFieldsNames(this.manager.resolveEntityInfo(finalType), this.getFieldNameConverter()));
                }
                proxy.lockValuesDirty.lock();
                try {
                    for (String field : selectFields) {
                        if (resPolyNames.contains(field)) continue;
                        proxy.values.put(field, res.getObject(field));
                    }
                }
                finally {
                    proxy.lockValuesDirty.unlock();
                }
                back.add(returnValueEntity);
            }
        }
        catch (Throwable throwable) {
            SqlUtils.closeQuietly(res, stmt, conn);
            throw throwable;
        }
        SqlUtils.closeQuietly(res, stmt, conn);
        return back.toArray((RawEntity[])Array.newInstance(finalType, back.size()));
    }

    private TypeManager getTypeManager() {
        return this.manager.getProvider().getTypeManager();
    }

    private static <O> Class<O> getClass(O object) {
        return object.getClass();
    }

    private <V> V convertValue(ResultSet res, String field, String polyName, Class<V> type) throws SQLException {
        TypeInfo<V> databaseType;
        if (this.isNull(res, field)) {
            return null;
        }
        if (polyName != null) {
            Class<Object> entityType = type;
            type = entityType = this.manager.getPolymorphicTypeMapper().invert(entityType, res.getString(polyName));
        }
        if ((databaseType = this.getTypeManager().getType(type)) == null) {
            throw new RuntimeException("UnrecognizedType: " + type.toString());
        }
        return databaseType.getLogicalType().pullFromDatabase(this.manager, res, type, field);
    }

    private String prepareParam(String name) {
        DatabaseProvider provider = this.manager.getProvider();
        String paramName = provider.processID(name);
        String paramValue = this.values.containsKey(name) ? "?" : "NULL";
        return String.format("%s = %s", paramName, paramValue);
    }

    private boolean isNull(ResultSet res, String field) throws SQLException {
        res.getObject(field);
        return res.wasNull();
    }

    private boolean instanceOf(Object value, Class<?> type) {
        if (value == null) {
            return true;
        }
        if (type.isPrimitive()) {
            if (type.equals(Boolean.TYPE)) {
                return this.instanceOf(value, Boolean.class);
            }
            if (type.equals(Character.TYPE)) {
                return this.instanceOf(value, Character.class);
            }
            if (type.equals(Byte.TYPE)) {
                return this.instanceOf(value, Byte.class);
            }
            if (type.equals(Short.TYPE)) {
                return this.instanceOf(value, Short.class);
            }
            if (type.equals(Integer.TYPE)) {
                return this.instanceOf(value, Integer.class);
            }
            if (type.equals(Long.TYPE)) {
                return this.instanceOf(value, Long.class);
            }
            if (type.equals(Float.TYPE)) {
                return this.instanceOf(value, Float.class);
            }
            if (type.equals(Double.TYPE)) {
                return this.instanceOf(value, Double.class);
            }
        } else {
            return type.isInstance(value);
        }
        return false;
    }

    private boolean isBigDecimal(Object value, Class<?> type) {
        return value instanceof BigDecimal && (this.isInteger(type) || this.isLong(type) || this.isFloat(type) || this.isDouble(type));
    }

    private Object handleBigDecimal(Object value, Class<?> type) {
        BigDecimal bd = (BigDecimal)value;
        if (this.isInteger(type)) {
            return bd.intValue();
        }
        if (this.isLong(type)) {
            return bd.longValue();
        }
        if (this.isFloat(type)) {
            return Float.valueOf(bd.floatValue());
        }
        if (this.isDouble(type)) {
            return bd.doubleValue();
        }
        throw new RuntimeException("Could not resolve actual type for object :" + value + ", expected type is " + type);
    }

    private boolean isDouble(Class<?> type) {
        return type.equals(Double.TYPE) || type.equals(Double.class);
    }

    private boolean isFloat(Class<?> type) {
        return type.equals(Float.TYPE) || type.equals(Float.class);
    }

    private boolean isLong(Class<?> type) {
        return type.equals(Long.TYPE) || type.equals(Long.class);
    }

    private boolean isInteger(Class<?> type) {
        return type.equals(Integer.TYPE) || type.equals(Integer.class);
    }
}


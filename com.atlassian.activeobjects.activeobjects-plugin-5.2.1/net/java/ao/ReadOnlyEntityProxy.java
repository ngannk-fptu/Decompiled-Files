/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.EntityManager;
import net.java.ao.ImplementationWrapper;
import net.java.ao.MethodImplWrapper;
import net.java.ao.RawEntity;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.FieldInfo;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;

public class ReadOnlyEntityProxy<T extends RawEntity<K>, K>
implements InvocationHandler {
    private static final String UNDEFINED_COMPARISION_RESULT = "Cannot compare two entities with null key - undefined behaviour";
    private final K key;
    private final EntityInfo<T, K> entityInfo;
    private final EntityManager manager;
    private ImplementationWrapper<T> implementation;
    private final Map<String, Object> values = new HashMap<String, Object>();

    public ReadOnlyEntityProxy(EntityManager manager, EntityInfo<T, K> entityInfo, K key) {
        this.manager = manager;
        this.entityInfo = entityInfo;
        this.key = key;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
        if (methodName.equals("save")) {
            throw new RuntimeException("'save' method called on a read-only entity of type " + this.entityInfo.getEntityType().getSimpleName());
        }
        if (this.implementation == null) {
            this.implementation = new ImplementationWrapper();
            this.implementation.init((RawEntity)proxy);
        }
        if (!((methodImpl = this.implementation.getMethod(methodName, method.getParameterTypes())) == null || Object.class.equals(declaringClass = methodImpl.getMethod().getDeclaringClass()) || (callingClassName = Common.getCallingClassName(1)) != null && callingClassName.equals(declaringClass.getName()))) {
            return methodImpl.getMethod().invoke(methodImpl.getInstance(), args);
        }
        if (methodName.equals("getEntityManager")) {
            return this.getManager();
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
        if (this.entityInfo.hasAccessor(method)) {
            return this.invokeGetter((RawEntity)proxy, this.getKey(), this.entityInfo.getField(method).getName(), method.getReturnType());
        }
        if (this.entityInfo.hasMutator(method)) {
            throw new RuntimeException("Setter method called on a read-only entity of type " + this.entityInfo.getEntityType().getSimpleName() + ": " + methodName);
        }
        return null;
    }

    public void addValue(String fieldName, ResultSet res) throws SQLException {
        FieldInfo fieldInfo = this.entityInfo.getField(fieldName);
        Class type = fieldInfo.getJavaType();
        String polyName = fieldInfo.getPolymorphicName();
        Object value = this.convertValue(res, fieldName, polyName, type);
        this.values.put(fieldName, value);
    }

    public K getKey() {
        return this.key;
    }

    public int hashCodeImpl() {
        return (Objects.hashCode(this.key) + this.entityInfo.hashCode()) % 65536;
    }

    public boolean equalsImpl(RawEntity<K> proxy, Object obj) {
        if (proxy == obj) {
            return true;
        }
        if (obj instanceof RawEntity) {
            RawEntity entity = (RawEntity)obj;
            String ourTableName = this.getTableNameConverter().getName(proxy.getEntityType());
            String theirTableName = this.getTableNameConverter().getName(entity.getEntityType());
            Object objectKey = Common.getPrimaryKeyValue(entity);
            if (objectKey == null && this.key == null) {
                throw new IllegalArgumentException(UNDEFINED_COMPARISION_RESULT);
            }
            return Objects.equals(objectKey, this.key) && theirTableName.equals(ourTableName);
        }
        return false;
    }

    private TableNameConverter getTableNameConverter() {
        return this.getManager().getNameConverters().getTableNameConverter();
    }

    public String toStringImpl() {
        return this.entityInfo.getName() + " {" + this.entityInfo.getPrimaryKey().getName() + " = " + String.valueOf(this.key) + "}";
    }

    private FieldNameConverter getFieldNameConverter() {
        return this.getManager().getNameConverters().getFieldNameConverter();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ReadOnlyEntityProxy) {
            ReadOnlyEntityProxy proxy = (ReadOnlyEntityProxy)obj;
            if (proxy.entityInfo.equals(this.entityInfo)) {
                if (proxy.key == null && this.key == null) {
                    throw new IllegalArgumentException(UNDEFINED_COMPARISION_RESULT);
                }
                return Objects.equals(proxy.key, this.key);
            }
        }
        return false;
    }

    public int hashCode() {
        return this.hashCodeImpl();
    }

    Class<T> getType() {
        return this.entityInfo.getEntityType();
    }

    private EntityManager getManager() {
        return this.manager;
    }

    private <V> V invokeGetter(RawEntity<?> entity, K key, String name, Class<V> type) throws Throwable {
        Object value = this.values.get(name);
        if (this.instanceOf(value, type)) {
            return (V)this.handleNullReturn(value, type);
        }
        if (this.isBigDecimal(value, type)) {
            return (V)this.handleBigDecimal(value, type);
        }
        return this.handleNullReturn(null, type);
    }

    private <V> V convertValue(ResultSet res, String field, String polyName, Class<V> type) throws SQLException {
        TypeManager manager;
        TypeInfo<V> databaseType;
        if (this.isNull(res, field)) {
            return null;
        }
        if (polyName != null) {
            Class<Object> entityType = type;
            type = entityType = this.getManager().getPolymorphicTypeMapper().invert(entityType, res.getString(polyName));
        }
        if ((databaseType = (manager = this.getTypeManager()).getType(type)) == null) {
            throw new RuntimeException("UnrecognizedType: " + type.toString());
        }
        return databaseType.getLogicalType().pullFromDatabase(this.getManager(), res, type, field);
    }

    private TypeManager getTypeManager() {
        return this.getManager().getProvider().getTypeManager();
    }

    private boolean isNull(ResultSet res, String field) throws SQLException {
        res.getObject(field);
        return res.wasNull();
    }

    private <V> V handleNullReturn(V back, Class<V> type) {
        if (back != null) {
            return back;
        }
        if (type.isPrimitive()) {
            if (type.equals(Boolean.TYPE)) {
                return (V)Boolean.FALSE;
            }
            if (type.equals(Character.TYPE)) {
                return (V)new Character(' ');
            }
            if (type.equals(Integer.TYPE)) {
                return (V)new Integer(0);
            }
            if (type.equals(Short.TYPE)) {
                return (V)new Short("0");
            }
            if (type.equals(Long.TYPE)) {
                return (V)new Long("0");
            }
            if (type.equals(Float.TYPE)) {
                return (V)new Float("0");
            }
            if (type.equals(Double.TYPE)) {
                return (V)new Double("0");
            }
            if (type.equals(Byte.TYPE)) {
                return (V)new Byte("0");
            }
        }
        return null;
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
        if (!(value instanceof BigDecimal)) {
            return false;
        }
        return type.equals(Integer.TYPE) || type.equals(Long.TYPE) || type.equals(Float.TYPE) || type.equals(Double.TYPE);
    }

    private Object handleBigDecimal(Object value, Class<?> type) {
        BigDecimal bd = (BigDecimal)value;
        if (type.equals(Integer.TYPE)) {
            return bd.intValue();
        }
        if (type.equals(Long.TYPE)) {
            return bd.longValue();
        }
        if (type.equals(Float.TYPE)) {
            return Float.valueOf(bd.floatValue());
        }
        if (type.equals(Double.TYPE)) {
            return bd.doubleValue();
        }
        throw new RuntimeException("Could not resolve actual type for object :" + value + ", expected type is " + type);
    }
}


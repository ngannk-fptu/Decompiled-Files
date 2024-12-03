/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.Validate
 */
package net.java.ao;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import net.java.ao.Accessor;
import net.java.ao.ActiveObjectsException;
import net.java.ao.AnnotationDelegate;
import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.java.ao.EntityProxyAccessor;
import net.java.ao.ManyToMany;
import net.java.ao.MethodFinder;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.OneToOne;
import net.java.ao.Polymorphic;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.Searchable;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.FieldNameProcessor;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.FieldInfo;
import net.java.ao.sql.SqlUtils;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;
import net.java.ao.util.StringUtils;
import org.apache.commons.lang3.Validate;

public final class Common {
    public static <T extends RawEntity<K>, K> T createPeer(EntityManager manager, Class<T> type, K key) throws SQLException {
        return manager.peer(manager.resolveEntityInfo(type), key);
    }

    public static String convertSimpleClassName(String name) {
        String[] array = name.split("\\.");
        return array[array.length - 1];
    }

    public static String convertDowncaseName(String name) {
        StringBuilder back = new StringBuilder();
        back.append(Character.toLowerCase(name.charAt(0)));
        back.append(name.substring(1));
        return back.toString();
    }

    @Deprecated
    public static String[] getMappingFields(FieldNameConverter converter, Class<? extends RawEntity<?>> from, Class<? extends RawEntity<?>> to) {
        LinkedHashSet<String> back = new LinkedHashSet<String>();
        for (Method method : from.getMethods()) {
            Class<? extends RawEntity<?>> attributeType = Common.getAttributeTypeFromMethod(method);
            if (attributeType == null) continue;
            if (to.isAssignableFrom(attributeType)) {
                back.add(converter.getName(method));
                continue;
            }
            if (attributeType.getAnnotation(Polymorphic.class) == null || !attributeType.isAssignableFrom(to)) continue;
            back.add(converter.getName(method));
        }
        return back.toArray(new String[back.size()]);
    }

    @Deprecated
    public static String[] getPolymorphicFieldNames(FieldNameConverter converter, Class<? extends RawEntity<?>> from, Class<? extends RawEntity<?>> to) {
        LinkedHashSet<String> back = new LinkedHashSet<String>();
        for (Method method : from.getMethods()) {
            Class<? extends RawEntity<?>> attributeType = Common.getAttributeTypeFromMethod(method);
            if (attributeType == null || !attributeType.isAssignableFrom(to) || attributeType.getAnnotation(Polymorphic.class) == null) continue;
            back.add(converter.getPolyTypeName(method));
        }
        return back.toArray(new String[back.size()]);
    }

    @Deprecated
    public static AnnotationDelegate getAnnotationDelegate(FieldNameConverter converter, Method method) {
        return new AnnotationDelegate(method, Common.findCounterpart(converter, method));
    }

    private static Method findCounterpart(FieldNameConverter converter, Method method) {
        return MethodFinder.getInstance().findCounterPartMethod(converter, method);
    }

    public static boolean isMutator(Method method) {
        return (Common.isAnnotatedMutator(method) || Common.isNamedAsSetter(method)) && Common.isValidMutator(method);
    }

    public static boolean isAnnotatedMutator(Method method) {
        return method.isAnnotationPresent(Mutator.class);
    }

    private static boolean isNamedAsSetter(Method method) {
        return method.getName().startsWith("set");
    }

    private static boolean isValidMutator(Method method) {
        return method.getReturnType() == Void.TYPE && method.getParameterTypes().length == 1;
    }

    public static boolean isAccessor(Method method) {
        return (Common.isAnnotatedAccessor(method) || Common.isNamedAsGetter(method)) && Common.isValidAccessor(method);
    }

    private static boolean isAnnotatedAccessor(Method method) {
        return method.isAnnotationPresent(Accessor.class);
    }

    private static boolean isNamedAsGetter(Method method) {
        return method.getName().startsWith("get") || method.getName().startsWith("is");
    }

    private static boolean isValidAccessor(Method method) {
        return method.getReturnType() != Void.TYPE && method.getParameterTypes().length == 0;
    }

    public static boolean isMutatorOrAccessor(Method method) {
        return Common.isMutator(method) || Common.isAccessor(method);
    }

    public static boolean isAnnotatedAsRelational(Method method) {
        return method.isAnnotationPresent(OneToOne.class) || method.isAnnotationPresent(OneToMany.class) || method.isAnnotationPresent(ManyToMany.class);
    }

    public static Class<?> getAttributeTypeFromMethod(Method method) {
        if (Common.isAnnotatedAsRelational(method)) {
            return null;
        }
        if (Common.isMutator(method)) {
            return Common.getMutatorParameterType(method);
        }
        if (Common.isAccessor(method)) {
            return Common.getAccessorReturnType(method);
        }
        return null;
    }

    private static Class<?> getMutatorParameterType(Method method) {
        Validate.isTrue((boolean)Common.isValidMutator(method), (String)"Method '%s' on class '%s' is not a valid mutator", (Object[])new Object[]{method.getName(), method.getDeclaringClass().getCanonicalName()});
        return method.getParameterTypes()[0];
    }

    private static Class<?> getAccessorReturnType(Method method) {
        Validate.isTrue((boolean)Common.isValidAccessor(method), (String)"Method '%s' on class '%s' is not a valid accessor", (Object[])new Object[]{method.getName(), method.getDeclaringClass().getCanonicalName()});
        return method.getReturnType();
    }

    public static String getCallingClassName(int depth) {
        StackTraceElement[] stack = new Exception().getStackTrace();
        return stack[depth + 2].getClassName();
    }

    public static List<String> getSearchableFields(EntityManager manager, Class<? extends RawEntity<?>> type) {
        ArrayList<String> back = new ArrayList<String>();
        for (Method m : type.getMethods()) {
            Searchable annot = Common.getAnnotationDelegate(manager.getNameConverters().getFieldNameConverter(), m).getAnnotation(Searchable.class);
            if (annot == null) continue;
            Class<?> attributeType = Common.getAttributeTypeFromMethod(m);
            String name = manager.getNameConverters().getFieldNameConverter().getName(m);
            if (name == null || RawEntity.class.isAssignableFrom(attributeType) || back.contains(name)) continue;
            back.add(name);
        }
        return back;
    }

    private static Method getPrimaryKeyAccessor(Class<? extends RawEntity<?>> type) {
        Iterable<Method> methods = MethodFinder.getInstance().findAnnotatedMethods(PrimaryKey.class, type);
        if (Iterables.isEmpty(methods)) {
            throw new RuntimeException("Entity " + type.getSimpleName() + " has no primary key field");
        }
        for (Method method : methods) {
            if (method.getReturnType().equals(Void.TYPE) || method.getParameterTypes().length != 0) continue;
            return method;
        }
        return null;
    }

    public static String getPrimaryKeyField(Class<? extends RawEntity<?>> type, FieldNameConverter converter) {
        Iterable<Method> methods = MethodFinder.getInstance().findAnnotatedMethods(PrimaryKey.class, type);
        if (Iterables.isEmpty(methods)) {
            throw new RuntimeException("Entity " + type.getSimpleName() + " has no primary key field");
        }
        return converter.getName(methods.iterator().next());
    }

    public static <K> TypeInfo<K> getPrimaryKeyType(TypeManager typeManager, Class<? extends RawEntity<K>> type) {
        return typeManager.getType(Common.getPrimaryKeyClassType(type));
    }

    public static <K> Class<K> getPrimaryKeyClassType(Class<? extends RawEntity<K>> type) {
        Iterable<Method> methods = MethodFinder.getInstance().findAnnotatedMethods(PrimaryKey.class, type);
        if (Iterables.isEmpty(methods)) {
            throw new RuntimeException("Entity " + type.getSimpleName() + " has no primary key field");
        }
        Method m = methods.iterator().next();
        Class<?> keyType = m.getReturnType();
        if (keyType.equals(Void.TYPE)) {
            keyType = m.getParameterTypes()[0];
        }
        return keyType;
    }

    public static <K> K getPrimaryKeyValue(RawEntity<K> entity) {
        if (entity instanceof EntityProxyAccessor) {
            return ((EntityProxyAccessor)((Object)entity)).getEntityProxy().getKey();
        }
        try {
            return (K)Common.getPrimaryKeyAccessor(entity.getEntityType()).invoke(entity, new Object[0]);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }
        catch (InvocationTargetException e) {
            return null;
        }
    }

    public static <K> void validatePrimaryKey(FieldInfo<K> primaryKeyInfo, Object value) {
        if (null == value) {
            throw new IllegalArgumentException("Cannot set primary key to NULL");
        }
        TypeInfo<K> typeInfo = primaryKeyInfo.getTypeInfo();
        Class<K> javaTypeClass = primaryKeyInfo.getJavaType();
        if (!typeInfo.isAllowedAsPrimaryKey()) {
            throw new ActiveObjectsException(javaTypeClass.getName() + " cannot be used as a primary key!");
        }
        typeInfo.getLogicalType().validate(value);
        if (value instanceof String && StringUtils.isBlank((String)value)) {
            throw new ActiveObjectsException("Cannot set primary key to blank String");
        }
    }

    public static boolean fuzzyCompare(TypeManager typeManager, Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        Object array = null;
        Object other = null;
        if (a.getClass().isArray()) {
            array = a;
            other = b;
        } else if (b.getClass().isArray()) {
            array = b;
            other = a;
        }
        if (array != null) {
            for (int i = 0; i < Array.getLength(array); ++i) {
                if (!Common.fuzzyCompare(typeManager, Array.get(array, i), other)) continue;
                return true;
            }
        }
        return typeManager.getType(a.getClass()).getLogicalType().valueEquals(a, b) || typeManager.getType(b.getClass()).getLogicalType().valueEquals(b, a);
    }

    public static boolean fuzzyTypeCompare(int typeA, int typeB) {
        if (typeA == 16) {
            switch (typeB) {
                case -5: {
                    return true;
                }
                case -7: {
                    return true;
                }
                case 4: {
                    return true;
                }
                case 2: {
                    return true;
                }
                case 5: {
                    return true;
                }
                case -6: {
                    return true;
                }
            }
        }
        if ((typeA == -5 || typeA == -7 || typeA == 4 || typeA == 2 || typeA == 5 || typeA == -6) && typeB == 16) {
            return true;
        }
        if (typeA == 2005 && (typeB == -1 || typeB == -16 || typeB == 12)) {
            return true;
        }
        if ((typeA == -1 || typeA == 12) && typeB == 2005) {
            return true;
        }
        if ((typeA == -5 || typeA == -7 || typeA == 3 || typeA == 8 || typeA == 6 || typeA == 4 || typeA == 7 || typeA == 5 || typeA == -6) && typeB == 2) {
            return true;
        }
        if (typeA == 12 && typeB == -9) {
            return true;
        }
        return typeA == typeB;
    }

    public static Set<Method> getValueFieldsMethods(Class<? extends RawEntity<?>> entity, final FieldNameConverter converter) {
        return Sets.filter((Set)Sets.newHashSet((Object[])entity.getMethods()), (Predicate)new Predicate<Method>(){

            public boolean apply(Method m) {
                AnnotationDelegate annotations = Common.getAnnotationDelegate(converter, m);
                return !annotations.isAnnotationPresent(Ignore.class) && !annotations.isAnnotationPresent(OneToOne.class) && !annotations.isAnnotationPresent(OneToMany.class) && !annotations.isAnnotationPresent(ManyToMany.class);
            }
        });
    }

    public static ImmutableSet<String> getValueFieldsNames(EntityInfo<? extends RawEntity<?>, ?> entityInfo, FieldNameConverter converter) {
        ArrayList<String> valueFieldsNames = new ArrayList<String>();
        for (FieldInfo fieldInfo : entityInfo.getFields()) {
            if (Entity.class.isAssignableFrom(fieldInfo.getJavaType()) || !fieldInfo.hasAccessor()) continue;
            valueFieldsNames.add(converter.getName(fieldInfo.getAccessor()));
        }
        return ImmutableSet.copyOf(valueFieldsNames);
    }

    public static List<String> preloadValue(Preload preload, final FieldNameConverter fnc) {
        ArrayList value = Lists.newArrayList((Object[])preload.value());
        if (fnc instanceof FieldNameProcessor) {
            return Lists.transform((List)value, (Function)new Function<String, String>(){

                public String apply(String from) {
                    return ((FieldNameProcessor)((Object)fnc)).convertName(from);
                }
            });
        }
        return value;
    }

    public static String where(OneToOne oneToOne, FieldNameConverter fnc) {
        return Common.where(oneToOne.where(), fnc);
    }

    public static String where(OneToMany oneToMany, FieldNameConverter fnc) {
        return Common.where(oneToMany.where(), fnc);
    }

    public static String where(ManyToMany manyToMany, FieldNameConverter fnc) {
        return Common.where(manyToMany.where(), fnc);
    }

    private static String where(String where, FieldNameConverter fnc) {
        if (fnc instanceof FieldNameProcessor) {
            Matcher matcher = SqlUtils.WHERE_CLAUSE.matcher(where);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, Common.convert(fnc, matcher.group(1)));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        return where;
    }

    public static String convert(FieldNameConverter fnc, String column) {
        if (fnc instanceof FieldNameProcessor) {
            return ((FieldNameProcessor)((Object)fnc)).convertName(column);
        }
        return column;
    }

    @Deprecated
    public static void closeQuietly(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    @Deprecated
    public static void closeQuietly(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    @Deprecated
    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    public static String shorten(String string, int length) {
        if (string == null || string.length() <= length) {
            return string;
        }
        int tailLength = length / 3;
        int prefixEndPosition = length - tailLength - 1;
        int hash = Math.abs((int)((long)string.hashCode() % Math.round(Math.pow(10.0, tailLength))));
        return string.substring(0, prefixEndPosition) + hash;
    }

    public static String prefix(String string, int length) {
        int tailLength = length / 3;
        int prefixEndPosition = length - tailLength - 1;
        if (string == null || prefixEndPosition > string.length()) {
            return string;
        }
        return string.substring(0, prefixEndPosition);
    }
}


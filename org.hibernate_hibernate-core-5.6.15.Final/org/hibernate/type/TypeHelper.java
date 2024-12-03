/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.type.CompositeType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

@Deprecated
public class TypeHelper {
    private TypeHelper() {
    }

    public static void deepCopy(Object[] values, Type[] types, boolean[] copy, Object[] target, SharedSessionContractImplementor session) {
        for (int i = 0; i < types.length; ++i) {
            if (!copy[i]) continue;
            target[i] = values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || values[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ? values[i] : types[i].deepCopy(values[i], session.getFactory());
        }
    }

    public static void beforeAssemble(Serializable[] row, Type[] types, SharedSessionContractImplementor session) {
        for (int i = 0; i < types.length; ++i) {
            if (row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN) continue;
            types[i].beforeAssemble(row[i], session);
        }
    }

    public static Object[] assemble(Serializable[] row, Type[] types, SharedSessionContractImplementor session, Object owner) {
        Object[] assembled = new Object[row.length];
        for (int i = 0; i < types.length; ++i) {
            assembled[i] = row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ? row[i] : types[i].assemble(row[i], session, owner);
        }
        return assembled;
    }

    public static Serializable[] disassemble(Object[] row, Type[] types, boolean[] nonCacheable, SharedSessionContractImplementor session, Object owner) {
        Serializable[] disassembled = new Serializable[row.length];
        for (int i = 0; i < row.length; ++i) {
            disassembled[i] = nonCacheable != null && nonCacheable[i] ? LazyPropertyInitializer.UNFETCHED_PROPERTY : (row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ? (Serializable)row[i] : types[i].disassemble(row[i], session, owner));
        }
        return disassembled;
    }

    public static Object[] replace(Object[] original, Object[] target, Type[] types, SharedSessionContractImplementor session, Object owner, Map copyCache) {
        Object[] copied = new Object[original.length];
        for (int i = 0; i < types.length; ++i) {
            copied[i] = original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ? target[i] : (target[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ? types[i].replace(original[i], null, session, owner, copyCache) : types[i].replace(original[i], target[i], session, owner, copyCache));
        }
        return copied;
    }

    public static Object[] replace(Object[] original, Object[] target, Type[] types, SharedSessionContractImplementor session, Object owner, Map copyCache, ForeignKeyDirection foreignKeyDirection) {
        Object[] copied = new Object[original.length];
        for (int i = 0; i < types.length; ++i) {
            copied[i] = original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ? target[i] : (target[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ? types[i].replace(original[i], null, session, owner, copyCache, foreignKeyDirection) : types[i].replace(original[i], target[i], session, owner, copyCache, foreignKeyDirection));
        }
        return copied;
    }

    public static Object[] replaceAssociations(Object[] original, Object[] target, Type[] types, SharedSessionContractImplementor session, Object owner, Map copyCache, ForeignKeyDirection foreignKeyDirection) {
        Object[] copied = new Object[original.length];
        for (int i = 0; i < types.length; ++i) {
            if (original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN) {
                copied[i] = target[i];
                continue;
            }
            if (types[i].isComponentType()) {
                CompositeType componentType = (CompositeType)types[i];
                Type[] subtypes = componentType.getSubtypes();
                Object[] origComponentValues = original[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues(original[i], session);
                Object[] targetComponentValues = target[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues(target[i], session);
                TypeHelper.replaceAssociations(origComponentValues, targetComponentValues, subtypes, session, null, copyCache, foreignKeyDirection);
                Object[] objects = TypeHelper.replaceAssociations(origComponentValues, targetComponentValues, subtypes, session, null, copyCache, foreignKeyDirection);
                if (componentType.isMutable() && target[i] != null && objects != null) {
                    componentType.setPropertyValues(target[i], objects, EntityMode.POJO);
                }
                copied[i] = target[i];
                continue;
            }
            copied[i] = !types[i].isAssociationType() ? target[i] : types[i].replace(original[i], target[i], session, owner, copyCache, foreignKeyDirection);
        }
        return copied;
    }

    @Deprecated
    public static int[] findDirty(NonIdentifierAttribute[] properties, Object[] currentState, Object[] previousState, boolean[][] includeColumns, boolean anyUninitializedProperties, SharedSessionContractImplementor session) {
        return TypeHelper.findDirty(properties, currentState, previousState, includeColumns, session);
    }

    public static int[] findDirty(NonIdentifierAttribute[] properties, Object[] currentState, Object[] previousState, boolean[][] includeColumns, SharedSessionContractImplementor session) {
        int[] results = null;
        int count = 0;
        int span = properties.length;
        for (int i = 0; i < span; ++i) {
            boolean dirty;
            boolean bl = dirty = currentState[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY && (previousState[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || properties[i].isDirtyCheckable() && properties[i].getType().isDirty(previousState[i], currentState[i], includeColumns[i], session));
            if (!dirty) continue;
            if (results == null) {
                results = new int[span];
            }
            results[count++] = i;
        }
        if (count == 0) {
            return null;
        }
        return ArrayHelper.trim(results, count);
    }

    @Deprecated
    public static int[] findModified(NonIdentifierAttribute[] properties, Object[] currentState, Object[] previousState, boolean[][] includeColumns, boolean[] includeProperties, boolean anyUninitializedProperties, SharedSessionContractImplementor session) {
        return TypeHelper.findModified(properties, currentState, previousState, includeColumns, includeProperties, session);
    }

    public static int[] findModified(NonIdentifierAttribute[] properties, Object[] currentState, Object[] previousState, boolean[][] includeColumns, boolean[] includeProperties, SharedSessionContractImplementor session) {
        int[] results = null;
        int count = 0;
        int span = properties.length;
        for (int i = 0; i < span; ++i) {
            boolean modified;
            boolean bl = modified = currentState[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY && includeProperties[i] && properties[i].isDirtyCheckable() && properties[i].getType().isModified(previousState[i], currentState[i], includeColumns[i], session);
            if (!modified) continue;
            if (results == null) {
                results = new int[span];
            }
            results[count++] = i;
        }
        if (count == 0) {
            return null;
        }
        int[] trimmed = new int[count];
        System.arraycopy(results, 0, trimmed, 0, count);
        return trimmed;
    }
}


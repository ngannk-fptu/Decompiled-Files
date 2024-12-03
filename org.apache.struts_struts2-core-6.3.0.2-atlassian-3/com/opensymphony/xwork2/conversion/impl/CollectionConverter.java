/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkList;
import com.opensymphony.xwork2.inject.Inject;
import java.lang.reflect.Member;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CollectionConverter
extends DefaultTypeConverter {
    private ObjectTypeDeterminer objectTypeDeterminer;

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer determiner) {
        this.objectTypeDeterminer = determiner;
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        Collection result;
        Class<String> memberType = String.class;
        if (target != null && (memberType = this.objectTypeDeterminer.getElementClass(target.getClass(), propertyName, null)) == null) {
            memberType = String.class;
        }
        if (toType.isAssignableFrom(value.getClass())) {
            result = (Collection)value;
        } else if (value.getClass().isArray()) {
            Object[] objArray = (Object[])value;
            TypeConverter converter = this.getTypeConverter(context);
            result = this.createCollection(toType, memberType, objArray.length);
            for (Object anObjArray : objArray) {
                Object convertedValue = converter.convertValue(context, target, member, propertyName, anObjArray, memberType);
                if (TypeConverter.NO_CONVERSION_POSSIBLE.equals(convertedValue)) continue;
                result.add(convertedValue);
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection col = (Collection)value;
            TypeConverter converter = this.getTypeConverter(context);
            result = this.createCollection(toType, memberType, col.size());
            for (Object aCol : col) {
                Object convertedValue = converter.convertValue(context, target, member, propertyName, aCol, memberType);
                if (TypeConverter.NO_CONVERSION_POSSIBLE.equals(convertedValue)) continue;
                result.add(convertedValue);
            }
        } else {
            result = this.createCollection(toType, memberType, -1);
            TypeConverter converter = this.getTypeConverter(context);
            Object convertedValue = converter.convertValue(context, target, member, propertyName, value, memberType);
            if (!TypeConverter.NO_CONVERSION_POSSIBLE.equals(convertedValue)) {
                result.add(convertedValue);
            }
        }
        return result;
    }

    private Collection createCollection(Class toType, Class memberType, int size) {
        AbstractCollection result = toType == Set.class ? (size > 0 ? new HashSet(size) : new HashSet()) : (toType == SortedSet.class ? new TreeSet() : (size > 0 ? new XWorkList(memberType, size) : new XWorkList(memberType)));
        return result;
    }
}


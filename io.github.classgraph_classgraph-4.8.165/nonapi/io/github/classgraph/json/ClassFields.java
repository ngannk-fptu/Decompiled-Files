/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import io.github.classgraph.ScanResult;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import nonapi.io.github.classgraph.json.ClassFieldCache;
import nonapi.io.github.classgraph.json.FieldTypeInfo;
import nonapi.io.github.classgraph.json.Id;
import nonapi.io.github.classgraph.json.JSONUtils;
import nonapi.io.github.classgraph.json.TypeResolutions;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

class ClassFields {
    final List<FieldTypeInfo> fieldOrder = new ArrayList<FieldTypeInfo>();
    final Map<String, FieldTypeInfo> fieldNameToFieldTypeInfo = new HashMap<String, FieldTypeInfo>();
    Field idField;
    private static final Comparator<Field> FIELD_NAME_ORDER_COMPARATOR = new Comparator<Field>(){

        @Override
        public int compare(Field a, Field b) {
            return a.getName().compareTo(b.getName());
        }
    };
    private static final Comparator<Field> SERIALIZATION_FORMAT_FIELD_NAME_ORDER_COMPARATOR = new Comparator<Field>(){

        @Override
        public int compare(Field a, Field b) {
            return a.getName().equals("format") ? -1 : (b.getName().equals("format") ? 1 : a.getName().compareTo(b.getName()));
        }
    };
    private static final String SERIALIZATION_FORMAT_CLASS_NAME = ScanResult.class.getName() + "$SerializationFormat";

    public ClassFields(Class<?> cls, boolean resolveTypes, boolean onlySerializePublicFields, ClassFieldCache classFieldCache, ReflectionUtils reflectionUtils) {
        HashSet<String> visibleFieldNames = new HashSet<String>();
        ArrayList fieldSuperclassReversedOrder = new ArrayList();
        TypeResolutions currTypeResolutions = null;
        Type currType = cls;
        while (currType != Object.class && currType != null) {
            Class currRawType;
            if (currType instanceof ParameterizedType) {
                ParameterizedType currParameterizedType = (ParameterizedType)currType;
                currRawType = (Class)currParameterizedType.getRawType();
            } else if (currType instanceof Class) {
                currRawType = currType;
            } else {
                throw new IllegalArgumentException("Illegal class type: " + currType);
            }
            Field[] fields = currRawType.getDeclaredFields();
            Arrays.sort(fields, cls.getName().equals(SERIALIZATION_FORMAT_CLASS_NAME) ? SERIALIZATION_FORMAT_FIELD_NAME_ORDER_COMPARATOR : FIELD_NAME_ORDER_COMPARATOR);
            ArrayList<FieldTypeInfo> fieldOrderWithinClass = new ArrayList<FieldTypeInfo>();
            for (Field field : fields) {
                if (!visibleFieldNames.add(field.getName())) continue;
                boolean isIdField = field.isAnnotationPresent(Id.class);
                if (isIdField) {
                    if (this.idField != null) {
                        throw new IllegalArgumentException("More than one @Id annotation: " + this.idField.getDeclaringClass() + "." + this.idField + " ; " + currRawType.getName() + "." + field.getName());
                    }
                    this.idField = field;
                }
                if (JSONUtils.fieldIsSerializable(field, onlySerializePublicFields, reflectionUtils)) {
                    Type fieldGenericType = field.getGenericType();
                    Type fieldTypePartiallyResolved = currTypeResolutions != null && resolveTypes ? currTypeResolutions.resolveTypeVariables(fieldGenericType) : fieldGenericType;
                    FieldTypeInfo fieldTypeInfo = new FieldTypeInfo(field, fieldTypePartiallyResolved, classFieldCache);
                    this.fieldNameToFieldTypeInfo.put(field.getName(), fieldTypeInfo);
                    fieldOrderWithinClass.add(fieldTypeInfo);
                    continue;
                }
                if (!isIdField) continue;
                throw new IllegalArgumentException("@Id annotation field must be accessible, final, and non-transient: " + currRawType.getName() + "." + field.getName());
            }
            fieldSuperclassReversedOrder.add(fieldOrderWithinClass);
            Type genericSuperType = currRawType.getGenericSuperclass();
            if (resolveTypes) {
                if (genericSuperType instanceof ParameterizedType) {
                    Type resolvedSupertype = currTypeResolutions == null ? genericSuperType : currTypeResolutions.resolveTypeVariables(genericSuperType);
                    currTypeResolutions = resolvedSupertype instanceof ParameterizedType ? new TypeResolutions((ParameterizedType)resolvedSupertype) : null;
                    currType = resolvedSupertype;
                    continue;
                }
                if (genericSuperType instanceof Class) {
                    currType = genericSuperType;
                    currTypeResolutions = null;
                    continue;
                }
                throw new IllegalArgumentException("Got unexpected supertype " + genericSuperType);
            }
            currType = genericSuperType;
        }
        for (int i = fieldSuperclassReversedOrder.size() - 1; i >= 0; --i) {
            List fieldGroupingForClass = (List)fieldSuperclassReversedOrder.get(i);
            this.fieldOrder.addAll(fieldGroupingForClass);
        }
    }
}


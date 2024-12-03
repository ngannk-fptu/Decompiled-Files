/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.FieldInternal;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexWriterImpl;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodInternal;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.ModuleInfo;
import org.jboss.jandex.NameTable;
import org.jboss.jandex.PackedDataOutputStream;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.RecordComponentInternal;
import org.jboss.jandex.StrongInternPool;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.UnsupportedVersion;
import org.jboss.jandex.WildcardType;

final class IndexWriterV2
extends IndexWriterImpl {
    static final int MIN_VERSION = 6;
    static final int MAX_VERSION = 10;
    private static final int MAGIC = -1161945323;
    private static final byte NULL_TARGET_TAG = 0;
    private static final byte FIELD_TAG = 1;
    private static final byte METHOD_TAG = 2;
    private static final byte METHOD_PARAMETER_TAG = 3;
    private static final byte CLASS_TAG = 4;
    private static final byte EMPTY_TYPE_TAG = 5;
    private static final byte CLASS_EXTENDS_TYPE_TAG = 6;
    private static final byte TYPE_PARAMETER_TAG = 7;
    private static final byte TYPE_PARAMETER_BOUND_TAG = 8;
    private static final byte METHOD_PARAMETER_TYPE_TAG = 9;
    private static final byte THROWS_TYPE_TAG = 10;
    private static final byte RECORD_COMPONENT_TAG = 11;
    private static final int AVALUE_BYTE = 1;
    private static final int AVALUE_SHORT = 2;
    private static final int AVALUE_INT = 3;
    private static final int AVALUE_CHAR = 4;
    private static final int AVALUE_FLOAT = 5;
    private static final int AVALUE_DOUBLE = 6;
    private static final int AVALUE_LONG = 7;
    private static final int AVALUE_BOOLEAN = 8;
    private static final int AVALUE_STRING = 9;
    private static final int AVALUE_CLASS = 10;
    private static final int AVALUE_ENUM = 11;
    private static final int AVALUE_ARRAY = 12;
    private static final int AVALUE_NESTED = 13;
    private static final int HAS_ENCLOSING_METHOD = 1;
    private static final int NO_ENCLOSING_METHOD = 0;
    private static final int NO_NESTING = 0;
    private static final int HAS_NESTING = 1;
    private final OutputStream out;
    private NameTable names;
    private HashMap<DotName, Integer> nameTable;
    private TreeMap<String, DotName> sortedNameTable;
    private ReferenceTable<AnnotationInstance> annotationTable;
    private ReferenceTable<Type> typeTable;
    private ReferenceTable<Type[]> typeListTable;

    IndexWriterV2(OutputStream out) {
        this.out = out;
    }

    @Override
    int write(Index index, int version) throws IOException {
        if (version < 6 || version > 10) {
            throw new UnsupportedVersion("Can't write index version " + version + "; this IndexWriterV2 only supports index versions " + 6 + "-" + 10);
        }
        PackedDataOutputStream stream = new PackedDataOutputStream(new BufferedOutputStream(this.out));
        stream.writeInt(-1161945323);
        stream.writeByte(version);
        stream.writePackedU32(index.annotations.size());
        stream.writePackedU32(index.implementors.size());
        stream.writePackedU32(index.subclasses.size());
        if (version >= 10) {
            stream.writePackedU32(index.users.size());
        }
        this.buildTables(index, version);
        this.writeByteTable(stream);
        this.writeStringTable(stream);
        this.writeNameTable(stream);
        stream.writePackedU32(this.typeTable.size());
        stream.writePackedU32(this.typeListTable.size());
        stream.writePackedU32(this.annotationTable.size());
        this.writeTypeTable(stream);
        this.writeTypeListTable(stream);
        if (version >= 10) {
            this.writeUsersTable(stream, index.users);
        }
        this.writeMethodTable(stream, version);
        this.writeFieldTable(stream);
        if (version >= 10) {
            this.writeRecordComponentTable(stream);
        }
        this.writeClasses(stream, index, version);
        if (version >= 10) {
            this.writeModules(stream, index, version);
        }
        stream.flush();
        return stream.size();
    }

    private void writeUsersTable(PackedDataOutputStream stream, Map<DotName, List<ClassInfo>> users) throws IOException {
        for (Map.Entry<DotName, List<ClassInfo>> entry : users.entrySet()) {
            this.writeUsersSet(stream, entry.getKey(), entry.getValue());
        }
    }

    private void writeUsersSet(PackedDataOutputStream stream, DotName user, List<ClassInfo> uses) throws IOException {
        stream.writePackedU32(this.positionOf(user));
        stream.writePackedU32(uses.size());
        for (ClassInfo use : uses) {
            stream.writePackedU32(this.positionOf(use.name()));
        }
    }

    private void writeStringTable(PackedDataOutputStream stream) throws IOException {
        StrongInternPool<String> stringPool = this.names.stringPool();
        stream.writePackedU32(stringPool.size());
        Iterator<String> iterator = stringPool.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            stream.writeUTF(string);
        }
    }

    private void writeByteTable(PackedDataOutputStream stream) throws IOException {
        StrongInternPool<byte[]> bytePool = this.names.bytePool();
        stream.writePackedU32(bytePool.size());
        Iterator<byte[]> iterator = bytePool.iterator();
        while (iterator.hasNext()) {
            byte[] bytes = iterator.next();
            stream.writePackedU32(bytes.length);
            stream.write(bytes);
        }
    }

    private void writeTypeTable(PackedDataOutputStream stream) throws IOException {
        List<Type> types = this.typeTable.list();
        for (Type type : types) {
            this.writeTypeEntry(stream, type);
        }
    }

    private void writeTypeListTable(PackedDataOutputStream stream) throws IOException {
        List<Type[]> typeLists = this.typeListTable.list();
        for (Type[] types : typeLists) {
            if (!this.markWritten(types)) continue;
            this.writeTypeListEntry(stream, types);
        }
    }

    private void writeTypeListEntry(PackedDataOutputStream stream, Type[] types) throws IOException {
        stream.writePackedU32(types.length);
        for (Type type : types) {
            stream.writePackedU32(this.positionOf(type));
        }
    }

    private void writeMethodTable(PackedDataOutputStream stream, int version) throws IOException {
        StrongInternPool<MethodInternal> methodPool = this.names.methodPool();
        stream.writePackedU32(methodPool.size());
        Iterator<MethodInternal> iterator = methodPool.iterator();
        while (iterator.hasNext()) {
            this.writeMethodEntry(stream, version, iterator.next());
        }
    }

    private void writeFieldTable(PackedDataOutputStream stream) throws IOException {
        StrongInternPool<FieldInternal> fieldPool = this.names.fieldPool();
        stream.writePackedU32(fieldPool.size());
        Iterator<FieldInternal> iterator = fieldPool.iterator();
        while (iterator.hasNext()) {
            this.writeFieldEntry(stream, iterator.next());
        }
    }

    private void writeRecordComponentTable(PackedDataOutputStream stream) throws IOException {
        StrongInternPool<RecordComponentInternal> recordComponentPool = this.names.recordComponentPool();
        stream.writePackedU32(recordComponentPool.size());
        Iterator<RecordComponentInternal> iterator = recordComponentPool.iterator();
        while (iterator.hasNext()) {
            this.writeRecordComponentEntry(stream, iterator.next());
        }
    }

    private void writeFieldEntry(PackedDataOutputStream stream, FieldInternal field) throws IOException {
        stream.writePackedU32(this.positionOf(field.nameBytes()));
        stream.writePackedU32(field.flags());
        stream.writePackedU32(this.positionOf(field.type()));
        AnnotationInstance[] annotations = field.annotationArray();
        stream.writePackedU32(annotations.length);
        for (AnnotationInstance annotation : annotations) {
            this.writeReferenceOrFull(stream, annotation);
        }
    }

    private void writeRecordComponentEntry(PackedDataOutputStream stream, RecordComponentInternal recordComponent) throws IOException {
        stream.writePackedU32(this.positionOf(recordComponent.nameBytes()));
        stream.writePackedU32(this.positionOf(recordComponent.type()));
        AnnotationInstance[] annotations = recordComponent.annotationArray();
        stream.writePackedU32(annotations.length);
        for (AnnotationInstance annotation : annotations) {
            this.writeReferenceOrFull(stream, annotation);
        }
    }

    private void writeMethodEntry(PackedDataOutputStream stream, int version, MethodInternal method) throws IOException {
        stream.writePackedU32(this.positionOf(method.nameBytes()));
        stream.writePackedU32(method.flags());
        stream.writePackedU32(this.positionOf(method.typeParameterArray()));
        Type receiverType = method.receiverTypeField();
        stream.writePackedU32(receiverType == null ? 0 : this.positionOf(receiverType));
        stream.writePackedU32(this.positionOf(method.returnType()));
        stream.writePackedU32(this.positionOf(method.parameterArray()));
        stream.writePackedU32(this.positionOf(method.exceptionArray()));
        if (version >= 7) {
            AnnotationValue defaultValue = method.defaultValue();
            stream.writeByte(defaultValue != null ? 1 : 0);
            if (defaultValue != null) {
                this.writeAnnotationValue(stream, defaultValue);
            }
        }
        if (version >= 8) {
            byte[][] parameterNamesBytes = method.parameterNamesBytes();
            stream.writePackedU32(parameterNamesBytes.length);
            for (byte[] parameterName : parameterNamesBytes) {
                stream.writePackedU32(this.positionOf(parameterName));
            }
        }
        AnnotationInstance[] annotations = method.annotationArray();
        stream.writePackedU32(annotations.length);
        for (AnnotationInstance annotation : annotations) {
            this.writeReferenceOrFull(stream, annotation);
        }
    }

    private void writeAnnotation(PackedDataOutputStream stream, AnnotationInstance instance) throws IOException {
        stream.writePackedU32(this.positionOf(instance.name()));
        AnnotationTarget target = instance.target();
        this.writeAnnotationTarget(stream, target);
        this.writeAnnotationValues(stream, instance.values());
    }

    private void writeAnnotationTarget(PackedDataOutputStream stream, AnnotationTarget target) throws IOException {
        if (target instanceof FieldInfo) {
            stream.writeByte(1);
        } else if (target instanceof MethodInfo) {
            stream.writeByte(2);
        } else if (target instanceof MethodParameterInfo) {
            MethodParameterInfo param = (MethodParameterInfo)target;
            stream.writeByte(3);
            stream.writePackedU32(param.position());
        } else if (target instanceof ClassInfo) {
            stream.writeByte(4);
        } else if (target instanceof TypeTarget) {
            this.writeTypeTarget(stream, (TypeTarget)target);
        } else if (target instanceof RecordComponentInfo) {
            stream.writeByte(11);
        } else if (target == null) {
            stream.writeByte(0);
        } else {
            throw new IllegalStateException("Unknown target");
        }
    }

    private void writeTypeTarget(PackedDataOutputStream stream, TypeTarget typeTarget) throws IOException {
        switch (typeTarget.usage()) {
            case EMPTY: {
                this.writeTypeTargetFields(stream, (byte)5, typeTarget);
                stream.writeByte(typeTarget.asEmpty().isReceiver() ? 1 : 0);
                break;
            }
            case CLASS_EXTENDS: {
                this.writeTypeTargetFields(stream, (byte)6, typeTarget);
                stream.writePackedU32(typeTarget.asClassExtends().position());
                break;
            }
            case METHOD_PARAMETER: {
                this.writeTypeTargetFields(stream, (byte)9, typeTarget);
                stream.writePackedU32(typeTarget.asMethodParameterType().position());
                break;
            }
            case TYPE_PARAMETER: {
                this.writeTypeTargetFields(stream, (byte)7, typeTarget);
                stream.writePackedU32(typeTarget.asTypeParameter().position());
                break;
            }
            case TYPE_PARAMETER_BOUND: {
                this.writeTypeTargetFields(stream, (byte)8, typeTarget);
                stream.writePackedU32(typeTarget.asTypeParameterBound().position());
                stream.writePackedU32(typeTarget.asTypeParameterBound().boundPosition());
                break;
            }
            case THROWS: {
                this.writeTypeTargetFields(stream, (byte)10, typeTarget);
                stream.writePackedU32(typeTarget.asThrows().position());
            }
        }
    }

    private void writeTypeTargetFields(PackedDataOutputStream stream, byte tag, TypeTarget target) throws IOException {
        stream.writeByte(tag);
        Type type = target.target();
        stream.writePackedU32(type == null ? 0 : this.positionOf(type));
    }

    private void writeNameTable(PackedDataOutputStream stream) throws IOException {
        stream.writePackedU32(this.nameTable.size());
        int pos = 1;
        for (Map.Entry<String, DotName> entry : this.sortedNameTable.entrySet()) {
            this.nameTable.put(entry.getValue(), pos++);
            DotName name = entry.getValue();
            assert (name.isComponentized());
            int nameDepth = 0;
            for (DotName prefix = name.prefix(); prefix != null; prefix = prefix.prefix()) {
                ++nameDepth;
            }
            nameDepth = nameDepth << 1 | (name.isInner() ? 1 : 0);
            stream.writePackedU32(nameDepth);
            stream.writePackedU32(this.positionOf(name.local()));
        }
    }

    private int positionOf(String string) {
        int pos = this.names.positionOf(string);
        if (pos < 1) {
            throw new IllegalStateException("Intern tables incomplete");
        }
        return pos;
    }

    private int positionOf(byte[] bytes) {
        int pos = this.names.positionOf(bytes);
        if (pos < 1) {
            throw new IllegalStateException("Intern tables incomplete");
        }
        return pos;
    }

    private int positionOf(MethodInternal method) {
        int pos = this.names.positionOf(method);
        if (pos < 1) {
            throw new IllegalStateException("Intern tables incomplete");
        }
        return pos;
    }

    private int positionOf(FieldInternal field) {
        int pos = this.names.positionOf(field);
        if (pos < 1) {
            throw new IllegalStateException("Intern tables incomplete");
        }
        return pos;
    }

    private int positionOf(RecordComponentInternal recordComponent) {
        int pos = this.names.positionOf(recordComponent);
        if (pos < 1) {
            throw new IllegalStateException("Intern tables incomplete");
        }
        return pos;
    }

    private int positionOf(DotName className) {
        Integer i = this.nameTable.get(className);
        if (i == null) {
            throw new IllegalStateException("Class not found in class table:" + className);
        }
        return i;
    }

    private int positionOf(Type type) {
        return this.typeTable.positionOf(type);
    }

    private int positionOf(Type[] types) {
        return this.typeListTable.positionOf(types);
    }

    private int positionOf(AnnotationInstance instance) {
        return this.annotationTable.positionOf(instance);
    }

    private boolean markWritten(Type[] types) {
        return this.typeListTable.markWritten(types);
    }

    private boolean markWritten(AnnotationInstance annotation) {
        return this.annotationTable.markWritten(annotation);
    }

    private void writeClasses(PackedDataOutputStream stream, Index index, int version) throws IOException {
        Collection<ClassInfo> classes = index.getKnownClasses();
        stream.writePackedU32(classes.size());
        for (ClassInfo clazz : classes) {
            this.writeClassEntry(stream, clazz, version);
        }
    }

    private void writeModules(PackedDataOutputStream stream, Index index, int version) throws IOException {
        Collection<ModuleInfo> modules = index.getKnownModules();
        stream.writePackedU32(modules.size());
        this.addClassName(DotName.createSimple("module-info"));
        for (ModuleInfo module : modules) {
            this.writeClassEntry(stream, module.moduleInfoClass(), version);
            this.writeModuleEntry(stream, module, version);
        }
    }

    private void writeClassEntry(PackedDataOutputStream stream, ClassInfo clazz, int version) throws IOException {
        boolean hasNesting;
        stream.writePackedU32(this.positionOf(clazz.name()));
        stream.writePackedU32(clazz.flags());
        stream.writePackedU32(clazz.superClassType() == null ? 0 : this.positionOf(clazz.superClassType()));
        stream.writePackedU32(this.positionOf(clazz.typeParameterArray()));
        stream.writePackedU32(this.positionOf(clazz.interfaceTypeArray()));
        ClassInfo.EnclosingMethodInfo enclosingMethod = clazz.enclosingMethod();
        boolean bl = hasNesting = clazz.nestingType() != ClassInfo.NestingType.TOP_LEVEL;
        if (version >= 9) {
            int mask = 0;
            if (hasNesting) {
                mask = (enclosingMethod != null ? 2 : 0) | 1;
            }
            stream.writeByte(mask);
        }
        if (hasNesting || version < 9) {
            DotName enclosingClass = clazz.enclosingClass();
            FieldInternal[] simpleName = clazz.nestingSimpleName();
            stream.writePackedU32(enclosingClass == null ? 0 : this.positionOf(enclosingClass));
            stream.writePackedU32(simpleName == null ? 0 : this.positionOf((String)simpleName));
            if (enclosingMethod == null) {
                if (version < 9) {
                    stream.writeByte(0);
                }
            } else {
                if (version < 9) {
                    stream.writeByte(1);
                }
                stream.writePackedU32(this.positionOf(enclosingMethod.name()));
                stream.writePackedU32(this.positionOf(enclosingMethod.enclosingClass()));
                stream.writePackedU32(this.positionOf(enclosingMethod.returnType()));
                stream.writePackedU32(this.positionOf(enclosingMethod.parametersArray()));
            }
        }
        stream.writePackedU32(clazz.annotations().size());
        FieldInternal[] fields = clazz.fieldArray();
        stream.writePackedU32(fields.length);
        for (FieldInternal field : fields) {
            stream.writePackedU32(this.positionOf(field));
        }
        if (version >= 10) {
            stream.writePackedU32(this.positionOf(clazz.fieldPositionArray()));
        }
        MethodInternal[] methods = clazz.methodArray();
        stream.writePackedU32(methods.length);
        for (MethodInternal method : methods) {
            stream.writePackedU32(this.positionOf(method));
        }
        if (version >= 10) {
            stream.writePackedU32(this.positionOf(clazz.methodPositionArray()));
        }
        if (version >= 10) {
            RecordComponentInternal[] recordComponents = clazz.recordComponentArray();
            stream.writePackedU32(recordComponents.length);
            for (RecordComponentInternal recordComponent : recordComponents) {
                stream.writePackedU32(this.positionOf(recordComponent));
            }
            stream.writePackedU32(this.positionOf(clazz.recordComponentPositionArray()));
        }
        Set<Map.Entry<DotName, List<AnnotationInstance>>> entrySet = clazz.annotations().entrySet();
        for (Map.Entry<DotName, List<AnnotationInstance>> entry : entrySet) {
            List<AnnotationInstance> value = entry.getValue();
            stream.writePackedU32(value.size());
            for (AnnotationInstance annotation : value) {
                this.writeReferenceOrFull(stream, annotation);
            }
        }
    }

    private void writeModuleEntry(PackedDataOutputStream stream, ModuleInfo module, int version) throws IOException {
        stream.writePackedU32(this.positionOf(module.name()));
        stream.writePackedU32(module.flags());
        stream.writePackedU32(module.version() == null ? 0 : this.positionOf(module.version()));
        stream.writePackedU32(module.mainClass() == null ? 0 : this.positionOf(module.mainClass()));
        List<ModuleInfo.RequiredModuleInfo> requires = module.requiresList();
        stream.writePackedU32(requires.size());
        for (ModuleInfo.RequiredModuleInfo requiredModuleInfo : requires) {
            stream.writePackedU32(this.positionOf(requiredModuleInfo.name()));
            stream.writePackedU32(requiredModuleInfo.flags());
            stream.writePackedU32(requiredModuleInfo.version() == null ? 0 : this.positionOf(requiredModuleInfo.version()));
        }
        List<ModuleInfo.ExportedPackageInfo> exports = module.exportsList();
        stream.writePackedU32(exports.size());
        for (ModuleInfo.ExportedPackageInfo exportedPackageInfo : exports) {
            stream.writePackedU32(this.positionOf(exportedPackageInfo.source()));
            stream.writePackedU32(exportedPackageInfo.flags());
            this.writeDotNames(stream, exportedPackageInfo.targetsList());
        }
        this.writeDotNames(stream, module.usesList());
        List<ModuleInfo.OpenedPackageInfo> list = module.opensList();
        stream.writePackedU32(list.size());
        for (ModuleInfo.OpenedPackageInfo opened : list) {
            stream.writePackedU32(this.positionOf(opened.source()));
            stream.writePackedU32(opened.flags());
            this.writeDotNames(stream, opened.targetsList());
        }
        List<ModuleInfo.ProvidedServiceInfo> list2 = module.providesList();
        stream.writePackedU32(list2.size());
        for (ModuleInfo.ProvidedServiceInfo provided : list2) {
            stream.writePackedU32(this.positionOf(provided.service()));
            this.writeDotNames(stream, provided.providersList());
        }
        this.writeDotNames(stream, module.packagesList());
    }

    private void writeDotNames(PackedDataOutputStream stream, List<DotName> names) throws IOException {
        stream.writePackedU32(names.size());
        for (DotName name : names) {
            stream.writePackedU32(this.positionOf(name));
        }
    }

    private void writeAnnotationValues(PackedDataOutputStream stream, Collection<AnnotationValue> values) throws IOException {
        stream.writePackedU32(values.size());
        for (AnnotationValue value : values) {
            this.writeAnnotationValue(stream, value);
        }
    }

    private void writeAnnotationValue(PackedDataOutputStream stream, AnnotationValue value) throws IOException {
        stream.writePackedU32(this.positionOf(value.name()));
        if (value instanceof AnnotationValue.ByteValue) {
            stream.writeByte(1);
            stream.writeByte(value.asByte() & 0xFF);
        } else if (value instanceof AnnotationValue.ShortValue) {
            stream.writeByte(2);
            stream.writePackedU32(value.asShort() & 0xFFFF);
        } else if (value instanceof AnnotationValue.IntegerValue) {
            stream.writeByte(3);
            stream.writePackedU32(value.asInt());
        } else if (value instanceof AnnotationValue.CharacterValue) {
            stream.writeByte(4);
            stream.writePackedU32(value.asChar());
        } else if (value instanceof AnnotationValue.FloatValue) {
            stream.writeByte(5);
            stream.writeFloat(value.asFloat());
        } else if (value instanceof AnnotationValue.DoubleValue) {
            stream.writeByte(6);
            stream.writeDouble(value.asDouble());
        } else if (value instanceof AnnotationValue.LongValue) {
            stream.writeByte(7);
            stream.writeLong(value.asLong());
        } else if (value instanceof AnnotationValue.BooleanValue) {
            stream.writeByte(8);
            stream.writeBoolean(value.asBoolean());
        } else if (value instanceof AnnotationValue.StringValue) {
            stream.writeByte(9);
            stream.writePackedU32(this.positionOf(value.asString()));
        } else if (value instanceof AnnotationValue.ClassValue) {
            stream.writeByte(10);
            stream.writePackedU32(this.positionOf(value.asClass()));
        } else if (value instanceof AnnotationValue.EnumValue) {
            stream.writeByte(11);
            stream.writePackedU32(this.positionOf(value.asEnumType()));
            stream.writePackedU32(this.positionOf(value.asEnum()));
        } else if (value instanceof AnnotationValue.ArrayValue) {
            AnnotationValue[] array = value.asArray();
            int length = array.length;
            stream.writeByte(12);
            stream.writePackedU32(length);
            for (AnnotationValue anArray : array) {
                this.writeAnnotationValue(stream, anArray);
            }
        } else if (value instanceof AnnotationValue.NestedAnnotation) {
            AnnotationInstance instance = value.asNested();
            stream.writeByte(13);
            this.writeReferenceOrFull(stream, instance);
        }
    }

    private void writeReference(PackedDataOutputStream stream, Type type, boolean nullable) throws IOException {
        if (nullable && type == null) {
            stream.writePackedU32(0);
            return;
        }
        stream.writePackedU32(this.positionOf(type));
    }

    private void writeReferenceOrFull(PackedDataOutputStream stream, AnnotationInstance annotation) throws IOException {
        stream.writePackedU32(this.positionOf(annotation));
        if (this.markWritten(annotation)) {
            this.writeAnnotation(stream, annotation);
        }
    }

    private void writeReference(PackedDataOutputStream stream, AnnotationInstance annotation) throws IOException {
        stream.writePackedU32(this.positionOf(annotation));
    }

    private void writeReferenceOrFull(PackedDataOutputStream stream, Type[] types) throws IOException {
        stream.writePackedU32(this.positionOf(types));
        if (this.markWritten(types)) {
            this.writeTypeListEntry(stream, types);
        }
    }

    private void writeTypeEntry(PackedDataOutputStream stream, Type type) throws IOException {
        stream.writeByte(type.kind().ordinal());
        switch (type.kind()) {
            case CLASS: {
                stream.writePackedU32(this.positionOf(type.name()));
                break;
            }
            case ARRAY: {
                ArrayType arrayType = type.asArrayType();
                stream.writePackedU32(arrayType.dimensions());
                this.writeReference(stream, arrayType.component(), false);
                break;
            }
            case PRIMITIVE: {
                stream.writeByte(type.asPrimitiveType().primitive().ordinal());
                break;
            }
            case VOID: {
                break;
            }
            case TYPE_VARIABLE: {
                TypeVariable typeVariable = type.asTypeVariable();
                stream.writePackedU32(this.positionOf(typeVariable.identifier()));
                this.writeReferenceOrFull(stream, typeVariable.boundArray());
                break;
            }
            case UNRESOLVED_TYPE_VARIABLE: {
                stream.writePackedU32(this.positionOf(type.asUnresolvedTypeVariable().identifier()));
                break;
            }
            case WILDCARD_TYPE: {
                WildcardType wildcardType = type.asWildcardType();
                stream.writePackedU32(wildcardType.isExtends() ? 1 : 0);
                this.writeReference(stream, wildcardType.bound(), false);
                break;
            }
            case PARAMETERIZED_TYPE: {
                ParameterizedType parameterizedType = type.asParameterizedType();
                Type owner = parameterizedType.owner();
                stream.writePackedU32(this.positionOf(parameterizedType.name()));
                this.writeReference(stream, owner, true);
                this.writeReferenceOrFull(stream, parameterizedType.argumentsArray());
            }
        }
        AnnotationInstance[] annotations = type.annotationArray();
        stream.writePackedU32(annotations.length);
        for (AnnotationInstance annotation : annotations) {
            this.writeReferenceOrFull(stream, annotation);
        }
    }

    private void buildTables(Index index, int version) {
        this.nameTable = new HashMap();
        this.sortedNameTable = new TreeMap();
        this.annotationTable = new ReferenceTable();
        this.typeTable = new ReferenceTable();
        this.typeListTable = new ReferenceTable();
        this.names = new NameTable();
        for (ClassInfo classInfo : index.getKnownClasses()) {
            this.addClass(classInfo);
        }
        if (version >= 10) {
            for (ModuleInfo moduleInfo : index.getKnownModules()) {
                this.addClass(moduleInfo.moduleInfoClass());
                this.addModule(moduleInfo);
            }
            if (index.users != null) {
                for (Map.Entry entry : index.users.entrySet()) {
                    this.addClassName((DotName)entry.getKey());
                    for (ClassInfo classInfo : (List)entry.getValue()) {
                        this.addClassName(classInfo.name());
                    }
                }
            }
        }
    }

    private void addClass(ClassInfo clazz) {
        String name;
        this.addClassName(clazz.name());
        if (clazz.superName() != null) {
            this.addClassName(clazz.superName());
        }
        this.addTypeList(clazz.typeParameterArray());
        this.addTypeList(clazz.interfaceTypeArray());
        this.addType(clazz.superClassType());
        DotName enclosingClass = clazz.enclosingClass();
        if (enclosingClass != null) {
            this.addClassName(enclosingClass);
        }
        if ((name = clazz.nestingSimpleName()) != null) {
            this.addString(name);
        }
        this.addEnclosingMethod(clazz.enclosingMethod());
        this.addMethodList(clazz.methodArray());
        this.names.intern(clazz.methodPositionArray());
        this.addFieldList(clazz.fieldArray());
        this.names.intern(clazz.fieldPositionArray());
        this.addRecordComponentList(clazz.recordComponentArray());
        this.names.intern(clazz.recordComponentPositionArray());
        for (Map.Entry<DotName, List<AnnotationInstance>> entry : clazz.annotations().entrySet()) {
            this.addClassName(entry.getKey());
            for (AnnotationInstance instance : entry.getValue()) {
                this.addAnnotation(instance);
            }
        }
    }

    private void addModule(ModuleInfo module) {
        this.addClassName(module.name());
        this.addNullableString(module.version());
        DotName mainClass = module.mainClass();
        if (mainClass != null) {
            this.addClassName(mainClass);
        }
        for (ModuleInfo.RequiredModuleInfo required : module.requires()) {
            this.addClassName(required.name());
            this.addNullableString(required.version());
        }
        for (ModuleInfo.ExportedPackageInfo exported : module.exports()) {
            this.addClassName(exported.source());
            this.addClassNames(exported.targets());
        }
        for (ModuleInfo.OpenedPackageInfo opened : module.opens()) {
            this.addClassName(opened.source());
            this.addClassNames(opened.targets());
        }
        this.addClassNames(module.uses());
        for (ModuleInfo.ProvidedServiceInfo provided : module.provides()) {
            this.addClassName(provided.service());
            this.addClassNames(provided.providers());
        }
        this.addClassNames(module.packages());
    }

    private void addAnnotation(AnnotationInstance instance) {
        this.addClassName(instance.name());
        for (AnnotationValue value : instance.values()) {
            this.buildAValueEntries(value);
        }
        this.addAnnotationTarget(instance.target());
        this.annotationTable.addReference(instance);
    }

    private void addAnnotationTarget(AnnotationTarget target) {
    }

    private void addFieldList(FieldInternal[] fields) {
        for (FieldInternal field : fields) {
            this.deepIntern(field);
        }
    }

    private void deepIntern(FieldInternal field) {
        this.addType(field.type());
        this.names.intern(field.nameBytes());
        this.names.intern(field);
    }

    private void addMethodList(MethodInternal[] methods) {
        for (MethodInternal method : methods) {
            this.deepIntern(method);
        }
    }

    private void deepIntern(MethodInternal method) {
        this.addType(method.returnType());
        this.addType(method.receiverTypeField());
        this.addTypeList(method.typeParameterArray());
        this.addTypeList(method.parameterArray());
        this.addTypeList(method.exceptionArray());
        AnnotationValue defaultValue = method.defaultValue();
        if (defaultValue != null) {
            this.buildAValueEntries(defaultValue);
        }
        for (byte[] parameterName : method.parameterNamesBytes()) {
            this.names.intern(parameterName);
        }
        this.names.intern(method.nameBytes());
        this.names.intern(method);
    }

    private void addRecordComponentList(RecordComponentInternal[] recordComponents) {
        for (RecordComponentInternal recordComponent : recordComponents) {
            this.deepIntern(recordComponent);
        }
    }

    private void deepIntern(RecordComponentInternal recordComponent) {
        this.addType(recordComponent.type());
        this.names.intern(recordComponent.nameBytes());
        this.names.intern(recordComponent);
    }

    private void addEnclosingMethod(ClassInfo.EnclosingMethodInfo enclosingMethod) {
        if (enclosingMethod == null) {
            return;
        }
        this.addString(enclosingMethod.name());
        this.addType(enclosingMethod.returnType());
        this.addTypeList(enclosingMethod.parametersArray());
        this.addClassName(enclosingMethod.enclosingClass());
    }

    private void addTypeList(Type[] types) {
        for (Type type : types) {
            this.addType(type);
        }
        this.typeListTable.addReference(types);
    }

    private void addType(Type type) {
        if (type == null) {
            return;
        }
        switch (type.kind()) {
            case CLASS: {
                this.addClassName(type.asClassType().name());
                break;
            }
            case ARRAY: {
                this.addType(type.asArrayType().component());
                break;
            }
            case TYPE_VARIABLE: {
                TypeVariable typeVariable = type.asTypeVariable();
                this.addString(typeVariable.identifier());
                this.addTypeList(typeVariable.boundArray());
                break;
            }
            case UNRESOLVED_TYPE_VARIABLE: {
                this.addString(type.asUnresolvedTypeVariable().identifier());
                break;
            }
            case WILDCARD_TYPE: {
                this.addType(type.asWildcardType().bound());
                break;
            }
            case PARAMETERIZED_TYPE: {
                ParameterizedType parameterizedType = type.asParameterizedType();
                this.addClassName(parameterizedType.name());
                this.addType(parameterizedType.owner());
                this.addTypeList(parameterizedType.argumentsArray());
                break;
            }
        }
        for (AnnotationInstance instance : type.annotationArray()) {
            this.addAnnotation(instance);
        }
        this.typeTable.addReference(type);
    }

    private void buildAValueEntries(AnnotationValue value) {
        this.addString(value.name());
        if (value instanceof AnnotationValue.StringValue) {
            this.addString(value.asString());
        } else if (value instanceof AnnotationValue.ClassValue) {
            this.addType(value.asClass());
        } else if (value instanceof AnnotationValue.EnumValue) {
            this.addClassName(value.asEnumType());
            this.addString(value.asEnum());
        } else if (value instanceof AnnotationValue.ArrayValue) {
            for (AnnotationValue entry : value.asArray()) {
                this.buildAValueEntries(entry);
            }
        } else if (value instanceof AnnotationValue.NestedAnnotation) {
            AnnotationInstance instance = value.asNested();
            this.addAnnotation(instance);
        }
    }

    private String addNullableString(String name) {
        if (name != null) {
            return this.addString(name);
        }
        return null;
    }

    private String addString(String name) {
        return this.names.intern(name);
    }

    private void addClassNames(List<DotName> names) {
        for (DotName name : names) {
            this.addClassName(name);
        }
    }

    private void addClassName(DotName name) {
        DotName prefix;
        if (!this.nameTable.containsKey(name)) {
            this.addString(name.local());
            this.nameTable.put(name, null);
            this.sortedNameTable.put(name.toString(), name);
        }
        if ((prefix = name.prefix()) != null) {
            this.addClassName(prefix);
        }
    }

    static class ReferenceTable<T> {
        private IdentityHashMap<T, ReferenceEntry> references = new IdentityHashMap();
        private List<T> table = new ArrayList<T>();
        private int counter = 1;

        ReferenceTable() {
        }

        void addReference(T reference) {
            if (this.references.containsKey(reference)) {
                return;
            }
            int index = this.counter++;
            this.references.put(reference, new ReferenceEntry(index));
            this.table.add(reference);
        }

        private ReferenceEntry getReferenceEntry(T reference) {
            ReferenceEntry entry = this.references.get(reference);
            if (entry == null) {
                throw new IllegalStateException();
            }
            return entry;
        }

        int positionOf(T reference) {
            ReferenceEntry entry = this.getReferenceEntry(reference);
            return entry.index;
        }

        boolean markWritten(T reference) {
            ReferenceEntry entry = this.getReferenceEntry(reference);
            boolean ret = entry.written;
            if (!ret) {
                entry.written = true;
            }
            return !ret;
        }

        List<T> list() {
            return this.table;
        }

        int size() {
            return this.references.size();
        }
    }

    static class ReferenceEntry {
        private int index;
        private boolean written;

        ReferenceEntry(int index) {
            this.index = index;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassExtendsTypeTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.EmptyTypeTarget;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.FieldInternal;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReaderImpl;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodInternal;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.MethodParameterTypeTarget;
import org.jboss.jandex.ModuleInfo;
import org.jboss.jandex.PackedDataInputStream;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.RecordComponentInternal;
import org.jboss.jandex.ThrowsTypeTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeParameterBoundTypeTarget;
import org.jboss.jandex.TypeParameterTypeTarget;
import org.jboss.jandex.TypeTarget;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.UnresolvedTypeVariable;
import org.jboss.jandex.Utils;
import org.jboss.jandex.VoidType;
import org.jboss.jandex.WildcardType;

final class IndexReaderV2
extends IndexReaderImpl {
    static final int MIN_VERSION = 6;
    static final int MAX_VERSION = 10;
    static final int MAX_DATA_VERSION = 4;
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
    private static final byte[] INIT_METHOD_NAME = Utils.toUTF8("<init>");
    private PackedDataInputStream input;
    private byte[][] byteTable;
    private String[] stringTable;
    private DotName[] nameTable;
    private Type[] typeTable;
    private Type[][] typeListTable;
    private AnnotationInstance[] annotationTable;
    private MethodInternal[] methodTable;
    private FieldInternal[] fieldTable;
    private RecordComponentInternal[] recordComponentTable;
    private HashMap<DotName, Set<DotName>> users;

    IndexReaderV2(PackedDataInputStream input) {
        this.input = input;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    Index read(int version) throws IOException {
        try {
            PackedDataInputStream stream = this.input;
            int annotationsSize = stream.readPackedU32();
            int implementorsSize = stream.readPackedU32();
            int subclassesSize = stream.readPackedU32();
            int usersSize = 0;
            if (version >= 10) {
                usersSize = stream.readPackedU32();
                this.users = new HashMap(usersSize);
            }
            this.readByteTable(stream);
            this.readStringTable(stream);
            this.readNameTable(stream);
            this.typeTable = new Type[stream.readPackedU32() + 1];
            this.typeListTable = new Type[stream.readPackedU32() + 1][];
            this.annotationTable = new AnnotationInstance[stream.readPackedU32() + 1];
            this.readTypeTable(stream);
            this.readTypeListTable(stream);
            if (version >= 10) {
                this.readUsers(stream, usersSize);
            }
            this.readMethodTable(stream, version);
            this.readFieldTable(stream);
            if (version >= 10) {
                this.readRecordComponentTable(stream);
            }
            Index index = this.readClasses(stream, annotationsSize, implementorsSize, subclassesSize, version);
            return index;
        }
        finally {
            this.byteTable = null;
            this.stringTable = null;
            this.nameTable = null;
            this.typeTable = null;
            this.typeListTable = null;
            this.annotationTable = null;
            this.methodTable = null;
            this.fieldTable = null;
            this.recordComponentTable = null;
            this.users = null;
        }
    }

    private void readUsers(PackedDataInputStream stream, int usersSize) throws IOException {
        for (int i = 0; i < usersSize; ++i) {
            DotName user = this.nameTable[stream.readPackedU32()];
            int usesCount = stream.readPackedU32();
            HashSet<DotName> uses = new HashSet<DotName>(usesCount);
            for (int j = 0; j < usesCount; ++j) {
                uses.add(this.nameTable[stream.readPackedU32()]);
            }
            this.users.put(user, uses);
        }
    }

    private void readByteTable(PackedDataInputStream stream) throws IOException {
        int size = stream.readPackedU32() + 1;
        byte[][] byArrayArray = new byte[size][];
        this.byteTable = byArrayArray;
        byte[][] byteTable = byArrayArray;
        for (int i = 1; i < size; ++i) {
            int len = stream.readPackedU32();
            byteTable[i] = new byte[len];
            stream.readFully(byteTable[i], 0, len);
        }
    }

    private void readStringTable(PackedDataInputStream stream) throws IOException {
        int size = stream.readPackedU32() + 1;
        this.stringTable = new String[size];
        String[] stringTable = this.stringTable;
        for (int i = 1; i < size; ++i) {
            stringTable[i] = stream.readUTF();
        }
    }

    private void readNameTable(PackedDataInputStream stream) throws IOException {
        int entries = stream.readPackedU32() + 1;
        int lastDepth = -1;
        DotName curr = null;
        this.nameTable = new DotName[entries];
        for (int i = 1; i < entries; ++i) {
            int depth = stream.readPackedU32();
            boolean inner = (depth & 1) == 1;
            String local = this.stringTable[stream.readPackedU32()];
            if ((depth >>= 1) <= lastDepth) {
                while (lastDepth-- >= depth) {
                    assert (curr != null);
                    curr = curr.prefix();
                }
            }
            this.nameTable[i] = curr = new DotName(curr, local, true, inner);
            lastDepth = depth;
        }
    }

    private void readTypeTable(PackedDataInputStream stream) throws IOException {
        for (int i = 1; i < this.typeTable.length; ++i) {
            this.typeTable[i] = this.readTypeEntry(stream);
        }
    }

    private int findNextNull(Object[] array, int start) {
        while (start < array.length) {
            if (array[start] == null) {
                return start;
            }
            ++start;
        }
        return array.length;
    }

    private void readTypeListTable(PackedDataInputStream stream) throws IOException {
        Type[][] typeListTable = this.typeListTable;
        int i = this.findNextNull((Object[])typeListTable, 1);
        while (i < typeListTable.length) {
            typeListTable[i] = this.readTypeListEntry(stream);
            i = this.findNextNull((Object[])typeListTable, i);
        }
    }

    private AnnotationInstance[] readAnnotations(PackedDataInputStream stream, AnnotationTarget target) throws IOException {
        int size = stream.readPackedU32();
        if (size == 0) {
            return AnnotationInstance.EMPTY_ARRAY;
        }
        AnnotationInstance[] annotations = new AnnotationInstance[size];
        for (int i = 0; i < size; ++i) {
            int reference = stream.readPackedU32();
            if (this.annotationTable[reference] == null) {
                this.annotationTable[reference] = this.readAnnotationEntry(stream, target);
            }
            annotations[i] = this.annotationTable[reference];
        }
        return annotations;
    }

    private AnnotationValue[] readAnnotationValues(PackedDataInputStream stream) throws IOException {
        int numValues = stream.readPackedU32();
        AnnotationValue[] values = numValues > 0 ? new AnnotationValue[numValues] : AnnotationValue.EMPTY_VALUE_ARRAY;
        for (int i = 0; i < numValues; ++i) {
            AnnotationValue value;
            values[i] = value = this.readAnnotationValue(stream);
        }
        return values;
    }

    private AnnotationValue readAnnotationValue(PackedDataInputStream stream) throws IOException {
        AnnotationValue value;
        String name = this.stringTable[stream.readPackedU32()];
        byte tag = stream.readByte();
        switch (tag) {
            case 1: {
                value = new AnnotationValue.ByteValue(name, stream.readByte());
                break;
            }
            case 2: {
                value = new AnnotationValue.ShortValue(name, (short)stream.readPackedU32());
                break;
            }
            case 3: {
                value = new AnnotationValue.IntegerValue(name, stream.readPackedU32());
                break;
            }
            case 4: {
                value = new AnnotationValue.CharacterValue(name, (char)stream.readPackedU32());
                break;
            }
            case 5: {
                value = new AnnotationValue.FloatValue(name, stream.readFloat());
                break;
            }
            case 6: {
                value = new AnnotationValue.DoubleValue(name, stream.readDouble());
                break;
            }
            case 7: {
                value = new AnnotationValue.LongValue(name, stream.readLong());
                break;
            }
            case 8: {
                value = new AnnotationValue.BooleanValue(name, stream.readBoolean());
                break;
            }
            case 9: {
                value = new AnnotationValue.StringValue(name, this.stringTable[stream.readPackedU32()]);
                break;
            }
            case 10: {
                value = new AnnotationValue.ClassValue(name, this.typeTable[stream.readPackedU32()]);
                break;
            }
            case 11: {
                value = new AnnotationValue.EnumValue(name, this.nameTable[stream.readPackedU32()], this.stringTable[stream.readPackedU32()]);
                break;
            }
            case 12: {
                value = new AnnotationValue.ArrayValue(name, this.readAnnotationValues(stream));
                break;
            }
            case 13: {
                int reference = stream.readPackedU32();
                AnnotationInstance nestedInstance = this.annotationTable[reference];
                if (nestedInstance == null) {
                    nestedInstance = this.annotationTable[reference] = this.readAnnotationEntry(stream, null);
                }
                value = new AnnotationValue.NestedAnnotation(name, nestedInstance);
                break;
            }
            default: {
                throw new IllegalStateException("Invalid annotation value tag:" + tag);
            }
        }
        return value;
    }

    private AnnotationInstance readAnnotationEntry(PackedDataInputStream stream, AnnotationTarget caller) throws IOException {
        DotName name = this.nameTable[stream.readPackedU32()];
        AnnotationTarget target = this.readAnnotationTarget(stream, caller);
        AnnotationValue[] values = this.readAnnotationValues(stream);
        return new AnnotationInstance(name, target, values);
    }

    private Type[] readTypeListReference(PackedDataInputStream stream) throws IOException {
        int reference = stream.readPackedU32();
        Type[] types = this.typeListTable[reference];
        if (types != null) {
            return types;
        }
        this.typeListTable[reference] = this.readTypeListEntry(stream);
        return this.typeListTable[reference];
    }

    private Type[] readTypeListEntry(PackedDataInputStream stream) throws IOException {
        int size = stream.readPackedU32();
        if (size == 0) {
            return Type.EMPTY_ARRAY;
        }
        Type[] types = new Type[size];
        for (int i = 0; i < size; ++i) {
            types[i] = this.typeTable[stream.readPackedU32()];
        }
        return types;
    }

    private Type readTypeEntry(PackedDataInputStream stream) throws IOException {
        Type.Kind kind = Type.Kind.fromOrdinal(stream.readUnsignedByte());
        switch (kind) {
            case CLASS: {
                DotName name = this.nameTable[stream.readPackedU32()];
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return new ClassType(name, annotations);
            }
            case ARRAY: {
                int dimensions = stream.readPackedU32();
                Type component = this.typeTable[stream.readPackedU32()];
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return new ArrayType(component, dimensions, annotations);
            }
            case PRIMITIVE: {
                int primitive = stream.readUnsignedByte();
                PrimitiveType type = PrimitiveType.fromOridinal(primitive);
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return annotations.length > 0 ? ((Type)type).copyType(annotations) : type;
            }
            case VOID: {
                VoidType type = VoidType.VOID;
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return annotations.length > 0 ? ((Type)type).copyType(annotations) : type;
            }
            case TYPE_VARIABLE: {
                String identifier = this.stringTable[stream.readPackedU32()];
                Type[] bounds = this.readTypeListReference(stream);
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return new TypeVariable(identifier, bounds, annotations);
            }
            case UNRESOLVED_TYPE_VARIABLE: {
                String identifier = this.stringTable[stream.readPackedU32()];
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return new UnresolvedTypeVariable(identifier, annotations);
            }
            case WILDCARD_TYPE: {
                boolean isExtends = stream.readPackedU32() == 1;
                Type bound = this.typeTable[stream.readPackedU32()];
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return new WildcardType(bound, isExtends, annotations);
            }
            case PARAMETERIZED_TYPE: {
                DotName name = this.nameTable[stream.readPackedU32()];
                int reference = stream.readPackedU32();
                Type owner = this.typeTable[reference];
                Type[] parameters = this.readTypeListReference(stream);
                AnnotationInstance[] annotations = this.readAnnotations(stream, null);
                return new ParameterizedType(name, parameters, owner, annotations);
            }
        }
        throw new IllegalStateException("Unrecognized type: " + (Object)((Object)kind));
    }

    private AnnotationTarget readAnnotationTarget(PackedDataInputStream stream, AnnotationTarget caller) throws IOException {
        byte tag = stream.readByte();
        switch (tag) {
            case 0: {
                return null;
            }
            case 1: 
            case 2: 
            case 4: 
            case 11: {
                return caller;
            }
            case 3: {
                short parameter = (short)stream.readPackedU32();
                return new MethodParameterInfo((MethodInfo)caller, parameter);
            }
            case 5: {
                Type target = this.typeTable[stream.readPackedU32()];
                boolean isReceiver = stream.readPackedU32() == 1;
                return new EmptyTypeTarget(caller, target, isReceiver);
            }
            case 6: {
                Type target = this.typeTable[stream.readPackedU32()];
                int pos = stream.readPackedU32();
                return new ClassExtendsTypeTarget(caller, target, pos);
            }
            case 7: {
                Type target = this.typeTable[stream.readPackedU32()];
                int pos = stream.readPackedU32();
                return new TypeParameterTypeTarget(caller, target, pos);
            }
            case 8: {
                Type target = this.typeTable[stream.readPackedU32()];
                int pos = stream.readPackedU32();
                int bound = stream.readPackedU32();
                return new TypeParameterBoundTypeTarget(caller, target, pos, bound);
            }
            case 9: {
                Type target = this.typeTable[stream.readPackedU32()];
                int pos = stream.readPackedU32();
                return new MethodParameterTypeTarget(caller, target, pos);
            }
            case 10: {
                Type target = this.typeTable[stream.readPackedU32()];
                int pos = stream.readPackedU32();
                return new ThrowsTypeTarget(caller, target, pos);
            }
        }
        throw new IllegalStateException("Invalid tag: " + tag);
    }

    private void readMethodTable(PackedDataInputStream stream, int version) throws IOException {
        int size = stream.readPackedU32() + 1;
        this.methodTable = new MethodInternal[size];
        for (int i = 1; i < size; ++i) {
            this.methodTable[i] = this.readMethodEntry(stream, version);
        }
    }

    private void readFieldTable(PackedDataInputStream stream) throws IOException {
        int size = stream.readPackedU32() + 1;
        this.fieldTable = new FieldInternal[size];
        for (int i = 1; i < size; ++i) {
            this.fieldTable[i] = this.readFieldEntry(stream);
        }
    }

    private void readRecordComponentTable(PackedDataInputStream stream) throws IOException {
        int size = stream.readPackedU32() + 1;
        this.recordComponentTable = new RecordComponentInternal[size];
        for (int i = 1; i < size; ++i) {
            this.recordComponentTable[i] = this.readRecordComponentEntry(stream);
        }
    }

    private MethodInternal readMethodEntry(PackedDataInputStream stream, int version) throws IOException {
        int size;
        byte[] name = this.byteTable[stream.readPackedU32()];
        short flags = (short)stream.readPackedU32();
        Type[] typeParameters = this.typeListTable[stream.readPackedU32()];
        int reference = stream.readPackedU32();
        Type receiverType = this.typeTable[reference];
        Type returnType = this.typeTable[stream.readPackedU32()];
        Type[] parameters = this.typeListTable[stream.readPackedU32()];
        Type[] exceptions = this.typeListTable[stream.readPackedU32()];
        AnnotationValue defaultValue = null;
        if (version >= 7) {
            boolean hasDefaultValue;
            boolean bl = hasDefaultValue = stream.readByte() > 0;
            if (hasDefaultValue) {
                defaultValue = this.readAnnotationValue(stream);
            }
        }
        Object methodParameterBytes = MethodInternal.EMPTY_PARAMETER_NAMES;
        if (version >= 8 && (size = stream.readPackedU32()) > 0) {
            methodParameterBytes = new byte[size][];
            for (int i = 0; i < size; ++i) {
                methodParameterBytes[i] = this.byteTable[stream.readPackedU32()];
            }
        }
        MethodInfo methodInfo = new MethodInfo();
        AnnotationInstance[] annotations = this.readAnnotations(stream, methodInfo);
        MethodInternal methodInternal = new MethodInternal(name, (byte[][])methodParameterBytes, parameters, returnType, flags, receiverType, typeParameters, exceptions, annotations, defaultValue);
        methodInfo.setMethodInternal(methodInternal);
        return methodInternal;
    }

    private FieldInternal readFieldEntry(PackedDataInputStream stream) throws IOException {
        byte[] name = this.byteTable[stream.readPackedU32()];
        short flags = (short)stream.readPackedU32();
        Type type = this.typeTable[stream.readPackedU32()];
        FieldInfo fieldInfo = new FieldInfo();
        AnnotationInstance[] annotations = this.readAnnotations(stream, fieldInfo);
        FieldInternal fieldInternal = new FieldInternal(name, type, flags, annotations);
        fieldInfo.setFieldInternal(fieldInternal);
        return fieldInternal;
    }

    private RecordComponentInternal readRecordComponentEntry(PackedDataInputStream stream) throws IOException {
        byte[] name = this.byteTable[stream.readPackedU32()];
        Type type = this.typeTable[stream.readPackedU32()];
        RecordComponentInfo recordComponentInfo = new RecordComponentInfo();
        AnnotationInstance[] annotations = this.readAnnotations(stream, recordComponentInfo);
        RecordComponentInternal recordComponentInternal = new RecordComponentInternal(name, type, annotations);
        recordComponentInfo.setRecordComponentInternal(recordComponentInternal);
        return recordComponentInternal;
    }

    private ClassInfo readClassEntry(PackedDataInputStream stream, Map<DotName, List<AnnotationInstance>> masterAnnotations, int version) throws IOException {
        int size;
        DotName name = this.nameTable[stream.readPackedU32()];
        short flags = (short)stream.readPackedU32();
        Type superType = this.typeTable[stream.readPackedU32()];
        Type[] typeParameters = this.typeListTable[stream.readPackedU32()];
        Type[] interfaceTypes = this.typeListTable[stream.readPackedU32()];
        boolean hasEnclosingMethod = false;
        boolean hasNesting = false;
        if (version >= 9) {
            int nestingMask = stream.readUnsignedByte();
            if (nestingMask > 0) {
                hasNesting = true;
                hasEnclosingMethod = (nestingMask & 2) == 2;
            }
        } else {
            hasNesting = true;
            hasEnclosingMethod = true;
        }
        DotName enclosingClass = null;
        ClassInfo.EnclosingMethodInfo enclosingMethod = null;
        String simpleName = null;
        if (hasNesting) {
            enclosingClass = this.nameTable[stream.readPackedU32()];
            simpleName = this.stringTable[stream.readPackedU32()];
            enclosingMethod = hasEnclosingMethod ? this.readEnclosingMethod(stream, version) : null;
        }
        HashMap<DotName, List<AnnotationInstance>> annotations = (size = stream.readPackedU32()) > 0 ? new HashMap<DotName, List<AnnotationInstance>>(size) : Collections.emptyMap();
        ClassInfo clazz = new ClassInfo(name, superType, flags, interfaceTypes);
        clazz.setTypeParameters(typeParameters);
        if (hasNesting) {
            clazz.setEnclosingMethod(enclosingMethod);
            clazz.setInnerClassInfo(enclosingClass, simpleName, version >= 9);
        }
        FieldInternal[] fields = this.readClassFields(stream, clazz);
        clazz.setFieldArray(fields);
        if (version >= 10) {
            clazz.setFieldPositionArray(this.byteTable[stream.readPackedU32()]);
        }
        MethodInternal[] methods = this.readClassMethods(stream, clazz);
        clazz.setMethodArray(methods);
        if (version >= 10) {
            clazz.setMethodPositionArray(this.byteTable[stream.readPackedU32()]);
        }
        if (version >= 10) {
            RecordComponentInternal[] recordComponents = this.readClassRecordComponents(stream, clazz);
            clazz.setRecordComponentArray(recordComponents);
            clazz.setRecordComponentPositionArray(this.byteTable[stream.readPackedU32()]);
        }
        for (int i = 0; i < size; ++i) {
            List<AnnotationInstance> instances = this.convertToList(this.readAnnotations(stream, clazz));
            if (instances.size() <= 0) continue;
            DotName annotationName = instances.get(0).name();
            annotations.put(annotationName, instances);
            this.addToMaster(masterAnnotations, annotationName, instances);
        }
        clazz.setAnnotations(annotations);
        return clazz;
    }

    private ModuleInfo readModuleEntry(PackedDataInputStream stream, ClassInfo moduleInfoClass) throws IOException {
        DotName moduleName = this.nameTable[stream.readPackedU32()];
        short moduleFlags = (short)stream.readPackedU32();
        String moduleVersion = this.stringTable[stream.readPackedU32()];
        DotName mainClass = this.nameTable[stream.readPackedU32()];
        ModuleInfo module = new ModuleInfo(moduleInfoClass, moduleName, moduleFlags, moduleVersion);
        module.setMainClass(mainClass);
        int requiredCount = stream.readPackedU32();
        List<ModuleInfo.RequiredModuleInfo> requires = Utils.listOfCapacity(requiredCount);
        for (int i = 0; i < requiredCount; ++i) {
            DotName name = this.nameTable[stream.readPackedU32()];
            short flags = (short)stream.readPackedU32();
            String version = this.stringTable[stream.readPackedU32()];
            requires.add(new ModuleInfo.RequiredModuleInfo(name, flags, version));
        }
        module.setRequires(requires);
        int exportedCount = stream.readPackedU32();
        List<ModuleInfo.ExportedPackageInfo> exports = Utils.listOfCapacity(exportedCount);
        for (int i = 0; i < exportedCount; ++i) {
            DotName source = this.nameTable[stream.readPackedU32()];
            short flags = (short)stream.readPackedU32();
            List<DotName> targets = this.readDotNames(stream);
            exports.add(new ModuleInfo.ExportedPackageInfo(source, flags, targets));
        }
        module.setExports(exports);
        module.setUses(this.readDotNames(stream));
        int openedCount = stream.readPackedU32();
        List<ModuleInfo.OpenedPackageInfo> opens = Utils.listOfCapacity(openedCount);
        for (int i = 0; i < openedCount; ++i) {
            DotName source = this.nameTable[stream.readPackedU32()];
            short flags = (short)stream.readPackedU32();
            List<DotName> targets = this.readDotNames(stream);
            opens.add(new ModuleInfo.OpenedPackageInfo(source, flags, targets));
        }
        module.setOpens(opens);
        int providedCount = stream.readPackedU32();
        List<ModuleInfo.ProvidedServiceInfo> provides = Utils.listOfCapacity(providedCount);
        for (int i = 0; i < providedCount; ++i) {
            DotName service = this.nameTable[stream.readPackedU32()];
            List<DotName> providers = this.readDotNames(stream);
            provides.add(new ModuleInfo.ProvidedServiceInfo(service, providers));
        }
        module.setProvides(provides);
        module.setPackages(this.readDotNames(stream));
        return module;
    }

    private List<DotName> readDotNames(PackedDataInputStream stream) throws IOException {
        int size = stream.readPackedU32();
        List<DotName> names = Utils.listOfCapacity(size);
        for (int i = 0; i < size; ++i) {
            names.add(this.nameTable[stream.readPackedU32()]);
        }
        return names;
    }

    private void addToMaster(Map<DotName, List<AnnotationInstance>> masterAnnotations, DotName name, List<AnnotationInstance> annotations) {
        List<AnnotationInstance> entry = masterAnnotations.get(name);
        if (entry == null) {
            masterAnnotations.put(name, new ArrayList<AnnotationInstance>(annotations));
            return;
        }
        entry.addAll(annotations);
    }

    private List<AnnotationInstance> convertToList(AnnotationInstance[] annotationInstances) {
        if (annotationInstances.length == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(annotationInstances));
    }

    private void addClassToMap(HashMap<DotName, List<ClassInfo>> map, DotName name, ClassInfo currentClass) {
        List<ClassInfo> list = map.get(name);
        if (list == null) {
            list = new ArrayList<ClassInfo>();
            map.put(name, list);
        }
        list.add(currentClass);
    }

    private FieldInternal[] readClassFields(PackedDataInputStream stream, ClassInfo clazz) throws IOException {
        int len = stream.readPackedU32();
        FieldInternal[] fields = len > 0 ? new FieldInternal[len] : FieldInternal.EMPTY_ARRAY;
        for (int i = 0; i < len; ++i) {
            FieldInternal field = this.fieldTable[stream.readPackedU32()];
            this.updateAnnotationTargetInfo(field.annotationArray(), clazz);
            fields[i] = field;
        }
        return fields;
    }

    private RecordComponentInternal[] readClassRecordComponents(PackedDataInputStream stream, ClassInfo clazz) throws IOException {
        int len = stream.readPackedU32();
        RecordComponentInternal[] recordComponents = len > 0 ? new RecordComponentInternal[len] : RecordComponentInternal.EMPTY_ARRAY;
        for (int i = 0; i < len; ++i) {
            RecordComponentInternal recordComponent = this.recordComponentTable[stream.readPackedU32()];
            this.updateAnnotationTargetInfo(recordComponent.annotationArray(), clazz);
            recordComponents[i] = recordComponent;
        }
        return recordComponents;
    }

    private MethodInternal[] readClassMethods(PackedDataInputStream stream, ClassInfo clazz) throws IOException {
        int len = stream.readPackedU32();
        MethodInternal[] methods = len > 0 ? new MethodInternal[len] : MethodInternal.EMPTY_ARRAY;
        for (int i = 0; i < len; ++i) {
            MethodInternal method = this.methodTable[stream.readPackedU32()];
            this.updateAnnotationTargetInfo(method.annotationArray(), clazz);
            methods[i] = method;
            if (method.parameterArray().length != 0 || !Arrays.equals(INIT_METHOD_NAME, method.nameBytes())) continue;
            clazz.setHasNoArgsConstructor(true);
        }
        return methods;
    }

    private void updateAnnotationTargetInfo(AnnotationInstance[] annotations, ClassInfo clazz) {
        for (AnnotationInstance annotation : annotations) {
            AnnotationTarget target = annotation.target();
            if (target instanceof TypeTarget) {
                target = ((TypeTarget)target).enclosingTarget();
            }
            if (target instanceof MethodInfo) {
                ((MethodInfo)target).setClassInfo(clazz);
                continue;
            }
            if (target instanceof MethodParameterInfo) {
                ((MethodParameterInfo)target).method().setClassInfo(clazz);
                continue;
            }
            if (target instanceof FieldInfo) {
                ((FieldInfo)target).setClassInfo(clazz);
                continue;
            }
            if (!(target instanceof RecordComponentInfo)) continue;
            ((RecordComponentInfo)target).setClassInfo(clazz);
        }
    }

    private ClassInfo.EnclosingMethodInfo readEnclosingMethod(PackedDataInputStream stream, int version) throws IOException {
        if (version < 9 && stream.readUnsignedByte() != 1) {
            return null;
        }
        String eName = this.stringTable[stream.readPackedU32()];
        DotName eClass = this.nameTable[stream.readPackedU32()];
        Type returnType = this.typeTable[stream.readPackedU32()];
        Type[] parameters = this.typeListTable[stream.readPackedU32()];
        return new ClassInfo.EnclosingMethodInfo(eName, returnType, parameters, eClass);
    }

    private Index readClasses(PackedDataInputStream stream, int annotationsSize, int implementorsSize, int subclassesSize, int version) throws IOException {
        int classesSize = stream.readPackedU32();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>(classesSize);
        HashMap<DotName, List<ClassInfo>> subclasses = new HashMap<DotName, List<ClassInfo>>(subclassesSize);
        HashMap<DotName, List<ClassInfo>> implementors = new HashMap<DotName, List<ClassInfo>>(implementorsSize);
        HashMap<DotName, List<AnnotationInstance>> masterAnnotations = new HashMap<DotName, List<AnnotationInstance>>(annotationsSize);
        for (int i = 0; i < classesSize; ++i) {
            ClassInfo clazz = this.readClassEntry(stream, masterAnnotations, version);
            this.addClassToMap(subclasses, clazz.superName(), clazz);
            for (Type interfaceType : clazz.interfaceTypeArray()) {
                this.addClassToMap(implementors, interfaceType.name(), clazz);
            }
            classes.put(clazz.name(), clazz);
        }
        Map<DotName, List<ClassInfo>> users = null;
        if (version >= 10) {
            users = new HashMap(this.users.size());
            for (Map.Entry entry : this.users.entrySet()) {
                ArrayList<ClassInfo> usedBy = new ArrayList<ClassInfo>(((Set)entry.getValue()).size());
                users.put((DotName)entry.getKey(), (List<ClassInfo>)usedBy);
                for (DotName usedByName : (Set)entry.getValue()) {
                    usedBy.add(classes.get(usedByName));
                }
            }
        } else {
            users = Collections.emptyMap();
        }
        Map<DotName, ModuleInfo> modules = version >= 10 ? this.readModules(stream, masterAnnotations, version) : Collections.emptyMap();
        return new Index(masterAnnotations, subclasses, implementors, classes, modules, users);
    }

    private Map<DotName, ModuleInfo> readModules(PackedDataInputStream stream, Map<DotName, List<AnnotationInstance>> masterAnnotations, int version) throws IOException {
        int modulesSize = stream.readPackedU32();
        HashMap<DotName, ModuleInfo> modules = modulesSize > 0 ? new HashMap<DotName, ModuleInfo>(modulesSize) : Collections.emptyMap();
        for (int i = 0; i < modulesSize; ++i) {
            ClassInfo clazz = this.readClassEntry(stream, masterAnnotations, version);
            ModuleInfo module = this.readModuleEntry(stream, clazz);
            modules.put(module.name(), module);
        }
        return modules;
    }

    @Override
    int toDataVersion(int version) {
        return 4;
    }
}


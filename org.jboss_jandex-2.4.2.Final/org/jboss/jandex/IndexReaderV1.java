/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReaderImpl;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodInternal;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.PackedDataInputStream;
import org.jboss.jandex.Type;
import org.jboss.jandex.Utils;

final class IndexReaderV1
extends IndexReaderImpl {
    static final int MIN_VERSION = 2;
    static final int MAX_VERSION = 3;
    private static final byte FIELD_TAG = 1;
    private static final byte METHOD_TAG = 2;
    private static final byte METHOD_PARAMETER_TAG = 3;
    private static final byte CLASS_TAG = 4;
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
    private PackedDataInputStream input;
    private DotName[] classTable;
    private String[] stringTable;
    private HashMap<DotName, List<AnnotationInstance>> masterAnnotations;

    IndexReaderV1(PackedDataInputStream input) {
        this.input = input;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    Index read(int version) throws IOException {
        try {
            PackedDataInputStream stream = this.input;
            this.masterAnnotations = new HashMap();
            this.readClassTable(stream);
            this.readStringTable(stream);
            Index index = this.readClasses(stream, version);
            return index;
        }
        finally {
            this.classTable = null;
            this.stringTable = null;
            this.masterAnnotations = null;
        }
    }

    private Index readClasses(PackedDataInputStream stream, int version) throws IOException {
        int entries = stream.readPackedU32();
        HashMap<DotName, List<ClassInfo>> subclasses = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, List<ClassInfo>> implementors = new HashMap<DotName, List<ClassInfo>>();
        HashMap<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>();
        Map<DotName, List<ClassInfo>> users = Collections.emptyMap();
        this.masterAnnotations = new HashMap();
        for (int i = 0; i < entries; ++i) {
            DotName name = this.classTable[stream.readPackedU32()];
            DotName superName = this.classTable[stream.readPackedU32()];
            short flags = stream.readShort();
            boolean hasNoArgsConstructor = version >= 3 && stream.readBoolean();
            int numIntfs = stream.readPackedU32();
            ArrayList<ClassType> interfaces = new ArrayList<ClassType>(numIntfs);
            for (int j = 0; j < numIntfs; ++j) {
                interfaces.add(new ClassType(this.classTable[stream.readPackedU32()]));
            }
            Type[] interfaceTypes = interfaces.toArray(new Type[interfaces.size()]);
            HashMap<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
            ClassType superClassType = superName == null ? null : new ClassType(superName);
            ClassInfo clazz = new ClassInfo(name, superClassType, flags, interfaceTypes, hasNoArgsConstructor);
            classes.put(name, clazz);
            this.addClassToMap(subclasses, superName, clazz);
            for (Type type : interfaces) {
                this.addClassToMap(implementors, type.name(), clazz);
            }
            this.readAnnotations(stream, annotations, clazz);
            clazz.setAnnotations(annotations);
        }
        return Index.create(this.masterAnnotations, subclasses, implementors, classes, users);
    }

    private void readAnnotations(PackedDataInputStream stream, Map<DotName, List<AnnotationInstance>> annotations, ClassInfo clazz) throws IOException {
        int numAnnotations = stream.readPackedU32();
        for (int j = 0; j < numAnnotations; ++j) {
            DotName annotationName = this.classTable[stream.readPackedU32()];
            int numTargets = stream.readPackedU32();
            for (int k = 0; k < numTargets; ++k) {
                AnnotationTarget target;
                int tag = stream.readPackedU32();
                switch (tag) {
                    case 1: {
                        String name = this.stringTable[stream.readPackedU32()];
                        Type type = this.readType(stream);
                        short flags = stream.readShort();
                        target = new FieldInfo(clazz, Utils.toUTF8(name), type, flags);
                        break;
                    }
                    case 2: {
                        target = this.readMethod(clazz, stream);
                        break;
                    }
                    case 3: {
                        MethodInfo method = this.readMethod(clazz, stream);
                        target = new MethodParameterInfo(method, (short)stream.readPackedU32());
                        break;
                    }
                    case 4: {
                        target = clazz;
                        break;
                    }
                    default: {
                        throw new UnsupportedOperationException();
                    }
                }
                AnnotationValue[] values = this.readAnnotationValues(stream);
                AnnotationInstance instance = new AnnotationInstance(annotationName, target, values);
                this.recordAnnotation(this.masterAnnotations, annotationName, instance);
                this.recordAnnotation(annotations, annotationName, instance);
            }
        }
    }

    private AnnotationValue[] readAnnotationValues(PackedDataInputStream stream) throws IOException {
        int numValues = stream.readPackedU32();
        AnnotationValue[] values = new AnnotationValue[numValues];
        for (int i = 0; i < numValues; ++i) {
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
                    value = new AnnotationValue.ClassValue(name, this.readType(stream));
                    break;
                }
                case 11: {
                    value = new AnnotationValue.EnumValue(name, this.classTable[stream.readPackedU32()], this.stringTable[stream.readPackedU32()]);
                    break;
                }
                case 12: {
                    value = new AnnotationValue.ArrayValue(name, this.readAnnotationValues(stream));
                    break;
                }
                case 13: {
                    DotName nestedName = this.classTable[stream.readPackedU32()];
                    AnnotationInstance nestedInstance = new AnnotationInstance(nestedName, null, this.readAnnotationValues(stream));
                    value = new AnnotationValue.NestedAnnotation(name, nestedInstance);
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid annotation value tag:" + tag);
                }
            }
            values[i] = value;
        }
        return values;
    }

    private MethodInfo readMethod(ClassInfo clazz, PackedDataInputStream stream) throws IOException {
        String name = this.stringTable[stream.readPackedU32()];
        int numArgs = stream.readPackedU32();
        ArrayList<Type> args = new ArrayList<Type>(numArgs);
        for (int i = 0; i < numArgs; ++i) {
            args.add(this.readType(stream));
        }
        Type[] parameters = args.toArray(new Type[args.size()]);
        Type returnType = this.readType(stream);
        short flags = stream.readShort();
        byte[] bytes = Utils.toUTF8(name);
        return new MethodInfo(clazz, bytes, MethodInternal.EMPTY_PARAMETER_NAMES, parameters, returnType, flags);
    }

    private void recordAnnotation(Map<DotName, List<AnnotationInstance>> annotations, DotName annotation, AnnotationInstance instance) {
        List<AnnotationInstance> list = annotations.get(annotation);
        if (list == null) {
            list = new ArrayList<AnnotationInstance>();
            annotations.put(annotation, list);
        }
        list.add(instance);
    }

    private void addClassToMap(HashMap<DotName, List<ClassInfo>> map, DotName name, ClassInfo currentClass) {
        List<ClassInfo> list = map.get(name);
        if (list == null) {
            list = new ArrayList<ClassInfo>();
            map.put(name, list);
        }
        list.add(currentClass);
    }

    private Type readType(PackedDataInputStream stream) throws IOException {
        Type.Kind kind = Type.Kind.fromOrdinal(stream.readByte());
        DotName name = this.classTable[stream.readPackedU32()];
        return Type.create(name, kind);
    }

    private void readStringTable(PackedDataInputStream stream) throws IOException {
        int entries = stream.readPackedU32();
        this.stringTable = new String[entries];
        for (int i = 0; i < entries; ++i) {
            this.stringTable[i] = stream.readUTF();
        }
    }

    private void readClassTable(PackedDataInputStream stream) throws IOException {
        int entries = stream.readPackedU32();
        int lastDepth = -1;
        DotName curr = null;
        this.classTable = new DotName[++entries];
        for (int i = 1; i < entries; ++i) {
            int depth = stream.readPackedU32();
            String local = stream.readUTF();
            if (depth <= lastDepth) {
                while (lastDepth-- >= depth) {
                    curr = curr.prefix();
                }
            }
            this.classTable[i] = curr = new DotName(curr, local, true, false);
            lastDepth = depth;
        }
    }

    @Override
    int toDataVersion(int version) {
        return version;
    }
}


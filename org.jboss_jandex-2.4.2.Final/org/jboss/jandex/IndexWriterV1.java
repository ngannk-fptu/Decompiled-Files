/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexWriterImpl;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.PackedDataOutputStream;
import org.jboss.jandex.StrongInternPool;
import org.jboss.jandex.Type;
import org.jboss.jandex.UnsupportedVersion;

final class IndexWriterV1
extends IndexWriterImpl {
    private static final int MAGIC = -1161945323;
    static final int MIN_VERSION = 1;
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
    private final OutputStream out;
    private StrongInternPool<String> pool;
    StrongInternPool.Index poolIndex;
    private TreeMap<DotName, Integer> classTable;

    IndexWriterV1(OutputStream out) {
        this.out = out;
    }

    @Override
    int write(Index index, int version) throws IOException {
        if (version < 1 || version > 3) {
            throw new UnsupportedVersion("Can't write index version " + version + "; this IndexWriterV1 only supports index versions " + 1 + "-" + 3);
        }
        PackedDataOutputStream stream = new PackedDataOutputStream(new BufferedOutputStream(this.out));
        stream.writeInt(-1161945323);
        stream.writeByte(version);
        this.buildTables(index);
        this.writeClassTable(stream);
        this.writeStringTable(stream);
        this.writeClasses(stream, index, version);
        stream.flush();
        return stream.size();
    }

    private void writeStringTable(PackedDataOutputStream stream) throws IOException {
        stream.writePackedU32(this.pool.size());
        Iterator<String> iter = this.pool.iterator();
        while (iter.hasNext()) {
            String string = iter.next();
            stream.writeUTF(string);
        }
    }

    private void writeClassTable(PackedDataOutputStream stream) throws IOException {
        stream.writePackedU32(this.classTable.size());
        int pos = 1;
        for (Map.Entry<DotName, Integer> entry : this.classTable.entrySet()) {
            entry.setValue(pos++);
            DotName name = entry.getKey();
            assert (name.isComponentized());
            int nameDepth = 0;
            for (DotName prefix = name.prefix(); prefix != null; prefix = prefix.prefix()) {
                ++nameDepth;
            }
            stream.writePackedU32(nameDepth);
            stream.writeUTF(name.local());
        }
    }

    private int positionOf(String string) {
        int i = this.poolIndex.positionOf(string) - 1;
        if (i < 0) {
            throw new IllegalStateException();
        }
        return i;
    }

    private int positionOf(DotName className) {
        Integer i = this.classTable.get(className = this.downgradeName(className));
        if (i == null) {
            throw new IllegalStateException("Class not found in class table:" + className);
        }
        return i;
    }

    private void writeClasses(PackedDataOutputStream stream, Index index, int version) throws IOException {
        Collection<ClassInfo> classes = index.getKnownClasses();
        stream.writePackedU32(classes.size());
        for (ClassInfo clazz : classes) {
            stream.writePackedU32(this.positionOf(clazz.name()));
            stream.writePackedU32(clazz.superName() == null ? 0 : this.positionOf(clazz.superName()));
            stream.writeShort(clazz.flags());
            if (version >= 3) {
                stream.writeBoolean(clazz.hasNoArgsConstructor());
            }
            DotName[] interfaces = clazz.interfaces();
            stream.writePackedU32(interfaces.length);
            for (DotName intf : interfaces) {
                stream.writePackedU32(this.positionOf(intf));
            }
            Set<Map.Entry<DotName, List<AnnotationInstance>>> entrySet = clazz.annotations().entrySet();
            stream.writePackedU32(entrySet.size());
            for (Map.Entry<DotName, List<AnnotationInstance>> entry : entrySet) {
                stream.writePackedU32(this.positionOf(entry.getKey()));
                List<AnnotationInstance> instances = entry.getValue();
                stream.writePackedU32(instances.size());
                for (AnnotationInstance instance : instances) {
                    AnnotationTarget target = instance.target();
                    if (target instanceof FieldInfo) {
                        FieldInfo field = (FieldInfo)target;
                        stream.writeByte(1);
                        stream.writePackedU32(this.positionOf(field.name()));
                        this.writeType(stream, field.type());
                        stream.writeShort(field.flags());
                    } else if (target instanceof MethodInfo) {
                        MethodInfo method = (MethodInfo)target;
                        stream.writeByte(2);
                        stream.writePackedU32(this.positionOf(method.name()));
                        stream.writePackedU32(method.args().length);
                        for (int i = 0; i < method.args().length; ++i) {
                            this.writeType(stream, method.args()[i]);
                        }
                        this.writeType(stream, method.returnType());
                        stream.writeShort(method.flags());
                    } else if (target instanceof MethodParameterInfo) {
                        MethodParameterInfo param = (MethodParameterInfo)target;
                        MethodInfo method = param.method();
                        stream.writeByte(3);
                        stream.writePackedU32(this.positionOf(method.name()));
                        stream.writePackedU32(method.args().length);
                        for (int i = 0; i < method.args().length; ++i) {
                            this.writeType(stream, method.args()[i]);
                        }
                        this.writeType(stream, method.returnType());
                        stream.writeShort(method.flags());
                        stream.writePackedU32(param.position());
                    } else if (target instanceof ClassInfo) {
                        stream.writeByte(4);
                    } else {
                        throw new IllegalStateException("Unknown target");
                    }
                    List<AnnotationValue> values = instance.values();
                    this.writeAnnotationValues(stream, values);
                }
            }
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
            this.writeType(stream, value.asClass());
        } else if (value instanceof AnnotationValue.EnumValue) {
            stream.writeByte(11);
            stream.writePackedU32(this.positionOf(value.asEnumType()));
            stream.writePackedU32(this.positionOf(value.asEnum()));
        } else if (value instanceof AnnotationValue.ArrayValue) {
            AnnotationValue[] array = value.asArray();
            int length = array.length;
            stream.writeByte(12);
            stream.writePackedU32(length);
            for (int i = 0; i < length; ++i) {
                this.writeAnnotationValue(stream, array[i]);
            }
        } else if (value instanceof AnnotationValue.NestedAnnotation) {
            AnnotationInstance instance = value.asNested();
            List<AnnotationValue> values = instance.values();
            stream.writeByte(13);
            stream.writePackedU32(this.positionOf(instance.name()));
            this.writeAnnotationValues(stream, values);
        }
    }

    private void writeType(PackedDataOutputStream stream, Type type) throws IOException {
        stream.writeByte(type.kind().ordinal());
        stream.writePackedU32(this.positionOf(type.name()));
    }

    private void buildTables(Index index) {
        this.pool = new StrongInternPool();
        this.classTable = new TreeMap();
        for (ClassInfo clazz : index.getKnownClasses()) {
            this.addClassName(clazz.name());
            if (clazz.superName() != null) {
                this.addClassName(clazz.superName());
            }
            for (DotName intf : clazz.interfaces()) {
                this.addClassName(intf);
            }
            for (Map.Entry entry : clazz.annotations().entrySet()) {
                this.addClassName((DotName)entry.getKey());
                for (AnnotationInstance instance : (List)entry.getValue()) {
                    AnnotationTarget target = instance.target();
                    if (target instanceof FieldInfo) {
                        FieldInfo field = (FieldInfo)target;
                        this.intern(field.name());
                        this.addClassName(field.type().name());
                    } else if (target instanceof MethodInfo) {
                        MethodInfo method = (MethodInfo)target;
                        this.intern(method.name());
                        for (Type type : method.args()) {
                            this.addClassName(type.name());
                        }
                        this.addClassName(method.returnType().name());
                    } else if (target instanceof MethodParameterInfo) {
                        MethodParameterInfo param = (MethodParameterInfo)target;
                        this.intern(param.method().name());
                        for (Type type : param.method().args()) {
                            this.addClassName(type.name());
                        }
                        this.addClassName(param.method().returnType().name());
                    }
                    for (AnnotationValue value : instance.values()) {
                        this.buildAValueEntries(index, value);
                    }
                }
            }
        }
        this.poolIndex = this.pool.index();
    }

    private void buildAValueEntries(Index index, AnnotationValue value) {
        block3: {
            block6: {
                block5: {
                    block4: {
                        block2: {
                            this.intern(value.name());
                            if (!(value instanceof AnnotationValue.StringValue)) break block2;
                            this.intern(value.asString());
                            break block3;
                        }
                        if (!(value instanceof AnnotationValue.ClassValue)) break block4;
                        this.addClassName(value.asClass().name());
                        break block3;
                    }
                    if (!(value instanceof AnnotationValue.EnumValue)) break block5;
                    this.addClassName(value.asEnumType());
                    this.intern(value.asEnum());
                    break block3;
                }
                if (!(value instanceof AnnotationValue.ArrayValue)) break block6;
                for (AnnotationValue entry : value.asArray()) {
                    this.buildAValueEntries(index, entry);
                }
                break block3;
            }
            if (!(value instanceof AnnotationValue.NestedAnnotation)) break block3;
            AnnotationInstance instance = value.asNested();
            List<AnnotationValue> values = instance.values();
            this.addClassName(instance.name());
            for (AnnotationValue entry : values) {
                this.buildAValueEntries(index, entry);
            }
        }
    }

    private String intern(String name) {
        return this.pool.intern(name);
    }

    private void addClassName(DotName name) {
        DotName prefix;
        if (!this.classTable.containsKey(name = this.downgradeName(name))) {
            this.classTable.put(name, null);
        }
        if ((prefix = name.prefix()) != null) {
            this.addClassName(prefix);
        }
    }

    private DotName downgradeName(DotName name) {
        DotName n = name;
        StringBuilder builder = null;
        while (n.isInner()) {
            if (builder == null) {
                builder = new StringBuilder();
            }
            builder.insert(0, n.local()).insert(0, '$');
            if (!n.prefix().isInner()) {
                builder.insert(0, n.prefix().local());
                name = new DotName(n.prefix().prefix(), builder.toString(), true, false);
            }
            n = n.prefix();
        }
        return name;
    }
}


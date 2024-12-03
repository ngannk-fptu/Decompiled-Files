/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.PortableContext;
import com.hazelcast.internal.serialization.impl.ClassDefinitionImpl;
import com.hazelcast.internal.serialization.impl.ClassDefinitionWriter;
import com.hazelcast.internal.serialization.impl.FieldDefinitionImpl;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.query.impl.getters.ExtractorHelper;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

final class PortableContextImpl
implements PortableContext {
    private static final Pattern NESTED_FIELD_PATTERN = Pattern.compile("\\.");
    private final int version;
    private final ConcurrentHashMap<Integer, ClassDefinitionContext> classDefContextMap = new ConcurrentHashMap();
    private final InternalSerializationService serializationService;
    private final ConstructorFunction<Integer, ClassDefinitionContext> constructorFunction = new ConstructorFunction<Integer, ClassDefinitionContext>(){

        @Override
        public ClassDefinitionContext createNew(Integer arg) {
            return new ClassDefinitionContext(arg);
        }
    };

    PortableContextImpl(InternalSerializationService serializationService, int version) {
        this.serializationService = serializationService;
        this.version = version;
    }

    @Override
    public int getClassVersion(int factoryId, int classId) {
        return this.getClassDefContext(factoryId).getClassVersion(classId);
    }

    @Override
    public void setClassVersion(int factoryId, int classId, int version) {
        this.getClassDefContext(factoryId).setClassVersion(classId, version);
    }

    @Override
    public ClassDefinition lookupClassDefinition(int factoryId, int classId, int version) {
        return this.getClassDefContext(factoryId).lookup(classId, version);
    }

    @Override
    public ClassDefinition lookupClassDefinition(Data data) throws IOException {
        int version;
        int classId;
        if (!data.isPortable()) {
            throw new IllegalArgumentException("Data is not Portable!");
        }
        BufferObjectDataInput in = this.serializationService.createObjectDataInput(data);
        int factoryId = in.readInt();
        ClassDefinition classDefinition = this.lookupClassDefinition(factoryId, classId = in.readInt(), version = in.readInt());
        if (classDefinition == null) {
            classDefinition = this.readClassDefinition(in, factoryId, classId, version);
        }
        return classDefinition;
    }

    ClassDefinition readClassDefinition(BufferObjectDataInput in, int factoryId, int classId, int version) throws IOException {
        boolean register = true;
        ClassDefinitionBuilder builder = new ClassDefinitionBuilder(factoryId, classId, version);
        in.readInt();
        int fieldCount = in.readInt();
        int offset = in.position();
        for (int i = 0; i < fieldCount; ++i) {
            int pos = in.readInt(offset + i * 4);
            in.position(pos);
            int len = in.readShort();
            char[] chars = new char[len];
            for (int k = 0; k < len; ++k) {
                chars[k] = (char)in.readUnsignedByte();
            }
            FieldType type = FieldType.get(in.readByte());
            String name = new String(chars);
            int fieldFactoryId = 0;
            int fieldClassId = 0;
            int fieldVersion = version;
            if (type == FieldType.PORTABLE) {
                if (in.readBoolean()) {
                    register = false;
                }
                fieldFactoryId = in.readInt();
                fieldClassId = in.readInt();
                if (register) {
                    fieldVersion = in.readInt();
                    this.readClassDefinition(in, fieldFactoryId, fieldClassId, fieldVersion);
                }
            } else if (type == FieldType.PORTABLE_ARRAY) {
                int k = in.readInt();
                fieldFactoryId = in.readInt();
                fieldClassId = in.readInt();
                if (k > 0) {
                    int p = in.readInt();
                    in.position(p);
                    fieldVersion = in.readInt();
                    this.readClassDefinition(in, fieldFactoryId, fieldClassId, fieldVersion);
                } else {
                    register = false;
                }
            }
            builder.addField(new FieldDefinitionImpl(i, name, type, fieldFactoryId, fieldClassId, fieldVersion));
        }
        ClassDefinition classDefinition = builder.build();
        if (register) {
            classDefinition = this.registerClassDefinition(classDefinition);
        }
        return classDefinition;
    }

    @Override
    public ClassDefinition registerClassDefinition(ClassDefinition cd) {
        return this.getClassDefContext(cd.getFactoryId()).register(cd);
    }

    @Override
    public ClassDefinition lookupOrRegisterClassDefinition(Portable p) throws IOException {
        int portableVersion = SerializationUtil.getPortableVersion(p, this.version);
        ClassDefinition cd = this.lookupClassDefinition(p.getFactoryId(), p.getClassId(), portableVersion);
        if (cd == null) {
            ClassDefinitionWriter writer = new ClassDefinitionWriter(this, p.getFactoryId(), p.getClassId(), portableVersion);
            p.writePortable(writer);
            cd = writer.registerAndGet();
        }
        return cd;
    }

    @Override
    public FieldDefinition getFieldDefinition(ClassDefinition classDef, String name) {
        FieldDefinition fd = classDef.getField(name);
        if (fd == null) {
            if (name.contains(".")) {
                String[] fieldNames = NESTED_FIELD_PATTERN.split(name);
                if (fieldNames.length <= 1) {
                    return fd;
                }
                ClassDefinition currentClassDef = classDef;
                for (int i = 0; i < fieldNames.length; ++i) {
                    fd = currentClassDef.getField(fieldNames[i]);
                    if (fd == null) {
                        fd = currentClassDef.getField(ExtractorHelper.extractAttributeNameNameWithoutArguments(fieldNames[i]));
                    }
                    if (i != fieldNames.length - 1) {
                        if (fd == null) {
                            throw new IllegalArgumentException("Unknown field: " + name);
                        }
                        currentClassDef = this.lookupClassDefinition(fd.getFactoryId(), fd.getClassId(), fd.getVersion());
                        if (currentClassDef != null) continue;
                        throw new IllegalArgumentException("Not a registered Portable field: " + fd);
                    }
                    break;
                }
            } else {
                fd = classDef.getField(ExtractorHelper.extractAttributeNameNameWithoutArguments(name));
            }
        }
        return fd;
    }

    private ClassDefinitionContext getClassDefContext(int factoryId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.classDefContextMap, factoryId, this.constructorFunction);
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public ManagedContext getManagedContext() {
        return this.serializationService.getManagedContext();
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.serializationService.getByteOrder();
    }

    private final class ClassDefinitionContext {
        final int factoryId;
        final ConcurrentMap<Long, ClassDefinition> versionedDefinitions = new ConcurrentHashMap<Long, ClassDefinition>();
        final ConcurrentMap<Integer, Integer> currentClassVersions = new ConcurrentHashMap<Integer, Integer>();

        private ClassDefinitionContext(int factoryId) {
            this.factoryId = factoryId;
        }

        int getClassVersion(int classId) {
            Integer version = (Integer)this.currentClassVersions.get(classId);
            return version != null ? version : -1;
        }

        void setClassVersion(int classId, int version) {
            Integer current = this.currentClassVersions.putIfAbsent(classId, version);
            if (current != null && current != version) {
                throw new IllegalArgumentException("Class-id: " + classId + " is already registered!");
            }
        }

        ClassDefinition lookup(int classId, int version) {
            long versionedClassId = Bits.combineToLong(classId, version);
            return (ClassDefinition)this.versionedDefinitions.get(versionedClassId);
        }

        ClassDefinition register(ClassDefinition cd) {
            long versionedClassId;
            ClassDefinition currentCd;
            if (cd == null) {
                return null;
            }
            if (cd.getFactoryId() != this.factoryId) {
                throw new HazelcastSerializationException("Invalid factory-id! " + this.factoryId + " -> " + cd);
            }
            if (cd instanceof ClassDefinitionImpl) {
                ClassDefinitionImpl cdImpl = (ClassDefinitionImpl)cd;
                cdImpl.setVersionIfNotSet(PortableContextImpl.this.getVersion());
            }
            if ((currentCd = this.versionedDefinitions.putIfAbsent(versionedClassId = Bits.combineToLong(cd.getClassId(), cd.getVersion()), cd)) == null) {
                return cd;
            }
            if (currentCd instanceof ClassDefinitionImpl) {
                if (!currentCd.equals(cd)) {
                    throw new HazelcastSerializationException("Incompatible class-definitions with same class-id: " + cd + " VS " + currentCd);
                }
                return currentCd;
            }
            this.versionedDefinitions.put(versionedClassId, cd);
            return cd;
        }
    }
}


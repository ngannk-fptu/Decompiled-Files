/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.internal.serialization.impl.DefaultPortableReader;
import com.hazelcast.internal.serialization.impl.DefaultPortableWriter;
import com.hazelcast.internal.serialization.impl.MorphingPortableReader;
import com.hazelcast.internal.serialization.impl.PortableContextImpl;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.StreamSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class PortableSerializer
implements StreamSerializer<Portable> {
    private final PortableContextImpl context;
    private final Map<Integer, PortableFactory> factories = new HashMap<Integer, PortableFactory>();

    PortableSerializer(PortableContextImpl context, Map<Integer, ? extends PortableFactory> portableFactories) {
        this.context = context;
        this.factories.putAll(portableFactories);
    }

    @Override
    public int getTypeId() {
        return -1;
    }

    @Override
    public void write(ObjectDataOutput out, Portable p) throws IOException {
        if (!(out instanceof BufferObjectDataOutput)) {
            throw new IllegalArgumentException("ObjectDataOutput must be instance of BufferObjectDataOutput!");
        }
        if (p.getClassId() == 0) {
            throw new IllegalArgumentException("Portable class ID cannot be zero!");
        }
        out.writeInt(p.getFactoryId());
        out.writeInt(p.getClassId());
        this.writeInternal((BufferObjectDataOutput)out, p);
    }

    void writeInternal(BufferObjectDataOutput out, Portable p) throws IOException {
        ClassDefinition cd = this.context.lookupOrRegisterClassDefinition(p);
        out.writeInt(cd.getVersion());
        DefaultPortableWriter writer = new DefaultPortableWriter(this, out, cd);
        p.writePortable(writer);
        writer.end();
    }

    @Override
    public Portable read(ObjectDataInput in) throws IOException {
        if (!(in instanceof BufferObjectDataInput)) {
            throw new IllegalArgumentException("ObjectDataInput must be instance of BufferObjectDataInput!");
        }
        int factoryId = in.readInt();
        int classId = in.readInt();
        return this.read((BufferObjectDataInput)in, factoryId, classId);
    }

    private Portable read(BufferObjectDataInput in, int factoryId, int classId) throws IOException {
        int version = in.readInt();
        Portable portable = this.createNewPortableInstance(factoryId, classId);
        int portableVersion = this.findPortableVersion(factoryId, classId, portable);
        DefaultPortableReader reader = this.createReader(in, factoryId, classId, version, portableVersion);
        portable.readPortable(reader);
        reader.end();
        return portable;
    }

    private int findPortableVersion(int factoryId, int classId, Portable portable) {
        int currentVersion = this.context.getClassVersion(factoryId, classId);
        if (currentVersion < 0 && (currentVersion = SerializationUtil.getPortableVersion(portable, this.context.getVersion())) > 0) {
            this.context.setClassVersion(factoryId, classId, currentVersion);
        }
        return currentVersion;
    }

    private Portable createNewPortableInstance(int factoryId, int classId) {
        PortableFactory portableFactory = this.factories.get(factoryId);
        if (portableFactory == null) {
            throw new HazelcastSerializationException("Could not find PortableFactory for factory-id: " + factoryId);
        }
        Portable portable = portableFactory.create(classId);
        if (portable == null) {
            throw new HazelcastSerializationException("Could not create Portable for class-id: " + classId);
        }
        return portable;
    }

    Portable readAndInitialize(BufferObjectDataInput in, int factoryId, int classId) throws IOException {
        Portable p = this.read(in, factoryId, classId);
        ManagedContext managedContext = this.context.getManagedContext();
        return managedContext != null ? (Portable)managedContext.initialize(p) : p;
    }

    DefaultPortableReader createReader(BufferObjectDataInput in) throws IOException {
        int factoryId = in.readInt();
        int classId = in.readInt();
        int version = in.readInt();
        return this.createReader(in, factoryId, classId, version, version);
    }

    DefaultPortableReader createMorphingReader(BufferObjectDataInput in) throws IOException {
        int factoryId = in.readInt();
        int classId = in.readInt();
        int version = in.readInt();
        Portable portable = this.createNewPortableInstance(factoryId, classId);
        int portableVersion = this.findPortableVersion(factoryId, classId, portable);
        return this.createReader(in, factoryId, classId, version, portableVersion);
    }

    public ClassDefinition setupPositionAndDefinition(BufferObjectDataInput in, int factoryId, int classId, int version) throws IOException {
        ClassDefinition cd;
        int effectiveVersion = version;
        if (effectiveVersion < 0) {
            effectiveVersion = this.context.getVersion();
        }
        if ((cd = this.context.lookupClassDefinition(factoryId, classId, effectiveVersion)) == null) {
            int begin = in.position();
            cd = this.context.readClassDefinition(in, factoryId, classId, effectiveVersion);
            in.position(begin);
        }
        return cd;
    }

    public DefaultPortableReader createReader(BufferObjectDataInput in, int factoryId, int classId, int version, int portableVersion) throws IOException {
        ClassDefinition cd = this.setupPositionAndDefinition(in, factoryId, classId, version);
        DefaultPortableReader reader = portableVersion == cd.getVersion() ? new DefaultPortableReader(this, in, cd) : new MorphingPortableReader(this, in, cd);
        return reader;
    }

    @Override
    public void destroy() {
        this.factories.clear();
    }
}


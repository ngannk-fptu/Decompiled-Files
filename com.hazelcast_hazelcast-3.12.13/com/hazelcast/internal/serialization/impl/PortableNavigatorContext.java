/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.PortablePathCursor;
import com.hazelcast.internal.serialization.impl.PortableSerializer;
import com.hazelcast.internal.serialization.impl.PortableUtils;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.query.impl.getters.ExtractorHelper;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

final class PortableNavigatorContext {
    private BufferObjectDataInput in;
    private int offset;
    private FieldDefinition fd;
    private Deque<NavigationFrame> multiPositions;
    private int finalPosition;
    private ClassDefinition cd;
    private PortableSerializer serializer;
    private final int initPosition;
    private final ClassDefinition initCd;
    private final int initOffset;
    private final int initFinalPosition;
    private final PortableSerializer initSerializer;

    PortableNavigatorContext(BufferObjectDataInput in, ClassDefinition cd, PortableSerializer serializer) {
        this.in = in;
        this.cd = cd;
        this.serializer = serializer;
        this.initFinalPositionAndOffset(in, cd);
        this.initCd = cd;
        this.initSerializer = serializer;
        this.initPosition = in.position();
        this.initFinalPosition = this.finalPosition;
        this.initOffset = this.offset;
    }

    private void initFinalPositionAndOffset(BufferObjectDataInput in, ClassDefinition cd) {
        int fieldCount;
        try {
            this.finalPosition = in.readInt();
            fieldCount = in.readInt();
        }
        catch (IOException e) {
            throw new HazelcastSerializationException(e);
        }
        if (fieldCount != cd.getFieldCount()) {
            throw new IllegalStateException("Field count[" + fieldCount + "] in stream does not match " + cd);
        }
        this.offset = in.position();
    }

    void reset() {
        this.cd = this.initCd;
        this.serializer = this.initSerializer;
        this.in.position(this.initPosition);
        this.finalPosition = this.initFinalPosition;
        this.offset = this.initOffset;
    }

    BufferObjectDataInput getIn() {
        return this.in;
    }

    int getCurrentOffset() {
        return this.offset;
    }

    int getCurrentFinalPosition() {
        return this.finalPosition;
    }

    FieldDefinition getCurrentFieldDefinition() {
        return this.fd;
    }

    ClassDefinition getCurrentClassDefinition() {
        return this.cd;
    }

    FieldType getCurrentFieldType() {
        return this.fd.getType();
    }

    boolean isCurrentFieldOfType(FieldType type) {
        return this.fd.getType() == type;
    }

    boolean areThereMultiPositions() {
        return this.multiPositions != null && !this.multiPositions.isEmpty();
    }

    NavigationFrame pollFirstMultiPosition() {
        return this.multiPositions.pollFirst();
    }

    void advanceContextToNextPortableToken(int factoryId, int classId, int version) throws IOException {
        this.cd = this.serializer.setupPositionAndDefinition(this.in, factoryId, classId, version);
        this.initFinalPositionAndOffset(this.in, this.cd);
    }

    void advanceContextToGivenFrame(NavigationFrame frame) {
        this.in.position(frame.streamPosition);
        this.offset = frame.streamOffset;
        this.cd = frame.cd;
    }

    void setupContextForGivenPathToken(PortablePathCursor path) {
        String fieldName = path.token();
        this.fd = this.cd.getField(fieldName);
        if (this.fd != null) {
            return;
        }
        fieldName = ExtractorHelper.extractAttributeNameNameWithoutArguments(path.token());
        this.fd = this.cd.getField(fieldName);
        if (this.fd == null || fieldName == null) {
            throw PortableUtils.createUnknownFieldException(this, path.path());
        }
    }

    boolean trySetupContextForSingleTokenPath(String path) {
        this.fd = this.cd.getField(path);
        return this.fd != null;
    }

    void populateAnyNavigationFrames(int pathTokenIndex, int len) {
        if (this.multiPositions == null) {
            this.multiPositions = new ArrayDeque<NavigationFrame>();
        }
        for (int cellIndex = len - 1; cellIndex > 0; --cellIndex) {
            this.multiPositions.addFirst(new NavigationFrame(this.cd, pathTokenIndex, cellIndex, this.in.position(), this.offset));
        }
    }

    static class NavigationFrame {
        final ClassDefinition cd;
        final int pathTokenIndex;
        final int arrayIndex;
        final int streamPosition;
        final int streamOffset;

        NavigationFrame(ClassDefinition cd, int pathTokenIndex, int arrayIndex, int streamPosition, int streamOffset) {
            this.cd = cd;
            this.pathTokenIndex = pathTokenIndex;
            this.arrayIndex = arrayIndex;
            this.streamPosition = streamPosition;
            this.streamOffset = streamOffset;
        }
    }
}


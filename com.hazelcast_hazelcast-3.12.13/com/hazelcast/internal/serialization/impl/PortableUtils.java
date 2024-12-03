/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.PortableNavigatorContext;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.query.impl.getters.ExtractorHelper;
import java.io.IOException;

final class PortableUtils {
    private PortableUtils() {
    }

    static int validateAndGetArrayQuantifierFromCurrentToken(String token, String fullPath) {
        String quantifier = ExtractorHelper.extractArgumentsFromAttributeName(token);
        if (quantifier == null) {
            throw new IllegalArgumentException("Malformed quantifier in " + fullPath);
        }
        int index = Integer.parseInt(quantifier);
        if (index < 0) {
            throw new IllegalArgumentException("Array index " + index + " cannot be negative in " + fullPath);
        }
        return index;
    }

    static int getPortableArrayCellPosition(BufferObjectDataInput in, int streamPosition, int cellIndex) throws IOException {
        return in.readInt(streamPosition + cellIndex * 4);
    }

    static int getStreamPositionOfTheField(FieldDefinition fd, BufferObjectDataInput in, int offset) throws IOException {
        int pos = in.readInt(offset + fd.getIndex() * 4);
        short len = in.readShort(pos);
        return pos + 2 + len + 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int getArrayLengthOfTheField(FieldDefinition fd, BufferObjectDataInput in, int offset) throws IOException {
        int originalPos = in.position();
        try {
            int pos = PortableUtils.getStreamPositionOfTheField(fd, in, offset);
            in.position(pos);
            int n = in.readInt();
            return n;
        }
        finally {
            in.position(originalPos);
        }
    }

    static boolean isCurrentPathTokenWithoutQuantifier(String token) {
        return !token.endsWith("]");
    }

    static boolean isCurrentPathTokenWithAnyQuantifier(String token) {
        return token.endsWith("[any]");
    }

    static HazelcastSerializationException createUnknownFieldException(PortableNavigatorContext ctx, String fullPath) {
        return new HazelcastSerializationException("Unknown field name: '" + fullPath + "' for ClassDefinition {id: " + ctx.getCurrentClassDefinition().getClassId() + ", version: " + ctx.getCurrentClassDefinition().getVersion() + "}");
    }

    static IllegalArgumentException createWrongUseOfAnyOperationException(PortableNavigatorContext ctx, String fullPath) {
        return new IllegalArgumentException("Wrong use of any operator: '" + fullPath + "' for ClassDefinition {id: " + ctx.getCurrentClassDefinition().getClassId() + ", version: " + ctx.getCurrentClassDefinition().getVersion() + "}");
    }

    static void validateArrayType(ClassDefinition cd, FieldDefinition fd, String fullPath) {
        if (!fd.getType().isArrayType()) {
            throw new IllegalArgumentException("Wrong use of array operator: '" + fullPath + "' for ClassDefinition {id: " + cd.getClassId() + ", version: " + cd.getVersion() + "}");
        }
    }

    static void validateFactoryAndClass(FieldDefinition fd, int factoryId, int classId, String fullPath) {
        if (factoryId != fd.getFactoryId()) {
            throw new IllegalArgumentException("Invalid factoryId! Expected: " + fd.getFactoryId() + ", Current: " + factoryId + " in path " + fullPath);
        }
        if (classId != fd.getClassId()) {
            throw new IllegalArgumentException("Invalid classId! Expected: " + fd.getClassId() + ", Current: " + classId + " in path " + fullPath);
        }
    }
}


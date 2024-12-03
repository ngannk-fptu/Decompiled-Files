/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.PortablePosition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import java.util.Collections;
import java.util.List;

final class PortablePositionFactory {
    private static final PortableSinglePosition NIL_NOT_LEAF = PortablePositionFactory.nil(false);
    private static final PortableSinglePosition NIL_LEAF_ANY = PortablePositionFactory.nil(true, true);
    private static final PortableSinglePosition NIL_NOT_LEAF_ANY = PortablePositionFactory.nil(false, true);
    private static final PortableSinglePosition EMPTY_LEAF_ANY = PortablePositionFactory.empty(true, true);
    private static final PortableSinglePosition EMPTY_NOT_LEAF_ANY = PortablePositionFactory.empty(false, true);

    private PortablePositionFactory() {
    }

    static PortablePosition nilAnyPosition(boolean lastToken) {
        return lastToken ? NIL_LEAF_ANY : NIL_NOT_LEAF_ANY;
    }

    static PortablePosition emptyAnyPosition(boolean lastToken) {
        return lastToken ? EMPTY_LEAF_ANY : EMPTY_NOT_LEAF_ANY;
    }

    static PortablePosition nilNotLeafPosition() {
        return NIL_NOT_LEAF;
    }

    static PortableSinglePosition createSinglePrimitivePosition(FieldDefinition fd, int streamPosition, int index, boolean leaf) {
        return new PortableSinglePosition(fd, streamPosition, index, leaf);
    }

    static PortableSinglePosition createSinglePortablePosition(FieldDefinition fd, int streamPosition, int factoryId, int classId, boolean nil, boolean leaf) {
        PortableSinglePosition position = new PortableSinglePosition(fd, streamPosition, -1, leaf);
        position.factoryId = factoryId;
        position.classId = classId;
        position.nil = nil;
        return position;
    }

    static PortableSinglePosition createSinglePortablePosition(FieldDefinition fd, int streamPosition, int factoryId, int classId, int index, int len, boolean leaf) {
        PortableSinglePosition position = new PortableSinglePosition(fd, streamPosition, index, leaf);
        position.factoryId = factoryId;
        position.classId = classId;
        position.len = len;
        position.nil = PortablePositionFactory.isEmptyNil(position);
        return position;
    }

    static PortableMultiPosition createMultiPosition(PortablePosition position) {
        return new PortableMultiPosition(position);
    }

    static PortableMultiPosition createMultiPosition(List<PortablePosition> positions) {
        return new PortableMultiPosition(positions);
    }

    static PortableSinglePosition empty(boolean leaf, boolean any) {
        PortableSinglePosition position = new PortableSinglePosition();
        position.len = 0;
        position.leaf = leaf;
        position.any = any;
        position.nil = PortablePositionFactory.isEmptyNil(position);
        return position;
    }

    private static boolean isEmptyNil(PortableSinglePosition position) {
        return position.isEmpty() && (!position.isLeaf() || position.getIndex() >= 0);
    }

    static PortableSinglePosition nil(boolean leaf) {
        PortableSinglePosition position = new PortableSinglePosition();
        position.nil = true;
        position.leaf = leaf;
        return position;
    }

    static PortableSinglePosition nil(boolean leaf, boolean any) {
        PortableSinglePosition position = new PortableSinglePosition();
        position.nil = true;
        position.leaf = leaf;
        position.any = any;
        return position;
    }

    private static class PortableMultiPosition
    extends PortableSinglePosition {
        private final List<PortablePosition> positions;

        PortableMultiPosition(PortablePosition position) {
            this.positions = Collections.singletonList(position);
        }

        PortableMultiPosition(List<PortablePosition> positions) {
            this.positions = positions;
        }

        @Override
        public boolean isMultiPosition() {
            return true;
        }

        @Override
        public FieldType getType() {
            if (this.positions.isEmpty()) {
                return null;
            }
            return this.positions.iterator().next().getType();
        }

        @Override
        public List<PortablePosition> asMultiPosition() {
            return this.positions;
        }
    }

    private static class PortableSinglePosition
    implements PortablePosition {
        private FieldDefinition fd;
        private int streamPosition;
        private boolean nil;
        private int index = -1;
        private int len = -1;
        private int factoryId = -1;
        private int classId = -1;
        private boolean leaf;
        private boolean any;

        PortableSinglePosition() {
        }

        PortableSinglePosition(FieldDefinition fd, int streamPosition, int index, boolean leaf) {
            this.fd = fd;
            this.streamPosition = streamPosition;
            this.index = index;
            this.leaf = leaf;
        }

        @Override
        public int getStreamPosition() {
            return this.streamPosition;
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        @Override
        public boolean isNull() {
            return this.nil;
        }

        @Override
        public int getLen() {
            return this.len;
        }

        @Override
        public boolean isEmpty() {
            return this.len == 0;
        }

        @Override
        public boolean isNullOrEmpty() {
            return this.isNull() || this.isEmpty();
        }

        @Override
        public boolean isLeaf() {
            return this.leaf;
        }

        @Override
        public boolean isAny() {
            return this.any;
        }

        @Override
        public int getFactoryId() {
            return this.factoryId;
        }

        @Override
        public int getClassId() {
            return this.classId;
        }

        @Override
        public boolean isMultiPosition() {
            return false;
        }

        @Override
        public List<PortablePosition> asMultiPosition() {
            throw new IllegalArgumentException("This position is not a multi-position!");
        }

        @Override
        public FieldType getType() {
            if (this.fd != null) {
                return this.fd.getType();
            }
            return null;
        }
    }
}


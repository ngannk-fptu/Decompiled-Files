/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.PortableNavigatorContext;
import com.hazelcast.internal.serialization.impl.PortablePathCursor;
import com.hazelcast.internal.serialization.impl.PortablePosition;
import com.hazelcast.internal.serialization.impl.PortablePositionFactory;
import com.hazelcast.internal.serialization.impl.PortableUtils;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.serialization.FieldType;
import java.io.IOException;
import java.util.LinkedList;

final class PortablePositionNavigator {
    private static final boolean SINGLE_CELL_ACCESS = true;
    private static final boolean WHOLE_ARRAY_ACCESS = false;

    private PortablePositionNavigator() {
    }

    static PortablePosition findPositionForReading(PortableNavigatorContext ctx, String pathString, PortablePathCursor path) throws IOException {
        PortablePosition result = PortablePositionNavigator.findPositionForReadingAssumingSingleAttributePath(ctx, pathString, path);
        if (result != null) {
            return result;
        }
        return PortablePositionNavigator.findPositionForReadingComplexPath(ctx, pathString, path);
    }

    private static PortablePosition findPositionForReadingAssumingSingleAttributePath(PortableNavigatorContext ctx, String pathString, PortablePathCursor path) throws IOException {
        PortablePosition result;
        path.initWithSingleTokenPath(pathString);
        if (ctx.trySetupContextForSingleTokenPath(path.path()) && (result = PortablePositionNavigator.createPositionForReadAccess(ctx, path, -1)) != null) {
            return result;
        }
        return null;
    }

    private static PortablePosition findPositionForReadingComplexPath(PortableNavigatorContext ctx, String pathString, PortablePathCursor path) throws IOException {
        path.init(pathString);
        PortablePosition result = PortablePositionNavigator.navigateThroughAllTokensAndReturnPositionForReading(ctx, path, null);
        if (ctx.areThereMultiPositions()) {
            return PortablePositionNavigator.processPendingMultiPositionsAndReturnMultiResult(ctx, path, result);
        }
        return PortablePositionNavigator.returnSingleResultWhenNoMultiPositions(path, result);
    }

    private static PortablePosition navigateThroughAllTokensAndReturnPositionForReading(PortableNavigatorContext ctx, PortablePathCursor path, PortableNavigatorContext.NavigationFrame frame) throws IOException {
        PortablePosition result;
        while ((result = PortablePositionNavigator.navigateToPathToken(ctx, path, frame)) == null || !result.isNullOrEmpty()) {
            frame = null;
            if (path.advanceToNextToken()) continue;
        }
        if (result == null) {
            throw PortableUtils.createUnknownFieldException(ctx, path.path());
        }
        return result;
    }

    private static PortablePosition navigateToPathToken(PortableNavigatorContext ctx, PortablePathCursor path, PortableNavigatorContext.NavigationFrame frame) throws IOException {
        ctx.setupContextForGivenPathToken(path);
        if (PortableUtils.isCurrentPathTokenWithoutQuantifier(path.token())) {
            PortablePosition result = PortablePositionNavigator.navigateToPathTokenWithoutQuantifier(ctx, path);
            if (result != null) {
                return result;
            }
        } else if (PortableUtils.isCurrentPathTokenWithAnyQuantifier(path.token())) {
            PortablePosition result = PortablePositionNavigator.navigateToPathTokenWithAnyQuantifier(ctx, path, frame);
            if (result != null) {
                return result;
            }
        } else {
            PortablePosition result = PortablePositionNavigator.navigateToPathTokenWithNumberQuantifier(ctx, path);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static PortablePosition navigateToPathTokenWithoutQuantifier(PortableNavigatorContext ctx, PortablePathCursor path) throws IOException {
        if (path.isLastToken()) {
            return PortablePositionNavigator.createPositionForReadAccess(ctx, path);
        }
        if (!PortablePositionNavigator.navigateContextToNextPortableTokenFromPortableField(ctx)) {
            return PortablePositionFactory.nilNotLeafPosition();
        }
        return null;
    }

    private static PortablePosition returnSingleResultWhenNoMultiPositions(PortablePathCursor path, PortablePosition result) {
        if (!result.isNullOrEmpty() && path.isAnyPath()) {
            return PortablePositionFactory.createMultiPosition(result);
        }
        return result;
    }

    private static PortablePosition processPendingMultiPositionsAndReturnMultiResult(PortableNavigatorContext ctx, PortablePathCursor path, PortablePosition result) throws IOException {
        LinkedList<PortablePosition> positions = new LinkedList<PortablePosition>();
        positions.add(result);
        while (ctx.areThereMultiPositions()) {
            PortableNavigatorContext.NavigationFrame frame = ctx.pollFirstMultiPosition();
            PortablePositionNavigator.setupContextAndPathWithFrameState(ctx, path, frame);
            result = PortablePositionNavigator.navigateThroughAllTokensAndReturnPositionForReading(ctx, path, frame);
            positions.add(result);
        }
        return PortablePositionFactory.createMultiPosition(positions);
    }

    private static void setupContextAndPathWithFrameState(PortableNavigatorContext ctx, PortablePathCursor path, PortableNavigatorContext.NavigationFrame frame) {
        ctx.advanceContextToGivenFrame(frame);
        path.index(frame.pathTokenIndex);
    }

    private static PortablePosition navigateToPathTokenWithAnyQuantifier(PortableNavigatorContext ctx, PortablePathCursor path, PortableNavigatorContext.NavigationFrame frame) throws IOException {
        PortableUtils.validateArrayType(ctx.getCurrentClassDefinition(), ctx.getCurrentFieldDefinition(), path.path());
        if (ctx.isCurrentFieldOfType(FieldType.PORTABLE_ARRAY)) {
            PortablePosition result = PortablePositionNavigator.navigateToPathTokenWithAnyQuantifierInPortableArray(ctx, path, frame);
            if (result != null) {
                return result;
            }
        } else {
            return PortablePositionNavigator.navigateToPathTokenWithAnyQuantifierInPrimitiveArray(ctx, path, frame);
        }
        return null;
    }

    private static PortablePosition navigateToPathTokenWithAnyQuantifierInPortableArray(PortableNavigatorContext ctx, PortablePathCursor path, PortableNavigatorContext.NavigationFrame frame) throws IOException {
        if (frame == null) {
            int len = PortablePositionNavigator.getArrayLengthOfTheField(ctx);
            PortablePosition result = PortablePositionNavigator.doValidateArrayLengthForAnyQuantifier(len, path.isLastToken());
            if (result != null) {
                return result;
            }
            ctx.populateAnyNavigationFrames(path.index(), len);
            int cellIndex = 0;
            result = PortablePositionNavigator.doNavigateToPortableArrayCell(ctx, path, cellIndex);
            if (result != null) {
                return result;
            }
        } else {
            PortablePosition result = PortablePositionNavigator.doNavigateToPortableArrayCell(ctx, path, frame.arrayIndex);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static PortablePosition doValidateArrayLengthForAnyQuantifier(int len, boolean lastToken) {
        if (len == 0) {
            return PortablePositionFactory.emptyAnyPosition(lastToken);
        }
        if (len == -1) {
            return PortablePositionFactory.nilAnyPosition(lastToken);
        }
        return null;
    }

    private static PortablePosition doNavigateToPortableArrayCell(PortableNavigatorContext ctx, PortablePathCursor path, int index) throws IOException {
        if (path.isLastToken()) {
            return PortablePositionNavigator.createPositionForReadAccess(ctx, path, index);
        }
        PortablePositionNavigator.navigateContextToNextPortableTokenFromPortableArrayCell(ctx, path, index);
        return null;
    }

    private static PortablePosition navigateToPathTokenWithAnyQuantifierInPrimitiveArray(PortableNavigatorContext ctx, PortablePathCursor path, PortableNavigatorContext.NavigationFrame frame) throws IOException {
        if (frame == null) {
            if (path.isLastToken()) {
                int len = PortablePositionNavigator.getArrayLengthOfTheField(ctx);
                PortablePosition result = PortablePositionNavigator.doValidateArrayLengthForAnyQuantifier(len, path.isLastToken());
                if (result != null) {
                    return result;
                }
                ctx.populateAnyNavigationFrames(path.index(), len);
                return PortablePositionNavigator.createPositionForReadAccess(ctx, path, 0);
            }
            throw PortableUtils.createWrongUseOfAnyOperationException(ctx, path.path());
        }
        if (path.isLastToken()) {
            return PortablePositionNavigator.createPositionForReadAccess(ctx, path, frame.arrayIndex);
        }
        throw PortableUtils.createWrongUseOfAnyOperationException(ctx, path.path());
    }

    private static PortablePosition navigateToPathTokenWithNumberQuantifier(PortableNavigatorContext ctx, PortablePathCursor path) throws IOException {
        PortableUtils.validateArrayType(ctx.getCurrentClassDefinition(), ctx.getCurrentFieldDefinition(), path.path());
        int index = PortableUtils.validateAndGetArrayQuantifierFromCurrentToken(path.token(), path.path());
        int len = PortablePositionNavigator.getArrayLengthOfTheField(ctx);
        if (len == 0) {
            return PortablePositionFactory.emptyAnyPosition(path.isLastToken());
        }
        if (len == -1) {
            return PortablePositionFactory.nilAnyPosition(path.isLastToken());
        }
        if (index >= len) {
            return PortablePositionFactory.nilAnyPosition(path.isLastToken());
        }
        if (path.isLastToken()) {
            return PortablePositionNavigator.createPositionForReadAccess(ctx, path, index);
        }
        if (ctx.isCurrentFieldOfType(FieldType.PORTABLE_ARRAY)) {
            PortablePositionNavigator.navigateContextToNextPortableTokenFromPortableArrayCell(ctx, path, index);
        }
        return null;
    }

    private static boolean navigateContextToNextPortableTokenFromPortableField(PortableNavigatorContext ctx) throws IOException {
        BufferObjectDataInput in = ctx.getIn();
        int pos = PortablePositionNavigator.getStreamPositionOfTheField(ctx);
        in.position(pos);
        boolean isNull = in.readBoolean();
        if (isNull) {
            return false;
        }
        int factoryId = in.readInt();
        int classId = in.readInt();
        int versionId = in.readInt();
        ctx.advanceContextToNextPortableToken(factoryId, classId, versionId);
        return true;
    }

    private static void navigateContextToNextPortableTokenFromPortableArrayCell(PortableNavigatorContext ctx, PortablePathCursor path, int index) throws IOException {
        BufferObjectDataInput in = ctx.getIn();
        int pos = PortablePositionNavigator.getStreamPositionOfTheField(ctx);
        in.position(pos);
        in.readInt();
        int factoryId = in.readInt();
        int classId = in.readInt();
        PortableUtils.validateFactoryAndClass(ctx.getCurrentFieldDefinition(), factoryId, classId, path.path());
        int cellOffset = in.position() + index * 4;
        in.position(cellOffset);
        int portablePosition = in.readInt();
        in.position(portablePosition);
        int versionId = in.readInt();
        ctx.advanceContextToNextPortableToken(factoryId, classId, versionId);
    }

    private static PortablePosition createPositionForReadAccess(PortableNavigatorContext ctx, PortablePathCursor path, int index) throws IOException {
        FieldType type = ctx.getCurrentFieldType();
        if (type.isArrayType()) {
            if (type == FieldType.PORTABLE_ARRAY) {
                return PortablePositionNavigator.createPositionForReadAccessInPortableArray(ctx, path, index);
            }
            return PortablePositionNavigator.createPositionForReadAccessInPrimitiveArray(ctx, path, index);
        }
        PortablePositionNavigator.validateNonArrayPosition(path, index);
        return PortablePositionNavigator.createPositionForReadAccessInFromAttribute(ctx, path, index, type);
    }

    private static PortablePosition createPositionForReadAccessInPortableArray(PortableNavigatorContext ctx, PortablePathCursor path, int index) throws IOException {
        if (index >= 0) {
            return PortablePositionNavigator.createPositionForPortableArrayAccess(ctx, path, index, true);
        }
        return PortablePositionNavigator.createPositionForPortableArrayAccess(ctx, path, index, false);
    }

    private static PortablePosition createPositionForReadAccessInPrimitiveArray(PortableNavigatorContext ctx, PortablePathCursor path, int index) throws IOException {
        if (index >= 0) {
            return PortablePositionNavigator.createPositionForSingleCellPrimitiveArrayAccess(ctx, path, index);
        }
        return PortablePositionNavigator.createPositionForPrimitiveFieldAccess(ctx, path, index);
    }

    private static PortablePosition createPositionForReadAccessInFromAttribute(PortableNavigatorContext ctx, PortablePathCursor path, int index, FieldType type) throws IOException {
        if (type != FieldType.PORTABLE) {
            return PortablePositionNavigator.createPositionForPrimitiveFieldAccess(ctx, path, index);
        }
        return PortablePositionNavigator.createPositionForPortableFieldAccess(ctx, path);
    }

    private static PortablePosition createPositionForReadAccess(PortableNavigatorContext ctx, PortablePathCursor path) throws IOException {
        int notArrayCellAccessIndex = -1;
        return PortablePositionNavigator.createPositionForReadAccess(ctx, path, notArrayCellAccessIndex);
    }

    private static void validateNonArrayPosition(PortablePathCursor path, int index) {
        if (index >= 0) {
            throw new IllegalArgumentException("Non array position expected, but the cell index is " + index + " in path" + path.path());
        }
    }

    private static PortablePosition createPositionForSingleCellPrimitiveArrayAccess(PortableNavigatorContext ctx, PortablePathCursor path, int index) throws IOException {
        int streamPosition;
        BufferObjectDataInput in = ctx.getIn();
        in.position(PortablePositionNavigator.getStreamPositionOfTheField(ctx));
        in.readInt();
        if (ctx.getCurrentFieldType() == FieldType.UTF || ctx.getCurrentFieldType() == FieldType.UTF_ARRAY) {
            for (int currentIndex = 0; index > currentIndex; ++currentIndex) {
                int indexElementLen = in.readInt();
                indexElementLen = indexElementLen < 0 ? 0 : indexElementLen;
                in.position(in.position() + indexElementLen);
            }
            streamPosition = in.position();
        } else {
            streamPosition = in.position() + index * ctx.getCurrentFieldType().getSingleType().getTypeSize();
        }
        return PortablePositionFactory.createSinglePrimitivePosition(ctx.getCurrentFieldDefinition(), streamPosition, index, path.isLastToken());
    }

    private static PortablePosition createPositionForPortableFieldAccess(PortableNavigatorContext ctx, PortablePathCursor path) throws IOException {
        BufferObjectDataInput in = ctx.getIn();
        in.position(PortablePositionNavigator.getStreamPositionOfTheField(ctx));
        boolean nil = in.readBoolean();
        int factoryId = in.readInt();
        int classId = in.readInt();
        int streamPosition = in.position();
        PortableUtils.validateFactoryAndClass(ctx.getCurrentFieldDefinition(), factoryId, classId, path.path());
        return PortablePositionFactory.createSinglePortablePosition(ctx.getCurrentFieldDefinition(), streamPosition, factoryId, classId, nil, path.isLastToken());
    }

    private static PortablePosition createPositionForPortableArrayAccess(PortableNavigatorContext ctx, PortablePathCursor path, int index, boolean singleCellAccess) throws IOException {
        BufferObjectDataInput in = ctx.getIn();
        in.position(PortablePositionNavigator.getStreamPositionOfTheField(ctx));
        int len = in.readInt();
        int factoryId = in.readInt();
        int classId = in.readInt();
        int streamPosition = in.position();
        PortableUtils.validateFactoryAndClass(ctx.getCurrentFieldDefinition(), factoryId, classId, path.path());
        if (singleCellAccess) {
            if (index < len) {
                int offset = in.position() + index * 4;
                in.position(offset);
                streamPosition = in.readInt();
            } else {
                return PortablePositionFactory.nil(path.isLastToken());
            }
        }
        return PortablePositionFactory.createSinglePortablePosition(ctx.getCurrentFieldDefinition(), streamPosition, factoryId, classId, index, len, path.isLastToken());
    }

    private static PortablePosition createPositionForPrimitiveFieldAccess(PortableNavigatorContext ctx, PortablePathCursor path, int index) throws IOException {
        ctx.getIn().position(PortablePositionNavigator.getStreamPositionOfTheField(ctx));
        return PortablePositionFactory.createSinglePrimitivePosition(ctx.getCurrentFieldDefinition(), ctx.getIn().position(), index, path.isLastToken());
    }

    private static int getStreamPositionOfTheField(PortableNavigatorContext ctx) throws IOException {
        return PortableUtils.getStreamPositionOfTheField(ctx.getCurrentFieldDefinition(), ctx.getIn(), ctx.getCurrentOffset());
    }

    private static int getArrayLengthOfTheField(PortableNavigatorContext ctx) throws IOException {
        return PortableUtils.getArrayLengthOfTheField(ctx.getCurrentFieldDefinition(), ctx.getIn(), ctx.getCurrentOffset());
    }
}


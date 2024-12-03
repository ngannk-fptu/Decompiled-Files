/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.jar.asm;

import net.bytebuddy.jar.asm.Attribute;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.TypePath;

final class Context {
    Attribute[] attributePrototypes;
    int parsingOptions;
    char[] charBuffer;
    int currentMethodAccessFlags;
    String currentMethodName;
    String currentMethodDescriptor;
    Label[] currentMethodLabels;
    int currentTypeAnnotationTarget;
    TypePath currentTypeAnnotationTargetPath;
    Label[] currentLocalVariableAnnotationRangeStarts;
    Label[] currentLocalVariableAnnotationRangeEnds;
    int[] currentLocalVariableAnnotationRangeIndices;
    int currentFrameOffset;
    int currentFrameType;
    int currentFrameLocalCount;
    int currentFrameLocalCountDelta;
    Object[] currentFrameLocalTypes;
    int currentFrameStackCount;
    Object[] currentFrameStackTypes;

    Context() {
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.DataInputStream;
import java.io.IOException;
import org.aspectj.util.TypeSafeEnum;
import org.aspectj.weaver.BCException;

public class AnnotationTargetKind
extends TypeSafeEnum {
    public static final AnnotationTargetKind ANNOTATION_TYPE = new AnnotationTargetKind("ANNOTATION_TYPE", 1);
    public static final AnnotationTargetKind CONSTRUCTOR = new AnnotationTargetKind("CONSTRUCTOR", 2);
    public static final AnnotationTargetKind FIELD = new AnnotationTargetKind("FIELD", 3);
    public static final AnnotationTargetKind LOCAL_VARIABLE = new AnnotationTargetKind("LOCAL_VARIABLE", 4);
    public static final AnnotationTargetKind METHOD = new AnnotationTargetKind("METHOD", 5);
    public static final AnnotationTargetKind PACKAGE = new AnnotationTargetKind("PACKAGE", 6);
    public static final AnnotationTargetKind PARAMETER = new AnnotationTargetKind("PARAMETER", 7);
    public static final AnnotationTargetKind TYPE = new AnnotationTargetKind("TYPE", 8);

    public AnnotationTargetKind(String name, int key) {
        super(name, key);
    }

    public static AnnotationTargetKind read(DataInputStream s) throws IOException {
        byte key = s.readByte();
        switch (key) {
            case 1: {
                return ANNOTATION_TYPE;
            }
            case 2: {
                return CONSTRUCTOR;
            }
            case 3: {
                return FIELD;
            }
            case 4: {
                return LOCAL_VARIABLE;
            }
            case 5: {
                return METHOD;
            }
            case 6: {
                return PACKAGE;
            }
            case 7: {
                return PARAMETER;
            }
            case 8: {
                return TYPE;
            }
        }
        throw new BCException("weird annotation target kind " + key);
    }
}


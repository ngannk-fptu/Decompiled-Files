/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

public abstract class AnnotationValue {
    protected int valueKind;
    public static final int STRING = 115;
    public static final int ENUM_CONSTANT = 101;
    public static final int CLASS = 99;
    public static final int ANNOTATION = 64;
    public static final int ARRAY = 91;
    public static final int PRIMITIVE_INT = 73;
    public static final int PRIMITIVE_BYTE = 66;
    public static final int PRIMITIVE_CHAR = 67;
    public static final int PRIMITIVE_DOUBLE = 68;
    public static final int PRIMITIVE_FLOAT = 70;
    public static final int PRIMITIVE_LONG = 74;
    public static final int PRIMITIVE_SHORT = 83;
    public static final int PRIMITIVE_BOOLEAN = 90;

    public abstract String stringify();

    public AnnotationValue(int kind) {
        this.valueKind = kind;
    }

    public static String whatKindIsThis(int kind) {
        switch (kind) {
            case 66: {
                return "byte";
            }
            case 67: {
                return "char";
            }
            case 68: {
                return "double";
            }
            case 70: {
                return "float";
            }
            case 73: {
                return "int";
            }
            case 74: {
                return "long";
            }
            case 83: {
                return "short";
            }
            case 90: {
                return "boolean";
            }
            case 115: {
                return "string";
            }
            case 101: {
                return "enum";
            }
            case 99: {
                return "class";
            }
            case 64: {
                return "annotation";
            }
            case 91: {
                return "array";
            }
        }
        throw new RuntimeException("Dont know what this is : " + kind);
    }
}


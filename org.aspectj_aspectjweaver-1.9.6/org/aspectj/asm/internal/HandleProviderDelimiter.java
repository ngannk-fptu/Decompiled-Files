/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

import org.aspectj.asm.IProgramElement;

public class HandleProviderDelimiter {
    public static final HandleProviderDelimiter JAVAPROJECT = new HandleProviderDelimiter('=');
    public static final HandleProviderDelimiter PACKAGEFRAGMENT = new HandleProviderDelimiter('<');
    public static final HandleProviderDelimiter FIELD = new HandleProviderDelimiter('^');
    public static final HandleProviderDelimiter METHOD = new HandleProviderDelimiter('~');
    public static final HandleProviderDelimiter INITIALIZER = new HandleProviderDelimiter('|');
    public static final HandleProviderDelimiter COMPILATIONUNIT = new HandleProviderDelimiter('{');
    public static final HandleProviderDelimiter CLASSFILE = new HandleProviderDelimiter('(');
    public static final HandleProviderDelimiter TYPE = new HandleProviderDelimiter('[');
    public static final HandleProviderDelimiter IMPORTDECLARATION = new HandleProviderDelimiter('#');
    public static final HandleProviderDelimiter COUNT = new HandleProviderDelimiter('!');
    public static final HandleProviderDelimiter ESCAPE = new HandleProviderDelimiter('\\');
    public static final HandleProviderDelimiter PACKAGEDECLARATION = new HandleProviderDelimiter('%');
    public static final HandleProviderDelimiter PACKAGEFRAGMENTROOT = new HandleProviderDelimiter('/');
    public static final HandleProviderDelimiter LOCALVARIABLE = new HandleProviderDelimiter('@');
    public static final HandleProviderDelimiter TYPE_PARAMETER = new HandleProviderDelimiter(']');
    public static final HandleProviderDelimiter ASPECT_CU = new HandleProviderDelimiter('*');
    public static final HandleProviderDelimiter ADVICE = new HandleProviderDelimiter('&');
    public static final HandleProviderDelimiter ASPECT_TYPE = new HandleProviderDelimiter('\'');
    public static final HandleProviderDelimiter CODEELEMENT = new HandleProviderDelimiter('?');
    public static final HandleProviderDelimiter ITD_FIELD = new HandleProviderDelimiter(',');
    public static final HandleProviderDelimiter ITD = new HandleProviderDelimiter(')');
    public static final HandleProviderDelimiter DECLARE = new HandleProviderDelimiter('`');
    public static final HandleProviderDelimiter POINTCUT = new HandleProviderDelimiter('\"');
    public static final HandleProviderDelimiter PHANTOM = new HandleProviderDelimiter(';');
    private static char empty = (char)32;
    private final char delim;

    private HandleProviderDelimiter(char delim) {
        this.delim = delim;
    }

    public char getDelimiter() {
        return this.delim;
    }

    public static char getDelimiter(IProgramElement ipe) {
        IProgramElement.Kind kind = ipe.getKind();
        if (kind.equals(IProgramElement.Kind.PROJECT)) {
            return JAVAPROJECT.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.PACKAGE)) {
            return PACKAGEFRAGMENT.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.FILE_JAVA)) {
            if (ipe.getName().endsWith(".aj")) {
                return ASPECT_CU.getDelimiter();
            }
            return COMPILATIONUNIT.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.FILE_ASPECTJ)) {
            return ASPECT_CU.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.IMPORT_REFERENCE)) {
            return IMPORTDECLARATION.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.PACKAGE_DECLARATION)) {
            return PACKAGEDECLARATION.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.CLASS) || kind.equals(IProgramElement.Kind.INTERFACE) || kind.equals(IProgramElement.Kind.ENUM) || kind.equals(IProgramElement.Kind.ANNOTATION)) {
            return TYPE.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.ASPECT)) {
            if (ipe.isAnnotationStyleDeclaration()) {
                return TYPE.getDelimiter();
            }
            return ASPECT_TYPE.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.INITIALIZER)) {
            return INITIALIZER.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.INTER_TYPE_FIELD)) {
            return ITD_FIELD.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.INTER_TYPE_METHOD) || kind.equals(IProgramElement.Kind.INTER_TYPE_CONSTRUCTOR) || kind.equals(IProgramElement.Kind.INTER_TYPE_PARENT)) {
            return ITD.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.CONSTRUCTOR) || kind.equals(IProgramElement.Kind.METHOD)) {
            return METHOD.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.FIELD) || kind.equals(IProgramElement.Kind.ENUM_VALUE)) {
            return FIELD.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.POINTCUT)) {
            if (ipe.isAnnotationStyleDeclaration()) {
                return METHOD.getDelimiter();
            }
            return POINTCUT.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.ADVICE)) {
            if (ipe.isAnnotationStyleDeclaration()) {
                return METHOD.getDelimiter();
            }
            return ADVICE.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.DECLARE_PARENTS) || kind.equals(IProgramElement.Kind.DECLARE_WARNING) || kind.equals(IProgramElement.Kind.DECLARE_ERROR) || kind.equals(IProgramElement.Kind.DECLARE_SOFT) || kind.equals(IProgramElement.Kind.DECLARE_PRECEDENCE) || kind.equals(IProgramElement.Kind.DECLARE_ANNOTATION_AT_CONSTRUCTOR) || kind.equals(IProgramElement.Kind.DECLARE_ANNOTATION_AT_FIELD) || kind.equals(IProgramElement.Kind.DECLARE_ANNOTATION_AT_METHOD) || kind.equals(IProgramElement.Kind.DECLARE_ANNOTATION_AT_TYPE)) {
            return DECLARE.getDelimiter();
        }
        if (kind.equals(IProgramElement.Kind.CODE)) {
            return CODEELEMENT.getDelimiter();
        }
        if (kind == IProgramElement.Kind.FILE) {
            if (ipe.getName().endsWith(".class")) {
                return CLASSFILE.getDelimiter();
            }
            if (ipe.getName().endsWith(".aj")) {
                return ASPECT_CU.getDelimiter();
            }
            if (ipe.getName().endsWith(".java")) {
                return COMPILATIONUNIT.getDelimiter();
            }
            return empty;
        }
        return empty;
    }
}


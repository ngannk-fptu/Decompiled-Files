/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.Classfile;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeSignature;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.Parser;

public class BaseTypeSignature
extends TypeSignature {
    private final char typeSignatureChar;

    BaseTypeSignature(char typeSignatureChar) {
        switch (typeSignatureChar) {
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'V': 
            case 'Z': {
                this.typeSignatureChar = typeSignatureChar;
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal " + BaseTypeSignature.class.getSimpleName() + " type: '" + typeSignatureChar + "'");
            }
        }
    }

    static String getTypeStr(char typeChar) {
        switch (typeChar) {
            case 'B': {
                return "byte";
            }
            case 'C': {
                return "char";
            }
            case 'D': {
                return "double";
            }
            case 'F': {
                return "float";
            }
            case 'I': {
                return "int";
            }
            case 'J': {
                return "long";
            }
            case 'S': {
                return "short";
            }
            case 'Z': {
                return "boolean";
            }
            case 'V': {
                return "void";
            }
        }
        return null;
    }

    static char getTypeChar(String typeStr) {
        switch (typeStr) {
            case "byte": {
                return 'B';
            }
            case "char": {
                return 'C';
            }
            case "double": {
                return 'D';
            }
            case "float": {
                return 'F';
            }
            case "int": {
                return 'I';
            }
            case "long": {
                return 'J';
            }
            case "short": {
                return 'S';
            }
            case "boolean": {
                return 'Z';
            }
            case "void": {
                return 'V';
            }
        }
        return '\u0000';
    }

    static Class<?> getType(char typeChar) {
        switch (typeChar) {
            case 'B': {
                return Byte.TYPE;
            }
            case 'C': {
                return Character.TYPE;
            }
            case 'D': {
                return Double.TYPE;
            }
            case 'F': {
                return Float.TYPE;
            }
            case 'I': {
                return Integer.TYPE;
            }
            case 'J': {
                return Long.TYPE;
            }
            case 'S': {
                return Short.TYPE;
            }
            case 'Z': {
                return Boolean.TYPE;
            }
            case 'V': {
                return Void.TYPE;
            }
        }
        return null;
    }

    public char getTypeSignatureChar() {
        return this.typeSignatureChar;
    }

    public String getTypeStr() {
        return BaseTypeSignature.getTypeStr(this.typeSignatureChar);
    }

    public Class<?> getType() {
        return BaseTypeSignature.getType(this.typeSignatureChar);
    }

    @Override
    protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
        this.addTypeAnnotation(annotationInfo);
    }

    @Override
    Class<?> loadClass() {
        return this.getType();
    }

    @Override
    <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
        Class<?> type = this.getType();
        if (!superclassOrInterfaceType.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Primitive class " + this.getTypeStr() + " cannot be cast to " + superclassOrInterfaceType.getName());
        }
        Class<?> classT = type;
        return classT;
    }

    static BaseTypeSignature parse(Parser parser) {
        switch (parser.peek()) {
            case 'B': {
                parser.next();
                return new BaseTypeSignature('B');
            }
            case 'C': {
                parser.next();
                return new BaseTypeSignature('C');
            }
            case 'D': {
                parser.next();
                return new BaseTypeSignature('D');
            }
            case 'F': {
                parser.next();
                return new BaseTypeSignature('F');
            }
            case 'I': {
                parser.next();
                return new BaseTypeSignature('I');
            }
            case 'J': {
                parser.next();
                return new BaseTypeSignature('J');
            }
            case 'S': {
                parser.next();
                return new BaseTypeSignature('S');
            }
            case 'Z': {
                parser.next();
                return new BaseTypeSignature('Z');
            }
            case 'V': {
                parser.next();
                return new BaseTypeSignature('V');
            }
        }
        return null;
    }

    @Override
    protected String getClassName() {
        return this.getTypeStr();
    }

    @Override
    protected ClassInfo getClassInfo() {
        return null;
    }

    @Override
    protected void findReferencedClassNames(Set<String> refdClassNames) {
    }

    @Override
    void setScanResult(ScanResult scanResult) {
    }

    public int hashCode() {
        return this.typeSignatureChar;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BaseTypeSignature)) {
            return false;
        }
        BaseTypeSignature other = (BaseTypeSignature)obj;
        return Objects.equals(this.typeAnnotationInfo, other.typeAnnotationInfo) && other.typeSignatureChar == this.typeSignatureChar;
    }

    @Override
    public boolean equalsIgnoringTypeParams(TypeSignature other) {
        if (!(other instanceof BaseTypeSignature)) {
            return false;
        }
        return this.typeSignatureChar == ((BaseTypeSignature)other).typeSignatureChar;
    }

    @Override
    protected void toStringInternal(boolean useSimpleNames, AnnotationInfoList annotationsToExclude, StringBuilder buf) {
        if (this.typeAnnotationInfo != null) {
            for (AnnotationInfo annotationInfo : this.typeAnnotationInfo) {
                if (annotationsToExclude != null && annotationsToExclude.contains(annotationInfo)) continue;
                annotationInfo.toString(useSimpleNames, buf);
                buf.append(' ');
            }
        }
        buf.append(this.getTypeStr());
    }
}


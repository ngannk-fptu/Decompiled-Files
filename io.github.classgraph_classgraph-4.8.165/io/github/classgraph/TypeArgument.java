/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.Classfile;
import io.github.classgraph.HierarchicalTypeSignature;
import io.github.classgraph.ReferenceTypeSignature;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public final class TypeArgument
extends HierarchicalTypeSignature {
    private final Wildcard wildcard;
    private final ReferenceTypeSignature typeSignature;

    private TypeArgument(Wildcard wildcard, ReferenceTypeSignature typeSignature) {
        this.wildcard = wildcard;
        this.typeSignature = typeSignature;
    }

    public Wildcard getWildcard() {
        return this.wildcard;
    }

    public ReferenceTypeSignature getTypeSignature() {
        return this.typeSignature;
    }

    @Override
    protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
        if (typePath.size() == 0 && this.wildcard != Wildcard.NONE) {
            this.addTypeAnnotation(annotationInfo);
        } else if (typePath.size() > 0 && typePath.get((int)0).typePathKind == 2) {
            if (this.typeSignature != null) {
                this.typeSignature.addTypeAnnotation(typePath.subList(1, typePath.size()), annotationInfo);
            }
        } else if (this.typeSignature != null) {
            this.typeSignature.addTypeAnnotation(typePath, annotationInfo);
        }
    }

    private static TypeArgument parse(Parser parser, String definingClassName) throws ParseException {
        char peek = parser.peek();
        if (peek == '*') {
            parser.expect('*');
            return new TypeArgument(Wildcard.ANY, null);
        }
        if (peek == '+') {
            parser.expect('+');
            ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
            if (typeSignature == null) {
                throw new ParseException(parser, "Missing '+' type bound");
            }
            return new TypeArgument(Wildcard.EXTENDS, typeSignature);
        }
        if (peek == '-') {
            parser.expect('-');
            ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
            if (typeSignature == null) {
                throw new ParseException(parser, "Missing '-' type bound");
            }
            return new TypeArgument(Wildcard.SUPER, typeSignature);
        }
        ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
        if (typeSignature == null) {
            throw new ParseException(parser, "Missing type bound");
        }
        return new TypeArgument(Wildcard.NONE, typeSignature);
    }

    static List<TypeArgument> parseList(Parser parser, String definingClassName) throws ParseException {
        if (parser.peek() == '<') {
            parser.expect('<');
            ArrayList<TypeArgument> typeArguments = new ArrayList<TypeArgument>(2);
            while (parser.peek() != '>') {
                if (!parser.hasMore()) {
                    throw new ParseException(parser, "Missing '>'");
                }
                typeArguments.add(TypeArgument.parse(parser, definingClassName));
            }
            parser.expect('>');
            return typeArguments;
        }
        return Collections.emptyList();
    }

    @Override
    protected String getClassName() {
        throw new IllegalArgumentException("getClassName() cannot be called here");
    }

    @Override
    protected ClassInfo getClassInfo() {
        throw new IllegalArgumentException("getClassInfo() cannot be called here");
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.typeSignature != null) {
            this.typeSignature.setScanResult(scanResult);
        }
    }

    public void findReferencedClassNames(Set<String> refdClassNames) {
        if (this.typeSignature != null) {
            this.typeSignature.findReferencedClassNames(refdClassNames);
        }
    }

    public int hashCode() {
        return (this.typeSignature != null ? this.typeSignature.hashCode() : 0) + 7 * this.wildcard.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TypeArgument)) {
            return false;
        }
        TypeArgument other = (TypeArgument)obj;
        return Objects.equals(this.typeAnnotationInfo, other.typeAnnotationInfo) && Objects.equals(this.typeSignature, other.typeSignature) && other.wildcard.equals((Object)this.wildcard);
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
        switch (this.wildcard) {
            case ANY: {
                buf.append('?');
                break;
            }
            case EXTENDS: {
                String typeSigStr = this.typeSignature.toString(useSimpleNames);
                buf.append(typeSigStr.equals("java.lang.Object") ? "?" : "? extends " + typeSigStr);
                break;
            }
            case SUPER: {
                buf.append("? super ");
                this.typeSignature.toString(useSimpleNames, buf);
                break;
            }
            default: {
                this.typeSignature.toString(useSimpleNames, buf);
            }
        }
    }

    public static enum Wildcard {
        NONE,
        ANY,
        EXTENDS,
        SUPER;

    }
}


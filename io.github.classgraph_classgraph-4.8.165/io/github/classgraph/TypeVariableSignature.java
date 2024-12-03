/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassRefOrTypeVariableSignature;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.ClassTypeSignature;
import io.github.classgraph.Classfile;
import io.github.classgraph.MethodTypeSignature;
import io.github.classgraph.ReferenceTypeSignature;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeParameter;
import io.github.classgraph.TypeSignature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;

public final class TypeVariableSignature
extends ClassRefOrTypeVariableSignature {
    private final String name;
    private final String definingClassName;
    MethodTypeSignature containingMethodSignature;
    private TypeParameter typeParameterCached;

    private TypeVariableSignature(String typeVariableName, String definingClassName) {
        this.name = typeVariableName;
        this.definingClassName = definingClassName;
    }

    public String getName() {
        return this.name;
    }

    public TypeParameter resolve() {
        if (this.typeParameterCached != null) {
            return this.typeParameterCached;
        }
        if (this.containingMethodSignature != null && this.containingMethodSignature.typeParameters != null && !this.containingMethodSignature.typeParameters.isEmpty()) {
            for (TypeParameter typeParameter : this.containingMethodSignature.typeParameters) {
                if (!typeParameter.name.equals(this.name)) continue;
                this.typeParameterCached = typeParameter;
                return typeParameter;
            }
        }
        if (this.getClassName() != null) {
            ClassInfo containingClassInfo = this.getClassInfo();
            if (containingClassInfo == null) {
                throw new IllegalArgumentException("Could not find ClassInfo object for " + this.definingClassName);
            }
            ClassTypeSignature containingClassSignature = null;
            try {
                containingClassSignature = containingClassInfo.getTypeSignature();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (containingClassSignature != null && containingClassSignature.typeParameters != null && !containingClassSignature.typeParameters.isEmpty()) {
                for (TypeParameter typeParameter : containingClassSignature.typeParameters) {
                    if (!typeParameter.name.equals(this.name)) continue;
                    this.typeParameterCached = typeParameter;
                    return typeParameter;
                }
            }
        }
        TypeParameter typeParameter = new TypeParameter(this.name, null, Collections.emptyList());
        typeParameter.setScanResult(this.scanResult);
        this.typeParameterCached = typeParameter;
        return typeParameter;
    }

    @Override
    protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
        if (!typePath.isEmpty()) {
            throw new IllegalArgumentException("Type variable should have empty typePath");
        }
        this.addTypeAnnotation(annotationInfo);
    }

    static TypeVariableSignature parse(Parser parser, String definingClassName) throws ParseException {
        char peek = parser.peek();
        if (peek == 'T') {
            parser.next();
            if (!TypeUtils.getIdentifierToken(parser, false)) {
                throw new ParseException(parser, "Could not parse type variable signature");
            }
            parser.expect(';');
            TypeVariableSignature typeVariableSignature = new TypeVariableSignature(parser.currToken(), definingClassName);
            ArrayList<TypeVariableSignature> typeVariableSignatures = (ArrayList<TypeVariableSignature>)parser.getState();
            if (typeVariableSignatures == null) {
                typeVariableSignatures = new ArrayList<TypeVariableSignature>();
                parser.setState(typeVariableSignatures);
            }
            typeVariableSignatures.add(typeVariableSignature);
            return typeVariableSignature;
        }
        return null;
    }

    @Override
    protected String getClassName() {
        return this.definingClassName;
    }

    @Override
    protected void findReferencedClassNames(Set<String> refdClassNames) {
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.typeParameterCached != null) {
            this.typeParameterCached.setScanResult(scanResult);
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TypeVariableSignature)) {
            return false;
        }
        TypeVariableSignature other = (TypeVariableSignature)obj;
        return other.name.equals(this.name) && Objects.equals(other.typeAnnotationInfo, this.typeAnnotationInfo);
    }

    @Override
    public boolean equalsIgnoringTypeParams(TypeSignature other) {
        if (other instanceof ClassRefTypeSignature) {
            TypeParameter typeParameter;
            if (((ClassRefTypeSignature)other).className.equals("java.lang.Object")) {
                return true;
            }
            try {
                typeParameter = this.resolve();
            }
            catch (IllegalArgumentException e) {
                return true;
            }
            if (typeParameter.classBound == null && (typeParameter.interfaceBounds == null || typeParameter.interfaceBounds.isEmpty())) {
                return true;
            }
            if (typeParameter.classBound != null) {
                if (typeParameter.classBound instanceof ClassRefTypeSignature) {
                    if (typeParameter.classBound.equals(other)) {
                        return true;
                    }
                } else {
                    if (typeParameter.classBound instanceof TypeVariableSignature) {
                        return this.equalsIgnoringTypeParams(typeParameter.classBound);
                    }
                    return false;
                }
            }
            if (typeParameter.interfaceBounds != null) {
                for (ReferenceTypeSignature interfaceBound : typeParameter.interfaceBounds) {
                    if (interfaceBound instanceof ClassRefTypeSignature) {
                        if (!interfaceBound.equals(other)) continue;
                        return true;
                    }
                    if (interfaceBound instanceof TypeVariableSignature) {
                        return this.equalsIgnoringTypeParams(interfaceBound);
                    }
                    return false;
                }
            }
            return false;
        }
        return this.equals(other);
    }

    public String toStringWithTypeBound() {
        try {
            return this.resolve().toString();
        }
        catch (IllegalArgumentException e) {
            return this.name;
        }
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
        buf.append(this.name);
    }
}


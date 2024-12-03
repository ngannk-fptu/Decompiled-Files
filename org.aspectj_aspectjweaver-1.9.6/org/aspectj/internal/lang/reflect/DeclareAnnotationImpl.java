/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import org.aspectj.internal.lang.reflect.SignaturePatternImpl;
import org.aspectj.internal.lang.reflect.TypePatternImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.SignaturePattern;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareAnnotationImpl
implements DeclareAnnotation {
    private Annotation theAnnotation;
    private String annText;
    private AjType<?> declaringType;
    private DeclareAnnotation.Kind kind;
    private TypePattern typePattern;
    private SignaturePattern signaturePattern;

    public DeclareAnnotationImpl(AjType<?> declaring, String kindString, String pattern, Annotation ann, String annText) {
        this.declaringType = declaring;
        if (kindString.equals("at_type")) {
            this.kind = DeclareAnnotation.Kind.Type;
        } else if (kindString.equals("at_field")) {
            this.kind = DeclareAnnotation.Kind.Field;
        } else if (kindString.equals("at_method")) {
            this.kind = DeclareAnnotation.Kind.Method;
        } else if (kindString.equals("at_constructor")) {
            this.kind = DeclareAnnotation.Kind.Constructor;
        } else {
            throw new IllegalStateException("Unknown declare annotation kind: " + kindString);
        }
        if (this.kind == DeclareAnnotation.Kind.Type) {
            this.typePattern = new TypePatternImpl(pattern);
        } else {
            this.signaturePattern = new SignaturePatternImpl(pattern);
        }
        this.theAnnotation = ann;
        this.annText = annText;
    }

    @Override
    public AjType<?> getDeclaringType() {
        return this.declaringType;
    }

    @Override
    public DeclareAnnotation.Kind getKind() {
        return this.kind;
    }

    @Override
    public SignaturePattern getSignaturePattern() {
        return this.signaturePattern;
    }

    @Override
    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    @Override
    public Annotation getAnnotation() {
        return this.theAnnotation;
    }

    @Override
    public String getAnnotationAsText() {
        return this.annText;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare @");
        switch (this.getKind()) {
            case Type: {
                sb.append("type : ");
                sb.append(this.getTypePattern().asString());
                break;
            }
            case Method: {
                sb.append("method : ");
                sb.append(this.getSignaturePattern().asString());
                break;
            }
            case Field: {
                sb.append("field : ");
                sb.append(this.getSignaturePattern().asString());
                break;
            }
            case Constructor: {
                sb.append("constructor : ");
                sb.append(this.getSignaturePattern().asString());
            }
        }
        sb.append(" : ");
        sb.append(this.getAnnotationAsText());
        return sb.toString();
    }
}


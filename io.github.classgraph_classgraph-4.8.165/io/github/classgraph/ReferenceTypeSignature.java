/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.TypeSignature;
import io.github.classgraph.TypeVariableSignature;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public abstract class ReferenceTypeSignature
extends TypeSignature {
    protected ReferenceTypeSignature() {
    }

    static ReferenceTypeSignature parseReferenceTypeSignature(Parser parser, String definingClassName) throws ParseException {
        ClassRefTypeSignature classTypeSignature = ClassRefTypeSignature.parse(parser, definingClassName);
        if (classTypeSignature != null) {
            return classTypeSignature;
        }
        TypeVariableSignature typeVariableSignature = TypeVariableSignature.parse(parser, definingClassName);
        if (typeVariableSignature != null) {
            return typeVariableSignature;
        }
        ArrayTypeSignature arrayTypeSignature = ArrayTypeSignature.parse(parser, definingClassName);
        if (arrayTypeSignature != null) {
            return arrayTypeSignature;
        }
        return null;
    }

    static ReferenceTypeSignature parseClassBound(Parser parser, String definingClassName) throws ParseException {
        parser.expect(':');
        return ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
    }
}


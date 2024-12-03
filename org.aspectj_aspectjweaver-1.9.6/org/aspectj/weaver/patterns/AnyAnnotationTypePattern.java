/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

public class AnyAnnotationTypePattern
extends AnnotationTypePattern {
    @Override
    public FuzzyBoolean fastMatches(AnnotatedElement annotated) {
        return FuzzyBoolean.YES;
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated) {
        return FuzzyBoolean.YES;
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        return FuzzyBoolean.YES;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(7);
    }

    @Override
    public void resolve(World world) {
    }

    public String toString() {
        return "@ANY";
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean isAny() {
        return true;
    }

    @Override
    public AnnotationTypePattern parameterizeWith(Map<String, UnresolvedType> arg0, World w) {
        return this;
    }

    @Override
    public void setForParameterAnnotationMatch() {
    }
}


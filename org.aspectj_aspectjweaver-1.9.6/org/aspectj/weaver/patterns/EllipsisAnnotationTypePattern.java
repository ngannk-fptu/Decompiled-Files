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
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

class EllipsisAnnotationTypePattern
extends AnnotationTypePattern {
    EllipsisAnnotationTypePattern() {
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated) {
        return FuzzyBoolean.NO;
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        return FuzzyBoolean.NO;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(6);
    }

    @Override
    public void resolve(World world) {
    }

    public String toString() {
        return "..";
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public AnnotationTypePattern parameterizeWith(Map arg0, World w) {
        return this;
    }

    @Override
    public void setForParameterAnnotationMatch() {
    }
}


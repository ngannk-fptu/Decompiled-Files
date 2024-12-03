/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

public class NotAnnotationTypePattern
extends AnnotationTypePattern {
    AnnotationTypePattern negatedPattern;

    public NotAnnotationTypePattern(AnnotationTypePattern pattern) {
        this.negatedPattern = pattern;
        this.setLocation(pattern.getSourceContext(), pattern.getStart(), pattern.getEnd());
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated) {
        return this.negatedPattern.matches(annotated).not();
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        return this.negatedPattern.matches(annotated, parameterAnnotations).not();
    }

    @Override
    public void resolve(World world) {
        this.negatedPattern.resolve(world);
    }

    @Override
    public AnnotationTypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding) {
        this.negatedPattern = this.negatedPattern.resolveBindings(scope, bindings, allowBinding);
        return this;
    }

    @Override
    public AnnotationTypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        AnnotationTypePattern newNegatedPattern = this.negatedPattern.parameterizeWith(typeVariableMap, w);
        NotAnnotationTypePattern ret = new NotAnnotationTypePattern(newNegatedPattern);
        ret.copyLocationFrom(this);
        if (this.isForParameterAnnotationMatch()) {
            ret.setForParameterAnnotationMatch();
        }
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(3);
        this.negatedPattern.write(s);
        this.writeLocation(s);
        s.writeBoolean(this.isForParameterAnnotationMatch());
    }

    public static AnnotationTypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        NotAnnotationTypePattern ret = new NotAnnotationTypePattern(AnnotationTypePattern.read(s, context));
        ret.readLocation(context, s);
        if (s.getMajorVersion() >= 4 && s.readBoolean()) {
            ((AnnotationTypePattern)ret).setForParameterAnnotationMatch();
        }
        return ret;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NotAnnotationTypePattern)) {
            return false;
        }
        NotAnnotationTypePattern other = (NotAnnotationTypePattern)obj;
        return other.negatedPattern.equals(this.negatedPattern) && other.isForParameterAnnotationMatch() == this.isForParameterAnnotationMatch();
    }

    public int hashCode() {
        int result = 17 + 37 * this.negatedPattern.hashCode();
        result = 37 * result + (this.isForParameterAnnotationMatch() ? 0 : 1);
        return result;
    }

    public String toString() {
        return "!" + this.negatedPattern.toString();
    }

    public AnnotationTypePattern getNegatedPattern() {
        return this.negatedPattern;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object traverse(PatternNodeVisitor visitor, Object data) {
        Object ret = this.accept(visitor, data);
        this.negatedPattern.traverse(visitor, ret);
        return ret;
    }

    @Override
    public void setForParameterAnnotationMatch() {
        this.negatedPattern.setForParameterAnnotationMatch();
    }
}


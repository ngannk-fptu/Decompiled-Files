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

public class OrAnnotationTypePattern
extends AnnotationTypePattern {
    private AnnotationTypePattern left;
    private AnnotationTypePattern right;

    public OrAnnotationTypePattern(AnnotationTypePattern left, AnnotationTypePattern right) {
        this.left = left;
        this.right = right;
        this.setLocation(left.getSourceContext(), left.getStart(), right.getEnd());
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated) {
        return this.left.matches(annotated).or(this.right.matches(annotated));
    }

    @Override
    public FuzzyBoolean matches(AnnotatedElement annotated, ResolvedType[] parameterAnnotations) {
        return this.left.matches(annotated, parameterAnnotations).or(this.right.matches(annotated, parameterAnnotations));
    }

    @Override
    public void resolve(World world) {
        this.left.resolve(world);
        this.right.resolve(world);
    }

    @Override
    public AnnotationTypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding) {
        this.left = this.left.resolveBindings(scope, bindings, allowBinding);
        this.right = this.right.resolveBindings(scope, bindings, allowBinding);
        return this;
    }

    @Override
    public AnnotationTypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        AnnotationTypePattern newLeft = this.left.parameterizeWith(typeVariableMap, w);
        AnnotationTypePattern newRight = this.right.parameterizeWith(typeVariableMap, w);
        OrAnnotationTypePattern ret = new OrAnnotationTypePattern(newLeft, newRight);
        ret.copyLocationFrom(this);
        if (this.isForParameterAnnotationMatch()) {
            ret.setForParameterAnnotationMatch();
        }
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object traverse(PatternNodeVisitor visitor, Object data) {
        Object ret = this.accept(visitor, data);
        this.left.traverse(visitor, ret);
        this.right.traverse(visitor, ret);
        return ret;
    }

    public static AnnotationTypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        OrAnnotationTypePattern p = new OrAnnotationTypePattern(AnnotationTypePattern.read(s, context), AnnotationTypePattern.read(s, context));
        p.readLocation(context, s);
        if (s.getMajorVersion() >= 4 && s.readBoolean()) {
            ((AnnotationTypePattern)p).setForParameterAnnotationMatch();
        }
        return p;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(4);
        this.left.write(s);
        this.right.write(s);
        this.writeLocation(s);
        s.writeBoolean(this.isForParameterAnnotationMatch());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OrAnnotationTypePattern)) {
            return false;
        }
        OrAnnotationTypePattern other = (OrAnnotationTypePattern)obj;
        return this.left.equals(other.left) && this.right.equals(other.right) && this.isForParameterAnnotationMatch() == other.isForParameterAnnotationMatch();
    }

    public int hashCode() {
        int result = 17;
        result = result * 37 + this.left.hashCode();
        result = result * 37 + this.right.hashCode();
        result = result * 37 + (this.isForParameterAnnotationMatch() ? 0 : 1);
        return result;
    }

    public String toString() {
        return "(" + this.left.toString() + " || " + this.right.toString() + ")";
    }

    public AnnotationTypePattern getLeft() {
        return this.left;
    }

    public AnnotationTypePattern getRight() {
        return this.right;
    }

    @Override
    public void setForParameterAnnotationMatch() {
        this.left.setForParameterAnnotationMatch();
        this.right.setForParameterAnnotationMatch();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AndAnnotationTypePattern;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;

public class OrTypePattern
extends TypePattern {
    private TypePattern left;
    private TypePattern right;

    public OrTypePattern(TypePattern left, TypePattern right) {
        super(false, false);
        this.left = left;
        this.right = right;
        this.setLocation(left.getSourceContext(), left.getStart(), right.getEnd());
    }

    public TypePattern getRight() {
        return this.right;
    }

    public TypePattern getLeft() {
        return this.left;
    }

    @Override
    protected boolean couldEverMatchSameTypesAs(TypePattern other) {
        return true;
    }

    @Override
    public FuzzyBoolean matchesInstanceof(ResolvedType type) {
        return this.left.matchesInstanceof(type).or(this.right.matchesInstanceof(type));
    }

    @Override
    protected boolean matchesExactly(ResolvedType type) {
        return this.left.matchesExactly(type) || this.right.matchesExactly(type);
    }

    @Override
    protected boolean matchesExactly(ResolvedType type, ResolvedType annotatedType) {
        return this.left.matchesExactly(type, annotatedType) || this.right.matchesExactly(type, annotatedType);
    }

    @Override
    public boolean matchesStatically(ResolvedType type) {
        return this.left.matchesStatically(type) || this.right.matchesStatically(type);
    }

    @Override
    public void setIsVarArgs(boolean isVarArgs) {
        this.isVarArgs = isVarArgs;
        this.left.setIsVarArgs(isVarArgs);
        this.right.setIsVarArgs(isVarArgs);
    }

    @Override
    public void setAnnotationTypePattern(AnnotationTypePattern annPatt) {
        if (annPatt == AnnotationTypePattern.ANY) {
            return;
        }
        if (this.left.annotationPattern == AnnotationTypePattern.ANY) {
            this.left.setAnnotationTypePattern(annPatt);
        } else {
            this.left.setAnnotationTypePattern(new AndAnnotationTypePattern(this.left.annotationPattern, annPatt));
        }
        if (this.right.annotationPattern == AnnotationTypePattern.ANY) {
            this.right.setAnnotationTypePattern(annPatt);
        } else {
            this.right.setAnnotationTypePattern(new AndAnnotationTypePattern(this.right.annotationPattern, annPatt));
        }
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(7);
        this.left.write(s);
        this.right.write(s);
        this.writeLocation(s);
    }

    public static TypePattern read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        OrTypePattern ret = new OrTypePattern(TypePattern.read(s, context), TypePattern.read(s, context));
        ret.readLocation(context, s);
        if (ret.left.isVarArgs && ret.right.isVarArgs) {
            ret.isVarArgs = true;
        }
        return ret;
    }

    @Override
    public TypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding, boolean requireExactType) {
        if (requireExactType) {
            return this.notExactType(scope);
        }
        this.left = this.left.resolveBindings(scope, bindings, false, false);
        this.right = this.right.resolveBindings(scope, bindings, false, false);
        return this;
    }

    @Override
    public TypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        TypePattern newLeft = this.left.parameterizeWith(typeVariableMap, w);
        TypePattern newRight = this.right.parameterizeWith(typeVariableMap, w);
        OrTypePattern ret = new OrTypePattern(newLeft, newRight);
        ret.copyLocationFrom(this);
        return ret;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        if (this.annotationPattern != AnnotationTypePattern.ANY) {
            buff.append('(');
            buff.append(this.annotationPattern.toString());
            buff.append(' ');
        }
        buff.append('(');
        buff.append(this.left.toString());
        buff.append(" || ");
        buff.append(this.right.toString());
        buff.append(')');
        if (this.annotationPattern != AnnotationTypePattern.ANY) {
            buff.append(')');
        }
        return buff.toString();
    }

    @Override
    public boolean isStarAnnotation() {
        return this.left.isStarAnnotation() || this.right.isStarAnnotation();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OrTypePattern)) {
            return false;
        }
        OrTypePattern other = (OrTypePattern)obj;
        return this.left.equals(other.left) && this.right.equals(other.right);
    }

    public int hashCode() {
        int ret = 17;
        ret += 37 * this.left.hashCode();
        return ret += 37 * this.right.hashCode();
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
}


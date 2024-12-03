/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;

public class OrPointcut
extends Pointcut {
    Pointcut left;
    Pointcut right;
    private int couldMatchKinds;

    public OrPointcut(Pointcut left, Pointcut right) {
        this.left = left;
        this.right = right;
        this.setLocation(left.getSourceContext(), left.getStart(), right.getEnd());
        this.pointcutKind = (byte)6;
        this.couldMatchKinds = left.couldMatchKinds() | right.couldMatchKinds();
    }

    @Override
    public int couldMatchKinds() {
        return this.couldMatchKinds;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        FuzzyBoolean leftMatch = this.left.fastMatch(type);
        if (leftMatch.alwaysTrue()) {
            return leftMatch;
        }
        return leftMatch.or(this.right.fastMatch(type));
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        FuzzyBoolean leftMatch = this.left.match(shadow);
        if (leftMatch.alwaysTrue()) {
            return leftMatch;
        }
        return leftMatch.or(this.right.match(shadow));
    }

    public String toString() {
        return "(" + this.left.toString() + " || " + this.right.toString() + ")";
    }

    public boolean equals(Object other) {
        if (!(other instanceof OrPointcut)) {
            return false;
        }
        OrPointcut o = (OrPointcut)other;
        return o.left.equals(this.left) && o.right.equals(this.right);
    }

    public int hashCode() {
        int result = 31;
        result = 37 * result + this.left.hashCode();
        result = 37 * result + this.right.hashCode();
        return result;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        Bindings old = bindings == null ? null : bindings.copy();
        this.left.resolveBindings(scope, bindings);
        this.right.resolveBindings(scope, old);
        if (bindings != null) {
            bindings.checkEquals(old, scope);
        }
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(6);
        this.left.write(s);
        this.right.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        OrPointcut ret = new OrPointcut(Pointcut.read(s, context), Pointcut.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        return Test.makeOr(this.left.findResidue(shadow, state), this.right.findResidue(shadow, state));
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        OrPointcut ret = new OrPointcut(this.left.concretize(inAspect, declaringType, bindings), this.right.concretize(inAspect, declaringType, bindings));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        OrPointcut ret = new OrPointcut(this.left.parameterizeWith(typeVariableMap, w), this.right.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    public Pointcut getLeft() {
        return this.left;
    }

    public Pointcut getRight() {
        return this.right;
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


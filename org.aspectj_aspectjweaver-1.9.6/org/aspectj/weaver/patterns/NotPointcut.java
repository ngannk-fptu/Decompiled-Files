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

public class NotPointcut
extends Pointcut {
    private Pointcut body;

    public NotPointcut(Pointcut negated) {
        this.body = negated;
        this.pointcutKind = (byte)7;
        this.setLocation(negated.getSourceContext(), negated.getStart(), negated.getEnd());
    }

    public NotPointcut(Pointcut pointcut, int startPos) {
        this(pointcut);
        this.setLocation(pointcut.getSourceContext(), startPos, pointcut.getEnd());
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    public Pointcut getNegatedPointcut() {
        return this.body;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return this.body.fastMatch(type).not();
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        return this.body.match(shadow).not();
    }

    public String toString() {
        return "!" + this.body.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof NotPointcut)) {
            return false;
        }
        NotPointcut o = (NotPointcut)other;
        return o.body.equals(this.body);
    }

    public int hashCode() {
        return 851 + this.body.hashCode();
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.body.resolveBindings(scope, null);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(7);
        this.body.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        NotPointcut ret = new NotPointcut(Pointcut.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        return Test.makeNot(this.body.findResidue(shadow, state));
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        NotPointcut ret = new NotPointcut(this.body.concretize(inAspect, declaringType, bindings));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        NotPointcut ret = new NotPointcut(this.body.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object traverse(PatternNodeVisitor visitor, Object data) {
        Object ret = this.accept(visitor, data);
        this.body.traverse(visitor, ret);
        return ret;
    }
}


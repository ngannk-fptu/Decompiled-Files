/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.CallExpr;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;

public class PerSingleton
extends PerClause {
    private ResolvedMember perSingletonAspectOfMethod;

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return FuzzyBoolean.YES;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        return FuzzyBoolean.YES;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        return this;
    }

    @Override
    public Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (this.perSingletonAspectOfMethod == null) {
            this.perSingletonAspectOfMethod = AjcMemberMaker.perSingletonAspectOfMethod(this.inAspect);
        }
        CallExpr myInstance = Expr.makeCallExpr(this.perSingletonAspectOfMethod, Expr.NONE, this.inAspect);
        state.setAspectInstance(myInstance);
        return Literal.TRUE;
    }

    @Override
    public PerClause concretize(ResolvedType inAspect) {
        PerSingleton ret = new PerSingleton();
        ret.copyLocationFrom(this);
        World world = inAspect.getWorld();
        ret.inAspect = inAspect;
        if (inAspect.isAnnotationStyleAspect() && !inAspect.isAbstract()) {
            if (this.getKind() == SINGLETON) {
                inAspect.crosscuttingMembers.addTypeMunger(world.getWeavingSupport().makePerClauseAspect(inAspect, this.getKind()));
            } else {
                inAspect.crosscuttingMembers.addLateTypeMunger(world.getWeavingSupport().makePerClauseAspect(inAspect, this.getKind()));
            }
        }
        if (inAspect.isAnnotationStyleAspect() && !inAspect.getWorld().isXnoInline()) {
            inAspect.crosscuttingMembers.addTypeMunger(world.getWeavingSupport().createAccessForInlineMunger(inAspect));
        }
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        SINGLETON.write(s);
        this.writeLocation(s);
    }

    public static PerClause readPerClause(VersionedDataInputStream s, ISourceContext context) throws IOException {
        PerSingleton ret = new PerSingleton();
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public PerClause.Kind getKind() {
        return SINGLETON;
    }

    public String toString() {
        return "persingleton(" + this.inAspect + ")";
    }

    @Override
    public String toDeclarationString() {
        return "";
    }

    public boolean equals(Object other) {
        if (!(other instanceof PerSingleton)) {
            return false;
        }
        PerSingleton pc = (PerSingleton)other;
        return pc.inAspect == null ? this.inAspect == null : pc.inAspect.equals(this.inAspect);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.inAspect == null ? 0 : this.inAspect.hashCode());
        return result;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.CrosscuttingMembers;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.CallExpr;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;

public class PerCflow
extends PerClause {
    private final boolean isBelow;
    private final Pointcut entry;

    public PerCflow(Pointcut entry, boolean isBelow) {
        this.entry = entry;
        this.isBelow = isBelow;
    }

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
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        return FuzzyBoolean.YES;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.entry.resolve(scope);
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        PerCflow ret = new PerCflow(this.entry.parameterizeWith(typeVariableMap, w), this.isBelow);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        CallExpr myInstance = Expr.makeCallExpr(AjcMemberMaker.perCflowAspectOfMethod(this.inAspect), Expr.NONE, this.inAspect);
        state.setAspectInstance(myInstance);
        return Test.makeCall(AjcMemberMaker.perCflowHasAspectMethod(this.inAspect), Expr.NONE);
    }

    @Override
    public PerClause concretize(ResolvedType inAspect) {
        PerCflow ret = new PerCflow(this.entry, this.isBelow);
        ret.inAspect = inAspect;
        if (inAspect.isAbstract()) {
            return ret;
        }
        ResolvedMemberImpl cflowStackField = new ResolvedMemberImpl(Member.FIELD, inAspect, 9, UnresolvedType.forName("org.aspectj.runtime.internal.CFlowStack"), "ajc$perCflowStack", UnresolvedType.NONE);
        World world = inAspect.getWorld();
        CrosscuttingMembers xcut = inAspect.crosscuttingMembers;
        Collection<ShadowMunger> previousCflowEntries = xcut.getCflowEntries();
        Pointcut concreteEntry = this.entry.concretize(inAspect, inAspect, 0, null);
        ArrayList<ShadowMunger> innerCflowEntries = new ArrayList<ShadowMunger>(xcut.getCflowEntries());
        innerCflowEntries.removeAll(previousCflowEntries);
        xcut.addConcreteShadowMunger(Advice.makePerCflowEntry(world, concreteEntry, this.isBelow, cflowStackField, inAspect, innerCflowEntries));
        if (inAspect.isAnnotationStyleAspect() && !inAspect.isAbstract()) {
            inAspect.crosscuttingMembers.addLateTypeMunger(inAspect.getWorld().getWeavingSupport().makePerClauseAspect(inAspect, this.getKind()));
        }
        if (inAspect.isAnnotationStyleAspect() && !inAspect.getWorld().isXnoInline()) {
            inAspect.crosscuttingMembers.addTypeMunger(inAspect.getWorld().getWeavingSupport().createAccessForInlineMunger(inAspect));
        }
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        PERCFLOW.write(s);
        this.entry.write(s);
        s.writeBoolean(this.isBelow);
        this.writeLocation(s);
    }

    public static PerClause readPerClause(VersionedDataInputStream s, ISourceContext context) throws IOException {
        PerCflow ret = new PerCflow(Pointcut.read(s, context), s.readBoolean());
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public PerClause.Kind getKind() {
        return PERCFLOW;
    }

    public Pointcut getEntry() {
        return this.entry;
    }

    public String toString() {
        return "percflow(" + this.inAspect + " on " + this.entry + ")";
    }

    @Override
    public String toDeclarationString() {
        if (this.isBelow) {
            return "percflowbelow(" + this.entry + ")";
        }
        return "percflow(" + this.entry + ")";
    }

    public boolean equals(Object other) {
        if (!(other instanceof PerCflow)) {
            return false;
        }
        PerCflow pc = (PerCflow)other;
        return pc.isBelow && this.isBelow && (pc.inAspect == null ? this.inAspect == null : pc.inAspect.equals(this.inAspect)) && (pc.entry == null ? this.entry == null : pc.entry.equals(this.entry));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.isBelow ? 0 : 1);
        result = 37 * result + (this.inAspect == null ? 0 : this.inAspect.hashCode());
        result = 37 * result + (this.entry == null ? 0 : this.entry.hashCode());
        return result;
    }
}


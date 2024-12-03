/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.PerObjectInterfaceTypeMunger;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.CallExpr;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;

public class PerObject
extends PerClause {
    private final boolean isThis;
    private final Pointcut entry;
    private static final int thisKindSet;
    private static final int targetKindSet;

    public PerObject(Pointcut entry, boolean isThis) {
        this.entry = entry;
        this.isThis = isThis;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public int couldMatchKinds() {
        return this.isThis ? thisKindSet : targetKindSet;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        if (this.isThis) {
            return FuzzyBoolean.fromBoolean(shadow.hasThis());
        }
        return FuzzyBoolean.fromBoolean(shadow.hasTarget());
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.entry.resolve(scope);
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        PerObject ret = new PerObject(this.entry.parameterizeWith(typeVariableMap, w), this.isThis);
        ret.copyLocationFrom(this);
        return ret;
    }

    private Var getVar(Shadow shadow) {
        return this.isThis ? shadow.getThisVar() : shadow.getTargetVar();
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        CallExpr myInstance = Expr.makeCallExpr(AjcMemberMaker.perObjectAspectOfMethod(this.inAspect), new Expr[]{this.getVar(shadow)}, this.inAspect);
        state.setAspectInstance(myInstance);
        return Test.makeCall(AjcMemberMaker.perObjectHasAspectMethod(this.inAspect), new Expr[]{this.getVar(shadow)});
    }

    @Override
    public PerClause concretize(ResolvedType inAspect) {
        PerObject ret = new PerObject(this.entry, this.isThis);
        ret.inAspect = inAspect;
        if (inAspect.isAbstract()) {
            return ret;
        }
        World world = inAspect.getWorld();
        Pointcut concreteEntry = this.entry.concretize(inAspect, inAspect, 0, null);
        inAspect.crosscuttingMembers.addConcreteShadowMunger(Advice.makePerObjectEntry(world, concreteEntry, this.isThis, inAspect));
        PerObjectInterfaceTypeMunger munger = new PerObjectInterfaceTypeMunger(inAspect, concreteEntry);
        inAspect.crosscuttingMembers.addLateTypeMunger(world.getWeavingSupport().concreteTypeMunger(munger, inAspect));
        if (inAspect.isAnnotationStyleAspect() && !inAspect.isAbstract()) {
            inAspect.crosscuttingMembers.addLateTypeMunger(inAspect.getWorld().getWeavingSupport().makePerClauseAspect(inAspect, this.getKind()));
        }
        if (inAspect.isAnnotationStyleAspect() && !inAspect.getWorld().isXnoInline()) {
            inAspect.crosscuttingMembers.addTypeMunger(world.getWeavingSupport().createAccessForInlineMunger(inAspect));
        }
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        PEROBJECT.write(s);
        this.entry.write(s);
        s.writeBoolean(this.isThis);
        this.writeLocation(s);
    }

    public static PerClause readPerClause(VersionedDataInputStream s, ISourceContext context) throws IOException {
        PerObject ret = new PerObject(Pointcut.read(s, context), s.readBoolean());
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public PerClause.Kind getKind() {
        return PEROBJECT;
    }

    public boolean isThis() {
        return this.isThis;
    }

    public String toString() {
        return "per" + (this.isThis ? "this" : "target") + "(" + this.entry + ")";
    }

    @Override
    public String toDeclarationString() {
        return this.toString();
    }

    public Pointcut getEntry() {
        return this.entry;
    }

    public boolean equals(Object other) {
        if (!(other instanceof PerObject)) {
            return false;
        }
        PerObject pc = (PerObject)other;
        return pc.isThis && this.isThis && (pc.inAspect == null ? this.inAspect == null : pc.inAspect.equals(this.inAspect)) && (pc.entry == null ? this.entry == null : pc.entry.equals(this.entry));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.isThis ? 0 : 1);
        result = 37 * result + (this.inAspect == null ? 0 : this.inAspect.hashCode());
        result = 37 * result + (this.entry == null ? 0 : this.entry.hashCode());
        return result;
    }

    static {
        int thisFlags = Shadow.ALL_SHADOW_KINDS_BITS;
        int targFlags = Shadow.ALL_SHADOW_KINDS_BITS;
        for (int i = 0; i < Shadow.SHADOW_KINDS.length; ++i) {
            Shadow.Kind kind = Shadow.SHADOW_KINDS[i];
            if (kind.neverHasThis()) {
                thisFlags -= kind.bit;
            }
            if (!kind.neverHasTarget()) continue;
            targFlags -= kind.bit;
        }
        thisKindSet = thisFlags;
        targetKindSet = targFlags;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.util.FileUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.CrosscuttingMembers;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;

public class CflowPointcut
extends Pointcut {
    private final Pointcut entry;
    boolean isBelow;
    private int[] freeVars;
    public static final ResolvedPointcutDefinition CFLOW_MARKER = new ResolvedPointcutDefinition(null, 0, null, UnresolvedType.NONE, Pointcut.makeMatchesNothing(Pointcut.RESOLVED));

    public CflowPointcut(Pointcut entry, boolean isBelow, int[] freeVars) {
        this.entry = entry;
        this.isBelow = isBelow;
        this.freeVars = freeVars;
        this.pointcutKind = (byte)10;
    }

    public boolean isCflowBelow() {
        return this.isBelow;
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    public Pointcut getEntry() {
        return this.entry;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(10);
        this.entry.write(s);
        s.writeBoolean(this.isBelow);
        FileUtil.writeIntArray(this.freeVars, s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        CflowPointcut ret = new CflowPointcut(Pointcut.read(s, context), s.readBoolean(), FileUtil.readIntArray(s));
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        CflowPointcut ret = new CflowPointcut(this.entry.parameterizeWith(typeVariableMap, w), this.isBelow, this.freeVars);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        if (bindings == null) {
            this.entry.resolveBindings(scope, null);
            this.entry.state = RESOLVED;
            this.freeVars = new int[0];
        } else {
            Bindings entryBindings = new Bindings(bindings.size());
            this.entry.resolveBindings(scope, entryBindings);
            this.entry.state = RESOLVED;
            this.freeVars = entryBindings.getUsedFormals();
            bindings.mergeIn(entryBindings, scope);
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof CflowPointcut)) {
            return false;
        }
        CflowPointcut o = (CflowPointcut)other;
        return o.entry.equals(this.entry) && o.isBelow == this.isBelow;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.entry.hashCode();
        result = 37 * result + (this.isBelow ? 0 : 1);
        return result;
    }

    public String toString() {
        return "cflow" + (this.isBelow ? "below" : "") + "(" + this.entry + ")";
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        throw new RuntimeException("unimplemented - did concretization fail?");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        Pointcut concreteEntry;
        if (this.isDeclare(bindings.getEnclosingAdvice())) {
            inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("cflowInDeclare", this.isBelow ? "below" : ""), bindings.getEnclosingAdvice().getSourceLocation(), null);
            return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
        }
        IntMap entryBindings = new IntMap();
        if (this.freeVars != null) {
            int len = this.freeVars.length;
            for (int i = 0; i < len; ++i) {
                int freeVar = this.freeVars[i];
                entryBindings.put(freeVar, i);
            }
        }
        entryBindings.copyContext(bindings);
        World world = inAspect.getWorld();
        ResolvedType concreteAspect = bindings.getConcreteAspect();
        CrosscuttingMembers xcut = concreteAspect.crosscuttingMembers;
        Collection<ShadowMunger> previousCflowEntries = xcut.getCflowEntries();
        entryBindings.pushEnclosingDefinition(CFLOW_MARKER);
        try {
            concreteEntry = this.entry.concretize(inAspect, declaringType, entryBindings);
        }
        finally {
            entryBindings.popEnclosingDefinitition();
        }
        ArrayList<ShadowMunger> innerCflowEntries = new ArrayList<ShadowMunger>(xcut.getCflowEntries());
        innerCflowEntries.removeAll(previousCflowEntries);
        if (this.freeVars == null || this.freeVars.length == 0) {
            ResolvedMember localCflowField = null;
            Object field = this.getCflowfield(xcut, concreteEntry, concreteAspect, "counter");
            if (field != null) {
                localCflowField = (ResolvedMember)field;
            } else {
                localCflowField = new ResolvedMemberImpl(Member.FIELD, concreteAspect, 9, NameMangler.cflowCounter(xcut), UnresolvedType.forName("org.aspectj.runtime.internal.CFlowCounter").getSignature());
                concreteAspect.crosscuttingMembers.addTypeMunger(world.getWeavingSupport().makeCflowCounterFieldAdder(localCflowField));
                concreteAspect.crosscuttingMembers.addConcreteShadowMunger(Advice.makeCflowEntry(world, concreteEntry, this.isBelow, localCflowField, this.freeVars == null ? 0 : this.freeVars.length, innerCflowEntries, inAspect));
                this.putCflowfield(xcut, concreteEntry, concreteAspect, localCflowField, "counter");
            }
            ConcreteCflowPointcut ret = new ConcreteCflowPointcut(concreteAspect, localCflowField, null, true);
            ret.copyLocationFrom(this);
            return ret;
        }
        ArrayList<ConcreteCflowPointcut.Slot> slots = new ArrayList<ConcreteCflowPointcut.Slot>();
        int len = this.freeVars.length;
        for (int i = 0; i < len; ++i) {
            int freeVar = this.freeVars[i];
            if (!bindings.hasKey(freeVar)) continue;
            int formalIndex = bindings.get(freeVar);
            ResolvedPointcutDefinition enclosingDef = bindings.peekEnclosingDefinition();
            ResolvedType formalType = null;
            formalType = enclosingDef != null && enclosingDef.getParameterTypes().length > 0 ? enclosingDef.getParameterTypes()[freeVar].resolve(world) : bindings.getAdviceSignature().getParameterTypes()[formalIndex].resolve(world);
            ConcreteCflowPointcut.Slot slot = new ConcreteCflowPointcut.Slot(formalIndex, formalType, i);
            slots.add(slot);
        }
        ResolvedMember localCflowField = null;
        Object field = this.getCflowfield(xcut, concreteEntry, concreteAspect, "stack");
        if (field != null) {
            localCflowField = (ResolvedMember)field;
        } else {
            localCflowField = new ResolvedMemberImpl(Member.FIELD, concreteAspect, 9, NameMangler.cflowStack(xcut), UnresolvedType.forName("org.aspectj.runtime.internal.CFlowStack").getSignature());
            concreteAspect.crosscuttingMembers.addConcreteShadowMunger(Advice.makeCflowEntry(world, concreteEntry, this.isBelow, localCflowField, this.freeVars.length, innerCflowEntries, inAspect));
            concreteAspect.crosscuttingMembers.addTypeMunger(world.getWeavingSupport().makeCflowStackFieldAdder(localCflowField));
            this.putCflowfield(xcut, concreteEntry, concreteAspect, localCflowField, "stack");
        }
        ConcreteCflowPointcut ret = new ConcreteCflowPointcut(concreteAspect, localCflowField, slots, false);
        ret.copyLocationFrom(this);
        return ret;
    }

    private String getKey(Pointcut p, ResolvedType a, String stackOrCounter) {
        StringBuffer sb = new StringBuffer();
        sb.append(a.getName());
        sb.append("::");
        sb.append(p.toString());
        sb.append("::");
        sb.append(stackOrCounter);
        return sb.toString();
    }

    private Object getCflowfield(CrosscuttingMembers xcut, Pointcut pcutkey, ResolvedType concreteAspect, String stackOrCounter) {
        String key = this.getKey(pcutkey, concreteAspect, stackOrCounter);
        Object o = null;
        o = this.isBelow ? xcut.getCflowBelowFields().get(key) : xcut.getCflowFields().get(key);
        return o;
    }

    private void putCflowfield(CrosscuttingMembers xcut, Pointcut pcutkey, ResolvedType concreteAspect, Object o, String stackOrCounter) {
        String key = this.getKey(pcutkey, concreteAspect, stackOrCounter);
        if (this.isBelow) {
            xcut.getCflowBelowFields().put(key, o);
        } else {
            xcut.getCflowFields().put(key, o);
        }
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}


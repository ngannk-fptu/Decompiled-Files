/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberImpl;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;

public class ConcreteCflowPointcut
extends Pointcut {
    private final Member cflowField;
    List<Slot> slots;
    boolean usesCounter;
    ResolvedType aspect;
    private static final Member cflowStackIsValidMethod = MemberImpl.method(NameMangler.CFLOW_STACK_UNRESOLVEDTYPE, 0, UnresolvedType.BOOLEAN, "isValid", UnresolvedType.NONE);
    private static final Member cflowCounterIsValidMethod = MemberImpl.method(NameMangler.CFLOW_COUNTER_UNRESOLVEDTYPE, 0, UnresolvedType.BOOLEAN, "isValid", UnresolvedType.NONE);

    public ConcreteCflowPointcut(ResolvedType aspect, Member cflowField, List<Slot> slots, boolean usesCounter) {
        this.aspect = aspect;
        this.cflowField = cflowField;
        this.slots = slots;
        this.usesCounter = usesCounter;
        this.pointcutKind = (byte)10;
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
        if (this.slots != null) {
            for (Slot slot : this.slots) {
                ResolvedType rt = slot.formalType;
                if (!rt.isMissing()) continue;
                ISourceLocation[] locs = new ISourceLocation[]{this.getSourceLocation()};
                Message m = new Message(WeaverMessages.format("missingTypePreventsMatch", rt.getName()), "", Message.WARNING, shadow.getSourceLocation(), null, locs);
                rt.getWorld().getMessageHandler().handleMessage(m);
                return FuzzyBoolean.NO;
            }
        }
        return FuzzyBoolean.MAYBE;
    }

    public int[] getUsedFormalSlots() {
        if (this.slots == null) {
            return new int[0];
        }
        int[] indices = new int[this.slots.size()];
        for (int i = 0; i < indices.length; ++i) {
            indices[i] = this.slots.get((int)i).formalIndex;
        }
        return indices;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        throw new RuntimeException("unimplemented");
    }

    public boolean equals(Object other) {
        if (!(other instanceof ConcreteCflowPointcut)) {
            return false;
        }
        ConcreteCflowPointcut o = (ConcreteCflowPointcut)other;
        return o.cflowField.equals(this.cflowField);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.cflowField.hashCode();
        return result;
    }

    public String toString() {
        return "concretecflow(" + this.cflowField + ")";
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (this.usesCounter) {
            return Test.makeFieldGetCall(this.cflowField, cflowCounterIsValidMethod, Expr.NONE);
        }
        if (this.slots != null) {
            for (Slot slot : this.slots) {
                state.set(slot.formalIndex, this.aspect.getWorld().getWeavingSupport().makeCflowAccessVar(slot.formalType, this.cflowField, slot.arrayIndex));
            }
        }
        return Test.makeFieldGetCall(this.cflowField, cflowStackIsValidMethod, Expr.NONE);
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public static class Slot {
        int formalIndex;
        ResolvedType formalType;
        int arrayIndex;

        public Slot(int formalIndex, ResolvedType formalType, int arrayIndex) {
            this.formalIndex = formalIndex;
            this.formalType = formalType;
            this.arrayIndex = arrayIndex;
        }

        public boolean equals(Object other) {
            if (!(other instanceof Slot)) {
                return false;
            }
            Slot o = (Slot)other;
            return o.formalIndex == this.formalIndex && o.arrayIndex == this.arrayIndex && o.formalType.equals(this.formalType);
        }

        public int hashCode() {
            int result = 19;
            result = 37 * result + this.formalIndex;
            result = 37 * result + this.arrayIndex;
            result = 37 * result + this.formalType.hashCode();
            return result;
        }

        public String toString() {
            return "Slot(" + this.formalIndex + ", " + this.formalType + ", " + this.arrayIndex + ")";
        }
    }
}


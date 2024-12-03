/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.util.TypeSafeEnum;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.Checker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.PoliceExtensionUse;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.AnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.ScopeWithTypeVariables;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.WithinAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinCodeAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;

public abstract class Pointcut
extends PatternNode {
    public String[] m_ignoreUnboundBindingForNames = EMPTY_STRING_ARRAY;
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final State SYMBOLIC = new State("symbolic", 0);
    public static final State RESOLVED = new State("resolved", 1);
    public static final State CONCRETE = new State("concrete", 2);
    protected byte pointcutKind;
    public State state;
    protected int lastMatchedShadowId;
    private FuzzyBoolean lastMatchedShadowResult;
    private String[] typeVariablesInScope = EMPTY_STRING_ARRAY;
    protected boolean hasBeenParameterized = false;
    public static final byte KINDED = 1;
    public static final byte WITHIN = 2;
    public static final byte THIS_OR_TARGET = 3;
    public static final byte ARGS = 4;
    public static final byte AND = 5;
    public static final byte OR = 6;
    public static final byte NOT = 7;
    public static final byte REFERENCE = 8;
    public static final byte IF = 9;
    public static final byte CFLOW = 10;
    public static final byte WITHINCODE = 12;
    public static final byte HANDLER = 13;
    public static final byte IF_TRUE = 14;
    public static final byte IF_FALSE = 15;
    public static final byte ANNOTATION = 16;
    public static final byte ATWITHIN = 17;
    public static final byte ATWITHINCODE = 18;
    public static final byte ATTHIS_OR_TARGET = 19;
    public static final byte NONE = 20;
    public static final byte ATARGS = 21;
    public static final byte USER_EXTENSION = 22;

    public Pointcut() {
        this.state = SYMBOLIC;
    }

    public abstract FuzzyBoolean fastMatch(FastMatchInfo var1);

    public abstract int couldMatchKinds();

    public String[] getTypeVariablesInScope() {
        return this.typeVariablesInScope;
    }

    public void setTypeVariablesInScope(String[] typeVars) {
        this.typeVariablesInScope = typeVars;
    }

    public final FuzzyBoolean match(Shadow shadow) {
        if (shadow.shadowId == this.lastMatchedShadowId) {
            return this.lastMatchedShadowResult;
        }
        FuzzyBoolean ret = shadow.getKind().isSet(this.couldMatchKinds()) ? this.matchInternal(shadow) : FuzzyBoolean.NO;
        this.lastMatchedShadowId = shadow.shadowId;
        this.lastMatchedShadowResult = ret;
        return ret;
    }

    protected abstract FuzzyBoolean matchInternal(Shadow var1);

    public byte getPointcutKind() {
        return this.pointcutKind;
    }

    protected abstract void resolveBindings(IScope var1, Bindings var2);

    public final Pointcut resolve(IScope scope) {
        this.assertState(SYMBOLIC);
        Bindings bindingTable = new Bindings(scope.getFormalCount());
        IScope bindingResolutionScope = scope;
        if (this.typeVariablesInScope.length > 0) {
            bindingResolutionScope = new ScopeWithTypeVariables(this.typeVariablesInScope, scope);
        }
        this.resolveBindings(bindingResolutionScope, bindingTable);
        bindingTable.checkAllBound(bindingResolutionScope);
        this.state = RESOLVED;
        return this;
    }

    public final Pointcut concretize(ResolvedType inAspect, ResolvedType declaringType, int arity) {
        Pointcut ret = this.concretize(inAspect, declaringType, IntMap.idMap(arity));
        ret.m_ignoreUnboundBindingForNames = this.m_ignoreUnboundBindingForNames;
        return ret;
    }

    public final Pointcut concretize(ResolvedType inAspect, ResolvedType declaringType, int arity, ShadowMunger advice) {
        IntMap map = IntMap.idMap(arity);
        map.setEnclosingAdvice(advice);
        map.setConcreteAspect(inAspect);
        return this.concretize(inAspect, declaringType, map);
    }

    public boolean isDeclare(ShadowMunger munger) {
        if (munger == null) {
            return false;
        }
        if (munger instanceof Checker) {
            return true;
        }
        return ((Advice)munger).getKind().equals(AdviceKind.Softener);
    }

    public final Pointcut concretize(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        Pointcut ret = this.concretize1(inAspect, declaringType, bindings);
        if (this.shouldCopyLocationForConcretize()) {
            ret.copyLocationFrom(this);
        }
        ret.state = CONCRETE;
        ret.m_ignoreUnboundBindingForNames = this.m_ignoreUnboundBindingForNames;
        return ret;
    }

    protected boolean shouldCopyLocationForConcretize() {
        return true;
    }

    protected abstract Pointcut concretize1(ResolvedType var1, ResolvedType var2, IntMap var3);

    public final Test findResidue(Shadow shadow, ExposedState state) {
        Test ret = this.findResidueInternal(shadow, state);
        this.lastMatchedShadowId = shadow.shadowId;
        return ret;
    }

    protected abstract Test findResidueInternal(Shadow var1, ExposedState var2);

    public void postRead(ResolvedType enclosingType) {
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        Pointcut ret;
        byte kind = s.readByte();
        switch (kind) {
            case 1: {
                ret = KindedPointcut.read(s, context);
                break;
            }
            case 2: {
                ret = WithinPointcut.read(s, context);
                break;
            }
            case 3: {
                ret = ThisOrTargetPointcut.read(s, context);
                break;
            }
            case 4: {
                ret = ArgsPointcut.read(s, context);
                break;
            }
            case 5: {
                ret = AndPointcut.read(s, context);
                break;
            }
            case 6: {
                ret = OrPointcut.read(s, context);
                break;
            }
            case 7: {
                ret = NotPointcut.read(s, context);
                break;
            }
            case 8: {
                ret = ReferencePointcut.read(s, context);
                break;
            }
            case 9: {
                ret = IfPointcut.read(s, context);
                break;
            }
            case 10: {
                ret = CflowPointcut.read(s, context);
                break;
            }
            case 12: {
                ret = WithincodePointcut.read(s, context);
                break;
            }
            case 13: {
                ret = HandlerPointcut.read(s, context);
                break;
            }
            case 14: {
                ret = IfPointcut.makeIfTruePointcut(RESOLVED);
                break;
            }
            case 15: {
                ret = IfPointcut.makeIfFalsePointcut(RESOLVED);
                break;
            }
            case 16: {
                ret = AnnotationPointcut.read(s, context);
                break;
            }
            case 17: {
                ret = WithinAnnotationPointcut.read(s, context);
                break;
            }
            case 18: {
                ret = WithinCodeAnnotationPointcut.read(s, context);
                break;
            }
            case 19: {
                ret = ThisOrTargetAnnotationPointcut.read(s, context);
                break;
            }
            case 21: {
                ret = ArgsAnnotationPointcut.read(s, context);
                break;
            }
            case 20: {
                ret = Pointcut.makeMatchesNothing(RESOLVED);
                break;
            }
            default: {
                throw new BCException("unknown kind: " + kind);
            }
        }
        ret.state = RESOLVED;
        ret.pointcutKind = kind;
        return ret;
    }

    public void check(ISourceContext ctx, World world) {
        PoliceExtensionUse pointcutPolice = new PoliceExtensionUse(world, this);
        this.accept(pointcutPolice, null);
        if (pointcutPolice.synchronizationDesignatorEncountered()) {
            world.setSynchronizationPointcutsInUse();
        }
    }

    public static Pointcut fromString(String str) {
        PatternParser parser = new PatternParser(str);
        return parser.parsePointcut();
    }

    public static Pointcut makeMatchesNothing(State state) {
        MatchesNothingPointcut ret = new MatchesNothingPointcut();
        ret.state = state;
        return ret;
    }

    public void assertState(State state) {
        if (this.state != state) {
            throw new BCException("expected state: " + state + " got: " + this.state);
        }
    }

    public abstract Pointcut parameterizeWith(Map<String, UnresolvedType> var1, World var2);

    static class MatchesNothingPointcut
    extends Pointcut {
        MatchesNothingPointcut() {
        }

        @Override
        protected Test findResidueInternal(Shadow shadow, ExposedState state) {
            return Literal.FALSE;
        }

        @Override
        public int couldMatchKinds() {
            return Shadow.NO_SHADOW_KINDS_BITS;
        }

        @Override
        public FuzzyBoolean fastMatch(FastMatchInfo type) {
            return FuzzyBoolean.NO;
        }

        @Override
        protected FuzzyBoolean matchInternal(Shadow shadow) {
            return FuzzyBoolean.NO;
        }

        @Override
        public void resolveBindings(IScope scope, Bindings bindings) {
        }

        @Override
        public void postRead(ResolvedType enclosingType) {
        }

        @Override
        public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
            return MatchesNothingPointcut.makeMatchesNothing(this.state);
        }

        @Override
        public void write(CompressingDataOutputStream s) throws IOException {
            s.writeByte(20);
        }

        public String toString() {
            return "";
        }

        @Override
        public Object accept(PatternNodeVisitor visitor, Object data) {
            return visitor.visit(this, data);
        }

        @Override
        public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
            return this;
        }
    }

    public static final class State
    extends TypeSafeEnum {
        public State(String name, int key) {
            super(name, key);
        }
    }
}


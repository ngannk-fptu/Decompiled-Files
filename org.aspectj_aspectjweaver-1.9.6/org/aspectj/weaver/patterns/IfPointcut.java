/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
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
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;

public class IfPointcut
extends Pointcut {
    public ResolvedMember testMethod;
    public int extraParameterFlags;
    private final String enclosingPointcutHint;
    public Pointcut residueSource;
    int baseArgsCount;
    private boolean findingResidue = false;
    private int ifLastMatchedShadowId;
    private Test ifLastMatchedShadowResidue;
    private IfPointcut partiallyConcretized = null;

    public IfPointcut(ResolvedMember testMethod, int extraParameterFlags) {
        this.testMethod = testMethod;
        this.extraParameterFlags = extraParameterFlags;
        this.pointcutKind = (byte)9;
        this.enclosingPointcutHint = null;
    }

    public IfPointcut(String enclosingPointcutHint) {
        this.pointcutKind = (byte)9;
        this.enclosingPointcutHint = enclosingPointcutHint;
        this.testMethod = null;
        this.extraParameterFlags = -1;
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
        if ((this.extraParameterFlags & 0x10) != 0) {
            if ((this.extraParameterFlags & 0x20) != 0) {
                return FuzzyBoolean.YES;
            }
            return FuzzyBoolean.NO;
        }
        return FuzzyBoolean.MAYBE;
    }

    public boolean alwaysFalse() {
        return false;
    }

    public boolean alwaysTrue() {
        return false;
    }

    public Pointcut getResidueSource() {
        return this.residueSource;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(9);
        s.writeBoolean(this.testMethod != null);
        if (this.testMethod != null) {
            this.testMethod.write(s);
        }
        s.writeByte(this.extraParameterFlags);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        boolean hasTestMethod = s.readBoolean();
        ResolvedMemberImpl resolvedTestMethod = null;
        if (hasTestMethod) {
            resolvedTestMethod = ResolvedMemberImpl.readResolvedMember(s, context);
        }
        IfPointcut ret = new IfPointcut(resolvedTestMethod, s.readByte());
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
    }

    public boolean equals(Object other) {
        if (!(other instanceof IfPointcut)) {
            return false;
        }
        IfPointcut o = (IfPointcut)other;
        if (o.testMethod == null) {
            return this.testMethod == null;
        }
        return o.testMethod.equals(this.testMethod);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.testMethod.hashCode();
        return result;
    }

    public String toString() {
        if (this.extraParameterFlags < 0) {
            return "if()";
        }
        return "if(" + this.testMethod + ")";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (this.findingResidue) {
            return Literal.TRUE;
        }
        this.findingResidue = true;
        try {
            if (shadow.shadowId == this.ifLastMatchedShadowId) {
                Test test = this.ifLastMatchedShadowResidue;
                return test;
            }
            Test ret = Literal.TRUE;
            ArrayList<Var> args = new ArrayList<Var>();
            if (this.extraParameterFlags >= 0) {
                if ((this.extraParameterFlags & 0x10) != 0) {
                    if ((this.extraParameterFlags & 0x20) != 0) {
                        ret = Literal.TRUE;
                        this.ifLastMatchedShadowId = shadow.shadowId;
                        this.ifLastMatchedShadowResidue = ret;
                        Literal literal = ret;
                        return literal;
                    }
                    ret = Literal.FALSE;
                    this.ifLastMatchedShadowId = shadow.shadowId;
                    this.ifLastMatchedShadowResidue = ret;
                    Literal literal = ret;
                    return literal;
                }
                if (this.baseArgsCount > 0) {
                    UnresolvedType[] pTypes;
                    ExposedState myState = new ExposedState(this.baseArgsCount);
                    myState.setConcreteAspect(state.getConcreteAspect());
                    this.residueSource.findResidue(shadow, myState);
                    UnresolvedType[] unresolvedTypeArray = pTypes = this.testMethod == null ? null : this.testMethod.getParameterTypes();
                    if (pTypes != null && this.baseArgsCount > pTypes.length) {
                        throw new BCException("Unexpected problem with testMethod " + this.testMethod + ": expecting " + this.baseArgsCount + " arguments");
                    }
                    for (int i = 0; i < this.baseArgsCount; ++i) {
                        Var v = myState.get(i);
                        if (v == null) continue;
                        args.add(v);
                        ret = Test.makeAnd(ret, Test.makeInstanceof(v, pTypes[i].resolve(shadow.getIWorld())));
                    }
                }
                if ((this.extraParameterFlags & 2) != 0) {
                    args.add(shadow.getThisJoinPointVar());
                }
                if ((this.extraParameterFlags & 4) != 0) {
                    args.add(shadow.getThisJoinPointStaticPartVar());
                }
                if ((this.extraParameterFlags & 8) != 0) {
                    args.add(shadow.getThisEnclosingJoinPointStaticPartVar());
                }
                if ((this.extraParameterFlags & 0x40) != 0) {
                    args.add(shadow.getThisAspectInstanceVar(state.getConcreteAspect()));
                }
            } else {
                int currentStateIndex = 0;
                for (int i = 0; i < this.testMethod.getParameterTypes().length; ++i) {
                    String argSignature = this.testMethod.getParameterTypes()[i].getSignature();
                    if (AjcMemberMaker.TYPEX_JOINPOINT.getSignature().equals(argSignature)) {
                        args.add(shadow.getThisJoinPointVar());
                        continue;
                    }
                    if (AjcMemberMaker.TYPEX_PROCEEDINGJOINPOINT.getSignature().equals(argSignature)) {
                        args.add(shadow.getThisJoinPointVar());
                        continue;
                    }
                    if (AjcMemberMaker.TYPEX_STATICJOINPOINT.getSignature().equals(argSignature)) {
                        args.add(shadow.getThisJoinPointStaticPartVar());
                        continue;
                    }
                    if (AjcMemberMaker.TYPEX_ENCLOSINGSTATICJOINPOINT.getSignature().equals(argSignature)) {
                        args.add(shadow.getThisEnclosingJoinPointStaticPartVar());
                        continue;
                    }
                    if (state.size() == 0 || currentStateIndex > state.size()) {
                        String[] paramNames = this.testMethod.getParameterNames();
                        StringBuffer errorParameter = new StringBuffer();
                        if (paramNames != null) {
                            errorParameter.append(this.testMethod.getParameterTypes()[i].getName()).append(" ");
                            errorParameter.append(paramNames[i]);
                            shadow.getIWorld().getMessageHandler().handleMessage(MessageUtil.error("Missing binding for if() pointcut method.  Parameter " + (i + 1) + "(" + errorParameter.toString() + ") must be bound - even in reference pointcuts  (compiler limitation)", this.testMethod.getSourceLocation()));
                        } else {
                            shadow.getIWorld().getMessageHandler().handleMessage(MessageUtil.error("Missing binding for if() pointcut method.  Parameter " + (i + 1) + " must be bound - even in reference pointcuts (compiler limitation)", this.testMethod.getSourceLocation()));
                        }
                        Literal literal = Literal.TRUE;
                        return literal;
                    }
                    Var v = state.get(currentStateIndex++);
                    while (v == null && currentStateIndex < state.size()) {
                        v = state.get(currentStateIndex++);
                    }
                    args.add(v);
                    ret = Test.makeAnd(ret, Test.makeInstanceof(v, this.testMethod.getParameterTypes()[i].resolve(shadow.getIWorld())));
                }
            }
            ret = Test.makeAnd(ret, Test.makeCall(this.testMethod, args.toArray(new Expr[args.size()])));
            this.ifLastMatchedShadowId = shadow.shadowId;
            this.ifLastMatchedShadowResidue = ret;
            Test test = ret;
            return test;
        }
        finally {
            this.findingResidue = false;
        }
    }

    @Override
    protected boolean shouldCopyLocationForConcretize() {
        return false;
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        IfPointcut ret;
        ResolvedPointcutDefinition def;
        if (this.isDeclare(bindings.getEnclosingAdvice())) {
            inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("ifInDeclare"), bindings.getEnclosingAdvice().getSourceLocation(), null);
            return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
        }
        if (this.partiallyConcretized != null) {
            return this.partiallyConcretized;
        }
        if (this.extraParameterFlags < 0 && this.testMethod == null) {
            def = bindings.peekEnclosingDefinition();
            if (def != null) {
                ResolvedType aspect = inAspect.getWorld().resolve(def.getDeclaringType());
                Iterator<ResolvedMember> memberIter = aspect.getMethods(true, true);
                while (memberIter.hasNext()) {
                    ResolvedMember method = memberIter.next();
                    if (!def.getName().equals(method.getName()) || def.getParameterTypes().length != method.getParameterTypes().length) continue;
                    boolean sameSig = true;
                    for (int j = 0; j < method.getParameterTypes().length; ++j) {
                        UnresolvedType argJ = method.getParameterTypes()[j];
                        if (argJ.equals(def.getParameterTypes()[j])) continue;
                        sameSig = false;
                        break;
                    }
                    if (!sameSig) continue;
                    this.testMethod = method;
                    break;
                }
                if (this.testMethod == null) {
                    inAspect.getWorld().showMessage(IMessage.ERROR, "Cannot find if() body from '" + def.toString() + "' for '" + this.enclosingPointcutHint + "'", this.getSourceLocation(), null);
                    return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
                }
            } else {
                this.testMethod = inAspect.getWorld().resolve(bindings.getAdviceSignature());
            }
            ret = new IfPointcut(this.enclosingPointcutHint);
            ret.testMethod = this.testMethod;
        } else {
            ret = new IfPointcut(this.testMethod, this.extraParameterFlags);
        }
        ret.copyLocationFrom(this);
        this.partiallyConcretized = ret;
        if (bindings.directlyInAdvice() && bindings.getEnclosingAdvice() == null) {
            inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("ifInPerClause"), this.getSourceLocation(), null);
            return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
        }
        if (bindings.directlyInAdvice()) {
            ShadowMunger advice = bindings.getEnclosingAdvice();
            ret.baseArgsCount = advice instanceof Advice ? ((Advice)advice).getBaseParameterCount() : 0;
            ret.residueSource = advice.getPointcut().concretize(inAspect, inAspect, ret.baseArgsCount, advice);
        } else {
            def = bindings.peekEnclosingDefinition();
            if (def == CflowPointcut.CFLOW_MARKER) {
                inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("ifLexicallyInCflow"), this.getSourceLocation(), null);
                return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
            }
            ret.baseArgsCount = def.getParameterTypes().length;
            if (ret.extraParameterFlags < 0) {
                ret.baseArgsCount = 0;
                for (int i = 0; i < this.testMethod.getParameterTypes().length; ++i) {
                    String argSignature = this.testMethod.getParameterTypes()[i].getSignature();
                    if (AjcMemberMaker.TYPEX_JOINPOINT.getSignature().equals(argSignature) || AjcMemberMaker.TYPEX_PROCEEDINGJOINPOINT.getSignature().equals(argSignature) || AjcMemberMaker.TYPEX_STATICJOINPOINT.getSignature().equals(argSignature) || AjcMemberMaker.TYPEX_ENCLOSINGSTATICJOINPOINT.getSignature().equals(argSignature)) continue;
                    ++ret.baseArgsCount;
                }
            }
            IntMap newBindings = IntMap.idMap(ret.baseArgsCount);
            newBindings.copyContext(bindings);
            ret.residueSource = def.getPointcut().concretize(inAspect, declaringType, newBindings);
        }
        return ret;
    }

    public Pointcut parameterizeWith(Map typeVariableMap, World w) {
        return this;
    }

    public static IfPointcut makeIfFalsePointcut(Pointcut.State state) {
        IfFalsePointcut ret = new IfFalsePointcut();
        ret.state = state;
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public static IfPointcut makeIfTruePointcut(Pointcut.State state) {
        IfTruePointcut ret = new IfTruePointcut();
        ret.state = state;
        return ret;
    }

    public void setAlways(boolean matches) {
        this.extraParameterFlags |= 0x10;
        if (matches) {
            this.extraParameterFlags |= 0x20;
        }
    }

    public static class IfTruePointcut
    extends IfPointcut {
        public IfTruePointcut() {
            super(null, 0);
            this.pointcutKind = (byte)14;
        }

        @Override
        public boolean alwaysTrue() {
            return true;
        }

        @Override
        protected Test findResidueInternal(Shadow shadow, ExposedState state) {
            return Literal.TRUE;
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
        public void postRead(ResolvedType enclosingType) {
        }

        @Override
        public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
            if (this.isDeclare(bindings.getEnclosingAdvice())) {
                inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("ifInDeclare"), bindings.getEnclosingAdvice().getSourceLocation(), null);
                return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
            }
            return IfTruePointcut.makeIfTruePointcut(this.state);
        }

        @Override
        public void write(CompressingDataOutputStream s) throws IOException {
            s.writeByte(14);
        }

        @Override
        public int hashCode() {
            int result = 37;
            return result;
        }

        @Override
        public String toString() {
            return "if(true)";
        }
    }

    public static class IfFalsePointcut
    extends IfPointcut {
        public IfFalsePointcut() {
            super(null, 0);
            this.pointcutKind = (byte)15;
        }

        @Override
        public int couldMatchKinds() {
            return Shadow.NO_SHADOW_KINDS_BITS;
        }

        @Override
        public boolean alwaysFalse() {
            return true;
        }

        @Override
        protected Test findResidueInternal(Shadow shadow, ExposedState state) {
            return Literal.FALSE;
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
            if (this.isDeclare(bindings.getEnclosingAdvice())) {
                inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("ifInDeclare"), bindings.getEnclosingAdvice().getSourceLocation(), null);
                return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
            }
            return IfFalsePointcut.makeIfFalsePointcut(this.state);
        }

        @Override
        public void write(CompressingDataOutputStream s) throws IOException {
            s.writeByte(15);
        }

        @Override
        public int hashCode() {
            int result = 17;
            return result;
        }

        @Override
        public String toString() {
            return "if(false)";
        }
    }
}


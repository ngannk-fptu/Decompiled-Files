/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collections;
import java.util.List;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.IHasSourceLocation;
import org.aspectj.weaver.Lint;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.Utils;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;

public abstract class Advice
extends ShadowMunger {
    protected AjAttribute.AdviceAttribute attribute;
    protected transient AdviceKind kind;
    protected Member signature;
    private boolean isAnnotationStyle;
    protected ResolvedType concreteAspect;
    protected List<ShadowMunger> innerCflowEntries = Collections.emptyList();
    protected int nFreeVars;
    protected TypePattern exceptionType;
    protected UnresolvedType[] bindingParameterTypes;
    protected boolean hasMatchedAtLeastOnce = false;
    protected List<Lint.Kind> suppressedLintKinds = null;
    public ISourceLocation lastReportedMonitorExitJoinpointLocation = null;
    private volatile int hashCode = 0;
    public static final int ExtraArgument = 1;
    public static final int ThisJoinPoint = 2;
    public static final int ThisJoinPointStaticPart = 4;
    public static final int ThisEnclosingJoinPointStaticPart = 8;
    public static final int ParameterMask = 15;
    public static final int ConstantReference = 16;
    public static final int ConstantValue = 32;
    public static final int ThisAspectInstance = 64;

    public static Advice makeCflowEntry(World world, Pointcut entry, boolean isBelow, Member stackField, int nFreeVars, List<ShadowMunger> innerCflowEntries, ResolvedType inAspect) {
        Advice ret = world.createAdviceMunger(isBelow ? AdviceKind.CflowBelowEntry : AdviceKind.CflowEntry, entry, stackField, 0, entry, inAspect);
        ret.innerCflowEntries = innerCflowEntries;
        ret.nFreeVars = nFreeVars;
        ret.setDeclaringType(inAspect);
        return ret;
    }

    public static Advice makePerCflowEntry(World world, Pointcut entry, boolean isBelow, Member stackField, ResolvedType inAspect, List<ShadowMunger> innerCflowEntries) {
        Advice ret = world.createAdviceMunger(isBelow ? AdviceKind.PerCflowBelowEntry : AdviceKind.PerCflowEntry, entry, stackField, 0, entry, inAspect);
        ret.innerCflowEntries = innerCflowEntries;
        ret.concreteAspect = inAspect;
        return ret;
    }

    public static Advice makePerObjectEntry(World world, Pointcut entry, boolean isThis, ResolvedType inAspect) {
        Advice ret = world.createAdviceMunger(isThis ? AdviceKind.PerThisEntry : AdviceKind.PerTargetEntry, entry, null, 0, entry, inAspect);
        ret.concreteAspect = inAspect;
        return ret;
    }

    public static Advice makePerTypeWithinEntry(World world, Pointcut p, ResolvedType inAspect) {
        Advice ret = world.createAdviceMunger(AdviceKind.PerTypeWithinEntry, p, null, 0, p, inAspect);
        ret.concreteAspect = inAspect;
        return ret;
    }

    @Override
    public boolean isAroundAdvice() {
        return this.attribute.getKind() == AdviceKind.Around;
    }

    public static Advice makeSoftener(World world, Pointcut entry, TypePattern exceptionType, ResolvedType inAspect, IHasSourceLocation loc) {
        Advice ret = world.createAdviceMunger(AdviceKind.Softener, entry, null, 0, loc, inAspect);
        ret.exceptionType = exceptionType;
        return ret;
    }

    public Advice(AjAttribute.AdviceAttribute attribute, Pointcut pointcut, Member signature) {
        super(pointcut, attribute.getStart(), attribute.getEnd(), attribute.getSourceContext(), 1);
        this.attribute = attribute;
        this.isAnnotationStyle = signature != null && !signature.getName().startsWith("ajc$");
        this.kind = attribute.getKind();
        this.signature = signature;
        this.bindingParameterTypes = signature != null ? signature.getParameterTypes() : new UnresolvedType[0];
    }

    @Override
    public boolean match(Shadow shadow, World world) {
        if (super.match(shadow, world)) {
            if (shadow.getKind() == Shadow.ExceptionHandler && (this.kind.isAfter() || this.kind == AdviceKind.Around)) {
                world.showMessage(IMessage.WARNING, WeaverMessages.format("onlyBeforeOnHandler"), this.getSourceLocation(), shadow.getSourceLocation());
                return false;
            }
            if ((shadow.getKind() == Shadow.SynchronizationLock || shadow.getKind() == Shadow.SynchronizationUnlock) && this.kind == AdviceKind.Around) {
                world.showMessage(IMessage.WARNING, WeaverMessages.format("noAroundOnSynchronization"), this.getSourceLocation(), shadow.getSourceLocation());
                return false;
            }
            if (this.hasExtraParameter() && this.kind == AdviceKind.AfterReturning) {
                ResolvedType shadowReturnType;
                boolean matches;
                ResolvedType resolvedExtraParameterType = this.getExtraParameterType().resolve(world);
                boolean bl = matches = resolvedExtraParameterType.isConvertableFrom(shadowReturnType = shadow.getReturnType().resolve(world)) && shadow.getKind().hasReturnValue();
                if (matches && resolvedExtraParameterType.isParameterizedType()) {
                    this.maybeIssueUncheckedMatchWarning(resolvedExtraParameterType, shadowReturnType, shadow, world);
                }
                return matches;
            }
            if (this.hasExtraParameter() && this.kind == AdviceKind.AfterThrowing) {
                ResolvedType exceptionType = this.getExtraParameterType().resolve(world);
                if (!exceptionType.isCheckedException() || exceptionType.getName().equals("java.lang.Exception")) {
                    return true;
                }
                UnresolvedType[] shadowThrows = shadow.getSignature().getExceptions(world);
                boolean matches = false;
                for (int i = 0; i < shadowThrows.length && !matches; ++i) {
                    ResolvedType type = shadowThrows[i].resolve(world);
                    if (!exceptionType.isAssignableFrom(type)) continue;
                    matches = true;
                }
                return matches;
            }
            if (this.kind == AdviceKind.PerTargetEntry) {
                return shadow.hasTarget();
            }
            if (this.kind == AdviceKind.PerThisEntry) {
                if (shadow.getEnclosingCodeSignature().getName().equals("<init>") && world.resolve(shadow.getEnclosingType()).isGroovyObject()) {
                    return false;
                }
                return shadow.hasThis();
            }
            if (this.kind == AdviceKind.Around) {
                if (shadow.getKind() == Shadow.PreInitialization) {
                    world.showMessage(IMessage.WARNING, WeaverMessages.format("aroundOnPreInit"), this.getSourceLocation(), shadow.getSourceLocation());
                    return false;
                }
                if (shadow.getKind() == Shadow.Initialization) {
                    world.showMessage(IMessage.WARNING, WeaverMessages.format("aroundOnInit"), this.getSourceLocation(), shadow.getSourceLocation());
                    return false;
                }
                if (shadow.getKind() == Shadow.StaticInitialization && shadow.getEnclosingType().resolve(world).isInterface()) {
                    world.showMessage(IMessage.ERROR, WeaverMessages.format("aroundOnInterfaceStaticInit", shadow.getEnclosingType().getName()), this.getSourceLocation(), shadow.getSourceLocation());
                    return false;
                }
                if (this.getSignature().getReturnType().equals(UnresolvedType.VOID)) {
                    if (!shadow.getReturnType().equals(UnresolvedType.VOID)) {
                        String s = shadow.toString();
                        String s2 = WeaverMessages.format("nonVoidReturn", s);
                        world.showMessage(IMessage.ERROR, s2, this.getSourceLocation(), shadow.getSourceLocation());
                        return false;
                    }
                } else {
                    if (this.getSignature().getReturnType().equals(UnresolvedType.OBJECT)) {
                        return true;
                    }
                    ResolvedType shadowReturnType = shadow.getReturnType().resolve(world);
                    ResolvedType adviceReturnType = this.getSignature().getGenericReturnType().resolve(world);
                    if (shadowReturnType.isParameterizedType() && adviceReturnType.isRawType()) {
                        ReferenceType adviceReturnGenericType;
                        ReferenceType shadowReturnGenericType = shadowReturnType.getGenericType();
                        if (((ResolvedType)shadowReturnGenericType).isAssignableFrom(adviceReturnGenericType = adviceReturnType.getGenericType()) && world.getLint().uncheckedAdviceConversion.isEnabled()) {
                            world.getLint().uncheckedAdviceConversion.signal(new String[]{shadow.toString(), shadowReturnType.getName(), adviceReturnType.getName()}, shadow.getSourceLocation(), new ISourceLocation[]{this.getSourceLocation()});
                        }
                    } else if (!shadowReturnType.isAssignableFrom(adviceReturnType)) {
                        world.showMessage(IMessage.ERROR, WeaverMessages.format("incompatibleReturnType", shadow), this.getSourceLocation(), shadow.getSourceLocation());
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void maybeIssueUncheckedMatchWarning(ResolvedType afterReturningType, ResolvedType shadowReturnType, Shadow shadow, World world) {
        boolean inDoubt;
        boolean bl = inDoubt = !afterReturningType.isAssignableFrom(shadowReturnType);
        if (inDoubt && world.getLint().uncheckedArgument.isEnabled()) {
            String uncheckedMatchWith = afterReturningType.getSimpleBaseName();
            if (shadowReturnType.isParameterizedType() && shadowReturnType.getRawType() == afterReturningType.getRawType()) {
                uncheckedMatchWith = shadowReturnType.getSimpleName();
            }
            if (!Utils.isSuppressing(this.getSignature().getAnnotations(), "uncheckedArgument")) {
                world.getLint().uncheckedArgument.signal(new String[]{afterReturningType.getSimpleName(), uncheckedMatchWith, afterReturningType.getSimpleBaseName(), shadow.toResolvedString(world)}, this.getSourceLocation(), new ISourceLocation[]{shadow.getSourceLocation()});
            }
        }
    }

    public AdviceKind getKind() {
        return this.kind;
    }

    public Member getSignature() {
        return this.signature;
    }

    public boolean hasExtraParameter() {
        return (this.getExtraParameterFlags() & 1) != 0;
    }

    protected int getExtraParameterFlags() {
        return this.attribute.getExtraParameterFlags();
    }

    protected int getExtraParameterCount() {
        return Advice.countOnes(this.getExtraParameterFlags() & 0xF);
    }

    public UnresolvedType[] getBindingParameterTypes() {
        return this.bindingParameterTypes;
    }

    public void setBindingParameterTypes(UnresolvedType[] types) {
        this.bindingParameterTypes = types;
    }

    public static int countOnes(int bits) {
        int ret = 0;
        while (bits != 0) {
            if ((bits & 1) != 0) {
                ++ret;
            }
            bits >>= 1;
        }
        return ret;
    }

    public int getBaseParameterCount() {
        return this.getSignature().getParameterTypes().length - this.getExtraParameterCount();
    }

    public String[] getBaseParameterNames(World world) {
        String[] allNames = this.getSignature().getParameterNames(world);
        int extras = this.getExtraParameterCount();
        if (extras == 0) {
            return allNames;
        }
        String[] result = new String[this.getBaseParameterCount()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = allNames[i];
        }
        return result;
    }

    public UnresolvedType getExtraParameterType() {
        if (!this.hasExtraParameter()) {
            return ResolvedType.MISSING;
        }
        if (this.signature instanceof ResolvedMember) {
            ResolvedMember method = (ResolvedMember)this.signature;
            UnresolvedType[] parameterTypes = method.getGenericParameterTypes();
            if (this.getConcreteAspect().isAnnotationStyleAspect()) {
                String[] pnames = method.getParameterNames();
                if (pnames != null) {
                    int i;
                    AnnotationAJ[] annos = this.getSignature().getAnnotations();
                    String parameterToLookup = null;
                    if (annos != null && (this.getKind() == AdviceKind.AfterThrowing || this.getKind() == AdviceKind.AfterReturning)) {
                        for (i = 0; i < annos.length && parameterToLookup == null; ++i) {
                            AnnotationAJ anno = annos[i];
                            String annosig = anno.getType().getSignature();
                            if (annosig.equals("Lorg/aspectj/lang/annotation/AfterThrowing;")) {
                                parameterToLookup = anno.getStringFormOfValue("throwing");
                                continue;
                            }
                            if (!annosig.equals("Lorg/aspectj/lang/annotation/AfterReturning;")) continue;
                            parameterToLookup = anno.getStringFormOfValue("returning");
                        }
                    }
                    if (parameterToLookup != null) {
                        for (i = 0; i < pnames.length; ++i) {
                            if (!pnames[i].equals(parameterToLookup)) continue;
                            return parameterTypes[i];
                        }
                    }
                }
                int baseParmCnt = this.getBaseParameterCount();
                while (baseParmCnt + 1 < parameterTypes.length && (parameterTypes[baseParmCnt].equals(AjcMemberMaker.TYPEX_JOINPOINT) || parameterTypes[baseParmCnt].equals(AjcMemberMaker.TYPEX_STATICJOINPOINT) || parameterTypes[baseParmCnt].equals(AjcMemberMaker.TYPEX_ENCLOSINGSTATICJOINPOINT))) {
                    ++baseParmCnt;
                }
                return parameterTypes[baseParmCnt];
            }
            return parameterTypes[this.getBaseParameterCount()];
        }
        return this.signature.getParameterTypes()[this.getBaseParameterCount()];
    }

    public UnresolvedType getDeclaringAspect() {
        return this.getOriginalSignature().getDeclaringType();
    }

    protected Member getOriginalSignature() {
        return this.signature;
    }

    protected String extraParametersToString() {
        if (this.getExtraParameterFlags() == 0) {
            return "";
        }
        return "(extraFlags: " + this.getExtraParameterFlags() + ")";
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public ShadowMunger concretize(ResolvedType fromType, World world, PerClause clause) {
        Pointcut p = this.pointcut.concretize(fromType, this.getDeclaringType(), this.signature.getArity(), this);
        if (clause != null) {
            Pointcut oldP = p;
            p = new AndPointcut(clause, p);
            p.copyLocationFrom(oldP);
            p.state = Pointcut.CONCRETE;
            p.m_ignoreUnboundBindingForNames = oldP.m_ignoreUnboundBindingForNames;
        }
        Advice munger = world.getWeavingSupport().createAdviceMunger(this.attribute, p, this.signature, fromType);
        munger.bindingParameterTypes = this.bindingParameterTypes;
        munger.setDeclaringType(this.getDeclaringType());
        return munger;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(").append(this.getKind()).append(this.extraParametersToString());
        sb.append(": ").append(this.pointcut).append("->").append(this.signature).append(")");
        return sb.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Advice)) {
            return false;
        }
        Advice o = (Advice)other;
        return o.kind.equals(this.kind) && (o.pointcut == null ? this.pointcut == null : o.pointcut.equals(this.pointcut)) && (o.signature == null ? this.signature == null : o.signature.equals(this.signature));
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + this.kind.hashCode();
            result = 37 * result + (this.pointcut == null ? 0 : this.pointcut.hashCode());
            this.hashCode = result = 37 * result + (this.signature == null ? 0 : this.signature.hashCode());
        }
        return this.hashCode;
    }

    public void setLexicalPosition(int lexicalPosition) {
        this.start = lexicalPosition;
    }

    public boolean isAnnotationStyle() {
        return this.isAnnotationStyle;
    }

    @Override
    public ResolvedType getConcreteAspect() {
        return this.concreteAspect;
    }

    public boolean hasMatchedSomething() {
        return this.hasMatchedAtLeastOnce;
    }

    public void setHasMatchedSomething(boolean hasMatchedSomething) {
        this.hasMatchedAtLeastOnce = hasMatchedSomething;
    }

    public abstract boolean hasDynamicTests();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.aspectj.apache.bcel.classfile.LocalVariable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.LineNumberTag;
import org.aspectj.apache.bcel.generic.LocalVariableTag;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.IEclipseSourceContext;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Lint;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelRenderer;
import org.aspectj.weaver.bcel.BcelShadow;
import org.aspectj.weaver.bcel.BcelVar;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.IfFinder;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;

class BcelAdvice
extends Advice {
    private Test runtimeTest;
    private ExposedState exposedState;
    private int containsInvokedynamic = 0;
    private Collection<ResolvedType> thrownExceptions = null;

    public BcelAdvice(AjAttribute.AdviceAttribute attribute, Pointcut pointcut, Member adviceSignature, ResolvedType concreteAspect) {
        super(attribute, pointcut, BcelAdvice.simplify(attribute.getKind(), adviceSignature));
        this.concreteAspect = concreteAspect;
    }

    @Override
    public boolean bindsProceedingJoinPoint() {
        UnresolvedType[] parameterTypes = this.signature.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (!parameterTypes[i].equals(UnresolvedType.PROCEEDING_JOINPOINT)) continue;
            return true;
        }
        return false;
    }

    private static Member simplify(AdviceKind kind, Member adviceSignature) {
        if (adviceSignature != null) {
            UnresolvedType adviceDeclaringType = adviceSignature.getDeclaringType();
            if ((kind != AdviceKind.Around || adviceDeclaringType instanceof ResolvedType && ((ResolvedType)adviceDeclaringType).getWorld().isXnoInline()) && adviceSignature instanceof BcelMethod) {
                BcelMethod bm = (BcelMethod)adviceSignature;
                if (bm.getMethod() != null && bm.getMethod().getAnnotations() != null) {
                    return adviceSignature;
                }
                ResolvedMemberImpl simplermember = new ResolvedMemberImpl(bm.getKind(), bm.getDeclaringType(), bm.getModifiers(), bm.getReturnType(), bm.getName(), bm.getParameterTypes());
                simplermember.setParameterNames(bm.getParameterNames());
                return simplermember;
            }
        }
        return adviceSignature;
    }

    @Override
    public ShadowMunger concretize(ResolvedType fromType, World world, PerClause clause) {
        boolean isAround;
        if (!world.areAllLintIgnored()) {
            this.suppressLintWarnings(world);
        }
        ShadowMunger ret = super.concretize(fromType, world, clause);
        if (!world.areAllLintIgnored()) {
            this.clearLintSuppressions(world, this.suppressedLintKinds);
        }
        IfFinder ifinder = new IfFinder();
        ret.getPointcut().accept(ifinder, null);
        boolean hasGuardTest = ifinder.hasIf && this.getKind() != AdviceKind.Around;
        boolean bl = isAround = this.getKind() == AdviceKind.Around;
        if ((this.getExtraParameterFlags() & 2) != 0 && !isAround && !hasGuardTest && world.getLint().noGuardForLazyTjp.isEnabled()) {
            world.getLint().noGuardForLazyTjp.signal("", this.getSourceLocation());
        }
        return ret;
    }

    @Override
    public ShadowMunger parameterizeWith(ResolvedType declaringType, Map<String, UnresolvedType> typeVariableMap) {
        Pointcut pc = this.getPointcut().parameterizeWith(typeVariableMap, declaringType.getWorld());
        BcelAdvice ret = null;
        Member adviceSignature = this.signature;
        if (this.signature instanceof ResolvedMember && this.signature.getDeclaringType().isGenericType()) {
            adviceSignature = ((ResolvedMember)this.signature).parameterizedWith(declaringType.getTypeParameters(), declaringType, declaringType.isParameterizedType());
        }
        ret = new BcelAdvice(this.attribute, pc, adviceSignature, this.concreteAspect);
        return ret;
    }

    @Override
    public boolean match(Shadow shadow, World world) {
        if (world.areAllLintIgnored()) {
            return super.match(shadow, world);
        }
        this.suppressLintWarnings(world);
        boolean ret = super.match(shadow, world);
        this.clearLintSuppressions(world, this.suppressedLintKinds);
        return ret;
    }

    @Override
    public void specializeOn(Shadow shadow) {
        if (this.getKind() == AdviceKind.Around) {
            ((BcelShadow)shadow).initializeForAroundClosure();
        }
        if (this.getKind() == null) {
            this.exposedState = new ExposedState(0);
            return;
        }
        if (this.getKind().isPerEntry()) {
            this.exposedState = new ExposedState(0);
        } else if (this.getKind().isCflow()) {
            this.exposedState = new ExposedState(this.nFreeVars);
        } else if (this.getSignature() != null) {
            this.exposedState = new ExposedState(this.getSignature());
        } else {
            this.exposedState = new ExposedState(0);
            return;
        }
        World world = shadow.getIWorld();
        if (!world.areAllLintIgnored()) {
            this.suppressLintWarnings(world);
        }
        this.exposedState.setConcreteAspect(this.concreteAspect);
        this.runtimeTest = this.getPointcut().findResidue(shadow, this.exposedState);
        if (!world.areAllLintIgnored()) {
            this.clearLintSuppressions(world, this.suppressedLintKinds);
        }
        if (this.getKind() == AdviceKind.PerThisEntry) {
            shadow.getThisVar();
        } else if (this.getKind() == AdviceKind.PerTargetEntry) {
            shadow.getTargetVar();
        }
        if ((this.getExtraParameterFlags() & 4) != 0) {
            ((BcelShadow)shadow).getThisJoinPointStaticPartVar();
            ((BcelShadow)shadow).getEnclosingClass().warnOnAddedStaticInitializer(shadow, this.getSourceLocation());
        }
        if ((this.getExtraParameterFlags() & 2) != 0) {
            boolean hasGuardTest = this.runtimeTest != Literal.TRUE && this.getKind() != AdviceKind.Around;
            boolean isAround = this.getKind() == AdviceKind.Around;
            ((BcelShadow)shadow).requireThisJoinPoint(hasGuardTest, isAround);
            ((BcelShadow)shadow).getEnclosingClass().warnOnAddedStaticInitializer(shadow, this.getSourceLocation());
            if (!hasGuardTest && world.getLint().multipleAdviceStoppingLazyTjp.isEnabled()) {
                ((BcelShadow)shadow).addAdvicePreventingLazyTjp(this);
            }
        }
        if ((this.getExtraParameterFlags() & 8) != 0) {
            ((BcelShadow)shadow).getThisEnclosingJoinPointStaticPartVar();
            ((BcelShadow)shadow).getEnclosingClass().warnOnAddedStaticInitializer(shadow, this.getSourceLocation());
        }
    }

    private boolean canInline(Shadow s) {
        if (this.attribute.isProceedInInners()) {
            return false;
        }
        if (this.concreteAspect == null || this.concreteAspect.isMissing()) {
            return false;
        }
        if (this.concreteAspect.getWorld().isXnoInline()) {
            return false;
        }
        BcelObjectType boType = BcelWorld.getBcelObjectType(this.concreteAspect);
        if (boType == null) {
            return false;
        }
        if (boType.javaClass.getMajor() >= 52 && this.containsInvokedynamic == 0) {
            this.containsInvokedynamic = 1;
            LazyMethodGen lmg = boType.getLazyClassGen().getLazyMethodGen(this.signature.getName(), this.signature.getSignature(), true);
            ResolvedType searchType = this.concreteAspect;
            while (lmg == null && (searchType = searchType.getSuperclass()) != null) {
                ReferenceTypeDelegate rtd = ((ReferenceType)searchType).getDelegate();
                if (!(rtd instanceof BcelObjectType)) continue;
                BcelObjectType bot = (BcelObjectType)rtd;
                if (bot.javaClass.getMajor() < 52) break;
                lmg = bot.getLazyClassGen().getLazyMethodGen(this.signature.getName(), this.signature.getSignature(), true);
            }
            if (lmg != null) {
                InstructionList ilist = lmg.getBody();
                for (InstructionHandle src = ilist.getStart(); src != null; src = src.getNext()) {
                    if (src.getInstruction().opcode != 186) continue;
                    this.containsInvokedynamic = 2;
                    break;
                }
            }
        }
        if (this.containsInvokedynamic == 2) {
            return false;
        }
        return boType.getLazyClassGen().isWoven();
    }

    private boolean aspectIsBroken() {
        ReferenceTypeDelegate rtDelegate;
        return this.concreteAspect instanceof ReferenceType && !((rtDelegate = ((ReferenceType)this.concreteAspect).getDelegate()) instanceof BcelObjectType);
    }

    @Override
    public boolean implementOn(Shadow s) {
        Member sig;
        this.hasMatchedAtLeastOnce = true;
        if (this.aspectIsBroken()) {
            return false;
        }
        BcelShadow shadow = (BcelShadow)s;
        if (!shadow.getWorld().isIgnoringUnusedDeclaredThrownException() && !this.getThrownExceptions().isEmpty()) {
            Member member = shadow.getSignature();
            if (member instanceof BcelMethod) {
                this.removeUnnecessaryProblems((BcelMethod)member, ((BcelMethod)member).getDeclarationLineNumber());
            } else {
                Member enclosingMember;
                ResolvedMember resolvedMember = shadow.getSignature().resolve(shadow.getWorld());
                if (resolvedMember instanceof BcelMethod && shadow.getEnclosingShadow() instanceof BcelShadow && (enclosingMember = shadow.getEnclosingShadow().getSignature()) instanceof BcelMethod) {
                    this.removeUnnecessaryProblems((BcelMethod)enclosingMember, ((BcelMethod)resolvedMember).getDeclarationLineNumber());
                }
            }
        }
        if (shadow.getIWorld().isJoinpointSynchronizationEnabled() && shadow.getKind() == Shadow.MethodExecution && (s.getSignature().getModifiers() & 0x20) != 0) {
            shadow.getIWorld().getLint().advisingSynchronizedMethods.signal(new String[]{shadow.toString()}, shadow.getSourceLocation(), new ISourceLocation[]{this.getSourceLocation()});
        }
        if (this.runtimeTest == Literal.FALSE && (sig = shadow.getSignature()).getArity() == 0 && shadow.getKind() == Shadow.MethodCall && sig.getName().charAt(0) == 'c' && sig.getReturnType().equals(ResolvedType.OBJECT) && sig.getName().equals("clone")) {
            return false;
        }
        if (this.getKind() == AdviceKind.Before) {
            shadow.weaveBefore(this);
        } else if (this.getKind() == AdviceKind.AfterReturning) {
            shadow.weaveAfterReturning(this);
        } else if (this.getKind() == AdviceKind.AfterThrowing) {
            UnresolvedType catchType = this.hasExtraParameter() ? this.getExtraParameterType() : UnresolvedType.THROWABLE;
            shadow.weaveAfterThrowing(this, catchType);
        } else if (this.getKind() == AdviceKind.After) {
            shadow.weaveAfter(this);
        } else if (this.getKind() == AdviceKind.Around) {
            LazyClassGen enclosingClass = shadow.getEnclosingClass();
            if (enclosingClass != null && enclosingClass.isInterface() && shadow.getEnclosingMethod().getName().charAt(0) == '<') {
                shadow.getWorld().getLint().cannotAdviseJoinpointInInterfaceWithAroundAdvice.signal(shadow.toString(), shadow.getSourceLocation());
                return false;
            }
            if (!this.canInline(s)) {
                shadow.weaveAroundClosure(this, this.hasDynamicTests());
            } else {
                shadow.weaveAroundInline(this, this.hasDynamicTests());
            }
        } else if (this.getKind() == AdviceKind.InterInitializer) {
            shadow.weaveAfterReturning(this);
        } else if (this.getKind().isCflow()) {
            shadow.weaveCflowEntry(this, this.getSignature());
        } else if (this.getKind() == AdviceKind.PerThisEntry) {
            shadow.weavePerObjectEntry(this, (BcelVar)shadow.getThisVar());
        } else if (this.getKind() == AdviceKind.PerTargetEntry) {
            shadow.weavePerObjectEntry(this, (BcelVar)shadow.getTargetVar());
        } else if (this.getKind() == AdviceKind.Softener) {
            shadow.weaveSoftener(this, ((ExactTypePattern)this.exceptionType).getType());
        } else if (this.getKind() == AdviceKind.PerTypeWithinEntry) {
            shadow.weavePerTypeWithinAspectInitialization(this, shadow.getEnclosingType());
        } else {
            throw new BCException("unimplemented kind: " + this.getKind());
        }
        return true;
    }

    private void removeUnnecessaryProblems(BcelMethod method, int problemLineNumber) {
        ISourceContext sourceContext = method.getSourceContext();
        if (sourceContext instanceof IEclipseSourceContext) {
            ((IEclipseSourceContext)sourceContext).removeUnnecessaryProblems(method, problemLineNumber);
        }
    }

    private Collection<ResolvedType> collectCheckedExceptions(UnresolvedType[] excs) {
        if (excs == null || excs.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<ResolvedType> ret = new ArrayList<ResolvedType>();
        World world = this.concreteAspect.getWorld();
        ResolvedType runtimeException = world.getCoreType(UnresolvedType.RUNTIME_EXCEPTION);
        ResolvedType error = world.getCoreType(UnresolvedType.ERROR);
        int len = excs.length;
        for (int i = 0; i < len; ++i) {
            ResolvedType t = world.resolve(excs[i], true);
            if (t.isMissing()) {
                world.getLint().cantFindType.signal(WeaverMessages.format("cftExceptionType", excs[i].getName()), this.getSourceLocation());
            }
            if (runtimeException.isAssignableFrom(t) || error.isAssignableFrom(t)) continue;
            ret.add(t);
        }
        return ret;
    }

    @Override
    public Collection<ResolvedType> getThrownExceptions() {
        if (this.thrownExceptions == null) {
            World world;
            ResolvedMember m;
            this.thrownExceptions = this.concreteAspect != null && this.concreteAspect.getWorld() != null && (this.getKind().isAfter() || this.getKind() == AdviceKind.Before || this.getKind() == AdviceKind.Around) ? ((m = (world = this.concreteAspect.getWorld()).resolve(this.signature)) == null ? Collections.emptyList() : this.collectCheckedExceptions(m.getExceptions())) : Collections.emptyList();
        }
        return this.thrownExceptions;
    }

    @Override
    public boolean mustCheckExceptions() {
        if (this.getConcreteAspect() == null) {
            return true;
        }
        return !this.getConcreteAspect().isAnnotationStyleAspect();
    }

    @Override
    public boolean hasDynamicTests() {
        return this.runtimeTest != null && this.runtimeTest != Literal.TRUE;
    }

    InstructionList getAdviceInstructions(BcelShadow s, BcelVar extraArgVar, InstructionHandle ifNoAdvice) {
        UnresolvedType extraParameterType;
        BcelShadow shadow = s;
        InstructionFactory fact = shadow.getFactory();
        BcelWorld world = shadow.getWorld();
        InstructionList il = new InstructionList();
        if (this.hasExtraParameter() && this.getKind() == AdviceKind.AfterReturning && !(extraParameterType = this.getExtraParameterType()).equals(UnresolvedType.OBJECT) && !extraParameterType.isPrimitiveType()) {
            il.append(BcelRenderer.renderTest(fact, world, Test.makeInstanceof(extraArgVar, this.getExtraParameterType().resolve(world)), null, ifNoAdvice, null));
        }
        il.append(this.getAdviceArgSetup(shadow, extraArgVar, null));
        il.append(this.getNonTestAdviceInstructions(shadow));
        InstructionHandle ifYesAdvice = il.getStart();
        il.insert(this.getTestInstructions(shadow, ifYesAdvice, ifNoAdvice, ifYesAdvice));
        if (shadow.getKind() == Shadow.MethodExecution && this.getKind() == AdviceKind.Before) {
            LocalVariableTable lvt;
            int lineNumber = 0;
            lineNumber = shadow.getEnclosingMethod().getMemberView().getLineNumberOfFirstInstruction();
            InstructionHandle start = il.getStart();
            if (lineNumber > 0) {
                start.addTargeter(new LineNumberTag(lineNumber));
            }
            if ((lvt = shadow.getEnclosingMethod().getMemberView().getMethod().getLocalVariableTable()) != null) {
                LocalVariable[] lvTable = lvt.getLocalVariableTable();
                for (int i = 0; i < lvTable.length; ++i) {
                    LocalVariable lv = lvTable[i];
                    if (lv.getStartPC() != 0) continue;
                    start.addTargeter(new LocalVariableTag(lv.getSignature(), lv.getName(), lv.getIndex(), 0));
                }
            }
        }
        return il;
    }

    public InstructionList getAdviceArgSetup(BcelShadow shadow, BcelVar extraVar, InstructionList closureInstantiation) {
        InstructionFactory fact = shadow.getFactory();
        BcelWorld world = shadow.getWorld();
        InstructionList il = new InstructionList();
        if (this.exposedState.getAspectInstance() != null) {
            il.append(BcelRenderer.renderExpr(fact, world, this.exposedState.getAspectInstance()));
        }
        boolean x = this.getDeclaringAspect().resolve(world).isAnnotationStyleAspect();
        boolean isAnnotationStyleAspect = this.getConcreteAspect() != null && this.getConcreteAspect().isAnnotationStyleAspect() && x;
        boolean previousIsClosure = false;
        int len = this.exposedState.size();
        for (int i = 0; i < len; ++i) {
            if (this.exposedState.isErroneousVar(i)) continue;
            BcelVar v = (BcelVar)this.exposedState.get(i);
            if (v == null) {
                if (!isAnnotationStyleAspect) continue;
                if ("Lorg/aspectj/lang/ProceedingJoinPoint;".equals(this.getSignature().getParameterTypes()[i].getSignature())) {
                    if (this.getKind() != AdviceKind.Around) {
                        previousIsClosure = false;
                        this.getConcreteAspect().getWorld().getMessageHandler().handleMessage(new Message("use of ProceedingJoinPoint is allowed only on around advice (arg " + i + " in " + this.toString() + ")", this.getSourceLocation(), true));
                        il.append(InstructionConstants.ACONST_NULL);
                        continue;
                    }
                    if (previousIsClosure) {
                        il.append(InstructionConstants.DUP);
                        continue;
                    }
                    previousIsClosure = true;
                    il.append(closureInstantiation.copy());
                    shadow.closureVarInitialized = true;
                    continue;
                }
                if ("Lorg/aspectj/lang/JoinPoint$StaticPart;".equals(this.getSignature().getParameterTypes()[i].getSignature())) {
                    previousIsClosure = false;
                    if ((this.getExtraParameterFlags() & 4) == 0) continue;
                    shadow.getThisJoinPointStaticPartBcelVar().appendLoad(il, fact);
                    continue;
                }
                if ("Lorg/aspectj/lang/JoinPoint;".equals(this.getSignature().getParameterTypes()[i].getSignature())) {
                    previousIsClosure = false;
                    if ((this.getExtraParameterFlags() & 2) == 0) continue;
                    il.append(shadow.loadThisJoinPoint());
                    continue;
                }
                if ("Lorg/aspectj/lang/JoinPoint$EnclosingStaticPart;".equals(this.getSignature().getParameterTypes()[i].getSignature())) {
                    previousIsClosure = false;
                    if ((this.getExtraParameterFlags() & 8) == 0) continue;
                    shadow.getThisEnclosingJoinPointStaticPartBcelVar().appendLoad(il, fact);
                    continue;
                }
                if (this.hasExtraParameter()) {
                    previousIsClosure = false;
                    extraVar.appendLoadAndConvert(il, fact, this.getExtraParameterType().resolve(world));
                    continue;
                }
                previousIsClosure = false;
                this.getConcreteAspect().getWorld().getMessageHandler().handleMessage(new Message("use of ProceedingJoinPoint is allowed only on around advice (arg " + i + " in " + this.toString() + ")", this.getSourceLocation(), true));
                il.append(InstructionConstants.ACONST_NULL);
                continue;
            }
            UnresolvedType desiredTy = this.getBindingParameterTypes()[i];
            v.appendLoadAndConvert(il, fact, desiredTy.resolve(world));
        }
        if (!isAnnotationStyleAspect) {
            if (this.getKind() == AdviceKind.Around) {
                il.append(closureInstantiation);
            } else if (this.hasExtraParameter()) {
                extraVar.appendLoadAndConvert(il, fact, this.getExtraParameterType().resolve(world));
            }
            if ((this.getExtraParameterFlags() & 4) != 0) {
                shadow.getThisJoinPointStaticPartBcelVar().appendLoad(il, fact);
            }
            if ((this.getExtraParameterFlags() & 2) != 0) {
                il.append(shadow.loadThisJoinPoint());
            }
            if ((this.getExtraParameterFlags() & 8) != 0) {
                shadow.getThisEnclosingJoinPointStaticPartBcelVar().appendLoad(il, fact);
            }
        }
        return il;
    }

    public InstructionList getNonTestAdviceInstructions(BcelShadow shadow) {
        return new InstructionList(Utility.createInvoke(shadow.getFactory(), shadow.getWorld(), this.getOriginalSignature()));
    }

    @Override
    public Member getOriginalSignature() {
        ResolvedMember rsig;
        Member sig = this.getSignature();
        if (sig instanceof ResolvedMember && (rsig = (ResolvedMember)sig).hasBackingGenericMember()) {
            return rsig.getBackingGenericMember();
        }
        return sig;
    }

    public InstructionList getTestInstructions(BcelShadow shadow, InstructionHandle sk, InstructionHandle fk, InstructionHandle next) {
        return BcelRenderer.renderTest(shadow.getFactory(), shadow.getWorld(), this.runtimeTest, sk, fk, next);
    }

    @Override
    public int compareTo(Object other) {
        ResolvedType o_declaringAspect;
        if (!(other instanceof BcelAdvice)) {
            return 0;
        }
        BcelAdvice o = (BcelAdvice)other;
        if (this.kind.getPrecedence() != o.kind.getPrecedence()) {
            if (this.kind.getPrecedence() > o.kind.getPrecedence()) {
                return 1;
            }
            return -1;
        }
        if (this.kind.isCflow()) {
            boolean isBelow;
            boolean bl = isBelow = this.kind == AdviceKind.CflowBelowEntry;
            if (this.innerCflowEntries.contains(o)) {
                return isBelow ? 1 : -1;
            }
            if (o.innerCflowEntries.contains(this)) {
                return isBelow ? -1 : 1;
            }
            return 0;
        }
        if (this.kind.isPerEntry() || this.kind == AdviceKind.Softener) {
            return 0;
        }
        World world = this.concreteAspect.getWorld();
        int ret = this.concreteAspect.getWorld().compareByPrecedence(this.concreteAspect, o.concreteAspect);
        if (ret != 0) {
            return ret;
        }
        ResolvedType declaringAspect = this.getDeclaringAspect().resolve(world);
        if (declaringAspect == (o_declaringAspect = o.getDeclaringAspect().resolve(world))) {
            if (this.kind.isAfter() || o.kind.isAfter()) {
                return this.getStart() < o.getStart() ? -1 : 1;
            }
            return this.getStart() < o.getStart() ? 1 : -1;
        }
        if (declaringAspect.isAssignableFrom(o_declaringAspect)) {
            return -1;
        }
        if (o_declaringAspect.isAssignableFrom(declaringAspect)) {
            return 1;
        }
        return 0;
    }

    public BcelVar[] getExposedStateAsBcelVars(boolean isAround) {
        if (isAround && this.getConcreteAspect() != null && this.getConcreteAspect().isAnnotationStyleAspect()) {
            return BcelVar.NONE;
        }
        if (this.exposedState == null) {
            return BcelVar.NONE;
        }
        int len = this.exposedState.vars.length;
        BcelVar[] ret = new BcelVar[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = (BcelVar)this.exposedState.vars[i];
        }
        return ret;
    }

    protected void suppressLintWarnings(World inWorld) {
        if (this.suppressedLintKinds == null) {
            if (this.signature instanceof BcelMethod) {
                this.suppressedLintKinds = Utility.getSuppressedWarnings(this.signature.getAnnotations(), inWorld.getLint());
            } else {
                this.suppressedLintKinds = Collections.emptyList();
                return;
            }
        }
        inWorld.getLint().suppressKinds(this.suppressedLintKinds);
    }

    protected void clearLintSuppressions(World inWorld, Collection<Lint.Kind> toClear) {
        inWorld.getLint().clearSuppressions(toClear);
    }

    public BcelAdvice(AdviceKind kind, Pointcut pointcut, Member signature, int extraArgumentFlags, int start, int end, ISourceContext sourceContext, ResolvedType concreteAspect) {
        this(new AjAttribute.AdviceAttribute(kind, pointcut, extraArgumentFlags, start, end, sourceContext), pointcut, signature, concreteAspect);
        this.thrownExceptions = Collections.emptyList();
    }
}


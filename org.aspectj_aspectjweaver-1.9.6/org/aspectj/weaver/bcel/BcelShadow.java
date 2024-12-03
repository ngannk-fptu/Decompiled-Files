/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.BranchHandle;
import org.aspectj.apache.bcel.generic.FieldInstruction;
import org.aspectj.apache.bcel.generic.INVOKEINTERFACE;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionLV;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.LineNumberTag;
import org.aspectj.apache.bcel.generic.LocalVariableTag;
import org.aspectj.apache.bcel.generic.MULTIANEWARRAY;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.TargetLostException;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberImpl;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.aspectj.weaver.NewFieldTypeMunger;
import org.aspectj.weaver.NewMethodTypeMunger;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.bcel.AnnotationAccessVar;
import org.aspectj.weaver.bcel.AspectInstanceVar;
import org.aspectj.weaver.bcel.BcelAdvice;
import org.aspectj.weaver.bcel.BcelClassWeaver;
import org.aspectj.weaver.bcel.BcelFieldRef;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelRenderer;
import org.aspectj.weaver.bcel.BcelVar;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.ExceptionRange;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Range;
import org.aspectj.weaver.bcel.ShadowRange;
import org.aspectj.weaver.bcel.TypeAnnotationAccessVar;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.patterns.AbstractPatternNodeVisitor;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;

public class BcelShadow
extends Shadow {
    private static final String[] NoDeclaredExceptions = new String[0];
    private ShadowRange range;
    private final BcelWorld world;
    private final LazyMethodGen enclosingMethod;
    public static boolean appliedLazyTjpOptimization;
    private String actualInstructionTargetType;
    private List<BcelAdvice> badAdvice = null;
    private int sourceline = -1;
    private BcelVar thisVar = null;
    private BcelVar targetVar = null;
    private BcelVar[] argVars = null;
    private Map<ResolvedType, AnnotationAccessVar> kindedAnnotationVars = null;
    private Map<ResolvedType, TypeAnnotationAccessVar> thisAnnotationVars = null;
    private Map<ResolvedType, TypeAnnotationAccessVar> targetAnnotationVars = null;
    private Map<ResolvedType, AnnotationAccessVar> withinAnnotationVars = null;
    private Map<ResolvedType, AnnotationAccessVar> withincodeAnnotationVars = null;
    private boolean allArgVarsInitialized = false;
    boolean closureVarInitialized = false;
    private BcelVar thisJoinPointVar = null;
    private boolean isThisJoinPointLazy;
    private int lazyTjpConsumers = 0;
    private BcelVar thisJoinPointStaticPartVar = null;
    BcelVar aroundClosureInstance = null;

    public BcelShadow(BcelWorld world, Shadow.Kind kind, Member signature, LazyMethodGen enclosingMethod, BcelShadow enclosingShadow) {
        super(kind, signature, enclosingShadow);
        this.world = world;
        this.enclosingMethod = enclosingMethod;
    }

    public BcelShadow copyInto(LazyMethodGen recipient, BcelShadow enclosing) {
        BcelShadow s = new BcelShadow(this.world, this.getKind(), this.getSignature(), recipient, enclosing);
        if (this.mungers.size() > 0) {
            List src = this.mungers;
            if (s.mungers == Collections.EMPTY_LIST) {
                s.mungers = new ArrayList();
            }
            List dest = s.mungers;
            Iterator i = src.iterator();
            while (i.hasNext()) {
                dest.add(i.next());
            }
        }
        return s;
    }

    @Override
    public World getIWorld() {
        return this.world;
    }

    private boolean deleteNewAndDup() {
        InstructionHandle ih;
        ConstantPool cpool = this.getEnclosingClass().getConstantPool();
        int depth = 1;
        for (ih = this.range.getStart(); ih != null; ih = ih.getPrev()) {
            Instruction inst = ih.getInstruction();
            if (inst.opcode == 183 && ((InvokeInstruction)inst).getName(cpool).equals("<init>")) {
                ++depth;
                continue;
            }
            if (inst.opcode == 187 && --depth == 0) break;
        }
        if (ih == null) {
            return false;
        }
        InstructionHandle newHandle = ih;
        InstructionHandle endHandle = newHandle.getNext();
        if (endHandle.getInstruction().opcode == 89) {
            InstructionHandle nextHandle = endHandle.getNext();
            this.retargetFrom(newHandle, nextHandle);
            this.retargetFrom(endHandle, nextHandle);
        } else if (endHandle.getInstruction().opcode == 90) {
            InstructionHandle dupHandle = endHandle;
            endHandle = endHandle.getNext();
            InstructionHandle nextHandle = endHandle.getNext();
            boolean skipEndRepositioning = false;
            if (endHandle.getInstruction().opcode != 95) {
                if (endHandle.getInstruction().opcode == 254) {
                    skipEndRepositioning = true;
                } else {
                    throw new RuntimeException("Unhandled kind of new " + endHandle);
                }
            }
            this.retargetFrom(newHandle, nextHandle);
            this.retargetFrom(dupHandle, nextHandle);
            if (!skipEndRepositioning) {
                this.retargetFrom(endHandle, nextHandle);
            }
        } else {
            endHandle = newHandle;
            InstructionHandle nextHandle = endHandle.getNext();
            this.retargetFrom(newHandle, nextHandle);
            this.getRange().insert(InstructionConstants.POP, Range.OutsideAfter);
        }
        try {
            this.range.getBody().delete(newHandle, endHandle);
        }
        catch (TargetLostException e) {
            throw new BCException("shouldn't happen");
        }
        return true;
    }

    private void retargetFrom(InstructionHandle old, InstructionHandle fresh) {
        for (InstructionTargeter targeter : old.getTargetersCopy()) {
            if (targeter instanceof ExceptionRange) {
                ExceptionRange it = (ExceptionRange)targeter;
                it.updateTarget(old, fresh, it.getBody());
                continue;
            }
            targeter.updateTarget(old, fresh);
        }
    }

    public void addAdvicePreventingLazyTjp(BcelAdvice advice) {
        if (this.badAdvice == null) {
            this.badAdvice = new ArrayList<BcelAdvice>();
        }
        this.badAdvice.add(advice);
    }

    @Override
    protected void prepareForMungers() {
        Object range;
        boolean deletedNewAndDup = true;
        if (this.getKind() == ConstructorCall) {
            if (!this.world.isJoinpointArrayConstructionEnabled() || !this.getSignature().getDeclaringType().isArray()) {
                deletedNewAndDup = this.deleteNewAndDup();
            }
            this.initializeArgVars();
        } else if (this.getKind() == PreInitialization) {
            range = this.getRange();
            ((Range)range).insert(InstructionConstants.NOP, Range.InsideAfter);
        } else if (this.getKind() == ExceptionHandler) {
            range = this.getRange();
            InstructionList body = ((Range)range).getBody();
            InstructionHandle start = ((Range)range).getStart();
            this.argVars = new BcelVar[1];
            UnresolvedType tx = this.getArgType(0);
            this.argVars[0] = this.genTempVar(tx, "ajc$arg0");
            InstructionHandle insertedInstruction = ((Range)range).insert(this.argVars[0].createStore(this.getFactory()), Range.OutsideBefore);
            for (InstructionTargeter t : start.getTargetersCopy()) {
                if (!(t instanceof ExceptionRange)) continue;
                ExceptionRange er = (ExceptionRange)t;
                er.updateTarget(start, insertedInstruction, body);
            }
        }
        this.isThisJoinPointLazy = true;
        this.badAdvice = null;
        for (ShadowMunger munger : this.mungers) {
            munger.specializeOn(this);
        }
        this.initializeThisJoinPoint();
        if (this.thisJoinPointVar != null && !this.isThisJoinPointLazy && this.badAdvice != null && this.badAdvice.size() > 1) {
            int valid = 0;
            for (BcelAdvice element : this.badAdvice) {
                ISourceLocation sLoc = element.getSourceLocation();
                if (sLoc == null || sLoc.getLine() <= 0) continue;
                ++valid;
            }
            if (valid != 0) {
                ISourceLocation[] badLocs = new ISourceLocation[valid];
                int i = 0;
                for (BcelAdvice element : this.badAdvice) {
                    ISourceLocation sLoc = element.getSourceLocation();
                    if (sLoc == null) continue;
                    badLocs[i++] = sLoc;
                }
                this.world.getLint().multipleAdviceStoppingLazyTjp.signal(new String[]{this.toString()}, this.getSourceLocation(), badLocs);
            }
        }
        this.badAdvice = null;
        InstructionFactory fact = this.getFactory();
        if (this.getKind().argsOnStack() && this.argVars != null) {
            if (this.getKind() == ExceptionHandler && this.range.getEnd().getNext().getInstruction().equals(InstructionConstants.POP)) {
                this.range.getEnd().getNext().setInstruction(InstructionConstants.NOP);
            } else {
                this.range.insert(BcelRenderer.renderExprs(fact, this.world, this.argVars), Range.InsideBefore);
                if (this.targetVar != null) {
                    this.range.insert(BcelRenderer.renderExpr(fact, this.world, this.targetVar), Range.InsideBefore);
                }
                if (!(this.getKind() != ConstructorCall || this.world.isJoinpointArrayConstructionEnabled() && this.getSignature().getDeclaringType().isArray() || !deletedNewAndDup)) {
                    this.range.insert(InstructionFactory.createDup(1), Range.InsideBefore);
                    this.range.insert(fact.createNew((ObjectType)BcelWorld.makeBcelType(this.getSignature().getDeclaringType())), Range.InsideBefore);
                }
            }
        }
    }

    public ShadowRange getRange() {
        return this.range;
    }

    public void setRange(ShadowRange range) {
        this.range = range;
    }

    public int getSourceLine() {
        if (this.sourceline != -1) {
            return this.sourceline;
        }
        Shadow.Kind kind = this.getKind();
        if ((kind == MethodExecution || kind == ConstructorExecution || kind == AdviceExecution || kind == StaticInitialization || kind == PreInitialization || kind == Initialization) && this.getEnclosingMethod().hasDeclaredLineNumberInfo()) {
            this.sourceline = this.getEnclosingMethod().getDeclarationLineNumber();
            return this.sourceline;
        }
        if (this.range == null) {
            if (this.getEnclosingMethod().hasBody()) {
                this.sourceline = Utility.getSourceLine(this.getEnclosingMethod().getBody().getStart());
                return this.sourceline;
            }
            this.sourceline = 0;
            return this.sourceline;
        }
        this.sourceline = Utility.getSourceLine(this.range.getStart());
        if (this.sourceline < 0) {
            this.sourceline = 0;
        }
        return this.sourceline;
    }

    @Override
    public ResolvedType getEnclosingType() {
        return this.getEnclosingClass().getType();
    }

    public LazyClassGen getEnclosingClass() {
        return this.enclosingMethod.getEnclosingClass();
    }

    public BcelWorld getWorld() {
        return this.world;
    }

    public static BcelShadow makeConstructorExecution(BcelWorld world, LazyMethodGen enclosingMethod, InstructionHandle justBeforeStart) {
        InstructionList body = enclosingMethod.getBody();
        BcelShadow s = new BcelShadow(world, ConstructorExecution, world.makeJoinPointSignatureFromMethod(enclosingMethod, Member.CONSTRUCTOR), enclosingMethod, null);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, justBeforeStart.getNext()), Range.genEnd(body));
        return s;
    }

    public static BcelShadow makeStaticInitialization(BcelWorld world, LazyMethodGen enclosingMethod) {
        InvokeInstruction ii;
        InstructionList body = enclosingMethod.getBody();
        InstructionHandle clinitStart = body.getStart();
        if (clinitStart.getInstruction() instanceof InvokeInstruction && (ii = (InvokeInstruction)clinitStart.getInstruction()).getName(enclosingMethod.getEnclosingClass().getConstantPool()).equals("ajc$preClinit")) {
            clinitStart = clinitStart.getNext();
        }
        InstructionHandle clinitEnd = body.getEnd();
        BcelShadow s = new BcelShadow(world, StaticInitialization, world.makeJoinPointSignatureFromMethod(enclosingMethod, Member.STATIC_INITIALIZATION), enclosingMethod, null);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, clinitStart), Range.genEnd(body, clinitEnd));
        return s;
    }

    public static BcelShadow makeExceptionHandler(BcelWorld world, ExceptionRange exceptionRange, LazyMethodGen enclosingMethod, InstructionHandle startOfHandler, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        UnresolvedType catchType = exceptionRange.getCatchType();
        ResolvedType inType = enclosingMethod.getEnclosingClass().getType();
        ResolvedMemberImpl sig = MemberImpl.makeExceptionHandlerSignature(inType, catchType);
        sig.setParameterNames(new String[]{BcelShadow.findHandlerParamName(startOfHandler)});
        BcelShadow s = new BcelShadow(world, ExceptionHandler, sig, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        InstructionHandle start = Range.genStart(body, startOfHandler);
        InstructionHandle end = Range.genEnd(body, start);
        r.associateWithTargets(start, end);
        exceptionRange.updateTarget(startOfHandler, start, body);
        return s;
    }

    private static String findHandlerParamName(InstructionHandle startOfHandler) {
        if (startOfHandler.getInstruction().isStoreInstruction() && startOfHandler.getNext() != null) {
            int slot = startOfHandler.getInstruction().getIndex();
            for (InstructionTargeter targeter : startOfHandler.getNext().getTargeters()) {
                LocalVariableTag t;
                if (!(targeter instanceof LocalVariableTag) || (t = (LocalVariableTag)targeter).getSlot() != slot) continue;
                return t.getName();
            }
        }
        return "<missing>";
    }

    public static BcelShadow makeIfaceInitialization(BcelWorld world, LazyMethodGen constructor, Member interfaceConstructorSignature) {
        constructor.getBody();
        BcelShadow s = new BcelShadow(world, Initialization, interfaceConstructorSignature, constructor, null);
        return s;
    }

    public void initIfaceInitializer(InstructionHandle end) {
        InstructionList body = this.enclosingMethod.getBody();
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(this);
        InstructionHandle nop = body.insert(end, InstructionConstants.NOP);
        r.associateWithTargets(Range.genStart(body, nop), Range.genEnd(body, nop));
    }

    public static BcelShadow makeUnfinishedInitialization(BcelWorld world, LazyMethodGen constructor) {
        BcelShadow ret = new BcelShadow(world, Initialization, world.makeJoinPointSignatureFromMethod(constructor, Member.CONSTRUCTOR), constructor, null);
        if (constructor.getEffectiveSignature() != null) {
            ret.setMatchingSignature(constructor.getEffectiveSignature().getEffectiveSignature());
        }
        return ret;
    }

    public static BcelShadow makeUnfinishedPreinitialization(BcelWorld world, LazyMethodGen constructor) {
        BcelShadow ret = new BcelShadow(world, PreInitialization, world.makeJoinPointSignatureFromMethod(constructor, Member.CONSTRUCTOR), constructor, null);
        if (constructor.getEffectiveSignature() != null) {
            ret.setMatchingSignature(constructor.getEffectiveSignature().getEffectiveSignature());
        }
        return ret;
    }

    public static BcelShadow makeMethodExecution(BcelWorld world, LazyMethodGen enclosingMethod, boolean lazyInit) {
        if (!lazyInit) {
            return BcelShadow.makeMethodExecution(world, enclosingMethod);
        }
        BcelShadow s = new BcelShadow(world, MethodExecution, enclosingMethod.getMemberView(), enclosingMethod, null);
        return s;
    }

    public void init() {
        if (this.range != null) {
            return;
        }
        InstructionList body = this.enclosingMethod.getBody();
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(this);
        r.associateWithTargets(Range.genStart(body), Range.genEnd(body));
    }

    public static BcelShadow makeMethodExecution(BcelWorld world, LazyMethodGen enclosingMethod) {
        return BcelShadow.makeShadowForMethod(world, enclosingMethod, MethodExecution, enclosingMethod.getMemberView());
    }

    public static BcelShadow makeShadowForMethod(BcelWorld world, LazyMethodGen enclosingMethod, Shadow.Kind kind, Member sig) {
        InstructionList body = enclosingMethod.getBody();
        BcelShadow s = new BcelShadow(world, kind, sig, enclosingMethod, null);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body), Range.genEnd(body));
        return s;
    }

    public static BcelShadow makeAdviceExecution(BcelWorld world, LazyMethodGen enclosingMethod) {
        InstructionList body = enclosingMethod.getBody();
        BcelShadow s = new BcelShadow(world, AdviceExecution, world.makeJoinPointSignatureFromMethod(enclosingMethod, Member.ADVICE), enclosingMethod, null);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body), Range.genEnd(body));
        return s;
    }

    public static BcelShadow makeConstructorCall(BcelWorld world, LazyMethodGen enclosingMethod, InstructionHandle callHandle, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        Member sig = world.makeJoinPointSignatureForMethodInvocation(enclosingMethod.getEnclosingClass(), (InvokeInstruction)callHandle.getInstruction());
        BcelShadow s = new BcelShadow(world, ConstructorCall, sig, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, callHandle), Range.genEnd(body, callHandle));
        BcelShadow.retargetAllBranches(callHandle, r.getStart());
        return s;
    }

    public static BcelShadow makeArrayConstructorCall(BcelWorld world, LazyMethodGen enclosingMethod, InstructionHandle arrayInstruction, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        Member sig = world.makeJoinPointSignatureForArrayConstruction(enclosingMethod.getEnclosingClass(), arrayInstruction);
        BcelShadow s = new BcelShadow(world, ConstructorCall, sig, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, arrayInstruction), Range.genEnd(body, arrayInstruction));
        BcelShadow.retargetAllBranches(arrayInstruction, r.getStart());
        return s;
    }

    public static BcelShadow makeMonitorEnter(BcelWorld world, LazyMethodGen enclosingMethod, InstructionHandle monitorInstruction, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        Member sig = world.makeJoinPointSignatureForMonitorEnter(enclosingMethod.getEnclosingClass(), monitorInstruction);
        BcelShadow s = new BcelShadow(world, SynchronizationLock, sig, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, monitorInstruction), Range.genEnd(body, monitorInstruction));
        BcelShadow.retargetAllBranches(monitorInstruction, r.getStart());
        return s;
    }

    public static BcelShadow makeMonitorExit(BcelWorld world, LazyMethodGen enclosingMethod, InstructionHandle monitorInstruction, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        Member sig = world.makeJoinPointSignatureForMonitorExit(enclosingMethod.getEnclosingClass(), monitorInstruction);
        BcelShadow s = new BcelShadow(world, SynchronizationUnlock, sig, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, monitorInstruction), Range.genEnd(body, monitorInstruction));
        BcelShadow.retargetAllBranches(monitorInstruction, r.getStart());
        return s;
    }

    public static BcelShadow makeMethodCall(BcelWorld world, LazyMethodGen enclosingMethod, InstructionHandle callHandle, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        BcelShadow s = new BcelShadow(world, MethodCall, world.makeJoinPointSignatureForMethodInvocation(enclosingMethod.getEnclosingClass(), (InvokeInstruction)callHandle.getInstruction()), enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, callHandle), Range.genEnd(body, callHandle));
        BcelShadow.retargetAllBranches(callHandle, r.getStart());
        return s;
    }

    public static BcelShadow makeShadowForMethodCall(BcelWorld world, LazyMethodGen enclosingMethod, InstructionHandle callHandle, BcelShadow enclosingShadow, Shadow.Kind kind, ResolvedMember sig) {
        InstructionList body = enclosingMethod.getBody();
        BcelShadow s = new BcelShadow(world, kind, sig, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, callHandle), Range.genEnd(body, callHandle));
        BcelShadow.retargetAllBranches(callHandle, r.getStart());
        return s;
    }

    public static BcelShadow makeFieldGet(BcelWorld world, ResolvedMember field, LazyMethodGen enclosingMethod, InstructionHandle getHandle, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        BcelShadow s = new BcelShadow(world, FieldGet, field, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, getHandle), Range.genEnd(body, getHandle));
        BcelShadow.retargetAllBranches(getHandle, r.getStart());
        return s;
    }

    public static BcelShadow makeFieldSet(BcelWorld world, ResolvedMember field, LazyMethodGen enclosingMethod, InstructionHandle setHandle, BcelShadow enclosingShadow) {
        InstructionList body = enclosingMethod.getBody();
        BcelShadow s = new BcelShadow(world, FieldSet, field, enclosingMethod, enclosingShadow);
        ShadowRange r = new ShadowRange(body);
        r.associateWithShadow(s);
        r.associateWithTargets(Range.genStart(body, setHandle), Range.genEnd(body, setHandle));
        BcelShadow.retargetAllBranches(setHandle, r.getStart());
        return s;
    }

    public static void retargetAllBranches(InstructionHandle from, InstructionHandle to) {
        for (InstructionTargeter source : from.getTargetersCopy()) {
            if (!(source instanceof InstructionBranch)) continue;
            source.updateTarget(from, to);
        }
    }

    public boolean terminatesWithReturn() {
        return this.getRange().getRealNext() == null;
    }

    public boolean arg0HoldsThis() {
        if (this.getKind().isEnclosingKind()) {
            return !Modifier.isStatic(this.getSignature().getModifiers());
        }
        if (this.enclosingShadow == null) {
            return !this.enclosingMethod.isStatic();
        }
        return ((BcelShadow)this.enclosingShadow).arg0HoldsThis();
    }

    @Override
    public Var getThisVar() {
        if (!this.hasThis()) {
            throw new IllegalStateException("no this");
        }
        this.initializeThisVar();
        return this.thisVar;
    }

    @Override
    public Var getThisAnnotationVar(UnresolvedType forAnnotationType) {
        if (!this.hasThis()) {
            throw new IllegalStateException("no this");
        }
        this.initializeThisAnnotationVars();
        Var v = this.thisAnnotationVars.get(forAnnotationType);
        if (v == null) {
            v = new TypeAnnotationAccessVar(forAnnotationType.resolve(this.world), (BcelVar)this.getThisVar());
        }
        return v;
    }

    @Override
    public Var getTargetVar() {
        if (!this.hasTarget()) {
            throw new IllegalStateException("no target");
        }
        this.initializeTargetVar();
        return this.targetVar;
    }

    @Override
    public Var getTargetAnnotationVar(UnresolvedType forAnnotationType) {
        if (!this.hasTarget()) {
            throw new IllegalStateException("no target");
        }
        this.initializeTargetAnnotationVars();
        Var v = this.targetAnnotationVars.get(forAnnotationType);
        if (v == null) {
            v = new TypeAnnotationAccessVar(forAnnotationType.resolve(this.world), (BcelVar)this.getTargetVar());
        }
        return v;
    }

    @Override
    public Var getArgVar(int i) {
        this.ensureInitializedArgVar(i);
        return this.argVars[i];
    }

    @Override
    public Var getArgAnnotationVar(int i, UnresolvedType forAnnotationType) {
        return new TypeAnnotationAccessVar(forAnnotationType.resolve(this.world), (BcelVar)this.getArgVar(i));
    }

    @Override
    public Var getKindedAnnotationVar(UnresolvedType forAnnotationType) {
        this.initializeKindedAnnotationVars();
        return this.kindedAnnotationVars.get(forAnnotationType);
    }

    @Override
    public Var getWithinAnnotationVar(UnresolvedType forAnnotationType) {
        this.initializeWithinAnnotationVars();
        return this.withinAnnotationVars.get(forAnnotationType);
    }

    @Override
    public Var getWithinCodeAnnotationVar(UnresolvedType forAnnotationType) {
        this.initializeWithinCodeAnnotationVars();
        return this.withincodeAnnotationVars.get(forAnnotationType);
    }

    @Override
    public final Var getThisJoinPointStaticPartVar() {
        return this.getThisJoinPointStaticPartBcelVar();
    }

    @Override
    public final Var getThisEnclosingJoinPointStaticPartVar() {
        return this.getThisEnclosingJoinPointStaticPartBcelVar();
    }

    public void requireThisJoinPoint(boolean hasGuardTest, boolean isAround) {
        if (!isAround) {
            if (!hasGuardTest) {
                this.isThisJoinPointLazy = false;
            } else {
                ++this.lazyTjpConsumers;
            }
        }
        if (this.thisJoinPointVar == null) {
            this.thisJoinPointVar = this.genTempVar(UnresolvedType.forName("org.aspectj.lang.JoinPoint"));
        }
    }

    @Override
    public Var getThisJoinPointVar() {
        this.requireThisJoinPoint(false, false);
        return this.thisJoinPointVar;
    }

    void initializeThisJoinPoint() {
        if (this.thisJoinPointVar == null) {
            return;
        }
        if (this.isThisJoinPointLazy) {
            this.isThisJoinPointLazy = this.checkLazyTjp();
        }
        if (this.isThisJoinPointLazy) {
            appliedLazyTjpOptimization = true;
            this.createThisJoinPoint();
            if (this.lazyTjpConsumers == 1) {
                return;
            }
            InstructionFactory fact = this.getFactory();
            InstructionList il = new InstructionList();
            il.append(InstructionConstants.ACONST_NULL);
            il.append(this.thisJoinPointVar.createStore(fact));
            this.range.insert(il, Range.OutsideBefore);
        } else {
            appliedLazyTjpOptimization = false;
            InstructionFactory fact = this.getFactory();
            InstructionList il = this.createThisJoinPoint();
            il.append(this.thisJoinPointVar.createStore(fact));
            this.range.insert(il, Range.OutsideBefore);
        }
    }

    private boolean checkLazyTjp() {
        for (ShadowMunger munger : this.mungers) {
            if (!(munger instanceof Advice) || ((Advice)munger).getKind() != AdviceKind.Around) continue;
            if (munger.getSourceLocation() != null && this.world.getLint().canNotImplementLazyTjp.isEnabled()) {
                this.world.getLint().canNotImplementLazyTjp.signal(new String[]{this.toString()}, this.getSourceLocation(), new ISourceLocation[]{munger.getSourceLocation()});
            }
            return false;
        }
        return true;
    }

    InstructionList loadThisJoinPoint() {
        InstructionFactory fact = this.getFactory();
        InstructionList il = new InstructionList();
        if (this.isThisJoinPointLazy) {
            il.append(this.createThisJoinPoint());
            if (this.lazyTjpConsumers > 1) {
                il.append(this.thisJoinPointVar.createStore(fact));
                InstructionHandle end = il.append(this.thisJoinPointVar.createLoad(fact));
                il.insert(InstructionFactory.createBranchInstruction((short)199, end));
                il.insert(this.thisJoinPointVar.createLoad(fact));
            }
        } else {
            this.thisJoinPointVar.appendLoad(il, fact);
        }
        return il;
    }

    InstructionList createThisJoinPoint() {
        InstructionFactory fact = this.getFactory();
        InstructionList il = new InstructionList();
        BcelVar staticPart = this.getThisJoinPointStaticPartBcelVar();
        staticPart.appendLoad(il, fact);
        if (this.hasThis()) {
            ((BcelVar)this.getThisVar()).appendLoad(il, fact);
        } else {
            il.append(InstructionConstants.ACONST_NULL);
        }
        if (this.hasTarget()) {
            ((BcelVar)this.getTargetVar()).appendLoad(il, fact);
        } else {
            il.append(InstructionConstants.ACONST_NULL);
        }
        switch (this.getArgCount()) {
            case 0: {
                il.append(fact.createInvoke("org.aspectj.runtime.reflect.Factory", "makeJP", LazyClassGen.tjpType, new Type[]{LazyClassGen.staticTjpType, Type.OBJECT, Type.OBJECT}, (short)184));
                break;
            }
            case 1: {
                ((BcelVar)this.getArgVar(0)).appendLoadAndConvert(il, fact, this.world.getCoreType(ResolvedType.OBJECT));
                il.append(fact.createInvoke("org.aspectj.runtime.reflect.Factory", "makeJP", LazyClassGen.tjpType, new Type[]{LazyClassGen.staticTjpType, Type.OBJECT, Type.OBJECT, Type.OBJECT}, (short)184));
                break;
            }
            case 2: {
                ((BcelVar)this.getArgVar(0)).appendLoadAndConvert(il, fact, this.world.getCoreType(ResolvedType.OBJECT));
                ((BcelVar)this.getArgVar(1)).appendLoadAndConvert(il, fact, this.world.getCoreType(ResolvedType.OBJECT));
                il.append(fact.createInvoke("org.aspectj.runtime.reflect.Factory", "makeJP", LazyClassGen.tjpType, new Type[]{LazyClassGen.staticTjpType, Type.OBJECT, Type.OBJECT, Type.OBJECT, Type.OBJECT}, (short)184));
                break;
            }
            default: {
                il.append(this.makeArgsObjectArray());
                il.append(fact.createInvoke("org.aspectj.runtime.reflect.Factory", "makeJP", LazyClassGen.tjpType, new Type[]{LazyClassGen.staticTjpType, Type.OBJECT, Type.OBJECT, new ArrayType(Type.OBJECT, 1)}, (short)184));
            }
        }
        return il;
    }

    public BcelVar getThisJoinPointStaticPartBcelVar() {
        return this.getThisJoinPointStaticPartBcelVar(false);
    }

    @Override
    public BcelVar getThisAspectInstanceVar(ResolvedType aspectType) {
        return new AspectInstanceVar(aspectType);
    }

    public BcelVar getThisJoinPointStaticPartBcelVar(boolean isEnclosingJp) {
        if (this.thisJoinPointStaticPartVar == null) {
            Field field = this.getEnclosingClass().getTjpField(this, isEnclosingJp);
            ResolvedType sjpType = null;
            sjpType = this.world.isTargettingAspectJRuntime12() ? this.world.getCoreType(UnresolvedType.JOINPOINT_STATICPART) : (isEnclosingJp ? this.world.getCoreType(UnresolvedType.JOINPOINT_ENCLOSINGSTATICPART) : this.world.getCoreType(UnresolvedType.JOINPOINT_STATICPART));
            this.thisJoinPointStaticPartVar = new BcelFieldRef(sjpType, this.getEnclosingClass().getClassName(), field.getName());
        }
        return this.thisJoinPointStaticPartVar;
    }

    public BcelVar getThisEnclosingJoinPointStaticPartBcelVar() {
        if (this.enclosingShadow == null) {
            return this.getThisJoinPointStaticPartBcelVar(true);
        }
        return ((BcelShadow)this.enclosingShadow).getThisJoinPointStaticPartBcelVar(true);
    }

    @Override
    public Member getEnclosingCodeSignature() {
        if (this.getKind().isEnclosingKind()) {
            return this.getSignature();
        }
        if (this.getKind() == Shadow.PreInitialization) {
            return this.getSignature();
        }
        if (this.enclosingShadow == null) {
            return this.getEnclosingMethod().getMemberView();
        }
        return this.enclosingShadow.getSignature();
    }

    public Member getRealEnclosingCodeSignature() {
        return this.enclosingMethod.getMemberView();
    }

    private InstructionList makeArgsObjectArray() {
        InstructionFactory fact = this.getFactory();
        BcelVar arrayVar = this.genTempVar(UnresolvedType.OBJECTARRAY);
        InstructionList il = new InstructionList();
        int alen = this.getArgCount();
        il.append(Utility.createConstant(fact, alen));
        il.append(fact.createNewArray(Type.OBJECT, (short)1));
        arrayVar.appendStore(il, fact);
        int stateIndex = 0;
        int len = this.getArgCount();
        for (int i = 0; i < len; ++i) {
            arrayVar.appendConvertableArrayStore(il, fact, stateIndex, (BcelVar)this.getArgVar(i));
            ++stateIndex;
        }
        arrayVar.appendLoad(il, fact);
        return il;
    }

    private void initializeThisVar() {
        if (this.thisVar != null) {
            return;
        }
        this.thisVar = new BcelVar(this.getThisType().resolve(this.world), 0);
        this.thisVar.setPositionInAroundState(0);
    }

    public void initializeTargetVar() {
        InstructionFactory fact = this.getFactory();
        if (this.targetVar != null) {
            return;
        }
        if (this.getKind().isTargetSameAsThis()) {
            if (this.hasThis()) {
                this.initializeThisVar();
            }
            this.targetVar = this.thisVar;
        } else {
            this.initializeArgVars();
            UnresolvedType type = this.getTargetType();
            type = this.ensureTargetTypeIsCorrect(type);
            this.targetVar = this.genTempVar(type, "ajc$target");
            this.range.insert(this.targetVar.createStore(fact), Range.OutsideBefore);
            this.targetVar.setPositionInAroundState(this.hasThis() ? 1 : 0);
        }
    }

    public UnresolvedType ensureTargetTypeIsCorrect(UnresolvedType tx) {
        Member msig = this.getSignature();
        if (msig.getArity() == 0 && this.getKind() == MethodCall && msig.getName().charAt(0) == 'c' && tx.equals(ResolvedType.OBJECT) && msig.getReturnType().equals(ResolvedType.OBJECT) && msig.getName().equals("clone")) {
            LocalVariableTag lvt;
            InstructionHandle searchPtr = this.range.getStart().getPrev();
            while (Range.isRangeHandle(searchPtr) || searchPtr.getInstruction().isStoreInstruction()) {
                searchPtr = searchPtr.getPrev();
            }
            if (searchPtr.getInstruction().isLoadInstruction() && (lvt = LazyMethodGen.getLocalVariableTag(searchPtr, searchPtr.getInstruction().getIndex())) != null) {
                return UnresolvedType.forSignature(lvt.getType());
            }
            if (searchPtr.getInstruction() instanceof FieldInstruction) {
                FieldInstruction si = (FieldInstruction)searchPtr.getInstruction();
                Type t = si.getFieldType(this.getEnclosingClass().getConstantPool());
                return BcelWorld.fromBcel(t);
            }
            if (searchPtr.getInstruction().opcode == 189) {
                return BcelWorld.fromBcel(new ArrayType(Type.OBJECT, 1));
            }
            if (searchPtr.getInstruction() instanceof MULTIANEWARRAY) {
                MULTIANEWARRAY ana = (MULTIANEWARRAY)searchPtr.getInstruction();
                return BcelWorld.fromBcel(new ArrayType(Type.OBJECT, (int)ana.getDimensions()));
            }
            throw new BCException("Can't determine real target of clone() when processing instruction " + searchPtr.getInstruction() + ".  Perhaps avoid selecting clone with your pointcut?");
        }
        return tx;
    }

    public void ensureInitializedArgVar(int argNumber) {
        if (this.allArgVarsInitialized || this.argVars != null && this.argVars[argNumber] != null) {
            return;
        }
        InstructionFactory fact = this.getFactory();
        int len = this.getArgCount();
        if (this.argVars == null) {
            this.argVars = new BcelVar[len];
        }
        int positionOffset = (this.hasTarget() ? 1 : 0) + (this.hasThis() && !this.getKind().isTargetSameAsThis() ? 1 : 0);
        if (this.getKind().argsOnStack()) {
            for (int i = len - 1; i >= 0; --i) {
                UnresolvedType type = this.getArgType(i);
                BcelVar tmp = this.genTempVar(type, "ajc$arg" + i);
                this.range.insert(tmp.createStore(this.getFactory()), Range.OutsideBefore);
                int position = i;
                tmp.setPositionInAroundState(position += positionOffset);
                this.argVars[i] = tmp;
            }
            this.allArgVarsInitialized = true;
        } else {
            int index = 0;
            if (this.arg0HoldsThis()) {
                ++index;
            }
            boolean allInited = true;
            for (int i = 0; i < len; ++i) {
                UnresolvedType type = this.getArgType(i);
                if (i == argNumber) {
                    this.argVars[argNumber] = this.genTempVar(type, "ajc$arg" + argNumber);
                    this.range.insert(this.argVars[argNumber].createCopyFrom(fact, index), Range.OutsideBefore);
                    this.argVars[argNumber].setPositionInAroundState(argNumber + positionOffset);
                }
                allInited = allInited && this.argVars[i] != null;
                index += type.getSize();
            }
            if (allInited && argNumber + 1 == len) {
                this.allArgVarsInitialized = true;
            }
        }
    }

    public void initializeArgVars() {
        if (this.allArgVarsInitialized) {
            return;
        }
        InstructionFactory fact = this.getFactory();
        int len = this.getArgCount();
        if (this.argVars == null) {
            this.argVars = new BcelVar[len];
        }
        int positionOffset = (this.hasTarget() ? 1 : 0) + (this.hasThis() && !this.getKind().isTargetSameAsThis() ? 1 : 0);
        if (this.getKind().argsOnStack()) {
            for (int i = len - 1; i >= 0; --i) {
                UnresolvedType type = this.getArgType(i);
                BcelVar tmp = this.genTempVar(type, "ajc$arg" + i);
                this.range.insert(tmp.createStore(this.getFactory()), Range.OutsideBefore);
                int position = i;
                tmp.setPositionInAroundState(position += positionOffset);
                this.argVars[i] = tmp;
            }
        } else {
            int index = 0;
            if (this.arg0HoldsThis()) {
                ++index;
            }
            for (int i = 0; i < len; ++i) {
                UnresolvedType type = this.getArgType(i);
                if (this.argVars[i] == null) {
                    BcelVar tmp = this.genTempVar(type, "ajc$arg" + i);
                    this.range.insert(tmp.createCopyFrom(fact, index), Range.OutsideBefore);
                    this.argVars[i] = tmp;
                    tmp.setPositionInAroundState(i + positionOffset);
                }
                index += type.resolve(this.world).getSize();
            }
        }
        this.allArgVarsInitialized = true;
    }

    public void initializeForAroundClosure() {
        this.initializeArgVars();
        if (this.hasTarget()) {
            this.initializeTargetVar();
        }
        if (this.hasThis()) {
            this.initializeThisVar();
        }
    }

    public void initializeThisAnnotationVars() {
        if (this.thisAnnotationVars != null) {
            return;
        }
        this.thisAnnotationVars = new HashMap<ResolvedType, TypeAnnotationAccessVar>();
    }

    public void initializeTargetAnnotationVars() {
        if (this.targetAnnotationVars != null) {
            return;
        }
        if (this.getKind().isTargetSameAsThis()) {
            if (this.hasThis()) {
                this.initializeThisAnnotationVars();
            }
            this.targetAnnotationVars = this.thisAnnotationVars;
        } else {
            this.targetAnnotationVars = new HashMap<ResolvedType, TypeAnnotationAccessVar>();
            ResolvedType[] rtx = this.getTargetType().resolve(this.world).getAnnotationTypes();
            for (int i = 0; i < rtx.length; ++i) {
                ResolvedType typeX = rtx[i];
                this.targetAnnotationVars.put(typeX, new TypeAnnotationAccessVar(typeX, (BcelVar)this.getTargetVar()));
            }
        }
    }

    protected ResolvedMember getRelevantMember(ResolvedMember foundMember, Member relevantMember, ResolvedType relevantType) {
        if (foundMember != null) {
            return foundMember;
        }
        foundMember = this.getSignature().resolve(this.world);
        if (foundMember == null && relevantMember != null) {
            foundMember = relevantType.lookupMemberWithSupersAndITDs(relevantMember);
        }
        List<ConcreteTypeMunger> mungers = relevantType.resolve(this.world).getInterTypeMungers();
        for (ConcreteTypeMunger typeMunger : mungers) {
            ResolvedMember fakerm;
            if (!(typeMunger.getMunger() instanceof NewMethodTypeMunger) && !(typeMunger.getMunger() instanceof NewConstructorTypeMunger) || !(fakerm = typeMunger.getSignature()).getName().equals(this.getSignature().getName()) || !fakerm.getParameterSignature().equals(this.getSignature().getParameterSignature())) continue;
            foundMember = foundMember.getKind() == ResolvedMember.CONSTRUCTOR ? AjcMemberMaker.interConstructor(relevantType, foundMember, typeMunger.getAspectType()) : AjcMemberMaker.interMethod(foundMember, typeMunger.getAspectType(), false);
            return foundMember;
        }
        return foundMember;
    }

    protected ResolvedType[] getAnnotations(ResolvedMember foundMember, Member relevantMember, ResolvedType relevantType) {
        if (foundMember == null) {
            List<ConcreteTypeMunger> mungers = relevantType.resolve(this.world).getInterTypeMungers();
            for (ConcreteTypeMunger munger : mungers) {
                ConcreteTypeMunger typeMunger = munger;
                if (!(typeMunger.getMunger() instanceof NewMethodTypeMunger) && !(typeMunger.getMunger() instanceof NewConstructorTypeMunger)) continue;
                ResolvedMember fakerm = typeMunger.getSignature();
                ResolvedMember ajcMethod = this.getSignature().getKind() == ResolvedMember.CONSTRUCTOR ? AjcMemberMaker.postIntroducedConstructor(typeMunger.getAspectType(), fakerm.getDeclaringType(), fakerm.getParameterTypes()) : AjcMemberMaker.interMethodDispatcher(fakerm, typeMunger.getAspectType());
                ResolvedMember rmm = this.findMethod(typeMunger.getAspectType(), ajcMethod);
                if (!fakerm.getName().equals(this.getSignature().getName()) || !fakerm.getParameterSignature().equals(this.getSignature().getParameterSignature())) continue;
                relevantType = typeMunger.getAspectType();
                foundMember = rmm;
                return foundMember.getAnnotationTypes();
            }
            foundMember = relevantType.lookupMemberWithSupersAndITDs(relevantMember);
            if (foundMember == null) {
                throw new IllegalStateException("Couldn't find member " + relevantMember + " for type " + relevantType);
            }
        }
        return foundMember.getAnnotationTypes();
    }

    public void initializeKindedAnnotationVars() {
        ResolvedMember foundMember;
        if (this.kindedAnnotationVars != null) {
            return;
        }
        this.kindedAnnotationVars = new HashMap<ResolvedType, AnnotationAccessVar>();
        ResolvedType[] annotations = null;
        Member shadowSignature = this.getSignature();
        Member annotationHolder = this.getSignature();
        ResolvedType relevantType = shadowSignature.getDeclaringType().resolve(this.world);
        if (relevantType.isRawType() || relevantType.isParameterizedType()) {
            relevantType = relevantType.getGenericType();
        }
        if (this.getKind() == Shadow.StaticInitialization) {
            annotations = relevantType.resolve(this.world).getAnnotationTypes();
        } else if (this.getKind() == Shadow.MethodCall || this.getKind() == Shadow.ConstructorCall) {
            foundMember = this.findMethod2(relevantType.resolve(this.world).getDeclaredMethods(), this.getSignature());
            annotations = this.getAnnotations(foundMember, shadowSignature, relevantType);
            annotationHolder = this.getRelevantMember(foundMember, shadowSignature, relevantType);
            relevantType = annotationHolder.getDeclaringType().resolve(this.world);
        } else if (this.getKind() == Shadow.FieldSet || this.getKind() == Shadow.FieldGet) {
            annotationHolder = this.findField(relevantType.getDeclaredFields(), this.getSignature());
            if (annotationHolder == null) {
                List<ConcreteTypeMunger> mungers = relevantType.resolve(this.world).getInterTypeMungers();
                for (ConcreteTypeMunger typeMunger : mungers) {
                    if (!(typeMunger.getMunger() instanceof NewFieldTypeMunger)) continue;
                    ResolvedMember fakerm = typeMunger.getSignature();
                    ResolvedMember ajcMethod = AjcMemberMaker.interFieldInitializer(fakerm, typeMunger.getAspectType());
                    ResolvedMember rmm = this.findMethod(typeMunger.getAspectType(), ajcMethod);
                    if (!fakerm.equals(this.getSignature())) continue;
                    relevantType = typeMunger.getAspectType();
                    annotationHolder = rmm;
                }
            }
            annotations = ((ResolvedMember)annotationHolder).getAnnotationTypes();
        } else if (this.getKind() == Shadow.MethodExecution || this.getKind() == Shadow.ConstructorExecution || this.getKind() == Shadow.AdviceExecution) {
            foundMember = this.findMethod2(relevantType.getDeclaredMethods(), this.getSignature());
            annotations = this.getAnnotations(foundMember, shadowSignature, relevantType);
            annotationHolder = this.getRelevantMember(foundMember, annotationHolder, relevantType);
            UnresolvedType ut = annotationHolder.getDeclaringType();
            relevantType = ut.resolve(this.world);
        } else if (this.getKind() == Shadow.ExceptionHandler) {
            relevantType = this.getSignature().getParameterTypes()[0].resolve(this.world);
            annotations = relevantType.getAnnotationTypes();
        } else if (this.getKind() == Shadow.PreInitialization || this.getKind() == Shadow.Initialization) {
            ResolvedMember found = this.findMethod2(relevantType.getDeclaredMethods(), this.getSignature());
            annotations = found.getAnnotationTypes();
        }
        if (annotations == null) {
            throw new BCException("Could not discover annotations for shadow: " + this.getKind());
        }
        for (ResolvedType annotationType : annotations) {
            AnnotationAccessVar accessVar = new AnnotationAccessVar(this, this.getKind(), annotationType.resolve(this.world), relevantType, annotationHolder, false);
            this.kindedAnnotationVars.put(annotationType, accessVar);
        }
    }

    private ResolvedMember findMethod2(ResolvedMember[] members, Member sig) {
        String signatureName = sig.getName();
        String parameterSignature = sig.getParameterSignature();
        for (ResolvedMember member : members) {
            if (!member.getName().equals(signatureName) || !member.getParameterSignature().equals(parameterSignature)) continue;
            return member;
        }
        return null;
    }

    private ResolvedMember findMethod(ResolvedType aspectType, ResolvedMember ajcMethod) {
        ResolvedMember[] decMethods = aspectType.getDeclaredMethods();
        for (int i = 0; i < decMethods.length; ++i) {
            ResolvedMember member = decMethods[i];
            if (!member.equals(ajcMethod)) continue;
            return member;
        }
        return null;
    }

    private ResolvedMember findField(ResolvedMember[] members, Member lookingFor) {
        for (int i = 0; i < members.length; ++i) {
            ResolvedMember member = members[i];
            if (!member.getName().equals(this.getSignature().getName()) || !member.getType().equals(this.getSignature().getType())) continue;
            return member;
        }
        return null;
    }

    public void initializeWithinAnnotationVars() {
        if (this.withinAnnotationVars != null) {
            return;
        }
        this.withinAnnotationVars = new HashMap<ResolvedType, AnnotationAccessVar>();
        ResolvedType[] annotations = this.getEnclosingType().resolve(this.world).getAnnotationTypes();
        for (int i = 0; i < annotations.length; ++i) {
            ResolvedType ann = annotations[i];
            Shadow.Kind k = Shadow.StaticInitialization;
            this.withinAnnotationVars.put(ann, new AnnotationAccessVar(this, k, ann, this.getEnclosingType(), null, true));
        }
    }

    public void initializeWithinCodeAnnotationVars() {
        if (this.withincodeAnnotationVars != null) {
            return;
        }
        this.withincodeAnnotationVars = new HashMap<ResolvedType, AnnotationAccessVar>();
        ResolvedType[] annotations = this.getEnclosingMethod().getMemberView().getAnnotationTypes();
        for (int i = 0; i < annotations.length; ++i) {
            ResolvedType ann = annotations[i];
            Shadow.Kind k = this.getEnclosingMethod().getMemberView().getKind() == Member.CONSTRUCTOR ? Shadow.ConstructorExecution : Shadow.MethodExecution;
            this.withincodeAnnotationVars.put(ann, new AnnotationAccessVar(this, k, ann, this.getEnclosingType(), this.getEnclosingCodeSignature(), true));
        }
    }

    void weaveBefore(BcelAdvice munger) {
        this.range.insert(munger.getAdviceInstructions(this, null, this.range.getRealStart()), Range.InsideBefore);
    }

    public void weaveAfter(BcelAdvice munger) {
        this.weaveAfterThrowing(munger, UnresolvedType.THROWABLE);
        this.weaveAfterReturning(munger);
    }

    public void weaveAfterReturning(BcelAdvice munger) {
        List<InstructionHandle> returns = this.findReturnInstructions();
        boolean hasReturnInstructions = !returns.isEmpty();
        InstructionList retList = new InstructionList();
        BcelVar returnValueVar = null;
        if (hasReturnInstructions) {
            returnValueVar = this.generateReturnInstructions(returns, retList);
        } else {
            retList.append(InstructionConstants.NOP);
        }
        InstructionList advice = this.getAfterReturningAdviceDispatchInstructions(munger, retList.getStart());
        if (hasReturnInstructions) {
            InstructionHandle gotoTarget = advice.getStart();
            for (InstructionHandle ih : returns) {
                this.retargetReturnInstruction(munger.hasExtraParameter(), returnValueVar, gotoTarget, ih);
            }
        }
        this.range.append(advice);
        this.range.append(retList);
    }

    private List<InstructionHandle> findReturnInstructions() {
        ArrayList<InstructionHandle> returns = new ArrayList<InstructionHandle>();
        for (InstructionHandle ih = this.range.getStart(); ih != this.range.getEnd(); ih = ih.getNext()) {
            if (!ih.getInstruction().isReturnInstruction()) continue;
            returns.add(ih);
        }
        return returns;
    }

    private BcelVar generateReturnInstructions(List<InstructionHandle> returns, InstructionList returnInstructions) {
        BcelVar returnValueVar = null;
        if (this.hasANonVoidReturnType()) {
            Instruction newReturnInstruction = null;
            for (int i = returns.size() - 1; newReturnInstruction == null && i >= 0; --i) {
                InstructionHandle ih = returns.get(i);
                if (ih.getInstruction().opcode == 177) continue;
                newReturnInstruction = Utility.copyInstruction(ih.getInstruction());
            }
            returnValueVar = this.genTempVar(this.getReturnType());
            returnValueVar.appendLoad(returnInstructions, this.getFactory());
            returnInstructions.append(newReturnInstruction);
        } else {
            InstructionHandle lastReturnHandle = returns.get(returns.size() - 1);
            Instruction newReturnInstruction = Utility.copyInstruction(lastReturnHandle.getInstruction());
            returnInstructions.append(newReturnInstruction);
        }
        return returnValueVar;
    }

    private boolean hasANonVoidReturnType() {
        return !this.getReturnType().equals(UnresolvedType.VOID);
    }

    private InstructionList getAfterReturningAdviceDispatchInstructions(BcelAdvice munger, InstructionHandle firstInstructionInReturnSequence) {
        InstructionList advice = new InstructionList();
        BcelVar tempVar = null;
        if (munger.hasExtraParameter()) {
            tempVar = this.insertAdviceInstructionsForBindingReturningParameter(advice);
        }
        advice.append(munger.getAdviceInstructions(this, tempVar, firstInstructionInReturnSequence));
        return advice;
    }

    private BcelVar insertAdviceInstructionsForBindingReturningParameter(InstructionList advice) {
        BcelVar tempVar;
        UnresolvedType tempVarType = this.getReturnType();
        if (tempVarType.equals(UnresolvedType.VOID)) {
            tempVar = this.genTempVar(UnresolvedType.OBJECT);
            advice.append(InstructionConstants.ACONST_NULL);
            tempVar.appendStore(advice, this.getFactory());
        } else {
            tempVar = this.genTempVar(tempVarType);
            advice.append(InstructionFactory.createDup(tempVarType.getSize()));
            tempVar.appendStore(advice, this.getFactory());
        }
        return tempVar;
    }

    private void retargetReturnInstruction(boolean hasReturningParameter, BcelVar returnValueVar, InstructionHandle gotoTarget, InstructionHandle returnHandle) {
        InstructionList newInstructions = new InstructionList();
        if (returnValueVar != null) {
            if (hasReturningParameter) {
                newInstructions.append(InstructionFactory.createDup(this.getReturnType().getSize()));
            }
            returnValueVar.appendStore(newInstructions, this.getFactory());
        }
        if (!this.isLastInstructionInRange(returnHandle, this.range)) {
            newInstructions.append(InstructionFactory.createBranchInstruction((short)167, gotoTarget));
        }
        if (newInstructions.isEmpty()) {
            newInstructions.append(InstructionConstants.NOP);
        }
        Utility.replaceInstruction(returnHandle, newInstructions, this.enclosingMethod);
    }

    private boolean isLastInstructionInRange(InstructionHandle ih, ShadowRange aRange) {
        return ih.getNext() == aRange.getEnd();
    }

    public void weaveAfterThrowing(BcelAdvice munger, UnresolvedType catchType) {
        if (this.getRange().getStart().getNext() == this.getRange().getEnd()) {
            return;
        }
        InstructionFactory fact = this.getFactory();
        InstructionList handler = new InstructionList();
        BcelVar exceptionVar = this.genTempVar(catchType);
        exceptionVar.appendStore(handler, fact);
        if (this.getEnclosingMethod().getName().equals("<clinit>")) {
            ResolvedType eiieType = this.world.resolve("java.lang.ExceptionInInitializerError");
            ObjectType eiieBcelType = (ObjectType)BcelWorld.makeBcelType(eiieType);
            InstructionList ih = new InstructionList(InstructionConstants.NOP);
            handler.append(exceptionVar.createLoad(fact));
            handler.append(fact.createInstanceOf(eiieBcelType));
            InstructionBranch bi = InstructionFactory.createBranchInstruction((short)153, ih.getStart());
            handler.append(bi);
            handler.append(exceptionVar.createLoad(fact));
            handler.append(fact.createCheckCast(eiieBcelType));
            handler.append(InstructionConstants.ATHROW);
            handler.append(ih);
        }
        InstructionList endHandler = new InstructionList(exceptionVar.createLoad(fact));
        handler.append(munger.getAdviceInstructions(this, exceptionVar, endHandler.getStart()));
        handler.append(endHandler);
        handler.append(InstructionConstants.ATHROW);
        InstructionHandle handlerStart = handler.getStart();
        if (this.isFallsThrough()) {
            InstructionHandle jumpTarget = handler.append(InstructionConstants.NOP);
            handler.insert(InstructionFactory.createBranchInstruction((short)167, jumpTarget));
        }
        InstructionHandle protectedEnd = handler.getStart();
        this.range.insert(handler, Range.InsideAfter);
        this.enclosingMethod.addExceptionHandler(this.range.getStart().getNext(), protectedEnd.getPrev(), handlerStart, (ObjectType)BcelWorld.makeBcelType(catchType), this.getKind().hasHighPriorityExceptions());
    }

    public void weaveSoftener(BcelAdvice munger, UnresolvedType catchType) {
        if (this.getRange().getStart().getNext() == this.getRange().getEnd()) {
            return;
        }
        InstructionFactory fact = this.getFactory();
        InstructionList handler = new InstructionList();
        InstructionList rtExHandler = new InstructionList();
        BcelVar exceptionVar = this.genTempVar(catchType);
        handler.append(fact.createNew("org.aspectj.lang.SoftException"));
        handler.append(InstructionFactory.createDup(1));
        handler.append(exceptionVar.createLoad(fact));
        handler.append(fact.createInvoke("org.aspectj.lang.SoftException", "<init>", Type.VOID, new Type[]{Type.THROWABLE}, (short)183));
        handler.append(InstructionConstants.ATHROW);
        exceptionVar.appendStore(rtExHandler, fact);
        rtExHandler.append(exceptionVar.createLoad(fact));
        rtExHandler.append(fact.createInstanceOf(new ObjectType("java.lang.RuntimeException")));
        rtExHandler.append(InstructionFactory.createBranchInstruction((short)153, handler.getStart()));
        rtExHandler.append(exceptionVar.createLoad(fact));
        rtExHandler.append(InstructionFactory.ATHROW);
        InstructionHandle handlerStart = rtExHandler.getStart();
        if (this.isFallsThrough()) {
            InstructionHandle jumpTarget = this.range.getEnd();
            rtExHandler.insert(InstructionFactory.createBranchInstruction((short)167, jumpTarget));
        }
        rtExHandler.append(handler);
        InstructionHandle protectedEnd = rtExHandler.getStart();
        this.range.insert(rtExHandler, Range.InsideAfter);
        this.enclosingMethod.addExceptionHandler(this.range.getStart().getNext(), protectedEnd.getPrev(), handlerStart, (ObjectType)BcelWorld.makeBcelType(catchType), this.getKind().hasHighPriorityExceptions());
    }

    public void weavePerObjectEntry(BcelAdvice munger, BcelVar onVar) {
        InstructionFactory fact = this.getFactory();
        InstructionList entryInstructions = new InstructionList();
        InstructionList entrySuccessInstructions = new InstructionList();
        onVar.appendLoad(entrySuccessInstructions, fact);
        entrySuccessInstructions.append(Utility.createInvoke(fact, this.world, (Member)AjcMemberMaker.perObjectBind(munger.getConcreteAspect())));
        InstructionList testInstructions = munger.getTestInstructions(this, entrySuccessInstructions.getStart(), this.range.getRealStart(), entrySuccessInstructions.getStart());
        entryInstructions.append(testInstructions);
        entryInstructions.append(entrySuccessInstructions);
        this.range.insert(entryInstructions, Range.InsideBefore);
    }

    public void weavePerTypeWithinAspectInitialization(BcelAdvice munger, UnresolvedType t) {
        ResolvedType tResolved = t.resolve(this.world);
        if (tResolved.isInterface()) {
            return;
        }
        ResolvedType aspectRT = munger.getConcreteAspect();
        BcelWorld.getBcelObjectType(aspectRT);
        if (!tResolved.canBeSeenBy(aspectRT) && !aspectRT.isPrivilegedAspect()) {
            return;
        }
        InstructionFactory fact = this.getFactory();
        InstructionList entryInstructions = new InstructionList();
        InstructionList entrySuccessInstructions = new InstructionList();
        String aspectname = munger.getConcreteAspect().getName();
        String ptwField = NameMangler.perTypeWithinFieldForTarget(munger.getConcreteAspect());
        entrySuccessInstructions.append(InstructionFactory.PUSH(fact.getConstantPool(), t.getName()));
        entrySuccessInstructions.append(fact.createInvoke(aspectname, "ajc$createAspectInstance", new ObjectType(aspectname), new Type[]{new ObjectType("java.lang.String")}, (short)184));
        entrySuccessInstructions.append(fact.createPutStatic(t.getName(), ptwField, new ObjectType(aspectname)));
        entryInstructions.append(entrySuccessInstructions);
        this.range.insert(entryInstructions, Range.InsideBefore);
    }

    public void weaveCflowEntry(final BcelAdvice munger, final Member cflowField) {
        boolean isPer;
        boolean bl = isPer = munger.getKind() == AdviceKind.PerCflowBelowEntry || munger.getKind() == AdviceKind.PerCflowEntry;
        if (!isPer && this.getKind() == PreInitialization) {
            return;
        }
        ArrayType objectArrayType = new ArrayType(Type.OBJECT, 1);
        final InstructionFactory fact = this.getFactory();
        final BcelVar testResult = this.genTempVar(UnresolvedType.BOOLEAN);
        InstructionList entryInstructions = new InstructionList();
        InstructionList entrySuccessInstructions = new InstructionList();
        if (munger.hasDynamicTests()) {
            entryInstructions.append(Utility.createConstant(fact, 0));
            testResult.appendStore(entryInstructions, fact);
            entrySuccessInstructions.append(Utility.createConstant(fact, 1));
            testResult.appendStore(entrySuccessInstructions, fact);
        }
        if (isPer) {
            entrySuccessInstructions.append(fact.createInvoke(munger.getConcreteAspect().getName(), "ajc$perCflowPush", Type.VOID, new Type[0], (short)184));
        } else {
            BcelVar[] cflowStateVars = munger.getExposedStateAsBcelVars(false);
            if (cflowStateVars.length == 0) {
                if (!cflowField.getType().getName().endsWith("CFlowCounter")) {
                    throw new RuntimeException("Incorrectly attempting counter operation on stacked cflow");
                }
                entrySuccessInstructions.append(Utility.createGet(fact, cflowField));
                entrySuccessInstructions.append(fact.createInvoke("org.aspectj.runtime.internal.CFlowCounter", "inc", Type.VOID, new Type[0], (short)182));
            } else {
                BcelVar arrayVar = this.genTempVar(UnresolvedType.OBJECTARRAY);
                int alen = cflowStateVars.length;
                entrySuccessInstructions.append(Utility.createConstant(fact, alen));
                entrySuccessInstructions.append(fact.createNewArray(Type.OBJECT, (short)1));
                arrayVar.appendStore(entrySuccessInstructions, fact);
                for (int i = 0; i < alen; ++i) {
                    arrayVar.appendConvertableArrayStore(entrySuccessInstructions, fact, i, cflowStateVars[i]);
                }
                entrySuccessInstructions.append(Utility.createGet(fact, cflowField));
                arrayVar.appendLoad(entrySuccessInstructions, fact);
                entrySuccessInstructions.append(fact.createInvoke("org.aspectj.runtime.internal.CFlowStack", "push", Type.VOID, new Type[]{objectArrayType}, (short)182));
            }
        }
        InstructionList testInstructions = munger.getTestInstructions(this, entrySuccessInstructions.getStart(), this.range.getRealStart(), entrySuccessInstructions.getStart());
        entryInstructions.append(testInstructions);
        entryInstructions.append(entrySuccessInstructions);
        BcelAdvice exitAdvice = new BcelAdvice(null, null, null, 0, 0, 0, null, munger.getConcreteAspect()){

            @Override
            public InstructionList getAdviceInstructions(BcelShadow s, BcelVar extraArgVar, InstructionHandle ifNoAdvice) {
                InstructionList exitInstructions = new InstructionList();
                if (munger.hasDynamicTests()) {
                    testResult.appendLoad(exitInstructions, fact);
                    exitInstructions.append(InstructionFactory.createBranchInstruction((short)153, ifNoAdvice));
                }
                exitInstructions.append(Utility.createGet(fact, cflowField));
                if (munger.getKind() != AdviceKind.PerCflowEntry && munger.getKind() != AdviceKind.PerCflowBelowEntry && munger.getExposedStateAsBcelVars(false).length == 0) {
                    exitInstructions.append(fact.createInvoke("org.aspectj.runtime.internal.CFlowCounter", "dec", Type.VOID, new Type[0], (short)182));
                } else {
                    exitInstructions.append(fact.createInvoke("org.aspectj.runtime.internal.CFlowStack", "pop", Type.VOID, new Type[0], (short)182));
                }
                return exitInstructions;
            }
        };
        this.weaveAfter(exitAdvice);
        this.range.insert(entryInstructions, Range.InsideBefore);
    }

    public void weaveAroundInline(BcelAdvice munger, boolean hasDynamicTest) {
        InstructionHandle end;
        ResolvedType rt;
        BcelObjectType ot;
        LazyMethodGen adviceMethod;
        ResolvedType declaringAspectType;
        ResolvedMember rm;
        Member mungerSig = munger.getSignature();
        if (mungerSig instanceof ResolvedMember && (rm = (ResolvedMember)mungerSig).hasBackingGenericMember()) {
            mungerSig = rm.getBackingGenericMember();
        }
        if ((declaringAspectType = this.world.resolve(mungerSig.getDeclaringType(), true)).isMissing()) {
            this.world.getLint().cantFindType.signal(new String[]{WeaverMessages.format("cftDuringAroundWeave", declaringAspectType.getClassName())}, this.getSourceLocation(), new ISourceLocation[]{munger.getSourceLocation()});
        }
        if (!(adviceMethod = (ot = BcelWorld.getBcelObjectType(rt = declaringAspectType.isParameterizedType() ? declaringAspectType.getGenericType() : declaringAspectType)).getLazyClassGen().getLazyMethodGen(mungerSig)).getCanInline()) {
            this.weaveAroundClosure(munger, hasDynamicTest);
            return;
        }
        if (this.isAnnotationStylePassingProceedingJoinPointOutOfAdvice(munger, hasDynamicTest, adviceMethod)) {
            return;
        }
        this.enclosingMethod.setCanInline(false);
        LazyClassGen shadowClass = this.getEnclosingClass();
        String extractedShadowMethodName = NameMangler.aroundShadowMethodName(this.getSignature(), shadowClass.getNewGeneratedNameTag());
        ArrayList<String> parameterNames = new ArrayList<String>();
        boolean shadowClassIsInterface = shadowClass.isInterface();
        LazyMethodGen extractedShadowMethod = this.extractShadowInstructionsIntoNewMethod(extractedShadowMethodName, shadowClassIsInterface ? 1 : 2, munger.getSourceLocation(), parameterNames, shadowClassIsInterface);
        ArrayList<BcelVar> argsToCallLocalAdviceMethodWith = new ArrayList<BcelVar>();
        ArrayList<BcelVar> proceedVarList = new ArrayList<BcelVar>();
        int extraParamOffset = 0;
        if (this.thisVar != null) {
            argsToCallLocalAdviceMethodWith.add(this.thisVar);
            proceedVarList.add(new BcelVar(this.thisVar.getType(), extraParamOffset));
            extraParamOffset += this.thisVar.getType().getSize();
        }
        if (this.targetVar != null && this.targetVar != this.thisVar) {
            argsToCallLocalAdviceMethodWith.add(this.targetVar);
            proceedVarList.add(new BcelVar(this.targetVar.getType(), extraParamOffset));
            extraParamOffset += this.targetVar.getType().getSize();
        }
        int len = this.getArgCount();
        for (int i = 0; i < len; ++i) {
            argsToCallLocalAdviceMethodWith.add(this.argVars[i]);
            proceedVarList.add(new BcelVar(this.argVars[i].getType(), extraParamOffset));
            extraParamOffset += this.argVars[i].getType().getSize();
        }
        if (this.thisJoinPointVar != null) {
            argsToCallLocalAdviceMethodWith.add(this.thisJoinPointVar);
            proceedVarList.add(new BcelVar(this.thisJoinPointVar.getType(), extraParamOffset));
            extraParamOffset += this.thisJoinPointVar.getType().getSize();
        }
        Type[] adviceParameterTypes = BcelWorld.makeBcelTypes(munger.getSignature().getParameterTypes());
        adviceMethod.getArgumentTypes();
        Type[] extractedMethodParameterTypes = extractedShadowMethod.getArgumentTypes();
        Type[] parameterTypes = new Type[extractedMethodParameterTypes.length + adviceParameterTypes.length + 1];
        int parameterIndex = 0;
        System.arraycopy(extractedMethodParameterTypes, 0, parameterTypes, parameterIndex, extractedMethodParameterTypes.length);
        parameterIndex += extractedMethodParameterTypes.length;
        parameterTypes[parameterIndex++] = BcelWorld.makeBcelType(adviceMethod.getEnclosingClass().getType());
        System.arraycopy(adviceParameterTypes, 0, parameterTypes, parameterIndex, adviceParameterTypes.length);
        String localAdviceMethodName = NameMangler.aroundAdviceMethodName(this.getSignature(), shadowClass.getNewGeneratedNameTag());
        int localAdviceMethodModifiers = 2 | (this.world.useFinal() & !shadowClassIsInterface ? 16 : 0) | 8;
        LazyMethodGen localAdviceMethod = new LazyMethodGen(localAdviceMethodModifiers, BcelWorld.makeBcelType(mungerSig.getReturnType()), localAdviceMethodName, parameterTypes, NoDeclaredExceptions, shadowClass);
        shadowClass.addMethodGen(localAdviceMethod);
        int nVars = adviceMethod.getMaxLocals() + extraParamOffset;
        IntMap varMap = IntMap.idMap(nVars);
        for (int i = extraParamOffset; i < nVars; ++i) {
            varMap.put(i - extraParamOffset, i);
        }
        InstructionFactory fact = this.getFactory();
        localAdviceMethod.getBody().insert(BcelClassWeaver.genInlineInstructions(adviceMethod, localAdviceMethod, varMap, fact, true));
        localAdviceMethod.setMaxLocals(nVars);
        InstructionList advice = new InstructionList();
        for (BcelVar var : argsToCallLocalAdviceMethodWith) {
            var.appendLoad(advice, fact);
        }
        boolean isAnnoStyleConcreteAspect = munger.getConcreteAspect().isAnnotationStyleAspect();
        boolean isAnnoStyleDeclaringAspect = munger.getDeclaringAspect() != null ? munger.getDeclaringAspect().resolve(this.world).isAnnotationStyleAspect() : false;
        InstructionList iList = null;
        if (isAnnoStyleConcreteAspect && isAnnoStyleDeclaringAspect) {
            iList = this.loadThisJoinPoint();
            iList.append(Utility.createConversion(this.getFactory(), LazyClassGen.tjpType, LazyClassGen.proceedingTjpType));
        } else {
            iList = new InstructionList(InstructionConstants.ACONST_NULL);
        }
        advice.append(munger.getAdviceArgSetup(this, null, iList));
        advice.append(Utility.createInvoke(fact, localAdviceMethod));
        advice.append(Utility.createConversion(this.getFactory(), BcelWorld.makeBcelType(mungerSig.getReturnType()), extractedShadowMethod.getReturnType(), this.world.isInJava5Mode()));
        if (!this.isFallsThrough()) {
            advice.append(InstructionFactory.createReturn(extractedShadowMethod.getReturnType()));
        }
        if (!hasDynamicTest) {
            this.range.append(advice);
        } else {
            InstructionList afterThingie = new InstructionList(InstructionConstants.NOP);
            InstructionList callback = this.makeCallToCallback(extractedShadowMethod);
            if (this.terminatesWithReturn()) {
                callback.append(InstructionFactory.createReturn(extractedShadowMethod.getReturnType()));
            } else {
                advice.append(InstructionFactory.createBranchInstruction((short)167, afterThingie.getStart()));
            }
            this.range.append(munger.getTestInstructions(this, advice.getStart(), callback.getStart(), advice.getStart()));
            this.range.append(advice);
            this.range.append(callback);
            this.range.append(afterThingie);
        }
        if (!munger.getDeclaringType().isAnnotationStyleAspect()) {
            String proceedName = NameMangler.proceedMethodName(munger.getSignature().getName());
            InstructionHandle curr = localAdviceMethod.getBody().getStart();
            InstructionHandle end2 = localAdviceMethod.getBody().getEnd();
            ConstantPool cpg = localAdviceMethod.getEnclosingClass().getConstantPool();
            while (curr != end2) {
                InstructionHandle next = curr.getNext();
                Instruction inst = curr.getInstruction();
                if (inst.opcode == 184 && proceedName.equals(((InvokeInstruction)inst).getMethodName(cpg))) {
                    localAdviceMethod.getBody().append(curr, this.getRedoneProceedCall(fact, extractedShadowMethod, munger, localAdviceMethod, proceedVarList));
                    Utility.deleteInstruction(curr, localAdviceMethod);
                }
                curr = next;
            }
        } else {
            InstructionHandle curr = localAdviceMethod.getBody().getStart();
            end = localAdviceMethod.getBody().getEnd();
            ConstantPool cpg = localAdviceMethod.getEnclosingClass().getConstantPool();
            while (curr != end) {
                InstructionHandle next = curr.getNext();
                Instruction inst = curr.getInstruction();
                if (inst instanceof INVOKEINTERFACE && "proceed".equals(((INVOKEINTERFACE)inst).getMethodName(cpg))) {
                    boolean isProceedWithArgs = ((INVOKEINTERFACE)inst).getArgumentTypes(cpg).length == 1;
                    InstructionList insteadProceedIl = this.getRedoneProceedCallForAnnotationStyle(fact, extractedShadowMethod, munger, localAdviceMethod, proceedVarList, isProceedWithArgs);
                    localAdviceMethod.getBody().append(curr, insteadProceedIl);
                    Utility.deleteInstruction(curr, localAdviceMethod);
                }
                curr = next;
            }
        }
        InstructionHandle start = localAdviceMethod.getBody().getStart();
        end = localAdviceMethod.getBody().getEnd();
        while (start.getInstruction().opcode == 254) {
            start = start.getNext();
        }
        while (end.getInstruction().opcode == 254) {
            end = end.getPrev();
        }
        Type[] args = localAdviceMethod.getArgumentTypes();
        int argNumber = 0;
        int slot = 0;
        while (slot < extraParamOffset) {
            String argumentName = null;
            argumentName = argNumber >= args.length || parameterNames.size() == 0 || argNumber >= parameterNames.size() ? new StringBuffer("unknown").append(argNumber).toString() : (String)parameterNames.get(argNumber);
            String argumentSignature = args[argNumber].getSignature();
            LocalVariableTag lvt = new LocalVariableTag(argumentSignature, argumentName, slot, 0);
            start.addTargeter(lvt);
            end.addTargeter(lvt);
            slot += args[argNumber].getSize();
            ++argNumber;
        }
    }

    private boolean isAnnotationStylePassingProceedingJoinPointOutOfAdvice(BcelAdvice munger, boolean hasDynamicTest, LazyMethodGen adviceMethod) {
        if (munger.getConcreteAspect().isAnnotationStyleAspect()) {
            boolean canSeeProceedPassedToOther = false;
            InstructionHandle curr = adviceMethod.getBody().getStart();
            InstructionHandle end = adviceMethod.getBody().getEnd();
            ConstantPool cpg = adviceMethod.getEnclosingClass().getConstantPool();
            while (curr != end) {
                InstructionHandle next = curr.getNext();
                Instruction inst = curr.getInstruction();
                if (inst instanceof InvokeInstruction && ((InvokeInstruction)inst).getSignature(cpg).indexOf("Lorg/aspectj/lang/ProceedingJoinPoint;") > 0) {
                    canSeeProceedPassedToOther = true;
                    break;
                }
                curr = next;
            }
            if (canSeeProceedPassedToOther) {
                adviceMethod.setCanInline(false);
                this.weaveAroundClosure(munger, hasDynamicTest);
                return true;
            }
        }
        return false;
    }

    private InstructionList getRedoneProceedCall(InstructionFactory fact, LazyMethodGen callbackMethod, BcelAdvice munger, LazyMethodGen localAdviceMethod, List<BcelVar> argVarList) {
        InstructionList ret = new InstructionList();
        BcelVar[] adviceVars = munger.getExposedStateAsBcelVars(true);
        IntMap proceedMap = this.makeProceedArgumentMap(adviceVars);
        ResolvedType[] proceedParamTypes = this.world.resolve(munger.getSignature().getParameterTypes());
        if (munger.getBaseParameterCount() + 1 < proceedParamTypes.length) {
            int len = munger.getBaseParameterCount() + 1;
            ResolvedType[] newTypes = new ResolvedType[len];
            System.arraycopy(proceedParamTypes, 0, newTypes, 0, len);
            proceedParamTypes = newTypes;
        }
        BcelVar[] proceedVars = Utility.pushAndReturnArrayOfVars(proceedParamTypes, ret, fact, localAdviceMethod);
        Type[] stateTypes = callbackMethod.getArgumentTypes();
        int len = stateTypes.length;
        for (int i = 0; i < len; ++i) {
            Type stateType = stateTypes[i];
            ResolvedType stateTypeX = BcelWorld.fromBcel(stateType).resolve(this.world);
            if (proceedMap.hasKey(i)) {
                proceedVars[proceedMap.get(i)].appendLoadAndConvert(ret, fact, stateTypeX);
                continue;
            }
            argVarList.get(i).appendLoad(ret, fact);
        }
        ret.append(Utility.createInvoke(fact, callbackMethod));
        ret.append(Utility.createConversion(fact, callbackMethod.getReturnType(), BcelWorld.makeBcelType(munger.getSignature().getReturnType()), this.world.isInJava5Mode()));
        return ret;
    }

    private InstructionList getRedoneProceedCallForAnnotationStyle(InstructionFactory fact, LazyMethodGen callbackMethod, BcelAdvice munger, LazyMethodGen localAdviceMethod, List<BcelVar> argVarList, boolean isProceedWithArgs) {
        InstructionList ret = new InstructionList();
        if (isProceedWithArgs) {
            ArrayType objectArrayType = Type.OBJECT_ARRAY;
            int theObjectArrayLocalNumber = localAdviceMethod.allocateLocal(objectArrayType);
            ret.append(InstructionFactory.createStore(objectArrayType, theObjectArrayLocalNumber));
            Type proceedingJpType = Type.getType("Lorg/aspectj/lang/ProceedingJoinPoint;");
            int pjpLocalNumber = localAdviceMethod.allocateLocal(proceedingJpType);
            ret.append(InstructionFactory.createStore(proceedingJpType, pjpLocalNumber));
            boolean pointcutBindsThis = this.bindsThis(munger);
            boolean pointcutBindsTarget = this.bindsTarget(munger);
            boolean targetIsSameAsThis = this.getKind().isTargetSameAsThis();
            int nextArgumentToProvideForCallback = 0;
            if (!(!this.hasThis() || pointcutBindsTarget && targetIsSameAsThis)) {
                if (pointcutBindsThis) {
                    ret.append(InstructionFactory.createLoad(objectArrayType, theObjectArrayLocalNumber));
                    ret.append(Utility.createConstant(fact, 0));
                    ret.append(InstructionFactory.createArrayLoad(Type.OBJECT));
                    ret.append(Utility.createConversion(fact, Type.OBJECT, callbackMethod.getArgumentTypes()[0]));
                } else {
                    ret.append(InstructionFactory.createALOAD(0));
                }
                ++nextArgumentToProvideForCallback;
            }
            if (this.hasTarget()) {
                if (pointcutBindsTarget) {
                    if (this.getKind().isTargetSameAsThis()) {
                        ret.append(InstructionFactory.createLoad(objectArrayType, theObjectArrayLocalNumber));
                        ret.append(Utility.createConstant(fact, pointcutBindsThis ? 1 : 0));
                        ret.append(InstructionFactory.createArrayLoad(Type.OBJECT));
                        ret.append(Utility.createConversion(fact, Type.OBJECT, callbackMethod.getArgumentTypes()[0]));
                    } else {
                        int position = this.hasThis() && pointcutBindsThis ? 1 : 0;
                        ret.append(InstructionFactory.createLoad(objectArrayType, theObjectArrayLocalNumber));
                        ret.append(Utility.createConstant(fact, position));
                        ret.append(InstructionFactory.createArrayLoad(Type.OBJECT));
                        ret.append(Utility.createConversion(fact, Type.OBJECT, callbackMethod.getArgumentTypes()[nextArgumentToProvideForCallback]));
                    }
                    ++nextArgumentToProvideForCallback;
                } else if (!this.getKind().isTargetSameAsThis()) {
                    ret.append(InstructionFactory.createLoad(localAdviceMethod.getArgumentTypes()[0], this.hasThis() ? 1 : 0));
                    ++nextArgumentToProvideForCallback;
                }
            }
            int indexIntoObjectArrayForArguments = (pointcutBindsThis ? 1 : 0) + (pointcutBindsTarget ? 1 : 0);
            int len = callbackMethod.getArgumentTypes().length;
            for (int i = nextArgumentToProvideForCallback; i < len; ++i) {
                Type stateType = callbackMethod.getArgumentTypes()[i];
                BcelWorld.fromBcel(stateType).resolve(this.world);
                if ("Lorg/aspectj/lang/JoinPoint;".equals(stateType.getSignature())) {
                    ret.append(new InstructionLV(25, pjpLocalNumber));
                    continue;
                }
                ret.append(InstructionFactory.createLoad(objectArrayType, theObjectArrayLocalNumber));
                ret.append(Utility.createConstant(fact, i - nextArgumentToProvideForCallback + indexIntoObjectArrayForArguments));
                ret.append(InstructionFactory.createArrayLoad(Type.OBJECT));
                ret.append(Utility.createConversion(fact, Type.OBJECT, stateType));
            }
        } else {
            Type proceedingJpType = Type.getType("Lorg/aspectj/lang/ProceedingJoinPoint;");
            int localJp = localAdviceMethod.allocateLocal(proceedingJpType);
            ret.append(InstructionFactory.createStore(proceedingJpType, localJp));
            int idx = 0;
            int len = callbackMethod.getArgumentTypes().length;
            for (int i = 0; i < len; ++i) {
                Type stateType = callbackMethod.getArgumentTypes()[i];
                BcelWorld.fromBcel(stateType).resolve(this.world);
                if ("Lorg/aspectj/lang/JoinPoint;".equals(stateType.getSignature())) {
                    ret.append(InstructionFactory.createALOAD(localJp));
                    ++idx;
                    continue;
                }
                ret.append(InstructionFactory.createLoad(stateType, idx));
                idx += stateType.getSize();
            }
        }
        ret.append(Utility.createInvoke(fact, callbackMethod));
        if (!UnresolvedType.OBJECT.equals(munger.getSignature().getReturnType())) {
            ret.append(Utility.createConversion(fact, callbackMethod.getReturnType(), Type.OBJECT));
        }
        ret.append(Utility.createConversion(fact, callbackMethod.getReturnType(), BcelWorld.makeBcelType(munger.getSignature().getReturnType()), this.world.isInJava5Mode()));
        return ret;
    }

    private boolean bindsThis(BcelAdvice munger) {
        UsesThisVisitor utv = new UsesThisVisitor();
        munger.getPointcut().accept(utv, null);
        return utv.usesThis;
    }

    private boolean bindsTarget(BcelAdvice munger) {
        UsesTargetVisitor utv = new UsesTargetVisitor();
        munger.getPointcut().accept(utv, null);
        return utv.usesTarget;
    }

    public void weaveAroundClosure(BcelAdvice munger, boolean hasDynamicTest) {
        InstructionList returnConversionCode;
        InstructionFactory fact = this.getFactory();
        this.enclosingMethod.setCanInline(false);
        int linenumber = this.getSourceLine();
        boolean shadowClassIsInterface = this.getEnclosingClass().isInterface();
        LazyMethodGen callbackMethod = this.extractShadowInstructionsIntoNewMethod(NameMangler.aroundShadowMethodName(this.getSignature(), this.getEnclosingClass().getNewGeneratedNameTag()), shadowClassIsInterface ? 1 : 0, munger.getSourceLocation(), new ArrayList<String>(), shadowClassIsInterface);
        BcelVar[] adviceVars = munger.getExposedStateAsBcelVars(true);
        String closureClassName = NameMangler.makeClosureClassName(this.getEnclosingClass().getType(), this.getEnclosingClass().getNewGeneratedNameTag());
        MemberImpl constructorSig = new MemberImpl(Member.CONSTRUCTOR, UnresolvedType.forName(closureClassName), 0, "<init>", "([Ljava/lang/Object;)V");
        BcelVar closureHolder = null;
        if (this.getKind() == PreInitialization) {
            closureHolder = this.genTempVar(AjcMemberMaker.AROUND_CLOSURE_TYPE);
        }
        InstructionList closureInstantiation = this.makeClosureInstantiation(constructorSig, closureHolder);
        this.makeClosureClassAndReturnConstructor(closureClassName, callbackMethod, this.makeProceedArgumentMap(adviceVars));
        if (this.getKind() == PreInitialization) {
            returnConversionCode = new InstructionList();
            BcelVar stateTempVar = this.genTempVar(UnresolvedType.OBJECTARRAY);
            closureHolder.appendLoad(returnConversionCode, fact);
            returnConversionCode.append(Utility.createInvoke(fact, this.world, AjcMemberMaker.aroundClosurePreInitializationGetter()));
            stateTempVar.appendStore(returnConversionCode, fact);
            Type[] stateTypes = this.getSuperConstructorParameterTypes();
            returnConversionCode.append(InstructionConstants.ALOAD_0);
            int len = stateTypes.length;
            for (int i = 0; i < len; ++i) {
                UnresolvedType bcelTX = BcelWorld.fromBcel(stateTypes[i]);
                ResolvedType stateRTX = this.world.resolve(bcelTX, true);
                if (stateRTX.isMissing()) {
                    this.world.getLint().cantFindType.signal(new String[]{WeaverMessages.format("cftDuringAroundWeavePreinit", bcelTX.getClassName())}, this.getSourceLocation(), new ISourceLocation[]{munger.getSourceLocation()});
                }
                stateTempVar.appendConvertableArrayLoad(returnConversionCode, fact, i, stateRTX);
            }
        } else {
            Member mungerSignature = munger.getSignature();
            if (munger.getSignature() instanceof ResolvedMember && ((ResolvedMember)mungerSignature).hasBackingGenericMember()) {
                mungerSignature = ((ResolvedMember)mungerSignature).getBackingGenericMember();
            }
            UnresolvedType returnType = mungerSignature.getReturnType();
            returnConversionCode = Utility.createConversion(this.getFactory(), BcelWorld.makeBcelType(returnType), callbackMethod.getReturnType(), this.world.isInJava5Mode());
            if (!this.isFallsThrough()) {
                returnConversionCode.append(InstructionFactory.createReturn(callbackMethod.getReturnType()));
            }
        }
        int bitflags = 0;
        if (this.getKind().isTargetSameAsThis()) {
            bitflags |= 0x10000;
        }
        if (this.hasThis()) {
            bitflags |= 0x1000;
        }
        if (this.bindsThis(munger)) {
            bitflags |= 0x100;
        }
        if (this.hasTarget()) {
            bitflags |= 0x10;
        }
        if (this.bindsTarget(munger)) {
            bitflags |= 1;
        }
        this.closureVarInitialized = false;
        if (munger.getConcreteAspect() != null && munger.getConcreteAspect().isAnnotationStyleAspect() && munger.getDeclaringAspect() != null && munger.getDeclaringAspect().resolve(this.world).isAnnotationStyleAspect()) {
            this.aroundClosureInstance = this.genTempVar(AjcMemberMaker.AROUND_CLOSURE_TYPE);
            closureInstantiation.append(InstructionFactory.createDup(1));
            this.aroundClosureInstance.appendStore(closureInstantiation, fact);
            closureInstantiation.append(fact.createConstant(bitflags));
            if (this.needAroundClosureStacking) {
                closureInstantiation.append(Utility.createInvoke(this.getFactory(), this.getWorld(), (Member)new MemberImpl(Member.METHOD, UnresolvedType.forName("org.aspectj.runtime.internal.AroundClosure"), 1, "linkStackClosureAndJoinPoint", String.format("%s%s", "(I)", "Lorg/aspectj/lang/ProceedingJoinPoint;"))));
            } else {
                closureInstantiation.append(Utility.createInvoke(this.getFactory(), this.getWorld(), (Member)new MemberImpl(Member.METHOD, UnresolvedType.forName("org.aspectj.runtime.internal.AroundClosure"), 1, "linkClosureAndJoinPoint", String.format("%s%s", "(I)", "Lorg/aspectj/lang/ProceedingJoinPoint;"))));
            }
        }
        InstructionList advice = new InstructionList();
        advice.append(munger.getAdviceArgSetup(this, null, closureInstantiation));
        InstructionHandle tryUnlinkPosition = advice.append(munger.getNonTestAdviceInstructions(this));
        if (this.needAroundClosureStacking && munger.getConcreteAspect() != null && munger.getConcreteAspect().isAnnotationStyleAspect() && munger.getDeclaringAspect() != null && munger.getDeclaringAspect().resolve(this.world).isAnnotationStyleAspect() && this.closureVarInitialized) {
            this.aroundClosureInstance.appendLoad(advice, fact);
            InstructionHandle unlinkInsn = advice.append(Utility.createInvoke(this.getFactory(), this.getWorld(), (Member)new MemberImpl(Member.METHOD, UnresolvedType.forName("org.aspectj.runtime.internal.AroundClosure"), 1, "unlink", "()V")));
            BranchHandle jumpOverHandler = advice.append(new InstructionBranch(167, null));
            InstructionHandle handlerStart = advice.append(this.aroundClosureInstance.createLoad(fact));
            advice.append(Utility.createInvoke(this.getFactory(), this.getWorld(), (Member)new MemberImpl(Member.METHOD, UnresolvedType.forName("org.aspectj.runtime.internal.AroundClosure"), 1, "unlink", "()V")));
            advice.append(InstructionConstants.ATHROW);
            InstructionHandle jumpTarget = advice.append(InstructionConstants.NOP);
            jumpOverHandler.setTarget(jumpTarget);
            this.enclosingMethod.addExceptionHandler(tryUnlinkPosition, unlinkInsn, handlerStart, null, false);
        }
        advice.append(returnConversionCode);
        if (this.getKind() == Shadow.MethodExecution && linenumber > 0) {
            advice.getStart().addTargeter(new LineNumberTag(linenumber));
        }
        if (!hasDynamicTest) {
            this.range.append(advice);
        } else {
            InstructionList callback = this.makeCallToCallback(callbackMethod);
            InstructionList postCallback = new InstructionList();
            if (this.terminatesWithReturn()) {
                callback.append(InstructionFactory.createReturn(callbackMethod.getReturnType()));
            } else {
                advice.append(InstructionFactory.createBranchInstruction((short)167, postCallback.append(InstructionConstants.NOP)));
            }
            this.range.append(munger.getTestInstructions(this, advice.getStart(), callback.getStart(), advice.getStart()));
            this.range.append(advice);
            this.range.append(callback);
            this.range.append(postCallback);
        }
    }

    InstructionList makeCallToCallback(LazyMethodGen callbackMethod) {
        InstructionFactory fact = this.getFactory();
        InstructionList callback = new InstructionList();
        if (this.thisVar != null) {
            callback.append(InstructionConstants.ALOAD_0);
        }
        if (this.targetVar != null && this.targetVar != this.thisVar) {
            callback.append(BcelRenderer.renderExpr(fact, this.world, this.targetVar));
        }
        callback.append(BcelRenderer.renderExprs(fact, this.world, this.argVars));
        if (this.thisJoinPointVar != null) {
            callback.append(BcelRenderer.renderExpr(fact, this.world, this.thisJoinPointVar));
        }
        callback.append(Utility.createInvoke(fact, callbackMethod));
        return callback;
    }

    private InstructionList makeClosureInstantiation(Member constructor, BcelVar holder) {
        InstructionFactory fact = this.getFactory();
        BcelVar arrayVar = this.genTempVar(UnresolvedType.OBJECTARRAY);
        InstructionList il = new InstructionList();
        int alen = this.getArgCount() + (this.thisVar == null ? 0 : 1) + (this.targetVar != null && this.targetVar != this.thisVar ? 1 : 0) + (this.thisJoinPointVar == null ? 0 : 1);
        il.append(Utility.createConstant(fact, alen));
        il.append(fact.createNewArray(Type.OBJECT, (short)1));
        arrayVar.appendStore(il, fact);
        int stateIndex = 0;
        if (this.thisVar != null) {
            arrayVar.appendConvertableArrayStore(il, fact, stateIndex, this.thisVar);
            this.thisVar.setPositionInAroundState(stateIndex);
            ++stateIndex;
        }
        if (this.targetVar != null && this.targetVar != this.thisVar) {
            arrayVar.appendConvertableArrayStore(il, fact, stateIndex, this.targetVar);
            this.targetVar.setPositionInAroundState(stateIndex);
            ++stateIndex;
        }
        int len = this.getArgCount();
        for (int i = 0; i < len; ++i) {
            arrayVar.appendConvertableArrayStore(il, fact, stateIndex, this.argVars[i]);
            this.argVars[i].setPositionInAroundState(stateIndex);
            ++stateIndex;
        }
        if (this.thisJoinPointVar != null) {
            arrayVar.appendConvertableArrayStore(il, fact, stateIndex, this.thisJoinPointVar);
            this.thisJoinPointVar.setPositionInAroundState(stateIndex);
            ++stateIndex;
        }
        il.append(fact.createNew(new ObjectType(constructor.getDeclaringType().getName())));
        il.append(InstructionConstants.DUP);
        arrayVar.appendLoad(il, fact);
        il.append(Utility.createInvoke(fact, this.world, constructor));
        if (this.getKind() == PreInitialization) {
            il.append(InstructionConstants.DUP);
            holder.appendStore(il, fact);
        }
        return il;
    }

    private IntMap makeProceedArgumentMap(BcelVar[] adviceArgs) {
        IntMap ret = new IntMap();
        int len = adviceArgs.length;
        for (int i = 0; i < len; ++i) {
            int pos;
            BcelVar v = adviceArgs[i];
            if (v == null || (pos = v.getPositionInAroundState()) < 0) continue;
            ret.put(pos, i);
        }
        return ret;
    }

    private LazyMethodGen makeClosureClassAndReturnConstructor(String closureClassName, LazyMethodGen callbackMethod, IntMap proceedMap) {
        String superClassName = "org.aspectj.runtime.internal.AroundClosure";
        ArrayType objectArrayType = new ArrayType(Type.OBJECT, 1);
        LazyClassGen closureClass = new LazyClassGen(closureClassName, superClassName, this.getEnclosingClass().getFileName(), 1, new String[0], this.getWorld());
        closureClass.setMajorMinor(this.getEnclosingClass().getMajor(), this.getEnclosingClass().getMinor());
        InstructionFactory fact = new InstructionFactory(closureClass.getConstantPool());
        LazyMethodGen constructor = new LazyMethodGen(1, Type.VOID, "<init>", new Type[]{objectArrayType}, new String[0], closureClass);
        InstructionList cbody = constructor.getBody();
        cbody.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        cbody.append(InstructionFactory.createLoad(objectArrayType, 1));
        cbody.append(fact.createInvoke(superClassName, "<init>", Type.VOID, new Type[]{objectArrayType}, (short)183));
        cbody.append(InstructionFactory.createReturn(Type.VOID));
        closureClass.addMethodGen(constructor);
        LazyMethodGen runMethod = new LazyMethodGen(1, Type.OBJECT, "run", new Type[]{objectArrayType}, new String[0], closureClass);
        InstructionList mbody = runMethod.getBody();
        BcelVar proceedVar = new BcelVar(UnresolvedType.OBJECTARRAY.resolve(this.world), 1);
        BcelVar stateVar = new BcelVar(UnresolvedType.OBJECTARRAY.resolve(this.world), runMethod.allocateLocal(1));
        mbody.append(InstructionFactory.createThis());
        mbody.append(fact.createGetField(superClassName, "state", objectArrayType));
        mbody.append(stateVar.createStore(fact));
        Type[] stateTypes = callbackMethod.getArgumentTypes();
        int len = stateTypes.length;
        for (int i = 0; i < len; ++i) {
            ResolvedType resolvedStateType = BcelWorld.fromBcel(stateTypes[i]).resolve(this.world);
            if (proceedMap.hasKey(i)) {
                mbody.append(proceedVar.createConvertableArrayLoad(fact, proceedMap.get(i), resolvedStateType));
                continue;
            }
            mbody.append(stateVar.createConvertableArrayLoad(fact, i, resolvedStateType));
        }
        mbody.append(Utility.createInvoke(fact, callbackMethod));
        if (this.getKind() == PreInitialization) {
            mbody.append(Utility.createSet(fact, AjcMemberMaker.aroundClosurePreInitializationField()));
            mbody.append(InstructionConstants.ACONST_NULL);
        } else {
            mbody.append(Utility.createConversion(fact, callbackMethod.getReturnType(), Type.OBJECT));
        }
        mbody.append(InstructionFactory.createReturn(Type.OBJECT));
        closureClass.addMethodGen(runMethod);
        this.getEnclosingClass().addGeneratedInner(closureClass);
        return constructor;
    }

    LazyMethodGen extractShadowInstructionsIntoNewMethod(String extractedMethodName, int extractedMethodVisibilityModifier, ISourceLocation adviceSourceLocation, List<String> parameterNames, boolean beingPlacedInInterface) {
        if (!this.getKind().allowsExtraction()) {
            throw new BCException("Attempt to extract method from a shadow kind (" + this.getKind() + ") that does not support this operation");
        }
        LazyMethodGen newMethod = this.createShadowMethodGen(extractedMethodName, extractedMethodVisibilityModifier, parameterNames, beingPlacedInInterface);
        IntMap remapper = this.makeRemap();
        this.range.extractInstructionsInto(newMethod, remapper, this.getKind() != PreInitialization && this.isFallsThrough());
        if (this.getKind() == PreInitialization) {
            this.addPreInitializationReturnCode(newMethod, this.getSuperConstructorParameterTypes());
        }
        this.getEnclosingClass().addMethodGen(newMethod, adviceSourceLocation);
        return newMethod;
    }

    private void addPreInitializationReturnCode(LazyMethodGen extractedMethod, Type[] superConstructorTypes) {
        InstructionList body = extractedMethod.getBody();
        InstructionFactory fact = this.getFactory();
        BcelVar arrayVar = new BcelVar(this.world.getCoreType(UnresolvedType.OBJECTARRAY), extractedMethod.allocateLocal(1));
        int len = superConstructorTypes.length;
        body.append(Utility.createConstant(fact, len));
        body.append(fact.createNewArray(Type.OBJECT, (short)1));
        arrayVar.appendStore(body, fact);
        for (int i = len - 1; i >= 0; ++i) {
            body.append(Utility.createConversion(fact, superConstructorTypes[i], Type.OBJECT));
            arrayVar.appendLoad(body, fact);
            body.append(InstructionConstants.SWAP);
            body.append(Utility.createConstant(fact, i));
            body.append(InstructionConstants.SWAP);
            body.append(InstructionFactory.createArrayStore(Type.OBJECT));
        }
        arrayVar.appendLoad(body, fact);
        body.append(InstructionConstants.ARETURN);
    }

    private Type[] getSuperConstructorParameterTypes() {
        InstructionHandle superCallHandle = this.getRange().getEnd().getNext();
        InvokeInstruction superCallInstruction = (InvokeInstruction)superCallHandle.getInstruction();
        return superCallInstruction.getArgumentTypes(this.getEnclosingClass().getConstantPool());
    }

    private IntMap makeRemap() {
        IntMap ret = new IntMap(5);
        int reti = 0;
        if (this.thisVar != null) {
            ret.put(0, reti++);
        }
        if (this.targetVar != null && this.targetVar != this.thisVar) {
            ret.put(this.targetVar.getSlot(), reti++);
        }
        int len = this.argVars.length;
        for (int i = 0; i < len; ++i) {
            ret.put(this.argVars[i].getSlot(), reti);
            reti += this.argVars[i].getType().getSize();
        }
        if (this.thisJoinPointVar != null) {
            ret.put(this.thisJoinPointVar.getSlot(), reti++);
        }
        if (!this.getKind().argsOnStack()) {
            int oldi = 0;
            int newi = 0;
            if (this.arg0HoldsThis()) {
                ret.put(0, 0);
                ++oldi;
                ++newi;
            }
            for (int i = 0; i < this.getArgCount(); ++i) {
                UnresolvedType type = this.getArgType(i);
                ret.put(oldi, newi);
                oldi += type.getSize();
                newi += type.getSize();
            }
        }
        return ret;
    }

    private LazyMethodGen createShadowMethodGen(String newMethodName, int visibilityModifier, List<String> parameterNames, boolean beingPlacedInInterface) {
        Type[] shadowParameterTypes = BcelWorld.makeBcelTypes(this.getArgTypes());
        int modifiers = (this.world.useFinal() && !beingPlacedInInterface ? 16 : 0) | 8 | visibilityModifier;
        if (this.targetVar != null && this.targetVar != this.thisVar) {
            ResolvedMember resolvedMember;
            UnresolvedType targetType = this.getTargetType();
            targetType = this.ensureTargetTypeIsCorrect(targetType);
            if (!(this.getKind() != FieldGet && this.getKind() != FieldSet || this.getActualTargetType() == null || this.getActualTargetType().equals(targetType.getName()))) {
                targetType = UnresolvedType.forName(this.getActualTargetType()).resolve(this.world);
            }
            if ((resolvedMember = this.getSignature().resolve(this.world)) != null && Modifier.isProtected(resolvedMember.getModifiers()) && !this.samePackage(resolvedMember.getDeclaringType().getPackageName(), this.getEnclosingType().getPackageName()) && !resolvedMember.getName().equals("clone")) {
                if (!this.hasThis()) {
                    if (Modifier.isStatic(this.enclosingMethod.getAccessFlags()) && this.enclosingMethod.getName().startsWith("access$")) {
                        targetType = BcelWorld.fromBcel(this.enclosingMethod.getArgumentTypes()[0]);
                    }
                } else {
                    if (!targetType.resolve(this.world).isAssignableFrom(this.getThisType().resolve(this.world))) {
                        throw new BCException("bad bytecode");
                    }
                    targetType = this.getThisType();
                }
            }
            parameterNames.add("target");
            shadowParameterTypes = this.addTypeToFront(BcelWorld.makeBcelType(targetType), shadowParameterTypes);
        }
        if (this.thisVar != null) {
            UnresolvedType thisType = this.getThisType();
            parameterNames.add(0, "ajc$this");
            shadowParameterTypes = this.addTypeToFront(BcelWorld.makeBcelType(thisType), shadowParameterTypes);
        }
        if (this.getKind() == Shadow.FieldSet || this.getKind() == Shadow.FieldGet) {
            parameterNames.add(this.getSignature().getName());
        } else {
            String[] pnames = this.getSignature().getParameterNames(this.world);
            if (pnames != null) {
                for (int i = 0; i < pnames.length; ++i) {
                    if (i == 0 && pnames[i].equals("this")) {
                        parameterNames.add("ajc$this");
                        continue;
                    }
                    parameterNames.add(pnames[i]);
                }
            }
        }
        if (this.thisJoinPointVar != null) {
            parameterNames.add("thisJoinPoint");
            shadowParameterTypes = this.addTypeToEnd(LazyClassGen.tjpType, shadowParameterTypes);
        }
        UnresolvedType returnType = this.getKind() == PreInitialization ? UnresolvedType.OBJECTARRAY : (this.getKind() == ConstructorCall ? this.getSignature().getDeclaringType() : (this.getKind() == FieldSet ? UnresolvedType.VOID : this.getSignature().getReturnType().resolve(this.world)));
        return new LazyMethodGen(modifiers, BcelWorld.makeBcelType(returnType), newMethodName, shadowParameterTypes, NoDeclaredExceptions, this.getEnclosingClass());
    }

    private boolean samePackage(String p1, String p2) {
        if (p1 == null) {
            return p2 == null;
        }
        if (p2 == null) {
            return false;
        }
        return p1.equals(p2);
    }

    private Type[] addTypeToFront(Type type, Type[] types) {
        int len = types.length;
        Type[] ret = new Type[len + 1];
        ret[0] = type;
        System.arraycopy(types, 0, ret, 1, len);
        return ret;
    }

    private Type[] addTypeToEnd(Type type, Type[] types) {
        int len = types.length;
        Type[] ret = new Type[len + 1];
        ret[len] = type;
        System.arraycopy(types, 0, ret, 0, len);
        return ret;
    }

    public BcelVar genTempVar(UnresolvedType utype) {
        ResolvedType rtype = utype.resolve(this.world);
        return new BcelVar(rtype, this.genTempVarIndex(rtype.getSize()));
    }

    public BcelVar genTempVar(UnresolvedType typeX, String localName) {
        BcelVar tv = this.genTempVar(typeX);
        return tv;
    }

    private int genTempVarIndex(int size) {
        return this.enclosingMethod.allocateLocal(size);
    }

    public InstructionFactory getFactory() {
        return this.getEnclosingClass().getFactory();
    }

    @Override
    public ISourceLocation getSourceLocation() {
        int sourceLine = this.getSourceLine();
        if (sourceLine == 0 || sourceLine == -1) {
            return this.getEnclosingClass().getType().getSourceLocation();
        }
        if (this.getKind() == Shadow.StaticInitialization && this.getEnclosingClass().getType().getSourceLocation().getOffset() != 0) {
            return this.getEnclosingClass().getType().getSourceLocation();
        }
        int offset = 0;
        Shadow.Kind kind = this.getKind();
        if ((kind == MethodExecution || kind == ConstructorExecution || kind == AdviceExecution || kind == StaticInitialization || kind == PreInitialization || kind == Initialization) && this.getEnclosingMethod().hasDeclaredLineNumberInfo()) {
            offset = this.getEnclosingMethod().getDeclarationOffset();
        }
        return this.getEnclosingClass().getType().getSourceContext().makeSourceLocation(sourceLine, offset);
    }

    public Shadow getEnclosingShadow() {
        return this.enclosingShadow;
    }

    public LazyMethodGen getEnclosingMethod() {
        return this.enclosingMethod;
    }

    public boolean isFallsThrough() {
        return !this.terminatesWithReturn();
    }

    public void setActualTargetType(String className) {
        this.actualInstructionTargetType = className;
    }

    public String getActualTargetType() {
        return this.actualInstructionTargetType;
    }

    private static class UsesTargetVisitor
    extends AbstractPatternNodeVisitor {
        boolean usesTarget = false;

        private UsesTargetVisitor() {
        }

        @Override
        public Object visit(ThisOrTargetPointcut node, Object data) {
            if (!node.isThis() && node.isBinding()) {
                this.usesTarget = true;
            }
            return node;
        }

        @Override
        public Object visit(AndPointcut node, Object data) {
            if (!this.usesTarget) {
                node.getLeft().accept(this, data);
            }
            if (!this.usesTarget) {
                node.getRight().accept(this, data);
            }
            return node;
        }

        @Override
        public Object visit(NotPointcut node, Object data) {
            if (!this.usesTarget) {
                node.getNegatedPointcut().accept(this, data);
            }
            return node;
        }

        @Override
        public Object visit(OrPointcut node, Object data) {
            if (!this.usesTarget) {
                node.getLeft().accept(this, data);
            }
            if (!this.usesTarget) {
                node.getRight().accept(this, data);
            }
            return node;
        }
    }

    private static class UsesThisVisitor
    extends AbstractPatternNodeVisitor {
        boolean usesThis = false;

        private UsesThisVisitor() {
        }

        @Override
        public Object visit(ThisOrTargetPointcut node, Object data) {
            if (node.isThis() && node.isBinding()) {
                this.usesThis = true;
            }
            return node;
        }

        @Override
        public Object visit(AndPointcut node, Object data) {
            if (!this.usesThis) {
                node.getLeft().accept(this, data);
            }
            if (!this.usesThis) {
                node.getRight().accept(this, data);
            }
            return node;
        }

        @Override
        public Object visit(NotPointcut node, Object data) {
            if (!this.usesThis) {
                node.getNegatedPointcut().accept(this, data);
            }
            return node;
        }

        @Override
        public Object visit(OrPointcut node, Object data) {
            if (!this.usesThis) {
                node.getLeft().accept(this, data);
            }
            if (!this.usesThis) {
                node.getRight().accept(this, data);
            }
            return node;
        }
    }
}


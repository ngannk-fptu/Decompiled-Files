/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.reflect.AnnotationFinder;
import org.aspectj.weaver.reflect.IReflectionWorld;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;
import org.aspectj.weaver.reflect.ReflectionVar;
import org.aspectj.weaver.tools.MatchingContext;

public class ReflectionShadow
extends Shadow {
    private final World world;
    private final ResolvedType enclosingType;
    private final ResolvedMember enclosingMember;
    private final MatchingContext matchContext;
    private Var thisVar = null;
    private Var targetVar = null;
    private Var[] argsVars = null;
    private Var atThisVar = null;
    private Var atTargetVar = null;
    private Map atArgsVars = new HashMap();
    private Map withinAnnotationVar = new HashMap();
    private Map withinCodeAnnotationVar = new HashMap();
    private Map annotationVar = new HashMap();
    private AnnotationFinder annotationFinder;

    public static Shadow makeExecutionShadow(World inWorld, java.lang.reflect.Member forMethod, MatchingContext withContext) {
        Shadow.Kind kind = forMethod instanceof Method ? Shadow.MethodExecution : Shadow.ConstructorExecution;
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(forMethod, inWorld);
        ResolvedType enclosingType = signature.getDeclaringType().resolve(inWorld);
        return new ReflectionShadow(inWorld, kind, signature, null, enclosingType, null, withContext);
    }

    public static Shadow makeAdviceExecutionShadow(World inWorld, Method forMethod, MatchingContext withContext) {
        Shadow.Kind kind = Shadow.AdviceExecution;
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedAdviceMember(forMethod, inWorld);
        ResolvedType enclosingType = signature.getDeclaringType().resolve(inWorld);
        return new ReflectionShadow(inWorld, kind, signature, null, enclosingType, null, withContext);
    }

    public static Shadow makeCallShadow(World inWorld, java.lang.reflect.Member aMember, java.lang.reflect.Member withinCode, MatchingContext withContext) {
        Shadow enclosingShadow = ReflectionShadow.makeExecutionShadow(inWorld, withinCode, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(aMember, inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(withinCode, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        Shadow.Kind kind = aMember instanceof Method ? Shadow.MethodCall : Shadow.ConstructorCall;
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public static Shadow makeCallShadow(World inWorld, java.lang.reflect.Member aMember, Class thisClass, MatchingContext withContext) {
        Shadow enclosingShadow = ReflectionShadow.makeStaticInitializationShadow(inWorld, thisClass, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(aMember, inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createStaticInitMember(thisClass, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        Shadow.Kind kind = aMember instanceof Method ? Shadow.MethodCall : Shadow.ConstructorCall;
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public static Shadow makeStaticInitializationShadow(World inWorld, Class forType, MatchingContext withContext) {
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createStaticInitMember(forType, inWorld);
        ResolvedType enclosingType = signature.getDeclaringType().resolve(inWorld);
        Shadow.Kind kind = Shadow.StaticInitialization;
        return new ReflectionShadow(inWorld, kind, signature, null, enclosingType, null, withContext);
    }

    public static Shadow makePreInitializationShadow(World inWorld, Constructor forConstructor, MatchingContext withContext) {
        Shadow.Kind kind = Shadow.PreInitialization;
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(forConstructor, inWorld);
        ResolvedType enclosingType = signature.getDeclaringType().resolve(inWorld);
        return new ReflectionShadow(inWorld, kind, signature, null, enclosingType, null, withContext);
    }

    public static Shadow makeInitializationShadow(World inWorld, Constructor forConstructor, MatchingContext withContext) {
        Shadow.Kind kind = Shadow.Initialization;
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(forConstructor, inWorld);
        ResolvedType enclosingType = signature.getDeclaringType().resolve(inWorld);
        return new ReflectionShadow(inWorld, kind, signature, null, enclosingType, null, withContext);
    }

    public static Shadow makeHandlerShadow(World inWorld, Class exceptionType, Class withinType, MatchingContext withContext) {
        Shadow.Kind kind = Shadow.ExceptionHandler;
        Shadow enclosingShadow = ReflectionShadow.makeStaticInitializationShadow(inWorld, withinType, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createHandlerMember(exceptionType, withinType, inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createStaticInitMember(withinType, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public static Shadow makeHandlerShadow(World inWorld, Class exceptionType, java.lang.reflect.Member withinCode, MatchingContext withContext) {
        Shadow.Kind kind = Shadow.ExceptionHandler;
        Shadow enclosingShadow = ReflectionShadow.makeExecutionShadow(inWorld, withinCode, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createHandlerMember(exceptionType, withinCode.getDeclaringClass(), inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(withinCode, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public static Shadow makeFieldGetShadow(World inWorld, Field forField, Class callerType, MatchingContext withContext) {
        Shadow enclosingShadow = ReflectionShadow.makeStaticInitializationShadow(inWorld, callerType, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedField(forField, inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createStaticInitMember(callerType, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        Shadow.Kind kind = Shadow.FieldGet;
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public static Shadow makeFieldGetShadow(World inWorld, Field forField, java.lang.reflect.Member inMember, MatchingContext withContext) {
        Shadow enclosingShadow = ReflectionShadow.makeExecutionShadow(inWorld, inMember, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedField(forField, inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(inMember, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        Shadow.Kind kind = Shadow.FieldGet;
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public static Shadow makeFieldSetShadow(World inWorld, Field forField, Class callerType, MatchingContext withContext) {
        Shadow enclosingShadow = ReflectionShadow.makeStaticInitializationShadow(inWorld, callerType, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedField(forField, inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createStaticInitMember(callerType, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        Shadow.Kind kind = Shadow.FieldSet;
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public static Shadow makeFieldSetShadow(World inWorld, Field forField, java.lang.reflect.Member inMember, MatchingContext withContext) {
        Shadow enclosingShadow = ReflectionShadow.makeExecutionShadow(inWorld, inMember, withContext);
        ResolvedMember signature = ReflectionBasedReferenceTypeDelegateFactory.createResolvedField(forField, inWorld);
        ResolvedMember enclosingMember = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(inMember, inWorld);
        ResolvedType enclosingType = enclosingMember.getDeclaringType().resolve(inWorld);
        Shadow.Kind kind = Shadow.FieldSet;
        return new ReflectionShadow(inWorld, kind, signature, enclosingShadow, enclosingType, enclosingMember, withContext);
    }

    public ReflectionShadow(World world, Shadow.Kind kind, Member signature, Shadow enclosingShadow, ResolvedType enclosingType, ResolvedMember enclosingMember, MatchingContext withContext) {
        super(kind, signature, enclosingShadow);
        this.world = world;
        this.enclosingType = enclosingType;
        this.enclosingMember = enclosingMember;
        this.matchContext = withContext;
        if (world instanceof IReflectionWorld) {
            this.annotationFinder = ((IReflectionWorld)((Object)world)).getAnnotationFinder();
        }
    }

    @Override
    public World getIWorld() {
        return this.world;
    }

    @Override
    public Var getThisVar() {
        if (this.thisVar == null && this.hasThis()) {
            this.thisVar = ReflectionVar.createThisVar(this.getThisType().resolve(this.world), this.annotationFinder);
        }
        return this.thisVar;
    }

    @Override
    public Var getTargetVar() {
        if (this.targetVar == null && this.hasTarget()) {
            this.targetVar = ReflectionVar.createTargetVar(this.getThisType().resolve(this.world), this.annotationFinder);
        }
        return this.targetVar;
    }

    @Override
    public UnresolvedType getEnclosingType() {
        return this.enclosingType;
    }

    @Override
    public Var getArgVar(int i) {
        if (this.argsVars == null) {
            this.argsVars = new Var[this.getArgCount()];
            for (int j = 0; j < this.argsVars.length; ++j) {
                this.argsVars[j] = ReflectionVar.createArgsVar(this.getArgType(j).resolve(this.world), j, this.annotationFinder);
            }
        }
        if (i < this.argsVars.length) {
            return this.argsVars[i];
        }
        return null;
    }

    @Override
    public Var getThisJoinPointVar() {
        return null;
    }

    @Override
    public Var getThisJoinPointStaticPartVar() {
        return null;
    }

    @Override
    public Var getThisEnclosingJoinPointStaticPartVar() {
        return null;
    }

    @Override
    public Var getThisAspectInstanceVar(ResolvedType aspectType) {
        return null;
    }

    @Override
    public Var getKindedAnnotationVar(UnresolvedType forAnnotationType) {
        ResolvedType annType = forAnnotationType.resolve(this.world);
        if (this.annotationVar.get(annType) == null) {
            ReflectionVar v = ReflectionVar.createAtAnnotationVar(annType, this.annotationFinder);
            this.annotationVar.put(annType, v);
        }
        return (Var)this.annotationVar.get(annType);
    }

    @Override
    public Var getWithinAnnotationVar(UnresolvedType forAnnotationType) {
        ResolvedType annType = forAnnotationType.resolve(this.world);
        if (this.withinAnnotationVar.get(annType) == null) {
            ReflectionVar v = ReflectionVar.createWithinAnnotationVar(annType, this.annotationFinder);
            this.withinAnnotationVar.put(annType, v);
        }
        return (Var)this.withinAnnotationVar.get(annType);
    }

    @Override
    public Var getWithinCodeAnnotationVar(UnresolvedType forAnnotationType) {
        ResolvedType annType = forAnnotationType.resolve(this.world);
        if (this.withinCodeAnnotationVar.get(annType) == null) {
            ReflectionVar v = ReflectionVar.createWithinCodeAnnotationVar(annType, this.annotationFinder);
            this.withinCodeAnnotationVar.put(annType, v);
        }
        return (Var)this.withinCodeAnnotationVar.get(annType);
    }

    @Override
    public Var getThisAnnotationVar(UnresolvedType forAnnotationType) {
        if (this.atThisVar == null) {
            this.atThisVar = ReflectionVar.createThisAnnotationVar(forAnnotationType.resolve(this.world), this.annotationFinder);
        }
        return this.atThisVar;
    }

    @Override
    public Var getTargetAnnotationVar(UnresolvedType forAnnotationType) {
        if (this.atTargetVar == null) {
            this.atTargetVar = ReflectionVar.createTargetAnnotationVar(forAnnotationType.resolve(this.world), this.annotationFinder);
        }
        return this.atTargetVar;
    }

    @Override
    public Var getArgAnnotationVar(int i, UnresolvedType forAnnotationType) {
        Var[] vars;
        ResolvedType annType = forAnnotationType.resolve(this.world);
        if (this.atArgsVars.get(annType) == null) {
            vars = new Var[this.getArgCount()];
            this.atArgsVars.put(annType, vars);
        }
        if (i > (vars = (Var[])this.atArgsVars.get(annType)).length - 1) {
            return null;
        }
        if (vars[i] == null) {
            vars[i] = ReflectionVar.createArgsAnnotationVar(annType, i, this.annotationFinder);
        }
        return vars[i];
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
            return this.enclosingMember;
        }
        return this.enclosingShadow.getSignature();
    }

    @Override
    public ISourceLocation getSourceLocation() {
        return null;
    }

    public MatchingContext getMatchingContext() {
        return this.matchContext;
    }
}


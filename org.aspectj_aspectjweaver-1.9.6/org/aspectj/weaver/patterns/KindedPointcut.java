/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Checker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.AnyWithAnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.TypePattern;

public class KindedPointcut
extends Pointcut {
    Shadow.Kind kind;
    private SignaturePattern signature;
    private int matchKinds;
    private ShadowMunger munger = null;

    public KindedPointcut(Shadow.Kind kind, SignaturePattern signature) {
        this.kind = kind;
        this.signature = signature;
        this.pointcutKind = 1;
        this.matchKinds = kind.bit;
    }

    public KindedPointcut(Shadow.Kind kind, SignaturePattern signature, ShadowMunger munger) {
        this(kind, signature);
        this.munger = munger;
    }

    public SignaturePattern getSignature() {
        return this.signature;
    }

    @Override
    public int couldMatchKinds() {
        return this.matchKinds;
    }

    public boolean couldEverMatchSameJoinPointsAs(KindedPointcut other) {
        if (this.kind != other.kind) {
            return false;
        }
        String myName = this.signature.getName().maybeGetSimpleName();
        String yourName = other.signature.getName().maybeGetSimpleName();
        if (myName != null && yourName != null && !myName.equals(yourName)) {
            return false;
        }
        return this.signature.getParameterTypes().ellipsisCount != 0 || other.signature.getParameterTypes().ellipsisCount != 0 || this.signature.getParameterTypes().getTypePatterns().length == other.signature.getParameterTypes().getTypePatterns().length;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        if (info.getKind() != null && info.getKind() != this.kind) {
            return FuzzyBoolean.NO;
        }
        if (info.world.optimizedMatching && (this.kind == Shadow.MethodExecution || this.kind == Shadow.Initialization) && info.getKind() == null) {
            boolean fastMatchingOnAspect = info.getType().isAspect();
            if (fastMatchingOnAspect) {
                return FuzzyBoolean.MAYBE;
            }
            if (this.getSignature().isExactDeclaringTypePattern()) {
                ExactTypePattern typePattern = (ExactTypePattern)this.getSignature().getDeclaringType();
                ResolvedType patternExactType = typePattern.getResolvedExactType(info.world);
                if (patternExactType.isInterface()) {
                    ResolvedType curr = info.getType();
                    Iterator<ResolvedType> hierarchyWalker = curr.getHierarchy(true, true);
                    boolean found = false;
                    while (hierarchyWalker.hasNext()) {
                        curr = hierarchyWalker.next();
                        if (!typePattern.matchesStatically(curr)) continue;
                        found = true;
                        break;
                    }
                    if (!found) {
                        return FuzzyBoolean.NO;
                    }
                } else if (patternExactType.isClass()) {
                    ResolvedType curr = info.getType();
                    while (!typePattern.matchesStatically(curr) && (curr = curr.getSuperclass()) != null) {
                    }
                    if (curr == null) {
                        return FuzzyBoolean.NO;
                    }
                }
            } else if (this.getSignature().getDeclaringType() instanceof AnyWithAnnotationTypePattern) {
                ExactAnnotationTypePattern exactAnnotationTypePattern;
                ResolvedType type = info.getType();
                AnnotationTypePattern annotationTypePattern = ((AnyWithAnnotationTypePattern)this.getSignature().getDeclaringType()).getAnnotationPattern();
                if (annotationTypePattern instanceof ExactAnnotationTypePattern && ((exactAnnotationTypePattern = (ExactAnnotationTypePattern)annotationTypePattern).getAnnotationValues() == null || exactAnnotationTypePattern.getAnnotationValues().size() == 0)) {
                    ResolvedType annotationType = exactAnnotationTypePattern.getAnnotationType().resolve(info.world);
                    if (type.hasAnnotation(annotationType)) {
                        return FuzzyBoolean.MAYBE;
                    }
                    if (annotationType.isInheritedAnnotation()) {
                        boolean found = false;
                        for (ResolvedType toMatchAgainst = type.getSuperclass(); toMatchAgainst != null; toMatchAgainst = toMatchAgainst.getSuperclass()) {
                            if (!toMatchAgainst.hasAnnotation(annotationType)) continue;
                            found = true;
                            break;
                        }
                        if (!found) {
                            return FuzzyBoolean.NO;
                        }
                    } else {
                        return FuzzyBoolean.NO;
                    }
                }
            }
        }
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        if (shadow.getKind() != this.kind) {
            return FuzzyBoolean.NO;
        }
        if (shadow.getKind() == Shadow.SynchronizationLock && this.kind == Shadow.SynchronizationLock) {
            return FuzzyBoolean.YES;
        }
        if (shadow.getKind() == Shadow.SynchronizationUnlock && this.kind == Shadow.SynchronizationUnlock) {
            return FuzzyBoolean.YES;
        }
        if (!this.signature.matches(shadow.getMatchingSignature(), shadow.getIWorld(), this.kind == Shadow.MethodCall)) {
            if (this.kind == Shadow.MethodCall) {
                this.warnOnConfusingSig(shadow);
            }
            return FuzzyBoolean.NO;
        }
        return FuzzyBoolean.YES;
    }

    private void warnOnConfusingSig(Shadow shadow) {
        if (!shadow.getIWorld().getLint().unmatchedSuperTypeInCall.isEnabled()) {
            return;
        }
        if (this.munger instanceof Checker) {
            return;
        }
        World world = shadow.getIWorld();
        UnresolvedType exactDeclaringType = this.signature.getDeclaringType().getExactType();
        ResolvedType shadowDeclaringType = shadow.getSignature().getDeclaringType().resolve(world);
        if (this.signature.getDeclaringType().isStar() || ResolvedType.isMissing(exactDeclaringType) || exactDeclaringType.resolve(world).isMissing()) {
            return;
        }
        if (!shadowDeclaringType.isAssignableFrom(exactDeclaringType.resolve(world))) {
            return;
        }
        ResolvedMember rm = shadow.getSignature().resolve(world);
        if (rm == null) {
            return;
        }
        int shadowModifiers = rm.getModifiers();
        if (!ResolvedType.isVisible(shadowModifiers, shadowDeclaringType, exactDeclaringType.resolve(world))) {
            return;
        }
        if (!this.signature.getReturnType().matchesStatically(shadow.getSignature().getReturnType().resolve(world))) {
            return;
        }
        if (exactDeclaringType.resolve(world).isInterface() && shadowDeclaringType.equals(world.resolve("java.lang.Object"))) {
            return;
        }
        SignaturePattern nonConfusingPattern = new SignaturePattern(this.signature.getKind(), this.signature.getModifiers(), this.signature.getReturnType(), TypePattern.ANY, this.signature.getName(), this.signature.getParameterTypes(), this.signature.getThrowsPattern(), this.signature.getAnnotationPattern());
        if (nonConfusingPattern.matches(shadow.getSignature(), shadow.getIWorld(), true)) {
            shadow.getIWorld().getLint().unmatchedSuperTypeInCall.signal(new String[]{shadow.getSignature().getDeclaringType().toString(), this.signature.getDeclaringType().toString()}, this.getSourceLocation(), new ISourceLocation[]{shadow.getSourceLocation()});
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof KindedPointcut)) {
            return false;
        }
        KindedPointcut o = (KindedPointcut)other;
        return o.kind == this.kind && o.signature.equals(this.signature);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.kind.hashCode();
        result = 37 * result + this.signature.hashCode();
        return result;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.kind.getSimpleName());
        buf.append("(");
        buf.append(this.signature.toString());
        buf.append(")");
        return buf.toString();
    }

    @Override
    public void postRead(ResolvedType enclosingType) {
        this.signature.postRead(enclosingType);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(1);
        this.kind.write(s);
        this.signature.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        Shadow.Kind kind = Shadow.Kind.read(s);
        SignaturePattern sig = SignaturePattern.read(s, context);
        KindedPointcut ret = new KindedPointcut(kind, sig);
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor visitor;
        if (this.kind == Shadow.Initialization) {
            // empty if block
        }
        this.signature = this.signature.resolveBindings(scope, bindings);
        if (this.kind == Shadow.ConstructorExecution && this.signature.getDeclaringType() != null) {
            World world = scope.getWorld();
            UnresolvedType exactType = this.signature.getDeclaringType().getExactType();
            if (this.signature.getKind() == Member.CONSTRUCTOR && !ResolvedType.isMissing(exactType) && exactType.resolve(world).isInterface() && !this.signature.getDeclaringType().isIncludeSubtypes()) {
                world.getLint().noInterfaceCtorJoinpoint.signal(exactType.toString(), this.getSourceLocation());
            }
        }
        if (this.kind == Shadow.StaticInitialization) {
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getDeclaringType().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noStaticInitJPsForParameterizedTypes"), this.getSourceLocation()));
            }
        }
        if (this.kind == Shadow.FieldGet || this.kind == Shadow.FieldSet) {
            UnresolvedType returnType;
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getDeclaringType().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noParameterizedTypesInGetAndSet"), this.getSourceLocation()));
            }
            if ((returnType = this.signature.getReturnType().getExactType()).equals(UnresolvedType.VOID)) {
                scope.message(MessageUtil.error(WeaverMessages.format("fieldCantBeVoid"), this.getSourceLocation()));
            }
        }
        if (this.kind == Shadow.Initialization || this.kind == Shadow.PreInitialization) {
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getDeclaringType().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noInitJPsForParameterizedTypes"), this.getSourceLocation()));
            }
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getThrowsPattern().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noGenericThrowables"), this.getSourceLocation()));
            }
        }
        if (this.kind == Shadow.MethodExecution || this.kind == Shadow.ConstructorExecution) {
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getDeclaringType().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noParameterizedDeclaringTypesInExecution"), this.getSourceLocation()));
            }
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getThrowsPattern().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noGenericThrowables"), this.getSourceLocation()));
            }
        }
        if (this.kind == Shadow.MethodCall || this.kind == Shadow.ConstructorCall) {
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getDeclaringType().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noParameterizedDeclaringTypesInCall"), this.getSourceLocation()));
            }
            visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
            this.signature.getThrowsPattern().traverse(visitor, null);
            if (visitor.wellHasItThen()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noGenericThrowables"), this.getSourceLocation()));
            }
            if (!scope.getWorld().isJoinpointArrayConstructionEnabled() && this.kind == Shadow.ConstructorCall && this.signature.getDeclaringType().isArray()) {
                scope.message(MessageUtil.warn(WeaverMessages.format("noNewArrayJoinpointsByDefault"), this.getSourceLocation()));
            }
        }
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        return this.match(shadow).alwaysTrue() ? Literal.TRUE : Literal.FALSE;
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        KindedPointcut ret = new KindedPointcut(this.kind, this.signature, bindings.getEnclosingAdvice());
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        KindedPointcut ret = new KindedPointcut(this.kind, (SignaturePattern)this.signature.parameterizeWith((Map)typeVariableMap, w), this.munger);
        ret.copyLocationFrom(this);
        return ret;
    }

    public Shadow.Kind getKind() {
        return this.kind;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}


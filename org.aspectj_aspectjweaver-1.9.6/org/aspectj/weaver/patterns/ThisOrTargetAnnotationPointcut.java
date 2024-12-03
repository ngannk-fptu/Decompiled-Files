/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingAnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.NameBindingPointcut;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;

public class ThisOrTargetAnnotationPointcut
extends NameBindingPointcut {
    private boolean isThis;
    private boolean alreadyWarnedAboutDEoW = false;
    private ExactAnnotationTypePattern annotationTypePattern;
    private String declarationText;
    private static final int thisKindSet;
    private static final int targetKindSet;

    public ThisOrTargetAnnotationPointcut(boolean isThis, ExactAnnotationTypePattern type) {
        this.isThis = isThis;
        this.annotationTypePattern = type;
        this.pointcutKind = (byte)19;
        this.buildDeclarationText();
    }

    public ThisOrTargetAnnotationPointcut(boolean isThis, ExactAnnotationTypePattern type, ShadowMunger munger) {
        this(isThis, type);
    }

    public ExactAnnotationTypePattern getAnnotationTypePattern() {
        return this.annotationTypePattern;
    }

    @Override
    public int couldMatchKinds() {
        return this.isThis ? thisKindSet : targetKindSet;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        ExactAnnotationTypePattern newPattern = (ExactAnnotationTypePattern)this.annotationTypePattern.parameterizeWith(typeVariableMap, w);
        if (newPattern.getAnnotationType() instanceof ResolvedType) {
            this.verifyRuntimeRetention(newPattern.getResolvedAnnotationType());
        }
        ThisOrTargetAnnotationPointcut ret = new ThisOrTargetAnnotationPointcut(this.isThis, (ExactAnnotationTypePattern)this.annotationTypePattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        if (!this.couldMatch(shadow)) {
            return FuzzyBoolean.NO;
        }
        ResolvedType toMatchAgainst = (this.isThis ? shadow.getThisType() : shadow.getTargetType()).resolve(shadow.getIWorld());
        this.annotationTypePattern.resolve(shadow.getIWorld());
        if (this.annotationTypePattern.matchesRuntimeType(toMatchAgainst).alwaysTrue()) {
            return FuzzyBoolean.YES;
        }
        return FuzzyBoolean.MAYBE;
    }

    public boolean isThis() {
        return this.isThis;
    }

    @Override
    protected void resolveBindings(IScope scope, Bindings bindings) {
        if (!scope.getWorld().isInJava5Mode()) {
            scope.message(MessageUtil.error(WeaverMessages.format(this.isThis ? "atthisNeedsJava5" : "attargetNeedsJava5"), this.getSourceLocation()));
            return;
        }
        this.annotationTypePattern = (ExactAnnotationTypePattern)this.annotationTypePattern.resolveBindings(scope, bindings, true);
        if (this.annotationTypePattern.annotationType == null) {
            return;
        }
        ResolvedType rAnnotationType = (ResolvedType)this.annotationTypePattern.annotationType;
        if (rAnnotationType.isTypeVariableReference()) {
            return;
        }
        this.verifyRuntimeRetention(rAnnotationType);
    }

    private void verifyRuntimeRetention(ResolvedType rAnnotationType) {
        if (!rAnnotationType.isAnnotationWithRuntimeRetention()) {
            IMessage m = MessageUtil.error(WeaverMessages.format("bindingNonRuntimeRetentionAnnotation", rAnnotationType.getName()), this.getSourceLocation());
            rAnnotationType.getWorld().getMessageHandler().handleMessage(m);
        }
    }

    @Override
    protected Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        if (this.isDeclare(bindings.getEnclosingAdvice())) {
            if (!this.alreadyWarnedAboutDEoW) {
                inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("thisOrTargetInDeclare", this.isThis ? "this" : "target"), bindings.getEnclosingAdvice().getSourceLocation(), null);
                this.alreadyWarnedAboutDEoW = true;
            }
            return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
        }
        ExactAnnotationTypePattern newType = (ExactAnnotationTypePattern)this.annotationTypePattern.remapAdviceFormals(bindings);
        ThisOrTargetAnnotationPointcut ret = new ThisOrTargetAnnotationPointcut(this.isThis, newType, bindings.getEnclosingAdvice());
        ret.alreadyWarnedAboutDEoW = this.alreadyWarnedAboutDEoW;
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (!this.couldMatch(shadow)) {
            return Literal.FALSE;
        }
        boolean alwaysMatches = this.match(shadow).alwaysTrue();
        Var var = this.isThis ? shadow.getThisVar() : shadow.getTargetVar();
        Var annVar = null;
        UnresolvedType annotationType = this.annotationTypePattern.annotationType;
        if (this.annotationTypePattern instanceof BindingAnnotationTypePattern) {
            BindingAnnotationTypePattern btp = (BindingAnnotationTypePattern)this.annotationTypePattern;
            annotationType = btp.annotationType;
            Var var2 = annVar = this.isThis ? shadow.getThisAnnotationVar(annotationType) : shadow.getTargetAnnotationVar(annotationType);
            if (annVar == null) {
                throw new RuntimeException("Impossible!");
            }
            state.set(btp.getFormalIndex(), annVar);
        }
        if (alwaysMatches && annVar == null) {
            return Literal.TRUE;
        }
        ResolvedType rType = annotationType.resolve(shadow.getIWorld());
        return Test.makeHasAnnotation(var, rType);
    }

    private boolean couldMatch(Shadow shadow) {
        return this.isThis ? shadow.hasThis() : shadow.hasTarget();
    }

    @Override
    public List<BindingPattern> getBindingAnnotationTypePatterns() {
        if (this.annotationTypePattern instanceof BindingAnnotationTypePattern) {
            ArrayList<BindingPattern> l = new ArrayList<BindingPattern>();
            l.add((BindingPattern)((Object)this.annotationTypePattern));
            return l;
        }
        return Collections.emptyList();
    }

    @Override
    public List<BindingTypePattern> getBindingTypePatterns() {
        return Collections.emptyList();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(19);
        s.writeBoolean(this.isThis);
        this.annotationTypePattern.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        boolean isThis = s.readBoolean();
        AnnotationTypePattern type = AnnotationTypePattern.read(s, context);
        ThisOrTargetAnnotationPointcut ret = new ThisOrTargetAnnotationPointcut(isThis, (ExactAnnotationTypePattern)type);
        ret.readLocation(context, s);
        return ret;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ThisOrTargetAnnotationPointcut)) {
            return false;
        }
        ThisOrTargetAnnotationPointcut other = (ThisOrTargetAnnotationPointcut)obj;
        return other.annotationTypePattern.equals(this.annotationTypePattern) && other.isThis == this.isThis;
    }

    public int hashCode() {
        return 17 + 37 * this.annotationTypePattern.hashCode() + (this.isThis ? 49 : 13);
    }

    private void buildDeclarationText() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.isThis ? "@this(" : "@target(");
        String annPatt = this.annotationTypePattern.toString();
        buf.append(annPatt.startsWith("@") ? annPatt.substring(1) : annPatt);
        buf.append(")");
        this.declarationText = buf.toString();
    }

    public String toString() {
        return this.declarationText;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
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


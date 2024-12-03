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
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.NameBindingPointcut;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;

public class ThisOrTargetPointcut
extends NameBindingPointcut {
    private boolean isThis;
    private TypePattern typePattern;
    private String declarationText;
    private static final int thisKindSet;
    private static final int targetKindSet;

    public boolean isBinding() {
        return this.typePattern instanceof BindingTypePattern;
    }

    public ThisOrTargetPointcut(boolean isThis, TypePattern type) {
        this.isThis = isThis;
        this.typePattern = type;
        this.pointcutKind = (byte)3;
        this.declarationText = (isThis ? "this(" : "target(") + type + ")";
    }

    public TypePattern getType() {
        return this.typePattern;
    }

    public boolean isThis() {
        return this.isThis;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        ThisOrTargetPointcut ret = new ThisOrTargetPointcut(this.isThis, this.typePattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public int couldMatchKinds() {
        return this.isThis ? thisKindSet : targetKindSet;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return FuzzyBoolean.MAYBE;
    }

    private boolean couldMatch(Shadow shadow) {
        return this.isThis ? shadow.hasThis() : shadow.hasTarget();
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        UnresolvedType typeToMatch;
        if (!this.couldMatch(shadow)) {
            return FuzzyBoolean.NO;
        }
        UnresolvedType unresolvedType = typeToMatch = this.isThis ? shadow.getThisType() : shadow.getTargetType();
        if (this.typePattern.getExactType().equals(ResolvedType.OBJECT)) {
            return FuzzyBoolean.YES;
        }
        return this.typePattern.matches(typeToMatch.resolve(shadow.getIWorld()), TypePattern.DYNAMIC);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(3);
        s.writeBoolean(this.isThis);
        this.typePattern.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        boolean isThis = s.readBoolean();
        TypePattern type = TypePattern.read(s, context);
        ThisOrTargetPointcut ret = new ThisOrTargetPointcut(isThis, type);
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.typePattern = this.typePattern.resolveBindings(scope, bindings, true, true);
        HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
        this.typePattern.traverse(visitor, null);
        if (visitor.wellHasItThen()) {
            scope.message(MessageUtil.error(WeaverMessages.format("noParameterizedTypesInThisAndTarget"), this.getSourceLocation()));
        }
    }

    @Override
    public void postRead(ResolvedType enclosingType) {
        this.typePattern.postRead(enclosingType);
    }

    @Override
    public List<BindingPattern> getBindingAnnotationTypePatterns() {
        return Collections.emptyList();
    }

    @Override
    public List<BindingTypePattern> getBindingTypePatterns() {
        if (this.typePattern instanceof BindingTypePattern) {
            ArrayList<BindingTypePattern> l = new ArrayList<BindingTypePattern>();
            l.add((BindingTypePattern)this.typePattern);
            return l;
        }
        return Collections.emptyList();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ThisOrTargetPointcut)) {
            return false;
        }
        ThisOrTargetPointcut o = (ThisOrTargetPointcut)other;
        return o.isThis == this.isThis && o.typePattern.equals(this.typePattern);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.isThis ? 0 : 1);
        result = 37 * result + this.typePattern.hashCode();
        return result;
    }

    public String toString() {
        return this.declarationText;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (!this.couldMatch(shadow)) {
            return Literal.FALSE;
        }
        if (this.typePattern == TypePattern.ANY) {
            return Literal.TRUE;
        }
        Var var = this.isThis ? shadow.getThisVar() : shadow.getTargetVar();
        return this.exposeStateForVar(var, this.typePattern, state, shadow.getIWorld());
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        if (this.isDeclare(bindings.getEnclosingAdvice())) {
            inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("thisOrTargetInDeclare", this.isThis ? "this" : "target"), bindings.getEnclosingAdvice().getSourceLocation(), null);
            return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
        }
        TypePattern newType = this.typePattern.remapAdviceFormals(bindings);
        if (inAspect.crosscuttingMembers != null) {
            inAspect.crosscuttingMembers.exposeType(newType.getExactType());
        }
        ThisOrTargetPointcut ret = new ThisOrTargetPointcut(this.isThis, newType);
        ret.copyLocationFrom(this);
        return ret;
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


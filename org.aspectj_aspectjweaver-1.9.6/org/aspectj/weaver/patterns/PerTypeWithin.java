/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.PerTypeWithinTargetTypeMunger;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.CallExpr;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.AnyAnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.ModifiersPattern;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.ThrowsPattern;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;
import org.aspectj.weaver.patterns.WithinPointcut;

public class PerTypeWithin
extends PerClause {
    private TypePattern typePattern;
    private static final int kindSet = Shadow.ALL_SHADOW_KINDS_BITS;

    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    public PerTypeWithin(TypePattern p) {
        this.typePattern = p;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public int couldMatchKinds() {
        return kindSet;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        PerTypeWithin ret = new PerTypeWithin(this.typePattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        if (this.typePattern.annotationPattern instanceof AnyAnnotationTypePattern) {
            return this.isWithinType(info.getType());
        }
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        ResolvedType enclosingType = shadow.getIWorld().resolve(shadow.getEnclosingType(), true);
        if (enclosingType.isMissing()) {
            Message msg = new Message("Cant find type pertypewithin matching...", shadow.getSourceLocation(), true, new ISourceLocation[]{this.getSourceLocation()});
            shadow.getIWorld().getMessageHandler().handleMessage(msg);
        }
        if (enclosingType.isInterface()) {
            return FuzzyBoolean.NO;
        }
        if (!enclosingType.canBeSeenBy(this.inAspect) && !this.inAspect.isPrivilegedAspect()) {
            return FuzzyBoolean.NO;
        }
        this.typePattern.resolve(shadow.getIWorld());
        return this.isWithinType(enclosingType);
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.typePattern = this.typePattern.resolveBindings(scope, bindings, false, false);
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        CallExpr myInstance = Expr.makeCallExpr(AjcMemberMaker.perTypeWithinLocalAspectOf(shadow.getEnclosingType(), this.inAspect), Expr.NONE, this.inAspect);
        state.setAspectInstance(myInstance);
        return this.match(shadow).alwaysTrue() ? Literal.TRUE : Literal.FALSE;
    }

    @Override
    public PerClause concretize(ResolvedType inAspect) {
        PerTypeWithin ret = new PerTypeWithin(this.typePattern);
        ret.copyLocationFrom(this);
        ret.inAspect = inAspect;
        if (inAspect.isAbstract()) {
            return ret;
        }
        World world = inAspect.getWorld();
        SignaturePattern sigpat = new SignaturePattern(Member.STATIC_INITIALIZATION, ModifiersPattern.ANY, TypePattern.ANY, TypePattern.ANY, NamePattern.ANY, TypePatternList.ANY, ThrowsPattern.ANY, AnnotationTypePattern.ANY);
        KindedPointcut staticInitStar = new KindedPointcut(Shadow.StaticInitialization, sigpat);
        WithinPointcut withinTp = new WithinPointcut(this.typePattern);
        AndPointcut andPcut = new AndPointcut(staticInitStar, withinTp);
        inAspect.crosscuttingMembers.addConcreteShadowMunger(Advice.makePerTypeWithinEntry(world, andPcut, inAspect));
        PerTypeWithinTargetTypeMunger munger = new PerTypeWithinTargetTypeMunger(inAspect, ret);
        inAspect.crosscuttingMembers.addTypeMunger(world.getWeavingSupport().concreteTypeMunger(munger, inAspect));
        if (inAspect.isAnnotationStyleAspect() && !inAspect.isAbstract()) {
            inAspect.crosscuttingMembers.addLateTypeMunger(world.getWeavingSupport().makePerClauseAspect(inAspect, this.getKind()));
        }
        if (inAspect.isAnnotationStyleAspect() && !world.isXnoInline()) {
            inAspect.crosscuttingMembers.addTypeMunger(world.getWeavingSupport().createAccessForInlineMunger(inAspect));
        }
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        PERTYPEWITHIN.write(s);
        this.typePattern.write(s);
        this.writeLocation(s);
    }

    public static PerClause readPerClause(VersionedDataInputStream s, ISourceContext context) throws IOException {
        PerTypeWithin ret = new PerTypeWithin(TypePattern.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public PerClause.Kind getKind() {
        return PERTYPEWITHIN;
    }

    public String toString() {
        return "pertypewithin(" + this.typePattern + ")";
    }

    @Override
    public String toDeclarationString() {
        return this.toString();
    }

    private FuzzyBoolean isWithinType(ResolvedType type) {
        while (type != null) {
            if (this.typePattern.matchesStatically(type)) {
                return FuzzyBoolean.YES;
            }
            type = type.getDeclaringType();
        }
        return FuzzyBoolean.NO;
    }

    public boolean equals(Object other) {
        if (!(other instanceof PerTypeWithin)) {
            return false;
        }
        PerTypeWithin pc = (PerTypeWithin)other;
        return (pc.inAspect == null ? this.inAspect == null : pc.inAspect.equals(this.inAspect)) && (pc.typePattern == null ? this.typePattern == null : pc.typePattern.equals(this.typePattern));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.inAspect == null ? 0 : this.inAspect.hashCode());
        result = 37 * result + (this.typePattern == null ? 0 : this.typePattern.hashCode());
        return result;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
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
import org.aspectj.weaver.patterns.AnyAnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;

public class WithinPointcut
extends Pointcut {
    private TypePattern typePattern;

    public WithinPointcut(TypePattern type) {
        this.typePattern = type;
        this.pointcutKind = (byte)2;
    }

    public TypePattern getTypePattern() {
        return this.typePattern;
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

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        WithinPointcut ret = new WithinPointcut(this.typePattern.parameterizeWith(typeVariableMap, w));
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
            shadow.getIWorld().getLint().cantFindType.signal(new String[]{WeaverMessages.format("cantFindTypeWithinpcd", shadow.getEnclosingType().getName())}, shadow.getSourceLocation(), new ISourceLocation[]{this.getSourceLocation()});
        }
        this.typePattern.resolve(shadow.getIWorld());
        return this.isWithinType(enclosingType);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(2);
        this.typePattern.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        TypePattern type = TypePattern.read(s, context);
        WithinPointcut ret = new WithinPointcut(type);
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.typePattern = this.typePattern.resolveBindings(scope, bindings, false, false);
        HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
        this.typePattern.traverse(visitor, null);
        if (visitor.wellHasItThen()) {
            scope.message(MessageUtil.error(WeaverMessages.format("noParameterizedTypePatternInWithin"), this.getSourceLocation()));
        }
    }

    @Override
    public void postRead(ResolvedType enclosingType) {
        this.typePattern.postRead(enclosingType);
    }

    public boolean couldEverMatchSameJoinPointsAs(WithinPointcut other) {
        return this.typePattern.couldEverMatchSameTypesAs(other.typePattern);
    }

    public boolean equals(Object other) {
        if (!(other instanceof WithinPointcut)) {
            return false;
        }
        WithinPointcut o = (WithinPointcut)other;
        return o.typePattern.equals(this.typePattern);
    }

    public int hashCode() {
        return this.typePattern.hashCode();
    }

    public String toString() {
        return "within(" + this.typePattern + ")";
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        return this.match(shadow).alwaysTrue() ? Literal.TRUE : Literal.FALSE;
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        WithinPointcut ret = new WithinPointcut(this.typePattern);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}


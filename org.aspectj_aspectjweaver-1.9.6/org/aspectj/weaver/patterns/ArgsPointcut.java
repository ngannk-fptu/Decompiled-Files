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
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.BCException;
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
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.NameBindingPointcut;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;

public class ArgsPointcut
extends NameBindingPointcut {
    private static final String ASPECTJ_JP_SIGNATURE_PREFIX = "Lorg/aspectj/lang/JoinPoint";
    private static final String ASPECTJ_SYNTHETIC_SIGNATURE_PREFIX = "Lorg/aspectj/runtime/internal/";
    private TypePatternList arguments;
    private String stringRepresentation;

    public ArgsPointcut(TypePatternList arguments) {
        this.arguments = arguments;
        this.pointcutKind = (byte)4;
        this.stringRepresentation = "args" + arguments.toString() + "";
    }

    public TypePatternList getArguments() {
        return this.arguments;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        ArgsPointcut ret = new ArgsPointcut(this.arguments.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
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
        ResolvedType[] argumentsToMatchAgainst = this.getArgumentsToMatchAgainst(shadow);
        FuzzyBoolean ret = this.arguments.matches(argumentsToMatchAgainst, TypePattern.DYNAMIC);
        return ret;
    }

    private ResolvedType[] getArgumentsToMatchAgainst(Shadow shadow) {
        if (shadow.isShadowForArrayConstructionJoinpoint()) {
            return shadow.getArgumentTypesForArrayConstructionShadow();
        }
        ResolvedType[] argumentsToMatchAgainst = shadow.getIWorld().resolve(shadow.getGenericArgTypes());
        if (shadow.getKind() == Shadow.AdviceExecution) {
            int numExtraArgs = 0;
            for (int i = 0; i < argumentsToMatchAgainst.length; ++i) {
                String argumentSignature = argumentsToMatchAgainst[i].getSignature();
                if (argumentSignature.startsWith(ASPECTJ_JP_SIGNATURE_PREFIX) || argumentSignature.startsWith(ASPECTJ_SYNTHETIC_SIGNATURE_PREFIX)) {
                    ++numExtraArgs;
                    continue;
                }
                numExtraArgs = 0;
            }
            if (numExtraArgs > 0) {
                int newArgLength = argumentsToMatchAgainst.length - numExtraArgs;
                ResolvedType[] argsSubset = new ResolvedType[newArgLength];
                System.arraycopy(argumentsToMatchAgainst, 0, argsSubset, 0, newArgLength);
                argumentsToMatchAgainst = argsSubset;
            }
        } else if (shadow.getKind() == Shadow.ConstructorExecution && shadow.getMatchingSignature().getParameterTypes().length < argumentsToMatchAgainst.length) {
            int newArgLength = shadow.getMatchingSignature().getParameterTypes().length;
            ResolvedType[] argsSubset = new ResolvedType[newArgLength];
            System.arraycopy(argumentsToMatchAgainst, 0, argsSubset, 0, newArgLength);
            argumentsToMatchAgainst = argsSubset;
        }
        return argumentsToMatchAgainst;
    }

    @Override
    public List<BindingPattern> getBindingAnnotationTypePatterns() {
        return Collections.emptyList();
    }

    @Override
    public List<BindingTypePattern> getBindingTypePatterns() {
        ArrayList<BindingTypePattern> l = new ArrayList<BindingTypePattern>();
        TypePattern[] pats = this.arguments.getTypePatterns();
        for (int i = 0; i < pats.length; ++i) {
            if (!(pats[i] instanceof BindingTypePattern)) continue;
            l.add((BindingTypePattern)pats[i]);
        }
        return l;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(4);
        this.arguments.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        ArgsPointcut ret = new ArgsPointcut(TypePatternList.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ArgsPointcut)) {
            return false;
        }
        ArgsPointcut o = (ArgsPointcut)other;
        return o.arguments.equals(this.arguments);
    }

    public int hashCode() {
        return this.arguments.hashCode();
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.arguments.resolveBindings(scope, bindings, true, true);
        if (this.arguments.ellipsisCount > 1) {
            scope.message(IMessage.ERROR, this, "uses more than one .. in args (compiler limitation)");
        }
    }

    @Override
    public void postRead(ResolvedType enclosingType) {
        this.arguments.postRead(enclosingType);
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        if (this.isDeclare(bindings.getEnclosingAdvice())) {
            inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("argsInDeclare"), bindings.getEnclosingAdvice().getSourceLocation(), null);
            return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
        }
        TypePatternList args = this.arguments.resolveReferences(bindings);
        if (inAspect.crosscuttingMembers != null) {
            inAspect.crosscuttingMembers.exposeTypes(args.getExactTypes());
        }
        ArgsPointcut ret = new ArgsPointcut(args);
        ret.copyLocationFrom(this);
        return ret;
    }

    private Test findResidueNoEllipsis(Shadow shadow, ExposedState state, TypePattern[] patterns) {
        ResolvedType[] argumentsToMatchAgainst = this.getArgumentsToMatchAgainst(shadow);
        int len = argumentsToMatchAgainst.length;
        if (patterns.length != len) {
            return Literal.FALSE;
        }
        Test ret = Literal.TRUE;
        for (int i = 0; i < len; ++i) {
            UnresolvedType argType = shadow.getGenericArgTypes()[i];
            TypePattern type = patterns[i];
            ResolvedType argRTX = shadow.getIWorld().resolve(argType, true);
            if (!(type instanceof BindingTypePattern)) {
                if (argRTX.isMissing()) {
                    shadow.getIWorld().getLint().cantFindType.signal(new String[]{WeaverMessages.format("cftArgType", argType.getName())}, shadow.getSourceLocation(), new ISourceLocation[]{this.getSourceLocation()});
                }
                if (type.matchesInstanceof(argRTX).alwaysTrue()) continue;
            }
            World world = shadow.getIWorld();
            ResolvedType typeToExpose = type.getExactType().resolve(world);
            if (typeToExpose.isParameterizedType()) {
                boolean inDoubt;
                boolean bl = inDoubt = type.matchesInstanceof(argRTX) == FuzzyBoolean.MAYBE;
                if (inDoubt && world.getLint().uncheckedArgument.isEnabled()) {
                    String uncheckedMatchWith = typeToExpose.getSimpleBaseName();
                    if (argRTX.isParameterizedType() && argRTX.getRawType() == typeToExpose.getRawType()) {
                        uncheckedMatchWith = argRTX.getSimpleName();
                    }
                    if (!this.isUncheckedArgumentWarningSuppressed()) {
                        world.getLint().uncheckedArgument.signal(new String[]{typeToExpose.getSimpleName(), uncheckedMatchWith, typeToExpose.getSimpleBaseName(), shadow.toResolvedString(world)}, this.getSourceLocation(), new ISourceLocation[]{shadow.getSourceLocation()});
                    }
                }
            }
            ret = Test.makeAnd(ret, this.exposeStateForVar(shadow.getArgVar(i), type, state, shadow.getIWorld()));
        }
        return ret;
    }

    private boolean isUncheckedArgumentWarningSuppressed() {
        return false;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        ResolvedType[] argsToMatch = this.getArgumentsToMatchAgainst(shadow);
        if (this.arguments.matches(argsToMatch, TypePattern.DYNAMIC).alwaysFalse()) {
            return Literal.FALSE;
        }
        int ellipsisCount = this.arguments.ellipsisCount;
        if (ellipsisCount == 0) {
            return this.findResidueNoEllipsis(shadow, state, this.arguments.getTypePatterns());
        }
        if (ellipsisCount == 1) {
            TypePattern[] patternsWithEllipsis = this.arguments.getTypePatterns();
            TypePattern[] patternsWithoutEllipsis = new TypePattern[argsToMatch.length];
            int lenWithEllipsis = patternsWithEllipsis.length;
            int lenWithoutEllipsis = patternsWithoutEllipsis.length;
            int indexWithEllipsis = 0;
            int indexWithoutEllipsis = 0;
            while (indexWithoutEllipsis < lenWithoutEllipsis) {
                TypePattern p;
                if ((p = patternsWithEllipsis[indexWithEllipsis++]) == TypePattern.ELLIPSIS) {
                    int newLenWithoutEllipsis = lenWithoutEllipsis - (lenWithEllipsis - indexWithEllipsis);
                    while (indexWithoutEllipsis < newLenWithoutEllipsis) {
                        patternsWithoutEllipsis[indexWithoutEllipsis++] = TypePattern.ANY;
                    }
                    continue;
                }
                patternsWithoutEllipsis[indexWithoutEllipsis++] = p;
            }
            return this.findResidueNoEllipsis(shadow, state, patternsWithoutEllipsis);
        }
        throw new BCException("unimplemented");
    }

    public String toString() {
        return this.stringRepresentation;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}


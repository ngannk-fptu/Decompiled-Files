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
import org.aspectj.weaver.patterns.AnnotationPatternList;
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

public class ArgsAnnotationPointcut
extends NameBindingPointcut {
    private AnnotationPatternList arguments;
    private String declarationText;

    public ArgsAnnotationPointcut(AnnotationPatternList arguments) {
        this.arguments = arguments;
        this.pointcutKind = (byte)21;
        this.buildDeclarationText();
    }

    public AnnotationPatternList getArguments() {
        return this.arguments;
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    public Pointcut parameterizeWith(Map typeVariableMap, World w) {
        ArgsAnnotationPointcut ret = new ArgsAnnotationPointcut(this.arguments.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        this.arguments.resolve(shadow.getIWorld());
        FuzzyBoolean ret = this.arguments.matches(shadow.getIWorld().resolve(shadow.getArgTypes()));
        return ret;
    }

    @Override
    protected void resolveBindings(IScope scope, Bindings bindings) {
        if (!scope.getWorld().isInJava5Mode()) {
            scope.message(MessageUtil.error(WeaverMessages.format("atargsNeedsJava5"), this.getSourceLocation()));
            return;
        }
        this.arguments.resolveBindings(scope, bindings, true);
        if (this.arguments.ellipsisCount > 1) {
            scope.message(IMessage.ERROR, this, "uses more than one .. in args (compiler limitation)");
        }
    }

    @Override
    protected Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        if (this.isDeclare(bindings.getEnclosingAdvice())) {
            inAspect.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("argsInDeclare"), bindings.getEnclosingAdvice().getSourceLocation(), null);
            return Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
        }
        AnnotationPatternList list = this.arguments.resolveReferences(bindings);
        ArgsAnnotationPointcut ret = new ArgsAnnotationPointcut(list);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        int len = shadow.getArgCount();
        int numArgsMatchedByEllipsis = len + this.arguments.ellipsisCount - this.arguments.size();
        if (numArgsMatchedByEllipsis < 0) {
            return Literal.FALSE;
        }
        if (numArgsMatchedByEllipsis > 0 && this.arguments.ellipsisCount == 0) {
            return Literal.FALSE;
        }
        Test ret = Literal.TRUE;
        int argsIndex = 0;
        for (int i = 0; i < this.arguments.size(); ++i) {
            if (this.arguments.get(i) == AnnotationTypePattern.ELLIPSIS) {
                argsIndex += numArgsMatchedByEllipsis;
                continue;
            }
            if (this.arguments.get(i) == AnnotationTypePattern.ANY) {
                ++argsIndex;
                continue;
            }
            ExactAnnotationTypePattern ap = (ExactAnnotationTypePattern)this.arguments.get(i);
            UnresolvedType argType = shadow.getArgType(argsIndex);
            ResolvedType rArgType = argType.resolve(shadow.getIWorld());
            if (rArgType.isMissing()) {
                shadow.getIWorld().getLint().cantFindType.signal(new String[]{WeaverMessages.format("cftArgType", argType.getName())}, shadow.getSourceLocation(), new ISourceLocation[]{this.getSourceLocation()});
            }
            ResolvedType rAnnType = ap.getAnnotationType().resolve(shadow.getIWorld());
            if (ap instanceof BindingAnnotationTypePattern) {
                BindingAnnotationTypePattern btp = (BindingAnnotationTypePattern)ap;
                Var annvar = shadow.getArgAnnotationVar(argsIndex, rAnnType);
                state.set(btp.getFormalIndex(), annvar);
            }
            if (!ap.matches(rArgType).alwaysTrue()) {
                ret = Test.makeAnd(ret, Test.makeHasAnnotation(shadow.getArgVar(argsIndex), rAnnType));
            }
            ++argsIndex;
        }
        return ret;
    }

    @Override
    public List<BindingPattern> getBindingAnnotationTypePatterns() {
        ArrayList<BindingPattern> l = new ArrayList<BindingPattern>();
        AnnotationTypePattern[] pats = this.arguments.getAnnotationPatterns();
        for (int i = 0; i < pats.length; ++i) {
            if (!(pats[i] instanceof BindingAnnotationTypePattern)) continue;
            l.add((BindingPattern)((Object)pats[i]));
        }
        return l;
    }

    @Override
    public List<BindingTypePattern> getBindingTypePatterns() {
        return Collections.emptyList();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(21);
        this.arguments.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        AnnotationPatternList annotationPatternList = AnnotationPatternList.read(s, context);
        ArgsAnnotationPointcut ret = new ArgsAnnotationPointcut(annotationPatternList);
        ret.readLocation(context, s);
        return ret;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ArgsAnnotationPointcut)) {
            return false;
        }
        ArgsAnnotationPointcut other = (ArgsAnnotationPointcut)obj;
        return other.arguments.equals(this.arguments);
    }

    public int hashCode() {
        return 17 + 37 * this.arguments.hashCode();
    }

    private void buildDeclarationText() {
        StringBuffer buf = new StringBuffer("@args");
        buf.append(this.arguments.toString());
        this.declarationText = buf.toString();
    }

    public String toString() {
        return this.declarationText;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}


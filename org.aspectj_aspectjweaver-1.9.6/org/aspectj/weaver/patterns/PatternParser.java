/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.internal.tools.PointcutDesignatorHandlerBasedPointcut;
import org.aspectj.weaver.patterns.AndAnnotationTypePattern;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.AndSignaturePattern;
import org.aspectj.weaver.patterns.AndTypePattern;
import org.aspectj.weaver.patterns.AnnotationPatternList;
import org.aspectj.weaver.patterns.AnnotationPointcut;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.AnyAnnotationTypePattern;
import org.aspectj.weaver.patterns.AnyWithAnnotationTypePattern;
import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.BasicToken;
import org.aspectj.weaver.patterns.BasicTokenSource;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclarePrecedence;
import org.aspectj.weaver.patterns.DeclareSoft;
import org.aspectj.weaver.patterns.DeclareTypeErrorOrWarning;
import org.aspectj.weaver.patterns.ExactAnnotationFieldTypePattern;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.HasMemberTypePattern;
import org.aspectj.weaver.patterns.ISignaturePattern;
import org.aspectj.weaver.patterns.IToken;
import org.aspectj.weaver.patterns.ITokenSource;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.ModifiersPattern;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.patterns.NotAnnotationTypePattern;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.NotSignaturePattern;
import org.aspectj.weaver.patterns.NotTypePattern;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.OrSignaturePattern;
import org.aspectj.weaver.patterns.OrTypePattern;
import org.aspectj.weaver.patterns.ParserException;
import org.aspectj.weaver.patterns.PerCflow;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.PerObject;
import org.aspectj.weaver.patterns.PerSingleton;
import org.aspectj.weaver.patterns.PerTypeWithin;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.ThrowsPattern;
import org.aspectj.weaver.patterns.TypeCategoryTypePattern;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;
import org.aspectj.weaver.patterns.TypeVariablePattern;
import org.aspectj.weaver.patterns.TypeVariablePatternList;
import org.aspectj.weaver.patterns.WildAnnotationTypePattern;
import org.aspectj.weaver.patterns.WildTypePattern;
import org.aspectj.weaver.patterns.WithinAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinCodeAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;

public class PatternParser {
    private ITokenSource tokenSource;
    private ISourceContext sourceContext;
    private boolean allowHasTypePatterns = false;
    private Set<PointcutDesignatorHandler> pointcutDesignatorHandlers = Collections.emptySet();
    private World world;
    private IToken pendingRightArrows;

    public PatternParser(ITokenSource tokenSource) {
        this.tokenSource = tokenSource;
        this.sourceContext = tokenSource.getSourceContext();
    }

    public void setPointcutDesignatorHandlers(Set<PointcutDesignatorHandler> handlers, World world) {
        this.pointcutDesignatorHandlers = handlers;
        this.world = world;
    }

    public PerClause maybeParsePerClause() {
        IToken tok = this.tokenSource.peek();
        if (tok == IToken.EOF) {
            return null;
        }
        if (tok.isIdentifier()) {
            String name = tok.getString();
            if (name.equals("issingleton")) {
                return this.parsePerSingleton();
            }
            if (name.equals("perthis")) {
                return this.parsePerObject(true);
            }
            if (name.equals("pertarget")) {
                return this.parsePerObject(false);
            }
            if (name.equals("percflow")) {
                return this.parsePerCflow(false);
            }
            if (name.equals("percflowbelow")) {
                return this.parsePerCflow(true);
            }
            if (name.equals("pertypewithin")) {
                return this.parsePerTypeWithin();
            }
            return null;
        }
        return null;
    }

    private PerClause parsePerCflow(boolean isBelow) {
        this.parseIdentifier();
        this.eat("(");
        Pointcut entry = this.parsePointcut();
        this.eat(")");
        return new PerCflow(entry, isBelow);
    }

    public boolean moreToParse() {
        return this.tokenSource.hasMoreTokens();
    }

    private PerClause parsePerObject(boolean isThis) {
        this.parseIdentifier();
        this.eat("(");
        Pointcut entry = this.parsePointcut();
        this.eat(")");
        return new PerObject(entry, isThis);
    }

    private PerClause parsePerTypeWithin() {
        this.parseIdentifier();
        this.eat("(");
        TypePattern withinTypePattern = this.parseTypePattern();
        this.eat(")");
        return new PerTypeWithin(withinTypePattern);
    }

    private PerClause parsePerSingleton() {
        this.parseIdentifier();
        this.eat("(");
        this.eat(")");
        return new PerSingleton();
    }

    public Declare parseDeclare() {
        Declare ret;
        int startPos = this.tokenSource.peek().getStart();
        this.eatIdentifier("declare");
        String kind = this.parseIdentifier();
        if (kind.equals("error")) {
            this.eat(":");
            ret = this.parseErrorOrWarning(true);
        } else if (kind.equals("warning")) {
            this.eat(":");
            ret = this.parseErrorOrWarning(false);
        } else if (kind.equals("precedence")) {
            this.eat(":");
            ret = this.parseDominates();
        } else {
            if (kind.equals("dominates")) {
                throw new ParserException("name changed to declare precedence", this.tokenSource.peek(-2));
            }
            if (kind.equals("parents")) {
                ret = this.parseParents();
            } else if (kind.equals("soft")) {
                this.eat(":");
                ret = this.parseSoft();
            } else {
                throw new ParserException("expected one of error, warning, parents, soft, precedence, @type, @method, @constructor, @field", this.tokenSource.peek(-1));
            }
        }
        int endPos = this.tokenSource.peek(-1).getEnd();
        ret.setLocation(this.sourceContext, startPos, endPos);
        return ret;
    }

    public Declare parseDeclareAnnotation() {
        DeclareAnnotation ret;
        int startPos = this.tokenSource.peek().getStart();
        this.eatIdentifier("declare");
        this.eat("@");
        String kind = this.parseIdentifier();
        this.eat(":");
        if (kind.equals("type")) {
            ret = this.parseDeclareAtType();
        } else if (kind.equals("method")) {
            ret = this.parseDeclareAtMethod(true);
        } else if (kind.equals("field")) {
            ret = this.parseDeclareAtField();
        } else if (kind.equals("constructor")) {
            ret = this.parseDeclareAtMethod(false);
        } else {
            throw new ParserException("one of type, method, field, constructor", this.tokenSource.peek(-1));
        }
        this.eat(";");
        int endPos = this.tokenSource.peek(-1).getEnd();
        ret.setLocation(this.sourceContext, startPos, endPos);
        return ret;
    }

    public DeclareAnnotation parseDeclareAtType() {
        this.allowHasTypePatterns = true;
        TypePattern p = this.parseTypePattern();
        this.allowHasTypePatterns = false;
        return new DeclareAnnotation(DeclareAnnotation.AT_TYPE, p);
    }

    public DeclareAnnotation parseDeclareAtMethod(boolean isMethod) {
        ISignaturePattern sp = this.parseCompoundMethodOrConstructorSignaturePattern(isMethod);
        if (!isMethod) {
            return new DeclareAnnotation(DeclareAnnotation.AT_CONSTRUCTOR, sp);
        }
        return new DeclareAnnotation(DeclareAnnotation.AT_METHOD, sp);
    }

    public DeclareAnnotation parseDeclareAtField() {
        ISignaturePattern compoundFieldSignaturePattern = this.parseCompoundFieldSignaturePattern();
        DeclareAnnotation da = new DeclareAnnotation(DeclareAnnotation.AT_FIELD, compoundFieldSignaturePattern);
        return da;
    }

    public ISignaturePattern parseCompoundFieldSignaturePattern() {
        int index = this.tokenSource.getIndex();
        try {
            ISignaturePattern atomicFieldSignaturePattern = this.parseMaybeParenthesizedFieldSignaturePattern();
            while (this.isEitherAndOrOr()) {
                if (this.maybeEat("&&")) {
                    atomicFieldSignaturePattern = new AndSignaturePattern(atomicFieldSignaturePattern, this.parseMaybeParenthesizedFieldSignaturePattern());
                }
                if (!this.maybeEat("||")) continue;
                atomicFieldSignaturePattern = new OrSignaturePattern(atomicFieldSignaturePattern, this.parseMaybeParenthesizedFieldSignaturePattern());
            }
            return atomicFieldSignaturePattern;
        }
        catch (ParserException e) {
            int nowAt = this.tokenSource.getIndex();
            this.tokenSource.setIndex(index);
            try {
                SignaturePattern fsp = this.parseFieldSignaturePattern();
                return fsp;
            }
            catch (Exception e2) {
                this.tokenSource.setIndex(nowAt);
                throw e;
            }
        }
    }

    private boolean isEitherAndOrOr() {
        String tokenstring = this.tokenSource.peek().getString();
        return tokenstring.equals("&&") || tokenstring.equals("||");
    }

    public ISignaturePattern parseCompoundMethodOrConstructorSignaturePattern(boolean isMethod) {
        ISignaturePattern atomicMethodCtorSignaturePattern = this.parseMaybeParenthesizedMethodOrConstructorSignaturePattern(isMethod);
        while (this.isEitherAndOrOr()) {
            if (this.maybeEat("&&")) {
                atomicMethodCtorSignaturePattern = new AndSignaturePattern(atomicMethodCtorSignaturePattern, this.parseMaybeParenthesizedMethodOrConstructorSignaturePattern(isMethod));
            }
            if (!this.maybeEat("||")) continue;
            atomicMethodCtorSignaturePattern = new OrSignaturePattern(atomicMethodCtorSignaturePattern, this.parseMaybeParenthesizedMethodOrConstructorSignaturePattern(isMethod));
        }
        return atomicMethodCtorSignaturePattern;
    }

    public DeclarePrecedence parseDominates() {
        ArrayList<TypePattern> l = new ArrayList<TypePattern>();
        do {
            l.add(this.parseTypePattern());
        } while (this.maybeEat(","));
        return new DeclarePrecedence(l);
    }

    private Declare parseParents() {
        this.eat(":");
        this.allowHasTypePatterns = true;
        TypePattern p = this.parseTypePattern(false, false);
        this.allowHasTypePatterns = false;
        IToken t = this.tokenSource.next();
        if (!t.getString().equals("extends") && !t.getString().equals("implements")) {
            throw new ParserException("extends or implements", t);
        }
        boolean isExtends = t.getString().equals("extends");
        ArrayList<TypePattern> l = new ArrayList<TypePattern>();
        do {
            l.add(this.parseTypePattern());
        } while (this.maybeEat(","));
        DeclareParents decp = new DeclareParents(p, l, isExtends);
        return decp;
    }

    private Declare parseSoft() {
        TypePattern p = this.parseTypePattern();
        this.eat(":");
        Pointcut pointcut = this.parsePointcut();
        return new DeclareSoft(p, pointcut);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Declare parseErrorOrWarning(boolean isError) {
        Pointcut pointcut = null;
        int index = this.tokenSource.getIndex();
        try {
            pointcut = this.parsePointcut();
        }
        catch (ParserException pe) {
            try {
                this.tokenSource.setIndex(index);
                boolean oldValue = this.allowHasTypePatterns;
                TypePattern typePattern = null;
                try {
                    this.allowHasTypePatterns = true;
                    typePattern = this.parseTypePattern();
                }
                finally {
                    this.allowHasTypePatterns = oldValue;
                }
                this.eat(":");
                String message = this.parsePossibleStringSequence(true);
                return new DeclareTypeErrorOrWarning(isError, typePattern, message);
            }
            catch (ParserException pe2) {
                throw pe;
            }
        }
        this.eat(":");
        String message = this.parsePossibleStringSequence(true);
        return new DeclareErrorOrWarning(isError, pointcut, message);
    }

    public Pointcut parsePointcut(boolean shouldConsumeAllInput) {
        Pointcut p = this.parsePointcut();
        if (shouldConsumeAllInput && this.tokenSource.hasMoreTokens()) {
            throw new ParserException("Found unexpected data after parsing pointcut", this.tokenSource.next());
        }
        return p;
    }

    public Pointcut parsePointcut() {
        Pointcut p = this.parseAtomicPointcut();
        if (this.maybeEat("&&")) {
            p = new AndPointcut(p, this.parseNotOrPointcut());
        }
        if (this.maybeEat("||")) {
            p = new OrPointcut(p, this.parsePointcut());
        }
        return p;
    }

    private Pointcut parseNotOrPointcut() {
        Pointcut p = this.parseAtomicPointcut();
        if (this.maybeEat("&&")) {
            p = new AndPointcut(p, this.parseNotOrPointcut());
        }
        return p;
    }

    private Pointcut parseAtomicPointcut() {
        if (this.maybeEat("!")) {
            int startPos = this.tokenSource.peek(-1).getStart();
            NotPointcut p = new NotPointcut(this.parseAtomicPointcut(), startPos);
            return p;
        }
        if (this.maybeEat("(")) {
            Pointcut p = this.parsePointcut();
            this.eat(")");
            return p;
        }
        if (this.maybeEat("@")) {
            int startPos = this.tokenSource.peek().getStart();
            Pointcut p = this.parseAnnotationPointcut();
            int endPos = this.tokenSource.peek(-1).getEnd();
            p.setLocation(this.sourceContext, startPos, endPos);
            return p;
        }
        int startPos = this.tokenSource.peek().getStart();
        Pointcut p = this.parseSinglePointcut();
        int endPos = this.tokenSource.peek(-1).getEnd();
        p.setLocation(this.sourceContext, startPos, endPos);
        return p;
    }

    public Pointcut parseSinglePointcut() {
        int start = this.tokenSource.getIndex();
        IToken t = this.tokenSource.peek();
        Pointcut p = t.maybeGetParsedPointcut();
        if (p != null) {
            this.tokenSource.next();
            return p;
        }
        String kind = this.parseIdentifier();
        if (kind.equals("execution") || kind.equals("call") || kind.equals("get") || kind.equals("set")) {
            p = this.parseKindedPointcut(kind);
        } else if (kind.equals("args")) {
            p = this.parseArgsPointcut();
        } else if (kind.equals("this")) {
            p = this.parseThisOrTargetPointcut(kind);
        } else if (kind.equals("target")) {
            p = this.parseThisOrTargetPointcut(kind);
        } else if (kind.equals("within")) {
            p = this.parseWithinPointcut();
        } else if (kind.equals("withincode")) {
            p = this.parseWithinCodePointcut();
        } else if (kind.equals("cflow")) {
            p = this.parseCflowPointcut(false);
        } else if (kind.equals("cflowbelow")) {
            p = this.parseCflowPointcut(true);
        } else if (kind.equals("adviceexecution")) {
            this.eat("(");
            this.eat(")");
            p = new KindedPointcut(Shadow.AdviceExecution, new SignaturePattern(Member.ADVICE, ModifiersPattern.ANY, TypePattern.ANY, TypePattern.ANY, NamePattern.ANY, TypePatternList.ANY, ThrowsPattern.ANY, AnnotationTypePattern.ANY));
        } else if (kind.equals("handler")) {
            this.eat("(");
            TypePattern typePat = this.parseTypePattern(false, false);
            this.eat(")");
            p = new HandlerPointcut(typePat);
        } else if (kind.equals("lock") || kind.equals("unlock")) {
            p = this.parseMonitorPointcut(kind);
        } else if (kind.equals("initialization")) {
            this.eat("(");
            SignaturePattern sig = this.parseConstructorSignaturePattern();
            this.eat(")");
            p = new KindedPointcut(Shadow.Initialization, sig);
        } else if (kind.equals("staticinitialization")) {
            this.eat("(");
            TypePattern typePat = this.parseTypePattern(false, false);
            this.eat(")");
            p = new KindedPointcut(Shadow.StaticInitialization, new SignaturePattern(Member.STATIC_INITIALIZATION, ModifiersPattern.ANY, TypePattern.ANY, typePat, NamePattern.ANY, TypePatternList.EMPTY, ThrowsPattern.ANY, AnnotationTypePattern.ANY));
        } else if (kind.equals("preinitialization")) {
            this.eat("(");
            SignaturePattern sig = this.parseConstructorSignaturePattern();
            this.eat(")");
            p = new KindedPointcut(Shadow.PreInitialization, sig);
        } else if (kind.equals("if")) {
            this.eat("(");
            if (this.maybeEatIdentifier("true")) {
                this.eat(")");
                p = new IfPointcut.IfTruePointcut();
            } else if (this.maybeEatIdentifier("false")) {
                this.eat(")");
                p = new IfPointcut.IfFalsePointcut();
            } else {
                if (!this.maybeEat(")")) {
                    throw new ParserException("in annotation style, if(...) pointcuts cannot contain code. Use if() and put the code in the annotated method", t);
                }
                p = new IfPointcut("");
            }
        } else {
            boolean matchedByExtensionDesignator = false;
            for (PointcutDesignatorHandler pcd : this.pointcutDesignatorHandlers) {
                if (!pcd.getDesignatorName().equals(kind)) continue;
                p = this.parseDesignatorPointcut(pcd);
                matchedByExtensionDesignator = true;
            }
            if (!matchedByExtensionDesignator) {
                this.tokenSource.setIndex(start);
                p = this.parseReferencePointcut();
            }
        }
        return p;
    }

    private void assertNoTypeVariables(String[] tvs, String errorMessage, IToken token) {
        if (tvs != null) {
            throw new ParserException(errorMessage, token);
        }
    }

    public Pointcut parseAnnotationPointcut() {
        int start = this.tokenSource.getIndex();
        IToken t = this.tokenSource.peek();
        String kind = this.parseIdentifier();
        IToken possibleTypeVariableToken = this.tokenSource.peek();
        String[] typeVariables = this.maybeParseSimpleTypeVariableList();
        if (typeVariables != null) {
            String message = "(";
            this.assertNoTypeVariables(typeVariables, message, possibleTypeVariableToken);
        }
        this.tokenSource.setIndex(start);
        if (kind.equals("annotation")) {
            return this.parseAtAnnotationPointcut();
        }
        if (kind.equals("args")) {
            return this.parseArgsAnnotationPointcut();
        }
        if (kind.equals("this") || kind.equals("target")) {
            return this.parseThisOrTargetAnnotationPointcut();
        }
        if (kind.equals("within")) {
            return this.parseWithinAnnotationPointcut();
        }
        if (kind.equals("withincode")) {
            return this.parseWithinCodeAnnotationPointcut();
        }
        throw new ParserException("pointcut name", t);
    }

    private Pointcut parseAtAnnotationPointcut() {
        this.parseIdentifier();
        this.eat("(");
        if (this.maybeEat(")")) {
            throw new ParserException("@AnnotationName or parameter", this.tokenSource.peek());
        }
        ExactAnnotationTypePattern type = this.parseAnnotationNameOrVarTypePattern();
        this.eat(")");
        return new AnnotationPointcut(type);
    }

    private SignaturePattern parseConstructorSignaturePattern() {
        SignaturePattern ret = this.parseMethodOrConstructorSignaturePattern();
        if (ret.getKind() == Member.CONSTRUCTOR) {
            return ret;
        }
        throw new ParserException("constructor pattern required, found method pattern", ret);
    }

    private Pointcut parseWithinCodePointcut() {
        this.eat("(");
        SignaturePattern sig = this.parseMethodOrConstructorSignaturePattern();
        this.eat(")");
        return new WithincodePointcut(sig);
    }

    private Pointcut parseCflowPointcut(boolean isBelow) {
        this.eat("(");
        Pointcut entry = this.parsePointcut();
        this.eat(")");
        return new CflowPointcut(entry, isBelow, null);
    }

    private Pointcut parseWithinPointcut() {
        this.eat("(");
        TypePattern type = this.parseTypePattern();
        this.eat(")");
        return new WithinPointcut(type);
    }

    private Pointcut parseThisOrTargetPointcut(String kind) {
        this.eat("(");
        TypePattern type = this.parseTypePattern();
        this.eat(")");
        return new ThisOrTargetPointcut(kind.equals("this"), type);
    }

    private Pointcut parseThisOrTargetAnnotationPointcut() {
        String kind = this.parseIdentifier();
        this.eat("(");
        if (this.maybeEat(")")) {
            throw new ParserException("expecting @AnnotationName or parameter, but found ')'", this.tokenSource.peek());
        }
        ExactAnnotationTypePattern type = this.parseAnnotationNameOrVarTypePattern();
        this.eat(")");
        return new ThisOrTargetAnnotationPointcut(kind.equals("this"), type);
    }

    private Pointcut parseWithinAnnotationPointcut() {
        this.parseIdentifier();
        this.eat("(");
        if (this.maybeEat(")")) {
            throw new ParserException("expecting @AnnotationName or parameter, but found ')'", this.tokenSource.peek());
        }
        ExactAnnotationTypePattern type = this.parseAnnotationNameOrVarTypePattern();
        this.eat(")");
        return new WithinAnnotationPointcut(type);
    }

    private Pointcut parseWithinCodeAnnotationPointcut() {
        this.parseIdentifier();
        this.eat("(");
        if (this.maybeEat(")")) {
            throw new ParserException("expecting @AnnotationName or parameter, but found ')'", this.tokenSource.peek());
        }
        ExactAnnotationTypePattern type = this.parseAnnotationNameOrVarTypePattern();
        this.eat(")");
        return new WithinCodeAnnotationPointcut(type);
    }

    private Pointcut parseArgsPointcut() {
        TypePatternList arguments = this.parseArgumentsPattern(false);
        return new ArgsPointcut(arguments);
    }

    private Pointcut parseArgsAnnotationPointcut() {
        this.parseIdentifier();
        AnnotationPatternList arguments = this.parseArgumentsAnnotationPattern();
        return new ArgsAnnotationPointcut(arguments);
    }

    private Pointcut parseReferencePointcut() {
        String simpleName;
        TypePattern onType = this.parseTypePattern();
        NamePattern name = null;
        if (onType.typeParameters.size() > 0) {
            this.eat(".");
            name = this.parseNamePattern();
        } else {
            name = this.tryToExtractName(onType);
        }
        if (name == null) {
            throw new ParserException("name pattern", this.tokenSource.peek());
        }
        if (onType.toString().equals("")) {
            onType = null;
        }
        if ((simpleName = name.maybeGetSimpleName()) == null) {
            throw new ParserException("(", this.tokenSource.peek(-1));
        }
        TypePatternList arguments = this.parseArgumentsPattern(false);
        return new ReferencePointcut(onType, simpleName, arguments);
    }

    private Pointcut parseDesignatorPointcut(PointcutDesignatorHandler pcdHandler) {
        this.eat("(");
        int parenCount = 1;
        StringBuffer pointcutBody = new StringBuffer();
        while (parenCount > 0) {
            if (this.maybeEat("(")) {
                ++parenCount;
                pointcutBody.append("(");
                continue;
            }
            if (this.maybeEat(")")) {
                if (--parenCount <= 0) continue;
                pointcutBody.append(")");
                continue;
            }
            pointcutBody.append(this.nextToken().getString());
        }
        ContextBasedMatcher pcExpr = pcdHandler.parse(pointcutBody.toString());
        return new PointcutDesignatorHandlerBasedPointcut(pcExpr, this.world);
    }

    public List<String> parseDottedIdentifier() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(this.parseIdentifier());
        while (this.maybeEat(".")) {
            ret.add(this.parseIdentifier());
        }
        return ret;
    }

    private KindedPointcut parseKindedPointcut(String kind) {
        SignaturePattern sig;
        this.eat("(");
        Shadow.Kind shadowKind = null;
        if (kind.equals("execution")) {
            sig = this.parseMethodOrConstructorSignaturePattern();
            if (sig.getKind() == Member.METHOD) {
                shadowKind = Shadow.MethodExecution;
            } else if (sig.getKind() == Member.CONSTRUCTOR) {
                shadowKind = Shadow.ConstructorExecution;
            }
        } else if (kind.equals("call")) {
            sig = this.parseMethodOrConstructorSignaturePattern();
            if (sig.getKind() == Member.METHOD) {
                shadowKind = Shadow.MethodCall;
            } else if (sig.getKind() == Member.CONSTRUCTOR) {
                shadowKind = Shadow.ConstructorCall;
            }
        } else if (kind.equals("get")) {
            sig = this.parseFieldSignaturePattern();
            shadowKind = Shadow.FieldGet;
        } else if (kind.equals("set")) {
            sig = this.parseFieldSignaturePattern();
            shadowKind = Shadow.FieldSet;
        } else {
            throw new ParserException("bad kind: " + kind, this.tokenSource.peek());
        }
        this.eat(")");
        return new KindedPointcut(shadowKind, sig);
    }

    private KindedPointcut parseMonitorPointcut(String kind) {
        this.eat("(");
        this.eat(")");
        if (kind.equals("lock")) {
            return new KindedPointcut(Shadow.SynchronizationLock, new SignaturePattern(Member.MONITORENTER, ModifiersPattern.ANY, TypePattern.ANY, TypePattern.ANY, NamePattern.ANY, TypePatternList.ANY, ThrowsPattern.ANY, AnnotationTypePattern.ANY));
        }
        return new KindedPointcut(Shadow.SynchronizationUnlock, new SignaturePattern(Member.MONITORENTER, ModifiersPattern.ANY, TypePattern.ANY, TypePattern.ANY, NamePattern.ANY, TypePatternList.ANY, ThrowsPattern.ANY, AnnotationTypePattern.ANY));
    }

    public TypePattern parseTypePattern() {
        return this.parseTypePattern(false, false);
    }

    public TypePattern parseTypePattern(boolean insideTypeParameters, boolean parameterAnnotationsPossible) {
        TypePattern p = this.parseAtomicTypePattern(insideTypeParameters, parameterAnnotationsPossible);
        if (this.maybeEat("&&")) {
            p = new AndTypePattern(p, this.parseNotOrTypePattern(insideTypeParameters, parameterAnnotationsPossible));
        }
        if (this.maybeEat("||")) {
            p = new OrTypePattern(p, this.parseTypePattern(insideTypeParameters, parameterAnnotationsPossible));
        }
        return p;
    }

    private TypePattern parseNotOrTypePattern(boolean insideTypeParameters, boolean parameterAnnotationsPossible) {
        TypePattern p = this.parseAtomicTypePattern(insideTypeParameters, parameterAnnotationsPossible);
        if (this.maybeEat("&&")) {
            p = new AndTypePattern(p, this.parseTypePattern(insideTypeParameters, parameterAnnotationsPossible));
        }
        return p;
    }

    private TypePattern parseAtomicTypePattern(boolean insideTypeParameters, boolean parameterAnnotationsPossible) {
        AnnotationTypePattern ap = this.maybeParseAnnotationPattern();
        if (this.maybeEat("!")) {
            TypePattern p = null;
            TypePattern tp = this.parseAtomicTypePattern(insideTypeParameters, parameterAnnotationsPossible);
            if (!(ap instanceof AnyAnnotationTypePattern)) {
                p = new NotTypePattern(tp);
                p = new AndTypePattern(this.setAnnotationPatternForTypePattern(TypePattern.ANY, ap, false), p);
            } else {
                p = new NotTypePattern(tp);
            }
            return p;
        }
        if (this.maybeEat("(")) {
            boolean isIncludeSubtypes;
            int openParenPos = this.tokenSource.peek(-1).getStart();
            TypePattern p = this.parseTypePattern(insideTypeParameters, false);
            if (p instanceof NotTypePattern && !(ap instanceof AnyAnnotationTypePattern)) {
                TypePattern tp = this.setAnnotationPatternForTypePattern(TypePattern.ANY, ap, parameterAnnotationsPossible);
                p = new AndTypePattern(tp, p);
            } else {
                p = this.setAnnotationPatternForTypePattern(p, ap, parameterAnnotationsPossible);
            }
            this.eat(")");
            int closeParenPos = this.tokenSource.peek(-1).getStart();
            boolean isVarArgs = this.maybeEat("...");
            if (isVarArgs) {
                p.setIsVarArgs(isVarArgs);
            }
            if (isIncludeSubtypes = this.maybeEat("+")) {
                p.includeSubtypes = true;
            }
            p.start = openParenPos;
            p.end = closeParenPos;
            return p;
        }
        int startPos = this.tokenSource.peek().getStart();
        if (ap.start != -1) {
            startPos = ap.start;
        }
        TypePattern p = this.parseSingleTypePattern(insideTypeParameters);
        int endPos = this.tokenSource.peek(-1).getEnd();
        p = this.setAnnotationPatternForTypePattern(p, ap, false);
        p.setLocation(this.sourceContext, startPos, endPos);
        return p;
    }

    private TypePattern setAnnotationPatternForTypePattern(TypePattern t, AnnotationTypePattern ap, boolean parameterAnnotationsPattern) {
        TypePattern ret = t;
        if (parameterAnnotationsPattern) {
            ap.setForParameterAnnotationMatch();
        }
        if (ap != AnnotationTypePattern.ANY) {
            if (t == TypePattern.ANY) {
                if (t.annotationPattern == AnnotationTypePattern.ANY) {
                    return new AnyWithAnnotationTypePattern(ap);
                }
                return new AnyWithAnnotationTypePattern(new AndAnnotationTypePattern(ap, t.annotationPattern));
            }
            if (t.annotationPattern == AnnotationTypePattern.ANY) {
                ret.setAnnotationTypePattern(ap);
            } else {
                ret.setAnnotationTypePattern(new AndAnnotationTypePattern(ap, t.annotationPattern));
            }
        }
        return ret;
    }

    public AnnotationTypePattern maybeParseAnnotationPattern() {
        AnnotationTypePattern ret = AnnotationTypePattern.ANY;
        AnnotationTypePattern nextPattern = null;
        while ((nextPattern = this.maybeParseSingleAnnotationPattern()) != null) {
            if (ret == AnnotationTypePattern.ANY) {
                ret = nextPattern;
                continue;
            }
            ret = new AndAnnotationTypePattern(ret, nextPattern);
        }
        return ret;
    }

    public AnnotationTypePattern maybeParseSingleAnnotationPattern() {
        AnnotationTypePattern ret = null;
        Map<String, String> values = null;
        int startIndex = this.tokenSource.getIndex();
        if (this.maybeEat("!")) {
            if (this.maybeEat("@")) {
                if (this.maybeEat("(")) {
                    TypePattern p = this.parseTypePattern();
                    ret = new NotAnnotationTypePattern(new WildAnnotationTypePattern(p));
                    this.eat(")");
                    return ret;
                }
                TypePattern p = this.parseSingleTypePattern();
                if (this.maybeEatAdjacent("(")) {
                    values = this.parseAnnotationValues();
                    this.eat(")");
                    ret = new NotAnnotationTypePattern(new WildAnnotationTypePattern(p, values));
                } else {
                    ret = new NotAnnotationTypePattern(new WildAnnotationTypePattern(p));
                }
                return ret;
            }
            this.tokenSource.setIndex(startIndex);
            return ret;
        }
        if (this.maybeEat("@")) {
            if (this.maybeEat("(")) {
                TypePattern p = this.parseTypePattern();
                ret = new WildAnnotationTypePattern(p);
                this.eat(")");
                return ret;
            }
            int atPos = this.tokenSource.peek(-1).getStart();
            TypePattern p = this.parseSingleTypePattern();
            if (this.maybeEatAdjacent("(")) {
                values = this.parseAnnotationValues();
                this.eat(")");
                ret = new WildAnnotationTypePattern(p, values);
            } else {
                ret = new WildAnnotationTypePattern(p);
            }
            ret.start = atPos;
            return ret;
        }
        this.tokenSource.setIndex(startIndex);
        return ret;
    }

    public Map<String, String> parseAnnotationValues() {
        HashMap<String, String> values = new HashMap<String, String>();
        boolean seenDefaultValue = false;
        do {
            String valueString;
            String possibleKeyString;
            if ((possibleKeyString = this.parseAnnotationNameValuePattern()) == null) {
                throw new ParserException("expecting simple literal ", this.tokenSource.peek(-1));
            }
            if (this.maybeEat("=")) {
                valueString = this.parseAnnotationNameValuePattern();
                if (valueString == null) {
                    throw new ParserException("expecting simple literal ", this.tokenSource.peek(-1));
                }
                values.put(possibleKeyString, valueString);
                continue;
            }
            if (this.maybeEat("!=")) {
                valueString = this.parseAnnotationNameValuePattern();
                if (valueString == null) {
                    throw new ParserException("expecting simple literal ", this.tokenSource.peek(-1));
                }
                values.put(possibleKeyString + "!", valueString);
                continue;
            }
            if (seenDefaultValue) {
                throw new ParserException("cannot specify two default values", this.tokenSource.peek(-1));
            }
            seenDefaultValue = true;
            values.put("value", possibleKeyString);
        } while (this.maybeEat(","));
        return values;
    }

    public TypePattern parseSingleTypePattern() {
        return this.parseSingleTypePattern(false);
    }

    public TypePattern parseSingleTypePattern(boolean insideTypeParameters) {
        if (insideTypeParameters && this.maybeEat("?")) {
            return this.parseGenericsWildcardTypePattern();
        }
        if (this.allowHasTypePatterns) {
            if (this.maybeEatIdentifier("hasmethod")) {
                return this.parseHasMethodTypePattern();
            }
            if (this.maybeEatIdentifier("hasfield")) {
                return this.parseHasFieldTypePattern();
            }
        }
        if (this.maybeEatIdentifier("is")) {
            int pos = this.tokenSource.getIndex() - 1;
            TypePattern typeIsPattern = this.parseIsTypePattern();
            if (typeIsPattern != null) {
                return typeIsPattern;
            }
            this.tokenSource.setIndex(pos);
        }
        List<NamePattern> names = this.parseDottedNamePattern();
        int dim = 0;
        while (this.maybeEat("[")) {
            this.eat("]");
            ++dim;
        }
        TypePatternList typeParameters = this.maybeParseTypeParameterList();
        int endPos = this.tokenSource.peek(-1).getEnd();
        boolean includeSubtypes = this.maybeEat("+");
        while (this.maybeEat("[")) {
            this.eat("]");
            ++dim;
        }
        boolean isVarArgs = this.maybeEat("...");
        if (names.size() == 1 && names.get(0).isAny() && dim == 0 && !isVarArgs && typeParameters == null) {
            return TypePattern.ANY;
        }
        return new WildTypePattern(names, includeSubtypes, dim + (isVarArgs ? 1 : 0), endPos, isVarArgs, typeParameters);
    }

    public TypePattern parseHasMethodTypePattern() {
        int startPos = this.tokenSource.peek(-1).getStart();
        this.eat("(");
        SignaturePattern sp = this.parseMethodOrConstructorSignaturePattern();
        this.eat(")");
        int endPos = this.tokenSource.peek(-1).getEnd();
        HasMemberTypePattern ret = new HasMemberTypePattern(sp);
        ret.setLocation(this.sourceContext, startPos, endPos);
        return ret;
    }

    public TypePattern parseIsTypePattern() {
        int startPos = this.tokenSource.peek(-1).getStart();
        if (!this.maybeEatAdjacent("(")) {
            return null;
        }
        IToken token = this.tokenSource.next();
        TypeCategoryTypePattern typeIsPattern = null;
        if (token.isIdentifier()) {
            String category = token.getString();
            if (category.equals("ClassType")) {
                typeIsPattern = new TypeCategoryTypePattern(1);
            } else if (category.equals("AspectType")) {
                typeIsPattern = new TypeCategoryTypePattern(3);
            } else if (category.equals("InterfaceType")) {
                typeIsPattern = new TypeCategoryTypePattern(2);
            } else if (category.equals("InnerType")) {
                typeIsPattern = new TypeCategoryTypePattern(4);
            } else if (category.equals("AnonymousType")) {
                typeIsPattern = new TypeCategoryTypePattern(5);
            } else if (category.equals("EnumType")) {
                typeIsPattern = new TypeCategoryTypePattern(6);
            } else if (category.equals("AnnotationType")) {
                typeIsPattern = new TypeCategoryTypePattern(7);
            } else if (category.equals("FinalType")) {
                typeIsPattern = new TypeCategoryTypePattern(8);
            } else if (category.equals("AbstractType")) {
                typeIsPattern = new TypeCategoryTypePattern(9);
            }
        }
        if (typeIsPattern == null) {
            return null;
        }
        if (!this.maybeEat(")")) {
            throw new ParserException(")", this.tokenSource.peek());
        }
        int endPos = this.tokenSource.peek(-1).getEnd();
        typeIsPattern.setLocation(this.tokenSource.getSourceContext(), startPos, endPos);
        return typeIsPattern;
    }

    public TypePattern parseHasFieldTypePattern() {
        int startPos = this.tokenSource.peek(-1).getStart();
        this.eat("(");
        SignaturePattern sp = this.parseFieldSignaturePattern();
        this.eat(")");
        int endPos = this.tokenSource.peek(-1).getEnd();
        HasMemberTypePattern ret = new HasMemberTypePattern(sp);
        ret.setLocation(this.sourceContext, startPos, endPos);
        return ret;
    }

    public TypePattern parseGenericsWildcardTypePattern() {
        ArrayList<NamePattern> names = new ArrayList<NamePattern>();
        names.add(new NamePattern("?"));
        TypePattern upperBound = null;
        TypePattern[] additionalInterfaceBounds = new TypePattern[]{};
        TypePattern lowerBound = null;
        if (this.maybeEatIdentifier("extends")) {
            upperBound = this.parseTypePattern(false, false);
            additionalInterfaceBounds = this.maybeParseAdditionalInterfaceBounds();
        }
        if (this.maybeEatIdentifier("super")) {
            lowerBound = this.parseTypePattern(false, false);
        }
        int endPos = this.tokenSource.peek(-1).getEnd();
        return new WildTypePattern(names, false, 0, endPos, false, null, upperBound, additionalInterfaceBounds, lowerBound);
    }

    protected ExactAnnotationTypePattern parseAnnotationNameOrVarTypePattern() {
        ExactAnnotationTypePattern p = null;
        int startPos = this.tokenSource.peek().getStart();
        if (this.maybeEat("@")) {
            throw new ParserException("@Foo form was deprecated in AspectJ 5 M2: annotation name or var ", this.tokenSource.peek(-1));
        }
        p = this.parseSimpleAnnotationName();
        int endPos = this.tokenSource.peek(-1).getEnd();
        p.setLocation(this.sourceContext, startPos, endPos);
        if (this.maybeEat("(")) {
            String formalName = this.parseIdentifier();
            p = new ExactAnnotationFieldTypePattern(p, formalName);
            this.eat(")");
        }
        return p;
    }

    private ExactAnnotationTypePattern parseSimpleAnnotationName() {
        StringBuffer annotationName = new StringBuffer();
        annotationName.append(this.parseIdentifier());
        while (this.maybeEat(".")) {
            annotationName.append('.');
            annotationName.append(this.parseIdentifier());
        }
        UnresolvedType type = UnresolvedType.forName(annotationName.toString());
        ExactAnnotationTypePattern p = new ExactAnnotationTypePattern(type, null);
        return p;
    }

    public List<NamePattern> parseDottedNamePattern() {
        ArrayList<NamePattern> names = new ArrayList<NamePattern>();
        StringBuffer buf = new StringBuffer();
        IToken previous = null;
        boolean justProcessedEllipsis = false;
        boolean justProcessedDot = false;
        boolean onADot = false;
        while (true) {
            IToken tok = null;
            int startPos = this.tokenSource.peek().getStart();
            String afterDot = null;
            while (true) {
                if (previous != null && previous.getString().equals(".")) {
                    justProcessedDot = true;
                }
                tok = this.tokenSource.peek();
                onADot = tok.getString().equals(".");
                if (previous != null && !this.isAdjacent(previous, tok)) break;
                if (tok.getString() == "*" || tok.isIdentifier() && tok.getString() != "...") {
                    buf.append(tok.getString());
                } else {
                    if (tok.getString() == "..." || tok.getLiteralKind() == null) break;
                    String s = tok.getString();
                    int dot = s.indexOf(46);
                    if (dot != -1) {
                        buf.append(s.substring(0, dot));
                        afterDot = s.substring(dot + 1);
                        previous = this.tokenSource.next();
                        break;
                    }
                    buf.append(s);
                }
                previous = this.tokenSource.next();
            }
            int endPos = this.tokenSource.peek(-1).getEnd();
            if (buf.length() == 0 && names.isEmpty()) {
                throw new ParserException("name pattern", tok);
            }
            if (buf.length() == 0 && justProcessedEllipsis) {
                throw new ParserException("name pattern cannot finish with ..", tok);
            }
            if (buf.length() == 0 && justProcessedDot && !onADot) {
                throw new ParserException("name pattern cannot finish with .", tok);
            }
            if (buf.length() == 0) {
                names.add(NamePattern.ELLIPSIS);
                justProcessedEllipsis = true;
            } else {
                this.checkLegalName(buf.toString(), previous);
                NamePattern ret = new NamePattern(buf.toString());
                ret.setLocation(this.sourceContext, startPos, endPos);
                names.add(ret);
                justProcessedEllipsis = false;
            }
            if (afterDot == null) {
                buf.setLength(0);
                if (!this.maybeEat(".")) break;
                previous = this.tokenSource.peek(-1);
                continue;
            }
            buf.setLength(0);
            buf.append(afterDot);
            afterDot = null;
        }
        return names;
    }

    public String parseAnnotationNameValuePattern() {
        IToken tok;
        StringBuffer buf = new StringBuffer();
        this.tokenSource.peek().getStart();
        boolean dotOK = false;
        int depth = 0;
        while (!((tok = this.tokenSource.peek()).getString() == ")" && depth == 0 || tok.getString() == "!=" && depth == 0 || tok.getString() == "=" && depth == 0 || tok.getString() == "," && depth == 0)) {
            if (tok == IToken.EOF) {
                throw new ParserException("eof", this.tokenSource.peek());
            }
            if (tok.getString() == "(") {
                ++depth;
            }
            if (tok.getString() == ")") {
                --depth;
            }
            if (tok.getString() == "{") {
                ++depth;
            }
            if (tok.getString() == "}") {
                --depth;
            }
            if (tok.getString() == "." && !dotOK) {
                throw new ParserException("dot not expected", tok);
            }
            buf.append(tok.getString());
            this.tokenSource.next();
            dotOK = true;
        }
        return buf.toString();
    }

    public NamePattern parseNamePattern() {
        IToken tok;
        StringBuffer buf = new StringBuffer();
        IToken previous = null;
        int startPos = this.tokenSource.peek().getStart();
        while (true) {
            tok = this.tokenSource.peek();
            if (previous != null && !this.isAdjacent(previous, tok)) break;
            if (tok.getString() == "*" || tok.isIdentifier()) {
                buf.append(tok.getString());
            } else {
                String s;
                if (tok.getLiteralKind() == null || (s = tok.getString()).indexOf(46) != -1) break;
                buf.append(s);
            }
            previous = this.tokenSource.next();
        }
        int endPos = this.tokenSource.peek(-1).getEnd();
        if (buf.length() == 0) {
            throw new ParserException("name pattern", tok);
        }
        this.checkLegalName(buf.toString(), previous);
        NamePattern ret = new NamePattern(buf.toString());
        ret.setLocation(this.sourceContext, startPos, endPos);
        return ret;
    }

    private void checkLegalName(String s, IToken tok) {
        char ch = s.charAt(0);
        if (ch != '*' && !Character.isJavaIdentifierStart(ch)) {
            throw new ParserException("illegal identifier start (" + ch + ")", tok);
        }
        int len = s.length();
        for (int i = 1; i < len; ++i) {
            ch = s.charAt(i);
            if (ch == '*' || Character.isJavaIdentifierPart(ch)) continue;
            throw new ParserException("illegal identifier character (" + ch + ")", tok);
        }
    }

    private boolean isAdjacent(IToken first, IToken second) {
        return first.getEnd() == second.getStart() - 1;
    }

    public ModifiersPattern parseModifiersPattern() {
        int start;
        int requiredFlags = 0;
        int forbiddenFlags = 0;
        while (true) {
            start = this.tokenSource.getIndex();
            boolean isForbidden = false;
            isForbidden = this.maybeEat("!");
            IToken t = this.tokenSource.next();
            int flag = ModifiersPattern.getModifierFlag(t.getString());
            if (flag == -1) break;
            if (isForbidden) {
                forbiddenFlags |= flag;
                continue;
            }
            requiredFlags |= flag;
        }
        this.tokenSource.setIndex(start);
        if (requiredFlags == 0 && forbiddenFlags == 0) {
            return ModifiersPattern.ANY;
        }
        return new ModifiersPattern(requiredFlags, forbiddenFlags);
    }

    public TypePatternList parseArgumentsPattern(boolean parameterAnnotationsPossible) {
        ArrayList<TypePattern> patterns = new ArrayList<TypePattern>();
        this.eat("(");
        if (this.maybeEat(")")) {
            return new TypePatternList();
        }
        do {
            if (this.maybeEat(".")) {
                this.eat(".");
                patterns.add(TypePattern.ELLIPSIS);
                continue;
            }
            patterns.add(this.parseTypePattern(false, parameterAnnotationsPossible));
        } while (this.maybeEat(","));
        this.eat(")");
        return new TypePatternList(patterns);
    }

    public AnnotationPatternList parseArgumentsAnnotationPattern() {
        ArrayList<AnnotationTypePattern> patterns = new ArrayList<AnnotationTypePattern>();
        this.eat("(");
        if (this.maybeEat(")")) {
            return new AnnotationPatternList();
        }
        do {
            if (this.maybeEat(".")) {
                this.eat(".");
                patterns.add(AnnotationTypePattern.ELLIPSIS);
                continue;
            }
            if (this.maybeEat("*")) {
                patterns.add(AnnotationTypePattern.ANY);
                continue;
            }
            patterns.add(this.parseAnnotationNameOrVarTypePattern());
        } while (this.maybeEat(","));
        this.eat(")");
        return new AnnotationPatternList(patterns);
    }

    public ThrowsPattern parseOptionalThrowsPattern() {
        IToken t = this.tokenSource.peek();
        if (t.isIdentifier() && t.getString().equals("throws")) {
            this.tokenSource.next();
            ArrayList<TypePattern> required = new ArrayList<TypePattern>();
            ArrayList<TypePattern> forbidden = new ArrayList<TypePattern>();
            do {
                boolean isForbidden = this.maybeEat("!");
                TypePattern p = this.parseTypePattern();
                if (isForbidden) {
                    forbidden.add(p);
                    continue;
                }
                required.add(p);
            } while (this.maybeEat(","));
            return new ThrowsPattern(new TypePatternList(required), new TypePatternList(forbidden));
        }
        return ThrowsPattern.ANY;
    }

    public SignaturePattern parseMethodOrConstructorSignaturePattern() {
        TypePattern declaringType;
        MemberKind kind;
        int startPos = this.tokenSource.peek().getStart();
        AnnotationTypePattern annotationPattern = this.maybeParseAnnotationPattern();
        ModifiersPattern modifiers = this.parseModifiersPattern();
        TypePattern returnType = this.parseTypePattern(false, false);
        NamePattern name = null;
        if (this.maybeEatNew(returnType)) {
            kind = Member.CONSTRUCTOR;
            declaringType = returnType.toString().length() == 0 ? TypePattern.ANY : returnType;
            returnType = TypePattern.ANY;
            name = NamePattern.ANY;
        } else {
            kind = Member.METHOD;
            IToken nameToken = this.tokenSource.peek();
            declaringType = this.parseTypePattern(false, false);
            if (this.maybeEat(".")) {
                nameToken = this.tokenSource.peek();
                name = this.parseNamePattern();
            } else {
                name = this.tryToExtractName(declaringType);
                if (declaringType.toString().equals("")) {
                    declaringType = TypePattern.ANY;
                }
            }
            if (name == null) {
                throw new ParserException("name pattern", this.tokenSource.peek());
            }
            String simpleName = name.maybeGetSimpleName();
            if (simpleName != null && simpleName.equals("new")) {
                throw new ParserException("method name (not constructor)", nameToken);
            }
        }
        TypePatternList parameterTypes = this.parseArgumentsPattern(true);
        ThrowsPattern throwsPattern = this.parseOptionalThrowsPattern();
        SignaturePattern ret = new SignaturePattern(kind, modifiers, returnType, declaringType, name, parameterTypes, throwsPattern, annotationPattern);
        int endPos = this.tokenSource.peek(-1).getEnd();
        ret.setLocation(this.sourceContext, startPos, endPos);
        return ret;
    }

    private boolean maybeEatNew(TypePattern returnType) {
        WildTypePattern p;
        if (returnType instanceof WildTypePattern && (p = (WildTypePattern)returnType).maybeExtractName("new")) {
            return true;
        }
        int start = this.tokenSource.getIndex();
        if (this.maybeEat(".")) {
            String id = this.maybeEatIdentifier();
            if (id != null && id.equals("new")) {
                return true;
            }
            this.tokenSource.setIndex(start);
        }
        return false;
    }

    public ISignaturePattern parseMaybeParenthesizedFieldSignaturePattern() {
        boolean negated;
        boolean bl = negated = this.tokenSource.peek().getString().equals("!") && this.tokenSource.peek(1).getString().equals("(");
        if (negated) {
            this.eat("!");
        }
        ISignaturePattern result = null;
        if (this.maybeEat("(")) {
            result = this.parseCompoundFieldSignaturePattern();
            this.eat(")", "missing ')' - unbalanced parentheses around field signature pattern in declare @field");
            if (negated) {
                result = new NotSignaturePattern(result);
            }
        } else {
            result = this.parseFieldSignaturePattern();
        }
        return result;
    }

    public ISignaturePattern parseMaybeParenthesizedMethodOrConstructorSignaturePattern(boolean isMethod) {
        boolean negated;
        boolean bl = negated = this.tokenSource.peek().getString().equals("!") && this.tokenSource.peek(1).getString().equals("(");
        if (negated) {
            this.eat("!");
        }
        ISignaturePattern result = null;
        if (this.maybeEat("(")) {
            result = this.parseCompoundMethodOrConstructorSignaturePattern(isMethod);
            this.eat(")", "missing ')' - unbalanced parentheses around method/ctor signature pattern in declare annotation");
            if (negated) {
                result = new NotSignaturePattern(result);
            }
        } else {
            boolean isConstructorPattern;
            SignaturePattern sp = this.parseMethodOrConstructorSignaturePattern();
            boolean bl2 = isConstructorPattern = sp.getKind() == Member.CONSTRUCTOR;
            if (isMethod && isConstructorPattern) {
                throw new ParserException("method signature pattern", this.tokenSource.peek(-1));
            }
            if (!isMethod && !isConstructorPattern) {
                throw new ParserException("constructor signature pattern", this.tokenSource.peek(-1));
            }
            result = sp;
        }
        return result;
    }

    public SignaturePattern parseFieldSignaturePattern() {
        NamePattern name;
        int startPos = this.tokenSource.peek().getStart();
        AnnotationTypePattern annotationPattern = this.maybeParseAnnotationPattern();
        ModifiersPattern modifiers = this.parseModifiersPattern();
        TypePattern returnType = this.parseTypePattern();
        TypePattern declaringType = this.parseTypePattern();
        if (this.maybeEat(".")) {
            name = this.parseNamePattern();
        } else {
            name = this.tryToExtractName(declaringType);
            if (name == null) {
                throw new ParserException("name pattern", this.tokenSource.peek());
            }
            if (declaringType.toString().equals("")) {
                declaringType = TypePattern.ANY;
            }
        }
        SignaturePattern ret = new SignaturePattern(Member.FIELD, modifiers, returnType, declaringType, name, TypePatternList.ANY, ThrowsPattern.ANY, annotationPattern);
        int endPos = this.tokenSource.peek(-1).getEnd();
        ret.setLocation(this.sourceContext, startPos, endPos);
        return ret;
    }

    private NamePattern tryToExtractName(TypePattern nextType) {
        if (nextType == TypePattern.ANY) {
            return NamePattern.ANY;
        }
        if (nextType instanceof WildTypePattern) {
            WildTypePattern p = (WildTypePattern)nextType;
            return p.extractName();
        }
        return null;
    }

    public TypeVariablePatternList maybeParseTypeVariableList() {
        if (!this.maybeEat("<")) {
            return null;
        }
        ArrayList<TypeVariablePattern> typeVars = new ArrayList<TypeVariablePattern>();
        TypeVariablePattern t = this.parseTypeVariable();
        typeVars.add(t);
        while (this.maybeEat(",")) {
            TypeVariablePattern nextT = this.parseTypeVariable();
            typeVars.add(nextT);
        }
        this.eat(">");
        TypeVariablePattern[] tvs = new TypeVariablePattern[typeVars.size()];
        typeVars.toArray(tvs);
        return new TypeVariablePatternList(tvs);
    }

    public String[] maybeParseSimpleTypeVariableList() {
        if (!this.maybeEat("<")) {
            return null;
        }
        ArrayList<String> typeVarNames = new ArrayList<String>();
        do {
            typeVarNames.add(this.parseIdentifier());
        } while (this.maybeEat(","));
        this.eat(">", "',' or '>'");
        String[] tvs = new String[typeVarNames.size()];
        typeVarNames.toArray(tvs);
        return tvs;
    }

    public TypePatternList maybeParseTypeParameterList() {
        if (!this.maybeEat("<")) {
            return null;
        }
        ArrayList<TypePattern> typePats = new ArrayList<TypePattern>();
        do {
            TypePattern tp = this.parseTypePattern(true, false);
            typePats.add(tp);
        } while (this.maybeEat(","));
        this.eat(">");
        TypePattern[] tps = new TypePattern[typePats.size()];
        typePats.toArray(tps);
        return new TypePatternList(tps);
    }

    public TypeVariablePattern parseTypeVariable() {
        TypePattern upperBound = null;
        TypePattern[] additionalInterfaceBounds = null;
        TypePattern lowerBound = null;
        String typeVariableName = this.parseIdentifier();
        if (this.maybeEatIdentifier("extends")) {
            upperBound = this.parseTypePattern();
            additionalInterfaceBounds = this.maybeParseAdditionalInterfaceBounds();
        } else if (this.maybeEatIdentifier("super")) {
            lowerBound = this.parseTypePattern();
        }
        return new TypeVariablePattern(typeVariableName, upperBound, additionalInterfaceBounds, lowerBound);
    }

    private TypePattern[] maybeParseAdditionalInterfaceBounds() {
        ArrayList<TypePattern> boundsList = new ArrayList<TypePattern>();
        while (this.maybeEat("&")) {
            TypePattern tp = this.parseTypePattern();
            boundsList.add(tp);
        }
        if (boundsList.size() == 0) {
            return null;
        }
        TypePattern[] ret = new TypePattern[boundsList.size()];
        boundsList.toArray(ret);
        return ret;
    }

    public String parsePossibleStringSequence(boolean shouldEnd) {
        StringBuffer result = new StringBuffer();
        IToken token = this.tokenSource.next();
        if (token.getLiteralKind() == null) {
            throw new ParserException("string", token);
        }
        while (token.getLiteralKind().equals("string")) {
            result.append(token.getString());
            boolean plus = this.maybeEat("+");
            if (!plus) break;
            token = this.tokenSource.next();
            if (token.getLiteralKind() != null) continue;
            throw new ParserException("string", token);
        }
        this.eatIdentifier(";");
        IToken t = this.tokenSource.next();
        if (shouldEnd && t != IToken.EOF) {
            throw new ParserException("<string>;", token);
        }
        int currentIndex = this.tokenSource.getIndex();
        this.tokenSource.setIndex(currentIndex - 1);
        return result.toString();
    }

    public String parseStringLiteral() {
        IToken token = this.tokenSource.next();
        String literalKind = token.getLiteralKind();
        if (literalKind == "string") {
            return token.getString();
        }
        throw new ParserException("string", token);
    }

    public String parseIdentifier() {
        IToken token = this.tokenSource.next();
        if (token.isIdentifier()) {
            return token.getString();
        }
        throw new ParserException("identifier", token);
    }

    public void eatIdentifier(String expectedValue) {
        IToken next = this.tokenSource.next();
        if (!next.getString().equals(expectedValue)) {
            throw new ParserException(expectedValue, next);
        }
    }

    public boolean maybeEatIdentifier(String expectedValue) {
        IToken next = this.tokenSource.peek();
        if (next.getString().equals(expectedValue)) {
            this.tokenSource.next();
            return true;
        }
        return false;
    }

    public void eat(String expectedValue) {
        this.eat(expectedValue, expectedValue);
    }

    private void eat(String expectedValue, String expectedMessage) {
        IToken next = this.nextToken();
        if (next.getString() != expectedValue) {
            if (expectedValue.equals(">") && next.getString().startsWith(">")) {
                this.pendingRightArrows = BasicToken.makeLiteral(next.getString().substring(1).intern(), "string", next.getStart() + 1, next.getEnd());
                return;
            }
            throw new ParserException(expectedMessage, next);
        }
    }

    private IToken nextToken() {
        if (this.pendingRightArrows != null) {
            IToken ret = this.pendingRightArrows;
            this.pendingRightArrows = null;
            return ret;
        }
        return this.tokenSource.next();
    }

    public boolean maybeEatAdjacent(String token) {
        IToken next = this.tokenSource.peek();
        if (next.getString() == token && this.isAdjacent(this.tokenSource.peek(-1), next)) {
            this.tokenSource.next();
            return true;
        }
        return false;
    }

    public boolean maybeEat(String token) {
        IToken next = this.tokenSource.peek();
        if (next.getString() == token) {
            this.tokenSource.next();
            return true;
        }
        return false;
    }

    public String maybeEatIdentifier() {
        IToken next = this.tokenSource.peek();
        if (next.isIdentifier()) {
            this.tokenSource.next();
            return next.getString();
        }
        return null;
    }

    public boolean peek(String token) {
        IToken next = this.tokenSource.peek();
        return next.getString() == token;
    }

    public void checkEof() {
        IToken last = this.tokenSource.next();
        if (last != IToken.EOF) {
            throw new ParserException("unexpected pointcut element: " + last.toString(), last);
        }
    }

    public PatternParser(String data) {
        this(BasicTokenSource.makeTokenSource(data, null));
    }

    public PatternParser(String data, ISourceContext context) {
        this(BasicTokenSource.makeTokenSource(data, context));
    }
}


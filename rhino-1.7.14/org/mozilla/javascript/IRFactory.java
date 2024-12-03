/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Decompiler;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.BigIntLiteral;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DestructuringForm;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.EmptyExpression;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.GeneratorExpression;
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Label;
import org.mozilla.javascript.ast.LabeledStatement;
import org.mozilla.javascript.ast.LetNode;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.RegExpLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.Symbol;
import org.mozilla.javascript.ast.TaggedTemplateLiteral;
import org.mozilla.javascript.ast.TemplateCharacters;
import org.mozilla.javascript.ast.TemplateLiteral;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.UpdateExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;
import org.mozilla.javascript.ast.XmlDotQuery;
import org.mozilla.javascript.ast.XmlElemRef;
import org.mozilla.javascript.ast.XmlExpression;
import org.mozilla.javascript.ast.XmlFragment;
import org.mozilla.javascript.ast.XmlLiteral;
import org.mozilla.javascript.ast.XmlMemberGet;
import org.mozilla.javascript.ast.XmlPropRef;
import org.mozilla.javascript.ast.XmlRef;
import org.mozilla.javascript.ast.XmlString;
import org.mozilla.javascript.ast.Yield;

public final class IRFactory
extends Parser {
    private static final int LOOP_DO_WHILE = 0;
    private static final int LOOP_WHILE = 1;
    private static final int LOOP_FOR = 2;
    private static final int ALWAYS_TRUE_BOOLEAN = 1;
    private static final int ALWAYS_FALSE_BOOLEAN = -1;
    private Decompiler decompiler = new Decompiler();

    public IRFactory() {
    }

    public IRFactory(CompilerEnvirons env) {
        this(env, env.getErrorReporter());
    }

    public IRFactory(CompilerEnvirons env, ErrorReporter errorReporter) {
        super(env, errorReporter);
    }

    public ScriptNode transformTree(AstRoot root) {
        this.currentScriptOrFn = root;
        this.inUseStrictDirective = root.isInStrictMode();
        int sourceStartOffset = this.decompiler.getCurrentOffset();
        ScriptNode script = (ScriptNode)this.transform(root);
        int sourceEndOffset = this.decompiler.getCurrentOffset();
        script.setEncodedSourceBounds(sourceStartOffset, sourceEndOffset);
        if (this.compilerEnv.isGeneratingSource()) {
            script.setEncodedSource(this.decompiler.getEncodedSource());
        }
        this.decompiler = null;
        return script;
    }

    public Node transform(AstNode node) {
        switch (node.getType()) {
            case 161: {
                return this.transformArrayComp((ArrayComprehension)node);
            }
            case 66: {
                return this.transformArrayLiteral((ArrayLiteral)node);
            }
            case 83: {
                return this.transformBigInt((BigIntLiteral)node);
            }
            case 133: {
                return this.transformBlock(node);
            }
            case 124: {
                return this.transformBreak((BreakStatement)node);
            }
            case 38: {
                return this.transformFunctionCall((FunctionCall)node);
            }
            case 125: {
                return this.transformContinue((ContinueStatement)node);
            }
            case 122: {
                return this.transformDoLoop((DoLoop)node);
            }
            case 132: 
            case 165: {
                return node;
            }
            case 123: {
                if (node instanceof ForInLoop) {
                    return this.transformForInLoop((ForInLoop)node);
                }
                return this.transformForLoop((ForLoop)node);
            }
            case 113: {
                return this.transformFunction((FunctionNode)node);
            }
            case 166: {
                return this.transformGenExpr((GeneratorExpression)node);
            }
            case 36: {
                return this.transformElementGet((ElementGet)node);
            }
            case 33: {
                return this.transformPropertyGet((PropertyGet)node);
            }
            case 106: {
                return this.transformCondExpr((ConditionalExpression)node);
            }
            case 116: {
                return this.transformIf((IfStatement)node);
            }
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 164: {
                return this.transformLiteral(node);
            }
            case 39: {
                return this.transformName((Name)node);
            }
            case 40: {
                return this.transformNumber((NumberLiteral)node);
            }
            case 30: {
                return this.transformNewExpr((NewExpression)node);
            }
            case 67: {
                return this.transformObjectLiteral((ObjectLiteral)node);
            }
            case 170: {
                return this.transformTemplateLiteral((TemplateLiteral)node);
            }
            case 173: {
                return this.transformTemplateLiteralCall((TaggedTemplateLiteral)node);
            }
            case 48: {
                return this.transformRegExp((RegExpLiteral)node);
            }
            case 4: {
                return this.transformReturn((ReturnStatement)node);
            }
            case 140: {
                return this.transformScript((ScriptNode)node);
            }
            case 41: {
                return this.transformString((StringLiteral)node);
            }
            case 118: {
                return this.transformSwitch((SwitchStatement)node);
            }
            case 50: {
                return this.transformThrow((ThrowStatement)node);
            }
            case 84: {
                return this.transformTry((TryStatement)node);
            }
            case 121: {
                return this.transformWhileLoop((WhileLoop)node);
            }
            case 127: {
                return this.transformWith((WithStatement)node);
            }
            case 73: 
            case 169: {
                return this.transformYield((Yield)node);
            }
        }
        if (node instanceof ExpressionStatement) {
            return this.transformExprStmt((ExpressionStatement)node);
        }
        if (node instanceof Assignment) {
            return this.transformAssignment((Assignment)node);
        }
        if (node instanceof UnaryExpression) {
            return this.transformUnary((UnaryExpression)node);
        }
        if (node instanceof UpdateExpression) {
            return this.transformUpdate((UpdateExpression)node);
        }
        if (node instanceof XmlMemberGet) {
            return this.transformXmlMemberGet((XmlMemberGet)node);
        }
        if (node instanceof InfixExpression) {
            return this.transformInfix((InfixExpression)node);
        }
        if (node instanceof VariableDeclaration) {
            return this.transformVariables((VariableDeclaration)node);
        }
        if (node instanceof ParenthesizedExpression) {
            return this.transformParenExpr((ParenthesizedExpression)node);
        }
        if (node instanceof LabeledStatement) {
            return this.transformLabeledStatement((LabeledStatement)node);
        }
        if (node instanceof LetNode) {
            return this.transformLetNode((LetNode)node);
        }
        if (node instanceof XmlRef) {
            return this.transformXmlRef((XmlRef)node);
        }
        if (node instanceof XmlLiteral) {
            return this.transformXmlLiteral((XmlLiteral)node);
        }
        throw new IllegalArgumentException("Can't transform: " + node);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformArrayComp(ArrayComprehension node) {
        int lineno = node.getLineno();
        Scope scopeNode = this.createScopeNode(161, lineno);
        String arrayName = this.currentScriptOrFn.getNextTempName();
        this.pushScope(scopeNode);
        try {
            this.defineSymbol(157, arrayName, false);
            Node block = new Node(133, lineno);
            Node newArray = this.createCallOrNew(30, this.createName("Array"));
            Node init = new Node(137, this.createAssignment(93, this.createName(arrayName), newArray), lineno);
            block.addChildToBack(init);
            block.addChildToBack(this.arrayCompTransformHelper(node, arrayName));
            scopeNode.addChildToBack(block);
            scopeNode.addChildToBack(this.createName(arrayName));
            Scope scope = scopeNode;
            return scope;
        }
        finally {
            this.popScope();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node arrayCompTransformHelper(ArrayComprehension node, String arrayName) {
        int i;
        this.decompiler.addToken(86);
        int lineno = node.getLineno();
        Node expr = this.transform(node.getResult());
        List<ArrayComprehensionLoop> loops = node.getLoops();
        int numLoops = loops.size();
        Node[] iterators = new Node[numLoops];
        Node[] iteratedObjs = new Node[numLoops];
        for (int i2 = 0; i2 < numLoops; ++i2) {
            ArrayComprehensionLoop acl = loops.get(i2);
            this.decompiler.addName(" ");
            this.decompiler.addToken(123);
            if (acl.isForEach()) {
                this.decompiler.addName("each ");
            }
            this.decompiler.addToken(90);
            AstNode iter = acl.getIterator();
            String name = null;
            if (iter.getType() == 39) {
                name = iter.getString();
                this.decompiler.addName(name);
            } else {
                this.decompile(iter);
                name = this.currentScriptOrFn.getNextTempName();
                this.defineSymbol(90, name, false);
                expr = IRFactory.createBinary(92, this.createAssignment(93, iter, this.createName(name)), expr);
            }
            Node init = this.createName(name);
            this.defineSymbol(157, name, false);
            iterators[i2] = init;
            if (acl.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            iteratedObjs[i2] = this.transform(acl.getIteratedObject());
            this.decompiler.addToken(91);
        }
        Node call = this.createCallOrNew(38, this.createPropertyGet(this.createName(arrayName), null, "push", 0));
        Node body = new Node(137, call, lineno);
        if (node.getFilter() != null) {
            this.decompiler.addName(" ");
            this.decompiler.addToken(116);
            this.decompiler.addToken(90);
            body = IRFactory.createIf(this.transform(node.getFilter()), body, null, lineno);
            this.decompiler.addToken(91);
        }
        int pushed = 0;
        try {
            for (i = numLoops - 1; i >= 0; --i) {
                ArrayComprehensionLoop acl = loops.get(i);
                Scope loop = this.createLoopNode(null, acl.getLineno());
                this.pushScope(loop);
                ++pushed;
                body = this.createForIn(157, loop, iterators[i], iteratedObjs[i], body, acl.isForEach(), acl.isForOf());
            }
        }
        finally {
            for (i = 0; i < pushed; ++i) {
                this.popScope();
            }
        }
        this.decompiler.addToken(87);
        call.addChildToBack(expr);
        return body;
    }

    private Node transformArrayLiteral(ArrayLiteral node) {
        if (node.isDestructuring()) {
            return node;
        }
        this.decompiler.addToken(86);
        List<AstNode> elems = node.getElements();
        Node array = new Node(66);
        ArrayList<Integer> skipIndexes = null;
        for (int i = 0; i < elems.size(); ++i) {
            AstNode elem = elems.get(i);
            if (elem.getType() != 132) {
                array.addChildToBack(this.transform(elem));
            } else {
                if (skipIndexes == null) {
                    skipIndexes = new ArrayList<Integer>();
                }
                skipIndexes.add(i);
            }
            if (i >= elems.size() - 1) continue;
            this.decompiler.addToken(92);
        }
        this.decompiler.addToken(87);
        array.putIntProp(21, node.getDestructuringLength());
        if (skipIndexes != null) {
            int[] skips = new int[skipIndexes.size()];
            for (int i = 0; i < skipIndexes.size(); ++i) {
                skips[i] = (Integer)skipIndexes.get(i);
            }
            array.putProp(11, skips);
        }
        return array;
    }

    private Node transformAssignment(Assignment node) {
        AstNode right = node.getRight();
        AstNode left = this.removeParens(node.getLeft());
        left = this.transformAssignmentLeft(node, left, right);
        Node target = null;
        if (this.isDestructuring(left)) {
            this.decompile(left);
            target = left;
        } else {
            target = this.transform(left);
        }
        this.decompiler.addToken(node.getType());
        return this.createAssignment(node.getType(), target, this.transform(right));
    }

    private AstNode transformAssignmentLeft(Assignment node, AstNode left, AstNode right) {
        if (right.getType() == 42 && node.getType() == 93 && left instanceof Name && right instanceof KeywordLiteral) {
            String identifier = ((Name)left).getIdentifier();
            for (AstNode p = node.getParent(); p != null; p = p.getParent()) {
                Name functionName;
                if (!(p instanceof FunctionNode) || (functionName = ((FunctionNode)p).getFunctionName()) == null || !functionName.getIdentifier().equals(identifier)) continue;
                PropertyGet propertyGet = new PropertyGet();
                KeywordLiteral thisKeyword = new KeywordLiteral();
                thisKeyword.setType(43);
                propertyGet.setLeft(thisKeyword);
                propertyGet.setRight(left);
                node.setLeft(propertyGet);
                return propertyGet;
            }
        }
        return left;
    }

    private Node transformBigInt(BigIntLiteral node) {
        this.decompiler.addBigInt(node.getBigInt());
        return node;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformBlock(AstNode node) {
        if (node instanceof Scope) {
            this.pushScope((Scope)node);
        }
        try {
            ArrayList<Node> kids = new ArrayList<Node>();
            for (Node kid : node) {
                kids.add(this.transform((AstNode)kid));
            }
            node.removeChildren();
            for (Node kid : kids) {
                node.addChildToBack(kid);
            }
            AstNode astNode = node;
            return astNode;
        }
        finally {
            if (node instanceof Scope) {
                this.popScope();
            }
        }
    }

    private Node transformBreak(BreakStatement node) {
        this.decompiler.addToken(124);
        if (node.getBreakLabel() != null) {
            this.decompiler.addName(node.getBreakLabel().getIdentifier());
        }
        this.decompiler.addEOL(85);
        return node;
    }

    private Node transformCondExpr(ConditionalExpression node) {
        Node test = this.transform(node.getTestExpression());
        this.decompiler.addToken(106);
        Node ifTrue = this.transform(node.getTrueExpression());
        this.decompiler.addToken(107);
        Node ifFalse = this.transform(node.getFalseExpression());
        return IRFactory.createCondExpr(test, ifTrue, ifFalse);
    }

    private Node transformContinue(ContinueStatement node) {
        this.decompiler.addToken(125);
        if (node.getLabel() != null) {
            this.decompiler.addName(node.getLabel().getIdentifier());
        }
        this.decompiler.addEOL(85);
        return node;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformDoLoop(DoLoop loop) {
        loop.setType(136);
        this.pushScope(loop);
        try {
            this.decompiler.addToken(122);
            this.decompiler.addEOL(88);
            Node body = this.transform(loop.getBody());
            this.decompiler.addToken(89);
            this.decompiler.addToken(121);
            this.decompiler.addToken(90);
            Node cond = this.transform(loop.getCondition());
            this.decompiler.addToken(91);
            this.decompiler.addEOL(85);
            Node node = IRFactory.createLoop(loop, 0, body, cond, null, null);
            return node;
        }
        finally {
            this.popScope();
        }
    }

    private Node transformElementGet(ElementGet node) {
        Node target = this.transform(node.getTarget());
        this.decompiler.addToken(86);
        Node element = this.transform(node.getElement());
        this.decompiler.addToken(87);
        return new Node(36, target, element);
    }

    private Node transformExprStmt(ExpressionStatement node) {
        Node expr = this.transform(node.getExpression());
        this.decompiler.addEOL(85);
        return new Node(node.getType(), expr, node.getLineno());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformForInLoop(ForInLoop loop) {
        this.decompiler.addToken(123);
        if (loop.isForEach()) {
            this.decompiler.addName("each ");
        }
        this.decompiler.addToken(90);
        loop.setType(136);
        this.pushScope(loop);
        try {
            int declType = -1;
            AstNode iter = loop.getIterator();
            if (iter instanceof VariableDeclaration) {
                declType = ((VariableDeclaration)iter).getType();
            }
            Node lhs = this.transform(iter);
            if (loop.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            Node obj = this.transform(loop.getIteratedObject());
            this.decompiler.addToken(91);
            this.decompiler.addEOL(88);
            Node body = this.transform(loop.getBody());
            this.decompiler.addEOL(89);
            Node node = this.createForIn(declType, loop, lhs, obj, body, loop.isForEach(), loop.isForOf());
            return node;
        }
        finally {
            this.popScope();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformForLoop(ForLoop loop) {
        this.decompiler.addToken(123);
        this.decompiler.addToken(90);
        loop.setType(136);
        Scope savedScope = this.currentScope;
        this.currentScope = loop;
        try {
            Node init = this.transform(loop.getInitializer());
            this.decompiler.addToken(85);
            Node test = this.transform(loop.getCondition());
            this.decompiler.addToken(85);
            Node incr = this.transform(loop.getIncrement());
            this.decompiler.addToken(91);
            this.decompiler.addEOL(88);
            Node body = this.transform(loop.getBody());
            this.decompiler.addEOL(89);
            Node node = IRFactory.createFor(loop, init, test, incr, body);
            return node;
        }
        finally {
            this.currentScope = savedScope;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformFunction(FunctionNode fn) {
        int functionType = fn.getFunctionType();
        int start = this.decompiler.markFunctionStart(functionType);
        Node mexpr = this.decompileFunctionHeader(fn);
        int index = this.currentScriptOrFn.addFunction(fn);
        Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(this, fn);
        try {
            Node destructuring = (Node)fn.getProp(23);
            fn.removeProp(23);
            int lineno = fn.getBody().getLineno();
            ++this.nestingOfFunction;
            Node body = this.transform(fn.getBody());
            if (!fn.isExpressionClosure()) {
                this.decompiler.addToken(89);
            }
            fn.setEncodedSourceBounds(start, this.decompiler.markFunctionEnd(start));
            if (functionType != 2 && !fn.isExpressionClosure()) {
                this.decompiler.addToken(1);
            }
            if (destructuring != null) {
                body.addChildToFront(new Node(137, destructuring, lineno));
            }
            int syntheticType = fn.getFunctionType();
            Node pn = IRFactory.initFunction(fn, index, body, syntheticType);
            if (mexpr != null) {
                pn = this.createAssignment(93, mexpr, pn);
                if (syntheticType != 2) {
                    pn = IRFactory.createExprStatementNoReturn(pn, fn.getLineno());
                }
            }
            Node node = pn;
            return node;
        }
        finally {
            --this.nestingOfFunction;
            savedVars.restore();
        }
    }

    private Node transformFunctionCall(FunctionCall node) {
        Node call = this.createCallOrNew(38, this.transform(node.getTarget()));
        call.setLineno(node.getLineno());
        this.decompiler.addToken(90);
        List<AstNode> args = node.getArguments();
        for (int i = 0; i < args.size(); ++i) {
            AstNode arg = args.get(i);
            call.addChildToBack(this.transform(arg));
            if (i >= args.size() - 1) continue;
            this.decompiler.addToken(92);
        }
        this.decompiler.addToken(91);
        return call;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformGenExpr(GeneratorExpression node) {
        Node pn;
        FunctionNode fn = new FunctionNode();
        fn.setSourceName(this.currentScriptOrFn.getNextTempName());
        fn.setIsGenerator();
        fn.setFunctionType(2);
        fn.setRequiresActivation();
        int functionType = fn.getFunctionType();
        int start = this.decompiler.markFunctionStart(functionType);
        Node mexpr = this.decompileFunctionHeader(fn);
        int index = this.currentScriptOrFn.addFunction(fn);
        Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(this, fn);
        try {
            Node destructuring = (Node)fn.getProp(23);
            fn.removeProp(23);
            int lineno = node.lineno;
            ++this.nestingOfFunction;
            Node body = this.genExprTransformHelper(node);
            if (!fn.isExpressionClosure()) {
                this.decompiler.addToken(89);
            }
            fn.setEncodedSourceBounds(start, this.decompiler.markFunctionEnd(start));
            if (functionType != 2 && !fn.isExpressionClosure()) {
                this.decompiler.addToken(1);
            }
            if (destructuring != null) {
                body.addChildToFront(new Node(137, destructuring, lineno));
            }
            int syntheticType = fn.getFunctionType();
            pn = IRFactory.initFunction(fn, index, body, syntheticType);
            if (mexpr != null) {
                pn = this.createAssignment(93, mexpr, pn);
                if (syntheticType != 2) {
                    pn = IRFactory.createExprStatementNoReturn(pn, fn.getLineno());
                }
            }
        }
        finally {
            --this.nestingOfFunction;
            savedVars.restore();
        }
        Node call = this.createCallOrNew(38, pn);
        call.setLineno(node.getLineno());
        this.decompiler.addToken(90);
        this.decompiler.addToken(91);
        return call;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node genExprTransformHelper(GeneratorExpression node) {
        int i;
        this.decompiler.addToken(90);
        int lineno = node.getLineno();
        Node expr = this.transform(node.getResult());
        List<GeneratorExpressionLoop> loops = node.getLoops();
        int numLoops = loops.size();
        Node[] iterators = new Node[numLoops];
        Node[] iteratedObjs = new Node[numLoops];
        for (int i2 = 0; i2 < numLoops; ++i2) {
            GeneratorExpressionLoop acl = loops.get(i2);
            this.decompiler.addName(" ");
            this.decompiler.addToken(123);
            this.decompiler.addToken(90);
            AstNode iter = acl.getIterator();
            String name = null;
            if (iter.getType() == 39) {
                name = iter.getString();
                this.decompiler.addName(name);
            } else {
                this.decompile(iter);
                name = this.currentScriptOrFn.getNextTempName();
                this.defineSymbol(90, name, false);
                expr = IRFactory.createBinary(92, this.createAssignment(93, iter, this.createName(name)), expr);
            }
            Node init = this.createName(name);
            this.defineSymbol(157, name, false);
            iterators[i2] = init;
            if (acl.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            iteratedObjs[i2] = this.transform(acl.getIteratedObject());
            this.decompiler.addToken(91);
        }
        Node yield = new Node(73, expr, node.getLineno());
        Node body = new Node(137, yield, lineno);
        if (node.getFilter() != null) {
            this.decompiler.addName(" ");
            this.decompiler.addToken(116);
            this.decompiler.addToken(90);
            body = IRFactory.createIf(this.transform(node.getFilter()), body, null, lineno);
            this.decompiler.addToken(91);
        }
        int pushed = 0;
        try {
            for (i = numLoops - 1; i >= 0; --i) {
                GeneratorExpressionLoop acl = loops.get(i);
                Scope loop = this.createLoopNode(null, acl.getLineno());
                this.pushScope(loop);
                ++pushed;
                body = this.createForIn(157, loop, iterators[i], iteratedObjs[i], body, acl.isForEach(), acl.isForOf());
            }
        }
        finally {
            for (i = 0; i < pushed; ++i) {
                this.popScope();
            }
        }
        this.decompiler.addToken(91);
        return body;
    }

    private Node transformIf(IfStatement n) {
        this.decompiler.addToken(116);
        this.decompiler.addToken(90);
        Node cond = this.transform(n.getCondition());
        this.decompiler.addToken(91);
        this.decompiler.addEOL(88);
        Node ifTrue = this.transform(n.getThenPart());
        Node ifFalse = null;
        if (n.getElsePart() != null) {
            this.decompiler.addToken(89);
            this.decompiler.addToken(117);
            this.decompiler.addEOL(88);
            ifFalse = this.transform(n.getElsePart());
        }
        this.decompiler.addEOL(89);
        return IRFactory.createIf(cond, ifTrue, ifFalse, n.getLineno());
    }

    private Node transformInfix(InfixExpression node) {
        Node left = this.transform(node.getLeft());
        this.decompiler.addToken(node.getType());
        Node right = this.transform(node.getRight());
        if (node instanceof XmlDotQuery) {
            this.decompiler.addToken(91);
        }
        return IRFactory.createBinary(node.getType(), left, right);
    }

    private Node transformLabeledStatement(LabeledStatement ls) {
        Label label = ls.getFirstLabel();
        List<Label> labels = ls.getLabels();
        this.decompiler.addName(label.getName());
        if (labels.size() > 1) {
            for (Label lb : labels.subList(1, labels.size())) {
                this.decompiler.addEOL(107);
                this.decompiler.addName(lb.getName());
            }
        }
        if (ls.getStatement().getType() == 133) {
            this.decompiler.addToken(67);
            this.decompiler.addEOL(88);
        } else {
            this.decompiler.addEOL(107);
        }
        Node statement = this.transform(ls.getStatement());
        if (ls.getStatement().getType() == 133) {
            this.decompiler.addEOL(89);
        }
        Node breakTarget = Node.newTarget();
        Node block = new Node(133, (Node)label, statement, breakTarget);
        label.target = breakTarget;
        return block;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformLetNode(LetNode node) {
        this.pushScope(node);
        try {
            boolean letExpr;
            this.decompiler.addToken(157);
            this.decompiler.addToken(90);
            Node vars = this.transformVariableInitializers(node.getVariables());
            this.decompiler.addToken(91);
            node.addChildToBack(vars);
            boolean bl = letExpr = node.getType() == 162;
            if (node.getBody() != null) {
                if (letExpr) {
                    this.decompiler.addName(" ");
                } else {
                    this.decompiler.addEOL(88);
                }
                node.addChildToBack(this.transform(node.getBody()));
                if (!letExpr) {
                    this.decompiler.addEOL(89);
                }
            }
            LetNode letNode = node;
            return letNode;
        }
        finally {
            this.popScope();
        }
    }

    private Node transformLiteral(AstNode node) {
        this.decompiler.addToken(node.getType());
        return node;
    }

    private Node transformName(Name node) {
        this.decompiler.addName(node.getIdentifier());
        return node;
    }

    private Node transformNewExpr(NewExpression node) {
        this.decompiler.addToken(30);
        Node nx = this.createCallOrNew(30, this.transform(node.getTarget()));
        nx.setLineno(node.getLineno());
        List<AstNode> args = node.getArguments();
        this.decompiler.addToken(90);
        for (int i = 0; i < args.size(); ++i) {
            AstNode arg = args.get(i);
            nx.addChildToBack(this.transform(arg));
            if (i >= args.size() - 1) continue;
            this.decompiler.addToken(92);
        }
        this.decompiler.addToken(91);
        if (node.getInitializer() != null) {
            nx.addChildToBack(this.transformObjectLiteral(node.getInitializer()));
        }
        return nx;
    }

    private Node transformNumber(NumberLiteral node) {
        this.decompiler.addNumber(node.getNumber());
        return node;
    }

    private Node transformObjectLiteral(ObjectLiteral node) {
        Object[] properties;
        if (node.isDestructuring()) {
            return node;
        }
        this.decompiler.addToken(88);
        List<ObjectProperty> elems = node.getElements();
        Node object = new Node(67);
        if (elems.isEmpty()) {
            properties = ScriptRuntime.emptyArgs;
        } else {
            int size = elems.size();
            int i = 0;
            properties = new Object[size];
            for (ObjectProperty prop : elems) {
                if (prop.isGetterMethod()) {
                    this.decompiler.addToken(155);
                } else if (prop.isSetterMethod()) {
                    this.decompiler.addToken(156);
                } else if (prop.isNormalMethod()) {
                    this.decompiler.addToken(167);
                }
                properties[i++] = this.getPropKey(prop.getLeft());
                if (!prop.isMethod()) {
                    this.decompiler.addToken(67);
                }
                Node right = this.transform(prop.getRight());
                if (prop.isGetterMethod()) {
                    right = IRFactory.createUnary(155, right);
                } else if (prop.isSetterMethod()) {
                    right = IRFactory.createUnary(156, right);
                } else if (prop.isNormalMethod()) {
                    right = IRFactory.createUnary(167, right);
                }
                object.addChildToBack(right);
                if (i >= size) continue;
                this.decompiler.addToken(92);
            }
        }
        this.decompiler.addToken(89);
        object.putProp(12, properties);
        return object;
    }

    private Object getPropKey(Node id) {
        Object key;
        if (id instanceof Name) {
            String s = ((Name)id).getIdentifier();
            this.decompiler.addName(s);
            key = ScriptRuntime.getIndexObject(s);
        } else if (id instanceof StringLiteral) {
            String s = ((StringLiteral)id).getValue();
            this.decompiler.addString(s);
            key = ScriptRuntime.getIndexObject(s);
        } else if (id instanceof NumberLiteral) {
            double n = ((NumberLiteral)id).getNumber();
            this.decompiler.addNumber(n);
            key = ScriptRuntime.getIndexObject(n);
        } else {
            throw Kit.codeBug();
        }
        return key;
    }

    private Node transformParenExpr(ParenthesizedExpression node) {
        AstNode expr = node.getExpression();
        this.decompiler.addToken(90);
        int count = 1;
        while (expr instanceof ParenthesizedExpression) {
            this.decompiler.addToken(90);
            ++count;
            expr = ((ParenthesizedExpression)expr).getExpression();
        }
        Node result = this.transform(expr);
        for (int i = 0; i < count; ++i) {
            this.decompiler.addToken(91);
        }
        result.putProp(19, Boolean.TRUE);
        return result;
    }

    private Node transformPropertyGet(PropertyGet node) {
        Node target = this.transform(node.getTarget());
        String name = node.getProperty().getIdentifier();
        this.decompiler.addToken(112);
        this.decompiler.addName(name);
        return this.createPropertyGet(target, null, name, 0);
    }

    private Node transformTemplateLiteral(TemplateLiteral node) {
        this.decompiler.addToken(170);
        List<AstNode> elems = node.getElements();
        Node pn = Node.newString("");
        for (int i = 0; i < elems.size(); ++i) {
            AstNode elem = elems.get(i);
            if (elem.getType() != 171) {
                this.decompiler.addToken(172);
                pn = IRFactory.createBinary(21, pn, this.transform(elem));
                this.decompiler.addToken(89);
                continue;
            }
            TemplateCharacters chars = (TemplateCharacters)elem;
            this.decompiler.addTemplateLiteral(chars.getRawValue());
            String value = chars.getValue();
            if (value.length() <= 0) continue;
            pn = IRFactory.createBinary(21, pn, Node.newString(value));
        }
        this.decompiler.addToken(170);
        return pn;
    }

    private Node transformTemplateLiteralCall(TaggedTemplateLiteral node) {
        Node call = this.createCallOrNew(38, this.transform(node.getTarget()));
        call.setLineno(node.getLineno());
        this.decompiler.addToken(170);
        TemplateLiteral templateLiteral = (TemplateLiteral)node.getTemplateLiteral();
        List<AstNode> elems = templateLiteral.getElements();
        call.addChildToBack(templateLiteral);
        for (int i = 0; i < elems.size(); ++i) {
            AstNode elem = elems.get(i);
            if (elem.getType() != 171) {
                this.decompiler.addToken(172);
                call.addChildToBack(this.transform(elem));
                this.decompiler.addToken(89);
                continue;
            }
            TemplateCharacters chars = (TemplateCharacters)elem;
            this.decompiler.addTemplateLiteral(chars.getRawValue());
        }
        this.currentScriptOrFn.addTemplateLiteral(templateLiteral);
        this.decompiler.addToken(170);
        return call;
    }

    private Node transformRegExp(RegExpLiteral node) {
        this.decompiler.addRegexp(node.getValue(), node.getFlags());
        this.currentScriptOrFn.addRegExp(node);
        return node;
    }

    private Node transformReturn(ReturnStatement node) {
        AstNode rv;
        Node value;
        boolean expClosure = Boolean.TRUE.equals(node.getProp(25));
        boolean isArrow = Boolean.TRUE.equals(node.getProp(27));
        if (expClosure) {
            if (!isArrow) {
                this.decompiler.addName(" ");
            }
        } else {
            this.decompiler.addToken(4);
        }
        Node node2 = value = (rv = node.getReturnValue()) == null ? null : this.transform(rv);
        if (!expClosure) {
            this.decompiler.addEOL(85);
        }
        return rv == null ? new Node(4, node.getLineno()) : new Node(4, value, node.getLineno());
    }

    private Node transformScript(ScriptNode node) {
        this.decompiler.addToken(140);
        if (this.currentScope != null) {
            Kit.codeBug();
        }
        this.currentScope = node;
        Node body = new Node(133);
        for (Node kid : node) {
            body.addChildToBack(this.transform((AstNode)kid));
        }
        node.removeChildren();
        Node children = body.getFirstChild();
        if (children != null) {
            node.addChildrenToBack(children);
        }
        return node;
    }

    private Node transformString(StringLiteral node) {
        this.decompiler.addString(node.getValue());
        return Node.newString(node.getValue());
    }

    private Node transformSwitch(SwitchStatement node) {
        this.decompiler.addToken(118);
        this.decompiler.addToken(90);
        Node switchExpr = this.transform(node.getExpression());
        this.decompiler.addToken(91);
        node.addChildToBack(switchExpr);
        Node block = new Node(133, (Node)node, node.getLineno());
        this.decompiler.addEOL(88);
        for (SwitchCase sc : node.getCases()) {
            AstNode expr = sc.getExpression();
            Node caseExpr = null;
            if (expr != null) {
                this.decompiler.addToken(119);
                caseExpr = this.transform(expr);
            } else {
                this.decompiler.addToken(120);
            }
            this.decompiler.addEOL(107);
            List<AstNode> stmts = sc.getStatements();
            Block body = new Block();
            if (stmts != null) {
                for (AstNode kid : stmts) {
                    body.addChildToBack(this.transform(kid));
                }
            }
            IRFactory.addSwitchCase(block, caseExpr, body);
        }
        this.decompiler.addEOL(89);
        IRFactory.closeSwitch(block);
        return block;
    }

    private Node transformThrow(ThrowStatement node) {
        this.decompiler.addToken(50);
        Node value = this.transform(node.getExpression());
        this.decompiler.addEOL(85);
        return new Node(50, value, node.getLineno());
    }

    private Node transformTry(TryStatement node) {
        this.decompiler.addToken(84);
        this.decompiler.addEOL(88);
        Node tryBlock = this.transform(node.getTryBlock());
        this.decompiler.addEOL(89);
        Block catchBlocks = new Block();
        for (CatchClause cc : node.getCatchClauses()) {
            this.decompiler.addToken(128);
            this.decompiler.addToken(90);
            String varName = cc.getVarName().getIdentifier();
            this.decompiler.addName(varName);
            Node catchCond = null;
            AstNode ccc = cc.getCatchCondition();
            if (ccc != null) {
                this.decompiler.addName(" ");
                this.decompiler.addToken(116);
                catchCond = this.transform(ccc);
            } else {
                catchCond = new EmptyExpression();
            }
            this.decompiler.addToken(91);
            this.decompiler.addEOL(88);
            Node body = this.transform(cc.getBody());
            this.decompiler.addEOL(89);
            catchBlocks.addChildToBack(this.createCatch(varName, catchCond, body, cc.getLineno()));
        }
        Node finallyBlock = null;
        if (node.getFinallyBlock() != null) {
            this.decompiler.addToken(129);
            this.decompiler.addEOL(88);
            finallyBlock = this.transform(node.getFinallyBlock());
            this.decompiler.addEOL(89);
        }
        return this.createTryCatchFinally(tryBlock, catchBlocks, finallyBlock, node.getLineno());
    }

    private Node transformUnary(UnaryExpression node) {
        int type = node.getType();
        if (type == 76) {
            return this.transformDefaultXmlNamepace(node);
        }
        this.decompiler.addToken(type);
        Node child = this.transform(node.getOperand());
        return IRFactory.createUnary(type, child);
    }

    private Node transformUpdate(UpdateExpression node) {
        int type = node.getType();
        if (node.isPrefix()) {
            this.decompiler.addToken(type);
        }
        Node child = this.transform(node.getOperand());
        if (node.isPostfix()) {
            this.decompiler.addToken(type);
        }
        return IRFactory.createIncDec(type, node.isPostfix(), child);
    }

    private Node transformVariables(VariableDeclaration node) {
        this.decompiler.addToken(node.getType());
        this.transformVariableInitializers(node);
        AstNode parent = node.getParent();
        if (!(parent instanceof Loop) && !(parent instanceof LetNode)) {
            this.decompiler.addEOL(85);
        }
        return node;
    }

    private Node transformVariableInitializers(VariableDeclaration node) {
        List<VariableInitializer> vars = node.getVariables();
        int size = vars.size();
        int i = 0;
        for (VariableInitializer var : vars) {
            AstNode target = var.getTarget();
            AstNode init = var.getInitializer();
            Node left = null;
            if (var.isDestructuring()) {
                this.decompile(target);
                left = target;
            } else {
                left = this.transform(target);
            }
            Node right = null;
            if (init != null) {
                this.decompiler.addToken(93);
                right = this.transform(init);
            }
            if (var.isDestructuring()) {
                if (right == null) {
                    node.addChildToBack(left);
                } else {
                    Node d = this.createDestructuringAssignment(node.getType(), left, right);
                    node.addChildToBack(d);
                }
            } else {
                if (right != null) {
                    left.addChildToBack(right);
                }
                node.addChildToBack(left);
            }
            if (i++ >= size - 1) continue;
            this.decompiler.addToken(92);
        }
        return node;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node transformWhileLoop(WhileLoop loop) {
        this.decompiler.addToken(121);
        loop.setType(136);
        this.pushScope(loop);
        try {
            this.decompiler.addToken(90);
            Node cond = this.transform(loop.getCondition());
            this.decompiler.addToken(91);
            this.decompiler.addEOL(88);
            Node body = this.transform(loop.getBody());
            this.decompiler.addEOL(89);
            Node node = IRFactory.createLoop(loop, 1, body, cond, null, null);
            return node;
        }
        finally {
            this.popScope();
        }
    }

    private Node transformWith(WithStatement node) {
        this.decompiler.addToken(127);
        this.decompiler.addToken(90);
        Node expr = this.transform(node.getExpression());
        this.decompiler.addToken(91);
        this.decompiler.addEOL(88);
        Node stmt = this.transform(node.getStatement());
        this.decompiler.addEOL(89);
        return this.createWith(expr, stmt, node.getLineno());
    }

    private Node transformYield(Yield node) {
        Node kid;
        this.decompiler.addToken(node.getType());
        Node node2 = kid = node.getValue() == null ? null : this.transform(node.getValue());
        if (kid != null) {
            return new Node(node.getType(), kid, node.getLineno());
        }
        return new Node(node.getType(), node.getLineno());
    }

    private Node transformXmlLiteral(XmlLiteral node) {
        Node pnXML = new Node(30, node.getLineno());
        List<XmlFragment> frags = node.getFragments();
        XmlString first = (XmlString)frags.get(0);
        boolean anon = first.getXml().trim().startsWith("<>");
        pnXML.addChildToBack(this.createName(anon ? "XMLList" : "XML"));
        Node pn = null;
        for (XmlFragment frag : frags) {
            if (frag instanceof XmlString) {
                String xml = ((XmlString)frag).getXml();
                this.decompiler.addName(xml);
                if (pn == null) {
                    pn = IRFactory.createString(xml);
                    continue;
                }
                pn = IRFactory.createBinary(21, pn, IRFactory.createString(xml));
                continue;
            }
            XmlExpression xexpr = (XmlExpression)frag;
            boolean isXmlAttr = xexpr.isXmlAttribute();
            this.decompiler.addToken(88);
            Node expr = xexpr.getExpression() instanceof EmptyExpression ? IRFactory.createString("") : this.transform(xexpr.getExpression());
            this.decompiler.addToken(89);
            if (isXmlAttr) {
                expr = IRFactory.createUnary(77, expr);
                Node prepend = IRFactory.createBinary(21, IRFactory.createString("\""), expr);
                expr = IRFactory.createBinary(21, prepend, IRFactory.createString("\""));
            } else {
                expr = IRFactory.createUnary(78, expr);
            }
            pn = IRFactory.createBinary(21, pn, expr);
        }
        pnXML.addChildToBack(pn);
        return pnXML;
    }

    private Node transformXmlMemberGet(XmlMemberGet node) {
        int flags;
        XmlRef ref = node.getMemberRef();
        Node pn = this.transform(node.getLeft());
        int n = flags = ref.isAttributeAccess() ? 2 : 0;
        if (node.getType() == 147) {
            flags |= 4;
            this.decompiler.addToken(147);
        } else {
            this.decompiler.addToken(112);
        }
        return this.transformXmlRef(pn, ref, flags);
    }

    private Node transformXmlRef(XmlRef node) {
        int memberTypeFlags = node.isAttributeAccess() ? 2 : 0;
        return this.transformXmlRef(null, node, memberTypeFlags);
    }

    private Node transformXmlRef(Node pn, XmlRef node, int memberTypeFlags) {
        Name namespace;
        String ns;
        if ((memberTypeFlags & 2) != 0) {
            this.decompiler.addToken(151);
        }
        String string = ns = (namespace = node.getNamespace()) != null ? namespace.getIdentifier() : null;
        if (ns != null) {
            this.decompiler.addName(ns);
            this.decompiler.addToken(148);
        }
        if (node instanceof XmlPropRef) {
            String name = ((XmlPropRef)node).getPropName().getIdentifier();
            this.decompiler.addName(name);
            return this.createPropertyGet(pn, ns, name, memberTypeFlags);
        }
        this.decompiler.addToken(86);
        Node expr = this.transform(((XmlElemRef)node).getExpression());
        this.decompiler.addToken(87);
        return this.createElementGet(pn, ns, expr, memberTypeFlags);
    }

    private Node transformDefaultXmlNamepace(UnaryExpression node) {
        this.decompiler.addToken(120);
        this.decompiler.addName(" xml");
        this.decompiler.addName(" namespace");
        this.decompiler.addToken(93);
        Node child = this.transform(node.getOperand());
        return IRFactory.createUnary(76, child);
    }

    private static void addSwitchCase(Node switchBlock, Node caseExpression, Node statements) {
        if (switchBlock.getType() != 133) {
            throw Kit.codeBug();
        }
        Jump switchNode = (Jump)switchBlock.getFirstChild();
        if (switchNode.getType() != 118) {
            throw Kit.codeBug();
        }
        Node gotoTarget = Node.newTarget();
        if (caseExpression != null) {
            Jump caseNode = new Jump(119, caseExpression);
            caseNode.target = gotoTarget;
            switchNode.addChildToBack(caseNode);
        } else {
            switchNode.setDefault(gotoTarget);
        }
        switchBlock.addChildToBack(gotoTarget);
        switchBlock.addChildToBack(statements);
    }

    private static void closeSwitch(Node switchBlock) {
        Node switchBreakTarget;
        if (switchBlock.getType() != 133) {
            throw Kit.codeBug();
        }
        Jump switchNode = (Jump)switchBlock.getFirstChild();
        if (switchNode.getType() != 118) {
            throw Kit.codeBug();
        }
        switchNode.target = switchBreakTarget = Node.newTarget();
        Node defaultTarget = switchNode.getDefault();
        if (defaultTarget == null) {
            defaultTarget = switchBreakTarget;
        }
        switchBlock.addChildAfter(IRFactory.makeJump(5, defaultTarget), switchNode);
        switchBlock.addChildToBack(switchBreakTarget);
    }

    private static Node createExprStatementNoReturn(Node expr, int lineno) {
        return new Node(137, expr, lineno);
    }

    private static Node createString(String string) {
        return Node.newString(string);
    }

    private Node createCatch(String varName, Node catchCond, Node stmts, int lineno) {
        if (catchCond == null) {
            catchCond = new Node(132);
        }
        return new Node(128, this.createName(varName), catchCond, stmts, lineno);
    }

    private static Node initFunction(FunctionNode fnNode, int functionIndex, Node statements, int functionType) {
        Node lastStmt;
        Name name;
        fnNode.setFunctionType(functionType);
        fnNode.addChildToBack(statements);
        int functionCount = fnNode.getFunctionCount();
        if (functionCount != 0) {
            fnNode.setRequiresActivation();
        }
        if (functionType == 2 && (name = fnNode.getFunctionName()) != null && name.length() != 0 && fnNode.getSymbol(name.getIdentifier()) == null) {
            fnNode.putSymbol(new Symbol(113, name.getIdentifier()));
            Node setFn = new Node(137, new Node(8, Node.newString(49, name.getIdentifier()), new Node(64)));
            statements.addChildrenToFront(setFn);
        }
        if ((lastStmt = statements.getLastChild()) == null || lastStmt.getType() != 4) {
            statements.addChildToBack(new Node(4));
        }
        Node result = Node.newString(113, fnNode.getName());
        result.putIntProp(1, functionIndex);
        return result;
    }

    private Scope createLoopNode(Node loopLabel, int lineno) {
        Scope result = this.createScopeNode(136, lineno);
        if (loopLabel != null) {
            ((Jump)loopLabel).setLoop(result);
        }
        return result;
    }

    private static Node createFor(Scope loop, Node init, Node test, Node incr, Node body) {
        if (init.getType() == 157) {
            Scope let = Scope.splitScope(loop);
            let.setType(157);
            let.addChildrenToBack(init);
            let.addChildToBack(IRFactory.createLoop(loop, 2, body, test, new Node(132), incr));
            return let;
        }
        return IRFactory.createLoop(loop, 2, body, test, init, incr);
    }

    private static Node createLoop(Jump loop, int loopType, Node body, Node cond, Node init, Node incr) {
        Node bodyTarget = Node.newTarget();
        Node condTarget = Node.newTarget();
        if (loopType == 2 && cond.getType() == 132) {
            cond = new Node(45);
        }
        Jump IFEQ = new Jump(6, cond);
        IFEQ.target = bodyTarget;
        Node breakTarget = Node.newTarget();
        loop.addChildToBack(bodyTarget);
        loop.addChildrenToBack(body);
        if (loopType == 1 || loopType == 2) {
            loop.addChildrenToBack(new Node(132, loop.getLineno()));
        }
        loop.addChildToBack(condTarget);
        loop.addChildToBack(IFEQ);
        loop.addChildToBack(breakTarget);
        loop.target = breakTarget;
        Node continueTarget = condTarget;
        if (loopType == 1 || loopType == 2) {
            loop.addChildToFront(IRFactory.makeJump(5, condTarget));
            if (loopType == 2) {
                int initType = init.getType();
                if (initType != 132) {
                    if (initType != 126 && initType != 157) {
                        init = new Node(137, init);
                    }
                    loop.addChildToFront(init);
                }
                Node incrTarget = Node.newTarget();
                loop.addChildAfter(incrTarget, body);
                if (incr.getType() != 132) {
                    incr = new Node(137, incr);
                    loop.addChildAfter(incr, incrTarget);
                }
                continueTarget = incrTarget;
            }
        }
        loop.setContinue(continueTarget);
        return loop;
    }

    /*
     * Enabled aggressive block sorting
     */
    private Node createForIn(int declType, Node loop, Node lhs, Node obj, Node body, boolean isForEach, boolean isForOf) {
        Node assign;
        Node lvalue;
        int type;
        int destructuringLen;
        int destructuring;
        block13: {
            block12: {
                destructuring = -1;
                destructuringLen = 0;
                type = lhs.getType();
                if (type != 126 && type != 157) break block12;
                Node kid = lhs.getLastChild();
                int kidType = kid.getType();
                if (kidType == 66 || kidType == 67) {
                    type = destructuring = kidType;
                    lvalue = kid;
                    destructuringLen = 0;
                    if (kid instanceof ArrayLiteral) {
                        destructuringLen = ((ArrayLiteral)kid).getDestructuringLength();
                    }
                    break block13;
                } else {
                    if (kidType != 39) {
                        this.reportError("msg.bad.for.in.lhs");
                        return null;
                    }
                    lvalue = Node.newString(39, kid.getString());
                }
                break block13;
            }
            if (type == 66 || type == 67) {
                destructuring = type;
                lvalue = lhs;
                destructuringLen = 0;
                if (lhs instanceof ArrayLiteral) {
                    destructuringLen = ((ArrayLiteral)lhs).getDestructuringLength();
                }
            } else {
                lvalue = IRFactory.makeReference(lhs);
                if (lvalue == null) {
                    this.reportError("msg.bad.for.in.lhs");
                    return null;
                }
            }
        }
        Node localBlock = new Node(145);
        int initType = isForEach ? 59 : (isForOf ? 61 : (destructuring != -1 ? 60 : 58));
        Node init = new Node(initType, obj);
        init.putProp(3, localBlock);
        Node cond = new Node(62);
        cond.putProp(3, localBlock);
        Node id = new Node(63);
        id.putProp(3, localBlock);
        Node newBody = new Node(133);
        if (destructuring != -1) {
            assign = this.createDestructuringAssignment(declType, lvalue, id);
            if (!(isForEach || isForOf || destructuring != 67 && destructuringLen == 2)) {
                this.reportError("msg.bad.for.in.destruct");
            }
        } else {
            assign = this.simpleAssignment(lvalue, id);
        }
        newBody.addChildToBack(new Node(137, assign));
        newBody.addChildToBack(body);
        loop = IRFactory.createLoop((Jump)loop, 1, newBody, cond, null, null);
        loop.addChildToFront(init);
        if (type == 126 || type == 157) {
            loop.addChildToFront(lhs);
        }
        localBlock.addChildToBack(loop);
        return localBlock;
    }

    private Node createTryCatchFinally(Node tryBlock, Node catchBlocks, Node finallyBlock, int lineno) {
        boolean hasFinally;
        boolean bl = hasFinally = finallyBlock != null && (finallyBlock.getType() != 133 || finallyBlock.hasChildren());
        if (tryBlock.getType() == 133 && !tryBlock.hasChildren() && !hasFinally) {
            return tryBlock;
        }
        boolean hasCatch = catchBlocks.hasChildren();
        if (!hasFinally && !hasCatch) {
            return tryBlock;
        }
        Node handlerBlock = new Node(145);
        Jump pn = new Jump(84, tryBlock, lineno);
        pn.putProp(3, handlerBlock);
        if (hasCatch) {
            Node catchTarget;
            Node endCatch = Node.newTarget();
            pn.addChildToBack(IRFactory.makeJump(5, endCatch));
            pn.target = catchTarget = Node.newTarget();
            pn.addChildToBack(catchTarget);
            Node catchScopeBlock = new Node(145);
            Node cb = catchBlocks.getFirstChild();
            boolean hasDefault = false;
            int scopeIndex = 0;
            while (cb != null) {
                Node condStmt;
                int catchLineNo = cb.getLineno();
                Node name = cb.getFirstChild();
                Node cond = name.getNext();
                Node catchStatement = cond.getNext();
                cb.removeChild(name);
                cb.removeChild(cond);
                cb.removeChild(catchStatement);
                catchStatement.addChildToBack(new Node(3));
                catchStatement.addChildToBack(IRFactory.makeJump(5, endCatch));
                if (cond.getType() == 132) {
                    condStmt = catchStatement;
                    hasDefault = true;
                } else {
                    condStmt = IRFactory.createIf(cond, catchStatement, null, catchLineNo);
                }
                Node catchScope = new Node(57, name, IRFactory.createUseLocal(handlerBlock));
                catchScope.putProp(3, catchScopeBlock);
                catchScope.putIntProp(14, scopeIndex);
                catchScopeBlock.addChildToBack(catchScope);
                catchScopeBlock.addChildToBack(this.createWith(IRFactory.createUseLocal(catchScopeBlock), condStmt, catchLineNo));
                cb = cb.getNext();
                ++scopeIndex;
            }
            pn.addChildToBack(catchScopeBlock);
            if (!hasDefault) {
                Node rethrow = new Node(51);
                rethrow.putProp(3, handlerBlock);
                pn.addChildToBack(rethrow);
            }
            pn.addChildToBack(endCatch);
        }
        if (hasFinally) {
            Node finallyTarget = Node.newTarget();
            pn.setFinally(finallyTarget);
            pn.addChildToBack(IRFactory.makeJump(139, finallyTarget));
            Node finallyEnd = Node.newTarget();
            pn.addChildToBack(IRFactory.makeJump(5, finallyEnd));
            pn.addChildToBack(finallyTarget);
            Node fBlock = new Node(129, finallyBlock);
            fBlock.putProp(3, handlerBlock);
            pn.addChildToBack(fBlock);
            pn.addChildToBack(finallyEnd);
        }
        handlerBlock.addChildToBack(pn);
        return handlerBlock;
    }

    private Node createWith(Node obj, Node body, int lineno) {
        this.setRequiresActivation();
        Node result = new Node(133, lineno);
        result.addChildToBack(new Node(2, obj));
        Node bodyNode = new Node(127, body, lineno);
        result.addChildrenToBack(bodyNode);
        result.addChildToBack(new Node(3));
        return result;
    }

    private static Node createIf(Node cond, Node ifTrue, Node ifFalse, int lineno) {
        int condStatus = IRFactory.isAlwaysDefinedBoolean(cond);
        if (condStatus == 1) {
            return ifTrue;
        }
        if (condStatus == -1) {
            if (ifFalse != null) {
                return ifFalse;
            }
            return new Node(133, lineno);
        }
        Node result = new Node(133, lineno);
        Node ifNotTarget = Node.newTarget();
        Jump IFNE = new Jump(7, cond);
        IFNE.target = ifNotTarget;
        result.addChildToBack(IFNE);
        result.addChildrenToBack(ifTrue);
        if (ifFalse != null) {
            Node endTarget = Node.newTarget();
            result.addChildToBack(IRFactory.makeJump(5, endTarget));
            result.addChildToBack(ifNotTarget);
            result.addChildrenToBack(ifFalse);
            result.addChildToBack(endTarget);
        } else {
            result.addChildToBack(ifNotTarget);
        }
        return result;
    }

    private static Node createCondExpr(Node cond, Node ifTrue, Node ifFalse) {
        int condStatus = IRFactory.isAlwaysDefinedBoolean(cond);
        if (condStatus == 1) {
            return ifTrue;
        }
        if (condStatus == -1) {
            return ifFalse;
        }
        return new Node(106, cond, ifTrue, ifFalse);
    }

    private static Node createUnary(int nodeType, Node child) {
        int childType = child.getType();
        switch (nodeType) {
            case 31: {
                Node n;
                if (childType == 39) {
                    child.setType(49);
                    Node left = child;
                    Node right = Node.newString(child.getString());
                    n = new Node(nodeType, left, right);
                } else if (childType == 33 || childType == 36) {
                    Node left = child.getFirstChild();
                    Node right = child.getLastChild();
                    child.removeChild(left);
                    child.removeChild(right);
                    n = new Node(nodeType, left, right);
                } else if (childType == 68) {
                    Node ref = child.getFirstChild();
                    child.removeChild(ref);
                    n = new Node(70, ref);
                } else {
                    n = new Node(nodeType, new Node(45), child);
                }
                return n;
            }
            case 32: {
                if (childType != 39) break;
                child.setType(141);
                return child;
            }
            case 27: {
                if (childType != 40) break;
                int value = ScriptRuntime.toInt32(child.getDouble());
                child.setDouble(~value);
                return child;
            }
            case 29: {
                if (childType != 40) break;
                child.setDouble(-child.getDouble());
                return child;
            }
            case 26: {
                int status = IRFactory.isAlwaysDefinedBoolean(child);
                if (status == 0) break;
                int type = status == 1 ? 44 : 45;
                if (childType == 45 || childType == 44) {
                    child.setType(type);
                    return child;
                }
                return new Node(type);
            }
        }
        return new Node(nodeType, child);
    }

    private Node createCallOrNew(int nodeType, Node child) {
        String name;
        int type = 0;
        if (child.getType() == 39) {
            name = child.getString();
            if (name.equals("eval")) {
                type = 1;
            } else if (name.equals("With")) {
                type = 2;
            }
        } else if (child.getType() == 33 && (name = child.getLastChild().getString()).equals("eval")) {
            type = 1;
        }
        Node node = new Node(nodeType, child);
        if (type != 0) {
            this.setRequiresActivation();
            node.putIntProp(10, type);
        }
        return node;
    }

    private static Node createIncDec(int nodeType, boolean post, Node child) {
        child = IRFactory.makeReference(child);
        int childType = child.getType();
        switch (childType) {
            case 33: 
            case 36: 
            case 39: 
            case 68: {
                Node n = new Node(nodeType, child);
                int incrDecrMask = 0;
                if (nodeType == 111) {
                    incrDecrMask |= 1;
                }
                if (post) {
                    incrDecrMask |= 2;
                }
                n.putIntProp(13, incrDecrMask);
                return n;
            }
        }
        throw Kit.codeBug();
    }

    private Node createPropertyGet(Node target, String namespace, String name, int memberTypeFlags) {
        if (namespace == null && memberTypeFlags == 0) {
            if (target == null) {
                return this.createName(name);
            }
            this.checkActivationName(name, 33);
            if (ScriptRuntime.isSpecialProperty(name)) {
                Node ref = new Node(72, target);
                ref.putProp(17, name);
                return new Node(68, ref);
            }
            return new Node(33, target, Node.newString(name));
        }
        Node elem = Node.newString(name);
        return this.createMemberRefGet(target, namespace, elem, memberTypeFlags |= 1);
    }

    private Node createElementGet(Node target, String namespace, Node elem, int memberTypeFlags) {
        if (namespace == null && memberTypeFlags == 0) {
            if (target == null) {
                throw Kit.codeBug();
            }
            return new Node(36, target, elem);
        }
        return this.createMemberRefGet(target, namespace, elem, memberTypeFlags);
    }

    private Node createMemberRefGet(Node target, String namespace, Node elem, int memberTypeFlags) {
        Node nsNode = null;
        if (namespace != null) {
            nsNode = namespace.equals("*") ? new Node(42) : this.createName(namespace);
        }
        Node ref = target == null ? (namespace == null ? new Node(81, elem) : new Node(82, nsNode, elem)) : (namespace == null ? new Node(79, target, elem) : new Node(80, target, nsNode, elem));
        if (memberTypeFlags != 0) {
            ref.putIntProp(16, memberTypeFlags);
        }
        return new Node(68, ref);
    }

    private static Node createBinary(int nodeType, Node left, Node right) {
        switch (nodeType) {
            case 21: {
                if (left.type == 41) {
                    String s2;
                    if (right.type == 41) {
                        s2 = right.getString();
                    } else {
                        if (right.type != 40) break;
                        s2 = ScriptRuntime.numberToString(right.getDouble(), 10);
                    }
                    String s1 = left.getString();
                    left.setString(s1.concat(s2));
                    return left;
                }
                if (left.type != 40) break;
                if (right.type == 40) {
                    left.setDouble(left.getDouble() + right.getDouble());
                    return left;
                }
                if (right.type != 41) break;
                String s1 = ScriptRuntime.numberToString(left.getDouble(), 10);
                String s2 = right.getString();
                right.setString(s1.concat(s2));
                return right;
            }
            case 22: {
                if (left.type == 40) {
                    double ld = left.getDouble();
                    if (right.type == 40) {
                        left.setDouble(ld - right.getDouble());
                        return left;
                    }
                    if (ld != 0.0) break;
                    return new Node(29, right);
                }
                if (right.type != 40 || right.getDouble() != 0.0) break;
                return new Node(28, left);
            }
            case 23: {
                if (left.type == 40) {
                    double ld = left.getDouble();
                    if (right.type == 40) {
                        left.setDouble(ld * right.getDouble());
                        return left;
                    }
                    if (ld != 1.0) break;
                    return new Node(28, right);
                }
                if (right.type != 40 || right.getDouble() != 1.0) break;
                return new Node(28, left);
            }
            case 24: {
                if (right.type != 40) break;
                double rd = right.getDouble();
                if (left.type == 40) {
                    left.setDouble(left.getDouble() / rd);
                    return left;
                }
                if (rd != 1.0) break;
                return new Node(28, left);
            }
            case 109: {
                int leftStatus = IRFactory.isAlwaysDefinedBoolean(left);
                if (leftStatus == -1) {
                    return left;
                }
                if (leftStatus != 1) break;
                return right;
            }
            case 108: {
                int leftStatus = IRFactory.isAlwaysDefinedBoolean(left);
                if (leftStatus == 1) {
                    return left;
                }
                if (leftStatus != -1) break;
                return right;
            }
        }
        return new Node(nodeType, left, right);
    }

    private Node createAssignment(int assignType, Node left, Node right) {
        int assignOp;
        Node ref = IRFactory.makeReference(left);
        if (ref == null) {
            if (left.getType() == 66 || left.getType() == 67) {
                if (assignType != 93) {
                    this.reportError("msg.bad.destruct.op");
                    return right;
                }
                return this.createDestructuringAssignment(-1, left, right);
            }
            this.reportError("msg.bad.assign.left");
            return right;
        }
        left = ref;
        switch (assignType) {
            case 93: {
                return this.simpleAssignment(left, right);
            }
            case 94: {
                assignOp = 9;
                break;
            }
            case 95: {
                assignOp = 10;
                break;
            }
            case 96: {
                assignOp = 11;
                break;
            }
            case 97: {
                assignOp = 18;
                break;
            }
            case 98: {
                assignOp = 19;
                break;
            }
            case 99: {
                assignOp = 20;
                break;
            }
            case 100: {
                assignOp = 21;
                break;
            }
            case 101: {
                assignOp = 22;
                break;
            }
            case 102: {
                assignOp = 23;
                break;
            }
            case 103: {
                assignOp = 24;
                break;
            }
            case 104: {
                assignOp = 25;
                break;
            }
            case 105: {
                assignOp = 75;
                break;
            }
            default: {
                throw Kit.codeBug();
            }
        }
        int nodeType = left.getType();
        switch (nodeType) {
            case 39: {
                Node op = new Node(assignOp, left, right);
                Node lvalueLeft = Node.newString(49, left.getString());
                return new Node(8, lvalueLeft, op);
            }
            case 33: 
            case 36: {
                Node obj = left.getFirstChild();
                Node id = left.getLastChild();
                int type = nodeType == 33 ? 143 : 144;
                Node opLeft = new Node(142);
                Node op = new Node(assignOp, opLeft, right);
                return new Node(type, obj, id, op);
            }
            case 68: {
                ref = left.getFirstChild();
                this.checkMutableReference(ref);
                Node opLeft = new Node(142);
                Node op = new Node(assignOp, opLeft, right);
                return new Node(146, ref, op);
            }
        }
        throw Kit.codeBug();
    }

    private static Node createUseLocal(Node localBlock) {
        if (145 != localBlock.getType()) {
            throw Kit.codeBug();
        }
        Node result = new Node(54);
        result.putProp(3, localBlock);
        return result;
    }

    private static Jump makeJump(int type, Node target) {
        Jump n = new Jump(type);
        n.target = target;
        return n;
    }

    private static Node makeReference(Node node) {
        int type = node.getType();
        switch (type) {
            case 33: 
            case 36: 
            case 39: 
            case 68: {
                return node;
            }
            case 38: {
                node.setType(71);
                return new Node(68, node);
            }
        }
        return null;
    }

    private static int isAlwaysDefinedBoolean(Node node) {
        switch (node.getType()) {
            case 42: 
            case 44: {
                return -1;
            }
            case 45: {
                return 1;
            }
            case 40: {
                double num = node.getDouble();
                if (!Double.isNaN(num) && num != 0.0) {
                    return 1;
                }
                return -1;
            }
        }
        return 0;
    }

    boolean isDestructuring(Node n) {
        return n instanceof DestructuringForm && ((DestructuringForm)((Object)n)).isDestructuring();
    }

    Node decompileFunctionHeader(FunctionNode fn) {
        boolean noParen;
        Node mexpr = null;
        if (fn.getFunctionName() != null) {
            this.decompiler.addName(fn.getName());
        } else if (fn.getMemberExprNode() != null) {
            mexpr = this.transform(fn.getMemberExprNode());
        }
        boolean isArrow = fn.getFunctionType() == 4;
        boolean bl = noParen = isArrow && fn.getLp() == -1;
        if (!noParen) {
            this.decompiler.addToken(90);
        }
        List<AstNode> params = fn.getParams();
        for (int i = 0; i < params.size(); ++i) {
            this.decompile(params.get(i));
            if (i >= params.size() - 1) continue;
            this.decompiler.addToken(92);
        }
        if (!noParen) {
            this.decompiler.addToken(91);
        }
        if (isArrow) {
            this.decompiler.addToken(168);
        }
        if (!fn.isExpressionClosure()) {
            this.decompiler.addEOL(88);
        }
        return mexpr;
    }

    void decompile(AstNode node) {
        switch (node.getType()) {
            case 66: {
                this.decompileArrayLiteral((ArrayLiteral)node);
                break;
            }
            case 67: {
                this.decompileObjectLiteral((ObjectLiteral)node);
                break;
            }
            case 41: {
                this.decompiler.addString(((StringLiteral)node).getValue());
                break;
            }
            case 39: {
                this.decompiler.addName(((Name)node).getIdentifier());
                break;
            }
            case 40: {
                this.decompiler.addNumber(((NumberLiteral)node).getNumber());
                break;
            }
            case 83: {
                this.decompiler.addBigInt(((BigIntLiteral)node).getBigInt());
                break;
            }
            case 33: {
                this.decompilePropertyGet((PropertyGet)node);
                break;
            }
            case 132: {
                break;
            }
            case 36: {
                this.decompileElementGet((ElementGet)node);
                break;
            }
            case 43: {
                this.decompiler.addToken(node.getType());
                break;
            }
            default: {
                Kit.codeBug("unexpected token: " + Token.typeToName(node.getType()));
            }
        }
    }

    void decompileArrayLiteral(ArrayLiteral node) {
        this.decompiler.addToken(86);
        List<AstNode> elems = node.getElements();
        int size = elems.size();
        for (int i = 0; i < size; ++i) {
            AstNode elem = elems.get(i);
            this.decompile(elem);
            if (i >= size - 1) continue;
            this.decompiler.addToken(92);
        }
        this.decompiler.addToken(87);
    }

    void decompileObjectLiteral(ObjectLiteral node) {
        this.decompiler.addToken(88);
        List<ObjectProperty> props = node.getElements();
        int size = props.size();
        for (int i = 0; i < size; ++i) {
            ObjectProperty prop = props.get(i);
            boolean shorthandPropertyName = Boolean.TRUE.equals(prop.getProp(26));
            this.decompile(prop.getLeft());
            if (!shorthandPropertyName) {
                this.decompiler.addToken(107);
                this.decompile(prop.getRight());
            }
            if (i >= size - 1) continue;
            this.decompiler.addToken(92);
        }
        this.decompiler.addToken(89);
    }

    void decompilePropertyGet(PropertyGet node) {
        this.decompile(node.getTarget());
        this.decompiler.addToken(112);
        this.decompile(node.getProperty());
    }

    void decompileElementGet(ElementGet node) {
        this.decompile(node.getTarget());
        this.decompiler.addToken(86);
        this.decompile(node.getElement());
        this.decompiler.addToken(87);
    }
}


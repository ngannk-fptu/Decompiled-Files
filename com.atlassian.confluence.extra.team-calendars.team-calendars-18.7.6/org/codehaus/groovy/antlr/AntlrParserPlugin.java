/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.TokenStreamRecognitionException;
import groovyjarjarantlr.collections.AST;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.antlr.ASTParserException;
import org.codehaus.groovy.antlr.ASTRuntimeException;
import org.codehaus.groovy.antlr.AntlrASTProcessSnippets;
import org.codehaus.groovy.antlr.EnumHelper;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.UnicodeLexerSharedInputState;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.antlr.treewalker.CompositeVisitor;
import org.codehaus.groovy.antlr.treewalker.MindMapPrinter;
import org.codehaus.groovy.antlr.treewalker.NodeAsHTMLPrinter;
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.codehaus.groovy.antlr.treewalker.SourceCodeTraversal;
import org.codehaus.groovy.antlr.treewalker.SourcePrinter;
import org.codehaus.groovy.antlr.treewalker.TraversalHelper;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.EnumConstantClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.MixinNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.AnnotationConstantExpression;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.ParserPlugin;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.XStreamUtils;
import org.codehaus.groovy.syntax.ASTHelper;
import org.codehaus.groovy.syntax.Numbers;
import org.codehaus.groovy.syntax.ParserException;
import org.codehaus.groovy.syntax.Reduction;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

public class AntlrParserPlugin
extends ASTHelper
implements ParserPlugin,
GroovyTokenTypes {
    protected AST ast;
    private ClassNode classNode;
    private MethodNode methodNode;
    private String[] tokenNames;
    private int innerClassCounter = 1;
    private boolean enumConstantBeingDef = false;
    private boolean forStatementBeingDef = false;
    private boolean firstParamIsVarArg = false;
    private boolean firstParam = false;

    @Override
    public Reduction parseCST(SourceUnit sourceUnit, Reader reader) throws CompilationFailedException {
        SourceBuffer sourceBuffer = new SourceBuffer();
        this.transformCSTIntoAST(sourceUnit, reader, sourceBuffer);
        this.processAST();
        return this.outputAST(sourceUnit, sourceBuffer);
    }

    protected void transformCSTIntoAST(SourceUnit sourceUnit, Reader reader, SourceBuffer sourceBuffer) throws CompilationFailedException {
        this.ast = null;
        this.setController(sourceUnit);
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(reader, sourceBuffer);
        UnicodeLexerSharedInputState inputState = new UnicodeLexerSharedInputState(unicodeReader);
        GroovyLexer lexer = new GroovyLexer(inputState);
        unicodeReader.setLexer(lexer);
        GroovyRecognizer parser = GroovyRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);
        this.tokenNames = parser.getTokenNames();
        parser.setFilename(sourceUnit.getName());
        try {
            parser.compilationUnit();
        }
        catch (TokenStreamRecognitionException tsre) {
            RecognitionException e = tsre.recog;
            SyntaxException se = new SyntaxException(e.getMessage(), (Throwable)e, e.getLine(), e.getColumn());
            se.setFatal(true);
            sourceUnit.addError(se);
        }
        catch (RecognitionException e) {
            SyntaxException se = new SyntaxException(e.getMessage(), (Throwable)e, e.getLine(), e.getColumn());
            se.setFatal(true);
            sourceUnit.addError(se);
        }
        catch (TokenStreamException e) {
            sourceUnit.addException(e);
        }
        this.ast = parser.getAST();
    }

    protected void processAST() {
        AntlrASTProcessSnippets snippets = new AntlrASTProcessSnippets();
        this.ast = snippets.process(this.ast);
    }

    public Reduction outputAST(final SourceUnit sourceUnit, final SourceBuffer sourceBuffer) {
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                AntlrParserPlugin.this.outputASTInVariousFormsIfNeeded(sourceUnit, sourceBuffer);
                return null;
            }
        });
        return null;
    }

    private void outputASTInVariousFormsIfNeeded(SourceUnit sourceUnit, SourceBuffer sourceBuffer) {
        TraversalHelper treewalker;
        VisitorAdapter visitor;
        PrintStream out;
        String formatProp = System.getProperty("ANTLR.AST".toLowerCase());
        if ("xml".equals(formatProp)) {
            AntlrParserPlugin.saveAsXML(sourceUnit.getName(), this.ast);
        }
        if ("groovy".equals(formatProp)) {
            try {
                out = new PrintStream(new FileOutputStream(sourceUnit.getName() + ".pretty.groovy"));
                visitor = new SourcePrinter(out, this.tokenNames);
                treewalker = new SourceCodeTraversal(visitor);
                treewalker.process(this.ast);
            }
            catch (FileNotFoundException e) {
                System.out.println("Cannot create " + sourceUnit.getName() + ".pretty.groovy");
            }
        }
        if ("mindmap".equals(formatProp)) {
            try {
                out = new PrintStream(new FileOutputStream(sourceUnit.getName() + ".mm"));
                visitor = new MindMapPrinter(out, this.tokenNames);
                treewalker = new PreOrderTraversal(visitor);
                treewalker.process(this.ast);
            }
            catch (FileNotFoundException e) {
                System.out.println("Cannot create " + sourceUnit.getName() + ".mm");
            }
        }
        if ("extendedMindmap".equals(formatProp)) {
            try {
                out = new PrintStream(new FileOutputStream(sourceUnit.getName() + ".mm"));
                visitor = new MindMapPrinter(out, this.tokenNames, sourceBuffer);
                treewalker = new PreOrderTraversal(visitor);
                treewalker.process(this.ast);
            }
            catch (FileNotFoundException e) {
                System.out.println("Cannot create " + sourceUnit.getName() + ".mm");
            }
        }
        if ("html".equals(formatProp)) {
            try {
                out = new PrintStream(new FileOutputStream(sourceUnit.getName() + ".html"));
                ArrayList<VisitorAdapter> v = new ArrayList<VisitorAdapter>();
                v.add(new NodeAsHTMLPrinter(out, this.tokenNames));
                v.add(new SourcePrinter(out, this.tokenNames));
                CompositeVisitor visitors = new CompositeVisitor(v);
                SourceCodeTraversal treewalker2 = new SourceCodeTraversal(visitors);
                treewalker2.process(this.ast);
            }
            catch (FileNotFoundException e) {
                System.out.println("Cannot create " + sourceUnit.getName() + ".html");
            }
        }
    }

    private static void saveAsXML(String name, AST ast) {
        XStreamUtils.serialize(name + ".antlr", ast);
    }

    @Override
    public ModuleNode buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction cst) throws ParserException {
        this.setClassLoader(classLoader);
        this.makeModule();
        try {
            List<Statement> statements;
            ClassNode scriptClassNode;
            this.convertGroovy(this.ast);
            if (this.output.getStatementBlock().isEmpty() && this.output.getMethods().isEmpty() && this.output.getClasses().isEmpty()) {
                this.output.addStatement(ReturnStatement.RETURN_NULL_OR_VOID);
            }
            if ((scriptClassNode = this.output.getScriptClassDummy()) != null && !(statements = this.output.getStatementBlock().getStatements()).isEmpty()) {
                Statement firstStatement = statements.get(0);
                Statement lastStatement = statements.get(statements.size() - 1);
                scriptClassNode.setSourcePosition(firstStatement);
                scriptClassNode.setLastColumnNumber(lastStatement.getLastColumnNumber());
                scriptClassNode.setLastLineNumber(lastStatement.getLastLineNumber());
            }
        }
        catch (ASTRuntimeException e) {
            throw new ASTParserException(e.getMessage() + ". File: " + sourceUnit.getName(), e);
        }
        return this.output;
    }

    protected void convertGroovy(AST node) {
        while (node != null) {
            int type = node.getType();
            switch (type) {
                case 16: {
                    this.packageDef(node);
                    break;
                }
                case 29: 
                case 60: {
                    this.importDef(node);
                    break;
                }
                case 13: 
                case 15: {
                    this.classDef(node);
                    break;
                }
                case 14: {
                    this.interfaceDef(node);
                    break;
                }
                case 8: {
                    this.methodDef(node);
                    break;
                }
                case 61: {
                    this.enumDef(node);
                    break;
                }
                case 64: {
                    this.annotationDef(node);
                    break;
                }
                default: {
                    Statement statement = this.statement(node);
                    this.output.addStatement(statement);
                }
            }
            node = node.getNextSibling();
        }
    }

    protected void packageDef(AST packageDef) {
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST node = packageDef.getFirstChild();
        if (AntlrParserPlugin.isType(65, node)) {
            this.processAnnotations(annotations, node);
            node = node.getNextSibling();
        }
        String name = AntlrParserPlugin.qualifiedName(node);
        PackageNode packageNode = this.setPackage(name, annotations);
        this.configureAST(packageNode, packageDef);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void importDef(AST importNode) {
        try {
            this.output.putNodeMetaData(ImportNode.class, ImportNode.class);
            boolean isStatic = importNode.getType() == 60;
            ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
            AST node = importNode.getFirstChild();
            if (AntlrParserPlugin.isType(65, node)) {
                this.processAnnotations(annotations, node);
                node = node.getNextSibling();
            }
            String alias = null;
            if (AntlrParserPlugin.isType(114, node)) {
                node = node.getFirstChild();
                AST aliasNode = node.getNextSibling();
                alias = this.identifier(aliasNode);
            }
            if (node.getNumberOfChildren() == 0) {
                String name = this.identifier(node);
                ClassNode type = ClassHelper.make(name);
                this.configureAST(type, importNode);
                this.addImport(type, name, alias, annotations);
                return;
            }
            AST packageNode = node.getFirstChild();
            String packageName = AntlrParserPlugin.qualifiedName(packageNode);
            AST nameNode = packageNode.getNextSibling();
            if (AntlrParserPlugin.isType(113, nameNode)) {
                if (isStatic) {
                    ClassNode type = ClassHelper.make(packageName);
                    this.configureAST(type, importNode);
                    this.addStaticStarImport(type, packageName, annotations);
                } else {
                    this.addStarImport(packageName, annotations);
                }
                if (alias != null) {
                    throw new GroovyBugError("imports like 'import foo.* as Bar' are not supported and should be caught by the grammar");
                }
            } else {
                String name = this.identifier(nameNode);
                if (isStatic) {
                    ClassNode type = ClassHelper.make(packageName);
                    this.configureAST(type, importNode);
                    this.addStaticImport(type, name, alias, annotations);
                } else {
                    ClassNode type = ClassHelper.make(packageName + "." + name);
                    this.configureAST(type, importNode);
                    this.addImport(type, name, alias, annotations);
                }
            }
        }
        finally {
            Object node = this.output.getNodeMetaData(ImportNode.class);
            if (node != null && node != ImportNode.class) {
                this.configureAST((ImportNode)node, importNode);
            }
            this.output.removeNodeMetaData(ImportNode.class);
        }
    }

    private void processAnnotations(List<AnnotationNode> annotations, AST node) {
        for (AST child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!AntlrParserPlugin.isType(66, child)) continue;
            annotations.add(this.annotation(child));
        }
    }

    protected void annotationDef(AST classDef) {
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST node = classDef.getFirstChild();
        int modifiers = 1;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            AntlrParserPlugin.checkNoInvalidModifier(classDef, "Annotation Definition", modifiers, 32, "synchronized");
            node = node.getNextSibling();
        }
        modifiers |= 0x2600;
        String name = this.identifier(node);
        node = node.getNextSibling();
        ClassNode superClass = ClassHelper.OBJECT_TYPE;
        GenericsType[] genericsType = null;
        if (AntlrParserPlugin.isType(72, node)) {
            genericsType = this.makeGenericsType(node);
            node = node.getNextSibling();
        }
        ClassNode[] interfaces = ClassNode.EMPTY_ARRAY;
        if (AntlrParserPlugin.isType(18, node)) {
            interfaces = this.interfaces(node);
            node = node.getNextSibling();
        }
        boolean syntheticPublic = (modifiers & 0x1000) != 0;
        this.classNode = new ClassNode(AntlrParserPlugin.dot(this.getPackageName(), name), modifiers &= 0xFFFFEFFF, superClass, interfaces, null);
        this.classNode.setSyntheticPublic(syntheticPublic);
        this.classNode.addAnnotations(annotations);
        this.classNode.setGenericsTypes(genericsType);
        this.classNode.addInterface(ClassHelper.Annotation_TYPE);
        this.configureAST(this.classNode, classDef);
        this.assertNodeType(6, node);
        this.objectBlock(node);
        this.output.addClass(this.classNode);
        this.classNode = null;
    }

    protected void interfaceDef(AST classDef) {
        int oldInnerClassCounter = this.innerClassCounter;
        this.innerInterfaceDef(classDef);
        this.classNode = null;
        this.innerClassCounter = oldInnerClassCounter;
    }

    protected void innerInterfaceDef(AST classDef) {
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST node = classDef.getFirstChild();
        int modifiers = 1;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            AntlrParserPlugin.checkNoInvalidModifier(classDef, "Interface", modifiers, 32, "synchronized");
            node = node.getNextSibling();
        }
        modifiers |= 0x600;
        String name = this.identifier(node);
        node = node.getNextSibling();
        ClassNode superClass = ClassHelper.OBJECT_TYPE;
        GenericsType[] genericsType = null;
        if (AntlrParserPlugin.isType(72, node)) {
            genericsType = this.makeGenericsType(node);
            node = node.getNextSibling();
        }
        ClassNode[] interfaces = ClassNode.EMPTY_ARRAY;
        if (AntlrParserPlugin.isType(18, node)) {
            interfaces = this.interfaces(node);
            node = node.getNextSibling();
        }
        ClassNode outerClass = this.classNode;
        boolean syntheticPublic = (modifiers & 0x1000) != 0;
        modifiers &= 0xFFFFEFFF;
        if (this.classNode != null) {
            name = this.classNode.getNameWithoutPackage() + "$" + name;
            String fullName = AntlrParserPlugin.dot(this.classNode.getPackageName(), name);
            this.classNode = new InnerClassNode(this.classNode, fullName, modifiers, superClass, interfaces, null);
        } else {
            this.classNode = new ClassNode(AntlrParserPlugin.dot(this.getPackageName(), name), modifiers, superClass, interfaces, null);
        }
        this.classNode.setSyntheticPublic(syntheticPublic);
        this.classNode.addAnnotations(annotations);
        this.classNode.setGenericsTypes(genericsType);
        this.configureAST(this.classNode, classDef);
        int oldClassCount = this.innerClassCounter;
        this.assertNodeType(6, node);
        this.objectBlock(node);
        this.output.addClass(this.classNode);
        this.classNode = outerClass;
        this.innerClassCounter = oldClassCount;
    }

    protected void classDef(AST classDef) {
        int oldInnerClassCounter = this.innerClassCounter;
        this.innerClassDef(classDef);
        this.classNode = null;
        this.innerClassCounter = oldInnerClassCounter;
    }

    private ClassNode getClassOrScript(ClassNode node) {
        if (node != null) {
            return node;
        }
        return this.output.getScriptClassDummy();
    }

    protected Expression anonymousInnerClassDef(AST node) {
        ClassNode oldNode = this.classNode;
        ClassNode outerClass = this.getClassOrScript(oldNode);
        String fullName = outerClass.getName() + '$' + this.innerClassCounter;
        ++this.innerClassCounter;
        this.classNode = this.enumConstantBeingDef ? new EnumConstantClassNode(outerClass, fullName, 1, ClassHelper.OBJECT_TYPE) : new InnerClassNode(outerClass, fullName, 1, ClassHelper.OBJECT_TYPE);
        ((InnerClassNode)this.classNode).setAnonymous(true);
        this.classNode.setEnclosingMethod(this.methodNode);
        this.assertNodeType(6, node);
        this.objectBlock(node);
        this.output.addClass(this.classNode);
        AnonymousInnerClassCarrier ret = new AnonymousInnerClassCarrier();
        ret.innerClass = this.classNode;
        this.classNode = oldNode;
        return ret;
    }

    protected void innerClassDef(AST classDef) {
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        if (AntlrParserPlugin.isType(15, classDef)) {
            annotations.add(new AnnotationNode(ClassHelper.make("groovy.transform.Trait")));
        }
        AST node = classDef.getFirstChild();
        int modifiers = 1;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            AntlrParserPlugin.checkNoInvalidModifier(classDef, "Class", modifiers, 32, "synchronized");
            node = node.getNextSibling();
        }
        String name = this.identifier(node);
        node = node.getNextSibling();
        GenericsType[] genericsType = null;
        if (AntlrParserPlugin.isType(72, node)) {
            genericsType = this.makeGenericsType(node);
            node = node.getNextSibling();
        }
        ClassNode superClass = null;
        if (AntlrParserPlugin.isType(18, node)) {
            superClass = this.makeTypeWithArguments(node);
            node = node.getNextSibling();
        }
        ClassNode[] interfaces = ClassNode.EMPTY_ARRAY;
        if (AntlrParserPlugin.isType(19, node)) {
            interfaces = this.interfaces(node);
            node = node.getNextSibling();
        }
        MixinNode[] mixins = new MixinNode[]{};
        ClassNode outerClass = this.classNode;
        boolean syntheticPublic = (modifiers & 0x1000) != 0;
        modifiers &= 0xFFFFEFFF;
        if (this.classNode != null) {
            name = this.classNode.getNameWithoutPackage() + "$" + name;
            String fullName = AntlrParserPlugin.dot(this.classNode.getPackageName(), name);
            if (this.classNode.isInterface()) {
                modifiers |= 8;
            }
            this.classNode = new InnerClassNode(this.classNode, fullName, modifiers, superClass, interfaces, mixins);
        } else {
            this.classNode = new ClassNode(AntlrParserPlugin.dot(this.getPackageName(), name), modifiers, superClass, interfaces, mixins);
        }
        this.classNode.addAnnotations(annotations);
        this.classNode.setGenericsTypes(genericsType);
        this.classNode.setSyntheticPublic(syntheticPublic);
        this.configureAST(this.classNode, classDef);
        this.output.addClass(this.classNode);
        int oldClassCount = this.innerClassCounter;
        this.assertNodeType(6, node);
        this.objectBlock(node);
        this.classNode = outerClass;
        this.innerClassCounter = oldClassCount;
    }

    protected void objectBlock(AST objectBlock) {
        block12: for (AST node = objectBlock.getFirstChild(); node != null; node = node.getNextSibling()) {
            int type = node.getType();
            switch (type) {
                case 6: {
                    this.objectBlock(node);
                    continue block12;
                }
                case 8: 
                case 68: {
                    this.methodDef(node);
                    continue block12;
                }
                case 46: {
                    this.constructorDef(node);
                    continue block12;
                }
                case 9: {
                    this.fieldDef(node);
                    continue block12;
                }
                case 11: {
                    this.staticInit(node);
                    continue block12;
                }
                case 10: {
                    this.objectInit(node);
                    continue block12;
                }
                case 61: {
                    this.enumDef(node);
                    continue block12;
                }
                case 62: {
                    this.enumConstantDef(node);
                    continue block12;
                }
                case 13: 
                case 15: {
                    this.innerClassDef(node);
                    continue block12;
                }
                case 14: {
                    this.innerInterfaceDef(node);
                    continue block12;
                }
                default: {
                    this.unknownAST(node);
                }
            }
        }
    }

    protected void enumDef(AST enumNode) {
        this.assertNodeType(61, enumNode);
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST node = enumNode.getFirstChild();
        int modifiers = 1;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            node = node.getNextSibling();
        }
        String name = this.identifier(node);
        node = node.getNextSibling();
        ClassNode[] interfaces = this.interfaces(node);
        node = node.getNextSibling();
        boolean syntheticPublic = (modifiers & 0x1000) != 0;
        String enumName = this.classNode != null ? name : AntlrParserPlugin.dot(this.getPackageName(), name);
        ClassNode enumClass = EnumHelper.makeEnumNode(enumName, modifiers &= 0xFFFFEFFF, interfaces, this.classNode);
        enumClass.setSyntheticPublic(syntheticPublic);
        ClassNode oldNode = this.classNode;
        enumClass.addAnnotations(annotations);
        this.classNode = enumClass;
        this.configureAST(this.classNode, enumNode);
        this.assertNodeType(6, node);
        this.objectBlock(node);
        this.classNode = oldNode;
        this.output.addClass(enumClass);
    }

    protected void enumConstantDef(AST node) {
        this.enumConstantBeingDef = true;
        this.assertNodeType(62, node);
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST element = node.getFirstChild();
        if (AntlrParserPlugin.isType(65, element)) {
            this.processAnnotations(annotations, element);
            element = element.getNextSibling();
        }
        String identifier = this.identifier(element);
        Expression init = null;
        if ((element = element.getNextSibling()) != null) {
            ClassNode innerClass;
            init = this.expression(element);
            if (element.getNextSibling() == null) {
                innerClass = AntlrParserPlugin.getAnonymousInnerClassNode(init);
                if (innerClass != null) {
                    init = null;
                }
            } else {
                element = element.getNextSibling();
                Expression next = this.expression(element);
                innerClass = AntlrParserPlugin.getAnonymousInnerClassNode(next);
            }
            if (innerClass != null) {
                innerClass.setSuperClass(this.classNode.getPlainNodeReference());
                innerClass.setModifiers(this.classNode.getModifiers() | 0x10);
                ClassExpression inner = new ClassExpression(innerClass);
                if (init == null) {
                    ListExpression le = new ListExpression();
                    le.addExpression(inner);
                    init = le;
                } else if (init instanceof ListExpression) {
                    ((ListExpression)init).addExpression(inner);
                } else {
                    ListExpression le = new ListExpression();
                    le.addExpression(init);
                    le.addExpression(inner);
                    init = le;
                }
                this.classNode.setModifiers(this.classNode.getModifiers() & 0xFFFFFFEF);
            } else if (AntlrParserPlugin.isType(33, element) && init instanceof ListExpression && !((ListExpression)init).isWrapped()) {
                ListExpression le = new ListExpression();
                le.addExpression(init);
                init = le;
            }
        }
        FieldNode enumField = EnumHelper.addEnumConstant(this.classNode, identifier, init);
        enumField.addAnnotations(annotations);
        this.configureAST(enumField, node);
        this.enumConstantBeingDef = false;
    }

    protected void throwsList(AST node, List<ClassNode> list) {
        String name = AntlrParserPlugin.isType(90, node) ? AntlrParserPlugin.qualifiedName(node) : this.identifier(node);
        ClassNode exception = ClassHelper.make(name);
        this.configureAST(exception, node);
        list.add(exception);
        AST next = node.getNextSibling();
        if (next != null) {
            this.throwsList(next, list);
        }
    }

    protected void methodDef(AST methodDef) {
        MethodNode oldNode = this.methodNode;
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST node = methodDef.getFirstChild();
        GenericsType[] generics = null;
        if (AntlrParserPlugin.isType(72, node)) {
            generics = this.makeGenericsType(node);
            node = node.getNextSibling();
        }
        int modifiers = 1;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            AntlrParserPlugin.checkNoInvalidModifier(methodDef, "Method", modifiers, 64, "volatile");
            node = node.getNextSibling();
        }
        if (this.isAnInterface()) {
            modifiers |= 0x400;
        }
        ClassNode returnType = null;
        if (AntlrParserPlugin.isType(12, node)) {
            returnType = this.makeTypeWithArguments(node);
            node = node.getNextSibling();
        }
        String name = this.identifier(node);
        if (this.classNode != null && !this.classNode.isAnnotationDefinition() && this.classNode.getNameWithoutPackage().equals(name)) {
            if (this.isAnInterface()) {
                throw new ASTRuntimeException(methodDef, "Constructor not permitted within an interface.");
            }
            throw new ASTRuntimeException(methodDef, "Invalid constructor format. Remove '" + returnType.getName() + "' as the return type if you want a constructor, or use a different name if you want a method.");
        }
        node = node.getNextSibling();
        Parameter[] parameters = Parameter.EMPTY_ARRAY;
        ClassNode[] exceptions = ClassNode.EMPTY_ARRAY;
        if (this.classNode == null || !this.classNode.isAnnotationDefinition()) {
            this.assertNodeType(20, node);
            parameters = this.parameters(node);
            if (parameters == null) {
                parameters = Parameter.EMPTY_ARRAY;
            }
            if (AntlrParserPlugin.isType(130, node = node.getNextSibling())) {
                AST throwsNode = node.getFirstChild();
                ArrayList<ClassNode> exceptionList = new ArrayList<ClassNode>();
                this.throwsList(throwsNode, exceptionList);
                exceptions = exceptionList.toArray(exceptions);
                node = node.getNextSibling();
            }
        }
        boolean hasAnnotationDefault = false;
        Statement code = null;
        boolean syntheticPublic = (modifiers & 0x1000) != 0;
        this.methodNode = new MethodNode(name, modifiers &= 0xFFFFEFFF, returnType, parameters, exceptions, code);
        if ((modifiers & 0x400) == 0) {
            if (node == null) {
                throw new ASTRuntimeException(methodDef, "You defined a method without body. Try adding a body, or declare it abstract.");
            }
            this.assertNodeType(7, node);
            code = this.statementList(node);
        } else if (node != null && this.classNode.isAnnotationDefinition()) {
            code = this.statement(node);
            hasAnnotationDefault = true;
        } else if ((modifiers & 0x400) > 0 && node != null) {
            throw new ASTRuntimeException(methodDef, "Abstract methods do not define a body.");
        }
        this.methodNode.setCode(code);
        this.methodNode.addAnnotations(annotations);
        this.methodNode.setGenericsTypes(generics);
        this.methodNode.setAnnotationDefault(hasAnnotationDefault);
        this.methodNode.setSyntheticPublic(syntheticPublic);
        this.configureAST(this.methodNode, methodDef);
        if (this.classNode != null) {
            this.classNode.addMethod(this.methodNode);
        } else {
            this.output.addMethod(this.methodNode);
        }
        this.methodNode = oldNode;
    }

    private static void checkNoInvalidModifier(AST node, String nodeType, int modifiers, int modifier, String modifierText) {
        if ((modifiers & modifier) != 0) {
            throw new ASTRuntimeException(node, nodeType + " has an incorrect modifier '" + modifierText + "'.");
        }
    }

    private boolean isAnInterface() {
        return this.classNode != null && (this.classNode.getModifiers() & 0x200) > 0;
    }

    protected void staticInit(AST staticInit) {
        BlockStatement code = (BlockStatement)this.statementList(staticInit);
        this.classNode.addStaticInitializerStatements(code.getStatements(), false);
    }

    protected void objectInit(AST init) {
        BlockStatement code = (BlockStatement)this.statementList(init);
        this.classNode.addObjectInitializerStatements(code);
    }

    protected void constructorDef(AST constructorDef) {
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST node = constructorDef.getFirstChild();
        int modifiers = 1;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            AntlrParserPlugin.checkNoInvalidModifier(constructorDef, "Constructor", modifiers, 8, "static");
            AntlrParserPlugin.checkNoInvalidModifier(constructorDef, "Constructor", modifiers, 16, "final");
            AntlrParserPlugin.checkNoInvalidModifier(constructorDef, "Constructor", modifiers, 1024, "abstract");
            AntlrParserPlugin.checkNoInvalidModifier(constructorDef, "Constructor", modifiers, 256, "native");
            node = node.getNextSibling();
        }
        this.assertNodeType(20, node);
        Parameter[] parameters = this.parameters(node);
        if (parameters == null) {
            parameters = Parameter.EMPTY_ARRAY;
        }
        node = node.getNextSibling();
        ClassNode[] exceptions = ClassNode.EMPTY_ARRAY;
        if (AntlrParserPlugin.isType(130, node)) {
            AST throwsNode = node.getFirstChild();
            ArrayList<ClassNode> exceptionList = new ArrayList<ClassNode>();
            this.throwsList(throwsNode, exceptionList);
            exceptions = exceptionList.toArray(exceptions);
            node = node.getNextSibling();
        }
        this.assertNodeType(7, node);
        boolean syntheticPublic = (modifiers & 0x1000) != 0;
        ConstructorNode constructorNode = this.classNode.addConstructor(modifiers &= 0xFFFFEFFF, parameters, exceptions, null);
        MethodNode oldMethod = this.methodNode;
        this.methodNode = constructorNode;
        Statement code = this.statementList(node);
        this.methodNode = oldMethod;
        constructorNode.setCode(code);
        constructorNode.setSyntheticPublic(syntheticPublic);
        constructorNode.addAnnotations(annotations);
        this.configureAST(constructorNode, constructorDef);
    }

    protected void fieldDef(AST fieldDef) {
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        AST node = fieldDef.getFirstChild();
        int modifiers = 0;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            node = node.getNextSibling();
        }
        if (this.classNode.isInterface() && ((modifiers |= 0x18) & 6) == 0) {
            modifiers |= 1;
        }
        ClassNode type = null;
        if (AntlrParserPlugin.isType(12, node)) {
            type = this.makeTypeWithArguments(node);
            node = node.getNextSibling();
        }
        String name = this.identifier(node);
        node = node.getNextSibling();
        Expression initialValue = null;
        if (node != null) {
            this.assertNodeType(124, node);
            initialValue = this.expression(node.getFirstChild());
        }
        if (this.classNode.isInterface() && initialValue == null && type != null) {
            if (type == ClassHelper.int_TYPE) {
                initialValue = new ConstantExpression(0);
            } else if (type == ClassHelper.long_TYPE) {
                initialValue = new ConstantExpression(0L);
            } else if (type == ClassHelper.double_TYPE) {
                initialValue = new ConstantExpression(0.0);
            } else if (type == ClassHelper.float_TYPE) {
                initialValue = new ConstantExpression(Float.valueOf(0.0f));
            } else if (type == ClassHelper.boolean_TYPE) {
                initialValue = ConstantExpression.FALSE;
            } else if (type == ClassHelper.short_TYPE) {
                initialValue = new ConstantExpression((short)0);
            } else if (type == ClassHelper.byte_TYPE) {
                initialValue = new ConstantExpression((byte)0);
            } else if (type == ClassHelper.char_TYPE) {
                initialValue = new ConstantExpression(Character.valueOf('\u0000'));
            }
        }
        FieldNode fieldNode = new FieldNode(name, modifiers, type, this.classNode, initialValue);
        fieldNode.addAnnotations(annotations);
        this.configureAST(fieldNode, fieldDef);
        if (!this.hasVisibility(modifiers)) {
            int fieldModifiers = 0;
            int flags = 216;
            if (!this.hasVisibility(modifiers)) {
                modifiers |= 1;
                fieldModifiers |= 2;
            }
            fieldNode.setModifiers(fieldModifiers |= modifiers & flags);
            fieldNode.setSynthetic(true);
            FieldNode storedNode = this.classNode.getDeclaredField(fieldNode.getName());
            if (storedNode != null && !this.classNode.hasProperty(name)) {
                fieldNode = storedNode;
                this.classNode.getFields().remove(storedNode);
            }
            PropertyNode propertyNode = new PropertyNode(fieldNode, modifiers, null, null);
            this.configureAST(propertyNode, fieldDef);
            this.classNode.addProperty(propertyNode);
        } else {
            fieldNode.setModifiers(modifiers);
            PropertyNode pn = this.classNode.getProperty(name);
            if (pn != null && pn.getField().isSynthetic()) {
                this.classNode.getFields().remove(pn.getField());
                pn.setField(fieldNode);
            }
            this.classNode.addField(fieldNode);
        }
    }

    protected ClassNode[] interfaces(AST node) {
        ArrayList<ClassNode> interfaceList = new ArrayList<ClassNode>();
        for (AST implementNode = node.getFirstChild(); implementNode != null; implementNode = implementNode.getNextSibling()) {
            interfaceList.add(this.makeTypeWithArguments(implementNode));
        }
        ClassNode[] interfaces = ClassNode.EMPTY_ARRAY;
        if (!interfaceList.isEmpty()) {
            interfaces = new ClassNode[interfaceList.size()];
            interfaceList.toArray(interfaces);
        }
        return interfaces;
    }

    protected Parameter[] parameters(AST parametersNode) {
        AST node = parametersNode.getFirstChild();
        this.firstParam = false;
        this.firstParamIsVarArg = false;
        if (node == null) {
            if (AntlrParserPlugin.isType(51, parametersNode)) {
                return Parameter.EMPTY_ARRAY;
            }
            return null;
        }
        ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        AST firstParameterNode = null;
        do {
            boolean bl = this.firstParam = firstParameterNode == null;
            if (firstParameterNode == null) {
                firstParameterNode = node;
            }
            parameters.add(this.parameter(node));
        } while ((node = node.getNextSibling()) != null);
        this.verifyParameters(parameters, firstParameterNode);
        Parameter[] answer = new Parameter[parameters.size()];
        parameters.toArray(answer);
        return answer;
    }

    private void verifyParameters(List<Parameter> parameters, AST firstParameterNode) {
        if (parameters.size() <= 1) {
            return;
        }
        Parameter first = parameters.get(0);
        if (this.firstParamIsVarArg) {
            throw new ASTRuntimeException(firstParameterNode, "The var-arg parameter " + first.getName() + " must be the last parameter.");
        }
    }

    protected Parameter parameter(AST paramNode) {
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        boolean variableParameterDef = AntlrParserPlugin.isType(47, paramNode);
        AST node = paramNode.getFirstChild();
        int modifiers = 0;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, modifiers);
            node = node.getNextSibling();
        }
        ClassNode type = ClassHelper.DYNAMIC_TYPE;
        if (AntlrParserPlugin.isType(12, node)) {
            type = this.makeTypeWithArguments(node);
            if (variableParameterDef) {
                type = type.makeArray();
            }
            node = node.getNextSibling();
        }
        String name = this.identifier(node);
        node = node.getNextSibling();
        VariableExpression leftExpression = new VariableExpression(name, type);
        leftExpression.setModifiers(modifiers);
        this.configureAST(leftExpression, paramNode);
        Parameter parameter = null;
        if (node != null) {
            this.assertNodeType(124, node);
            Expression rightExpression = this.expression(node.getFirstChild());
            if (this.isAnInterface()) {
                throw new ASTRuntimeException(node, "Cannot specify default value for method parameter '" + name + " = " + rightExpression.getText() + "' inside an interface");
            }
            parameter = new Parameter(type, name, rightExpression);
        } else {
            parameter = new Parameter(type, name);
        }
        if (this.firstParam) {
            this.firstParamIsVarArg = variableParameterDef;
        }
        this.configureAST(parameter, paramNode);
        parameter.addAnnotations(annotations);
        parameter.setModifiers(modifiers);
        return parameter;
    }

    protected int modifiers(AST modifierNode, List<AnnotationNode> annotations, int defaultModifiers) {
        this.assertNodeType(5, modifierNode);
        boolean access = false;
        int answer = 0;
        block15: for (AST node = modifierNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            int type = node.getType();
            switch (type) {
                case 60: {
                    continue block15;
                }
                case 66: {
                    annotations.add(this.annotation(node));
                    continue block15;
                }
                case 115: {
                    answer = this.setModifierBit(node, answer, 2);
                    access = this.setAccessTrue(node, access);
                    continue block15;
                }
                case 117: {
                    answer = this.setModifierBit(node, answer, 4);
                    access = this.setAccessTrue(node, access);
                    continue block15;
                }
                case 116: {
                    answer = this.setModifierBit(node, answer, 1);
                    access = this.setAccessTrue(node, access);
                    continue block15;
                }
                case 39: {
                    answer = this.setModifierBit(node, answer, 1024);
                    continue block15;
                }
                case 38: {
                    answer = this.setModifierBit(node, answer, 16);
                    continue block15;
                }
                case 119: {
                    answer = this.setModifierBit(node, answer, 256);
                    continue block15;
                }
                case 83: {
                    answer = this.setModifierBit(node, answer, 8);
                    continue block15;
                }
                case 43: {
                    answer = this.setModifierBit(node, answer, 2048);
                    continue block15;
                }
                case 121: {
                    answer = this.setModifierBit(node, answer, 32);
                    continue block15;
                }
                case 118: {
                    answer = this.setModifierBit(node, answer, 128);
                    continue block15;
                }
                case 122: {
                    answer = this.setModifierBit(node, answer, 64);
                    continue block15;
                }
                default: {
                    this.unknownAST(node);
                }
            }
        }
        if (!access) {
            answer |= defaultModifiers;
            if (defaultModifiers == 1) {
                answer |= 0x1000;
            }
        }
        return answer;
    }

    protected boolean setAccessTrue(AST node, boolean access) {
        if (!access) {
            return true;
        }
        throw new ASTRuntimeException(node, "Cannot specify modifier: " + node.getText() + " when access scope has already been defined");
    }

    protected int setModifierBit(AST node, int answer, int bit) {
        if ((answer & bit) != 0) {
            throw new ASTRuntimeException(node, "Cannot repeat modifier: " + node.getText());
        }
        return answer | bit;
    }

    protected AnnotationNode annotation(AST annotationNode) {
        AST node = annotationNode.getFirstChild();
        String name = AntlrParserPlugin.qualifiedName(node);
        AnnotationNode annotatedNode = new AnnotationNode(ClassHelper.make(name));
        this.configureAST(annotatedNode, annotationNode);
        while (AntlrParserPlugin.isType(67, node = node.getNextSibling())) {
            AST memberNode = node.getFirstChild();
            String param = this.identifier(memberNode);
            Expression expression = this.expression(memberNode.getNextSibling());
            if (annotatedNode.getMember(param) != null) {
                throw new ASTRuntimeException(memberNode, "Annotation member '" + param + "' has already been associated with a value");
            }
            annotatedNode.setMember(param, expression);
        }
        return annotatedNode;
    }

    protected Statement statement(AST node) {
        Statement statement = null;
        int type = node.getType();
        switch (type) {
            case 7: 
            case 152: {
                statement = this.statementList(node);
                break;
            }
            case 27: {
                statement = this.methodCall(node);
                break;
            }
            case 9: {
                statement = this.variableDef(node);
                break;
            }
            case 22: {
                return this.labelledStatement(node);
            }
            case 147: {
                statement = this.assertStatement(node);
                break;
            }
            case 144: {
                statement = this.breakStatement(node);
                break;
            }
            case 145: {
                statement = this.continueStatement(node);
                break;
            }
            case 137: {
                statement = this.ifStatement(node);
                break;
            }
            case 141: {
                statement = this.forStatement(node);
                break;
            }
            case 143: {
                statement = this.returnStatement(node);
                break;
            }
            case 121: {
                statement = this.synchronizedStatement(node);
                break;
            }
            case 140: {
                statement = this.switchStatement(node);
                break;
            }
            case 151: {
                statement = this.tryStatement(node);
                break;
            }
            case 146: {
                statement = this.throwStatement(node);
                break;
            }
            case 139: {
                statement = this.whileStatement(node);
                break;
            }
            default: {
                statement = new ExpressionStatement(this.expression(node));
            }
        }
        if (statement != null) {
            this.configureAST(statement, node);
        }
        return statement;
    }

    protected Statement statementList(AST code) {
        BlockStatement block = this.siblingsToBlockStatement(code.getFirstChild());
        this.configureAST(block, code);
        return block;
    }

    protected Statement statementListNoChild(AST node, AST alternativeConfigureNode) {
        BlockStatement block = this.siblingsToBlockStatement(node);
        if (node != null) {
            this.configureAST(block, node);
        } else {
            this.configureAST(block, alternativeConfigureNode);
        }
        return block;
    }

    private BlockStatement siblingsToBlockStatement(AST firstSiblingNode) {
        BlockStatement block = new BlockStatement();
        for (AST node = firstSiblingNode; node != null; node = node.getNextSibling()) {
            block.addStatement(this.statement(node));
        }
        return block;
    }

    protected Statement assertStatement(AST assertNode) {
        AST node = assertNode.getFirstChild();
        BooleanExpression booleanExpression = this.booleanExpression(node);
        Expression messageExpression = null;
        messageExpression = (node = node.getNextSibling()) != null ? this.expression(node) : ConstantExpression.NULL;
        AssertStatement assertStatement = new AssertStatement(booleanExpression, messageExpression);
        this.configureAST(assertStatement, assertNode);
        return assertStatement;
    }

    protected Statement breakStatement(AST node) {
        BreakStatement breakStatement = new BreakStatement(this.label(node));
        this.configureAST(breakStatement, node);
        return breakStatement;
    }

    protected Statement continueStatement(AST node) {
        ContinueStatement continueStatement = new ContinueStatement(this.label(node));
        this.configureAST(continueStatement, node);
        return continueStatement;
    }

    protected Statement forStatement(AST forNode) {
        Parameter forParameter;
        Expression collectionExpression;
        AST inNode = forNode.getFirstChild();
        if (AntlrParserPlugin.isType(77, inNode)) {
            this.forStatementBeingDef = true;
            ClosureListExpression clist = this.closureListExpression(inNode);
            this.forStatementBeingDef = false;
            int size = clist.getExpressions().size();
            if (size != 3) {
                throw new ASTRuntimeException(inNode, "3 expressions are required for the classic for loop, you gave " + size);
            }
            collectionExpression = clist;
            forParameter = ForStatement.FOR_LOOP_DUMMY;
        } else {
            AST variableNode = inNode.getFirstChild();
            AST collectionNode = variableNode.getNextSibling();
            ClassNode type = ClassHelper.OBJECT_TYPE;
            if (AntlrParserPlugin.isType(9, variableNode)) {
                AST node = variableNode.getFirstChild();
                if (AntlrParserPlugin.isType(5, node)) {
                    int modifiersMask = this.modifiers(node, new ArrayList<AnnotationNode>(), 0);
                    if ((modifiersMask & 0xFFFFFFEF) != 0) {
                        throw new ASTRuntimeException(node, "Only the 'final' modifier is allowed in front of the for loop variable.");
                    }
                    node = node.getNextSibling();
                }
                type = this.makeTypeWithArguments(node);
                variableNode = node.getNextSibling();
            }
            String variable = this.identifier(variableNode);
            collectionExpression = this.expression(collectionNode);
            forParameter = new Parameter(type, variable);
            this.configureAST(forParameter, variableNode);
        }
        AST node = inNode.getNextSibling();
        Statement block = AntlrParserPlugin.isType(128, node) ? EmptyStatement.INSTANCE : this.statement(node);
        ForStatement forStatement = new ForStatement(forParameter, collectionExpression, block);
        this.configureAST(forStatement, forNode);
        return forStatement;
    }

    protected Statement ifStatement(AST ifNode) {
        AST node = ifNode.getFirstChild();
        this.assertNodeType(28, node);
        BooleanExpression booleanExpression = this.booleanExpression(node);
        node = node.getNextSibling();
        Statement ifBlock = this.statement(node);
        Statement elseBlock = EmptyStatement.INSTANCE;
        node = node.getNextSibling();
        if (node != null) {
            elseBlock = this.statement(node);
        }
        IfStatement ifStatement = new IfStatement(booleanExpression, ifBlock, elseBlock);
        this.configureAST(ifStatement, ifNode);
        return ifStatement;
    }

    protected Statement labelledStatement(AST labelNode) {
        AST node = labelNode.getFirstChild();
        String label = this.identifier(node);
        Statement statement = this.statement(node.getNextSibling());
        statement.addStatementLabel(label);
        return statement;
    }

    protected Statement methodCall(AST code) {
        Expression expression = this.methodCallExpression(code);
        ExpressionStatement expressionStatement = new ExpressionStatement(expression);
        this.configureAST(expressionStatement, code);
        return expressionStatement;
    }

    protected Expression declarationExpression(AST variableDef) {
        Expression leftExpression;
        AST node = variableDef.getFirstChild();
        ClassNode type = null;
        ArrayList<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        int modifiers = 0;
        if (AntlrParserPlugin.isType(5, node)) {
            modifiers = this.modifiers(node, annotations, 0);
            node = node.getNextSibling();
        }
        if (AntlrParserPlugin.isType(12, node)) {
            type = this.makeTypeWithArguments(node);
            node = node.getNextSibling();
        }
        Expression rightExpression = EmptyExpression.INSTANCE;
        if (AntlrParserPlugin.isType(124, node)) {
            node = node.getFirstChild();
            AST left = node.getFirstChild();
            ArgumentListExpression alist = new ArgumentListExpression();
            for (AST varDef = left; varDef != null; varDef = varDef.getNextSibling()) {
                this.assertNodeType(9, varDef);
                DeclarationExpression de = (DeclarationExpression)this.declarationExpression(varDef);
                alist.addExpression(de.getVariableExpression());
            }
            leftExpression = alist;
            AST right = node.getNextSibling();
            if (right != null) {
                rightExpression = this.expression(right);
            }
        } else {
            String name = this.identifier(node);
            VariableExpression ve = new VariableExpression(name, type);
            ve.setModifiers(modifiers);
            leftExpression = ve;
            AST right = node.getNextSibling();
            if (right != null) {
                this.assertNodeType(124, right);
                rightExpression = this.expression(right.getFirstChild());
            }
        }
        this.configureAST(leftExpression, node);
        Token token = AntlrParserPlugin.makeToken(100, variableDef);
        DeclarationExpression expression = new DeclarationExpression(leftExpression, token, rightExpression);
        expression.addAnnotations(annotations);
        this.configureAST(expression, variableDef);
        ExpressionStatement expressionStatement = new ExpressionStatement(expression);
        this.configureAST(expressionStatement, variableDef);
        return expression;
    }

    protected Statement variableDef(AST variableDef) {
        ExpressionStatement expressionStatement = new ExpressionStatement(this.declarationExpression(variableDef));
        this.configureAST(expressionStatement, variableDef);
        return expressionStatement;
    }

    protected Statement returnStatement(AST node) {
        AST exprNode = node.getFirstChild();
        ConstantExpression expression = exprNode == null ? ConstantExpression.NULL : this.expression(exprNode);
        ReturnStatement returnStatement = new ReturnStatement(expression);
        this.configureAST(returnStatement, node);
        return returnStatement;
    }

    protected Statement switchStatement(AST switchNode) {
        AST node = switchNode.getFirstChild();
        Expression expression = this.expression(node);
        Statement defaultStatement = EmptyStatement.INSTANCE;
        ArrayList<CaseStatement> list = new ArrayList<CaseStatement>();
        node = node.getNextSibling();
        while (AntlrParserPlugin.isType(32, node)) {
            Statement tmpDefaultStatement;
            AST child = node.getFirstChild();
            if (AntlrParserPlugin.isType(150, child)) {
                LinkedList cases = new LinkedList();
                tmpDefaultStatement = this.caseStatements(child, cases);
                list.addAll(cases);
            } else {
                tmpDefaultStatement = this.statement(child.getNextSibling());
            }
            if (tmpDefaultStatement != EmptyStatement.INSTANCE) {
                if (defaultStatement == EmptyStatement.INSTANCE) {
                    defaultStatement = tmpDefaultStatement;
                } else {
                    throw new ASTRuntimeException(switchNode, "The default case is already defined.");
                }
            }
            node = node.getNextSibling();
        }
        if (node != null) {
            this.unknownAST(node);
        }
        SwitchStatement switchStatement = new SwitchStatement(expression, list, defaultStatement);
        this.configureAST(switchStatement, switchNode);
        return switchStatement;
    }

    protected Statement caseStatements(AST node, List cases) {
        LinkedList<Expression> expressions = new LinkedList<Expression>();
        Statement statement = EmptyStatement.INSTANCE;
        Statement defaultStatement = EmptyStatement.INSTANCE;
        AST nextSibling = node;
        do {
            Expression expression = this.expression(nextSibling.getFirstChild());
            expressions.add(expression);
        } while (AntlrParserPlugin.isType(150, nextSibling = nextSibling.getNextSibling()));
        if (nextSibling != null) {
            if (AntlrParserPlugin.isType(129, nextSibling)) {
                defaultStatement = this.statement(nextSibling.getNextSibling());
                statement = EmptyStatement.INSTANCE;
            } else {
                statement = this.statement(nextSibling);
            }
        }
        Iterator iterator = expressions.iterator();
        while (iterator.hasNext()) {
            Expression expr = (Expression)iterator.next();
            CaseStatement stmt = iterator.hasNext() ? new CaseStatement(expr, EmptyStatement.INSTANCE) : new CaseStatement(expr, statement);
            this.configureAST(stmt, node);
            cases.add(stmt);
        }
        return defaultStatement;
    }

    protected Statement synchronizedStatement(AST syncNode) {
        AST node = syncNode.getFirstChild();
        Expression expression = this.expression(node);
        Statement code = this.statement(node.getNextSibling());
        SynchronizedStatement synchronizedStatement = new SynchronizedStatement(expression, code);
        this.configureAST(synchronizedStatement, syncNode);
        return synchronizedStatement;
    }

    protected Statement throwStatement(AST node) {
        AST expressionNode = node.getFirstChild();
        if (expressionNode == null) {
            expressionNode = node.getNextSibling();
        }
        if (expressionNode == null) {
            throw new ASTRuntimeException(node, "No expression available");
        }
        ThrowStatement throwStatement = new ThrowStatement(this.expression(expressionNode));
        this.configureAST(throwStatement, node);
        return throwStatement;
    }

    protected Statement tryStatement(AST tryStatementNode) {
        AST node;
        AST tryNode = tryStatementNode.getFirstChild();
        Statement tryStatement = this.statement(tryNode);
        Statement finallyStatement = EmptyStatement.INSTANCE;
        ArrayList<CatchStatement> catches = new ArrayList<CatchStatement>();
        for (node = tryNode.getNextSibling(); node != null && AntlrParserPlugin.isType(153, node); node = node.getNextSibling()) {
            List<CatchStatement> catchStatements = this.catchStatement(node);
            catches.addAll(catchStatements);
        }
        if (AntlrParserPlugin.isType(152, node)) {
            finallyStatement = this.statement(node);
            node = node.getNextSibling();
        }
        if (finallyStatement instanceof EmptyStatement && catches.isEmpty()) {
            throw new ASTRuntimeException(tryStatementNode, "A try statement must have at least one catch or finally block.");
        }
        TryCatchStatement tryCatchStatement = new TryCatchStatement(tryStatement, finallyStatement);
        this.configureAST(tryCatchStatement, tryStatementNode);
        for (CatchStatement statement : catches) {
            tryCatchStatement.addCatch(statement);
        }
        return tryCatchStatement;
    }

    protected List<CatchStatement> catchStatement(AST catchNode) {
        AST node = catchNode.getFirstChild();
        LinkedList<CatchStatement> catches = new LinkedList<CatchStatement>();
        Statement code = this.statement(node.getNextSibling());
        if (78 == node.getType()) {
            AST variableNode = node.getNextSibling();
            AST multicatches = node.getFirstChild();
            if (multicatches.getType() != 79) {
                String variable = this.identifier(multicatches);
                Parameter catchParameter = new Parameter(ClassHelper.DYNAMIC_TYPE, variable);
                CatchStatement answer = new CatchStatement(catchParameter, code);
                this.configureAST(answer, catchNode);
                catches.add(answer);
            } else {
                String variable = this.identifier(multicatches.getNextSibling());
                for (AST exceptionNodes = multicatches.getFirstChild(); exceptionNodes != null; exceptionNodes = exceptionNodes.getNextSibling()) {
                    ClassNode exceptionType = this.buildName(exceptionNodes);
                    Parameter catchParameter = new Parameter(exceptionType, variable);
                    CatchStatement answer = new CatchStatement(catchParameter, code);
                    this.configureAST(answer, catchNode);
                    catches.add(answer);
                }
            }
        }
        return catches;
    }

    protected Statement whileStatement(AST whileNode) {
        AST node = whileNode.getFirstChild();
        this.assertNodeType(28, node);
        if (AntlrParserPlugin.isType(9, node.getFirstChild())) {
            throw new ASTRuntimeException(whileNode, "While loop condition contains a declaration; this is currently unsupported.");
        }
        BooleanExpression booleanExpression = this.booleanExpression(node);
        Statement block = AntlrParserPlugin.isType(128, node = node.getNextSibling()) ? EmptyStatement.INSTANCE : this.statement(node);
        WhileStatement whileStatement = new WhileStatement(booleanExpression, block);
        this.configureAST(whileStatement, whileNode);
        return whileStatement;
    }

    protected Expression expression(AST node) {
        return this.expression(node, false);
    }

    protected Expression expression(AST node, boolean convertToConstant) {
        VariableExpression ve;
        Expression expression = this.expressionSwitch(node);
        if (convertToConstant && expression instanceof VariableExpression && !(ve = (VariableExpression)expression).isThisExpression() && !ve.isSuperExpression()) {
            expression = new ConstantExpression(ve.getName());
        }
        this.configureAST(expression, node);
        return expression;
    }

    protected Expression expressionSwitch(AST node) {
        int type = node.getType();
        switch (type) {
            case 28: {
                return this.expression(node.getFirstChild());
            }
            case 33: {
                return this.expressionList(node);
            }
            case 7: {
                return this.blockExpression(node);
            }
            case 50: {
                return this.closureExpression(node);
            }
            case 44: {
                return this.specialConstructorCallExpression(node, ClassNode.SUPER);
            }
            case 27: {
                return this.methodCallExpression(node);
            }
            case 159: {
                return this.constructorCallExpression(node);
            }
            case 45: {
                return this.specialConstructorCallExpression(node, ClassNode.THIS);
            }
            case 97: 
            case 174: {
                return this.ternaryExpression(node);
            }
            case 90: 
            case 154: 
            case 155: {
                return this.dotExpression(node);
            }
            case 87: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 132: {
                return this.variableExpression(node);
            }
            case 57: {
                return this.listExpression(node);
            }
            case 58: {
                return this.mapExpression(node);
            }
            case 54: {
                return this.mapEntryExpression(node);
            }
            case 55: {
                return this.spreadExpression(node);
            }
            case 56: {
                return this.spreadMapExpression(node);
            }
            case 156: {
                return this.methodPointerExpression(node);
            }
            case 24: {
                return this.indexExpression(node);
            }
            case 158: {
                return this.instanceofExpression(node);
            }
            case 114: {
                return this.asExpression(node);
            }
            case 23: {
                return this.castExpression(node);
            }
            case 161: {
                return this.literalExpression(node, Boolean.TRUE);
            }
            case 157: {
                return this.literalExpression(node, Boolean.FALSE);
            }
            case 160: {
                return this.literalExpression(node, null);
            }
            case 88: {
                return this.literalExpression(node, node.getText());
            }
            case 48: {
                return this.gstring(node);
            }
            case 200: 
            case 202: 
            case 204: {
                return this.decimalExpression(node);
            }
            case 199: 
            case 201: 
            case 203: {
                return this.integerExpression(node);
            }
            case 196: {
                NotExpression notExpression = new NotExpression(this.expression(node.getFirstChild()));
                this.configureAST(notExpression, node);
                return notExpression;
            }
            case 30: {
                return this.unaryMinusExpression(node);
            }
            case 195: {
                BitwiseNegationExpression bitwiseNegationExpression = new BitwiseNegationExpression(this.expression(node.getFirstChild()));
                this.configureAST(bitwiseNegationExpression, node);
                return bitwiseNegationExpression;
            }
            case 31: {
                return this.unaryPlusExpression(node);
            }
            case 190: {
                return this.prefixExpression(node, 250);
            }
            case 193: {
                return this.prefixExpression(node, 260);
            }
            case 25: {
                return this.postfixExpression(node, 250);
            }
            case 26: {
                return this.postfixExpression(node, 260);
            }
            case 124: {
                return this.binaryExpression(100, node);
            }
            case 181: {
                return this.binaryExpression(123, node);
            }
            case 182: {
                return this.binaryExpression(121, node);
            }
            case 180: {
                return this.binaryExpression(120, node);
            }
            case 183: {
                return this.binaryExpression(122, node);
            }
            case 184: {
                return this.binaryExpression(128, node);
            }
            case 185: {
                return this.binaryExpression(125, node);
            }
            case 89: {
                return this.binaryExpression(124, node);
            }
            case 100: {
                return this.binaryExpression(126, node);
            }
            case 186: {
                return this.binaryExpression(127, node);
            }
            case 176: {
                return this.binaryExpression(164, node);
            }
            case 175: {
                return this.binaryExpression(162, node);
            }
            case 125: {
                return this.binaryExpression(341, node);
            }
            case 170: {
                return this.binaryExpression(351, node);
            }
            case 134: {
                return this.binaryExpression(340, node);
            }
            case 172: {
                return this.binaryExpression(350, node);
            }
            case 177: {
                return this.binaryExpression(342, node);
            }
            case 171: {
                return this.binaryExpression(352, node);
            }
            case 148: {
                return this.binaryExpression(200, node);
            }
            case 162: {
                return this.binaryExpression(210, node);
            }
            case 149: {
                return this.binaryExpression(201, node);
            }
            case 163: {
                return this.binaryExpression(211, node);
            }
            case 113: {
                return this.binaryExpression(202, node);
            }
            case 164: {
                return this.binaryExpression(212, node);
            }
            case 194: {
                return this.binaryExpression(206, node);
            }
            case 173: {
                return this.binaryExpression(216, node);
            }
            case 191: {
                return this.binaryExpression(203, node);
            }
            case 165: {
                return this.binaryExpression(213, node);
            }
            case 192: {
                return this.binaryExpression(205, node);
            }
            case 166: {
                return this.binaryExpression(215, node);
            }
            case 187: {
                return this.binaryExpression(280, node);
            }
            case 169: {
                return this.binaryExpression(285, node);
            }
            case 102: {
                return this.binaryExpression(281, node);
            }
            case 167: {
                return this.binaryExpression(286, node);
            }
            case 103: {
                return this.binaryExpression(282, node);
            }
            case 168: {
                return this.binaryExpression(287, node);
            }
            case 9: {
                return this.declarationExpression(node);
            }
            case 178: {
                return this.binaryExpression(90, node);
            }
            case 179: {
                return this.binaryExpression(94, node);
            }
            case 188: {
                return this.rangeExpression(node, true);
            }
            case 189: {
                return this.rangeExpression(node, false);
            }
            case 53: {
                return this.dynamicMemberExpression(node);
            }
            case 142: {
                return this.binaryExpression(573, node);
            }
            case 66: {
                return new AnnotationConstantExpression(this.annotation(node));
            }
            case 77: {
                return this.closureListExpression(node);
            }
            case 85: 
            case 91: {
                return this.tupleExpression(node);
            }
            case 6: {
                return this.anonymousInnerClassDef(node);
            }
        }
        this.unknownAST(node);
        return null;
    }

    private TupleExpression tupleExpression(AST node) {
        TupleExpression exp = new TupleExpression();
        this.configureAST(exp, node);
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
            this.assertNodeType(9, node);
            AST nameNode = node.getFirstChild().getNextSibling();
            VariableExpression varExp = new VariableExpression(nameNode.getText());
            this.configureAST(varExp, nameNode);
            exp.addExpression(varExp);
        }
        return exp;
    }

    private ClosureListExpression closureListExpression(AST node) {
        this.isClosureListExpressionAllowedHere(node);
        LinkedList<Expression> list = new LinkedList<Expression>();
        for (AST exprNode = node.getFirstChild(); exprNode != null; exprNode = exprNode.getNextSibling()) {
            if (AntlrParserPlugin.isType(28, exprNode)) {
                Expression expr = this.expression(exprNode);
                this.configureAST(expr, exprNode);
                list.add(expr);
                continue;
            }
            this.assertNodeType(37, exprNode);
            list.add(EmptyExpression.INSTANCE);
        }
        ClosureListExpression cle = new ClosureListExpression(list);
        this.configureAST(cle, node);
        return cle;
    }

    private void isClosureListExpressionAllowedHere(AST node) {
        if (!this.forStatementBeingDef) {
            throw new ASTRuntimeException(node, "Expression list of the form (a; b; c) is not supported in this context.");
        }
    }

    protected Expression dynamicMemberExpression(AST dynamicMemberNode) {
        AST node = dynamicMemberNode.getFirstChild();
        return this.expression(node);
    }

    protected Expression ternaryExpression(AST ternaryNode) {
        TernaryExpression ret;
        AST node = ternaryNode.getFirstChild();
        Expression base = this.expression(node);
        node = node.getNextSibling();
        Expression left = this.expression(node);
        if ((node = node.getNextSibling()) == null) {
            ret = new ElvisOperatorExpression(base, left);
        } else {
            Expression right = this.expression(node);
            BooleanExpression booleanExpression = new BooleanExpression(base);
            booleanExpression.setSourcePosition(base);
            ret = new TernaryExpression(booleanExpression, left, right);
        }
        this.configureAST(ret, ternaryNode);
        return ret;
    }

    protected Expression variableExpression(AST node) {
        String text = node.getText();
        VariableExpression variableExpression = new VariableExpression(text);
        this.configureAST(variableExpression, node);
        return variableExpression;
    }

    protected Expression literalExpression(AST node, Object value) {
        ConstantExpression constantExpression = new ConstantExpression(value, value instanceof Boolean);
        this.configureAST(constantExpression, node);
        return constantExpression;
    }

    protected Expression rangeExpression(AST rangeNode, boolean inclusive) {
        AST node = rangeNode.getFirstChild();
        Expression left = this.expression(node);
        Expression right = this.expression(node.getNextSibling());
        RangeExpression rangeExpression = new RangeExpression(left, right, inclusive);
        this.configureAST(rangeExpression, rangeNode);
        return rangeExpression;
    }

    protected Expression spreadExpression(AST node) {
        AST exprNode = node.getFirstChild();
        AST listNode = exprNode.getFirstChild();
        Expression right = this.expression(listNode);
        SpreadExpression spreadExpression = new SpreadExpression(right);
        this.configureAST(spreadExpression, node);
        return spreadExpression;
    }

    protected Expression spreadMapExpression(AST node) {
        AST exprNode = node.getFirstChild();
        Expression expr = this.expression(exprNode);
        SpreadMapExpression spreadMapExpression = new SpreadMapExpression(expr);
        this.configureAST(spreadMapExpression, node);
        return spreadMapExpression;
    }

    protected Expression methodPointerExpression(AST node) {
        AST exprNode = node.getFirstChild();
        Expression objectExpression = this.expression(exprNode);
        AST mNode = exprNode.getNextSibling();
        Expression methodName = AntlrParserPlugin.isType(53, mNode) ? this.expression(mNode) : new ConstantExpression(this.identifier(mNode));
        this.configureAST(methodName, mNode);
        MethodPointerExpression methodPointerExpression = new MethodPointerExpression(objectExpression, methodName);
        this.configureAST(methodPointerExpression, node);
        return methodPointerExpression;
    }

    protected Expression listExpression(AST listNode) {
        ArrayList<Expression> expressions = new ArrayList<Expression>();
        AST elist = listNode.getFirstChild();
        this.assertNodeType(33, elist);
        for (AST node = elist.getFirstChild(); node != null; node = node.getNextSibling()) {
            switch (node.getType()) {
                case 54: {
                    this.assertNodeType(101, node);
                    break;
                }
                case 56: {
                    this.assertNodeType(55, node);
                }
            }
            expressions.add(this.expression(node));
        }
        ListExpression listExpression = new ListExpression(expressions);
        this.configureAST(listExpression, listNode);
        return listExpression;
    }

    protected Expression mapExpression(AST mapNode) {
        ArrayList<MapEntryExpression> expressions = new ArrayList<MapEntryExpression>();
        AST elist = mapNode.getFirstChild();
        if (elist != null) {
            this.assertNodeType(33, elist);
            for (AST node = elist.getFirstChild(); node != null; node = node.getNextSibling()) {
                switch (node.getType()) {
                    case 54: 
                    case 56: {
                        break;
                    }
                    case 55: {
                        this.assertNodeType(56, node);
                        break;
                    }
                    default: {
                        this.assertNodeType(54, node);
                    }
                }
                expressions.add(this.mapEntryExpression(node));
            }
        }
        MapExpression mapExpression = new MapExpression(expressions);
        this.configureAST(mapExpression, mapNode);
        return mapExpression;
    }

    protected MapEntryExpression mapEntryExpression(AST node) {
        if (node.getType() == 56) {
            AST rightNode = node.getFirstChild();
            Expression keyExpression = this.spreadMapExpression(node);
            Expression rightExpression = this.expression(rightNode);
            MapEntryExpression mapEntryExpression = new MapEntryExpression(keyExpression, rightExpression);
            this.configureAST(mapEntryExpression, node);
            return mapEntryExpression;
        }
        AST keyNode = node.getFirstChild();
        Expression keyExpression = this.expression(keyNode);
        AST valueNode = keyNode.getNextSibling();
        Expression valueExpression = this.expression(valueNode);
        MapEntryExpression mapEntryExpression = new MapEntryExpression(keyExpression, valueExpression);
        this.configureAST(mapEntryExpression, node);
        return mapEntryExpression;
    }

    protected Expression instanceofExpression(AST node) {
        AST leftNode = node.getFirstChild();
        Expression leftExpression = this.expression(leftNode);
        AST rightNode = leftNode.getNextSibling();
        ClassNode type = this.buildName(rightNode);
        this.assertTypeNotNull(type, rightNode);
        ClassExpression rightExpression = new ClassExpression(type);
        this.configureAST(rightExpression, rightNode);
        BinaryExpression binaryExpression = new BinaryExpression(leftExpression, AntlrParserPlugin.makeToken(544, node), rightExpression);
        this.configureAST(binaryExpression, node);
        return binaryExpression;
    }

    protected void assertTypeNotNull(ClassNode type, AST rightNode) {
        if (type == null) {
            throw new ASTRuntimeException(rightNode, "No type available for: " + AntlrParserPlugin.qualifiedName(rightNode));
        }
    }

    protected Expression asExpression(AST node) {
        AST leftNode = node.getFirstChild();
        Expression leftExpression = this.expression(leftNode);
        AST rightNode = leftNode.getNextSibling();
        ClassNode type = this.makeTypeWithArguments(rightNode);
        return CastExpression.asExpression(type, leftExpression);
    }

    protected Expression castExpression(AST castNode) {
        AST node = castNode.getFirstChild();
        ClassNode type = this.makeTypeWithArguments(node);
        this.assertTypeNotNull(type, node);
        AST expressionNode = node.getNextSibling();
        Expression expression = this.expression(expressionNode);
        CastExpression castExpression = new CastExpression(type, expression);
        this.configureAST(castExpression, castNode);
        return castExpression;
    }

    protected Expression indexExpression(AST indexNode) {
        AST bracket = indexNode.getFirstChild();
        AST leftNode = bracket.getNextSibling();
        Expression leftExpression = this.expression(leftNode);
        AST rightNode = leftNode.getNextSibling();
        Expression rightExpression = this.expression(rightNode);
        BinaryExpression binaryExpression = new BinaryExpression(leftExpression, AntlrParserPlugin.makeToken(30, bracket), rightExpression);
        this.configureAST(binaryExpression, indexNode);
        return binaryExpression;
    }

    protected Expression binaryExpression(int type, AST node) {
        Token token = AntlrParserPlugin.makeToken(type, node);
        AST leftNode = node.getFirstChild();
        Expression leftExpression = this.expression(leftNode);
        AST rightNode = leftNode.getNextSibling();
        if (rightNode == null) {
            return leftExpression;
        }
        if (!(!Types.ofType(type, 1100) || leftExpression instanceof VariableExpression || leftExpression.getClass() == PropertyExpression.class || leftExpression instanceof FieldExpression || leftExpression instanceof AttributeExpression || leftExpression instanceof DeclarationExpression || leftExpression instanceof TupleExpression)) {
            if (leftExpression instanceof ConstantExpression) {
                throw new ASTRuntimeException(node, "\n[" + ((ConstantExpression)leftExpression).getValue() + "] is a constant expression, but it should be a variable expression");
            }
            if (leftExpression instanceof BinaryExpression) {
                int lefttype = ((BinaryExpression)leftExpression).getOperation().getType();
                if (!Types.ofType(lefttype, 1100) && lefttype != 30) {
                    throw new ASTRuntimeException(node, "\n" + ((BinaryExpression)leftExpression).getText() + " is a binary expression, but it should be a variable expression");
                }
            } else {
                if (leftExpression instanceof GStringExpression) {
                    throw new ASTRuntimeException(node, "\n\"" + ((GStringExpression)leftExpression).getText() + "\" is a GString expression, but it should be a variable expression");
                }
                if (leftExpression instanceof MethodCallExpression) {
                    throw new ASTRuntimeException(node, "\n\"" + ((MethodCallExpression)leftExpression).getText() + "\" is a method call expression, but it should be a variable expression");
                }
                if (leftExpression instanceof MapExpression) {
                    throw new ASTRuntimeException(node, "\n'" + ((MapExpression)leftExpression).getText() + "' is a map expression, but it should be a variable expression");
                }
                throw new ASTRuntimeException(node, "\n" + leftExpression.getClass() + ", with its value '" + leftExpression.getText() + "', is a bad expression as the left hand side of an assignment operator");
            }
        }
        Expression rightExpression = this.expression(rightNode);
        BinaryExpression binaryExpression = new BinaryExpression(leftExpression, token, rightExpression);
        this.configureAST(binaryExpression, node);
        return binaryExpression;
    }

    protected Expression prefixExpression(AST node, int token) {
        Expression expression = this.expression(node.getFirstChild());
        PrefixExpression prefixExpression = new PrefixExpression(AntlrParserPlugin.makeToken(token, node), expression);
        this.configureAST(prefixExpression, node);
        return prefixExpression;
    }

    protected Expression postfixExpression(AST node, int token) {
        Expression expression = this.expression(node.getFirstChild());
        PostfixExpression postfixExpression = new PostfixExpression(expression, AntlrParserPlugin.makeToken(token, node));
        this.configureAST(postfixExpression, node);
        return postfixExpression;
    }

    protected BooleanExpression booleanExpression(AST node) {
        BooleanExpression booleanExpression = new BooleanExpression(this.expression(node));
        this.configureAST(booleanExpression, node);
        return booleanExpression;
    }

    protected Expression dotExpression(AST node) {
        AST identifierNode;
        AST leftNode = node.getFirstChild();
        if (leftNode != null && (identifierNode = leftNode.getNextSibling()) != null) {
            Expression leftExpression = this.expression(leftNode);
            if (AntlrParserPlugin.isType(52, identifierNode)) {
                Expression field = this.expression(identifierNode.getFirstChild(), true);
                AttributeExpression attributeExpression = new AttributeExpression(leftExpression, field, node.getType() != 90);
                if (node.getType() == 154) {
                    attributeExpression.setSpreadSafe(true);
                }
                this.configureAST(attributeExpression, node);
                return attributeExpression;
            }
            if (AntlrParserPlugin.isType(7, identifierNode)) {
                Statement code = this.statementList(identifierNode);
                ClosureExpression closureExpression = new ClosureExpression(Parameter.EMPTY_ARRAY, code);
                this.configureAST(closureExpression, identifierNode);
                PropertyExpression propertyExpression = new PropertyExpression(leftExpression, closureExpression);
                if (node.getType() == 154) {
                    propertyExpression.setSpreadSafe(true);
                }
                this.configureAST(propertyExpression, node);
                return propertyExpression;
            }
            Expression property = this.expression(identifierNode, true);
            if (property instanceof VariableExpression) {
                VariableExpression ve = (VariableExpression)property;
                property = new ConstantExpression(ve.getName());
            }
            PropertyExpression propertyExpression = new PropertyExpression(leftExpression, property, node.getType() != 90);
            if (node.getType() == 154) {
                propertyExpression.setSpreadSafe(true);
            }
            this.configureAST(propertyExpression, node);
            return propertyExpression;
        }
        return this.methodCallExpression(node);
    }

    protected Expression specialConstructorCallExpression(AST methodCallNode, ClassNode special) {
        AST node = methodCallNode.getFirstChild();
        Expression arguments = this.arguments(node);
        ConstructorCallExpression expression = new ConstructorCallExpression(special, arguments);
        this.configureAST(expression, methodCallNode);
        return expression;
    }

    protected Expression methodCallExpression(AST methodCallNode) {
        AST selector;
        Expression objectExpression;
        AST node = methodCallNode.getFirstChild();
        AST elist = node.getNextSibling();
        List<GenericsType> typeArgumentList = null;
        boolean implicitThis = false;
        boolean safe = AntlrParserPlugin.isType(155, node);
        boolean spreadSafe = AntlrParserPlugin.isType(154, node);
        if (AntlrParserPlugin.isType(90, node) || safe || spreadSafe) {
            AST objectNode = node.getFirstChild();
            objectExpression = this.expression(objectNode);
            selector = objectNode.getNextSibling();
        } else {
            implicitThis = true;
            objectExpression = VariableExpression.THIS_EXPRESSION;
            selector = node;
        }
        if (AntlrParserPlugin.isType(70, selector)) {
            typeArgumentList = this.getTypeArgumentsList(selector);
            selector = selector.getNextSibling();
        }
        Expression name = null;
        if (AntlrParserPlugin.isType(99, selector)) {
            implicitThis = true;
            name = new ConstantExpression("super");
            if (objectExpression instanceof VariableExpression && ((VariableExpression)objectExpression).isThisExpression()) {
                objectExpression = VariableExpression.SUPER_EXPRESSION;
            }
        } else {
            if (this.isPrimitiveTypeLiteral(selector)) {
                throw new ASTRuntimeException(selector, "Primitive type literal: " + selector.getText() + " cannot be used as a method name");
            }
            if (AntlrParserPlugin.isType(52, selector)) {
                Expression field = this.expression(selector.getFirstChild(), true);
                AttributeExpression attributeExpression = new AttributeExpression(objectExpression, field, node.getType() != 90);
                this.configureAST(attributeExpression, node);
                Expression arguments = this.arguments(elist);
                MethodCallExpression expression = new MethodCallExpression((Expression)attributeExpression, "call", arguments);
                AntlrParserPlugin.setTypeArgumentsOnMethodCallExpression(expression, typeArgumentList);
                this.configureAST(expression, methodCallNode);
                return expression;
            }
            if (!implicitThis || AntlrParserPlugin.isType(53, selector) || AntlrParserPlugin.isType(87, selector) || AntlrParserPlugin.isType(48, selector) || AntlrParserPlugin.isType(88, selector)) {
                name = this.expression(selector, true);
            } else {
                implicitThis = false;
                name = new ConstantExpression("call");
                objectExpression = this.expression(selector, true);
            }
        }
        if (selector.getText().equals("this") || selector.getText().equals("super")) {
            throw new ASTRuntimeException(elist, "Constructor call must be the first statement in a constructor.");
        }
        Expression arguments = this.arguments(elist);
        MethodCallExpression expression = new MethodCallExpression(objectExpression, name, arguments);
        expression.setSafe(safe);
        expression.setSpreadSafe(spreadSafe);
        expression.setImplicitThis(implicitThis);
        AntlrParserPlugin.setTypeArgumentsOnMethodCallExpression(expression, typeArgumentList);
        Expression ret = expression;
        if (implicitThis && "this".equals(expression.getMethodAsString())) {
            ret = new ConstructorCallExpression(this.classNode, arguments);
        }
        this.configureAST(ret, methodCallNode);
        return ret;
    }

    private static void setTypeArgumentsOnMethodCallExpression(MethodCallExpression expression, List<GenericsType> typeArgumentList) {
        if (typeArgumentList != null && !typeArgumentList.isEmpty()) {
            expression.setGenericsTypes(typeArgumentList.toArray(new GenericsType[typeArgumentList.size()]));
        }
    }

    protected Expression constructorCallExpression(AST node) {
        AST elist;
        AST constructorCallNode = node;
        ClassNode type = this.makeTypeWithArguments(constructorCallNode);
        if (AntlrParserPlugin.isType(45, node) || AntlrParserPlugin.isType(159, node)) {
            node = node.getFirstChild();
        }
        if ((elist = node.getNextSibling()) == null && AntlrParserPlugin.isType(33, node)) {
            elist = node;
            if ("(".equals(type.getName())) {
                type = this.classNode;
            }
        }
        if (AntlrParserPlugin.isType(17, elist)) {
            AST expressionNode = elist.getFirstChild();
            if (expressionNode == null) {
                throw new ASTRuntimeException(elist, "No expression for the array constructor call");
            }
            List size = this.arraySizeExpression(expressionNode);
            ArrayExpression arrayExpression = new ArrayExpression(type, null, size);
            this.configureAST(arrayExpression, constructorCallNode);
            return arrayExpression;
        }
        Expression arguments = this.arguments(elist);
        ClassNode innerClass = AntlrParserPlugin.getAnonymousInnerClassNode(arguments);
        ConstructorCallExpression ret = new ConstructorCallExpression(type, arguments);
        if (innerClass != null) {
            ret.setType(innerClass);
            ret.setUsingAnonymousInnerClass(true);
            innerClass.setUnresolvedSuperClass(type);
        }
        this.configureAST(ret, constructorCallNode);
        return ret;
    }

    private static ClassNode getAnonymousInnerClassNode(Expression arguments) {
        if (arguments instanceof TupleExpression) {
            TupleExpression te = (TupleExpression)arguments;
            List<Expression> expressions = te.getExpressions();
            if (expressions.isEmpty()) {
                return null;
            }
            Expression last = expressions.remove(expressions.size() - 1);
            if (last instanceof AnonymousInnerClassCarrier) {
                AnonymousInnerClassCarrier carrier = (AnonymousInnerClassCarrier)last;
                return carrier.innerClass;
            }
            expressions.add(last);
        } else if (arguments instanceof AnonymousInnerClassCarrier) {
            AnonymousInnerClassCarrier carrier = (AnonymousInnerClassCarrier)arguments;
            return carrier.innerClass;
        }
        return null;
    }

    protected List arraySizeExpression(AST node) {
        List<Expression> list;
        Expression size = null;
        if (AntlrParserPlugin.isType(17, node)) {
            AST right = node.getNextSibling();
            size = right != null ? this.expression(right) : ConstantExpression.EMPTY_EXPRESSION;
            AST child = node.getFirstChild();
            if (child == null) {
                throw new ASTRuntimeException(node, "No expression for the array constructor call");
            }
            list = this.arraySizeExpression(child);
        } else {
            size = this.expression(node);
            list = new ArrayList<Expression>();
        }
        list.add(size);
        return list;
    }

    protected Expression enumArguments(AST elist) {
        ArrayList<Expression> expressionList = new ArrayList<Expression>();
        for (AST node = elist; node != null; node = node.getNextSibling()) {
            Expression expression = this.expression(node);
            expressionList.add(expression);
        }
        ArgumentListExpression argumentListExpression = new ArgumentListExpression(expressionList);
        this.configureAST(argumentListExpression, elist);
        return argumentListExpression;
    }

    protected Expression arguments(AST elist) {
        ArrayList<Expression> expressionList = new ArrayList<Expression>();
        boolean namedArguments = false;
        for (AST node = elist; node != null; node = node.getNextSibling()) {
            if (AntlrParserPlugin.isType(33, node)) {
                for (Object child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    namedArguments |= this.addArgumentExpression((AST)child, expressionList);
                }
                continue;
            }
            namedArguments |= this.addArgumentExpression(node, expressionList);
        }
        if (namedArguments) {
            if (!expressionList.isEmpty()) {
                ArrayList<Expression> argumentList = new ArrayList<Expression>();
                for (Object e : expressionList) {
                    Expression expression = (Expression)e;
                    if (expression instanceof MapEntryExpression) continue;
                    argumentList.add(expression);
                }
                if (!argumentList.isEmpty()) {
                    expressionList.removeAll(argumentList);
                    AntlrParserPlugin.checkDuplicateNamedParams(elist, expressionList);
                    MapExpression mapExpression = new MapExpression(expressionList);
                    this.configureAST(mapExpression, elist);
                    argumentList.add(0, mapExpression);
                    ArgumentListExpression argumentListExpression = new ArgumentListExpression(argumentList);
                    this.configureAST(argumentListExpression, elist);
                    return argumentListExpression;
                }
            }
            AntlrParserPlugin.checkDuplicateNamedParams(elist, expressionList);
            NamedArgumentListExpression namedArgumentListExpression = new NamedArgumentListExpression(expressionList);
            this.configureAST(namedArgumentListExpression, elist);
            return namedArgumentListExpression;
        }
        ArgumentListExpression argumentListExpression = new ArgumentListExpression(expressionList);
        this.configureAST(argumentListExpression, elist);
        return argumentListExpression;
    }

    private static void checkDuplicateNamedParams(AST elist, List expressionList) {
        if (expressionList.isEmpty()) {
            return;
        }
        HashSet<String> namedArgumentNames = new HashSet<String>();
        for (Object expression : expressionList) {
            MapEntryExpression meExp = (MapEntryExpression)expression;
            if (!(meExp.getKeyExpression() instanceof ConstantExpression)) continue;
            String argName = meExp.getKeyExpression().getText();
            if (!namedArgumentNames.contains(argName)) {
                namedArgumentNames.add(argName);
                continue;
            }
            throw new ASTRuntimeException(elist, "Duplicate named parameter '" + argName + "' found.");
        }
    }

    protected boolean addArgumentExpression(AST node, List<Expression> expressionList) {
        if (node.getType() == 56) {
            AST rightNode = node.getFirstChild();
            Expression keyExpression = this.spreadMapExpression(node);
            Expression rightExpression = this.expression(rightNode);
            MapEntryExpression mapEntryExpression = new MapEntryExpression(keyExpression, rightExpression);
            expressionList.add(mapEntryExpression);
            return true;
        }
        Expression expression = this.expression(node);
        expressionList.add(expression);
        return expression instanceof MapEntryExpression;
    }

    protected Expression expressionList(AST node) {
        ArrayList<Expression> expressionList = new ArrayList<Expression>();
        for (AST child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            expressionList.add(this.expression(child));
        }
        if (expressionList.size() == 1) {
            return (Expression)expressionList.get(0);
        }
        ListExpression listExpression = new ListExpression(expressionList);
        listExpression.setWrapped(true);
        this.configureAST(listExpression, node);
        return listExpression;
    }

    protected ClosureExpression closureExpression(AST node) {
        AST paramNode = node.getFirstChild();
        Parameter[] parameters = null;
        AST codeNode = paramNode;
        if (AntlrParserPlugin.isType(20, paramNode) || AntlrParserPlugin.isType(51, paramNode)) {
            parameters = this.parameters(paramNode);
            codeNode = paramNode.getNextSibling();
        }
        Statement code = this.statementListNoChild(codeNode, node);
        ClosureExpression closureExpression = new ClosureExpression(parameters, code);
        this.configureAST(closureExpression, node);
        return closureExpression;
    }

    protected Expression blockExpression(AST node) {
        AST codeNode = node.getFirstChild();
        if (codeNode == null) {
            return ConstantExpression.NULL;
        }
        if (codeNode.getType() == 28 && codeNode.getNextSibling() == null) {
            return this.expression(codeNode);
        }
        Parameter[] parameters = Parameter.EMPTY_ARRAY;
        Statement code = this.statementListNoChild(codeNode, node);
        ClosureExpression closureExpression = new ClosureExpression(parameters, code);
        this.configureAST(closureExpression, node);
        String callName = "call";
        ArgumentListExpression noArguments = new ArgumentListExpression();
        MethodCallExpression call = new MethodCallExpression((Expression)closureExpression, callName, (Expression)noArguments);
        this.configureAST(call, node);
        return call;
    }

    protected Expression unaryMinusExpression(AST unaryMinusExpr) {
        AST node = unaryMinusExpr.getFirstChild();
        String text = node.getText();
        switch (node.getType()) {
            case 200: 
            case 202: 
            case 204: {
                ConstantExpression constantExpression = new ConstantExpression(Numbers.parseDecimal("-" + text));
                this.configureAST(constantExpression, unaryMinusExpr);
                return constantExpression;
            }
            case 199: 
            case 201: 
            case 203: {
                ConstantExpression constantLongExpression = new ConstantExpression(Numbers.parseInteger("-" + text));
                this.configureAST(constantLongExpression, unaryMinusExpr);
                return constantLongExpression;
            }
        }
        UnaryMinusExpression unaryMinusExpression = new UnaryMinusExpression(this.expression(node));
        this.configureAST(unaryMinusExpression, unaryMinusExpr);
        return unaryMinusExpression;
    }

    protected Expression unaryPlusExpression(AST unaryPlusExpr) {
        AST node = unaryPlusExpr.getFirstChild();
        switch (node.getType()) {
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                return this.expression(node);
            }
        }
        UnaryPlusExpression unaryPlusExpression = new UnaryPlusExpression(this.expression(node));
        this.configureAST(unaryPlusExpression, unaryPlusExpr);
        return unaryPlusExpression;
    }

    protected ConstantExpression decimalExpression(AST node) {
        Number number;
        String text = node.getText();
        ConstantExpression constantExpression = new ConstantExpression(number, (number = Numbers.parseDecimal(text)) instanceof Double || number instanceof Float);
        this.configureAST(constantExpression, node);
        return constantExpression;
    }

    protected ConstantExpression integerExpression(AST node) {
        String text = node.getText();
        Number number = Numbers.parseInteger(text);
        boolean keepPrimitive = number instanceof Integer || number instanceof Long;
        ConstantExpression constantExpression = new ConstantExpression(number, keepPrimitive);
        this.configureAST(constantExpression, node);
        return constantExpression;
    }

    protected Expression gstring(AST gstringNode) {
        ArrayList<ConstantExpression> strings = new ArrayList<ConstantExpression>();
        ArrayList<Expression> values = new ArrayList<Expression>();
        StringBuilder buffer = new StringBuilder();
        boolean isPrevString = false;
        block3: for (AST node = gstringNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            int type = node.getType();
            String text = null;
            switch (type) {
                case 88: {
                    if (isPrevString) {
                        this.assertNodeType(87, node);
                    }
                    isPrevString = true;
                    text = node.getText();
                    ConstantExpression constantExpression = new ConstantExpression(text);
                    this.configureAST(constantExpression, node);
                    strings.add(constantExpression);
                    buffer.append(text);
                    continue block3;
                }
                default: {
                    if (!isPrevString) {
                        this.assertNodeType(87, node);
                    }
                    isPrevString = false;
                    Expression expression = this.expression(node);
                    values.add(expression);
                    buffer.append("$");
                    buffer.append(expression.getText());
                }
            }
        }
        GStringExpression gStringExpression = new GStringExpression(buffer.toString(), strings, values);
        this.configureAST(gStringExpression, gstringNode);
        return gStringExpression;
    }

    protected ClassNode type(AST typeNode) {
        return this.buildName(typeNode.getFirstChild());
    }

    public static String qualifiedName(AST qualifiedNameNode) {
        if (AntlrParserPlugin.isType(87, qualifiedNameNode)) {
            return qualifiedNameNode.getText();
        }
        if (AntlrParserPlugin.isType(90, qualifiedNameNode)) {
            StringBuilder buffer = new StringBuilder();
            boolean first = true;
            for (AST node = qualifiedNameNode.getFirstChild(); node != null && !AntlrParserPlugin.isType(70, node); node = node.getNextSibling()) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(".");
                }
                buffer.append(AntlrParserPlugin.qualifiedName(node));
            }
            return buffer.toString();
        }
        return qualifiedNameNode.getText();
    }

    private int getBoundType(AST node) {
        if (node == null) {
            return -1;
        }
        if (AntlrParserPlugin.isType(75, node)) {
            return 75;
        }
        if (AntlrParserPlugin.isType(76, node)) {
            return 76;
        }
        throw new ASTRuntimeException(node, "Unexpected node type: " + this.getTokenName(node) + " found when expecting type: " + this.getTokenName(75) + " or type: " + this.getTokenName(76));
    }

    private GenericsType makeGenericsArgumentType(AST typeArgument) {
        GenericsType gt;
        AST rootNode = typeArgument.getFirstChild();
        if (AntlrParserPlugin.isType(74, rootNode)) {
            ClassNode base = ClassHelper.makeWithoutCaching("?");
            if (rootNode.getNextSibling() != null) {
                int boundType = this.getBoundType(rootNode.getNextSibling());
                ClassNode[] gts = this.makeGenericsBounds(rootNode, boundType);
                gt = boundType == 75 ? new GenericsType(base, gts, null) : new GenericsType(base, null, gts[0]);
            } else {
                gt = new GenericsType(base, null, null);
            }
            gt.setName("?");
            gt.setWildcard(true);
        } else {
            ClassNode argument = this.makeTypeWithArguments(rootNode);
            gt = new GenericsType(argument);
        }
        this.configureAST(gt, typeArgument);
        return gt;
    }

    protected ClassNode makeTypeWithArguments(AST rootNode) {
        ClassNode basicType = this.makeType(rootNode);
        AST node = rootNode.getFirstChild();
        if (node == null || AntlrParserPlugin.isType(24, node) || AntlrParserPlugin.isType(17, node)) {
            return basicType;
        }
        if (!AntlrParserPlugin.isType(90, node)) {
            if ((node = node.getFirstChild()) == null) {
                return basicType;
            }
            return this.addTypeArguments(basicType, node);
        }
        for (node = node.getFirstChild(); node != null && !AntlrParserPlugin.isType(70, node); node = node.getNextSibling()) {
        }
        return node == null ? basicType : this.addTypeArguments(basicType, node);
    }

    private ClassNode addTypeArguments(ClassNode basicType, AST node) {
        List<GenericsType> typeArgumentList = this.getTypeArgumentsList(node);
        basicType.setGenericsTypes(typeArgumentList.toArray(new GenericsType[typeArgumentList.size()]));
        return basicType;
    }

    private List<GenericsType> getTypeArgumentsList(AST node) {
        this.assertNodeType(70, node);
        LinkedList<GenericsType> typeArgumentList = new LinkedList<GenericsType>();
        for (AST typeArgument = node.getFirstChild(); typeArgument != null; typeArgument = typeArgument.getNextSibling()) {
            this.assertNodeType(71, typeArgument);
            GenericsType gt = this.makeGenericsArgumentType(typeArgument);
            typeArgumentList.add(gt);
        }
        return typeArgumentList;
    }

    private ClassNode[] makeGenericsBounds(AST rn, int boundType) {
        AST boundsRoot = rn.getNextSibling();
        if (boundsRoot == null) {
            return null;
        }
        this.assertNodeType(boundType, boundsRoot);
        LinkedList<ClassNode> bounds = new LinkedList<ClassNode>();
        for (AST boundsNode = boundsRoot.getFirstChild(); boundsNode != null; boundsNode = boundsNode.getNextSibling()) {
            ClassNode bound = null;
            bound = this.makeTypeWithArguments(boundsNode);
            this.configureAST(bound, boundsNode);
            bounds.add(bound);
        }
        if (bounds.isEmpty()) {
            return null;
        }
        return bounds.toArray(new ClassNode[bounds.size()]);
    }

    protected GenericsType[] makeGenericsType(AST rootNode) {
        AST typeParameter = rootNode.getFirstChild();
        LinkedList<GenericsType> ret = new LinkedList<GenericsType>();
        this.assertNodeType(73, typeParameter);
        while (AntlrParserPlugin.isType(73, typeParameter)) {
            AST typeNode = typeParameter.getFirstChild();
            ClassNode type = this.makeType(typeParameter);
            GenericsType gt = new GenericsType(type, this.makeGenericsBounds(typeNode, 75), null);
            this.configureAST(gt, typeParameter);
            ret.add(gt);
            typeParameter = typeParameter.getNextSibling();
        }
        return ret.toArray(new GenericsType[0]);
    }

    protected ClassNode makeType(AST typeNode) {
        ClassNode answer = ClassHelper.DYNAMIC_TYPE;
        AST node = typeNode.getFirstChild();
        if (node != null) {
            if (AntlrParserPlugin.isType(24, node) || AntlrParserPlugin.isType(17, node)) {
                answer = this.makeType(node).makeArray();
            } else {
                answer = ClassHelper.make(AntlrParserPlugin.qualifiedName(node));
                if (answer.isUsingGenerics()) {
                    ClassNode newAnswer = ClassHelper.makeWithoutCaching(answer.getName());
                    newAnswer.setRedirect(answer);
                    answer = newAnswer;
                }
            }
            this.configureAST(answer, node);
        }
        return answer;
    }

    protected ClassNode buildName(AST node) {
        if (AntlrParserPlugin.isType(12, node)) {
            node = node.getFirstChild();
        }
        ClassNode answer = null;
        if (AntlrParserPlugin.isType(90, node) || AntlrParserPlugin.isType(155, node)) {
            answer = ClassHelper.make(AntlrParserPlugin.qualifiedName(node));
        } else if (this.isPrimitiveTypeLiteral(node)) {
            answer = ClassHelper.make(node.getText());
        } else {
            if (AntlrParserPlugin.isType(24, node) || AntlrParserPlugin.isType(17, node)) {
                AST child = node.getFirstChild();
                answer = this.buildName(child).makeArray();
                this.configureAST(answer, node);
                return answer;
            }
            String identifier = node.getText();
            answer = ClassHelper.make(identifier);
        }
        AST nextSibling = node.getNextSibling();
        if (AntlrParserPlugin.isType(24, nextSibling) || AntlrParserPlugin.isType(17, node)) {
            answer = answer.makeArray();
            this.configureAST(answer, node);
            return answer;
        }
        this.configureAST(answer, node);
        return answer;
    }

    protected boolean isPrimitiveTypeLiteral(AST node) {
        int type = node.getType();
        switch (type) {
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                return true;
            }
        }
        return false;
    }

    protected String identifier(AST node) {
        this.assertNodeType(87, node);
        return node.getText();
    }

    protected String label(AST labelNode) {
        AST node = labelNode.getFirstChild();
        if (node == null) {
            return null;
        }
        return this.identifier(node);
    }

    protected boolean hasVisibility(int modifiers) {
        return (modifiers & 7) != 0;
    }

    protected void configureAST(ASTNode node, AST ast) {
        if (ast == null) {
            throw new ASTRuntimeException(ast, "PARSER BUG: Tried to configure " + node.getClass().getName() + " with null Node");
        }
        node.setColumnNumber(ast.getColumn());
        node.setLineNumber(ast.getLine());
        if (ast instanceof GroovySourceAST) {
            node.setLastColumnNumber(((GroovySourceAST)ast).getColumnLast());
            node.setLastLineNumber(((GroovySourceAST)ast).getLineLast());
        }
    }

    protected static Token makeToken(int typeCode, AST node) {
        return Token.newSymbol(typeCode, node.getLine(), node.getColumn());
    }

    protected String getFirstChildText(AST node) {
        AST child = node.getFirstChild();
        return child != null ? child.getText() : null;
    }

    public static boolean isType(int typeCode, AST node) {
        return node != null && node.getType() == typeCode;
    }

    private String getTokenName(int token) {
        if (this.tokenNames == null) {
            return "" + token;
        }
        return this.tokenNames[token];
    }

    private String getTokenName(AST node) {
        if (node == null) {
            return "null";
        }
        return this.getTokenName(node.getType());
    }

    protected void assertNodeType(int type, AST node) {
        if (node == null) {
            throw new ASTRuntimeException(node, "No child node available in AST when expecting type: " + this.getTokenName(type));
        }
        if (node.getType() != type) {
            throw new ASTRuntimeException(node, "Unexpected node type: " + this.getTokenName(node) + " found when expecting type: " + this.getTokenName(type));
        }
    }

    protected void notImplementedYet(AST node) {
        throw new ASTRuntimeException(node, "AST node not implemented yet for type: " + this.getTokenName(node));
    }

    protected void unknownAST(AST node) {
        if (node.getType() == 13) {
            throw new ASTRuntimeException(node, "Class definition not expected here. Please define the class at an appropriate place or perhaps try using a block/Closure instead.");
        }
        if (node.getType() == 8) {
            throw new ASTRuntimeException(node, "Method definition not expected here. Please define the method at an appropriate place or perhaps try using a block/Closure instead.");
        }
        throw new ASTRuntimeException(node, "Unknown type: " + this.getTokenName(node));
    }

    protected void dumpTree(AST ast) {
        for (AST node = ast.getFirstChild(); node != null; node = node.getNextSibling()) {
            this.dump(node);
        }
    }

    protected void dump(AST node) {
        System.out.println("Type: " + this.getTokenName(node) + " text: " + node.getText());
    }

    private static class AnonymousInnerClassCarrier
    extends Expression {
        ClassNode innerClass;

        private AnonymousInnerClassCarrier() {
        }

        @Override
        public Expression transformExpression(ExpressionTransformer transformer) {
            return null;
        }

        @Override
        public void setSourcePosition(ASTNode node) {
            super.setSourcePosition(node);
            this.innerClass.setSourcePosition(node);
        }

        @Override
        public void setColumnNumber(int columnNumber) {
            super.setColumnNumber(columnNumber);
            this.innerClass.setColumnNumber(columnNumber);
        }

        @Override
        public void setLineNumber(int lineNumber) {
            super.setLineNumber(lineNumber);
            this.innerClass.setLineNumber(lineNumber);
        }

        @Override
        public void setLastColumnNumber(int columnNumber) {
            super.setLastColumnNumber(columnNumber);
            this.innerClass.setLastColumnNumber(columnNumber);
        }

        @Override
        public void setLastLineNumber(int lineNumber) {
            super.setLastLineNumber(lineNumber);
            this.innerClass.setLastLineNumber(lineNumber);
        }
    }
}


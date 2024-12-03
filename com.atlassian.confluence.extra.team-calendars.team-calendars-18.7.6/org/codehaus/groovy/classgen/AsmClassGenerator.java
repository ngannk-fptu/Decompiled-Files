/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import groovy.lang.GroovyRuntimeException;
import groovyjarjarasm.asm.AnnotationVisitor;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.FieldVisitor;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Type;
import groovyjarjarasm.asm.util.TraceMethodVisitor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.InterfaceHelperClassNode;
import org.codehaus.groovy.ast.MethodNode;
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
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
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
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
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
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.BytecodeInstruction;
import org.codehaus.groovy.classgen.BytecodeSequence;
import org.codehaus.groovy.classgen.ClassGenerator;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.BytecodeVariable;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.MethodCallerMultiAdapter;
import org.codehaus.groovy.classgen.asm.MopWriter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.OptimizingStatementWriter;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.WriterControllerFactory;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.syntax.RuntimeParserException;

public class AsmClassGenerator
extends ClassGenerator {
    private final ClassVisitor cv;
    private GeneratorContext context;
    private String sourceFile;
    public static final MethodCallerMultiAdapter setField = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "setField", false, false);
    public static final MethodCallerMultiAdapter getField = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "getField", false, false);
    public static final MethodCallerMultiAdapter setGroovyObjectField = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "setGroovyObjectField", false, false);
    public static final MethodCallerMultiAdapter getGroovyObjectField = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "getGroovyObjectField", false, false);
    static final MethodCallerMultiAdapter setFieldOnSuper = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "setFieldOnSuper", false, false);
    static final MethodCallerMultiAdapter getFieldOnSuper = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "getFieldOnSuper", false, false);
    public static final MethodCallerMultiAdapter setProperty = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "setProperty", false, false);
    static final MethodCallerMultiAdapter getProperty = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "getProperty", false, false);
    static final MethodCallerMultiAdapter setGroovyObjectProperty = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "setGroovyObjectProperty", false, false);
    static final MethodCallerMultiAdapter getGroovyObjectProperty = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "getGroovyObjectProperty", false, false);
    static final MethodCallerMultiAdapter setPropertyOnSuper = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "setPropertyOnSuper", false, false);
    static final MethodCallerMultiAdapter getPropertyOnSuper = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "getPropertyOnSuper", false, false);
    static final MethodCaller spreadMap = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "spreadMap");
    static final MethodCaller despreadList = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "despreadList");
    static final MethodCaller getMethodPointer = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "getMethodPointer");
    static final MethodCaller createListMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "createList");
    static final MethodCaller createMapMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "createMap");
    static final MethodCaller createRangeMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "createRange");
    static final MethodCaller createPojoWrapperMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "createPojoWrapper");
    static final MethodCaller createGroovyObjectWrapperMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "createGroovyObjectWrapper");
    private Map<String, ClassNode> referencedClasses = new HashMap<String, ClassNode>();
    private boolean passingParams;
    public static final boolean CREATE_DEBUG_INFO = true;
    public static final boolean CREATE_LINE_NUMBER_INFO = true;
    public static final boolean ASM_DEBUG = false;
    private ASTNode currentASTNode = null;
    private Map genericParameterNames = null;
    private SourceUnit source;
    private WriterController controller;

    public AsmClassGenerator(SourceUnit source, GeneratorContext context, ClassVisitor classVisitor, String sourceFile) {
        this.source = source;
        this.context = context;
        this.cv = classVisitor;
        this.sourceFile = sourceFile;
        this.genericParameterNames = new HashMap();
    }

    @Override
    public SourceUnit getSourceUnit() {
        return this.source;
    }

    public WriterController getController() {
        return this.controller;
    }

    @Override
    public void visitClass(ClassNode classNode) {
        this.referencedClasses.clear();
        WriterControllerFactory factory = (WriterControllerFactory)classNode.getNodeMetaData(WriterControllerFactory.class);
        WriterController normalController = new WriterController();
        this.controller = factory != null ? factory.makeController(normalController) : normalController;
        this.controller.init(this, this.context, this.cv, classNode);
        if (this.controller.shouldOptimizeForInt() || factory != null) {
            OptimizingStatementWriter.setNodeMeta(this.controller.getTypeChooser(), classNode);
        }
        try {
            InnerClassNode innerClass;
            MethodNode enclosingMethod;
            this.cv.visit(this.controller.getBytecodeVersion(), AsmClassGenerator.adjustedClassModifiersForClassWriting(classNode), this.controller.getInternalClassName(), BytecodeHelper.getGenericsSignature(classNode), this.controller.getInternalBaseClassName(), BytecodeHelper.getClassInternalNames(classNode.getInterfaces()));
            this.cv.visitSource(this.sourceFile, null);
            if (classNode instanceof InnerClassNode && (enclosingMethod = (innerClass = (InnerClassNode)classNode).getEnclosingMethod()) != null) {
                String outerClassName = BytecodeHelper.getClassInternalName(innerClass.getOuterClass().getName());
                this.cv.visitOuterClass(outerClassName, enclosingMethod.getName(), BytecodeHelper.getMethodDescriptor(enclosingMethod));
            }
            if (classNode.getName().endsWith("package-info")) {
                PackageNode packageNode = classNode.getPackage();
                if (packageNode != null) {
                    this.visitAnnotations(classNode, packageNode, this.cv);
                }
                this.cv.visitEnd();
                return;
            }
            this.visitAnnotations(classNode, this.cv);
            if (classNode.isInterface()) {
                ClassNode owner = classNode;
                if (owner instanceof InnerClassNode) {
                    owner = owner.getOuterClass();
                }
                String outerClassName = classNode.getName();
                String name = outerClassName + "$" + this.context.getNextInnerClassIdx();
                this.controller.setInterfaceClassLoadingClass(new InterfaceHelperClassNode(owner, name, 4136, ClassHelper.OBJECT_TYPE, this.controller.getCallSiteWriter().getCallSites()));
                super.visitClass(classNode);
                this.createInterfaceSyntheticStaticFields();
            } else {
                super.visitClass(classNode);
                MopWriter.Factory mopWriterFactory = (MopWriter.Factory)classNode.getNodeMetaData(MopWriter.Factory.class);
                if (mopWriterFactory == null) {
                    mopWriterFactory = MopWriter.FACTORY;
                }
                MopWriter mopWriter = mopWriterFactory.create(this.controller);
                mopWriter.createMopMethods();
                this.controller.getCallSiteWriter().generateCallSiteArray();
                this.createSyntheticStaticFields();
            }
            Iterator<InnerClassNode> iter = classNode.getInnerClasses();
            while (iter.hasNext()) {
                InnerClassNode innerClass2 = iter.next();
                this.makeInnerClassEntry(innerClass2);
            }
            this.makeInnerClassEntry(classNode);
            this.cv.visitEnd();
        }
        catch (GroovyRuntimeException e) {
            e.setModule(classNode.getModule());
            throw e;
        }
        catch (NegativeArraySizeException nase) {
            throw new GroovyRuntimeException("NegativeArraySizeException while processing " + this.sourceFile, nase);
        }
        catch (NullPointerException npe) {
            throw new GroovyRuntimeException("NPE while processing " + this.sourceFile, npe);
        }
    }

    private void makeInnerClassEntry(ClassNode cn) {
        int mods;
        if (!(cn instanceof InnerClassNode)) {
            return;
        }
        InnerClassNode innerClass = (InnerClassNode)cn;
        String innerClassName = innerClass.getName();
        String innerClassInternalName = BytecodeHelper.getClassInternalName(innerClassName);
        int index = innerClassName.lastIndexOf(36);
        if (index >= 0) {
            innerClassName = innerClassName.substring(index + 1);
        }
        String outerClassName = BytecodeHelper.getClassInternalName(innerClass.getOuterClass().getName());
        MethodNode enclosingMethod = innerClass.getEnclosingMethod();
        if (enclosingMethod != null) {
            outerClassName = null;
            if (innerClass.isAnonymous()) {
                innerClassName = null;
            }
        }
        if (Modifier.isPrivate(mods = AsmClassGenerator.adjustedClassModifiersForInnerClassTable(cn))) {
            innerClass.setModifiers(mods ^= 2);
        }
        this.cv.visitInnerClass(innerClassInternalName, outerClassName, innerClassName, mods);
    }

    private static int adjustedClassModifiersForInnerClassTable(ClassNode classNode) {
        int modifiers = classNode.getModifiers();
        modifiers &= 0xFFFFFFDF;
        if (classNode.isInterface()) {
            modifiers &= 0xFFFFBFFF;
            modifiers &= 0xFFFFFFEF;
        }
        modifiers = AsmClassGenerator.fixInnerClassModifiers(classNode, modifiers);
        return modifiers;
    }

    private static int fixInnerClassModifiers(ClassNode classNode, int modifiers) {
        if (classNode instanceof InnerClassNode) {
            if (Modifier.isPrivate(modifiers)) {
                modifiers &= 0xFFFFFFFD;
            }
            if (Modifier.isProtected(modifiers)) {
                modifiers = modifiers & 0xFFFFFFFB | 1;
            }
        }
        return modifiers;
    }

    private static int adjustedClassModifiersForClassWriting(ClassNode classNode) {
        int modifiers = classNode.getModifiers();
        boolean needsSuper = !classNode.isInterface();
        modifiers = needsSuper ? modifiers | 0x20 : modifiers & 0xFFFFFFDF;
        modifiers &= 0xFFFFFFF7;
        modifiers = AsmClassGenerator.fixInnerClassModifiers(classNode, modifiers);
        if (classNode.isInterface()) {
            modifiers &= 0xFFFFBFFF;
            modifiers &= 0xFFFFFFEF;
        }
        return modifiers;
    }

    public void visitGenericType(GenericsType genericsType) {
        ClassNode type = genericsType.getType();
        this.genericParameterNames.put(type.getName(), genericsType);
    }

    private static String[] buildExceptions(ClassNode[] exceptions) {
        if (exceptions == null) {
            return null;
        }
        String[] ret = new String[exceptions.length];
        for (int i = 0; i < exceptions.length; ++i) {
            ret[i] = BytecodeHelper.getClassInternalName(exceptions[i]);
        }
        return ret;
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        this.controller.resetLineNumber();
        Parameter[] parameters = node.getParameters();
        String methodType = BytecodeHelper.getMethodDescriptor(node.getReturnType(), parameters);
        String signature = BytecodeHelper.getGenericsMethodSignature(node);
        int modifiers = node.getModifiers();
        if (AsmClassGenerator.isVargs(node.getParameters())) {
            modifiers |= 0x80;
        }
        MethodVisitor mv = this.cv.visitMethod(modifiers, node.getName(), methodType, signature, AsmClassGenerator.buildExceptions(node.getExceptions()));
        this.controller.setMethodVisitor(mv);
        this.visitAnnotations(node, mv);
        for (int i = 0; i < parameters.length; ++i) {
            this.visitParameterAnnotations(parameters[i], i, mv);
        }
        if (this.controller.getClassNode().isAnnotationDefinition() && !node.isStaticConstructor()) {
            this.visitAnnotationDefault(node, mv);
        } else if (!node.isAbstract()) {
            Statement code = node.getCode();
            mv.visitCode();
            if (code instanceof BytecodeSequence && ((BytecodeSequence)code).getInstructions().size() == 1 && ((BytecodeSequence)code).getInstructions().get(0) instanceof BytecodeInstruction) {
                ((BytecodeInstruction)((BytecodeSequence)code).getInstructions().get(0)).visit(mv);
            } else {
                this.visitStdMethod(node, isConstructor, parameters, code);
            }
            try {
                mv.visitMaxs(0, 0);
            }
            catch (Exception e) {
                StringWriter writer = null;
                if (mv instanceof TraceMethodVisitor) {
                    TraceMethodVisitor tracer = (TraceMethodVisitor)mv;
                    writer = new StringWriter();
                    PrintWriter p = new PrintWriter(writer);
                    tracer.p.print(p);
                    p.flush();
                }
                StringBuilder outBuffer = new StringBuilder();
                outBuffer.append("ASM reporting processing error for ");
                outBuffer.append(this.controller.getClassNode().toString() + "#" + node.getName());
                outBuffer.append(" with signature " + node.getTypeDescriptor());
                outBuffer.append(" in " + this.sourceFile + ":" + node.getLineNumber());
                if (writer != null) {
                    outBuffer.append("\nLast known generated bytecode in last generated method or constructor:\n");
                    outBuffer.append(writer);
                }
                throw new GroovyRuntimeException(outBuffer.toString(), e);
            }
        }
        mv.visitEnd();
    }

    private void visitStdMethod(MethodNode node, boolean isConstructor, Parameter[] parameters, Statement code) {
        this.controller.getCompileStack().init(node.getVariableScope(), parameters);
        this.controller.getCallSiteWriter().makeSiteEntry();
        MethodVisitor mv = this.controller.getMethodVisitor();
        ClassNode superClass = this.controller.getClassNode().getSuperClass();
        if (isConstructor && (code == null || !((ConstructorNode)node).firstStatementIsSpecialConstructorCall())) {
            boolean hasCallToSuper = false;
            if (code != null && this.controller.getClassNode() instanceof InnerClassNode && code instanceof BlockStatement) {
                for (Statement statement : ((BlockStatement)code).getStatements()) {
                    ConstructorCallExpression call;
                    Expression expression;
                    if (!(statement instanceof ExpressionStatement) || !((expression = ((ExpressionStatement)statement).getExpression()) instanceof ConstructorCallExpression) || !(call = (ConstructorCallExpression)expression).isSuperCall()) continue;
                    hasCallToSuper = true;
                    break;
                }
            }
            if (!hasCallToSuper) {
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(183, BytecodeHelper.getClassInternalName(superClass), "<init>", "()V", false);
            }
        }
        super.visitConstructorOrMethod(node, isConstructor);
        this.controller.getCompileStack().clear();
        if (node.isVoidMethod()) {
            mv.visitInsn(177);
        } else {
            ClassNode type = node.getReturnType().redirect();
            if (ClassHelper.isPrimitiveType(type)) {
                mv.visitLdcInsn(0);
                this.controller.getOperandStack().push(ClassHelper.int_TYPE);
                this.controller.getOperandStack().doGroovyCast(type);
                BytecodeHelper.doReturn(mv, type);
                this.controller.getOperandStack().remove(1);
            } else {
                mv.visitInsn(1);
                BytecodeHelper.doReturn(mv, type);
            }
        }
    }

    void visitAnnotationDefaultExpression(AnnotationVisitor av, ClassNode type, Expression exp) {
        if (exp instanceof ClosureExpression) {
            ClassNode closureClass = this.controller.getClosureWriter().getOrAddClosureClass((ClosureExpression)exp, 1);
            Type t = Type.getType(BytecodeHelper.getTypeDescription(closureClass));
            av.visit(null, t);
        } else if (type.isArray()) {
            ListExpression list = (ListExpression)exp;
            AnnotationVisitor avl = av.visitArray(null);
            ClassNode componentType = type.getComponentType();
            for (Expression lExp : list.getExpressions()) {
                this.visitAnnotationDefaultExpression(avl, componentType, lExp);
            }
        } else if (ClassHelper.isPrimitiveType(type) || type.equals(ClassHelper.STRING_TYPE)) {
            ConstantExpression constExp = (ConstantExpression)exp;
            av.visit(null, constExp.getValue());
        } else if (ClassHelper.CLASS_Type.equals(type)) {
            ClassNode clazz = exp.getType();
            Type t = Type.getType(BytecodeHelper.getTypeDescription(clazz));
            av.visit(null, t);
        } else if (type.isDerivedFrom(ClassHelper.Enum_Type)) {
            PropertyExpression pExp = (PropertyExpression)exp;
            ClassExpression cExp = (ClassExpression)pExp.getObjectExpression();
            String desc = BytecodeHelper.getTypeDescription(cExp.getType());
            String name = pExp.getPropertyAsString();
            av.visitEnum(null, desc, name);
        } else if (type.implementsInterface(ClassHelper.Annotation_TYPE)) {
            AnnotationConstantExpression avExp = (AnnotationConstantExpression)exp;
            AnnotationNode value = (AnnotationNode)avExp.getValue();
            AnnotationVisitor avc = av.visitAnnotation(null, BytecodeHelper.getTypeDescription(avExp.getType()));
            this.visitAnnotationAttributes(value, avc);
        } else {
            throw new GroovyBugError("unexpected annotation type " + type.getName());
        }
        av.visitEnd();
    }

    private void visitAnnotationDefault(MethodNode node, MethodVisitor mv) {
        if (!node.hasAnnotationDefault()) {
            return;
        }
        Expression exp = ((ReturnStatement)node.getCode()).getExpression();
        AnnotationVisitor av = mv.visitAnnotationDefault();
        this.visitAnnotationDefaultExpression(av, node.getReturnType(), exp);
    }

    private static boolean isVargs(Parameter[] p) {
        if (p.length == 0) {
            return false;
        }
        ClassNode clazz = p[p.length - 1].getType();
        return clazz.isArray();
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        this.controller.setConstructorNode(node);
        super.visitConstructor(node);
    }

    @Override
    public void visitMethod(MethodNode node) {
        this.controller.setMethodNode(node);
        super.visitMethod(node);
    }

    @Override
    public void visitField(FieldNode fieldNode) {
        Integer value;
        ConstantExpression cexp;
        this.onLineNumber(fieldNode, "visitField: " + fieldNode.getName());
        ClassNode t = fieldNode.getType();
        String signature = BytecodeHelper.getGenericsBounds(t);
        Expression initialValueExpression = fieldNode.getInitialValueExpression();
        ConstantExpression constantExpression = cexp = initialValueExpression instanceof ConstantExpression ? (ConstantExpression)initialValueExpression : null;
        if (cexp != null) {
            cexp = Verifier.transformToPrimitiveConstantIfPossible(cexp);
        }
        Integer n = value = cexp != null && ClassHelper.isStaticConstantInitializerType(cexp.getType()) && cexp.getType().equals(t) && fieldNode.isStatic() && fieldNode.isFinal() ? cexp.getValue() : null;
        if (value != null) {
            if (ClassHelper.byte_TYPE.equals(t) || ClassHelper.short_TYPE.equals(t)) {
                value = ((Number)value).intValue();
            } else if (ClassHelper.char_TYPE.equals(t)) {
                value = ((Character)((Object)value)).charValue();
            }
        }
        FieldVisitor fv = this.cv.visitField(fieldNode.getModifiers(), fieldNode.getName(), BytecodeHelper.getTypeDescription(t), signature, value);
        this.visitAnnotations(fieldNode, fv);
        fv.visitEnd();
    }

    @Override
    public void visitProperty(PropertyNode statement) {
        this.onLineNumber(statement, "visitProperty:" + statement.getField().getName());
        this.controller.setMethodNode(null);
    }

    @Override
    protected void visitStatement(Statement statement) {
        throw new GroovyBugError("visitStatement should not be visited here.");
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        statement.getCode().visit(this);
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        this.controller.getStatementWriter().writeBlockStatement(block);
    }

    @Override
    public void visitForLoop(ForStatement loop) {
        this.controller.getStatementWriter().writeForStatement(loop);
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        this.controller.getStatementWriter().writeWhileLoop(loop);
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        this.controller.getStatementWriter().writeDoWhileLoop(loop);
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        this.controller.getStatementWriter().writeIfElse(ifElse);
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        this.controller.getStatementWriter().writeAssert(statement);
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        this.controller.getStatementWriter().writeTryCatchFinally(statement);
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        this.controller.getStatementWriter().writeSwitch(statement);
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
        this.controller.getStatementWriter().writeBreak(statement);
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
        this.controller.getStatementWriter().writeContinue(statement);
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        this.controller.getStatementWriter().writeSynchronized(statement);
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        this.controller.getStatementWriter().writeThrow(statement);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        this.controller.getStatementWriter().writeReturn(statement);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        this.controller.getStatementWriter().writeExpressionStatement(statement);
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        this.onLineNumber(expression, "visitTernaryExpression");
        this.controller.getBinaryExpressionHelper().evaluateTernary(expression);
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        this.onLineNumber(expression, "visitDeclarationExpression: \"" + expression.getText() + "\"");
        this.controller.getBinaryExpressionHelper().evaluateEqual(expression, true);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        this.onLineNumber(expression, "visitBinaryExpression: \"" + expression.getOperation().getText() + "\" ");
        this.controller.getBinaryExpressionHelper().eval(expression);
        this.controller.getAssertionWriter().record(expression.getOperation());
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        this.controller.getBinaryExpressionHelper().evaluatePostfixMethod(expression);
        this.controller.getAssertionWriter().record(expression);
    }

    public void throwException(String s) {
        throw new RuntimeParserException(s, this.currentASTNode);
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        this.controller.getBinaryExpressionHelper().evaluatePrefixMethod(expression);
        this.controller.getAssertionWriter().record(expression);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        this.controller.getClosureWriter().writeClosure(expression);
    }

    protected void loadThisOrOwner() {
        if (this.isInnerClass()) {
            this.visitFieldExpression(new FieldExpression(this.controller.getClassNode().getDeclaredField("owner")));
        } else {
            this.loadThis(null);
        }
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
        String constantName = expression.getConstantName();
        if (this.controller.isStaticConstructor() || constantName == null) {
            this.controller.getOperandStack().pushConstant(expression);
        } else {
            this.controller.getMethodVisitor().visitFieldInsn(178, this.controller.getInternalClassName(), constantName, BytecodeHelper.getTypeDescription(expression.getType()));
            this.controller.getOperandStack().push(expression.getType());
        }
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
        throw new GroovyBugError("SpreadExpression should not be visited here");
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        Expression subExpression = expression.getExpression();
        this.controller.getAssertionWriter().disableTracker();
        subExpression.visit(this);
        this.controller.getOperandStack().box();
        spreadMap.call(this.controller.getMethodVisitor());
        this.controller.getAssertionWriter().reenableTracker();
        this.controller.getOperandStack().replace(ClassHelper.OBJECT_TYPE);
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        Expression subExpression = expression.getExpression();
        subExpression.visit(this);
        this.controller.getOperandStack().box();
        this.controller.getOperandStack().pushDynamicName(expression.getMethodName());
        getMethodPointer.call(this.controller.getMethodVisitor());
        this.controller.getOperandStack().replace(ClassHelper.CLOSURE_TYPE, 2);
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        this.controller.getUnaryExpressionHelper().writeUnaryMinus(expression);
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        this.controller.getUnaryExpressionHelper().writeUnaryPlus(expression);
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        this.controller.getUnaryExpressionHelper().writeBitwiseNegate(expression);
    }

    @Override
    public void visitCastExpression(CastExpression castExpression) {
        ClassNode type = castExpression.getType();
        Expression subExpression = castExpression.getExpression();
        subExpression.visit(this);
        if (ClassHelper.OBJECT_TYPE.equals(type)) {
            return;
        }
        if (castExpression.isCoerce()) {
            this.controller.getOperandStack().doAsType(type);
        } else if (AsmClassGenerator.isNullConstant(subExpression) && !ClassHelper.isPrimitiveType(type)) {
            this.controller.getOperandStack().replace(type);
        } else {
            ClassNode subExprType = this.controller.getTypeChooser().resolveType(subExpression, this.controller.getClassNode());
            if (castExpression.isStrict() || !ClassHelper.isPrimitiveType(type) && WideningCategories.implementsInterfaceOrSubclassOf(subExprType, type)) {
                BytecodeHelper.doCast(this.controller.getMethodVisitor(), type);
                this.controller.getOperandStack().replace(type);
            } else {
                this.controller.getOperandStack().doGroovyCast(type);
            }
        }
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
        this.controller.getUnaryExpressionHelper().writeNotExpression(expression);
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        this.controller.getCompileStack().pushBooleanExpression();
        int mark = this.controller.getOperandStack().getStackLength();
        Expression inner = expression.getExpression();
        inner.visit(this);
        this.controller.getOperandStack().castToBool(mark, true);
        this.controller.getCompileStack().pop();
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        this.onLineNumber(call, "visitMethodCallExpression: \"" + call.getMethod() + "\":");
        this.controller.getInvocationWriter().writeInvokeMethod(call);
        this.controller.getAssertionWriter().record(call.getMethod());
    }

    protected boolean emptyArguments(Expression arguments) {
        return AsmClassGenerator.argumentSize(arguments) == 0;
    }

    public static boolean containsSpreadExpression(Expression arguments) {
        List<Expression> args = null;
        if (arguments instanceof TupleExpression) {
            TupleExpression tupleExpression = (TupleExpression)arguments;
            args = tupleExpression.getExpressions();
        } else if (arguments instanceof ListExpression) {
            ListExpression le = (ListExpression)arguments;
            args = le.getExpressions();
        } else {
            return arguments instanceof SpreadExpression;
        }
        Iterator<Expression> iter = args.iterator();
        while (iter.hasNext()) {
            if (!(iter.next() instanceof SpreadExpression)) continue;
            return true;
        }
        return false;
    }

    public static int argumentSize(Expression arguments) {
        if (arguments instanceof TupleExpression) {
            TupleExpression tupleExpression = (TupleExpression)arguments;
            int size = tupleExpression.getExpressions().size();
            return size;
        }
        return 1;
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        this.onLineNumber(call, "visitStaticMethodCallExpression: \"" + call.getMethod() + "\":");
        this.controller.getInvocationWriter().writeInvokeStaticMethod(call);
        this.controller.getAssertionWriter().record(call);
    }

    public static boolean isNullConstant(Expression expr) {
        return expr instanceof ConstantExpression && ((ConstantExpression)expr).getValue() == null;
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        this.onLineNumber(call, "visitConstructorCallExpression: \"" + call.getType().getName() + "\":");
        if (call.isSpecialCall()) {
            this.controller.getInvocationWriter().writeSpecialConstructorCall(call);
            return;
        }
        this.controller.getInvocationWriter().writeInvokeConstructor(call);
        this.controller.getAssertionWriter().record(call);
    }

    private static String makeFieldClassName(ClassNode type) {
        String internalName = BytecodeHelper.getClassInternalName(type);
        StringBuilder ret = new StringBuilder(internalName.length());
        for (int i = 0; i < internalName.length(); ++i) {
            char c = internalName.charAt(i);
            if (c == '/') {
                ret.append('$');
                continue;
            }
            if (c == ';') continue;
            ret.append(c);
        }
        return ret.toString();
    }

    private static String getStaticFieldName(ClassNode type) {
        ClassNode componentType = type;
        StringBuilder prefix = new StringBuilder();
        while (componentType.isArray()) {
            prefix.append("$");
            componentType = componentType.getComponentType();
        }
        if (prefix.length() != 0) {
            prefix.insert(0, "array");
        }
        String name = prefix + "$class$" + AsmClassGenerator.makeFieldClassName(componentType);
        return name;
    }

    public static boolean samePackages(String pkg1, String pkg2) {
        return pkg1 == null && pkg2 == null || pkg1 != null && pkg1.equals(pkg2);
    }

    private static boolean isValidFieldNodeForByteCodeAccess(FieldNode fn, ClassNode accessingNode) {
        if (fn == null) {
            return false;
        }
        ClassNode declaringClass = fn.getDeclaringClass();
        if (Modifier.isPublic(fn.getModifiers()) || declaringClass.equals(accessingNode)) {
            return true;
        }
        boolean samePackages = AsmClassGenerator.samePackages(declaringClass.getPackageName(), accessingNode.getPackageName());
        if (Modifier.isProtected(fn.getModifiers()) && (samePackages || accessingNode.isDerivedFrom(declaringClass))) {
            return true;
        }
        if (!Modifier.isPrivate(fn.getModifiers())) {
            return samePackages;
        }
        return false;
    }

    public static FieldNode getDeclaredFieldOfCurrentClassOrAccessibleFieldOfSuper(ClassNode accessingNode, ClassNode current, String name, boolean skipCurrent) {
        FieldNode currentClassField;
        if (!skipCurrent && AsmClassGenerator.isValidFieldNodeForByteCodeAccess(currentClassField = current.getDeclaredField(name), accessingNode)) {
            return currentClassField;
        }
        for (ClassNode node = current.getSuperClass(); node != null; node = node.getSuperClass()) {
            FieldNode fn = node.getDeclaredField(name);
            if (!AsmClassGenerator.isValidFieldNodeForByteCodeAccess(fn, accessingNode)) continue;
            return fn;
        }
        return null;
    }

    private void visitAttributeOrProperty(PropertyExpression expression, MethodCallerMultiAdapter adapter) {
        String name;
        MethodVisitor mv = this.controller.getMethodVisitor();
        Expression objectExpression = expression.getObjectExpression();
        ClassNode classNode = this.controller.getClassNode();
        if (AsmClassGenerator.isThisOrSuper(objectExpression) && (name = expression.getPropertyAsString()) != null) {
            FieldNode field = null;
            boolean privateSuperField = false;
            if (AsmClassGenerator.isSuperExpression(objectExpression)) {
                field = classNode.getSuperClass().getDeclaredField(name);
                if (field != null && (field.getModifiers() & 2) != 0) {
                    privateSuperField = true;
                }
            } else if (this.controller.isNotExplicitThisInClosure(expression.isImplicitThis())) {
                field = classNode.getDeclaredField(name);
                if (field == null && classNode instanceof InnerClassNode) {
                    for (ClassNode outer = classNode.getOuterClass(); outer != null; outer = outer.getSuperClass()) {
                        FieldNode outerClassField = outer.getDeclaredField(name);
                        if (outerClassField == null || !outerClassField.isStatic() || !outerClassField.isFinal()) continue;
                        if (outer != classNode.getOuterClass() && Modifier.isPrivate(outerClassField.getModifiers())) {
                            throw new GroovyBugError("Trying to access private constant field [" + outerClassField.getDeclaringClass() + "#" + outerClassField.getName() + "] from inner class");
                        }
                        PropertyExpression pexp = new PropertyExpression((Expression)new ClassExpression(outer), expression.getProperty());
                        pexp.visit(this.controller.getAcg());
                        return;
                    }
                }
                if (field == null && expression instanceof AttributeExpression && AsmClassGenerator.isThisExpression(objectExpression) && this.controller.isStaticContext()) {
                    for (ClassNode current = classNode.getSuperClass(); field == null && current != null; current = current.getSuperClass()) {
                        field = current.getDeclaredField(name);
                    }
                    if (field != null && (field.isProtected() || field.isPublic())) {
                        this.visitFieldExpression(new FieldExpression(field));
                        return;
                    }
                }
            }
            if (field != null && !privateSuperField) {
                this.visitFieldExpression(new FieldExpression(field));
                return;
            }
            if (AsmClassGenerator.isSuperExpression(objectExpression)) {
                if (this.controller.getCompileStack().isLHS()) {
                    this.setPropertyOfSuperClass(classNode, expression, mv);
                    return;
                }
                String prefix = "get";
                String propName = prefix + MetaClassHelper.capitalize(name);
                this.visitMethodCallExpression(new MethodCallExpression(objectExpression, propName, MethodCallExpression.NO_ARGUMENTS));
                return;
            }
        }
        String propName = expression.getPropertyAsString();
        if (expression.getObjectExpression() instanceof ClassExpression && propName != null && propName.equals("this")) {
            ClassNode type = objectExpression.getType();
            ClassNode iterType = classNode;
            if (this.controller.getCompileStack().isInSpecialConstructorCall() && classNode instanceof InnerClassNode) {
                boolean staticInnerClass = classNode.isStaticClass();
                if (classNode.getOuterClass().equals(type)) {
                    ConstructorNode ctor = this.controller.getConstructorNode();
                    Expression receiver = !staticInnerClass ? new VariableExpression(ctor.getParameters()[0]) : new ClassExpression(type);
                    receiver.setSourcePosition(expression);
                    receiver.visit(this);
                    return;
                }
            }
            mv.visitVarInsn(25, 0);
            while (!iterType.equals(type)) {
                String ownerName = BytecodeHelper.getClassInternalName(iterType);
                if (iterType.getOuterClass() == null) break;
                FieldNode thisField = iterType.getField("this$0");
                iterType = iterType.getOuterClass();
                if (thisField == null) {
                    mv.visitMethodInsn(182, BytecodeHelper.getClassInternalName(ClassHelper.CLOSURE_TYPE), "getThisObject", "()Ljava/lang/Object;", false);
                    mv.visitTypeInsn(192, BytecodeHelper.getClassInternalName(iterType));
                    continue;
                }
                ClassNode thisFieldType = thisField.getType();
                if (ClassHelper.CLOSURE_TYPE.equals(thisFieldType)) {
                    mv.visitFieldInsn(180, ownerName, "this$0", BytecodeHelper.getTypeDescription(ClassHelper.CLOSURE_TYPE));
                    mv.visitMethodInsn(182, BytecodeHelper.getClassInternalName(ClassHelper.CLOSURE_TYPE), "getThisObject", "()Ljava/lang/Object;", false);
                    mv.visitTypeInsn(192, BytecodeHelper.getClassInternalName(iterType));
                    continue;
                }
                String typeName = BytecodeHelper.getTypeDescription(iterType);
                mv.visitFieldInsn(180, ownerName, "this$0", typeName);
            }
            this.controller.getOperandStack().push(type);
            return;
        }
        if (propName != null) {
            if (adapter == getProperty && !expression.isSpreadSafe()) {
                this.controller.getCallSiteWriter().makeGetPropertySite(objectExpression, propName, expression.isSafe(), expression.isImplicitThis());
            } else if (adapter == getGroovyObjectProperty && !expression.isSpreadSafe()) {
                this.controller.getCallSiteWriter().makeGroovyObjectGetPropertySite(objectExpression, propName, expression.isSafe(), expression.isImplicitThis());
            } else {
                this.controller.getCallSiteWriter().fallbackAttributeOrPropertySite(expression, objectExpression, propName, adapter);
            }
        } else {
            this.controller.getCallSiteWriter().fallbackAttributeOrPropertySite(expression, objectExpression, null, adapter);
        }
    }

    private void setPropertyOfSuperClass(ClassNode classNode, PropertyExpression expression, MethodVisitor mv) {
        String fieldName = expression.getPropertyAsString();
        FieldNode fieldNode = classNode.getSuperClass().getField(fieldName);
        if (null == fieldNode) {
            throw new RuntimeParserException("Failed to find field[" + fieldName + "] of " + classNode.getName() + "'s super class", expression);
        }
        if (fieldNode.isFinal()) {
            throw new RuntimeParserException("Cannot modify final field[" + fieldName + "] of " + classNode.getName() + "'s super class", expression);
        }
        MethodNode setter = this.findSetterOfSuperClass(classNode, fieldNode);
        MethodNode getter = this.findGetterOfSuperClass(classNode, fieldNode);
        if (Modifier.isPrivate(fieldNode.getModifiers()) && !this.getterAndSetterExists(setter, getter)) {
            throw new RuntimeParserException("Cannot access private field[" + fieldName + "] of " + classNode.getName() + "'s super class", expression);
        }
        OperandStack operandStack = this.controller.getOperandStack();
        operandStack.doAsType(fieldNode.getType());
        mv.visitVarInsn(25, 0);
        operandStack.push(classNode);
        operandStack.swap();
        String owner = BytecodeHelper.getClassInternalName(classNode.getSuperClass().getName());
        String desc = BytecodeHelper.getTypeDescription(fieldNode.getType());
        if (fieldNode.isPublic() || fieldNode.isProtected()) {
            mv.visitFieldInsn(181, owner, fieldName, desc);
        } else {
            mv.visitMethodInsn(183, owner, setter.getName(), BytecodeHelper.getMethodDescriptor(setter), false);
        }
    }

    private boolean getterAndSetterExists(MethodNode setter, MethodNode getter) {
        return null != setter && null != getter && setter.getDeclaringClass().equals(getter.getDeclaringClass());
    }

    private MethodNode findSetterOfSuperClass(ClassNode classNode, FieldNode fieldNode) {
        String setterMethodName = "set" + MetaClassHelper.capitalize(fieldNode.getName());
        return classNode.getSuperClass().getSetterMethod(setterMethodName);
    }

    private MethodNode findGetterOfSuperClass(ClassNode classNode, FieldNode fieldNode) {
        String getterMethodName = "get" + MetaClassHelper.capitalize(fieldNode.getName());
        return classNode.getSuperClass().getGetterMethod(getterMethodName);
    }

    private boolean isThisOrSuperInStaticContext(Expression objectExpression) {
        if (this.controller.isInClosure()) {
            return false;
        }
        return this.controller.isStaticContext() && AsmClassGenerator.isThisOrSuper(objectExpression);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        MethodCallerMultiAdapter adapter;
        Expression objectExpression = expression.getObjectExpression();
        OperandStack operandStack = this.controller.getOperandStack();
        int mark = operandStack.getStackLength() - 1;
        if (this.controller.getCompileStack().isLHS()) {
            adapter = setProperty;
            if (AsmClassGenerator.isGroovyObject(objectExpression)) {
                adapter = setGroovyObjectProperty;
            }
            if (this.isThisOrSuperInStaticContext(objectExpression)) {
                adapter = setProperty;
            }
        } else {
            adapter = getProperty;
            if (AsmClassGenerator.isGroovyObject(objectExpression)) {
                adapter = getGroovyObjectProperty;
            }
            if (this.isThisOrSuperInStaticContext(objectExpression)) {
                adapter = getProperty;
            }
        }
        this.visitAttributeOrProperty(expression, adapter);
        if (this.controller.getCompileStack().isLHS()) {
            operandStack.remove(operandStack.getStackLength() - mark);
        } else {
            this.controller.getAssertionWriter().record(expression.getProperty());
        }
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
        MethodCallerMultiAdapter adapter;
        FieldNode field;
        String name;
        Expression objectExpression = expression.getObjectExpression();
        ClassNode classNode = this.controller.getClassNode();
        if (AsmClassGenerator.isThisOrSuper(objectExpression) && (name = expression.getPropertyAsString()) != null && (field = AsmClassGenerator.getDeclaredFieldOfCurrentClassOrAccessibleFieldOfSuper(classNode, classNode, name, AsmClassGenerator.isSuperExpression(objectExpression))) != null) {
            FieldExpression exp = new FieldExpression(field);
            exp.setSourcePosition(expression);
            this.visitFieldExpression(exp);
            return;
        }
        OperandStack operandStack = this.controller.getOperandStack();
        int mark = operandStack.getStackLength() - 1;
        if (this.controller.getCompileStack().isLHS()) {
            adapter = setField;
            if (AsmClassGenerator.isGroovyObject(objectExpression)) {
                adapter = setGroovyObjectField;
            }
            if (AsmClassGenerator.usesSuper(expression)) {
                adapter = setFieldOnSuper;
            }
        } else {
            adapter = getField;
            if (AsmClassGenerator.isGroovyObject(objectExpression)) {
                adapter = getGroovyObjectField;
            }
            if (AsmClassGenerator.usesSuper(expression)) {
                adapter = getFieldOnSuper;
            }
        }
        this.visitAttributeOrProperty(expression, adapter);
        if (!this.controller.getCompileStack().isLHS()) {
            this.controller.getAssertionWriter().record(expression.getProperty());
        } else {
            operandStack.remove(operandStack.getStackLength() - mark);
        }
    }

    private static boolean usesSuper(PropertyExpression pe) {
        Expression expression = pe.getObjectExpression();
        if (expression instanceof VariableExpression) {
            VariableExpression varExp = (VariableExpression)expression;
            String variable = varExp.getName();
            return variable.equals("super");
        }
        return false;
    }

    private static boolean isGroovyObject(Expression objectExpression) {
        return AsmClassGenerator.isThisExpression(objectExpression) || objectExpression.getType().isDerivedFromGroovyObject() && !(objectExpression instanceof ClassExpression);
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
        FieldNode field = expression.getField();
        if (field.isStatic()) {
            if (this.controller.getCompileStack().isLHS()) {
                this.storeStaticField(expression);
            } else {
                this.loadStaticField(expression);
            }
        } else if (this.controller.getCompileStack().isLHS()) {
            this.storeThisInstanceField(expression);
        } else {
            this.loadInstanceField(expression);
        }
        if (this.controller.getCompileStack().isLHS()) {
            this.controller.getAssertionWriter().record(expression);
        }
    }

    public void loadStaticField(FieldExpression fldExp) {
        String ownerName;
        MethodVisitor mv = this.controller.getMethodVisitor();
        FieldNode field = fldExp.getField();
        boolean holder = field.isHolder() && !this.controller.isInClosureConstructor();
        ClassNode type = field.getType();
        String string = ownerName = field.getOwner().equals(this.controller.getClassNode()) ? this.controller.getInternalClassName() : BytecodeHelper.getClassInternalName(field.getOwner());
        if (holder) {
            mv.visitFieldInsn(178, ownerName, fldExp.getFieldName(), BytecodeHelper.getTypeDescription(type));
            mv.visitMethodInsn(182, "groovy/lang/Reference", "get", "()Ljava/lang/Object;", false);
            this.controller.getOperandStack().push(ClassHelper.OBJECT_TYPE);
        } else {
            mv.visitFieldInsn(178, ownerName, fldExp.getFieldName(), BytecodeHelper.getTypeDescription(type));
            this.controller.getOperandStack().push(field.getType());
        }
    }

    public void loadInstanceField(FieldExpression fldExp) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        FieldNode field = fldExp.getField();
        boolean holder = field.isHolder() && !this.controller.isInClosureConstructor();
        ClassNode type = field.getType();
        String ownerName = field.getOwner().equals(this.controller.getClassNode()) ? this.controller.getInternalClassName() : BytecodeHelper.getClassInternalName(field.getOwner());
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, ownerName, fldExp.getFieldName(), BytecodeHelper.getTypeDescription(type));
        if (holder) {
            mv.visitMethodInsn(182, "groovy/lang/Reference", "get", "()Ljava/lang/Object;", false);
            this.controller.getOperandStack().push(ClassHelper.OBJECT_TYPE);
        } else {
            this.controller.getOperandStack().push(field.getType());
        }
    }

    private void storeThisInstanceField(FieldExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        FieldNode field = expression.getField();
        boolean setReferenceFromReference = field.isHolder() && expression.isUseReferenceDirectly();
        String ownerName = field.getOwner().equals(this.controller.getClassNode()) ? this.controller.getInternalClassName() : BytecodeHelper.getClassInternalName(field.getOwner());
        OperandStack operandStack = this.controller.getOperandStack();
        if (setReferenceFromReference) {
            mv.visitVarInsn(25, 0);
            operandStack.push(this.controller.getClassNode());
            operandStack.swap();
            mv.visitFieldInsn(181, ownerName, field.getName(), BytecodeHelper.getTypeDescription(field.getType()));
        } else if (field.isHolder()) {
            operandStack.doGroovyCast(field.getOriginType());
            operandStack.box();
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, ownerName, expression.getFieldName(), BytecodeHelper.getTypeDescription(field.getType()));
            mv.visitInsn(95);
            mv.visitMethodInsn(182, "groovy/lang/Reference", "set", "(Ljava/lang/Object;)V", false);
        } else {
            operandStack.doGroovyCast(field.getOriginType());
            mv.visitVarInsn(25, 0);
            operandStack.push(this.controller.getClassNode());
            operandStack.swap();
            mv.visitFieldInsn(181, ownerName, field.getName(), BytecodeHelper.getTypeDescription(field.getType()));
        }
    }

    private void storeStaticField(FieldExpression expression) {
        String ownerName;
        MethodVisitor mv = this.controller.getMethodVisitor();
        FieldNode field = expression.getField();
        boolean holder = field.isHolder() && !this.controller.isInClosureConstructor();
        this.controller.getOperandStack().doGroovyCast(field);
        String string = ownerName = field.getOwner().equals(this.controller.getClassNode()) ? this.controller.getInternalClassName() : BytecodeHelper.getClassInternalName(field.getOwner());
        if (holder) {
            this.controller.getOperandStack().box();
            mv.visitFieldInsn(178, ownerName, expression.getFieldName(), BytecodeHelper.getTypeDescription(field.getType()));
            mv.visitInsn(95);
            mv.visitMethodInsn(182, "groovy/lang/Reference", "set", "(Ljava/lang/Object;)V", false);
        } else {
            mv.visitFieldInsn(179, ownerName, expression.getFieldName(), BytecodeHelper.getTypeDescription(field.getType()));
        }
        this.controller.getOperandStack().remove(1);
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        String variableName = expression.getName();
        ClassNode classNode = this.controller.getClassNode();
        if (variableName.equals("this")) {
            if (this.controller.isStaticMethod() || !this.controller.getCompileStack().isImplicitThis() && this.controller.isStaticContext()) {
                if (this.controller.isInClosure()) {
                    classNode = this.controller.getOutermostClass();
                }
                this.visitClassExpression(new ClassExpression(classNode));
            } else {
                this.loadThis(expression);
            }
            return;
        }
        if (variableName.equals("super")) {
            if (this.controller.isStaticMethod()) {
                this.visitClassExpression(new ClassExpression(classNode.getSuperClass()));
            } else {
                this.loadThis(expression);
            }
            return;
        }
        BytecodeVariable variable = this.controller.getCompileStack().getVariable(variableName, false);
        if (variable == null) {
            this.processClassVariable(expression);
        } else {
            this.controller.getOperandStack().loadOrStoreVariable(variable, expression.isUseReferenceDirectly());
        }
        if (!this.controller.getCompileStack().isLHS()) {
            this.controller.getAssertionWriter().record(expression);
        }
    }

    private void loadThis(VariableExpression thisExpression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitVarInsn(25, 0);
        if (this.controller.isInClosure() && !this.controller.getCompileStack().isImplicitThis()) {
            ClassNode expectedType;
            mv.visitMethodInsn(182, "groovy/lang/Closure", "getThisObject", "()Ljava/lang/Object;", false);
            ClassNode classNode = expectedType = thisExpression != null ? this.controller.getTypeChooser().resolveType(thisExpression, this.controller.getOutermostClass()) : null;
            if (!ClassHelper.OBJECT_TYPE.equals(expectedType) && !ClassHelper.isPrimitiveType(expectedType)) {
                BytecodeHelper.doCast(mv, expectedType);
                this.controller.getOperandStack().push(expectedType);
            } else {
                this.controller.getOperandStack().push(ClassHelper.OBJECT_TYPE);
            }
        } else {
            this.controller.getOperandStack().push(this.controller.getClassNode());
        }
    }

    private void processClassVariable(VariableExpression expression) {
        if (this.passingParams && this.controller.isInScriptBody()) {
            MethodVisitor mv = this.controller.getMethodVisitor();
            mv.visitTypeInsn(187, "org/codehaus/groovy/runtime/ScriptReference");
            mv.visitInsn(89);
            this.loadThisOrOwner();
            mv.visitLdcInsn(expression.getName());
            mv.visitMethodInsn(183, "org/codehaus/groovy/runtime/ScriptReference", "<init>", "(Lgroovy/lang/Script;Ljava/lang/String;)V", false);
        } else {
            PropertyExpression pexp = new PropertyExpression((Expression)new VariableExpression("this"), expression.getName());
            pexp.getObjectExpression().setSourcePosition(expression);
            pexp.getProperty().setSourcePosition(expression);
            pexp.setImplicitThis(true);
            this.visitPropertyExpression(pexp);
        }
    }

    protected void createInterfaceSyntheticStaticFields() {
        InterfaceHelperClassNode icl = this.controller.getInterfaceClassLoadingClass();
        if (this.referencedClasses.isEmpty()) {
            Iterator<InnerClassNode> it = this.controller.getClassNode().getInnerClasses();
            while (it.hasNext()) {
                InnerClassNode inner = it.next();
                if (inner != icl) continue;
                it.remove();
                return;
            }
            return;
        }
        this.addInnerClass(icl);
        for (Map.Entry<String, ClassNode> entry : this.referencedClasses.entrySet()) {
            String staticFieldName = entry.getKey();
            ClassNode cn = entry.getValue();
            icl.addField(staticFieldName, 4104, ClassHelper.CLASS_Type.getPlainNodeReference(), new ClassExpression(cn));
        }
    }

    protected void createSyntheticStaticFields() {
        MethodVisitor mv;
        if (this.referencedClasses.isEmpty()) {
            return;
        }
        for (Map.Entry<String, ClassNode> entry : this.referencedClasses.entrySet()) {
            String staticFieldName = entry.getKey();
            ClassNode cn = entry.getValue();
            FieldNode fn = this.controller.getClassNode().getDeclaredField(staticFieldName);
            if (fn != null) {
                boolean modifiers;
                boolean type = fn.getType().redirect() == ClassHelper.CLASS_Type;
                boolean bl = modifiers = fn.getModifiers() == 4104;
                if (!type || !modifiers) {
                    String text = "";
                    if (!type) {
                        text = " with wrong type: " + fn.getType() + " (java.lang.Class needed)";
                    }
                    if (!modifiers) {
                        text = " with wrong modifiers: " + fn.getModifiers() + " (" + 4104 + " needed)";
                    }
                    this.throwException("tried to set a static synthetic field " + staticFieldName + " in " + this.controller.getClassNode().getName() + " for class resolving, but found already a node of that name " + text);
                }
            } else {
                this.cv.visitField(4106, staticFieldName, "Ljava/lang/Class;", null, null);
            }
            mv = this.cv.visitMethod(4106, "$get$" + staticFieldName, "()Ljava/lang/Class;", null, null);
            mv.visitCode();
            mv.visitFieldInsn(178, this.controller.getInternalClassName(), staticFieldName, "Ljava/lang/Class;");
            mv.visitInsn(89);
            Label l0 = new Label();
            mv.visitJumpInsn(199, l0);
            mv.visitInsn(87);
            mv.visitLdcInsn(BytecodeHelper.getClassLoadingTypeDescription(cn));
            mv.visitMethodInsn(184, this.controller.getInternalClassName(), "class$", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitInsn(89);
            mv.visitFieldInsn(179, this.controller.getInternalClassName(), staticFieldName, "Ljava/lang/Class;");
            mv.visitLabel(l0);
            mv.visitInsn(176);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        mv = this.cv.visitMethod(4104, "class$", "(Ljava/lang/String;)Ljava/lang/Class;", null, null);
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(184, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(176);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(58, 1);
        mv.visitTypeInsn(187, "java/lang/NoClassDefFoundError");
        mv.visitInsn(89);
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(182, "java/lang/ClassNotFoundException", "getMessage", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(183, "java/lang/NoClassDefFoundError", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(191);
        mv.visitTryCatchBlock(l0, l2, l2, "java/lang/ClassNotFoundException");
        mv.visitMaxs(3, 2);
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
        ClassNode type = expression.getType();
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (BytecodeHelper.isClassLiteralPossible(type) || BytecodeHelper.isSameCompilationUnit(this.controller.getClassNode(), type)) {
            if (this.controller.getClassNode().isInterface()) {
                InterfaceHelperClassNode interfaceClassLoadingClass = this.controller.getInterfaceClassLoadingClass();
                if (BytecodeHelper.isClassLiteralPossible(interfaceClassLoadingClass)) {
                    BytecodeHelper.visitClassLiteral(mv, interfaceClassLoadingClass);
                    this.controller.getOperandStack().push(ClassHelper.CLASS_Type);
                    return;
                }
            } else {
                BytecodeHelper.visitClassLiteral(mv, type);
                this.controller.getOperandStack().push(ClassHelper.CLASS_Type);
                return;
            }
        }
        String staticFieldName = AsmClassGenerator.getStaticFieldName(type);
        this.referencedClasses.put(staticFieldName, type);
        String internalClassName = this.controller.getInternalClassName();
        if (this.controller.getClassNode().isInterface()) {
            internalClassName = BytecodeHelper.getClassInternalName(this.controller.getInterfaceClassLoadingClass());
            mv.visitFieldInsn(178, internalClassName, staticFieldName, "Ljava/lang/Class;");
        } else {
            mv.visitMethodInsn(184, internalClassName, "$get$" + staticFieldName, "()Ljava/lang/Class;", false);
        }
        this.controller.getOperandStack().push(ClassHelper.CLASS_Type);
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        OperandStack operandStack = this.controller.getOperandStack();
        expression.getFrom().visit(this);
        operandStack.box();
        expression.getTo().visit(this);
        operandStack.box();
        operandStack.pushBool(expression.isInclusive());
        createRangeMethod.call(this.controller.getMethodVisitor());
        operandStack.replace(ClassHelper.RANGE_TYPE, 3);
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        throw new GroovyBugError("MapEntryExpression should not be visited here");
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        List<MapEntryExpression> entries = expression.getMapEntryExpressions();
        int size = entries.size();
        BytecodeHelper.pushConstant(mv, size * 2);
        mv.visitTypeInsn(189, "java/lang/Object");
        int i = 0;
        Iterator<MapEntryExpression> iter = entries.iterator();
        while (iter.hasNext()) {
            MapEntryExpression object;
            MapEntryExpression entry = object = iter.next();
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i++);
            entry.getKeyExpression().visit(this);
            this.controller.getOperandStack().box();
            mv.visitInsn(83);
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i++);
            entry.getValueExpression().visit(this);
            this.controller.getOperandStack().box();
            mv.visitInsn(83);
            this.controller.getOperandStack().remove(2);
        }
        createMapMethod.call(mv);
        this.controller.getOperandStack().push(ClassHelper.MAP_TYPE);
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression ale) {
        if (AsmClassGenerator.containsSpreadExpression(ale)) {
            this.despreadList(ale.getExpressions(), true);
        } else {
            this.visitTupleExpression(ale, true);
        }
    }

    public void despreadList(List expressions, boolean wrap) {
        ArrayList<Expression> spreadIndexes = new ArrayList<Expression>();
        ArrayList<Expression> spreadExpressions = new ArrayList<Expression>();
        ArrayList<Expression> normalArguments = new ArrayList<Expression>();
        for (int i = 0; i < expressions.size(); ++i) {
            Object expr = expressions.get(i);
            if (!(expr instanceof SpreadExpression)) {
                normalArguments.add((Expression)expr);
                continue;
            }
            spreadIndexes.add(new ConstantExpression(i - spreadExpressions.size(), true));
            spreadExpressions.add(((SpreadExpression)expr).getExpression());
        }
        this.visitTupleExpression(new ArgumentListExpression(normalArguments), wrap);
        new TupleExpression(spreadExpressions).visit(this);
        new ArrayExpression(ClassHelper.int_TYPE, spreadIndexes, null).visit(this);
        this.controller.getOperandStack().remove(1);
        despreadList.call(this.controller.getMethodVisitor());
    }

    @Override
    public void visitTupleExpression(TupleExpression expression) {
        this.visitTupleExpression(expression, false);
    }

    void visitTupleExpression(TupleExpression expression, boolean useWrapper) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        int size = expression.getExpressions().size();
        BytecodeHelper.pushConstant(mv, size);
        mv.visitTypeInsn(189, "java/lang/Object");
        for (int i = 0; i < size; ++i) {
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i);
            Expression argument = expression.getExpression(i);
            argument.visit(this);
            this.controller.getOperandStack().box();
            if (useWrapper && argument instanceof CastExpression) {
                this.loadWrapper(argument);
            }
            mv.visitInsn(83);
            this.controller.getOperandStack().remove(1);
        }
    }

    public void loadWrapper(Expression argument) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        ClassNode goalClass = argument.getType();
        this.visitClassExpression(new ClassExpression(goalClass));
        if (goalClass.isDerivedFromGroovyObject()) {
            createGroovyObjectWrapperMethod.call(mv);
        } else {
            createPojoWrapperMethod.call(mv);
        }
        this.controller.getOperandStack().remove(1);
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        ClassNode elementType = expression.getElementType();
        String arrayTypeName = BytecodeHelper.getClassInternalName(elementType);
        List<Expression> sizeExpression = expression.getSizeExpression();
        int size = 0;
        int dimensions = 0;
        if (sizeExpression != null) {
            for (Expression element : sizeExpression) {
                if (element == ConstantExpression.EMPTY_EXPRESSION) break;
                ++dimensions;
                element.visit(this);
                this.controller.getOperandStack().doGroovyCast(ClassHelper.int_TYPE);
            }
            this.controller.getOperandStack().remove(dimensions);
        } else {
            size = expression.getExpressions().size();
            BytecodeHelper.pushConstant(mv, size);
        }
        int storeIns = 83;
        if (sizeExpression != null) {
            arrayTypeName = BytecodeHelper.getTypeDescription(expression.getType());
            mv.visitMultiANewArrayInsn(arrayTypeName, dimensions);
        } else if (ClassHelper.isPrimitiveType(elementType)) {
            int primType = 0;
            if (elementType == ClassHelper.boolean_TYPE) {
                primType = 4;
                storeIns = 84;
            } else if (elementType == ClassHelper.char_TYPE) {
                primType = 5;
                storeIns = 85;
            } else if (elementType == ClassHelper.float_TYPE) {
                primType = 6;
                storeIns = 81;
            } else if (elementType == ClassHelper.double_TYPE) {
                primType = 7;
                storeIns = 82;
            } else if (elementType == ClassHelper.byte_TYPE) {
                primType = 8;
                storeIns = 84;
            } else if (elementType == ClassHelper.short_TYPE) {
                primType = 9;
                storeIns = 86;
            } else if (elementType == ClassHelper.int_TYPE) {
                primType = 10;
                storeIns = 79;
            } else if (elementType == ClassHelper.long_TYPE) {
                primType = 11;
                storeIns = 80;
            }
            mv.visitIntInsn(188, primType);
        } else {
            mv.visitTypeInsn(189, arrayTypeName);
        }
        for (int i = 0; i < size; ++i) {
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i);
            Expression elementExpression = expression.getExpression(i);
            if (elementExpression == null) {
                ConstantExpression.NULL.visit(this);
            } else {
                elementExpression.visit(this);
                this.controller.getOperandStack().doGroovyCast(elementType);
            }
            mv.visitInsn(storeIns);
            this.controller.getOperandStack().remove(1);
        }
        this.controller.getOperandStack().push(expression.getType());
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        this.controller.getCompileStack().pushVariableScope(expression.getVariableScope());
        List<Expression> expressions = expression.getExpressions();
        final int size = expressions.size();
        for (int i = 0; i < size; ++i) {
            Expression expr = expressions.get(i);
            if (!(expr instanceof DeclarationExpression)) continue;
            DeclarationExpression de = (DeclarationExpression)expr;
            BinaryExpression be = new BinaryExpression(de.getLeftExpression(), de.getOperation(), de.getRightExpression());
            expressions.set(i, be);
            de.setRightExpression(ConstantExpression.NULL);
            this.visitDeclarationExpression(de);
        }
        LinkedList<Object> instructions = new LinkedList<Object>();
        BytecodeSequence seq = new BytecodeSequence(instructions);
        BlockStatement bs = new BlockStatement();
        bs.addStatement(seq);
        Parameter closureIndex = new Parameter(ClassHelper.int_TYPE, "__closureIndex");
        ClosureExpression ce = new ClosureExpression(new Parameter[]{closureIndex}, bs);
        ce.setVariableScope(expression.getVariableScope());
        instructions.add(ConstantExpression.NULL);
        final Label dflt = new Label();
        final Label tableEnd = new Label();
        final Label[] labels = new Label[size];
        instructions.add(new BytecodeInstruction(){

            @Override
            public void visit(MethodVisitor mv) {
                mv.visitVarInsn(21, 1);
                mv.visitTableSwitchInsn(0, size - 1, dflt, labels);
            }
        });
        for (int i = 0; i < size; ++i) {
            final Label label = new Label();
            Expression expr = expressions.get(i);
            final boolean isStatement = expr instanceof Statement;
            labels[i] = label;
            instructions.add(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    mv.visitLabel(label);
                    if (!isStatement) {
                        mv.visitInsn(87);
                    }
                }
            });
            instructions.add(expr);
            instructions.add(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    mv.visitJumpInsn(167, tableEnd);
                }
            });
        }
        instructions.add(new BytecodeInstruction(){

            @Override
            public void visit(MethodVisitor mv) {
                mv.visitLabel(dflt);
            }
        });
        ConstantExpression text = new ConstantExpression("invalid index for closure");
        ConstructorCallExpression cce = new ConstructorCallExpression(ClassHelper.make(IllegalArgumentException.class), text);
        ThrowStatement ts = new ThrowStatement(cce);
        instructions.add(ts);
        instructions.add(new BytecodeInstruction(){

            @Override
            public void visit(MethodVisitor mv) {
                mv.visitLabel(tableEnd);
                mv.visitInsn(176);
            }
        });
        this.visitClosureExpression(ce);
        BytecodeHelper.pushConstant(mv, size);
        mv.visitTypeInsn(189, "java/lang/Object");
        int listArrayVar = this.controller.getCompileStack().defineTemporaryVariable("_listOfClosures", true);
        for (int i = 0; i < size; ++i) {
            mv.visitTypeInsn(187, "org/codehaus/groovy/runtime/CurriedClosure");
            mv.visitInsn(92);
            mv.visitInsn(95);
            mv.visitInsn(4);
            mv.visitTypeInsn(189, "java/lang/Object");
            mv.visitInsn(89);
            mv.visitInsn(3);
            mv.visitLdcInsn(i);
            mv.visitMethodInsn(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(83);
            mv.visitMethodInsn(183, "org/codehaus/groovy/runtime/CurriedClosure", "<init>", "(Lgroovy/lang/Closure;[Ljava/lang/Object;)V", false);
            mv.visitVarInsn(25, listArrayVar);
            mv.visitInsn(95);
            BytecodeHelper.pushConstant(mv, i);
            mv.visitInsn(95);
            mv.visitInsn(83);
        }
        mv.visitInsn(87);
        mv.visitVarInsn(25, listArrayVar);
        createListMethod.call(mv);
        this.controller.getCompileStack().removeVar(listArrayVar);
        this.controller.getOperandStack().pop();
    }

    @Override
    public void visitBytecodeSequence(BytecodeSequence bytecodeSequence) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        List instructions = bytecodeSequence.getInstructions();
        int mark = this.controller.getOperandStack().getStackLength();
        for (Object part : instructions) {
            if (part == EmptyExpression.INSTANCE) {
                mv.visitInsn(1);
                continue;
            }
            if (part instanceof Expression) {
                ((Expression)part).visit(this);
                continue;
            }
            if (part instanceof Statement) {
                Statement stm = (Statement)part;
                stm.visit(this);
                mv.visitInsn(1);
                continue;
            }
            BytecodeInstruction runner = (BytecodeInstruction)part;
            runner.visit(mv);
        }
        this.controller.getOperandStack().remove(mark - this.controller.getOperandStack().getStackLength());
    }

    @Override
    public void visitListExpression(ListExpression expression) {
        this.onLineNumber(expression, "ListExpression");
        int size = expression.getExpressions().size();
        boolean containsSpreadExpression = AsmClassGenerator.containsSpreadExpression(expression);
        boolean containsOnlyConstants = !containsSpreadExpression && AsmClassGenerator.containsOnlyConstants(expression);
        OperandStack operandStack = this.controller.getOperandStack();
        if (!containsSpreadExpression) {
            MethodVisitor mv = this.controller.getMethodVisitor();
            BytecodeHelper.pushConstant(mv, size);
            mv.visitTypeInsn(189, "java/lang/Object");
            int maxInit = 1000;
            if (size < maxInit || !containsOnlyConstants) {
                for (int i = 0; i < size; ++i) {
                    mv.visitInsn(89);
                    BytecodeHelper.pushConstant(mv, i);
                    expression.getExpression(i).visit(this);
                    operandStack.box();
                    mv.visitInsn(83);
                }
                this.controller.getOperandStack().remove(size);
            } else {
                List<Expression> expressions = expression.getExpressions();
                ArrayList<String> methods = new ArrayList<String>();
                MethodVisitor oldMv = mv;
                int index = 0;
                while (index < size) {
                    String methodName = "$createListEntry_" + this.controller.getNextHelperMethodIndex();
                    methods.add(methodName);
                    mv = this.controller.getClassVisitor().visitMethod(4106, methodName, "([Ljava/lang/Object;)V", null, null);
                    this.controller.setMethodVisitor(mv);
                    mv.visitCode();
                    int methodBlockSize = Math.min(size - index, maxInit);
                    int methodBlockEnd = index + methodBlockSize;
                    while (index < methodBlockEnd) {
                        mv.visitVarInsn(25, 0);
                        mv.visitLdcInsn(index);
                        expressions.get(index).visit(this);
                        operandStack.box();
                        mv.visitInsn(83);
                        ++index;
                    }
                    operandStack.remove(methodBlockSize);
                    mv.visitInsn(177);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                mv = oldMv;
                this.controller.setMethodVisitor(mv);
                for (String methodName : methods) {
                    mv.visitInsn(89);
                    mv.visitMethodInsn(184, this.controller.getInternalClassName(), methodName, "([Ljava/lang/Object;)V", false);
                }
            }
        } else {
            this.despreadList(expression.getExpressions(), false);
        }
        createListMethod.call(this.controller.getMethodVisitor());
        operandStack.push(ClassHelper.LIST_TYPE);
    }

    private static boolean containsOnlyConstants(ListExpression list) {
        for (Expression exp : list.getExpressions()) {
            if (exp instanceof ConstantExpression) continue;
            return false;
        }
        return true;
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitTypeInsn(187, "org/codehaus/groovy/runtime/GStringImpl");
        mv.visitInsn(89);
        int size = expression.getValues().size();
        BytecodeHelper.pushConstant(mv, size);
        mv.visitTypeInsn(189, "java/lang/Object");
        for (int i = 0; i < size; ++i) {
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i);
            expression.getValue(i).visit(this);
            this.controller.getOperandStack().box();
            mv.visitInsn(83);
        }
        this.controller.getOperandStack().remove(size);
        List<ConstantExpression> strings = expression.getStrings();
        size = strings.size();
        BytecodeHelper.pushConstant(mv, size);
        mv.visitTypeInsn(189, "java/lang/String");
        for (int i = 0; i < size; ++i) {
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i);
            this.controller.getOperandStack().pushConstant(strings.get(i));
            this.controller.getOperandStack().box();
            mv.visitInsn(83);
        }
        this.controller.getOperandStack().remove(size);
        mv.visitMethodInsn(183, "org/codehaus/groovy/runtime/GStringImpl", "<init>", "([Ljava/lang/Object;[Ljava/lang/String;)V", false);
        this.controller.getOperandStack().push(ClassHelper.GSTRING_TYPE);
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
    }

    private void visitAnnotations(AnnotatedNode targetNode, Object visitor) {
        this.visitAnnotations(targetNode, targetNode, visitor);
    }

    private void visitAnnotations(AnnotatedNode targetNode, AnnotatedNode sourceNode, Object visitor) {
        for (AnnotationNode an : sourceNode.getAnnotations()) {
            if (an.isBuiltIn() || an.hasSourceRetention()) continue;
            AnnotationVisitor av = this.getAnnotationVisitor(targetNode, an, visitor);
            this.visitAnnotationAttributes(an, av);
            av.visitEnd();
        }
    }

    private void visitParameterAnnotations(Parameter parameter, int paramNumber, MethodVisitor mv) {
        for (AnnotationNode an : parameter.getAnnotations()) {
            if (an.isBuiltIn() || an.hasSourceRetention()) continue;
            String annotationDescriptor = BytecodeHelper.getTypeDescription(an.getClassNode());
            AnnotationVisitor av = mv.visitParameterAnnotation(paramNumber, annotationDescriptor, an.hasRuntimeRetention());
            this.visitAnnotationAttributes(an, av);
            av.visitEnd();
        }
    }

    private AnnotationVisitor getAnnotationVisitor(AnnotatedNode targetNode, AnnotationNode an, Object visitor) {
        String annotationDescriptor = BytecodeHelper.getTypeDescription(an.getClassNode());
        if (targetNode instanceof MethodNode) {
            return ((MethodVisitor)visitor).visitAnnotation(annotationDescriptor, an.hasRuntimeRetention());
        }
        if (targetNode instanceof FieldNode) {
            return ((FieldVisitor)visitor).visitAnnotation(annotationDescriptor, an.hasRuntimeRetention());
        }
        if (targetNode instanceof ClassNode) {
            return ((ClassVisitor)visitor).visitAnnotation(annotationDescriptor, an.hasRuntimeRetention());
        }
        this.throwException("Cannot create an AnnotationVisitor. Please report Groovy bug");
        return null;
    }

    private void visitAnnotationAttributes(AnnotationNode an, AnnotationVisitor av) {
        HashMap<String, Object> constantAttrs = new HashMap<String, Object>();
        HashMap<String, PropertyExpression> enumAttrs = new HashMap<String, PropertyExpression>();
        HashMap<String, Object> atAttrs = new HashMap<String, Object>();
        HashMap<String, ListExpression> arrayAttrs = new HashMap<String, ListExpression>();
        for (String string : an.getMembers().keySet()) {
            Expression expr = an.getMember(string);
            if (expr instanceof AnnotationConstantExpression) {
                atAttrs.put(string, ((AnnotationConstantExpression)expr).getValue());
                continue;
            }
            if (expr instanceof ConstantExpression) {
                constantAttrs.put(string, ((ConstantExpression)expr).getValue());
                continue;
            }
            if (expr instanceof ClassExpression) {
                constantAttrs.put(string, Type.getType(BytecodeHelper.getTypeDescription(expr.getType())));
                continue;
            }
            if (expr instanceof PropertyExpression) {
                enumAttrs.put(string, (PropertyExpression)expr);
                continue;
            }
            if (expr instanceof ListExpression) {
                arrayAttrs.put(string, (ListExpression)expr);
                continue;
            }
            if (!(expr instanceof ClosureExpression)) continue;
            ClassNode closureClass = this.controller.getClosureWriter().getOrAddClosureClass((ClosureExpression)expr, 1);
            constantAttrs.put(string, Type.getType(BytecodeHelper.getTypeDescription(closureClass)));
        }
        for (Map.Entry entry : constantAttrs.entrySet()) {
            av.visit((String)entry.getKey(), entry.getValue());
        }
        for (Map.Entry entry : enumAttrs.entrySet()) {
            PropertyExpression propExp = (PropertyExpression)entry.getValue();
            av.visitEnum((String)entry.getKey(), BytecodeHelper.getTypeDescription(propExp.getObjectExpression().getType()), String.valueOf(((ConstantExpression)propExp.getProperty()).getValue()));
        }
        for (Map.Entry entry : atAttrs.entrySet()) {
            AnnotationNode atNode = (AnnotationNode)entry.getValue();
            AnnotationVisitor av2 = av.visitAnnotation((String)entry.getKey(), BytecodeHelper.getTypeDescription(atNode.getClassNode()));
            this.visitAnnotationAttributes(atNode, av2);
            av2.visitEnd();
        }
        this.visitArrayAttributes(an, arrayAttrs, av);
    }

    private void visitArrayAttributes(AnnotationNode an, Map<String, ListExpression> arrayAttr, AnnotationVisitor av) {
        if (arrayAttr.isEmpty()) {
            return;
        }
        for (Map.Entry<String, ListExpression> entry : arrayAttr.entrySet()) {
            AnnotationVisitor av2 = av.visitArray(entry.getKey());
            List<Expression> values = entry.getValue().getExpressions();
            if (!values.isEmpty()) {
                int arrayElementType = AsmClassGenerator.determineCommonArrayType(values);
                for (Expression exprChild : values) {
                    this.visitAnnotationArrayElement(exprChild, arrayElementType, av2);
                }
            }
            av2.visitEnd();
        }
    }

    private static int determineCommonArrayType(List values) {
        Expression expr = (Expression)values.get(0);
        int arrayElementType = -1;
        if (expr instanceof AnnotationConstantExpression) {
            arrayElementType = 1;
        } else if (expr instanceof ConstantExpression) {
            arrayElementType = 2;
        } else if (expr instanceof ClassExpression) {
            arrayElementType = 3;
        } else if (expr instanceof PropertyExpression) {
            arrayElementType = 4;
        }
        return arrayElementType;
    }

    private void visitAnnotationArrayElement(Expression expr, int arrayElementType, AnnotationVisitor av) {
        switch (arrayElementType) {
            case 1: {
                AnnotationNode atAttr = (AnnotationNode)((AnnotationConstantExpression)expr).getValue();
                AnnotationVisitor av2 = av.visitAnnotation(null, BytecodeHelper.getTypeDescription(atAttr.getClassNode()));
                this.visitAnnotationAttributes(atAttr, av2);
                av2.visitEnd();
                break;
            }
            case 2: {
                av.visit(null, ((ConstantExpression)expr).getValue());
                break;
            }
            case 3: {
                av.visit(null, Type.getType(BytecodeHelper.getTypeDescription(expr.getType())));
                break;
            }
            case 4: {
                PropertyExpression propExpr = (PropertyExpression)expr;
                av.visitEnum(null, BytecodeHelper.getTypeDescription(propExpr.getObjectExpression().getType()), String.valueOf(((ConstantExpression)propExpr.getProperty()).getValue()));
            }
        }
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression cle) {
        cle.visit(this.controller.getMethodVisitor());
        this.controller.getOperandStack().push(cle.getType());
    }

    public static boolean isThisExpression(Expression expression) {
        if (expression instanceof VariableExpression) {
            VariableExpression varExp = (VariableExpression)expression;
            return varExp.getName().equals("this");
        }
        return false;
    }

    private static boolean isSuperExpression(Expression expression) {
        if (expression instanceof VariableExpression) {
            VariableExpression varExp = (VariableExpression)expression;
            return varExp.getName().equals("super");
        }
        return false;
    }

    private static boolean isThisOrSuper(Expression expression) {
        return AsmClassGenerator.isThisExpression(expression) || AsmClassGenerator.isSuperExpression(expression);
    }

    public void onLineNumber(ASTNode statement, String message) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (statement == null) {
            return;
        }
        int line = statement.getLineNumber();
        this.currentASTNode = statement;
        if (line < 0) {
            return;
        }
        if (line == this.controller.getLineNumber()) {
            return;
        }
        this.controller.setLineNumber(line);
        if (mv != null) {
            Label l = new Label();
            mv.visitLabel(l);
            mv.visitLineNumber(line, l);
        }
    }

    private boolean isInnerClass() {
        return this.controller.getClassNode() instanceof InnerClassNode;
    }

    protected CompileUnit getCompileUnit() {
        CompileUnit answer = this.controller.getClassNode().getCompileUnit();
        if (answer == null) {
            answer = this.context.getCompileUnit();
        }
        return answer;
    }

    public boolean addInnerClass(ClassNode innerClass) {
        ModuleNode mn = this.controller.getClassNode().getModule();
        innerClass.setModule(mn);
        mn.getUnit().addGeneratedInnerClass((InnerClassNode)innerClass);
        return this.innerClasses.add(innerClass);
    }
}


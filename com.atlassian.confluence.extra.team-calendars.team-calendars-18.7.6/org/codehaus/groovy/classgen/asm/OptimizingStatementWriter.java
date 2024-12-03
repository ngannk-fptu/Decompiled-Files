/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.ast.tools.ParameterUtils;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.BinaryExpressionMultiTypeDispatcher;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.StatementWriter;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.BytecodeInterface8;

public class OptimizingStatementWriter
extends StatementWriter {
    private static MethodCaller[] guards = new MethodCaller[]{null, MethodCaller.newStatic(BytecodeInterface8.class, "isOrigInt"), MethodCaller.newStatic(BytecodeInterface8.class, "isOrigL"), MethodCaller.newStatic(BytecodeInterface8.class, "isOrigD"), MethodCaller.newStatic(BytecodeInterface8.class, "isOrigC"), MethodCaller.newStatic(BytecodeInterface8.class, "isOrigB"), MethodCaller.newStatic(BytecodeInterface8.class, "isOrigS"), MethodCaller.newStatic(BytecodeInterface8.class, "isOrigF"), MethodCaller.newStatic(BytecodeInterface8.class, "isOrigZ")};
    private static final MethodCaller disabledStandardMetaClass = MethodCaller.newStatic(BytecodeInterface8.class, "disabledStandardMetaClass");
    private boolean fastPathBlocked = false;
    private WriterController controller;

    public OptimizingStatementWriter(WriterController controller) {
        super(controller);
        this.controller = controller;
    }

    private boolean notEnableFastPath(StatementMeta meta) {
        return this.fastPathBlocked || meta == null || !meta.optimize || this.controller.isFastPath();
    }

    private FastPathData writeGuards(StatementMeta meta, Statement statement) {
        if (this.notEnableFastPath(meta)) {
            return null;
        }
        this.controller.getAcg().onLineNumber(statement, null);
        MethodVisitor mv = this.controller.getMethodVisitor();
        FastPathData fastPathData = new FastPathData();
        Label slowPath = new Label();
        for (int i = 0; i < guards.length; ++i) {
            if (!meta.involvedTypes[i]) continue;
            guards[i].call(mv);
            mv.visitJumpInsn(153, slowPath);
        }
        String owner = BytecodeHelper.getClassInternalName(this.controller.getClassNode());
        MethodNode mn = this.controller.getMethodNode();
        if (mn != null) {
            mv.visitFieldInsn(178, owner, "__$stMC", "Z");
            mv.visitJumpInsn(154, slowPath);
        }
        disabledStandardMetaClass.call(mv);
        mv.visitJumpInsn(154, slowPath);
        mv.visitJumpInsn(167, fastPathData.pathStart);
        mv.visitLabel(slowPath);
        return fastPathData;
    }

    private void writeFastPathPrelude(FastPathData meta) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitJumpInsn(167, meta.afterPath);
        mv.visitLabel(meta.pathStart);
        this.controller.switchToFastPath();
    }

    private void writeFastPathEpilogue(FastPathData meta) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitLabel(meta.afterPath);
        this.controller.switchToSlowPath();
    }

    @Override
    public void writeBlockStatement(BlockStatement statement) {
        StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
        FastPathData fastPathData = this.writeGuards(meta, statement);
        if (fastPathData == null) {
            super.writeBlockStatement(statement);
        } else {
            boolean oldFastPathBlock = this.fastPathBlocked;
            this.fastPathBlocked = true;
            super.writeBlockStatement(statement);
            this.fastPathBlocked = oldFastPathBlock;
            this.writeFastPathPrelude(fastPathData);
            super.writeBlockStatement(statement);
            this.writeFastPathEpilogue(fastPathData);
        }
    }

    @Override
    public void writeDoWhileLoop(DoWhileStatement statement) {
        if (this.controller.isFastPath()) {
            super.writeDoWhileLoop(statement);
        } else {
            StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
            FastPathData fastPathData = this.writeGuards(meta, statement);
            boolean oldFastPathBlock = this.fastPathBlocked;
            this.fastPathBlocked = true;
            super.writeDoWhileLoop(statement);
            this.fastPathBlocked = oldFastPathBlock;
            if (fastPathData == null) {
                return;
            }
            this.writeFastPathPrelude(fastPathData);
            super.writeDoWhileLoop(statement);
            this.writeFastPathEpilogue(fastPathData);
        }
    }

    @Override
    protected void writeIteratorHasNext(MethodVisitor mv) {
        if (this.controller.isFastPath()) {
            mv.visitMethodInsn(185, "java/util/Iterator", "hasNext", "()Z", true);
        } else {
            super.writeIteratorHasNext(mv);
        }
    }

    @Override
    protected void writeIteratorNext(MethodVisitor mv) {
        if (this.controller.isFastPath()) {
            mv.visitMethodInsn(185, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        } else {
            super.writeIteratorNext(mv);
        }
    }

    @Override
    protected void writeForInLoop(ForStatement statement) {
        if (this.controller.isFastPath()) {
            super.writeForInLoop(statement);
        } else {
            StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
            FastPathData fastPathData = this.writeGuards(meta, statement);
            boolean oldFastPathBlock = this.fastPathBlocked;
            this.fastPathBlocked = true;
            super.writeForInLoop(statement);
            this.fastPathBlocked = oldFastPathBlock;
            if (fastPathData == null) {
                return;
            }
            this.writeFastPathPrelude(fastPathData);
            super.writeForInLoop(statement);
            this.writeFastPathEpilogue(fastPathData);
        }
    }

    @Override
    protected void writeForLoopWithClosureList(ForStatement statement) {
        if (this.controller.isFastPath()) {
            super.writeForLoopWithClosureList(statement);
        } else {
            StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
            FastPathData fastPathData = this.writeGuards(meta, statement);
            boolean oldFastPathBlock = this.fastPathBlocked;
            this.fastPathBlocked = true;
            super.writeForLoopWithClosureList(statement);
            this.fastPathBlocked = oldFastPathBlock;
            if (fastPathData == null) {
                return;
            }
            this.writeFastPathPrelude(fastPathData);
            super.writeForLoopWithClosureList(statement);
            this.writeFastPathEpilogue(fastPathData);
        }
    }

    @Override
    public void writeWhileLoop(WhileStatement statement) {
        if (this.controller.isFastPath()) {
            super.writeWhileLoop(statement);
        } else {
            StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
            FastPathData fastPathData = this.writeGuards(meta, statement);
            boolean oldFastPathBlock = this.fastPathBlocked;
            this.fastPathBlocked = true;
            super.writeWhileLoop(statement);
            this.fastPathBlocked = oldFastPathBlock;
            if (fastPathData == null) {
                return;
            }
            this.writeFastPathPrelude(fastPathData);
            super.writeWhileLoop(statement);
            this.writeFastPathEpilogue(fastPathData);
        }
    }

    @Override
    public void writeIfElse(IfStatement statement) {
        StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
        FastPathData fastPathData = this.writeGuards(meta, statement);
        if (fastPathData == null) {
            super.writeIfElse(statement);
        } else {
            boolean oldFastPathBlock = this.fastPathBlocked;
            this.fastPathBlocked = true;
            super.writeIfElse(statement);
            this.fastPathBlocked = oldFastPathBlock;
            if (fastPathData == null) {
                return;
            }
            this.writeFastPathPrelude(fastPathData);
            super.writeIfElse(statement);
            this.writeFastPathEpilogue(fastPathData);
        }
    }

    private boolean isNewPathFork(StatementMeta meta) {
        if (meta == null || !meta.optimize) {
            return false;
        }
        if (this.fastPathBlocked) {
            return false;
        }
        return !this.controller.isFastPath();
    }

    @Override
    public void writeReturn(ReturnStatement statement) {
        if (this.controller.isFastPath()) {
            super.writeReturn(statement);
        } else {
            StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
            if (this.isNewPathFork(meta) && this.writeDeclarationExtraction(statement)) {
                if (meta.declaredVariableExpression != null) {
                    this.controller.getCompileStack().defineVariable(meta.declaredVariableExpression, false);
                }
                FastPathData fastPathData = this.writeGuards(meta, statement);
                boolean oldFastPathBlock = this.fastPathBlocked;
                this.fastPathBlocked = true;
                super.writeReturn(statement);
                this.fastPathBlocked = oldFastPathBlock;
                if (fastPathData == null) {
                    return;
                }
                this.writeFastPathPrelude(fastPathData);
                super.writeReturn(statement);
                this.writeFastPathEpilogue(fastPathData);
            } else {
                super.writeReturn(statement);
            }
        }
    }

    @Override
    public void writeExpressionStatement(ExpressionStatement statement) {
        if (this.controller.isFastPath()) {
            super.writeExpressionStatement(statement);
        } else {
            StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
            if (this.isNewPathFork(meta) && this.writeDeclarationExtraction(statement)) {
                if (meta.declaredVariableExpression != null) {
                    this.controller.getCompileStack().defineVariable(meta.declaredVariableExpression, false);
                }
                FastPathData fastPathData = this.writeGuards(meta, statement);
                boolean oldFastPathBlock = this.fastPathBlocked;
                this.fastPathBlocked = true;
                super.writeExpressionStatement(statement);
                this.fastPathBlocked = oldFastPathBlock;
                if (fastPathData == null) {
                    return;
                }
                this.writeFastPathPrelude(fastPathData);
                super.writeExpressionStatement(statement);
                this.writeFastPathEpilogue(fastPathData);
            } else {
                super.writeExpressionStatement(statement);
            }
        }
    }

    private boolean writeDeclarationExtraction(Statement statement) {
        Expression ex = null;
        if (statement instanceof ReturnStatement) {
            ReturnStatement rs = (ReturnStatement)statement;
            ex = rs.getExpression();
        } else if (statement instanceof ExpressionStatement) {
            ExpressionStatement es = (ExpressionStatement)statement;
            ex = es.getExpression();
        } else {
            throw new GroovyBugError("unknown statement type :" + statement.getClass());
        }
        if (!(ex instanceof DeclarationExpression)) {
            return true;
        }
        DeclarationExpression declaration = (DeclarationExpression)ex;
        if ((ex = declaration.getLeftExpression()) instanceof TupleExpression) {
            return false;
        }
        StatementMeta meta = (StatementMeta)statement.getNodeMetaData(StatementMeta.class);
        if (meta != null) {
            meta.declaredVariableExpression = declaration.getVariableExpression();
        }
        BinaryExpression assignment = new BinaryExpression(declaration.getLeftExpression(), declaration.getOperation(), declaration.getRightExpression());
        assignment.setSourcePosition(declaration);
        assignment.copyNodeMetaData(declaration);
        if (statement instanceof ReturnStatement) {
            ReturnStatement rs = (ReturnStatement)statement;
            rs.setExpression(assignment);
        } else if (statement instanceof ExpressionStatement) {
            ExpressionStatement es = (ExpressionStatement)statement;
            es.setExpression(assignment);
        } else {
            throw new GroovyBugError("unknown statement type :" + statement.getClass());
        }
        return true;
    }

    public static void setNodeMeta(TypeChooser chooser, ClassNode classNode) {
        if (classNode.getNodeMetaData(ClassNodeSkip.class) != null) {
            return;
        }
        new OptVisitor(chooser).visitClass(classNode);
    }

    private static StatementMeta addMeta(ASTNode node) {
        StatementMeta metaOld = (StatementMeta)node.getNodeMetaData(StatementMeta.class);
        StatementMeta meta = metaOld;
        if (meta == null) {
            meta = new StatementMeta();
        }
        meta.optimize = true;
        if (metaOld == null) {
            node.setNodeMetaData(StatementMeta.class, meta);
        }
        return meta;
    }

    private static StatementMeta addMeta(ASTNode node, OptimizeFlagsCollector opt) {
        StatementMeta meta = OptimizingStatementWriter.addMeta(node);
        meta.chainInvolvedTypes(opt);
        return meta;
    }

    private static class OptVisitor
    extends ClassCodeVisitorSupport {
        private final TypeChooser typeChooser;
        private ClassNode node;
        private OptimizeFlagsCollector opt = new OptimizeFlagsCollector();
        private boolean optimizeMethodCall = true;
        private VariableScope scope;
        private static final VariableScope nonStaticScope = new VariableScope();

        public OptVisitor(TypeChooser chooser) {
            this.typeChooser = chooser;
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return null;
        }

        @Override
        public void visitClass(ClassNode node) {
            this.optimizeMethodCall = !node.implementsInterface(ClassHelper.GROOVY_INTERCEPTABLE_TYPE);
            this.node = node;
            this.scope = nonStaticScope;
            super.visitClass(node);
            this.scope = null;
            this.node = null;
        }

        @Override
        public void visitMethod(MethodNode node) {
            this.scope = node.getVariableScope();
            super.visitMethod(node);
            this.opt.reset();
        }

        @Override
        public void visitConstructor(ConstructorNode node) {
            this.scope = node.getVariableScope();
            super.visitConstructor(node);
        }

        @Override
        public void visitReturnStatement(ReturnStatement statement) {
            this.opt.push();
            super.visitReturnStatement(statement);
            if (this.opt.shouldOptimize()) {
                OptimizingStatementWriter.addMeta(statement, this.opt);
            }
            this.opt.pop(this.opt.shouldOptimize());
        }

        @Override
        public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
            super.visitUnaryMinusExpression(expression);
            StatementMeta meta = OptimizingStatementWriter.addMeta(expression);
            meta.type = ClassHelper.OBJECT_TYPE;
        }

        @Override
        public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
            super.visitUnaryPlusExpression(expression);
            StatementMeta meta = OptimizingStatementWriter.addMeta(expression);
            meta.type = ClassHelper.OBJECT_TYPE;
        }

        @Override
        public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
            super.visitBitwiseNegationExpression(expression);
            StatementMeta meta = OptimizingStatementWriter.addMeta(expression);
            meta.type = ClassHelper.OBJECT_TYPE;
        }

        private void addTypeInformation(Expression expression, Expression orig) {
            ClassNode type = this.typeChooser.resolveType(expression, this.node);
            if (ClassHelper.isPrimitiveType(type)) {
                StatementMeta meta = OptimizingStatementWriter.addMeta(orig);
                meta.type = type;
                this.opt.chainShouldOptimize(true);
                this.opt.chainInvolvedType(type);
            }
        }

        @Override
        public void visitPrefixExpression(PrefixExpression expression) {
            super.visitPrefixExpression(expression);
            this.addTypeInformation(expression.getExpression(), expression);
        }

        @Override
        public void visitPostfixExpression(PostfixExpression expression) {
            super.visitPostfixExpression(expression);
            this.addTypeInformation(expression.getExpression(), expression);
        }

        @Override
        public void visitDeclarationExpression(DeclarationExpression expression) {
            Expression right = expression.getRightExpression();
            right.visit(this);
            ClassNode leftType = this.typeChooser.resolveType(expression.getLeftExpression(), this.node);
            Expression rightExpression = expression.getRightExpression();
            ClassNode rightType = this.optimizeDivWithIntOrLongTarget(rightExpression, leftType);
            if (rightType == null) {
                rightType = this.typeChooser.resolveType(expression.getRightExpression(), this.node);
            }
            if (ClassHelper.isPrimitiveType(leftType) && ClassHelper.isPrimitiveType(rightType)) {
                if (right instanceof ConstantExpression) {
                    this.opt.chainCanOptimize(true);
                } else {
                    this.opt.chainShouldOptimize(true);
                }
                StatementMeta meta = OptimizingStatementWriter.addMeta(expression);
                ClassNode declarationType = this.typeChooser.resolveType(expression, this.node);
                meta.type = declarationType != null ? declarationType : leftType;
                this.opt.chainInvolvedType(leftType);
                this.opt.chainInvolvedType(rightType);
            }
        }

        @Override
        public void visitBinaryExpression(BinaryExpression expression) {
            if (expression.getNodeMetaData(StatementMeta.class) != null) {
                return;
            }
            super.visitBinaryExpression(expression);
            ClassNode leftType = this.typeChooser.resolveType(expression.getLeftExpression(), this.node);
            ClassNode rightType = this.typeChooser.resolveType(expression.getRightExpression(), this.node);
            ClassNode resultType = null;
            int operation = expression.getOperation().getType();
            if (operation == 30 && leftType.isArray()) {
                this.opt.chainShouldOptimize(true);
                resultType = leftType.getComponentType();
            } else {
                switch (operation) {
                    case 120: 
                    case 123: 
                    case 124: 
                    case 125: 
                    case 126: 
                    case 127: {
                        if (WideningCategories.isIntCategory(leftType) && WideningCategories.isIntCategory(rightType)) {
                            this.opt.chainShouldOptimize(true);
                        } else if (WideningCategories.isLongCategory(leftType) && WideningCategories.isLongCategory(rightType)) {
                            this.opt.chainShouldOptimize(true);
                        } else if (WideningCategories.isDoubleCategory(leftType) && WideningCategories.isDoubleCategory(rightType)) {
                            this.opt.chainShouldOptimize(true);
                        } else {
                            this.opt.chainCanOptimize(true);
                        }
                        resultType = ClassHelper.boolean_TYPE;
                        break;
                    }
                    case 162: 
                    case 164: 
                    case 166: 
                    case 168: {
                        if (ClassHelper.boolean_TYPE.equals(leftType) && ClassHelper.boolean_TYPE.equals(rightType)) {
                            this.opt.chainShouldOptimize(true);
                        } else {
                            this.opt.chainCanOptimize(true);
                        }
                        expression.setType(ClassHelper.boolean_TYPE);
                        resultType = ClassHelper.boolean_TYPE;
                        break;
                    }
                    case 203: 
                    case 213: {
                        if (WideningCategories.isLongCategory(leftType) && WideningCategories.isLongCategory(rightType)) {
                            resultType = ClassHelper.BigDecimal_TYPE;
                            this.opt.chainShouldOptimize(true);
                            break;
                        }
                        if (WideningCategories.isBigDecCategory(leftType) && WideningCategories.isBigDecCategory(rightType) || !WideningCategories.isDoubleCategory(leftType) || !WideningCategories.isDoubleCategory(rightType)) break;
                        resultType = ClassHelper.double_TYPE;
                        this.opt.chainShouldOptimize(true);
                        break;
                    }
                    case 206: 
                    case 216: {
                        break;
                    }
                    case 100: {
                        resultType = this.optimizeDivWithIntOrLongTarget(expression.getRightExpression(), leftType);
                        this.opt.chainCanOptimize(true);
                        break;
                    }
                    default: {
                        if (WideningCategories.isIntCategory(leftType) && WideningCategories.isIntCategory(rightType)) {
                            resultType = ClassHelper.int_TYPE;
                            this.opt.chainShouldOptimize(true);
                            break;
                        }
                        if (WideningCategories.isLongCategory(leftType) && WideningCategories.isLongCategory(rightType)) {
                            resultType = ClassHelper.long_TYPE;
                            this.opt.chainShouldOptimize(true);
                            break;
                        }
                        if (WideningCategories.isBigDecCategory(leftType) && WideningCategories.isBigDecCategory(rightType) || !WideningCategories.isDoubleCategory(leftType) || !WideningCategories.isDoubleCategory(rightType)) break;
                        resultType = ClassHelper.double_TYPE;
                        this.opt.chainShouldOptimize(true);
                    }
                }
            }
            if (resultType != null) {
                StatementMeta meta = OptimizingStatementWriter.addMeta(expression);
                meta.type = resultType;
                this.opt.chainInvolvedType(resultType);
                this.opt.chainInvolvedType(leftType);
                this.opt.chainInvolvedType(rightType);
            }
        }

        private ClassNode optimizeDivWithIntOrLongTarget(Expression rhs, ClassNode assignmentTartgetType) {
            ClassNode target;
            if (!(rhs instanceof BinaryExpression)) {
                return null;
            }
            BinaryExpression binExp = (BinaryExpression)rhs;
            int op = binExp.getOperation().getType();
            if (op != 203 && op != 213) {
                return null;
            }
            ClassNode originalResultType = this.typeChooser.resolveType(binExp, this.node);
            if (!originalResultType.equals(ClassHelper.BigDecimal_TYPE) || !WideningCategories.isLongCategory(assignmentTartgetType) && !WideningCategories.isFloatingCategory(assignmentTartgetType)) {
                return null;
            }
            ClassNode leftType = this.typeChooser.resolveType(binExp.getLeftExpression(), this.node);
            if (!WideningCategories.isLongCategory(leftType)) {
                return null;
            }
            ClassNode rightType = this.typeChooser.resolveType(binExp.getRightExpression(), this.node);
            if (!WideningCategories.isLongCategory(rightType)) {
                return null;
            }
            if (WideningCategories.isIntCategory(leftType) && WideningCategories.isIntCategory(rightType)) {
                target = ClassHelper.int_TYPE;
            } else if (WideningCategories.isLongCategory(leftType) && WideningCategories.isLongCategory(rightType)) {
                target = ClassHelper.long_TYPE;
            } else if (WideningCategories.isDoubleCategory(leftType) && WideningCategories.isDoubleCategory(rightType)) {
                target = ClassHelper.double_TYPE;
            } else {
                return null;
            }
            StatementMeta meta = OptimizingStatementWriter.addMeta(rhs);
            meta.type = target;
            this.opt.chainInvolvedType(target);
            return target;
        }

        @Override
        public void visitExpressionStatement(ExpressionStatement statement) {
            if (statement.getNodeMetaData(StatementMeta.class) != null) {
                return;
            }
            this.opt.push();
            super.visitExpressionStatement(statement);
            if (this.opt.shouldOptimize()) {
                OptimizingStatementWriter.addMeta(statement, this.opt);
            }
            this.opt.pop(this.opt.shouldOptimize());
        }

        @Override
        public void visitBlockStatement(BlockStatement block) {
            this.opt.push();
            boolean optAll = true;
            for (Statement statement : block.getStatements()) {
                this.opt.push();
                statement.visit(this);
                optAll = optAll && this.opt.canOptimize();
                this.opt.pop(true);
            }
            if (block.isEmpty()) {
                this.opt.chainCanOptimize(true);
                this.opt.pop(true);
            } else {
                this.opt.chainShouldOptimize(optAll);
                if (optAll) {
                    OptimizingStatementWriter.addMeta(block, this.opt);
                }
                this.opt.pop(optAll);
            }
        }

        @Override
        public void visitIfElse(IfStatement statement) {
            this.opt.push();
            super.visitIfElse(statement);
            if (this.opt.shouldOptimize()) {
                OptimizingStatementWriter.addMeta(statement, this.opt);
            }
            this.opt.pop(this.opt.shouldOptimize());
        }

        @Override
        public void visitStaticMethodCallExpression(StaticMethodCallExpression expression) {
            if (expression.getNodeMetaData(StatementMeta.class) != null) {
                return;
            }
            super.visitStaticMethodCallExpression(expression);
            this.setMethodTarget(expression, expression.getMethod(), expression.getArguments(), true);
        }

        @Override
        public void visitMethodCallExpression(MethodCallExpression expression) {
            if (expression.getNodeMetaData(StatementMeta.class) != null) {
                return;
            }
            super.visitMethodCallExpression(expression);
            Expression object = expression.getObjectExpression();
            boolean setTarget = AsmClassGenerator.isThisExpression(object);
            if (!setTarget) {
                if (!(object instanceof ClassExpression)) {
                    return;
                }
                setTarget = object.equals(this.node);
            }
            if (!setTarget) {
                return;
            }
            this.setMethodTarget(expression, expression.getMethodAsString(), expression.getArguments(), true);
        }

        @Override
        public void visitConstructorCallExpression(ConstructorCallExpression call) {
            if (call.getNodeMetaData(StatementMeta.class) != null) {
                return;
            }
            super.visitConstructorCallExpression(call);
        }

        private void setMethodTarget(Expression expression, String name, Expression callArgs, boolean isMethod) {
            ClassNode type;
            MethodNode target;
            if (name == null) {
                return;
            }
            if (!this.optimizeMethodCall) {
                return;
            }
            if (AsmClassGenerator.containsSpreadExpression(callArgs)) {
                return;
            }
            Parameter[] paraTypes = null;
            if (callArgs instanceof ArgumentListExpression) {
                ArgumentListExpression args = (ArgumentListExpression)callArgs;
                int size = args.getExpressions().size();
                paraTypes = new Parameter[size];
                int i = 0;
                for (Expression exp : args.getExpressions()) {
                    ClassNode type2 = this.typeChooser.resolveType(exp, this.node);
                    if (!OptVisitor.validTypeForCall(type2)) {
                        return;
                    }
                    paraTypes[i] = new Parameter(type2, "");
                    ++i;
                }
            } else {
                ClassNode type3 = this.typeChooser.resolveType(callArgs, this.node);
                if (!OptVisitor.validTypeForCall(type3)) {
                    return;
                }
                paraTypes = new Parameter[]{new Parameter(type3, "")};
            }
            if (isMethod) {
                target = this.node.getMethod(name, paraTypes);
                if (target == null) {
                    return;
                }
                if (!target.getDeclaringClass().equals(this.node)) {
                    return;
                }
                if (this.scope.isInStaticContext() && !target.isStatic()) {
                    return;
                }
                type = target.getReturnType().redirect();
            } else {
                type = expression.getType();
                target = OptVisitor.selectConstructor(type, paraTypes);
                if (target == null) {
                    return;
                }
            }
            StatementMeta meta = OptimizingStatementWriter.addMeta(expression);
            meta.target = target;
            meta.type = type;
            this.opt.chainShouldOptimize(true);
        }

        private static MethodNode selectConstructor(ClassNode node, Parameter[] paraTypes) {
            List<ConstructorNode> cl = node.getDeclaredConstructors();
            MethodNode res = null;
            for (ConstructorNode cn : cl) {
                if (!ParameterUtils.parametersEqual(cn.getParameters(), paraTypes)) continue;
                res = cn;
                break;
            }
            if (res != null && res.isPublic()) {
                return res;
            }
            return null;
        }

        private static boolean validTypeForCall(ClassNode type) {
            if (ClassHelper.isPrimitiveType(type)) {
                return true;
            }
            return (type.getModifiers() & 0x10) > 0;
        }

        @Override
        public void visitClosureExpression(ClosureExpression expression) {
        }

        @Override
        public void visitForLoop(ForStatement statement) {
            this.opt.push();
            super.visitForLoop(statement);
            if (this.opt.shouldOptimize()) {
                OptimizingStatementWriter.addMeta(statement, this.opt);
            }
            this.opt.pop(this.opt.shouldOptimize());
        }
    }

    private static class OptimizeFlagsCollector {
        private OptimizeFlagsEntry current = new OptimizeFlagsEntry();
        private LinkedList<OptimizeFlagsEntry> olderEntries = new LinkedList();

        private OptimizeFlagsCollector() {
        }

        public void push() {
            this.olderEntries.addLast(this.current);
            this.current = new OptimizeFlagsEntry();
        }

        public void pop(boolean propagateFlags) {
            OptimizeFlagsEntry old = this.current;
            this.current = this.olderEntries.removeLast();
            if (propagateFlags) {
                this.chainCanOptimize(old.canOptimize);
                this.chainShouldOptimize(old.shouldOptimize);
                for (int i = 0; i < BinaryExpressionMultiTypeDispatcher.typeMapKeyNames.length; ++i) {
                    boolean[] blArray = this.current.involvedTypes;
                    int n = i;
                    blArray[n] = blArray[n] | old.involvedTypes[i];
                }
            }
        }

        public String toString() {
            StringBuilder ret = this.current.shouldOptimize ? new StringBuilder("should optimize, can = " + this.current.canOptimize) : (this.current.canOptimize ? new StringBuilder("can optimize") : new StringBuilder("don't optimize"));
            ret.append(" involvedTypes =");
            for (int i = 0; i < BinaryExpressionMultiTypeDispatcher.typeMapKeyNames.length; ++i) {
                if (!this.current.involvedTypes[i]) continue;
                ret.append(" ").append(BinaryExpressionMultiTypeDispatcher.typeMapKeyNames[i]);
            }
            return ret.toString();
        }

        private boolean shouldOptimize() {
            return this.current.shouldOptimize;
        }

        private boolean canOptimize() {
            return this.current.canOptimize || this.current.shouldOptimize;
        }

        public void chainShouldOptimize(boolean opt) {
            this.current.shouldOptimize = this.shouldOptimize() || opt;
        }

        public void chainCanOptimize(boolean opt) {
            this.current.canOptimize = this.current.canOptimize || opt;
        }

        public void chainInvolvedType(ClassNode type) {
            Integer res = BinaryExpressionMultiTypeDispatcher.typeMap.get(type);
            if (res == null) {
                return;
            }
            ((OptimizeFlagsEntry)this.current).involvedTypes[res.intValue()] = true;
        }

        public void reset() {
            this.current.canOptimize = false;
            this.current.shouldOptimize = false;
            OptimizeFlagsEntry.access$102(this.current, new boolean[BinaryExpressionMultiTypeDispatcher.typeMapKeyNames.length]);
        }

        private static class OptimizeFlagsEntry {
            private boolean canOptimize = false;
            private boolean shouldOptimize = false;
            private boolean[] involvedTypes = new boolean[BinaryExpressionMultiTypeDispatcher.typeMapKeyNames.length];

            private OptimizeFlagsEntry() {
            }

            static /* synthetic */ boolean[] access$102(OptimizeFlagsEntry x0, boolean[] x1) {
                x0.involvedTypes = x1;
                return x1;
            }
        }
    }

    public static class StatementMeta {
        private boolean optimize = false;
        protected MethodNode target;
        protected ClassNode type;
        protected VariableExpression declaredVariableExpression;
        protected boolean[] involvedTypes = new boolean[BinaryExpressionMultiTypeDispatcher.typeMapKeyNames.length];

        public void chainInvolvedTypes(OptimizeFlagsCollector opt) {
            for (int i = 0; i < BinaryExpressionMultiTypeDispatcher.typeMapKeyNames.length; ++i) {
                if (!opt.current.involvedTypes[i]) continue;
                this.involvedTypes[i] = true;
            }
        }

        public String toString() {
            StringBuilder ret = new StringBuilder("optimize=" + this.optimize + " target=" + this.target + " type=" + this.type + " involvedTypes=");
            for (int i = 0; i < BinaryExpressionMultiTypeDispatcher.typeMapKeyNames.length; ++i) {
                if (!this.involvedTypes[i]) continue;
                ret.append(" ").append(BinaryExpressionMultiTypeDispatcher.typeMapKeyNames[i]);
            }
            return ret.toString();
        }
    }

    public static class ClassNodeSkip {
    }

    private static class FastPathData {
        private Label pathStart = new Label();
        private Label afterPath = new Label();

        private FastPathData() {
        }
    }
}


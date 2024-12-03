/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
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
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.syntax.Token;

public class SecureASTCustomizer
extends CompilationCustomizer {
    private boolean isPackageAllowed = true;
    private boolean isMethodDefinitionAllowed = true;
    private boolean isClosuresAllowed = true;
    private List<String> importsWhitelist;
    private List<String> importsBlacklist;
    private List<String> staticImportsWhitelist;
    private List<String> staticImportsBlacklist;
    private List<String> starImportsWhitelist;
    private List<String> starImportsBlacklist;
    private List<String> staticStarImportsWhitelist;
    private List<String> staticStarImportsBlacklist;
    private boolean isIndirectImportCheckEnabled;
    private List<Class<? extends Statement>> statementsWhitelist;
    private List<Class<? extends Statement>> statementsBlacklist;
    private final List<StatementChecker> statementCheckers = new LinkedList<StatementChecker>();
    private List<Class<? extends Expression>> expressionsWhitelist;
    private List<Class<? extends Expression>> expressionsBlacklist;
    private final List<ExpressionChecker> expressionCheckers = new LinkedList<ExpressionChecker>();
    private List<Integer> tokensWhitelist;
    private List<Integer> tokensBlacklist;
    private List<String> constantTypesWhiteList;
    private List<String> constantTypesBlackList;
    private List<String> receiversWhiteList;
    private List<String> receiversBlackList;

    public SecureASTCustomizer() {
        super(CompilePhase.CANONICALIZATION);
    }

    public boolean isMethodDefinitionAllowed() {
        return this.isMethodDefinitionAllowed;
    }

    public void setMethodDefinitionAllowed(boolean methodDefinitionAllowed) {
        this.isMethodDefinitionAllowed = methodDefinitionAllowed;
    }

    public boolean isPackageAllowed() {
        return this.isPackageAllowed;
    }

    public boolean isClosuresAllowed() {
        return this.isClosuresAllowed;
    }

    public void setClosuresAllowed(boolean closuresAllowed) {
        this.isClosuresAllowed = closuresAllowed;
    }

    public void setPackageAllowed(boolean packageAllowed) {
        this.isPackageAllowed = packageAllowed;
    }

    public List<String> getImportsBlacklist() {
        return this.importsBlacklist;
    }

    public void setImportsBlacklist(List<String> importsBlacklist) {
        if (this.importsWhitelist != null || this.starImportsWhitelist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.importsBlacklist = importsBlacklist;
    }

    public List<String> getImportsWhitelist() {
        return this.importsWhitelist;
    }

    public void setImportsWhitelist(List<String> importsWhitelist) {
        if (this.importsBlacklist != null || this.starImportsBlacklist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.importsWhitelist = importsWhitelist;
    }

    public List<String> getStarImportsBlacklist() {
        return this.starImportsBlacklist;
    }

    public void setStarImportsBlacklist(List<String> starImportsBlacklist) {
        if (this.importsWhitelist != null || this.starImportsWhitelist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.starImportsBlacklist = SecureASTCustomizer.normalizeStarImports(starImportsBlacklist);
        if (this.importsBlacklist == null) {
            this.importsBlacklist = Collections.emptyList();
        }
    }

    public List<String> getStarImportsWhitelist() {
        return this.starImportsWhitelist;
    }

    public void setStarImportsWhitelist(List<String> starImportsWhitelist) {
        if (this.importsBlacklist != null || this.starImportsBlacklist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.starImportsWhitelist = SecureASTCustomizer.normalizeStarImports(starImportsWhitelist);
        if (this.importsWhitelist == null) {
            this.importsWhitelist = Collections.emptyList();
        }
    }

    private static List<String> normalizeStarImports(List<String> starImports) {
        ArrayList<String> result = new ArrayList<String>(starImports.size());
        for (String starImport : starImports) {
            if (starImport.endsWith(".*")) {
                result.add(starImport);
                continue;
            }
            if (starImport.endsWith(".")) {
                result.add(starImport + "*");
                continue;
            }
            result.add(starImport + ".*");
        }
        return Collections.unmodifiableList(result);
    }

    public List<String> getStaticImportsBlacklist() {
        return this.staticImportsBlacklist;
    }

    public void setStaticImportsBlacklist(List<String> staticImportsBlacklist) {
        if (this.staticImportsWhitelist != null || this.staticStarImportsWhitelist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.staticImportsBlacklist = staticImportsBlacklist;
    }

    public List<String> getStaticImportsWhitelist() {
        return this.staticImportsWhitelist;
    }

    public void setStaticImportsWhitelist(List<String> staticImportsWhitelist) {
        if (this.staticImportsBlacklist != null || this.staticStarImportsBlacklist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.staticImportsWhitelist = staticImportsWhitelist;
    }

    public List<String> getStaticStarImportsBlacklist() {
        return this.staticStarImportsBlacklist;
    }

    public void setStaticStarImportsBlacklist(List<String> staticStarImportsBlacklist) {
        if (this.staticImportsWhitelist != null || this.staticStarImportsWhitelist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.staticStarImportsBlacklist = SecureASTCustomizer.normalizeStarImports(staticStarImportsBlacklist);
        if (this.staticImportsBlacklist == null) {
            this.staticImportsBlacklist = Collections.emptyList();
        }
    }

    public List<String> getStaticStarImportsWhitelist() {
        return this.staticStarImportsWhitelist;
    }

    public void setStaticStarImportsWhitelist(List<String> staticStarImportsWhitelist) {
        if (this.staticImportsBlacklist != null || this.staticStarImportsBlacklist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.staticStarImportsWhitelist = SecureASTCustomizer.normalizeStarImports(staticStarImportsWhitelist);
        if (this.staticImportsWhitelist == null) {
            this.staticImportsWhitelist = Collections.emptyList();
        }
    }

    public List<Class<? extends Expression>> getExpressionsBlacklist() {
        return this.expressionsBlacklist;
    }

    public void setExpressionsBlacklist(List<Class<? extends Expression>> expressionsBlacklist) {
        if (this.expressionsWhitelist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.expressionsBlacklist = expressionsBlacklist;
    }

    public List<Class<? extends Expression>> getExpressionsWhitelist() {
        return this.expressionsWhitelist;
    }

    public void setExpressionsWhitelist(List<Class<? extends Expression>> expressionsWhitelist) {
        if (this.expressionsBlacklist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.expressionsWhitelist = expressionsWhitelist;
    }

    public List<Class<? extends Statement>> getStatementsBlacklist() {
        return this.statementsBlacklist;
    }

    public void setStatementsBlacklist(List<Class<? extends Statement>> statementsBlacklist) {
        if (this.statementsWhitelist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.statementsBlacklist = statementsBlacklist;
    }

    public List<Class<? extends Statement>> getStatementsWhitelist() {
        return this.statementsWhitelist;
    }

    public void setStatementsWhitelist(List<Class<? extends Statement>> statementsWhitelist) {
        if (this.statementsBlacklist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.statementsWhitelist = statementsWhitelist;
    }

    public List<Integer> getTokensBlacklist() {
        return this.tokensBlacklist;
    }

    public boolean isIndirectImportCheckEnabled() {
        return this.isIndirectImportCheckEnabled;
    }

    public void setIndirectImportCheckEnabled(boolean indirectImportCheckEnabled) {
        this.isIndirectImportCheckEnabled = indirectImportCheckEnabled;
    }

    public void setTokensBlacklist(List<Integer> tokensBlacklist) {
        if (this.tokensWhitelist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.tokensBlacklist = tokensBlacklist;
    }

    public List<Integer> getTokensWhitelist() {
        return this.tokensWhitelist;
    }

    public void setTokensWhitelist(List<Integer> tokensWhitelist) {
        if (this.tokensBlacklist != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.tokensWhitelist = tokensWhitelist;
    }

    public void addStatementCheckers(StatementChecker ... checkers) {
        this.statementCheckers.addAll(Arrays.asList(checkers));
    }

    public void addExpressionCheckers(ExpressionChecker ... checkers) {
        this.expressionCheckers.addAll(Arrays.asList(checkers));
    }

    public List<String> getConstantTypesBlackList() {
        return this.constantTypesBlackList;
    }

    public void setConstantTypesBlackList(List<String> constantTypesBlackList) {
        if (this.constantTypesWhiteList != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.constantTypesBlackList = constantTypesBlackList;
    }

    public List<String> getConstantTypesWhiteList() {
        return this.constantTypesWhiteList;
    }

    public void setConstantTypesWhiteList(List<String> constantTypesWhiteList) {
        if (this.constantTypesBlackList != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.constantTypesWhiteList = constantTypesWhiteList;
    }

    public void setConstantTypesClassesWhiteList(List<Class> constantTypesWhiteList) {
        LinkedList<String> values = new LinkedList<String>();
        for (Class aClass : constantTypesWhiteList) {
            values.add(aClass.getName());
        }
        this.setConstantTypesWhiteList(values);
    }

    public void setConstantTypesClassesBlackList(List<Class> constantTypesBlackList) {
        LinkedList<String> values = new LinkedList<String>();
        for (Class aClass : constantTypesBlackList) {
            values.add(aClass.getName());
        }
        this.setConstantTypesBlackList(values);
    }

    public List<String> getReceiversBlackList() {
        return this.receiversBlackList;
    }

    public void setReceiversBlackList(List<String> receiversBlackList) {
        if (this.receiversWhiteList != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.receiversBlackList = receiversBlackList;
    }

    public void setReceiversClassesBlackList(List<Class> receiversBlacklist) {
        LinkedList<String> values = new LinkedList<String>();
        for (Class aClass : receiversBlacklist) {
            values.add(aClass.getName());
        }
        this.setReceiversBlackList(values);
    }

    public List<String> getReceiversWhiteList() {
        return this.receiversWhiteList;
    }

    public void setReceiversWhiteList(List<String> receiversWhiteList) {
        if (this.receiversBlackList != null) {
            throw new IllegalArgumentException("You are not allowed to set both whitelist and blacklist");
        }
        this.receiversWhiteList = receiversWhiteList;
    }

    public void setReceiversClassesWhiteList(List<Class> receiversWhitelist) {
        LinkedList<String> values = new LinkedList<String>();
        for (Class aClass : receiversWhitelist) {
            values.add(aClass.getName());
        }
        this.setReceiversWhiteList(values);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        String className;
        ModuleNode ast = source.getAST();
        if (!this.isPackageAllowed && ast.getPackage() != null) {
            throw new SecurityException("Package definitions are not allowed");
        }
        this.checkMethodDefinitionAllowed(classNode);
        if (this.importsBlacklist != null || this.importsWhitelist != null || this.starImportsBlacklist != null || this.starImportsWhitelist != null) {
            for (ImportNode importNode : ast.getImports()) {
                className = importNode.getClassName();
                this.assertImportIsAllowed(className);
            }
            for (ImportNode importNode : ast.getStarImports()) {
                className = importNode.getPackageName();
                this.assertStarImportIsAllowed(className + "*");
            }
        }
        if (this.staticImportsBlacklist != null || this.staticImportsWhitelist != null || this.staticStarImportsBlacklist != null || this.staticStarImportsWhitelist != null) {
            for (Map.Entry entry : ast.getStaticImports().entrySet()) {
                className = ((ImportNode)entry.getValue()).getClassName();
                this.assertStaticImportIsAllowed((String)entry.getKey(), className);
            }
            for (Map.Entry entry : ast.getStaticStarImports().entrySet()) {
                className = ((ImportNode)entry.getValue()).getClassName();
                this.assertStaticImportIsAllowed((String)entry.getKey(), className);
            }
        }
        SecuringCodeVisitor visitor = new SecuringCodeVisitor();
        ast.getStatementBlock().visit(visitor);
        for (ClassNode clNode : ast.getClasses()) {
            if (clNode == classNode) continue;
            this.checkMethodDefinitionAllowed(clNode);
            for (MethodNode methodNode : clNode.getMethods()) {
                if (methodNode.isSynthetic() || methodNode.getCode() == null) continue;
                methodNode.getCode().visit(visitor);
            }
        }
        List<MethodNode> list = SecureASTCustomizer.filterMethods(classNode);
        if (this.isMethodDefinitionAllowed) {
            for (MethodNode method : list) {
                if (method.getDeclaringClass() != classNode || method.getCode() == null) continue;
                method.getCode().visit(visitor);
            }
        }
    }

    private void checkMethodDefinitionAllowed(ClassNode owner) {
        if (this.isMethodDefinitionAllowed) {
            return;
        }
        List<MethodNode> methods = SecureASTCustomizer.filterMethods(owner);
        if (!methods.isEmpty()) {
            throw new SecurityException("Method definitions are not allowed");
        }
    }

    private static List<MethodNode> filterMethods(ClassNode owner) {
        LinkedList<MethodNode> result = new LinkedList<MethodNode>();
        List<MethodNode> methods = owner.getMethods();
        for (MethodNode method : methods) {
            if (method.getDeclaringClass() != owner || method.isSynthetic() || "main".equals(method.getName()) || "run".equals(method.getName()) && owner.isScriptBody()) continue;
            result.add(method);
        }
        return result;
    }

    private void assertStarImportIsAllowed(String packageName) {
        if (this.starImportsWhitelist != null && !this.starImportsWhitelist.contains(packageName)) {
            throw new SecurityException("Importing [" + packageName + "] is not allowed");
        }
        if (this.starImportsBlacklist != null && this.starImportsBlacklist.contains(packageName)) {
            throw new SecurityException("Importing [" + packageName + "] is not allowed");
        }
    }

    private void assertImportIsAllowed(String className) {
        String packageName;
        ClassNode node;
        if (this.importsWhitelist != null && !this.importsWhitelist.contains(className)) {
            if (this.starImportsWhitelist != null) {
                node = ClassHelper.make(className);
                packageName = node.getPackageName();
                if (!this.starImportsWhitelist.contains(packageName + ".*")) {
                    throw new SecurityException("Importing [" + className + "] is not allowed");
                }
            } else {
                throw new SecurityException("Importing [" + className + "] is not allowed");
            }
        }
        if (this.importsBlacklist != null && this.importsBlacklist.contains(className)) {
            throw new SecurityException("Importing [" + className + "] is not allowed");
        }
        if (this.starImportsBlacklist != null && this.starImportsBlacklist.contains((packageName = (node = ClassHelper.make(className)).getPackageName()) + ".*")) {
            throw new SecurityException("Importing [" + className + "] is not allowed");
        }
    }

    private void assertStaticImportIsAllowed(String member, String className) {
        String fqn;
        String string = fqn = member.equals(className) ? member : className + "." + member;
        if (this.staticImportsWhitelist != null && !this.staticImportsWhitelist.contains(fqn)) {
            if (this.staticStarImportsWhitelist != null) {
                if (!this.staticStarImportsWhitelist.contains(className + ".*")) {
                    throw new SecurityException("Importing [" + fqn + "] is not allowed");
                }
            } else {
                throw new SecurityException("Importing [" + fqn + "] is not allowed");
            }
        }
        if (this.staticImportsBlacklist != null && this.staticImportsBlacklist.contains(fqn)) {
            throw new SecurityException("Importing [" + fqn + "] is not allowed");
        }
        if (this.staticStarImportsBlacklist != null && this.staticStarImportsBlacklist.contains(className + ".*")) {
            throw new SecurityException("Importing [" + fqn + "] is not allowed");
        }
    }

    public static interface StatementChecker {
        public boolean isAuthorized(Statement var1);
    }

    public static interface ExpressionChecker {
        public boolean isAuthorized(Expression var1);
    }

    private class SecuringCodeVisitor
    implements GroovyCodeVisitor {
        private SecuringCodeVisitor() {
        }

        private void assertStatementAuthorized(Statement statement) throws SecurityException {
            Class<?> clazz = statement.getClass();
            if (SecureASTCustomizer.this.statementsBlacklist != null && SecureASTCustomizer.this.statementsBlacklist.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed");
            }
            if (SecureASTCustomizer.this.statementsWhitelist != null && !SecureASTCustomizer.this.statementsWhitelist.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed");
            }
            for (StatementChecker statementChecker : SecureASTCustomizer.this.statementCheckers) {
                if (statementChecker.isAuthorized(statement)) continue;
                throw new SecurityException("Statement [" + clazz.getSimpleName() + "] is not allowed");
            }
        }

        private void assertExpressionAuthorized(Expression expression) throws SecurityException {
            Class<?> clazz = expression.getClass();
            if (SecureASTCustomizer.this.expressionsBlacklist != null && SecureASTCustomizer.this.expressionsBlacklist.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed: " + expression.getText());
            }
            if (SecureASTCustomizer.this.expressionsWhitelist != null && !SecureASTCustomizer.this.expressionsWhitelist.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed: " + expression.getText());
            }
            for (ExpressionChecker expressionChecker : SecureASTCustomizer.this.expressionCheckers) {
                if (expressionChecker.isAuthorized(expression)) continue;
                throw new SecurityException("Expression [" + clazz.getSimpleName() + "] is not allowed: " + expression.getText());
            }
            if (SecureASTCustomizer.this.isIndirectImportCheckEnabled) {
                try {
                    String typename;
                    Expression expr;
                    if (expression instanceof ConstructorCallExpression) {
                        SecureASTCustomizer.this.assertImportIsAllowed(expression.getType().getName());
                    } else if (expression instanceof MethodCallExpression) {
                        expr = (MethodCallExpression)expression;
                        ClassNode objectExpressionType = ((MethodCallExpression)expr).getObjectExpression().getType();
                        String typename2 = this.getExpressionType(objectExpressionType).getName();
                        SecureASTCustomizer.this.assertImportIsAllowed(typename2);
                        SecureASTCustomizer.this.assertStaticImportIsAllowed(((MethodCallExpression)expr).getMethodAsString(), typename2);
                    } else if (expression instanceof StaticMethodCallExpression) {
                        expr = (StaticMethodCallExpression)expression;
                        typename = ((StaticMethodCallExpression)expr).getOwnerType().getName();
                        SecureASTCustomizer.this.assertImportIsAllowed(typename);
                        SecureASTCustomizer.this.assertStaticImportIsAllowed(((StaticMethodCallExpression)expr).getMethod(), typename);
                    } else if (expression instanceof MethodPointerExpression) {
                        expr = (MethodPointerExpression)expression;
                        typename = ((MethodPointerExpression)expr).getType().getName();
                        SecureASTCustomizer.this.assertImportIsAllowed(typename);
                        SecureASTCustomizer.this.assertStaticImportIsAllowed(((MethodPointerExpression)expr).getText(), typename);
                    }
                }
                catch (SecurityException e) {
                    throw new SecurityException("Indirect import checks prevents usage of expression", e);
                }
            }
        }

        private ClassNode getExpressionType(ClassNode objectExpressionType) {
            return objectExpressionType.isArray() ? this.getExpressionType(objectExpressionType.getComponentType()) : objectExpressionType;
        }

        private void assertTokenAuthorized(Token token) throws SecurityException {
            int value = token.getType();
            if (SecureASTCustomizer.this.tokensBlacklist != null && SecureASTCustomizer.this.tokensBlacklist.contains(value)) {
                throw new SecurityException("Token " + token + " is not allowed");
            }
            if (SecureASTCustomizer.this.tokensWhitelist != null && !SecureASTCustomizer.this.tokensWhitelist.contains(value)) {
                throw new SecurityException("Token " + token + " is not allowed");
            }
        }

        @Override
        public void visitBlockStatement(BlockStatement block) {
            this.assertStatementAuthorized(block);
            for (Statement statement : block.getStatements()) {
                statement.visit(this);
            }
        }

        @Override
        public void visitForLoop(ForStatement forLoop) {
            this.assertStatementAuthorized(forLoop);
            forLoop.getCollectionExpression().visit(this);
            forLoop.getLoopBlock().visit(this);
        }

        @Override
        public void visitWhileLoop(WhileStatement loop) {
            this.assertStatementAuthorized(loop);
            loop.getBooleanExpression().visit(this);
            loop.getLoopBlock().visit(this);
        }

        @Override
        public void visitDoWhileLoop(DoWhileStatement loop) {
            this.assertStatementAuthorized(loop);
            loop.getBooleanExpression().visit(this);
            loop.getLoopBlock().visit(this);
        }

        @Override
        public void visitIfElse(IfStatement ifElse) {
            this.assertStatementAuthorized(ifElse);
            ifElse.getBooleanExpression().visit(this);
            ifElse.getIfBlock().visit(this);
            Statement elseBlock = ifElse.getElseBlock();
            if (elseBlock instanceof EmptyStatement) {
                this.visitEmptyStatement((EmptyStatement)elseBlock);
            } else {
                elseBlock.visit(this);
            }
        }

        @Override
        public void visitExpressionStatement(ExpressionStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
        }

        @Override
        public void visitReturnStatement(ReturnStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
        }

        @Override
        public void visitAssertStatement(AssertStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getBooleanExpression().visit(this);
            statement.getMessageExpression().visit(this);
        }

        @Override
        public void visitTryCatchFinally(TryCatchStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getTryStatement().visit(this);
            for (CatchStatement catchStatement : statement.getCatchStatements()) {
                catchStatement.visit(this);
            }
            Statement finallyStatement = statement.getFinallyStatement();
            if (finallyStatement instanceof EmptyStatement) {
                this.visitEmptyStatement((EmptyStatement)finallyStatement);
            } else {
                finallyStatement.visit(this);
            }
        }

        protected void visitEmptyStatement(EmptyStatement statement) {
        }

        @Override
        public void visitSwitch(SwitchStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
            for (CaseStatement caseStatement : statement.getCaseStatements()) {
                caseStatement.visit(this);
            }
            statement.getDefaultStatement().visit(this);
        }

        @Override
        public void visitCaseStatement(CaseStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
            statement.getCode().visit(this);
        }

        @Override
        public void visitBreakStatement(BreakStatement statement) {
            this.assertStatementAuthorized(statement);
        }

        @Override
        public void visitContinueStatement(ContinueStatement statement) {
            this.assertStatementAuthorized(statement);
        }

        @Override
        public void visitThrowStatement(ThrowStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
        }

        @Override
        public void visitSynchronizedStatement(SynchronizedStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
            statement.getCode().visit(this);
        }

        @Override
        public void visitCatchStatement(CatchStatement statement) {
            this.assertStatementAuthorized(statement);
            statement.getCode().visit(this);
        }

        @Override
        public void visitMethodCallExpression(MethodCallExpression call) {
            this.assertExpressionAuthorized(call);
            Expression receiver = call.getObjectExpression();
            String typeName = receiver.getType().getName();
            if (SecureASTCustomizer.this.receiversWhiteList != null && !SecureASTCustomizer.this.receiversWhiteList.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            }
            if (SecureASTCustomizer.this.receiversBlackList != null && SecureASTCustomizer.this.receiversBlackList.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            }
            receiver.visit(this);
            Expression method = call.getMethod();
            this.checkConstantTypeIfNotMethodNameOrProperty(method);
            call.getArguments().visit(this);
        }

        @Override
        public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
            this.assertExpressionAuthorized(call);
            String typeName = call.getOwnerType().getName();
            if (SecureASTCustomizer.this.receiversWhiteList != null && !SecureASTCustomizer.this.receiversWhiteList.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            }
            if (SecureASTCustomizer.this.receiversBlackList != null && SecureASTCustomizer.this.receiversBlackList.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            }
            call.getArguments().visit(this);
        }

        @Override
        public void visitConstructorCallExpression(ConstructorCallExpression call) {
            this.assertExpressionAuthorized(call);
            call.getArguments().visit(this);
        }

        @Override
        public void visitTernaryExpression(TernaryExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getBooleanExpression().visit(this);
            expression.getTrueExpression().visit(this);
            expression.getFalseExpression().visit(this);
        }

        @Override
        public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitTernaryExpression(expression);
        }

        @Override
        public void visitBinaryExpression(BinaryExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.assertTokenAuthorized(expression.getOperation());
            expression.getLeftExpression().visit(this);
            expression.getRightExpression().visit(this);
        }

        @Override
        public void visitPrefixExpression(PrefixExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.assertTokenAuthorized(expression.getOperation());
            expression.getExpression().visit(this);
        }

        @Override
        public void visitPostfixExpression(PostfixExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.assertTokenAuthorized(expression.getOperation());
            expression.getExpression().visit(this);
        }

        @Override
        public void visitBooleanExpression(BooleanExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitClosureExpression(ClosureExpression expression) {
            this.assertExpressionAuthorized(expression);
            if (!SecureASTCustomizer.this.isClosuresAllowed) {
                throw new SecurityException("Closures are not allowed");
            }
            expression.getCode().visit(this);
        }

        @Override
        public void visitTupleExpression(TupleExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitListOfExpressions(expression.getExpressions());
        }

        @Override
        public void visitMapExpression(MapExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitListOfExpressions(expression.getMapEntryExpressions());
        }

        @Override
        public void visitMapEntryExpression(MapEntryExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getKeyExpression().visit(this);
            expression.getValueExpression().visit(this);
        }

        @Override
        public void visitListExpression(ListExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitListOfExpressions(expression.getExpressions());
        }

        @Override
        public void visitRangeExpression(RangeExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getFrom().visit(this);
            expression.getTo().visit(this);
        }

        @Override
        public void visitPropertyExpression(PropertyExpression expression) {
            this.assertExpressionAuthorized(expression);
            Expression receiver = expression.getObjectExpression();
            String typeName = receiver.getType().getName();
            if (SecureASTCustomizer.this.receiversWhiteList != null && !SecureASTCustomizer.this.receiversWhiteList.contains(typeName)) {
                throw new SecurityException("Property access not allowed on [" + typeName + "]");
            }
            if (SecureASTCustomizer.this.receiversBlackList != null && SecureASTCustomizer.this.receiversBlackList.contains(typeName)) {
                throw new SecurityException("Property access not allowed on [" + typeName + "]");
            }
            receiver.visit(this);
            Expression property = expression.getProperty();
            this.checkConstantTypeIfNotMethodNameOrProperty(property);
        }

        private void checkConstantTypeIfNotMethodNameOrProperty(Expression expr) {
            if (expr instanceof ConstantExpression) {
                if (!"java.lang.String".equals(expr.getType().getName())) {
                    expr.visit(this);
                }
            } else {
                expr.visit(this);
            }
        }

        @Override
        public void visitAttributeExpression(AttributeExpression expression) {
            this.assertExpressionAuthorized(expression);
            Expression receiver = expression.getObjectExpression();
            String typeName = receiver.getType().getName();
            if (SecureASTCustomizer.this.receiversWhiteList != null && !SecureASTCustomizer.this.receiversWhiteList.contains(typeName)) {
                throw new SecurityException("Attribute access not allowed on [" + typeName + "]");
            }
            if (SecureASTCustomizer.this.receiversBlackList != null && SecureASTCustomizer.this.receiversBlackList.contains(typeName)) {
                throw new SecurityException("Attribute access not allowed on [" + typeName + "]");
            }
            receiver.visit(this);
            Expression property = expression.getProperty();
            this.checkConstantTypeIfNotMethodNameOrProperty(property);
        }

        @Override
        public void visitFieldExpression(FieldExpression expression) {
            this.assertExpressionAuthorized(expression);
        }

        @Override
        public void visitMethodPointerExpression(MethodPointerExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
            expression.getMethodName().visit(this);
        }

        @Override
        public void visitConstantExpression(ConstantExpression expression) {
            this.assertExpressionAuthorized(expression);
            String type = expression.getType().getName();
            if (SecureASTCustomizer.this.constantTypesWhiteList != null && !SecureASTCustomizer.this.constantTypesWhiteList.contains(type)) {
                throw new SecurityException("Constant expression type [" + type + "] is not allowed");
            }
            if (SecureASTCustomizer.this.constantTypesBlackList != null && SecureASTCustomizer.this.constantTypesBlackList.contains(type)) {
                throw new SecurityException("Constant expression type [" + type + "] is not allowed");
            }
        }

        @Override
        public void visitClassExpression(ClassExpression expression) {
            this.assertExpressionAuthorized(expression);
        }

        @Override
        public void visitVariableExpression(VariableExpression expression) {
            this.assertExpressionAuthorized(expression);
            String type = expression.getType().getName();
            if (SecureASTCustomizer.this.constantTypesWhiteList != null && !SecureASTCustomizer.this.constantTypesWhiteList.contains(type)) {
                throw new SecurityException("Usage of variables of type [" + type + "] is not allowed");
            }
            if (SecureASTCustomizer.this.constantTypesBlackList != null && SecureASTCustomizer.this.constantTypesBlackList.contains(type)) {
                throw new SecurityException("Usage of variables of type [" + type + "] is not allowed");
            }
        }

        @Override
        public void visitDeclarationExpression(DeclarationExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitBinaryExpression(expression);
        }

        protected void visitListOfExpressions(List<? extends Expression> list) {
            if (list == null) {
                return;
            }
            for (Expression expression : list) {
                if (expression instanceof SpreadExpression) {
                    Expression spread = ((SpreadExpression)expression).getExpression();
                    spread.visit(this);
                    continue;
                }
                expression.visit(this);
            }
        }

        @Override
        public void visitGStringExpression(GStringExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitListOfExpressions(expression.getStrings());
            this.visitListOfExpressions(expression.getValues());
        }

        @Override
        public void visitArrayExpression(ArrayExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitListOfExpressions(expression.getExpressions());
            this.visitListOfExpressions(expression.getSizeExpression());
        }

        @Override
        public void visitSpreadExpression(SpreadExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitSpreadMapExpression(SpreadMapExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitNotExpression(NotExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitCastExpression(CastExpression expression) {
            this.assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitArgumentlistExpression(ArgumentListExpression expression) {
            this.assertExpressionAuthorized(expression);
            this.visitTupleExpression(expression);
        }

        @Override
        public void visitClosureListExpression(ClosureListExpression closureListExpression) {
            this.assertExpressionAuthorized(closureListExpression);
            if (!SecureASTCustomizer.this.isClosuresAllowed) {
                throw new SecurityException("Closures are not allowed");
            }
            this.visitListOfExpressions(closureListExpression.getExpressions());
        }

        @Override
        public void visitBytecodeExpression(BytecodeExpression expression) {
            this.assertExpressionAuthorized(expression);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.gse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.tools.gse.StringSetMap;

public class DependencyTracker
extends ClassCodeVisitorSupport {
    private Set<String> current;
    private Map<String, ?> precompiledDependencies;
    private SourceUnit source;
    private StringSetMap cache;

    public DependencyTracker(SourceUnit source, StringSetMap cache) {
        this(source, cache, new HashMap());
    }

    public DependencyTracker(SourceUnit source, StringSetMap cache, Map<String, ?> precompiledEntries) {
        this.source = source;
        this.cache = cache;
        this.precompiledDependencies = precompiledEntries;
    }

    private void addToCache(ClassNode node) {
        if (node == null) {
            return;
        }
        String name = node.getName();
        if (!this.precompiledDependencies.containsKey(name) && !node.isPrimaryClassNode()) {
            return;
        }
        this.current.add(node.getName());
        this.addToCache(node.getSuperClass());
        this.addToCache(node.getInterfaces());
    }

    private void addToCache(ClassNode[] nodes) {
        if (nodes == null) {
            return;
        }
        for (ClassNode node : nodes) {
            this.addToCache(node);
        }
    }

    @Override
    public void visitClass(ClassNode node) {
        Set<String> old = this.current;
        this.current = this.cache.get(node.getName());
        this.addToCache(node);
        super.visitClass(node);
        this.current = old;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
        super.visitClassExpression(expression);
        this.addToCache(expression.getType());
    }

    @Override
    public void visitField(FieldNode node) {
        super.visitField(node);
        this.addToCache(node.getType());
    }

    @Override
    public void visitMethod(MethodNode node) {
        for (Parameter p : node.getParameters()) {
            this.addToCache(p.getType());
        }
        this.addToCache(node.getReturnType());
        this.addToCache(node.getExceptions());
        super.visitMethod(node);
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        super.visitArrayExpression(expression);
        this.addToCache(expression.getType());
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression);
        this.addToCache(expression.getType());
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        super.visitVariableExpression(expression);
        this.addToCache(expression.getType());
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        super.visitCatchStatement(statement);
        this.addToCache(statement.getVariable().getType());
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        super.visitAnnotations(node);
        for (AnnotationNode an : node.getAnnotations()) {
            this.addToCache(an.getClassNode());
        }
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call);
        this.addToCache(call.getType());
    }
}


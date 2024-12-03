/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Variable;

public class VariableScope {
    private Map<String, Variable> declaredVariables = Collections.emptyMap();
    private Map<String, Variable> referencedLocalVariables = Collections.emptyMap();
    private Map<String, Variable> referencedClassVariables = Collections.emptyMap();
    private boolean inStaticContext = false;
    private boolean resolvesDynamic = false;
    private ClassNode clazzScope;
    private VariableScope parent;

    public VariableScope() {
    }

    public VariableScope(VariableScope parent) {
        this.parent = parent;
    }

    public Variable getDeclaredVariable(String name) {
        return this.declaredVariables.get(name);
    }

    public boolean isReferencedLocalVariable(String name) {
        return this.referencedLocalVariables.containsKey(name);
    }

    public boolean isReferencedClassVariable(String name) {
        return this.referencedClassVariables.containsKey(name);
    }

    public VariableScope getParent() {
        return this.parent;
    }

    public boolean isInStaticContext() {
        return this.inStaticContext;
    }

    public void setInStaticContext(boolean inStaticContext) {
        this.inStaticContext = inStaticContext;
    }

    public void setClassScope(ClassNode node) {
        this.clazzScope = node;
    }

    public ClassNode getClassScope() {
        return this.clazzScope;
    }

    public boolean isClassScope() {
        return this.clazzScope != null;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public VariableScope copy() {
        VariableScope copy = new VariableScope();
        copy.clazzScope = this.clazzScope;
        if (!this.declaredVariables.isEmpty()) {
            copy.declaredVariables = new LinkedHashMap<String, Variable>(this.declaredVariables);
        }
        copy.inStaticContext = this.inStaticContext;
        copy.parent = this.parent;
        if (!this.referencedClassVariables.isEmpty()) {
            copy.referencedClassVariables = new LinkedHashMap<String, Variable>(this.referencedClassVariables);
        }
        if (!this.referencedLocalVariables.isEmpty()) {
            copy.referencedLocalVariables = new LinkedHashMap<String, Variable>(this.referencedLocalVariables);
        }
        copy.resolvesDynamic = this.resolvesDynamic;
        return copy;
    }

    public void putDeclaredVariable(Variable var) {
        if (this.declaredVariables == Collections.EMPTY_MAP) {
            this.declaredVariables = new LinkedHashMap<String, Variable>();
        }
        this.declaredVariables.put(var.getName(), var);
    }

    public Iterator<Variable> getReferencedLocalVariablesIterator() {
        return this.referencedLocalVariables.values().iterator();
    }

    public int getReferencedLocalVariablesCount() {
        return this.referencedLocalVariables.size();
    }

    public Variable getReferencedLocalVariable(String name) {
        return this.referencedLocalVariables.get(name);
    }

    public void putReferencedLocalVariable(Variable var) {
        if (this.referencedLocalVariables == Collections.EMPTY_MAP) {
            this.referencedLocalVariables = new LinkedHashMap<String, Variable>();
        }
        this.referencedLocalVariables.put(var.getName(), var);
    }

    public void putReferencedClassVariable(Variable var) {
        if (this.referencedClassVariables == Collections.EMPTY_MAP) {
            this.referencedClassVariables = new LinkedHashMap<String, Variable>();
        }
        this.referencedClassVariables.put(var.getName(), var);
    }

    public Variable getReferencedClassVariable(String name) {
        return this.referencedClassVariables.get(name);
    }

    public Object removeReferencedClassVariable(String name) {
        if (this.referencedClassVariables == Collections.EMPTY_MAP) {
            return null;
        }
        return this.referencedClassVariables.remove(name);
    }

    public Map<String, Variable> getReferencedClassVariables() {
        if (this.referencedClassVariables == Collections.EMPTY_MAP) {
            return this.referencedClassVariables;
        }
        return Collections.unmodifiableMap(this.referencedClassVariables);
    }

    public Iterator<Variable> getReferencedClassVariablesIterator() {
        return this.getReferencedClassVariables().values().iterator();
    }

    public Map<String, Variable> getDeclaredVariables() {
        if (this.declaredVariables == Collections.EMPTY_MAP) {
            return this.declaredVariables;
        }
        return Collections.unmodifiableMap(this.declaredVariables);
    }

    public Iterator<Variable> getDeclaredVariablesIterator() {
        return this.getDeclaredVariables().values().iterator();
    }
}


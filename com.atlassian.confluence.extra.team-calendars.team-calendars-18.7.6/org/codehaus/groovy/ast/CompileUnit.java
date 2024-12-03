/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovy.lang.GroovyClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;

public class CompileUnit {
    private final List<ModuleNode> modules = new ArrayList<ModuleNode>();
    private Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
    private CompilerConfiguration config;
    private GroovyClassLoader classLoader;
    private CodeSource codeSource;
    private Map<String, ClassNode> classesToCompile = new HashMap<String, ClassNode>();
    private Map<String, SourceUnit> classNameToSource = new HashMap<String, SourceUnit>();
    private Map<String, InnerClassNode> generatedInnerClasses = new HashMap<String, InnerClassNode>();

    public CompileUnit(GroovyClassLoader classLoader, CompilerConfiguration config) {
        this(classLoader, null, config);
    }

    public CompileUnit(GroovyClassLoader classLoader, CodeSource codeSource, CompilerConfiguration config) {
        this.classLoader = classLoader;
        this.config = config;
        this.codeSource = codeSource;
    }

    public List<ModuleNode> getModules() {
        return this.modules;
    }

    public void addModule(ModuleNode node) {
        if (node == null) {
            return;
        }
        this.modules.add(node);
        node.setUnit(this);
        this.addClasses(node.getClasses());
    }

    public ClassNode getClass(String name) {
        ClassNode cn = this.classes.get(name);
        if (cn != null) {
            return cn;
        }
        return this.classesToCompile.get(name);
    }

    public List getClasses() {
        ArrayList<ClassNode> answer = new ArrayList<ClassNode>();
        for (ModuleNode module : this.modules) {
            answer.addAll(module.getClasses());
        }
        return answer;
    }

    public CompilerConfiguration getConfig() {
        return this.config;
    }

    public GroovyClassLoader getClassLoader() {
        return this.classLoader;
    }

    public CodeSource getCodeSource() {
        return this.codeSource;
    }

    void addClasses(List<ClassNode> classList) {
        for (ClassNode node : classList) {
            this.addClass(node);
        }
    }

    public void addClass(ClassNode node) {
        String name = (node = node.redirect()).getName();
        ClassNode stored = this.classes.get(name);
        if (stored != null && stored != node) {
            SourceUnit nodeSource = node.getModule().getContext();
            SourceUnit storedSource = stored.getModule().getContext();
            String txt = "Invalid duplicate class definition of class " + node.getName() + " : ";
            if (nodeSource == storedSource) {
                txt = txt + "The source " + nodeSource.getName() + " contains at least two definitions of the class " + node.getName() + ".\n";
                if (node.isScriptBody() || stored.isScriptBody()) {
                    txt = txt + "One of the classes is an explicit generated class using the class statement, the other is a class generated from the script body based on the file name. Solutions are to change the file name or to change the class name.\n";
                }
            } else {
                txt = txt + "The sources " + nodeSource.getName() + " and " + storedSource.getName() + " each contain a class with the name " + node.getName() + ".\n";
            }
            nodeSource.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException(txt, node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), nodeSource));
        }
        this.classes.put(name, node);
        if (this.classesToCompile.containsKey(name)) {
            ClassNode cn = this.classesToCompile.get(name);
            cn.setRedirect(node);
            this.classesToCompile.remove(name);
        }
    }

    public void addClassNodeToCompile(ClassNode node, SourceUnit location) {
        this.classesToCompile.put(node.getName(), node);
        this.classNameToSource.put(node.getName(), location);
    }

    public SourceUnit getScriptSourceLocation(String className) {
        return this.classNameToSource.get(className);
    }

    public boolean hasClassNodeToCompile() {
        return !this.classesToCompile.isEmpty();
    }

    public Iterator<String> iterateClassNodeToCompile() {
        return this.classesToCompile.keySet().iterator();
    }

    public InnerClassNode getGeneratedInnerClass(String name) {
        return this.generatedInnerClasses.get(name);
    }

    public void addGeneratedInnerClass(InnerClassNode icn) {
        this.generatedInnerClasses.put(icn.getName(), icn);
    }

    public Map<String, InnerClassNode> getGeneratedInnerClasses() {
        return Collections.unmodifiableMap(this.generatedInnerClasses);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.control.SourceUnit;

public class ASTHelper {
    private SourceUnit controller;
    private ClassLoader classLoader;
    protected ModuleNode output;
    private String packageName;
    protected static Map resolutions = new HashMap();

    public ASTHelper(SourceUnit controller, ClassLoader classLoader) {
        this();
        this.controller = controller;
        this.classLoader = classLoader;
    }

    public ASTHelper() {
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.setPackage(packageName, new ArrayList<AnnotationNode>());
    }

    public PackageNode setPackage(String packageName, List<AnnotationNode> annotations) {
        this.packageName = packageName;
        if (packageName != null && packageName.length() > 0) {
            packageName = packageName + '.';
        }
        PackageNode packageNode = new PackageNode(packageName);
        packageNode.addAnnotations(annotations);
        this.output.setPackage(packageNode);
        return packageNode;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public SourceUnit getController() {
        return this.controller;
    }

    public void setController(SourceUnit controller) {
        this.controller = controller;
    }

    public static String dot(String base, String name) {
        if (base != null && base.length() > 0) {
            return base + "." + name;
        }
        return name;
    }

    protected void makeModule() {
        this.output = new ModuleNode(this.controller);
        resolutions.clear();
    }

    protected String dot(String base) {
        return ASTHelper.dot(base, "");
    }

    protected void addImport(ClassNode type, String name, String aliasName) {
        this.addImport(type, name, aliasName, new ArrayList<AnnotationNode>());
    }

    protected void addImport(ClassNode type, String name, String aliasName, List<AnnotationNode> annotations) {
        if (aliasName == null) {
            aliasName = name;
        }
        this.output.addImport(aliasName, type, annotations);
    }

    protected void addStaticImport(ClassNode type, String name, String alias) {
        this.addStaticImport(type, name, alias, new ArrayList<AnnotationNode>());
    }

    protected void addStaticImport(ClassNode type, String name, String alias, List<AnnotationNode> annotations) {
        if (alias == null) {
            alias = name;
        }
        this.output.addStaticImport(type, name, alias, annotations);
    }

    protected void addStaticStarImport(ClassNode type, String importClass) {
        this.addStaticStarImport(type, importClass, new ArrayList<AnnotationNode>());
    }

    protected void addStaticStarImport(ClassNode type, String importClass, List<AnnotationNode> annotations) {
        this.output.addStaticStarImport(importClass, type, annotations);
    }

    protected void addStarImport(String importPackage) {
        this.addStarImport(importPackage, new ArrayList<AnnotationNode>());
    }

    protected void addStarImport(String importPackage, List<AnnotationNode> annotations) {
        this.output.addStarImport(this.dot(importPackage), annotations);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovy.lang.Binding;
import groovyjarjarasm.asm.Opcodes;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.transform.BaseScriptASTTransformation;

public class ModuleNode
extends ASTNode
implements Opcodes {
    private BlockStatement statementBlock = new BlockStatement();
    List<ClassNode> classes = new LinkedList<ClassNode>();
    private List<MethodNode> methods = new ArrayList<MethodNode>();
    private Map<String, ImportNode> imports = new HashMap<String, ImportNode>();
    private List<ImportNode> starImports = new ArrayList<ImportNode>();
    private Map<String, ImportNode> staticImports = new LinkedHashMap<String, ImportNode>();
    private Map<String, ImportNode> staticStarImports = new LinkedHashMap<String, ImportNode>();
    private CompileUnit unit;
    private PackageNode packageNode;
    private String description;
    private boolean createClassForStatements = true;
    private transient SourceUnit context;
    private boolean importsResolved = false;
    private ClassNode scriptDummy;
    private String mainClassName = null;
    private final Parameter[] SCRIPT_CONTEXT_CTOR = new Parameter[]{new Parameter(ClassHelper.BINDING_TYPE, "context")};

    public ModuleNode(SourceUnit context) {
        this.context = context;
    }

    public ModuleNode(CompileUnit unit) {
        this.unit = unit;
    }

    public BlockStatement getStatementBlock() {
        return this.statementBlock;
    }

    public List<MethodNode> getMethods() {
        return this.methods;
    }

    public List<ClassNode> getClasses() {
        if (this.createClassForStatements && (!this.statementBlock.isEmpty() || !this.methods.isEmpty() || this.isPackageInfo())) {
            ClassNode mainClass = this.createStatementsClass();
            this.mainClassName = mainClass.getName();
            this.createClassForStatements = false;
            this.classes.add(0, mainClass);
            mainClass.setModule(this);
            this.addToCompileUnit(mainClass);
        }
        return this.classes;
    }

    private boolean isPackageInfo() {
        return this.context != null && this.context.getName() != null && this.context.getName().endsWith("package-info.groovy");
    }

    public List<ImportNode> getImports() {
        return new ArrayList<ImportNode>(this.imports.values());
    }

    public List<ImportNode> getStarImports() {
        return this.starImports;
    }

    public ClassNode getImportType(String alias) {
        ImportNode importNode = this.imports.get(alias);
        return importNode == null ? null : importNode.getType();
    }

    public ImportNode getImport(String alias) {
        return this.imports.get(alias);
    }

    public void addImport(String alias, ClassNode type) {
        this.addImport(alias, type, new ArrayList<AnnotationNode>());
    }

    public void addImport(String alias, ClassNode type, List<AnnotationNode> annotations) {
        ImportNode importNode = new ImportNode(type, alias);
        this.imports.put(alias, importNode);
        importNode.addAnnotations(annotations);
        this.storeLastAddedImportNode(importNode);
    }

    public void addStarImport(String packageName) {
        this.addStarImport(packageName, new ArrayList<AnnotationNode>());
    }

    public void addStarImport(String packageName, List<AnnotationNode> annotations) {
        ImportNode importNode = new ImportNode(packageName);
        importNode.addAnnotations(annotations);
        this.starImports.add(importNode);
        this.storeLastAddedImportNode(importNode);
    }

    public void addStatement(Statement node) {
        this.statementBlock.addStatement(node);
    }

    public void addClass(ClassNode node) {
        if (this.classes.isEmpty()) {
            this.mainClassName = node.getName();
        }
        this.classes.add(node);
        node.setModule(this);
        this.addToCompileUnit(node);
    }

    private void addToCompileUnit(ClassNode node) {
        if (this.unit != null) {
            this.unit.addClass(node);
        }
    }

    public void addMethod(MethodNode node) {
        this.methods.add(node);
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
    }

    public String getPackageName() {
        return this.packageNode == null ? null : this.packageNode.getName();
    }

    public PackageNode getPackage() {
        return this.packageNode;
    }

    public void setPackage(PackageNode packageNode) {
        this.packageNode = packageNode;
    }

    public void setPackageName(String packageName) {
        this.packageNode = new PackageNode(packageName);
    }

    public boolean hasPackageName() {
        return this.packageNode != null && this.packageNode.getName() != null;
    }

    public boolean hasPackage() {
        return this.packageNode != null;
    }

    public SourceUnit getContext() {
        return this.context;
    }

    public String getDescription() {
        if (this.context != null) {
            return this.context.getName();
        }
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompileUnit getUnit() {
        return this.unit;
    }

    void setUnit(CompileUnit unit) {
        this.unit = unit;
    }

    public ClassNode getScriptClassDummy() {
        ClassNode classNode;
        if (this.scriptDummy != null) {
            this.setScriptBaseClassFromConfig(this.scriptDummy);
            return this.scriptDummy;
        }
        String name = this.getPackageName();
        if (name == null) {
            name = "";
        }
        if (this.getDescription() == null) {
            throw new RuntimeException("Cannot generate main(String[]) class for statements when we have no file description");
        }
        name = name + GeneratorContext.encodeAsValidClassName(this.extractClassFromFileDescription());
        if (this.isPackageInfo()) {
            classNode = new ClassNode(name, 1536, ClassHelper.OBJECT_TYPE);
        } else {
            classNode = new ClassNode(name, 1, ClassHelper.SCRIPT_TYPE);
            this.setScriptBaseClassFromConfig(classNode);
            classNode.setScript(true);
            classNode.setScriptBody(true);
        }
        this.scriptDummy = classNode;
        return classNode;
    }

    private void setScriptBaseClassFromConfig(ClassNode cn) {
        String baseClassName = null;
        if (this.unit != null) {
            baseClassName = this.unit.getConfig().getScriptBaseClass();
        } else if (this.context != null) {
            baseClassName = this.context.getConfiguration().getScriptBaseClass();
        }
        if (baseClassName != null && !cn.getSuperClass().getName().equals(baseClassName)) {
            cn.setSuperClass(ClassHelper.make(baseClassName));
            AnnotationNode annotationNode = new AnnotationNode(BaseScriptASTTransformation.MY_TYPE);
            cn.addAnnotation(annotationNode);
        }
    }

    protected ClassNode createStatementsClass() {
        ClassNode classNode = this.getScriptClassDummy();
        if (classNode.getName().endsWith("package-info")) {
            return classNode;
        }
        this.handleMainMethodIfPresent(this.methods);
        classNode.addMethod(new MethodNode("main", 9, ClassHelper.VOID_TYPE, new Parameter[]{new Parameter(ClassHelper.STRING_TYPE.makeArray(), "args")}, ClassNode.EMPTY_ARRAY, new ExpressionStatement(new MethodCallExpression((Expression)new ClassExpression(ClassHelper.make(InvokerHelper.class)), "runScript", (Expression)new ArgumentListExpression(new ClassExpression(classNode), new VariableExpression("args"))))));
        MethodNode methodNode = new MethodNode("run", 1, ClassHelper.OBJECT_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, this.statementBlock);
        methodNode.setIsScriptBody();
        classNode.addMethod(methodNode);
        classNode.addConstructor(1, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, new BlockStatement());
        ExpressionStatement stmt = classNode.getSuperClass().getDeclaredConstructor(this.SCRIPT_CONTEXT_CTOR) != null ? new ExpressionStatement(new ConstructorCallExpression(ClassNode.SUPER, new ArgumentListExpression(new VariableExpression("context")))) : new ExpressionStatement(new MethodCallExpression((Expression)new VariableExpression("super"), "setBinding", (Expression)new ArgumentListExpression(new VariableExpression("context"))));
        classNode.addConstructor(1, new Parameter[]{new Parameter(ClassHelper.make(Binding.class), "context")}, ClassNode.EMPTY_ARRAY, stmt);
        for (MethodNode node : this.methods) {
            int modifiers = node.getModifiers();
            if ((modifiers & 0x400) != 0) {
                throw new RuntimeException("Cannot use abstract methods in a script, they are only available inside classes. Method: " + node.getName());
            }
            node.setModifiers(modifiers);
            classNode.addMethod(node);
        }
        return classNode;
    }

    private void handleMainMethodIfPresent(List methods) {
        boolean found = false;
        Iterator iter = methods.iterator();
        while (iter.hasNext()) {
            MethodNode node = (MethodNode)iter.next();
            if (!node.getName().equals("main") || !node.isStatic() || node.getParameters().length != 1) continue;
            ClassNode argType = node.getParameters()[0].getType();
            ClassNode retType = node.getReturnType();
            boolean argTypeMatches = argType.equals(ClassHelper.OBJECT_TYPE) || argType.getName().contains("String[]");
            boolean retTypeMatches = retType == ClassHelper.VOID_TYPE || retType == ClassHelper.OBJECT_TYPE;
            if (!retTypeMatches || !argTypeMatches) continue;
            if (found) {
                throw new RuntimeException("Repetitive main method found.");
            }
            found = true;
            if (this.statementBlock.isEmpty()) {
                this.addStatement(node.getCode());
            }
            iter.remove();
        }
    }

    protected String extractClassFromFileDescription() {
        String answer = this.getDescription();
        try {
            URI uri = new URI(answer);
            String path = uri.getPath();
            String schemeSpecific = uri.getSchemeSpecificPart();
            if (path != null) {
                answer = path;
            } else if (schemeSpecific != null) {
                answer = schemeSpecific;
            }
        }
        catch (URISyntaxException uri) {
            // empty catch block
        }
        int slashIdx = answer.lastIndexOf(47);
        int separatorIdx = answer.lastIndexOf(File.separatorChar);
        int dotIdx = answer.lastIndexOf(46);
        if (dotIdx > 0 && dotIdx > Math.max(slashIdx, separatorIdx)) {
            answer = answer.substring(0, dotIdx);
        }
        if (slashIdx >= 0) {
            answer = answer.substring(slashIdx + 1);
        }
        if ((separatorIdx = answer.lastIndexOf(File.separatorChar)) >= 0) {
            answer = answer.substring(separatorIdx + 1);
        }
        return answer;
    }

    public boolean isEmpty() {
        return this.classes.isEmpty() && this.statementBlock.getStatements().isEmpty();
    }

    public void sortClasses() {
        if (this.isEmpty()) {
            return;
        }
        List<ClassNode> classes = this.getClasses();
        LinkedList<ClassNode> sorted = new LinkedList<ClassNode>();
        int level = 1;
        while (!classes.isEmpty()) {
            Iterator<ClassNode> cni = classes.iterator();
            while (cni.hasNext()) {
                ClassNode cn;
                ClassNode sn = cn = cni.next();
                for (int i = 0; sn != null && i < level; sn = sn.getSuperClass(), ++i) {
                }
                if (sn != null && sn.isPrimaryClassNode()) continue;
                cni.remove();
                sorted.addLast(cn);
            }
            ++level;
        }
        this.classes = sorted;
    }

    public boolean hasImportsResolved() {
        return this.importsResolved;
    }

    public void setImportsResolved(boolean importsResolved) {
        this.importsResolved = importsResolved;
    }

    public Map<String, ImportNode> getStaticImports() {
        return this.staticImports;
    }

    public Map<String, ImportNode> getStaticStarImports() {
        return this.staticStarImports;
    }

    public void addStaticImport(ClassNode type, String fieldName, String alias) {
        this.addStaticImport(type, fieldName, alias, new ArrayList<AnnotationNode>());
    }

    public void addStaticImport(ClassNode type, String fieldName, String alias, List<AnnotationNode> annotations) {
        ImportNode node = new ImportNode(type, fieldName, alias);
        node.addAnnotations(annotations);
        this.staticImports.put(alias, node);
        this.storeLastAddedImportNode(node);
    }

    public void addStaticStarImport(String name, ClassNode type) {
        this.addStaticStarImport(name, type, new ArrayList<AnnotationNode>());
    }

    public void addStaticStarImport(String name, ClassNode type, List<AnnotationNode> annotations) {
        ImportNode node = new ImportNode(type);
        node.addAnnotations(annotations);
        this.staticStarImports.put(name, node);
        this.storeLastAddedImportNode(node);
    }

    private void storeLastAddedImportNode(ImportNode node) {
        if (this.getNodeMetaData(ImportNode.class) == ImportNode.class) {
            this.putNodeMetaData(ImportNode.class, node);
        }
    }

    public String getMainClassName() {
        return this.mainClassName;
    }
}


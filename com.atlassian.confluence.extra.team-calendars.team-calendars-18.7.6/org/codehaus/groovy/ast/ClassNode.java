/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovyjarjarasm.asm.Opcodes;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.MixinNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.ClassNodeUtils;
import org.codehaus.groovy.ast.tools.ParameterUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public class ClassNode
extends AnnotatedNode
implements Opcodes {
    public static final ClassNode[] EMPTY_ARRAY = new ClassNode[0];
    public static final ClassNode THIS = new ClassNode(Object.class);
    public static final ClassNode SUPER = new ClassNode(Object.class);
    private String name;
    private int modifiers;
    private boolean syntheticPublic;
    private ClassNode[] interfaces;
    private MixinNode[] mixins;
    private List<ConstructorNode> constructors;
    private List<Statement> objectInitializers;
    private MapOfLists methods;
    private List<MethodNode> methodsList;
    private LinkedList<FieldNode> fields;
    private List<PropertyNode> properties;
    private Map<String, FieldNode> fieldIndex;
    private ModuleNode module;
    private CompileUnit compileUnit;
    private boolean staticClass = false;
    private boolean scriptBody = false;
    private boolean script;
    private ClassNode superClass;
    protected boolean isPrimaryNode;
    protected List<InnerClassNode> innerClasses;
    private Map<CompilePhase, Map<Class<? extends ASTTransformation>, Set<ASTNode>>> transformInstances;
    protected Object lazyInitLock = new Object();
    protected Class clazz;
    private boolean lazyInitDone = true;
    private ClassNode componentType = null;
    private ClassNode redirect = null;
    private boolean annotated;
    private GenericsType[] genericsTypes = null;
    private boolean usesGenerics = false;
    private boolean placeholder;
    private MethodNode enclosingMethod = null;

    public ClassNode redirect() {
        if (this.redirect == null) {
            return this;
        }
        return this.redirect.redirect();
    }

    public void setRedirect(ClassNode cn) {
        if (this.isPrimaryNode) {
            throw new GroovyBugError("tried to set a redirect for a primary ClassNode (" + this.getName() + "->" + cn.getName() + ").");
        }
        if (cn != null) {
            cn = cn.redirect();
        }
        if (cn == this) {
            return;
        }
        this.redirect = cn;
    }

    public ClassNode makeArray() {
        ClassNode cn;
        if (this.redirect != null) {
            ClassNode res = this.redirect().makeArray();
            res.componentType = this;
            return res;
        }
        if (this.clazz != null) {
            Class<?> ret = Array.newInstance(this.clazz, 0).getClass();
            cn = new ClassNode(ret, this);
        } else {
            cn = new ClassNode(this);
        }
        return cn;
    }

    public boolean isPrimaryClassNode() {
        return this.redirect().isPrimaryNode || this.componentType != null && this.componentType.isPrimaryClassNode();
    }

    private ClassNode(ClassNode componentType) {
        this(componentType.getName() + "[]", 1, ClassHelper.OBJECT_TYPE);
        this.componentType = componentType.redirect();
        this.isPrimaryNode = false;
    }

    private ClassNode(Class c, ClassNode componentType) {
        this(c);
        this.componentType = componentType;
        this.isPrimaryNode = false;
    }

    public ClassNode(Class c) {
        this(c.getName(), c.getModifiers(), null, null, MixinNode.EMPTY_ARRAY);
        this.clazz = c;
        this.lazyInitDone = false;
        CompileUnit cu = this.getCompileUnit();
        if (cu != null) {
            cu.addClass(this);
        }
        this.isPrimaryNode = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void lazyClassInit() {
        Object object = this.lazyInitLock;
        synchronized (object) {
            if (this.redirect != null) {
                throw new GroovyBugError("lazyClassInit called on a proxy ClassNode, that must not happen.A redirect() call is missing somewhere!");
            }
            if (this.lazyInitDone) {
                return;
            }
            VMPluginFactory.getPlugin().configureClassNode(this.compileUnit, this);
            this.lazyInitDone = true;
        }
    }

    public MethodNode getEnclosingMethod() {
        return this.redirect().enclosingMethod;
    }

    public void setEnclosingMethod(MethodNode enclosingMethod) {
        this.redirect().enclosingMethod = enclosingMethod;
    }

    public boolean isSyntheticPublic() {
        return this.syntheticPublic;
    }

    public void setSyntheticPublic(boolean syntheticPublic) {
        this.syntheticPublic = syntheticPublic;
    }

    public ClassNode(String name, int modifiers, ClassNode superClass) {
        this(name, modifiers, superClass, EMPTY_ARRAY, MixinNode.EMPTY_ARRAY);
    }

    public ClassNode(String name, int modifiers, ClassNode superClass, ClassNode[] interfaces, MixinNode[] mixins) {
        this.name = name;
        this.modifiers = modifiers;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.mixins = mixins;
        this.isPrimaryNode = true;
        if (superClass != null) {
            this.usesGenerics = superClass.isUsingGenerics();
        }
        if (!this.usesGenerics && interfaces != null) {
            for (ClassNode anInterface : interfaces) {
                boolean bl = this.usesGenerics = this.usesGenerics || anInterface.isUsingGenerics();
                if (this.usesGenerics) break;
            }
        }
        this.methods = new MapOfLists();
        this.methodsList = new ArrayList<MethodNode>();
    }

    public void setSuperClass(ClassNode superClass) {
        this.redirect().superClass = superClass;
    }

    public List<FieldNode> getFields() {
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        if (this.redirect != null) {
            return this.redirect().getFields();
        }
        if (this.fields == null) {
            this.fields = new LinkedList();
        }
        return this.fields;
    }

    public ClassNode[] getInterfaces() {
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        if (this.redirect != null) {
            return this.redirect().getInterfaces();
        }
        return this.interfaces;
    }

    public void setInterfaces(ClassNode[] interfaces) {
        if (this.redirect != null) {
            this.redirect().setInterfaces(interfaces);
        } else {
            this.interfaces = interfaces;
        }
    }

    public MixinNode[] getMixins() {
        return this.redirect().mixins;
    }

    public List<MethodNode> getMethods() {
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        if (this.redirect != null) {
            return this.redirect().getMethods();
        }
        return this.methodsList;
    }

    public List<MethodNode> getAbstractMethods() {
        ArrayList<MethodNode> result = new ArrayList<MethodNode>(3);
        for (MethodNode method : this.getDeclaredMethodsMap().values()) {
            if (!method.isAbstract()) continue;
            result.add(method);
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    public List<MethodNode> getAllDeclaredMethods() {
        return new ArrayList<MethodNode>(this.getDeclaredMethodsMap().values());
    }

    public Set<ClassNode> getAllInterfaces() {
        LinkedHashSet<ClassNode> res = new LinkedHashSet<ClassNode>();
        this.getAllInterfaces(res);
        return res;
    }

    private void getAllInterfaces(Set<ClassNode> res) {
        if (this.isInterface()) {
            res.add(this);
        }
        for (ClassNode anInterface : this.getInterfaces()) {
            res.add(anInterface);
            anInterface.getAllInterfaces(res);
        }
    }

    public Map<String, MethodNode> getDeclaredMethodsMap() {
        ClassNode parent = this.getSuperClass();
        Map<String, MethodNode> result = parent != null ? parent.getDeclaredMethodsMap() : new LinkedHashMap<String, MethodNode>();
        ClassNodeUtils.addInterfaceMethods(this, result);
        for (MethodNode method : this.getMethods()) {
            String sig = method.getTypeDescriptor();
            result.put(sig, method);
        }
        return result;
    }

    public String getName() {
        return this.redirect().name;
    }

    public String getUnresolvedName() {
        return this.name;
    }

    public String setName(String name) {
        this.redirect().name = name;
        return this.redirect().name;
    }

    public int getModifiers() {
        return this.redirect().modifiers;
    }

    public void setModifiers(int modifiers) {
        this.redirect().modifiers = modifiers;
    }

    public List<PropertyNode> getProperties() {
        ClassNode r = this.redirect();
        if (r.properties == null) {
            r.properties = new ArrayList<PropertyNode>();
        }
        return r.properties;
    }

    public List<ConstructorNode> getDeclaredConstructors() {
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        ClassNode r = this.redirect();
        if (r.constructors == null) {
            r.constructors = new ArrayList<ConstructorNode>();
        }
        return r.constructors;
    }

    public ConstructorNode getDeclaredConstructor(Parameter[] parameters) {
        for (ConstructorNode method : this.getDeclaredConstructors()) {
            if (!this.parametersEqual(method.getParameters(), parameters)) continue;
            return method;
        }
        return null;
    }

    public void removeConstructor(ConstructorNode node) {
        this.redirect().constructors.remove(node);
    }

    public ModuleNode getModule() {
        return this.redirect().module;
    }

    public PackageNode getPackage() {
        return this.getModule() == null ? null : this.getModule().getPackage();
    }

    public void setModule(ModuleNode module) {
        this.redirect().module = module;
        if (module != null) {
            this.redirect().compileUnit = module.getUnit();
        }
    }

    public void addField(FieldNode node) {
        ClassNode r = this.redirect();
        node.setDeclaringClass(r);
        node.setOwner(r);
        if (r.fields == null) {
            r.fields = new LinkedList();
        }
        if (r.fieldIndex == null) {
            r.fieldIndex = new LinkedHashMap<String, FieldNode>();
        }
        r.fields.add(node);
        r.fieldIndex.put(node.getName(), node);
    }

    public void addFieldFirst(FieldNode node) {
        ClassNode r = this.redirect();
        node.setDeclaringClass(r);
        node.setOwner(r);
        if (r.fields == null) {
            r.fields = new LinkedList();
        }
        if (r.fieldIndex == null) {
            r.fieldIndex = new LinkedHashMap<String, FieldNode>();
        }
        r.fields.addFirst(node);
        r.fieldIndex.put(node.getName(), node);
    }

    public void addProperty(PropertyNode node) {
        node.setDeclaringClass(this.redirect());
        FieldNode field = node.getField();
        this.addField(field);
        ClassNode r = this.redirect();
        if (r.properties == null) {
            r.properties = new ArrayList<PropertyNode>();
        }
        r.properties.add(node);
    }

    public PropertyNode addProperty(String name, int modifiers, ClassNode type, Expression initialValueExpression, Statement getterBlock, Statement setterBlock) {
        for (PropertyNode pn : this.getProperties()) {
            if (!pn.getName().equals(name)) continue;
            if (pn.getInitialExpression() == null && initialValueExpression != null) {
                pn.getField().setInitialValueExpression(initialValueExpression);
            }
            if (pn.getGetterBlock() == null && getterBlock != null) {
                pn.setGetterBlock(getterBlock);
            }
            if (pn.getSetterBlock() == null && setterBlock != null) {
                pn.setSetterBlock(setterBlock);
            }
            return pn;
        }
        PropertyNode node = new PropertyNode(name, modifiers, type, this.redirect(), initialValueExpression, getterBlock, setterBlock);
        this.addProperty(node);
        return node;
    }

    public boolean hasProperty(String name) {
        return this.getProperty(name) != null;
    }

    public PropertyNode getProperty(String name) {
        for (PropertyNode pn : this.getProperties()) {
            if (!pn.getName().equals(name)) continue;
            return pn;
        }
        return null;
    }

    public void addConstructor(ConstructorNode node) {
        node.setDeclaringClass(this);
        ClassNode r = this.redirect();
        if (r.constructors == null) {
            r.constructors = new ArrayList<ConstructorNode>();
        }
        r.constructors.add(node);
    }

    public ConstructorNode addConstructor(int modifiers, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        ConstructorNode node = new ConstructorNode(modifiers, parameters, exceptions, code);
        this.addConstructor(node);
        return node;
    }

    public void addMethod(MethodNode node) {
        node.setDeclaringClass(this);
        this.redirect().methodsList.add(node);
        this.redirect().methods.put(node.getName(), node);
    }

    public void removeMethod(MethodNode node) {
        this.redirect().methodsList.remove(node);
        this.redirect().methods.remove(node.getName(), node);
    }

    public MethodNode addMethod(String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        MethodNode other = this.getDeclaredMethod(name, parameters);
        if (other != null) {
            return other;
        }
        MethodNode node = new MethodNode(name, modifiers, returnType, parameters, exceptions, code);
        this.addMethod(node);
        return node;
    }

    public boolean hasDeclaredMethod(String name, Parameter[] parameters) {
        MethodNode other = this.getDeclaredMethod(name, parameters);
        return other != null;
    }

    public boolean hasMethod(String name, Parameter[] parameters) {
        MethodNode other = this.getMethod(name, parameters);
        return other != null;
    }

    public MethodNode addSyntheticMethod(String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        MethodNode answer = this.addMethod(name, modifiers | 0x1000, returnType, parameters, exceptions, code);
        answer.setSynthetic(true);
        return answer;
    }

    public FieldNode addField(String name, int modifiers, ClassNode type, Expression initialValue) {
        FieldNode node = new FieldNode(name, modifiers, type, this.redirect(), initialValue);
        this.addField(node);
        return node;
    }

    public FieldNode addFieldFirst(String name, int modifiers, ClassNode type, Expression initialValue) {
        FieldNode node = new FieldNode(name, modifiers, type, this.redirect(), initialValue);
        this.addFieldFirst(node);
        return node;
    }

    public void addInterface(ClassNode type) {
        ClassNode[] interfaces;
        boolean skip = false;
        for (ClassNode existing : interfaces = this.redirect().interfaces) {
            if (!type.equals(existing)) continue;
            skip = true;
            break;
        }
        if (!skip) {
            ClassNode[] newInterfaces = new ClassNode[interfaces.length + 1];
            System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
            newInterfaces[interfaces.length] = type;
            this.redirect().interfaces = newInterfaces;
        }
    }

    public boolean equals(Object o) {
        if (this.redirect != null) {
            return this.redirect().equals(o);
        }
        if (!(o instanceof ClassNode)) {
            return false;
        }
        ClassNode cn = (ClassNode)o;
        return cn.getText().equals(this.getText());
    }

    public int hashCode() {
        if (this.redirect != null) {
            return this.redirect().hashCode();
        }
        return this.getName().hashCode();
    }

    public void addMixin(MixinNode mixin) {
        MixinNode[] mixins = this.redirect().mixins;
        boolean skip = false;
        for (MixinNode existing : mixins) {
            if (!mixin.equals(existing)) continue;
            skip = true;
            break;
        }
        if (!skip) {
            MixinNode[] newMixins = new MixinNode[mixins.length + 1];
            System.arraycopy(mixins, 0, newMixins, 0, mixins.length);
            newMixins[mixins.length] = mixin;
            this.redirect().mixins = newMixins;
        }
    }

    public FieldNode getDeclaredField(String name) {
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        ClassNode r = this.redirect();
        if (r.fieldIndex == null) {
            r.fieldIndex = new LinkedHashMap<String, FieldNode>();
        }
        return r.fieldIndex.get(name);
    }

    public FieldNode getField(String name) {
        for (ClassNode node = this; node != null; node = node.getSuperClass()) {
            FieldNode fn = node.getDeclaredField(name);
            if (fn == null) continue;
            return fn;
        }
        return null;
    }

    public FieldNode getOuterField(String name) {
        return null;
    }

    public ClassNode getOuterClass() {
        return null;
    }

    public void addObjectInitializerStatements(Statement statements) {
        this.getObjectInitializerStatements().add(statements);
    }

    public List<Statement> getObjectInitializerStatements() {
        if (this.objectInitializers == null) {
            this.objectInitializers = new LinkedList<Statement>();
        }
        return this.objectInitializers;
    }

    private MethodNode getOrAddStaticConstructorNode() {
        MethodNode method = null;
        List<MethodNode> declaredMethods = this.getDeclaredMethods("<clinit>");
        if (declaredMethods.isEmpty()) {
            method = this.addMethod("<clinit>", 8, ClassHelper.VOID_TYPE, Parameter.EMPTY_ARRAY, EMPTY_ARRAY, new BlockStatement());
            method.setSynthetic(true);
        } else {
            method = declaredMethods.get(0);
        }
        return method;
    }

    public void addStaticInitializerStatements(List<Statement> staticStatements, boolean fieldInit) {
        MethodNode method = this.getOrAddStaticConstructorNode();
        BlockStatement block = null;
        Statement statement = method.getCode();
        if (statement == null) {
            block = new BlockStatement();
        } else if (statement instanceof BlockStatement) {
            block = (BlockStatement)statement;
        } else {
            block = new BlockStatement();
            block.addStatement(statement);
        }
        if (!fieldInit) {
            block.addStatements(staticStatements);
        } else {
            List<Statement> blockStatements = block.getStatements();
            staticStatements.addAll(blockStatements);
            blockStatements.clear();
            blockStatements.addAll(staticStatements);
        }
    }

    public void positionStmtsAfterEnumInitStmts(List<Statement> staticFieldStatements) {
        MethodNode method = this.getOrAddStaticConstructorNode();
        Statement statement = method.getCode();
        if (statement instanceof BlockStatement) {
            BlockStatement block = (BlockStatement)statement;
            List<Statement> blockStatements = block.getStatements();
            ListIterator<Statement> litr = blockStatements.listIterator();
            while (litr.hasNext()) {
                FieldExpression fExp;
                BinaryExpression bExp;
                Statement stmt = litr.next();
                if (!(stmt instanceof ExpressionStatement) || !(((ExpressionStatement)stmt).getExpression() instanceof BinaryExpression) || !((bExp = (BinaryExpression)((ExpressionStatement)stmt).getExpression()).getLeftExpression() instanceof FieldExpression) || !(fExp = (FieldExpression)bExp.getLeftExpression()).getFieldName().equals("$VALUES")) continue;
                for (Statement tmpStmt : staticFieldStatements) {
                    litr.add(tmpStmt);
                }
            }
        }
    }

    public List<MethodNode> getDeclaredMethods(String name) {
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        if (this.redirect != null) {
            return this.redirect().getDeclaredMethods(name);
        }
        return this.methods.getNotNull(name);
    }

    public List<MethodNode> getMethods(String name) {
        ArrayList<MethodNode> answer = new ArrayList<MethodNode>();
        for (ClassNode node = this; node != null; node = node.getSuperClass()) {
            answer.addAll(node.getDeclaredMethods(name));
        }
        return answer;
    }

    public MethodNode getDeclaredMethod(String name, Parameter[] parameters) {
        for (MethodNode method : this.getDeclaredMethods(name)) {
            if (!this.parametersEqual(method.getParameters(), parameters)) continue;
            return method;
        }
        return null;
    }

    public MethodNode getMethod(String name, Parameter[] parameters) {
        for (MethodNode method : this.getMethods(name)) {
            if (!this.parametersEqual(method.getParameters(), parameters)) continue;
            return method;
        }
        return null;
    }

    public boolean isDerivedFrom(ClassNode type) {
        if (this.equals(ClassHelper.VOID_TYPE)) {
            return type.equals(ClassHelper.VOID_TYPE);
        }
        if (type.equals(ClassHelper.OBJECT_TYPE)) {
            return true;
        }
        for (ClassNode node = this; node != null; node = node.getSuperClass()) {
            if (!type.equals(node)) continue;
            return true;
        }
        return false;
    }

    public boolean isDerivedFromGroovyObject() {
        return this.implementsInterface(ClassHelper.GROOVY_OBJECT_TYPE);
    }

    public boolean implementsInterface(ClassNode classNode) {
        ClassNode node = this.redirect();
        do {
            if (!node.declaresInterface(classNode)) continue;
            return true;
        } while ((node = node.getSuperClass()) != null);
        return false;
    }

    public boolean declaresInterface(ClassNode classNode) {
        ClassNode[] interfaces;
        for (ClassNode cn : interfaces = this.redirect().getInterfaces()) {
            if (!cn.equals(classNode)) continue;
            return true;
        }
        for (ClassNode cn : interfaces) {
            if (!cn.declaresInterface(classNode)) continue;
            return true;
        }
        return false;
    }

    public ClassNode getSuperClass() {
        if (!this.lazyInitDone && !this.isResolved()) {
            throw new GroovyBugError("ClassNode#getSuperClass for " + this.getName() + " called before class resolving");
        }
        ClassNode sn = this.redirect().getUnresolvedSuperClass();
        if (sn != null) {
            sn = sn.redirect();
        }
        return sn;
    }

    public ClassNode getUnresolvedSuperClass() {
        return this.getUnresolvedSuperClass(true);
    }

    public ClassNode getUnresolvedSuperClass(boolean useRedirect) {
        if (!useRedirect) {
            return this.superClass;
        }
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        return this.redirect().superClass;
    }

    public void setUnresolvedSuperClass(ClassNode sn) {
        this.superClass = sn;
    }

    public ClassNode[] getUnresolvedInterfaces() {
        return this.getUnresolvedInterfaces(true);
    }

    public ClassNode[] getUnresolvedInterfaces(boolean useRedirect) {
        if (!useRedirect) {
            return this.interfaces;
        }
        if (!this.redirect().lazyInitDone) {
            this.redirect().lazyClassInit();
        }
        return this.redirect().interfaces;
    }

    public CompileUnit getCompileUnit() {
        if (this.redirect != null) {
            return this.redirect().getCompileUnit();
        }
        if (this.compileUnit == null && this.module != null) {
            this.compileUnit = this.module.getUnit();
        }
        return this.compileUnit;
    }

    protected void setCompileUnit(CompileUnit cu) {
        if (this.redirect != null) {
            this.redirect().setCompileUnit(cu);
        }
        if (this.compileUnit != null) {
            this.compileUnit = cu;
        }
    }

    protected boolean parametersEqual(Parameter[] a, Parameter[] b) {
        return ParameterUtils.parametersEqual(a, b);
    }

    public String getPackageName() {
        int idx = this.getName().lastIndexOf(46);
        if (idx > 0) {
            return this.getName().substring(0, idx);
        }
        return null;
    }

    public String getNameWithoutPackage() {
        int idx = this.getName().lastIndexOf(46);
        if (idx > 0) {
            return this.getName().substring(idx + 1);
        }
        return this.getName();
    }

    public void visitContents(GroovyClassVisitor visitor) {
        for (PropertyNode pn : this.getProperties()) {
            visitor.visitProperty(pn);
        }
        for (FieldNode fn : this.getFields()) {
            visitor.visitField(fn);
        }
        for (ConstructorNode cn : this.getDeclaredConstructors()) {
            visitor.visitConstructor(cn);
        }
        for (MethodNode mn : this.getMethods()) {
            visitor.visitMethod(mn);
        }
    }

    public MethodNode getGetterMethod(String getterName) {
        return this.getGetterMethod(getterName, true);
    }

    public MethodNode getGetterMethod(String getterName, boolean searchSuperClasses) {
        ClassNode parent;
        AnnotatedNode getterMethod = null;
        boolean booleanReturnOnly = getterName.startsWith("is");
        for (MethodNode method : this.getDeclaredMethods(getterName)) {
            if (!getterName.equals(method.getName()) || ClassHelper.VOID_TYPE == method.getReturnType() || method.getParameters().length != 0 || booleanReturnOnly && !ClassHelper.Boolean_TYPE.equals(ClassHelper.getWrapper(method.getReturnType())) || getterMethod != null && !getterMethod.isSynthetic()) continue;
            getterMethod = method;
        }
        if (getterMethod != null) {
            return getterMethod;
        }
        if (searchSuperClasses && (parent = this.getSuperClass()) != null) {
            return parent.getGetterMethod(getterName);
        }
        return null;
    }

    public MethodNode getSetterMethod(String setterName) {
        return this.getSetterMethod(setterName, true);
    }

    public MethodNode getSetterMethod(String setterName, boolean voidOnly) {
        for (MethodNode method : this.getDeclaredMethods(setterName)) {
            if (!setterName.equals(method.getName()) || voidOnly && ClassHelper.VOID_TYPE != method.getReturnType() || method.getParameters().length != 1) continue;
            return method;
        }
        ClassNode parent = this.getSuperClass();
        if (parent != null) {
            return parent.getSetterMethod(setterName, voidOnly);
        }
        return null;
    }

    public boolean isStaticClass() {
        return this.redirect().staticClass;
    }

    public void setStaticClass(boolean staticClass) {
        this.redirect().staticClass = staticClass;
    }

    public boolean isScriptBody() {
        return this.redirect().scriptBody;
    }

    public void setScriptBody(boolean scriptBody) {
        this.redirect().scriptBody = scriptBody;
    }

    public boolean isScript() {
        return this.redirect().script || this.isDerivedFrom(ClassHelper.SCRIPT_TYPE);
    }

    public void setScript(boolean script) {
        this.redirect().script = script;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean showRedirect) {
        if (this.isArray()) {
            return this.componentType.toString(showRedirect) + "[]";
        }
        StringBuilder ret = new StringBuilder(this.getName());
        if (this.placeholder) {
            ret = new StringBuilder(this.getUnresolvedName());
        }
        if (!this.placeholder && this.genericsTypes != null) {
            ret.append(" <");
            for (int i = 0; i < this.genericsTypes.length; ++i) {
                if (i != 0) {
                    ret.append(", ");
                }
                GenericsType genericsType = this.genericsTypes[i];
                ret.append(this.genericTypeAsString(genericsType));
            }
            ret.append(">");
        }
        if (this.redirect != null && showRedirect) {
            ret.append(" -> ").append(this.redirect().toString());
        }
        return ret.toString();
    }

    private String genericTypeAsString(GenericsType genericsType) {
        StringBuilder ret = new StringBuilder(genericsType.getName());
        if (genericsType.getUpperBounds() != null) {
            ret.append(" extends ");
            for (int i = 0; i < genericsType.getUpperBounds().length; ++i) {
                ClassNode classNode = genericsType.getUpperBounds()[i];
                if (classNode.equals(this)) {
                    ret.append(classNode.getName());
                } else {
                    ret.append(classNode.toString(false));
                }
                if (i + 1 >= genericsType.getUpperBounds().length) continue;
                ret.append(" & ");
            }
        } else if (genericsType.getLowerBound() != null) {
            ClassNode classNode = genericsType.getLowerBound();
            if (classNode.equals(this)) {
                ret.append(" super ").append(classNode.getName());
            } else {
                ret.append(" super ").append(classNode);
            }
        }
        return ret.toString();
    }

    public boolean hasPossibleMethod(String name, Expression arguments) {
        int count = 0;
        if (arguments instanceof TupleExpression) {
            TupleExpression tuple = (TupleExpression)arguments;
            count = tuple.getExpressions().size();
        }
        ClassNode node = this;
        do {
            for (MethodNode method : this.getMethods(name)) {
                if (method.getParameters().length != count || method.isStatic()) continue;
                return true;
            }
        } while ((node = node.getSuperClass()) != null);
        return false;
    }

    public MethodNode tryFindPossibleMethod(String name, Expression arguments) {
        int count = 0;
        if (!(arguments instanceof TupleExpression)) {
            return null;
        }
        TupleExpression tuple = (TupleExpression)arguments;
        count = tuple.getExpressions().size();
        MethodNode res = null;
        ClassNode node = this;
        TupleExpression args = (TupleExpression)arguments;
        do {
            for (MethodNode method : node.getMethods(name)) {
                int i;
                if (method.getParameters().length != count) continue;
                boolean match = true;
                for (i = 0; i != count; ++i) {
                    if (args.getType().isDerivedFrom(method.getParameters()[i].getType())) continue;
                    match = false;
                    break;
                }
                if (!match) continue;
                if (res == null) {
                    res = method;
                    continue;
                }
                if (res.getParameters().length != count) {
                    return null;
                }
                if (node.equals(this)) {
                    return null;
                }
                match = true;
                for (i = 0; i != count; ++i) {
                    if (res.getParameters()[i].getType().equals(method.getParameters()[i].getType())) continue;
                    match = false;
                    break;
                }
                if (match) continue;
                return null;
            }
        } while ((node = node.getSuperClass()) != null);
        return res;
    }

    public boolean hasPossibleStaticMethod(String name, Expression arguments) {
        int count = 0;
        if (arguments instanceof TupleExpression) {
            TupleExpression tuple = (TupleExpression)arguments;
            count = tuple.getExpressions().size();
        } else if (arguments instanceof MapExpression) {
            count = 1;
        }
        for (MethodNode method : this.getMethods(name)) {
            if (!method.isStatic()) continue;
            Parameter[] parameters = method.getParameters();
            if (parameters.length == count) {
                return true;
            }
            if (parameters.length > 0 && parameters[parameters.length - 1].getType().isArray() && count >= parameters.length - 1) {
                return true;
            }
            int nonDefaultParameters = 0;
            for (Parameter parameter : parameters) {
                if (parameter.hasInitialExpression()) continue;
                ++nonDefaultParameters;
            }
            if (count >= parameters.length || nonDefaultParameters > count) continue;
            return true;
        }
        return false;
    }

    public boolean isInterface() {
        return (this.getModifiers() & 0x200) > 0;
    }

    public boolean isResolved() {
        return this.redirect().clazz != null || this.componentType != null && this.componentType.isResolved();
    }

    public boolean isArray() {
        return this.componentType != null;
    }

    public ClassNode getComponentType() {
        return this.componentType;
    }

    public Class getTypeClass() {
        Class c = this.redirect().clazz;
        if (c != null) {
            return c;
        }
        ClassNode component = this.redirect().componentType;
        if (component != null && component.isResolved()) {
            ClassNode cn = component.makeArray();
            this.setRedirect(cn);
            return this.redirect().clazz;
        }
        throw new GroovyBugError("ClassNode#getTypeClass for " + this.getName() + " is called before the type class is set ");
    }

    public boolean hasPackageName() {
        return this.redirect().name.indexOf(46) > 0;
    }

    public void setAnnotated(boolean flag) {
        this.annotated = flag;
    }

    public boolean isAnnotated() {
        return this.annotated;
    }

    public GenericsType[] getGenericsTypes() {
        return this.genericsTypes;
    }

    public void setGenericsTypes(GenericsType[] genericsTypes) {
        this.usesGenerics = this.usesGenerics || genericsTypes != null;
        this.genericsTypes = genericsTypes;
    }

    public void setGenericsPlaceHolder(boolean b) {
        this.usesGenerics = this.usesGenerics || b;
        this.placeholder = b;
    }

    public boolean isGenericsPlaceHolder() {
        return this.placeholder;
    }

    public boolean isUsingGenerics() {
        return this.usesGenerics;
    }

    public void setUsingGenerics(boolean b) {
        this.usesGenerics = b;
    }

    public ClassNode getPlainNodeReference() {
        if (ClassHelper.isPrimitiveType(this)) {
            return this;
        }
        ClassNode n = new ClassNode(this.name, this.modifiers, this.superClass, null, null);
        n.isPrimaryNode = false;
        n.setRedirect(this.redirect());
        if (this.isArray()) {
            n.componentType = this.redirect().getComponentType();
        }
        return n;
    }

    public boolean isAnnotationDefinition() {
        return this.redirect().isPrimaryNode && this.isInterface() && (this.getModifiers() & 0x2000) != 0;
    }

    @Override
    public List<AnnotationNode> getAnnotations() {
        if (this.redirect != null) {
            return this.redirect.getAnnotations();
        }
        this.lazyClassInit();
        return super.getAnnotations();
    }

    @Override
    public List<AnnotationNode> getAnnotations(ClassNode type) {
        if (this.redirect != null) {
            return this.redirect.getAnnotations(type);
        }
        this.lazyClassInit();
        return super.getAnnotations(type);
    }

    public void addTransform(Class<? extends ASTTransformation> transform, ASTNode node) {
        GroovyASTTransformation annotation = transform.getAnnotation(GroovyASTTransformation.class);
        if (annotation == null) {
            return;
        }
        Set<ASTNode> nodes = this.getTransformInstances().get((Object)annotation.phase()).get(transform);
        if (nodes == null) {
            nodes = new LinkedHashSet<ASTNode>();
            this.getTransformInstances().get((Object)annotation.phase()).put(transform, nodes);
        }
        nodes.add(node);
    }

    public Map<Class<? extends ASTTransformation>, Set<ASTNode>> getTransforms(CompilePhase phase) {
        return this.getTransformInstances().get((Object)phase);
    }

    public void renameField(String oldName, String newName) {
        ClassNode r = this.redirect();
        if (r.fieldIndex == null) {
            r.fieldIndex = new LinkedHashMap<String, FieldNode>();
        }
        Map<String, FieldNode> index = r.fieldIndex;
        index.put(newName, index.remove(oldName));
    }

    public void removeField(String oldName) {
        ClassNode r = this.redirect();
        if (r.fieldIndex == null) {
            r.fieldIndex = new LinkedHashMap<String, FieldNode>();
        }
        Map<String, FieldNode> index = r.fieldIndex;
        r.fields.remove(index.get(oldName));
        index.remove(oldName);
    }

    public boolean isEnum() {
        return (this.getModifiers() & 0x4000) != 0;
    }

    public Iterator<InnerClassNode> getInnerClasses() {
        return (this.innerClasses == null ? Collections.emptyList() : this.innerClasses).iterator();
    }

    private Map<CompilePhase, Map<Class<? extends ASTTransformation>, Set<ASTNode>>> getTransformInstances() {
        if (this.transformInstances == null) {
            this.transformInstances = new EnumMap<CompilePhase, Map<Class<? extends ASTTransformation>, Set<ASTNode>>>(CompilePhase.class);
            for (CompilePhase phase : CompilePhase.values()) {
                this.transformInstances.put(phase, new LinkedHashMap());
            }
        }
        return this.transformInstances;
    }

    public boolean isRedirectNode() {
        return this.redirect != null;
    }

    @Override
    public String getText() {
        return this.getName();
    }

    private static class MapOfLists {
        private Map<Object, List<MethodNode>> map;

        private MapOfLists() {
        }

        public List<MethodNode> get(Object key) {
            return this.map == null ? null : this.map.get(key);
        }

        public List<MethodNode> getNotNull(Object key) {
            List<MethodNode> ret = this.get(key);
            if (ret == null) {
                ret = Collections.emptyList();
            }
            return ret;
        }

        public void put(Object key, MethodNode value) {
            if (this.map == null) {
                this.map = new LinkedHashMap<Object, List<MethodNode>>();
            }
            if (this.map.containsKey(key)) {
                this.get(key).add(value);
            } else {
                ArrayList<MethodNode> list = new ArrayList<MethodNode>(2);
                list.add(value);
                this.map.put(key, list);
            }
        }

        public void remove(Object key, MethodNode value) {
            this.get(key).remove(value);
        }
    }
}


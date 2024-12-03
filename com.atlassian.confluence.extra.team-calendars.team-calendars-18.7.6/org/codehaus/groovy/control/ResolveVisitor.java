/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.AnnotationConstantExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.ClassNodeResolver;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.trait.Traits;

public class ResolveVisitor
extends ClassCodeExpressionTransformer {
    private ClassNode currentClass;
    public static final String[] DEFAULT_IMPORTS = new String[]{"java.lang.", "java.io.", "java.net.", "java.util.", "groovy.lang.", "groovy.util."};
    private CompilationUnit compilationUnit;
    private SourceUnit source;
    private VariableScope currentScope;
    private boolean isTopLevelProperty = true;
    private boolean inPropertyExpression = false;
    private boolean inClosure = false;
    private Map<String, GenericsType> genericParameterNames = new HashMap<String, GenericsType>();
    private Set<FieldNode> fieldTypesChecked = new HashSet<FieldNode>();
    private boolean checkingVariableTypeInDeclaration = false;
    private ImportNode currImportNode = null;
    private MethodNode currentMethod;
    private ClassNodeResolver classNodeResolver;

    private static String replacePoints(String name) {
        return name.replace('.', '$');
    }

    public ResolveVisitor(CompilationUnit cu) {
        this.compilationUnit = cu;
        this.classNodeResolver = new ClassNodeResolver();
    }

    public void startResolving(ClassNode node, SourceUnit source) {
        this.source = source;
        this.visitClass(node);
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        ClassNode[] exceptions;
        Parameter[] paras;
        VariableScope oldScope = this.currentScope;
        this.currentScope = node.getVariableScope();
        Map<String, GenericsType> oldPNames = this.genericParameterNames;
        this.genericParameterNames = new HashMap<String, GenericsType>(this.genericParameterNames);
        this.resolveGenericsHeader(node.getGenericsTypes());
        for (Parameter p : paras = node.getParameters()) {
            p.setInitialExpression(this.transform(p.getInitialExpression()));
            this.resolveOrFail(p.getType(), p.getType());
            this.visitAnnotations(p);
        }
        for (ClassNode t : exceptions = node.getExceptions()) {
            this.resolveOrFail(t, node);
        }
        this.resolveOrFail(node.getReturnType(), node);
        MethodNode oldCurrentMethod = this.currentMethod;
        this.currentMethod = node;
        super.visitConstructorOrMethod(node, isConstructor);
        this.currentMethod = oldCurrentMethod;
        this.genericParameterNames = oldPNames;
        this.currentScope = oldScope;
    }

    @Override
    public void visitField(FieldNode node) {
        ClassNode t = node.getType();
        if (!this.fieldTypesChecked.contains(node)) {
            this.resolveOrFail(t, node);
        }
        super.visitField(node);
    }

    @Override
    public void visitProperty(PropertyNode node) {
        ClassNode t = node.getType();
        this.resolveOrFail(t, node);
        super.visitProperty(node);
        this.fieldTypesChecked.add(node.getField());
    }

    private boolean resolveToInner(ClassNode type) {
        int len;
        String name;
        if (type instanceof ConstructedClassWithPackage) {
            return false;
        }
        if (type instanceof ConstructedNestedClass) {
            return false;
        }
        String saved = name = type.getName();
        while ((len = name.lastIndexOf(46)) != -1) {
            name = name.substring(0, len) + "$" + name.substring(len + 1);
            type.setName(name);
            if (!this.resolve(type)) continue;
            return true;
        }
        if (this.resolveToNestedOfCurrent(type)) {
            return true;
        }
        type.setName(saved);
        return false;
    }

    private boolean resolveToNestedOfCurrent(ClassNode type) {
        ConstructedNestedClass tmp;
        if (type instanceof ConstructedNestedClass) {
            return false;
        }
        String name = type.getName();
        if (this.currentClass != type && !name.contains(".") && type.getClass().equals(ClassNode.class) && this.resolve(tmp = new ConstructedNestedClass(this.currentClass, name))) {
            type.setRedirect(tmp);
            return true;
        }
        return false;
    }

    private void resolveOrFail(ClassNode type, String msg, ASTNode node) {
        if (this.resolve(type)) {
            return;
        }
        if (this.resolveToInner(type)) {
            return;
        }
        this.addError("unable to resolve class " + type.getName() + " " + msg, node);
    }

    private void resolveOrFail(ClassNode type, ASTNode node, boolean prefereImports) {
        this.resolveGenericsTypes(type.getGenericsTypes());
        if (prefereImports && this.resolveAliasFromModule(type)) {
            return;
        }
        this.resolveOrFail(type, node);
    }

    private void resolveOrFail(ClassNode type, ASTNode node) {
        this.resolveOrFail(type, "", node);
    }

    private boolean resolve(ClassNode type) {
        return this.resolve(type, true, true, true);
    }

    private boolean resolve(ClassNode type, boolean testModuleImports, boolean testDefaultImports, boolean testStaticInnerClasses) {
        this.resolveGenericsTypes(type.getGenericsTypes());
        if (type.isResolved() || type.isPrimaryClassNode()) {
            return true;
        }
        if (type.isArray()) {
            ClassNode element = type.getComponentType();
            boolean resolved = this.resolve(element, testModuleImports, testDefaultImports, testStaticInnerClasses);
            if (resolved) {
                ClassNode cn = element.makeArray();
                type.setRedirect(cn);
            }
            return resolved;
        }
        if (this.currentClass == type) {
            return true;
        }
        if (this.genericParameterNames.get(type.getName()) != null) {
            GenericsType gt = this.genericParameterNames.get(type.getName());
            type.setRedirect(gt.getType());
            type.setGenericsTypes(new GenericsType[]{gt});
            type.setGenericsPlaceHolder(true);
            return true;
        }
        if (this.currentClass.getNameWithoutPackage().equals(type.getName())) {
            type.setRedirect(this.currentClass);
            return true;
        }
        return this.resolveNestedClass(type) || this.resolveFromModule(type, testModuleImports) || this.resolveFromCompileUnit(type) || this.resolveFromDefaultImports(type, testDefaultImports) || this.resolveFromStaticInnerClasses(type, testStaticInnerClasses) || this.resolveToOuter(type);
    }

    private boolean resolveNestedClass(ClassNode type) {
        ConstructedNestedClass val;
        if (type instanceof ConstructedNestedClass || type instanceof ConstructedClassWithPackage) {
            return false;
        }
        LinkedHashMap<String, Object> hierClasses = new LinkedHashMap<String, Object>();
        for (Object classToCheck = this.currentClass; classToCheck != ClassHelper.OBJECT_TYPE && classToCheck != null && !hierClasses.containsKey(((ClassNode)classToCheck).getName()); classToCheck = ((ClassNode)classToCheck).getSuperClass()) {
            hierClasses.put(((ClassNode)classToCheck).getName(), classToCheck);
        }
        for (ClassNode classToCheck : hierClasses.values()) {
            val = new ConstructedNestedClass(classToCheck, type.getName());
            if (this.resolveFromCompileUnit(val)) {
                type.setRedirect(val);
                return true;
            }
            for (ClassNode next : classToCheck.getAllInterfaces()) {
                if (type.getName().contains(next.getName()) || !this.resolve(val = new ConstructedNestedClass(next, type.getName()), false, false, false)) continue;
                type.setRedirect(val);
                return true;
            }
        }
        if (!(this.currentClass instanceof InnerClassNode)) {
            return false;
        }
        LinkedList<ClassNode> outerClasses = new LinkedList<ClassNode>();
        for (ClassNode outer = this.currentClass.getOuterClass(); outer != null; outer = outer.getOuterClass()) {
            outerClasses.addFirst(outer);
        }
        for (ClassNode testNode : outerClasses) {
            val = new ConstructedNestedClass(testNode, type.getName());
            if (this.resolveFromCompileUnit(val)) {
                type.setRedirect(val);
                return true;
            }
            for (ClassNode next : testNode.getAllInterfaces()) {
                if (type.getName().contains(next.getName()) || !this.resolve(val = new ConstructedNestedClass(next, type.getName()), false, false, false)) continue;
                type.setRedirect(val);
                return true;
            }
        }
        return false;
    }

    private static String replaceLastPoint(String name) {
        int lastPoint = name.lastIndexOf(46);
        name = new StringBuffer().append(name.substring(0, lastPoint)).append("$").append(name.substring(lastPoint + 1)).toString();
        return name;
    }

    private boolean resolveFromStaticInnerClasses(ClassNode type, boolean testStaticInnerClasses) {
        if (type instanceof ConstructedNestedClass) {
            return false;
        }
        if (type instanceof LowerCaseClass) {
            return false;
        }
        if (testStaticInnerClasses &= type.hasPackageName()) {
            if (type instanceof ConstructedClassWithPackage) {
                ConstructedClassWithPackage tmp = (ConstructedClassWithPackage)type;
                String savedName = tmp.className;
                tmp.className = ResolveVisitor.replaceLastPoint(savedName);
                if (this.resolve(tmp, false, true, true)) {
                    type.setRedirect(tmp.redirect());
                    return true;
                }
                tmp.className = savedName;
            } else {
                String savedName = type.getName();
                String replacedPointType = ResolveVisitor.replaceLastPoint(savedName);
                type.setName(replacedPointType);
                if (this.resolve(type, false, true, true)) {
                    return true;
                }
                type.setName(savedName);
            }
        }
        return false;
    }

    private boolean resolveFromDefaultImports(ClassNode type, boolean testDefaultImports) {
        testDefaultImports &= !type.hasPackageName();
        if (testDefaultImports &= !(type instanceof LowerCaseClass)) {
            for (String packagePrefix : DEFAULT_IMPORTS) {
                String name = type.getName();
                ConstructedClassWithPackage tmp = new ConstructedClassWithPackage(packagePrefix, name);
                if (!this.resolve(tmp, false, false, false)) continue;
                type.setRedirect(tmp.redirect());
                return true;
            }
            String name = type.getName();
            if (name.equals("BigInteger")) {
                type.setRedirect(ClassHelper.BigInteger_TYPE);
                return true;
            }
            if (name.equals("BigDecimal")) {
                type.setRedirect(ClassHelper.BigDecimal_TYPE);
                return true;
            }
        }
        return false;
    }

    private boolean resolveFromCompileUnit(ClassNode type) {
        CompileUnit compileUnit = this.currentClass.getCompileUnit();
        if (compileUnit == null) {
            return false;
        }
        ClassNode cuClass = compileUnit.getClass(type.getName());
        if (cuClass != null) {
            if (type != cuClass) {
                type.setRedirect(cuClass);
            }
            return true;
        }
        return false;
    }

    private void ambiguousClass(ClassNode type, ClassNode iType, String name) {
        if (type.getName().equals(iType.getName())) {
            this.addError("reference to " + name + " is ambiguous, both class " + type.getName() + " and " + iType.getName() + " match", type);
        } else {
            type.setRedirect(iType);
        }
    }

    private boolean resolveAliasFromModule(ClassNode type) {
        String name;
        if (type instanceof ConstructedClassWithPackage) {
            return false;
        }
        ModuleNode module = this.currentClass.getModule();
        if (module == null) {
            return false;
        }
        String pname = name = type.getName();
        int index = name.length();
        do {
            ConstructedNestedClass tmp;
            pname = name.substring(0, index);
            ClassNode aliasedNode = null;
            ImportNode importNode = module.getImport(pname);
            if (importNode != null && importNode != this.currImportNode) {
                aliasedNode = importNode.getType();
            }
            if (aliasedNode == null && (importNode = module.getStaticImports().get(pname)) != null && importNode != this.currImportNode && this.resolve(tmp = new ConstructedNestedClass(importNode.getType(), importNode.getFieldName()), false, false, true) && (tmp.getModifiers() & 8) != 0) {
                type.setRedirect(tmp.redirect());
                return true;
            }
            if (aliasedNode == null) continue;
            if (pname.length() == name.length()) {
                type.setRedirect(aliasedNode);
                return true;
            }
            String className = aliasedNode.getNameWithoutPackage() + '$' + name.substring(pname.length() + 1).replace('.', '$');
            ConstructedClassWithPackage tmp2 = new ConstructedClassWithPackage(aliasedNode.getPackageName() + ".", className);
            if (!this.resolve(tmp2, true, true, false)) continue;
            type.setRedirect(tmp2.redirect());
            return true;
        } while ((index = pname.lastIndexOf(46)) != -1);
        return false;
    }

    private boolean resolveFromModule(ClassNode type, boolean testModuleImports) {
        if (type instanceof ConstructedNestedClass) {
            return false;
        }
        if (type instanceof LowerCaseClass) {
            return this.resolveAliasFromModule(type);
        }
        String name = type.getName();
        ModuleNode module = this.currentClass.getModule();
        if (module == null) {
            return false;
        }
        boolean newNameUsed = false;
        if (!type.hasPackageName() && module.hasPackageName() && !(type instanceof ConstructedClassWithPackage)) {
            type.setName(module.getPackageName() + name);
            newNameUsed = true;
        }
        List<ClassNode> moduleClasses = module.getClasses();
        for (ClassNode mClass : moduleClasses) {
            if (!mClass.getName().equals(type.getName())) continue;
            if (mClass != type) {
                type.setRedirect(mClass);
            }
            return true;
        }
        if (newNameUsed) {
            type.setName(name);
        }
        if (testModuleImports) {
            ConstructedNestedClass tmp;
            ConstructedClassWithPackage tmp2;
            if (this.resolveAliasFromModule(type)) {
                return true;
            }
            if (module.hasPackageName() && this.resolve(tmp2 = new ConstructedClassWithPackage(module.getPackageName(), name), false, false, false)) {
                this.ambiguousClass(type, tmp2, name);
                type.setRedirect(tmp2.redirect());
                return true;
            }
            for (ImportNode importNode : module.getStaticImports().values()) {
                if (!importNode.getFieldName().equals(name) || !this.resolve(tmp = new ConstructedNestedClass(importNode.getType(), name), false, false, true) || (tmp.getModifiers() & 8) == 0) continue;
                type.setRedirect(tmp.redirect());
                return true;
            }
            for (ImportNode importNode : module.getStarImports()) {
                String packagePrefix = importNode.getPackageName();
                ConstructedClassWithPackage tmp3 = new ConstructedClassWithPackage(packagePrefix, name);
                if (!this.resolve(tmp3, false, false, true)) continue;
                this.ambiguousClass(type, tmp3, name);
                type.setRedirect(tmp3.redirect());
                return true;
            }
            for (ImportNode importNode : module.getStaticStarImports().values()) {
                tmp = new ConstructedNestedClass(importNode.getType(), name);
                if (!this.resolve(tmp, false, false, true) || (tmp.getModifiers() & 8) == 0) continue;
                this.ambiguousClass(type, tmp, name);
                type.setRedirect(tmp.redirect());
                return true;
            }
        }
        return false;
    }

    private boolean resolveToOuter(ClassNode type) {
        String name = type.getName();
        if (type instanceof LowerCaseClass) {
            this.classNodeResolver.cacheClass(name, ClassNodeResolver.NO_CLASS);
            return false;
        }
        if (this.currentClass.getModule().hasPackageName() && name.indexOf(46) == -1) {
            return false;
        }
        ClassNodeResolver.LookupResult lr = null;
        lr = this.classNodeResolver.resolveName(name, this.compilationUnit);
        if (lr != null) {
            if (lr.isSourceUnit()) {
                SourceUnit su = lr.getSourceUnit();
                this.currentClass.getCompileUnit().addClassNodeToCompile(type, su);
            } else {
                type.setRedirect(lr.getClassNode());
            }
            return true;
        }
        return false;
    }

    @Override
    public Expression transform(Expression exp) {
        if (exp == null) {
            return null;
        }
        Expression ret = null;
        if (exp instanceof VariableExpression) {
            ret = this.transformVariableExpression((VariableExpression)exp);
        } else if (exp.getClass() == PropertyExpression.class) {
            ret = this.transformPropertyExpression((PropertyExpression)exp);
        } else if (exp instanceof DeclarationExpression) {
            ret = this.transformDeclarationExpression((DeclarationExpression)exp);
        } else if (exp instanceof BinaryExpression) {
            ret = this.transformBinaryExpression((BinaryExpression)exp);
        } else if (exp instanceof MethodCallExpression) {
            ret = this.transformMethodCallExpression((MethodCallExpression)exp);
        } else if (exp instanceof ClosureExpression) {
            ret = this.transformClosureExpression((ClosureExpression)exp);
        } else if (exp instanceof ConstructorCallExpression) {
            ret = this.transformConstructorCallExpression((ConstructorCallExpression)exp);
        } else if (exp instanceof AnnotationConstantExpression) {
            ret = this.transformAnnotationConstantExpression((AnnotationConstantExpression)exp);
        } else {
            this.resolveOrFail(exp.getType(), exp);
            ret = exp.transformExpression(this);
        }
        if (ret != null && ret != exp) {
            ret.setSourcePosition(exp);
        }
        return ret;
    }

    private static String lookupClassName(PropertyExpression pe) {
        boolean doInitialClassTest = true;
        String name = "";
        for (Expression it = pe; it != null; it = it.getObjectExpression()) {
            if (it instanceof VariableExpression) {
                VariableExpression ve = (VariableExpression)it;
                if (ve.isSuperExpression() || ve.isThisExpression()) {
                    return null;
                }
                String varName = ve.getName();
                if (doInitialClassTest) {
                    if (!ResolveVisitor.testVanillaNameForClass(varName)) {
                        return null;
                    }
                    doInitialClassTest = false;
                    name = varName;
                    break;
                }
                name = varName + "." + name;
                break;
            }
            if (it.getClass() != PropertyExpression.class) {
                return null;
            }
            PropertyExpression current = it;
            String propertyPart = current.getPropertyAsString();
            if (propertyPart == null || propertyPart.equals("class")) {
                return null;
            }
            if (doInitialClassTest) {
                if (!ResolveVisitor.testVanillaNameForClass(propertyPart)) {
                    return null;
                }
                doInitialClassTest = false;
                name = propertyPart;
                continue;
            }
            name = propertyPart + "." + name;
        }
        if (name.length() == 0) {
            return null;
        }
        return name;
    }

    private static Expression correctClassClassChain(PropertyExpression pe) {
        LinkedList<PropertyExpression> stack = new LinkedList<PropertyExpression>();
        ClassExpression found = null;
        for (Expression it = pe; it != null; it = it.getObjectExpression()) {
            if (it instanceof ClassExpression) {
                found = (ClassExpression)it;
                break;
            }
            if (it.getClass() != PropertyExpression.class) {
                return pe;
            }
            stack.addFirst((PropertyExpression)it);
        }
        if (found == null) {
            return pe;
        }
        if (stack.isEmpty()) {
            return pe;
        }
        Object stackElement = stack.removeFirst();
        if (stackElement.getClass() != PropertyExpression.class) {
            return pe;
        }
        PropertyExpression classPropertyExpression = (PropertyExpression)stackElement;
        String propertyNamePart = classPropertyExpression.getPropertyAsString();
        if (propertyNamePart == null || !propertyNamePart.equals("class")) {
            return pe;
        }
        found.setSourcePosition(classPropertyExpression);
        if (stack.isEmpty()) {
            return found;
        }
        stackElement = stack.removeFirst();
        if (stackElement.getClass() != PropertyExpression.class) {
            return pe;
        }
        PropertyExpression classPropertyExpressionContainer = (PropertyExpression)stackElement;
        classPropertyExpressionContainer.setObjectExpression(found);
        return pe;
    }

    protected Expression transformPropertyExpression(PropertyExpression pe) {
        ClassNode type;
        boolean itlp = this.isTopLevelProperty;
        boolean ipe = this.inPropertyExpression;
        Expression objectExpression = pe.getObjectExpression();
        this.inPropertyExpression = true;
        this.isTopLevelProperty = objectExpression.getClass() != PropertyExpression.class;
        objectExpression = this.transform(objectExpression);
        this.inPropertyExpression = false;
        Expression property = this.transform(pe.getProperty());
        this.isTopLevelProperty = itlp;
        this.inPropertyExpression = ipe;
        boolean spreadSafe = pe.isSpreadSafe();
        PropertyExpression old = pe;
        pe = new PropertyExpression(objectExpression, property, pe.isSafe());
        pe.setSpreadSafe(spreadSafe);
        pe.setSourcePosition(old);
        String className = ResolveVisitor.lookupClassName(pe);
        if (className != null && this.resolve(type = ClassHelper.make(className))) {
            ClassExpression ret = new ClassExpression(type);
            ret.setSourcePosition(pe);
            return ret;
        }
        if (objectExpression instanceof ClassExpression && pe.getPropertyAsString() != null) {
            ClassExpression ce = (ClassExpression)objectExpression;
            for (ClassNode classNode = ce.getType(); classNode != null; classNode = classNode.getSuperClass()) {
                ConstructedNestedClass type2 = new ConstructedNestedClass(classNode, pe.getPropertyAsString());
                if (!this.resolve(type2, false, false, false) || classNode != ce.getType() && !this.isVisibleNestedClass(type2, ce.getType())) continue;
                ClassExpression ret = new ClassExpression(type2);
                ret.setSourcePosition(ce);
                return ret;
            }
        }
        Expression ret = pe;
        this.checkThisAndSuperAsPropertyAccess(pe);
        if (this.isTopLevelProperty) {
            ret = ResolveVisitor.correctClassClassChain(pe);
        }
        return ret;
    }

    private boolean isVisibleNestedClass(ClassNode type, ClassNode ceType) {
        if (!type.isRedirectNode()) {
            return false;
        }
        ClassNode redirect = type.redirect();
        if (Modifier.isPublic(redirect.getModifiers()) || Modifier.isProtected(redirect.getModifiers())) {
            return true;
        }
        PackageNode classPackage = ceType.getPackage();
        PackageNode nestedPackage = redirect.getPackage();
        return (redirect.getModifiers() & 7) == 0 && (classPackage == null && nestedPackage == null || classPackage != null && nestedPackage != null && classPackage.getName().equals(nestedPackage.getName()));
    }

    private boolean directlyImplementsTrait(ClassNode trait) {
        ClassNode[] interfaces = this.currentClass.getInterfaces();
        if (interfaces == null) {
            return this.currentClass.getSuperClass().equals(trait);
        }
        for (ClassNode node : interfaces) {
            if (!node.equals(trait)) continue;
            return true;
        }
        return this.currentClass.getSuperClass().equals(trait);
    }

    private void checkThisAndSuperAsPropertyAccess(PropertyExpression expression) {
        if (expression.isImplicitThis()) {
            return;
        }
        String prop = expression.getPropertyAsString();
        if (prop == null) {
            return;
        }
        if (!prop.equals("this") && !prop.equals("super")) {
            return;
        }
        ClassNode type = expression.getObjectExpression().getType();
        if (expression.getObjectExpression() instanceof ClassExpression) {
            ClassNode iterType;
            if (!(this.currentClass instanceof InnerClassNode) && !Traits.isTrait(type)) {
                this.addError("The usage of 'Class.this' and 'Class.super' is only allowed in nested/inner classes.", expression);
                return;
            }
            if (this.currentScope != null && !this.currentScope.isInStaticContext() && Traits.isTrait(type) && "super".equals(prop) && this.directlyImplementsTrait(type)) {
                return;
            }
            for (iterType = this.currentClass; iterType != null && !iterType.equals(type); iterType = iterType.getOuterClass()) {
            }
            if (iterType == null) {
                this.addError("The class '" + type.getName() + "' needs to be an outer class of '" + this.currentClass.getName() + "' when using '.this' or '.super'.", expression);
            }
            if ((this.currentClass.getModifiers() & 8) == 0) {
                return;
            }
            if (this.currentScope != null && !this.currentScope.isInStaticContext()) {
                return;
            }
            this.addError("The usage of 'Class.this' and 'Class.super' within static nested class '" + this.currentClass.getName() + "' is not allowed in a static context.", expression);
        }
    }

    protected Expression transformVariableExpression(VariableExpression ve) {
        this.visitAnnotations(ve);
        Variable v = ve.getAccessedVariable();
        if (!(v instanceof DynamicVariable) && !this.checkingVariableTypeInDeclaration) {
            return ve;
        }
        if (v instanceof DynamicVariable) {
            String name = ve.getName();
            ClassNode t = ClassHelper.make(name);
            boolean isClass = t.isResolved();
            if (!isClass) {
                if (Character.isLowerCase(name.charAt(0))) {
                    t = new LowerCaseClass(name);
                }
                if (!(isClass = this.resolve(t))) {
                    isClass = this.resolveToNestedOfCurrent(t);
                }
            }
            if (isClass) {
                for (VariableScope scope = this.currentScope; scope != null && !scope.isRoot() && !scope.isRoot() && scope.removeReferencedClassVariable(ve.getName()) != null; scope = scope.getParent()) {
                }
                ClassExpression ce = new ClassExpression(t);
                ce.setSourcePosition(ve);
                return ce;
            }
        }
        this.resolveOrFail(ve.getType(), ve);
        ClassNode origin = ve.getOriginType();
        if (origin != ve.getType()) {
            this.resolveOrFail(origin, ve);
        }
        return ve;
    }

    private static boolean testVanillaNameForClass(String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        return !Character.isLowerCase(name.charAt(0));
    }

    private static boolean isLeftSquareBracket(int op) {
        return op == 1905 || op == 30 || op == 810 || op == 811;
    }

    protected Expression transformBinaryExpression(BinaryExpression be) {
        Expression left = this.transform(be.getLeftExpression());
        int type = be.getOperation().getType();
        if ((type == 1100 || type == 100) && left instanceof ClassExpression) {
            ClassExpression ce = (ClassExpression)left;
            String error = "you tried to assign a value to the class '" + ce.getType().getName() + "'";
            if (ce.getType().isScript()) {
                error = error + ". Do you have a script with this name?";
            }
            this.addError(error, be.getLeftExpression());
            return be;
        }
        if (left instanceof ClassExpression && ResolveVisitor.isLeftSquareBracket(type)) {
            if (be.getRightExpression() instanceof ListExpression) {
                ListExpression list = (ListExpression)be.getRightExpression();
                if (list.getExpressions().isEmpty()) {
                    ClassExpression ce = new ClassExpression(left.getType().makeArray());
                    ce.setSourcePosition(be);
                    return ce;
                }
                boolean map = true;
                for (Expression expression : list.getExpressions()) {
                    if (expression instanceof MapEntryExpression) continue;
                    map = false;
                    break;
                }
                if (map) {
                    MapExpression me = new MapExpression();
                    for (Expression expression : list.getExpressions()) {
                        me.addMapEntryExpression((MapEntryExpression)this.transform(expression));
                    }
                    me.setSourcePosition(list);
                    CastExpression castExpression = new CastExpression(left.getType(), me);
                    castExpression.setSourcePosition(be);
                    return castExpression;
                }
            }
            if (be.getRightExpression() instanceof MapEntryExpression) {
                MapExpression me = new MapExpression();
                me.addMapEntryExpression((MapEntryExpression)this.transform(be.getRightExpression()));
                me.setSourcePosition(be.getRightExpression());
                CastExpression ce = new CastExpression(left.getType(), me);
                ce.setSourcePosition(be);
                return ce;
            }
        }
        Expression right = this.transform(be.getRightExpression());
        be.setLeftExpression(left);
        be.setRightExpression(right);
        return be;
    }

    protected Expression transformClosureExpression(ClosureExpression ce) {
        Statement code;
        boolean oldInClosure = this.inClosure;
        this.inClosure = true;
        Parameter[] paras = ce.getParameters();
        if (paras != null) {
            for (Parameter para : paras) {
                Expression initialVal;
                ClassNode t = para.getType();
                this.resolveOrFail(t, ce);
                this.visitAnnotations(para);
                if (para.hasInitialExpression() && (initialVal = para.getInitialExpression()) instanceof Expression) {
                    para.setInitialExpression(this.transform(initialVal));
                }
                this.visitAnnotations(para);
            }
        }
        if ((code = ce.getCode()) != null) {
            code.visit(this);
        }
        this.inClosure = oldInClosure;
        return ce;
    }

    protected Expression transformConstructorCallExpression(ConstructorCallExpression cce) {
        ClassNode type = cce.getType();
        this.resolveOrFail(type, cce);
        if (Modifier.isAbstract(type.getModifiers())) {
            this.addError("You cannot create an instance from the abstract " + ResolveVisitor.getDescription(type) + ".", cce);
        }
        Expression ret = cce.transformExpression(this);
        return ret;
    }

    private static String getDescription(ClassNode node) {
        return (node.isInterface() ? "interface" : "class") + " '" + node.getName() + "'";
    }

    protected Expression transformMethodCallExpression(MethodCallExpression mce) {
        Expression args = this.transform(mce.getArguments());
        Expression method = this.transform(mce.getMethod());
        Expression object = this.transform(mce.getObjectExpression());
        this.resolveGenericsTypes(mce.getGenericsTypes());
        MethodCallExpression result = new MethodCallExpression(object, method, args);
        result.setSafe(mce.isSafe());
        result.setImplicitThis(mce.isImplicitThis());
        result.setSpreadSafe(mce.isSpreadSafe());
        result.setSourcePosition(mce);
        result.setGenericsTypes(mce.getGenericsTypes());
        result.setMethodTarget(mce.getMethodTarget());
        return result;
    }

    protected Expression transformDeclarationExpression(DeclarationExpression de) {
        this.visitAnnotations(de);
        Expression oldLeft = de.getLeftExpression();
        this.checkingVariableTypeInDeclaration = true;
        Expression left = this.transform(oldLeft);
        this.checkingVariableTypeInDeclaration = false;
        if (left instanceof ClassExpression) {
            ClassExpression ce = (ClassExpression)left;
            this.addError("you tried to assign a value to the class " + ce.getType().getName(), oldLeft);
            return de;
        }
        Expression right = this.transform(de.getRightExpression());
        if (right == de.getRightExpression()) {
            this.fixDeclaringClass(de);
            return de;
        }
        DeclarationExpression newDeclExpr = new DeclarationExpression(left, de.getOperation(), right);
        newDeclExpr.setDeclaringClass(de.getDeclaringClass());
        this.fixDeclaringClass(newDeclExpr);
        newDeclExpr.setSourcePosition(de);
        newDeclExpr.addAnnotations(de.getAnnotations());
        return newDeclExpr;
    }

    private void fixDeclaringClass(DeclarationExpression newDeclExpr) {
        if (newDeclExpr.getDeclaringClass() == null && this.currentMethod != null) {
            newDeclExpr.setDeclaringClass(this.currentMethod.getDeclaringClass());
        }
    }

    protected Expression transformAnnotationConstantExpression(AnnotationConstantExpression ace) {
        AnnotationNode an = (AnnotationNode)ace.getValue();
        ClassNode type = an.getClassNode();
        this.resolveOrFail(type, ", unable to find class for annotation", an);
        for (Map.Entry<String, Expression> member : an.getMembers().entrySet()) {
            member.setValue(this.transform(member.getValue()));
        }
        return ace;
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        List<AnnotationNode> annotations = node.getAnnotations();
        if (annotations.isEmpty()) {
            return;
        }
        HashMap<String, AnnotationNode> tmpAnnotations = new HashMap<String, AnnotationNode>();
        for (AnnotationNode an : annotations) {
            AnnotationNode anyPrevAnnNode;
            Class annTypeClass;
            Retention retAnn;
            if (an.isBuiltIn()) continue;
            ClassNode annType = an.getClassNode();
            this.resolveOrFail(annType, ",  unable to find class for annotation", an);
            for (Map.Entry<String, Expression> member : an.getMembers().entrySet()) {
                Expression newValue = this.transform(member.getValue());
                newValue = this.transformInlineConstants(newValue);
                member.setValue(newValue);
                this.checkAnnotationMemberValue(newValue);
            }
            if (!annType.isResolved() || (retAnn = (annTypeClass = annType.getTypeClass()).getAnnotation(Retention.class)) == null || !retAnn.value().equals((Object)RetentionPolicy.RUNTIME) || (anyPrevAnnNode = tmpAnnotations.put(annTypeClass.getName(), an)) == null) continue;
            this.addError("Cannot specify duplicate annotation on the same member : " + annType.getName(), an);
        }
    }

    private Expression transformInlineConstants(Expression exp) {
        if (exp instanceof PropertyExpression) {
            PropertyExpression pe = (PropertyExpression)exp;
            if (pe.getObjectExpression() instanceof ClassExpression) {
                ClassExpression ce = (ClassExpression)pe.getObjectExpression();
                ClassNode type = ce.getType();
                if (type.isEnum()) {
                    return exp;
                }
                FieldNode fn = type.getField(pe.getPropertyAsString());
                if (fn != null && !fn.isEnum() && fn.isStatic() && fn.isFinal() && fn.getInitialValueExpression() instanceof ConstantExpression) {
                    return fn.getInitialValueExpression();
                }
            }
        } else {
            ConstantExpression ce;
            if (exp instanceof ListExpression) {
                ListExpression le = (ListExpression)exp;
                ListExpression result = new ListExpression();
                for (Expression e : le.getExpressions()) {
                    result.addExpression(this.transformInlineConstants(e));
                }
                return result;
            }
            if (exp instanceof AnnotationConstantExpression && (ce = (ConstantExpression)exp).getValue() instanceof AnnotationNode) {
                AnnotationNode an = (AnnotationNode)ce.getValue();
                for (Map.Entry<String, Expression> member : an.getMembers().entrySet()) {
                    member.setValue(this.transformInlineConstants(member.getValue()));
                }
            }
        }
        return exp;
    }

    private void checkAnnotationMemberValue(Expression newValue) {
        if (newValue instanceof PropertyExpression) {
            PropertyExpression pe = (PropertyExpression)newValue;
            if (!(pe.getObjectExpression() instanceof ClassExpression)) {
                this.addError("unable to find class '" + pe.getText() + "' for annotation attribute constant", pe.getObjectExpression());
            }
        } else if (newValue instanceof ListExpression) {
            ListExpression le = (ListExpression)newValue;
            for (Expression e : le.getExpressions()) {
                this.checkAnnotationMemberValue(e);
            }
        }
    }

    @Override
    public void visitClass(ClassNode node) {
        ClassNode sn;
        ClassNode oldNode = this.currentClass;
        if (node instanceof InnerClassNode) {
            if (Modifier.isStatic(node.getModifiers())) {
                this.genericParameterNames = new HashMap<String, GenericsType>();
            }
        } else {
            this.genericParameterNames = new HashMap<String, GenericsType>();
        }
        this.currentClass = node;
        this.resolveGenericsHeader(node.getGenericsTypes());
        ModuleNode module = node.getModule();
        if (!module.hasImportsResolved()) {
            ClassNode type;
            Iterator<ImportNode> iterator = module.getImports().iterator();
            while (iterator.hasNext()) {
                ImportNode importNode;
                this.currImportNode = importNode = iterator.next();
                type = importNode.getType();
                if (this.resolve(type, false, false, true)) {
                    this.currImportNode = null;
                    continue;
                }
                this.currImportNode = null;
                this.addError("unable to resolve class " + type.getName(), type);
            }
            for (ImportNode importNode : module.getStaticStarImports().values()) {
                type = importNode.getType();
                if (this.resolve(type, false, false, true)) continue;
                if (type.getPackageName() == null && node.getPackageName() != null) {
                    String oldTypeName = type.getName();
                    type.setName(node.getPackageName() + "." + oldTypeName);
                    if (this.resolve(type, false, false, true)) continue;
                    type.setName(oldTypeName);
                }
                this.addError("unable to resolve class " + type.getName(), type);
            }
            for (ImportNode importNode : module.getStaticImports().values()) {
                type = importNode.getType();
                if (this.resolve(type, true, true, true)) continue;
                this.addError("unable to resolve class " + type.getName(), type);
            }
            for (ImportNode importNode : module.getStaticStarImports().values()) {
                type = importNode.getType();
                if (this.resolve(type, true, true, true)) continue;
                this.addError("unable to resolve class " + type.getName(), type);
            }
            module.setImportsResolved(true);
        }
        if ((sn = node.getUnresolvedSuperClass()) != null) {
            this.resolveOrFail(sn, node, true);
        }
        for (ClassNode anInterface : node.getInterfaces()) {
            this.resolveOrFail(anInterface, node, true);
        }
        this.checkCyclicInheritence(node, node.getUnresolvedSuperClass(), node.getInterfaces());
        super.visitClass(node);
        this.currentClass = oldNode;
    }

    private void checkCyclicInheritence(ClassNode originalNode, ClassNode parentToCompare, ClassNode[] interfacesToCompare) {
        if (!originalNode.isInterface()) {
            if (parentToCompare == null) {
                return;
            }
            if (originalNode == parentToCompare.redirect()) {
                this.addError("Cyclic inheritance involving " + parentToCompare.getName() + " in class " + originalNode.getName(), originalNode);
                return;
            }
            if (interfacesToCompare != null && interfacesToCompare.length > 0) {
                for (ClassNode intfToCompare : interfacesToCompare) {
                    if (originalNode != intfToCompare.redirect()) continue;
                    this.addError("Cycle detected: the type " + originalNode.getName() + " cannot implement itself", originalNode);
                    return;
                }
            }
            if (parentToCompare == ClassHelper.OBJECT_TYPE) {
                return;
            }
            this.checkCyclicInheritence(originalNode, parentToCompare.getUnresolvedSuperClass(), null);
        } else if (interfacesToCompare != null && interfacesToCompare.length > 0) {
            for (ClassNode intfToCompare : interfacesToCompare) {
                if (originalNode != intfToCompare.redirect()) continue;
                this.addError("Cyclic inheritance involving " + intfToCompare.getName() + " in interface " + originalNode.getName(), originalNode);
                return;
            }
            for (ClassNode intf : interfacesToCompare) {
                this.checkCyclicInheritence(originalNode, null, intf.getInterfaces());
            }
        } else {
            return;
        }
    }

    @Override
    public void visitCatchStatement(CatchStatement cs) {
        this.resolveOrFail(cs.getExceptionType(), cs);
        if (cs.getExceptionType() == ClassHelper.DYNAMIC_TYPE) {
            cs.getVariable().setType(ClassHelper.make(Exception.class));
        }
        super.visitCatchStatement(cs);
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        this.resolveOrFail(forLoop.getVariableType(), forLoop);
        super.visitForLoop(forLoop);
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        VariableScope oldScope = this.currentScope;
        this.currentScope = block.getVariableScope();
        super.visitBlockStatement(block);
        this.currentScope = oldScope;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    private boolean resolveGenericsTypes(GenericsType[] types) {
        if (types == null) {
            return true;
        }
        this.currentClass.setUsingGenerics(true);
        boolean resolved = true;
        for (GenericsType type : types) {
            resolved = this.resolveGenericsType(type) && resolved;
        }
        return resolved;
    }

    private void resolveGenericsHeader(GenericsType[] types) {
        if (types == null) {
            return;
        }
        this.currentClass.setUsingGenerics(true);
        for (GenericsType type : types) {
            ClassNode classNode = type.getType();
            String name = type.getName();
            ClassNode[] bounds = type.getUpperBounds();
            if (bounds != null) {
                boolean nameAdded = false;
                for (ClassNode upperBound : bounds) {
                    if (!nameAdded && upperBound != null || !this.resolve(classNode)) {
                        this.genericParameterNames.put(name, type);
                        type.setPlaceholder(true);
                        classNode.setRedirect(upperBound);
                        nameAdded = true;
                    }
                    this.resolveOrFail(upperBound, classNode);
                }
                continue;
            }
            this.genericParameterNames.put(name, type);
            classNode.setRedirect(ClassHelper.OBJECT_TYPE);
            type.setPlaceholder(true);
        }
    }

    private boolean resolveGenericsType(GenericsType genericsType) {
        if (genericsType.isResolved()) {
            return true;
        }
        this.currentClass.setUsingGenerics(true);
        ClassNode type = genericsType.getType();
        String name = type.getName();
        ClassNode[] bounds = genericsType.getUpperBounds();
        if (!this.genericParameterNames.containsKey(name)) {
            if (bounds != null) {
                for (ClassNode upperBound : bounds) {
                    this.resolveOrFail(upperBound, genericsType);
                    type.setRedirect(upperBound);
                    this.resolveGenericsTypes(upperBound.getGenericsTypes());
                }
            } else if (genericsType.isWildcard()) {
                type.setRedirect(ClassHelper.OBJECT_TYPE);
            } else {
                this.resolveOrFail(type, genericsType);
            }
        } else {
            GenericsType gt = this.genericParameterNames.get(name);
            type.setRedirect(gt.getType());
            genericsType.setPlaceholder(true);
        }
        if (genericsType.getLowerBound() != null) {
            this.resolveOrFail(genericsType.getLowerBound(), genericsType);
        }
        if (this.resolveGenericsTypes(type.getGenericsTypes())) {
            genericsType.setResolved(genericsType.getType().isResolved());
        }
        return genericsType.isResolved();
    }

    public void setClassNodeResolver(ClassNodeResolver classNodeResolver) {
        this.classNodeResolver = classNodeResolver;
    }

    private static class LowerCaseClass
    extends ClassNode {
        String className;

        public LowerCaseClass(String name) {
            super(name, 1, ClassHelper.OBJECT_TYPE);
            this.isPrimaryNode = false;
            this.className = name;
        }

        @Override
        public String getName() {
            if (this.redirect() != this) {
                return super.getName();
            }
            return this.className;
        }

        @Override
        public boolean hasPackageName() {
            if (this.redirect() != this) {
                return super.hasPackageName();
            }
            return false;
        }

        @Override
        public String setName(String name) {
            if (this.redirect() != this) {
                return super.setName(name);
            }
            throw new GroovyBugError("LowerCaseClass#setName should not be called");
        }
    }

    private static class ConstructedClassWithPackage
    extends ClassNode {
        String prefix;
        String className;

        public ConstructedClassWithPackage(String pkg, String name) {
            super(pkg + name, 1, ClassHelper.OBJECT_TYPE);
            this.isPrimaryNode = false;
            this.prefix = pkg;
            this.className = name;
        }

        @Override
        public String getName() {
            if (this.redirect() != this) {
                return super.getName();
            }
            return this.prefix + this.className;
        }

        @Override
        public boolean hasPackageName() {
            if (this.redirect() != this) {
                return super.hasPackageName();
            }
            return this.className.indexOf(46) != -1;
        }

        @Override
        public String setName(String name) {
            if (this.redirect() != this) {
                return super.setName(name);
            }
            throw new GroovyBugError("ConstructedClassWithPackage#setName should not be called");
        }
    }

    private static class ConstructedNestedClass
    extends ClassNode {
        ClassNode knownEnclosingType;

        public ConstructedNestedClass(ClassNode outer, String inner) {
            inner = ResolveVisitor.replacePoints(inner);
            super(outer.getName() + "$" + inner, 1, ClassHelper.OBJECT_TYPE);
            this.knownEnclosingType = outer;
            this.isPrimaryNode = false;
        }

        @Override
        public boolean hasPackageName() {
            if (this.redirect() != this) {
                return super.hasPackageName();
            }
            return this.knownEnclosingType.hasPackageName();
        }

        @Override
        public String setName(String name) {
            if (this.redirect() != this) {
                return super.setName(name);
            }
            throw new GroovyBugError("ConstructedNestedClass#setName should not be called");
        }
    }
}


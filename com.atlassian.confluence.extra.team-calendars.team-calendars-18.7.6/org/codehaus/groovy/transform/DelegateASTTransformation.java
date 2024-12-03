/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.Delegate;
import groovy.lang.GroovyObject;
import groovy.lang.Lazy;
import groovy.lang.Reference;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class DelegateASTTransformation
extends AbstractASTTransformation {
    private static final Class MY_CLASS = Delegate.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode DEPRECATED_TYPE = ClassHelper.make(Deprecated.class);
    private static final ClassNode GROOVYOBJECT_TYPE = ClassHelper.make(GroovyObject.class);
    private static final ClassNode LAZY_TYPE = ClassHelper.make(Lazy.class);
    private static final String MEMBER_DEPRECATED = "deprecated";
    private static final String MEMBER_INTERFACES = "interfaces";
    private static final String MEMBER_INCLUDES = "includes";
    private static final String MEMBER_EXCLUDES = "excludes";
    private static final String MEMBER_INCLUDE_TYPES = "includeTypes";
    private static final String MEMBER_EXCLUDE_TYPES = "excludeTypes";
    private static final String MEMBER_PARAMETER_ANNOTATIONS = "parameterAnnotations";
    private static final String MEMBER_METHOD_ANNOTATIONS = "methodAnnotations";

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (parent instanceof FieldNode) {
            FieldNode fieldNode = (FieldNode)parent;
            ClassNode type = fieldNode.getType();
            ClassNode owner = fieldNode.getOwner();
            if (type.equals(ClassHelper.OBJECT_TYPE) || type.equals(GROOVYOBJECT_TYPE)) {
                this.addError(MY_TYPE_NAME + " field '" + fieldNode.getName() + "' has an inappropriate type: " + type.getName() + ". Please add an explicit type but not java.lang.Object or groovy.lang.GroovyObject.", parent);
                return;
            }
            if (type.equals(owner)) {
                this.addError(MY_TYPE_NAME + " field '" + fieldNode.getName() + "' has an inappropriate type: " + type.getName() + ". Delegation to own type not supported. Please use a different type.", parent);
                return;
            }
            List<MethodNode> fieldMethods = GeneralUtils.getAllMethods(type);
            for (ClassNode next : type.getAllInterfaces()) {
                fieldMethods.addAll(GeneralUtils.getAllMethods(next));
            }
            boolean skipInterfaces = this.memberHasValue(node, MEMBER_INTERFACES, false);
            boolean includeDeprecated = this.memberHasValue(node, MEMBER_DEPRECATED, true) || type.isInterface() && !skipInterfaces;
            List<String> excludes = DelegateASTTransformation.getMemberList(node, MEMBER_EXCLUDES);
            List<String> includes = DelegateASTTransformation.getMemberList(node, MEMBER_INCLUDES);
            List<ClassNode> excludeTypes = this.getClassList(node, MEMBER_EXCLUDE_TYPES);
            List<ClassNode> includeTypes = this.getClassList(node, MEMBER_INCLUDE_TYPES);
            this.checkIncludeExclude(node, excludes, includes, excludeTypes, includeTypes, MY_TYPE_NAME);
            List<MethodNode> ownerMethods = GeneralUtils.getAllMethods(owner);
            for (MethodNode mn : fieldMethods) {
                this.addDelegateMethod(node, fieldNode, owner, ownerMethods, mn, includeDeprecated, includes, excludes, includeTypes, excludeTypes);
            }
            for (PropertyNode prop : GeneralUtils.getAllProperties(type)) {
                if (prop.isStatic() || !prop.isPublic()) continue;
                String name = prop.getName();
                this.addGetterIfNeeded(fieldNode, owner, prop, name, includes, excludes);
                this.addSetterIfNeeded(fieldNode, owner, prop, name, includes, excludes);
            }
            if (type.isArray()) {
                boolean skipLength;
                boolean bl = skipLength = excludes != null && (excludes.contains("length") || excludes.contains("getLength"));
                if (!skipLength) {
                    owner.addMethod("getLength", 1, ClassHelper.int_TYPE, Parameter.EMPTY_ARRAY, null, GeneralUtils.returnS(GeneralUtils.propX((Expression)GeneralUtils.varX(fieldNode), "length")));
                }
            }
            if (skipInterfaces) {
                return;
            }
            Set<ClassNode> allInterfaces = GeneralUtils.getInterfacesAndSuperInterfaces(type);
            Set<ClassNode> ownerIfaces = owner.getAllInterfaces();
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(fieldNode.getDeclaringClass());
            genericsSpec = GenericsUtils.createGenericsSpec(fieldNode.getType(), genericsSpec);
            for (ClassNode iface : allInterfaces) {
                if (!Modifier.isPublic(iface.getModifiers()) || ownerIfaces.contains(iface)) continue;
                ClassNode[] ifaces = owner.getInterfaces();
                ClassNode[] newIfaces = new ClassNode[ifaces.length + 1];
                for (int i = 0; i < ifaces.length; ++i) {
                    newIfaces[i] = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, ifaces[i]);
                }
                newIfaces[ifaces.length] = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, iface);
                owner.setInterfaces(newIfaces);
            }
        }
    }

    private void addSetterIfNeeded(FieldNode fieldNode, ClassNode owner, PropertyNode prop, String name, List<String> includes, List<String> excludes) {
        String setterName = "set" + Verifier.capitalize(name);
        if ((prop.getModifiers() & 0x10) == 0 && owner.getSetterMethod(setterName) == null && owner.getProperty(name) == null && !this.shouldSkipPropertyMethod(name, setterName, excludes, includes)) {
            owner.addMethod(setterName, 1, ClassHelper.VOID_TYPE, GeneralUtils.params(new Parameter(GenericsUtils.nonGeneric(prop.getType()), "value")), null, GeneralUtils.assignS(GeneralUtils.propX((Expression)GeneralUtils.varX(fieldNode), name), GeneralUtils.varX("value")));
        }
    }

    private void addGetterIfNeeded(FieldNode fieldNode, ClassNode owner, PropertyNode prop, String name, List<String> includes, List<String> excludes) {
        boolean isPrimBool = prop.getOriginType().equals(ClassHelper.boolean_TYPE);
        boolean willHaveGetAccessor = true;
        boolean willHaveIsAccessor = isPrimBool;
        String suffix = Verifier.capitalize(name);
        if (isPrimBool) {
            ClassNode cNode = prop.getDeclaringClass();
            if (cNode.getGetterMethod("is" + suffix) != null && cNode.getGetterMethod("get" + suffix) == null) {
                willHaveGetAccessor = false;
            }
            if (cNode.getGetterMethod("get" + suffix) != null && cNode.getGetterMethod("is" + suffix) == null) {
                willHaveIsAccessor = false;
            }
        }
        Reference<Boolean> ownerWillHaveGetAccessor = new Reference<Boolean>();
        Reference<Boolean> ownerWillHaveIsAccessor = new Reference<Boolean>();
        DelegateASTTransformation.extractAccessorInfo(owner, name, ownerWillHaveGetAccessor, ownerWillHaveIsAccessor);
        for (String prefix : new String[]{"get", "is"}) {
            String getterName = prefix + suffix;
            if ((!prefix.equals("get") || !willHaveGetAccessor || ownerWillHaveGetAccessor.get().booleanValue()) && (!prefix.equals("is") || !willHaveIsAccessor || ownerWillHaveIsAccessor.get().booleanValue()) || this.shouldSkipPropertyMethod(name, getterName, excludes, includes)) continue;
            owner.addMethod(getterName, 1, GenericsUtils.nonGeneric(prop.getType()), Parameter.EMPTY_ARRAY, null, GeneralUtils.returnS(GeneralUtils.propX((Expression)GeneralUtils.varX(fieldNode), name)));
        }
    }

    private static void extractAccessorInfo(ClassNode owner, String name, Reference<Boolean> willHaveGetAccessor, Reference<Boolean> willHaveIsAccessor) {
        String suffix = Verifier.capitalize(name);
        boolean hasGetAccessor = owner.getGetterMethod("get" + suffix) != null;
        boolean hasIsAccessor = owner.getGetterMethod("is" + suffix) != null;
        PropertyNode prop = owner.getProperty(name);
        willHaveGetAccessor.set(hasGetAccessor || prop != null && !hasIsAccessor);
        willHaveIsAccessor.set(hasIsAccessor || prop != null && !hasGetAccessor && prop.getOriginType().equals(ClassHelper.boolean_TYPE));
    }

    private boolean shouldSkipPropertyMethod(String propertyName, String methodName, List<String> excludes, List<String> includes) {
        return DelegateASTTransformation.deemedInternalName(propertyName) || excludes != null && (excludes.contains(propertyName) || excludes.contains(methodName)) || includes != null && !includes.isEmpty() && !includes.contains(propertyName) && !includes.contains(methodName);
    }

    private void addDelegateMethod(AnnotationNode node, FieldNode fieldNode, ClassNode owner, List<MethodNode> ownMethods, MethodNode candidate, boolean includeDeprecated, List<String> includes, List<String> excludes, List<ClassNode> includeTypes, List<ClassNode> excludeTypes) {
        if (!candidate.isPublic() || candidate.isStatic() || 0 != (candidate.getModifiers() & 0x1000)) {
            return;
        }
        if (!candidate.getAnnotations(DEPRECATED_TYPE).isEmpty() && !includeDeprecated) {
            return;
        }
        if (DelegateASTTransformation.shouldSkip(candidate.getName(), excludes, includes)) {
            return;
        }
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(fieldNode.getDeclaringClass());
        genericsSpec = GenericsUtils.addMethodGenerics(candidate, genericsSpec);
        GenericsUtils.extractSuperClassGenerics(fieldNode.getType(), candidate.getDeclaringClass(), genericsSpec);
        if (!excludeTypes.isEmpty() || !includeTypes.isEmpty()) {
            Iterator<MethodNode> correctedMethodNode = GenericsUtils.correctToGenericsSpec(genericsSpec, candidate);
            boolean checkReturn = fieldNode.getType().getMethods().contains(candidate);
            if (DelegateASTTransformation.shouldSkipOnDescriptor(checkReturn, genericsSpec, correctedMethodNode, excludeTypes, includeTypes)) {
                return;
            }
        }
        for (MethodNode mn : GROOVYOBJECT_TYPE.getMethods()) {
            if (!mn.getTypeDescriptor().equals(candidate.getTypeDescriptor())) continue;
            return;
        }
        for (MethodNode mn : owner.getMethods()) {
            if (!mn.getTypeDescriptor().equals(candidate.getTypeDescriptor())) continue;
            return;
        }
        MethodNode existingNode = null;
        for (MethodNode mn : ownMethods) {
            if (!mn.getTypeDescriptor().equals(candidate.getTypeDescriptor()) || mn.isAbstract() || mn.isStatic()) continue;
            existingNode = mn;
            break;
        }
        if (existingNode == null || existingNode.getCode() == null) {
            ArgumentListExpression args = new ArgumentListExpression();
            Parameter[] params = candidate.getParameters();
            Parameter[] newParams = new Parameter[params.length];
            List<String> currentMethodGenPlaceholders = this.genericPlaceholderNames(candidate);
            for (int i = 0; i < newParams.length; ++i) {
                ClassNode newParamType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, params[i].getType(), currentMethodGenPlaceholders);
                Parameter newParam = new Parameter(newParamType, this.getParamName(params, i, fieldNode.getName()));
                newParam.setInitialExpression(params[i].getInitialExpression());
                if (this.memberHasValue(node, MEMBER_PARAMETER_ANNOTATIONS, true)) {
                    newParam.addAnnotations(this.copyAnnotatedNodeAnnotations(params[i], MY_TYPE_NAME));
                }
                newParams[i] = newParam;
                args.addExpression(GeneralUtils.varX(newParam));
            }
            boolean alsoLazy = !fieldNode.getAnnotations(LAZY_TYPE).isEmpty();
            MethodCallExpression mce = GeneralUtils.callX(alsoLazy ? GeneralUtils.propX((Expression)GeneralUtils.varX("this"), fieldNode.getName().substring(1)) : GeneralUtils.varX(fieldNode.getName(), GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, fieldNode.getType())), candidate.getName(), (Expression)args);
            mce.setSourcePosition(fieldNode);
            ClassNode returnType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, candidate.getReturnType(), currentMethodGenPlaceholders);
            MethodNode newMethod = owner.addMethod(candidate.getName(), candidate.getModifiers() & 0xFFFFFBFF & 0xFFFFFEFF, returnType, newParams, candidate.getExceptions(), GeneralUtils.stmt(mce));
            newMethod.setGenericsTypes(candidate.getGenericsTypes());
            if (this.memberHasValue(node, MEMBER_METHOD_ANNOTATIONS, true)) {
                newMethod.addAnnotations(this.copyAnnotatedNodeAnnotations(candidate, MY_TYPE_NAME));
            }
        }
    }

    private List<String> genericPlaceholderNames(MethodNode candidate) {
        GenericsType[] candidateGenericsTypes = candidate.getGenericsTypes();
        ArrayList<String> names = new ArrayList<String>();
        if (candidateGenericsTypes != null) {
            for (GenericsType gt : candidateGenericsTypes) {
                names.add(gt.getName());
            }
        }
        return names;
    }

    private String getParamName(Parameter[] params, int i, String fieldName) {
        String name = params[i].getName();
        while (name.equals(fieldName) || this.clashesWithOtherParams(name, params, i)) {
            name = "_" + name;
        }
        return name;
    }

    private boolean clashesWithOtherParams(String name, Parameter[] params, int i) {
        for (int j = 0; j < params.length; ++j) {
            if (i == j || !params[j].getName().equals(name)) continue;
            return true;
        }
        return false;
    }
}


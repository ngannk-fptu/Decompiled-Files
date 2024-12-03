/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.trait;

import groovy.transform.CompileStatic;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.ASTTransformationCollectorCodeVisitor;
import org.codehaus.groovy.transform.sc.StaticCompileTransformation;
import org.codehaus.groovy.transform.trait.SuperCallTraitTransformer;
import org.codehaus.groovy.transform.trait.TraitHelpersTuple;
import org.codehaus.groovy.transform.trait.Traits;

public abstract class TraitComposer {
    public static final ClassNode COMPILESTATIC_CLASSNODE = ClassHelper.make(CompileStatic.class);

    public static void doExtendTraits(ClassNode cNode, SourceUnit unit, CompilationUnit cu) {
        if (cNode.isInterface()) {
            return;
        }
        boolean isItselfTrait = Traits.isTrait(cNode);
        SuperCallTraitTransformer superCallTransformer = new SuperCallTraitTransformer(unit);
        if (isItselfTrait) {
            TraitComposer.checkTraitAllowed(cNode, unit);
            return;
        }
        if (!cNode.getNameWithoutPackage().endsWith("$Trait$Helper")) {
            List<ClassNode> traits = TraitComposer.findTraits(cNode);
            for (ClassNode trait : traits) {
                TraitHelpersTuple helpers = Traits.findHelpers(trait);
                TraitComposer.applyTrait(trait, cNode, helpers);
                superCallTransformer.visitClass(cNode);
                if (unit == null) continue;
                ASTTransformationCollectorCodeVisitor collector = new ASTTransformationCollectorCodeVisitor(unit, cu.getTransformLoader());
                collector.visitClass(cNode);
            }
        }
    }

    private static List<ClassNode> findTraits(ClassNode cNode) {
        LinkedHashSet<ClassNode> interfaces = new LinkedHashSet<ClassNode>();
        Traits.collectAllInterfacesReverseOrder(cNode, interfaces);
        LinkedList<ClassNode> traits = new LinkedList<ClassNode>();
        for (ClassNode candidate : interfaces) {
            if (!Traits.isAnnotatedWithTrait(candidate)) continue;
            traits.add(candidate);
        }
        return traits;
    }

    private static void checkTraitAllowed(ClassNode bottomTrait, SourceUnit unit) {
        ClassNode superClass = bottomTrait.getSuperClass();
        if (superClass == null || ClassHelper.OBJECT_TYPE.equals(superClass)) {
            return;
        }
        if (!Traits.isTrait(superClass)) {
            unit.addError(new SyntaxException("A trait can only inherit from another trait", superClass.getLineNumber(), superClass.getColumnNumber()));
        }
    }

    private static void applyTrait(ClassNode trait, ClassNode cNode, TraitHelpersTuple helpers) {
        ClassNode helperClassNode = helpers.getHelper();
        ClassNode fieldHelperClassNode = helpers.getFieldHelper();
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(cNode);
        genericsSpec = GenericsUtils.createGenericsSpec(trait, genericsSpec);
        for (MethodNode methodNode : helperClassNode.getAllDeclaredMethods()) {
            String name = methodNode.getName();
            Parameter[] helperMethodParams = methodNode.getParameters();
            boolean isAbstract = methodNode.isAbstract();
            if (isAbstract || helperMethodParams.length <= 0 || (methodNode.getModifiers() & 8) != 8 || name.contains("$") && (methodNode.getModifiers() & 0x1000) != 0) continue;
            ArgumentListExpression argList = new ArgumentListExpression();
            argList.addExpression(new VariableExpression("this"));
            Parameter[] origParams = new Parameter[helperMethodParams.length - 1];
            Parameter[] params = new Parameter[helperMethodParams.length - 1];
            System.arraycopy(methodNode.getParameters(), 1, params, 0, params.length);
            Map<String, ClassNode> methodGenericsSpec = new LinkedHashMap<String, ClassNode>(genericsSpec);
            MethodNode originalMethod = trait.getMethod(name, params);
            if (originalMethod != null) {
                methodGenericsSpec = GenericsUtils.addMethodGenerics(originalMethod, methodGenericsSpec);
            }
            for (int i = 1; i < helperMethodParams.length; ++i) {
                Parameter parameter = helperMethodParams[i];
                ClassNode originType = parameter.getOriginType();
                ClassNode fixedType = GenericsUtils.correctToGenericsSpecRecurse(methodGenericsSpec, originType);
                Parameter newParam = new Parameter(fixedType, "arg" + i);
                LinkedList<AnnotationNode> copied = new LinkedList<AnnotationNode>();
                LinkedList<AnnotationNode> notCopied = new LinkedList<AnnotationNode>();
                GeneralUtils.copyAnnotatedNodeAnnotations(parameter, copied, notCopied);
                newParam.addAnnotations(copied);
                params[i - 1] = newParam;
                origParams[i - 1] = parameter;
                argList.addExpression(new VariableExpression(params[i - 1]));
            }
            TraitComposer.createForwarderMethod(trait, cNode, methodNode, originalMethod, helperClassNode, methodGenericsSpec, helperMethodParams, origParams, params, argList);
        }
        cNode.addObjectInitializerStatements(new ExpressionStatement(new MethodCallExpression((Expression)new ClassExpression(helperClassNode), "$init$", (Expression)new ArgumentListExpression(new VariableExpression("this")))));
        MethodCallExpression staticInitCall = new MethodCallExpression((Expression)new ClassExpression(helperClassNode), "$static$init$", (Expression)new ArgumentListExpression(new ClassExpression(cNode)));
        MethodNode staticInitMethod = new MethodNode("$static$init$", 9, ClassHelper.VOID_TYPE, new Parameter[]{new Parameter(ClassHelper.CLASS_Type, "clazz")}, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
        staticInitMethod.setDeclaringClass(helperClassNode);
        staticInitCall.setMethodTarget(staticInitMethod);
        cNode.addStaticInitializerStatements(Collections.singletonList(new ExpressionStatement(staticInitCall)), false);
        if (fieldHelperClassNode != null && !cNode.declaresInterface(fieldHelperClassNode)) {
            cNode.addInterface(fieldHelperClassNode);
            LinkedList<MethodNode> declaredMethods = new LinkedList<MethodNode>();
            for (MethodNode declaredMethod : fieldHelperClassNode.getAllDeclaredMethods()) {
                if (declaredMethod.getName().endsWith("$get")) {
                    declaredMethods.add(0, declaredMethod);
                    continue;
                }
                declaredMethods.add(declaredMethod);
            }
            for (MethodNode methodNode : declaredMethods) {
                boolean finalSetter;
                Parameter[] newParams;
                String fieldName = methodNode.getName();
                if (!fieldName.endsWith("$get") && !fieldName.endsWith("$set")) continue;
                int suffixIdx = fieldName.lastIndexOf("$");
                fieldName = fieldName.substring(0, suffixIdx);
                String operation = methodNode.getName().substring(suffixIdx + 1);
                boolean getter = "get".equals(operation);
                ClassNode returnType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, methodNode.getReturnType());
                int fieldMods = 0;
                int isStatic = 0;
                boolean publicField = true;
                FieldNode helperField = null;
                fieldMods = 0;
                isStatic = 0;
                for (Integer mod : Traits.FIELD_PREFIXES) {
                    helperField = fieldHelperClassNode.getField(String.format("$0x%04x", mod) + fieldName);
                    if (helperField == null) continue;
                    if ((mod & 8) != 0) {
                        isStatic = 8;
                    }
                    fieldMods |= mod.intValue();
                    break;
                }
                if (helperField == null) {
                    helperField = fieldHelperClassNode.getField("$ins$0" + fieldName);
                    if (helperField == null) {
                        publicField = false;
                        helperField = fieldHelperClassNode.getField("$ins$1" + fieldName);
                    }
                    if (helperField == null) {
                        publicField = true;
                        helperField = fieldHelperClassNode.getField("$static$0" + fieldName);
                        if (helperField == null) {
                            publicField = false;
                            helperField = fieldHelperClassNode.getField("$static$1" + fieldName);
                        }
                        fieldMods |= 8;
                        isStatic = 8;
                    }
                    fieldMods |= publicField ? 1 : 2;
                }
                if (getter && helperField != null) {
                    LinkedList<AnnotationNode> copied = new LinkedList<AnnotationNode>();
                    LinkedList<AnnotationNode> notCopied = new LinkedList<AnnotationNode>();
                    GeneralUtils.copyAnnotatedNodeAnnotations(helperField, copied, notCopied);
                    FieldNode fieldNode = cNode.addField(fieldName, fieldMods, returnType, null);
                    fieldNode.addAnnotations(copied);
                    if (fieldNode.isFinal()) {
                        String baseName = fieldNode.isStatic() ? "$static$init$" : "$init$";
                        StaticMethodCallExpression mce = GeneralUtils.callX(helperClassNode, baseName + fieldNode.getName(), (Expression)GeneralUtils.args(fieldNode.isStatic() ? GeneralUtils.classX(cNode) : GeneralUtils.varX("this")));
                        Statement stmt = GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.varX(fieldNode.getName(), fieldNode.getType()), mce));
                        if (isStatic == 0) {
                            cNode.addObjectInitializerStatements(stmt);
                        } else {
                            cNode.addStaticInitializerStatements(Collections.singletonList(stmt), false);
                        }
                    }
                }
                if (getter) {
                    newParams = Parameter.EMPTY_ARRAY;
                } else {
                    ClassNode originType = methodNode.getParameters()[0].getOriginType();
                    ClassNode fixedType = originType.isGenericsPlaceHolder() ? ClassHelper.OBJECT_TYPE : GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, originType);
                    newParams = new Parameter[]{new Parameter(fixedType, "val")};
                }
                VariableExpression fieldExpr = GeneralUtils.varX(cNode.getField(fieldName));
                boolean bl = finalSetter = !getter && (fieldMods & 0x10) != 0;
                Statement body = getter ? GeneralUtils.returnS(fieldExpr) : (finalSetter ? null : GeneralUtils.stmt(new BinaryExpression(fieldExpr, Token.newSymbol(100, 0, 0), GeneralUtils.varX(newParams[0]))));
                MethodNode impl = new MethodNode(methodNode.getName(), 1 | isStatic, returnType, newParams, ClassNode.EMPTY_ARRAY, body);
                AnnotationNode an = new AnnotationNode(COMPILESTATIC_CLASSNODE);
                impl.addAnnotation(an);
                cNode.addTransform(StaticCompileTransformation.class, an);
                cNode.addMethod(impl);
            }
        }
    }

    private static void createForwarderMethod(ClassNode trait, ClassNode targetNode, MethodNode helperMethod, MethodNode originalMethod, ClassNode helperClassNode, Map<String, ClassNode> genericsSpec, Parameter[] helperMethodParams, Parameter[] traitMethodParams, Parameter[] forwarderParams, ArgumentListExpression helperMethodArgList) {
        MethodCallExpression mce = new MethodCallExpression((Expression)new ClassExpression(helperClassNode), helperMethod.getName(), (Expression)helperMethodArgList);
        mce.setImplicitThis(false);
        genericsSpec = GenericsUtils.addMethodGenerics(helperMethod, genericsSpec);
        ClassNode[] exceptionNodes = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, TraitComposer.copyExceptions(helperMethod.getExceptions()));
        ClassNode fixedReturnType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, helperMethod.getReturnType());
        boolean noCastRequired = genericsSpec.isEmpty() || fixedReturnType.getName().equals(ClassHelper.VOID_TYPE.getName());
        Expression forwardExpression = noCastRequired ? mce : new CastExpression(fixedReturnType, mce);
        int access = helperMethod.getModifiers();
        boolean isHelperForStaticMethod = helperMethodParams[0].getOriginType().equals(ClassHelper.CLASS_Type);
        if (Modifier.isPrivate(access) && !isHelperForStaticMethod) {
            return;
        }
        if (!isHelperForStaticMethod) {
            access ^= 8;
        }
        MethodNode forwarder = new MethodNode(helperMethod.getName(), access, fixedReturnType, forwarderParams, exceptionNodes, new ExpressionStatement(forwardExpression));
        LinkedList<AnnotationNode> copied = new LinkedList<AnnotationNode>();
        List<AnnotationNode> notCopied = Collections.emptyList();
        GeneralUtils.copyAnnotatedNodeAnnotations(helperMethod, copied, notCopied);
        if (!copied.isEmpty()) {
            forwarder.addAnnotations(copied);
        }
        if (originalMethod != null) {
            GenericsType[] newGt = GenericsUtils.applyGenericsContextToPlaceHolders(genericsSpec, originalMethod.getGenericsTypes());
            newGt = TraitComposer.removeNonPlaceHolders(newGt);
            forwarder.setGenericsTypes(newGt);
        } else {
            GenericsType[] genericsTypes = helperMethod.getGenericsTypes();
            if (genericsTypes != null) {
                Map<String, ClassNode> methodSpec = new HashMap<String, ClassNode>();
                methodSpec = GenericsUtils.addMethodGenerics(helperMethod, methodSpec);
                GenericsType[] newGt = GenericsUtils.applyGenericsContextToPlaceHolders(methodSpec, helperMethod.getGenericsTypes());
                forwarder.setGenericsTypes(newGt);
            }
        }
        AnnotationNode bridgeAnnotation = new AnnotationNode(Traits.TRAITBRIDGE_CLASSNODE);
        bridgeAnnotation.addMember("traitClass", new ClassExpression(trait));
        bridgeAnnotation.addMember("desc", new ConstantExpression(BytecodeHelper.getMethodDescriptor(helperMethod.getReturnType(), traitMethodParams)));
        forwarder.addAnnotation(bridgeAnnotation);
        if (!TraitComposer.shouldSkipMethod(targetNode, forwarder.getName(), forwarderParams)) {
            targetNode.addMethod(forwarder);
        }
        TraitComposer.createSuperForwarder(targetNode, forwarder, genericsSpec);
    }

    private static GenericsType[] removeNonPlaceHolders(GenericsType[] oldTypes) {
        if (oldTypes == null || oldTypes.length == 0) {
            return oldTypes;
        }
        ArrayList<GenericsType> l = new ArrayList<GenericsType>(Arrays.asList(oldTypes));
        Iterator<GenericsType> it = l.iterator();
        boolean modified = false;
        while (it.hasNext()) {
            GenericsType gt = it.next();
            if (gt.isPlaceholder()) continue;
            it.remove();
            modified = true;
        }
        if (!modified) {
            return oldTypes;
        }
        if (l.isEmpty()) {
            return null;
        }
        return l.toArray(new GenericsType[l.size()]);
    }

    private static void createSuperForwarder(ClassNode targetNode, MethodNode forwarder, Map<String, ClassNode> genericsSpec) {
        ArrayList<ClassNode> interfaces = new ArrayList<ClassNode>(Traits.collectAllInterfacesReverseOrder(targetNode, new LinkedHashSet<ClassNode>()));
        String name = forwarder.getName();
        Parameter[] forwarderParameters = forwarder.getParameters();
        LinkedHashSet<ClassNode> traits = new LinkedHashSet<ClassNode>();
        LinkedList<MethodNode> superForwarders = new LinkedList<MethodNode>();
        for (ClassNode node : interfaces) {
            MethodNode method;
            if (!Traits.isTrait(node) || (method = node.getDeclaredMethod(name, forwarderParameters)) == null) continue;
            traits.add(node);
            superForwarders.add(method);
        }
        for (MethodNode superForwarder : superForwarders) {
            TraitComposer.doCreateSuperForwarder(targetNode, superForwarder, traits.toArray(new ClassNode[traits.size()]), genericsSpec);
        }
    }

    private static void doCreateSuperForwarder(ClassNode targetNode, MethodNode forwarderMethod, ClassNode[] interfacesToGenerateForwarderFor, Map<String, ClassNode> genericsSpec) {
        int i;
        Parameter[] parameters = forwarderMethod.getParameters();
        Parameter[] superForwarderParams = new Parameter[parameters.length];
        for (i = 0; i < parameters.length; ++i) {
            Parameter parameter = parameters[i];
            ClassNode originType = parameter.getOriginType();
            superForwarderParams[i] = new Parameter(GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, originType), parameter.getName());
        }
        for (i = 0; i < interfacesToGenerateForwarderFor.length; ++i) {
            ClassNode current = interfacesToGenerateForwarderFor[i];
            ClassNode next = i < interfacesToGenerateForwarderFor.length - 1 ? interfacesToGenerateForwarderFor[i + 1] : null;
            String forwarderName = Traits.getSuperTraitMethodName(current, forwarderMethod.getName());
            if (targetNode.getDeclaredMethod(forwarderName, superForwarderParams) != null) continue;
            ClassNode returnType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, forwarderMethod.getReturnType());
            Statement delegate = next == null ? TraitComposer.createSuperFallback(forwarderMethod, returnType) : TraitComposer.createDelegatingForwarder(forwarderMethod, next);
            MethodNode methodNode = targetNode.addMethod(forwarderName, 4097, returnType, superForwarderParams, ClassNode.EMPTY_ARRAY, delegate);
            methodNode.setGenericsTypes(forwarderMethod.getGenericsTypes());
        }
    }

    private static Statement createSuperFallback(MethodNode forwarderMethod, ClassNode returnType) {
        Parameter[] forwarderMethodParameters;
        ArgumentListExpression args = new ArgumentListExpression();
        for (Parameter forwarderMethodParameter : forwarderMethodParameters = forwarderMethod.getParameters()) {
            args.addExpression(new VariableExpression(forwarderMethodParameter));
        }
        BinaryExpression instanceOfExpr = new BinaryExpression(new VariableExpression("this"), Token.newSymbol(544, -1, -1), new ClassExpression(Traits.GENERATED_PROXY_CLASSNODE));
        MethodCallExpression superCall = new MethodCallExpression((Expression)new VariableExpression("super"), forwarderMethod.getName(), (Expression)args);
        superCall.setImplicitThis(false);
        CastExpression proxyReceiver = new CastExpression(Traits.GENERATED_PROXY_CLASSNODE, new VariableExpression("this"));
        MethodCallExpression getProxy = new MethodCallExpression((Expression)proxyReceiver, "getProxyTarget", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        getProxy.setImplicitThis(true);
        StaticMethodCallExpression proxyCall = new StaticMethodCallExpression(ClassHelper.make(InvokerHelper.class), "invokeMethod", new ArgumentListExpression(getProxy, new ConstantExpression(forwarderMethod.getName()), new ArrayExpression(ClassHelper.OBJECT_TYPE, args.getExpressions())));
        IfStatement stmt = new IfStatement(new BooleanExpression(instanceOfExpr), new ExpressionStatement(new CastExpression(returnType, proxyCall)), new ExpressionStatement(superCall));
        return stmt;
    }

    private static Statement createDelegatingForwarder(MethodNode forwarderMethod, ClassNode next) {
        Statement result;
        Parameter[] forwarderMethodParameters;
        TraitHelpersTuple helpers = Traits.findHelpers(next);
        ArgumentListExpression args = new ArgumentListExpression();
        args.addExpression(new VariableExpression("this"));
        for (Parameter forwarderMethodParameter : forwarderMethodParameters = forwarderMethod.getParameters()) {
            args.addExpression(new VariableExpression(forwarderMethodParameter));
        }
        StaticMethodCallExpression delegateCall = new StaticMethodCallExpression(helpers.getHelper(), forwarderMethod.getName(), args);
        if (ClassHelper.VOID_TYPE.equals(forwarderMethod.getReturnType())) {
            BlockStatement stmt = new BlockStatement();
            stmt.addStatement(new ExpressionStatement(delegateCall));
            stmt.addStatement(new ReturnStatement(new ConstantExpression(null)));
            result = stmt;
        } else {
            result = new ReturnStatement(delegateCall);
        }
        return result;
    }

    private static ClassNode[] copyExceptions(ClassNode[] sourceExceptions) {
        ClassNode[] exceptionNodes = new ClassNode[sourceExceptions == null ? 0 : sourceExceptions.length];
        System.arraycopy(sourceExceptions, 0, exceptionNodes, 0, exceptionNodes.length);
        return exceptionNodes;
    }

    private static boolean shouldSkipMethod(ClassNode cNode, String name, Parameter[] params) {
        return TraitComposer.isExistingProperty(name, cNode, params) || cNode.getDeclaredMethod(name, params) != null;
    }

    private static boolean isExistingProperty(String methodName, ClassNode cNode, Parameter[] params) {
        String propertyName = methodName;
        boolean getter = false;
        if (methodName.startsWith("get")) {
            propertyName = propertyName.substring(3);
            getter = true;
        } else if (methodName.startsWith("is")) {
            propertyName = propertyName.substring(2);
            getter = true;
        } else if (methodName.startsWith("set")) {
            propertyName = propertyName.substring(3);
        } else {
            return false;
        }
        if (getter && params.length > 0) {
            return false;
        }
        if (!getter && params.length != 1) {
            return false;
        }
        if (propertyName.length() == 0) {
            return false;
        }
        PropertyNode pNode = cNode.getProperty(propertyName = MetaClassHelper.convertPropertyName(propertyName));
        return pNode != null;
    }
}


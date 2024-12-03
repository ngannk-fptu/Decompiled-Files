/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc;

import groovy.lang.Reference;
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.MopWriter;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.WriterControllerFactory;
import org.codehaus.groovy.classgen.asm.sc.StaticCompilationMopWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesTypeChooser;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticCompilationVisitor
extends StaticTypeCheckingVisitor {
    private static final ClassNode TYPECHECKED_CLASSNODE = ClassHelper.make(TypeChecked.class);
    private static final ClassNode COMPILESTATIC_CLASSNODE = ClassHelper.make(CompileStatic.class);
    private static final ClassNode[] TYPECHECKED_ANNOTATIONS = new ClassNode[]{TYPECHECKED_CLASSNODE, COMPILESTATIC_CLASSNODE};
    public static final ClassNode ARRAYLIST_CLASSNODE = ClassHelper.make(ArrayList.class);
    public static final MethodNode ARRAYLIST_CONSTRUCTOR;
    public static final MethodNode ARRAYLIST_ADD_METHOD;
    private final TypeChooser typeChooser = new StaticTypesTypeChooser();
    private ClassNode classNode;

    public StaticCompilationVisitor(SourceUnit unit, ClassNode node) {
        super(unit, node);
    }

    @Override
    protected ClassNode[] getTypeCheckingAnnotations() {
        return TYPECHECKED_ANNOTATIONS;
    }

    public static boolean isStaticallyCompiled(AnnotatedNode node) {
        if (node.getNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE) != null) {
            return (Boolean)node.getNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE);
        }
        if (node instanceof MethodNode) {
            return StaticCompilationVisitor.isStaticallyCompiled(node.getDeclaringClass());
        }
        if (node instanceof InnerClassNode) {
            return StaticCompilationVisitor.isStaticallyCompiled(((InnerClassNode)node).getOuterClass());
        }
        return false;
    }

    private void addPrivateFieldAndMethodAccessors(ClassNode node) {
        StaticCompilationVisitor.addPrivateBridgeMethods(node);
        StaticCompilationVisitor.addPrivateFieldsAccessors(node);
        Iterator<InnerClassNode> it = node.getInnerClasses();
        while (it.hasNext()) {
            this.addPrivateFieldAndMethodAccessors(it.next());
        }
    }

    private void addDynamicOuterClassAccessorsCallback(final ClassNode outer) {
        if (outer != null && !StaticCompilationVisitor.isStaticallyCompiled(outer) && outer.getNodeMetaData((Object)StaticCompilationMetadataKeys.DYNAMIC_OUTER_NODE_CALLBACK) == null) {
            outer.putNodeMetaData((Object)StaticCompilationMetadataKeys.DYNAMIC_OUTER_NODE_CALLBACK, new CompilationUnit.PrimaryClassNodeOperation(){

                @Override
                public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                    if (classNode == outer) {
                        StaticCompilationVisitor.addPrivateBridgeMethods(classNode);
                        StaticCompilationVisitor.addPrivateFieldsAccessors(classNode);
                    }
                }
            });
        }
    }

    @Override
    public void visitClass(ClassNode node) {
        boolean skip = this.shouldSkipClassNode(node);
        if (!skip && !this.anyMethodSkip(node)) {
            node.putNodeMetaData(MopWriter.Factory.class, StaticCompilationMopWriter.FACTORY);
        }
        ClassNode oldCN = this.classNode;
        this.classNode = node;
        Iterator<InnerClassNode> innerClasses = this.classNode.getInnerClasses();
        while (innerClasses.hasNext()) {
            InnerClassNode innerClassNode = innerClasses.next();
            boolean innerStaticCompile = !skip && !this.isSkippedInnerClass(innerClassNode);
            innerClassNode.putNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE, innerStaticCompile);
            innerClassNode.putNodeMetaData(WriterControllerFactory.class, node.getNodeMetaData(WriterControllerFactory.class));
            if (!innerStaticCompile || this.anyMethodSkip(innerClassNode)) continue;
            innerClassNode.putNodeMetaData(MopWriter.Factory.class, StaticCompilationMopWriter.FACTORY);
        }
        super.visitClass(node);
        this.addPrivateFieldAndMethodAccessors(node);
        if (StaticCompilationVisitor.isStaticallyCompiled(node)) {
            this.addDynamicOuterClassAccessorsCallback(node.getOuterClass());
        }
        this.classNode = oldCN;
    }

    private boolean anyMethodSkip(ClassNode node) {
        for (MethodNode methodNode : node.getMethods()) {
            if (!this.isSkipMode(methodNode)) continue;
            return true;
        }
        return false;
    }

    private void checkForConstructorWithCSButClassWithout(MethodNode node) {
        if (!(node instanceof ConstructorNode)) {
            return;
        }
        Object meta = node.getNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE);
        if (!Boolean.TRUE.equals(meta)) {
            return;
        }
        ClassNode clz = this.typeCheckingContext.getEnclosingClassNode();
        meta = clz.getNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE);
        if (Boolean.TRUE.equals(meta)) {
            return;
        }
        if (clz.getObjectInitializerStatements().isEmpty() && clz.getFields().isEmpty() && clz.getProperties().isEmpty()) {
            return;
        }
        this.addStaticTypeError("Cannot statically compile constructor implicitly including non static elements from object initializers, properties or fields.", node);
    }

    @Override
    public void visitMethod(MethodNode node) {
        if (this.isSkipMode(node)) {
            node.putNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE, false);
        }
        super.visitMethod(node);
        this.checkForConstructorWithCSButClassWithout(node);
        if (StaticCompilationVisitor.isStaticallyCompiled(node)) {
            this.addDynamicOuterClassAccessorsCallback(node.getDeclaringClass());
        }
    }

    private static void addPrivateFieldsAccessors(ClassNode node) {
        Set accessedFields = (Set)node.getNodeMetaData((Object)StaticTypesMarker.PV_FIELDS_ACCESS);
        Set mutatedFields = (Set)node.getNodeMetaData((Object)StaticTypesMarker.PV_FIELDS_MUTATION);
        if (accessedFields == null && mutatedFields == null) {
            return;
        }
        Map privateFieldAccessors = (Map)node.getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_FIELDS_ACCESSORS);
        Map privateFieldMutators = (Map)node.getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_FIELDS_MUTATORS);
        if (privateFieldAccessors != null || privateFieldMutators != null) {
            return;
        }
        int acc = -1;
        privateFieldAccessors = accessedFields != null ? new HashMap() : null;
        privateFieldMutators = mutatedFields != null ? new HashMap() : null;
        int access = 4105;
        for (FieldNode fieldNode : node.getFields()) {
            Expression receiver;
            Parameter param;
            boolean generateMutator;
            boolean generateAccessor = accessedFields != null && accessedFields.contains(fieldNode);
            boolean bl = generateMutator = mutatedFields != null && mutatedFields.contains(fieldNode);
            if (generateAccessor) {
                param = new Parameter(node.getPlainNodeReference(), "$that");
                receiver = fieldNode.isStatic() ? new ClassExpression(node) : new VariableExpression(param);
                ExpressionStatement stmt = new ExpressionStatement(new PropertyExpression(receiver, fieldNode.getName()));
                MethodNode accessor = node.addMethod("pfaccess$" + ++acc, 4105, fieldNode.getOriginType(), new Parameter[]{param}, ClassNode.EMPTY_ARRAY, stmt);
                privateFieldAccessors.put(fieldNode.getName(), accessor);
            }
            if (!generateMutator) continue;
            if (!generateAccessor) {
                ++acc;
            }
            param = new Parameter(node.getPlainNodeReference(), "$that");
            receiver = fieldNode.isStatic() ? new ClassExpression(node) : new VariableExpression(param);
            Parameter value = new Parameter(fieldNode.getOriginType(), "$value");
            Statement stmt = GeneralUtils.assignS(new PropertyExpression(receiver, fieldNode.getName()), new VariableExpression(value));
            MethodNode mutator = node.addMethod("pfaccess$0" + acc, 4105, fieldNode.getOriginType(), new Parameter[]{param, value}, ClassNode.EMPTY_ARRAY, stmt);
            privateFieldMutators.put(fieldNode.getName(), mutator);
        }
        if (privateFieldAccessors != null) {
            node.setNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_FIELDS_ACCESSORS, privateFieldAccessors);
        }
        if (privateFieldMutators != null) {
            node.setNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_FIELDS_MUTATORS, privateFieldMutators);
        }
    }

    private static void addPrivateBridgeMethods(ClassNode node) {
        Set accessedMethods = (Set)node.getNodeMetaData((Object)StaticTypesMarker.PV_METHODS_ACCESS);
        if (accessedMethods == null) {
            return;
        }
        ArrayList<MethodNode> methods = new ArrayList<MethodNode>(node.getAllDeclaredMethods());
        methods.addAll(node.getDeclaredConstructors());
        HashMap<MethodNode, ConstructorNode> privateBridgeMethods = (HashMap<MethodNode, ConstructorNode>)node.getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_BRIDGE_METHODS);
        if (privateBridgeMethods != null) {
            return;
        }
        privateBridgeMethods = new HashMap<MethodNode, ConstructorNode>();
        int i = -1;
        int access = 4105;
        for (MethodNode method : methods) {
            MethodNode bridge;
            ArgumentListExpression arguments;
            if (!accessedMethods.contains(method)) continue;
            List<String> methodSpecificGenerics = StaticCompilationVisitor.methodSpecificGenerics(method);
            ++i;
            ClassNode declaringClass = method.getDeclaringClass();
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(node);
            genericsSpec = GenericsUtils.addMethodGenerics(method, genericsSpec);
            GenericsUtils.extractSuperClassGenerics(node, declaringClass, genericsSpec);
            Parameter[] methodParameters = method.getParameters();
            Parameter[] newParams = new Parameter[methodParameters.length + 1];
            for (int j = 1; j < newParams.length; ++j) {
                Parameter orig = methodParameters[j - 1];
                newParams[j] = new Parameter(GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, orig.getOriginType(), methodSpecificGenerics), orig.getName());
            }
            if (method.getParameters() == null || method.getParameters().length == 0) {
                arguments = ArgumentListExpression.EMPTY_ARGUMENTS;
            } else {
                LinkedList<Expression> args = new LinkedList<Expression>();
                for (Parameter parameter : methodParameters) {
                    args.add(new VariableExpression(parameter));
                }
                arguments = new ArgumentListExpression(args);
            }
            if (method instanceof ConstructorNode) {
                ClassNode thatType = null;
                Iterator<InnerClassNode> innerClasses = node.getInnerClasses();
                if (innerClasses.hasNext()) {
                    thatType = innerClasses.next();
                } else {
                    thatType = new InnerClassNode(node.redirect(), node.getName() + "$1", 4104, ClassHelper.OBJECT_TYPE);
                    node.getModule().addClass(thatType);
                }
                newParams[0] = new Parameter(thatType.getPlainNodeReference(), "$that");
                ConstructorCallExpression cce = new ConstructorCallExpression(ClassNode.THIS, arguments);
                ExpressionStatement body = new ExpressionStatement(cce);
                bridge = node.addConstructor(4096, newParams, ClassNode.EMPTY_ARRAY, body);
            } else {
                newParams[0] = new Parameter(node.getPlainNodeReference(), "$that");
                Expression receiver = method.isStatic() ? new ClassExpression(node) : new VariableExpression(newParams[0]);
                MethodCallExpression mce = new MethodCallExpression(receiver, method.getName(), (Expression)arguments);
                mce.setMethodTarget(method);
                ExpressionStatement returnStatement = new ExpressionStatement(mce);
                bridge = node.addMethod("access$" + i, 4105, GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, method.getReturnType(), methodSpecificGenerics), newParams, method.getExceptions(), returnStatement);
            }
            GenericsType[] origGenericsTypes = method.getGenericsTypes();
            if (origGenericsTypes != null) {
                bridge.setGenericsTypes(GenericsUtils.applyGenericsContextToPlaceHolders(genericsSpec, origGenericsTypes));
            }
            privateBridgeMethods.put(method, (ConstructorNode)bridge);
            bridge.addAnnotation(new AnnotationNode(COMPILESTATIC_CLASSNODE));
        }
        if (!privateBridgeMethods.isEmpty()) {
            node.setNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_BRIDGE_METHODS, privateBridgeMethods);
        }
    }

    private static List<String> methodSpecificGenerics(MethodNode method) {
        ArrayList<String> genericTypeTokens = new ArrayList<String>();
        GenericsType[] candidateGenericsTypes = method.getGenericsTypes();
        if (candidateGenericsTypes != null) {
            for (GenericsType gt : candidateGenericsTypes) {
                genericTypeTokens.add(gt.getName());
            }
        }
        return genericTypeTokens;
    }

    private static void memorizeInitialExpressions(MethodNode node) {
        if (node.getParameters() != null) {
            for (Parameter parameter : node.getParameters()) {
                parameter.putNodeMetaData((Object)StaticTypesMarker.INITIAL_EXPRESSION, parameter.getInitialExpression());
            }
        }
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        super.visitMethodCallExpression(call);
        MethodNode target = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (target != null) {
            call.setMethodTarget(target);
            StaticCompilationVisitor.memorizeInitialExpressions(target);
        }
        if (call.getMethodTarget() == null && call.getLineNumber() > 0) {
            this.addError("Target method for method call expression hasn't been set", call);
        }
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call);
        MethodNode target = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (target == null && call.getLineNumber() > 0) {
            this.addError("Target constructor for constructor call expression hasn't been set", call);
        } else if (target == null) {
            ArgumentListExpression argumentListExpression = InvocationWriter.makeArgumentList(call.getArguments());
            List<Expression> expressions = argumentListExpression.getExpressions();
            ClassNode[] args = new ClassNode[expressions.size()];
            for (int i = 0; i < args.length; ++i) {
                args[i] = this.typeChooser.resolveType(expressions.get(i), this.classNode);
            }
            MethodNode constructor = this.findMethodOrFail(call, call.isSuperCall() ? this.classNode.getSuperClass() : this.classNode, "<init>", args);
            call.putNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, constructor);
            target = constructor;
        }
        if (target != null) {
            StaticCompilationVisitor.memorizeInitialExpressions(target);
        }
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        super.visitForLoop(forLoop);
        Expression collectionExpression = forLoop.getCollectionExpression();
        if (!(collectionExpression instanceof ClosureListExpression)) {
            ClassNode collectionType = this.getType(forLoop.getCollectionExpression());
            ClassNode componentType = StaticCompilationVisitor.inferLoopElementType(collectionType);
            forLoop.getVariable().setType(componentType);
            forLoop.getVariable().setOriginType(componentType);
        }
    }

    @Override
    protected MethodNode findMethodOrFail(Expression expr, ClassNode receiver, String name, ClassNode ... args) {
        MethodNode methodNode = super.findMethodOrFail(expr, receiver, name, args);
        if (expr instanceof BinaryExpression && methodNode != null) {
            expr.putNodeMetaData((Object)StaticCompilationMetadataKeys.BINARY_EXP_TARGET, new Object[]{methodNode, name});
        }
        return methodNode;
    }

    @Override
    protected boolean existsProperty(final PropertyExpression pexp, boolean checkForReadOnly, final ClassCodeVisitorSupport visitor) {
        Expression objectExpression = pexp.getObjectExpression();
        ClassNode objectExpressionType = this.getType(objectExpression);
        final Reference<ClassNode> rType = new Reference<ClassNode>(objectExpressionType);
        ClassCodeVisitorSupport receiverMemoizer = new ClassCodeVisitorSupport(){

            @Override
            protected SourceUnit getSourceUnit() {
                return null;
            }

            @Override
            public void visitField(FieldNode node) {
                ClassNode declaringClass;
                if (visitor != null) {
                    visitor.visitField(node);
                }
                if ((declaringClass = node.getDeclaringClass()) != null) {
                    if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(declaringClass, ClassHelper.LIST_TYPE)) {
                        boolean spread = declaringClass.getDeclaredField(node.getName()) != node;
                        pexp.setSpreadSafe(spread);
                    }
                    rType.set(declaringClass);
                }
            }

            @Override
            public void visitMethod(MethodNode node) {
                ClassNode declaringClass;
                if (visitor != null) {
                    visitor.visitMethod(node);
                }
                if ((declaringClass = node.getDeclaringClass()) != null) {
                    if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(declaringClass, ClassHelper.LIST_TYPE)) {
                        List<MethodNode> properties = declaringClass.getDeclaredMethods(node.getName());
                        boolean spread = true;
                        for (MethodNode mn : properties) {
                            if (node != mn) continue;
                            spread = false;
                            break;
                        }
                        pexp.setSpreadSafe(spread);
                    }
                    rType.set(declaringClass);
                }
            }

            @Override
            public void visitProperty(PropertyNode node) {
                ClassNode declaringClass;
                if (visitor != null) {
                    visitor.visitProperty(node);
                }
                if ((declaringClass = node.getDeclaringClass()) != null) {
                    if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(declaringClass, ClassHelper.LIST_TYPE)) {
                        List<PropertyNode> properties = declaringClass.getProperties();
                        boolean spread = true;
                        for (PropertyNode propertyNode : properties) {
                            if (propertyNode != node) continue;
                            spread = false;
                            break;
                        }
                        pexp.setSpreadSafe(spread);
                    }
                    rType.set(declaringClass);
                }
            }
        };
        boolean exists = super.existsProperty(pexp, checkForReadOnly, receiverMemoizer);
        if (exists) {
            if (objectExpression.getNodeMetaData((Object)StaticCompilationMetadataKeys.PROPERTY_OWNER) == null) {
                objectExpression.putNodeMetaData((Object)StaticCompilationMetadataKeys.PROPERTY_OWNER, rType.get());
            }
            if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(objectExpressionType, ClassHelper.LIST_TYPE)) {
                objectExpression.putNodeMetaData((Object)StaticCompilationMetadataKeys.COMPONENT_TYPE, this.inferComponentType(objectExpressionType, ClassHelper.int_TYPE));
            }
        }
        return exists;
    }

    @Override
    public void visitPropertyExpression(PropertyExpression pexp) {
        super.visitPropertyExpression(pexp);
        Object dynamic = pexp.getNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION);
        if (dynamic != null) {
            pexp.getObjectExpression().putNodeMetaData((Object)StaticCompilationMetadataKeys.RECEIVER_OF_DYNAMIC_PROPERTY, dynamic);
        }
    }

    static {
        ARRAYLIST_ADD_METHOD = ARRAYLIST_CLASSNODE.getMethod("add", new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE, "o")});
        ARRAYLIST_CONSTRUCTOR = new ConstructorNode(1, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
        ARRAYLIST_CONSTRUCTOR.setDeclaringClass(ARRAYLIST_CLASSNODE);
    }
}


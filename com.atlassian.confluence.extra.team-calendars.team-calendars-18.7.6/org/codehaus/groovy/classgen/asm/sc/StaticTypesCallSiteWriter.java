/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.CallSiteWriter;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.MethodCallerMultiAdapter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.sc.StaticInvocationWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesWriterController;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticTypesCallSiteWriter
extends CallSiteWriter
implements Opcodes {
    private static final ClassNode INVOKERHELPER_TYPE = ClassHelper.make(InvokerHelper.class);
    private static final MethodNode GROOVYOBJECT_GETPROPERTY_METHOD = ClassHelper.GROOVY_OBJECT_TYPE.getMethod("getProperty", new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "propertyName")});
    private static final MethodNode INVOKERHELPER_GETPROPERTY_METHOD = INVOKERHELPER_TYPE.getMethod("getProperty", new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE, "object"), new Parameter(ClassHelper.STRING_TYPE, "propertyName")});
    private static final MethodNode INVOKERHELPER_GETPROPERTYSAFE_METHOD = INVOKERHELPER_TYPE.getMethod("getPropertySafe", new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE, "object"), new Parameter(ClassHelper.STRING_TYPE, "propertyName")});
    private static final MethodNode CLOSURE_GETTHISOBJECT_METHOD = ClassHelper.CLOSURE_TYPE.getMethod("getThisObject", new Parameter[0]);
    private static final ClassNode COLLECTION_TYPE = ClassHelper.make(Collection.class);
    private static final MethodNode COLLECTION_SIZE_METHOD = COLLECTION_TYPE.getMethod("size", Parameter.EMPTY_ARRAY);
    private static final MethodNode MAP_GET_METHOD = ClassHelper.MAP_TYPE.getMethod("get", new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE, "key")});
    private StaticTypesWriterController controller;

    public StaticTypesCallSiteWriter(StaticTypesWriterController controller) {
        super(controller);
        this.controller = controller;
    }

    @Override
    public void generateCallSiteArray() {
        CallSiteWriter regularCallSiteWriter = this.controller.getRegularCallSiteWriter();
        if (regularCallSiteWriter.hasCallSiteUse()) {
            regularCallSiteWriter.generateCallSiteArray();
        }
    }

    @Override
    public void makeCallSite(Expression receiver, String message, Expression arguments, boolean safe, boolean implicitThis, boolean callCurrent, boolean callStatic) {
    }

    @Override
    public void makeGetPropertySite(Expression receiver, String methodName, boolean safe, boolean implicitThis) {
        List<MethodNode> methodNodes;
        boolean isStaticProperty;
        Variable variable;
        Object type;
        Object dynamic = receiver.getNodeMetaData((Object)StaticCompilationMetadataKeys.RECEIVER_OF_DYNAMIC_PROPERTY);
        if (dynamic != null) {
            this.makeDynamicGetProperty(receiver, methodName, safe);
            return;
        }
        TypeChooser typeChooser = this.controller.getTypeChooser();
        ClassNode classNode = this.controller.getClassNode();
        ClassNode receiverType = (ClassNode)receiver.getNodeMetaData((Object)StaticCompilationMetadataKeys.PROPERTY_OWNER);
        if (receiverType == null) {
            receiverType = typeChooser.resolveType(receiver, classNode);
        }
        if ((type = receiver.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE)) == null && receiver instanceof VariableExpression && (variable = ((VariableExpression)receiver).getAccessedVariable()) instanceof Expression) {
            type = ((Expression)((Object)variable)).getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
        }
        if (type != null) {
            receiverType = (ClassNode)type;
        }
        boolean isClassReceiver = false;
        if (StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(receiverType)) {
            isClassReceiver = true;
            receiverType = receiverType.getGenericsTypes()[0].getType();
        }
        if (ClassHelper.isPrimitiveType(receiverType)) {
            receiverType = ClassHelper.getWrapper(receiverType);
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (receiverType.isArray() && methodName.equals("length")) {
            receiver.visit(this.controller.getAcg());
            ClassNode arrayGetReturnType = typeChooser.resolveType(receiver, classNode);
            this.controller.getOperandStack().doGroovyCast(arrayGetReturnType);
            mv.visitInsn(190);
            this.controller.getOperandStack().replace(ClassHelper.int_TYPE);
            return;
        }
        if ((receiverType.implementsInterface(COLLECTION_TYPE) || COLLECTION_TYPE.equals(receiverType)) && ("size".equals(methodName) || "length".equals(methodName))) {
            MethodCallExpression expr = new MethodCallExpression(receiver, "size", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
            expr.setMethodTarget(COLLECTION_SIZE_METHOD);
            expr.setImplicitThis(implicitThis);
            expr.setSafe(safe);
            expr.visit(this.controller.getAcg());
            return;
        }
        boolean bl = isStaticProperty = receiver instanceof ClassExpression && (receiverType.isDerivedFrom(receiver.getType()) || receiverType.implementsInterface(receiver.getType()));
        if (!isStaticProperty && (receiverType.implementsInterface(ClassHelper.MAP_TYPE) || ClassHelper.MAP_TYPE.equals(receiverType))) {
            this.writeMapDotProperty(receiver, methodName, mv, safe);
            return;
        }
        if (this.makeGetPropertyWithGetter(receiver, receiverType, methodName, safe, implicitThis)) {
            return;
        }
        if (this.makeGetField(receiver, receiverType, methodName, safe, implicitThis, AsmClassGenerator.samePackages(receiverType.getPackageName(), classNode.getPackageName()))) {
            return;
        }
        if (receiver instanceof ClassExpression) {
            if (this.makeGetField(receiver, receiver.getType(), methodName, safe, implicitThis, AsmClassGenerator.samePackages(receiver.getType().getPackageName(), classNode.getPackageName()))) {
                return;
            }
            if (this.makeGetPropertyWithGetter(receiver, receiver.getType(), methodName, safe, implicitThis)) {
                return;
            }
            if (this.makeGetPrivateFieldWithBridgeMethod(receiver, receiver.getType(), methodName, safe, implicitThis)) {
                return;
            }
        }
        if (isClassReceiver) {
            if (this.makeGetPropertyWithGetter(receiver, ClassHelper.CLASS_Type, methodName, safe, implicitThis)) {
                return;
            }
            if (this.makeGetField(receiver, ClassHelper.CLASS_Type, methodName, safe, false, true)) {
                return;
            }
        }
        if (receiverType.isEnum()) {
            mv.visitFieldInsn(178, BytecodeHelper.getClassInternalName(receiverType), methodName, BytecodeHelper.getTypeDescription(receiverType));
            this.controller.getOperandStack().push(receiverType);
            return;
        }
        if (this.makeGetPrivateFieldWithBridgeMethod(receiver, receiverType, methodName, safe, implicitThis)) {
            return;
        }
        String getterName = "get" + MetaClassHelper.capitalize(methodName);
        String altGetterName = "is" + MetaClassHelper.capitalize(methodName);
        if (receiverType.isInterface()) {
            Set<ClassNode> allInterfaces = receiverType.getAllInterfaces();
            Object getterMethod = null;
            for (ClassNode anInterface : allInterfaces) {
                getterMethod = anInterface.getGetterMethod(getterName);
                if (getterMethod == null) {
                    getterMethod = anInterface.getGetterMethod(altGetterName);
                }
                if (getterMethod == null) continue;
                break;
            }
            if (getterMethod == null) {
                getterMethod = ClassHelper.OBJECT_TYPE.getGetterMethod(getterName);
            }
            if (getterMethod != null) {
                MethodCallExpression call = new MethodCallExpression(receiver, getterName, (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
                call.setMethodTarget((MethodNode)getterMethod);
                call.setImplicitThis(false);
                call.setSourcePosition(receiver);
                call.setSafe(safe);
                call.visit(this.controller.getAcg());
                return;
            }
        }
        List<MethodNode> methods = StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(this.controller.getSourceUnit().getClassLoader(), receiverType, getterName, ClassNode.EMPTY_ARRAY);
        for (MethodNode m : StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(this.controller.getSourceUnit().getClassLoader(), receiverType, altGetterName, ClassNode.EMPTY_ARRAY)) {
            if (!ClassHelper.Boolean_TYPE.equals(ClassHelper.getWrapper(m.getReturnType()))) continue;
            methods.add(m);
        }
        if (!methods.isEmpty() && (methodNodes = StaticTypeCheckingSupport.chooseBestMethod(receiverType, methods, ClassNode.EMPTY_ARRAY)).size() == 1) {
            MethodNode getter = methodNodes.get(0);
            MethodCallExpression call = new MethodCallExpression(receiver, getter.getName(), (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
            call.setMethodTarget(getter);
            call.setImplicitThis(false);
            call.setSafe(safe);
            call.setSourcePosition(receiver);
            call.visit(this.controller.getAcg());
            return;
        }
        if (!isStaticProperty && (receiverType.implementsInterface(ClassHelper.LIST_TYPE) || ClassHelper.LIST_TYPE.equals(receiverType))) {
            this.writeListDotProperty(receiver, methodName, mv, safe);
            return;
        }
        this.controller.getSourceUnit().addError(new SyntaxException("Access to " + (receiver instanceof ClassExpression ? receiver.getType() : receiverType).toString(false) + "#" + methodName + " is forbidden", receiver.getLineNumber(), receiver.getColumnNumber(), receiver.getLastLineNumber(), receiver.getLastColumnNumber()));
        this.controller.getMethodVisitor().visitInsn(1);
        this.controller.getOperandStack().push(ClassHelper.OBJECT_TYPE);
    }

    private void makeDynamicGetProperty(Expression receiver, String methodName, boolean safe) {
        MethodNode target = safe ? INVOKERHELPER_GETPROPERTYSAFE_METHOD : INVOKERHELPER_GETPROPERTY_METHOD;
        MethodCallExpression mce = new MethodCallExpression((Expression)new ClassExpression(INVOKERHELPER_TYPE), target.getName(), (Expression)new ArgumentListExpression(receiver, new ConstantExpression(methodName)));
        mce.setSafe(false);
        mce.setImplicitThis(false);
        mce.setMethodTarget(target);
        mce.visit(this.controller.getAcg());
    }

    private void writeMapDotProperty(Expression receiver, String methodName, MethodVisitor mv, boolean safe) {
        receiver.visit(this.controller.getAcg());
        Label exit = new Label();
        if (safe) {
            Label doGet = new Label();
            mv.visitJumpInsn(199, doGet);
            this.controller.getOperandStack().remove(1);
            mv.visitInsn(1);
            mv.visitJumpInsn(167, exit);
            mv.visitLabel(doGet);
            receiver.visit(this.controller.getAcg());
        }
        mv.visitLdcInsn(methodName);
        mv.visitMethodInsn(185, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        if (safe) {
            mv.visitLabel(exit);
        }
        this.controller.getOperandStack().replace(ClassHelper.OBJECT_TYPE);
    }

    private void writeListDotProperty(Expression receiver, String methodName, MethodVisitor mv, boolean safe) {
        ClassNode componentType = (ClassNode)receiver.getNodeMetaData((Object)StaticCompilationMetadataKeys.COMPONENT_TYPE);
        if (componentType == null) {
            componentType = ClassHelper.OBJECT_TYPE;
        }
        CompileStack compileStack = this.controller.getCompileStack();
        Label exit = new Label();
        if (safe) {
            receiver.visit(this.controller.getAcg());
            Label doGet = new Label();
            mv.visitJumpInsn(199, doGet);
            this.controller.getOperandStack().remove(1);
            mv.visitInsn(1);
            mv.visitJumpInsn(167, exit);
            mv.visitLabel(doGet);
        }
        VariableExpression tmpList = new VariableExpression("tmpList", ClassHelper.make(ArrayList.class));
        int var = compileStack.defineTemporaryVariable(tmpList, false);
        VariableExpression iterator = new VariableExpression("iterator", ClassHelper.Iterator_TYPE);
        int it = compileStack.defineTemporaryVariable(iterator, false);
        VariableExpression nextVar = new VariableExpression("next", componentType);
        final int next = compileStack.defineTemporaryVariable(nextVar, false);
        mv.visitTypeInsn(187, "java/util/ArrayList");
        mv.visitInsn(89);
        receiver.visit(this.controller.getAcg());
        mv.visitMethodInsn(185, "java/util/List", "size", "()I", true);
        this.controller.getOperandStack().remove(1);
        mv.visitMethodInsn(183, "java/util/ArrayList", "<init>", "(I)V", false);
        mv.visitVarInsn(58, var);
        Label l1 = new Label();
        mv.visitLabel(l1);
        receiver.visit(this.controller.getAcg());
        mv.visitMethodInsn(185, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
        this.controller.getOperandStack().remove(1);
        mv.visitVarInsn(58, it);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(25, it);
        mv.visitMethodInsn(185, "java/util/Iterator", "hasNext", "()Z", true);
        Label l3 = new Label();
        mv.visitJumpInsn(153, l3);
        mv.visitVarInsn(25, it);
        mv.visitMethodInsn(185, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(192, BytecodeHelper.getClassInternalName(componentType));
        mv.visitVarInsn(58, next);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitVarInsn(25, var);
        final ClassNode finalComponentType = componentType;
        PropertyExpression pexp = new PropertyExpression((Expression)new BytecodeExpression(){

            @Override
            public void visit(MethodVisitor mv) {
                mv.visitVarInsn(25, next);
            }

            @Override
            public ClassNode getType() {
                return finalComponentType;
            }
        }, methodName);
        pexp.visit(this.controller.getAcg());
        this.controller.getOperandStack().box();
        this.controller.getOperandStack().remove(1);
        mv.visitMethodInsn(185, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(87);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitJumpInsn(167, l2);
        mv.visitLabel(l3);
        mv.visitVarInsn(25, var);
        if (safe) {
            mv.visitLabel(exit);
        }
        this.controller.getOperandStack().push(ClassHelper.make(ArrayList.class));
        this.controller.getCompileStack().removeVar(next);
        this.controller.getCompileStack().removeVar(it);
        this.controller.getCompileStack().removeVar(var);
    }

    private boolean makeGetPrivateFieldWithBridgeMethod(Expression receiver, ClassNode receiverType, String fieldName, boolean safe, boolean implicitThis) {
        MethodNode methodNode;
        Map accessors;
        FieldNode field = receiverType.getField(fieldName);
        ClassNode outerClass = receiverType.getOuterClass();
        if (field == null && implicitThis && outerClass != null && !receiverType.isStaticClass()) {
            Expression pexp;
            if (this.controller.isInClosure()) {
                MethodCallExpression mce = new MethodCallExpression((Expression)new VariableExpression("this"), "getThisObject", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
                mce.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, this.controller.getOutermostClass());
                mce.setImplicitThis(true);
                mce.setMethodTarget(CLOSURE_GETTHISOBJECT_METHOD);
                pexp = new CastExpression(this.controller.getOutermostClass(), mce);
            } else {
                pexp = new PropertyExpression((Expression)new ClassExpression(outerClass), "this");
                pexp.setImplicitThis(true);
            }
            pexp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, outerClass);
            pexp.setSourcePosition(receiver);
            return this.makeGetPrivateFieldWithBridgeMethod(pexp, outerClass, fieldName, safe, true);
        }
        ClassNode classNode = this.controller.getClassNode();
        if (field != null && Modifier.isPrivate(field.getModifiers()) && (StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(receiverType, classNode) || StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(classNode, receiverType)) && !receiverType.equals(classNode) && (accessors = (Map)receiverType.redirect().getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_FIELDS_ACCESSORS)) != null && (methodNode = (MethodNode)accessors.get(fieldName)) != null) {
            MethodCallExpression mce = new MethodCallExpression(receiver, methodNode.getName(), (Expression)new ArgumentListExpression(field.isStatic() ? new ConstantExpression(null) : receiver));
            mce.setMethodTarget(methodNode);
            mce.setSafe(safe);
            mce.setImplicitThis(implicitThis);
            mce.visit(this.controller.getAcg());
            return true;
        }
        return false;
    }

    @Override
    public void makeGroovyObjectGetPropertySite(Expression receiver, String methodName, boolean safe, boolean implicitThis) {
        MethodCallExpression currentCall;
        TypeChooser typeChooser = this.controller.getTypeChooser();
        ClassNode classNode = this.controller.getClassNode();
        ClassNode receiverType = typeChooser.resolveType(receiver, classNode);
        if (receiver instanceof VariableExpression && ((VariableExpression)receiver).isThisExpression() && !this.controller.isInClosure()) {
            receiverType = classNode;
        }
        String property = methodName;
        if (implicitThis && this.controller.getInvocationWriter() instanceof StaticInvocationWriter && (currentCall = ((StaticInvocationWriter)this.controller.getInvocationWriter()).getCurrentCall()) != null && currentCall.getNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER) != null) {
            property = (String)currentCall.getNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER);
            String[] props = property.split("\\.");
            BytecodeExpression thisLoader = new BytecodeExpression(){

                @Override
                public void visit(MethodVisitor mv) {
                    mv.visitVarInsn(25, 0);
                }
            };
            thisLoader.setType(ClassHelper.CLOSURE_TYPE);
            PropertyExpression pexp = new PropertyExpression(thisLoader, new ConstantExpression(props[0]), safe);
            int propsLength = props.length;
            for (int i = 1; i < propsLength; ++i) {
                String prop = props[i];
                pexp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, ClassHelper.CLOSURE_TYPE);
                pexp = new PropertyExpression((Expression)pexp, prop);
            }
            ((ASTNode)pexp).visit(this.controller.getAcg());
            return;
        }
        if (this.makeGetPropertyWithGetter(receiver, receiverType, property, safe, implicitThis)) {
            return;
        }
        if (this.makeGetPrivateFieldWithBridgeMethod(receiver, receiverType, property, safe, implicitThis)) {
            return;
        }
        if (this.makeGetField(receiver, receiverType, property, safe, implicitThis, AsmClassGenerator.samePackages(receiverType.getPackageName(), classNode.getPackageName()))) {
            return;
        }
        MethodCallExpression call = new MethodCallExpression(receiver, "getProperty", (Expression)new ArgumentListExpression(new ConstantExpression(property)));
        call.setImplicitThis(implicitThis);
        call.setSafe(safe);
        call.setMethodTarget(GROOVYOBJECT_GETPROPERTY_METHOD);
        call.visit(this.controller.getAcg());
    }

    @Override
    public void makeCallSiteArrayInitializer() {
    }

    private boolean makeGetPropertyWithGetter(Expression receiver, ClassNode receiverType, String methodName, boolean safe, boolean implicitThis) {
        String getterName = "get" + MetaClassHelper.capitalize(methodName);
        MethodNode getterNode = receiverType.getGetterMethod(getterName);
        if (getterNode == null) {
            getterName = "is" + MetaClassHelper.capitalize(methodName);
            getterNode = receiverType.getGetterMethod(getterName);
        }
        if (getterNode != null && receiver instanceof ClassExpression && !ClassHelper.CLASS_Type.equals(receiverType) && !getterNode.isStatic()) {
            return false;
        }
        PropertyNode propertyNode = receiverType.getProperty(methodName);
        if (getterNode == null && propertyNode != null) {
            String prefix = "get";
            if (ClassHelper.boolean_TYPE.equals(propertyNode.getOriginType())) {
                prefix = "is";
            }
            getterName = prefix + MetaClassHelper.capitalize(methodName);
            getterNode = new MethodNode(getterName, 1, propertyNode.getOriginType(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
            getterNode.setDeclaringClass(receiverType);
            if (propertyNode.isStatic()) {
                getterNode.setModifiers(9);
            }
        }
        if (getterNode != null) {
            MethodCallExpression call = new MethodCallExpression(receiver, getterName, (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
            call.setSourcePosition(receiver);
            call.setMethodTarget(getterNode);
            call.setImplicitThis(implicitThis);
            call.setSafe(safe);
            call.visit(this.controller.getAcg());
            return true;
        }
        if (receiverType instanceof InnerClassNode && !receiverType.isStaticClass() && this.makeGetPropertyWithGetter(receiver, receiverType.getOuterClass(), methodName, safe, implicitThis)) {
            return true;
        }
        for (ClassNode node : receiverType.getInterfaces()) {
            if (!this.makeGetPropertyWithGetter(receiver, node, methodName, safe, implicitThis)) continue;
            return true;
        }
        ClassNode superClass = receiverType.getSuperClass();
        if (superClass != null) {
            return this.makeGetPropertyWithGetter(receiver, superClass, methodName, safe, implicitThis);
        }
        return false;
    }

    boolean makeGetField(Expression receiver, ClassNode receiverType, String fieldName, boolean safe, boolean implicitThis, boolean samePackage) {
        FieldNode field = receiverType.getField(fieldName);
        if (field != null && StaticTypesCallSiteWriter.isDirectAccessAllowed(field, this.controller.getClassNode(), samePackage)) {
            CompileStack compileStack = this.controller.getCompileStack();
            MethodVisitor mv = this.controller.getMethodVisitor();
            ClassNode replacementType = field.getOriginType();
            OperandStack operandStack = this.controller.getOperandStack();
            if (field.isStatic()) {
                mv.visitFieldInsn(178, BytecodeHelper.getClassInternalName(field.getOwner()), fieldName, BytecodeHelper.getTypeDescription(replacementType));
                operandStack.push(replacementType);
            } else {
                if (implicitThis) {
                    compileStack.pushImplicitThis(implicitThis);
                }
                receiver.visit(this.controller.getAcg());
                if (implicitThis) {
                    compileStack.popImplicitThis();
                }
                Label exit = new Label();
                if (safe) {
                    mv.visitInsn(89);
                    Label doGet = new Label();
                    mv.visitJumpInsn(199, doGet);
                    mv.visitInsn(87);
                    mv.visitInsn(1);
                    mv.visitJumpInsn(167, exit);
                    mv.visitLabel(doGet);
                }
                if (!operandStack.getTopOperand().isDerivedFrom(field.getOwner())) {
                    mv.visitTypeInsn(192, BytecodeHelper.getClassInternalName(field.getOwner()));
                }
                mv.visitFieldInsn(180, BytecodeHelper.getClassInternalName(field.getOwner()), fieldName, BytecodeHelper.getTypeDescription(replacementType));
                if (safe) {
                    if (ClassHelper.isPrimitiveType(replacementType)) {
                        operandStack.replace(replacementType);
                        operandStack.box();
                        replacementType = operandStack.getTopOperand();
                    }
                    mv.visitLabel(exit);
                }
            }
            operandStack.replace(replacementType);
            return true;
        }
        for (ClassNode intf : receiverType.getInterfaces()) {
            if (intf == receiverType || !this.makeGetField(receiver, intf, fieldName, safe, implicitThis, false)) continue;
            return true;
        }
        ClassNode superClass = receiverType.getSuperClass();
        if (superClass != null) {
            return this.makeGetField(receiver, superClass, fieldName, safe, implicitThis, false);
        }
        return false;
    }

    private static boolean isDirectAccessAllowed(FieldNode a, ClassNode receiver, boolean isSamePackage) {
        ClassNode receiverType;
        ClassNode declaringClass = a.getDeclaringClass().redirect();
        if (declaringClass.equals(receiverType = receiver.redirect())) {
            return true;
        }
        if (receiverType instanceof InnerClassNode) {
            while (receiverType != null && receiverType instanceof InnerClassNode) {
                if (declaringClass.equals(receiverType)) {
                    return true;
                }
                receiverType = receiverType.getOuterClass();
            }
        }
        return a.isPublic() || a.isProtected() && isSamePackage;
    }

    @Override
    public void makeSiteEntry() {
    }

    @Override
    public void prepareCallSite(String message) {
    }

    @Override
    public void makeSingleArgumentCall(Expression receiver, String message, Expression arguments) {
        ClassNode aType;
        ClassNode classNode;
        TypeChooser typeChooser = this.controller.getTypeChooser();
        ClassNode rType = typeChooser.resolveType(receiver, classNode = this.controller.getClassNode());
        if (this.trySubscript(receiver, message, arguments, rType, aType = typeChooser.resolveType(arguments, classNode))) {
            return;
        }
        rType = (ClassNode)receiver.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
        if (receiver instanceof VariableExpression && rType == null) {
            VariableExpression ve = (VariableExpression)((VariableExpression)receiver).getAccessedVariable();
            rType = (ClassNode)ve.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE);
        }
        if (rType != null && this.trySubscript(receiver, message, arguments, rType, aType)) {
            return;
        }
        throw new GroovyBugError("At line " + receiver.getLineNumber() + " column " + receiver.getColumnNumber() + "\nOn receiver: " + receiver.getText() + " with message: " + message + " and arguments: " + arguments.getText() + "\nThis method should not have been called. Please try to create a simple example reproducing\nthis error and file a bug report at https://issues.apache.org/jira/browse/GROOVY");
    }

    private boolean trySubscript(Expression receiver, String message, Expression arguments, ClassNode rType, ClassNode aType) {
        if (ClassHelper.getWrapper(rType).isDerivedFrom(ClassHelper.Number_TYPE) && ClassHelper.getWrapper(aType).isDerivedFrom(ClassHelper.Number_TYPE)) {
            if ("plus".equals(message) || "minus".equals(message) || "multiply".equals(message) || "div".equals(message)) {
                this.writeNumberNumberCall(receiver, message, arguments);
                return true;
            }
            if ("power".equals(message)) {
                this.writePowerCall(receiver, arguments, rType, aType);
                return true;
            }
            if ("mod".equals(message) || "leftShift".equals(message) || "rightShift".equals(message) || "rightShiftUnsigned".equals(message) || "and".equals(message) || "or".equals(message) || "xor".equals(message)) {
                this.writeOperatorCall(receiver, arguments, message);
                return true;
            }
        } else {
            if (ClassHelper.STRING_TYPE.equals(rType) && "plus".equals(message)) {
                this.writeStringPlusCall(receiver, message, arguments);
                return true;
            }
            if ("getAt".equals(message)) {
                if (rType.isArray() && ClassHelper.getWrapper(aType).isDerivedFrom(ClassHelper.Number_TYPE)) {
                    this.writeArrayGet(receiver, arguments, rType, aType);
                    return true;
                }
                MethodNode getAtNode = null;
                for (ClassNode current = rType; current != null && getAtNode == null; current = current.getSuperClass()) {
                    getAtNode = current.getMethod("getAt", new Parameter[]{new Parameter(aType, "index")});
                    if (getAtNode == null && ClassHelper.isPrimitiveType(aType)) {
                        getAtNode = current.getMethod("getAt", new Parameter[]{new Parameter(ClassHelper.getWrapper(aType), "index")});
                        continue;
                    }
                    if (getAtNode != null || !aType.isDerivedFrom(ClassHelper.Number_TYPE)) continue;
                    getAtNode = current.getMethod("getAt", new Parameter[]{new Parameter(ClassHelper.getUnwrapper(aType), "index")});
                }
                if (getAtNode != null) {
                    MethodCallExpression call = new MethodCallExpression(receiver, "getAt", arguments);
                    call.setSourcePosition(arguments);
                    call.setImplicitThis(false);
                    call.setMethodTarget(getAtNode);
                    call.visit(this.controller.getAcg());
                    return true;
                }
                ClassNode[] args = new ClassNode[]{aType};
                boolean acceptAnyMethod = ClassHelper.MAP_TYPE.equals(rType) || rType.implementsInterface(ClassHelper.MAP_TYPE) || ClassHelper.LIST_TYPE.equals(rType) || rType.implementsInterface(ClassHelper.LIST_TYPE);
                List<MethodNode> nodes = StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(this.controller.getSourceUnit().getClassLoader(), rType, message, args);
                if (nodes.isEmpty()) {
                    rType = rType.getPlainNodeReference();
                    nodes = StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(this.controller.getSourceUnit().getClassLoader(), rType, message, args);
                }
                if ((nodes = StaticTypeCheckingSupport.chooseBestMethod(rType, nodes, args)).size() == 1 || nodes.size() > 1 && acceptAnyMethod) {
                    MethodNode methodNode = nodes.get(0);
                    MethodCallExpression call = new MethodCallExpression(receiver, message, arguments);
                    call.setSourcePosition(arguments);
                    call.setImplicitThis(false);
                    call.setMethodTarget(methodNode);
                    call.visit(this.controller.getAcg());
                    return true;
                }
                if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(rType, ClassHelper.MAP_TYPE)) {
                    MethodCallExpression call = new MethodCallExpression(receiver, "get", arguments);
                    call.setMethodTarget(MAP_GET_METHOD);
                    call.setSourcePosition(arguments);
                    call.setImplicitThis(false);
                    call.visit(this.controller.getAcg());
                    return true;
                }
            }
        }
        return false;
    }

    private void writeArrayGet(Expression receiver, Expression arguments, ClassNode rType, ClassNode aType) {
        OperandStack operandStack = this.controller.getOperandStack();
        int m1 = operandStack.getStackLength();
        receiver.visit(this.controller.getAcg());
        arguments.visit(this.controller.getAcg());
        operandStack.doGroovyCast(ClassHelper.int_TYPE);
        int m2 = operandStack.getStackLength();
        this.controller.getMethodVisitor().visitInsn(50);
        operandStack.replace(rType.getComponentType(), m2 - m1);
    }

    private void writeOperatorCall(Expression receiver, Expression arguments, String operator) {
        this.prepareSiteAndReceiver(receiver, operator, false, this.controller.getCompileStack().isLHS());
        this.controller.getOperandStack().doGroovyCast(ClassHelper.Number_TYPE);
        this.visitBoxedArgument(arguments);
        this.controller.getOperandStack().doGroovyCast(ClassHelper.Number_TYPE);
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/typehandling/NumberMath", operator, "(Ljava/lang/Number;Ljava/lang/Number;)Ljava/lang/Number;", false);
        this.controller.getOperandStack().replace(ClassHelper.Number_TYPE, 2);
    }

    private void writePowerCall(Expression receiver, Expression arguments, ClassNode rType, ClassNode aType) {
        OperandStack operandStack = this.controller.getOperandStack();
        int m1 = operandStack.getStackLength();
        this.prepareSiteAndReceiver(receiver, "power", false, this.controller.getCompileStack().isLHS());
        operandStack.doGroovyCast(ClassHelper.getWrapper(rType));
        this.visitBoxedArgument(arguments);
        operandStack.doGroovyCast(ClassHelper.getWrapper(aType));
        int m2 = operandStack.getStackLength();
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (ClassHelper.BigDecimal_TYPE.equals(rType) && ClassHelper.Integer_TYPE.equals(ClassHelper.getWrapper(aType))) {
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/DefaultGroovyMethods", "power", "(Ljava/math/BigDecimal;Ljava/lang/Integer;)Ljava/lang/Number;", false);
        } else if (ClassHelper.BigInteger_TYPE.equals(rType) && ClassHelper.Integer_TYPE.equals(ClassHelper.getWrapper(aType))) {
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/DefaultGroovyMethods", "power", "(Ljava/math/BigInteger;Ljava/lang/Integer;)Ljava/lang/Number;", false);
        } else if (ClassHelper.Long_TYPE.equals(ClassHelper.getWrapper(rType)) && ClassHelper.Integer_TYPE.equals(ClassHelper.getWrapper(aType))) {
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/DefaultGroovyMethods", "power", "(Ljava/lang/Long;Ljava/lang/Integer;)Ljava/lang/Number;", false);
        } else if (ClassHelper.Integer_TYPE.equals(ClassHelper.getWrapper(rType)) && ClassHelper.Integer_TYPE.equals(ClassHelper.getWrapper(aType))) {
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/DefaultGroovyMethods", "power", "(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Number;", false);
        } else {
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/DefaultGroovyMethods", "power", "(Ljava/lang/Number;Ljava/lang/Number;)Ljava/lang/Number;", false);
        }
        this.controller.getOperandStack().replace(ClassHelper.Number_TYPE, m2 - m1);
    }

    private void writeStringPlusCall(Expression receiver, String message, Expression arguments) {
        OperandStack operandStack = this.controller.getOperandStack();
        int m1 = operandStack.getStackLength();
        this.prepareSiteAndReceiver(receiver, message, false, this.controller.getCompileStack().isLHS());
        this.visitBoxedArgument(arguments);
        int m2 = operandStack.getStackLength();
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/DefaultGroovyMethods", "plus", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;", false);
        this.controller.getOperandStack().replace(ClassHelper.STRING_TYPE, m2 - m1);
    }

    private void writeNumberNumberCall(Expression receiver, String message, Expression arguments) {
        OperandStack operandStack = this.controller.getOperandStack();
        int m1 = operandStack.getStackLength();
        this.prepareSiteAndReceiver(receiver, message, false, this.controller.getCompileStack().isLHS());
        this.controller.getOperandStack().doGroovyCast(ClassHelper.Number_TYPE);
        this.visitBoxedArgument(arguments);
        this.controller.getOperandStack().doGroovyCast(ClassHelper.Number_TYPE);
        int m2 = operandStack.getStackLength();
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/dgmimpl/NumberNumber" + MetaClassHelper.capitalize(message), message, "(Ljava/lang/Number;Ljava/lang/Number;)Ljava/lang/Number;", false);
        this.controller.getOperandStack().replace(ClassHelper.Number_TYPE, m2 - m1);
    }

    @Override
    public void fallbackAttributeOrPropertySite(PropertyExpression expression, Expression objectExpression, String name, MethodCallerMultiAdapter adapter) {
        if (name != null && (adapter == AsmClassGenerator.setField || adapter == AsmClassGenerator.setGroovyObjectField)) {
            TypeChooser typeChooser = this.controller.getTypeChooser();
            ClassNode classNode = this.controller.getClassNode();
            ClassNode rType = typeChooser.resolveType(objectExpression, classNode);
            if (this.controller.getCompileStack().isLHS() ? this.setField(expression, objectExpression, rType, name) : this.getField(expression, objectExpression, rType, name)) {
                return;
            }
        }
        super.fallbackAttributeOrPropertySite(expression, objectExpression, name, adapter);
    }

    private boolean setField(PropertyExpression expression, Expression objectExpression, ClassNode rType, String name) {
        if (expression.isSafe()) {
            return false;
        }
        FieldNode fn = AsmClassGenerator.getDeclaredFieldOfCurrentClassOrAccessibleFieldOfSuper(this.controller.getClassNode(), rType, name, false);
        if (fn == null) {
            return false;
        }
        OperandStack stack = this.controller.getOperandStack();
        stack.doGroovyCast(fn.getType());
        MethodVisitor mv = this.controller.getMethodVisitor();
        String ownerName = BytecodeHelper.getClassInternalName(fn.getOwner());
        if (!fn.isStatic()) {
            this.controller.getCompileStack().pushLHS(false);
            objectExpression.visit(this.controller.getAcg());
            this.controller.getCompileStack().popLHS();
            if (!rType.equals(stack.getTopOperand())) {
                BytecodeHelper.doCast(mv, rType);
                stack.replace(rType);
            }
            stack.swap();
            mv.visitFieldInsn(181, ownerName, name, BytecodeHelper.getTypeDescription(fn.getType()));
            stack.remove(1);
        } else {
            mv.visitFieldInsn(179, ownerName, name, BytecodeHelper.getTypeDescription(fn.getType()));
        }
        return true;
    }

    private boolean getField(PropertyExpression expression, Expression receiver, ClassNode receiverType, String name) {
        boolean implicitThis;
        ClassNode classNode = this.controller.getClassNode();
        boolean safe = expression.isSafe();
        if (this.makeGetField(receiver, receiverType, name, safe, implicitThis = expression.isImplicitThis(), AsmClassGenerator.samePackages(receiverType.getPackageName(), classNode.getPackageName()))) {
            return true;
        }
        if (receiver instanceof ClassExpression) {
            if (this.makeGetField(receiver, receiver.getType(), name, safe, implicitThis, AsmClassGenerator.samePackages(receiver.getType().getPackageName(), classNode.getPackageName()))) {
                return true;
            }
            if (this.makeGetPrivateFieldWithBridgeMethod(receiver, receiver.getType(), name, safe, implicitThis)) {
                return true;
            }
        }
        if (this.makeGetPrivateFieldWithBridgeMethod(receiver, receiverType, name, safe, implicitThis)) {
            return true;
        }
        boolean isClassReceiver = false;
        if (StaticTypeCheckingSupport.isClassClassNodeWrappingConcreteType(receiverType)) {
            isClassReceiver = true;
            receiverType = receiverType.getGenericsTypes()[0].getType();
        }
        if (isClassReceiver && this.makeGetField(receiver, ClassHelper.CLASS_Type, name, safe, false, true)) {
            return true;
        }
        if (receiverType.isEnum()) {
            this.controller.getMethodVisitor().visitFieldInsn(178, BytecodeHelper.getClassInternalName(receiverType), name, BytecodeHelper.getTypeDescription(receiverType));
            this.controller.getOperandStack().push(receiverType);
            return true;
        }
        return false;
    }
}


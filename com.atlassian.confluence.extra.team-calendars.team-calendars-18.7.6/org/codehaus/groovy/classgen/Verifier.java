/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.ClassNodeUtils;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.ast.tools.PropertyNodeUtils;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.BytecodeInstruction;
import org.codehaus.groovy.classgen.BytecodeSequence;
import org.codehaus.groovy.classgen.ReturnAdder;
import org.codehaus.groovy.classgen.VerifierCodeVisitor;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.MopWriter;
import org.codehaus.groovy.classgen.asm.OptimizingStatementWriter;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.RuntimeParserException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.trait.Traits;

public class Verifier
implements GroovyClassVisitor,
Opcodes {
    public static final String STATIC_METACLASS_BOOL = "__$stMC";
    public static final String SWAP_INIT = "__$swapInit";
    public static final String INITIAL_EXPRESSION = "INITIAL_EXPRESSION";
    public static final String DEFAULT_PARAMETER_GENERATED = "DEFAULT_PARAMETER_GENERATED";
    public static final String __TIMESTAMP = "__timeStamp";
    public static final String __TIMESTAMP__ = "__timeStamp__239_neverHappen";
    private static final Parameter[] INVOKE_METHOD_PARAMS = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "method"), new Parameter(ClassHelper.OBJECT_TYPE, "arguments")};
    private static final Parameter[] SET_PROPERTY_PARAMS = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "property"), new Parameter(ClassHelper.OBJECT_TYPE, "value")};
    private static final Parameter[] GET_PROPERTY_PARAMS = new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "property")};
    private static final Parameter[] SET_METACLASS_PARAMS = new Parameter[]{new Parameter(ClassHelper.METACLASS_TYPE, "mc")};
    private ClassNode classNode;
    private MethodNode methodNode;

    public ClassNode getClassNode() {
        return this.classNode;
    }

    protected void setClassNode(ClassNode classNode) {
        this.classNode = classNode;
    }

    public MethodNode getMethodNode() {
        return this.methodNode;
    }

    private static FieldNode setMetaClassFieldIfNotExists(ClassNode node, FieldNode metaClassField) {
        if (metaClassField != null) {
            return metaClassField;
        }
        final String classInternalName = BytecodeHelper.getClassInternalName(node);
        metaClassField = node.addField("metaClass", 4226, ClassHelper.METACLASS_TYPE, new BytecodeExpression(ClassHelper.METACLASS_TYPE){

            @Override
            public void visit(MethodVisitor mv) {
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(182, classInternalName, "$getStaticMetaClass", "()Lgroovy/lang/MetaClass;", false);
            }
        });
        metaClassField.setSynthetic(true);
        return metaClassField;
    }

    private static FieldNode getMetaClassField(ClassNode node) {
        FieldNode ret = node.getDeclaredField("metaClass");
        if (ret != null) {
            ClassNode mcFieldType = ret.getType();
            if (!mcFieldType.equals(ClassHelper.METACLASS_TYPE)) {
                throw new RuntimeParserException("The class " + node.getName() + " cannot declare field 'metaClass' of type " + mcFieldType.getName() + " as it needs to be of the type " + ClassHelper.METACLASS_TYPE.getName() + " for internal groovy purposes", ret);
            }
            return ret;
        }
        ClassNode current = node;
        while (current != ClassHelper.OBJECT_TYPE && (current = current.getSuperClass()) != null) {
            ret = current.getDeclaredField("metaClass");
            if (ret == null || Modifier.isPrivate(ret.getModifiers())) continue;
            return ret;
        }
        return null;
    }

    @Override
    public void visitClass(ClassNode node) {
        this.classNode = node;
        if (Traits.isTrait(node) || this.classNode.isInterface()) {
            ConstructorNode dummy = new ConstructorNode(0, null);
            this.addInitialization(node, dummy);
            node.visitContents(this);
            if (this.classNode.getNodeMetaData(OptimizingStatementWriter.ClassNodeSkip.class) == null) {
                this.classNode.setNodeMetaData(OptimizingStatementWriter.ClassNodeSkip.class, true);
            }
            return;
        }
        ClassNode[] classNodes = this.classNode.getInterfaces();
        ArrayList<String> interfaces = new ArrayList<String>();
        for (ClassNode classNode : classNodes) {
            interfaces.add(classNode.getName());
        }
        HashSet interfaceSet = new HashSet(interfaces);
        if (interfaceSet.size() != interfaces.size()) {
            throw new RuntimeParserException("Duplicate interfaces in implements list: " + interfaces, this.classNode);
        }
        this.addDefaultParameterMethods(node);
        this.addDefaultParameterConstructors(node);
        String classInternalName = BytecodeHelper.getClassInternalName(node);
        this.addStaticMetaClassField(node, classInternalName);
        boolean knownSpecialCase = node.isDerivedFrom(ClassHelper.GSTRING_TYPE) || node.isDerivedFrom(ClassHelper.GROOVY_OBJECT_SUPPORT_TYPE);
        Verifier.addFastPathHelperFieldsAndHelperMethod(node, classInternalName, knownSpecialCase);
        if (!knownSpecialCase) {
            this.addGroovyObjectInterfaceAndMethods(node, classInternalName);
        }
        this.addDefaultConstructor(node);
        this.addInitialization(node);
        Verifier.checkReturnInObjectInitializer(node.getObjectInitializerStatements());
        node.getObjectInitializerStatements().clear();
        node.visitContents(this);
        Verifier.checkForDuplicateMethods(node);
        this.addCovariantMethods(node);
    }

    private static void checkForDuplicateMethods(ClassNode cn) {
        HashSet<String> descriptors = new HashSet<String>();
        for (MethodNode mn : cn.getMethods()) {
            if (mn.isSynthetic()) continue;
            String mySig = GeneralUtils.makeDescriptorWithoutReturnType(mn);
            if (descriptors.contains(mySig)) {
                if (mn.isScriptBody() || mySig.equals(Verifier.scriptBodySignatureWithoutReturnType(cn))) {
                    throw new RuntimeParserException("The method " + mn.getText() + " is a duplicate of the one declared for this script's body code", mn);
                }
                throw new RuntimeParserException("The method " + mn.getText() + " duplicates another method of the same signature", mn);
            }
            descriptors.add(mySig);
        }
    }

    private static String scriptBodySignatureWithoutReturnType(ClassNode cn) {
        for (MethodNode mn : cn.getMethods()) {
            if (!mn.isScriptBody()) continue;
            return GeneralUtils.makeDescriptorWithoutReturnType(mn);
        }
        return null;
    }

    private static FieldNode checkFieldDoesNotExist(ClassNode node, String fieldName) {
        FieldNode ret = node.getDeclaredField(fieldName);
        if (ret != null) {
            if (Modifier.isPublic(ret.getModifiers()) && ret.getType().redirect() == ClassHelper.boolean_TYPE) {
                return ret;
            }
            throw new RuntimeParserException("The class " + node.getName() + " cannot declare field '" + fieldName + "' as this field is needed for internal groovy purposes", ret);
        }
        return null;
    }

    private static void addFastPathHelperFieldsAndHelperMethod(ClassNode node, String classInternalName, boolean knownSpecialCase) {
        if (node.getNodeMetaData(OptimizingStatementWriter.ClassNodeSkip.class) != null) {
            return;
        }
        FieldNode stMCB = Verifier.checkFieldDoesNotExist(node, STATIC_METACLASS_BOOL);
        if (stMCB == null) {
            stMCB = node.addField(STATIC_METACLASS_BOOL, 4233, ClassHelper.boolean_TYPE, null);
            stMCB.setSynthetic(true);
        }
    }

    protected void addDefaultConstructor(ClassNode node) {
        if (!node.getDeclaredConstructors().isEmpty()) {
            return;
        }
        BlockStatement empty = new BlockStatement();
        empty.setSourcePosition(node);
        ConstructorNode constructor = new ConstructorNode(1, empty);
        constructor.setSourcePosition(node);
        constructor.setHasNoRealSourcePosition(true);
        node.addConstructor(constructor);
    }

    private void addStaticMetaClassField(final ClassNode node, final String classInternalName) {
        String _staticClassInfoFieldName = "$staticClassInfo";
        while (node.getDeclaredField(_staticClassInfoFieldName) != null) {
            _staticClassInfoFieldName = _staticClassInfoFieldName + "$";
        }
        final String staticMetaClassFieldName = _staticClassInfoFieldName;
        FieldNode staticMetaClassField = node.addField(staticMetaClassFieldName, 4106, ClassHelper.make(ClassInfo.class, false), null);
        staticMetaClassField.setSynthetic(true);
        node.addSyntheticMethod("$getStaticMetaClass", 4, ClassHelper.make(MetaClass.class), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, new BytecodeSequence(new BytecodeInstruction(){

            @Override
            public void visit(MethodVisitor mv) {
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
                if (BytecodeHelper.isClassLiteralPossible(node) || BytecodeHelper.isSameCompilationUnit(Verifier.this.classNode, node)) {
                    BytecodeHelper.visitClassLiteral(mv, node);
                } else {
                    mv.visitMethodInsn(184, classInternalName, "$get$$class$" + classInternalName.replaceAll("\\/", "\\$"), "()Ljava/lang/Class;", false);
                }
                Label l1 = new Label();
                mv.visitJumpInsn(165, l1);
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/ScriptBytecodeAdapter", "initMetaClass", "(Ljava/lang/Object;)Lgroovy/lang/MetaClass;", false);
                mv.visitInsn(176);
                mv.visitLabel(l1);
                mv.visitFieldInsn(178, classInternalName, staticMetaClassFieldName, "Lorg/codehaus/groovy/reflection/ClassInfo;");
                mv.visitVarInsn(58, 1);
                mv.visitVarInsn(25, 1);
                Label l0 = new Label();
                mv.visitJumpInsn(199, l0);
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
                mv.visitMethodInsn(184, "org/codehaus/groovy/reflection/ClassInfo", "getClassInfo", "(Ljava/lang/Class;)Lorg/codehaus/groovy/reflection/ClassInfo;", false);
                mv.visitInsn(89);
                mv.visitVarInsn(58, 1);
                mv.visitFieldInsn(179, classInternalName, staticMetaClassFieldName, "Lorg/codehaus/groovy/reflection/ClassInfo;");
                mv.visitLabel(l0);
                mv.visitVarInsn(25, 1);
                mv.visitMethodInsn(182, "org/codehaus/groovy/reflection/ClassInfo", "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
                mv.visitInsn(176);
            }
        }));
    }

    protected void addGroovyObjectInterfaceAndMethods(ClassNode node, final String classInternalName) {
        Parameter[] parameters;
        if (!node.isDerivedFromGroovyObject()) {
            node.addInterface(ClassHelper.make(GroovyObject.class));
        }
        FieldNode metaClassField = Verifier.getMetaClassField(node);
        if (!node.hasMethod("getMetaClass", Parameter.EMPTY_ARRAY)) {
            metaClassField = Verifier.setMetaClassFieldIfNotExists(node, metaClassField);
            this.addMethod(node, !Modifier.isAbstract(node.getModifiers()), "getMetaClass", 1, ClassHelper.METACLASS_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    Label nullLabel = new Label();
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, classInternalName, "metaClass", "Lgroovy/lang/MetaClass;");
                    mv.visitInsn(89);
                    mv.visitJumpInsn(198, nullLabel);
                    mv.visitInsn(176);
                    mv.visitLabel(nullLabel);
                    mv.visitInsn(87);
                    mv.visitVarInsn(25, 0);
                    mv.visitInsn(89);
                    mv.visitMethodInsn(182, classInternalName, "$getStaticMetaClass", "()Lgroovy/lang/MetaClass;", false);
                    mv.visitFieldInsn(181, classInternalName, "metaClass", "Lgroovy/lang/MetaClass;");
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, classInternalName, "metaClass", "Lgroovy/lang/MetaClass;");
                    mv.visitInsn(176);
                }
            }));
        }
        if (!node.hasMethod("setMetaClass", parameters = new Parameter[]{new Parameter(ClassHelper.METACLASS_TYPE, "mc")})) {
            Statement setMetaClassCode;
            if (Modifier.isFinal((metaClassField = Verifier.setMetaClassFieldIfNotExists(node, metaClassField)).getModifiers())) {
                ConstantExpression text = new ConstantExpression("cannot set read-only meta class");
                ConstructorCallExpression cce = new ConstructorCallExpression(ClassHelper.make(IllegalArgumentException.class), text);
                setMetaClassCode = new ExpressionStatement(cce);
            } else {
                ArrayList<4> list = new ArrayList<4>();
                list.add(new BytecodeInstruction(){

                    @Override
                    public void visit(MethodVisitor mv) {
                        mv.visitVarInsn(25, 0);
                        mv.visitVarInsn(25, 1);
                        mv.visitFieldInsn(181, classInternalName, "metaClass", "Lgroovy/lang/MetaClass;");
                        mv.visitInsn(177);
                    }
                });
                setMetaClassCode = new BytecodeSequence(list);
            }
            this.addMethod(node, !Modifier.isAbstract(node.getModifiers()), "setMetaClass", 1, ClassHelper.VOID_TYPE, SET_METACLASS_PARAMS, ClassNode.EMPTY_ARRAY, setMetaClassCode);
        }
        if (!node.hasMethod("invokeMethod", INVOKE_METHOD_PARAMS)) {
            VariableExpression vMethods = new VariableExpression("method");
            VariableExpression vArguments = new VariableExpression("arguments");
            VariableScope blockScope = new VariableScope();
            blockScope.putReferencedLocalVariable(vMethods);
            blockScope.putReferencedLocalVariable(vArguments);
            this.addMethod(node, !Modifier.isAbstract(node.getModifiers()), "invokeMethod", 1, ClassHelper.OBJECT_TYPE, INVOKE_METHOD_PARAMS, ClassNode.EMPTY_ARRAY, new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    mv.visitVarInsn(25, 0);
                    mv.visitMethodInsn(182, classInternalName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
                    mv.visitVarInsn(25, 0);
                    mv.visitVarInsn(25, 1);
                    mv.visitVarInsn(25, 2);
                    mv.visitMethodInsn(185, "groovy/lang/MetaClass", "invokeMethod", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", true);
                    mv.visitInsn(176);
                }
            }));
        }
        if (!node.hasMethod("getProperty", GET_PROPERTY_PARAMS)) {
            this.addMethod(node, !Modifier.isAbstract(node.getModifiers()), "getProperty", 1, ClassHelper.OBJECT_TYPE, GET_PROPERTY_PARAMS, ClassNode.EMPTY_ARRAY, new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    mv.visitVarInsn(25, 0);
                    mv.visitMethodInsn(182, classInternalName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
                    mv.visitVarInsn(25, 0);
                    mv.visitVarInsn(25, 1);
                    mv.visitMethodInsn(185, "groovy/lang/MetaClass", "getProperty", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", true);
                    mv.visitInsn(176);
                }
            }));
        }
        if (!node.hasMethod("setProperty", SET_PROPERTY_PARAMS)) {
            this.addMethod(node, !Modifier.isAbstract(node.getModifiers()), "setProperty", 1, ClassHelper.VOID_TYPE, SET_PROPERTY_PARAMS, ClassNode.EMPTY_ARRAY, new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    mv.visitVarInsn(25, 0);
                    mv.visitMethodInsn(182, classInternalName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
                    mv.visitVarInsn(25, 0);
                    mv.visitVarInsn(25, 1);
                    mv.visitVarInsn(25, 2);
                    mv.visitMethodInsn(185, "groovy/lang/MetaClass", "setProperty", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", true);
                    mv.visitInsn(177);
                }
            }));
        }
    }

    protected void addMethod(ClassNode node, boolean shouldBeSynthetic, String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        if (shouldBeSynthetic) {
            node.addSyntheticMethod(name, modifiers, returnType, parameters, exceptions, code);
        } else {
            node.addMethod(name, modifiers & 0xFFFFEFFF, returnType, parameters, exceptions, code);
        }
    }

    @Deprecated
    protected void addTimeStamp(ClassNode node) {
    }

    private static void checkReturnInObjectInitializer(List<Statement> init) {
        CodeVisitorSupport cvs = new CodeVisitorSupport(){

            @Override
            public void visitClosureExpression(ClosureExpression expression) {
            }

            @Override
            public void visitReturnStatement(ReturnStatement statement) {
                throw new RuntimeParserException("'return' is not allowed in object initializer", statement);
            }
        };
        for (Statement stm : init) {
            stm.visit(cvs);
        }
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        CodeVisitorSupport checkSuper = new CodeVisitorSupport(){
            boolean firstMethodCall = true;
            String type = null;

            @Override
            public void visitMethodCallExpression(MethodCallExpression call) {
                if (!this.firstMethodCall) {
                    return;
                }
                this.firstMethodCall = false;
                String name = call.getMethodAsString();
                if (name == null) {
                    return;
                }
                if (!name.equals("super") && !name.equals("this")) {
                    return;
                }
                this.type = name;
                call.getArguments().visit(this);
                this.type = null;
            }

            @Override
            public void visitConstructorCallExpression(ConstructorCallExpression call) {
                if (!call.isSpecialCall()) {
                    return;
                }
                this.type = call.getText();
                call.getArguments().visit(this);
                this.type = null;
            }

            @Override
            public void visitVariableExpression(VariableExpression expression) {
                if (this.type == null) {
                    return;
                }
                String name = expression.getName();
                if (!name.equals("this") && !name.equals("super")) {
                    return;
                }
                throw new RuntimeParserException("cannot reference " + name + " inside of " + this.type + "(....) before supertype constructor has been called", expression);
            }
        };
        Statement s = node.getCode();
        if (s == null) {
            return;
        }
        s.visit(new VerifierCodeVisitor(this));
        s.visit(checkSuper);
    }

    @Override
    public void visitMethod(MethodNode node) {
        if (MopWriter.isMopMethod(node.getName())) {
            throw new RuntimeParserException("Found unexpected MOP methods in the class node for " + this.classNode.getName() + "(" + node.getName() + ")", this.classNode);
        }
        this.methodNode = node;
        Verifier.adjustTypesIfStaticMainMethod(node);
        this.addReturnIfNeeded(node);
        Statement statement = node.getCode();
        if (statement != null) {
            statement.visit(new VerifierCodeVisitor(this));
        }
    }

    private static void adjustTypesIfStaticMainMethod(MethodNode node) {
        Parameter param;
        Parameter[] params;
        if (node.getName().equals("main") && node.isStatic() && (params = node.getParameters()).length == 1 && ((param = params[0]).getType() == null || param.getType() == ClassHelper.OBJECT_TYPE)) {
            param.setType(ClassHelper.STRING_TYPE.makeArray());
            ClassNode returnType = node.getReturnType();
            if (returnType == ClassHelper.OBJECT_TYPE) {
                node.setReturnType(ClassHelper.VOID_TYPE);
            }
        }
    }

    protected void addReturnIfNeeded(MethodNode node) {
        ReturnAdder adder = new ReturnAdder();
        adder.visitMethod(node);
    }

    @Override
    public void visitField(FieldNode node) {
    }

    private boolean methodNeedsReplacement(MethodNode m) {
        if (m == null) {
            return true;
        }
        if (m.getDeclaringClass() == this.getClassNode()) {
            return false;
        }
        return !Modifier.isFinal(m.getModifiers());
    }

    @Override
    public void visitProperty(PropertyNode node) {
        Statement setterBlock;
        String name = node.getName();
        FieldNode field = node.getField();
        String getterName = "get" + Verifier.capitalize(name);
        String setterName = "set" + Verifier.capitalize(name);
        int accessorModifiers = PropertyNodeUtils.adjustPropertyModifiersForMethod(node);
        Statement getterBlock = node.getGetterBlock();
        if (getterBlock == null) {
            MethodNode getter = this.classNode.getGetterMethod(getterName, !node.isStatic());
            if (getter == null && ClassHelper.boolean_TYPE == node.getType()) {
                String secondGetterName = "is" + Verifier.capitalize(name);
                getter = this.classNode.getGetterMethod(secondGetterName);
            }
            if (!node.isPrivate() && this.methodNeedsReplacement(getter)) {
                getterBlock = this.createGetterBlock(node, field);
            }
        }
        if ((setterBlock = node.getSetterBlock()) == null) {
            MethodNode setter = this.classNode.getSetterMethod(setterName, false);
            if (!node.isPrivate() && !Modifier.isFinal(accessorModifiers) && this.methodNeedsReplacement(setter)) {
                setterBlock = this.createSetterBlock(node, field);
            }
        }
        int getterModifiers = accessorModifiers;
        if (node.isStatic()) {
            getterModifiers = 0xFFFFFFEF & getterModifiers;
        }
        if (getterBlock != null) {
            MethodNode getter = new MethodNode(getterName, getterModifiers, node.getType(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, getterBlock);
            getter.setSynthetic(true);
            this.addPropertyMethod(getter);
            this.visitMethod(getter);
            if (ClassHelper.boolean_TYPE == node.getType() || ClassHelper.Boolean_TYPE == node.getType()) {
                String secondGetterName = "is" + Verifier.capitalize(name);
                MethodNode secondGetter = new MethodNode(secondGetterName, getterModifiers, node.getType(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, getterBlock);
                secondGetter.setSynthetic(true);
                this.addPropertyMethod(secondGetter);
                this.visitMethod(secondGetter);
            }
        }
        if (setterBlock != null) {
            Parameter[] setterParameterTypes = new Parameter[]{new Parameter(node.getType(), "value")};
            MethodNode setter = new MethodNode(setterName, accessorModifiers, ClassHelper.VOID_TYPE, setterParameterTypes, ClassNode.EMPTY_ARRAY, setterBlock);
            setter.setSynthetic(true);
            this.addPropertyMethod(setter);
            this.visitMethod(setter);
        }
    }

    protected void addPropertyMethod(MethodNode method) {
        this.classNode.addMethod(method);
        List<MethodNode> abstractMethods = this.classNode.getAbstractMethods();
        if (abstractMethods == null) {
            return;
        }
        String methodName = method.getName();
        Parameter[] parameters = method.getParameters();
        ClassNode methodReturnType = method.getReturnType();
        for (MethodNode node : abstractMethods) {
            ClassNode nodeReturnType;
            if (!node.getDeclaringClass().equals(this.classNode) || !node.getName().equals(methodName) || node.getParameters().length != parameters.length) continue;
            if (parameters.length == 1) {
                ClassNode abstractMethodParameterType = node.getParameters()[0].getType();
                ClassNode methodParameterType = parameters[0].getType();
                if (!methodParameterType.isDerivedFrom(abstractMethodParameterType) && !methodParameterType.implementsInterface(abstractMethodParameterType)) continue;
            }
            if (!methodReturnType.isDerivedFrom(nodeReturnType = node.getReturnType()) && !methodReturnType.implementsInterface(nodeReturnType)) continue;
            node.setModifiers(node.getModifiers() ^ 0x400);
            node.setCode(method.getCode());
        }
    }

    protected void addDefaultParameterMethods(final ClassNode node) {
        ArrayList<MethodNode> methods = new ArrayList<MethodNode>(node.getMethods());
        this.addDefaultParameters(methods, new DefaultArgsAction(){

            @Override
            public void call(ArgumentListExpression arguments, Parameter[] newParams, MethodNode method) {
                MethodNode oldMethod;
                final BlockStatement code = new BlockStatement();
                MethodNode newMethod = new MethodNode(method.getName(), method.getModifiers(), method.getReturnType(), newParams, method.getExceptions(), code);
                for (Expression argument : arguments.getExpressions()) {
                    ClassNode type;
                    if (argument instanceof CastExpression) {
                        argument = ((CastExpression)argument).getExpression();
                    }
                    if (argument instanceof ConstructorCallExpression && (type = argument.getType()) instanceof InnerClassNode && ((InnerClassNode)type).isAnonymous()) {
                        type.setEnclosingMethod(newMethod);
                    }
                    if (!(argument instanceof ClosureExpression)) continue;
                    final List<Parameter> newMethodNodeParameters = Arrays.asList(newParams);
                    CodeVisitorSupport visitor = new CodeVisitorSupport(){

                        @Override
                        public void visitVariableExpression(VariableExpression expression) {
                            Variable v = expression.getAccessedVariable();
                            if (!(v instanceof Parameter)) {
                                return;
                            }
                            Parameter param = (Parameter)v;
                            if (param.hasInitialExpression() && code.getVariableScope().getDeclaredVariable(param.getName()) == null && !newMethodNodeParameters.contains(param)) {
                                VariableExpression localVariable = new VariableExpression(param.getName(), ClassHelper.makeReference());
                                DeclarationExpression declarationExpression = new DeclarationExpression(localVariable, Token.newSymbol(100, -1, -1), (Expression)new ConstructorCallExpression(ClassHelper.makeReference(), param.getInitialExpression()));
                                code.addStatement(new ExpressionStatement(declarationExpression));
                                code.getVariableScope().putDeclaredVariable(localVariable);
                            }
                        }
                    };
                    visitor.visitClosureExpression((ClosureExpression)argument);
                }
                MethodCallExpression expression = new MethodCallExpression((Expression)VariableExpression.THIS_EXPRESSION, method.getName(), (Expression)arguments);
                expression.setMethodTarget(method);
                expression.setImplicitThis(true);
                if (method.isVoidMethod()) {
                    code.addStatement(new ExpressionStatement(expression));
                } else {
                    code.addStatement(new ReturnStatement(expression));
                }
                List<AnnotationNode> annotations = method.getAnnotations();
                if (annotations != null) {
                    newMethod.addAnnotations(annotations);
                }
                if ((oldMethod = node.getDeclaredMethod(method.getName(), newParams)) != null) {
                    throw new RuntimeParserException("The method with default parameters \"" + method.getTypeDescriptor() + "\" defines a method \"" + newMethod.getTypeDescriptor() + "\" that is already defined.", method);
                }
                Verifier.this.addPropertyMethod(newMethod);
                newMethod.setGenericsTypes(method.getGenericsTypes());
                newMethod.putNodeMetaData(Verifier.DEFAULT_PARAMETER_GENERATED, true);
            }
        });
    }

    protected void addDefaultParameterConstructors(final ClassNode node) {
        ArrayList<ConstructorNode> methods = new ArrayList<ConstructorNode>(node.getDeclaredConstructors());
        this.addDefaultParameters(methods, new DefaultArgsAction(){

            @Override
            public void call(ArgumentListExpression arguments, Parameter[] newParams, MethodNode method) {
                ConstructorNode ctor = (ConstructorNode)method;
                ConstructorCallExpression expression = new ConstructorCallExpression(ClassNode.THIS, arguments);
                ExpressionStatement code = new ExpressionStatement(expression);
                Verifier.this.addConstructor(newParams, ctor, code, node);
            }
        });
    }

    protected void addConstructor(Parameter[] newParams, ConstructorNode ctor, Statement code, ClassNode node) {
        node.addConstructor(ctor.getModifiers(), newParams, ctor.getExceptions(), code);
    }

    protected void addDefaultParameters(List methods, DefaultArgsAction action) {
        for (Object next : methods) {
            MethodNode method = (MethodNode)next;
            if (!method.hasDefaultValue()) continue;
            this.addDefaultParameters(action, method);
        }
    }

    protected void addDefaultParameters(DefaultArgsAction action, MethodNode method) {
        Parameter[] parameters = method.getParameters();
        int counter = 0;
        ArrayList<Object> paramValues = new ArrayList<Object>();
        int size = parameters.length;
        for (int i = size - 1; i >= 0; --i) {
            Parameter parameter = parameters[i];
            if (parameter == null || !parameter.hasInitialExpression()) continue;
            paramValues.add(i);
            paramValues.add(new CastExpression(parameter.getType(), parameter.getInitialExpression()));
            ++counter;
        }
        for (int j = 1; j <= counter; ++j) {
            Parameter[] newParams = new Parameter[parameters.length - j];
            ArgumentListExpression arguments = new ArgumentListExpression();
            int index = 0;
            int k = 1;
            for (Parameter parameter : parameters) {
                if (parameter == null) {
                    throw new GroovyBugError("Parameter should not be null for method " + this.methodNode.getName());
                }
                if (k > counter - j && parameter.hasInitialExpression()) {
                    arguments.addExpression(new CastExpression(parameter.getType(), parameter.getInitialExpression()));
                    ++k;
                    continue;
                }
                if (parameter.hasInitialExpression()) {
                    newParams[index++] = parameter;
                    arguments.addExpression(new CastExpression(parameter.getType(), new VariableExpression(parameter.getName())));
                    ++k;
                    continue;
                }
                newParams[index++] = parameter;
                arguments.addExpression(new CastExpression(parameter.getType(), new VariableExpression(parameter.getName())));
            }
            action.call(arguments, newParams, method);
        }
        for (Parameter parameter : parameters) {
            parameter.putNodeMetaData(INITIAL_EXPRESSION, parameter.getInitialExpression());
            parameter.setInitialExpression(null);
        }
    }

    protected void addClosureCode(InnerClassNode node) {
    }

    protected void addInitialization(final ClassNode node) {
        boolean addSwapInit = Verifier.moveOptimizedConstantsInitialization(node);
        for (ConstructorNode cn : node.getDeclaredConstructors()) {
            this.addInitialization(node, cn);
        }
        if (addSwapInit) {
            BytecodeSequence seq = new BytecodeSequence(new BytecodeInstruction(){

                @Override
                public void visit(MethodVisitor mv) {
                    mv.visitMethodInsn(184, BytecodeHelper.getClassInternalName(node), Verifier.SWAP_INIT, "()V", false);
                }
            });
            ArrayList<Statement> swapCall = new ArrayList<Statement>(1);
            swapCall.add(seq);
            node.addStaticInitializerStatements(swapCall, true);
        }
    }

    protected void addInitialization(ClassNode node, ConstructorNode constructorNode) {
        Statement firstStatement = constructorNode.getFirstStatement();
        if (firstStatement instanceof BytecodeSequence) {
            return;
        }
        ConstructorCallExpression first = Verifier.getFirstIfSpecialConstructorCall(firstStatement);
        if (first != null && first.isThisCall()) {
            return;
        }
        ArrayList<Statement> statements = new ArrayList<Statement>();
        ArrayList<Statement> staticStatements = new ArrayList<Statement>();
        boolean isEnum = node.isEnum();
        ArrayList<Statement> initStmtsAfterEnumValuesInit = new ArrayList<Statement>();
        HashSet<String> explicitStaticPropsInEnum = new HashSet<String>();
        if (isEnum) {
            for (PropertyNode propNode : node.getProperties()) {
                if (propNode.isSynthetic() || !propNode.getField().isStatic()) continue;
                explicitStaticPropsInEnum.add(propNode.getField().getName());
            }
            for (FieldNode fieldNode : node.getFields()) {
                if (fieldNode.isSynthetic() || !fieldNode.isStatic() || fieldNode.getType() == node) continue;
                explicitStaticPropsInEnum.add(fieldNode.getName());
            }
        }
        if (!Traits.isTrait(node)) {
            for (FieldNode fn : node.getFields()) {
                this.addFieldInitialization(statements, staticStatements, fn, isEnum, initStmtsAfterEnumValuesInit, explicitStaticPropsInEnum);
            }
        }
        statements.addAll(node.getObjectInitializerStatements());
        Statement code = constructorNode.getCode();
        BlockStatement block = new BlockStatement();
        List<Statement> otherStatements = block.getStatements();
        if (code instanceof BlockStatement) {
            block = (BlockStatement)code;
            otherStatements = block.getStatements();
        } else if (code != null) {
            otherStatements.add(code);
        }
        if (!otherStatements.isEmpty()) {
            Statement stmtThis$0;
            if (first != null) {
                otherStatements.remove(0);
                statements.add(0, firstStatement);
            }
            if ((stmtThis$0 = this.getImplicitThis$0StmtIfInnerClass(otherStatements)) != null) {
                statements.add(0, stmtThis$0);
            }
            statements.addAll(otherStatements);
        }
        BlockStatement newBlock = new BlockStatement(statements, block.getVariableScope());
        newBlock.setSourcePosition(block);
        constructorNode.setCode(newBlock);
        if (!staticStatements.isEmpty()) {
            if (isEnum) {
                staticStatements.removeAll(initStmtsAfterEnumValuesInit);
                node.addStaticInitializerStatements(staticStatements, true);
                if (!initStmtsAfterEnumValuesInit.isEmpty()) {
                    node.positionStmtsAfterEnumInitStmts(initStmtsAfterEnumValuesInit);
                }
            } else {
                node.addStaticInitializerStatements(staticStatements, true);
            }
        }
    }

    private Statement getImplicitThis$0StmtIfInnerClass(List<Statement> otherStatements) {
        if (!(this.classNode instanceof InnerClassNode)) {
            return null;
        }
        for (Statement stmt : otherStatements) {
            if (stmt instanceof BlockStatement) {
                List<Statement> stmts = ((BlockStatement)stmt).getStatements();
                for (Statement bstmt : stmts) {
                    if (!(bstmt instanceof ExpressionStatement) || !Verifier.extractImplicitThis$0StmtIfInnerClassFromExpression(stmts, bstmt)) continue;
                    return bstmt;
                }
                continue;
            }
            if (!(stmt instanceof ExpressionStatement) || !Verifier.extractImplicitThis$0StmtIfInnerClassFromExpression(otherStatements, stmt)) continue;
            return stmt;
        }
        return null;
    }

    private static boolean extractImplicitThis$0StmtIfInnerClassFromExpression(List<Statement> stmts, Statement bstmt) {
        Expression lExpr;
        Expression expr = ((ExpressionStatement)bstmt).getExpression();
        if (expr instanceof BinaryExpression && (lExpr = ((BinaryExpression)expr).getLeftExpression()) instanceof FieldExpression && "this$0".equals(((FieldExpression)lExpr).getFieldName())) {
            stmts.remove(bstmt);
            return true;
        }
        return false;
    }

    private static ConstructorCallExpression getFirstIfSpecialConstructorCall(Statement code) {
        if (code == null || !(code instanceof ExpressionStatement)) {
            return null;
        }
        Expression expression = ((ExpressionStatement)code).getExpression();
        if (!(expression instanceof ConstructorCallExpression)) {
            return null;
        }
        ConstructorCallExpression cce = (ConstructorCallExpression)expression;
        if (cce.isSpecialCall()) {
            return cce;
        }
        return null;
    }

    protected void addFieldInitialization(List list, List staticList, FieldNode fieldNode, boolean isEnumClassNode, List initStmtsAfterEnumValuesInit, Set explicitStaticPropsInEnum) {
        Expression expression = fieldNode.getInitialExpression();
        if (expression != null) {
            FieldExpression fe = new FieldExpression(fieldNode);
            if (fieldNode.getType().equals(ClassHelper.REFERENCE_TYPE) && (fieldNode.getModifiers() & 0x1000) != 0) {
                fe.setUseReferenceDirectly(true);
            }
            ExpressionStatement statement = new ExpressionStatement(new BinaryExpression(fe, Token.newSymbol(100, fieldNode.getLineNumber(), fieldNode.getColumnNumber()), expression));
            if (fieldNode.isStatic()) {
                Expression initialValueExpression = fieldNode.getInitialValueExpression();
                if (initialValueExpression instanceof ConstantExpression) {
                    ConstantExpression cexp = (ConstantExpression)initialValueExpression;
                    cexp = Verifier.transformToPrimitiveConstantIfPossible(cexp);
                    if (fieldNode.isFinal() && ClassHelper.isStaticConstantInitializerType(cexp.getType()) && cexp.getType().equals(fieldNode.getType())) {
                        return;
                    }
                    staticList.add(0, statement);
                } else {
                    staticList.add(statement);
                }
                fieldNode.setInitialValueExpression(null);
                if (isEnumClassNode && explicitStaticPropsInEnum.contains(fieldNode.getName())) {
                    initStmtsAfterEnumValuesInit.add(statement);
                }
            } else {
                list.add(statement);
            }
        }
    }

    public static String capitalize(String name) {
        return MetaClassHelper.capitalize(name);
    }

    protected Statement createGetterBlock(PropertyNode propertyNode, final FieldNode field) {
        return new BytecodeSequence(new BytecodeInstruction(){

            @Override
            public void visit(MethodVisitor mv) {
                if (field.isStatic()) {
                    mv.visitFieldInsn(178, BytecodeHelper.getClassInternalName(Verifier.this.classNode), field.getName(), BytecodeHelper.getTypeDescription(field.getType()));
                } else {
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, BytecodeHelper.getClassInternalName(Verifier.this.classNode), field.getName(), BytecodeHelper.getTypeDescription(field.getType()));
                }
                BytecodeHelper.doReturn(mv, field.getType());
            }
        });
    }

    protected Statement createSetterBlock(PropertyNode propertyNode, final FieldNode field) {
        return new BytecodeSequence(new BytecodeInstruction(){

            @Override
            public void visit(MethodVisitor mv) {
                if (field.isStatic()) {
                    BytecodeHelper.load(mv, field.getType(), 0);
                    mv.visitFieldInsn(179, BytecodeHelper.getClassInternalName(Verifier.this.classNode), field.getName(), BytecodeHelper.getTypeDescription(field.getType()));
                } else {
                    mv.visitVarInsn(25, 0);
                    BytecodeHelper.load(mv, field.getType(), 1);
                    mv.visitFieldInsn(181, BytecodeHelper.getClassInternalName(Verifier.this.classNode), field.getName(), BytecodeHelper.getTypeDescription(field.getType()));
                }
                mv.visitInsn(177);
            }
        });
    }

    public void visitGenericType(GenericsType genericsType) {
    }

    public static long getTimestamp(Class clazz) {
        if (clazz.getClassLoader() instanceof GroovyClassLoader.InnerLoader) {
            GroovyClassLoader.InnerLoader innerLoader = (GroovyClassLoader.InnerLoader)clazz.getClassLoader();
            return innerLoader.getTimeStamp();
        }
        Field[] fields = clazz.getFields();
        for (int i = 0; i != fields.length; ++i) {
            String name;
            if (!Modifier.isStatic(fields[i].getModifiers()) || !(name = fields[i].getName()).startsWith(__TIMESTAMP__)) continue;
            try {
                return Long.decode(name.substring(__TIMESTAMP__.length()));
            }
            catch (NumberFormatException e) {
                return Long.MAX_VALUE;
            }
        }
        return Long.MAX_VALUE;
    }

    protected void addCovariantMethods(ClassNode classNode) {
        HashMap methodsToAdd = new HashMap();
        HashMap genericsSpec = new HashMap();
        Map<String, MethodNode> abstractMethods = ClassNodeUtils.getDeclaredMethodMapsFromInterfaces(classNode);
        HashMap<String, MethodNode> allInterfaceMethods = new HashMap<String, MethodNode>(abstractMethods);
        ClassNodeUtils.addDeclaredMethodMapsFromSuperInterfaces(classNode, allInterfaceMethods);
        ArrayList<MethodNode> declaredMethods = new ArrayList<MethodNode>(classNode.getMethods());
        Iterator methodsIterator = declaredMethods.iterator();
        while (methodsIterator.hasNext()) {
            MethodNode methodNode;
            MethodNode m = (MethodNode)methodsIterator.next();
            abstractMethods.remove(m.getTypeDescriptor());
            if (m.isStatic() || !m.isPublic() && !m.isProtected()) {
                methodsIterator.remove();
            }
            if ((methodNode = (MethodNode)allInterfaceMethods.get(m.getTypeDescriptor())) == null || (m.getModifiers() & 0x1000) != 0 || m.isPublic() || m.isStaticConstructor()) continue;
            throw new RuntimeParserException("The method " + m.getName() + " should be public as it implements the corresponding method from interface " + methodNode.getDeclaringClass(), m);
        }
        this.addCovariantMethods(classNode, declaredMethods, abstractMethods, methodsToAdd, genericsSpec);
        HashMap<String, MethodNode> declaredMethodsMap = new HashMap<String, MethodNode>();
        if (!methodsToAdd.isEmpty()) {
            for (MethodNode methodNode : declaredMethods) {
                declaredMethodsMap.put(methodNode.getTypeDescriptor(), methodNode);
            }
        }
        for (Object object : methodsToAdd.entrySet()) {
            Map.Entry entry = (Map.Entry)object;
            MethodNode method = (MethodNode)entry.getValue();
            MethodNode mn = (MethodNode)declaredMethodsMap.get(entry.getKey());
            if (mn != null && mn.getDeclaringClass().equals(classNode)) continue;
            this.addPropertyMethod(method);
        }
    }

    /*
     * WARNING - void declaration
     */
    private void addCovariantMethods(ClassNode classNode, List declaredMethods, Map abstractMethods, Map methodsToAdd, Map oldGenericsSpec) {
        void var10_15;
        ClassNode[] interfaces;
        ClassNode sn = classNode.getUnresolvedSuperClass(false);
        if (sn != null) {
            MethodNode method;
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(sn, oldGenericsSpec);
            List<MethodNode> classMethods = sn.getMethods();
            for (Object e : declaredMethods) {
                method = (MethodNode)e;
                if (method.isStatic()) continue;
                this.storeMissingCovariantMethods(classMethods, method, methodsToAdd, genericsSpec, false);
            }
            if (!abstractMethods.isEmpty()) {
                for (Object object : classMethods) {
                    method = (MethodNode)object;
                    if (method.isStatic()) continue;
                    this.storeMissingCovariantMethods(abstractMethods.values(), method, methodsToAdd, Collections.EMPTY_MAP, true);
                }
            }
            this.addCovariantMethods(sn.redirect(), declaredMethods, abstractMethods, methodsToAdd, genericsSpec);
        }
        ClassNode[] classNodeArray = interfaces = classNode.getInterfaces();
        int n = classNodeArray.length;
        boolean bl = false;
        while (var10_15 < n) {
            ClassNode anInterface = classNodeArray[var10_15];
            List<MethodNode> interfacesMethods = anInterface.getMethods();
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(anInterface, oldGenericsSpec);
            for (Object declaredMethod : declaredMethods) {
                MethodNode method = (MethodNode)declaredMethod;
                if (method.isStatic()) continue;
                this.storeMissingCovariantMethods(interfacesMethods, method, methodsToAdd, genericsSpec, false);
            }
            this.addCovariantMethods(anInterface, declaredMethods, abstractMethods, methodsToAdd, genericsSpec);
            ++var10_15;
        }
    }

    private MethodNode getCovariantImplementation(final MethodNode oldMethod, final MethodNode overridingMethod, Map genericsSpec, boolean ignoreError) {
        if (!oldMethod.getName().equals(overridingMethod.getName())) {
            return null;
        }
        if ((overridingMethod.getModifiers() & 0x40) != 0) {
            return null;
        }
        if (oldMethod.isPrivate()) {
            return null;
        }
        boolean normalEqualParameters = Verifier.equalParametersNormal(overridingMethod, oldMethod);
        boolean genericEqualParameters = Verifier.equalParametersWithGenerics(overridingMethod, oldMethod, genericsSpec);
        if (!normalEqualParameters && !genericEqualParameters) {
            return null;
        }
        genericsSpec = GenericsUtils.addMethodGenerics(overridingMethod, genericsSpec);
        ClassNode mr = overridingMethod.getReturnType();
        ClassNode omr = oldMethod.getReturnType();
        boolean equalReturnType = mr.equals(omr);
        ClassNode testmr = GenericsUtils.correctToGenericsSpec(genericsSpec, omr);
        if (!this.isAssignable(mr, testmr)) {
            if (ignoreError) {
                return null;
            }
            throw new RuntimeParserException("The return type of " + overridingMethod.getTypeDescriptor() + " in " + overridingMethod.getDeclaringClass().getName() + " is incompatible with " + testmr.getName() + " in " + oldMethod.getDeclaringClass().getName(), overridingMethod);
        }
        if (equalReturnType && normalEqualParameters) {
            return null;
        }
        if ((oldMethod.getModifiers() & 0x10) != 0) {
            throw new RuntimeParserException("Cannot override final method " + oldMethod.getTypeDescriptor() + " in " + oldMethod.getDeclaringClass().getName(), overridingMethod);
        }
        if (oldMethod.isStatic() != overridingMethod.isStatic()) {
            throw new RuntimeParserException("Cannot override method " + oldMethod.getTypeDescriptor() + " in " + oldMethod.getDeclaringClass().getName() + " with disparate static modifier", overridingMethod);
        }
        if (!equalReturnType) {
            boolean oldM = ClassHelper.isPrimitiveType(oldMethod.getReturnType());
            boolean newM = ClassHelper.isPrimitiveType(overridingMethod.getReturnType());
            if (oldM || newM) {
                String message = "";
                message = oldM && newM ? " with old and new method having different primitive return types" : (newM ? " with new method having a primitive return type and old method not" : " with old method having a primitive return type and new method not");
                throw new RuntimeParserException("Cannot override method " + oldMethod.getTypeDescriptor() + " in " + oldMethod.getDeclaringClass().getName() + message, overridingMethod);
            }
        }
        MethodNode newMethod = new MethodNode(oldMethod.getName(), overridingMethod.getModifiers() | 0x1000 | 0x40, Verifier.cleanType(oldMethod.getReturnType()), Verifier.cleanParameters(oldMethod.getParameters()), oldMethod.getExceptions(), null);
        ArrayList<15> instructions = new ArrayList<15>(1);
        instructions.add(new BytecodeInstruction(){

            @Override
            public void visit(MethodVisitor mv) {
                mv.visitVarInsn(25, 0);
                Parameter[] para = oldMethod.getParameters();
                Parameter[] goal = overridingMethod.getParameters();
                int doubleSlotOffset = 0;
                for (int i = 0; i < para.length; ++i) {
                    ClassNode type = para[i].getType();
                    BytecodeHelper.load(mv, type, i + 1 + doubleSlotOffset);
                    if (type.redirect() == ClassHelper.double_TYPE || type.redirect() == ClassHelper.long_TYPE) {
                        ++doubleSlotOffset;
                    }
                    if (type.equals(goal[i].getType())) continue;
                    BytecodeHelper.doCast(mv, goal[i].getType());
                }
                mv.visitMethodInsn(182, BytecodeHelper.getClassInternalName(Verifier.this.classNode), overridingMethod.getName(), BytecodeHelper.getMethodDescriptor(overridingMethod.getReturnType(), overridingMethod.getParameters()), false);
                BytecodeHelper.doReturn(mv, oldMethod.getReturnType());
            }
        });
        newMethod.setCode(new BytecodeSequence(instructions));
        return newMethod;
    }

    private boolean isAssignable(ClassNode node, ClassNode testNode) {
        if (node.isArray() && testNode.isArray()) {
            return this.isArrayAssignable(node.getComponentType(), testNode.getComponentType());
        }
        if (testNode.isInterface() && (node.equals(testNode) || node.implementsInterface(testNode))) {
            return true;
        }
        return node.isDerivedFrom(testNode);
    }

    private boolean isArrayAssignable(ClassNode node, ClassNode testNode) {
        if (node.isArray() && testNode.isArray()) {
            return this.isArrayAssignable(node.getComponentType(), testNode.getComponentType());
        }
        return this.isAssignable(node, testNode);
    }

    private static Parameter[] cleanParameters(Parameter[] parameters) {
        Parameter[] params = new Parameter[parameters.length];
        for (int i = 0; i < params.length; ++i) {
            params[i] = new Parameter(Verifier.cleanType(parameters[i].getType()), parameters[i].getName());
        }
        return params;
    }

    private static ClassNode cleanType(ClassNode type) {
        if (type.isArray()) {
            return Verifier.cleanType(type.getComponentType()).makeArray();
        }
        return type.getPlainNodeReference();
    }

    private void storeMissingCovariantMethods(Collection methods, MethodNode method, Map methodsToAdd, Map genericsSpec, boolean ignoreError) {
        for (Object next : methods) {
            MethodNode toOverride = (MethodNode)next;
            MethodNode bridgeMethod = this.getCovariantImplementation(toOverride, method, genericsSpec, ignoreError);
            if (bridgeMethod == null) continue;
            methodsToAdd.put(bridgeMethod.getTypeDescriptor(), bridgeMethod);
            return;
        }
    }

    private static boolean equalParametersNormal(MethodNode m1, MethodNode m2) {
        Parameter[] p2;
        Parameter[] p1 = m1.getParameters();
        if (p1.length != (p2 = m2.getParameters()).length) {
            return false;
        }
        for (int i = 0; i < p2.length; ++i) {
            ClassNode type = p2[i].getType();
            ClassNode parameterType = p1[i].getType();
            if (parameterType.equals(type)) continue;
            return false;
        }
        return true;
    }

    private static boolean equalParametersWithGenerics(MethodNode m1, MethodNode m2, Map genericsSpec) {
        Parameter[] p2;
        Parameter[] p1 = m1.getParameters();
        if (p1.length != (p2 = m2.getParameters()).length) {
            return false;
        }
        for (int i = 0; i < p2.length; ++i) {
            ClassNode type = p2[i].getType();
            ClassNode genericsType = GenericsUtils.correctToGenericsSpec((Map<String, ClassNode>)genericsSpec, type);
            ClassNode parameterType = p1[i].getType();
            if (parameterType.equals(genericsType)) continue;
            return false;
        }
        return true;
    }

    private static boolean moveOptimizedConstantsInitialization(ClassNode node) {
        if (node.isInterface() && !Traits.isTrait(node)) {
            return false;
        }
        int mods = 4105;
        String name = SWAP_INIT;
        BlockStatement methodCode = new BlockStatement();
        methodCode.addStatement(new SwapInitStatement());
        boolean swapInitRequired = false;
        for (FieldNode fn : node.getFields()) {
            if (!fn.isStatic() || !fn.isSynthetic() || !fn.getName().startsWith("$const$") || fn.getInitialExpression() == null) continue;
            FieldExpression fe = new FieldExpression(fn);
            if (fn.getType().equals(ClassHelper.REFERENCE_TYPE)) {
                fe.setUseReferenceDirectly(true);
            }
            ConstantExpression init = (ConstantExpression)fn.getInitialExpression();
            init = new ConstantExpression(init.getValue(), true);
            ExpressionStatement statement = new ExpressionStatement(new BinaryExpression(fe, Token.newSymbol(100, fn.getLineNumber(), fn.getColumnNumber()), init));
            fn.setInitialValueExpression(null);
            methodCode.addStatement(statement);
            swapInitRequired = true;
        }
        if (swapInitRequired) {
            node.addSyntheticMethod(name, 4105, ClassHelper.VOID_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, methodCode);
        }
        return swapInitRequired;
    }

    public static ConstantExpression transformToPrimitiveConstantIfPossible(ConstantExpression constantExpression) {
        ConstantExpression result;
        Object value = constantExpression.getValue();
        if (value == null) {
            return constantExpression;
        }
        ClassNode type = constantExpression.getType();
        if (ClassHelper.isPrimitiveType(type)) {
            return constantExpression;
        }
        if (value instanceof String && ((String)value).length() == 1) {
            result = new ConstantExpression(Character.valueOf(((String)value).charAt(0)));
            result.setType(ClassHelper.char_TYPE);
        } else {
            type = ClassHelper.getUnwrapper(type);
            result = new ConstantExpression(value, true);
            result.setType(type);
        }
        return result;
    }

    private static class SwapInitStatement
    extends BytecodeSequence {
        private WriterController controller;

        public SwapInitStatement() {
            super(new SwapInitInstruction());
            ((SwapInitInstruction)this.getInstructions().get((int)0)).statement = this;
        }

        @Override
        public void visit(GroovyCodeVisitor visitor) {
            if (visitor instanceof AsmClassGenerator) {
                AsmClassGenerator generator = (AsmClassGenerator)visitor;
                this.controller = generator.getController();
            }
            super.visit(visitor);
        }

        private static class SwapInitInstruction
        extends BytecodeInstruction {
            SwapInitStatement statement;

            private SwapInitInstruction() {
            }

            @Override
            public void visit(MethodVisitor mv) {
                this.statement.controller.getCallSiteWriter().makeCallSiteArrayInitializer();
            }
        }
    }

    public static interface DefaultArgsAction {
        public void call(ArgumentListExpression var1, Parameter[] var2, MethodNode var3);
    }
}


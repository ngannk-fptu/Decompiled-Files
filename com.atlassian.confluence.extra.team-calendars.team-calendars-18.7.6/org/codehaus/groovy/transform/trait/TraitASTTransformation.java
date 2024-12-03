/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.trait;

import groovy.transform.CompilationUnitAware;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.ASTTransformationCollectorCodeVisitor;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.trait.NAryOperationRewriter;
import org.codehaus.groovy.transform.trait.SuperCallTraitTransformer;
import org.codehaus.groovy.transform.trait.TraitReceiverTransformer;
import org.codehaus.groovy.transform.trait.Traits;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class TraitASTTransformation
extends AbstractASTTransformation
implements CompilationUnitAware {
    public static final String DO_DYNAMIC = TraitReceiverTransformer.class + ".doDynamic";
    public static final String POST_TYPECHECKING_REPLACEMENT = TraitReceiverTransformer.class + ".replacement";
    private static final ClassNode INVOKERHELPER_CLASSNODE = ClassHelper.make(InvokerHelper.class);
    private static final ClassNode OVERRIDE_CLASSNODE = ClassHelper.make(Override.class);
    private SourceUnit unit;
    private CompilationUnit compilationUnit;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode anno = (AnnotationNode)nodes[0];
        if (!Traits.TRAIT_CLASSNODE.equals(anno.getClassNode())) {
            return;
        }
        this.unit = source;
        this.init(nodes, source);
        if (parent instanceof ClassNode) {
            ClassNode cNode = (ClassNode)parent;
            if (!this.checkNotInterface(cNode, Traits.TRAIT_TYPE_NAME)) {
                return;
            }
            this.checkNoConstructor(cNode);
            this.checkExtendsClause(cNode);
            this.generateMethodsWithDefaultArgs(cNode);
            this.replaceExtendsByImplements(cNode);
            ClassNode helperClassNode = this.createHelperClass(cNode);
            this.resolveHelperClassIfNecessary(helperClassNode);
        }
    }

    private void resolveHelperClassIfNecessary(ClassNode helperClassNode) {
        if (helperClassNode == null) {
            return;
        }
        for (ClassNode cNode : this.unit.getAST().getClasses()) {
            ClassNode unresolvedHelperNode = (ClassNode)cNode.getNodeMetaData("UNRESOLVED_HELPER_CLASS");
            if (unresolvedHelperNode == null || !unresolvedHelperNode.getName().equals(helperClassNode.getName())) continue;
            unresolvedHelperNode.setRedirect(helperClassNode);
        }
    }

    private void generateMethodsWithDefaultArgs(ClassNode cNode) {
        DefaultArgsMethodsAdder adder = new DefaultArgsMethodsAdder();
        adder.addDefaultParameterMethods(cNode);
    }

    private void checkExtendsClause(ClassNode cNode) {
        ClassNode superClass = cNode.getSuperClass();
        if (superClass.isInterface() && !Traits.isTrait(superClass)) {
            this.addError("Trait cannot extend an interface. Use 'implements' instead", cNode);
        }
    }

    private void replaceExtendsByImplements(ClassNode cNode) {
        ClassNode superClass = cNode.getUnresolvedSuperClass();
        if (Traits.isTrait(superClass)) {
            cNode.setSuperClass(ClassHelper.OBJECT_TYPE);
            cNode.setUnresolvedSuperClass(ClassHelper.OBJECT_TYPE);
            cNode.addInterface(superClass);
            this.resolveScope(cNode);
        }
    }

    private void resolveScope(ClassNode cNode) {
        VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(this.unit);
        scopeVisitor.visitClass(cNode);
    }

    private void checkNoConstructor(ClassNode cNode) {
        if (!cNode.getDeclaredConstructors().isEmpty()) {
            this.addError("Error processing trait '" + cNode.getName() + "'.  Constructors are not allowed.", cNode);
        }
    }

    private ClassNode createHelperClass(ClassNode cNode) {
        InnerClassNode helper = new InnerClassNode(cNode, Traits.helperClassName(cNode), 5129, ClassHelper.OBJECT_TYPE, ClassNode.EMPTY_ARRAY, null);
        cNode.setModifiers(1537);
        this.checkInnerClasses(cNode);
        MethodNode initializer = this.createInitMethod(false, cNode, helper);
        MethodNode staticInitializer = this.createInitMethod(true, cNode, helper);
        this.generatePropertyMethods(cNode);
        ArrayList<Object> fields = new ArrayList<FieldNode>();
        HashSet<String> fieldNames = new HashSet<String>();
        for (FieldNode field : cNode.getFields()) {
            if ("metaClass".equals(field.getName()) || field.isSynthetic() && field.getName().indexOf(36) >= 0) continue;
            fields.add(field);
            fieldNames.add(field.getName());
        }
        InnerClassNode fieldHelper = null;
        if (!fields.isEmpty()) {
            fieldHelper = new InnerClassNode(cNode, Traits.fieldHelperClassName(cNode), 1545, ClassHelper.OBJECT_TYPE);
        }
        ArrayList<MethodNode> methods = new ArrayList<MethodNode>(cNode.getMethods());
        LinkedList<MethodNode> nonPublicAPIMethods = new LinkedList<MethodNode>();
        for (MethodNode methodNode : methods) {
            boolean declared = methodNode.getDeclaringClass() == cNode;
            if (!declared) continue;
            if (!methodNode.isSynthetic() && (methodNode.isProtected() || methodNode.getModifiers() == 0)) {
                this.unit.addError(new SyntaxException("Cannot have protected/package private method in a trait (" + cNode.getName() + "#" + methodNode.getTypeDescriptor() + ")", methodNode.getLineNumber(), methodNode.getColumnNumber()));
                return null;
            }
            helper.addMethod(this.processMethod(cNode, helper, methodNode, fieldHelper, fieldNames));
            if (!methodNode.isPrivate() && !methodNode.isStatic()) continue;
            nonPublicAPIMethods.add(methodNode);
        }
        for (MethodNode methodNode : nonPublicAPIMethods) {
            cNode.removeMethod(methodNode);
        }
        for (FieldNode fieldNode : fields) {
            this.processField(fieldNode, initializer, staticInitializer, fieldHelper, helper, cNode, fieldNames);
        }
        cNode.getProperties().clear();
        this.copyClassAnnotations(cNode, helper);
        fields = new ArrayList<FieldNode>(cNode.getFields());
        for (FieldNode fieldNode : fields) {
            cNode.removeField(fieldNode.getName());
        }
        this.registerASTTransformations(helper);
        this.unit.getAST().addClass(helper);
        if (fieldHelper != null) {
            this.unit.getAST().addClass(fieldHelper);
        }
        this.resolveScope(helper);
        if (fieldHelper != null) {
            this.resolveScope(fieldHelper);
        }
        return helper;
    }

    private MethodNode createInitMethod(boolean isStatic, ClassNode cNode, ClassNode helper) {
        MethodNode initializer = new MethodNode(isStatic ? "$static$init$" : "$init$", 4105, ClassHelper.VOID_TYPE, new Parameter[]{this.createSelfParameter(cNode, isStatic)}, ClassNode.EMPTY_ARRAY, new BlockStatement());
        helper.addMethod(initializer);
        return initializer;
    }

    private void registerASTTransformations(final ClassNode helper) {
        ASTTransformationCollectorCodeVisitor collector = new ASTTransformationCollectorCodeVisitor(this.unit, this.compilationUnit.getTransformLoader());
        collector.visitClass(helper);
        this.compilationUnit.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                if (classNode == helper) {
                    PostTypeCheckingExpressionReplacer replacer = new PostTypeCheckingExpressionReplacer(source);
                    replacer.visitClass(helper);
                }
            }
        }, CompilePhase.INSTRUCTION_SELECTION.getPhaseNumber());
    }

    private void copyClassAnnotations(ClassNode cNode, ClassNode helper) {
        List<AnnotationNode> annotations = cNode.getAnnotations();
        for (AnnotationNode annotation : annotations) {
            if (annotation.getClassNode().equals(Traits.TRAIT_CLASSNODE)) continue;
            helper.addAnnotation(annotation);
        }
    }

    private void checkInnerClasses(ClassNode cNode) {
        Iterator<InnerClassNode> it = cNode.getInnerClasses();
        while (it.hasNext()) {
            InnerClassNode origin = it.next();
            if ((origin.getModifiers() & 8) != 0) continue;
            this.unit.addError(new SyntaxException("Cannot have non-static inner class inside a trait (" + origin.getName() + ")", origin.getLineNumber(), origin.getColumnNumber()));
        }
    }

    private void generatePropertyMethods(ClassNode cNode) {
        for (PropertyNode node : cNode.getProperties()) {
            TraitASTTransformation.processProperty(cNode, node);
        }
    }

    private static void processProperty(ClassNode cNode, PropertyNode node) {
        Statement setterBlock;
        Statement getterBlock;
        String name = node.getName();
        FieldNode field = node.getField();
        int propNodeModifiers = node.getModifiers();
        String getterName = "get" + Verifier.capitalize(name);
        String setterName = "set" + Verifier.capitalize(name);
        if ((propNodeModifiers & 0x40) != 0) {
            propNodeModifiers -= 64;
        }
        if ((propNodeModifiers & 0x80) != 0) {
            propNodeModifiers -= 128;
        }
        if ((getterBlock = node.getGetterBlock()) == null) {
            MethodNode getter = cNode.getGetterMethod(getterName);
            if (getter == null && ClassHelper.boolean_TYPE == node.getType()) {
                String secondGetterName = "is" + Verifier.capitalize(name);
                getter = cNode.getGetterMethod(secondGetterName);
            }
            if (!node.isPrivate() && TraitASTTransformation.methodNeedsReplacement(cNode, getter)) {
                getterBlock = new ExpressionStatement(new FieldExpression(field));
            }
        }
        if ((setterBlock = node.getSetterBlock()) == null) {
            MethodNode setter = cNode.getSetterMethod(setterName, false);
            if (!node.isPrivate() && (propNodeModifiers & 0x10) == 0 && TraitASTTransformation.methodNeedsReplacement(cNode, setter)) {
                setterBlock = new ExpressionStatement(new BinaryExpression(new FieldExpression(field), Token.newSymbol(100, 0, 0), new VariableExpression("value")));
            }
        }
        if (getterBlock != null) {
            MethodNode getter = new MethodNode(getterName, propNodeModifiers, node.getType(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, getterBlock);
            getter.setSynthetic(true);
            cNode.addMethod(getter);
            if (ClassHelper.boolean_TYPE == node.getType() || ClassHelper.Boolean_TYPE == node.getType()) {
                String secondGetterName = "is" + Verifier.capitalize(name);
                MethodNode secondGetter = new MethodNode(secondGetterName, propNodeModifiers, node.getType(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, getterBlock);
                secondGetter.setSynthetic(true);
                cNode.addMethod(secondGetter);
            }
        }
        if (setterBlock != null) {
            Parameter[] setterParameterTypes = new Parameter[]{new Parameter(node.getType(), "value")};
            VariableExpression var = (VariableExpression)((BinaryExpression)((ExpressionStatement)setterBlock).getExpression()).getRightExpression();
            var.setAccessedVariable(setterParameterTypes[0]);
            MethodNode setter = new MethodNode(setterName, propNodeModifiers, ClassHelper.VOID_TYPE, setterParameterTypes, ClassNode.EMPTY_ARRAY, setterBlock);
            setter.setSynthetic(true);
            cNode.addMethod(setter);
        }
    }

    private static boolean methodNeedsReplacement(ClassNode classNode, MethodNode m) {
        if (m == null) {
            return true;
        }
        if (m.getDeclaringClass() == classNode) {
            return false;
        }
        return (m.getModifiers() & 0x10) == 0;
    }

    private void processField(FieldNode field, MethodNode initializer, MethodNode staticInitializer, ClassNode fieldHelper, ClassNode helper, ClassNode trait, Set<String> knownFields) {
        MethodNode selectedMethod;
        if (field.isProtected()) {
            this.unit.addError(new SyntaxException("Cannot have protected field in a trait (" + trait.getName() + "#" + field.getName() + ")", field.getLineNumber(), field.getColumnNumber()));
            return;
        }
        Expression initialExpression = field.getInitialExpression();
        MethodNode methodNode = selectedMethod = field.isStatic() ? staticInitializer : initializer;
        if (initialExpression != null) {
            VariableExpression thisObject = new VariableExpression(selectedMethod.getParameters()[0]);
            ExpressionStatement initCode = new ExpressionStatement(initialExpression);
            this.processBody(thisObject, initCode, trait, helper, fieldHelper, knownFields);
            if (field.isFinal()) {
                String baseName = field.isStatic() ? "$static$init$" : "$init$";
                MethodNode fieldInitializer = new MethodNode(baseName + Traits.remappedFieldName(trait, field.getName()), 4105, field.getOriginType(), new Parameter[]{this.createSelfParameter(trait, field.isStatic())}, ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(initCode.getExpression()));
                helper.addMethod(fieldInitializer);
            }
            BlockStatement code = (BlockStatement)selectedMethod.getCode();
            MethodCallExpression mce = field.isStatic() ? new MethodCallExpression((Expression)new ClassExpression(INVOKERHELPER_CLASSNODE), "invokeStaticMethod", (Expression)new ArgumentListExpression(thisObject, new ConstantExpression(Traits.helperSetterName(field)), initCode.getExpression())) : new MethodCallExpression((Expression)new CastExpression(this.createReceiverType(field.isStatic(), fieldHelper), thisObject), Traits.helperSetterName(field), (Expression)new CastExpression(field.getOriginType(), initCode.getExpression()));
            mce.setImplicitThis(false);
            mce.setSourcePosition(initialExpression);
            code.addStatement(new ExpressionStatement(mce));
        }
        fieldHelper.addMethod(Traits.helperSetterName(field), 1025, field.getOriginType(), new Parameter[]{new Parameter(field.getOriginType(), "val")}, ClassNode.EMPTY_ARRAY, null);
        fieldHelper.addMethod(Traits.helperGetterName(field), 1025, field.getOriginType(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, null);
        int mods = field.getModifiers() & 0x9B;
        String dummyFieldName = String.format("$0x%04x", mods) + Traits.remappedFieldName(field.getOwner(), field.getName());
        FieldNode dummyField = new FieldNode(dummyFieldName, 4121, field.getOriginType(), fieldHelper, null);
        LinkedList<AnnotationNode> copied = new LinkedList<AnnotationNode>();
        LinkedList<AnnotationNode> notCopied = new LinkedList<AnnotationNode>();
        GeneralUtils.copyAnnotatedNodeAnnotations(field, copied, notCopied);
        dummyField.addAnnotations(copied);
        fieldHelper.addField(dummyField);
        dummyFieldName = (field.isStatic() ? "$static" : "$ins") + (field.isPublic() ? "$0" : "$1") + Traits.remappedFieldName(field.getOwner(), field.getName());
        dummyField = new FieldNode(dummyFieldName, 4121, field.getOriginType(), fieldHelper, null);
        copied = new LinkedList();
        notCopied = new LinkedList();
        GeneralUtils.copyAnnotatedNodeAnnotations(field, copied, notCopied);
        dummyField.addAnnotations(copied);
        fieldHelper.addField(dummyField);
    }

    private MethodNode processMethod(ClassNode traitClass, ClassNode traitHelperClass, MethodNode methodNode, ClassNode fieldHelper, Collection<String> knownFields) {
        Parameter[] initialParams = methodNode.getParameters();
        Parameter[] newParams = new Parameter[initialParams.length + 1];
        newParams[0] = this.createSelfParameter(traitClass, methodNode.isStatic());
        System.arraycopy(initialParams, 0, newParams, 1, initialParams.length);
        int mod = methodNode.isPrivate() ? 2 : 1;
        MethodNode mNode = new MethodNode(methodNode.getName(), mod | 8, methodNode.getReturnType(), newParams, methodNode.getExceptions(), this.processBody(new VariableExpression(newParams[0]), methodNode.getCode(), traitClass, traitHelperClass, fieldHelper, knownFields));
        mNode.setSourcePosition(methodNode);
        mNode.addAnnotations(TraitASTTransformation.filterAnnotations(methodNode.getAnnotations()));
        mNode.setGenericsTypes(methodNode.getGenericsTypes());
        if (methodNode.isAbstract()) {
            mNode.setModifiers(1025);
        } else {
            methodNode.addAnnotation(new AnnotationNode(Traits.IMPLEMENTED_CLASSNODE));
        }
        methodNode.setCode(null);
        if (!methodNode.isPrivate() && !methodNode.isStatic()) {
            methodNode.setModifiers(1025);
        }
        return mNode;
    }

    private static List<AnnotationNode> filterAnnotations(List<AnnotationNode> annotations) {
        ArrayList<AnnotationNode> result = new ArrayList<AnnotationNode>(annotations.size());
        for (AnnotationNode annotation : annotations) {
            if (OVERRIDE_CLASSNODE.equals(annotation.getClassNode())) continue;
            result.add(annotation);
        }
        return result;
    }

    private Parameter createSelfParameter(ClassNode traitClass, boolean isStatic) {
        ClassNode rawType = traitClass.getPlainNodeReference();
        ClassNode type = this.createReceiverType(isStatic, rawType);
        return new Parameter(type, isStatic ? "$static$self" : "$self");
    }

    private ClassNode createReceiverType(boolean isStatic, ClassNode rawType) {
        ClassNode type;
        if (isStatic) {
            type = ClassHelper.CLASS_Type.getPlainNodeReference();
            type.setGenericsTypes(new GenericsType[]{new GenericsType(rawType)});
        } else {
            type = rawType;
        }
        return type;
    }

    private Statement processBody(VariableExpression thisObject, Statement code, ClassNode trait, ClassNode traitHelper, ClassNode fieldHelper, Collection<String> knownFields) {
        if (code == null) {
            return null;
        }
        NAryOperationRewriter operationRewriter = new NAryOperationRewriter(this.unit, knownFields);
        code.visit(operationRewriter);
        SuperCallTraitTransformer superTrn = new SuperCallTraitTransformer(this.unit);
        code.visit(superTrn);
        TraitReceiverTransformer trn = new TraitReceiverTransformer(thisObject, this.unit, trait, traitHelper, fieldHelper, knownFields);
        code.visit(trn);
        return code;
    }

    @Override
    public void setCompilationUnit(CompilationUnit unit) {
        this.compilationUnit = unit;
    }

    private static class PostTypeCheckingExpressionReplacer
    extends ClassCodeExpressionTransformer {
        private final SourceUnit sourceUnit;

        private PostTypeCheckingExpressionReplacer(SourceUnit sourceUnit) {
            this.sourceUnit = sourceUnit;
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return this.sourceUnit;
        }

        @Override
        public Expression transform(Expression exp) {
            Expression replacement;
            if (exp != null && (replacement = (Expression)exp.getNodeMetaData(POST_TYPECHECKING_REPLACEMENT)) != null) {
                return replacement;
            }
            return super.transform(exp);
        }
    }

    private static class DefaultArgsMethodsAdder
    extends Verifier {
        private DefaultArgsMethodsAdder() {
        }

        @Override
        public void addDefaultParameterMethods(ClassNode node) {
            this.setClassNode(node);
            super.addDefaultParameterMethods(node);
        }
    }
}


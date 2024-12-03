/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.BuilderASTTransformation;

public class DefaultStrategy
extends BuilderASTTransformation.AbstractBuilderStrategy {
    private static final Expression DEFAULT_INITIAL_VALUE = null;
    private static final int PUBLIC_STATIC = 9;

    @Override
    public void build(BuilderASTTransformation transform, AnnotatedNode annotatedNode, AnnotationNode anno) {
        if (this.unsupportedAttribute(transform, anno, "forClass")) {
            return;
        }
        if (annotatedNode instanceof ClassNode) {
            this.buildClass(transform, (ClassNode)annotatedNode, anno);
        } else if (annotatedNode instanceof MethodNode) {
            this.buildMethod(transform, (MethodNode)annotatedNode, anno);
        }
    }

    public void buildMethod(BuilderASTTransformation transform, MethodNode mNode, AnnotationNode anno) {
        if (transform.getMemberValue(anno, "includes") != null || transform.getMemberValue(anno, "excludes") != null) {
            transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: includes/excludes only allowed on classes", anno);
        }
        ClassNode buildee = mNode.getDeclaringClass();
        ClassNode builder = DefaultStrategy.createBuilder(anno, buildee);
        DefaultStrategy.createBuilderFactoryMethod(anno, buildee, builder);
        for (Parameter parameter : mNode.getParameters()) {
            builder.addField(DefaultStrategy.createFieldCopy(buildee, parameter));
            builder.addMethod(this.createBuilderMethodForProp(builder, new BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo(parameter.getName(), parameter.getType()), DefaultStrategy.getPrefix(anno)));
        }
        builder.addMethod(DefaultStrategy.createBuildMethodForMethod(anno, buildee, mNode, mNode.getParameters()));
    }

    public void buildClass(BuilderASTTransformation transform, ClassNode buildee, AnnotationNode anno) {
        ArrayList<String> excludes = new ArrayList<String>();
        ArrayList<String> includes = new ArrayList<String>();
        if (!this.getIncludeExclude(transform, anno, buildee, excludes, includes)) {
            return;
        }
        ClassNode builder = DefaultStrategy.createBuilder(anno, buildee);
        DefaultStrategy.createBuilderFactoryMethod(anno, buildee, builder);
        List<FieldNode> fields = GeneralUtils.getInstancePropertyFields(buildee);
        List<FieldNode> filteredFields = DefaultStrategy.selectFieldsFromExistingClass(fields, includes, excludes);
        for (FieldNode fieldNode : filteredFields) {
            ClassNode correctedType = DefaultStrategy.getCorrectedType(buildee, fieldNode);
            String fieldName = fieldNode.getName();
            builder.addField(DefaultStrategy.createFieldCopy(buildee, fieldName, correctedType));
            builder.addMethod(this.createBuilderMethodForProp(builder, new BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo(fieldName, correctedType), DefaultStrategy.getPrefix(anno)));
        }
        builder.addMethod(DefaultStrategy.createBuildMethod(anno, buildee, filteredFields));
    }

    private static ClassNode getCorrectedType(ClassNode buildee, FieldNode fieldNode) {
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(fieldNode.getDeclaringClass());
        GenericsUtils.extractSuperClassGenerics(fieldNode.getType(), buildee, genericsSpec);
        return GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, fieldNode.getType());
    }

    private static void createBuilderFactoryMethod(AnnotationNode anno, ClassNode buildee, ClassNode builder) {
        buildee.getModule().addClass(builder);
        buildee.addMethod(DefaultStrategy.createBuilderMethod(anno, builder));
    }

    private static ClassNode createBuilder(AnnotationNode anno, ClassNode buildee) {
        return new InnerClassNode(buildee, DefaultStrategy.getFullName(anno, buildee), 9, ClassHelper.OBJECT_TYPE);
    }

    private static String getFullName(AnnotationNode anno, ClassNode buildee) {
        String builderClassName = AbstractASTTransformation.getMemberStringValue(anno, "builderClassName", buildee.getNameWithoutPackage() + "Builder");
        return buildee.getName() + "$" + builderClassName;
    }

    private static String getPrefix(AnnotationNode anno) {
        return AbstractASTTransformation.getMemberStringValue(anno, "prefix", "");
    }

    private static MethodNode createBuildMethodForMethod(AnnotationNode anno, ClassNode buildee, MethodNode mNode, Parameter[] params) {
        ClassNode returnType;
        String buildMethodName = AbstractASTTransformation.getMemberStringValue(anno, "buildMethodName", "build");
        BlockStatement body = new BlockStatement();
        if (mNode instanceof ConstructorNode) {
            returnType = GenericsUtils.newClass(buildee);
            body.addStatement(GeneralUtils.returnS(GeneralUtils.ctorX(GenericsUtils.newClass(mNode.getDeclaringClass()), GeneralUtils.args(params))));
        } else {
            body.addStatement(GeneralUtils.returnS(GeneralUtils.callX(GenericsUtils.newClass(mNode.getDeclaringClass()), mNode.getName(), (Expression)GeneralUtils.args(params))));
            returnType = GenericsUtils.newClass(mNode.getReturnType());
        }
        return new MethodNode(buildMethodName, 1, returnType, BuilderASTTransformation.NO_PARAMS, BuilderASTTransformation.NO_EXCEPTIONS, body);
    }

    private static MethodNode createBuilderMethod(AnnotationNode anno, ClassNode builder) {
        String builderMethodName = AbstractASTTransformation.getMemberStringValue(anno, "builderMethodName", "builder");
        BlockStatement body = new BlockStatement();
        body.addStatement(GeneralUtils.returnS(GeneralUtils.ctorX(builder)));
        return new MethodNode(builderMethodName, 9, builder, BuilderASTTransformation.NO_PARAMS, BuilderASTTransformation.NO_EXCEPTIONS, body);
    }

    private static MethodNode createBuildMethod(AnnotationNode anno, ClassNode buildee, List<FieldNode> fields) {
        String buildMethodName = AbstractASTTransformation.getMemberStringValue(anno, "buildMethodName", "build");
        BlockStatement body = new BlockStatement();
        body.addStatement(GeneralUtils.returnS(DefaultStrategy.initializeInstance(buildee, fields, body)));
        return new MethodNode(buildMethodName, 1, GenericsUtils.newClass(buildee), BuilderASTTransformation.NO_PARAMS, BuilderASTTransformation.NO_EXCEPTIONS, body);
    }

    private MethodNode createBuilderMethodForProp(ClassNode builder, BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo pinfo, String prefix) {
        ClassNode fieldType = pinfo.getType();
        String fieldName = pinfo.getName();
        String setterName = this.getSetterName(prefix, fieldName);
        return new MethodNode(setterName, 1, GenericsUtils.newClass(builder), GeneralUtils.params(GeneralUtils.param(fieldType, fieldName)), BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.propX((Expression)GeneralUtils.varX("this"), GeneralUtils.constX(fieldName)), GeneralUtils.varX(fieldName, fieldType))), GeneralUtils.returnS(GeneralUtils.varX("this", builder))));
    }

    private static FieldNode createFieldCopy(ClassNode buildee, Parameter param) {
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(buildee);
        GenericsUtils.extractSuperClassGenerics(param.getType(), buildee, genericsSpec);
        ClassNode correctedParamType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, param.getType());
        return new FieldNode(param.getName(), 2, correctedParamType, buildee, param.getInitialExpression());
    }

    private static FieldNode createFieldCopy(ClassNode buildee, String fieldName, ClassNode fieldType) {
        return new FieldNode(fieldName, 2, fieldType, buildee, DEFAULT_INITIAL_VALUE);
    }

    private static List<FieldNode> selectFieldsFromExistingClass(List<FieldNode> fieldNodes, List<String> includes, List<String> excludes) {
        ArrayList<FieldNode> fields = new ArrayList<FieldNode>();
        for (FieldNode fNode : fieldNodes) {
            if (AbstractASTTransformation.shouldSkip(fNode.getName(), excludes, includes)) continue;
            fields.add(fNode);
        }
        return fields;
    }

    private static Expression initializeInstance(ClassNode buildee, List<FieldNode> fields, BlockStatement body) {
        VariableExpression instance = GeneralUtils.varX("_the" + buildee.getNameWithoutPackage(), buildee);
        body.addStatement(GeneralUtils.declS(instance, GeneralUtils.ctorX(buildee)));
        for (FieldNode field : fields) {
            body.addStatement(GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.propX((Expression)instance, field.getName()), GeneralUtils.varX(field))));
        }
        return instance;
    }
}


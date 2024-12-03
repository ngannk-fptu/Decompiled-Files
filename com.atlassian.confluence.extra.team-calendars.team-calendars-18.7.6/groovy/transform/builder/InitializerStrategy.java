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
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.BuilderASTTransformation;
import org.codehaus.groovy.transform.ImmutableASTTransformation;

public class InitializerStrategy
extends BuilderASTTransformation.AbstractBuilderStrategy {
    private static final int PUBLIC_STATIC = 9;
    private static final Expression DEFAULT_INITIAL_VALUE = null;

    @Override
    public void build(BuilderASTTransformation transform, AnnotatedNode annotatedNode, AnnotationNode anno) {
        if (this.unsupportedAttribute(transform, anno, "forClass")) {
            return;
        }
        if (annotatedNode instanceof ClassNode) {
            this.createBuilderForAnnotatedClass(transform, (ClassNode)annotatedNode, anno);
        } else if (annotatedNode instanceof MethodNode) {
            this.createBuilderForAnnotatedMethod(transform, (MethodNode)annotatedNode, anno);
        }
    }

    private void createBuilderForAnnotatedClass(BuilderASTTransformation transform, ClassNode buildee, AnnotationNode anno) {
        ArrayList<String> excludes = new ArrayList<String>();
        ArrayList<String> includes = new ArrayList<String>();
        if (!this.getIncludeExclude(transform, anno, buildee, excludes, includes)) {
            return;
        }
        List<FieldNode> fields = GeneralUtils.getInstancePropertyFields(buildee);
        List<FieldNode> filteredFields = InitializerStrategy.filterFields(fields, includes, excludes);
        if (filteredFields.isEmpty()) {
            transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: at least one property is required for this strategy", anno);
        }
        ClassNode builder = InitializerStrategy.createInnerHelperClass(buildee, InitializerStrategy.getBuilderClassName(buildee, anno), filteredFields.size());
        InitializerStrategy.addFields(buildee, filteredFields, builder);
        this.buildCommon(buildee, anno, filteredFields, builder);
        InitializerStrategy.createBuildeeConstructors(transform, buildee, builder, filteredFields, true);
    }

    private void createBuilderForAnnotatedMethod(BuilderASTTransformation transform, MethodNode mNode, AnnotationNode anno) {
        if (transform.getMemberValue(anno, "includes") != null || transform.getMemberValue(anno, "includes") != null) {
            transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: includes/excludes only allowed on classes", anno);
        }
        if (mNode instanceof ConstructorNode) {
            mNode.setModifiers(4098);
        } else {
            if ((mNode.getModifiers() & 8) == 0) {
                transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: method builders only allowed on static methods", anno);
            }
            mNode.setModifiers(4106);
        }
        ClassNode buildee = mNode.getDeclaringClass();
        Parameter[] parameters = mNode.getParameters();
        if (parameters.length == 0) {
            transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: at least one parameter is required for this strategy", anno);
        }
        ClassNode builder = InitializerStrategy.createInnerHelperClass(buildee, InitializerStrategy.getBuilderClassName(buildee, anno), parameters.length);
        List<FieldNode> convertedFields = InitializerStrategy.convertParamsToFields(builder, parameters);
        this.buildCommon(buildee, anno, convertedFields, builder);
        if (mNode instanceof ConstructorNode) {
            InitializerStrategy.createBuildeeConstructors(transform, buildee, builder, convertedFields, false);
        } else {
            InitializerStrategy.createBuildeeMethods(buildee, mNode, builder, convertedFields);
        }
    }

    private static String getBuilderClassName(ClassNode buildee, AnnotationNode anno) {
        return AbstractASTTransformation.getMemberStringValue(anno, "builderClassName", buildee.getNameWithoutPackage() + "Initializer");
    }

    private static void addFields(ClassNode buildee, List<FieldNode> filteredFields, ClassNode builder) {
        for (FieldNode filteredField : filteredFields) {
            builder.addField(InitializerStrategy.createFieldCopy(buildee, filteredField));
        }
    }

    private void buildCommon(ClassNode buildee, AnnotationNode anno, List<FieldNode> fieldNodes, ClassNode builder) {
        String prefix = AbstractASTTransformation.getMemberStringValue(anno, "prefix", "");
        String buildMethodName = AbstractASTTransformation.getMemberStringValue(anno, "buildMethodName", "create");
        InitializerStrategy.createBuilderConstructors(builder, buildee, fieldNodes);
        buildee.getModule().addClass(builder);
        String builderMethodName = AbstractASTTransformation.getMemberStringValue(anno, "builderMethodName", "createInitializer");
        buildee.addMethod(InitializerStrategy.createBuilderMethod(buildMethodName, builder, fieldNodes.size(), builderMethodName));
        for (int i = 0; i < fieldNodes.size(); ++i) {
            builder.addMethod(this.createBuilderMethodForField(builder, fieldNodes, prefix, i));
        }
        builder.addMethod(InitializerStrategy.createBuildMethod(builder, buildMethodName, fieldNodes));
    }

    private static List<FieldNode> convertParamsToFields(ClassNode builder, Parameter[] parameters) {
        ArrayList<FieldNode> fieldNodes = new ArrayList<FieldNode>();
        for (Parameter parameter : parameters) {
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(builder);
            ClassNode correctedType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, parameter.getType());
            FieldNode fieldNode = new FieldNode(parameter.getName(), parameter.getModifiers(), correctedType, builder, DEFAULT_INITIAL_VALUE);
            fieldNodes.add(fieldNode);
            builder.addField(fieldNode);
        }
        return fieldNodes;
    }

    private static ClassNode createInnerHelperClass(ClassNode buildee, String builderClassName, int fieldsSize) {
        String fullName = buildee.getName() + "$" + builderClassName;
        InnerClassNode builder = new InnerClassNode(buildee, fullName, 9, ClassHelper.OBJECT_TYPE);
        GenericsType[] gtypes = new GenericsType[fieldsSize];
        for (int i = 0; i < gtypes.length; ++i) {
            gtypes[i] = InitializerStrategy.makePlaceholder(i);
        }
        builder.setGenericsTypes(gtypes);
        return builder;
    }

    private static MethodNode createBuilderMethod(String buildMethodName, ClassNode builder, int numFields, String builderMethodName) {
        BlockStatement body = new BlockStatement();
        body.addStatement(GeneralUtils.returnS(GeneralUtils.callX(builder, buildMethodName)));
        ClassNode returnType = GenericsUtils.makeClassSafeWithGenerics(builder, InitializerStrategy.unsetGenTypes(numFields));
        return new MethodNode(builderMethodName, 9, returnType, BuilderASTTransformation.NO_PARAMS, BuilderASTTransformation.NO_EXCEPTIONS, body);
    }

    private static GenericsType[] unsetGenTypes(int numFields) {
        GenericsType[] gtypes = new GenericsType[numFields];
        for (int i = 0; i < gtypes.length; ++i) {
            gtypes[i] = new GenericsType(ClassHelper.make(UNSET.class));
        }
        return gtypes;
    }

    private static GenericsType[] setGenTypes(int numFields) {
        GenericsType[] gtypes = new GenericsType[numFields];
        for (int i = 0; i < gtypes.length; ++i) {
            gtypes[i] = new GenericsType(ClassHelper.make(SET.class));
        }
        return gtypes;
    }

    private static void createBuilderConstructors(ClassNode builder, ClassNode buildee, List<FieldNode> fields) {
        builder.addConstructor(2, BuilderASTTransformation.NO_PARAMS, BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.ctorSuperS()));
        BlockStatement body = new BlockStatement();
        body.addStatement(GeneralUtils.ctorSuperS());
        InitializerStrategy.initializeFields(fields, body);
        builder.addConstructor(2, InitializerStrategy.getParams(fields, buildee), BuilderASTTransformation.NO_EXCEPTIONS, body);
    }

    private static void createBuildeeConstructors(BuilderASTTransformation transform, ClassNode buildee, ClassNode builder, List<FieldNode> fields, boolean needsConstructor) {
        ConstructorNode initializer = InitializerStrategy.createInitializerConstructor(buildee, builder, fields);
        if (transform.hasAnnotation(buildee, ImmutableASTTransformation.MY_TYPE)) {
            initializer.putNodeMetaData("Immutable.Safe", Boolean.TRUE);
        } else if (needsConstructor) {
            BlockStatement body = new BlockStatement();
            body.addStatement(GeneralUtils.ctorSuperS());
            InitializerStrategy.initializeFields(fields, body);
            buildee.addConstructor(4098, InitializerStrategy.getParams(fields, buildee), BuilderASTTransformation.NO_EXCEPTIONS, body);
        }
    }

    private static void createBuildeeMethods(ClassNode buildee, MethodNode mNode, ClassNode builder, List<FieldNode> fields) {
        ClassNode paramType = GenericsUtils.makeClassSafeWithGenerics(builder, InitializerStrategy.setGenTypes(fields.size()));
        ArrayList<Expression> argsList = new ArrayList<Expression>();
        Parameter initParam = GeneralUtils.param(paramType, "initializer");
        for (FieldNode fieldNode : fields) {
            argsList.add(GeneralUtils.propX((Expression)GeneralUtils.varX(initParam), fieldNode.getName()));
        }
        String newName = "$" + mNode.getName();
        buildee.addMethod(mNode.getName(), 9, mNode.getReturnType(), GeneralUtils.params(GeneralUtils.param(paramType, "initializer")), BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.stmt(GeneralUtils.callX(buildee, newName, (Expression)GeneralUtils.args(argsList)))));
        InitializerStrategy.renameMethod(buildee, mNode, newName);
    }

    private static void renameMethod(ClassNode buildee, MethodNode mNode, String newName) {
        buildee.addMethod(newName, mNode.getModifiers(), mNode.getReturnType(), mNode.getParameters(), mNode.getExceptions(), mNode.getCode());
        buildee.removeMethod(mNode);
    }

    private static Parameter[] getParams(List<FieldNode> fields, ClassNode cNode) {
        Parameter[] parameters = new Parameter[fields.size()];
        for (int i = 0; i < parameters.length; ++i) {
            FieldNode fNode = fields.get(i);
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(fNode.getDeclaringClass());
            GenericsUtils.extractSuperClassGenerics(fNode.getType(), cNode, genericsSpec);
            ClassNode correctedType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, fNode.getType());
            parameters[i] = new Parameter(correctedType, fNode.getName());
        }
        return parameters;
    }

    private static ConstructorNode createInitializerConstructor(ClassNode buildee, ClassNode builder, List<FieldNode> fields) {
        ClassNode paramType = GenericsUtils.makeClassSafeWithGenerics(builder, InitializerStrategy.setGenTypes(fields.size()));
        ArrayList<Expression> argsList = new ArrayList<Expression>();
        Parameter initParam = GeneralUtils.param(paramType, "initializer");
        for (FieldNode fieldNode : fields) {
            argsList.add(GeneralUtils.propX((Expression)GeneralUtils.varX(initParam), fieldNode.getName()));
        }
        return buildee.addConstructor(1, GeneralUtils.params(GeneralUtils.param(paramType, "initializer")), BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.ctorThisS(GeneralUtils.args(argsList))));
    }

    private static MethodNode createBuildMethod(ClassNode builder, String buildMethodName, List<FieldNode> fields) {
        ClassNode returnType = GenericsUtils.makeClassSafeWithGenerics(builder, InitializerStrategy.unsetGenTypes(fields.size()));
        return new MethodNode(buildMethodName, 9, returnType, BuilderASTTransformation.NO_PARAMS, BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.returnS(GeneralUtils.ctorX(returnType))));
    }

    private MethodNode createBuilderMethodForField(ClassNode builder, List<FieldNode> fields, String prefix, int fieldPos) {
        String fieldName = fields.get(fieldPos).getName();
        String setterName = this.getSetterName(prefix, fieldName);
        GenericsType[] gtypes = new GenericsType[fields.size()];
        ArrayList<Expression> argList = new ArrayList<Expression>();
        for (int i = 0; i < fields.size(); ++i) {
            gtypes[i] = i == fieldPos ? new GenericsType(ClassHelper.make(SET.class)) : InitializerStrategy.makePlaceholder(i);
            argList.add(i == fieldPos ? GeneralUtils.propX((Expression)GeneralUtils.varX("this"), GeneralUtils.constX(fieldName)) : GeneralUtils.varX(fields.get(i).getName()));
        }
        ClassNode returnType = GenericsUtils.makeClassSafeWithGenerics(builder, gtypes);
        FieldNode fNode = fields.get(fieldPos);
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(fNode.getDeclaringClass());
        GenericsUtils.extractSuperClassGenerics(fNode.getType(), builder, genericsSpec);
        ClassNode correctedType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, fNode.getType());
        return new MethodNode(setterName, 1, returnType, GeneralUtils.params(GeneralUtils.param(correctedType, fieldName)), BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.propX((Expression)GeneralUtils.varX("this"), GeneralUtils.constX(fieldName)), GeneralUtils.varX(fieldName, correctedType))), GeneralUtils.returnS(GeneralUtils.ctorX(returnType, GeneralUtils.args(argList)))));
    }

    private static GenericsType makePlaceholder(int i) {
        ClassNode type = ClassHelper.makeWithoutCaching("T" + i);
        type.setRedirect(ClassHelper.OBJECT_TYPE);
        type.setGenericsPlaceHolder(true);
        return new GenericsType(type);
    }

    private static FieldNode createFieldCopy(ClassNode buildee, FieldNode fNode) {
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(fNode.getDeclaringClass());
        GenericsUtils.extractSuperClassGenerics(fNode.getType(), buildee, genericsSpec);
        ClassNode correctedType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, fNode.getType());
        return new FieldNode(fNode.getName(), fNode.getModifiers(), correctedType, buildee, DEFAULT_INITIAL_VALUE);
    }

    private static List<FieldNode> filterFields(List<FieldNode> fieldNodes, List<String> includes, List<String> excludes) {
        ArrayList<FieldNode> fields = new ArrayList<FieldNode>();
        for (FieldNode fNode : fieldNodes) {
            if (AbstractASTTransformation.shouldSkip(fNode.getName(), excludes, includes)) continue;
            fields.add(fNode);
        }
        return fields;
    }

    private static void initializeFields(List<FieldNode> fields, BlockStatement body) {
        for (FieldNode field : fields) {
            body.addStatement(GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.propX((Expression)GeneralUtils.varX("this"), field.getName()), GeneralUtils.varX(GeneralUtils.param(field.getType(), field.getName())))));
        }
    }

    public static abstract class UNSET {
    }

    public static abstract class SET {
    }
}


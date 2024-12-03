/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.builder;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.BuilderASTTransformation;

public class ExternalStrategy
extends BuilderASTTransformation.AbstractBuilderStrategy {
    private static final Expression DEFAULT_INITIAL_VALUE = null;

    @Override
    public void build(BuilderASTTransformation transform, AnnotatedNode annotatedNode, AnnotationNode anno) {
        if (!(annotatedNode instanceof ClassNode)) {
            transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: building for " + annotatedNode.getClass().getSimpleName() + " not supported by " + this.getClass().getSimpleName(), annotatedNode);
            return;
        }
        ClassNode builder = (ClassNode)annotatedNode;
        String prefix = BuilderASTTransformation.getMemberStringValue(anno, "prefix", "");
        ClassNode buildee = transform.getMemberClassValue(anno, "forClass");
        if (buildee == null) {
            transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: 'forClass' must be specified for " + this.getClass().getName(), anno);
            return;
        }
        ArrayList<String> excludes = new ArrayList<String>();
        ArrayList<String> includes = new ArrayList<String>();
        if (!this.getIncludeExclude(transform, anno, buildee, excludes, includes)) {
            return;
        }
        if (this.unsupportedAttribute(transform, anno, "builderClassName")) {
            return;
        }
        if (this.unsupportedAttribute(transform, anno, "builderMethodName")) {
            return;
        }
        List<BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo> props = buildee.getModule() == null ? ExternalStrategy.getPropertyInfoFromBeanInfo(buildee, includes, excludes) : ExternalStrategy.getPropertyInfoFromClassNode(buildee, includes, excludes);
        for (String name : includes) {
            this.checkKnownProperty(transform, anno, name, props);
        }
        for (BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo prop : props) {
            builder.addField(ExternalStrategy.createFieldCopy(builder, prop));
            builder.addMethod(this.createBuilderMethodForField(builder, prop, prefix));
        }
        builder.addMethod(ExternalStrategy.createBuildMethod(transform, anno, buildee, props));
    }

    private static MethodNode createBuildMethod(BuilderASTTransformation transform, AnnotationNode anno, ClassNode sourceClass, List<BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo> fields) {
        String buildMethodName = BuilderASTTransformation.getMemberStringValue(anno, "buildMethodName", "build");
        BlockStatement body = new BlockStatement();
        Expression sourceClassInstance = ExternalStrategy.initializeInstance(sourceClass, fields, body);
        body.addStatement(GeneralUtils.returnS(sourceClassInstance));
        return new MethodNode(buildMethodName, 1, sourceClass, BuilderASTTransformation.NO_PARAMS, BuilderASTTransformation.NO_EXCEPTIONS, body);
    }

    private MethodNode createBuilderMethodForField(ClassNode builderClass, BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo prop, String prefix) {
        String propName = prop.getName().equals("class") ? "clazz" : prop.getName();
        String setterName = this.getSetterName(prefix, prop.getName());
        return new MethodNode(setterName, 1, GenericsUtils.newClass(builderClass), GeneralUtils.params(GeneralUtils.param(GenericsUtils.newClass(prop.getType()), propName)), BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.propX((Expression)GeneralUtils.varX("this"), GeneralUtils.constX(propName)), GeneralUtils.varX(propName))), GeneralUtils.returnS(GeneralUtils.varX("this", GenericsUtils.newClass(builderClass)))));
    }

    private static FieldNode createFieldCopy(ClassNode builderClass, BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo prop) {
        String propName = prop.getName();
        return new FieldNode(propName.equals("class") ? "clazz" : propName, 2, GenericsUtils.newClass(prop.getType()), builderClass, DEFAULT_INITIAL_VALUE);
    }

    public static List<BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo> getPropertyInfoFromBeanInfo(ClassNode cNode, List<String> includes, List<String> excludes) {
        ArrayList<BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo> result = new ArrayList<BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(cNode.getTypeClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (AbstractASTTransformation.shouldSkip(descriptor.getName(), excludes, includes) || descriptor.isHidden() || descriptor.getWriteMethod() == null) continue;
                result.add(new BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo(descriptor.getName(), ClassHelper.make(descriptor.getPropertyType())));
            }
        }
        catch (IntrospectionException introspectionException) {
            // empty catch block
        }
        return result;
    }

    private static Expression initializeInstance(ClassNode sourceClass, List<BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo> props, BlockStatement body) {
        VariableExpression instance = GeneralUtils.varX("_the" + sourceClass.getNameWithoutPackage(), sourceClass);
        body.addStatement(GeneralUtils.declS(instance, GeneralUtils.ctorX(sourceClass)));
        for (BuilderASTTransformation.AbstractBuilderStrategy.PropertyInfo prop : props) {
            body.addStatement(GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.propX((Expression)instance, prop.getName()), GeneralUtils.varX(prop.getName().equals("class") ? "clazz" : prop.getName(), GenericsUtils.newClass(prop.getType())))));
        }
        return instance;
    }
}


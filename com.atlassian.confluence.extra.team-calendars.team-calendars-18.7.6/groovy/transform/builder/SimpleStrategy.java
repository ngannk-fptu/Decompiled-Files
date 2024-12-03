/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.builder;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.BuilderASTTransformation;

public class SimpleStrategy
extends BuilderASTTransformation.AbstractBuilderStrategy {
    @Override
    public void build(BuilderASTTransformation transform, AnnotatedNode annotatedNode, AnnotationNode anno) {
        if (!(annotatedNode instanceof ClassNode)) {
            transform.addError("Error during " + BuilderASTTransformation.MY_TYPE_NAME + " processing: building for " + annotatedNode.getClass().getSimpleName() + " not supported by " + this.getClass().getSimpleName(), annotatedNode);
            return;
        }
        ClassNode buildee = (ClassNode)annotatedNode;
        if (this.unsupportedAttribute(transform, anno, "builderClassName")) {
            return;
        }
        if (this.unsupportedAttribute(transform, anno, "buildMethodName")) {
            return;
        }
        if (this.unsupportedAttribute(transform, anno, "builderMethodName")) {
            return;
        }
        if (this.unsupportedAttribute(transform, anno, "forClass")) {
            return;
        }
        ArrayList<String> excludes = new ArrayList<String>();
        ArrayList<String> includes = new ArrayList<String>();
        if (!this.getIncludeExclude(transform, anno, buildee, excludes, includes)) {
            return;
        }
        String prefix = BuilderASTTransformation.getMemberStringValue(anno, "prefix", "set");
        List<FieldNode> fields = GeneralUtils.getInstancePropertyFields(buildee);
        for (String name : includes) {
            this.checkKnownField(transform, anno, name, fields);
        }
        for (FieldNode field : fields) {
            String fieldName = field.getName();
            if (AbstractASTTransformation.shouldSkip(fieldName, excludes, includes)) continue;
            String methodName = this.getSetterName(prefix, fieldName);
            Parameter parameter = GeneralUtils.param(field.getType(), fieldName);
            buildee.addMethod(methodName, 1, GenericsUtils.newClass(buildee), GeneralUtils.params(parameter), BuilderASTTransformation.NO_EXCEPTIONS, GeneralUtils.block(GeneralUtils.stmt(GeneralUtils.assignX(GeneralUtils.fieldX(field), GeneralUtils.varX(parameter))), GeneralUtils.returnS(GeneralUtils.varX("this"))));
        }
    }
}


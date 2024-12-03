/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.text.markup.MarkupBuilderCodeTransformer;
import groovy.text.markup.MarkupTemplateEngine;
import groovy.text.markup.TemplateConfiguration;
import java.util.LinkedList;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

class TemplateASTTransformer
extends CompilationCustomizer {
    private static final ClassNode TEMPLATECONFIG_CLASSNODE = ClassHelper.make(TemplateConfiguration.class);
    private final TemplateConfiguration config;

    public TemplateASTTransformer(TemplateConfiguration config) {
        super(CompilePhase.SEMANTIC_ANALYSIS);
        this.config = config;
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        if (classNode.isScriptBody()) {
            classNode.setSuperClass(ClassHelper.make(this.config.getBaseTemplateClass()));
            this.createConstructor(classNode);
            this.transformRunMethod(classNode, source);
            VariableScopeVisitor visitor = new VariableScopeVisitor(source);
            visitor.visitClass(classNode);
        }
    }

    private void transformRunMethod(ClassNode classNode, SourceUnit source) {
        MethodNode runMethod = classNode.getDeclaredMethod("run", Parameter.EMPTY_ARRAY);
        Statement code = runMethod.getCode();
        MarkupBuilderCodeTransformer transformer = new MarkupBuilderCodeTransformer(source, classNode, this.config.isAutoEscape());
        code.visit(transformer);
    }

    private void createConstructor(ClassNode classNode) {
        Parameter[] params = new Parameter[]{new Parameter(MarkupTemplateEngine.MARKUPTEMPLATEENGINE_CLASSNODE, "engine"), new Parameter(ClassHelper.MAP_TYPE.getPlainNodeReference(), "model"), new Parameter(ClassHelper.MAP_TYPE.getPlainNodeReference(), "modelTypes"), new Parameter(TEMPLATECONFIG_CLASSNODE, "tplConfig")};
        LinkedList<Expression> vars = new LinkedList<Expression>();
        for (Parameter param : params) {
            vars.add(new VariableExpression(param));
        }
        ExpressionStatement body = new ExpressionStatement(new ConstructorCallExpression(ClassNode.SUPER, new ArgumentListExpression(vars)));
        ConstructorNode ctor = new ConstructorNode(1, params, ClassNode.EMPTY_ARRAY, body);
        classNode.addConstructor(ctor);
    }
}


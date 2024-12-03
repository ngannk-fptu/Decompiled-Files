/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BugException;
import freemarker.core.BuiltInWithParseTimeParameters;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ExpressionWithFixedResult;
import freemarker.core.LazilyGeneratedCollectionModel;
import freemarker.core.LazyCollectionTemplateModelIterator;
import freemarker.core.LazySequenceIterator;
import freemarker.core.LocalLambdaExpression;
import freemarker.core.Macro;
import freemarker.core.NonMethodException;
import freemarker.core.NonSequenceOrCollectionException;
import freemarker.core.ParseException;
import freemarker.core.Token;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.util.Collections;
import java.util.List;

abstract class IntermediateStreamOperationLikeBuiltIn
extends BuiltInWithParseTimeParameters {
    private Expression elementTransformerExp;
    private ElementTransformer precreatedElementTransformer;
    private boolean lazilyGeneratedResultEnabled;

    IntermediateStreamOperationLikeBuiltIn() {
    }

    @Override
    void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
        if (parameters.size() != 1) {
            throw this.newArgumentCountException("requires exactly 1", openParen, closeParen);
        }
        Expression elementTransformerExp = parameters.get(0);
        this.setElementTransformerExp(elementTransformerExp);
    }

    private void setElementTransformerExp(Expression elementTransformerExp) throws ParseException {
        this.elementTransformerExp = elementTransformerExp;
        if (this.elementTransformerExp instanceof LocalLambdaExpression) {
            LocalLambdaExpression localLambdaExp = (LocalLambdaExpression)this.elementTransformerExp;
            this.checkLocalLambdaParamCount(localLambdaExp, 1);
            this.precreatedElementTransformer = new LocalLambdaElementTransformer(localLambdaExp);
        }
    }

    @Override
    protected final boolean isLocalLambdaParameterSupported() {
        return true;
    }

    @Override
    final void enableLazilyGeneratedResult() {
        this.lazilyGeneratedResultEnabled = true;
    }

    protected final boolean isLazilyGeneratedResultEnabled() {
        return this.lazilyGeneratedResultEnabled;
    }

    @Override
    protected void setTarget(Expression target) {
        super.setTarget(target);
        target.enableLazilyGeneratedResult();
    }

    @Override
    protected List<Expression> getArgumentsAsList() {
        return Collections.singletonList(this.elementTransformerExp);
    }

    @Override
    protected int getArgumentsCount() {
        return 1;
    }

    @Override
    protected Expression getArgumentParameterValue(int argIdx) {
        if (argIdx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.elementTransformerExp;
    }

    protected Expression getElementTransformerExp() {
        return this.elementTransformerExp;
    }

    @Override
    protected void cloneArguments(Expression clone, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        try {
            ((IntermediateStreamOperationLikeBuiltIn)clone).setElementTransformerExp(this.elementTransformerExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        }
        catch (ParseException e) {
            throw new BugException("Deep-clone elementTransformerExp failed", e);
        }
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        boolean targetIsSequence;
        TemplateModelIterator targetIterator;
        TemplateModel targetValue = this.target.eval(env);
        if (targetValue instanceof TemplateCollectionModel) {
            targetIterator = this.isLazilyGeneratedResultEnabled() ? new LazyCollectionTemplateModelIterator((TemplateCollectionModel)targetValue) : ((TemplateCollectionModel)targetValue).iterator();
            targetIsSequence = targetValue instanceof LazilyGeneratedCollectionModel ? ((LazilyGeneratedCollectionModel)targetValue).isSequence() : targetValue instanceof TemplateSequenceModel;
        } else if (targetValue instanceof TemplateSequenceModel) {
            targetIterator = new LazySequenceIterator((TemplateSequenceModel)targetValue);
            targetIsSequence = true;
        } else {
            throw new NonSequenceOrCollectionException(this.target, targetValue, env);
        }
        return this.calculateResult(targetIterator, targetValue, targetIsSequence, this.evalElementTransformerExp(env), env);
    }

    private ElementTransformer evalElementTransformerExp(Environment env) throws TemplateException {
        if (this.precreatedElementTransformer != null) {
            return this.precreatedElementTransformer;
        }
        TemplateModel elementTransformerModel = this.elementTransformerExp.eval(env);
        if (elementTransformerModel instanceof TemplateMethodModel) {
            return new MethodElementTransformer((TemplateMethodModel)elementTransformerModel);
        }
        if (elementTransformerModel instanceof Macro) {
            return new FunctionElementTransformer((Macro)elementTransformerModel, this.elementTransformerExp);
        }
        throw new NonMethodException(this.elementTransformerExp, elementTransformerModel, true, true, null, env);
    }

    protected abstract TemplateModel calculateResult(TemplateModelIterator var1, TemplateModel var2, boolean var3, ElementTransformer var4, Environment var5) throws TemplateException;

    private static class FunctionElementTransformer
    implements ElementTransformer {
        private final Macro templateTransformer;
        private final Expression elementTransformerExp;

        public FunctionElementTransformer(Macro templateTransformer, Expression elementTransformerExp) {
            this.templateTransformer = templateTransformer;
            this.elementTransformerExp = elementTransformerExp;
        }

        @Override
        public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateException {
            ExpressionWithFixedResult functionArgExp = new ExpressionWithFixedResult(element, this.elementTransformerExp);
            return env.invokeFunction(env, this.templateTransformer, Collections.singletonList(functionArgExp), this.elementTransformerExp);
        }
    }

    private static class MethodElementTransformer
    implements ElementTransformer {
        private final TemplateMethodModel elementTransformer;

        public MethodElementTransformer(TemplateMethodModel elementTransformer) {
            this.elementTransformer = elementTransformer;
        }

        @Override
        public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateModelException {
            Object result = this.elementTransformer.exec(Collections.singletonList(element));
            return result instanceof TemplateModel ? (TemplateModel)result : env.getObjectWrapper().wrap(result);
        }
    }

    private static class LocalLambdaElementTransformer
    implements ElementTransformer {
        private final LocalLambdaExpression elementTransformerExp;

        public LocalLambdaElementTransformer(LocalLambdaExpression elementTransformerExp) {
            this.elementTransformerExp = elementTransformerExp;
        }

        @Override
        public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateException {
            return this.elementTransformerExp.invokeLambdaDefinedFunction(element, env);
        }
    }

    static interface ElementTransformer {
        public TemplateModel transformElement(TemplateModel var1, Environment var2) throws TemplateException;
    }
}


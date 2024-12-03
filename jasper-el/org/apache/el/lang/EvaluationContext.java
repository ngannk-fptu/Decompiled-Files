/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELResolver
 *  javax.el.EvaluationListener
 *  javax.el.FunctionMapper
 *  javax.el.ImportHandler
 *  javax.el.VariableMapper
 */
package org.apache.el.lang;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.EvaluationListener;
import javax.el.FunctionMapper;
import javax.el.ImportHandler;
import javax.el.VariableMapper;
import org.apache.el.lang.LambdaExpressionNestedState;
import org.apache.el.util.MessageFactory;

public final class EvaluationContext
extends ELContext {
    private final ELContext elContext;
    private final FunctionMapper fnMapper;
    private final VariableMapper varMapper;
    private LambdaExpressionNestedState lambdaExpressionNestedState;

    public EvaluationContext(ELContext elContext, FunctionMapper fnMapper, VariableMapper varMapper) {
        this.elContext = elContext;
        this.fnMapper = fnMapper;
        this.varMapper = varMapper;
    }

    public ELContext getELContext() {
        return this.elContext;
    }

    public FunctionMapper getFunctionMapper() {
        return this.fnMapper;
    }

    public VariableMapper getVariableMapper() {
        return this.varMapper;
    }

    public Object getContext(Class key) {
        return this.elContext.getContext(key);
    }

    public ELResolver getELResolver() {
        return this.elContext.getELResolver();
    }

    public boolean isPropertyResolved() {
        return this.elContext.isPropertyResolved();
    }

    public void putContext(Class key, Object contextObject) {
        this.elContext.putContext(key, contextObject);
    }

    public void setPropertyResolved(boolean resolved) {
        this.elContext.setPropertyResolved(resolved);
    }

    public Locale getLocale() {
        return this.elContext.getLocale();
    }

    public void setLocale(Locale locale) {
        this.elContext.setLocale(locale);
    }

    public void setPropertyResolved(Object base, Object property) {
        this.elContext.setPropertyResolved(base, property);
    }

    public ImportHandler getImportHandler() {
        return this.elContext.getImportHandler();
    }

    public void addEvaluationListener(EvaluationListener listener) {
        this.elContext.addEvaluationListener(listener);
    }

    public List<EvaluationListener> getEvaluationListeners() {
        return this.elContext.getEvaluationListeners();
    }

    public void notifyBeforeEvaluation(String expression) {
        this.elContext.notifyBeforeEvaluation(expression);
    }

    public void notifyAfterEvaluation(String expression) {
        this.elContext.notifyAfterEvaluation(expression);
    }

    public void notifyPropertyResolved(Object base, Object property) {
        this.elContext.notifyPropertyResolved(base, property);
    }

    public boolean isLambdaArgument(String name) {
        return this.elContext.isLambdaArgument(name);
    }

    public Object getLambdaArgument(String name) {
        return this.elContext.getLambdaArgument(name);
    }

    public void enterLambdaScope(Map<String, Object> arguments) {
        this.elContext.enterLambdaScope(arguments);
    }

    public void exitLambdaScope() {
        this.elContext.exitLambdaScope();
    }

    public Object convertToType(Object obj, Class<?> type) {
        return this.elContext.convertToType(obj, type);
    }

    public LambdaExpressionNestedState getLambdaExpressionNestedState() {
        if (this.lambdaExpressionNestedState != null) {
            return this.lambdaExpressionNestedState;
        }
        if (this.elContext instanceof EvaluationContext) {
            return ((EvaluationContext)this.elContext).getLambdaExpressionNestedState();
        }
        return null;
    }

    public void setLambdaExpressionNestedState(LambdaExpressionNestedState lambdaExpressionNestedState) {
        if (this.lambdaExpressionNestedState != null) {
            throw new IllegalStateException(MessageFactory.get("error.lambda.wrongNestedState"));
        }
        this.lambdaExpressionNestedState = lambdaExpressionNestedState;
    }
}


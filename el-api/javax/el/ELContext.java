/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.el.ELManager;
import javax.el.ELResolver;
import javax.el.EvaluationListener;
import javax.el.FunctionMapper;
import javax.el.ImportHandler;
import javax.el.Util;
import javax.el.VariableMapper;

public abstract class ELContext {
    private Locale locale;
    private Map<Class<?>, Object> map;
    private boolean resolved = false;
    private ImportHandler importHandler = null;
    private List<EvaluationListener> listeners;
    private Deque<Map<String, Object>> lambdaArguments = new ArrayDeque<Map<String, Object>>();

    public void setPropertyResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public void setPropertyResolved(Object base, Object property) {
        this.setPropertyResolved(true);
        this.notifyPropertyResolved(base, property);
    }

    public boolean isPropertyResolved() {
        return this.resolved;
    }

    public void putContext(Class key, Object contextObject) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(contextObject);
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(key, contextObject);
    }

    public Object getContext(Class key) {
        Objects.requireNonNull(key);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public abstract ELResolver getELResolver();

    public ImportHandler getImportHandler() {
        if (this.importHandler == null) {
            this.importHandler = new ImportHandler();
        }
        return this.importHandler;
    }

    public abstract FunctionMapper getFunctionMapper();

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public abstract VariableMapper getVariableMapper();

    public void addEvaluationListener(EvaluationListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<EvaluationListener>();
        }
        this.listeners.add(listener);
    }

    public List<EvaluationListener> getEvaluationListeners() {
        return this.listeners == null ? Collections.emptyList() : this.listeners;
    }

    public void notifyBeforeEvaluation(String expression) {
        if (this.listeners == null) {
            return;
        }
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.beforeEvaluation(this, expression);
            }
            catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public void notifyAfterEvaluation(String expression) {
        if (this.listeners == null) {
            return;
        }
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.afterEvaluation(this, expression);
            }
            catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public void notifyPropertyResolved(Object base, Object property) {
        if (this.listeners == null) {
            return;
        }
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.propertyResolved(this, base, property);
            }
            catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public boolean isLambdaArgument(String name) {
        for (Map<String, Object> arguments : this.lambdaArguments) {
            if (!arguments.containsKey(name)) continue;
            return true;
        }
        return false;
    }

    public Object getLambdaArgument(String name) {
        for (Map<String, Object> arguments : this.lambdaArguments) {
            Object result = arguments.get(name);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public void enterLambdaScope(Map<String, Object> arguments) {
        this.lambdaArguments.push(arguments);
    }

    public void exitLambdaScope() {
        this.lambdaArguments.pop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object convertToType(Object obj, Class<?> type) {
        boolean originalResolved = this.isPropertyResolved();
        this.setPropertyResolved(false);
        try {
            ELResolver resolver = this.getELResolver();
            if (resolver != null) {
                Object result = resolver.convertToType(this, obj, type);
                if (this.isPropertyResolved()) {
                    Object object = result;
                    return object;
                }
            }
        }
        finally {
            this.setPropertyResolved(originalResolved);
        }
        return ELManager.getExpressionFactory().coerceToType(obj, type);
    }
}


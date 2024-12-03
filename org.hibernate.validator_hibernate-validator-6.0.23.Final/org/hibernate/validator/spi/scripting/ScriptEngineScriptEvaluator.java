/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.spi.scripting;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;

@Incubating
public class ScriptEngineScriptEvaluator
implements ScriptEvaluator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ScriptEngine engine;

    public ScriptEngineScriptEvaluator(ScriptEngine engine) {
        this.engine = engine;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object evaluate(String script, Map<String, Object> bindings) throws ScriptEvaluationException {
        if (this.engineAllowsParallelAccessFromMultipleThreads()) {
            return this.doEvaluate(script, bindings);
        }
        ScriptEngine scriptEngine = this.engine;
        synchronized (scriptEngine) {
            return this.doEvaluate(script, bindings);
        }
    }

    private Object doEvaluate(String script, Map<String, Object> bindings) throws ScriptEvaluationException {
        try {
            return this.engine.eval(script, (Bindings)new SimpleBindings(bindings));
        }
        catch (Exception e) {
            throw LOG.getErrorExecutingScriptException(script, e);
        }
    }

    private boolean engineAllowsParallelAccessFromMultipleThreads() {
        String threadingType = (String)this.engine.getFactory().getParameter("THREADING");
        return "THREAD-ISOLATED".equals(threadingType) || "STATELESS".equals(threadingType);
    }
}


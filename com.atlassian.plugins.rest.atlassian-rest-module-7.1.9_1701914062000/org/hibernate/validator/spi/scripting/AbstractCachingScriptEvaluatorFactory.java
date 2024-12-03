/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.spi.scripting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorNotFoundException;

@Incubating
public abstract class AbstractCachingScriptEvaluatorFactory
implements ScriptEvaluatorFactory {
    private final ConcurrentMap<String, ScriptEvaluator> scriptEvaluatorCache = new ConcurrentHashMap<String, ScriptEvaluator>();

    @Override
    public ScriptEvaluator getScriptEvaluatorByLanguageName(String languageName) {
        return this.scriptEvaluatorCache.computeIfAbsent(languageName, this::createNewScriptEvaluator);
    }

    @Override
    public void clear() {
        this.scriptEvaluatorCache.clear();
    }

    protected abstract ScriptEvaluator createNewScriptEvaluator(String var1) throws ScriptEvaluatorNotFoundException;
}


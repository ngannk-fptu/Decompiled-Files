/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.spi.scripting;

import java.util.Map;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;

@Incubating
public interface ScriptEvaluator {
    public Object evaluate(String var1, Map<String, Object> var2) throws ScriptEvaluationException;
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.spi.scripting;

import org.hibernate.validator.Incubating;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;

@Incubating
public interface ScriptEvaluatorFactory {
    public ScriptEvaluator getScriptEvaluatorByLanguageName(String var1);

    public void clear();
}


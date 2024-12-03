/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.bsf.BSFEngine
 *  org.apache.bsf.BSFManager
 */
package org.apache.axis.components.script;

import org.apache.axis.components.script.Script;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFManager;

public class BSF
implements Script {
    public Object run(String language, String name, String scriptStr, String methodName, Object[] argValues) throws Exception {
        BSFManager manager = new BSFManager();
        BSFEngine engine = manager.loadScriptingEngine(language);
        manager.exec(language, "service script for '" + name + "'", 0, 0, (Object)scriptStr);
        Object result = engine.call(null, methodName, argValues);
        return result;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestResult
 */
package org.codehaus.groovy.runtime;

import junit.framework.Test;
import junit.framework.TestResult;
import org.codehaus.groovy.runtime.InvokerHelper;

public class ScriptTestAdapter
implements Test {
    private Class scriptClass;
    private String[] arguments;

    public ScriptTestAdapter(Class scriptClass, String[] arguments) {
        this.scriptClass = scriptClass;
        this.arguments = arguments;
    }

    public int countTestCases() {
        return 1;
    }

    public void run(TestResult result) {
        try {
            result.startTest((Test)this);
            InvokerHelper.runScript(this.scriptClass, this.arguments);
            result.endTest((Test)this);
        }
        catch (Exception e) {
            result.addError((Test)this, (Throwable)e);
        }
    }

    public String toString() {
        return "TestCase for script: " + this.scriptClass.getName();
    }
}


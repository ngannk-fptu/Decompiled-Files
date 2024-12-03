/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro;

import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.EngineManager;
import org.radeox.test.macro.MacroTestSupport;

public class ParamMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$ParamMacroTest;

    public ParamMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$ParamMacroTest == null ? (class$org$radeox$test$macro$ParamMacroTest = ParamMacroTest.class$("org.radeox.test.macro.ParamMacroTest")) : class$org$radeox$test$macro$ParamMacroTest);
    }

    public void testParamMacro() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", "stephan");
        this.context.setParameters(params);
        String result = EngineManager.getInstance().render("{hello:$user}", this.context);
        ParamMacroTest.assertEquals((String)"Hello <b>stephan</b>", (String)result);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


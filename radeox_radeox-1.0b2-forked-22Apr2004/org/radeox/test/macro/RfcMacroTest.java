/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.EngineManager;
import org.radeox.test.macro.MacroTestSupport;

public class RfcMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$RfcMacroTest;

    public RfcMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$RfcMacroTest == null ? (class$org$radeox$test$macro$RfcMacroTest = RfcMacroTest.class$("org.radeox.test.macro.RfcMacroTest")) : class$org$radeox$test$macro$RfcMacroTest);
    }

    public void testRfc() {
        String result = EngineManager.getInstance().render("{rfc:1}", this.context);
        RfcMacroTest.assertEquals((String)"RFC is rendered", (String)"<a href=\"http://zvon.org/tmRFC/RFC1/Output/index.html\">RFC1</a>", (String)result);
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


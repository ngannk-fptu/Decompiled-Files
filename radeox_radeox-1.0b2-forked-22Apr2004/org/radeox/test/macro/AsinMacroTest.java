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

public class AsinMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$AsinMacroTest;

    public AsinMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$AsinMacroTest == null ? (class$org$radeox$test$macro$AsinMacroTest = AsinMacroTest.class$("org.radeox.test.macro.AsinMacroTest")) : class$org$radeox$test$macro$AsinMacroTest);
    }

    public void testAsin() {
        String result = EngineManager.getInstance().render("{asin:B00005YVUD}", this.context);
        AsinMacroTest.assertEquals((String)"(<a href=\"http://www.amazon.de/exec/obidos/ASIN/B00005YVUD\">Amazon.de</a>)", (String)result);
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


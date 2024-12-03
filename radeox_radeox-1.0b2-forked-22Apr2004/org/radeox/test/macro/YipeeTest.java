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

public class YipeeTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$YipeeTest;

    public YipeeTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$YipeeTest == null ? (class$org$radeox$test$macro$YipeeTest = YipeeTest.class$("org.radeox.test.macro.YipeeTest")) : class$org$radeox$test$macro$YipeeTest);
    }

    public void testYipee() {
        String result = EngineManager.getInstance().render("{yipee}", this.context);
        YipeeTest.assertEquals((String)"Yipee ay ey", (String)result);
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


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

public class IsbnMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$IsbnMacroTest;

    public IsbnMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$IsbnMacroTest == null ? (class$org$radeox$test$macro$IsbnMacroTest = IsbnMacroTest.class$("org.radeox.test.macro.IsbnMacroTest")) : class$org$radeox$test$macro$IsbnMacroTest);
    }

    public void testIsbn() {
        String result = EngineManager.getInstance().render("{isbn:0201615630}", this.context);
        IsbnMacroTest.assertEquals((String)"(<a href=\"http://www.amazon.com/exec/obidos/ASIN/0201615630\">Amazon.com</a>)", (String)result);
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


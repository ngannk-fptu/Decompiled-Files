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

public class XrefMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$XrefMacroTest;

    public XrefMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$XrefMacroTest == null ? (class$org$radeox$test$macro$XrefMacroTest = XrefMacroTest.class$("org.radeox.test.macro.XrefMacroTest")) : class$org$radeox$test$macro$XrefMacroTest);
    }

    public void testLineNumber() {
        String result = EngineManager.getInstance().render("{xref:com.tirsen.nanning.MixinInstance@Nanning|83}", this.context);
        XrefMacroTest.assertEquals((String)"<a href=\"http://nanning.sourceforge.net/xref/com/tirsen/nanning/MixinInstance.html#83\">com.tirsen.nanning.MixinInstance</a>", (String)result);
    }

    public void testNoLineNumber() {
        String result = EngineManager.getInstance().render("{xref:com.tirsen.nanning.MixinInstance@Nanning}", this.context);
        XrefMacroTest.assertEquals((String)"<a href=\"http://nanning.sourceforge.net/xref/com/tirsen/nanning/MixinInstance.html\">com.tirsen.nanning.MixinInstance</a>", (String)result);
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


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

public class ApiMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$ApiMacroTest;

    public ApiMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$ApiMacroTest == null ? (class$org$radeox$test$macro$ApiMacroTest = ApiMacroTest.class$("org.radeox.test.macro.ApiMacroTest")) : class$org$radeox$test$macro$ApiMacroTest);
    }

    public void testApi() {
        String result = EngineManager.getInstance().render("{api:java.lang.object}", this.context);
        ApiMacroTest.assertEquals((String)"<a href=\"http://java.sun.com/j2se/1.4.1/docs/api/java/lang/object.html\">java.lang.object</a>", (String)result);
    }

    public void testRuby() {
        String result = EngineManager.getInstance().render("{api:String@Ruby}", this.context);
        ApiMacroTest.assertEquals((String)"Ruby namespace is used", (String)"<a href=\"http://www.rubycentral.com/book/ref_c_string.html\">String</a>", (String)result);
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


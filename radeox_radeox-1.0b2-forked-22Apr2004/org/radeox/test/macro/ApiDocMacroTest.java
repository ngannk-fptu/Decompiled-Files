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

public class ApiDocMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$ApiDocMacroTest;

    public ApiDocMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$ApiDocMacroTest == null ? (class$org$radeox$test$macro$ApiDocMacroTest = ApiDocMacroTest.class$("org.radeox.test.macro.ApiDocMacroTest")) : class$org$radeox$test$macro$ApiDocMacroTest);
    }

    public void testApi() {
        String result = EngineManager.getInstance().render("{api-docs}", this.context);
        ApiDocMacroTest.assertEquals((String)"ApiDocs are rendered", (String)"<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th>Binding</th><th>BaseUrl</th><th>Converter Name</th></tr><tr class=\"table-odd\"><td>java131</td><td><span class=\"nobr\"><a href=\"http://java.sun.com/j2se/1.3.1/docs/api/\">&#104;ttp://java.sun.com/j2se/1.3.1/docs/api/</a></span></td><td>Java</td></tr><tr class=\"table-even\"><td>java</td><td><span class=\"nobr\"><a href=\"http://java.sun.com/j2se/1.4.1/docs/api/\">&#104;ttp://java.sun.com/j2se/1.4.1/docs/api/</a></span></td><td>Java</td></tr><tr class=\"table-odd\"><td>ruby</td><td><span class=\"nobr\"><a href=\"http://www.rubycentral.com/book/ref_c_\">&#104;ttp://www.rubycentral.com/book/ref_c_</a></span></td><td>Ruby</td></tr><tr class=\"table-even\"><td>radeox</td><td><span class=\"nobr\"><a href=\"http://snipsnap.org/docs/api/\">&#104;ttp://snipsnap.org/docs/api/</a></span></td><td>Java</td></tr><tr class=\"table-odd\"><td>nanning</td><td><span class=\"nobr\"><a href=\"http://nanning.sourceforge.net/apidocs/\">&#104;ttp://nanning.sourceforge.net/apidocs/</a></span></td><td>Java</td></tr><tr class=\"table-even\"><td>java12</td><td><span class=\"nobr\"><a href=\"http://java.sun.com/j2se/1.2/docs/api/\">&#104;ttp://java.sun.com/j2se/1.2/docs/api/</a></span></td><td>Java</td></tr><tr class=\"table-odd\"><td>j2ee</td><td><span class=\"nobr\"><a href=\"http://java.sun.com/j2ee/sdk_1.3/techdocs/api/\">&#104;ttp://java.sun.com/j2ee/sdk_1.3/techdocs/api/</a></span></td><td>Java</td></tr></table>", (String)result);
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


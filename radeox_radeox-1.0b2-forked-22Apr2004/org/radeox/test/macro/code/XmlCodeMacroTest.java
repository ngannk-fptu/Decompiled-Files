/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro.code;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.EngineManager;
import org.radeox.test.macro.MacroTestSupport;

public class XmlCodeMacroTest
extends MacroTestSupport {
    final String S_CODE = "<div class=\"code\"><pre>";
    final String E_CODE = "</pre></div>";
    final String S_XML_TAG = "<span class=\"xml&#45;tag\">&#60;";
    final String E_XML_TAG = "&#62;</span>";
    final String S_XML_KEYWORD = "<span class=\"xml&#45;keyword\">";
    final String E_XML_KEYWORD = "</span>";
    final String S_XML_QUOTE = "<span class=\"xml&#45;quote\">\"";
    final String E_XML_QUOTE = "\"</span>";
    static /* synthetic */ Class class$org$radeox$test$macro$code$XmlCodeMacroTest;

    public XmlCodeMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$code$XmlCodeMacroTest == null ? (class$org$radeox$test$macro$code$XmlCodeMacroTest = XmlCodeMacroTest.class$("org.radeox.test.macro.code.XmlCodeMacroTest")) : class$org$radeox$test$macro$code$XmlCodeMacroTest);
    }

    public void testXmlCodeXmlElement() {
        String result = EngineManager.getInstance().render("{code:xml}<xml a=\"attr\"><node>text</node></xml>{code}", this.context);
        XmlCodeMacroTest.assertEquals((String)"<div class=\"code\"><pre><span class=\"xml&#45;tag\">&#60;xml a=<span class=\"xml&#45;quote\">\"attr\"</span>&#62;</span><span class=\"xml&#45;tag\">&#60;node&#62;</span>text<span class=\"xml&#45;tag\">&#60;/node&#62;</span><span class=\"xml&#45;tag\">&#60;/xml&#62;</span></pre></div>", (String)result);
    }

    public void testXmlCodeXsl() {
        String sInput = "{code:xml}<xsl:anytag/>{code}";
        String sExpected = "<div class=\"code\"><pre><span class=\"xml&#45;tag\">&#60;<span class=\"xml&#45;keyword\">xsl:anytag</span>/&#62;</span></pre></div>";
        String sResult = EngineManager.getInstance().render(sInput, this.context);
        XmlCodeMacroTest.assertEquals((String)sExpected, (String)sResult);
    }

    public void testXmlCodeXslWithAttr() {
        String sInput = "{code:xml}<xsl:anytag attr=\"1\"/>{code}";
        String sExpected = "<div class=\"code\"><pre><span class=\"xml&#45;tag\">&#60;<span class=\"xml&#45;keyword\">xsl:anytag</span> attr=<span class=\"xml&#45;quote\">\"1\"</span>/&#62;</span></pre></div>";
        String sResult = EngineManager.getInstance().render(sInput, this.context);
        XmlCodeMacroTest.assertEquals((String)sExpected, (String)sResult);
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


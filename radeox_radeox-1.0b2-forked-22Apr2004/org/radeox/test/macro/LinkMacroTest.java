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
import org.radeox.api.engine.RenderEngine;
import org.radeox.test.macro.MacroTestSupport;

public class LinkMacroTest
extends MacroTestSupport {
    public RenderEngine engine = EngineManager.getInstance();
    static /* synthetic */ Class class$org$radeox$test$macro$LinkMacroTest;

    public LinkMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$LinkMacroTest == null ? (class$org$radeox$test$macro$LinkMacroTest = LinkMacroTest.class$("org.radeox.test.macro.LinkMacroTest")) : class$org$radeox$test$macro$LinkMacroTest);
    }

    public void testSimpleLink() {
        String result = this.engine.render("{link:TEST|http://foo.com/}", this.context);
        LinkMacroTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://foo.com/\">TEST</a></span>", (String)result);
    }

    public void testSimpleLinkWithoutName() {
        String result = this.engine.render("{link:http://foo.com/}", this.context);
        LinkMacroTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://foo.com/\">&#104;ttp://foo.com/</a></span>", (String)result);
    }

    public void testCorrectEndWithSpace() {
        String result = this.engine.render("{link:TEST|http://foo.com/} ", this.context);
        LinkMacroTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://foo.com/\">TEST</a></span> ", (String)result);
    }

    public void testCorrectEndWithComma() {
        String result = this.engine.render("{link:TEST|http://foo.com/},", this.context);
        LinkMacroTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://foo.com/\">TEST</a></span>,", (String)result);
    }

    public void testCorrectEndWithSpaceAndComma() {
        String result = this.engine.render("{link:TEST|http://foo.com/} ,", this.context);
        LinkMacroTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://foo.com/\">TEST</a></span> ,", (String)result);
    }

    public void testSimpleLinkWithoutNameAndComma() {
        String result = this.engine.render("{link:http://foo.com/},", this.context);
        LinkMacroTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://foo.com/\">&#104;ttp://foo.com/</a></span>,", (String)result);
    }

    public void testLinkWithAmpersand() {
        String result = this.engine.render("{link:test|http://foo.com/foo.cgi?test=aaa&test1=bbb},", this.context);
        LinkMacroTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://foo.com/foo.cgi?test=aaa&#38;test1=bbb\">test</a></span>,", (String)result);
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


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test;

import java.io.IOException;
import java.io.StringWriter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.EngineManager;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.BaseRenderEngine;
import org.radeox.engine.context.BaseRenderContext;
import org.radeox.test.filter.mock.MockWikiRenderEngine;

public class BaseRenderEngineTest
extends TestCase {
    RenderContext context;
    static /* synthetic */ Class class$org$radeox$test$BaseRenderEngineTest;

    public BaseRenderEngineTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.context = new BaseRenderContext();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$BaseRenderEngineTest == null ? (class$org$radeox$test$BaseRenderEngineTest = BaseRenderEngineTest.class$("org.radeox.test.BaseRenderEngineTest")) : class$org$radeox$test$BaseRenderEngineTest);
    }

    public void testBoldInList() {
        RenderEngine engine = EngineManager.getInstance();
        BaseRenderEngineTest.assertEquals((String)"<ul class=\"minus\">\n<li><b class=\"bold\">test</b></li>\n</ul>", (String)engine.render("- __test__", this.context));
    }

    public void testRenderEngine() {
        String result = EngineManager.getInstance().render("__SnipSnap__ {link:Radeox|http://radeox.org}", this.context);
        BaseRenderEngineTest.assertEquals((String)"<b class=\"bold\">SnipSnap</b> <span class=\"nobr\"><a href=\"http://radeox.org\">Radeox</a></span>", (String)result);
    }

    public void testEmpty() {
        String result = EngineManager.getInstance().render("", this.context);
        BaseRenderEngineTest.assertEquals((String)"", (String)result);
    }

    public void testDefaultEngine() {
        RenderEngine engine = EngineManager.getInstance();
        RenderEngine engineDefault = EngineManager.getInstance("radeox");
        BaseRenderEngineTest.assertEquals((String)engine.getName(), (String)engineDefault.getName());
    }

    public void testWriter() {
        BaseRenderEngine engine = new BaseRenderEngine();
        StringWriter writer = new StringWriter();
        try {
            engine.render(writer, "__SnipSnap__", this.context);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        BaseRenderEngineTest.assertEquals((String)"BaseRenderEngine writes to Writer", (String)"<b class=\"bold\">SnipSnap</b>", (String)writer.toString());
    }

    public void testFilterOrder() {
        RenderEngine engine = EngineManager.getInstance();
        this.context.setRenderEngine(new MockWikiRenderEngine());
        BaseRenderEngineTest.assertEquals((String)"'<link>' - '&#60;link&#62;'", (String)engine.render("[<link>]", this.context));
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


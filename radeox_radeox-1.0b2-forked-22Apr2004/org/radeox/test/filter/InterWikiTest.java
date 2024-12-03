/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.filter;

import java.io.IOException;
import java.io.StringWriter;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.filter.LinkTestFilter;
import org.radeox.filter.interwiki.InterWiki;
import org.radeox.test.filter.FilterTestSupport;
import org.radeox.test.filter.mock.MockInterWikiRenderEngine;

public class InterWikiTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$InterWikiTest;

    public InterWikiTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new LinkTestFilter();
        this.context.getRenderContext().setRenderEngine(new MockInterWikiRenderEngine());
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$InterWikiTest == null ? (class$org$radeox$test$filter$InterWikiTest = InterWikiTest.class$("org.radeox.test.filter.InterWikiTest")) : class$org$radeox$test$filter$InterWikiTest);
    }

    public void testAnchorInterWiki() {
        InterWikiTest.assertEquals((String)"<a href=\"http://www.c2.com/cgi/wiki?foo#anchor\">foo@C2</a>", (String)this.filter.filter("[foo@C2#anchor]", this.context));
    }

    public void testInterWiki() {
        InterWikiTest.assertEquals((String)"<a href=\"http://snipsnap.org/space/stephan\">stephan@SnipSnap</a>", (String)this.filter.filter("[stephan@SnipSnap]", this.context));
    }

    public void testGoogle() {
        InterWikiTest.assertEquals((String)"<a href=\"http://www.google.com/search?q=stephan\">stephan@Google</a>", (String)this.filter.filter("[stephan@Google]", this.context));
    }

    public void testInterWikiAlias() {
        InterWikiTest.assertEquals((String)"<a href=\"http://snipsnap.org/space/AliasStephan\">Alias</a>", (String)this.filter.filter("[Alias|AliasStephan@SnipSnap]", this.context));
    }

    public void testInterWikiExpander() {
        InterWiki interWiki = InterWiki.getInstance();
        StringWriter writer = new StringWriter();
        try {
            interWiki.expand(writer, "Google", "stephan", "StephanAlias");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        InterWikiTest.assertEquals((String)"<a href=\"http://www.google.com/search?q=stephan\">StephanAlias</a>", (String)writer.toString());
    }

    public void testCacheable() {
        RenderContext renderContext = this.context.getRenderContext();
        renderContext.setCacheable(false);
        this.filter.filter("[stephan@SnipSnap]", this.context);
        InterWikiTest.assertTrue((String)"InterWiki is cacheable", (boolean)renderContext.isCacheable());
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


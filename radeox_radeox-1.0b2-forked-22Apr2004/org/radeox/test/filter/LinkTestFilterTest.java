/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.filter;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.filter.LinkTestFilter;
import org.radeox.test.filter.FilterTestSupport;
import org.radeox.test.filter.mock.MockWikiRenderEngine;

public class LinkTestFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$LinkTestFilterTest;

    public LinkTestFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new LinkTestFilter();
        this.context.getRenderContext().setRenderEngine(new MockWikiRenderEngine());
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$LinkTestFilterTest == null ? (class$org$radeox$test$filter$LinkTestFilterTest = LinkTestFilterTest.class$("org.radeox.test.filter.LinkTestFilterTest")) : class$org$radeox$test$filter$LinkTestFilterTest);
    }

    public void testUrlInLink() {
        LinkTestFilterTest.assertEquals((String)"Url is reported", (String)"<div class=\"error\">Do not surround URLs with [...].</div>", (String)this.filter.filter("[http://radeox.org]", this.context));
    }

    public void testCreate() {
        LinkTestFilterTest.assertEquals((String)"'Roller' - 'Roller'", (String)this.filter.filter("[Roller]", this.context));
    }

    public void testLink() {
        LinkTestFilterTest.assertEquals((String)"link:SnipSnap|SnipSnap", (String)this.filter.filter("[SnipSnap]", this.context));
    }

    public void testLinkLower() {
        LinkTestFilterTest.assertEquals((String)"link:stephan|stephan", (String)this.filter.filter("[stephan]", this.context));
    }

    public void testLinkAlias() {
        LinkTestFilterTest.assertEquals((String)"link:stephan|alias", (String)this.filter.filter("[alias|stephan]", this.context));
    }

    public void testLinkAliasAnchor() {
        LinkTestFilterTest.assertEquals((String)"link:stephan|alias#hash", (String)this.filter.filter("[alias|stephan#hash]", this.context));
    }

    public void testLinkAliasAnchorType() {
        LinkTestFilterTest.assertEquals((String)"link:stephan|alias#hash", (String)this.filter.filter("[alias|type:stephan#hash]", this.context));
    }

    public void testLinkCacheable() {
        RenderContext renderContext = this.context.getRenderContext();
        renderContext.setCacheable(false);
        this.filter.filter("[SnipSnap]", this.context);
        renderContext.commitCache();
        LinkTestFilterTest.assertTrue((String)"Normal link is cacheable", (boolean)renderContext.isCacheable());
    }

    public void testCreateLinkNotCacheable() {
        RenderContext renderContext = this.context.getRenderContext();
        renderContext.setCacheable(false);
        this.filter.filter("[Roller]", this.context);
        renderContext.commitCache();
        LinkTestFilterTest.assertTrue((String)"Non existing link is not cacheable", (!renderContext.isCacheable() ? 1 : 0) != 0);
    }

    public void testLinksWithEscapedChars() {
        LinkTestFilterTest.assertEquals((String)"'<link>' - '&#60;link&#62;'", (String)this.filter.filter("[<link>]", this.context));
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


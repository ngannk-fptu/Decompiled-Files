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
import org.radeox.filter.WikiLinkFilter;
import org.radeox.test.filter.FilterTestSupport;
import org.radeox.test.filter.mock.MockOldWikiRenderEngine;

public class WikiLinkFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$WikiLinkFilterTest;

    public WikiLinkFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new WikiLinkFilter();
        this.context.getRenderContext().setRenderEngine(new MockOldWikiRenderEngine());
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$WikiLinkFilterTest == null ? (class$org$radeox$test$filter$WikiLinkFilterTest = WikiLinkFilterTest.class$("org.radeox.test.filter.WikiLinkFilterTest")) : class$org$radeox$test$filter$WikiLinkFilterTest);
    }

    public void testWikiCreate() {
        WikiLinkFilterTest.assertEquals((String)"WebRoller?", (String)this.filter.filter("WebRoller", this.context));
    }

    public void testWikiLink() {
        WikiLinkFilterTest.assertEquals((String)"link:SnipSnap", (String)this.filter.filter("SnipSnap", this.context));
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


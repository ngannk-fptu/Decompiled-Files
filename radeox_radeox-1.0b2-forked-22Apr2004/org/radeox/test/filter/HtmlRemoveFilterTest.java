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
import org.radeox.filter.HtmlRemoveFilter;
import org.radeox.test.filter.FilterTestSupport;

public class HtmlRemoveFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$HtmlRemoveFilterTest;

    public HtmlRemoveFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new HtmlRemoveFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$HtmlRemoveFilterTest == null ? (class$org$radeox$test$filter$HtmlRemoveFilterTest = HtmlRemoveFilterTest.class$("org.radeox.test.filter.HtmlRemoveFilterTest")) : class$org$radeox$test$filter$HtmlRemoveFilterTest);
    }

    public void testHtmlRemove() {
        HtmlRemoveFilterTest.assertEquals((String)"Test", (String)this.filter.filter("<tag attr=\"Text\">Test</tag>", this.context));
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


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
import org.radeox.filter.HeadingFilter;
import org.radeox.test.filter.FilterTestSupport;

public class HeadingFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$HeadingFilterTest;

    public HeadingFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new HeadingFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$HeadingFilterTest == null ? (class$org$radeox$test$filter$HeadingFilterTest = HeadingFilterTest.class$("org.radeox.test.filter.HeadingFilterTest")) : class$org$radeox$test$filter$HeadingFilterTest);
    }

    public void testHeading() {
        HeadingFilterTest.assertEquals((String)"<h3 class=\"heading-1\">Test</h3>", (String)this.filter.filter("1 Test", this.context));
    }

    public void testSubHeadings() {
        HeadingFilterTest.assertEquals((String)"<h3 class=\"heading-1\">Test</h3>\n<h3 class=\"heading-1-1\">Test</h3>\n<h3 class=\"heading-1-1-1\">Test</h3>\n<h3 class=\"heading-1\">Test</h3>", (String)this.filter.filter("1 Test\n1.1 Test\n1.1.1 Test\n1 Test", this.context));
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


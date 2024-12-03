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
import org.radeox.filter.LineFilter;
import org.radeox.test.filter.FilterTestSupport;

public class LineFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$LineFilterTest;

    public LineFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new LineFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$LineFilterTest == null ? (class$org$radeox$test$filter$LineFilterTest = LineFilterTest.class$("org.radeox.test.filter.LineFilterTest")) : class$org$radeox$test$filter$LineFilterTest);
    }

    public void test3Line() {
        LineFilterTest.assertEquals((String)"Test---Text", (String)this.filter.filter("Test---Text", this.context));
    }

    public void test4Line() {
        LineFilterTest.assertEquals((String)"Test<hr class=\"line\"/>Text", (String)this.filter.filter("Test----Text", this.context));
    }

    public void test5Line() {
        LineFilterTest.assertEquals((String)"Test<hr class=\"line\"/>Text", (String)this.filter.filter("Test-----Text", this.context));
    }

    public void testSimpleLine() {
        LineFilterTest.assertEquals((String)"<hr class=\"line\"/>\n", (String)this.filter.filter("-----\n", this.context));
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


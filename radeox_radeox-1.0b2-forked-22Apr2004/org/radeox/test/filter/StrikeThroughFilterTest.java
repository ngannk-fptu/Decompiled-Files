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
import org.radeox.filter.StrikeThroughFilter;
import org.radeox.test.filter.FilterTestSupport;

public class StrikeThroughFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$StrikeThroughFilterTest;

    public StrikeThroughFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new StrikeThroughFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$StrikeThroughFilterTest == null ? (class$org$radeox$test$filter$StrikeThroughFilterTest = StrikeThroughFilterTest.class$("org.radeox.test.filter.StrikeThroughFilterTest")) : class$org$radeox$test$filter$StrikeThroughFilterTest);
    }

    public void testStrikeThroughDash() {
        StrikeThroughFilterTest.assertEquals((String)"Test<strike class=\"strike\">Test-Text</strike>", (String)this.filter.filter("Test--Test-Text--", this.context));
    }

    public void testStrikeThroughDoubleDash() {
        StrikeThroughFilterTest.assertEquals((String)"Test<strike class=\"strike\">Test</strike>Text--", (String)this.filter.filter("Test--Test--Text--", this.context));
    }

    public void testStartStrikeThrough() {
        StrikeThroughFilterTest.assertEquals((String)"Test<strike class=\"strike\">Text</strike>", (String)this.filter.filter("Test--Text--", this.context));
    }

    public void testEndStrikeThrough() {
        StrikeThroughFilterTest.assertEquals((String)"<strike class=\"strike\">Text</strike>Test", (String)this.filter.filter("--Text--Test", this.context));
    }

    public void testStrikeThrough() {
        StrikeThroughFilterTest.assertEquals((String)"Test<strike class=\"strike\">Text</strike>Test", (String)this.filter.filter("Test--Text--Test", this.context));
    }

    public void testFourDashes() {
        StrikeThroughFilterTest.assertEquals((String)"----", (String)this.filter.filter("----", this.context));
    }

    public void testFiveDashes() {
        StrikeThroughFilterTest.assertEquals((String)"-----", (String)this.filter.filter("-----", this.context));
    }

    public void testHtmlComment() {
        StrikeThroughFilterTest.assertEquals((String)"<!-- comment -->", (String)this.filter.filter("<!-- comment -->", this.context));
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


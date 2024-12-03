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
import org.radeox.filter.TypographyFilter;
import org.radeox.test.filter.FilterTestSupport;

public class TypographyFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$FilterTestSupport;

    public TypographyFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new TypographyFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$FilterTestSupport == null ? (class$org$radeox$test$filter$FilterTestSupport = TypographyFilterTest.class$("org.radeox.test.filter.FilterTestSupport")) : class$org$radeox$test$filter$FilterTestSupport);
    }

    public void testElipsis() {
        TypographyFilterTest.assertEquals((String)"Test &#8230; Text", (String)this.filter.filter("Test ... Text", this.context));
    }

    public void testNotAfter() {
        TypographyFilterTest.assertEquals((String)"...Text", (String)this.filter.filter("...Text", this.context));
    }

    public void testEndOfLine() {
        TypographyFilterTest.assertEquals((String)"Text&#8230;", (String)this.filter.filter("Text...", this.context));
    }

    public void test4Dots() {
        TypographyFilterTest.assertEquals((String)"Test .... Text", (String)this.filter.filter("Test .... Text", this.context));
    }

    public void testLineStart() {
        TypographyFilterTest.assertEquals((String)"&#8230; Text", (String)this.filter.filter("... Text", this.context));
    }

    public void testLineEnd() {
        TypographyFilterTest.assertEquals((String)"Test &#8230;", (String)this.filter.filter("Test ...", this.context));
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


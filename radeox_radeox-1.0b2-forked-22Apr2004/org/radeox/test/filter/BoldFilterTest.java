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
import org.radeox.filter.BoldFilter;
import org.radeox.test.filter.FilterTestSupport;

public class BoldFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$BoldFilterTest;

    public BoldFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new BoldFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$BoldFilterTest == null ? (class$org$radeox$test$filter$BoldFilterTest = BoldFilterTest.class$("org.radeox.test.filter.BoldFilterTest")) : class$org$radeox$test$filter$BoldFilterTest);
    }

    public void testBold() {
        BoldFilterTest.assertEquals((String)"<b class=\"bold\">Text</b>", (String)this.filter.filter("__Text__", this.context));
    }

    public void testBoldMustStartAndEndWithSpace() {
        BoldFilterTest.assertEquals((String)"Test__Text__Test", (String)this.filter.filter("Test__Text__Test", this.context));
    }

    public void testBoldWithPunctuation() {
        BoldFilterTest.assertEquals((String)"<b class=\"bold\">Text</b>:", (String)this.filter.filter("__Text__:", this.context));
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


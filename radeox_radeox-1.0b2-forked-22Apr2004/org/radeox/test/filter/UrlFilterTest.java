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
import org.radeox.filter.UrlFilter;
import org.radeox.test.filter.FilterTestSupport;

public class UrlFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$UrlFilterTest;

    public UrlFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new UrlFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$UrlFilterTest == null ? (class$org$radeox$test$filter$UrlFilterTest = UrlFilterTest.class$("org.radeox.test.filter.UrlFilterTest")) : class$org$radeox$test$filter$UrlFilterTest);
    }

    public void testHttp() {
        UrlFilterTest.assertEquals((String)"<span class=\"nobr\"><a href=\"http://radeox.org\">&#104;ttp://radeox.org</a></span>", (String)this.filter.filter("http://radeox.org", this.context));
    }

    public void testHttps() {
        UrlFilterTest.assertEquals((String)"<span class=\"nobr\"><a href=\"https://radeox.org\">&#104;ttps://radeox.org</a></span>", (String)this.filter.filter("https://radeox.org", this.context));
    }

    public void testFtp() {
        UrlFilterTest.assertEquals((String)"<span class=\"nobr\"><a href=\"ftp://radeox.org\">&#102;tp://radeox.org</a></span>", (String)this.filter.filter("ftp://radeox.org", this.context));
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


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
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.EscapeFilter;
import org.radeox.filter.Filter;
import org.radeox.filter.FilterPipe;
import org.radeox.filter.context.FilterContext;
import org.radeox.test.filter.FilterTestSupport;
import org.radeox.util.Encoder;

public class EscapeFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$EscapeFilterTest;

    public EscapeFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new EscapeFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$EscapeFilterTest == null ? (class$org$radeox$test$filter$EscapeFilterTest = EscapeFilterTest.class$("org.radeox.test.filter.EscapeFilterTest")) : class$org$radeox$test$filter$EscapeFilterTest);
    }

    public void testEscapeH() {
        EscapeFilterTest.assertEquals((String)"h is escaped", (String)"&#104;", (String)this.filter.filter("\\h", this.context));
    }

    public void testBackslash() {
        EscapeFilterTest.assertEquals((String)"\\\\ is kept escaped", (String)"\\\\", (String)this.filter.filter("\\\\", this.context));
    }

    public void testBeforeEscape() {
        FilterPipe fp = new FilterPipe();
        Filter f = new Filter(){

            public String[] replaces() {
                return new String[0];
            }

            public void setInitialContext(InitialRenderContext context) {
            }

            public String[] before() {
                return FilterPipe.EMPTY_BEFORE;
            }

            public String filter(String input, FilterContext context) {
                return null;
            }

            public String getDescription() {
                return "";
            }
        };
        fp.addFilter(f);
        fp.addFilter(this.filter);
        EscapeFilterTest.assertEquals((String)"EscapeFilter is first", (Object)fp.getFilter(0), (Object)this.filter);
    }

    public void testHTMLEncoderEscape() {
        EscapeFilterTest.assertEquals((String)"&#60;link&#62;", (String)Encoder.escape("<link>"));
    }

    public void testHTMLEncoderUnescape() {
        EscapeFilterTest.assertEquals((String)"<link>", (String)Encoder.unescape("&#60;link&#62;"));
    }

    public void testAmpersandEscape() {
        EscapeFilterTest.assertEquals((String)"&#38;", (String)this.filter.filter("&", this.context));
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


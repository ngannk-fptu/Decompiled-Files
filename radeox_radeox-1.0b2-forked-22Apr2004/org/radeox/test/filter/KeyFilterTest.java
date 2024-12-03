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
import org.radeox.filter.KeyFilter;
import org.radeox.test.filter.FilterTestSupport;

public class KeyFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$KeyFilterTest;

    public KeyFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new KeyFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$KeyFilterTest == null ? (class$org$radeox$test$filter$KeyFilterTest = KeyFilterTest.class$("org.radeox.test.filter.KeyFilterTest")) : class$org$radeox$test$filter$KeyFilterTest);
    }

    public void testAltKey() {
        KeyFilterTest.assertEquals((String)"<span class=\"key\">Alt-1</span>", (String)this.filter.filter("Alt-1", this.context));
    }

    public void testCtrlKey() {
        KeyFilterTest.assertEquals((String)"<span class=\"key\">Ctrl-1</span>", (String)this.filter.filter("Ctrl-1", this.context));
    }

    public void testShiftKey() {
        KeyFilterTest.assertEquals((String)"<span class=\"key\">Shift-1</span>", (String)this.filter.filter("Shift-1", this.context));
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


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
import org.radeox.filter.ItalicFilter;
import org.radeox.test.filter.FilterTestSupport;

public class ItalicFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$FilterTestSupport;

    public ItalicFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new ItalicFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$FilterTestSupport == null ? (class$org$radeox$test$filter$FilterTestSupport = ItalicFilterTest.class$("org.radeox.test.filter.FilterTestSupport")) : class$org$radeox$test$filter$FilterTestSupport);
    }

    public void testItalic() {
        ItalicFilterTest.assertEquals((String)"<i class=\"italic\">Text</i>", (String)this.filter.filter("~~Text~~", this.context));
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


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
import org.radeox.filter.NewlineFilter;
import org.radeox.test.filter.FilterTestSupport;

public class NewlineFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$NewlineFilterTest;

    public NewlineFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new NewlineFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$NewlineFilterTest == null ? (class$org$radeox$test$filter$NewlineFilterTest = NewlineFilterTest.class$("org.radeox.test.filter.NewlineFilterTest")) : class$org$radeox$test$filter$NewlineFilterTest);
    }

    public void testNewline() {
        NewlineFilterTest.assertEquals((String)"Test<br/>Text", (String)this.filter.filter("Test\\\\Text", this.context));
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


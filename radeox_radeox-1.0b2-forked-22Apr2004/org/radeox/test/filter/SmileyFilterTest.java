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
import org.radeox.filter.SmileyFilter;
import org.radeox.test.filter.FilterTestSupport;

public class SmileyFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$SmileyFilterTest;

    public SmileyFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new SmileyFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$SmileyFilterTest == null ? (class$org$radeox$test$filter$SmileyFilterTest = SmileyFilterTest.class$("org.radeox.test.filter.SmileyFilterTest")) : class$org$radeox$test$filter$SmileyFilterTest);
    }

    public void testSmiley() {
        SmileyFilterTest.assertEquals((String)":-)", (String)this.filter.filter(":-(", this.context));
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


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.filter;

import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.filter.ParamFilter;
import org.radeox.test.filter.FilterTestSupport;

public class ParamFilterTest
extends FilterTestSupport {
    static /* synthetic */ Class class$org$radeox$test$filter$ParamFilterTest;

    public ParamFilterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.filter = new ParamFilter();
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$ParamFilterTest == null ? (class$org$radeox$test$filter$ParamFilterTest = ParamFilterTest.class$("org.radeox.test.filter.ParamFilterTest")) : class$org$radeox$test$filter$ParamFilterTest);
    }

    public void testParam() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("var1", "test");
        this.context.getRenderContext().setParameters(params);
        ParamFilterTest.assertEquals((String)"test", (String)this.filter.filter("{$var1}", this.context));
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


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.filter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.Filter;
import org.radeox.filter.FilterPipe;
import org.radeox.filter.context.FilterContext;
import org.radeox.test.filter.mock.MockReplacedFilter;
import org.radeox.test.filter.mock.MockReplacesFilter;

public class FilterPipeTest
extends TestCase {
    static /* synthetic */ Class class$org$radeox$test$filter$FilterPipeTest;

    public FilterPipeTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$filter$FilterPipeTest == null ? (class$org$radeox$test$filter$FilterPipeTest = FilterPipeTest.class$("org.radeox.test.filter.FilterPipeTest")) : class$org$radeox$test$filter$FilterPipeTest);
    }

    public void testBefore() {
        FilterPipe fp = new FilterPipe();
        Filter f1 = new Filter(){

            public String[] before() {
                return FilterPipe.EMPTY_BEFORE;
            }

            public void setInitialContext(InitialRenderContext context) {
            }

            public String[] replaces() {
                return new String[0];
            }

            public String filter(String input, FilterContext context) {
                return null;
            }

            public String getDescription() {
                return "";
            }
        };
        Filter f2 = new Filter(){

            public String[] before() {
                return FilterPipe.FIRST_BEFORE;
            }

            public String[] replaces() {
                return new String[0];
            }

            public void setInitialContext(InitialRenderContext context) {
            }

            public String filter(String input, FilterContext context) {
                return null;
            }

            public String getDescription() {
                return "";
            }
        };
        fp.addFilter(f1);
        fp.addFilter(f2);
        FilterPipeTest.assertEquals((String)"'FIRST_BEFORE Filter is first in FilterPipe", (Object)fp.getFilter(0), (Object)f2);
    }

    public void testReplace() {
        FilterPipe fp = new FilterPipe();
        MockReplacedFilter f1 = new MockReplacedFilter();
        MockReplacesFilter f2 = new MockReplacesFilter();
        fp.addFilter(f1);
        fp.addFilter(f2);
        fp.init();
        FilterPipeTest.assertTrue((String)"MockReplacedFilter is removed from FilterPipe", (-1 == fp.index("org.radeox.test.filter.mock.MockReplacedFilter") ? 1 : 0) != 0);
        FilterPipeTest.assertTrue((String)"MockReplacesFilter is not removed from FilterPipe", (-1 != fp.index("org.radeox.test.filter.mock.MockReplacesFilter") ? 1 : 0) != 0);
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


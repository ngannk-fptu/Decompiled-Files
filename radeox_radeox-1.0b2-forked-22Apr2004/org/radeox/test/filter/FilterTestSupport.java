/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.TestCase
 */
package org.radeox.test.filter;

import junit.framework.TestCase;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.engine.context.BaseRenderContext;
import org.radeox.filter.Filter;
import org.radeox.filter.context.BaseFilterContext;
import org.radeox.filter.context.FilterContext;

public class FilterTestSupport
extends TestCase {
    protected Filter filter;
    protected FilterContext context = new BaseFilterContext();

    public FilterTestSupport(String s) {
        super(s);
        this.context.setRenderContext(new BaseRenderContext());
    }

    protected void setUp() throws Exception {
        super.setUp();
        if (null != this.filter) {
            this.filter.setInitialContext(new BaseInitialRenderContext());
        }
    }
}


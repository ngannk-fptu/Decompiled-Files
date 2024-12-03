/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.TestCase
 */
package org.radeox.test.macro;

import junit.framework.TestCase;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseRenderContext;

public class MacroTestSupport
extends TestCase {
    protected RenderContext context;

    public MacroTestSupport(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        this.context = new BaseRenderContext();
        super.setUp();
    }
}


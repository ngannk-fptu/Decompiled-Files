/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.TestCase
 */
package org.radeox.test;

import junit.framework.TestCase;
import org.radeox.EngineManager;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseRenderContext;

public class RenderEnginePerformanceTest
extends TestCase {
    private RenderContext context = new BaseRenderContext();
    private String wikiMarkup;
    private RenderEngine engine;

    public RenderEnginePerformanceTest(String text) {
        super("testPerformance");
        this.wikiMarkup = text;
        this.engine = EngineManager.getInstance();
    }

    public void testPerformance() {
        System.err.println("Test Size: " + this.wikiMarkup.length());
        this.engine.render(this.wikiMarkup, this.context);
    }
}


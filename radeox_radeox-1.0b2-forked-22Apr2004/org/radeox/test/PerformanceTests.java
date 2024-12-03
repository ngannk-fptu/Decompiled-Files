/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.clarkware.junitperf.TimedTest
 *  junit.framework.Test
 *  junit.framework.TestSuite
 *  junit.textui.TestRunner
 */
package org.radeox.test;

import com.clarkware.junitperf.TimedTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.radeox.EngineManager;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseRenderContext;
import org.radeox.test.RenderEnginePerformanceTest;
import org.radeox.util.logging.Logger;
import org.radeox.util.logging.NullLogger;

public class PerformanceTests {
    public static void main(String[] args) throws IOException {
        TestRunner.run((Test)PerformanceTests.suite());
    }

    public static Test suite() throws IOException {
        String tmp;
        File wikiTxt = new File("wiki.txt");
        BufferedReader reader = new BufferedReader(new FileReader(wikiTxt.getCanonicalFile()));
        StringBuffer input = new StringBuffer();
        while ((tmp = reader.readLine()) != null) {
            input.append(tmp);
        }
        Logger.setHandler(new NullLogger());
        System.err.println(EngineManager.getInstance().render("__initialized__", (RenderContext)new BaseRenderContext()));
        TestSuite s = new TestSuite();
        long maxElapsedTime = 30000L;
        StringBuffer testString = new StringBuffer();
        for (int i = 0; i < 10; ++i) {
            testString.append(input);
            RenderEnginePerformanceTest renderEngineTest = new RenderEnginePerformanceTest(testString.toString());
            TimedTest timedTest = new TimedTest((Test)renderEngineTest, maxElapsedTime, false);
            s.addTest((Test)timedTest);
        }
        return s;
    }
}


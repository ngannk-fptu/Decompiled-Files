/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.test.filter.AllFilterTests;
import org.radeox.test.macro.AllMacroTests;
import org.radeox.test.macro.list.AllListTests;
import org.radeox.test.regex.AllRegexTests;
import radeox.test.groovy.AllGroovyTests;

public class AllTests
extends TestCase {
    static /* synthetic */ Class class$org$radeox$test$BaseRenderEngineTest;

    public AllTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTest(AllFilterTests.suite());
        s.addTest(AllMacroTests.suite());
        s.addTest(AllListTests.suite());
        s.addTest(AllGroovyTests.suite());
        s.addTest(AllRegexTests.suite());
        s.addTestSuite(class$org$radeox$test$BaseRenderEngineTest == null ? (class$org$radeox$test$BaseRenderEngineTest = AllTests.class$("org.radeox.test.BaseRenderEngineTest")) : class$org$radeox$test$BaseRenderEngineTest);
        return s;
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


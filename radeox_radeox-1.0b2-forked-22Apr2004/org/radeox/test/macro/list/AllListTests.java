/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro.list;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllListTests
extends TestCase {
    static /* synthetic */ Class class$org$radeox$test$macro$list$SimpleListTest;
    static /* synthetic */ Class class$org$radeox$test$macro$list$ExampleListFormatterTest;
    static /* synthetic */ Class class$org$radeox$test$macro$list$AtoZListFormatterTest;

    public AllListTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTestSuite(class$org$radeox$test$macro$list$SimpleListTest == null ? (class$org$radeox$test$macro$list$SimpleListTest = AllListTests.class$("org.radeox.test.macro.list.SimpleListTest")) : class$org$radeox$test$macro$list$SimpleListTest);
        s.addTestSuite(class$org$radeox$test$macro$list$ExampleListFormatterTest == null ? (class$org$radeox$test$macro$list$ExampleListFormatterTest = AllListTests.class$("org.radeox.test.macro.list.ExampleListFormatterTest")) : class$org$radeox$test$macro$list$ExampleListFormatterTest);
        s.addTestSuite(class$org$radeox$test$macro$list$AtoZListFormatterTest == null ? (class$org$radeox$test$macro$list$AtoZListFormatterTest = AllListTests.class$("org.radeox.test.macro.list.AtoZListFormatterTest")) : class$org$radeox$test$macro$list$AtoZListFormatterTest);
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


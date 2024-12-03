/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package radeox.test.groovy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllGroovyTests
extends TestCase {
    static /* synthetic */ Class class$radeox$test$groovy$RadeoxTemplateEngineTest;

    public AllGroovyTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTestSuite(class$radeox$test$groovy$RadeoxTemplateEngineTest == null ? (class$radeox$test$groovy$RadeoxTemplateEngineTest = AllGroovyTests.class$("radeox.test.groovy.RadeoxTemplateEngineTest")) : class$radeox$test$groovy$RadeoxTemplateEngineTest);
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


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro.code;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllCodeMacroTests
extends TestCase {
    static /* synthetic */ Class class$org$radeox$test$macro$code$XmlCodeMacroTest;

    public AllCodeMacroTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTestSuite(class$org$radeox$test$macro$code$XmlCodeMacroTest == null ? (class$org$radeox$test$macro$code$XmlCodeMacroTest = AllCodeMacroTests.class$("org.radeox.test.macro.code.XmlCodeMacroTest")) : class$org$radeox$test$macro$code$XmlCodeMacroTest);
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


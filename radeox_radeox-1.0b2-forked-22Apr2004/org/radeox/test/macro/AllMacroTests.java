/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.test.macro.code.AllCodeMacroTests;

public class AllMacroTests
extends TestCase {
    static /* synthetic */ Class class$org$radeox$test$macro$ApiMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$ApiDocMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$AsinMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$FilePathMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$IsbnMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$LinkMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$ParamMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$TableMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$XrefMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$MailToMacroTest;
    static /* synthetic */ Class class$org$radeox$test$macro$RfcMacroTest;

    public AllMacroTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTestSuite(class$org$radeox$test$macro$ApiMacroTest == null ? (class$org$radeox$test$macro$ApiMacroTest = AllMacroTests.class$("org.radeox.test.macro.ApiMacroTest")) : class$org$radeox$test$macro$ApiMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$ApiDocMacroTest == null ? (class$org$radeox$test$macro$ApiDocMacroTest = AllMacroTests.class$("org.radeox.test.macro.ApiDocMacroTest")) : class$org$radeox$test$macro$ApiDocMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$AsinMacroTest == null ? (class$org$radeox$test$macro$AsinMacroTest = AllMacroTests.class$("org.radeox.test.macro.AsinMacroTest")) : class$org$radeox$test$macro$AsinMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$FilePathMacroTest == null ? (class$org$radeox$test$macro$FilePathMacroTest = AllMacroTests.class$("org.radeox.test.macro.FilePathMacroTest")) : class$org$radeox$test$macro$FilePathMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$IsbnMacroTest == null ? (class$org$radeox$test$macro$IsbnMacroTest = AllMacroTests.class$("org.radeox.test.macro.IsbnMacroTest")) : class$org$radeox$test$macro$IsbnMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$LinkMacroTest == null ? (class$org$radeox$test$macro$LinkMacroTest = AllMacroTests.class$("org.radeox.test.macro.LinkMacroTest")) : class$org$radeox$test$macro$LinkMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$ParamMacroTest == null ? (class$org$radeox$test$macro$ParamMacroTest = AllMacroTests.class$("org.radeox.test.macro.ParamMacroTest")) : class$org$radeox$test$macro$ParamMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$TableMacroTest == null ? (class$org$radeox$test$macro$TableMacroTest = AllMacroTests.class$("org.radeox.test.macro.TableMacroTest")) : class$org$radeox$test$macro$TableMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$XrefMacroTest == null ? (class$org$radeox$test$macro$XrefMacroTest = AllMacroTests.class$("org.radeox.test.macro.XrefMacroTest")) : class$org$radeox$test$macro$XrefMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$MailToMacroTest == null ? (class$org$radeox$test$macro$MailToMacroTest = AllMacroTests.class$("org.radeox.test.macro.MailToMacroTest")) : class$org$radeox$test$macro$MailToMacroTest);
        s.addTestSuite(class$org$radeox$test$macro$RfcMacroTest == null ? (class$org$radeox$test$macro$RfcMacroTest = AllMacroTests.class$("org.radeox.test.macro.RfcMacroTest")) : class$org$radeox$test$macro$RfcMacroTest);
        s.addTest(AllCodeMacroTests.suite());
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


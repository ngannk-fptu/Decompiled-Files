/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 */
package org.radeox.test.macro;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.radeox.EngineManager;
import org.radeox.test.macro.MacroTestSupport;

public class FilePathMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$FilePathMacroTest;

    public FilePathMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$FilePathMacroTest == null ? (class$org$radeox$test$macro$FilePathMacroTest = FilePathMacroTest.class$("org.radeox.test.macro.FilePathMacroTest")) : class$org$radeox$test$macro$FilePathMacroTest);
    }

    public void testFile() {
        String result = EngineManager.getInstance().render("{file-path://share}", this.context);
        FilePathMacroTest.assertEquals((String)"&#92;&#92;share", (String)result);
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


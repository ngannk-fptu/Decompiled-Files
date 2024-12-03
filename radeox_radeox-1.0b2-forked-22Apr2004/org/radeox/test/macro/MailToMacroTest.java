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

public class MailToMacroTest
extends MacroTestSupport {
    static /* synthetic */ Class class$org$radeox$test$macro$MailToMacroTest;

    public MailToMacroTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(class$org$radeox$test$macro$MailToMacroTest == null ? (class$org$radeox$test$macro$MailToMacroTest = MailToMacroTest.class$("org.radeox.test.macro.MailToMacroTest")) : class$org$radeox$test$macro$MailToMacroTest);
    }

    public void testMailto() {
        String result = EngineManager.getInstance().render("{mailto:stephan@mud.de}", this.context);
        MailToMacroTest.assertEquals((String)"<a href=\"mailto:stephan@mud.de\">stephan@mud.de</a>", (String)result);
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


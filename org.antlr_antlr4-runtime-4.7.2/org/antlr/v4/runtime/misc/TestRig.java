/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.misc;

import java.lang.reflect.Method;

public class TestRig {
    public static void main(String[] args) {
        try {
            Class<?> testRigClass = Class.forName("org.antlr.v4.gui.TestRig");
            System.err.println("Warning: TestRig moved to org.antlr.v4.gui.TestRig; calling automatically");
            try {
                Method mainMethod = testRigClass.getMethod("main", String[].class);
                mainMethod.invoke(null, new Object[]{args});
            }
            catch (Exception nsme) {
                System.err.println("Problems calling org.antlr.v4.gui.TestRig.main(args)");
            }
        }
        catch (ClassNotFoundException cnfe) {
            System.err.println("Use of TestRig now requires the use of the tool jar, antlr-4.X-complete.jar");
            System.err.println("Maven users need group ID org.antlr and artifact ID antlr4");
        }
    }
}


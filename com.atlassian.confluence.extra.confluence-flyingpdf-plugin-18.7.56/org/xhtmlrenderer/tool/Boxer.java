/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.tool;

import java.io.File;
import org.xhtmlrenderer.test.DocumentDiffTest;

public class Boxer {
    private void run(String filen) {
        try {
            File file = new File(filen);
            if (!file.exists() || file.isDirectory()) {
                throw new IllegalArgumentException(filen + " not a file, or is a directory. Give me a single file name.");
            }
            System.out.println(DocumentDiffTest.xhtmlToDiff(filen, 1024, 768));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException("Give a file name");
            }
            new Boxer().run(args[0]);
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.io.File;
import org.xhtmlrenderer.test.DocumentDiffTest;

public class DocumentDiffGenerate {
    public static void main(String[] args) throws Exception {
        DocumentDiffTest ddt = new DocumentDiffTest();
        if (args.length == 2) {
            DocumentDiffTest.generateTestFile(args[0], args[1], 500, 500);
        } else {
            ddt.generateDiffs(new File("tests/diff"), 500, 500);
        }
    }
}


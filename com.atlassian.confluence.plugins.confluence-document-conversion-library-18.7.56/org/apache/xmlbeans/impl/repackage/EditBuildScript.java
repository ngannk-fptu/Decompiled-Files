/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.repackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class EditBuildScript {
    public static void main(String[] args) throws Exception {
        String tokenStr;
        if (args.length != 3) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }
        args[0] = args[0].replace('/', File.separatorChar);
        File buildFile = new File(args[0]);
        StringBuffer sb = EditBuildScript.readFile(buildFile);
        int i = sb.indexOf(tokenStr = "<property name=\"" + args[1] + "\" value=\"");
        if (i < 0) {
            throw new IllegalArgumentException("Can't find token: " + tokenStr);
        }
        int j = i + tokenStr.length();
        while (sb.charAt(j) != '\"') {
            ++j;
        }
        sb.replace(i + tokenStr.length(), j, args[2]);
        EditBuildScript.writeFile(buildFile, sb);
    }

    /*
     * Exception decompiling
     */
    static StringBuffer readFile(File f) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    static void writeFile(File f, StringBuffer chars) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(f.toPath(), StandardCharsets.ISO_8859_1, new OpenOption[0]);
             StringReader r = new StringReader(chars.toString());){
            EditBuildScript.copy(r, w);
        }
    }

    static void copy(Reader r, Writer w) throws IOException {
        int n;
        char[] buffer = new char[16384];
        while ((n = r.read(buffer, 0, buffer.length)) >= 0) {
            w.write(buffer, 0, n);
        }
    }
}


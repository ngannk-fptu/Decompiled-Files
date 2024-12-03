/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.repackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.xmlbeans.impl.repackage.Repackager;

public class Repackage {
    private final File _sourceBase;
    private final File _targetBase;
    private List<List<String>> _fromPackages;
    private List<List<String>> _toPackages;
    private Pattern _packagePattern;
    private final Repackager _repackager;
    private Map<String, String> _movedDirs;
    private List<String> _moveAlongFiles;
    private int _skippedFiles;

    public static void main(String[] args) throws Exception {
        new Repackage(args).repackage();
    }

    private Repackage(String[] args) {
        String sourceDir = null;
        String targetDir = null;
        String repackageSpec = null;
        boolean failure = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-repackage") && i + 1 < args.length) {
                repackageSpec = args[++i];
                continue;
            }
            if (args[i].equals("-f") && i + 1 < args.length) {
                sourceDir = args[++i];
                continue;
            }
            if (args[i].equals("-t") && i + 1 < args.length) {
                targetDir = args[++i];
                continue;
            }
            failure = true;
        }
        if (failure || repackageSpec == null || sourceDir == null ^ targetDir == null) {
            throw new RuntimeException("Usage: repackage -repackage [spec] [ -f [sourcedir] -t [targetdir] ]");
        }
        this._repackager = new Repackager(repackageSpec);
        if (sourceDir == null || targetDir == null) {
            this._targetBase = null;
            this._sourceBase = null;
        } else {
            this._sourceBase = new File(sourceDir);
            this._targetBase = new File(targetDir);
        }
    }

    public void repackage() throws Exception {
        if (this._sourceBase == null || this._targetBase == null) {
            System.out.println(this._repackager.repackage(this.readInputStream(System.in)).toString());
            return;
        }
        this._fromPackages = this._repackager.getFromPackages();
        this._toPackages = this._repackager.getToPackages();
        this._packagePattern = Pattern.compile("^\\s*package\\s+((?:\\w|\\.)*)\\s*;", 8);
        this._moveAlongFiles = new ArrayList<String>();
        this._movedDirs = new HashMap<String, String>();
        this._targetBase.mkdirs();
        ArrayList<File> files = new ArrayList<File>();
        this.fillFiles(files, this._sourceBase);
        System.out.println("Repackaging " + files.size() + " files ...");
        int prefixLength = this._sourceBase.getCanonicalPath().length();
        for (File from : files) {
            String name = from.getCanonicalPath().substring(prefixLength + 1);
            this.repackageFile(name);
        }
        this.finishMovingFiles();
        if (this._skippedFiles > 0) {
            System.out.println("Skipped " + this._skippedFiles + " unmodified files.");
        }
    }

    public void repackageFile(String name) throws IOException {
        if (name.endsWith(".java")) {
            this.repackageJavaFile(name);
        } else if (name.endsWith(".xsdconfig") || name.endsWith(".xml") || name.endsWith(".g")) {
            this.repackageNonJavaFile(name);
        } else if (name.startsWith("bin" + File.separatorChar)) {
            this.repackageNonJavaFile(name);
        } else {
            this.moveAlongWithJavaFiles(name);
        }
    }

    public void moveAlongWithJavaFiles(String name) {
        this._moveAlongFiles.add(name);
    }

    public void finishMovingFiles() throws IOException {
        for (String name : this._moveAlongFiles) {
            String toName;
            String srcDir = Repackager.dirForPath(name);
            String toDir = this._movedDirs.get(srcDir);
            String string = toName = toDir == null ? name : new File(toDir, new File(name).getName()).toString();
            if (name.endsWith(".html")) {
                this.repackageNonJavaFile(name, toName);
                continue;
            }
            this.justMoveNonJavaFile(name, toName);
        }
    }

    public void repackageNonJavaFile(String name) throws IOException {
        File sourceFile = new File(this._sourceBase, name);
        File targetFile = new File(this._targetBase, name);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
        } else {
            this.writeFile(targetFile, this._repackager.repackage(this.readFile(sourceFile)));
        }
    }

    public void repackageNonJavaFile(String sourceName, String targetName) throws IOException {
        File sourceFile = new File(this._sourceBase, sourceName);
        File targetFile = new File(this._targetBase, targetName);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
        } else {
            this.writeFile(targetFile, this._repackager.repackage(this.readFile(sourceFile)));
        }
    }

    public void justMoveNonJavaFile(String sourceName, String targetName) throws IOException {
        File sourceFile = new File(this._sourceBase, sourceName);
        File targetFile = new File(this._targetBase, targetName);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
        } else {
            Repackage.copyFile(sourceFile, targetFile);
        }
    }

    public void repackageJavaFile(String name) throws IOException {
        File sourceFile = new File(this._sourceBase, name);
        StringBuffer sb = this.readFile(sourceFile);
        Matcher packageMatcher = this._packagePattern.matcher(sb);
        if (packageMatcher.find()) {
            boolean swapped;
            String pkg = packageMatcher.group(1);
            int pkgStart = packageMatcher.start(1);
            int pkgEnd = packageMatcher.end(1);
            if (packageMatcher.find()) {
                throw new RuntimeException("Two package specifications found: " + name);
            }
            List<String> filePath = Repackager.splitPath(name, File.separatorChar);
            String srcDir = Repackager.dirForPath(name);
            do {
                swapped = false;
                for (int i = 1; i < filePath.size(); ++i) {
                    String spec1 = filePath.get(i - 1);
                    String spec2 = filePath.get(i);
                    if (spec1.indexOf(58) >= spec2.indexOf(58)) continue;
                    filePath.set(i - 1, spec2);
                    filePath.set(i, spec1);
                    swapped = true;
                }
            } while (swapped);
            List<String> pkgPath = Repackager.splitPath(pkg, '.');
            int f = filePath.size() - 2;
            if (f < 0 || filePath.size() - 1 < pkgPath.size()) {
                throw new RuntimeException("Package spec differs from file path: " + name);
            }
            for (int i = pkgPath.size() - 1; i >= 0; --i) {
                if (!pkgPath.get(i).equals(filePath.get(f))) {
                    throw new RuntimeException("Package spec differs from file path: " + name);
                }
                --f;
            }
            List<String> changeTo = null;
            List<String> changeFrom = null;
            block3: for (int i = 0; i < this._fromPackages.size(); ++i) {
                List<String> from = this._fromPackages.get(i);
                if (from.size() > pkgPath.size()) continue;
                for (int j = 0; j < from.size(); ++j) {
                    if (!from.get(j).equals(pkgPath.get(j))) continue block3;
                }
                changeFrom = from;
                changeTo = this._toPackages.get(i);
                break;
            }
            if (changeTo != null) {
                int i;
                String newPkg = "";
                String newName = "";
                for (i = 0; i < changeTo.size(); ++i) {
                    if (i > 0) {
                        newPkg = newPkg + ".";
                        newName = newName + File.separatorChar;
                    }
                    newPkg = newPkg + (String)changeTo.get(i);
                    newName = newName + changeTo.get(i);
                }
                for (i = filePath.size() - pkgPath.size() - 2; i >= 0; --i) {
                    newName = filePath.get(i) + File.separatorChar + newName;
                }
                for (i = changeFrom.size(); i < pkgPath.size(); ++i) {
                    newName = newName + File.separatorChar + pkgPath.get(i);
                    newPkg = newPkg + '.' + pkgPath.get(i);
                }
                newName = newName + File.separatorChar + filePath.get(filePath.size() - 1);
                sb.replace(pkgStart, pkgEnd, newPkg);
                name = newName;
                String newDir = Repackager.dirForPath(name);
                if (!srcDir.equals(newDir)) {
                    this._movedDirs.put(srcDir, newDir);
                }
            }
        }
        File targetFile = new File(this._targetBase, name);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
            return;
        }
        this.writeFile(new File(this._targetBase, name), this._repackager.repackage(sb));
    }

    void writeFile(File f, StringBuffer chars) throws IOException {
        f.getParentFile().mkdirs();
        try (BufferedWriter w = Files.newBufferedWriter(f.toPath(), StandardCharsets.ISO_8859_1, new OpenOption[0]);
             StringReader r = new StringReader(chars.toString());){
            Repackage.copy(r, w);
        }
    }

    /*
     * Exception decompiling
     */
    StringBuffer readFile(File f) throws IOException {
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

    /*
     * Exception decompiling
     */
    StringBuffer readInputStream(InputStream is) throws IOException {
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

    public static void copyFile(File from, File to) throws IOException {
        to.getParentFile().mkdirs();
        try (FileInputStream in = new FileInputStream(from);
             FileOutputStream out = new FileOutputStream(to);){
            Repackage.copy(in, out);
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int n;
        byte[] buffer = new byte[16384];
        while ((n = in.read(buffer, 0, buffer.length)) >= 0) {
            out.write(buffer, 0, n);
        }
    }

    public static void copy(Reader r, Writer w) throws IOException {
        int n;
        char[] buffer = new char[16384];
        while ((n = r.read(buffer, 0, buffer.length)) >= 0) {
            w.write(buffer, 0, n);
        }
    }

    public void fillFiles(List<File> files, File file) {
        if (!file.isDirectory()) {
            files.add(file);
            return;
        }
        if (file.getName().equals("build")) {
            return;
        }
        if (file.getName().equals("CVS")) {
            return;
        }
        String[] entries = file.list();
        if (entries == null) {
            throw new RuntimeException("Directory can't be accessed: " + file.toString());
        }
        for (String entry : entries) {
            this.fillFiles(files, new File(file, entry));
        }
    }

    public void recursiveDelete(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            String[] entries = file.list();
            if (entries == null) {
                throw new RuntimeException("Directory can't be accessed: " + file.toString());
            }
            for (String entry : entries) {
                this.recursiveDelete(new File(file, entry));
            }
        }
        file.delete();
    }
}


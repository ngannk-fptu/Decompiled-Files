/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.java.Groovifier;
import org.codehaus.groovy.antlr.java.Java2GroovyConverter;
import org.codehaus.groovy.antlr.java.JavaLexer;
import org.codehaus.groovy.antlr.java.JavaRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.codehaus.groovy.antlr.treewalker.SourceCodeTraversal;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool;
import org.codehaus.groovy.tools.groovydoc.LinkArgument;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDocAssembler;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyPackageDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyRootDoc;
import org.codehaus.groovy.tools.shell.util.Logger;

public class GroovyRootDocBuilder {
    private final Logger log = Logger.create(GroovyRootDocBuilder.class);
    private static final char FS = '/';
    private List<LinkArgument> links;
    private final GroovyDocTool tool;
    private final String[] sourcepaths;
    private final SimpleGroovyRootDoc rootDoc;
    private final Properties properties;

    public GroovyRootDocBuilder(GroovyDocTool tool, String[] sourcepaths, List<LinkArgument> links, Properties properties) {
        this.tool = tool;
        this.sourcepaths = sourcepaths;
        this.links = links;
        this.rootDoc = new SimpleGroovyRootDoc("root");
        this.properties = properties;
    }

    public Map<String, GroovyClassDoc> getClassDocsFromSingleSource(String packagePath, String file, String src) throws RecognitionException, TokenStreamException {
        if (file.indexOf(".java") > 0) {
            return this.parseJava(packagePath, file, src);
        }
        if (file.indexOf(".sourcefile") > 0) {
            return this.parseJava(packagePath, file, src);
        }
        return this.parseGroovy(packagePath, file, src);
    }

    private Map<String, GroovyClassDoc> parseJava(String packagePath, String file, String src) throws RecognitionException, TokenStreamException {
        SourceBuffer sourceBuffer = new SourceBuffer();
        JavaRecognizer parser = GroovyRootDocBuilder.getJavaParser(src, sourceBuffer);
        String[] tokenNames = parser.getTokenNames();
        try {
            parser.compilationUnit();
        }
        catch (OutOfMemoryError e) {
            this.log.error("Out of memory while processing: " + packagePath + "/" + file);
            throw e;
        }
        AST ast = parser.getAST();
        Java2GroovyConverter java2groovyConverter = new Java2GroovyConverter(tokenNames);
        PreOrderTraversal java2groovyTraverser = new PreOrderTraversal(java2groovyConverter);
        java2groovyTraverser.process(ast);
        Groovifier groovifier = new Groovifier(tokenNames, false);
        PreOrderTraversal groovifierTraverser = new PreOrderTraversal(groovifier);
        groovifierTraverser.process(ast);
        SimpleGroovyClassDocAssembler visitor = new SimpleGroovyClassDocAssembler(packagePath, file, sourceBuffer, this.links, this.properties, false);
        SourceCodeTraversal traverser = new SourceCodeTraversal(visitor);
        traverser.process(ast);
        return visitor.getGroovyClassDocs();
    }

    private Map<String, GroovyClassDoc> parseGroovy(String packagePath, String file, String src) throws RecognitionException, TokenStreamException {
        SourceBuffer sourceBuffer = new SourceBuffer();
        GroovyRecognizer parser = GroovyRootDocBuilder.getGroovyParser(src, sourceBuffer);
        try {
            parser.compilationUnit();
        }
        catch (OutOfMemoryError e) {
            this.log.error("Out of memory while processing: " + packagePath + "/" + file);
            throw e;
        }
        AST ast = parser.getAST();
        SimpleGroovyClassDocAssembler visitor = new SimpleGroovyClassDocAssembler(packagePath, file, sourceBuffer, this.links, this.properties, true);
        SourceCodeTraversal traverser = new SourceCodeTraversal(visitor);
        traverser.process(ast);
        return visitor.getGroovyClassDocs();
    }

    private static JavaRecognizer getJavaParser(String input, SourceBuffer sourceBuffer) {
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(input), sourceBuffer);
        JavaLexer lexer = new JavaLexer(unicodeReader);
        unicodeReader.setLexer(lexer);
        JavaRecognizer parser = JavaRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);
        return parser;
    }

    private static GroovyRecognizer getGroovyParser(String input, SourceBuffer sourceBuffer) {
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(input), sourceBuffer);
        GroovyLexer lexer = new GroovyLexer(unicodeReader);
        unicodeReader.setLexer(lexer);
        GroovyRecognizer parser = GroovyRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);
        return parser;
    }

    public void buildTree(List<String> filenames) throws IOException {
        this.setOverview();
        ArrayList<File> sourcepathFiles = new ArrayList<File>();
        if (this.sourcepaths != null) {
            for (String sourcepath : this.sourcepaths) {
                sourcepathFiles.add(new File(sourcepath).getAbsoluteFile());
            }
        }
        block1: for (String filename : filenames) {
            File srcFile = new File(filename);
            if (srcFile.exists()) {
                this.processFile(filename, srcFile, true);
                continue;
            }
            for (File spath : sourcepathFiles) {
                srcFile = new File(spath, filename);
                if (!srcFile.exists()) continue;
                this.processFile(filename, srcFile, false);
                continue block1;
            }
        }
    }

    private void setOverview() {
        String path = this.properties.getProperty("overviewFile");
        if (path != null && path.length() > 0) {
            try {
                String content = ResourceGroovyMethods.getText(new File(path));
                this.calcThenSetOverviewDescription(content);
            }
            catch (IOException e) {
                System.err.println("Unable to load overview file: " + e.getMessage());
            }
        }
    }

    private void processFile(String filename, File srcFile, boolean isAbsolute) throws IOException {
        String src = ResourceGroovyMethods.getText(srcFile);
        String relPackage = GroovyDocTool.getPath(filename).replace('\\', '/');
        String packagePath = isAbsolute ? "DefaultPackage" : relPackage;
        String file = GroovyDocTool.getFile(filename);
        SimpleGroovyPackageDoc packageDoc = null;
        if (!isAbsolute) {
            packageDoc = (SimpleGroovyPackageDoc)this.rootDoc.packageNamed(packagePath);
        }
        if (filename.endsWith("package.html") || filename.endsWith("package-info.java") || filename.endsWith("package-info.groovy")) {
            if (packageDoc == null) {
                packageDoc = new SimpleGroovyPackageDoc(relPackage);
                packagePath = relPackage;
            }
            this.processPackageInfo(src, filename, packageDoc);
            this.rootDoc.put(packagePath, packageDoc);
            return;
        }
        try {
            Iterator<Map.Entry<String, GroovyClassDoc>> iterator;
            Map<String, GroovyClassDoc> classDocs = this.getClassDocsFromSingleSource(packagePath, file, src);
            this.rootDoc.putAllClasses(classDocs);
            if (isAbsolute && (iterator = classDocs.entrySet().iterator()).hasNext()) {
                Map.Entry<String, GroovyClassDoc> docEntry = iterator.next();
                String fullPath = docEntry.getValue().getFullPathName();
                int slash = fullPath.lastIndexOf(47);
                if (slash > 0) {
                    packagePath = fullPath.substring(0, slash);
                }
                packageDoc = (SimpleGroovyPackageDoc)this.rootDoc.packageNamed(packagePath);
            }
            if (packageDoc == null) {
                packageDoc = new SimpleGroovyPackageDoc(packagePath);
            }
            packageDoc.putAll(classDocs);
            this.rootDoc.put(packagePath, packageDoc);
        }
        catch (RecognitionException e) {
            this.log.error("ignored due to RecognitionException: " + filename + " [" + e.getMessage() + "]");
            this.log.debug("ignored due to RecognitionException: " + filename + " [" + e.getMessage() + "]", e);
        }
        catch (TokenStreamException e) {
            this.log.error("ignored due to TokenStreamException: " + filename + " [" + e.getMessage() + "]");
            this.log.debug("ignored due to TokenStreamException: " + filename + " [" + e.getMessage() + "]", e);
        }
    }

    void processPackageInfo(String src, String filename, SimpleGroovyPackageDoc packageDoc) {
        String relPath = packageDoc.getRelativeRootPath();
        String description = this.calcThenSetPackageDescription(src, filename, relPath);
        packageDoc.setDescription(description);
        String altDescription = this.calcThenSetPackageDescription(src, filename, "");
        GroovyRootDocBuilder.calcThenSetSummary(altDescription, packageDoc);
    }

    private String calcThenSetPackageDescription(String src, String filename, String relPath) {
        String description;
        if (filename.endsWith(".html")) {
            description = GroovyRootDocBuilder.scrubOffExcessiveTags(src);
            description = GroovyRootDocBuilder.pruneTagFromFront(description, "p");
            description = GroovyRootDocBuilder.pruneTagFromEnd(description, "/p");
        } else {
            description = GroovyRootDocBuilder.trimPackageAndComments(src);
        }
        description = this.replaceTags(description, relPath);
        return description;
    }

    private String replaceTags(String orig, String relPath) {
        String result = orig.replaceAll("(?m)^\\s*\\*", "");
        result = this.replaceAllTags(result, "", "", SimpleGroovyClassDoc.LINK_REGEX, relPath);
        result = this.replaceAllTags(result, "<TT>", "</TT>", SimpleGroovyClassDoc.CODE_REGEX, relPath);
        result = this.replaceAllTags(result + "@endMarker", "<DL><DT><B>$1:</B></DT><DD>", "</DD></DL>", SimpleGroovyClassDoc.TAG_REGEX, relPath);
        result = result.substring(0, result.length() - 10);
        return SimpleGroovyClassDoc.decodeSpecialSymbols(result);
    }

    private String replaceAllTags(String self, String s1, String s2, Pattern regex, String relPath) {
        return SimpleGroovyClassDoc.replaceAllTags(self, s1, s2, regex, this.links, relPath, this.rootDoc, null);
    }

    private static void calcThenSetSummary(String src, SimpleGroovyPackageDoc packageDoc) {
        packageDoc.setSummary(SimpleGroovyDoc.calculateFirstSentence(src));
    }

    private void calcThenSetOverviewDescription(String src) {
        String description = GroovyRootDocBuilder.scrubOffExcessiveTags(src);
        this.rootDoc.setDescription(description);
    }

    private static String trimPackageAndComments(String src) {
        return src.replaceFirst("(?sm)^package.*", "").replaceFirst("(?sm)/.*\\*\\*(.*)\\*/", "$1").replaceAll("(?m)^\\s*\\*", "");
    }

    private static String scrubOffExcessiveTags(String src) {
        String description = GroovyRootDocBuilder.pruneTagFromFront(src, "html");
        description = GroovyRootDocBuilder.pruneTagFromFront(description, "/head");
        description = GroovyRootDocBuilder.pruneTagFromFront(description, "body");
        description = GroovyRootDocBuilder.pruneTagFromEnd(description, "/html");
        return GroovyRootDocBuilder.pruneTagFromEnd(description, "/body");
    }

    private static String pruneTagFromFront(String description, String tag) {
        int index = Math.max(GroovyRootDocBuilder.indexOfTag(description, tag.toLowerCase()), GroovyRootDocBuilder.indexOfTag(description, tag.toUpperCase()));
        if (index < 0) {
            return description;
        }
        return description.substring(index);
    }

    private static String pruneTagFromEnd(String description, String tag) {
        int index = Math.max(description.lastIndexOf("<" + tag.toLowerCase() + ">"), description.lastIndexOf("<" + tag.toUpperCase() + ">"));
        if (index < 0) {
            return description;
        }
        return description.substring(0, index);
    }

    private static int indexOfTag(String text, String tag) {
        int pos = text.indexOf("<" + tag + ">");
        if (pos > 0) {
            pos += tag.length() + 2;
        }
        return pos;
    }

    public GroovyRootDoc getRootDoc() {
        this.rootDoc.resolve();
        return this.rootDoc;
    }
}


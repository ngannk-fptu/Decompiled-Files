/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

import groovyjarjarantlr.collections.AST;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.java.Groovifier;
import org.codehaus.groovy.antlr.java.Java2GroovyConverter;
import org.codehaus.groovy.antlr.java.JavaLexer;
import org.codehaus.groovy.antlr.java.JavaRecognizer;
import org.codehaus.groovy.antlr.java.PreJava2GroovyConverter;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.treewalker.MindMapPrinter;
import org.codehaus.groovy.antlr.treewalker.NodePrinter;
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.codehaus.groovy.antlr.treewalker.SourceCodeTraversal;
import org.codehaus.groovy.antlr.treewalker.SourcePrinter;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class Java2GroovyProcessor {
    public static void processFiles(List<String> fileNames) throws Exception {
        for (String filename : fileNames) {
            File f = new File(filename);
            String text = ResourceGroovyMethods.getText(f);
            System.out.println(Java2GroovyProcessor.convert(filename, text, true, true));
        }
    }

    public static String convert(String filename, String input) throws Exception {
        return Java2GroovyProcessor.convert(filename, input, false, false);
    }

    public static String convert(String filename, String input, boolean withHeader, boolean withNewLines) throws Exception {
        JavaRecognizer parser = Java2GroovyProcessor.getJavaParser(input);
        String[] tokenNames = parser.getTokenNames();
        parser.compilationUnit();
        AST ast = parser.getAST();
        if ("mindmap".equals(System.getProperty("ANTLR.AST".toLowerCase()))) {
            try {
                PrintStream out = new PrintStream(new FileOutputStream(filename + ".mm"));
                MindMapPrinter visitor = new MindMapPrinter(out, tokenNames);
                PreOrderTraversal treewalker = new PreOrderTraversal(visitor);
                treewalker.process(ast);
            }
            catch (FileNotFoundException e) {
                System.out.println("Cannot create " + filename + ".mm");
            }
        }
        Java2GroovyProcessor.modifyJavaASTintoGroovyAST(tokenNames, ast);
        String[] groovyTokenNames = Java2GroovyProcessor.getGroovyTokenNames(input);
        Java2GroovyProcessor.groovifyFatJavaLikeGroovyAST(ast, groovyTokenNames);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SourcePrinter visitor = new SourcePrinter(new PrintStream(baos), groovyTokenNames, withNewLines);
        SourceCodeTraversal traverser = new SourceCodeTraversal(visitor);
        traverser.process(ast);
        String header = "";
        if (withHeader) {
            header = "/*\n  Automatically Converted from Java Source \n  \n  by java2groovy v0.0.1   Copyright Jeremy Rayner 2007\n  \n  !! NOT FIT FOR ANY PURPOSE !! \n  'java2groovy' cannot be used to convert one working program into another  */\n\n";
        }
        return header + new String(baos.toByteArray());
    }

    private static void groovifyFatJavaLikeGroovyAST(AST ast, String[] groovyTokenNames) {
        Groovifier groovifier = new Groovifier(groovyTokenNames);
        PreOrderTraversal groovifierTraverser = new PreOrderTraversal(groovifier);
        groovifierTraverser.process(ast);
    }

    private static void modifyJavaASTintoGroovyAST(String[] tokenNames, AST ast) {
        PreJava2GroovyConverter preJava2groovyConverter = new PreJava2GroovyConverter(tokenNames);
        PreOrderTraversal preJava2groovyTraverser = new PreOrderTraversal(preJava2groovyConverter);
        preJava2groovyTraverser.process(ast);
        Java2GroovyConverter java2groovyConverter = new Java2GroovyConverter(tokenNames);
        PreOrderTraversal java2groovyTraverser = new PreOrderTraversal(java2groovyConverter);
        java2groovyTraverser.process(ast);
    }

    private static JavaRecognizer getJavaParser(String input) {
        JavaRecognizer parser = null;
        SourceBuffer sourceBuffer = new SourceBuffer();
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(input), sourceBuffer);
        JavaLexer lexer = new JavaLexer(unicodeReader);
        unicodeReader.setLexer(lexer);
        parser = JavaRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);
        return parser;
    }

    public static String mindmap(String input) throws Exception {
        JavaRecognizer parser = Java2GroovyProcessor.getJavaParser(input);
        String[] tokenNames = parser.getTokenNames();
        parser.compilationUnit();
        AST ast = parser.getAST();
        Java2GroovyProcessor.modifyJavaASTintoGroovyAST(tokenNames, ast);
        String[] groovyTokenNames = Java2GroovyProcessor.getGroovyTokenNames(input);
        Java2GroovyProcessor.groovifyFatJavaLikeGroovyAST(ast, groovyTokenNames);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MindMapPrinter visitor = new MindMapPrinter(new PrintStream(baos), groovyTokenNames);
        SourceCodeTraversal traverser = new SourceCodeTraversal(visitor);
        traverser.process(ast);
        return new String(baos.toByteArray());
    }

    public static String nodePrinter(String input) throws Exception {
        JavaRecognizer parser = Java2GroovyProcessor.getJavaParser(input);
        String[] tokenNames = parser.getTokenNames();
        parser.compilationUnit();
        AST ast = parser.getAST();
        Java2GroovyProcessor.modifyJavaASTintoGroovyAST(tokenNames, ast);
        String[] groovyTokenNames = Java2GroovyProcessor.getGroovyTokenNames(input);
        Java2GroovyProcessor.groovifyFatJavaLikeGroovyAST(ast, groovyTokenNames);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NodePrinter visitor = new NodePrinter(new PrintStream(baos), groovyTokenNames);
        SourceCodeTraversal traverser = new SourceCodeTraversal(visitor);
        traverser.process(ast);
        return new String(baos.toByteArray());
    }

    private static String[] getGroovyTokenNames(String input) {
        GroovyRecognizer groovyParser = null;
        SourceBuffer groovySourceBuffer = new SourceBuffer();
        UnicodeEscapingReader groovyUnicodeReader = new UnicodeEscapingReader(new StringReader(input), groovySourceBuffer);
        GroovyLexer groovyLexer = new GroovyLexer(groovyUnicodeReader);
        groovyUnicodeReader.setLexer(groovyLexer);
        groovyParser = GroovyRecognizer.make(groovyLexer);
        return groovyParser.getTokenNames();
    }
}


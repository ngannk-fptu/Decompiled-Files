/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.ASTFactory;
import groovyjarjarantlr.CommonAST;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.debug.misc.ASTFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;

class Main {
    static boolean whitespaceIncluded = false;
    static boolean showTree = false;
    static boolean verbose = false;

    Main() {
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                System.err.println("Parsing...");
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].equals("-showtree")) {
                        showTree = true;
                        continue;
                    }
                    if (args[i].equals("-verbose")) {
                        verbose = true;
                        continue;
                    }
                    if (args[i].equals("-trace")) {
                        GroovyRecognizer.tracing = true;
                        GroovyLexer.tracing = true;
                        continue;
                    }
                    if (args[i].equals("-traceParser")) {
                        GroovyRecognizer.tracing = true;
                        continue;
                    }
                    if (args[i].equals("-traceLexer")) {
                        GroovyLexer.tracing = true;
                        continue;
                    }
                    if (args[i].equals("-whitespaceIncluded")) {
                        whitespaceIncluded = true;
                        continue;
                    }
                    Main.doFile(new File(args[i]));
                }
            } else {
                System.err.println("Usage: java -jar groovyc.jar [-showtree] [-verbose] [-trace{,Lexer,Parser}]<directory or file name>");
            }
        }
        catch (Exception e) {
            System.err.println("exception: " + e);
            e.printStackTrace(System.err);
        }
    }

    public static void doFile(File f) throws Exception {
        if (f.isDirectory()) {
            String[] files = f.list();
            for (int i = 0; i < files.length; ++i) {
                Main.doFile(new File(f, files[i]));
            }
        } else if (f.getName().endsWith(".groovy")) {
            System.err.println(" --- " + f.getAbsolutePath());
            SourceBuffer sourceBuffer = new SourceBuffer();
            UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new FileReader(f), sourceBuffer);
            GroovyLexer lexer = new GroovyLexer(unicodeReader);
            unicodeReader.setLexer(lexer);
            Main.parseFile(f.getName(), lexer, sourceBuffer);
        }
    }

    public static void parseFile(String f, GroovyLexer l, SourceBuffer sourceBuffer) throws Exception {
        try {
            GroovyRecognizer parser = GroovyRecognizer.make(l);
            parser.setSourceBuffer(sourceBuffer);
            parser.setFilename(f);
            if (whitespaceIncluded) {
                Token t;
                GroovyLexer lexer = parser.getLexer();
                lexer.setWhitespaceIncluded(true);
                do {
                    t = lexer.nextToken();
                    System.out.println(t);
                } while (t != null && t.getType() != 1);
                return;
            }
            parser.compilationUnit();
            System.out.println("parseFile " + f + " => " + parser.getAST());
            Main.doTreeAction(f, parser.getAST(), parser.getTokenNames());
        }
        catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();
        }
    }

    public static void doTreeAction(String f, AST t, String[] tokenNames) {
        if (t == null) {
            return;
        }
        if (showTree) {
            CommonAST.setVerboseStringConversion(true, tokenNames);
            ASTFactory factory = new ASTFactory();
            AST r = factory.create(0, "AST ROOT");
            r.setFirstChild(t);
            final ASTFrame frame = new ASTFrame("Groovy AST", r);
            frame.setVisible(true);
            frame.addWindowListener(new WindowAdapter(){

                @Override
                public void windowClosing(WindowEvent e) {
                    frame.setVisible(false);
                    frame.dispose();
                    System.exit(0);
                }
            });
            if (verbose) {
                System.out.println(t.toStringList());
            }
        }
    }
}


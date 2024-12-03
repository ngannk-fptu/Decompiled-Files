/*
 * Decompiled with CFR 0.152.
 */
package antlr.preprocessor;

import antlr.ANTLRException;
import antlr.TokenStreamException;
import antlr.Tool;
import antlr.collections.impl.IndexedVector;
import antlr.preprocessor.Grammar;
import antlr.preprocessor.GrammarFile;
import antlr.preprocessor.Option;
import antlr.preprocessor.Preprocessor;
import antlr.preprocessor.PreprocessorLexer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;

public class Hierarchy {
    protected Grammar LexerRoot = null;
    protected Grammar ParserRoot = null;
    protected Grammar TreeParserRoot = null;
    protected Hashtable symbols;
    protected Hashtable files;
    protected Tool antlrTool;

    public Hierarchy(Tool tool) {
        this.antlrTool = tool;
        this.LexerRoot = new Grammar(tool, "Lexer", null, null);
        this.ParserRoot = new Grammar(tool, "Parser", null, null);
        this.TreeParserRoot = new Grammar(tool, "TreeParser", null, null);
        this.symbols = new Hashtable(10);
        this.files = new Hashtable(10);
        this.LexerRoot.setPredefined(true);
        this.ParserRoot.setPredefined(true);
        this.TreeParserRoot.setPredefined(true);
        this.symbols.put(this.LexerRoot.getName(), this.LexerRoot);
        this.symbols.put(this.ParserRoot.getName(), this.ParserRoot);
        this.symbols.put(this.TreeParserRoot.getName(), this.TreeParserRoot);
    }

    public void addGrammar(Grammar grammar) {
        grammar.setHierarchy(this);
        this.symbols.put(grammar.getName(), grammar);
        GrammarFile grammarFile = this.getFile(grammar.getFileName());
        grammarFile.addGrammar(grammar);
    }

    public void addGrammarFile(GrammarFile grammarFile) {
        this.files.put(grammarFile.getName(), grammarFile);
    }

    public void expandGrammarsInFile(String string) {
        GrammarFile grammarFile = this.getFile(string);
        Enumeration enumeration = grammarFile.getGrammars().elements();
        while (enumeration.hasMoreElements()) {
            Grammar grammar = (Grammar)enumeration.nextElement();
            grammar.expandInPlace();
        }
    }

    public Grammar findRoot(Grammar grammar) {
        if (grammar.getSuperGrammarName() == null) {
            return grammar;
        }
        Grammar grammar2 = grammar.getSuperGrammar();
        if (grammar2 == null) {
            return grammar;
        }
        return this.findRoot(grammar2);
    }

    public GrammarFile getFile(String string) {
        return (GrammarFile)this.files.get(string);
    }

    public Grammar getGrammar(String string) {
        return (Grammar)this.symbols.get(string);
    }

    public static String optionsToString(IndexedVector indexedVector) {
        String string = "options {" + System.getProperty("line.separator");
        Enumeration enumeration = indexedVector.elements();
        while (enumeration.hasMoreElements()) {
            string = string + (Option)enumeration.nextElement() + System.getProperty("line.separator");
        }
        string = string + "}" + System.getProperty("line.separator") + System.getProperty("line.separator");
        return string;
    }

    public void readGrammarFile(String string) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(string));
        this.addGrammarFile(new GrammarFile(this.antlrTool, string));
        PreprocessorLexer preprocessorLexer = new PreprocessorLexer(bufferedReader);
        preprocessorLexer.setFilename(string);
        Preprocessor preprocessor = new Preprocessor(preprocessorLexer);
        preprocessor.setTool(this.antlrTool);
        preprocessor.setFilename(string);
        try {
            preprocessor.grammarFile(this, string);
        }
        catch (TokenStreamException tokenStreamException) {
            this.antlrTool.toolError("Token stream error reading grammar(s):\n" + tokenStreamException);
        }
        catch (ANTLRException aNTLRException) {
            this.antlrTool.toolError("error reading grammar(s):\n" + aNTLRException);
        }
    }

    public boolean verifyThatHierarchyIsComplete() {
        Grammar grammar;
        boolean bl = true;
        Enumeration enumeration = this.symbols.elements();
        while (enumeration.hasMoreElements()) {
            Grammar grammar2;
            grammar = (Grammar)enumeration.nextElement();
            if (grammar.getSuperGrammarName() == null || (grammar2 = grammar.getSuperGrammar()) != null) continue;
            this.antlrTool.toolError("grammar " + grammar.getSuperGrammarName() + " not defined");
            bl = false;
            this.symbols.remove(grammar.getName());
        }
        if (!bl) {
            return false;
        }
        enumeration = this.symbols.elements();
        while (enumeration.hasMoreElements()) {
            grammar = (Grammar)enumeration.nextElement();
            if (grammar.getSuperGrammarName() == null) continue;
            grammar.setType(this.findRoot(grammar).getName());
        }
        return true;
    }

    public Tool getTool() {
        return this.antlrTool;
    }

    public void setTool(Tool tool) {
        this.antlrTool = tool;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.preprocessor;

import groovyjarjarantlr.Tool;
import groovyjarjarantlr.collections.impl.IndexedVector;
import groovyjarjarantlr.preprocessor.Grammar;
import groovyjarjarantlr.preprocessor.Hierarchy;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class GrammarFile {
    protected String fileName;
    protected String headerAction = "";
    protected IndexedVector options;
    protected IndexedVector grammars;
    protected boolean expanded = false;
    protected Tool tool;

    public GrammarFile(Tool tool, String string) {
        this.fileName = string;
        this.grammars = new IndexedVector();
        this.tool = tool;
    }

    public void addGrammar(Grammar grammar) {
        this.grammars.appendElement(grammar.getName(), grammar);
    }

    public void generateExpandedFile() throws IOException {
        if (!this.expanded) {
            return;
        }
        String string = this.nameForExpandedGrammarFile(this.getName());
        PrintWriter printWriter = this.tool.openOutputFile(string);
        printWriter.println(this.toString());
        printWriter.close();
    }

    public IndexedVector getGrammars() {
        return this.grammars;
    }

    public String getName() {
        return this.fileName;
    }

    public String nameForExpandedGrammarFile(String string) {
        if (this.expanded) {
            return "expanded" + this.tool.fileMinusPath(string);
        }
        return string;
    }

    public void setExpanded(boolean bl) {
        this.expanded = bl;
    }

    public void addHeaderAction(String string) {
        this.headerAction = this.headerAction + string + System.getProperty("line.separator");
    }

    public void setOptions(IndexedVector indexedVector) {
        this.options = indexedVector;
    }

    public String toString() {
        String string = this.headerAction == null ? "" : this.headerAction;
        String string2 = this.options == null ? "" : Hierarchy.optionsToString(this.options);
        StringBuffer stringBuffer = new StringBuffer(10000);
        stringBuffer.append(string);
        stringBuffer.append(string2);
        Enumeration enumeration = this.grammars.elements();
        while (enumeration.hasMoreElements()) {
            Grammar grammar = (Grammar)enumeration.nextElement();
            stringBuffer.append(grammar.toString());
        }
        return stringBuffer.toString();
    }
}


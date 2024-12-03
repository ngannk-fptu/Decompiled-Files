/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.JavaCodeGeneratorPrintWriterManager;
import groovyjarjarantlr.PrintWriterWithSMAP;
import groovyjarjarantlr.Tool;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DefaultJavaCodeGeneratorPrintWriterManager
implements JavaCodeGeneratorPrintWriterManager {
    private Grammar grammar;
    private PrintWriterWithSMAP smapOutput;
    private PrintWriter currentOutput;
    private Tool tool;
    private Map sourceMaps = new HashMap();
    private String currentFileName;

    public PrintWriter setupOutput(Tool tool, Grammar grammar) throws IOException {
        return this.setupOutput(tool, grammar, null);
    }

    public PrintWriter setupOutput(Tool tool, String string) throws IOException {
        return this.setupOutput(tool, null, string);
    }

    public PrintWriter setupOutput(Tool tool, Grammar grammar, String string) throws IOException {
        this.tool = tool;
        this.grammar = grammar;
        if (string == null) {
            string = grammar.getClassName();
        }
        this.smapOutput = new PrintWriterWithSMAP(tool.openOutputFile(string + ".java"));
        this.currentFileName = string + ".java";
        this.currentOutput = this.smapOutput;
        return this.currentOutput;
    }

    public void startMapping(int n) {
        this.smapOutput.startMapping(n);
    }

    public void startSingleSourceLineMapping(int n) {
        this.smapOutput.startSingleSourceLineMapping(n);
    }

    public void endMapping() {
        this.smapOutput.endMapping();
    }

    public void finishOutput() throws IOException {
        this.currentOutput.close();
        if (this.grammar != null) {
            PrintWriter printWriter = this.tool.openOutputFile(this.grammar.getClassName() + ".smap");
            String string = this.grammar.getFilename();
            int n = (string = string.replace('\\', '/')).lastIndexOf(47);
            if (n != -1) {
                string = string.substring(n + 1);
            }
            this.smapOutput.dump(printWriter, this.grammar.getClassName(), string);
            this.sourceMaps.put(this.currentFileName, this.smapOutput.getSourceMap());
        }
        this.currentOutput = null;
    }

    public Map getSourceMaps() {
        return this.sourceMaps;
    }

    public int getCurrentOutputLine() {
        return this.smapOutput.getCurrentOutputLine();
    }
}


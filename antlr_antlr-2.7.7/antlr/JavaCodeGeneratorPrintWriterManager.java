/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Grammar;
import antlr.Tool;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public interface JavaCodeGeneratorPrintWriterManager {
    public PrintWriter setupOutput(Tool var1, Grammar var2) throws IOException;

    public PrintWriter setupOutput(Tool var1, String var2) throws IOException;

    public void startMapping(int var1);

    public void startSingleSourceLineMapping(int var1);

    public void endMapping();

    public void finishOutput() throws IOException;

    public Map getSourceMaps();
}


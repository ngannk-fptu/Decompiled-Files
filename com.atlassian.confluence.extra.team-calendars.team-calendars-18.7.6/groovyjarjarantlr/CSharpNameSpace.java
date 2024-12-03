/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.NameSpace;
import java.io.PrintWriter;

public class CSharpNameSpace
extends NameSpace {
    public CSharpNameSpace(String string) {
        super(string);
    }

    void emitDeclarations(PrintWriter printWriter) {
        printWriter.println("namespace " + this.getName());
        printWriter.println("{");
    }

    void emitClosures(PrintWriter printWriter) {
        printWriter.println("}");
    }
}


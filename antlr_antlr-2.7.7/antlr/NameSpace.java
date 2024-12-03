/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class NameSpace {
    private Vector names = new Vector();
    private String _name;

    public NameSpace(String string) {
        this._name = new String(string);
        this.parse(string);
    }

    public String getName() {
        return this._name;
    }

    protected void parse(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "::");
        while (stringTokenizer.hasMoreTokens()) {
            this.names.addElement(stringTokenizer.nextToken());
        }
    }

    void emitDeclarations(PrintWriter printWriter) {
        Enumeration enumeration = this.names.elements();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            printWriter.println("ANTLR_BEGIN_NAMESPACE(" + string + ")");
        }
    }

    void emitClosures(PrintWriter printWriter) {
        for (int i = 0; i < this.names.size(); ++i) {
            printWriter.println("ANTLR_END_NAMESPACE");
        }
    }
}


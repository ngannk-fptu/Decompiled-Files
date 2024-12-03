/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrintWriterWithSMAP
extends PrintWriter {
    private int currentOutputLine = 1;
    private int currentSourceLine = 0;
    private Map sourceMap = new HashMap();
    private boolean lastPrintCharacterWasCR = false;
    private boolean mapLines = false;
    private boolean mapSingleSourceLine = false;
    private boolean anythingWrittenSinceMapping = false;

    public PrintWriterWithSMAP(OutputStream outputStream) {
        super(outputStream);
    }

    public PrintWriterWithSMAP(OutputStream outputStream, boolean bl) {
        super(outputStream, bl);
    }

    public PrintWriterWithSMAP(Writer writer) {
        super(writer);
    }

    public PrintWriterWithSMAP(Writer writer, boolean bl) {
        super(writer, bl);
    }

    public void startMapping(int n) {
        this.mapLines = true;
        if (n != -888) {
            this.currentSourceLine = n;
        }
    }

    public void startSingleSourceLineMapping(int n) {
        this.mapSingleSourceLine = true;
        this.mapLines = true;
        if (n != -888) {
            this.currentSourceLine = n;
        }
    }

    public void endMapping() {
        this.mapLine(false);
        this.mapLines = false;
        this.mapSingleSourceLine = false;
    }

    protected void mapLine(boolean bl) {
        if (this.mapLines && this.anythingWrittenSinceMapping) {
            Integer n = new Integer(this.currentSourceLine);
            Integer n2 = new Integer(this.currentOutputLine);
            ArrayList<Integer> arrayList = (ArrayList<Integer>)this.sourceMap.get(n);
            if (arrayList == null) {
                arrayList = new ArrayList<Integer>();
                this.sourceMap.put(n, arrayList);
            }
            if (!arrayList.contains(n2)) {
                arrayList.add(n2);
            }
        }
        if (bl) {
            ++this.currentOutputLine;
        }
        if (!this.mapSingleSourceLine) {
            ++this.currentSourceLine;
        }
        this.anythingWrittenSinceMapping = false;
    }

    public void dump(PrintWriter printWriter, String string, String string2) {
        printWriter.println("SMAP");
        printWriter.println(string + ".java");
        printWriter.println("G");
        printWriter.println("*S G");
        printWriter.println("*F");
        printWriter.println("+ 0 " + string2);
        printWriter.println(string2);
        printWriter.println("*L");
        ArrayList arrayList = new ArrayList(this.sourceMap.keySet());
        Collections.sort(arrayList);
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            Integer n = (Integer)iterator.next();
            List list = (List)this.sourceMap.get(n);
            Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                Integer n2 = (Integer)iterator2.next();
                printWriter.println(n + ":" + n2);
            }
        }
        printWriter.println("*E");
        printWriter.close();
    }

    public void write(char[] cArray, int n, int n2) {
        int n3 = n + n2;
        for (int i = n; i < n3; ++i) {
            this.checkChar(cArray[i]);
        }
        super.write(cArray, n, n2);
    }

    public void checkChar(int n) {
        if (this.lastPrintCharacterWasCR && n != 10) {
            this.mapLine(true);
        } else if (n == 10) {
            this.mapLine(true);
        } else if (!Character.isWhitespace((char)n)) {
            this.anythingWrittenSinceMapping = true;
        }
        this.lastPrintCharacterWasCR = n == 13;
    }

    public void write(int n) {
        this.checkChar(n);
        super.write(n);
    }

    public void write(String string, int n, int n2) {
        int n3 = n + n2;
        for (int i = n; i < n3; ++i) {
            this.checkChar(string.charAt(i));
        }
        super.write(string, n, n2);
    }

    public void println() {
        this.mapLine(true);
        super.println();
        this.lastPrintCharacterWasCR = false;
    }

    public Map getSourceMap() {
        return this.sourceMap;
    }

    public int getCurrentOutputLine() {
        return this.currentOutputLine;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.io.PrintWriter;
import java.util.Hashtable;
import org.apache.regexp.RECompiler;

public class REDebugCompiler
extends RECompiler {
    static Hashtable hashOpcode = new Hashtable();

    String opcodeToString(char c) {
        String string = (String)hashOpcode.get(new Integer(c));
        if (string == null) {
            string = "OP_????";
        }
        return string;
    }

    String charToString(char c) {
        if (c < ' ' || c > '\u007f') {
            return "\\" + c;
        }
        return String.valueOf(c);
    }

    String nodeToString(int n) {
        char c = this.instruction[n + 0];
        char c2 = this.instruction[n + 1];
        return this.opcodeToString(c) + ", opdata = " + c2;
    }

    public void dumpProgram(PrintWriter printWriter) {
        int n = 0;
        while (n < this.lenInstruction) {
            int n2;
            char c = this.instruction[n + 0];
            int n3 = this.instruction[n + 1];
            short s = (short)this.instruction[n + 2];
            printWriter.print(n + ". " + this.nodeToString(n) + ", next = ");
            if (s == 0) {
                printWriter.print("none");
            } else {
                printWriter.print(n + s);
            }
            n += 3;
            if (c == '[') {
                printWriter.print(", [");
                n2 = n3;
                int n4 = 0;
                while (n4 < n2) {
                    char c2;
                    char c3;
                    if ((c3 = this.instruction[n++]) == (c2 = this.instruction[n++])) {
                        printWriter.print(this.charToString(c3));
                    } else {
                        printWriter.print(this.charToString(c3) + "-" + this.charToString(c2));
                    }
                    ++n4;
                }
                printWriter.print("]");
            }
            if (c == 'A') {
                printWriter.print(", \"");
                n2 = n3;
                while (n2-- != 0) {
                    printWriter.print(this.charToString(this.instruction[n++]));
                }
                printWriter.print("\"");
            }
            printWriter.println("");
        }
    }

    static {
        hashOpcode.put(new Integer(56), "OP_RELUCTANTSTAR");
        hashOpcode.put(new Integer(61), "OP_RELUCTANTPLUS");
        hashOpcode.put(new Integer(47), "OP_RELUCTANTMAYBE");
        hashOpcode.put(new Integer(69), "OP_END");
        hashOpcode.put(new Integer(94), "OP_BOL");
        hashOpcode.put(new Integer(36), "OP_EOL");
        hashOpcode.put(new Integer(46), "OP_ANY");
        hashOpcode.put(new Integer(91), "OP_ANYOF");
        hashOpcode.put(new Integer(124), "OP_BRANCH");
        hashOpcode.put(new Integer(65), "OP_ATOM");
        hashOpcode.put(new Integer(42), "OP_STAR");
        hashOpcode.put(new Integer(43), "OP_PLUS");
        hashOpcode.put(new Integer(63), "OP_MAYBE");
        hashOpcode.put(new Integer(78), "OP_NOTHING");
        hashOpcode.put(new Integer(71), "OP_GOTO");
        hashOpcode.put(new Integer(92), "OP_ESCAPE");
        hashOpcode.put(new Integer(40), "OP_OPEN");
        hashOpcode.put(new Integer(41), "OP_CLOSE");
        hashOpcode.put(new Integer(35), "OP_BACKREF");
        hashOpcode.put(new Integer(80), "OP_POSIXCLASS");
        hashOpcode.put(new Integer(60), "OP_OPEN_CLUSTER");
        hashOpcode.put(new Integer(62), "OP_CLOSE_CLUSTER");
    }
}


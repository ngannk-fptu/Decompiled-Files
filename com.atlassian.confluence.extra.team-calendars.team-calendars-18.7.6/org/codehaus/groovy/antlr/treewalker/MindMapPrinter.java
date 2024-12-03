/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import java.io.PrintStream;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.LineColumn;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

public class MindMapPrinter
extends VisitorAdapter {
    private final String[] tokenNames;
    private final PrintStream out;
    private int depth;
    private SourceBuffer sourceBuffer;

    public MindMapPrinter(PrintStream out, String[] tokenNames) {
        this.tokenNames = tokenNames;
        this.out = out;
    }

    public MindMapPrinter(PrintStream out, String[] tokenNames, SourceBuffer sourceBuffer) {
        this.tokenNames = tokenNames;
        this.out = out;
        this.sourceBuffer = sourceBuffer;
    }

    @Override
    public void setUp() {
        this.depth = 0;
        this.out.println("<map version='0.7.1'><node TEXT='AST'>");
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        if (visit == 1) {
            ++this.depth;
            String name = this.getName(t);
            String colour = MindMapPrinter.getColour(t);
            String folded = this.getFolded(t);
            this.out.print("<node TEXT='" + name + "' POSITION='right'" + colour + folded + ">");
        } else if (visit == 4) {
            this.out.println("</node>");
            --this.depth;
        }
    }

    @Override
    public void tearDown() {
        this.out.println("</node></map>");
    }

    private String getFolded(GroovySourceAST t) {
        if (this.depth > 2 && t.getNumberOfChildren() > 0) {
            switch (t.getType()) {
                case 8: 
                case 9: 
                case 28: {
                    return " FOLDED='true'";
                }
            }
        }
        if (t.getType() == 29) {
            return " FOLDED='true'";
        }
        return "";
    }

    private static String getColour(GroovySourceAST t) {
        String colour = "";
        String black = " COLOR=\"#000000\"";
        String cyan = " COLOR=\"#006699\"";
        String blue = " COLOR=\"#17178B\"";
        String green = " COLOR=\"#008000\"";
        switch (t.getType()) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 14: 
            case 17: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 30: 
            case 31: 
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: 
            case 64: 
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: 
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: 
            case 76: 
            case 80: 
            case 85: 
            case 86: 
            case 89: 
            case 90: 
            case 91: 
            case 96: 
            case 97: 
            case 100: 
            case 101: 
            case 102: 
            case 103: 
            case 113: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 127: 
            case 128: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 148: 
            case 149: 
            case 154: 
            case 155: 
            case 156: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 175: 
            case 176: 
            case 177: 
            case 178: 
            case 179: 
            case 180: 
            case 181: 
            case 184: 
            case 185: 
            case 186: 
            case 187: 
            case 188: 
            case 189: 
            case 190: 
            case 191: 
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 196: 
            case 197: 
            case 198: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
            case 206: 
            case 207: 
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 214: 
            case 218: 
            case 220: 
            case 221: 
            case 222: 
            case 223: 
            case 224: 
            case 225: 
            case 228: 
            case 229: 
            case 230: {
                colour = black;
                break;
            }
            case 88: 
            case 212: 
            case 213: {
                colour = green;
                break;
            }
            case 12: 
            case 13: 
            case 16: 
            case 18: 
            case 19: 
            case 29: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 92: 
            case 93: 
            case 94: 
            case 98: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: {
                colour = blue;
                break;
            }
            case 87: {
                colour = cyan;
                break;
            }
            default: {
                colour = black;
            }
        }
        if (black.equals(colour) && t.getNumberOfChildren() == 0) {
            colour = cyan;
        }
        return colour;
    }

    private String getName(GroovySourceAST t) {
        String name = this.tokenNames[t.getType()] + " <" + t.getType() + ">";
        if (!MindMapPrinter.escape(this.tokenNames[t.getType()]).equals(MindMapPrinter.escape(t.getText()))) {
            name = name + " : " + t.getText();
        }
        switch (t.getType()) {
            case 8: 
            case 9: {
                GroovySourceAST identNode = t.childOfType(87);
                if (identNode == null) break;
                name = name + " : " + identNode.getText() + "";
            }
        }
        name = MindMapPrinter.escape(name);
        if (this.sourceBuffer != null) {
            name = name + "&#xa;";
            name = name + t.getLine() + "," + t.getColumn() + " - " + t.getLineLast() + "," + t.getColumnLast();
            name = name + "&#xa;";
            name = name + MindMapPrinter.escape(this.sourceBuffer.getSnippet(new LineColumn(t.getLine(), t.getColumn()), new LineColumn(t.getLineLast(), t.getColumnLast())));
        }
        return name;
    }

    private static String escape(String name) {
        if (name == null) {
            return null;
        }
        if (name.length() > 200) {
            name = name.substring(0, 100) + " ..... " + name.substring(name.length() - 100);
        }
        name = name.replace('\"', ' ');
        name = name.replace('\'', ' ');
        name = name.replaceAll("&", "&amp;");
        name = name.replaceAll("<", "&lt;");
        name = name.replaceAll(">", "&gt;");
        name = name.trim();
        return name;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GenericXMLWriter
extends PrintWriter {
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private String lineSeparator;
    private int tab = 0;

    private static void appendEscapedChar(StringBuffer buffer, char c) {
        String replacement = GenericXMLWriter.getReplacement(c);
        if (replacement != null) {
            buffer.append('&');
            buffer.append(replacement);
            buffer.append(';');
        } else {
            buffer.append(c);
        }
    }

    private static String getEscaped(String s) {
        StringBuffer result = new StringBuffer(s.length() + 10);
        int i = 0;
        while (i < s.length()) {
            GenericXMLWriter.appendEscapedChar(result, s.charAt(i));
            ++i;
        }
        return result.toString();
    }

    private static String getReplacement(char c) {
        switch (c) {
            case '<': {
                return "lt";
            }
            case '>': {
                return "gt";
            }
            case '\"': {
                return "quot";
            }
            case '\'': {
                return "apos";
            }
            case '&': {
                return "amp";
            }
        }
        return null;
    }

    public GenericXMLWriter(OutputStream stream, String lineSeparator, boolean printXmlVersion) {
        this(new PrintWriter(stream), lineSeparator, printXmlVersion);
    }

    public GenericXMLWriter(Writer writer, String lineSeparator, boolean printXmlVersion) {
        super(writer);
        this.lineSeparator = lineSeparator;
        if (printXmlVersion) {
            this.print(XML_VERSION);
            this.print(this.lineSeparator);
        }
    }

    public void endTag(String name, boolean insertTab, boolean insertNewLine) {
        --this.tab;
        this.printTag(String.valueOf('/') + name, null, insertTab, insertNewLine, false);
    }

    public void printString(String string, boolean insertTab, boolean insertNewLine) {
        if (insertTab) {
            this.printTabulation();
        }
        this.print(string);
        if (insertNewLine) {
            this.print(this.lineSeparator);
        }
    }

    private void printTabulation() {
        int i = 0;
        while (i < this.tab) {
            this.print('\t');
            ++i;
        }
    }

    public void printTag(String name, HashMap parameters, boolean insertTab, boolean insertNewLine, boolean closeTag) {
        if (insertTab) {
            this.printTabulation();
        }
        this.print('<');
        this.print(name);
        if (parameters != null) {
            int length = parameters.size();
            Map.Entry[] entries = new Map.Entry[length];
            parameters.entrySet().toArray(entries);
            Arrays.sort(entries, new Comparator(){

                public int compare(Object o1, Object o2) {
                    Map.Entry entry1 = (Map.Entry)o1;
                    Map.Entry entry2 = (Map.Entry)o2;
                    return ((String)entry1.getKey()).compareTo((String)entry2.getKey());
                }
            });
            int i = 0;
            while (i < length) {
                this.print(' ');
                this.print(entries[i].getKey());
                this.print("=\"");
                this.print(GenericXMLWriter.getEscaped(String.valueOf(entries[i].getValue())));
                this.print('\"');
                ++i;
            }
        }
        if (closeTag) {
            this.print("/>");
        } else {
            this.print(">");
        }
        if (insertNewLine) {
            this.print(this.lineSeparator);
        }
        if (parameters != null && !closeTag) {
            ++this.tab;
        }
    }

    public void startTag(String name, boolean insertTab) {
        this.printTag(name, null, insertTab, true, false);
        ++this.tab;
    }
}


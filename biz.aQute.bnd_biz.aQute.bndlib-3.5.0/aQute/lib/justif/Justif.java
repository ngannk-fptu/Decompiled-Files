/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.justif;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;

public class Justif {
    final int[] tabs;
    final int width;
    StringBuilder sb = new StringBuilder();
    Formatter f = new Formatter(this.sb);

    public Justif(int width, int ... tabs) {
        int[] nArray;
        if (tabs == null || tabs.length == 0) {
            int[] nArray2 = new int[5];
            nArray2[0] = 30;
            nArray2[1] = 40;
            nArray2[2] = 50;
            nArray2[3] = 60;
            nArray = nArray2;
            nArray2[4] = 70;
        } else {
            nArray = tabs;
        }
        this.tabs = nArray;
        this.width = width == 0 ? 73 : width;
    }

    public Justif() {
        this(0, new int[0]);
    }

    public void wrap(StringBuilder sb) {
        ArrayList<Integer> indents = new ArrayList<Integer>();
        int indent = 0;
        int linelength = 0;
        int lastSpace = 0;
        int r = 0;
        boolean begin = true;
        block9: while (r < sb.length()) {
            int i;
            switch (sb.charAt(r++)) {
                case '\r': {
                    indents.clear();
                    sb.setCharAt(r - 1, '\n');
                }
                case '\n': {
                    indent = indents.isEmpty() ? 0 : (Integer)indents.remove(0);
                    linelength = 0;
                    begin = true;
                    lastSpace = 0;
                    continue block9;
                }
                case ' ': {
                    if (begin) {
                        ++indent;
                    } else {
                        while (r < sb.length() && sb.charAt(r) == ' ') {
                            sb.delete(r, r + 1);
                        }
                    }
                    lastSpace = r - 1;
                    ++linelength;
                    continue block9;
                }
                case '\t': {
                    sb.deleteCharAt(--r);
                    indents.add(indent);
                    if (r >= sb.length()) continue block9;
                    char digit = sb.charAt(r);
                    if (Character.isDigit(digit)) {
                        sb.deleteCharAt(r);
                        int column = digit - 48;
                        indent = column < this.tabs.length ? this.tabs[column] : column * 8;
                        int diff = indent - linelength;
                        if (diff <= 0) continue block9;
                        for (int i2 = 0; i2 < diff; ++i2) {
                            sb.insert(r, ' ');
                        }
                        r += diff;
                        linelength += diff;
                        continue block9;
                    }
                    System.err.println("missing digit after \t");
                    continue block9;
                }
                case '\f': {
                    sb.setCharAt(r - 1, '\n');
                    for (i = 0; i < indent; ++i) {
                        sb.insert(r, ' ');
                    }
                    r += indent;
                    while (r < sb.length() && sb.charAt(r) == ' ') {
                        sb.delete(r, r + 1);
                    }
                    linelength = 0;
                    lastSpace = 0;
                    continue block9;
                }
                case '$': {
                    char c;
                    if (sb.length() > r && ((c = sb.charAt(r)) == '-' || c == '_' || c == '\u2014')) {
                        sb.delete(r - 1, r);
                        begin = false;
                        ++linelength;
                        while (linelength < this.width - 1) {
                            sb.insert(r++, c);
                            ++linelength;
                        }
                        continue block9;
                    }
                }
                case '\u00a0': {
                    sb.setCharAt(r - 1, ' ');
                }
            }
            begin = false;
            if (++linelength <= this.width) continue;
            if (lastSpace == 0) {
                lastSpace = r - 1;
                sb.insert(lastSpace, ' ');
                ++r;
            }
            sb.setCharAt(lastSpace, '\n');
            linelength = r - lastSpace - 1;
            for (i = 0; i < indent; ++i) {
                sb.insert(lastSpace + 1, ' ');
                ++linelength;
            }
            r += indent;
            lastSpace = 0;
        }
    }

    public String wrap() {
        this.wrap(this.sb);
        return this.sb.toString();
    }

    public Formatter formatter() {
        return this.f;
    }

    public String toString() {
        this.wrap(this.sb);
        return this.sb.toString();
    }

    public void indent(int indent, String string) {
        for (int i = 0; i < string.length(); ++i) {
            int j;
            char c = string.charAt(i);
            if (i == 0) {
                for (j = 0; j < indent; ++j) {
                    this.sb.append(' ');
                }
                continue;
            }
            this.sb.append(c);
            if (c != '\n') continue;
            for (j = 0; j < indent; ++j) {
                this.sb.append(' ');
            }
        }
    }

    public void entry(String key, String separator, Object value) {
        this.sb.append(key);
        this.sb.append("\t1");
        this.sb.append(separator);
        this.sb.append("\t2");
        if (value instanceof Iterable) {
            Iterator it = ((Iterable)value).iterator();
            boolean hadone = false;
            String del = "";
            while (it.hasNext()) {
                this.sb.append(del).append(it.next() + "");
                this.sb.append("\r");
                hadone = true;
                del = "\t2";
            }
            if (!hadone) {
                this.sb.append("\r");
            }
        } else {
            this.sb.append(value + "");
            this.sb.append("\r");
        }
    }

    public void table(Map<String, Object> table, String separator) {
        for (Map.Entry<String, Object> e : table.entrySet()) {
            this.entry(e.getKey(), separator, e.getValue());
        }
    }

    public String toString(Object o) {
        String s = "" + o;
        if (s.length() > 50) {
            return s.replaceAll(",", ", \\\f");
        }
        return s;
    }
}


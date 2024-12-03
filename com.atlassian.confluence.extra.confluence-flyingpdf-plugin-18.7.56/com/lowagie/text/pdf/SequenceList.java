/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SequenceList {
    protected static final int COMMA = 1;
    protected static final int MINUS = 2;
    protected static final int NOT = 3;
    protected static final int TEXT = 4;
    protected static final int NUMBER = 5;
    protected static final int END = 6;
    protected static final char EOT = '\uffff';
    private static final int FIRST = 0;
    private static final int DIGIT = 1;
    private static final int OTHER = 2;
    private static final int DIGIT2 = 3;
    private static final String NOT_OTHER = "-,!0123456789";
    protected char[] text;
    protected int ptr = 0;
    protected int number;
    protected String other;
    protected int low;
    protected int high;
    protected boolean odd;
    protected boolean even;
    protected boolean inverse;

    protected SequenceList(String range) {
        this.text = range.toCharArray();
    }

    protected char nextChar() {
        char c;
        do {
            if (this.ptr < this.text.length) continue;
            return '\uffff';
        } while ((c = this.text[this.ptr++]) <= ' ');
        return c;
    }

    protected void putBack() {
        --this.ptr;
        if (this.ptr < 0) {
            this.ptr = 0;
        }
    }

    protected int getType() {
        StringBuilder buf = new StringBuilder();
        int state = 0;
        while (true) {
            char c;
            if ((c = this.nextChar()) == '\uffff') {
                if (state == 1) {
                    this.other = buf.toString();
                    this.number = Integer.parseInt(this.other);
                    return 5;
                }
                if (state == 2) {
                    this.other = buf.toString().toLowerCase();
                    return 4;
                }
                return 6;
            }
            switch (state) {
                case 0: {
                    switch (c) {
                        case '!': {
                            return 3;
                        }
                        case '-': {
                            return 2;
                        }
                        case ',': {
                            return 1;
                        }
                    }
                    buf.append(c);
                    if (c >= '0' && c <= '9') {
                        state = 1;
                        break;
                    }
                    state = 2;
                    break;
                }
                case 1: {
                    if (c >= '0' && c <= '9') {
                        buf.append(c);
                        break;
                    }
                    this.putBack();
                    this.other = buf.toString();
                    this.number = Integer.parseInt(this.other);
                    return 5;
                }
                case 2: {
                    if (NOT_OTHER.indexOf(c) < 0) {
                        buf.append(c);
                        break;
                    }
                    this.putBack();
                    this.other = buf.toString().toLowerCase();
                    return 4;
                }
            }
        }
    }

    private void otherProc() {
        if (this.other.equals("odd") || this.other.equals("o")) {
            this.odd = true;
            this.even = false;
        } else if (this.other.equals("even") || this.other.equals("e")) {
            this.odd = false;
            this.even = true;
        }
    }

    protected boolean getAttributes() {
        this.low = -1;
        this.high = -1;
        this.inverse = false;
        this.even = false;
        this.odd = false;
        int state = 2;
        while (true) {
            int type;
            if ((type = this.getType()) == 6 || type == 1) {
                if (state == 1) {
                    this.high = this.low;
                }
                return type == 6;
            }
            block0 : switch (state) {
                case 2: {
                    switch (type) {
                        case 3: {
                            this.inverse = true;
                            break block0;
                        }
                        case 2: {
                            state = 3;
                            break block0;
                        }
                    }
                    if (type == 5) {
                        this.low = this.number;
                        state = 1;
                        break;
                    }
                    this.otherProc();
                    break;
                }
                case 1: {
                    switch (type) {
                        case 3: {
                            this.inverse = true;
                            state = 2;
                            this.high = this.low;
                            break block0;
                        }
                        case 2: {
                            state = 3;
                            break block0;
                        }
                    }
                    this.high = this.low;
                    state = 2;
                    this.otherProc();
                    break;
                }
                case 3: {
                    switch (type) {
                        case 3: {
                            this.inverse = true;
                            state = 2;
                            break block0;
                        }
                        case 2: {
                            break block0;
                        }
                        case 5: {
                            this.high = this.number;
                            state = 2;
                            break block0;
                        }
                    }
                    state = 2;
                    this.otherProc();
                }
            }
        }
    }

    public static List<Integer> expand(String ranges, int maxNumber) {
        SequenceList parse = new SequenceList(ranges);
        LinkedList<Integer> list = new LinkedList<Integer>();
        boolean sair = false;
        while (!sair) {
            sair = parse.getAttributes();
            if (parse.low == -1 && parse.high == -1 && !parse.even && !parse.odd) continue;
            if (parse.low < 1) {
                parse.low = 1;
            }
            if (parse.high < 1 || parse.high > maxNumber) {
                parse.high = maxNumber;
            }
            if (parse.low > maxNumber) {
                parse.low = maxNumber;
            }
            int inc = 1;
            if (parse.inverse) {
                if (parse.low > parse.high) {
                    int t = parse.low;
                    parse.low = parse.high;
                    parse.high = t;
                }
                ListIterator it = list.listIterator();
                while (it.hasNext()) {
                    int n = (Integer)it.next();
                    if (parse.even && (n & 1) == 1 || parse.odd && (n & 1) == 0 || n < parse.low || n > parse.high) continue;
                    it.remove();
                }
                continue;
            }
            if (parse.low > parse.high) {
                inc = -1;
                if (parse.odd || parse.even) {
                    --inc;
                    parse.low = parse.even ? (parse.low &= 0xFFFFFFFE) : parse.low - ((parse.low & 1) == 1 ? 0 : 1);
                }
                for (int k = parse.low; k >= parse.high; k += inc) {
                    list.add(k);
                }
                continue;
            }
            if (parse.odd || parse.even) {
                ++inc;
                parse.low = parse.odd ? (parse.low |= 1) : parse.low + ((parse.low & 1) == 1 ? 1 : 0);
            }
            for (int k = parse.low; k <= parse.high; k += inc) {
                list.add(k);
            }
        }
        return list;
    }
}


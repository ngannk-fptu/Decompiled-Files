/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import java.util.ArrayList;
import java.util.Arrays;

class Mf2Parser {
    private int b0;
    private int e0;
    private int l1;
    private int b1;
    private int e1;
    private EventHandler eventHandler = null;
    private CharSequence input = null;
    private int size = 0;
    private int begin = 0;
    private int end = 0;
    private static final int[] MAP0 = new int[]{24, 24, 24, 24, 24, 24, 24, 24, 24, 1, 1, 24, 24, 1, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 1, 24, 24, 24, 2, 24, 24, 24, 3, 4, 5, 6, 24, 7, 8, 24, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9, 24, 24, 10, 24, 24, 24, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 24, 12, 24, 24, 11, 24, 13, 11, 14, 11, 15, 11, 11, 16, 11, 11, 11, 17, 18, 19, 11, 11, 11, 11, 11, 20, 11, 11, 21, 11, 11, 11, 22, 24, 23, 24, 24};
    private static final int[] MAP1 = new int[]{108, 124, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 156, 181, 181, 181, 181, 181, 214, 215, 213, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 383, 330, 396, 353, 291, 262, 247, 308, 330, 330, 330, 322, 292, 284, 292, 284, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 347, 347, 347, 347, 347, 347, 347, 277, 292, 292, 292, 292, 292, 292, 292, 292, 369, 330, 330, 331, 329, 330, 330, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 291, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 292, 330, 24, 13, 11, 14, 11, 15, 11, 11, 16, 11, 11, 11, 17, 18, 19, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 24, 12, 24, 24, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 24, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 20, 11, 11, 21, 11, 11, 11, 22, 24, 23, 24, 24, 24, 24, 24, 24, 24, 8, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9, 24, 24, 10, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 11, 11, 24, 24, 24, 24, 24, 24, 24, 24, 24, 1, 1, 24, 24, 1, 24, 24, 24, 2, 24, 24, 24, 3, 4, 5, 6, 24, 7, 8, 24};
    private static final int[] MAP2 = new int[]{55296, 63744, 64976, 65008, 65534, 65536, 983040, 63743, 64975, 65007, 65533, 65535, 983039, 0x10FFFF, 24, 11, 24, 11, 24, 11, 24};
    private static final int[] INITIAL = new int[]{1, 2, 3, 4, 133, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private static final int[] TRANSITION = new int[]{237, 237, 237, 237, 237, 237, 237, 237, 200, 208, 455, 237, 237, 237, 237, 237, 236, 230, 455, 237, 237, 237, 237, 237, 237, 245, 376, 382, 237, 237, 237, 237, 237, 380, 314, 382, 237, 237, 237, 237, 237, 263, 455, 237, 237, 237, 237, 237, 237, 295, 455, 237, 237, 237, 237, 237, 237, 322, 287, 281, 252, 237, 237, 237, 237, 344, 287, 281, 252, 237, 237, 237, 255, 358, 455, 237, 237, 237, 237, 237, 417, 380, 455, 237, 237, 237, 237, 237, 419, 390, 215, 329, 252, 237, 237, 237, 237, 398, 275, 382, 237, 237, 237, 237, 419, 390, 215, 410, 252, 237, 237, 237, 419, 390, 215, 329, 309, 237, 237, 237, 419, 390, 222, 365, 252, 237, 237, 237, 419, 390, 427, 329, 302, 237, 237, 237, 419, 435, 215, 329, 252, 237, 237, 237, 419, 443, 215, 329, 252, 237, 237, 237, 419, 390, 215, 329, 372, 237, 237, 237, 419, 390, 215, 336, 451, 237, 237, 237, 402, 390, 215, 329, 252, 237, 237, 237, 350, 463, 269, 237, 237, 237, 237, 237, 474, 471, 269, 237, 237, 237, 237, 237, 237, 380, 455, 237, 237, 237, 237, 237, 192, 192, 192, 192, 192, 192, 192, 192, 277, 192, 192, 192, 192, 192, 192, 0, 414, 595, 0, 277, 22, 663, 0, 414, 595, 0, 277, 22, 663, 32, 277, 16, 16, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 277, 22, 22, 22, 0, 22, 22, 0, 482, 547, 0, 0, 0, 0, 0, 18, 0, 0, 277, 0, 0, 768, 0, 768, 0, 0, 0, 277, 0, 22, 0, 0, 0, 277, 20, 31, 0, 0, 0, 348, 0, 414, 0, 0, 595, 0, 277, 22, 663, 0, 277, 0, 0, 0, 0, 0, 26, 0, 482, 547, 0, 0, 960, 0, 0, 482, 547, 0, 38, 0, 0, 0, 0, 277, 704, 0, 0, 277, 0, 663, 663, 0, 663, 27, 0, 482, 547, 348, 0, 414, 0, 0, 482, 547, 348, 0, 414, 0, 896, 277, 0, 663, 663, 0, 663, 0, 0, 1088, 0, 0, 0, 0, 1088, 277, 18, 0, 0, 0, 0, 18, 0, 482, 547, 348, 36, 414, 0, 0, 482, 547, 1024, 0, 0, 0, 0, 277, 0, 0, 0, 0, 0, 0, 0, 22, 0, 277, 0, 663, 663, 0, 663, 0, 348, 20, 0, 0, 0, 0, 0, 0, 0, 17, 0, 595, 17, 33, 482, 547, 348, 0, 414, 0, 0, 832, 0, 0, 0, 0, 0, 0, 595, 0, 29, 414, 595, 0, 277, 22, 663, 0, 277, 0, 663, 663, 24, 663, 0, 348, 277, 0, 663, 663, 25, 663, 0, 348, 37, 482, 547, 0, 0, 0, 0, 0, 277, 22, 0, 0, 1088, 0, 0, 0, 1088, 1088, 0, 0, 1152, 0, 0, 0, 0, 0, 1152, 0, 1152, 1152, 0};
    private static final int[] EXPECTED = new int[]{20, 4100, 65540, 131076, 32772, 131108, 131332, 98308, 196616, 1076, 1556, 3588, 90116, 69124, 132340, 16, 32768, 32, 256, 8, 8, 1024, 512, 8192, 16384, 64, 128, 16, 32768, 32, 1024, 8192, 16384, 64, 128, 32768, 16384, 16384};
    private static final String[] TOKEN = new String[]{"(0)", "END", "WhiteSpace", "Text", "Variable", "Function", "MarkupStart", "MarkupEnd", "Name", "Nmtoken", "Literal", "'*'", "'='", "'let'", "'match'", "'when'", "'{'", "'}'"};

    public Mf2Parser(CharSequence string, EventHandler t) {
        this.initialize(string, t);
    }

    public void initialize(CharSequence source, EventHandler parsingEventHandler) {
        this.eventHandler = parsingEventHandler;
        this.input = source;
        this.size = source.length();
        this.reset(0, 0, 0);
    }

    public CharSequence getInput() {
        return this.input;
    }

    public int getTokenOffset() {
        return this.b0;
    }

    public int getTokenEnd() {
        return this.e0;
    }

    public final void reset(int l, int b, int e) {
        this.b0 = b;
        this.e0 = b;
        this.l1 = l;
        this.b1 = b;
        this.e1 = e;
        this.end = e;
        this.eventHandler.reset(this.input);
    }

    public void reset() {
        this.reset(0, 0, 0);
    }

    public static String getOffendingToken(ParseException e) {
        return e.getOffending() < 0 ? null : TOKEN[e.getOffending()];
    }

    public static String[] getExpectedTokenSet(ParseException e) {
        String[] expected = e.getExpected() >= 0 ? new String[]{TOKEN[e.getExpected()]} : Mf2Parser.getTokenSet(-e.getState());
        return expected;
    }

    public String getErrorMessage(ParseException e) {
        String message = e.getMessage();
        Object[] tokenSet = Mf2Parser.getExpectedTokenSet(e);
        String found = Mf2Parser.getOffendingToken(e);
        int size = e.getEnd() - e.getBegin();
        message = message + (found == null ? "" : ", found " + found) + "\nwhile expecting " + (String)(tokenSet.length == 1 ? tokenSet[0] : Arrays.toString(tokenSet)) + "\n" + (size == 0 || found != null ? "" : "after successfully scanning " + size + " characters beginning ");
        String prefix = this.input.subSequence(0, e.getBegin()).toString();
        int line = prefix.replaceAll("[^\n]", "").length() + 1;
        int column = prefix.length() - prefix.lastIndexOf(10);
        return message + "at line " + line + ", column " + column + ":\n..." + this.input.subSequence(e.getBegin(), Math.min(this.input.length(), e.getBegin() + 64)) + "...";
    }

    public void parse_Message() {
        this.eventHandler.startNonterminal("Message", this.e0);
        while (true) {
            this.lookahead1W(12);
            if (this.l1 != 13) break;
            this.whitespace();
            this.parse_Declaration();
        }
        switch (this.l1) {
            case 16: {
                this.whitespace();
                this.parse_Pattern();
                break;
            }
            default: {
                this.whitespace();
                this.parse_Selector();
                do {
                    this.whitespace();
                    this.parse_Variant();
                    this.lookahead1W(4);
                } while (this.l1 == 15);
                break;
            }
        }
        this.eventHandler.endNonterminal("Message", this.e0);
    }

    private void parse_Declaration() {
        this.eventHandler.startNonterminal("Declaration", this.e0);
        this.consume(13);
        this.lookahead1W(0);
        this.consume(4);
        this.lookahead1W(1);
        this.consume(12);
        this.lookahead1W(2);
        this.consume(16);
        this.lookahead1W(9);
        this.whitespace();
        this.parse_Expression();
        this.consume(17);
        this.eventHandler.endNonterminal("Declaration", this.e0);
    }

    private void parse_Selector() {
        this.eventHandler.startNonterminal("Selector", this.e0);
        this.consume(14);
        do {
            this.lookahead1W(2);
            this.consume(16);
            this.lookahead1W(9);
            this.whitespace();
            this.parse_Expression();
            this.consume(17);
            this.lookahead1W(7);
        } while (this.l1 == 16);
        this.eventHandler.endNonterminal("Selector", this.e0);
    }

    private void parse_Variant() {
        this.eventHandler.startNonterminal("Variant", this.e0);
        this.consume(15);
        do {
            this.lookahead1W(11);
            this.whitespace();
            this.parse_VariantKey();
            this.lookahead1W(13);
        } while (this.l1 != 16);
        this.whitespace();
        this.parse_Pattern();
        this.eventHandler.endNonterminal("Variant", this.e0);
    }

    private void parse_VariantKey() {
        this.eventHandler.startNonterminal("VariantKey", this.e0);
        switch (this.l1) {
            case 10: {
                this.consume(10);
                break;
            }
            case 9: {
                this.consume(9);
                break;
            }
            default: {
                this.consume(11);
            }
        }
        this.eventHandler.endNonterminal("VariantKey", this.e0);
    }

    private void parse_Pattern() {
        this.eventHandler.startNonterminal("Pattern", this.e0);
        this.consume(16);
        block3: while (true) {
            this.lookahead1(8);
            if (this.l1 == 17) break;
            switch (this.l1) {
                case 3: {
                    this.consume(3);
                    continue block3;
                }
            }
            this.parse_Placeholder();
        }
        this.consume(17);
        this.eventHandler.endNonterminal("Pattern", this.e0);
    }

    private void parse_Placeholder() {
        this.eventHandler.startNonterminal("Placeholder", this.e0);
        this.consume(16);
        this.lookahead1W(14);
        if (this.l1 != 17) {
            switch (this.l1) {
                case 6: {
                    this.whitespace();
                    this.parse_Markup();
                    break;
                }
                case 7: {
                    this.consume(7);
                    break;
                }
                default: {
                    this.whitespace();
                    this.parse_Expression();
                }
            }
        }
        this.lookahead1W(3);
        this.consume(17);
        this.eventHandler.endNonterminal("Placeholder", this.e0);
    }

    private void parse_Expression() {
        this.eventHandler.startNonterminal("Expression", this.e0);
        switch (this.l1) {
            case 5: {
                this.parse_Annotation();
                break;
            }
            default: {
                this.parse_Operand();
                this.lookahead1W(5);
                if (this.l1 != 5) break;
                this.whitespace();
                this.parse_Annotation();
            }
        }
        this.eventHandler.endNonterminal("Expression", this.e0);
    }

    private void parse_Operand() {
        this.eventHandler.startNonterminal("Operand", this.e0);
        switch (this.l1) {
            case 10: {
                this.consume(10);
                break;
            }
            default: {
                this.consume(4);
            }
        }
        this.eventHandler.endNonterminal("Operand", this.e0);
    }

    private void parse_Annotation() {
        this.eventHandler.startNonterminal("Annotation", this.e0);
        this.consume(5);
        while (true) {
            this.lookahead1W(6);
            if (this.l1 != 8) break;
            this.whitespace();
            this.parse_Option();
        }
        this.eventHandler.endNonterminal("Annotation", this.e0);
    }

    private void parse_Option() {
        this.eventHandler.startNonterminal("Option", this.e0);
        this.consume(8);
        this.lookahead1W(1);
        this.consume(12);
        this.lookahead1W(10);
        switch (this.l1) {
            case 10: {
                this.consume(10);
                break;
            }
            case 9: {
                this.consume(9);
                break;
            }
            default: {
                this.consume(4);
            }
        }
        this.eventHandler.endNonterminal("Option", this.e0);
    }

    private void parse_Markup() {
        this.eventHandler.startNonterminal("Markup", this.e0);
        this.consume(6);
        while (true) {
            this.lookahead1W(6);
            if (this.l1 != 8) break;
            this.whitespace();
            this.parse_Option();
        }
        this.eventHandler.endNonterminal("Markup", this.e0);
    }

    private void consume(int t) {
        if (this.l1 == t) {
            this.whitespace();
            this.eventHandler.terminal(TOKEN[this.l1], this.b1, this.e1);
            this.b0 = this.b1;
            this.e0 = this.e1;
            this.l1 = 0;
        } else {
            this.error(this.b1, this.e1, 0, this.l1, t);
        }
    }

    private void whitespace() {
        if (this.e0 != this.b1) {
            this.eventHandler.whitespace(this.e0, this.b1);
            this.e0 = this.b1;
        }
    }

    private int matchW(int tokenSetId) {
        int code;
        while ((code = this.match(tokenSetId)) == 2) {
        }
        return code;
    }

    private void lookahead1W(int tokenSetId) {
        if (this.l1 == 0) {
            this.l1 = this.matchW(tokenSetId);
            this.b1 = this.begin;
            this.e1 = this.end;
        }
    }

    private void lookahead1(int tokenSetId) {
        if (this.l1 == 0) {
            this.l1 = this.match(tokenSetId);
            this.b1 = this.begin;
            this.e1 = this.end;
        }
    }

    private int error(int b, int e, int s, int l, int t) {
        throw new ParseException(b, e, s, l, t);
    }

    private int match(int tokenSetId) {
        this.begin = this.end;
        int current = this.end;
        int result = INITIAL[tokenSetId];
        int state = 0;
        int code = result & 0x3F;
        while (code != 0) {
            int c1;
            int charclass;
            int c0 = current < this.size ? this.input.charAt(current) : 0;
            ++current;
            if (c0 < 128) {
                charclass = MAP0[c0];
            } else if (c0 < 55296) {
                c1 = c0 >> 4;
                charclass = MAP1[(c0 & 0xF) + MAP1[(c1 & 0x1F) + MAP1[c1 >> 5]]];
            } else {
                if (c0 < 56320) {
                    int n = c1 = current < this.size ? (int)this.input.charAt(current) : 0;
                    if (c1 >= 56320 && c1 < 57344) {
                        ++current;
                        c0 = ((c0 & 0x3FF) << 10) + (c1 & 0x3FF) + 65536;
                    }
                }
                int lo = 0;
                int hi = 6;
                int m = 3;
                while (true) {
                    if (MAP2[m] > c0) {
                        hi = m - 1;
                    } else if (MAP2[7 + m] < c0) {
                        lo = m + 1;
                    } else {
                        charclass = MAP2[14 + m];
                        break;
                    }
                    if (lo > hi) {
                        charclass = 0;
                        break;
                    }
                    m = hi + lo >> 1;
                }
            }
            state = code;
            int i0 = (charclass << 6) + code - 1;
            if ((code = TRANSITION[(i0 & 7) + TRANSITION[i0 >> 3]]) <= 63) continue;
            result = code;
            code &= 0x3F;
            this.end = current;
        }
        if ((result >>= 6) == 0) {
            char c1;
            this.end = current - 1;
            char c = c1 = this.end < this.size ? this.input.charAt(this.end) : (char)'\u0000';
            if (c1 >= '\udc00' && c1 < '\ue000') {
                --this.end;
            }
            return this.error(this.begin, this.end, state, -1, -1);
        }
        if (this.end > this.size) {
            this.end = this.size;
        }
        return (result & 0x1F) - 1;
    }

    private static String[] getTokenSet(int tokenSetId) {
        ArrayList<String> expected = new ArrayList<String>();
        int s = tokenSetId < 0 ? -tokenSetId : INITIAL[tokenSetId] & 0x3F;
        for (int i = 0; i < 18; i += 32) {
            int j = i;
            int i0 = (i >> 5) * 38 + s - 1;
            int f = EXPECTED[i0];
            while (f != 0) {
                if ((f & 1) != 0) {
                    expected.add(TOKEN[j]);
                }
                f >>>= 1;
                ++j;
            }
        }
        return expected.toArray(new String[0]);
    }

    public static class Nonterminal
    extends Symbol {
        public Symbol[] children;

        public Nonterminal(String name, int begin, int end, Symbol[] children) {
            super(name, begin, end);
            this.children = children;
        }

        @Override
        public void send(EventHandler e) {
            e.startNonterminal(this.name, this.begin);
            int pos = this.begin;
            for (Symbol c : this.children) {
                if (pos < c.begin) {
                    e.whitespace(pos, c.begin);
                }
                c.send(e);
                pos = c.end;
            }
            if (pos < this.end) {
                e.whitespace(pos, this.end);
            }
            e.endNonterminal(this.name, this.end);
        }
    }

    public static class Terminal
    extends Symbol {
        public Terminal(String name, int begin, int end) {
            super(name, begin, end);
        }

        @Override
        public void send(EventHandler e) {
            e.terminal(this.name, this.begin, this.end);
        }
    }

    public static abstract class Symbol {
        public String name;
        public int begin;
        public int end;

        protected Symbol(String name, int begin, int end) {
            this.name = name;
            this.begin = begin;
            this.end = end;
        }

        public abstract void send(EventHandler var1);
    }

    public static class TopDownTreeBuilder
    implements EventHandler {
        private CharSequence input = null;
        public Nonterminal[] stack = new Nonterminal[64];
        private int top = -1;

        @Override
        public void reset(CharSequence input) {
            this.input = input;
            this.top = -1;
        }

        @Override
        public void startNonterminal(String name, int begin) {
            Nonterminal nonterminal = new Nonterminal(name, begin, begin, new Symbol[0]);
            if (this.top >= 0) {
                this.addChild(nonterminal);
            }
            if (++this.top >= this.stack.length) {
                this.stack = Arrays.copyOf(this.stack, this.stack.length << 1);
            }
            this.stack[this.top] = nonterminal;
        }

        @Override
        public void endNonterminal(String name, int end) {
            this.stack[this.top].end = end;
            if (this.top > 0) {
                --this.top;
            }
        }

        @Override
        public void terminal(String name, int begin, int end) {
            this.addChild(new Terminal(name, begin, end));
        }

        @Override
        public void whitespace(int begin, int end) {
        }

        private void addChild(Symbol s) {
            Nonterminal current = this.stack[this.top];
            current.children = Arrays.copyOf(current.children, current.children.length + 1);
            current.children[current.children.length - 1] = s;
        }

        public void serialize(EventHandler e) {
            e.reset(this.input);
            this.stack[0].send(e);
        }
    }

    public static interface EventHandler {
        public void reset(CharSequence var1);

        public void startNonterminal(String var1, int var2);

        public void endNonterminal(String var1, int var2);

        public void terminal(String var1, int var2, int var3);

        public void whitespace(int var1, int var2);
    }

    static class ParseException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private int begin;
        private int end;
        private int offending;
        private int expected;
        private int state;

        public ParseException(int b, int e, int s, int o, int x) {
            this.begin = b;
            this.end = e;
            this.state = s;
            this.offending = o;
            this.expected = x;
        }

        @Override
        public String getMessage() {
            return this.offending < 0 ? "lexical analysis failed" : "syntax error";
        }

        public void serialize(EventHandler eventHandler) {
        }

        public int getBegin() {
            return this.begin;
        }

        public int getEnd() {
            return this.end;
        }

        public int getState() {
            return this.state;
        }

        public int getOffending() {
            return this.offending;
        }

        public int getExpected() {
            return this.expected;
        }

        public boolean isAmbiguousInput() {
            return false;
        }
    }
}


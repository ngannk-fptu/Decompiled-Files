/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CharFormatter;

class CppCharFormatter
implements CharFormatter {
    CppCharFormatter() {
    }

    public String escapeChar(int n, boolean bl) {
        switch (n) {
            case 10: {
                return "\\n";
            }
            case 9: {
                return "\\t";
            }
            case 13: {
                return "\\r";
            }
            case 92: {
                return "\\\\";
            }
            case 39: {
                return "\\'";
            }
            case 34: {
                return "\\\"";
            }
        }
        if (n < 32 || n > 126) {
            if (n > 255) {
                String string = Integer.toString(n, 16);
                while (string.length() < 4) {
                    string = '0' + string;
                }
                return "\\u" + string;
            }
            return "\\" + Integer.toString(n, 8);
        }
        return String.valueOf((char)n);
    }

    public String escapeString(String string) {
        String string2 = new String();
        for (int i = 0; i < string.length(); ++i) {
            string2 = string2 + this.escapeChar(string.charAt(i), false);
        }
        return string2;
    }

    public String literalChar(int n) {
        String string = "0x" + Integer.toString(n, 16);
        if (n >= 0 && n <= 126) {
            string = string + " /* '" + this.escapeChar(n, true) + "' */ ";
        }
        return string;
    }

    public String literalString(String string) {
        return "\"" + this.escapeString(string) + "\"";
    }
}


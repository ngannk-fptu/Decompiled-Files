/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CharFormatter;

class CSharpCharFormatter
implements CharFormatter {
    CSharpCharFormatter() {
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
                return bl ? "\\'" : "'";
            }
            case 34: {
                return bl ? "\"" : "\\\"";
            }
        }
        if (n < 32 || n > 126) {
            if (0 <= n && n <= 15) {
                return "\\u000" + Integer.toString(n, 16);
            }
            if (16 <= n && n <= 255) {
                return "\\u00" + Integer.toString(n, 16);
            }
            if (256 <= n && n <= 4095) {
                return "\\u0" + Integer.toString(n, 16);
            }
            return "\\u" + Integer.toString(n, 16);
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
        return "'" + this.escapeChar(n, true) + "'";
    }

    public String literalString(String string) {
        return "@\"\"\"" + this.escapeString(string) + "\"\"\"";
    }
}


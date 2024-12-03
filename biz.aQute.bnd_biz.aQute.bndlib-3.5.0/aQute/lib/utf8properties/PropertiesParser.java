/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.utf8properties;

import aQute.lib.hex.Hex;
import aQute.lib.strings.Strings;
import aQute.service.reporter.Reporter;
import java.util.Properties;

final class PropertiesParser {
    private final char[] source;
    private final int length;
    private final Reporter reporter;
    private final String file;
    private static final char MIN_DELIMETER = '\t';
    private static final char MAX_DELIMETER = '\\';
    private static final byte[] INFO = new byte[93];
    private static final byte WS = 1;
    private static final byte KEY = 2;
    private static final byte LINE = 4;
    private static final byte NOKEY = 8;
    private int n = 0;
    private int line = 0;
    private int pos = -1;
    private int marker = 0;
    private char current;
    private Properties properties;
    private boolean validKey;
    private boolean continuation = true;

    PropertiesParser(String source, String file, Reporter reporter, Properties properties) {
        this.source = source.toCharArray();
        this.file = file;
        this.reporter = reporter;
        this.length = this.source.length;
        this.properties = properties;
    }

    boolean hasNext() {
        return this.n < this.length;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    char next() {
        if (this.n >= this.length) {
            this.current = '\n';
            return '\n';
        }
        this.current = this.source[this.n++];
        try {
            switch (this.current) {
                case '\\': {
                    char p;
                    if (this.continuation && ((p = this.peek()) == '\r' || p == '\n')) {
                        this.next();
                        this.next();
                        this.skipWhitespace();
                    }
                    char c = this.current;
                    return c;
                }
                case '\r': {
                    this.current = (char)10;
                    if (this.peek() == '\n') {
                        ++this.n;
                    }
                    ++this.line;
                    this.pos = -1;
                    char c = this.current;
                    return c;
                }
                case '\n': {
                    if (this.peek() == '\r') {
                        ++this.n;
                    }
                    ++this.line;
                    this.pos = -1;
                    char c = this.current;
                    return c;
                }
                case '\t': 
                case '\f': {
                    char c = this.current;
                    return c;
                }
            }
            if (this.current < ' ') {
                this.error("Invalid character in properties: %x at pos %s", this.current, this.pos);
                this.current = '?';
                char c = '?';
                return c;
            }
            char c = this.current;
            return c;
        }
        finally {
            ++this.pos;
        }
    }

    void skip(byte delimeters) {
        while (this.isIn(delimeters)) {
            this.next();
        }
    }

    char peek() {
        if (this.hasNext()) {
            return this.source[this.n];
        }
        return '\n';
    }

    void parse() {
        while (this.hasNext()) {
            this.marker = this.n;
            this.next();
            this.skipWhitespace();
            if (this.isEmptyOrComment(this.current)) {
                this.skipLine();
                continue;
            }
            this.validKey = true;
            String key = this.key();
            if (!this.validKey) {
                this.error("Invalid property key: `%s`", key);
            }
            this.skipWhitespace();
            if (this.current == ':' || this.current == '=') {
                this.next();
                this.skipWhitespace();
                if (this.current == '\n') {
                    this.properties.put(key, "");
                    continue;
                }
            }
            if (this.current == '\n') {
                this.error("No value specified for key: %s. An empty value should be specified as '%<s:' or '%<s='", key);
                this.properties.put(key, "");
                continue;
            }
            String value = this.token((byte)4, key.startsWith("-"));
            this.properties.put(key, value);
            assert (this.current == '\n');
        }
        int start = this.n;
    }

    private void skipWhitespace() {
        this.skip((byte)1);
    }

    public boolean isEmptyOrComment(char c) {
        return c == '\n' || c == '#' || c == '!';
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void skipLine() {
        this.continuation = false;
        try {
            while (!this.isIn((byte)4)) {
                this.next();
            }
        }
        finally {
            this.continuation = true;
        }
    }

    private final String token(byte delimeters, boolean check) {
        StringBuilder sb = new StringBuilder();
        char quote = '\u0000';
        boolean expectDelimeter = false;
        while (!this.isIn(delimeters)) {
            char tmp = this.current;
            if (tmp == '\\') {
                tmp = this.backslash();
                if (tmp == '\u0000') {
                    break;
                }
            } else if (check) {
                switch (this.current) {
                    case '\\': {
                        break;
                    }
                    case '\"': 
                    case '\'': {
                        if (quote == '\u0000') {
                            if (expectDelimeter) {
                                this.error("Found a quote '%s' while expecting a delimeter. You should quote the whole values, you can use both single and double quotes", Character.valueOf(tmp));
                                expectDelimeter = false;
                            }
                            quote = tmp;
                            break;
                        }
                        if (quote != tmp) break;
                        quote = '\u0000';
                        expectDelimeter = true;
                        break;
                    }
                    case '\t': 
                    case '\f': 
                    case ' ': {
                        break;
                    }
                    case ',': 
                    case ':': 
                    case ';': 
                    case '=': {
                        expectDelimeter = false;
                        break;
                    }
                    default: {
                        if (!expectDelimeter) break;
                        this.error("Expected a delimeter, like comma or semicolon, after a quoted string but found '%s'", Character.valueOf(tmp));
                        expectDelimeter = false;
                    }
                }
            }
            sb.append(tmp);
            this.next();
        }
        return sb.toString();
    }

    private final String key() {
        StringBuilder sb = new StringBuilder();
        while (!this.isIn((byte)2)) {
            char tmp;
            if (this.isIn((byte)8)) {
                this.validKey = false;
            }
            if ((tmp = this.current) == '\\' && (tmp = this.backslash()) == '\u0000') break;
            sb.append(tmp);
            this.next();
        }
        return sb.toString();
    }

    private final boolean isIn(byte delimeters) {
        if (this.current < '\t' || this.current > '\\') {
            return false;
        }
        return (INFO[this.current] & delimeters) != 0;
    }

    private final char backslash() {
        char c = this.next();
        switch (c) {
            case '\n': {
                return '\u0000';
            }
            case 'u': {
                StringBuilder sb = new StringBuilder();
                c = '\u0000';
                for (int i = 0; i < 4; ++i) {
                    sb.append(this.next());
                }
                String unicode = sb.toString();
                if (!Hex.isHex(unicode)) {
                    this.error("Invalid unicode string \\u%s", sb);
                    return '?';
                }
                return (char)Integer.parseInt(unicode, 16);
            }
            case ':': 
            case '=': {
                return c;
            }
            case 't': {
                return '\t';
            }
            case 'f': {
                return '\f';
            }
            case 'r': {
                return '\r';
            }
            case 'n': {
                return '\n';
            }
            case '\\': {
                return '\\';
            }
            case '\t': 
            case '\f': 
            case ' ': {
                this.error("Found \\<whitespace>. This is allowed in a properties file but not in bnd to prevent mistakes", new Object[0]);
                return c;
            }
        }
        return c;
    }

    private void error(String msg, Object ... args) {
        if (this.reporter != null) {
            int line = this.line;
            String context = this.context();
            Reporter.SetLocation loc = this.reporter.error("%s: <<%s>>", Strings.format(msg, args), context);
            loc.line(line);
            loc.context(context);
            if (this.file != null) {
                loc.file(this.file);
            }
            loc.length(context.length());
        }
    }

    private String context() {
        int loc;
        for (loc = this.n; loc < this.length && this.source[loc] != '\n'; ++loc) {
        }
        return new String(this.source, this.marker, loc - this.marker);
    }

    static {
        PropertiesParser.INFO[9] = 3;
        PropertiesParser.INFO[10] = 6;
        PropertiesParser.INFO[12] = 3;
        PropertiesParser.INFO[32] = 3;
        PropertiesParser.INFO[44] = 8;
        PropertiesParser.INFO[59] = 8;
        PropertiesParser.INFO[33] = 8;
        PropertiesParser.INFO[39] = 8;
        PropertiesParser.INFO[34] = 8;
        PropertiesParser.INFO[35] = 8;
        PropertiesParser.INFO[40] = 8;
        PropertiesParser.INFO[41] = 8;
        PropertiesParser.INFO[58] = 2;
        PropertiesParser.INFO[61] = 2;
        PropertiesParser.INFO[92] = 8;
    }
}


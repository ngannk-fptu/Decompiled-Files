/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class StringHandler
extends Handler {
    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException {
        StringHandler.string(app, object.toString());
    }

    static void string(Appendable app, String s) throws IOException {
        app.append('\"');
        block9: for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\"': {
                    app.append("\\\"");
                    continue block9;
                }
                case '\\': {
                    app.append("\\\\");
                    continue block9;
                }
                case '\b': {
                    app.append("\\b");
                    continue block9;
                }
                case '\f': {
                    app.append("\\f");
                    continue block9;
                }
                case '\n': {
                    app.append("\\n");
                    continue block9;
                }
                case '\r': {
                    app.append("\\r");
                    continue block9;
                }
                case '\t': {
                    app.append("\\t");
                    continue block9;
                }
                default: {
                    if (Character.isISOControl(c)) {
                        app.append("\\u");
                        app.append("0123456789ABCDEF".charAt(0xF & c >> 12));
                        app.append("0123456789ABCDEF".charAt(0xF & c >> 8));
                        app.append("0123456789ABCDEF".charAt(0xF & c >> 4));
                        app.append("0123456789ABCDEF".charAt(0xF & c >> 0));
                        continue block9;
                    }
                    app.append(c);
                }
            }
        }
        app.append('\"');
    }

    @Override
    public Object decode(Decoder dec, String s) throws Exception {
        return s;
    }

    @Override
    public Object decode(Decoder dec, Number s) {
        return s.toString();
    }

    @Override
    public Object decode(Decoder dec, boolean s) {
        return Boolean.toString(s);
    }

    @Override
    public Object decode(Decoder dec) {
        return null;
    }

    @Override
    public Object decodeObject(Decoder r) throws Exception {
        return this.collect(r, '}');
    }

    @Override
    public Object decodeArray(Decoder r) throws Exception {
        return this.collect(r, ']');
    }

    private Object collect(Decoder isr, char close) throws Exception {
        boolean instring = false;
        int level = 1;
        StringBuilder sb = new StringBuilder();
        int c = isr.current();
        while (c > 0 && level > 0) {
            sb.append((char)c);
            if (instring) {
                switch (c) {
                    case 34: {
                        instring = true;
                        break;
                    }
                    case 91: 
                    case 123: {
                        ++level;
                        break;
                    }
                    case 93: 
                    case 125: {
                        --level;
                    }
                }
            } else {
                switch (c) {
                    case 34: {
                        instring = false;
                        break;
                    }
                    case 92: {
                        sb.append((char)isr.read());
                    }
                }
            }
            c = isr.read();
        }
        return sb.toString();
    }
}


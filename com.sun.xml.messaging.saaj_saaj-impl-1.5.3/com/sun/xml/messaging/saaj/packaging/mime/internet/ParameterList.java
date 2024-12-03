/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ParameterList {
    private final HashMap<String, String> list;

    public ParameterList() {
        this.list = new HashMap();
    }

    private ParameterList(HashMap<String, String> m) {
        this.list = m;
    }

    public ParameterList(String s) throws ParseException {
        HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        this.list = new HashMap();
        while (true) {
            HeaderTokenizer.Token tk;
            int type;
            if ((type = (tk = h.next()).getType()) == -4) {
                return;
            }
            if ((char)type != ';') break;
            tk = h.next();
            if (tk.getType() == -4) {
                return;
            }
            if (tk.getType() != -1) {
                throw new ParseException();
            }
            String name = tk.getValue().toLowerCase();
            tk = h.next();
            if ((char)tk.getType() != '=') {
                throw new ParseException();
            }
            tk = h.next();
            type = tk.getType();
            if (type != -1 && type != -2) {
                throw new ParseException();
            }
            this.list.put(name, tk.getValue());
        }
        throw new ParseException();
    }

    public int size() {
        return this.list.size();
    }

    public String get(String name) {
        return this.list.get(name.trim().toLowerCase());
    }

    public void set(String name, String value) {
        this.list.put(name.trim().toLowerCase(), value);
    }

    public void remove(String name) {
        this.list.remove(name.trim().toLowerCase());
    }

    public Iterator<String> getNames() {
        return this.list.keySet().iterator();
    }

    public String toString() {
        return this.toString(0);
    }

    public String toString(int used) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : this.list.entrySet()) {
            String name = e.getKey();
            String value = this.quote(e.getValue());
            sb.append("; ");
            int len = name.length() + value.length() + 1;
            if ((used += 2) + len > 76) {
                sb.append("\r\n\t");
                used = 8;
            }
            sb.append(name).append('=');
            if ((used += name.length() + 1) + value.length() > 76) {
                String s = MimeUtility.fold(used, value);
                sb.append(s);
                int lastlf = s.lastIndexOf(10);
                if (lastlf >= 0) {
                    used += s.length() - lastlf - 1;
                    continue;
                }
                used += s.length();
                continue;
            }
            sb.append(value);
            used += value.length();
        }
        return sb.toString();
    }

    private String quote(String value) {
        if ("".equals(value)) {
            return "\"\"";
        }
        return MimeUtility.quote(value, "()<>@,;:\\\"\t []/?=");
    }

    public ParameterList copy() {
        return new ParameterList((HashMap)this.list.clone());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.encoding.HeaderTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceException;

final class ParameterList {
    private final Map<String, String> list;

    ParameterList(String s) {
        HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        this.list = new HashMap<String, String>();
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
                throw new WebServiceException();
            }
            String name = tk.getValue().toLowerCase();
            tk = h.next();
            if ((char)tk.getType() != '=') {
                throw new WebServiceException();
            }
            tk = h.next();
            type = tk.getType();
            if (type != -1 && type != -2) {
                throw new WebServiceException();
            }
            this.list.put(name, tk.getValue());
        }
        throw new WebServiceException();
    }

    int size() {
        return this.list.size();
    }

    String get(String name) {
        return this.list.get(name.trim().toLowerCase());
    }

    Iterator<String> getNames() {
        return this.list.keySet().iterator();
    }
}


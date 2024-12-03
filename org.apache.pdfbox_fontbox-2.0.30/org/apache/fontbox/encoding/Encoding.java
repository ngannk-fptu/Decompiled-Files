/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.encoding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Encoding {
    protected Map<Integer, String> codeToName = new HashMap<Integer, String>(250);
    protected Map<String, Integer> nameToCode = new HashMap<String, Integer>(250);

    protected void addCharacterEncoding(int code, String name) {
        this.codeToName.put(code, name);
        this.nameToCode.put(name, code);
    }

    public Integer getCode(String name) {
        return this.nameToCode.get(name);
    }

    public String getName(int code) {
        String name = this.codeToName.get(code);
        if (name != null) {
            return name;
        }
        return ".notdef";
    }

    public Map<Integer, String> getCodeToNameMap() {
        return Collections.unmodifiableMap(this.codeToName);
    }
}


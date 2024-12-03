/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.ToString;

public class ContentType
implements Serializable {
    private String type;
    private List<Param> params;

    public ContentType(String type) {
        this(type, null);
    }

    public ContentType(String type, List<Param> params) {
        this.type = type;
        this.params = params;
    }

    public String getType() {
        return this.type;
    }

    public List<Param> getParams() {
        return this.params;
    }

    public void addParam(Param val) {
        if (this.params == null) {
            this.params = new ArrayList<Param>();
        }
        this.params.add(val);
    }

    public String encode() {
        if (this.params == null) {
            return this.getType();
        }
        StringBuilder sb = new StringBuilder(this.getType());
        for (Param p : this.params) {
            sb.append("; ");
            sb.append(p.getName());
            sb.append("=");
            sb.append(p.getValue());
        }
        return sb.toString();
    }

    public static ContentType decode(String val) {
        String[] els = val.split(";");
        if (els[0] == null) {
            throw new RuntimeException("Invalid content type: " + val);
        }
        ContentType ct = new ContentType(els[0]);
        for (int i = 1; i < els.length; ++i) {
            if (els[i] == null) continue;
            String[] sp = els[i].split("=");
            if (sp.length == 1) {
                ct.addParam(new Param(sp[0].trim(), ""));
                continue;
            }
            if (sp.length == 2) {
                ct.addParam(new Param(sp[0].trim(), sp[1].trim()));
                continue;
            }
            throw new RuntimeException("Invalid content type: " + val);
        }
        return ct;
    }

    public String toString() {
        return new ToString(this).append("type", this.getType()).append("params", this.getParams()).toString();
    }

    public static class Param {
        private String name;
        private String value;

        public Param(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return new ToString(this).append("name", this.getName()).append("value", this.getValue()).toString();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.tag;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Tag {
    static final String NameStartChar = ":A-Z_a-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf]\ufdf0-\ufffd";
    static final String NameChar = "[:A-Z_a-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf]\ufdf0-\ufffd0-9.\u00b7\u0300-\u036f\u203f-\u2040\\-]";
    static final String Name = "[:A-Z_a-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf]\ufdf0-\ufffd][:A-Z_a-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf]\ufdf0-\ufffd0-9.\u00b7\u0300-\u036f\u203f-\u2040\\-]*";
    public static final Pattern NAME_P = Pattern.compile("[:A-Z_a-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf]\ufdf0-\ufffd][:A-Z_a-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf]\ufdf0-\ufffd0-9.\u00b7\u0300-\u036f\u203f-\u2040\\-]*");
    Tag parent;
    String name;
    final Map<String, String> attributes = new LinkedHashMap<String, String>();
    final List<Object> content = new ArrayList<Object>();
    static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
    boolean cdata;

    public Tag(String name, Object ... contents) {
        this.name = name;
        for (Object c : contents) {
            this.content.add(c);
        }
    }

    public Tag(Tag parent, String name, Object ... contents) {
        this(name, contents);
        parent.addContent(this);
    }

    public Tag(String name, Map<String, String> attributes, Object ... contents) {
        this(name, contents);
        this.attributes.putAll(attributes);
    }

    public Tag(String name, Map<String, String> attributes) {
        this(name, attributes, new Object[0]);
    }

    public Tag(String name, String[] attributes, Object ... contents) {
        this(name, contents);
        for (int i = 0; i < attributes.length; i += 2) {
            this.addAttribute(attributes[i], attributes[i + 1]);
        }
    }

    public Tag(String name, String[] attributes) {
        this(name, attributes, new Object[0]);
    }

    public Tag addAttribute(String key, String value) {
        if (value != null) {
            this.attributes.put(key, value);
        }
        return this;
    }

    public Tag addAttribute(String key, Object value) {
        if (value == null) {
            return this;
        }
        this.attributes.put(key, value.toString());
        return this;
    }

    public Tag addAttribute(String key, int value) {
        this.attributes.put(key, Integer.toString(value));
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Tag addAttribute(String key, Date value) {
        if (value != null) {
            SimpleDateFormat simpleDateFormat = format;
            synchronized (simpleDateFormat) {
                this.attributes.put(key, format.format(value));
            }
        }
        return this;
    }

    public Tag addContent(String string) {
        if (string != null) {
            this.content.add(string);
        }
        return this;
    }

    public Tag addContent(Tag tag) {
        this.content.add(tag);
        tag.parent = this;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public String getAttribute(String key, String deflt) {
        String answer = this.getAttribute(key);
        return answer == null ? deflt : answer;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public List<Object> getContents() {
        return this.content;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        this.print(0, new PrintWriter(sw));
        return sw.toString();
    }

    public List<Object> getContents(String tag) {
        ArrayList<Object> out = new ArrayList<Object>();
        for (Object o : this.content) {
            if (!(o instanceof Tag) || !((Tag)o).getName().equals(tag)) continue;
            out.add(o);
        }
        return out;
    }

    public String getContentsAsString() {
        StringBuilder sb = new StringBuilder();
        this.getContentsAsString(sb);
        return sb.toString();
    }

    public void getContentsAsString(StringBuilder sb) {
        for (Object o : this.content) {
            if (o instanceof Tag) {
                ((Tag)o).getContentsAsString(sb);
                continue;
            }
            sb.append(o);
        }
    }

    public Tag print(int indent, PrintWriter pw) {
        this.spaces(pw, indent);
        pw.print('<');
        pw.print(this.name);
        for (String key : this.attributes.keySet()) {
            String value = Tag.escape(this.attributes.get(key));
            pw.print(' ');
            pw.print(key);
            pw.print("=\"");
            pw.print(value);
            pw.print("\"");
        }
        if (this.content.size() == 0) {
            pw.print('/');
        } else {
            pw.print('>');
            Object last = null;
            for (Object c : this.content) {
                if (c instanceof Tag) {
                    if (last == null && indent >= 0) {
                        pw.print('\n');
                    }
                    Tag tag = (Tag)c;
                    tag.print(indent + 2, pw);
                } else {
                    if (c == null) continue;
                    String s = c.toString();
                    if (this.cdata) {
                        pw.print("<![CDATA[");
                        s = s.replaceAll("]]>", "]]]]><![CDATA[>");
                        pw.print(s);
                        pw.print("]]>");
                    } else {
                        pw.print(Tag.escape(s));
                    }
                }
                last = c;
            }
            if (last instanceof Tag) {
                this.spaces(pw, indent);
            }
            pw.print("</");
            pw.print(this.name);
        }
        pw.print('>');
        if (indent >= 0) {
            pw.print('\n');
        }
        return this;
    }

    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        block6: for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<': {
                    sb.append("&lt;");
                    continue block6;
                }
                case '>': {
                    sb.append("&gt;");
                    continue block6;
                }
                case '\"': {
                    sb.append("&quot;");
                    continue block6;
                }
                case '&': {
                    sb.append("&amp;");
                    continue block6;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    void spaces(PrintWriter pw, int n) {
        while (n-- > 0) {
            pw.print(' ');
        }
    }

    public Collection<Tag> select(String path) {
        return this.select(path, null);
    }

    public Collection<Tag> select(String path, Tag mapping) {
        ArrayList<Tag> v = new ArrayList<Tag>();
        this.select(path, v, mapping);
        return v;
    }

    void select(String path, List<Tag> results, Tag mapping) {
        if (path.startsWith("//")) {
            int i = path.indexOf(47, 2);
            String name = path.substring(2, i < 0 ? path.length() : i);
            for (Object o : this.content) {
                if (!(o instanceof Tag)) continue;
                Tag child = (Tag)o;
                if (this.match(name, child, mapping)) {
                    results.add(child);
                }
                child.select(path, results, mapping);
            }
            return;
        }
        if (path.length() == 0) {
            results.add(this);
            return;
        }
        int i = path.indexOf("/");
        String elementName = path;
        String remainder = "";
        if (i > 0) {
            elementName = path.substring(0, i);
            remainder = path.substring(i + 1);
        }
        for (Object o : this.content) {
            Tag child;
            if (!(o instanceof Tag) || !(child = (Tag)o).getName().equals(elementName) && !elementName.equals("*")) continue;
            child.select(remainder, results, mapping);
        }
    }

    public boolean match(String search, Tag child, Tag mapping) {
        int t;
        String target = child.getName();
        String sn = null;
        String tn = null;
        if (search.equals("*")) {
            return true;
        }
        int s = search.indexOf(58);
        if (s > 0) {
            sn = search.substring(0, s);
            search = search.substring(s + 1);
        }
        if ((t = target.indexOf(58)) > 0) {
            tn = target.substring(0, t);
            target = target.substring(t + 1);
        }
        if (!search.equals(target)) {
            return false;
        }
        if (mapping == null) {
            return tn == sn || sn != null && sn.equals(tn);
        }
        String suri = sn == null ? mapping.getAttribute("xmlns") : mapping.getAttribute("xmlns:" + sn);
        String turi = tn == null ? child.findRecursiveAttribute("xmlns") : child.findRecursiveAttribute("xmlns:" + tn);
        return turi == null && suri == null || turi != null && turi.equals(suri);
    }

    public String getString(String path) {
        String attribute = null;
        int index = path.indexOf("@");
        if (index >= 0) {
            attribute = path.substring(index + 1);
            path = index > 0 ? path.substring(index - 1) : "";
        }
        Collection<Tag> tags = this.select(path);
        StringBuilder sb = new StringBuilder();
        for (Tag tag : tags) {
            if (attribute == null) {
                tag.getContentsAsString(sb);
                continue;
            }
            sb.append(tag.getAttribute(attribute));
        }
        return sb.toString();
    }

    public String getStringContent() {
        StringBuilder sb = new StringBuilder();
        for (Object c : this.content) {
            if (c instanceof Tag) continue;
            sb.append(c);
        }
        return sb.toString();
    }

    public String getNameSpace() {
        return this.getNameSpace(this.name);
    }

    public String getNameSpace(String name) {
        int index = name.indexOf(58);
        if (index > 0) {
            String ns = name.substring(0, index);
            return this.findRecursiveAttribute("xmlns:" + ns);
        }
        return this.findRecursiveAttribute("xmlns");
    }

    public String findRecursiveAttribute(String name) {
        String value = this.getAttribute(name);
        if (value != null) {
            return value;
        }
        if (this.parent != null) {
            return this.parent.findRecursiveAttribute(name);
        }
        return null;
    }

    public String getLocalName() {
        int index = this.name.indexOf(58);
        if (index <= 0) {
            return this.name;
        }
        return this.name.substring(index + 1);
    }

    public void rename(String string) {
        this.name = string;
    }

    public void setCDATA() {
        this.cdata = true;
    }

    public String compact() {
        StringWriter sw = new StringWriter();
        this.print(Integer.MIN_VALUE, new PrintWriter(sw));
        return sw.toString();
    }

    public String validate() {
        try (Formatter f = new Formatter();){
            if (this.invalid(f)) {
                String string = f.toString();
                return string;
            }
            String string = null;
            return string;
        }
    }

    boolean invalid(Formatter f) {
        boolean invalid = false;
        if (!NAME_P.matcher(this.name).matches()) {
            f.format("%s: Invalid name %s\n", this.getPath(), this.name);
        }
        for (Object o : this.content) {
            if (!(o instanceof Tag)) continue;
            invalid |= ((Tag)o).invalid(f);
        }
        return invalid;
    }

    private String getPath() {
        if (this.parent == null) {
            return this.name;
        }
        return this.parent.getPath() + "/" + this.name;
    }
}


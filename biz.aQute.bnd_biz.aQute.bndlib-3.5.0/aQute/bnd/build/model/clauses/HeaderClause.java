/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.clauses;

import aQute.bnd.header.Attrs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class HeaderClause
implements Cloneable,
Comparable<HeaderClause> {
    private static final String INTERNAL_LIST_SEPARATOR = ";";
    private static final String INTERNAL_LIST_SEPARATOR_NEWLINES = ";\\\n\t\t";
    protected String name;
    protected Attrs attribs;

    public HeaderClause(String name, Attrs attribs) {
        assert (name != null);
        this.name = name;
        this.attribs = attribs == null ? new Attrs() : attribs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Attrs getAttribs() {
        return this.attribs;
    }

    public List<String> getListAttrib(String attrib) {
        String string = this.attribs.get(attrib);
        if (string == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, ",");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken().trim());
        }
        return result;
    }

    public void setListAttrib(String attrib, Collection<? extends String> value) {
        if (value == null || value.isEmpty()) {
            this.attribs.remove(attrib);
        } else {
            StringBuilder buffer = new StringBuilder();
            boolean first = true;
            for (String string : value) {
                if (!first) {
                    buffer.append(',');
                }
                buffer.append(string);
                first = false;
            }
            this.attribs.put(attrib, buffer.toString());
        }
    }

    public void formatTo(StringBuilder buffer) {
        this.formatTo(buffer, null);
    }

    public void formatTo(StringBuilder buffer, Comparator<Map.Entry<String, String>> sorter) {
        String separator = this.newlinesBetweenAttributes() ? INTERNAL_LIST_SEPARATOR_NEWLINES : INTERNAL_LIST_SEPARATOR;
        String tmpName = this.name;
        if (tmpName.indexOf(44) > -1) {
            tmpName = "'" + tmpName + "'";
        }
        buffer.append(tmpName);
        if (this.attribs != null) {
            Set<Map.Entry<String, String>> set;
            if (sorter != null) {
                set = new TreeSet<Map.Entry<String, String>>(sorter);
                set.addAll(this.attribs.entrySet());
            } else {
                set = this.attribs.entrySet();
            }
            for (Map.Entry<String, String> entry : set) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (value == null || value.length() <= 0) continue;
                buffer.append(separator);
                if (value.indexOf(44) > -1 || value.indexOf(61) > -1) {
                    value = "'" + value + "'";
                }
                buffer.append(name).append('=').append(value);
            }
        }
    }

    protected boolean newlinesBetweenAttributes() {
        return false;
    }

    public HeaderClause clone() {
        try {
            HeaderClause clone = (HeaderClause)super.clone();
            clone.name = this.name;
            clone.attribs = new Attrs(this.attribs);
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(HeaderClause other) {
        return this.name.compareTo(other.name);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.attribs == null ? 0 : this.attribs.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        HeaderClause other = (HeaderClause)obj;
        if (this.attribs == null ? other.attribs != null : !this.attribs.isEqual(other.attribs)) {
            return false;
        }
        return !(this.name == null ? other.name != null : !this.name.equals(other.name));
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        this.formatTo(b);
        return b.toString();
    }
}


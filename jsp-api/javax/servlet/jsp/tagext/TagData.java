/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import java.util.Enumeration;
import java.util.Hashtable;

public class TagData
implements Cloneable {
    public static final Object REQUEST_TIME_VALUE = new Object();
    private final Hashtable<String, Object> attributes;

    public TagData(Object[][] atts) {
        this.attributes = atts == null ? new Hashtable() : new Hashtable(atts.length);
        if (atts != null) {
            for (Object[] att : atts) {
                this.attributes.put((String)att[0], att[1]);
            }
        }
    }

    public TagData(Hashtable<String, Object> attrs) {
        this.attributes = attrs;
    }

    public String getId() {
        return this.getAttributeString("id");
    }

    public Object getAttribute(String attName) {
        return this.attributes.get(attName);
    }

    public void setAttribute(String attName, Object value) {
        this.attributes.put(attName, value);
    }

    public String getAttributeString(String attName) {
        Object o = this.attributes.get(attName);
        if (o == null) {
            return null;
        }
        return (String)o;
    }

    public Enumeration<String> getAttributes() {
        return this.attributes.keys();
    }
}


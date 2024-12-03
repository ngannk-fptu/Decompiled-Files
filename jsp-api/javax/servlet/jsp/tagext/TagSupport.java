/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;

public class TagSupport
implements IterationTag,
Serializable {
    private static final long serialVersionUID = 1L;
    private Tag parent;
    private Map<String, Object> values;
    protected String id;
    protected transient PageContext pageContext;

    public static final Tag findAncestorWithClass(Tag from, Class klass) {
        boolean isInterface = false;
        if (from == null || klass == null || !Tag.class.isAssignableFrom(klass) && !(isInterface = klass.isInterface())) {
            return null;
        }
        Tag tag;
        while ((tag = from.getParent()) != null) {
            if (isInterface && klass.isInstance(tag) || klass.isAssignableFrom(tag.getClass())) {
                return tag;
            }
            from = tag;
        }
        return null;
    }

    @Override
    public int doStartTag() throws JspException {
        return 0;
    }

    @Override
    public int doEndTag() throws JspException {
        return 6;
    }

    @Override
    public int doAfterBody() throws JspException {
        return 0;
    }

    @Override
    public void release() {
        this.parent = null;
        this.id = null;
        if (this.values != null) {
            this.values.clear();
        }
        this.values = null;
    }

    @Override
    public void setParent(Tag t) {
        this.parent = t;
    }

    @Override
    public Tag getParent() {
        return this.parent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public void setValue(String k, Object o) {
        if (this.values == null) {
            this.values = new ConcurrentHashMap<String, Object>();
        }
        this.values.put(k, o);
    }

    public Object getValue(String k) {
        if (this.values == null) {
            return null;
        }
        return this.values.get(k);
    }

    public void removeValue(String k) {
        if (this.values != null) {
            this.values.remove(k);
        }
    }

    public Enumeration<String> getValues() {
        if (this.values == null) {
            return null;
        }
        return Collections.enumeration(this.values.keySet());
    }
}


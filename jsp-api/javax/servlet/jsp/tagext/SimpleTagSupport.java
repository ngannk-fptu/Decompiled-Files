/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagAdapter;

public class SimpleTagSupport
implements SimpleTag {
    private JspTag parentTag;
    private JspContext jspContext;
    private JspFragment jspBody;

    @Override
    public void doTag() throws JspException, IOException {
    }

    @Override
    public void setParent(JspTag parent) {
        this.parentTag = parent;
    }

    @Override
    public JspTag getParent() {
        return this.parentTag;
    }

    @Override
    public void setJspContext(JspContext pc) {
        this.jspContext = pc;
    }

    protected JspContext getJspContext() {
        return this.jspContext;
    }

    @Override
    public void setJspBody(JspFragment jspBody) {
        this.jspBody = jspBody;
    }

    protected JspFragment getJspBody() {
        return this.jspBody;
    }

    public static final JspTag findAncestorWithClass(JspTag from, Class<?> klass) {
        boolean isInterface = false;
        if (from == null || klass == null || !JspTag.class.isAssignableFrom(klass) && !(isInterface = klass.isInterface())) {
            return null;
        }
        while (true) {
            JspTag parent = null;
            if (from instanceof SimpleTag) {
                parent = ((SimpleTag)from).getParent();
            } else if (from instanceof Tag) {
                parent = ((Tag)from).getParent();
            }
            if (parent == null) {
                return null;
            }
            if (parent instanceof TagAdapter) {
                parent = ((TagAdapter)parent).getAdaptee();
            }
            if (isInterface && klass.isInstance(parent) || klass.isAssignableFrom(parent.getClass())) {
                return parent;
            }
            from = parent;
        }
    }
}


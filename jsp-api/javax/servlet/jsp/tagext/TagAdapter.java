/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.Tag;

public class TagAdapter
implements Tag {
    private final SimpleTag simpleTagAdaptee;
    private Tag parent;
    private boolean parentDetermined;

    public TagAdapter(SimpleTag adaptee) {
        if (adaptee == null) {
            throw new IllegalArgumentException();
        }
        this.simpleTagAdaptee = adaptee;
    }

    @Override
    public void setPageContext(PageContext pc) {
        throw new UnsupportedOperationException("Illegal to invoke setPageContext() on TagAdapter wrapper");
    }

    @Override
    public void setParent(Tag parentTag) {
        throw new UnsupportedOperationException("Illegal to invoke setParent() on TagAdapter wrapper");
    }

    @Override
    public Tag getParent() {
        if (!this.parentDetermined) {
            JspTag adapteeParent = this.simpleTagAdaptee.getParent();
            if (adapteeParent != null) {
                this.parent = adapteeParent instanceof Tag ? (Tag)adapteeParent : new TagAdapter((SimpleTag)adapteeParent);
            }
            this.parentDetermined = true;
        }
        return this.parent;
    }

    public JspTag getAdaptee() {
        return this.simpleTagAdaptee;
    }

    @Override
    public int doStartTag() throws JspException {
        throw new UnsupportedOperationException("Illegal to invoke doStartTag() on TagAdapter wrapper");
    }

    @Override
    public int doEndTag() throws JspException {
        throw new UnsupportedOperationException("Illegal to invoke doEndTag() on TagAdapter wrapper");
    }

    @Override
    public void release() {
        throw new UnsupportedOperationException("Illegal to invoke release() on TagAdapter wrapper");
    }
}


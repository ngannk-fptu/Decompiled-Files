/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public interface SimpleTag
extends JspTag {
    public void doTag() throws JspException, IOException;

    public void setParent(JspTag var1);

    public JspTag getParent();

    public void setJspContext(JspContext var1);

    public void setJspBody(JspFragment var1);
}


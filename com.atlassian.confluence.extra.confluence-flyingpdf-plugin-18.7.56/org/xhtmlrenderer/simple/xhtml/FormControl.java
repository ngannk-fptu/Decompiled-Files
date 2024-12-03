/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml;

import org.w3c.dom.Element;
import org.xhtmlrenderer.simple.xhtml.FormControlListener;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;

public interface FormControl {
    public Element getElement();

    public XhtmlForm getForm();

    public void addFormControlListener(FormControlListener var1);

    public void removeFormControlListener(FormControlListener var1);

    public String getName();

    public boolean isEnabled();

    public void setEnabled(boolean var1);

    public String getInitialValue();

    public boolean isSuccessful();

    public boolean isMultiple();

    public void setSuccessful(boolean var1);

    public String getValue();

    public void setValue(String var1);

    public String[] getMultipleValues();

    public void setMultipleValues(String[] var1);

    public void reset();
}


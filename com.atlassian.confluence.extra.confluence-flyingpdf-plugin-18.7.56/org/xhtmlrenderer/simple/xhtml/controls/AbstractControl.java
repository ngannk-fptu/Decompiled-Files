/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlListener;
import org.xhtmlrenderer.simple.xhtml.FormListener;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;

public abstract class AbstractControl
implements FormControl {
    private XhtmlForm _form;
    private Element _element;
    private String _name;
    private String _initialValue;
    private String _value;
    private boolean _successful;
    private boolean _enabled;
    private List _listeners = new ArrayList();

    public AbstractControl(XhtmlForm form, Element e) {
        this._form = form;
        this._element = e;
        this._name = e.getAttribute("name");
        if (this._name.length() == 0) {
            this._name = e.getAttribute("id");
        }
        this._value = this._initialValue = e.getAttribute("value");
        this._successful = this._enabled = e.getAttribute("disabled").length() == 0;
        if (form != null) {
            form.addFormListener(new FormListener(){

                @Override
                public void submitted(XhtmlForm form) {
                }

                @Override
                public void resetted(XhtmlForm form) {
                    AbstractControl.this.reset();
                }
            });
        }
    }

    protected void fireChanged() {
        Iterator iter = this._listeners.iterator();
        while (iter.hasNext()) {
            ((FormControlListener)iter.next()).changed(this);
        }
    }

    protected void fireSuccessful() {
        Iterator iter = this._listeners.iterator();
        while (iter.hasNext()) {
            ((FormControlListener)iter.next()).successful(this);
        }
    }

    protected void fireEnabled() {
        Iterator iter = this._listeners.iterator();
        while (iter.hasNext()) {
            ((FormControlListener)iter.next()).enabled(this);
        }
    }

    @Override
    public void addFormControlListener(FormControlListener listener) {
        this._listeners.add(listener);
    }

    @Override
    public void removeFormControlListener(FormControlListener listener) {
        this._listeners.remove(listener);
    }

    @Override
    public Element getElement() {
        return this._element;
    }

    @Override
    public XhtmlForm getForm() {
        return this._form;
    }

    @Override
    public String getName() {
        return this._name;
    }

    @Override
    public String getInitialValue() {
        return this._initialValue;
    }

    protected void setInitialValue(String value) {
        this._initialValue = value;
        this._value = value;
    }

    @Override
    public String getValue() {
        if (this.isMultiple()) {
            return null;
        }
        return this._value;
    }

    @Override
    public void setValue(String value) {
        if (!this.isMultiple()) {
            this._value = value;
            this.fireChanged();
        }
    }

    @Override
    public String[] getMultipleValues() {
        return null;
    }

    @Override
    public void setMultipleValues(String[] values) {
    }

    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return this._enabled;
    }

    @Override
    public boolean isSuccessful() {
        return this._successful && this._enabled;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public void setSuccessful(boolean successful) {
        this._successful = successful;
        this.fireSuccessful();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this._enabled = enabled;
        this.fireEnabled();
    }

    @Override
    public void reset() {
        this.setValue(this._initialValue);
    }

    public static String collectText(Element e) {
        StringBuffer result = new StringBuffer();
        Node node = e.getFirstChild();
        if (node != null) {
            do {
                if (node.getNodeType() != 3) continue;
                Text text = (Text)node;
                result.append(text.getData());
            } while ((node = node.getNextSibling()) != null);
        }
        return result.toString().trim();
    }

    public static int getIntAttribute(Element e, String attribute, int def) {
        int result = def;
        String str = e.getAttribute(attribute);
        if (str.length() > 0) {
            try {
                result = Integer.parseInt(str);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return result;
    }
}


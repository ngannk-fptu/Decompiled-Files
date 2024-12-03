/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.simple.xhtml.controls.AbstractControl;
import org.xhtmlrenderer.simple.xhtml.controls.ButtonControlListener;

public class ButtonControl
extends AbstractControl {
    private String _type;
    private String _label;
    private boolean _extended;
    private List _listeners = new ArrayList();

    public ButtonControl(XhtmlForm form, Element e) {
        super(form, e);
        this._extended = e.getNodeName().equalsIgnoreCase("button");
        this._label = this._extended ? ButtonControl.collectText(e) : this.getValue();
        this._type = e.getAttribute("type").toLowerCase();
        if (!this._type.equals("reset") && !this._type.equals("button")) {
            this._type = "submit";
        }
    }

    public String getType() {
        return this._type;
    }

    public String getLabel() {
        return this._label;
    }

    public boolean isExtended() {
        return this._extended;
    }

    public void addButtonControlListener(ButtonControlListener listener) {
        this._listeners.add(listener);
    }

    public void removeButtonControlListener(ButtonControlListener listener) {
        this._listeners.remove(listener);
    }

    public boolean press() {
        Iterator iter = this._listeners.iterator();
        while (iter.hasNext()) {
            if (((ButtonControlListener)iter.next()).pressed(this)) continue;
            return false;
        }
        return true;
    }
}


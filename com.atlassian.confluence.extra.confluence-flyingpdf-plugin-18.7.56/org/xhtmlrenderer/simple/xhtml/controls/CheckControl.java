/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml.controls;

import org.w3c.dom.Element;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.simple.xhtml.controls.AbstractControl;

public class CheckControl
extends AbstractControl {
    private boolean _initialValue;
    private boolean _radio;

    public CheckControl(XhtmlForm form, Element e) {
        super(form, e);
        this._initialValue = e.getAttribute("checked").length() != 0;
        this.setSuccessful(this._initialValue);
        this._radio = e.getAttribute("type").equals("radio");
    }

    @Override
    public void setSuccessful(boolean successful) {
        super.setSuccessful(successful);
        if (this._radio && successful) {
            XhtmlForm form = this.getForm();
            if (form == null) {
                return;
            }
            for (FormControl control : form.getAllControls(this.getName())) {
                CheckControl check;
                if (!(control instanceof CheckControl) || !(check = (CheckControl)control).isRadio() || check == this) continue;
                check.setSuccessful(false);
            }
        }
    }

    public boolean isRadio() {
        return this._radio;
    }

    @Override
    public void reset() {
        this.setSuccessful(this._initialValue);
    }
}


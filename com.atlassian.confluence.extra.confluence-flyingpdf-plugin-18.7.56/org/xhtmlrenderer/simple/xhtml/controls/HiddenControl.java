/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml.controls;

import org.w3c.dom.Element;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.simple.xhtml.controls.AbstractControl;

public class HiddenControl
extends AbstractControl {
    public HiddenControl(XhtmlForm form, Element e) {
        super(form, e);
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}


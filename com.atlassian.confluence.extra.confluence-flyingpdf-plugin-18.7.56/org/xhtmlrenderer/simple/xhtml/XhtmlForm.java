/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.xhtmlrenderer.simple.extend.URLUTF8Encoder;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormListener;
import org.xhtmlrenderer.simple.xhtml.controls.ButtonControl;
import org.xhtmlrenderer.simple.xhtml.controls.CheckControl;
import org.xhtmlrenderer.simple.xhtml.controls.HiddenControl;
import org.xhtmlrenderer.simple.xhtml.controls.SelectControl;
import org.xhtmlrenderer.simple.xhtml.controls.TextControl;

public class XhtmlForm {
    protected String _action;
    protected String _method;
    protected List _controls = new LinkedList();
    private List _listeners = new ArrayList();

    public XhtmlForm(String action, String method) {
        this._action = action;
        this._method = method;
    }

    public void addFormListener(FormListener listener) {
        this._listeners.add(listener);
    }

    public void removeFormListener(FormListener listener) {
        this._listeners.remove(listener);
    }

    public FormControl getControl(String name) {
        for (FormControl control : this._controls) {
            if (!control.getName().equals(name)) continue;
            return control;
        }
        return null;
    }

    public List getAllControls(String name) {
        ArrayList<FormControl> result = new ArrayList<FormControl>();
        for (FormControl control : this._controls) {
            if (!control.getName().equals(name)) continue;
            result.add(control);
        }
        return result;
    }

    public Iterator getControls() {
        return this._controls.iterator();
    }

    public FormControl createControl(Element e) {
        return XhtmlForm.createControl(this, e);
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static FormControl createControl(XhtmlForm form, Element e) {
        void var2_12;
        if (e == null) {
            return null;
        }
        String name = e.getNodeName();
        if (name.equals("input")) {
            String type = e.getAttribute("type");
            if (type.equals("text") || type.equals("password")) {
                TextControl textControl = new TextControl(form, e);
            } else if (type.equals("hidden")) {
                HiddenControl hiddenControl = new HiddenControl(form, e);
            } else if (type.equals("button") || type.equals("submit") || type.equals("reset")) {
                ButtonControl buttonControl = new ButtonControl(form, e);
            } else {
                if (!type.equals("checkbox") && !type.equals("radio")) return null;
                CheckControl checkControl = new CheckControl(form, e);
            }
        } else if (name.equals("textarea")) {
            TextControl textControl = new TextControl(form, e);
        } else if (name.equals("button")) {
            ButtonControl buttonControl = new ButtonControl(form, e);
        } else {
            if (!name.equals("select")) return null;
            SelectControl selectControl = new SelectControl(form, e);
        }
        if (form == null) return var2_12;
        form._controls.add(var2_12);
        return var2_12;
    }

    public void reset() {
        Iterator iter = this._listeners.iterator();
        while (iter.hasNext()) {
            ((FormListener)iter.next()).resetted(this);
        }
    }

    public void submit() {
        StringBuffer data = new StringBuffer();
        Iterator iter = this.getControls();
        while (iter.hasNext()) {
            FormControl control = (FormControl)iter.next();
            if (!control.isSuccessful()) continue;
            if (control.isMultiple()) {
                String[] values = control.getMultipleValues();
                for (int i = 0; i < values.length; ++i) {
                    if (data.length() > 0) {
                        data.append('&');
                    }
                    data.append(URLUTF8Encoder.encode(control.getName()));
                    data.append('=');
                    data.append(URLUTF8Encoder.encode(values[i]));
                }
                continue;
            }
            if (data.length() > 0) {
                data.append('&');
            }
            data.append(URLUTF8Encoder.encode(control.getName()));
            data.append('=');
            data.append(URLUTF8Encoder.encode(control.getValue()));
        }
        System.out.println("Form submitted!");
        System.out.println("Action: ".concat(this._action));
        System.out.println("Method: ".concat(this._method));
        System.out.println("Data: ".concat(data.toString()));
        iter = this._listeners.iterator();
        while (iter.hasNext()) {
            ((FormListener)iter.next()).submitted(this);
        }
    }
}


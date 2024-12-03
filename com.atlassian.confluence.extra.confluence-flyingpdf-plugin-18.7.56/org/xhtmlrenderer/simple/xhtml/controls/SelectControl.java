/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml.controls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.simple.xhtml.controls.AbstractControl;

public class SelectControl
extends AbstractControl {
    private int _size;
    private boolean _multiple;
    private List _values;
    private String _initialValue;
    private String[] _initialValues;
    private Map _options;

    public SelectControl(XhtmlForm form, Element e) {
        super(form, e);
        this._size = SelectControl.getIntAttribute(e, "size", 1);
        boolean bl = this._multiple = e.getAttribute("multiple").length() != 0;
        if (this._multiple) {
            this._values = new ArrayList();
        }
        super.setValue(null);
        this.setSuccessful(false);
        this._options = new LinkedHashMap();
        this.traverseOptions(e, "");
        if (this._multiple) {
            this._initialValues = this.getMultipleValues();
            if (this._initialValues.length > 0) {
                this.setSuccessful(true);
            }
        } else {
            this._initialValue = this.getValue();
            if (this._initialValue != null) {
                this.setSuccessful(true);
            }
        }
    }

    private void traverseOptions(Element e, String prefix) {
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            if (children.item(i).getNodeType() != 1) continue;
            Element child = (Element)children.item(i);
            if (child.getNodeName().equalsIgnoreCase("optgroup")) {
                this.traverseOptions(child, prefix + child.getAttribute("label") + " ");
                continue;
            }
            if (!child.getNodeName().equalsIgnoreCase("option")) continue;
            String value = child.getAttribute("value");
            String label = child.getAttribute("label");
            String content = SelectControl.collectText(child);
            if (value.length() == 0) {
                value = content;
            }
            label = label.length() == 0 ? content : prefix.concat(label);
            this._options.put(value, label);
            if (child.getAttribute("selected").length() == 0) continue;
            if (this.isMultiple()) {
                if (this._values.contains(value)) continue;
                this._values.add(value);
                continue;
            }
            this.setValue(value);
        }
    }

    public int getSize() {
        return this._size;
    }

    @Override
    public boolean isMultiple() {
        return this._multiple;
    }

    public Map getOptions() {
        return new LinkedHashMap(this._options);
    }

    @Override
    public void setValue(String value) {
        if (!this.isMultiple()) {
            if (this._options.containsKey(value)) {
                super.setValue(value);
                this.setSuccessful(true);
            } else {
                this.setSuccessful(false);
                super.setValue(null);
            }
        }
    }

    @Override
    public String[] getMultipleValues() {
        if (this.isMultiple()) {
            return this._values.toArray(new String[this._values.size()]);
        }
        return null;
    }

    @Override
    public void setMultipleValues(String[] values) {
        if (this.isMultiple()) {
            this._values.clear();
            for (int i = 0; i < values.length; ++i) {
                if (this._options.get(values[i]) == null || this._values.contains(values[i])) continue;
                this._values.add(values[i]);
            }
            if (this._values.isEmpty()) {
                this.setSuccessful(false);
            } else {
                this.setSuccessful(true);
            }
            this.fireChanged();
        }
    }

    @Override
    public void reset() {
        if (this.isMultiple()) {
            this.setMultipleValues(this._initialValues);
        } else {
            this.setValue(this._initialValue);
        }
    }
}


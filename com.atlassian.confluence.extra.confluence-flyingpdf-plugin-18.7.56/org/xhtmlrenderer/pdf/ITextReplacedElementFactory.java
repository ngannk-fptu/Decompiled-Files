/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.BookmarkElement;
import org.xhtmlrenderer.pdf.CheckboxFormField;
import org.xhtmlrenderer.pdf.EmptyReplacedElement;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.RadioButtonFormField;
import org.xhtmlrenderer.pdf.TextFormField;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

public class ITextReplacedElementFactory
implements ReplacedElementFactory {
    private ITextOutputDevice _outputDevice;
    private Map _radioButtonsByElem = new HashMap();
    private Map _radioButtonsByName = new HashMap();

    public ITextReplacedElementFactory(ITextOutputDevice outputDevice) {
        this._outputDevice = outputDevice;
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        Element e = box.getElement();
        if (e == null) {
            return null;
        }
        String nodeName = e.getNodeName();
        if (nodeName.equals("img")) {
            FSImage fsImage;
            String srcAttr = e.getAttribute("src");
            if (srcAttr != null && srcAttr.length() > 0 && (fsImage = uac.getImageResource(srcAttr).getImage()) != null) {
                if (cssWidth != -1 || cssHeight != -1) {
                    fsImage.scale(cssWidth, cssHeight);
                }
                return new ITextImageElement(fsImage);
            }
        } else {
            if (nodeName.equals("input")) {
                String type = e.getAttribute("type");
                if (type.equals("hidden")) {
                    return new EmptyReplacedElement(1, 1);
                }
                if (type.equals("checkbox")) {
                    return new CheckboxFormField(c, box, cssWidth, cssHeight);
                }
                if (type.equals("radio")) {
                    return new EmptyReplacedElement(0, 0);
                }
                return new TextFormField(c, box, cssWidth, cssHeight);
            }
            if (nodeName.equals("bookmark")) {
                BookmarkElement result = new BookmarkElement();
                if (e.hasAttribute("name")) {
                    String name = e.getAttribute("name");
                    c.addBoxId(name, box);
                    result.setAnchorName(name);
                }
                return result;
            }
        }
        return null;
    }

    private boolean isTextarea(Element e) {
        if (!e.getNodeName().equals("textarea")) {
            return false;
        }
        Node n = e.getFirstChild();
        while (n != null) {
            short nodeType = n.getNodeType();
            if (nodeType == 3 || nodeType == 4) continue;
            return false;
        }
        return true;
    }

    private void saveResult(Element e, RadioButtonFormField result) {
        this._radioButtonsByElem.put(e, result);
        String fieldName = result.getFieldName(this._outputDevice, e);
        ArrayList<RadioButtonFormField> fields = (ArrayList<RadioButtonFormField>)this._radioButtonsByName.get(fieldName);
        if (fields == null) {
            fields = new ArrayList<RadioButtonFormField>();
            this._radioButtonsByName.put(fieldName, fields);
        }
        fields.add(result);
    }

    @Override
    public void reset() {
        this._radioButtonsByElem = new HashMap();
        this._radioButtonsByName = new HashMap();
    }

    @Override
    public void remove(Element e) {
        String fieldName;
        List values;
        RadioButtonFormField field = (RadioButtonFormField)this._radioButtonsByElem.remove(e);
        if (field != null && (values = (List)this._radioButtonsByName.get(fieldName = field.getFieldName(this._outputDevice, e))) != null) {
            values.remove(field);
            if (values.size() == 0) {
                this._radioButtonsByName.remove(fieldName);
            }
        }
    }

    public void remove(String fieldName) {
        List values = (List)this._radioButtonsByName.get(fieldName);
        if (values != null) {
            for (RadioButtonFormField field : values) {
                this._radioButtonsByElem.remove(field.getBox().getElement());
            }
        }
        this._radioButtonsByName.remove(fieldName);
    }

    public List getRadioButtons(String name) {
        return (List)this._radioButtonsByName.get(name);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
    }
}


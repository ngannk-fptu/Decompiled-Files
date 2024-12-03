/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import org.apache.wml.WMLDocument;
import org.apache.wml.dom.WMLAElementImpl;
import org.apache.wml.dom.WMLAccessElementImpl;
import org.apache.wml.dom.WMLAnchorElementImpl;
import org.apache.wml.dom.WMLBElementImpl;
import org.apache.wml.dom.WMLBigElementImpl;
import org.apache.wml.dom.WMLBrElementImpl;
import org.apache.wml.dom.WMLCardElementImpl;
import org.apache.wml.dom.WMLDoElementImpl;
import org.apache.wml.dom.WMLElementImpl;
import org.apache.wml.dom.WMLEmElementImpl;
import org.apache.wml.dom.WMLFieldsetElementImpl;
import org.apache.wml.dom.WMLGoElementImpl;
import org.apache.wml.dom.WMLHeadElementImpl;
import org.apache.wml.dom.WMLIElementImpl;
import org.apache.wml.dom.WMLImgElementImpl;
import org.apache.wml.dom.WMLInputElementImpl;
import org.apache.wml.dom.WMLMetaElementImpl;
import org.apache.wml.dom.WMLNoopElementImpl;
import org.apache.wml.dom.WMLOneventElementImpl;
import org.apache.wml.dom.WMLOptgroupElementImpl;
import org.apache.wml.dom.WMLOptionElementImpl;
import org.apache.wml.dom.WMLPElementImpl;
import org.apache.wml.dom.WMLPostfieldElementImpl;
import org.apache.wml.dom.WMLPrevElementImpl;
import org.apache.wml.dom.WMLRefreshElementImpl;
import org.apache.wml.dom.WMLSelectElementImpl;
import org.apache.wml.dom.WMLSetvarElementImpl;
import org.apache.wml.dom.WMLSmallElementImpl;
import org.apache.wml.dom.WMLStrongElementImpl;
import org.apache.wml.dom.WMLTableElementImpl;
import org.apache.wml.dom.WMLTdElementImpl;
import org.apache.wml.dom.WMLTemplateElementImpl;
import org.apache.wml.dom.WMLTimerElementImpl;
import org.apache.wml.dom.WMLTrElementImpl;
import org.apache.wml.dom.WMLUElementImpl;
import org.apache.wml.dom.WMLWmlElementImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class WMLDocumentImpl
extends DocumentImpl
implements WMLDocument {
    private static final long serialVersionUID = -6582904849512384104L;
    private static Hashtable _elementTypesWML;
    private static final Class[] _elemClassSigWML;

    @Override
    public Element createElement(String string) throws DOMException {
        Class clazz = (Class)_elementTypesWML.get(string);
        if (clazz != null) {
            try {
                Constructor constructor = clazz.getConstructor(_elemClassSigWML);
                return (Element)constructor.newInstance(this, string);
            }
            catch (Exception exception) {
                Throwable throwable = exception instanceof InvocationTargetException ? ((InvocationTargetException)exception).getTargetException() : exception;
                System.out.println("Exception " + throwable.getClass().getName());
                System.out.println(throwable.getMessage());
                throw new IllegalStateException("Tag '" + string + "' associated with an Element class that failed to construct.");
            }
        }
        return new WMLElementImpl(this, string);
    }

    @Override
    protected boolean canRenameElements(String string, String string2, ElementImpl elementImpl) {
        return _elementTypesWML.get(string2) == _elementTypesWML.get(elementImpl.getTagName());
    }

    public WMLDocumentImpl(DocumentType documentType) {
        super(documentType, false);
    }

    static {
        _elemClassSigWML = new Class[]{WMLDocumentImpl.class, String.class};
        _elementTypesWML = new Hashtable();
        _elementTypesWML.put("b", WMLBElementImpl.class);
        _elementTypesWML.put("noop", WMLNoopElementImpl.class);
        _elementTypesWML.put("a", WMLAElementImpl.class);
        _elementTypesWML.put("setvar", WMLSetvarElementImpl.class);
        _elementTypesWML.put("access", WMLAccessElementImpl.class);
        _elementTypesWML.put("strong", WMLStrongElementImpl.class);
        _elementTypesWML.put("postfield", WMLPostfieldElementImpl.class);
        _elementTypesWML.put("do", WMLDoElementImpl.class);
        _elementTypesWML.put("wml", WMLWmlElementImpl.class);
        _elementTypesWML.put("tr", WMLTrElementImpl.class);
        _elementTypesWML.put("go", WMLGoElementImpl.class);
        _elementTypesWML.put("big", WMLBigElementImpl.class);
        _elementTypesWML.put("anchor", WMLAnchorElementImpl.class);
        _elementTypesWML.put("timer", WMLTimerElementImpl.class);
        _elementTypesWML.put("small", WMLSmallElementImpl.class);
        _elementTypesWML.put("optgroup", WMLOptgroupElementImpl.class);
        _elementTypesWML.put("head", WMLHeadElementImpl.class);
        _elementTypesWML.put("td", WMLTdElementImpl.class);
        _elementTypesWML.put("fieldset", WMLFieldsetElementImpl.class);
        _elementTypesWML.put("img", WMLImgElementImpl.class);
        _elementTypesWML.put("refresh", WMLRefreshElementImpl.class);
        _elementTypesWML.put("onevent", WMLOneventElementImpl.class);
        _elementTypesWML.put("input", WMLInputElementImpl.class);
        _elementTypesWML.put("prev", WMLPrevElementImpl.class);
        _elementTypesWML.put("table", WMLTableElementImpl.class);
        _elementTypesWML.put("meta", WMLMetaElementImpl.class);
        _elementTypesWML.put("template", WMLTemplateElementImpl.class);
        _elementTypesWML.put("br", WMLBrElementImpl.class);
        _elementTypesWML.put("option", WMLOptionElementImpl.class);
        _elementTypesWML.put("u", WMLUElementImpl.class);
        _elementTypesWML.put("p", WMLPElementImpl.class);
        _elementTypesWML.put("select", WMLSelectElementImpl.class);
        _elementTypesWML.put("em", WMLEmElementImpl.class);
        _elementTypesWML.put("i", WMLIElementImpl.class);
        _elementTypesWML.put("card", WMLCardElementImpl.class);
    }
}


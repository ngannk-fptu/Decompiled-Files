/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.stax.FOMDiv;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMText
extends FOMElement
implements Text {
    private static final long serialVersionUID = 5177795905116574120L;
    protected Text.Type type = Text.Type.TEXT;

    protected FOMText(Text.Type type, String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
        this.init(type);
    }

    protected FOMText(Text.Type type, QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
        this.init(type);
    }

    protected FOMText(Text.Type type, String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
        this.init(type);
    }

    private void init(Text.Type type) {
        this.type = type;
        if (Text.Type.TEXT.equals((Object)type)) {
            this.setAttributeValue(TYPE, "text");
        } else if (Text.Type.HTML.equals((Object)type)) {
            this.setAttributeValue(TYPE, "html");
        } else if (Text.Type.XHTML.equals((Object)type)) {
            this.setAttributeValue(TYPE, "xhtml");
        } else {
            this.removeAttribute(TYPE);
        }
    }

    @Override
    public final Text.Type getTextType() {
        return this.type;
    }

    @Override
    public Text setTextType(Text.Type type) {
        this.complete();
        this.init(type);
        return this;
    }

    @Override
    public Div getValueElement() {
        return (Div)((Object)this.getFirstChildWithName(Constants.DIV));
    }

    @Override
    public Text setValueElement(Div value) {
        this.complete();
        if (value != null) {
            if (this.getFirstChildWithName(Constants.DIV) != null) {
                this.getFirstChildWithName(Constants.DIV).discard();
            }
            this.init(Text.Type.XHTML);
            this.setFirstChild((OMElement)((Object)value));
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    @Override
    public String getValue() {
        String val = null;
        if (Text.Type.TEXT.equals((Object)this.type)) {
            val = this.getText();
        } else if (Text.Type.HTML.equals((Object)this.type)) {
            val = this.getText();
        } else if (Text.Type.XHTML.equals((Object)this.type)) {
            FOMDiv div = (FOMDiv)this.getFirstChildWithName(Constants.DIV);
            val = div != null ? div.getInternalValue() : null;
        }
        return val;
    }

    public <T extends Element> T setText(String value) {
        return this.setText(Text.Type.TEXT, value);
    }

    public <T extends Element> T setText(Text.Type type, String value) {
        this.complete();
        this.init(type);
        if (value != null) {
            for (OMNode child = this.getFirstOMChild(); child != null; child = child.getNextOMSibling()) {
                if (child.getType() != 4) continue;
                child.detach();
            }
            this.getOMFactory().createOMText((OMContainer)this, value);
        } else {
            this._removeAllChildren();
        }
        return (T)this;
    }

    @Override
    public Text setValue(String value) {
        this.complete();
        if (value != null) {
            if (Text.Type.TEXT.equals((Object)this.type)) {
                this.setText(this.type, value);
            } else if (Text.Type.HTML.equals((Object)this.type)) {
                this.setText(this.type, value);
            } else if (Text.Type.XHTML.equals((Object)this.type)) {
                IRI baseUri = null;
                value = "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + value + "</div>";
                Element element = null;
                try {
                    baseUri = this.getResolvedBaseUri();
                    element = this._parse(value, baseUri);
                }
                catch (Exception e) {
                    // empty catch block
                }
                if (element != null && element instanceof Div) {
                    this.setValueElement((Div)element);
                }
            }
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    @Override
    public String getWrappedValue() {
        if (Text.Type.XHTML.equals((Object)this.type)) {
            return ((Object)this.getFirstChildWithName(Constants.DIV)).toString();
        }
        return this.getValue();
    }

    @Override
    public Text setWrappedValue(String wrappedValue) {
        this.complete();
        if (Text.Type.XHTML.equals((Object)this.type)) {
            IRI baseUri = null;
            Element element = null;
            try {
                baseUri = this.getResolvedBaseUri();
                element = this._parse(wrappedValue, baseUri);
            }
            catch (Exception e) {
                // empty catch block
            }
            if (element != null && element instanceof Div) {
                this.setValueElement((Div)element);
            }
        } else {
            this.setValue(wrappedValue);
        }
        return this;
    }

    @Override
    public IRI getBaseUri() {
        Div el;
        if (Text.Type.XHTML.equals((Object)this.type) && (el = this.getValueElement()) != null && el.getAttributeValue(BASE) != null) {
            if (this.getAttributeValue(BASE) != null) {
                return super.getBaseUri().resolve(el.getAttributeValue(BASE));
            }
            return this._getUriValue(el.getAttributeValue(BASE));
        }
        return super.getBaseUri();
    }

    @Override
    public IRI getResolvedBaseUri() {
        Div el;
        if (Text.Type.XHTML.equals((Object)this.type) && (el = this.getValueElement()) != null && el.getAttributeValue(BASE) != null) {
            return super.getResolvedBaseUri().resolve(el.getAttributeValue(BASE));
        }
        return super.getResolvedBaseUri();
    }

    @Override
    public String getLanguage() {
        Div el;
        if (Text.Type.XHTML.equals((Object)this.type) && (el = this.getValueElement()) != null && el.getAttributeValue(LANG) != null) {
            return el.getAttributeValue(LANG);
        }
        return super.getLanguage();
    }

    @Override
    public Object clone() {
        FOMText text = (FOMText)super.clone();
        text.type = this.type;
        return text;
    }
}


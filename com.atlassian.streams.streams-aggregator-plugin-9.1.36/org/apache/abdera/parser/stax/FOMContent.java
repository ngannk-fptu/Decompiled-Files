/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 *  javax.activation.URLDataSource
 */
package org.apache.abdera.parser.stax;

import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.activation.URLDataSource;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.parser.stax.FOMDiv;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.util.Constants;
import org.apache.axiom.attachments.utils.DataHandlerUtils;
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
public class FOMContent
extends FOMExtensibleElement
implements Content {
    private static final long serialVersionUID = -5499917654824498563L;
    protected Content.Type type = Content.Type.TEXT;

    protected FOMContent(String name, OMNamespace namespace, Content.Type type, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
        this.init(type);
    }

    protected FOMContent(QName qname, Content.Type type, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
        this.init(type);
    }

    protected FOMContent(String localName, Content.Type type, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
        this.init(type);
    }

    protected FOMContent(Content.Type type, OMContainer parent, OMFactory factory) throws OMException {
        super(CONTENT, parent, factory);
        this.init(type);
    }

    private void init(Content.Type type) {
        this.type = type;
        if (Content.Type.TEXT.equals((Object)type)) {
            this.setAttributeValue(TYPE, "text");
        } else if (Content.Type.HTML.equals((Object)type)) {
            this.setAttributeValue(TYPE, "html");
        } else if (Content.Type.XHTML.equals((Object)type)) {
            this.setAttributeValue(TYPE, "xhtml");
        } else if (Content.Type.XML.equals((Object)type)) {
            this.setAttributeValue(TYPE, "application/xml");
        } else {
            this.removeAttribute(TYPE);
        }
    }

    @Override
    public final Content.Type getContentType() {
        return this.type;
    }

    @Override
    public Content setContentType(Content.Type type) {
        this.complete();
        this.init(type);
        return this;
    }

    @Override
    public <T extends Element> T getValueElement() {
        FOMFactory factory = (FOMFactory)this.getFactory();
        return factory.getElementWrapper((Element)((Object)this.getFirstElement()));
    }

    @Override
    public <T extends Element> Content setValueElement(T value) {
        this.complete();
        if (value != null) {
            String mt;
            MimeType mtype;
            if (this.getFirstElement() != null) {
                this.getFirstElement().discard();
            }
            if ((mtype = this.getMimeType()) == null && (mt = this.getFactory().getMimeType(value)) != null) {
                this.setMimeType(mt);
                mtype = this.getMimeType();
            }
            if (value instanceof Div && !this.type.equals((Object)Content.Type.XML)) {
                this.init(Content.Type.XHTML);
            } else if (mtype == null) {
                this.init(Content.Type.XML);
            }
            OMElement el = (OMElement)((Object)(value instanceof ElementWrapper ? ((ElementWrapper)value).getInternal() : value));
            this.setFirstChild(el);
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    @Override
    public MimeType getMimeType() {
        MimeType type = null;
        String mimeType = this.getAttributeValue(TYPE);
        if (mimeType != null) {
            try {
                type = new MimeType(mimeType);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return type;
    }

    @Override
    public Content setMimeType(String type) {
        this.complete();
        try {
            if (type != null) {
                this.setAttributeValue(TYPE, new MimeType(type).toString());
            } else {
                this.removeAttribute(TYPE);
            }
        }
        catch (MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
        return this;
    }

    @Override
    public IRI getSrc() {
        return this._getUriValue(this.getAttributeValue(SRC));
    }

    @Override
    public IRI getResolvedSrc() {
        return this._resolve(this.getResolvedBaseUri(), this.getSrc());
    }

    @Override
    public Content setSrc(String src) {
        this.complete();
        if (src != null) {
            this.setAttributeValue(SRC, new IRI(src).toString());
        } else {
            this.removeAttribute(SRC);
        }
        return this;
    }

    @Override
    public DataHandler getDataHandler() {
        if (!Content.Type.MEDIA.equals((Object)this.type)) {
            throw new UnsupportedOperationException(Localizer.get("DATA.HANDLER.NOT.SUPPORTED"));
        }
        MimeType type = this.getMimeType();
        URL src = null;
        try {
            src = this.getSrc().toURL();
        }
        catch (Exception e) {
            // empty catch block
        }
        DataHandler dh = null;
        dh = src == null ? (DataHandler)DataHandlerUtils.getDataHandlerFromText(this.getText(), type != null ? type.toString() : null) : new DataHandler((DataSource)new URLDataSource(src));
        return dh;
    }

    @Override
    public Content setDataHandler(DataHandler dataHandler) {
        this.complete();
        if (!Content.Type.MEDIA.equals((Object)this.type)) {
            throw new IllegalArgumentException();
        }
        if (dataHandler.getContentType() != null) {
            try {
                this.setMimeType(dataHandler.getContentType());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this._removeAllChildren();
        this.addChild(this.factory.createOMText(dataHandler, true));
        return this;
    }

    @Override
    public String getValue() {
        String val = null;
        if (Content.Type.TEXT.equals((Object)this.type)) {
            val = this.getText();
        } else if (Content.Type.HTML.equals((Object)this.type)) {
            val = this.getText();
        } else if (Content.Type.XHTML.equals((Object)this.type)) {
            FOMDiv div = (FOMDiv)this.getFirstChildWithName(Constants.DIV);
            if (div != null) {
                val = div.getInternalValue();
            }
        } else if (Content.Type.XML.equals((Object)this.type)) {
            OMElement el = this.getFirstElement();
            if (el != null) {
                val = ((Object)el).toString();
            }
        } else if (Content.Type.MEDIA.equals((Object)this.type)) {
            val = this.getText();
        }
        return val;
    }

    public <T extends Element> T setText(Content.Type type, String value) {
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

    public <T extends Element> T setText(String value) {
        return this.setText(Content.Type.TEXT, value);
    }

    @Override
    public Content setValue(String value) {
        this.complete();
        if (value != null) {
            this.removeAttribute(SRC);
        }
        if (value != null) {
            if (Content.Type.TEXT.equals((Object)this.type)) {
                this._removeAllChildren();
                this.setText(this.type, value);
            } else if (Content.Type.HTML.equals((Object)this.type)) {
                this._removeAllChildren();
                this.setText(this.type, value);
            } else if (Content.Type.XHTML.equals((Object)this.type)) {
                IRI baseUri = null;
                Element element = null;
                value = "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + value + "</div>";
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
            } else if (Content.Type.XML.equals((Object)this.type)) {
                IRI baseUri = null;
                Element element = null;
                try {
                    baseUri = this.getResolvedBaseUri();
                    element = this._parse(value, baseUri);
                }
                catch (Exception e) {
                    // empty catch block
                }
                if (element != null) {
                    this.setValueElement(element);
                }
                try {
                    if (this.getMimeType() == null) {
                        this.setMimeType("application/xml");
                    }
                }
                catch (Exception e) {}
            } else if (Content.Type.MEDIA.equals((Object)this.type)) {
                this._removeAllChildren();
                this.setText(this.type, value);
                try {
                    if (this.getMimeType() == null) {
                        this.setMimeType("text/plain");
                    }
                }
                catch (Exception exception) {}
            }
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    @Override
    public String getWrappedValue() {
        if (Content.Type.XHTML.equals((Object)this.type)) {
            return ((Object)this.getFirstChildWithName(Constants.DIV)).toString();
        }
        return this.getText();
    }

    @Override
    public Content setWrappedValue(String wrappedValue) {
        this.complete();
        if (Content.Type.XHTML.equals((Object)this.type)) {
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
            this.setText(wrappedValue);
        }
        return this;
    }

    @Override
    public IRI getBaseUri() {
        Object el;
        if (Content.Type.XHTML.equals((Object)this.type) && (el = this.getValueElement()) != null && el.getAttributeValue(BASE) != null) {
            if (this.getAttributeValue(BASE) != null) {
                return super.getBaseUri().resolve(el.getAttributeValue(BASE));
            }
            return this._getUriValue(el.getAttributeValue(BASE));
        }
        return super.getBaseUri();
    }

    @Override
    public IRI getResolvedBaseUri() {
        Object el;
        if (Content.Type.XHTML.equals((Object)this.type) && (el = this.getValueElement()) != null && el.getAttributeValue(BASE) != null) {
            return super.getResolvedBaseUri().resolve(el.getAttributeValue(BASE));
        }
        return super.getResolvedBaseUri();
    }

    @Override
    public String getLanguage() {
        Object el;
        if (Content.Type.XHTML.equals((Object)this.type) && (el = this.getValueElement()).getAttributeValue(LANG) != null) {
            return el.getAttributeValue(LANG);
        }
        return super.getLanguage();
    }

    @Override
    public Object clone() {
        FOMContent content = (FOMContent)super.clone();
        content.type = this.type;
        return content;
    }
}


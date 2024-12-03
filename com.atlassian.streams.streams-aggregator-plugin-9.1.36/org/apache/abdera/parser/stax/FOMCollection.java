/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.parser.stax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMCollection
extends FOMExtensibleElement
implements Collection {
    private static final String[] ENTRY = new String[]{"application/atom+xml;type=\"entry\""};
    private static final String[] EMPTY = new String[0];
    private static final long serialVersionUID = -5291734055253987136L;

    protected FOMCollection(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMCollection(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMCollection(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMCollection(OMContainer parent, OMFactory factory) {
        super(COLLECTION, parent, factory);
    }

    @Override
    public String getTitle() {
        Text title = (Text)this.getFirstChild(TITLE);
        return title != null ? title.getValue() : null;
    }

    private Text setTitle(String title, Text.Type type) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Text text = fomfactory.newText(PREFIXED_TITLE, type);
        text.setValue(title);
        this._setChild(PREFIXED_TITLE, (OMElement)((Object)text));
        return text;
    }

    @Override
    public Text setTitle(String title) {
        return this.setTitle(title, Text.Type.TEXT);
    }

    @Override
    public Text setTitleAsHtml(String title) {
        return this.setTitle(title, Text.Type.HTML);
    }

    @Override
    public Text setTitleAsXHtml(String title) {
        return this.setTitle(title, Text.Type.XHTML);
    }

    @Override
    public Text getTitleElement() {
        return (Text)this.getFirstChild(TITLE);
    }

    @Override
    public IRI getHref() {
        return this._getUriValue(this.getAttributeValue(HREF));
    }

    @Override
    public IRI getResolvedHref() {
        return this._resolve(this.getResolvedBaseUri(), this.getHref());
    }

    @Override
    public Collection setHref(String href) {
        this.complete();
        if (href != null) {
            this.setAttributeValue(HREF, new IRI(href).toString());
        } else {
            this.removeAttribute(HREF);
        }
        return this;
    }

    @Override
    public String[] getAccept() {
        ArrayList<String> accept = new ArrayList<String>();
        Iterator i = this.getChildrenWithName(ACCEPT);
        if (i == null || !i.hasNext()) {
            i = this.getChildrenWithName(PRE_RFC_ACCEPT);
        }
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String t = e.getText();
            if (t == null) continue;
            accept.add(t.trim());
        }
        if (accept.size() > 0) {
            String[] list = accept.toArray(new String[accept.size()]);
            return MimeTypeHelper.condense(list);
        }
        return EMPTY;
    }

    public Collection setAccept(String mediaRange) {
        return this.setAccept(new String[]{mediaRange});
    }

    @Override
    public Collection setAccept(String ... mediaRanges) {
        this.complete();
        if (mediaRanges != null && mediaRanges.length > 0) {
            this._removeChildren(ACCEPT, true);
            this._removeChildren(PRE_RFC_ACCEPT, true);
            if (mediaRanges.length == 1 && mediaRanges[0].equals("")) {
                this.addExtension(ACCEPT);
            } else {
                for (String type : mediaRanges = MimeTypeHelper.condense(mediaRanges)) {
                    if (type.equalsIgnoreCase("entry")) {
                        this.addSimpleExtension(ACCEPT, "application/atom+xml;type=entry");
                        continue;
                    }
                    try {
                        this.addSimpleExtension(ACCEPT, new MimeType(type).toString());
                    }
                    catch (MimeTypeParseException e) {
                        throw new org.apache.abdera.util.MimeTypeParseException(e);
                    }
                }
            }
        } else {
            this._removeChildren(ACCEPT, true);
            this._removeChildren(PRE_RFC_ACCEPT, true);
        }
        return this;
    }

    @Override
    public Collection addAccepts(String mediaRange) {
        return this.addAccepts(new String[]{mediaRange});
    }

    @Override
    public Collection addAccepts(String ... mediaRanges) {
        this.complete();
        if (mediaRanges != null) {
            for (String type : mediaRanges) {
                if (this.accepts(type)) continue;
                try {
                    this.addSimpleExtension(ACCEPT, new MimeType(type).toString());
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }
        return this;
    }

    @Override
    public Collection addAcceptsEntry() {
        return this.addAccepts("application/atom+xml;type=entry");
    }

    @Override
    public Collection setAcceptsEntry() {
        return this.setAccept("application/atom+xml;type=entry");
    }

    @Override
    public Collection setAcceptsNothing() {
        return this.setAccept("");
    }

    @Override
    public boolean acceptsEntry() {
        return this.accepts("application/atom+xml;type=entry");
    }

    @Override
    public boolean acceptsNothing() {
        return this.accepts("");
    }

    @Override
    public boolean accepts(String mediaType) {
        String[] accept = this.getAccept();
        if (accept.length == 0) {
            accept = ENTRY;
        }
        for (String a : accept) {
            if (!MimeTypeHelper.isMatch(a, mediaType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean accepts(MimeType mediaType) {
        return this.accepts(mediaType.toString());
    }

    @Override
    public Categories addCategories() {
        this.complete();
        return ((FOMFactory)this.factory).newCategories(this);
    }

    @Override
    public Collection addCategories(Categories categories) {
        this.complete();
        this.addChild((OMElement)((Object)categories));
        return this;
    }

    @Override
    public Categories addCategories(String href) {
        this.complete();
        Categories cats = ((FOMFactory)this.factory).newCategories();
        cats.setHref(href);
        this.addCategories(cats);
        return cats;
    }

    @Override
    public Categories addCategories(List<Category> categories, boolean fixed, String scheme) {
        this.complete();
        Categories cats = ((FOMFactory)this.factory).newCategories();
        cats.setFixed(fixed);
        if (scheme != null) {
            cats.setScheme(scheme);
        }
        if (categories != null) {
            for (Category category : categories) {
                cats.addCategory(category);
            }
        }
        this.addCategories(cats);
        return cats;
    }

    @Override
    public List<Categories> getCategories() {
        List<Categories> list = this._getChildrenAsSet(CATEGORIES);
        if (list == null || list.size() == 0) {
            list = this._getChildrenAsSet(PRE_RFC_CATEGORIES);
        }
        return list;
    }
}


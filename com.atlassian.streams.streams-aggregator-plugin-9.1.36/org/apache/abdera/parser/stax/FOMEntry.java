/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.parser.stax;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.io.InputStreamDataSource;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.stax.FOMDocument;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.MimeTypeParseException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMEntry
extends FOMExtensibleElement
implements Entry {
    private static final long serialVersionUID = 1L;

    public FOMEntry() {
        super(Constants.ENTRY, new FOMDocument(), new FOMFactory());
    }

    protected FOMEntry(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMEntry(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMEntry(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMEntry(OMContainer parent, OMFactory factory) throws OMException {
        super(ENTRY, parent, factory);
    }

    @Override
    public Person getAuthor() {
        return (Person)((Object)this.getFirstChildWithName(AUTHOR));
    }

    @Override
    public List<Person> getAuthors() {
        return this._getChildrenAsSet(AUTHOR);
    }

    @Override
    public Entry addAuthor(Person person) {
        this.complete();
        this.addChild((OMElement)((Object)person));
        return this;
    }

    @Override
    public Person addAuthor(String name) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newAuthor(this);
        person.setName(name);
        return person;
    }

    @Override
    public Person addAuthor(String name, String email, String uri) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newAuthor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    @Override
    public List<Category> getCategories() {
        return this._getChildrenAsSet(CATEGORY);
    }

    @Override
    public List<Category> getCategories(String scheme) {
        return FOMHelper.getCategories(this, scheme);
    }

    @Override
    public Entry addCategory(Category category) {
        this.complete();
        Element el = (Element)category.getParentElement();
        if (el != null && el instanceof Categories) {
            Categories cats = (Categories)category.getParentElement();
            category = (Category)category.clone();
            try {
                if (category.getScheme() == null && cats.getScheme() != null) {
                    category.setScheme(cats.getScheme().toString());
                }
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        this.addChild((OMElement)((Object)category));
        return this;
    }

    @Override
    public Category addCategory(String term) {
        this.complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Category category = factory.newCategory(this);
        category.setTerm(term);
        return category;
    }

    @Override
    public Category addCategory(String scheme, String term, String label) {
        this.complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Category category = factory.newCategory(this);
        category.setTerm(term);
        category.setScheme(scheme);
        category.setLabel(label);
        return category;
    }

    @Override
    public Content getContentElement() {
        return (Content)((Object)this.getFirstChildWithName(CONTENT));
    }

    @Override
    public Entry setContentElement(Content content) {
        this.complete();
        if (content != null) {
            this._setChild(CONTENT, (OMElement)((Object)content));
        } else {
            this._removeChildren(CONTENT, false);
        }
        return this;
    }

    @Override
    public Content setContent(String value) {
        this.complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Content content = factory.newContent();
        content.setValue(value);
        this.setContentElement(content);
        return content;
    }

    @Override
    public Content setContentAsHtml(String value) {
        return this.setContent(value, Content.Type.HTML);
    }

    @Override
    public Content setContentAsXhtml(String value) {
        return this.setContent(value, Content.Type.XHTML);
    }

    @Override
    public Content setContent(String value, Content.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Content content = factory.newContent(type);
        content.setValue(value);
        this.setContentElement(content);
        return content;
    }

    @Override
    public Content setContent(Element value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Content content = factory.newContent();
        content.setValueElement(value);
        this.setContentElement(content);
        return content;
    }

    @Override
    public Content setContent(Element element, String mediaType) {
        try {
            if (MimeTypeHelper.isText(mediaType)) {
                throw new IllegalArgumentException();
            }
            FOMFactory factory = (FOMFactory)this.factory;
            Content content = factory.newContent(new MimeType(mediaType));
            content.setValueElement(element);
            this.setContentElement(content);
            return content;
        }
        catch (javax.activation.MimeTypeParseException e) {
            throw new MimeTypeParseException(e);
        }
    }

    @Override
    public Content setContent(DataHandler dataHandler) {
        return this.setContent(dataHandler, dataHandler.getContentType());
    }

    @Override
    public Content setContent(DataHandler dataHandler, String mediatype) {
        if (MimeTypeHelper.isText(mediatype)) {
            try {
                return this.setContent(dataHandler.getInputStream(), mediatype);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FOMFactory factory = (FOMFactory)this.factory;
        Content content = factory.newContent(Content.Type.MEDIA);
        content.setDataHandler(dataHandler);
        if (mediatype != null) {
            content.setMimeType(mediatype);
        }
        this.setContentElement(content);
        return content;
    }

    @Override
    public Content setContent(InputStream in) {
        InputStreamDataSource ds = new InputStreamDataSource(in);
        DataHandler dh = new DataHandler((DataSource)ds);
        Content content = this.setContent(dh);
        return content;
    }

    @Override
    public Content setContent(InputStream in, String mediatype) {
        if (MimeTypeHelper.isText(mediatype)) {
            try {
                StringBuilder buf = new StringBuilder();
                String charset = MimeTypeHelper.getCharset(mediatype);
                Document doc = this.getDocument();
                charset = charset != null ? charset : (doc != null ? doc.getCharset() : null);
                charset = charset != null ? charset : "UTF-8";
                InputStreamReader isr = new InputStreamReader(in, charset);
                char[] data = new char[500];
                int r = -1;
                while ((r = isr.read(data)) != -1) {
                    buf.append(data, 0, r);
                }
                return this.setContent(buf.toString(), mediatype);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        InputStreamDataSource ds = new InputStreamDataSource(in, mediatype);
        DataHandler dh = new DataHandler((DataSource)ds);
        return this.setContent(dh, mediatype);
    }

    @Override
    public Content setContent(String value, String mediatype) {
        try {
            FOMFactory factory = (FOMFactory)this.factory;
            Content content = factory.newContent(new MimeType(mediatype));
            content.setValue(value);
            content.setMimeType(mediatype);
            this.setContentElement(content);
            return content;
        }
        catch (javax.activation.MimeTypeParseException e) {
            throw new MimeTypeParseException(e);
        }
    }

    @Override
    public Content setContent(IRI uri, String mediatype) {
        try {
            FOMFactory factory = (FOMFactory)this.factory;
            Content content = factory.newContent(new MimeType(mediatype));
            content.setSrc(uri.toString());
            this.setContentElement(content);
            return content;
        }
        catch (javax.activation.MimeTypeParseException e) {
            throw new MimeTypeParseException(e);
        }
    }

    @Override
    public List<Person> getContributors() {
        return this._getChildrenAsSet(CONTRIBUTOR);
    }

    @Override
    public Entry addContributor(Person person) {
        this.complete();
        this.addChild((OMElement)((Object)person));
        return this;
    }

    @Override
    public Person addContributor(String name) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newContributor(this);
        person.setName(name);
        return person;
    }

    @Override
    public Person addContributor(String name, String email, String uri) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newContributor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    @Override
    public IRIElement getIdElement() {
        return (IRIElement)((Object)this.getFirstChildWithName(ID));
    }

    @Override
    public Entry setIdElement(IRIElement id) {
        this.complete();
        if (id != null) {
            this._setChild(ID, (OMElement)((Object)id));
        } else {
            this._removeChildren(ID, false);
        }
        return this;
    }

    @Override
    public IRI getId() {
        IRIElement id = this.getIdElement();
        return id != null ? id.getValue() : null;
    }

    @Override
    public IRIElement setId(String value) {
        return this.setId(value, false);
    }

    @Override
    public IRIElement newId() {
        return this.setId(this.getFactory().newUuidUri(), false);
    }

    @Override
    public IRIElement setId(String value, boolean normalize) {
        this.complete();
        if (value == null) {
            this._removeChildren(ID, false);
            return null;
        }
        IRIElement id = this.getIdElement();
        if (id != null) {
            if (normalize) {
                id.setNormalizedValue(value);
            } else {
                id.setValue(value);
            }
            return id;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        IRIElement iri = fomfactory.newID(this);
        iri.setValue(normalize ? IRI.normalizeString(value) : value);
        return iri;
    }

    @Override
    public List<Link> getLinks() {
        return this._getChildrenAsSet(LINK);
    }

    @Override
    public List<Link> getLinks(String rel) {
        return FOMHelper.getLinks((Element)this, rel);
    }

    @Override
    public List<Link> getLinks(String ... rels) {
        return FOMHelper.getLinks((Element)this, rels);
    }

    @Override
    public Entry addLink(Link link) {
        this.complete();
        this.addChild((OMElement)((Object)link));
        return this;
    }

    @Override
    public Link addLink(String href) {
        this.complete();
        return this.addLink(href, null);
    }

    @Override
    public Link addLink(String href, String rel) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Link link = fomfactory.newLink(this);
        link.setHref(href);
        if (rel != null) {
            link.setRel(rel);
        }
        return link;
    }

    @Override
    public Link addLink(String href, String rel, String type, String title, String hreflang, long length) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Link link = fomfactory.newLink(this);
        link.setHref(href);
        link.setRel(rel);
        link.setMimeType(type);
        link.setTitle(title);
        link.setHrefLang(hreflang);
        link.setLength(length);
        return link;
    }

    @Override
    public DateTime getPublishedElement() {
        return (DateTime)((Object)this.getFirstChildWithName(PUBLISHED));
    }

    @Override
    public Entry setPublishedElement(DateTime dateTime) {
        this.complete();
        if (dateTime != null) {
            this._setChild(PUBLISHED, (OMElement)((Object)dateTime));
        } else {
            this._removeChildren(PUBLISHED, false);
        }
        return this;
    }

    @Override
    public Date getPublished() {
        DateTime dte = this.getPublishedElement();
        return dte != null ? dte.getDate() : null;
    }

    private DateTime setPublished(AtomDate value) {
        this.complete();
        if (value == null) {
            this._removeChildren(PUBLISHED, false);
            return null;
        }
        DateTime dte = this.getPublishedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        DateTime dt = fomfactory.newPublished(this);
        dt.setValue(value);
        return dt;
    }

    @Override
    public DateTime setPublished(Date value) {
        return this.setPublished(value != null ? AtomDate.valueOf(value) : null);
    }

    @Override
    public DateTime setPublished(String value) {
        return this.setPublished(value != null ? AtomDate.valueOf(value) : null);
    }

    @Override
    public Text getRightsElement() {
        return this.getTextElement(RIGHTS);
    }

    @Override
    public Entry setRightsElement(Text text) {
        this.complete();
        this.setTextElement(RIGHTS, text, false);
        return this;
    }

    @Override
    public Text setRights(String value) {
        this.complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newRights();
        text.setValue(value);
        this.setRightsElement(text);
        return text;
    }

    @Override
    public Text setRightsAsHtml(String value) {
        return this.setRights(value, Text.Type.HTML);
    }

    @Override
    public Text setRightsAsXhtml(String value) {
        return this.setRights(value, Text.Type.XHTML);
    }

    @Override
    public Text setRights(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newRights(type);
        text.setValue(value);
        this.setRightsElement(text);
        return text;
    }

    @Override
    public Text setRights(Div value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newRights(Text.Type.XHTML);
        text.setValueElement(value);
        this.setRightsElement(text);
        return text;
    }

    @Override
    public String getRights() {
        return this.getText(RIGHTS);
    }

    @Override
    public Source getSource() {
        return (Source)((Object)this.getFirstChildWithName(SOURCE));
    }

    @Override
    public Entry setSource(Source source) {
        this.complete();
        if (source != null) {
            if (source instanceof Feed) {
                source = ((Feed)source).getAsSource();
            }
            this._setChild(SOURCE, (OMElement)((Object)source));
        } else {
            this._removeChildren(SOURCE, false);
        }
        return this;
    }

    @Override
    public Text getSummaryElement() {
        return this.getTextElement(SUMMARY);
    }

    @Override
    public Entry setSummaryElement(Text text) {
        this.complete();
        this.setTextElement(SUMMARY, text, false);
        return this;
    }

    @Override
    public Text setSummary(String value) {
        this.complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSummary();
        text.setValue(value);
        this.setSummaryElement(text);
        return text;
    }

    @Override
    public Text setSummaryAsHtml(String value) {
        return this.setSummary(value, Text.Type.HTML);
    }

    @Override
    public Text setSummaryAsXhtml(String value) {
        return this.setSummary(value, Text.Type.XHTML);
    }

    @Override
    public Text setSummary(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSummary(type);
        text.setValue(value);
        this.setSummaryElement(text);
        return text;
    }

    @Override
    public Text setSummary(Div value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSummary(Text.Type.XHTML);
        text.setValueElement(value);
        this.setSummaryElement(text);
        return text;
    }

    @Override
    public String getSummary() {
        return this.getText(SUMMARY);
    }

    @Override
    public Text getTitleElement() {
        return this.getTextElement(TITLE);
    }

    @Override
    public Entry setTitleElement(Text title) {
        this.complete();
        this.setTextElement(TITLE, title, false);
        return this;
    }

    @Override
    public Text setTitle(String value) {
        this.complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newTitle();
        text.setValue(value);
        this.setTitleElement(text);
        return text;
    }

    @Override
    public Text setTitleAsHtml(String value) {
        return this.setTitle(value, Text.Type.HTML);
    }

    @Override
    public Text setTitleAsXhtml(String value) {
        return this.setTitle(value, Text.Type.XHTML);
    }

    @Override
    public Text setTitle(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newTitle(type);
        text.setValue(value);
        this.setTitleElement(text);
        return text;
    }

    @Override
    public Text setTitle(Div value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newTitle(Text.Type.XHTML);
        text.setValueElement(value);
        this.setTitleElement(text);
        return text;
    }

    @Override
    public String getTitle() {
        return this.getText(TITLE);
    }

    @Override
    public DateTime getUpdatedElement() {
        return (DateTime)((Object)this.getFirstChildWithName(UPDATED));
    }

    @Override
    public Entry setUpdatedElement(DateTime updated) {
        this.complete();
        if (updated != null) {
            this._setChild(UPDATED, (OMElement)((Object)updated));
        } else {
            this._removeChildren(UPDATED, false);
        }
        return this;
    }

    @Override
    public Date getUpdated() {
        DateTime dte = this.getUpdatedElement();
        return dte != null ? dte.getDate() : null;
    }

    private DateTime setUpdated(AtomDate value) {
        this.complete();
        if (value == null) {
            this._removeChildren(UPDATED, false);
            return null;
        }
        DateTime dte = this.getUpdatedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        DateTime dt = fomfactory.newUpdated(this);
        dt.setValue(value);
        return dt;
    }

    @Override
    public DateTime setUpdated(Date value) {
        return this.setUpdated(value != null ? AtomDate.valueOf(value) : null);
    }

    @Override
    public DateTime setUpdated(String value) {
        return this.setUpdated(value != null ? AtomDate.valueOf(value) : null);
    }

    @Override
    public DateTime getEditedElement() {
        DateTime dt = (DateTime)((Object)this.getFirstChildWithName(EDITED));
        if (dt == null) {
            dt = (DateTime)((Object)this.getFirstChildWithName(PRE_RFC_EDITED));
        }
        return dt;
    }

    @Override
    public void setEditedElement(DateTime updated) {
        this.complete();
        this.declareNamespace("http://www.w3.org/2007/app", "app");
        this._removeChildren(PRE_RFC_EDITED, false);
        if (updated != null) {
            this._setChild(EDITED, (OMElement)((Object)updated));
        } else {
            this._removeChildren(EDITED, false);
        }
    }

    @Override
    public Date getEdited() {
        DateTime dte = this.getEditedElement();
        return dte != null ? dte.getDate() : null;
    }

    private DateTime setEdited(AtomDate value) {
        this.complete();
        this.declareNamespace("http://www.w3.org/2007/app", "app");
        if (value == null) {
            this._removeChildren(PRE_RFC_EDITED, false);
            this._removeChildren(EDITED, false);
            return null;
        }
        DateTime dte = this.getEditedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        DateTime dt = fomfactory.newEdited(this);
        dt.setValue(value);
        return dt;
    }

    @Override
    public DateTime setEdited(Date value) {
        return this.setEdited(value != null ? AtomDate.valueOf(value) : null);
    }

    @Override
    public DateTime setEdited(String value) {
        return this.setUpdated(value != null ? AtomDate.valueOf(value) : null);
    }

    @Override
    public Control getControl(boolean create) {
        Control control = this.getControl();
        if (control == null && create) {
            control = this.getFactory().newControl();
            this.setControl(control);
        }
        return control;
    }

    @Override
    public Control getControl() {
        Control control = (Control)((Object)this.getFirstChildWithName(CONTROL));
        if (control == null) {
            control = (Control)((Object)this.getFirstChildWithName(PRE_RFC_CONTROL));
        }
        return control;
    }

    @Override
    public Entry setControl(Control control) {
        this.complete();
        this._removeChildren(PRE_RFC_CONTROL, true);
        if (control != null) {
            this._setChild(CONTROL, (OMElement)((Object)control));
        } else {
            this._removeChildren(CONTROL, false);
        }
        return this;
    }

    @Override
    public Link getLink(String rel) {
        List<Link> links = this.getLinks(rel);
        Link link = null;
        if (links.size() > 0) {
            link = links.get(0);
        }
        return link;
    }

    @Override
    public Link getAlternateLink() {
        return this.getLink("alternate");
    }

    @Override
    public Link getEnclosureLink() {
        return this.getLink("enclosure");
    }

    @Override
    public Link getEditLink() {
        return this.getLink("edit");
    }

    @Override
    public Link getSelfLink() {
        return this.getLink("self");
    }

    @Override
    public Link getEditMediaLink() {
        return this.getLink("edit-media");
    }

    @Override
    public IRI getLinkResolvedHref(String rel) {
        Link link = this.getLink(rel);
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public IRI getAlternateLinkResolvedHref() {
        Link link = this.getAlternateLink();
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public IRI getEnclosureLinkResolvedHref() {
        Link link = this.getEnclosureLink();
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public IRI getEditLinkResolvedHref() {
        Link link = this.getEditLink();
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public IRI getEditMediaLinkResolvedHref() {
        Link link = this.getEditMediaLink();
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public IRI getSelfLinkResolvedHref() {
        Link link = this.getSelfLink();
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public String getContent() {
        Content content = this.getContentElement();
        return content != null ? content.getValue() : null;
    }

    @Override
    public InputStream getContentStream() throws IOException {
        Content content = this.getContentElement();
        DataHandler dh = content.getDataHandler();
        return dh.getInputStream();
    }

    @Override
    public IRI getContentSrc() {
        Content content = this.getContentElement();
        return content != null ? content.getResolvedSrc() : null;
    }

    @Override
    public Content.Type getContentType() {
        Content content = this.getContentElement();
        return content != null ? content.getContentType() : null;
    }

    @Override
    public Text.Type getRightsType() {
        Text text = this.getRightsElement();
        return text != null ? text.getTextType() : null;
    }

    @Override
    public Text.Type getSummaryType() {
        Text text = this.getSummaryElement();
        return text != null ? text.getTextType() : null;
    }

    @Override
    public Text.Type getTitleType() {
        Text text = this.getTitleElement();
        return text != null ? text.getTextType() : null;
    }

    @Override
    public MimeType getContentMimeType() {
        Content content = this.getContentElement();
        return content != null ? content.getMimeType() : null;
    }

    @Override
    public Link getAlternateLink(String type, String hreflang) {
        return this.selectLink(this.getLinks("alternate"), type, hreflang);
    }

    @Override
    public IRI getAlternateLinkResolvedHref(String type, String hreflang) {
        Link link = this.getAlternateLink(type, hreflang);
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public Link getEditMediaLink(String type, String hreflang) {
        return this.selectLink(this.getLinks("edit-media"), type, hreflang);
    }

    @Override
    public IRI getEditMediaLinkResolvedHref(String type, String hreflang) {
        Link link = this.getEditMediaLink(type, hreflang);
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public Entry setDraft(boolean draft) {
        this.complete();
        Control control = this.getControl();
        if (control == null && draft) {
            control = ((FOMFactory)this.factory).newControl(this);
        }
        if (control != null) {
            control.setDraft(draft);
        }
        return this;
    }

    @Override
    public boolean isDraft() {
        Control control = this.getControl();
        return control != null ? control.isDraft() : false;
    }

    @Override
    public Control addControl() {
        this.complete();
        Control control = this.getControl();
        if (control == null) {
            control = ((FOMFactory)this.factory).newControl(this);
        }
        return control;
    }
}


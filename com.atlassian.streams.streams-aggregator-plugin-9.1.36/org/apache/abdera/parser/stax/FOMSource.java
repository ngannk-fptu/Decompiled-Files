/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRIHelper;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMFeed;
import org.apache.abdera.parser.stax.util.FOMHelper;
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
public class FOMSource
extends FOMExtensibleElement
implements Source {
    private static final long serialVersionUID = 9153127297531238021L;

    protected FOMSource(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMSource(OMContainer parent, OMFactory factory) throws OMException {
        super(SOURCE, parent, factory);
    }

    protected FOMSource(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMSource(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
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
    public <T extends Source> T addAuthor(Person person) {
        this.complete();
        this.addChild((OMElement)((Object)person));
        return (T)this;
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
    public <T extends Source> T addCategory(Category category) {
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
        return (T)this;
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
    public List<Person> getContributors() {
        return this._getChildrenAsSet(CONTRIBUTOR);
    }

    @Override
    public <T extends Source> T addContributor(Person person) {
        this.complete();
        this.addChild((OMElement)((Object)person));
        return (T)this;
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
    public <T extends Source> T setIdElement(IRIElement id) {
        this.complete();
        if (id != null) {
            this._setChild(ID, (OMElement)((Object)id));
        } else {
            this._removeChildren(ID, false);
        }
        return (T)this;
    }

    @Override
    public IRI getId() {
        IRIElement id = this.getIdElement();
        return id != null ? id.getValue() : null;
    }

    @Override
    public IRIElement setId(String value) {
        this.complete();
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
    public <T extends Source> T addLink(Link link) {
        this.complete();
        this.addChild((OMElement)((Object)link));
        return (T)this;
    }

    @Override
    public Link addLink(String href) {
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
    public Text getRightsElement() {
        return this.getTextElement(RIGHTS);
    }

    @Override
    public <T extends Source> T setRightsElement(Text text) {
        this.complete();
        this.setTextElement(RIGHTS, text, false);
        return (T)this;
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
    public Text getSubtitleElement() {
        return this.getTextElement(SUBTITLE);
    }

    @Override
    public <T extends Source> T setSubtitleElement(Text text) {
        this.complete();
        this.setTextElement(SUBTITLE, text, false);
        return (T)this;
    }

    @Override
    public Text setSubtitle(String value) {
        this.complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSubtitle();
        text.setValue(value);
        this.setSubtitleElement(text);
        return text;
    }

    @Override
    public Text setSubtitleAsHtml(String value) {
        return this.setSubtitle(value, Text.Type.HTML);
    }

    @Override
    public Text setSubtitleAsXhtml(String value) {
        return this.setSubtitle(value, Text.Type.XHTML);
    }

    @Override
    public Text setSubtitle(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSubtitle(type);
        text.setValue(value);
        this.setSubtitleElement(text);
        return text;
    }

    @Override
    public Text setSubtitle(Div value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSubtitle(Text.Type.XHTML);
        text.setValueElement(value);
        this.setSubtitleElement(text);
        return text;
    }

    @Override
    public String getSubtitle() {
        return this.getText(SUBTITLE);
    }

    @Override
    public Text getTitleElement() {
        return this.getTextElement(TITLE);
    }

    @Override
    public <T extends Source> T setTitleElement(Text text) {
        this.complete();
        this.setTextElement(TITLE, text, false);
        return (T)this;
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
    public <T extends Source> T setUpdatedElement(DateTime updated) {
        this.complete();
        if (updated != null) {
            this._setChild(UPDATED, (OMElement)((Object)updated));
        } else {
            this._removeChildren(UPDATED, false);
        }
        return (T)this;
    }

    @Override
    public String getUpdatedString() {
        DateTime dte = this.getUpdatedElement();
        return dte != null ? dte.getString() : null;
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
    public Generator getGenerator() {
        return (Generator)((Object)this.getFirstChildWithName(GENERATOR));
    }

    @Override
    public <T extends Source> T setGenerator(Generator generator) {
        this.complete();
        if (generator != null) {
            this._setChild(GENERATOR, (OMElement)((Object)generator));
        } else {
            this._removeChildren(GENERATOR, false);
        }
        return (T)this;
    }

    @Override
    public Generator setGenerator(String uri, String version, String value) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Generator generator = fomfactory.newGenerator(this);
        if (uri != null) {
            generator.setUri(uri);
        }
        if (version != null) {
            generator.setVersion(version);
        }
        if (value != null) {
            generator.setText(value);
        }
        return generator;
    }

    @Override
    public IRIElement getIconElement() {
        return (IRIElement)((Object)this.getFirstChildWithName(ICON));
    }

    @Override
    public <T extends Source> T setIconElement(IRIElement iri) {
        this.complete();
        if (iri != null) {
            this._setChild(ICON, (OMElement)((Object)iri));
        } else {
            this._removeChildren(ICON, false);
        }
        return (T)this;
    }

    @Override
    public IRIElement setIcon(String value) {
        this.complete();
        if (value == null) {
            this._removeChildren(ICON, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        IRIElement iri = fomfactory.newIcon(this);
        iri.setValue(value);
        return iri;
    }

    @Override
    public IRI getIcon() {
        IRIElement iri = this.getIconElement();
        IRI uri = iri != null ? iri.getResolvedValue() : null;
        return IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri) ? null : uri;
    }

    @Override
    public IRIElement getLogoElement() {
        return (IRIElement)((Object)this.getFirstChildWithName(LOGO));
    }

    @Override
    public <T extends Source> T setLogoElement(IRIElement iri) {
        this.complete();
        if (iri != null) {
            this._setChild(LOGO, (OMElement)((Object)iri));
        } else {
            this._removeChildren(LOGO, false);
        }
        return (T)this;
    }

    @Override
    public IRIElement setLogo(String value) {
        this.complete();
        if (value == null) {
            this._removeChildren(LOGO, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        IRIElement iri = fomfactory.newLogo(this);
        iri.setValue(value);
        return iri;
    }

    @Override
    public IRI getLogo() {
        IRIElement iri = this.getLogoElement();
        IRI uri = iri != null ? iri.getResolvedValue() : null;
        return IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri) ? null : uri;
    }

    @Override
    public Link getLink(String rel) {
        List<Link> self = this.getLinks(rel);
        Link link = null;
        if (self.size() > 0) {
            link = self.get(0);
        }
        return link;
    }

    @Override
    public Link getSelfLink() {
        return this.getLink("self");
    }

    @Override
    public Link getAlternateLink() {
        return this.getLink("alternate");
    }

    @Override
    public IRI getLinkResolvedHref(String rel) {
        Link link = this.getLink(rel);
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public IRI getSelfLinkResolvedHref() {
        Link link = this.getSelfLink();
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public IRI getAlternateLinkResolvedHref() {
        Link link = this.getAlternateLink();
        return link != null ? link.getResolvedHref() : null;
    }

    @Override
    public Text.Type getRightsType() {
        Text text = this.getRightsElement();
        return text != null ? text.getTextType() : null;
    }

    @Override
    public Text.Type getSubtitleType() {
        Text text = this.getSubtitleElement();
        return text != null ? text.getTextType() : null;
    }

    @Override
    public Text.Type getTitleType() {
        Text text = this.getTitleElement();
        return text != null ? text.getTextType() : null;
    }

    @Override
    public Collection getCollection() {
        Collection coll = (Collection)this.getFirstChild(COLLECTION);
        if (coll == null) {
            coll = (Collection)this.getFirstChild(PRE_RFC_COLLECTION);
        }
        return coll;
    }

    @Override
    public <T extends Source> T setCollection(Collection collection) {
        this.complete();
        if (collection != null) {
            this._removeChildren(PRE_RFC_COLLECTION, true);
            this._setChild(COLLECTION, (OMElement)((Object)collection));
        } else {
            this._removeChildren(COLLECTION, false);
        }
        return (T)this;
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
    public Feed getAsFeed() {
        FOMFeed feed = (FOMFeed)((FOMFactory)this.factory).newFeed();
        Iterator i = this.getChildElements();
        while (i.hasNext()) {
            FOMElement child = (FOMElement)i.next();
            if (child.getQName().equals(ENTRY)) continue;
            feed.addChild((OMNode)child.clone());
        }
        try {
            if (this.getBaseUri() != null) {
                feed.setBaseUri(this.getBaseUri());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return feed;
    }
}


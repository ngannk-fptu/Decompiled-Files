/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.parser.stax;

import java.util.ArrayList;
import java.util.List;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.ExtensionFactoryMap;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.stax.FOMBuilder;
import org.apache.abdera.parser.stax.FOMCategories;
import org.apache.abdera.parser.stax.FOMCategory;
import org.apache.abdera.parser.stax.FOMCollection;
import org.apache.abdera.parser.stax.FOMComment;
import org.apache.abdera.parser.stax.FOMContent;
import org.apache.abdera.parser.stax.FOMControl;
import org.apache.abdera.parser.stax.FOMDateTime;
import org.apache.abdera.parser.stax.FOMDiv;
import org.apache.abdera.parser.stax.FOMDocument;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.parser.stax.FOMEntry;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFeed;
import org.apache.abdera.parser.stax.FOMGenerator;
import org.apache.abdera.parser.stax.FOMIRI;
import org.apache.abdera.parser.stax.FOMLink;
import org.apache.abdera.parser.stax.FOMMultipartCollection;
import org.apache.abdera.parser.stax.FOMParser;
import org.apache.abdera.parser.stax.FOMPerson;
import org.apache.abdera.parser.stax.FOMProcessingInstruction;
import org.apache.abdera.parser.stax.FOMService;
import org.apache.abdera.parser.stax.FOMSource;
import org.apache.abdera.parser.stax.FOMText;
import org.apache.abdera.parser.stax.FOMTextValue;
import org.apache.abdera.parser.stax.FOMWorkspace;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMFactory
extends OMLinkedListImplFactory
implements Factory,
Constants,
ExtensionFactory {
    private final ExtensionFactoryMap factoriesMap;
    private final Abdera abdera;

    public static void registerAsDefault() {
        System.setProperty("org.apache.axiom.om.OMMetaFactory", FOMFactory.class.getName());
    }

    public FOMFactory() {
        this(new Abdera());
    }

    public FOMFactory(Abdera abdera) {
        List<ExtensionFactory> f = abdera.getConfiguration().getExtensionFactories();
        this.factoriesMap = new ExtensionFactoryMap(f != null ? new ArrayList<ExtensionFactory>(f) : new ArrayList());
        this.abdera = abdera;
    }

    @Override
    public Parser newParser() {
        return new FOMParser(this.abdera);
    }

    @Override
    public <T extends Element> Document<T> newDocument() {
        return new FOMDocument(this);
    }

    public <T extends Element> Document<T> newDocument(OMXMLParserWrapper parserWrapper) {
        return new FOMDocument(parserWrapper, this);
    }

    public <T extends Element> Document<T> newDocument(T root, OMXMLParserWrapper parserWrapper) {
        FOMDocument doc = (FOMDocument)this.newDocument(parserWrapper);
        doc.setRoot(root);
        return doc;
    }

    @Override
    public Service newService(Base parent) {
        return new FOMService((OMContainer)((Object)parent), this);
    }

    @Override
    public Workspace newWorkspace() {
        return this.newWorkspace(null);
    }

    @Override
    public Workspace newWorkspace(Element parent) {
        return new FOMWorkspace((OMContainer)((Object)parent), this);
    }

    @Override
    public Collection newCollection() {
        return this.newCollection(null);
    }

    @Override
    public Collection newCollection(Element parent) {
        return new FOMCollection((OMContainer)((Object)parent), this);
    }

    public Collection newMultipartCollection(Element parent) {
        return new FOMMultipartCollection((OMContainer)((Object)parent), this);
    }

    @Override
    public Feed newFeed() {
        Document doc = this.newDocument();
        return this.newFeed(doc);
    }

    @Override
    public Entry newEntry() {
        Document doc = this.newDocument();
        return this.newEntry(doc);
    }

    @Override
    public Service newService() {
        Document doc = this.newDocument();
        return this.newService(doc);
    }

    @Override
    public Feed newFeed(Base parent) {
        return new FOMFeed((OMContainer)((Object)parent), this);
    }

    @Override
    public Entry newEntry(Base parent) {
        return new FOMEntry((OMContainer)((Object)parent), this);
    }

    @Override
    public Category newCategory() {
        return this.newCategory(null);
    }

    @Override
    public Category newCategory(Element parent) {
        return new FOMCategory((OMContainer)((Object)parent), this);
    }

    @Override
    public Content newContent() {
        return this.newContent(Content.Type.TEXT);
    }

    @Override
    public Content newContent(Content.Type type) {
        if (type == null) {
            type = Content.Type.TEXT;
        }
        return this.newContent(type, null);
    }

    @Override
    public Content newContent(Content.Type type, Element parent) {
        if (type == null) {
            type = Content.Type.TEXT;
        }
        FOMContent content = new FOMContent(type, (OMContainer)((Object)parent), (OMFactory)this);
        if (type.equals((Object)Content.Type.XML)) {
            content.setMimeType("application/xml");
        }
        return content;
    }

    @Override
    public Content newContent(MimeType mediaType) {
        return this.newContent(mediaType, null);
    }

    @Override
    public Content newContent(MimeType mediaType, Element parent) {
        Content.Type type = MimeTypeHelper.isXml(mediaType.toString()) ? Content.Type.XML : Content.Type.MEDIA;
        Content content = this.newContent(type, parent);
        content.setMimeType(mediaType.toString());
        return content;
    }

    @Override
    public DateTime newDateTime(QName qname, Element parent) {
        return new FOMDateTime(qname, (OMContainer)((Object)parent), this);
    }

    @Override
    public Generator newDefaultGenerator() {
        return this.newDefaultGenerator(null);
    }

    @Override
    public Generator newDefaultGenerator(Element parent) {
        Generator generator = this.newGenerator(parent);
        generator.setVersion("v1.0-SNAPSHOT");
        generator.setText("Abdera");
        generator.setUri("http://abdera.apache.org");
        return generator;
    }

    @Override
    public Generator newGenerator() {
        return this.newGenerator(null);
    }

    @Override
    public Generator newGenerator(Element parent) {
        return new FOMGenerator((OMContainer)((Object)parent), this);
    }

    @Override
    public IRIElement newID() {
        return this.newID(null);
    }

    @Override
    public IRIElement newID(Element parent) {
        return new FOMIRI(Constants.ID, (OMContainer)((Object)parent), this);
    }

    @Override
    public IRIElement newIRIElement(QName qname, Element parent) {
        return new FOMIRI(qname, (OMContainer)((Object)parent), this);
    }

    @Override
    public Link newLink() {
        return this.newLink(null);
    }

    @Override
    public Link newLink(Element parent) {
        return new FOMLink((OMContainer)((Object)parent), this);
    }

    @Override
    public Person newPerson(QName qname, Element parent) {
        return new FOMPerson(qname, (OMContainer)((Object)parent), this);
    }

    @Override
    public Source newSource() {
        return this.newSource(null);
    }

    @Override
    public Source newSource(Element parent) {
        return new FOMSource((OMContainer)((Object)parent), this);
    }

    @Override
    public Text newText(QName qname, Text.Type type) {
        return this.newText(qname, type, null);
    }

    @Override
    public Text newText(QName qname, Text.Type type, Element parent) {
        if (type == null) {
            type = Text.Type.TEXT;
        }
        return new FOMText(type, qname, (OMContainer)((Object)parent), (OMFactory)this);
    }

    @Override
    public <T extends Element> T newElement(QName qname) {
        return this.newElement(qname, null);
    }

    @Override
    public <T extends Element> T newElement(QName qname, Base parent) {
        return this.newExtensionElement(qname, parent);
    }

    @Override
    public <T extends Element> T newExtensionElement(QName qname) {
        return this.newExtensionElement(qname, null);
    }

    @Override
    public <T extends Element> T newExtensionElement(QName qname, Base parent) {
        String ns = qname.getNamespaceURI();
        Element el = (Element)((Object)this.createElement(qname, (OMContainer)((Object)parent), this, null));
        return (T)("http://www.w3.org/2005/Atom".equals(ns) || "http://www.w3.org/2007/app".equals(ns) ? el : this.factoriesMap.getElementWrapper(el));
    }

    @Override
    public Control newControl() {
        return this.newControl(null);
    }

    @Override
    public Control newControl(Element parent) {
        return new FOMControl((OMContainer)((Object)parent), this);
    }

    @Override
    public DateTime newPublished() {
        return this.newPublished(null);
    }

    @Override
    public DateTime newPublished(Element parent) {
        return this.newDateTime(Constants.PUBLISHED, parent);
    }

    @Override
    public DateTime newUpdated() {
        return this.newUpdated(null);
    }

    @Override
    public DateTime newUpdated(Element parent) {
        return this.newDateTime(Constants.UPDATED, parent);
    }

    @Override
    public DateTime newEdited() {
        return this.newEdited(null);
    }

    @Override
    public DateTime newEdited(Element parent) {
        return this.newDateTime(Constants.EDITED, parent);
    }

    @Override
    public IRIElement newIcon() {
        return this.newIcon(null);
    }

    @Override
    public IRIElement newIcon(Element parent) {
        return this.newIRIElement(Constants.ICON, parent);
    }

    @Override
    public IRIElement newLogo() {
        return this.newLogo(null);
    }

    @Override
    public IRIElement newLogo(Element parent) {
        return this.newIRIElement(Constants.LOGO, parent);
    }

    @Override
    public IRIElement newUri() {
        return this.newUri(null);
    }

    @Override
    public IRIElement newUri(Element parent) {
        return this.newIRIElement(Constants.URI, parent);
    }

    @Override
    public Person newAuthor() {
        return this.newAuthor(null);
    }

    @Override
    public Person newAuthor(Element parent) {
        return this.newPerson(Constants.AUTHOR, parent);
    }

    @Override
    public Person newContributor() {
        return this.newContributor(null);
    }

    @Override
    public Person newContributor(Element parent) {
        return this.newPerson(Constants.CONTRIBUTOR, parent);
    }

    @Override
    public Text newTitle() {
        return this.newTitle(Text.Type.TEXT);
    }

    @Override
    public Text newTitle(Element parent) {
        return this.newTitle(Text.Type.TEXT, parent);
    }

    @Override
    public Text newTitle(Text.Type type) {
        return this.newTitle(type, null);
    }

    @Override
    public Text newTitle(Text.Type type, Element parent) {
        return this.newText(Constants.TITLE, type, parent);
    }

    @Override
    public Text newSubtitle() {
        return this.newSubtitle(Text.Type.TEXT);
    }

    @Override
    public Text newSubtitle(Element parent) {
        return this.newSubtitle(Text.Type.TEXT, parent);
    }

    @Override
    public Text newSubtitle(Text.Type type) {
        return this.newSubtitle(type, null);
    }

    @Override
    public Text newSubtitle(Text.Type type, Element parent) {
        return this.newText(Constants.SUBTITLE, type, parent);
    }

    @Override
    public Text newSummary() {
        return this.newSummary(Text.Type.TEXT);
    }

    @Override
    public Text newSummary(Element parent) {
        return this.newSummary(Text.Type.TEXT, parent);
    }

    @Override
    public Text newSummary(Text.Type type) {
        return this.newSummary(type, null);
    }

    @Override
    public Text newSummary(Text.Type type, Element parent) {
        return this.newText(Constants.SUMMARY, type, parent);
    }

    @Override
    public Text newRights() {
        return this.newRights(Text.Type.TEXT);
    }

    @Override
    public Text newRights(Element parent) {
        return this.newRights(Text.Type.TEXT, parent);
    }

    @Override
    public Text newRights(Text.Type type) {
        return this.newRights(type, null);
    }

    @Override
    public Text newRights(Text.Type type, Element parent) {
        return this.newText(Constants.RIGHTS, type, parent);
    }

    @Override
    public Element newName() {
        return this.newName(null);
    }

    @Override
    public Element newName(Element parent) {
        return this.newElement(Constants.NAME, parent);
    }

    @Override
    public Element newEmail() {
        return this.newEmail(null);
    }

    @Override
    public Element newEmail(Element parent) {
        return this.newElement(Constants.EMAIL, parent);
    }

    @Override
    public Div newDiv() {
        return this.newDiv(null);
    }

    @Override
    public Div newDiv(Base parent) {
        return new FOMDiv(DIV, (OMContainer)((Object)parent), this);
    }

    protected OMElement createElement(QName qname, OMContainer parent, OMFactory factory, Object objecttype) {
        FOMElement element = null;
        OMNamespace namespace = this.createOMNamespace(qname.getNamespaceURI(), qname.getPrefix());
        if (FEED.equals(qname)) {
            element = new FOMFeed(qname.getLocalPart(), namespace, parent, factory);
        } else if (SERVICE.equals(qname) || PRE_RFC_SERVICE.equals(qname)) {
            element = new FOMService(qname.getLocalPart(), namespace, parent, factory);
        } else if (ENTRY.equals(qname)) {
            element = new FOMEntry(qname.getLocalPart(), namespace, parent, factory);
        } else if (AUTHOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), namespace, parent, factory);
        } else if (CATEGORY.equals(qname)) {
            element = new FOMCategory(qname.getLocalPart(), namespace, parent, factory);
        } else if (CONTENT.equals(qname)) {
            Content.Type type = (Content.Type)((Object)objecttype);
            element = new FOMContent(qname.getLocalPart(), namespace, type, parent, factory);
        } else if (CONTRIBUTOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), namespace, parent, factory);
        } else if (GENERATOR.equals(qname)) {
            element = new FOMGenerator(qname.getLocalPart(), namespace, parent, factory);
        } else if (ICON.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
        } else if (ID.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
        } else if (LOGO.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
        } else if (LINK.equals(qname)) {
            element = new FOMLink(qname.getLocalPart(), namespace, parent, factory);
        } else if (PUBLISHED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), namespace, parent, factory);
        } else if (RIGHTS.equals(qname)) {
            Text.Type type = (Text.Type)((Object)objecttype);
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else if (SOURCE.equals(qname)) {
            element = new FOMSource(qname.getLocalPart(), namespace, parent, factory);
        } else if (SUBTITLE.equals(qname)) {
            Text.Type type = (Text.Type)((Object)objecttype);
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else if (SUMMARY.equals(qname)) {
            Text.Type type = (Text.Type)((Object)objecttype);
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else if (TITLE.equals(qname)) {
            Text.Type type = (Text.Type)((Object)objecttype);
            element = new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
        } else {
            element = UPDATED.equals(qname) ? new FOMDateTime(qname.getLocalPart(), namespace, parent, factory) : (WORKSPACE.equals(qname) || PRE_RFC_WORKSPACE.equals(qname) ? new FOMWorkspace(qname.getLocalPart(), namespace, parent, factory) : (COLLECTION.equals(qname) || PRE_RFC_COLLECTION.equals(qname) ? new FOMCollection(qname.getLocalPart(), namespace, parent, factory) : (NAME.equals(qname) ? new FOMElement(qname.getLocalPart(), namespace, parent, factory) : (EMAIL.equals(qname) ? new FOMElement(qname.getLocalPart(), namespace, parent, factory) : (URI.equals(qname) ? new FOMIRI(qname.getLocalPart(), namespace, parent, factory) : (CONTROL.equals(qname) || PRE_RFC_CONTROL.equals(qname) ? new FOMControl(qname.getLocalPart(), namespace, parent, factory) : (DIV.equals(qname) ? new FOMDiv(qname.getLocalPart(), namespace, parent, factory) : (CATEGORIES.equals(qname) || PRE_RFC_CATEGORIES.equals(qname) ? new FOMCategories(qname.getLocalPart(), namespace, parent, factory) : (EDITED.equals(qname) || PRE_RFC_EDITED.equals(qname) ? new FOMDateTime(qname.getLocalPart(), namespace, parent, factory) : (parent instanceof ExtensibleElement || parent instanceof Document ? new FOMExtensibleElement(qname, parent, this) : new FOMExtensibleElement(qname, null, this)))))))))));
        }
        return element;
    }

    protected OMElement createElement(QName qname, OMContainer parent, FOMBuilder builder) {
        FOMElement element = null;
        if (FEED.equals(qname)) {
            element = new FOMFeed(qname.getLocalPart(), parent, this, builder);
        } else if (SERVICE.equals(qname) || PRE_RFC_SERVICE.equals(qname)) {
            element = new FOMService(qname.getLocalPart(), parent, this, builder);
        } else if (ENTRY.equals(qname)) {
            element = new FOMEntry(qname.getLocalPart(), parent, this, builder);
        } else if (AUTHOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), parent, this, builder);
        } else if (CATEGORY.equals(qname)) {
            element = new FOMCategory(qname.getLocalPart(), parent, this, builder);
        } else if (CONTENT.equals(qname)) {
            Content.Type type = builder.getContentType();
            if (type == null) {
                type = Content.Type.TEXT;
            }
            element = new FOMContent(qname.getLocalPart(), type, parent, this, builder);
        } else if (CONTRIBUTOR.equals(qname)) {
            element = new FOMPerson(qname.getLocalPart(), parent, this, builder);
        } else if (GENERATOR.equals(qname)) {
            element = new FOMGenerator(qname.getLocalPart(), parent, this, builder);
        } else if (ICON.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (ID.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (LOGO.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (LINK.equals(qname)) {
            element = new FOMLink(qname.getLocalPart(), parent, this, builder);
        } else if (PUBLISHED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), parent, this, builder);
        } else if (RIGHTS.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null) {
                type = Text.Type.TEXT;
            }
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (SOURCE.equals(qname)) {
            element = new FOMSource(qname.getLocalPart(), parent, this, builder);
        } else if (SUBTITLE.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null) {
                type = Text.Type.TEXT;
            }
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (SUMMARY.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null) {
                type = Text.Type.TEXT;
            }
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (TITLE.equals(qname)) {
            Text.Type type = builder.getTextType();
            if (type == null) {
                type = Text.Type.TEXT;
            }
            element = new FOMText(type, qname.getLocalPart(), parent, this, builder);
        } else if (UPDATED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), parent, this, builder);
        } else if (WORKSPACE.equals(qname) || PRE_RFC_WORKSPACE.equals(qname)) {
            element = new FOMWorkspace(qname.getLocalPart(), parent, this, builder);
        } else if (COLLECTION.equals(qname) || PRE_RFC_COLLECTION.equals(qname)) {
            element = new FOMCollection(qname.getLocalPart(), parent, this, builder);
        } else if (NAME.equals(qname)) {
            element = new FOMElement(qname.getLocalPart(), parent, this, builder);
        } else if (EMAIL.equals(qname)) {
            element = new FOMElement(qname.getLocalPart(), parent, this, builder);
        } else if (URI.equals(qname)) {
            element = new FOMIRI(qname.getLocalPart(), parent, this, builder);
        } else if (CONTROL.equals(qname) || PRE_RFC_CONTROL.equals(qname)) {
            element = new FOMControl(qname.getLocalPart(), parent, this, builder);
        } else if (DIV.equals(qname)) {
            element = new FOMDiv(qname.getLocalPart(), parent, this, builder);
        } else if (CATEGORIES.equals(qname) || PRE_RFC_CATEGORIES.equals(qname)) {
            element = new FOMCategories(qname.getLocalPart(), parent, this, builder);
        } else if (EDITED.equals(qname) || PRE_RFC_EDITED.equals(qname)) {
            element = new FOMDateTime(qname.getLocalPart(), parent, this, builder);
        } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
            element = new FOMExtensibleElement(qname.getLocalPart(), parent, this, builder);
        }
        return element;
    }

    @Override
    public Factory registerExtension(ExtensionFactory factory) {
        this.factoriesMap.addFactory(factory);
        return this;
    }

    @Override
    public Categories newCategories() {
        Document doc = this.newDocument();
        return this.newCategories(doc);
    }

    @Override
    public Categories newCategories(Base parent) {
        return new FOMCategories((OMContainer)((Object)parent), this);
    }

    @Override
    public String newUuidUri() {
        return FOMHelper.generateUuid();
    }

    @Override
    public <T extends Element> T getElementWrapper(Element internal) {
        if (internal == null) {
            return null;
        }
        String ns = internal.getQName().getNamespaceURI();
        return (T)("http://www.w3.org/2005/Atom".equals(ns) || "http://www.w3.org/2007/app".equals(ns) || internal.getQName().equals(DIV) ? internal : this.factoriesMap.getElementWrapper(internal));
    }

    @Override
    public String[] getNamespaces() {
        return this.factoriesMap.getNamespaces();
    }

    @Override
    public boolean handlesNamespace(String namespace) {
        return this.factoriesMap.handlesNamespace(namespace);
    }

    @Override
    public Abdera getAbdera() {
        return this.abdera;
    }

    @Override
    public <T extends Base> String getMimeType(T base) {
        String type = this.factoriesMap.getMimeType(base);
        return type;
    }

    @Override
    public String[] listExtensionFactories() {
        return this.factoriesMap.listExtensionFactories();
    }

    @Override
    public OMText createOMText(Object arg0, boolean arg1) {
        return new FOMTextValue(null, arg0, arg1, (OMFactory)this, false);
    }

    @Override
    public OMText createOMText(OMContainer arg0, char[] arg1, int arg2) {
        return new FOMTextValue(arg0, arg1, arg2, (OMFactory)this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, QName arg1, int arg2) {
        return new FOMTextValue(arg0, arg1, arg2, (OMFactory)this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, QName arg1) {
        return new FOMTextValue(arg0, arg1, (OMFactory)this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, String arg1, int arg2) {
        return new FOMTextValue(arg0, arg1, arg2, (OMFactory)this, false);
    }

    @Override
    public OMText createOMText(OMContainer arg0, String arg1, String arg2, boolean arg3) {
        return new FOMTextValue(arg0, arg1, arg2, arg3, this);
    }

    @Override
    public OMText createOMText(OMContainer arg0, String arg1) {
        return new FOMTextValue(arg0, arg1, (OMFactory)this);
    }

    @Override
    public OMText createOMText(String arg0, int arg1) {
        return new FOMTextValue(arg0, arg1, (OMFactory)this);
    }

    @Override
    public OMText createOMText(String arg0, String arg1, boolean arg2) {
        return new FOMTextValue(arg0, arg1, arg2, (OMFactory)this);
    }

    @Override
    public OMText createOMText(String arg0) {
        return new FOMTextValue(arg0, (OMFactory)this);
    }

    @Override
    public OMComment createOMComment(OMContainer arg0, String arg1) {
        return new FOMComment(arg0, arg1, this, false);
    }

    @Override
    public OMProcessingInstruction createOMProcessingInstruction(OMContainer arg0, String arg1, String arg2) {
        return new FOMProcessingInstruction(arg0, arg1, arg2, this, false);
    }
}


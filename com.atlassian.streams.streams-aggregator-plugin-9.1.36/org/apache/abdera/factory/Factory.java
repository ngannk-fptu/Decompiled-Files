/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.factory;

import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Factory {
    public Parser newParser();

    public <T extends Element> Document<T> newDocument();

    public Service newService();

    public Service newService(Base var1);

    public Workspace newWorkspace();

    public Workspace newWorkspace(Element var1);

    public Collection newCollection();

    public Collection newCollection(Element var1);

    public Feed newFeed();

    public Feed newFeed(Base var1);

    public Entry newEntry();

    public Entry newEntry(Base var1);

    public Category newCategory();

    public Category newCategory(Element var1);

    public Content newContent();

    public Content newContent(Content.Type var1);

    public Content newContent(Content.Type var1, Element var2);

    public Content newContent(MimeType var1);

    public Content newContent(MimeType var1, Element var2);

    public DateTime newPublished();

    public DateTime newPublished(Element var1);

    public DateTime newUpdated();

    public DateTime newUpdated(Element var1);

    public DateTime newEdited();

    public DateTime newEdited(Element var1);

    public DateTime newDateTime(QName var1, Element var2);

    public Generator newDefaultGenerator();

    public Generator newDefaultGenerator(Element var1);

    public Generator newGenerator();

    public Generator newGenerator(Element var1);

    public IRIElement newID();

    public IRIElement newID(Element var1);

    public IRIElement newIcon();

    public IRIElement newIcon(Element var1);

    public IRIElement newLogo();

    public IRIElement newLogo(Element var1);

    public IRIElement newUri();

    public IRIElement newUri(Element var1);

    public IRIElement newIRIElement(QName var1, Element var2);

    public Link newLink();

    public Link newLink(Element var1);

    public Person newAuthor();

    public Person newAuthor(Element var1);

    public Person newContributor();

    public Person newContributor(Element var1);

    public Person newPerson(QName var1, Element var2);

    public Source newSource();

    public Source newSource(Element var1);

    public Text newText(QName var1, Text.Type var2);

    public Text newText(QName var1, Text.Type var2, Element var3);

    public Text newTitle();

    public Text newTitle(Element var1);

    public Text newTitle(Text.Type var1);

    public Text newTitle(Text.Type var1, Element var2);

    public Text newSubtitle();

    public Text newSubtitle(Element var1);

    public Text newSubtitle(Text.Type var1);

    public Text newSubtitle(Text.Type var1, Element var2);

    public Text newSummary();

    public Text newSummary(Element var1);

    public Text newSummary(Text.Type var1);

    public Text newSummary(Text.Type var1, Element var2);

    public Text newRights();

    public Text newRights(Element var1);

    public Text newRights(Text.Type var1);

    public Text newRights(Text.Type var1, Element var2);

    public Element newName();

    public Element newName(Element var1);

    public Element newEmail();

    public Element newEmail(Element var1);

    public <T extends Element> T newElement(QName var1);

    public <T extends Element> T newElement(QName var1, Base var2);

    public <T extends Element> T newExtensionElement(QName var1);

    public <T extends Element> T newExtensionElement(QName var1, Base var2);

    public Control newControl();

    public Control newControl(Element var1);

    public Div newDiv();

    public Div newDiv(Base var1);

    public Factory registerExtension(ExtensionFactory var1);

    public Categories newCategories();

    public Categories newCategories(Base var1);

    public String newUuidUri();

    public Abdera getAbdera();

    public <T extends Base> String getMimeType(T var1);

    public String[] listExtensionFactories();
}


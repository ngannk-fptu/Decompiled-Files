/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.MimeType
 */
package org.apache.abdera.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.MimeType;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Entry
extends ExtensibleElement {
    public Person getAuthor();

    public List<Person> getAuthors();

    public Entry addAuthor(Person var1);

    public Person addAuthor(String var1);

    public Person addAuthor(String var1, String var2, String var3);

    public List<Category> getCategories();

    public List<Category> getCategories(String var1);

    public Entry addCategory(Category var1);

    public Category addCategory(String var1);

    public Category addCategory(String var1, String var2, String var3);

    public Content getContentElement();

    public Entry setContentElement(Content var1);

    public Content setContent(String var1);

    public Content setContentAsHtml(String var1);

    public Content setContentAsXhtml(String var1);

    public Content setContent(String var1, Content.Type var2);

    public Content setContent(Element var1);

    public Content setContent(Element var1, String var2);

    public Content setContent(DataHandler var1);

    public Content setContent(DataHandler var1, String var2);

    public Content setContent(InputStream var1);

    public Content setContent(InputStream var1, String var2);

    public Content setContent(String var1, String var2);

    public Content setContent(IRI var1, String var2);

    public String getContent();

    public InputStream getContentStream() throws IOException;

    public IRI getContentSrc();

    public Content.Type getContentType();

    public MimeType getContentMimeType();

    public List<Person> getContributors();

    public Entry addContributor(Person var1);

    public Person addContributor(String var1);

    public Person addContributor(String var1, String var2, String var3);

    public IRIElement getIdElement();

    public Entry setIdElement(IRIElement var1);

    public IRI getId();

    public IRIElement setId(String var1);

    public IRIElement newId();

    public IRIElement setId(String var1, boolean var2);

    public List<Link> getLinks();

    public List<Link> getLinks(String var1);

    public List<Link> getLinks(String ... var1);

    public Entry addLink(Link var1);

    public Link addLink(String var1);

    public Link addLink(String var1, String var2);

    public Link addLink(String var1, String var2, String var3, String var4, String var5, long var6);

    public DateTime getPublishedElement();

    public Entry setPublishedElement(DateTime var1);

    public Date getPublished();

    public DateTime setPublished(Date var1);

    public DateTime setPublished(String var1);

    public Text getRightsElement();

    public Entry setRightsElement(Text var1);

    public Text setRights(String var1);

    public Text setRightsAsHtml(String var1);

    public Text setRightsAsXhtml(String var1);

    public Text setRights(String var1, Text.Type var2);

    public Text setRights(Div var1);

    public String getRights();

    public Text.Type getRightsType();

    public Source getSource();

    public Entry setSource(Source var1);

    public Text getSummaryElement();

    public Entry setSummaryElement(Text var1);

    public Text setSummary(String var1);

    public Text setSummaryAsHtml(String var1);

    public Text setSummaryAsXhtml(String var1);

    public Text setSummary(String var1, Text.Type var2);

    public Text setSummary(Div var1);

    public String getSummary();

    public Text.Type getSummaryType();

    public Text getTitleElement();

    public Entry setTitleElement(Text var1);

    public Text setTitle(String var1);

    public Text setTitleAsHtml(String var1);

    public Text setTitleAsXhtml(String var1);

    public Text setTitle(String var1, Text.Type var2);

    public Text setTitle(Div var1);

    public String getTitle();

    public Text.Type getTitleType();

    public DateTime getUpdatedElement();

    public Entry setUpdatedElement(DateTime var1);

    public Date getUpdated();

    public DateTime setUpdated(Date var1);

    public DateTime setUpdated(String var1);

    public DateTime getEditedElement();

    public void setEditedElement(DateTime var1);

    public Date getEdited();

    public DateTime setEdited(Date var1);

    public DateTime setEdited(String var1);

    public Control getControl(boolean var1);

    public Control getControl();

    public Entry setControl(Control var1);

    public Entry setDraft(boolean var1);

    public boolean isDraft();

    public Link getLink(String var1);

    public Link getAlternateLink();

    public Link getAlternateLink(String var1, String var2);

    public Link getEnclosureLink();

    public Link getEditLink();

    public Link getEditMediaLink();

    public Link getEditMediaLink(String var1, String var2);

    public Link getSelfLink();

    public IRI getLinkResolvedHref(String var1);

    public IRI getAlternateLinkResolvedHref();

    public IRI getAlternateLinkResolvedHref(String var1, String var2);

    public IRI getEnclosureLinkResolvedHref();

    public IRI getEditLinkResolvedHref();

    public IRI getEditMediaLinkResolvedHref();

    public IRI getEditMediaLinkResolvedHref(String var1, String var2);

    public IRI getSelfLinkResolvedHref();

    public Control addControl();
}


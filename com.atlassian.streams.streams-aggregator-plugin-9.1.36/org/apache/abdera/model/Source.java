/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.util.Date;
import java.util.List;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Source
extends ExtensibleElement {
    public Person getAuthor();

    public List<Person> getAuthors();

    public <T extends Source> T addAuthor(Person var1);

    public Person addAuthor(String var1);

    public Person addAuthor(String var1, String var2, String var3);

    public List<Category> getCategories();

    public List<Category> getCategories(String var1);

    public <T extends Source> T addCategory(Category var1);

    public Category addCategory(String var1);

    public Category addCategory(String var1, String var2, String var3);

    public List<Person> getContributors();

    public <T extends Source> T addContributor(Person var1);

    public Person addContributor(String var1);

    public Person addContributor(String var1, String var2, String var3);

    public Generator getGenerator();

    public <T extends Source> T setGenerator(Generator var1);

    public Generator setGenerator(String var1, String var2, String var3);

    public IRIElement getIconElement();

    public <T extends Source> T setIconElement(IRIElement var1);

    public IRIElement setIcon(String var1);

    public IRI getIcon();

    public IRIElement getIdElement();

    public <T extends Source> T setIdElement(IRIElement var1);

    public IRI getId();

    public IRIElement setId(String var1);

    public IRIElement newId();

    public IRIElement setId(String var1, boolean var2);

    public List<Link> getLinks();

    public List<Link> getLinks(String var1);

    public List<Link> getLinks(String ... var1);

    public <T extends Source> T addLink(Link var1);

    public Link addLink(String var1);

    public Link addLink(String var1, String var2);

    public Link addLink(String var1, String var2, String var3, String var4, String var5, long var6);

    public IRIElement getLogoElement();

    public <T extends Source> T setLogoElement(IRIElement var1);

    public IRIElement setLogo(String var1);

    public IRI getLogo();

    public Text getRightsElement();

    public <T extends Source> T setRightsElement(Text var1);

    public Text setRights(String var1);

    public Text setRightsAsHtml(String var1);

    public Text setRightsAsXhtml(String var1);

    public Text setRights(String var1, Text.Type var2);

    public Text setRights(Div var1);

    public String getRights();

    public Text.Type getRightsType();

    public Text getSubtitleElement();

    public <T extends Source> T setSubtitleElement(Text var1);

    public Text setSubtitle(String var1);

    public Text setSubtitleAsHtml(String var1);

    public Text setSubtitleAsXhtml(String var1);

    public Text setSubtitle(String var1, Text.Type var2);

    public Text setSubtitle(Div var1);

    public String getSubtitle();

    public Text.Type getSubtitleType();

    public Text getTitleElement();

    public <T extends Source> T setTitleElement(Text var1);

    public Text setTitle(String var1);

    public Text setTitleAsHtml(String var1);

    public Text setTitleAsXhtml(String var1);

    public Text setTitle(String var1, Text.Type var2);

    public Text setTitle(Div var1);

    public String getTitle();

    public Text.Type getTitleType();

    public DateTime getUpdatedElement();

    public <T extends Source> T setUpdatedElement(DateTime var1);

    public String getUpdatedString();

    public Date getUpdated();

    public DateTime setUpdated(Date var1);

    public DateTime setUpdated(String var1);

    public Link getLink(String var1);

    public Link getSelfLink();

    public Link getAlternateLink();

    public Link getAlternateLink(String var1, String var2);

    public IRI getLinkResolvedHref(String var1);

    public IRI getSelfLinkResolvedHref();

    public IRI getAlternateLinkResolvedHref();

    public IRI getAlternateLinkResolvedHref(String var1, String var2);

    public Collection getCollection();

    public <T extends Source> T setCollection(Collection var1);

    public Feed getAsFeed();
}


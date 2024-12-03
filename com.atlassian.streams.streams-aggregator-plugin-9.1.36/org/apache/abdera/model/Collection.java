/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.model;

import java.util.List;
import javax.activation.MimeType;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Collection
extends ExtensibleElement {
    public String getTitle();

    public Text setTitle(String var1);

    public Text setTitleAsHtml(String var1);

    public Text setTitleAsXHtml(String var1);

    public Text getTitleElement();

    public IRI getHref();

    public IRI getResolvedHref();

    public Collection setHref(String var1);

    public String[] getAccept();

    public Collection setAccept(String ... var1);

    public boolean accepts(String var1);

    public boolean acceptsEntry();

    public boolean acceptsNothing();

    public Collection setAcceptsEntry();

    public Collection setAcceptsNothing();

    public Collection addAccepts(String var1);

    public Collection addAccepts(String ... var1);

    public Collection addAcceptsEntry();

    public boolean accepts(MimeType var1);

    public List<Categories> getCategories();

    public Categories addCategories();

    public Categories addCategories(String var1);

    public Collection addCategories(Categories var1);

    public Categories addCategories(List<Category> var1, boolean var2, String var3);
}


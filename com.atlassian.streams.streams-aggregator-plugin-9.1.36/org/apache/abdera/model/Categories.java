/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.util.List;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.ExtensibleElement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Categories
extends ExtensibleElement {
    public IRI getHref();

    public IRI getResolvedHref();

    public Categories setHref(String var1);

    public boolean isFixed();

    public Categories setFixed(boolean var1);

    public IRI getScheme();

    public Categories setScheme(String var1);

    public List<Category> getCategories();

    public List<Category> getCategories(String var1);

    public List<Category> getCategoriesWithScheme();

    public List<Category> getCategoriesWithScheme(String var1);

    public Categories addCategory(Category var1);

    public Category addCategory(String var1);

    public Category addCategory(String var1, String var2, String var3);

    public boolean contains(String var1);

    public boolean contains(String var1, String var2);

    public boolean isOutOfLine();
}


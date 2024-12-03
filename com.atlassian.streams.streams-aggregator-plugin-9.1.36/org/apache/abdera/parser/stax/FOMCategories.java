/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.parser.stax.FOMDocument;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMCategories
extends FOMExtensibleElement
implements Categories {
    private static final long serialVersionUID = 5480273546375102411L;

    public FOMCategories() {
        super(CATEGORIES, new FOMDocument(), new FOMFactory());
        this.init();
    }

    protected FOMCategories(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
        this.init();
    }

    protected FOMCategories(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
        this.init();
    }

    protected FOMCategories(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMCategories(OMContainer parent, OMFactory factory) throws OMException {
        super(CATEGORIES, parent, factory);
        this.init();
    }

    private void init() {
        this.declareNamespace("http://www.w3.org/2005/Atom", "atom");
    }

    @Override
    public Categories addCategory(Category category) {
        this.complete();
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
    public List<Category> getCategories() {
        return this._getChildrenAsSet(CATEGORY);
    }

    @Override
    public List<Category> getCategories(String scheme) {
        return FOMHelper.getCategories(this, scheme);
    }

    private List<Category> copyCategoriesWithScheme(List<Category> cats) {
        ArrayList<Category> newcats = new ArrayList<Category>();
        IRI scheme = this.getScheme();
        for (Category cat : cats) {
            Category newcat = (Category)cat.clone();
            if (newcat.getScheme() == null && scheme != null) {
                newcat.setScheme(scheme.toString());
            }
            newcats.add(newcat);
        }
        return newcats;
    }

    @Override
    public List<Category> getCategoriesWithScheme() {
        return this.copyCategoriesWithScheme(this.getCategories());
    }

    @Override
    public List<Category> getCategoriesWithScheme(String scheme) {
        return this.copyCategoriesWithScheme(this.getCategories(scheme));
    }

    @Override
    public IRI getScheme() {
        String value = this.getAttributeValue(SCHEME);
        return value != null ? new IRI(value) : null;
    }

    @Override
    public boolean isFixed() {
        String value = this.getAttributeValue(FIXED);
        return value != null && value.equals("yes");
    }

    @Override
    public Categories setFixed(boolean fixed) {
        this.complete();
        if (fixed && !this.isFixed()) {
            this.setAttributeValue(FIXED, "yes");
        } else if (!fixed && this.isFixed()) {
            this.removeAttribute(FIXED);
        }
        return this;
    }

    @Override
    public Categories setScheme(String scheme) {
        this.complete();
        if (scheme != null) {
            this.setAttributeValue(SCHEME, new IRI(scheme).toString());
        } else {
            this.removeAttribute(SCHEME);
        }
        return this;
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
    public Categories setHref(String href) {
        this.complete();
        if (href != null) {
            this.setAttributeValue(HREF, new IRI(href).toString());
        } else {
            this.removeAttribute(HREF);
        }
        return this;
    }

    @Override
    public boolean contains(String term) {
        return this.contains(term, null);
    }

    @Override
    public boolean contains(String term, String scheme) {
        List<Category> categories = this.getCategories();
        IRI catscheme = this.getScheme();
        IRI uri = scheme != null ? new IRI(scheme) : catscheme;
        for (Category category : categories) {
            IRI s;
            String t = category.getTerm();
            IRI iRI = s = category.getScheme() != null ? category.getScheme() : catscheme;
            if (!t.equals(term) || !(uri != null ? uri.equals(s) : s == null)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isOutOfLine() {
        boolean answer = false;
        try {
            answer = this.getHref() != null;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return answer;
    }
}


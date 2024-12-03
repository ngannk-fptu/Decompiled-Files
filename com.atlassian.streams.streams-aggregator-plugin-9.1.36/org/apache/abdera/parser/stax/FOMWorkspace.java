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
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMWorkspace
extends FOMExtensibleElement
implements Workspace {
    private static final long serialVersionUID = -421749865550509424L;

    protected FOMWorkspace(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMWorkspace(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMWorkspace(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
    }

    protected FOMWorkspace(OMContainer parent, OMFactory factory) throws OMException {
        super(WORKSPACE, parent, factory);
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
    public List<Collection> getCollections() {
        List<Collection> list = this._getChildrenAsSet(COLLECTION);
        if (list == null || list.size() == 0) {
            list = this._getChildrenAsSet(PRE_RFC_COLLECTION);
        }
        return list;
    }

    @Override
    public Collection getCollection(String title) {
        List<Collection> cols = this.getCollections();
        Collection col = null;
        for (Collection c : cols) {
            if (!c.getTitle().equals(title)) continue;
            col = c;
            break;
        }
        return col;
    }

    @Override
    public Workspace addCollection(Collection collection) {
        this.complete();
        this.addChild((OMElement)((Object)collection));
        return this;
    }

    @Override
    public Collection addCollection(String title, String href) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Collection collection = fomfactory.newCollection(this);
        collection.setTitle(title);
        collection.setHref(href);
        return collection;
    }

    @Override
    public Collection addMultipartCollection(String title, String href) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Collection collection = fomfactory.newMultipartCollection(this);
        collection.setTitle(title);
        collection.setHref(href);
        return collection;
    }

    @Override
    public Collection getCollectionThatAccepts(MimeType ... types) {
        Collection collection = null;
        for (Collection coll : this.getCollections()) {
            int matches = 0;
            for (MimeType type : types) {
                if (!coll.accepts(type)) continue;
                ++matches;
            }
            if (matches != types.length) continue;
            collection = coll;
            break;
        }
        return collection;
    }

    @Override
    public Collection getCollectionThatAccepts(String ... types) {
        Collection collection = null;
        for (Collection coll : this.getCollections()) {
            int matches = 0;
            for (String type : types) {
                if (!coll.accepts(type)) continue;
                ++matches;
            }
            if (matches != types.length) continue;
            collection = coll;
            break;
        }
        return collection;
    }

    @Override
    public List<Collection> getCollectionsThatAccept(MimeType ... types) {
        ArrayList<Collection> collections = new ArrayList<Collection>();
        for (Collection coll : this.getCollections()) {
            int matches = 0;
            for (MimeType type : types) {
                if (!coll.accepts(type)) continue;
                ++matches;
            }
            if (matches != types.length) continue;
            collections.add(coll);
        }
        return collections;
    }

    @Override
    public List<Collection> getCollectionsThatAccept(String ... types) {
        ArrayList<Collection> collections = new ArrayList<Collection>();
        for (Collection coll : this.getCollections()) {
            int matches = 0;
            for (String type : types) {
                if (!coll.accepts(type)) continue;
                ++matches;
            }
            if (matches != types.length) continue;
            collections.add(coll);
        }
        return collections;
    }
}


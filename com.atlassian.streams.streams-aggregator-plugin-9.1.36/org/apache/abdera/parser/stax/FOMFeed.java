/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Source;
import org.apache.abdera.parser.stax.FOMDocument;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMSource;
import org.apache.abdera.util.Constants;
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
public class FOMFeed
extends FOMSource
implements Feed {
    private static final long serialVersionUID = 4552921210185524535L;

    public FOMFeed() {
        super(Constants.FEED, new FOMDocument(), new FOMFactory());
    }

    protected FOMFeed(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMFeed(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMFeed(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMFeed(OMContainer parent, OMFactory factory) throws OMException {
        super(FEED, parent, factory);
    }

    @Override
    public List<Entry> getEntries() {
        return this._getChildrenAsSet(ENTRY);
    }

    @Override
    public Feed addEntry(Entry entry) {
        this.complete();
        this.addChild((OMElement)((Object)entry));
        return this;
    }

    @Override
    public Entry addEntry() {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        return fomfactory.newEntry(this);
    }

    @Override
    public Feed insertEntry(Entry entry) {
        this.complete();
        OMElement el = this.getFirstChildWithName(ENTRY);
        if (el == null) {
            this.addEntry(entry);
        } else {
            entry.setParentElement(this);
            el.insertSiblingBefore((OMElement)((Object)entry));
        }
        return this;
    }

    @Override
    public Entry insertEntry() {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Entry entry = fomfactory.newEntry(null);
        this.insertEntry(entry);
        return entry;
    }

    @Override
    public Source getAsSource() {
        FOMSource source = (FOMSource)((FOMFactory)this.factory).newSource(null);
        Iterator i = this.getChildElements();
        while (i.hasNext()) {
            FOMElement child = (FOMElement)i.next();
            if (child.getQName().equals(ENTRY)) continue;
            source.addChild((OMNode)child.clone());
        }
        try {
            if (this.getBaseUri() != null) {
                source.setBaseUri(this.getBaseUri());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return source;
    }

    @Override
    public void addChild(OMNode node) {
        OMElement el;
        if (this.isComplete() && node instanceof OMElement && !(node instanceof Entry) && (el = this.getFirstChildWithName(ENTRY)) != null) {
            el.insertSiblingBefore(node);
            return;
        }
        super.addChild(node);
    }

    @Override
    public Feed sortEntriesByUpdated(boolean new_first) {
        this.complete();
        this.sortEntries(new UpdatedComparator(new_first));
        return this;
    }

    @Override
    public Feed sortEntriesByEdited(boolean new_first) {
        this.complete();
        this.sortEntries(new EditedComparator(new_first));
        return this;
    }

    @Override
    public Feed sortEntries(Comparator<Entry> comparator) {
        this.complete();
        if (comparator == null) {
            return this;
        }
        List<Entry> entries = this.getEntries();
        Entry[] a = entries.toArray(new Entry[entries.size()]);
        Arrays.sort(a, comparator);
        for (Entry e : entries) {
            e.discard();
        }
        for (Entry e : a) {
            this.addEntry(e);
        }
        return this;
    }

    @Override
    public Entry getEntry(String id) {
        if (id == null) {
            return null;
        }
        List<Entry> l = this.getEntries();
        for (Entry e : l) {
            IRI eid = e.getId();
            if (eid == null || !eid.equals(new IRI(id))) continue;
            return e;
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class UpdatedComparator
    implements Comparator<Entry> {
        private boolean new_first = true;

        UpdatedComparator(boolean new_first) {
            this.new_first = new_first;
        }

        @Override
        public int compare(Entry o1, Entry o2) {
            Date d1 = o1.getUpdated();
            Date d2 = o2.getUpdated();
            if (d1 == null && d2 == null) {
                return 0;
            }
            if (d1 == null && d2 != null) {
                return -1;
            }
            if (d1 != null && d2 == null) {
                return 1;
            }
            int r = d1.compareTo(d2);
            return this.new_first ? -r : r;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class EditedComparator
    implements Comparator<Entry> {
        private boolean new_first = true;

        EditedComparator(boolean new_first) {
            this.new_first = new_first;
        }

        @Override
        public int compare(Entry o1, Entry o2) {
            Date d1 = o1.getEdited();
            Date d2 = o2.getEdited();
            if (d1 == null) {
                d1 = o1.getUpdated();
            }
            if (d2 == null) {
                d2 = o2.getUpdated();
            }
            if (d1 == null && d2 == null) {
                return 0;
            }
            if (d1 == null && d2 != null) {
                return -1;
            }
            if (d1 != null && d2 == null) {
                return 1;
            }
            int r = d1.compareTo(d2);
            return this.new_first ? -r : r;
        }
    }
}


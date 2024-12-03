/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.parser.stax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.stax.FOMDocument;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMService
extends FOMExtensibleElement
implements Service {
    private static final long serialVersionUID = 7982751563668891240L;

    public FOMService() {
        super(Constants.SERVICE, new FOMDocument(), new FOMFactory());
        this.declareAtomNs();
    }

    protected FOMService(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
        this.declareAtomNs();
    }

    protected FOMService(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
        this.declareAtomNs();
    }

    protected FOMService(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMService(OMContainer parent, OMFactory factory) throws OMException {
        super(SERVICE, parent, factory);
        this.declareAtomNs();
    }

    private void declareAtomNs() {
        this.declareDefaultNamespace("http://www.w3.org/2007/app");
        this.declareNamespace("http://www.w3.org/2005/Atom", "atom");
    }

    @Override
    public List<Workspace> getWorkspaces() {
        List<Workspace> list = this._getChildrenAsSet(WORKSPACE);
        if (list == null || list.size() == 0) {
            list = this._getChildrenAsSet(PRE_RFC_WORKSPACE);
        }
        return list;
    }

    @Override
    public Workspace getWorkspace(String title) {
        List<Workspace> workspaces = this.getWorkspaces();
        Workspace workspace = null;
        for (Workspace w : workspaces) {
            if (!w.getTitle().equals(title)) continue;
            workspace = w;
            break;
        }
        return workspace;
    }

    @Override
    public Service addWorkspace(Workspace workspace) {
        this.complete();
        this.addChild((OMElement)((Object)workspace));
        return this;
    }

    @Override
    public Workspace addWorkspace(String title) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Workspace workspace = fomfactory.newWorkspace(this);
        workspace.setTitle(title);
        return workspace;
    }

    @Override
    public Collection getCollection(String workspace, String collection) {
        Collection col = null;
        Workspace w = this.getWorkspace(workspace);
        if (w != null) {
            col = w.getCollection(collection);
        }
        return col;
    }

    @Override
    public Collection getCollectionThatAccepts(MimeType ... types) {
        Workspace workspace;
        Collection collection = null;
        Iterator<Workspace> i$ = this.getWorkspaces().iterator();
        while (i$.hasNext() && (collection = (workspace = i$.next()).getCollectionThatAccepts(types)) == null) {
        }
        return collection;
    }

    @Override
    public Collection getCollectionThatAccepts(String ... types) {
        Workspace workspace;
        Collection collection = null;
        Iterator<Workspace> i$ = this.getWorkspaces().iterator();
        while (i$.hasNext() && (collection = (workspace = i$.next()).getCollectionThatAccepts(types)) == null) {
        }
        return collection;
    }

    @Override
    public List<Collection> getCollectionsThatAccept(MimeType ... types) {
        ArrayList<Collection> collections = new ArrayList<Collection>();
        for (Workspace workspace : this.getWorkspaces()) {
            List<Collection> colls = workspace.getCollectionsThatAccept(types);
            collections.addAll(colls);
        }
        return collections;
    }

    @Override
    public List<Collection> getCollectionsThatAccept(String ... types) {
        ArrayList<Collection> collections = new ArrayList<Collection>();
        for (Workspace workspace : this.getWorkspaces()) {
            List<Collection> colls = workspace.getCollectionsThatAccept(types);
            collections.addAll(colls);
        }
        return collections;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeExistsException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.commons.nodetype.NodeTypeStorage;

public class NodeTypeStorageImpl
implements NodeTypeStorage {
    private final Map<Name, QNodeTypeDefinition> definitions = new HashMap<Name, QNodeTypeDefinition>();

    @Override
    public Iterator<QNodeTypeDefinition> getAllDefinitions() throws RepositoryException {
        return this.definitions.values().iterator();
    }

    @Override
    public Iterator<QNodeTypeDefinition> getDefinitions(Name[] nodeTypeNames) throws NoSuchNodeTypeException, RepositoryException {
        if (nodeTypeNames == null) {
            return this.definitions.values().iterator();
        }
        ArrayList<QNodeTypeDefinition> defs = new ArrayList<QNodeTypeDefinition>(nodeTypeNames.length);
        for (Name name : nodeTypeNames) {
            if (!this.definitions.containsKey(name)) {
                throw new NoSuchNodeTypeException("{" + name.getNamespaceURI() + "}" + name.getLocalName());
            }
            defs.add(this.definitions.get(name));
        }
        return defs.iterator();
    }

    @Override
    public void registerNodeTypes(QNodeTypeDefinition[] nodeTypeDefs, boolean allowUpdate) throws RepositoryException {
        if (nodeTypeDefs == null) {
            throw new IllegalArgumentException("nodeTypeDefs must not be null");
        }
        if (!allowUpdate) {
            for (QNodeTypeDefinition ntd : nodeTypeDefs) {
                Name name = ntd.getName();
                if (!this.definitions.containsKey(name)) continue;
                throw new NodeTypeExistsException("{" + name.getNamespaceURI() + "}" + name.getLocalName());
            }
        }
        for (QNodeTypeDefinition ntd : nodeTypeDefs) {
            this.definitions.put(ntd.getName(), ntd);
        }
    }

    @Override
    public void unregisterNodeTypes(Name[] nodeTypeNames) throws NoSuchNodeTypeException, RepositoryException {
        for (Name name : nodeTypeNames) {
            if (this.definitions.containsKey(name)) continue;
            throw new NoSuchNodeTypeException("{" + name.getNamespaceURI() + "}" + name.getLocalName());
        }
        for (Name name : nodeTypeNames) {
            this.definitions.remove(name);
        }
    }
}


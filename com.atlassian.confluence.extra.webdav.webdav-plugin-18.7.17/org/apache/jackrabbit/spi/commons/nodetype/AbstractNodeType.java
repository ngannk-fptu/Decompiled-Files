/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.ArrayList;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.commons.iterator.NodeTypeIteratorAdapter;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.AbstractNodeTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNodeType
implements NodeType {
    private static final Logger log = LoggerFactory.getLogger(AbstractNodeType.class);
    protected final AbstractNodeTypeManager ntMgr;
    protected final QNodeTypeDefinition ntd;
    protected final NamePathResolver resolver;

    public AbstractNodeType(QNodeTypeDefinition ntd, AbstractNodeTypeManager ntMgr, NamePathResolver resolver) {
        this.ntd = ntd;
        this.ntMgr = ntMgr;
        this.resolver = resolver;
    }

    public QNodeTypeDefinition getDefinition() {
        return this.ntd;
    }

    @Override
    public String getName() {
        try {
            return this.resolver.getJCRName(this.ntd.getName());
        }
        catch (NamespaceException e) {
            log.error("encountered unregistered namespace in node type name", (Throwable)e);
            return this.ntd.getName().toString();
        }
    }

    @Override
    public boolean isAbstract() {
        return this.ntd.isAbstract();
    }

    @Override
    public boolean isMixin() {
        return this.ntd.isMixin();
    }

    @Override
    public boolean isQueryable() {
        return this.ntd.isQueryable();
    }

    @Override
    public String[] getDeclaredSupertypeNames() {
        Name[] ntNames = this.ntd.getSupertypes();
        String[] supertypes = new String[ntNames.length];
        for (int i = 0; i < ntNames.length; ++i) {
            try {
                supertypes[i] = this.resolver.getJCRName(ntNames[i]);
                continue;
            }
            catch (NamespaceException e) {
                log.error("encountered unregistered namespace in node type name", (Throwable)e);
                supertypes[i] = ntNames[i].toString();
            }
        }
        return supertypes;
    }

    @Override
    public NodeType[] getDeclaredSupertypes() {
        Name[] ntNames = this.ntd.getSupertypes();
        NodeType[] supertypes = new NodeType[ntNames.length];
        for (int i = 0; i < ntNames.length; ++i) {
            try {
                supertypes[i] = this.ntMgr.getNodeType(ntNames[i]);
                continue;
            }
            catch (NoSuchNodeTypeException e) {
                log.error("undefined supertype", (Throwable)e);
                return new NodeType[0];
            }
        }
        return supertypes;
    }

    @Override
    public NodeTypeIterator getDeclaredSubtypes() {
        return this.getSubtypes(true);
    }

    @Override
    public NodeTypeIterator getSubtypes() {
        return this.getSubtypes(false);
    }

    @Override
    public NodeDefinition[] getDeclaredChildNodeDefinitions() {
        QNodeDefinition[] cnda = this.ntd.getChildNodeDefs();
        NodeDefinition[] nodeDefs = new NodeDefinition[cnda.length];
        for (int i = 0; i < cnda.length; ++i) {
            nodeDefs[i] = this.ntMgr.getNodeDefinition(cnda[i]);
        }
        return nodeDefs;
    }

    @Override
    public String getPrimaryItemName() {
        try {
            Name piName = this.ntd.getPrimaryItemName();
            if (piName != null) {
                return this.resolver.getJCRName(piName);
            }
            return null;
        }
        catch (NamespaceException e) {
            log.error("encountered unregistered namespace in name of primary item", (Throwable)e);
            return this.ntd.getName().toString();
        }
    }

    @Override
    public PropertyDefinition[] getDeclaredPropertyDefinitions() {
        QPropertyDefinition[] pda = this.ntd.getPropertyDefs();
        PropertyDefinition[] propDefs = new PropertyDefinition[pda.length];
        for (int i = 0; i < pda.length; ++i) {
            propDefs[i] = this.ntMgr.getPropertyDefinition(pda[i]);
        }
        return propDefs;
    }

    @Override
    public boolean isNodeType(String nodeTypeName) {
        Name ntName;
        try {
            ntName = this.resolver.getQName(nodeTypeName);
        }
        catch (NamespaceException e) {
            log.warn("invalid node type name: " + nodeTypeName, (Throwable)e);
            return false;
        }
        catch (NameException e) {
            log.warn("invalid node type name: " + nodeTypeName, (Throwable)e);
            return false;
        }
        return this.isNodeType(ntName);
    }

    public abstract boolean isNodeType(Name var1);

    public NodeTypeIterator getSubtypes(boolean directOnly) {
        NodeTypeIterator iter;
        try {
            iter = this.ntMgr.getAllNodeTypes();
        }
        catch (RepositoryException e) {
            log.error("failed to retrieve registered node types", (Throwable)e);
            return NodeTypeIteratorAdapter.EMPTY;
        }
        ArrayList<NodeType> result = new ArrayList<NodeType>();
        String thisName = this.getName();
        block2: while (iter.hasNext()) {
            NodeType nt = iter.nextNodeType();
            if (nt.getName().equals(thisName)) continue;
            if (directOnly) {
                for (String name : nt.getDeclaredSupertypeNames()) {
                    if (!name.equals(thisName)) continue;
                    result.add(nt);
                    continue block2;
                }
                continue;
            }
            if (!nt.isNodeType(thisName)) continue;
            result.add(nt);
        }
        return new NodeTypeIteratorAdapter(result);
    }
}


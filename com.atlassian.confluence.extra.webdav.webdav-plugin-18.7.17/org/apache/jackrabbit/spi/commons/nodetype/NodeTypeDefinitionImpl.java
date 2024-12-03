/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.NamespaceException;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.NodeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.nodetype.PropertyDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTypeDefinitionImpl
implements NodeTypeDefinition {
    private static final Logger log = LoggerFactory.getLogger(NodeTypeDefinitionImpl.class);
    protected final QNodeTypeDefinition ntd;
    private final NamePathResolver resolver;
    private final ValueFactory valueFactory;

    public NodeTypeDefinitionImpl(QNodeTypeDefinition ntd, NamePathResolver resolver, ValueFactory valueFactory) {
        this.ntd = ntd;
        this.resolver = resolver;
        this.valueFactory = valueFactory;
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
    public boolean isMixin() {
        return this.ntd.isMixin();
    }

    @Override
    public boolean hasOrderableChildNodes() {
        return this.ntd.hasOrderableChildNodes();
    }

    @Override
    public boolean isAbstract() {
        return this.ntd.isAbstract();
    }

    @Override
    public boolean isQueryable() {
        return this.ntd.isQueryable();
    }

    @Override
    public PropertyDefinition[] getDeclaredPropertyDefinitions() {
        QPropertyDefinition[] pds = this.ntd.getPropertyDefs();
        PropertyDefinition[] propDefs = new PropertyDefinition[pds.length];
        for (int i = 0; i < pds.length; ++i) {
            propDefs[i] = new PropertyDefinitionImpl(pds[i], this.resolver, this.valueFactory);
        }
        return propDefs;
    }

    @Override
    public NodeDefinition[] getDeclaredChildNodeDefinitions() {
        QNodeDefinition[] cnda = this.ntd.getChildNodeDefs();
        NodeDefinition[] nodeDefs = new NodeDefinition[cnda.length];
        for (int i = 0; i < cnda.length; ++i) {
            nodeDefs[i] = new NodeDefinitionImpl(cnda[i], this.resolver);
        }
        return nodeDefs;
    }

    @Override
    public String[] getDeclaredSupertypeNames() {
        Name[] stNames = this.ntd.getSupertypes();
        String[] dstn = new String[stNames.length];
        for (int i = 0; i < stNames.length; ++i) {
            try {
                dstn[i] = this.resolver.getJCRName(stNames[i]);
                continue;
            }
            catch (NamespaceException e) {
                log.error("invalid node type name: " + stNames[i], (Throwable)e);
                dstn[i] = stNames[i].toString();
            }
        }
        return dstn;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.NodeDefinitionTemplateImpl;
import org.apache.jackrabbit.spi.commons.nodetype.NodeTypeTemplateImpl;
import org.apache.jackrabbit.spi.commons.nodetype.PropertyDefinitionTemplateImpl;

public abstract class AbstractNodeTypeManager
implements NodeTypeManager {
    public abstract NodeType getNodeType(Name var1) throws NoSuchNodeTypeException;

    public abstract NodeDefinition getNodeDefinition(QNodeDefinition var1);

    public abstract PropertyDefinition getPropertyDefinition(QPropertyDefinition var1);

    public abstract NamePathResolver getNamePathResolver();

    @Override
    public NodeTypeTemplate createNodeTypeTemplate() throws UnsupportedRepositoryOperationException, RepositoryException {
        return new NodeTypeTemplateImpl(this.getNamePathResolver());
    }

    @Override
    public NodeTypeTemplate createNodeTypeTemplate(NodeTypeDefinition ntd) throws UnsupportedRepositoryOperationException, RepositoryException {
        return new NodeTypeTemplateImpl(ntd, this.getNamePathResolver());
    }

    @Override
    public NodeDefinitionTemplate createNodeDefinitionTemplate() throws UnsupportedRepositoryOperationException, RepositoryException {
        return new NodeDefinitionTemplateImpl(this.getNamePathResolver());
    }

    @Override
    public PropertyDefinitionTemplate createPropertyDefinitionTemplate() throws UnsupportedRepositoryOperationException, RepositoryException {
        return new PropertyDefinitionTemplateImpl(this.getNamePathResolver());
    }

    @Override
    public NodeType registerNodeType(NodeTypeDefinition ntd, boolean allowUpdate) throws RepositoryException {
        NodeTypeDefinition[] ntds = new NodeTypeDefinition[]{ntd};
        return this.registerNodeTypes(ntds, allowUpdate).nextNodeType();
    }

    @Override
    public void unregisterNodeType(String name) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException {
        this.unregisterNodeTypes(new String[]{name});
    }
}


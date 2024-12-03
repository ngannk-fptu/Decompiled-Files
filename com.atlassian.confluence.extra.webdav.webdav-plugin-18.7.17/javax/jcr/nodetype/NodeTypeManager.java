/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeExistsException;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;

public interface NodeTypeManager {
    public NodeType getNodeType(String var1) throws NoSuchNodeTypeException, RepositoryException;

    public boolean hasNodeType(String var1) throws RepositoryException;

    public NodeTypeIterator getAllNodeTypes() throws RepositoryException;

    public NodeTypeIterator getPrimaryNodeTypes() throws RepositoryException;

    public NodeTypeIterator getMixinNodeTypes() throws RepositoryException;

    public NodeTypeTemplate createNodeTypeTemplate() throws UnsupportedRepositoryOperationException, RepositoryException;

    public NodeTypeTemplate createNodeTypeTemplate(NodeTypeDefinition var1) throws UnsupportedRepositoryOperationException, RepositoryException;

    public NodeDefinitionTemplate createNodeDefinitionTemplate() throws UnsupportedRepositoryOperationException, RepositoryException;

    public PropertyDefinitionTemplate createPropertyDefinitionTemplate() throws UnsupportedRepositoryOperationException, RepositoryException;

    public NodeType registerNodeType(NodeTypeDefinition var1, boolean var2) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException;

    public NodeTypeIterator registerNodeTypes(NodeTypeDefinition[] var1, boolean var2) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException;

    public void unregisterNodeType(String var1) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException;

    public void unregisterNodeTypes(String[] var1) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException;
}


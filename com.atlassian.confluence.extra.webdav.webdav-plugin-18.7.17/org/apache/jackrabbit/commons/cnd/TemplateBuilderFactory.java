/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.cnd;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import org.apache.jackrabbit.commons.cnd.DefinitionBuilderFactory;

public class TemplateBuilderFactory
extends DefinitionBuilderFactory<NodeTypeTemplate, NamespaceRegistry> {
    private final NodeTypeManager nodeTypeManager;
    private final ValueFactory valueFactory;
    private NamespaceRegistry namespaceRegistry;

    public TemplateBuilderFactory(NodeTypeManager nodeTypeManager, ValueFactory valueFactory, NamespaceRegistry namespaceRegistry) {
        this.nodeTypeManager = nodeTypeManager;
        this.valueFactory = valueFactory;
        this.namespaceRegistry = namespaceRegistry;
    }

    public TemplateBuilderFactory(Session session) throws RepositoryException {
        this(session.getWorkspace().getNodeTypeManager(), session.getValueFactory(), session.getWorkspace().getNamespaceRegistry());
    }

    @Override
    public DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<NodeTypeTemplate> newNodeTypeDefinitionBuilder() throws UnsupportedRepositoryOperationException, RepositoryException {
        return new NodeTypeTemplateBuilder();
    }

    @Override
    public void setNamespaceMapping(NamespaceRegistry namespaceRegistry) {
        this.namespaceRegistry = namespaceRegistry;
    }

    @Override
    public NamespaceRegistry getNamespaceMapping() {
        return this.namespaceRegistry;
    }

    @Override
    public void setNamespace(String prefix, String uri) {
        try {
            this.namespaceRegistry.registerNamespace(prefix, uri);
        }
        catch (RepositoryException repositoryException) {
            // empty catch block
        }
    }

    public class NodeDefinitionTemplateBuilder
    extends DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<NodeTypeTemplate> {
        private final NodeTypeTemplateBuilder ntd;
        private final NodeDefinitionTemplate template;
        private final List<String> requiredPrimaryTypes = new ArrayList<String>();

        public NodeDefinitionTemplateBuilder(NodeTypeTemplateBuilder ntd) throws UnsupportedRepositoryOperationException, RepositoryException {
            this.ntd = ntd;
            this.template = TemplateBuilderFactory.this.nodeTypeManager.createNodeDefinitionTemplate();
        }

        @Override
        public void setName(String name) throws RepositoryException {
            super.setName(name);
            this.template.setName(name);
        }

        @Override
        public void addRequiredPrimaryType(String name) {
            this.requiredPrimaryTypes.add(name);
        }

        @Override
        public void setDefaultPrimaryType(String name) throws ConstraintViolationException {
            this.template.setDefaultPrimaryTypeName(name);
        }

        @Override
        public void setDeclaringNodeType(String name) {
        }

        @Override
        public void build() throws ConstraintViolationException {
            this.template.setAutoCreated(this.autocreate);
            this.template.setMandatory(this.isMandatory);
            this.template.setOnParentVersion(this.onParent);
            this.template.setProtected(this.isProtected);
            this.template.setRequiredPrimaryTypeNames(this.requiredPrimaryTypes.toArray(new String[this.requiredPrimaryTypes.size()]));
            this.template.setSameNameSiblings(this.allowSns);
            List templates = this.ntd.template.getNodeDefinitionTemplates();
            templates.add(this.template);
        }
    }

    public class PropertyDefinitionTemplateBuilder
    extends DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<NodeTypeTemplate> {
        private final NodeTypeTemplateBuilder ntd;
        private final PropertyDefinitionTemplate template;
        private final List<Value> values = new ArrayList<Value>();
        private final List<String> constraints = new ArrayList<String>();

        public PropertyDefinitionTemplateBuilder(NodeTypeTemplateBuilder ntd) throws UnsupportedRepositoryOperationException, RepositoryException {
            this.ntd = ntd;
            this.template = TemplateBuilderFactory.this.nodeTypeManager.createPropertyDefinitionTemplate();
        }

        @Override
        public void setName(String name) throws RepositoryException {
            super.setName(name);
            this.template.setName(name);
        }

        @Override
        public void addDefaultValues(String value) throws ValueFormatException {
            this.values.add(TemplateBuilderFactory.this.valueFactory.createValue(value, this.getRequiredType()));
        }

        @Override
        public void addValueConstraint(String constraint) {
            this.constraints.add(constraint);
        }

        @Override
        public void setDeclaringNodeType(String name) {
        }

        @Override
        public void build() throws IllegalStateException {
            this.template.setAutoCreated(this.autocreate);
            this.template.setMandatory(this.isMandatory);
            this.template.setOnParentVersion(this.onParent);
            this.template.setProtected(this.isProtected);
            this.template.setRequiredType(this.requiredType);
            this.template.setValueConstraints(this.constraints.toArray(new String[this.constraints.size()]));
            this.template.setDefaultValues(this.values.toArray(new Value[this.values.size()]));
            this.template.setMultiple(this.isMultiple);
            this.template.setAvailableQueryOperators(this.queryOperators);
            this.template.setFullTextSearchable(this.fullTextSearchable);
            this.template.setQueryOrderable(this.queryOrderable);
            List templates = this.ntd.template.getPropertyDefinitionTemplates();
            templates.add(this.template);
        }
    }

    public class NodeTypeTemplateBuilder
    extends DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<NodeTypeTemplate> {
        private final NodeTypeTemplate template;
        private final List<String> supertypes = new ArrayList<String>();

        public NodeTypeTemplateBuilder() throws UnsupportedRepositoryOperationException, RepositoryException {
            this.template = TemplateBuilderFactory.this.nodeTypeManager.createNodeTypeTemplate();
        }

        @Override
        public DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<NodeTypeTemplate> newNodeDefinitionBuilder() throws UnsupportedRepositoryOperationException, RepositoryException {
            return new NodeDefinitionTemplateBuilder(this);
        }

        @Override
        public DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<NodeTypeTemplate> newPropertyDefinitionBuilder() throws UnsupportedRepositoryOperationException, RepositoryException {
            return new PropertyDefinitionTemplateBuilder(this);
        }

        @Override
        public NodeTypeTemplate build() throws ConstraintViolationException {
            this.template.setMixin(this.isMixin);
            this.template.setOrderableChildNodes(this.isOrderable);
            this.template.setAbstract(this.isAbstract);
            this.template.setQueryable(this.queryable);
            this.template.setDeclaredSuperTypeNames(this.supertypes.toArray(new String[this.supertypes.size()]));
            return this.template;
        }

        @Override
        public void setName(String name) throws RepositoryException {
            super.setName(name);
            this.template.setName(name);
        }

        @Override
        public void addSupertype(String name) {
            this.supertypes.add(name);
        }

        @Override
        public void setPrimaryItemName(String name) throws ConstraintViolationException {
            this.template.setPrimaryItemName(name);
        }
    }
}


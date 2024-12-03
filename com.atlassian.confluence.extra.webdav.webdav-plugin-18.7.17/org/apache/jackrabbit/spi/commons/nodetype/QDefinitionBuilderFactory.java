/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.cnd.DefinitionBuilderFactory;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.commons.QNodeTypeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.conversion.DefaultNamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceMapping;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.QNodeDefinitionBuilder;
import org.apache.jackrabbit.spi.commons.nodetype.QPropertyDefinitionBuilder;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;
import org.apache.jackrabbit.spi.commons.value.QValueFactoryImpl;
import org.apache.jackrabbit.spi.commons.value.ValueFormat;
import org.apache.jackrabbit.util.ISO9075;

public class QDefinitionBuilderFactory
extends DefinitionBuilderFactory<QNodeTypeDefinition, NamespaceMapping> {
    private static final NameFactory NAME_FACTORY = NameFactoryImpl.getInstance();
    public static final NamespaceMapping NS_DEFAULTS;
    private NamespaceMapping nsMappings = new NamespaceMapping(NS_DEFAULTS);
    private NamePathResolver resolver = new DefaultNamePathResolver(this.nsMappings);

    @Override
    public DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<QNodeTypeDefinition> newNodeTypeDefinitionBuilder() {
        return new QNodeTypeDefinitionBuilderImpl();
    }

    @Override
    public void setNamespaceMapping(NamespaceMapping nsMapping) {
        this.nsMappings = nsMapping;
        this.resolver = new DefaultNamePathResolver(nsMapping);
    }

    @Override
    public NamespaceMapping getNamespaceMapping() {
        return this.nsMappings;
    }

    @Override
    public void setNamespace(String prefix, String uri) {
        try {
            this.nsMappings.setMapping(prefix, uri);
        }
        catch (NamespaceException namespaceException) {
            // empty catch block
        }
    }

    private Name toName(String name) throws IllegalNameException, NamespaceException {
        Name n = this.resolver.getQName(name);
        String decodedLocalName = ISO9075.decode(n.getLocalName());
        return NAME_FACTORY.create(n.getNamespaceURI(), decodedLocalName);
    }

    static {
        try {
            NS_DEFAULTS = new NamespaceMapping();
            NS_DEFAULTS.setMapping("", "");
            NS_DEFAULTS.setMapping("jcr", "http://www.jcp.org/jcr/1.0");
            NS_DEFAULTS.setMapping("mix", "http://www.jcp.org/jcr/mix/1.0");
            NS_DEFAULTS.setMapping("nt", "http://www.jcp.org/jcr/nt/1.0");
            NS_DEFAULTS.setMapping("rep", "internal");
        }
        catch (NamespaceException e) {
            throw new InternalError(e.toString());
        }
    }

    private class QNodeDefinitionBuilderImpl
    extends DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<QNodeTypeDefinition> {
        private final QNodeTypeDefinitionBuilderImpl ntd;
        private final QNodeDefinitionBuilder builder = new QNodeDefinitionBuilder();

        public QNodeDefinitionBuilderImpl(QNodeTypeDefinitionBuilderImpl ntd) {
            this.ntd = ntd;
        }

        @Override
        public void setName(String name) throws RepositoryException {
            super.setName(name);
            if ("*".equals(name)) {
                this.builder.setName(NameConstants.ANY_NAME);
            } else {
                this.builder.setName(QDefinitionBuilderFactory.this.toName(name));
            }
        }

        @Override
        public void setAllowsSameNameSiblings(boolean allowSns) throws RepositoryException {
            super.setAllowsSameNameSiblings(allowSns);
            this.builder.setAllowsSameNameSiblings(allowSns);
        }

        @Override
        public void setAutoCreated(boolean autocreate) throws RepositoryException {
            super.setAutoCreated(autocreate);
            this.builder.setAutoCreated(autocreate);
        }

        @Override
        public void setOnParentVersion(int onParent) throws RepositoryException {
            super.setOnParentVersion(onParent);
            this.builder.setOnParentVersion(onParent);
        }

        @Override
        public void setProtected(boolean isProtected) throws RepositoryException {
            super.setProtected(isProtected);
            this.builder.setProtected(isProtected);
        }

        @Override
        public void setMandatory(boolean isMandatory) throws RepositoryException {
            super.setMandatory(isMandatory);
            this.builder.setMandatory(isMandatory);
        }

        @Override
        public void addRequiredPrimaryType(String name) throws IllegalNameException, NamespaceException {
            this.builder.addRequiredPrimaryType(QDefinitionBuilderFactory.this.toName(name));
        }

        @Override
        public void setDefaultPrimaryType(String name) throws IllegalNameException, NamespaceException {
            this.builder.setDefaultPrimaryType(QDefinitionBuilderFactory.this.toName(name));
        }

        @Override
        public void setDeclaringNodeType(String name) throws IllegalNameException, NamespaceException {
            this.builder.setDeclaringNodeType(QDefinitionBuilderFactory.this.toName(name));
        }

        @Override
        public void build() {
            this.ntd.childNodeDefs.add(this.builder.build());
        }
    }

    private class QPropertyDefinitionBuilderImpl
    extends DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<QNodeTypeDefinition> {
        private final QNodeTypeDefinitionBuilderImpl ntd;
        private final QPropertyDefinitionBuilder builder = new QPropertyDefinitionBuilder();

        public QPropertyDefinitionBuilderImpl(QNodeTypeDefinitionBuilderImpl ntd) {
            this.ntd = ntd;
        }

        @Override
        public void setName(String name) throws RepositoryException {
            super.setName(name);
            if ("*".equals(name)) {
                this.builder.setName(NameConstants.ANY_NAME);
            } else {
                this.builder.setName(QDefinitionBuilderFactory.this.toName(name));
            }
        }

        @Override
        public void setRequiredType(int type) throws RepositoryException {
            super.setRequiredType(type);
            this.builder.setRequiredType(type);
        }

        @Override
        public void setMultiple(boolean isMultiple) throws RepositoryException {
            super.setMultiple(isMultiple);
            this.builder.setMultiple(isMultiple);
        }

        @Override
        public void setFullTextSearchable(boolean fullTextSearchable) throws RepositoryException {
            super.setFullTextSearchable(fullTextSearchable);
            this.builder.setFullTextSearchable(fullTextSearchable);
        }

        @Override
        public void setQueryOrderable(boolean queryOrderable) throws RepositoryException {
            super.setQueryOrderable(queryOrderable);
            this.builder.setQueryOrderable(queryOrderable);
        }

        @Override
        public void setAvailableQueryOperators(String[] queryOperators) throws RepositoryException {
            super.setAvailableQueryOperators(queryOperators);
            this.builder.setAvailableQueryOperators(queryOperators);
        }

        @Override
        public void setAutoCreated(boolean autocreate) throws RepositoryException {
            super.setAutoCreated(autocreate);
            this.builder.setAutoCreated(autocreate);
        }

        @Override
        public void setOnParentVersion(int onParent) throws RepositoryException {
            super.setOnParentVersion(onParent);
            this.builder.setOnParentVersion(onParent);
        }

        @Override
        public void setProtected(boolean isProtected) throws RepositoryException {
            super.setProtected(isProtected);
            this.builder.setProtected(isProtected);
        }

        @Override
        public void setMandatory(boolean isMandatory) throws RepositoryException {
            super.setMandatory(isMandatory);
            this.builder.setMandatory(isMandatory);
        }

        @Override
        public void addDefaultValues(String value) throws RepositoryException {
            this.builder.addDefaultValue(ValueFormat.getQValue(value, this.getRequiredType(), QDefinitionBuilderFactory.this.resolver, QValueFactoryImpl.getInstance()));
        }

        @Override
        public void addValueConstraint(String constraint) throws InvalidConstraintException {
            this.builder.addValueConstraint(ValueConstraint.create(this.getRequiredType(), constraint, QDefinitionBuilderFactory.this.resolver));
        }

        @Override
        public void setDeclaringNodeType(String name) throws IllegalNameException, NamespaceException {
            this.builder.setDeclaringNodeType(QDefinitionBuilderFactory.this.toName(name));
        }

        @Override
        public void build() throws IllegalStateException {
            this.ntd.propertyDefs.add(this.builder.build());
        }
    }

    private class QNodeTypeDefinitionBuilderImpl
    extends DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<QNodeTypeDefinition> {
        private Name name;
        private final List<Name> supertypes = new ArrayList<Name>();
        private Name primaryItem;
        private final List<QPropertyDefinition> propertyDefs = new ArrayList<QPropertyDefinition>();
        private final List<QNodeDefinition> childNodeDefs = new ArrayList<QNodeDefinition>();

        private QNodeTypeDefinitionBuilderImpl() {
        }

        @Override
        public DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<QNodeTypeDefinition> newNodeDefinitionBuilder() {
            return new QNodeDefinitionBuilderImpl(this);
        }

        @Override
        public DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<QNodeTypeDefinition> newPropertyDefinitionBuilder() {
            return new QPropertyDefinitionBuilderImpl(this);
        }

        @Override
        public QNodeTypeDefinition build() {
            if (!this.isMixin && !NameConstants.NT_BASE.equals(this.name)) {
                this.supertypes.add(NameConstants.NT_BASE);
            }
            return new QNodeTypeDefinitionImpl(this.name, this.supertypes.toArray(new Name[this.supertypes.size()]), null, this.isMixin, this.isAbstract, this.queryable, this.isOrderable, this.primaryItem, this.propertyDefs.toArray(new QPropertyDefinition[this.propertyDefs.size()]), this.childNodeDefs.toArray(new QNodeDefinition[this.childNodeDefs.size()]));
        }

        @Override
        public void setName(String name) throws RepositoryException {
            super.setName(name);
            this.name = QDefinitionBuilderFactory.this.toName(name);
        }

        @Override
        public void addSupertype(String name) throws IllegalNameException, NamespaceException {
            this.supertypes.add(QDefinitionBuilderFactory.this.toName(name));
        }

        @Override
        public void setPrimaryItemName(String name) throws IllegalNameException, NamespaceException {
            this.primaryItem = QDefinitionBuilderFactory.this.toName(name);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueConstraint;
import org.apache.jackrabbit.spi.commons.conversion.DefaultNamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;
import org.apache.jackrabbit.spi.commons.value.QValueValue;

public class NodeTypeDefinitionFactory {
    private final NodeTypeManager ntMgr;
    private final NamePathResolver resolver;

    public NodeTypeDefinitionFactory(Session session) throws RepositoryException {
        this.ntMgr = session.getWorkspace().getNodeTypeManager();
        this.resolver = new DefaultNamePathResolver(session);
    }

    public List<NodeTypeDefinition> create(Collection<QNodeTypeDefinition> defs) throws RepositoryException {
        ArrayList<NodeTypeDefinition> list = new ArrayList<NodeTypeDefinition>(defs.size());
        for (QNodeTypeDefinition qNtd : defs) {
            list.add(this.create(qNtd));
        }
        return list;
    }

    public NodeTypeDefinition create(QNodeTypeDefinition qNtd) throws RepositoryException {
        NodeTypeTemplate nt = this.ntMgr.createNodeTypeTemplate();
        nt.setName(this.getJCRName(qNtd.getName()));
        nt.setDeclaredSuperTypeNames(this.getJCRNames(qNtd.getSupertypes()));
        nt.setAbstract(qNtd.isAbstract());
        nt.setMixin(qNtd.isMixin());
        nt.setOrderableChildNodes(qNtd.hasOrderableChildNodes());
        nt.setPrimaryItemName(this.getJCRName(qNtd.getPrimaryItemName()));
        nt.setQueryable(qNtd.isQueryable());
        List nodeDefs = nt.getNodeDefinitionTemplates();
        for (QNodeDefinition qNd : qNtd.getChildNodeDefs()) {
            nodeDefs.add(this.create(qNd));
        }
        List propDefs = nt.getPropertyDefinitionTemplates();
        for (QPropertyDefinition qPd : qNtd.getPropertyDefs()) {
            propDefs.add(this.create(qPd));
        }
        return nt;
    }

    public NodeDefinition create(QNodeDefinition qNd) throws RepositoryException {
        NodeDefinitionTemplate nt = this.ntMgr.createNodeDefinitionTemplate();
        nt.setName(this.getJCRName(qNd.getName()));
        nt.setAutoCreated(qNd.isAutoCreated());
        nt.setMandatory(qNd.isMandatory());
        nt.setOnParentVersion(qNd.getOnParentVersion());
        nt.setProtected(qNd.isProtected());
        nt.setSameNameSiblings(qNd.allowsSameNameSiblings());
        nt.setDefaultPrimaryTypeName(this.getJCRName(qNd.getDefaultPrimaryType()));
        nt.setRequiredPrimaryTypeNames(this.getJCRNames(qNd.getRequiredPrimaryTypes()));
        return nt;
    }

    public PropertyDefinition create(QPropertyDefinition qPd) throws RepositoryException {
        PropertyDefinitionTemplate pt = this.ntMgr.createPropertyDefinitionTemplate();
        pt.setName(this.getJCRName(qPd.getName()));
        pt.setAutoCreated(qPd.isAutoCreated());
        pt.setMandatory(qPd.isMandatory());
        pt.setOnParentVersion(qPd.getOnParentVersion());
        pt.setProtected(qPd.isProtected());
        pt.setRequiredType(qPd.getRequiredType());
        pt.setMultiple(qPd.isMultiple());
        pt.setFullTextSearchable(qPd.isFullTextSearchable());
        pt.setValueConstraints(this.createValueConstraints(qPd.getRequiredType(), qPd.getValueConstraints()));
        pt.setAvailableQueryOperators(qPd.getAvailableQueryOperators());
        pt.setQueryOrderable(qPd.isQueryOrderable());
        pt.setDefaultValues(this.createValues(qPd.getDefaultValues()));
        return pt;
    }

    private String[] getJCRNames(Name[] names) throws NamespaceException {
        if (names == null) {
            return null;
        }
        String[] ret = new String[names.length];
        for (int i = 0; i < names.length; ++i) {
            ret[i] = this.resolver.getJCRName(names[i]);
        }
        return ret;
    }

    private String getJCRName(Name name) throws NamespaceException {
        if (name == null) {
            return null;
        }
        return this.resolver.getJCRName(name);
    }

    private String[] createValueConstraints(int type, QValueConstraint[] qv) throws RepositoryException {
        String[] ret = new String[qv.length];
        for (int i = 0; i < ret.length; ++i) {
            try {
                ValueConstraint c = ValueConstraint.create(type, qv[i].getString());
                ret[i] = c.getDefinition(this.resolver);
                continue;
            }
            catch (InvalidConstraintException e) {
                throw new RepositoryException("Internal error while converting value constraints.", e);
            }
        }
        return ret;
    }

    private Value[] createValues(QValue[] qv) {
        if (qv == null) {
            return null;
        }
        Value[] ret = new Value[qv.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = new QValueValue(qv[i], this.resolver);
        }
        return ret;
    }
}


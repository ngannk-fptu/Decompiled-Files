/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public abstract class AbstractRepository
implements Repository {
    private static final Set<String> STANDARD_KEYS = new HashSet<String>(){
        {
            this.add("identifier.stability");
            this.add("level.1.supported");
            this.add("level.2.supported");
            this.add("option.node.type.management.supported");
            this.add("node.type.management.autocreated.definitions.supported");
            this.add("node.type.management.inheritance");
            this.add("node.type.management.multiple.binary.properties.supported");
            this.add("node.type.management.multivalued.properties.supported");
            this.add("node.type.management.orderable.child.nodes.supported");
            this.add("node.type.management.overrides.supported");
            this.add("node.type.management.primary.item.name.supported");
            this.add("node.type.management.property.types");
            this.add("node.type.management.residual.definitions.supported");
            this.add("node.type.management.same.name.siblings.supported");
            this.add("node.type.management.value.constraints.supported");
            this.add("node.type.management.update.in.use.suported");
            this.add("option.access.control.supported");
            this.add("option.journaled.observation.supported");
            this.add("option.lifecycle.supported");
            this.add("option.locking.supported");
            this.add("option.observation.supported");
            this.add("option.node.and.property.with.same.name.supported");
            this.add("option.query.sql.supported");
            this.add("option.retention.supported");
            this.add("option.shareable.nodes.supported");
            this.add("option.simple.versioning.supported");
            this.add("option.transactions.supported");
            this.add("option.unfiled.content.supported");
            this.add("option.update.mixin.node.types.supported");
            this.add("option.update.primary.node.type.supported");
            this.add("option.versioning.supported");
            this.add("option.workspace.management.supported");
            this.add("option.xml.export.supported");
            this.add("option.xml.import.supported");
            this.add("option.activities.supported");
            this.add("option.baselines.supported");
            this.add("query.full.text.search.supported");
            this.add("query.joins");
            this.add("query.languages");
            this.add("query.stored.queries.supported");
            this.add("query.xpath.doc.order");
            this.add("query.xpath.pos.index");
            this.add("jcr.repository.name");
            this.add("jcr.repository.vendor");
            this.add("jcr.repository.vendor.url");
            this.add("jcr.specification.name");
            this.add("jcr.specification.version");
            this.add("write.supported");
        }
    };

    @Override
    public boolean isStandardDescriptor(String key) {
        return STANDARD_KEYS.contains(key);
    }

    public Session login(Credentials credentials, String workspaceName, Map<String, Object> attributes) throws LoginException, NoSuchWorkspaceException, RepositoryException {
        return this.login(credentials, workspaceName);
    }

    @Override
    public Session login() throws RepositoryException {
        return this.login(null, null);
    }

    @Override
    public Session login(Credentials credentials) throws RepositoryException {
        return this.login(credentials, null);
    }

    @Override
    public Session login(String workspace) throws RepositoryException {
        return this.login(null, workspace);
    }
}


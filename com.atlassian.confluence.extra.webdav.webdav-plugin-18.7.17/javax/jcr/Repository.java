/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

public interface Repository {
    public static final String SPEC_VERSION_DESC = "jcr.specification.version";
    public static final String SPEC_NAME_DESC = "jcr.specification.name";
    public static final String REP_VENDOR_DESC = "jcr.repository.vendor";
    public static final String REP_VENDOR_URL_DESC = "jcr.repository.vendor.url";
    public static final String REP_NAME_DESC = "jcr.repository.name";
    public static final String REP_VERSION_DESC = "jcr.repository.version";
    public static final String WRITE_SUPPORTED = "write.supported";
    public static final String IDENTIFIER_STABILITY = "identifier.stability";
    public static final String IDENTIFIER_STABILITY_METHOD_DURATION = "identifier.stability.method.duration";
    public static final String IDENTIFIER_STABILITY_SAVE_DURATION = "identifier.stability.save.duration";
    public static final String IDENTIFIER_STABILITY_SESSION_DURATION = "identifier.stability.session.duration";
    public static final String IDENTIFIER_STABILITY_INDEFINITE_DURATION = "identifier.stability.indefinite.duration";
    public static final String OPTION_XML_EXPORT_SUPPORTED = "option.xml.export.supported";
    public static final String OPTION_XML_IMPORT_SUPPORTED = "option.xml.import.supported";
    public static final String OPTION_UNFILED_CONTENT_SUPPORTED = "option.unfiled.content.supported";
    public static final String OPTION_VERSIONING_SUPPORTED = "option.versioning.supported";
    public static final String OPTION_SIMPLE_VERSIONING_SUPPORTED = "option.simple.versioning.supported";
    public static final String OPTION_ACTIVITIES_SUPPORTED = "option.activities.supported";
    public static final String OPTION_BASELINES_SUPPORTED = "option.baselines.supported";
    public static final String OPTION_ACCESS_CONTROL_SUPPORTED = "option.access.control.supported";
    public static final String OPTION_LOCKING_SUPPORTED = "option.locking.supported";
    public static final String OPTION_OBSERVATION_SUPPORTED = "option.observation.supported";
    public static final String OPTION_JOURNALED_OBSERVATION_SUPPORTED = "option.journaled.observation.supported";
    public static final String OPTION_RETENTION_SUPPORTED = "option.retention.supported";
    public static final String OPTION_LIFECYCLE_SUPPORTED = "option.lifecycle.supported";
    public static final String OPTION_TRANSACTIONS_SUPPORTED = "option.transactions.supported";
    public static final String OPTION_WORKSPACE_MANAGEMENT_SUPPORTED = "option.workspace.management.supported";
    public static final String OPTION_UPDATE_PRIMARY_NODE_TYPE_SUPPORTED = "option.update.primary.node.type.supported";
    public static final String OPTION_UPDATE_MIXIN_NODE_TYPES_SUPPORTED = "option.update.mixin.node.types.supported";
    public static final String OPTION_SHAREABLE_NODES_SUPPORTED = "option.shareable.nodes.supported";
    public static final String OPTION_NODE_TYPE_MANAGEMENT_SUPPORTED = "option.node.type.management.supported";
    public static final String OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED = "option.node.and.property.with.same.name.supported";
    public static final String NODE_TYPE_MANAGEMENT_INHERITANCE = "node.type.management.inheritance";
    public static final String NODE_TYPE_MANAGEMENT_INHERITANCE_MINIMAL = "node.type.management.inheritance.minimal";
    public static final String NODE_TYPE_MANAGEMENT_INHERITANCE_SINGLE = "node.type.management.inheritance.single";
    public static final String NODE_TYPE_MANAGEMENT_INHERITANCE_MULTIPLE = "node.type.management.inheritance.multiple";
    public static final String NODE_TYPE_MANAGEMENT_OVERRIDES_SUPPORTED = "node.type.management.overrides.supported";
    public static final String NODE_TYPE_MANAGEMENT_PRIMARY_ITEM_NAME_SUPPORTED = "node.type.management.primary.item.name.supported";
    public static final String NODE_TYPE_MANAGEMENT_ORDERABLE_CHILD_NODES_SUPPORTED = "node.type.management.orderable.child.nodes.supported";
    public static final String NODE_TYPE_MANAGEMENT_RESIDUAL_DEFINITIONS_SUPPORTED = "node.type.management.residual.definitions.supported";
    public static final String NODE_TYPE_MANAGEMENT_AUTOCREATED_DEFINITIONS_SUPPORTED = "node.type.management.autocreated.definitions.supported";
    public static final String NODE_TYPE_MANAGEMENT_SAME_NAME_SIBLINGS_SUPPORTED = "node.type.management.same.name.siblings.supported";
    public static final String NODE_TYPE_MANAGEMENT_PROPERTY_TYPES = "node.type.management.property.types";
    public static final String NODE_TYPE_MANAGEMENT_MULTIVALUED_PROPERTIES_SUPPORTED = "node.type.management.multivalued.properties.supported";
    public static final String NODE_TYPE_MANAGEMENT_MULTIPLE_BINARY_PROPERTIES_SUPPORTED = "node.type.management.multiple.binary.properties.supported";
    public static final String NODE_TYPE_MANAGEMENT_VALUE_CONSTRAINTS_SUPPORTED = "node.type.management.value.constraints.supported";
    public static final String NODE_TYPE_MANAGEMENT_UPDATE_IN_USE_SUPORTED = "node.type.management.update.in.use.suported";
    public static final String QUERY_LANGUAGES = "query.languages";
    public static final String QUERY_STORED_QUERIES_SUPPORTED = "query.stored.queries.supported";
    public static final String QUERY_FULL_TEXT_SEARCH_SUPPORTED = "query.full.text.search.supported";
    public static final String QUERY_JOINS = "query.joins";
    public static final String QUERY_JOINS_NONE = "query.joins.none";
    public static final String QUERY_JOINS_INNER = "query.joins.inner";
    public static final String QUERY_JOINS_INNER_OUTER = "query.joins.inner.outer";
    public static final String LEVEL_1_SUPPORTED = "level.1.supported";
    public static final String LEVEL_2_SUPPORTED = "level.2.supported";
    public static final String OPTION_QUERY_SQL_SUPPORTED = "option.query.sql.supported";
    public static final String QUERY_XPATH_POS_INDEX = "query.xpath.pos.index";
    public static final String QUERY_XPATH_DOC_ORDER = "query.xpath.doc.order";

    public String[] getDescriptorKeys();

    public boolean isStandardDescriptor(String var1);

    public boolean isSingleValueDescriptor(String var1);

    public Value getDescriptorValue(String var1);

    public Value[] getDescriptorValues(String var1);

    public String getDescriptor(String var1);

    public Session login(Credentials var1, String var2) throws LoginException, NoSuchWorkspaceException, RepositoryException;

    public Session login(Credentials var1) throws LoginException, RepositoryException;

    public Session login(String var1) throws LoginException, NoSuchWorkspaceException, RepositoryException;

    public Session login() throws LoginException, RepositoryException;
}


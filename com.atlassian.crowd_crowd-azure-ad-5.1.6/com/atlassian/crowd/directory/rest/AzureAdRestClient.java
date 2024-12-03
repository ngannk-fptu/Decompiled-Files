/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Charsets
 *  com.sun.jersey.api.client.Client
 *  com.sun.jersey.api.client.ClientResponse
 *  com.sun.jersey.api.client.UniformInterfaceException
 *  com.sun.jersey.api.client.WebResource
 *  com.sun.jersey.api.client.filter.ClientFilter
 *  com.sun.jersey.client.impl.ClientRequestImpl
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.UriBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.rest;

import com.atlassian.crowd.directory.query.GraphQuery;
import com.atlassian.crowd.directory.query.MicrosoftGraphQueryParam;
import com.atlassian.crowd.directory.query.MicrosoftGraphQueryParams;
import com.atlassian.crowd.directory.query.ODataSelect;
import com.atlassian.crowd.directory.query.ODataTop;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.directory.rest.entity.GraphDirectoryObjectList;
import com.atlassian.crowd.directory.rest.entity.PageableGraphList;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryGroupList;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryUserList;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroupList;
import com.atlassian.crowd.directory.rest.entity.user.GraphUsersList;
import com.atlassian.crowd.directory.rest.util.IoUtilsWrapper;
import com.atlassian.crowd.directory.rest.util.JerseyLoggingFilter;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.security.xml.SecureXmlParserFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.client.impl.ClientRequestImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AzureAdRestClient {
    private static final Logger log = LoggerFactory.getLogger(AzureAdRestClient.class);
    public static final String GRAPH_API_VERSION = "/v1.0";
    public static final String GRAPH_USERS_ENDPOINT_SUFFIX = "users";
    public static final String GRAPH_GROUPS_ENDPOINT_SUFFIX = "groups";
    public static final String METADATA_ENDPOINT_SUFFIX = "$metadata";
    public static final String MEMBER_OF_NAVIGATIONAL_PROPERTY = "memberOf";
    public static final String MEMBERS_NAVIGATIONAL_PROPERTY = "members";
    public static final String DELTA_QUERY_ENDPOINT_SUFFIX = "delta";
    public static final String TRASH_ENDPOINT_SUFFIX = "directory/deleteditems";
    private static final String SCHEMA_XPATH = "/Edmx/DataServices/Schema";
    private static final String DELTA_RETURN_PATHS_XPATH = "/Edmx/DataServices/Schema/Function[@Name='delta']/ReturnType";
    private static final String CHARSET_PARAMETER_NAME = "charset";
    private static final String ALIAS_ATTRIBUTE_NAME = "Alias";
    private static final String RETURN_TYPE_ATTRIBUTE_NAME = "Type";
    private static final String NAMESPACE_ATTRIBUTE_NAME = "Namespace";
    public static final String COLLECTION_TYPE_FORMAT = "Collection(%s.%s)";
    public static final String USER_SUFFIX = "user";
    public static final String GROUP_SUFFIX = "group";
    private final Client client;
    private final String graphBaseEndpoint;
    private final IoUtilsWrapper ioUtilsWrapper;

    @VisibleForTesting
    public Client getClient() {
        return this.client;
    }

    @SuppressFBWarnings(value={"XPATH_INJECTION"}, justification="No user input processed")
    public AzureAdRestClient(Client client, AzureApiUriResolver endpointDataProvider, IoUtilsWrapper ioUtilsWrapper) {
        this.client = client;
        this.graphBaseEndpoint = endpointDataProvider.getGraphApiUrl();
        this.ioUtilsWrapper = ioUtilsWrapper;
    }

    public GraphUsersList searchUsers(GraphQuery query) throws OperationFailedException {
        return this.handleRequest(() -> (GraphUsersList)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(GRAPH_USERS_ENDPOINT_SUFFIX).queryParams(MicrosoftGraphQueryParams.asQueryParams(query.getFilter(), query.getSelect(), query.getLimit())).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(GraphUsersList.class));
    }

    public GraphGroupList searchGroups(GraphQuery query) throws OperationFailedException {
        return this.handleRequest(() -> (GraphGroupList)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(GRAPH_GROUPS_ENDPOINT_SUFFIX).queryParams(MicrosoftGraphQueryParams.asQueryParams(query.getFilter(), query.getSelect(), query.getLimit())).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(GraphGroupList.class));
    }

    public GraphDirectoryObjectList getDirectParentsOfUser(String nameOrExternalId, ODataSelect select) throws OperationFailedException {
        return this.handleRequest(() -> (GraphDirectoryObjectList)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(GRAPH_USERS_ENDPOINT_SUFFIX).path(nameOrExternalId).path(MEMBER_OF_NAVIGATIONAL_PROPERTY).queryParams(MicrosoftGraphQueryParams.asQueryParams(ODataTop.FULL_PAGE, select)).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(GraphDirectoryObjectList.class));
    }

    public GraphDirectoryObjectList getDirectParentsOfGroup(String groupId, ODataSelect select) throws OperationFailedException {
        return this.handleRequest(() -> (GraphDirectoryObjectList)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(GRAPH_GROUPS_ENDPOINT_SUFFIX).path(groupId).path(MEMBER_OF_NAVIGATIONAL_PROPERTY).queryParams(MicrosoftGraphQueryParams.asQueryParams(ODataTop.FULL_PAGE, select)).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(GraphDirectoryObjectList.class));
    }

    public GraphDirectoryObjectList getDirectChildrenOfGroup(String groupId, ODataSelect select) throws OperationFailedException {
        return this.handleRequest(() -> (GraphDirectoryObjectList)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(GRAPH_GROUPS_ENDPOINT_SUFFIX).path(groupId).path(MEMBERS_NAVIGATIONAL_PROPERTY).queryParams(MicrosoftGraphQueryParams.asQueryParams(select)).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(GraphDirectoryObjectList.class));
    }

    public GraphDeltaQueryUserList performUsersDeltaQuery(MicrosoftGraphQueryParam parameter) throws OperationFailedException {
        return this.handleRequest(() -> (GraphDeltaQueryUserList)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(GRAPH_USERS_ENDPOINT_SUFFIX).path(DELTA_QUERY_ENDPOINT_SUFFIX).queryParams(MicrosoftGraphQueryParams.asQueryParams(parameter)).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(GraphDeltaQueryUserList.class));
    }

    public GraphDeltaQueryGroupList performGroupsDeltaQuery(MicrosoftGraphQueryParam ... parameters) throws OperationFailedException {
        return this.handleRequest(() -> (GraphDeltaQueryGroupList)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(GRAPH_GROUPS_ENDPOINT_SUFFIX).path(DELTA_QUERY_ENDPOINT_SUFFIX).queryParams(MicrosoftGraphQueryParams.asQueryParams(parameters)).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(GraphDeltaQueryGroupList.class));
    }

    @SuppressFBWarnings(value={"XXE_DOCUMENT"}, justification="uses atlassian-secure-xml")
    public boolean supportsDeltaQuery() {
        try {
            log.debug("Fetching metadata from URI {}", (Object)UriBuilder.fromUri((String)this.getGraphBaseResource()).path(METADATA_ENDPOINT_SUFFIX).build(new Object[0]).toString());
            ClientResponse response = (ClientResponse)this.loggingResource(this.client.resource(this.getGraphBaseResource())).path(METADATA_ENDPOINT_SUFFIX).get(ClientResponse.class);
            this.checkStatusCode(response);
            Charset encoding = this.extractEncoding(response);
            String xmlResponseBody = (String)response.getEntity(String.class);
            Document metadataDocument = SecureXmlParserFactory.newDocumentBuilder().parse(this.ioUtilsWrapper.toInputStream(xmlResponseBody, encoding));
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression schemaXpath = xPath.compile(SCHEMA_XPATH);
            NodeList schemaNodes = (NodeList)schemaXpath.evaluate(metadataDocument, XPathConstants.NODESET);
            XPathExpression deltaGroupsQueryXpath = xPath.compile(DELTA_RETURN_PATHS_XPATH);
            NodeList deltaReturnTypes = (NodeList)deltaGroupsQueryXpath.evaluate(metadataDocument, XPathConstants.NODESET);
            return this.supportsUsersAndGroupsDeltaQuery(schemaNodes, deltaReturnTypes);
        }
        catch (IOException | XPathExpressionException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean supportsUsersAndGroupsDeltaQuery(NodeList schemaNodes, NodeList deltaReturnTypes) {
        Set possiblePrefixes = IntStream.range(0, schemaNodes.getLength()).mapToObj(schemaNodes::item).map(node -> this.getPresentAttributeValues((Node)node, ALIAS_ATTRIBUTE_NAME, NAMESPACE_ATTRIBUTE_NAME)).flatMap(Collection::stream).collect(Collectors.toSet());
        Set userTypes = possiblePrefixes.stream().map(a -> String.format(COLLECTION_TYPE_FORMAT, a, USER_SUFFIX)).collect(Collectors.toSet());
        Set groupTypes = possiblePrefixes.stream().map(a -> String.format(COLLECTION_TYPE_FORMAT, a, GROUP_SUFFIX)).collect(Collectors.toSet());
        Set types = IntStream.range(0, deltaReturnTypes.getLength()).mapToObj(deltaReturnTypes::item).flatMap(node -> this.getPresentAttributeValues((Node)node, RETURN_TYPE_ATTRIBUTE_NAME).stream()).collect(Collectors.toSet());
        return !Collections.disjoint(userTypes, types) && !Collections.disjoint(groupTypes, types);
    }

    private Set<String> getPresentAttributeValues(Node node, String ... names) {
        Optional<NamedNodeMap> attributes = Optional.ofNullable(node).map(Node::getAttributes);
        if (!attributes.isPresent()) {
            return Collections.emptySet();
        }
        HashSet<String> result = new HashSet<String>();
        for (String name : names) {
            attributes.map(a -> a.getNamedItem(name)).map(Node::getNodeValue).ifPresent(result::add);
        }
        return result;
    }

    private void checkStatusCode(ClientResponse response) {
        if (response.getStatus() >= 300) {
            ClientRequestImpl request = new ClientRequestImpl(response.getLocation(), "GET");
            throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
        }
    }

    private Charset extractEncoding(ClientResponse response) {
        return response.getType().getParameters().entrySet().stream().filter(entry -> ((String)entry.getKey()).equals(CHARSET_PARAMETER_NAME)).findFirst().map(entry -> Charset.forName((String)entry.getValue())).orElse(Charsets.UTF_8);
    }

    @VisibleForTesting
    public String getGraphBaseResource() {
        return this.graphBaseEndpoint + GRAPH_API_VERSION;
    }

    public <T extends PageableGraphList> T getNextPage(String nextLink, Class<T> resultsClass) throws OperationFailedException {
        return (T)this.handleRequest(() -> (PageableGraphList)this.loggingResource(this.client.resource(nextLink)).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(resultsClass));
    }

    public <T extends PageableGraphList> T getNextPage(String nextLink, Class<T> resultsClass, ODataTop limit) throws OperationFailedException {
        URI nextLinkWithUpdatedLimit = UriBuilder.fromUri((String)nextLink).replaceQueryParam("$top", new Object[]{limit.asRawValue()}).build(new Object[0]);
        return (T)this.handleRequest(() -> (PageableGraphList)this.loggingResource(this.client.resource(nextLinkWithUpdatedLimit)).accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).get(resultsClass));
    }

    @VisibleForTesting
    public <T> T handleRequest(Supplier<T> requestSupplier) throws OperationFailedException {
        try {
            return requestSupplier.get();
        }
        catch (UniformInterfaceException e) {
            String message = String.format("Microsoft Graph API has returned an error response. Response status code: %d, content %s", e.getResponse().getStatus(), e.getResponse().getEntity(String.class));
            throw new OperationFailedException(message, (Throwable)e);
        }
    }

    private WebResource loggingResource(WebResource baseResource) {
        baseResource.addFilter((ClientFilter)new JerseyLoggingFilter());
        return baseResource;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.projectcreate.linking.spi.AggregateRootLinkType
 *  com.atlassian.plugins.projectcreate.linking.spi.LocalRoot
 *  com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot
 *  com.atlassian.plugins.projectcreate.spi.AggregateRoot
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability
 *  com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.sun.jersey.spi.resource.PerRequest
 *  io.atlassian.fugue.Either
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.plugins.projectcreate.producer.link;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.projectcreate.linking.spi.AggregateRootLinkType;
import com.atlassian.plugins.projectcreate.linking.spi.LocalRoot;
import com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot;
import com.atlassian.plugins.projectcreate.producer.crud.service.AggregateRootTypeCapabilitiesService;
import com.atlassian.plugins.projectcreate.producer.link.service.RootLinkCapabilityService;
import com.atlassian.plugins.projectcreate.producer.link.util.InternalHostApplicationAccessor;
import com.atlassian.plugins.projectcreate.producer.link.util.LinkingUrlFactory;
import com.atlassian.plugins.projectcreate.spi.AggregateRoot;
import com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability;
import com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.sun.jersey.spi.resource.PerRequest;
import io.atlassian.fugue.Either;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/")
@PerRequest
public class ProjectLinkingResource {
    private final InternalHostApplication internalHostApplication;
    private final LinkingUrlFactory linkingUrlFactory;
    private final TransactionTemplate transactionTemplate;
    private final Logger log = LoggerFactory.getLogger(ProjectLinkingResource.class);
    private final AggregateRootTypeCapabilitiesService aggregateRootTypeCapabilitiesService;
    private final RootLinkCapabilityService rootLinkCapabilityService;
    private final Object entityLinkUpdateLock = new Object();

    @Autowired
    public ProjectLinkingResource(InternalHostApplicationAccessor internalHostApplicationAccessor, LinkingUrlFactory linkingUrlFactory, @ComponentImport TransactionTemplate transactionTemplate, AggregateRootTypeCapabilitiesService aggregateRootTypeCapabilitiesService, RootLinkCapabilityService rootLinkCapabilityService) {
        this.internalHostApplication = internalHostApplicationAccessor.get();
        this.linkingUrlFactory = linkingUrlFactory;
        this.transactionTemplate = transactionTemplate;
        this.aggregateRootTypeCapabilitiesService = aggregateRootTypeCapabilitiesService;
        this.rootLinkCapabilityService = rootLinkCapabilityService;
    }

    private Links getLinks(String selfSuffix, String collectionSuffix) {
        String baseUrl = StringUtils.stripEnd((String)this.internalHostApplication.getBaseUrl().toString(), (String)"/");
        Links links = new Links();
        links.setSelf(baseUrl + "/rest/capabilities" + StringUtils.defaultString((String)selfSuffix));
        links.setCollection(baseUrl + "/rest/capabilities" + StringUtils.defaultString((String)collectionSuffix));
        links.setBase(baseUrl);
        links.setAwareness(baseUrl + "/rest/capabilities/awareness");
        links.setAggregateRoots(baseUrl + "/rest/capabilities/aggregate-roots");
        return links;
    }

    private List<LocalRoot> getExistingLocalRoots() {
        ArrayList<LocalRoot> existingLocalRoots = new ArrayList<LocalRoot>();
        List<AggregateRootTypeCapability> capabilities = this.aggregateRootTypeCapabilitiesService.getCapabilities();
        for (AggregateRootTypeCapability capability : capabilities) {
            final String type = capability.getType();
            Iterables.addAll(existingLocalRoots, (Iterable)Iterables.transform((Iterable)capability.getExistingRoots(), (Function)new Function<AggregateRoot, LocalRoot>(){

                public LocalRoot apply(@Nullable AggregateRoot input) {
                    if (input != null) {
                        return new LocalRoot(type, input.key());
                    }
                    return null;
                }
            }));
        }
        return existingLocalRoots;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getLinkables() {
        return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

            public Response doInTransaction() {
                List localRoots = ProjectLinkingResource.this.getExistingLocalRoots();
                Iterable<AggregateRootLinkType> linkTypes = ProjectLinkingResource.this.rootLinkCapabilityService.getSortedLinkers();
                ArrayList<String> links = new ArrayList<String>();
                for (LocalRoot localRoot : localRoots) {
                    for (AggregateRootLinkType linkType : linkTypes) {
                        Either eitherStatusOrRemoteRoots = linkType.getRemoteLinkedRootsForLinkedRoot(localRoot);
                        if (eitherStatusOrRemoteRoots.isLeft()) {
                            ResponseStatusWithMessage statusWithMessage = (ResponseStatusWithMessage)eitherStatusOrRemoteRoots.left().get();
                            return Response.status((Response.Status)statusWithMessage.status()).entity((Object)statusWithMessage.messageI18nKey()).build();
                        }
                        Iterable remoteRoots = (Iterable)eitherStatusOrRemoteRoots.right().get();
                        for (RemoteRoot remoteRoot : remoteRoots) {
                            links.add(ProjectLinkingResource.this.linkingUrlFactory.getLinkDetailsUrl(localRoot, remoteRoot));
                        }
                    }
                }
                LinkablesResponse response = new LinkablesResponse();
                response.setLinkables(links);
                response.setLinks(ProjectLinkingResource.this.getLinks("/aggregate-root-link", ""));
                return Response.ok((Object)response).build();
            }
        });
    }

    @GET
    @Path(value="/{buckettype}/{key}/{applinkid}/{remotetype}/{remotekey}")
    @Produces(value={"application/json"})
    public Response getRemoteLinkDetails(@Context HttpServletRequest request, final @PathParam(value="buckettype") String bucketType, final @PathParam(value="key") String key, final @PathParam(value="applinkid") String applinkid, final @PathParam(value="remotetype") String remoteType, final @PathParam(value="remotekey") String remoteKey) {
        return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

            public Response doInTransaction() {
                try {
                    LocalRoot localRoot = new LocalRoot(bucketType, key);
                    Iterable<AggregateRootLinkType> linkTypes = ProjectLinkingResource.this.rootLinkCapabilityService.getSortedLinkers();
                    for (AggregateRootLinkType linkType : linkTypes) {
                        Either eitherStatusOrRemoteRoots = linkType.getRemoteLinkedRootsForLinkedRoot(localRoot);
                        if (eitherStatusOrRemoteRoots.isLeft()) {
                            ResponseStatusWithMessage statusWithMessage = (ResponseStatusWithMessage)eitherStatusOrRemoteRoots.left().get();
                            return Response.status((Response.Status)statusWithMessage.status()).entity((Object)statusWithMessage.messageI18nKey()).build();
                        }
                        Iterable remoteRoots = (Iterable)eitherStatusOrRemoteRoots.right().get();
                        for (RemoteRoot remoteRoot : remoteRoots) {
                            if (!ProjectLinkingResource.this.linkingUrlFactory.getInstanceIdHash(remoteRoot.getRemoteUrl()).equals(applinkid) || !remoteRoot.getRootType().equals(remoteType) || !remoteRoot.getRootKey().equals(remoteKey)) continue;
                            BucketDetailsResponse response = new BucketDetailsResponse();
                            response.setLocal(ProjectLinkingResource.this.linkingUrlFactory.getRootUrl(localRoot));
                            response.setRemote(ProjectLinkingResource.this.linkingUrlFactory.getRootUrlForRemote(remoteRoot));
                            response.setLinks(ProjectLinkingResource.this.getLinks("/aggregate-root-link/" + URLEncoder.encode(bucketType, "UTF-8") + "/" + URLEncoder.encode(key, "UTF-8") + "/" + URLEncoder.encode(applinkid, "UTF-8") + "/" + remoteRoot.getRootType() + "/" + URLEncoder.encode(remoteRoot.getRootKey(), "UTF-8"), "/aggregate-root-link/" + URLEncoder.encode(bucketType, "UTF-8") + "/" + URLEncoder.encode(key, "UTF-8")));
                            return Response.ok((Object)response).build();
                        }
                    }
                    return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                }
                catch (Exception e) {
                    ProjectLinkingResource.this.log.error("putLinkedBucketForKey() threw exception", (Throwable)e);
                    return Response.serverError().build();
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createLinkedBucket(@Context HttpServletRequest request, final PostLinkedBucketRequest createLinkRequest) {
        Object object = this.entityLinkUpdateLock;
        synchronized (object) {
            return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

                public Response doInTransaction() {
                    try {
                        LocalRoot localRoot = ProjectLinkingResource.this.linkingUrlFactory.getLocalRootForUrl(createLinkRequest.getLocal());
                        RemoteRoot remoteRoot = ProjectLinkingResource.this.linkingUrlFactory.getRemoteRootForUrl(createLinkRequest.getTarget());
                        Iterable<AggregateRootLinkType> rootLinkTypes = ProjectLinkingResource.this.rootLinkCapabilityService.getSortedLinkers();
                        for (AggregateRootLinkType rootLinkType : rootLinkTypes) {
                            if (!rootLinkType.canCreateLinkToType(localRoot.getRootType(), remoteRoot.getRootType())) continue;
                            Response.Status status = rootLinkType.createLink(localRoot, remoteRoot);
                            return Response.status((Response.Status)status).build();
                        }
                        return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
                    }
                    catch (Exception e) {
                        ProjectLinkingResource.this.log.error("createLinkedBucket() threw exception", (Throwable)e);
                        return Response.serverError().build();
                    }
                }
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @DELETE
    @Path(value="/{buckettype}/{key}/{applinkid}/{remotetype}/{remotekey}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response deleteLinkedBucketForKey(@Context HttpServletRequest request, final @PathParam(value="buckettype") String bucketType, final @PathParam(value="key") String key, final @PathParam(value="applinkid") String applinkid, final @PathParam(value="remotetype") String remoteType, final @PathParam(value="remotekey") String remoteKey) {
        Object object = this.entityLinkUpdateLock;
        synchronized (object) {
            return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

                public Response doInTransaction() {
                    try {
                        LocalRoot localRoot = new LocalRoot(bucketType, key);
                        Iterable<AggregateRootLinkType> linkTypes = ProjectLinkingResource.this.rootLinkCapabilityService.getSortedLinkers();
                        for (AggregateRootLinkType linkType : linkTypes) {
                            Either eitherStatusOrRemoteRoots = linkType.getRemoteLinkedRootsForLinkedRoot(localRoot);
                            if (eitherStatusOrRemoteRoots.isLeft()) {
                                ResponseStatusWithMessage statusWithMessage = (ResponseStatusWithMessage)eitherStatusOrRemoteRoots.left().get();
                                return Response.status((Response.Status)statusWithMessage.status()).entity((Object)statusWithMessage.messageI18nKey()).build();
                            }
                            Iterable remoteRoots = (Iterable)eitherStatusOrRemoteRoots.right().get();
                            for (RemoteRoot remoteRoot : remoteRoots) {
                                if (!ProjectLinkingResource.this.linkingUrlFactory.getInstanceIdHash(remoteRoot.getRemoteUrl()).equals(applinkid) || !remoteRoot.getRootType().equals(remoteType) || !remoteRoot.getRootKey().equals(remoteKey)) continue;
                                linkType.deleteLink(localRoot, remoteRoot);
                                return Response.noContent().build();
                            }
                        }
                        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                    }
                    catch (Exception ex) {
                        ProjectLinkingResource.this.log.error("deleteLinkedBucketForKey() threw exception", (Throwable)ex);
                        return Response.serverError().build();
                    }
                }
            });
        }
    }

    public static class BucketDetailsResponse {
        @XmlElement
        private Links links;
        @XmlElement
        private String local;
        @XmlElement
        private String remote;

        public Links getLinks() {
            return this.links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

        public String getLocal() {
            return this.local;
        }

        public void setLocal(String local) {
            this.local = local;
        }

        public String getRemote() {
            return this.remote;
        }

        public void setRemote(String remote) {
            this.remote = remote;
        }
    }

    public static class PostLinkedBucketRequest {
        @XmlElement
        private String local;
        @XmlElement
        private String target;

        public String getLocal() {
            return this.local;
        }

        public void setLocal(String local) {
            this.local = local;
        }

        public String getTarget() {
            return this.target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }

    public static class LinkedBucketsResponse {
        @XmlElement
        private Links links;
        @XmlElement
        private List<String> linkedAggregateRoots;

        public Links getLinks() {
            return this.links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

        public List<String> getLinkedAggregateRoots() {
            return this.linkedAggregateRoots;
        }

        public void setLinkedAggregateRoots(List<String> linkedAggregateRoots) {
            this.linkedAggregateRoots = linkedAggregateRoots;
        }
    }

    public static class LinkablesResponse {
        @XmlElement
        private Links links;
        @XmlElement(name="aggregate-root-links")
        private List<String> linkables;

        public Links getLinks() {
            return this.links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

        public List<String> getLinkables() {
            return this.linkables;
        }

        public void setLinkables(List<String> linkables) {
            this.linkables = linkables;
        }
    }

    public static class Links {
        @XmlElement
        private String self;
        @XmlElement
        private String collection;
        @XmlElement
        private String base;
        @XmlElement
        private String awareness;
        @XmlElement(name="aggregate-root")
        private String aggregateRoots;

        public String getSelf() {
            return this.self;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public String getCollection() {
            return this.collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

        public String getBase() {
            return this.base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getAwareness() {
            return this.awareness;
        }

        public void setAwareness(String awareness) {
            this.awareness = awareness;
        }

        public String getAggregateRoots() {
            return this.aggregateRoots;
        }

        public void setAggregateRoots(String aggregateRoots) {
            this.aggregateRoots = aggregateRoots;
        }
    }
}


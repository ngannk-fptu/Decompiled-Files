/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.projectcreate.spi.AggregateRoot
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootSubType
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability
 *  com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.CorsAllowed
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.sun.jersey.spi.resource.PerRequest
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.plugins.projectcreate.producer.crud.rest;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.projectcreate.producer.crud.rest.AggregateRootsCapabilitiesRepresentation;
import com.atlassian.plugins.projectcreate.producer.crud.rest.AggregateRootsRepresentation;
import com.atlassian.plugins.projectcreate.producer.crud.rest.NewOrUpdatedAggregateRootRepresentation;
import com.atlassian.plugins.projectcreate.producer.crud.rest.SingleAggregateRootRepresentation;
import com.atlassian.plugins.projectcreate.producer.crud.service.AggregateRootTypeCapabilitiesService;
import com.atlassian.plugins.projectcreate.producer.link.util.InternalHostApplicationAccessor;
import com.atlassian.plugins.projectcreate.spi.AggregateRoot;
import com.atlassian.plugins.projectcreate.spi.AggregateRootSubType;
import com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability;
import com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.CorsAllowed;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.sun.jersey.spi.resource.PerRequest;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/")
@Produces(value={"application/json"})
@PerRequest
@CorsAllowed
public class AggregateRootsCRUDResource {
    public static final String AGGREGATE_ROOT_CAPABILITY_URL = "/rest/capabilities/aggregate-root/";
    private final Function<ResponseStatusWithMessage, Response> RESPONSE_BUILDER = new Function<ResponseStatusWithMessage, Response>(){

        public Response apply(ResponseStatusWithMessage responseStatusWithMessage) {
            return AggregateRootsCRUDResource.this.constructResponse(responseStatusWithMessage);
        }
    };
    private final AggregateRootTypeCapabilitiesService aggregateRootTypeCapabilitiesService;
    private final ApplicationProperties applicationProperties;
    private final I18nResolver i18nResolver;
    private final UserManager userManager;
    private final TransactionTemplate transactionTemplate;
    private final InternalHostApplication internalHostApplication;

    @Autowired
    public AggregateRootsCRUDResource(AggregateRootTypeCapabilitiesService aggregateRootTypeCapabilitiesService, @ComponentImport ApplicationProperties applicationProperties, @ComponentImport I18nResolver i18nResolver, @ComponentImport UserManager userManager, @ComponentImport TransactionTemplate transactionTemplate, InternalHostApplicationAccessor internalHostApplicationAccessor) {
        this.aggregateRootTypeCapabilitiesService = aggregateRootTypeCapabilitiesService;
        this.applicationProperties = applicationProperties;
        this.i18nResolver = i18nResolver;
        this.userManager = userManager;
        this.transactionTemplate = transactionTemplate;
        this.internalHostApplication = internalHostApplicationAccessor.get();
    }

    @GET
    @AnonymousAllowed
    public Response getRootManagementCapabilities() {
        return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

            public Response doInTransaction() {
                return Response.ok((Object)new AggregateRootsCapabilitiesRepresentation(AggregateRootsCRUDResource.this.i18nResolver, AggregateRootsCRUDResource.this.aggregateRootTypeCapabilitiesService.getCapabilities(), AggregateRootsCRUDResource.this.internalHostApplication.getBaseUrl().toString())).build();
            }
        });
    }

    @GET
    @Path(value="{rootType}")
    public Response getRootsOfType(final @PathParam(value="rootType") String rootType, @QueryParam(value="subtype") String subType) {
        return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

            public Response doInTransaction() {
                return AggregateRootsCRUDResource.this.withUsername((Function<String, Response>)((Function)new Function<String, Response>(){

                    public Response apply(String username) {
                        Either capabilityOrErrorResponse = AggregateRootsCRUDResource.this.resolveCapabilityAndCheckPermissions(rootType, username, true);
                        return (Response)capabilityOrErrorResponse.fold((java.util.function.Function)new Function<AggregateRootTypeCapability, Response>(){

                            public Response apply(AggregateRootTypeCapability input) {
                                return Response.ok((Object)new AggregateRootsRepresentation(input.getExistingRoots(), AggregateRootsCRUDResource.this.internalHostApplication.getBaseUrl().toString(), rootType)).build();
                            }
                        }, (java.util.function.Function)Functions.identity());
                    }
                }));
            }
        });
    }

    @PUT
    @Consumes(value={"application/json"})
    @Path(value="{rootType}/{rootKey}")
    public Response createOrUpdateRootOfType(final @PathParam(value="rootType") String rootType, final @PathParam(value="rootKey") String rootKey, final NewOrUpdatedAggregateRootRepresentation newAggregateRoot) {
        return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

            public Response doInTransaction() {
                return AggregateRootsCRUDResource.this.withUsername((Function<String, Response>)((Function)new Function<String, Response>(){

                    public Response apply(final String username) {
                        Either capabilityOrErrorResponse = AggregateRootsCRUDResource.this.resolveCapabilityAndCheckPermissions(rootType, username, true);
                        return (Response)capabilityOrErrorResponse.fold((java.util.function.Function)new Function<AggregateRootTypeCapability, Response>(){

                            public Response apply(final AggregateRootTypeCapability capability) {
                                return (Response)Option.option((Object)newAggregateRoot.subtype).fold((Supplier)new com.google.common.base.Supplier<Response>(){

                                    public Response get() {
                                        return AggregateRootsCRUDResource.this.createRootAndConstructResponse(capability, username, rootKey, newAggregateRoot.label, (Option<String>)Option.none(), newAggregateRoot.context);
                                    }
                                }, (java.util.function.Function)new Function<String, Response>(){

                                    public Response apply(final String subtypeKey) {
                                        Iterable aggregateRootSubTypes = capability.getSubTypes();
                                        Option matchingSubtype = Iterables.findFirst((Iterable)aggregateRootSubTypes, (Predicate)new com.google.common.base.Predicate<AggregateRootSubType>(){

                                            public boolean apply(AggregateRootSubType input) {
                                                return input.getKey().equals(subtypeKey);
                                            }
                                        });
                                        if (matchingSubtype.isEmpty()) {
                                            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                                        }
                                        return AggregateRootsCRUDResource.this.createRootAndConstructResponse(capability, username, rootKey, newAggregateRoot.label, (Option<String>)Option.some((Object)subtypeKey), newAggregateRoot.context);
                                    }
                                });
                            }
                        }, (java.util.function.Function)Functions.identity());
                    }
                }));
            }
        });
    }

    @GET
    @Path(value="{rootType}/{rootKey}")
    public Response getRoot(final @PathParam(value="rootType") String rootType, final @PathParam(value="rootKey") String rootKey) {
        return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

            public Response doInTransaction() {
                return AggregateRootsCRUDResource.this.withUsername((Function<String, Response>)((Function)new Function<String, Response>(){

                    public Response apply(final String username) {
                        Either capabilityOrErrorResponse = AggregateRootsCRUDResource.this.resolveCapabilityAndCheckPermissions(rootType, username, false);
                        return (Response)capabilityOrErrorResponse.fold((java.util.function.Function)new Function<AggregateRootTypeCapability, Response>(){

                            public Response apply(AggregateRootTypeCapability capability) {
                                Option maybeRoot = capability.getRootByKey(Option.some((Object)username), rootKey);
                                return (Response)maybeRoot.fold((Supplier)new com.google.common.base.Supplier<Response>(){

                                    public Response get() {
                                        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                                    }
                                }, (java.util.function.Function)new Function<AggregateRoot, Response>(){

                                    public Response apply(AggregateRoot root) {
                                        return Response.ok((Object)new SingleAggregateRootRepresentation(root, AggregateRootsCRUDResource.this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), rootType)).build();
                                    }
                                });
                            }
                        }, (java.util.function.Function)Functions.identity());
                    }
                }));
            }
        });
    }

    @DELETE
    @Path(value="{rootType}/{rootKey}")
    public Response deleteRoot(final @PathParam(value="rootType") String rootType, final @PathParam(value="rootKey") String rootKey) {
        return (Response)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Response>(){

            public Response doInTransaction() {
                return AggregateRootsCRUDResource.this.withUsername((Function<String, Response>)((Function)new Function<String, Response>(){

                    public Response apply(final String username) {
                        Either capabilityOrErrorResponse = AggregateRootsCRUDResource.this.resolveCapabilityAndCheckPermissions(rootType, username, false);
                        return (Response)capabilityOrErrorResponse.fold((java.util.function.Function)new Function<AggregateRootTypeCapability, Response>(){

                            public Response apply(final AggregateRootTypeCapability capability) {
                                Option maybeRoot = capability.getRootByKey(Option.some((Object)username), rootKey);
                                return (Response)maybeRoot.fold((Supplier)new com.google.common.base.Supplier<Response>(){

                                    public Response get() {
                                        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                                    }
                                }, (java.util.function.Function)new Function<AggregateRoot, Response>(){

                                    public Response apply(AggregateRoot root) {
                                        return (Response)capability.deleteRoot(username, rootKey).fold((java.util.function.Function)AggregateRootsCRUDResource.this.RESPONSE_BUILDER, (java.util.function.Function)AggregateRootsCRUDResource.this.RESPONSE_BUILDER);
                                    }
                                });
                            }
                        }, (java.util.function.Function)Functions.identity());
                    }
                }));
            }
        });
    }

    private Response createRootAndConstructResponse(final AggregateRootTypeCapability capability, String username, final String newRootKey, String newRootLabel, Option<String> subtype, Map<String, String> context) {
        Either failureOrRoot = capability.createRoot(username, newRootKey, newRootLabel, subtype, context);
        return (Response)failureOrRoot.fold(this.RESPONSE_BUILDER, (java.util.function.Function)new Function<AggregateRoot, Response>(){

            public Response apply(AggregateRoot root) {
                URI uri;
                String baseUrl = AggregateRootsCRUDResource.this.internalHostApplication.getBaseUrl().toString();
                try {
                    uri = new URI(baseUrl + AggregateRootsCRUDResource.AGGREGATE_ROOT_CAPABILITY_URL + URLEncoder.encode(capability.getLabelI18nKey(), "UTF-8") + "/" + URLEncoder.encode(newRootKey, "UTF-8"));
                }
                catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                return Response.created((URI)uri).entity((Object)new SingleAggregateRootRepresentation(root, baseUrl, capability.getType())).build();
            }
        });
    }

    private Response constructResponse(ResponseStatusWithMessage responseStatusWithMessage) {
        return Response.status((Response.Status)responseStatusWithMessage.status()).entity((Object)this.i18nResolver.getText(responseStatusWithMessage.messageI18nKey(), (Serializable[])responseStatusWithMessage.getMessageArgs())).build();
    }

    private Either<AggregateRootTypeCapability, Response> resolveCapabilityAndCheckPermissions(String rootType, final String remoteUsername, final boolean checkCreatePermission) {
        Option<AggregateRootTypeCapability> capability = this.aggregateRootTypeCapabilitiesService.getCapability(rootType);
        return (Either)capability.fold((Supplier)new com.google.common.base.Supplier<Either<AggregateRootTypeCapability, Response>>(){

            public Either<AggregateRootTypeCapability, Response> get() {
                return Either.right((Object)Response.status((Response.Status)Response.Status.NOT_FOUND).build());
            }
        }, (java.util.function.Function)new Function<AggregateRootTypeCapability, Either<AggregateRootTypeCapability, Response>>(){

            public Either<AggregateRootTypeCapability, Response> apply(AggregateRootTypeCapability input) {
                if (checkCreatePermission && !input.canUserCreateRoot(remoteUsername)) {
                    return Either.right((Object)Response.status((Response.Status)Response.Status.FORBIDDEN).build());
                }
                return Either.left((Object)input);
            }
        });
    }

    private Response withUsername(Function<String, Response> usernameToResponseFunction) {
        return (Response)Option.option((Object)this.userManager.getRemoteUsername()).fold((Supplier)new com.google.common.base.Supplier<Response>(){

            public Response get() {
                return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
            }
        }, usernameToResponseFunction);
    }
}


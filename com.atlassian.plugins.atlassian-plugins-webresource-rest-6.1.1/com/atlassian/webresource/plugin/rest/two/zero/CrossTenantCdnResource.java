/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.PluginResourceLocator
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.cdn.mapper.Mapping
 *  com.atlassian.plugin.webresource.cdn.mapper.WebResourceMapper
 *  com.atlassian.plugin.webresource.impl.config.Config
 *  com.atlassian.plugin.webresource.prebake.PrebakeConfig
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webresource.plugin.rest.two.zero;

import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.cdn.mapper.Mapping;
import com.atlassian.plugin.webresource.cdn.mapper.WebResourceMapper;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.prebake.PrebakeConfig;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="ct-cdn")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class CrossTenantCdnResource {
    private static final Logger log = LoggerFactory.getLogger(CrossTenantCdnResource.class);
    private final WebResourceIntegration webResourceIntegration;
    private final Config config;

    public CrossTenantCdnResource(WebResourceIntegration webResourceIntegration, PluginResourceLocator pluginResourceLocator) {
        this.webResourceIntegration = webResourceIntegration;
        this.config = pluginResourceLocator.temporaryWayToGetGlobalsDoNotUseIt().getConfig();
    }

    @Path(value="status")
    @GET
    @Produces(value={"application/json"})
    public Response healthInfo(@QueryParam(value="includeMappings") @DefaultValue(value="true") boolean includeMappings) {
        log.debug("Collection general info about CT-CDN");
        String hash = this.config.computeGlobalStateHash();
        WebResourceMapper wrm = this.config.getWebResourceMapper();
        Optional prebakeConfig = this.webResourceIntegration.getCDNStrategy() != null ? this.webResourceIntegration.getCDNStrategy().getPrebakeConfig() : Optional.empty();
        Map<String, List<String>> simpleMappings = StreamSupport.stream(wrm.mappings().all().spliterator(), false).collect(Collectors.toMap(Mapping::originalResource, Mapping::mappedResources));
        Info.WebResourceMapper wrmInfo = new Info.WebResourceMapper(includeMappings ? simpleMappings : null, simpleMappings.size(), simpleMappings.values().stream().mapToInt(List::size).sum(), wrm.getClass().getName());
        return Response.ok((Object)new Info(new Info.State(this.config.getCtCdnBaseUrl(), hash, this.webResourceIntegration.isCtCdnMappingEnabled()), new Info.PreBaker(this.config.isPreBakeEnabled()), new Info.PrebakeConfig(prebakeConfig.map(PrebakeConfig::getPattern).orElse("[EMPTY]"), prebakeConfig.map(pc -> pc.getMappingLocation(hash).getAbsolutePath()).orElse("[EMPTY]"), prebakeConfig.map(pc -> pc.getMappingLocation(hash).exists()).orElse(false)), wrmInfo)).build();
    }

    @Path(value="mappings")
    @PUT
    @Produces(value={"application/json"})
    public final Response reloadMappings() {
        return this.whenPreBakeIsEnabled(() -> {
            log.info("Trying to reload WebResourceMapper");
            try {
                this.config.reloadWebResourceMapper();
                return Response.ok((Object)new ReloadStatus("Mappings were reloaded", this.config.getWebResourceMapper().mappings().size(), null)).build();
            }
            catch (Exception e) {
                log.warn(e.getMessage(), (Throwable)e);
                return Response.ok((Object)new ReloadStatus(e.getMessage(), this.config.getWebResourceMapper().mappings().size(), ExceptionUtils.getStackTrace((Throwable)e))).build();
            }
        });
    }

    private Response whenPreBakeIsEnabled(Supplier<Response> normalResponse) {
        if (this.config.isPreBakeEnabled()) {
            return normalResponse.get();
        }
        log.warn("Pre-baking called but feature is not enabled!");
        return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
    }

    public static class Info {
        public State state;
        public PreBaker preBaker;
        public PrebakeConfig prebakeConfig;
        public WebResourceMapper webResourceMapper;

        public Info(State state, PreBaker preBaker, PrebakeConfig prebakeConfig, WebResourceMapper webResourceMapper) {
            this.state = state;
            this.preBaker = preBaker;
            this.prebakeConfig = prebakeConfig;
            this.webResourceMapper = webResourceMapper;
        }

        public static class WebResourceMapper {
            public String implementationClass;
            public int numberOfEntries;
            public int countOfValues;
            public Map<String, List<String>> mappings;

            public WebResourceMapper(Map<String, List<String>> mappings, int numberOfEntries, int countOfValues, String implementationClass) {
                this.mappings = mappings;
                this.numberOfEntries = numberOfEntries;
                this.countOfValues = countOfValues;
                this.implementationClass = implementationClass;
            }
        }

        public static class PrebakeConfig {
            public String mappingFilePattern;
            public String mappingFileLocation;
            public boolean fileExists;

            public PrebakeConfig(String mappingFilePattern, String mappingFileLocation, boolean fileExists) {
                this.mappingFilePattern = mappingFilePattern;
                this.mappingFileLocation = mappingFileLocation;
                this.fileExists = fileExists;
            }
        }

        public static class PreBaker {
            public boolean enabled;

            public PreBaker(boolean enabled) {
                this.enabled = enabled;
            }
        }

        public static class State {
            public String baseUrl;
            public String productStateHash;
            public boolean enabled;

            public State(String baseUrl, String productStateHash, boolean enabled) {
                this.baseUrl = baseUrl;
                this.productStateHash = productStateHash;
                this.enabled = enabled;
            }
        }
    }

    public static class ReloadStatus {
        public String status;
        public int size;
        public String exception;

        public ReloadStatus(String status, int size, String exception) {
            this.status = status;
            this.size = size;
            this.exception = exception;
        }
    }
}


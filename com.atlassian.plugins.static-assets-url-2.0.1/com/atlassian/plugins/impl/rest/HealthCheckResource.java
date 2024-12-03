/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.impl.rest;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/health-checks")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class HealthCheckResource {
    private PluginAccessor pluginAccessor;

    public HealthCheckResource(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @GET
    public Response get() {
        List allWebResources = this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebResourceModuleDescriptor.class);
        Map<String, List<WebResourceWithNonPublicFonts>> pluginKeysAndResources = allWebResources.stream().map(x$0 -> WebResourceWithNonPublicFonts.from(x$0)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.groupingBy(WebResourceWithNonPublicFonts::getPluginKey));
        List result = pluginKeysAndResources.entrySet().stream().map(entry -> new PluginWithNonPublicFonts((String)entry.getKey(), (List)entry.getValue())).collect(Collectors.toList());
        return Response.ok(result).build();
    }

    private static class WebResourceWithNonPublicFonts {
        private final String pluginKey;
        @JsonProperty
        public final String moduleKey;
        @JsonProperty
        public final List<String> nonPublicFontResources;

        public WebResourceWithNonPublicFonts(String pluginKey, String moduleKey, List<String> nonPublicFontResources) {
            this.pluginKey = pluginKey;
            this.moduleKey = moduleKey;
            this.nonPublicFontResources = nonPublicFontResources;
        }

        public String getPluginKey() {
            return this.pluginKey;
        }

        private static Optional<WebResourceWithNonPublicFonts> from(WebResourceModuleDescriptor webResourceModuleDescriptor) {
            List<String> nonPublicFonts = WebResourceWithNonPublicFonts.getNonPublicFonts(webResourceModuleDescriptor);
            if (nonPublicFonts.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new WebResourceWithNonPublicFonts(webResourceModuleDescriptor.getPluginKey(), webResourceModuleDescriptor.getKey(), nonPublicFonts));
        }

        private static List<String> getNonPublicFonts(WebResourceModuleDescriptor webResource) {
            return webResource.getResourceDescriptors().stream().filter(rd -> Objects.equals(rd.getType(), "download")).filter(WebResourceWithNonPublicFonts::isFont).filter(rd -> !WebResourceWithNonPublicFonts.isPubliclyAvailable(rd)).map(ResourceDescriptor::getName).collect(Collectors.toList());
        }

        private static boolean isPubliclyAvailable(ResourceDescriptor rd) {
            return Objects.equals(rd.getParameter("allow-public-use"), Boolean.TRUE.toString());
        }

        private static boolean isFont(ResourceDescriptor rd) {
            return StringUtils.endsWithAny((CharSequence)rd.getLocation().toLowerCase(), (CharSequence[])new CharSequence[]{"ttf", "otf", "woff", "woff2", "eot"});
        }
    }

    private static class PluginWithNonPublicFonts {
        @JsonProperty
        private final String pluginKey;
        @JsonProperty
        private final List<WebResourceWithNonPublicFonts> resourcesWithNonPublicFonts;

        public PluginWithNonPublicFonts(String pluginKey, List<WebResourceWithNonPublicFonts> resourcesWithNonPublicFonts) {
            this.pluginKey = pluginKey;
            this.resourcesWithNonPublicFonts = resourcesWithNonPublicFonts;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.StreamingOutput
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.IOUtils;

@Path(value="/{pluginKey}/media")
@WebSudoNotRequired
public class PluginMediaResource {
    public static final String IMAGES_PLUGIN_LOGO_DEFAULT_PNG = "/images/plugin-logo-default.png";
    public static final String IMAGES_PLUGIN_ICON_DEFAULT_PNG = "/images/plugin-icon-default.png";
    public static final String IMAGES_CHARLIE64X58_GIF = "/images/Charlie64x58.gif";
    public static final String PLUGINICON_RESOURCE_PATH = "plugin-icon";
    public static final String PLUGINLOGO_RESOURCE_PATH = "plugin-logo";
    public static final String PLUGINBANNER_RESOURCE_PATH = "plugin-banner";
    public static final String VENDORICON_RESOURCE_PATH = "vendor-icon";
    public static final String VENDORLOGO_RESOURCE_PATH = "vendor-logo";
    private static final String MEDIA_TYPE_PNG = "image/png";
    private static final String MEDIA_TYPE_GIF = "image/gif";
    private final PluginRetriever pluginRetriever;
    private final PluginMetadataAccessor metadata;

    public PluginMediaResource(PluginRetriever pluginRetriever, PluginMetadataAccessor metadata) {
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
    }

    @GET
    @Path(value="plugin-icon")
    public Response getPluginIcon(@PathParam(value="pluginKey") PathSegment pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(UpmUriEscaper.unescape(pluginKey.getPath())).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return this.getStreamingOutputDecision(this.metadata.getPluginIconInputStream(plugin), this.resourceFileName(plugin, PLUGINICON_RESOURCE_PATH), IMAGES_PLUGIN_ICON_DEFAULT_PNG, MEDIA_TYPE_PNG);
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value="plugin-logo")
    public Response getPluginLogo(@PathParam(value="pluginKey") PathSegment pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(UpmUriEscaper.unescape(pluginKey.getPath())).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return this.getStreamingOutputDecision(this.metadata.getPluginLogoInputStream(plugin), this.resourceFileName(plugin, PLUGINLOGO_RESOURCE_PATH), IMAGES_PLUGIN_LOGO_DEFAULT_PNG, MEDIA_TYPE_PNG);
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value="plugin-banner")
    public Response getPluginBanner(@PathParam(value="pluginKey") PathSegment pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(UpmUriEscaper.unescape(pluginKey.getPath())).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return this.getStreamingOutputDecision(this.metadata.getPluginBannerInputStream(plugin), this.resourceFileName(plugin, PLUGINBANNER_RESOURCE_PATH), IMAGES_CHARLIE64X58_GIF, MEDIA_TYPE_GIF);
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value="vendor-icon")
    public Response getVendorIcon(@PathParam(value="pluginKey") PathSegment pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(UpmUriEscaper.unescape(pluginKey.getPath())).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return this.getStreamingOutputDecision(this.metadata.getVendorIconInputStream(plugin), this.resourceFileName(plugin, VENDORICON_RESOURCE_PATH), IMAGES_CHARLIE64X58_GIF, MEDIA_TYPE_GIF);
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value="vendor-logo")
    public Response getVendorLogo(@PathParam(value="pluginKey") PathSegment pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(UpmUriEscaper.unescape(pluginKey.getPath())).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return this.getStreamingOutputDecision(this.metadata.getVendorLogoInputStream(plugin), this.resourceFileName(plugin, VENDORLOGO_RESOURCE_PATH), IMAGES_CHARLIE64X58_GIF, MEDIA_TYPE_GIF);
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private Response getStreamingOutputDecision(Option<InputStream> optionIS, Option<String> fileName, String defaultLocation, String defaultContentType) {
        Iterator<InputStream> iterator = optionIS.iterator();
        if (iterator.hasNext()) {
            InputStream is = iterator.next();
            return Response.ok().type(this.contentTypeFromInputStreamOrFileName(is, fileName).getOrElse(defaultContentType)).entity((Object)this.getStreamingOutputForResource(is)).build();
        }
        InputStream inputStream = this.getClass().getResourceAsStream(defaultLocation);
        return Response.ok().type(defaultContentType).entity((Object)this.getStreamingOutputForResource(inputStream)).build();
    }

    private Option<String> contentTypeFromInputStreamOrFileName(InputStream is, Option<String> filename) {
        try {
            return Option.option(URLConnection.guessContentTypeFromStream(is));
        }
        catch (IOException e) {
            Iterator<String> iterator = filename.iterator();
            if (iterator.hasNext()) {
                String f = iterator.next();
                return Option.option(URLConnection.guessContentTypeFromName(f));
            }
            return Option.none();
        }
    }

    private Option<String> resourceFileName(Plugin plugin, String resourceItem) {
        return Option.option(plugin.getPluginInformation().getParameters().get(resourceItem));
    }

    private StreamingOutput getStreamingOutputForResource(final InputStream is) {
        return new StreamingOutput(){

            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    BufferedImage image = ImageIO.read(is);
                    ImageIO.write((RenderedImage)image, "png", output);
                }
                finally {
                    IOUtils.closeQuietly((InputStream)is);
                    IOUtils.closeQuietly((OutputStream)output);
                }
            }
        };
    }
}


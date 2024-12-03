/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.themes.ColorSchemeBean;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class ColoursWebResourceTransformer
implements DimensionAwareWebResourceTransformerFactory {
    private final ColourSchemeManager colourSchemeManager;

    public ColoursWebResourceTransformer(ColourSchemeManager colourSchemeManager) {
        this.colourSchemeManager = colourSchemeManager;
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty();
    }

    public DimensionAwareTransformerUrlBuilder makeUrlBuilder(TransformerParameters parameters) {
        return new DimensionAwareTransformerUrlBuilder(){

            public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
            }

            public void addToUrl(UrlBuilder urlBuilder) {
            }
        };
    }

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters parameters) {
        return (transformableResource, params) -> new DownloadableResource(){

            public boolean isResourceModified(HttpServletRequest request, HttpServletResponse response) {
                return false;
            }

            public void serveResource(HttpServletRequest request, HttpServletResponse response) throws DownloadException {
            }

            public void streamResource(OutputStream out) {
                VelocityContext context = new VelocityContext();
                ColourScheme colourScheme = ColoursWebResourceTransformer.this.colourSchemeManager.getDefaultColourScheme();
                context.put("colorScheme", (Object)new ColorSchemeBean(colourScheme));
                String transformableResourceStream = VelocityUtils.getRenderedTemplate(transformableResource.location().getLocation(), (Context)context);
                try {
                    transformableResourceStream = transformableResourceStream.replace("##.*", "").replaceAll("\\s+", " ");
                    out.write(transformableResourceStream.getBytes(Charset.forName("UTF-8")));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public String getContentType() {
                return "text/css";
            }
        };
    }
}


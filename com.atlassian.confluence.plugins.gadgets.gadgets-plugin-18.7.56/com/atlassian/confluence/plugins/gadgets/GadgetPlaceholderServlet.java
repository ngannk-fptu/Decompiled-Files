/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.Supplier
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  com.atlassian.confluence.util.http.HttpResponse
 *  com.atlassian.confluence.util.http.HttpRetrievalService
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.j3d.util.ImageGenerator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import com.atlassian.confluence.plugins.gadgets.events.GadgetPlaceholderRenderedEvent;
import com.atlassian.confluence.plugins.gadgets.requestcontext.RequestContextBuilder;
import com.atlassian.confluence.plugins.gadgets.whitelist.GadgetWhiteListManager;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.HttpRetrievalService;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.util.concurrent.Lazy;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.j3d.util.ImageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GadgetPlaceholderServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(GadgetPlaceholderServlet.class);
    private static final Color PLACEHOLDER_BACKGROUND = new Color(240, 240, 240);
    private static final Color INVALID_MACRO_BACKGROUND = new Color(255, 204, 204);
    private static final int MACRO_ICON_SIZE = 24;
    private static final String CACHE_NAME = "gadget.placeholder.cache";
    private final HttpRetrievalService httpRetrievalService;
    private final RequestContextBuilder requestContextBuilder;
    private final GadgetSpecFactory gadgetSpecFactory;
    private final GadgetWhiteListManager whiteListManager;
    private final I18nResolver resolver;
    private final EventPublisher eventPublisher;
    private final Font font;
    private final com.atlassian.util.concurrent.Supplier<Cache<String, BufferedImage>> placeholderCache;
    private final boolean isClustered;

    public GadgetPlaceholderServlet(GadgetSpecFactory gadgetSpecFactory, ClusterManager clusterManager, HttpRetrievalService httpRetrievalService, RequestContextBuilder requestContextBuilder, CacheManager cacheFactory, GadgetWhiteListManager gadgetWhiteListManager, I18nResolver resolver, EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.isClustered = clusterManager.isClustered();
        this.httpRetrievalService = httpRetrievalService;
        this.gadgetSpecFactory = gadgetSpecFactory;
        this.requestContextBuilder = requestContextBuilder;
        this.whiteListManager = gadgetWhiteListManager;
        this.resolver = resolver;
        this.font = this.createFont();
        this.placeholderCache = this.isClustered ? null : Lazy.supplier(() -> cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().local().build()));
    }

    private Font createFont() {
        Font fon = null;
        String fontPath = "fonts/verdana.ttf";
        InputStream fontStream = ClassLoaderUtils.getResourceAsStream((String)fontPath, ((Object)((Object)this)).getClass());
        if (fontStream != null) {
            try {
                fon = Font.createFont(0, fontStream).deriveFont(12.0f);
            }
            catch (FontFormatException ex) {
                log.info("Attempted to load verdana but it was not a true-type font", (Throwable)ex);
            }
            catch (IOException ex) {
                log.info("Exception while trying to load the font file: " + fontPath, (Throwable)ex);
            }
        }
        if (fon == null) {
            BufferedImage bf = new BufferedImage(1, 1, 2);
            Graphics2D g2d = bf.createGraphics();
            fon = g2d.getFont().deriveFont(12.0f);
            g2d.dispose();
        }
        return fon;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            BufferedImage image;
            String gadgetUrl = req.getParameter("gadgetId");
            if (gadgetUrl == null) {
                resp.sendError(400, "Request missing gadgetId");
                return;
            }
            URI uri = new URI(new String(Base64.decodeBase64((String)gadgetUrl), "UTF-8"));
            if (this.whiteListManager.isAllowedGadgetUri(uri)) {
                this.eventPublisher.publish((Object)new GadgetPlaceholderRenderedEvent(uri));
                GadgetRequestContext requestContext = this.requestContextBuilder.buildRequestContext(false);
                GadgetSpec spec = this.gadgetSpecFactory.getGadgetSpec(uri, requestContext);
                Supplier imgSupplier = () -> this.getPlaceholderImage(spec.getTitle(), this.getIconStream(spec), PLACEHOLDER_BACKGROUND);
                image = this.isClustered ? (BufferedImage)imgSupplier.get() : (BufferedImage)((Cache)this.placeholderCache.get()).get((Object)gadgetUrl, imgSupplier);
            } else {
                image = this.getPlaceholderImage(this.resolver.getText("gadgets.not.allowed.uri", new Serializable[]{uri.toString()}), null, INVALID_MACRO_BACKGROUND);
            }
            ImageRenderUtils.writePngToStream((BufferedImage)image, (HttpServletResponse)resp);
        }
        catch (URISyntaxException e) {
            log.warn("Invalid gadget url");
            throw new RuntimeException(e);
        }
    }

    private InputStream getIconStream(GadgetSpec gadgetSpec) {
        InputStream result = null;
        if (gadgetSpec != null) {
            if (gadgetSpec.getThumbnail() != null) {
                try {
                    HttpResponse httpResponse = this.httpRetrievalService.get(gadgetSpec.getThumbnail().toString());
                    if (!httpResponse.isFailed()) {
                        result = httpResponse.getResponse();
                    }
                }
                catch (IOException ex) {
                    log.debug("Exception retrieving custom macro icon", (Throwable)ex);
                }
                if (result == null) {
                    log.info("The custom icon for macro {} could not be retrieved from {}.", (Object)gadgetSpec.getTitle(), (Object)gadgetSpec.getUrl());
                }
            }
            if (result == null) {
                result = this.getServletContext().getResourceAsStream("/images/icons/macrobrowser/dropdown/" + gadgetSpec.getTitle() + ".png");
            }
        }
        if (result == null) {
            try {
                result = new FileInputStream(this.getServletContext().getRealPath("/images/icons/macrobrowser/macro-placeholder-default.png"));
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public BufferedImage getPlaceholderImage(String styledString, InputStream iconStream, Color backgroundColor) {
        float macroTitleTextOffsetX;
        FontMetrics metrics = this.getFontMetrics(this.font);
        int textHeight = metrics.getMaxAscent() + metrics.getMaxDescent();
        int textWidth = 0;
        int horizontalPadding = 10;
        int verticalPadding = 10;
        int iconToTextSpacing = 5;
        int placeholderImageWidth = (textWidth += metrics.stringWidth(styledString)) + horizontalPadding + 24 + iconToTextSpacing;
        int placeholderImageHeight = textHeight + verticalPadding;
        BufferedImage bufferedImage = new BufferedImage(placeholderImageWidth, placeholderImageHeight, 2);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, placeholderImageWidth, placeholderImageHeight);
        if (iconStream != null) {
            Image macroIconImage = this.getIcon(iconStream, 24);
            int macroIconImageWidth = macroIconImage.getWidth(null);
            int macroIconImageHeight = macroIconImage.getHeight(null);
            int macroIconOffsetX = (placeholderImageHeight - macroIconImageWidth) / 2;
            int macroIconOffsetY = (placeholderImageHeight - macroIconImageHeight) / 2;
            graphics.drawImage(macroIconImage, macroIconOffsetX, macroIconOffsetY, macroIconImageWidth, macroIconImageHeight, null);
        }
        graphics.setFont(this.font);
        float fontMidlineToBaselineOffset = (float)metrics.getMaxAscent() - (float)textHeight / 2.0f;
        float baselineOffset = (float)(verticalPadding + textHeight) / 2.0f + fontMidlineToBaselineOffset;
        float currentOffsetX = macroTitleTextOffsetX = (float)horizontalPadding / 2.0f + 24.0f;
        graphics.setColor(Color.BLACK);
        graphics.drawString(styledString, currentOffsetX, baselineOffset);
        graphics.dispose();
        return bufferedImage;
    }

    private Image getIcon(InputStream inputStream, int iconSize) {
        BufferedImage result;
        try {
            result = ImageIO.read(inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)inputStream);
        }
        if (((Image)result).getWidth(null) > iconSize || ((Image)result).getHeight(null) > iconSize) {
            AreaAveragingScaleFilter scaleFilter = new AreaAveragingScaleFilter(iconSize, iconSize);
            FilteredImageSource filteredImage = new FilteredImageSource(((Image)result).getSource(), scaleFilter);
            ImageGenerator generator = new ImageGenerator();
            filteredImage.startProduction((ImageConsumer)generator);
            result = generator.getImage();
            result.flush();
        }
        return result;
    }

    private FontMetrics getFontMetrics(Font font) {
        BufferedImage bufferedImage = new BufferedImage(1, 1, 2);
        Graphics2D graphics = bufferedImage.createGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        graphics.dispose();
        return fontMetrics;
    }
}


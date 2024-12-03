/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.plugins.roadmap.RoadmapMacroCacheSupplier;
import com.atlassian.plugins.roadmap.RoadmapRenderer;
import com.atlassian.plugins.roadmap.RoadmapRequest;
import com.atlassian.plugins.roadmap.TimelinePlannerJsonBuilder;
import com.atlassian.plugins.roadmap.TimelinePlannerMacroManager;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.PNGRoadMapRenderer;
import com.atlassian.sal.api.message.I18nResolver;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageServlet
extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(ImageServlet.class);
    private TimelinePlannerMacroManager timelinePlannerMacroManager;
    private final I18nResolver i18n;
    private final RoadmapMacroCacheSupplier cacheSupplier;

    public ImageServlet(I18nResolver i18nResolver, RoadmapMacroCacheSupplier cacheSupplier, TimelinePlannerMacroManager timelinePlannerMacroManager) {
        this.i18n = i18nResolver;
        this.timelinePlannerMacroManager = timelinePlannerMacroManager;
        this.cacheSupplier = cacheSupplier;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoadmapRequest roadmapRequest = new RoadmapRequest(req);
        if (!this.isValid(roadmapRequest)) {
            this.logger.error("Roadmap ImageServlet - roadmap's request is invalid");
            this.responseDefaultRoadmap(resp);
            return;
        }
        String imageCacheKey = String.format("%s_%s_%s", roadmapRequest.getHash(), roadmapRequest.getWidth(), roadmapRequest.getHeight());
        Optional<byte[]> roadmapImage = Optional.ofNullable((byte[])this.cacheSupplier.getImageCache().get((Object)imageCacheKey));
        if (!roadmapImage.isPresent()) {
            roadmapImage = this.renderRoadmap(roadmapRequest);
            roadmapImage.ifPresent(image -> this.cacheSupplier.getImageCache().put((Object)imageCacheKey, image));
        }
        if (roadmapImage.isPresent()) {
            resp.setContentType("image/png");
            resp.getOutputStream().write(roadmapImage.get());
        } else {
            this.responseDefaultRoadmap(resp);
        }
    }

    private Optional<byte[]> renderRoadmap(RoadmapRequest roadmapRequest) throws IOException {
        boolean isTimeline;
        Optional<String> source;
        if (roadmapRequest.getId() > 0L && roadmapRequest.getVersion() > 0) {
            try {
                MacroDefinition roadmapMacro = this.timelinePlannerMacroManager.findRoadmapMacroDefinition(roadmapRequest.getId(), roadmapRequest.getVersion(), roadmapRequest.getHash());
                if (roadmapMacro == null) {
                    source = Optional.empty();
                    isTimeline = false;
                }
                source = Optional.ofNullable(roadmapMacro.getParameter("source"));
                isTimeline = BooleanUtils.toBoolean((String)roadmapMacro.getParameter("timeline"));
            }
            catch (XhtmlException e) {
                this.logger.error("Roadmap source error: ", (Throwable)e);
                throw new IOException(e);
            }
        } else {
            source = Optional.ofNullable((String)this.cacheSupplier.getMarcoSourceCache().get((Object)roadmapRequest.getHash()));
            isTimeline = roadmapRequest.isTimeline();
        }
        if (source.isPresent()) {
            BufferedImage bufferedImage;
            Optional<Integer> widthOption = Optional.empty();
            Optional<Integer> heightOption = Optional.empty();
            if (roadmapRequest.getWidth() > 0 && roadmapRequest.getHeight() > 0) {
                widthOption = Optional.of(roadmapRequest.getWidth());
                heightOption = Optional.of(roadmapRequest.getHeight());
            }
            if (isTimeline) {
                TimelinePlanner roadmap = TimelinePlannerJsonBuilder.fromJson(source.get());
                PNGRoadMapRenderer pngRoadMapRenderer = new PNGRoadMapRenderer();
                pngRoadMapRenderer.setI18n(this.i18n);
                bufferedImage = pngRoadMapRenderer.renderAsImage(roadmap, widthOption, heightOption, roadmapRequest.isPlaceholder());
            } else {
                bufferedImage = RoadmapRenderer.drawImage(URLDecoder.decode(source.get(), "UTF-8"), widthOption, heightOption, roadmapRequest.isPlaceholder(), this.i18n);
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)bufferedImage, "png", buf);
            return Optional.of(buf.toByteArray());
        }
        return Optional.empty();
    }

    private void responseDefaultRoadmap(HttpServletResponse resp) throws IOException {
        InputStream inputStream = ((Object)((Object)this)).getClass().getClassLoader().getResourceAsStream("images/roadmap.png");
        resp.setContentType("image/png");
        IOUtils.copy((InputStream)inputStream, (OutputStream)resp.getOutputStream());
        inputStream.close();
    }

    private boolean isValid(RoadmapRequest roadmapRequest) {
        return StringUtils.isNotBlank((CharSequence)roadmapRequest.getHash());
    }
}


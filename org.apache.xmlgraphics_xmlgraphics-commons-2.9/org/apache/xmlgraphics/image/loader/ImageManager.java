/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageProcessingHints;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.cache.ImageCache;
import org.apache.xmlgraphics.image.loader.pipeline.ImageProviderPipeline;
import org.apache.xmlgraphics.image.loader.pipeline.PipelineFactory;
import org.apache.xmlgraphics.image.loader.spi.ImageImplRegistry;
import org.apache.xmlgraphics.image.loader.spi.ImagePreloader;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.image.loader.util.Penalty;
import org.apache.xmlgraphics.io.XmlSourceUtil;

public class ImageManager {
    protected static final Log log = LogFactory.getLog(ImageManager.class);
    private ImageImplRegistry registry;
    private ImageContext imageContext;
    private ImageCache cache = new ImageCache();
    private PipelineFactory pipelineFactory = new PipelineFactory(this);

    public ImageManager(ImageContext context) {
        this(ImageImplRegistry.getDefaultInstance(), context);
    }

    public ImageManager(ImageImplRegistry registry, ImageContext context) {
        this.registry = registry;
        this.imageContext = context;
    }

    public ImageImplRegistry getRegistry() {
        return this.registry;
    }

    public ImageContext getImageContext() {
        return this.imageContext;
    }

    public ImageCache getCache() {
        return this.cache;
    }

    public PipelineFactory getPipelineFactory() {
        return this.pipelineFactory;
    }

    public ImageInfo getImageInfo(String uri, ImageSessionContext session) throws ImageException, IOException {
        if (this.getCache() != null) {
            return this.getCache().needImageInfo(uri, session, this);
        }
        return this.preloadImage(uri, session);
    }

    public ImageInfo preloadImage(String uri, ImageSessionContext session) throws ImageException, IOException {
        Source src = session.needSource(uri);
        ImageInfo info = this.preloadImage(uri, src);
        session.returnSource(uri, src);
        return info;
    }

    public ImageInfo preloadImage(String uri, Source src) throws ImageException, IOException {
        Iterator iter = this.registry.getPreloaderIterator();
        while (iter.hasNext()) {
            ImagePreloader preloader = (ImagePreloader)iter.next();
            ImageInfo info = preloader.preloadImage(uri, src, this.imageContext);
            if (info == null) continue;
            return info;
        }
        throw new ImageException("The file format is not supported. No ImagePreloader found for " + uri);
    }

    private Map prepareHints(Map hints, ImageSessionContext sessionContext) {
        HashMap<Object, Object> newHints = new HashMap<Object, Object>();
        if (hints != null) {
            newHints.putAll(hints);
        }
        if (!newHints.containsKey(ImageProcessingHints.IMAGE_SESSION_CONTEXT) && sessionContext != null) {
            newHints.put(ImageProcessingHints.IMAGE_SESSION_CONTEXT, sessionContext);
        }
        if (!newHints.containsKey(ImageProcessingHints.IMAGE_MANAGER)) {
            newHints.put(ImageProcessingHints.IMAGE_MANAGER, this);
        }
        return newHints;
    }

    public Image getImage(ImageInfo info, ImageFlavor flavor, Map hints, ImageSessionContext session) throws ImageException, IOException {
        hints = this.prepareHints(hints, session);
        Image img = null;
        ImageProviderPipeline pipeline = this.getPipelineFactory().newImageConverterPipeline(info, flavor);
        if (pipeline != null) {
            img = pipeline.execute(info, hints, session);
        }
        if (img == null) {
            throw new ImageException("Cannot load image (no suitable loader/converter combination available) for " + info);
        }
        XmlSourceUtil.closeQuietly(session.getSource(info.getOriginalURI()));
        return img;
    }

    public Image getImage(ImageInfo info, ImageFlavor[] flavors, Map hints, ImageSessionContext session) throws ImageException, IOException {
        hints = this.prepareHints(hints, session);
        Image img = null;
        ImageProviderPipeline[] candidates = this.getPipelineFactory().determineCandidatePipelines(info, flavors);
        ImageProviderPipeline pipeline = this.choosePipeline(candidates);
        if (pipeline != null) {
            img = pipeline.execute(info, hints, session);
        }
        if (img == null) {
            throw new ImageException("Cannot load image (no suitable loader/converter combination available) for " + info);
        }
        XmlSourceUtil.closeQuietly(session.getSource(info.getOriginalURI()));
        return img;
    }

    public Image getImage(ImageInfo info, ImageFlavor flavor, ImageSessionContext session) throws ImageException, IOException {
        return this.getImage(info, flavor, ImageUtil.getDefaultHints(session), session);
    }

    public Image getImage(ImageInfo info, ImageFlavor[] flavors, ImageSessionContext session) throws ImageException, IOException {
        return this.getImage(info, flavors, ImageUtil.getDefaultHints(session), session);
    }

    public void closeImage(String uri, ImageSessionContext session) {
        XmlSourceUtil.closeQuietly(session.getSource(uri));
    }

    public Image convertImage(Image image, ImageFlavor[] flavors, Map hints) throws ImageException, IOException {
        hints = this.prepareHints(hints, null);
        ImageInfo info = image.getInfo();
        Image img = null;
        for (ImageFlavor flavor : flavors) {
            if (!image.getFlavor().equals(flavor)) continue;
            return image;
        }
        ImageProviderPipeline[] candidates = this.getPipelineFactory().determineCandidatePipelines(image, flavors);
        ImageProviderPipeline pipeline = this.choosePipeline(candidates);
        if (pipeline != null) {
            img = pipeline.execute(info, image, hints, null);
        }
        if (img == null) {
            throw new ImageException("Cannot convert image " + image + " (no suitable converter combination available)");
        }
        return img;
    }

    public Image convertImage(Image image, ImageFlavor[] flavors) throws ImageException, IOException {
        return this.convertImage(image, flavors, null);
    }

    public ImageProviderPipeline choosePipeline(ImageProviderPipeline[] candidates) {
        int i;
        ImageProviderPipeline pipeline = null;
        int minPenalty = Integer.MAX_VALUE;
        int count = candidates.length;
        if (log.isTraceEnabled()) {
            log.trace((Object)"Candidate Pipelines:");
            for (i = 0; i < count; ++i) {
                if (candidates[i] == null) continue;
                log.trace((Object)("  " + i + ": " + candidates[i].getConversionPenalty(this.getRegistry()) + " for " + candidates[i]));
            }
        }
        for (i = count - 1; i >= 0; --i) {
            Penalty penalty;
            if (candidates[i] == null || (penalty = candidates[i].getConversionPenalty(this.getRegistry())).isInfinitePenalty() || penalty.getValue() > minPenalty) continue;
            pipeline = candidates[i];
            minPenalty = penalty.getValue();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Chosen pipeline: " + pipeline));
        }
        return pipeline;
    }
}


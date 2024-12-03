/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageManager;
import org.apache.xmlgraphics.image.loader.impl.CompositeImageLoader;
import org.apache.xmlgraphics.image.loader.pipeline.ImageConversionEdge;
import org.apache.xmlgraphics.image.loader.pipeline.ImageProviderPipeline;
import org.apache.xmlgraphics.image.loader.pipeline.ImageRepresentation;
import org.apache.xmlgraphics.image.loader.spi.ImageConverter;
import org.apache.xmlgraphics.image.loader.spi.ImageImplRegistry;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;
import org.apache.xmlgraphics.image.loader.spi.ImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.util.Penalty;
import org.apache.xmlgraphics.util.dijkstra.DefaultEdgeDirectory;
import org.apache.xmlgraphics.util.dijkstra.DijkstraAlgorithm;
import org.apache.xmlgraphics.util.dijkstra.Vertex;

public class PipelineFactory {
    protected static final Log log = LogFactory.getLog(PipelineFactory.class);
    private ImageManager manager;
    private int converterEdgeDirectoryVersion = -1;
    private DefaultEdgeDirectory converterEdgeDirectory;

    public PipelineFactory(ImageManager manager) {
        this.manager = manager;
    }

    private DefaultEdgeDirectory getEdgeDirectory() {
        ImageImplRegistry registry = this.manager.getRegistry();
        if (registry.getImageConverterModifications() != this.converterEdgeDirectoryVersion) {
            Collection converters = registry.getImageConverters();
            DefaultEdgeDirectory dir = new DefaultEdgeDirectory();
            for (Object converter1 : converters) {
                ImageConverter converter = (ImageConverter)converter1;
                Penalty penalty = Penalty.toPenalty(converter.getConversionPenalty());
                penalty = penalty.add(registry.getAdditionalPenalty(converter.getClass().getName()));
                dir.addEdge(new ImageConversionEdge(converter, penalty));
            }
            this.converterEdgeDirectoryVersion = registry.getImageConverterModifications();
            this.converterEdgeDirectory = dir;
        }
        return this.converterEdgeDirectory;
    }

    public ImageProviderPipeline newImageConverterPipeline(Image originalImage, ImageFlavor targetFlavor) {
        DefaultEdgeDirectory dir = this.getEdgeDirectory();
        ImageRepresentation destination = new ImageRepresentation(targetFlavor);
        ImageProviderPipeline pipeline = this.findPipeline(dir, originalImage.getFlavor(), destination);
        return pipeline;
    }

    public ImageProviderPipeline newImageConverterPipeline(ImageInfo imageInfo, ImageFlavor targetFlavor) {
        ImageProviderPipeline[] candidates = this.determineCandidatePipelines(imageInfo, targetFlavor);
        if (candidates.length > 0) {
            Arrays.sort(candidates, new PipelineComparator());
            ImageProviderPipeline pipeline = candidates[0];
            if (pipeline != null && log.isDebugEnabled()) {
                log.debug((Object)("Pipeline: " + pipeline + " with penalty " + pipeline.getConversionPenalty()));
            }
            return pipeline;
        }
        return null;
    }

    public ImageProviderPipeline[] determineCandidatePipelines(ImageInfo imageInfo, ImageFlavor targetFlavor) {
        String originalMime = imageInfo.getMimeType();
        ImageImplRegistry registry = this.manager.getRegistry();
        ArrayList<ImageProviderPipeline> candidates = new ArrayList<ImageProviderPipeline>();
        DefaultEdgeDirectory dir = this.getEdgeDirectory();
        ImageLoaderFactory[] loaderFactories = registry.getImageLoaderFactories(imageInfo, targetFlavor);
        if (loaderFactories != null) {
            ImageLoader loader;
            if (loaderFactories.length == 1) {
                loader = loaderFactories[0].newImageLoader(targetFlavor);
            } else {
                int count = loaderFactories.length;
                ImageLoader[] loaders = new ImageLoader[count];
                for (int i = 0; i < count; ++i) {
                    loaders[i] = loaderFactories[i].newImageLoader(targetFlavor);
                }
                loader = new CompositeImageLoader(loaders);
            }
            ImageProviderPipeline pipeline = new ImageProviderPipeline(this.manager.getCache(), loader);
            candidates.add(pipeline);
        } else {
            if (log.isTraceEnabled()) {
                log.trace((Object)("No ImageLoaderFactory found that can load this format (" + targetFlavor + ") directly. Trying ImageConverters instead..."));
            }
            ImageRepresentation destination = new ImageRepresentation(targetFlavor);
            loaderFactories = registry.getImageLoaderFactories(originalMime);
            if (loaderFactories != null) {
                for (ImageLoaderFactory loaderFactory : loaderFactories) {
                    ImageFlavor[] flavors;
                    for (ImageFlavor flavor : flavors = loaderFactory.getSupportedFlavors(originalMime)) {
                        ImageProviderPipeline pipeline = this.findPipeline(dir, flavor, destination);
                        if (pipeline == null) continue;
                        ImageLoader loader = loaderFactory.newImageLoader(flavor);
                        pipeline.setImageLoader(loader);
                        candidates.add(pipeline);
                    }
                }
            }
        }
        return candidates.toArray(new ImageProviderPipeline[candidates.size()]);
    }

    private ImageProviderPipeline findPipeline(DefaultEdgeDirectory dir, ImageFlavor originFlavor, ImageRepresentation destination) {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(dir);
        ImageRepresentation origin = new ImageRepresentation(originFlavor);
        dijkstra.execute(origin, destination);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Lowest penalty: " + dijkstra.getLowestPenalty(destination)));
        }
        Vertex prev = destination;
        Vertex pred = dijkstra.getPredecessor(destination);
        if (pred == null) {
            if (log.isTraceEnabled()) {
                log.trace((Object)"No route found!");
            }
            return null;
        }
        LinkedList<ImageConversionEdge> stops = new LinkedList<ImageConversionEdge>();
        while ((pred = dijkstra.getPredecessor(prev)) != null) {
            ImageConversionEdge edge = (ImageConversionEdge)dir.getBestEdge(pred, prev);
            stops.addFirst(edge);
            prev = pred;
        }
        ImageProviderPipeline pipeline = new ImageProviderPipeline(this.manager.getCache(), null);
        for (Object e : stops) {
            ImageConversionEdge edge = (ImageConversionEdge)e;
            pipeline.addConverter(edge.getImageConverter());
        }
        return pipeline;
    }

    public ImageProviderPipeline[] determineCandidatePipelines(ImageInfo imageInfo, ImageFlavor[] flavors) {
        ArrayList<ImageProviderPipeline> candidates = new ArrayList<ImageProviderPipeline>();
        for (ImageFlavor flavor : flavors) {
            Penalty p;
            ImageProviderPipeline pipeline = this.newImageConverterPipeline(imageInfo, flavor);
            if (pipeline == null || (p = pipeline.getConversionPenalty(this.manager.getRegistry())).isInfinitePenalty()) continue;
            candidates.add(pipeline);
        }
        return candidates.toArray(new ImageProviderPipeline[candidates.size()]);
    }

    public ImageProviderPipeline[] determineCandidatePipelines(Image sourceImage, ImageFlavor[] flavors) {
        ArrayList<ImageProviderPipeline> candidates = new ArrayList<ImageProviderPipeline>();
        for (ImageFlavor flavor : flavors) {
            ImageProviderPipeline pipeline = this.newImageConverterPipeline(sourceImage, flavor);
            if (pipeline == null) continue;
            candidates.add(pipeline);
        }
        return candidates.toArray(new ImageProviderPipeline[candidates.size()]);
    }

    private static class PipelineComparator
    implements Comparator,
    Serializable {
        private static final long serialVersionUID = 1161513617996198090L;

        private PipelineComparator() {
        }

        public int compare(Object o1, Object o2) {
            ImageProviderPipeline p1 = (ImageProviderPipeline)o1;
            ImageProviderPipeline p2 = (ImageProviderPipeline)o2;
            return p1.getConversionPenalty() - p2.getConversionPenalty();
        }
    }
}


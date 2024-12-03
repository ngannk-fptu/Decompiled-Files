/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.renderer.RenderRequest;
import org.apache.tika.renderer.RenderResults;
import org.apache.tika.renderer.Renderer;
import org.apache.tika.utils.ServiceLoaderUtils;

public class CompositeRenderer
implements Renderer,
Initializable {
    private Map<MediaType, Renderer> rendererMap = new HashMap<MediaType, Renderer>();

    public CompositeRenderer(ServiceLoader serviceLoader) {
        this(CompositeRenderer.getDefaultRenderers(serviceLoader));
    }

    public CompositeRenderer(List<Renderer> renderers) {
        ConcurrentHashMap<MediaType, Renderer> tmp = new ConcurrentHashMap<MediaType, Renderer>();
        ParseContext empty = new ParseContext();
        for (Renderer renderer : renderers) {
            for (MediaType mt : renderer.getSupportedTypes(empty)) {
                tmp.put(mt, renderer);
            }
        }
        this.rendererMap = Collections.unmodifiableMap(tmp);
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.rendererMap.keySet();
    }

    @Override
    public RenderResults render(InputStream is, Metadata metadata, ParseContext parseContext, RenderRequest ... requests) throws IOException, TikaException {
        String mediaTypeString = metadata.get(TikaCoreProperties.TYPE);
        if (mediaTypeString == null) {
            throw new TikaException("need to specify file type in metadata");
        }
        MediaType mt = MediaType.parse(mediaTypeString);
        if (mt == null) {
            throw new TikaException("can't parse mediaType: " + mediaTypeString);
        }
        Renderer renderer = this.rendererMap.get(mt);
        if (renderer == null) {
            throw new TikaException("I regret I can't find a renderer for " + mt);
        }
        return renderer.render(is, metadata, parseContext, requests);
    }

    public Renderer getLeafRenderer(MediaType mt) {
        return this.rendererMap.get(mt);
    }

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
    }

    private static List<Renderer> getDefaultRenderers(ServiceLoader loader) {
        List<Renderer> staticRenderers = loader.loadStaticServiceProviders(Renderer.class);
        ServiceLoaderUtils.sortLoadedClasses(staticRenderers);
        return staticRenderers;
    }
}


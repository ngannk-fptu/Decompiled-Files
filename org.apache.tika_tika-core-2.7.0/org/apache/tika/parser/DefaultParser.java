/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.AbstractEncodingDetectorParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.RenderingParser;
import org.apache.tika.renderer.CompositeRenderer;
import org.apache.tika.renderer.Renderer;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultParser
extends CompositeParser {
    private static final long serialVersionUID = 3612324825403757520L;
    private final transient ServiceLoader loader;

    public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader, Collection<Class<? extends Parser>> excludeParsers, EncodingDetector encodingDetector, Renderer renderer) {
        super(registry, DefaultParser.getDefaultParsers(loader, encodingDetector, renderer, excludeParsers));
        this.loader = loader;
    }

    public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader, Collection<Class<? extends Parser>> excludeParsers) {
        super(registry, DefaultParser.getDefaultParsers(loader, new DefaultEncodingDetector(loader), new CompositeRenderer(loader), excludeParsers));
        this.loader = loader;
    }

    public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader, EncodingDetector encodingDetector, Renderer renderer) {
        this(registry, loader, Collections.EMPTY_SET, encodingDetector, renderer);
    }

    public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader) {
        this(registry, loader, Collections.EMPTY_SET, new DefaultEncodingDetector(loader), new CompositeRenderer(loader));
    }

    public DefaultParser(MediaTypeRegistry registry, ClassLoader loader) {
        this(registry, new ServiceLoader(loader));
    }

    public DefaultParser(ClassLoader loader) {
        this(MediaTypeRegistry.getDefaultRegistry(), new ServiceLoader(loader));
    }

    public DefaultParser(MediaTypeRegistry registry) {
        this(registry, new ServiceLoader());
    }

    public DefaultParser() {
        this(MediaTypeRegistry.getDefaultRegistry());
    }

    private static List<Parser> getDefaultParsers(ServiceLoader loader, EncodingDetector encodingDetector, Renderer renderer, Collection<Class<? extends Parser>> excludeParsers) {
        List parsers = loader.loadStaticServiceProviders(Parser.class, excludeParsers);
        if (encodingDetector != null) {
            for (Parser p : parsers) {
                DefaultParser.setEncodingDetector(p, encodingDetector);
            }
        }
        if (renderer != null) {
            for (Parser p : parsers) {
                DefaultParser.setRenderer(p, renderer);
            }
        }
        ServiceLoaderUtils.sortLoadedClasses(parsers);
        Collections.reverse(parsers);
        return parsers;
    }

    private static void setEncodingDetector(Parser p, EncodingDetector encodingDetector) {
        if (p instanceof AbstractEncodingDetectorParser) {
            ((AbstractEncodingDetectorParser)p).setEncodingDetector(encodingDetector);
        } else if (p instanceof CompositeParser) {
            for (Parser child : ((CompositeParser)p).getAllComponentParsers()) {
                DefaultParser.setEncodingDetector(child, encodingDetector);
            }
        } else if (p instanceof ParserDecorator) {
            DefaultParser.setEncodingDetector(((ParserDecorator)p).getWrappedParser(), encodingDetector);
        }
    }

    private static void setRenderer(Parser p, Renderer renderer) {
        if (p instanceof RenderingParser) {
            ((RenderingParser)((Object)p)).setRenderer(renderer);
        } else if (p instanceof CompositeParser) {
            for (Parser child : ((CompositeParser)p).getAllComponentParsers()) {
                DefaultParser.setRenderer(child, renderer);
            }
        } else if (p instanceof ParserDecorator) {
            DefaultParser.setRenderer(((ParserDecorator)p).getWrappedParser(), renderer);
        }
    }

    @Override
    public Map<MediaType, Parser> getParsers(ParseContext context) {
        Map<MediaType, Parser> map = super.getParsers(context);
        if (this.loader != null) {
            MediaTypeRegistry registry = this.getMediaTypeRegistry();
            List<Parser> parsers = this.loader.loadDynamicServiceProviders(Parser.class);
            Collections.reverse(parsers);
            for (Parser parser : parsers) {
                for (MediaType type : parser.getSupportedTypes(context)) {
                    map.put(registry.normalize(type), parser);
                }
            }
        }
        return map;
    }

    @Override
    public List<Parser> getAllComponentParsers() {
        List<Parser> parsers = super.getAllComponentParsers();
        if (this.loader != null) {
            parsers = new ArrayList<Parser>(parsers);
            parsers.addAll(this.loader.loadDynamicServiceProviders(Parser.class));
        }
        return parsers;
    }
}


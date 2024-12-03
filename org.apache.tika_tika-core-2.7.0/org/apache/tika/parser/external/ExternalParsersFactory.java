/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.external;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.external.ExternalParser;
import org.apache.tika.parser.external.ExternalParsersConfigReader;

public class ExternalParsersFactory {
    public static List<ExternalParser> create() throws IOException, TikaException {
        return ExternalParsersFactory.create(new ServiceLoader());
    }

    public static List<ExternalParser> create(ServiceLoader loader) throws IOException, TikaException {
        return ExternalParsersFactory.create("tika-external-parsers.xml", loader);
    }

    public static List<ExternalParser> create(String filename, ServiceLoader loader) throws IOException, TikaException {
        String filepath = ExternalParsersFactory.class.getPackage().getName().replace('.', '/') + "/" + filename;
        Enumeration<URL> files = loader.findServiceResources(filepath);
        ArrayList<URL> list = Collections.list(files);
        URL[] urls = list.toArray(new URL[0]);
        return ExternalParsersFactory.create(urls);
    }

    public static List<ExternalParser> create(URL ... urls) throws IOException, TikaException {
        ArrayList<ExternalParser> parsers = new ArrayList<ExternalParser>();
        for (URL url : urls) {
            try (InputStream stream = url.openStream();){
                parsers.addAll(ExternalParsersConfigReader.read(stream));
            }
        }
        return parsers;
    }

    public static void attachExternalParsers(TikaConfig config) throws IOException, TikaException {
        ExternalParsersFactory.attachExternalParsers(ExternalParsersFactory.create(), config);
    }

    public static void attachExternalParsers(List<ExternalParser> parsers, TikaConfig config) {
        Parser parser = config.getParser();
        if (parser instanceof CompositeParser) {
            CompositeParser cParser = (CompositeParser)parser;
            Map<MediaType, Parser> map = cParser.getParsers();
        }
    }
}


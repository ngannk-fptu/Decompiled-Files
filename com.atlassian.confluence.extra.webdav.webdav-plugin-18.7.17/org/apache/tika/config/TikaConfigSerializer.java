/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.tika.config.LoadErrorHandler;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.CompositeEncodingDetector;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.language.translate.DefaultTranslator;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.multiple.AbstractMultipleParser;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TikaConfigSerializer {
    public static void serialize(TikaConfig config, Mode mode, Writer writer, Charset charset) throws Exception {
        DocumentBuilder docBuilder = XMLReaderUtils.getDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("properties");
        doc.appendChild(rootElement);
        TikaConfigSerializer.addMimeComment(mode, rootElement, doc);
        TikaConfigSerializer.addServiceLoader(mode, rootElement, doc, config);
        TikaConfigSerializer.addExecutorService(mode, rootElement, doc, config);
        TikaConfigSerializer.addEncodingDetectors(mode, rootElement, doc, config);
        TikaConfigSerializer.addTranslator(mode, rootElement, doc, config);
        TikaConfigSerializer.addDetectors(mode, rootElement, doc, config);
        TikaConfigSerializer.addParsers(mode, rootElement, doc, config);
        Transformer transformer = XMLReaderUtils.getTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty("encoding", charset.name());
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
    }

    private static void addExecutorService(Mode mode, Element rootElement, Document doc, TikaConfig config) {
        ExecutorService executor = config.getExecutorService();
    }

    private static void addServiceLoader(Mode mode, Element rootElement, Document doc, TikaConfig config) {
        ServiceLoader loader = config.getServiceLoader();
        if (mode == Mode.MINIMAL && loader.isDynamic() && loader.getLoadErrorHandler() == LoadErrorHandler.IGNORE) {
            return;
        }
        Element dslEl = doc.createElement("service-loader");
        dslEl.setAttribute("dynamic", Boolean.toString(loader.isDynamic()));
        dslEl.setAttribute("loadErrorHandler", loader.getLoadErrorHandler().toString());
        rootElement.appendChild(dslEl);
    }

    private static void addTranslator(Mode mode, Element rootElement, Document doc, TikaConfig config) {
        Translator translator = config.getTranslator();
        if (mode == Mode.MINIMAL && translator instanceof DefaultTranslator) {
            Comment mimeComment = doc.createComment("for example: <translator class=\"org.apache.tika.language.translate.GoogleTranslator\"/>");
            rootElement.appendChild(mimeComment);
        } else {
            if (translator instanceof DefaultTranslator && (mode == Mode.STATIC || mode == Mode.STATIC_FULL)) {
                translator = ((DefaultTranslator)translator).getTranslator();
            }
            if (translator != null) {
                Element translatorElement = doc.createElement("translator");
                translatorElement.setAttribute("class", translator.getClass().getCanonicalName());
                rootElement.appendChild(translatorElement);
            } else {
                rootElement.appendChild(doc.createComment("No translators available"));
            }
        }
    }

    private static void addMimeComment(Mode mode, Element rootElement, Document doc) {
        Comment mimeComment = doc.createComment("for example: <mimeTypeRepository resource=\"/org/apache/tika/mime/tika-mimetypes.xml\"/>");
        rootElement.appendChild(mimeComment);
    }

    private static void addEncodingDetectors(Mode mode, Element rootElement, Document doc, TikaConfig config) throws Exception {
        EncodingDetector encDetector = config.getEncodingDetector();
        if (mode == Mode.MINIMAL && encDetector instanceof DefaultEncodingDetector) {
            Comment detComment = doc.createComment("for example: <encodingDetectors><encodingDetector class=\"org.apache.tika.detect.DefaultEncodingDetector\"></encodingDetectors>");
            rootElement.appendChild(detComment);
            return;
        }
        Element encDetectorsElement = doc.createElement("encodingDetectors");
        if (mode == Mode.CURRENT && encDetector instanceof DefaultEncodingDetector || !(encDetector instanceof CompositeEncodingDetector)) {
            Element encDetectorElement = doc.createElement("encodingDetector");
            encDetectorElement.setAttribute("class", encDetector.getClass().getCanonicalName());
            encDetectorsElement.appendChild(encDetectorElement);
        } else {
            List<EncodingDetector> children = ((CompositeEncodingDetector)encDetector).getDetectors();
            for (EncodingDetector d : children) {
                Element encDetectorElement = doc.createElement("encodingDetector");
                encDetectorElement.setAttribute("class", d.getClass().getCanonicalName());
                encDetectorsElement.appendChild(encDetectorElement);
            }
        }
        rootElement.appendChild(encDetectorsElement);
    }

    private static void addDetectors(Mode mode, Element rootElement, Document doc, TikaConfig config) throws Exception {
        Detector detector = config.getDetector();
        if (mode == Mode.MINIMAL && detector instanceof DefaultDetector) {
            Comment detComment = doc.createComment("for example: <detectors><detector class=\"org.apache.tika.detector.MimeTypes\"></detectors>");
            rootElement.appendChild(detComment);
            return;
        }
        Element detectorsElement = doc.createElement("detectors");
        if (mode == Mode.CURRENT && detector instanceof DefaultDetector || !(detector instanceof CompositeDetector)) {
            Element detectorElement = doc.createElement("detector");
            detectorElement.setAttribute("class", detector.getClass().getCanonicalName());
            detectorsElement.appendChild(detectorElement);
        } else {
            List<Detector> children = ((CompositeDetector)detector).getDetectors();
            for (Detector d : children) {
                Element detectorElement = doc.createElement("detector");
                detectorElement.setAttribute("class", d.getClass().getCanonicalName());
                detectorsElement.appendChild(detectorElement);
            }
        }
        rootElement.appendChild(detectorsElement);
    }

    private static void addParsers(Mode mode, Element rootElement, Document doc, TikaConfig config) throws Exception {
        Parser parser = config.getParser();
        if (mode == Mode.MINIMAL && parser instanceof DefaultParser) {
            return;
        }
        if (mode == Mode.MINIMAL) {
            mode = Mode.CURRENT;
        }
        Element parsersElement = doc.createElement("parsers");
        rootElement.appendChild(parsersElement);
        TikaConfigSerializer.addParser(mode, parsersElement, doc, parser);
    }

    private static void addParser(Mode mode, Element rootElement, Document doc, Parser parser) throws Exception {
        ParserDecorator decoration = null;
        if (parser instanceof ParserDecorator && parser.getClass().getName().startsWith(ParserDecorator.class.getName() + "$")) {
            decoration = (ParserDecorator)parser;
            parser = decoration.getWrappedParser();
        }
        boolean outputParser = true;
        List<Object> children = Collections.emptyList();
        if (mode != Mode.CURRENT || !(parser instanceof DefaultParser)) {
            if (parser instanceof CompositeParser) {
                children = ((CompositeParser)parser).getAllComponentParsers();
                if (parser.getClass().equals(CompositeParser.class)) {
                    outputParser = false;
                }
                if (parser instanceof DefaultParser && (mode == Mode.STATIC || mode == Mode.STATIC_FULL)) {
                    outputParser = false;
                }
            } else if (parser instanceof AbstractMultipleParser) {
                children = ((AbstractMultipleParser)parser).getAllParsers();
            }
        }
        if (outputParser) {
            rootElement = TikaConfigSerializer.addParser(mode, rootElement, doc, parser, decoration);
        }
        for (Parser childParser : children) {
            TikaConfigSerializer.addParser(mode, rootElement, doc, childParser);
        }
    }

    private static Element addParser(Mode mode, Element rootElement, Document doc, Parser parser, ParserDecorator decorator) throws Exception {
        Element mimeElement;
        ParseContext context = new ParseContext();
        TreeSet<MediaType> addedTypes = new TreeSet<MediaType>();
        TreeSet<MediaType> excludedTypes = new TreeSet<MediaType>();
        if (decorator != null) {
            TreeSet<MediaType> types = new TreeSet<MediaType>(decorator.getSupportedTypes(context));
            addedTypes.addAll(types);
            for (MediaType type : parser.getSupportedTypes(context)) {
                if (!types.contains(type)) {
                    excludedTypes.add(type);
                }
                addedTypes.remove(type);
            }
        } else if (mode == Mode.STATIC_FULL) {
            addedTypes.addAll(parser.getSupportedTypes(context));
        }
        String className = parser.getClass().getCanonicalName();
        Element parserElement = doc.createElement("parser");
        parserElement.setAttribute("class", className);
        rootElement.appendChild(parserElement);
        if (parser instanceof AbstractMultipleParser) {
            Element paramsElement = doc.createElement("params");
            Element paramElement = doc.createElement("param");
            paramElement.setAttribute("name", "metadataPolicy");
            paramElement.setAttribute("value", ((AbstractMultipleParser)parser).getMetadataPolicy().toString());
            paramsElement.appendChild(paramElement);
            parserElement.appendChild(paramsElement);
        }
        for (MediaType type : addedTypes) {
            mimeElement = doc.createElement("mime");
            mimeElement.appendChild(doc.createTextNode(type.toString()));
            parserElement.appendChild(mimeElement);
        }
        for (MediaType type : excludedTypes) {
            mimeElement = doc.createElement("mime-exclude");
            mimeElement.appendChild(doc.createTextNode(type.toString()));
            parserElement.appendChild(mimeElement);
        }
        return parserElement;
    }

    public static enum Mode {
        MINIMAL,
        CURRENT,
        STATIC,
        STATIC_FULL;

    }
}


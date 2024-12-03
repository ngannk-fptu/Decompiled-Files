/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.config;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.tika.config.Field;
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
import org.apache.tika.utils.StringUtils;
import org.apache.tika.utils.XMLReaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TikaConfigSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(TikaConfigSerializer.class);
    private static Map<Class, String> PRIMITIVES = new HashMap<Class, String>();

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
                TikaConfigSerializer.serializeParams(doc, encDetectorElement, d);
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
                TikaConfigSerializer.serializeParams(doc, detectorElement, d);
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
        TikaConfigSerializer.serializeParams(doc, parserElement, parser);
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

    public static void serializeParams(Document doc, Element element, Object object) {
        Matcher setterMatcher = Pattern.compile("\\Aset([A-Z].*)").matcher("");
        Matcher getterMatcher = Pattern.compile("\\A(?:get|is)([A-Z].+)\\Z").matcher("");
        MethodTuples nonPrimitiveSetters = new MethodTuples();
        MethodTuples primitiveSetters = new MethodTuples();
        MethodTuples nonPrimitiveGetters = new MethodTuples();
        MethodTuples primitiveGetters = new MethodTuples();
        for (Method method : object.getClass().getMethods()) {
            String paramName;
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (setterMatcher.reset(method.getName()).find()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    LOG.trace("inaccessible setter: {} in {}", (Object)method.getName(), object.getClass());
                    continue;
                }
                if (method.getAnnotation(Field.class) == null) continue;
                if (parameterTypes.length != 1) {
                    LOG.warn("setter with wrong number of params " + method.getName() + " " + parameterTypes.length);
                    continue;
                }
                paramName = TikaConfigSerializer.methodToParamName(setterMatcher.group(1));
                if (PRIMITIVES.containsKey(parameterTypes[0])) {
                    primitiveSetters.add(new MethodTuple(paramName, method, parameterTypes[0]));
                    continue;
                }
                nonPrimitiveSetters.add(new MethodTuple(paramName, method, parameterTypes[0]));
                continue;
            }
            if (!getterMatcher.reset(method.getName()).find() || parameterTypes.length != 0) continue;
            paramName = TikaConfigSerializer.methodToParamName(getterMatcher.group(1));
            if (PRIMITIVES.containsKey(method.getReturnType())) {
                primitiveGetters.add(new MethodTuple(paramName, method, method.getReturnType()));
                continue;
            }
            nonPrimitiveGetters.add(new MethodTuple(paramName, method, method.getReturnType()));
        }
        TikaConfigSerializer.serializePrimitives(doc, element, object, primitiveSetters, primitiveGetters);
        TikaConfigSerializer.serializeNonPrimitives(doc, element, object, nonPrimitiveSetters, nonPrimitiveGetters);
    }

    private static String methodToParamName(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        return name.substring(0, 1).toLowerCase(Locale.US) + name.substring(1);
    }

    private static void serializeNonPrimitives(Document doc, Element element, Object object, MethodTuples setterTuples, MethodTuples getterTuples) {
        for (Map.Entry<String, Set<MethodTuple>> e : setterTuples.tuples.entrySet()) {
            Set<MethodTuple> getters = getterTuples.tuples.get(e.getKey());
            TikaConfigSerializer.processNonPrimitive(e.getKey(), e.getValue(), getters, doc, element, object);
            if (getterTuples.tuples.containsKey(e.getKey())) continue;
            LOG.warn("no getter for setter non-primitive: {} in {}", (Object)e.getKey(), object.getClass());
        }
    }

    private static void processNonPrimitive(String name, Set<MethodTuple> setters, Set<MethodTuple> getters, Document doc, Element element, Object object) {
        for (MethodTuple setter : setters) {
            for (MethodTuple getter : getters) {
                if (!setter.singleParam.equals(getter.singleParam)) continue;
                TikaConfigSerializer.serializeObject(name, doc, element, setter, getter, object);
                return;
            }
        }
    }

    private static void serializeObject(String name, Document doc, Element element, MethodTuple setter, MethodTuple getter, Object object) {
        Object item = null;
        try {
            item = getter.method.invoke(object, new Object[0]);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn("couldn't get " + name + " on " + object.getClass(), (Throwable)e);
            return;
        }
        if (item == null) {
            LOG.warn("Getter {} on {} returned null", (Object)getter.name, object.getClass());
        }
        Element entry = doc.createElement(name);
        entry.setAttribute("class", item.getClass().getCanonicalName());
        element.appendChild(entry);
        TikaConfigSerializer.serializeParams(doc, element, item);
    }

    private static void serializePrimitives(Document doc, Element root, Object object, MethodTuples setterTuples, MethodTuples getterTuples) {
        Element paramsElement = null;
        if (object instanceof AbstractMultipleParser) {
            paramsElement = doc.createElement("params");
            Element paramElement = doc.createElement("param");
            paramElement.setAttribute("name", "metadataPolicy");
            paramElement.setAttribute("value", ((AbstractMultipleParser)object).getMetadataPolicy().toString());
            paramsElement.appendChild(paramElement);
            root.appendChild(paramsElement);
        }
        for (Map.Entry<String, Set<MethodTuple>> e : setterTuples.tuples.entrySet()) {
            if (!getterTuples.tuples.containsKey(e.getKey())) {
                LOG.info("no getter for setter: {} in {}", (Object)e.getKey(), object.getClass());
                continue;
            }
            Set<MethodTuple> getters = getterTuples.tuples.get(e.getKey());
            Set<MethodTuple> setters = e.getValue();
            MethodTuple getterTuple = null;
            block4: for (MethodTuple getterCandidate : getters) {
                for (MethodTuple setter : setters) {
                    if (!getterCandidate.singleParam.equals(setter.singleParam)) continue;
                    getterTuple = getterCandidate;
                    continue block4;
                }
            }
            if (getterTuple == null) {
                LOG.debug("Could not find getter to match setter for: {}", (Object)e.getKey());
                continue;
            }
            Object value = null;
            try {
                value = getterTuple.method.invoke(object, new Object[0]);
            }
            catch (IllegalAccessException ex) {
                LOG.error("couldn't invoke " + getterTuple, (Throwable)ex);
                continue;
            }
            catch (InvocationTargetException ex) {
                LOG.error("couldn't invoke " + getterTuple, (Throwable)ex);
                continue;
            }
            if (value == null) {
                LOG.debug("null value: {} in {}", (Object)getterTuple.name, object.getClass());
            }
            String valString = value == null ? "" : value.toString();
            Element param = doc.createElement("param");
            param.setAttribute("name", getterTuple.name);
            param.setAttribute("type", PRIMITIVES.get(getterTuple.singleParam));
            if (List.class.isAssignableFrom(getterTuple.singleParam)) {
                TikaConfigSerializer.addList(param, doc, getterTuple, (List)value);
            } else if (Map.class.isAssignableFrom(getterTuple.singleParam)) {
                TikaConfigSerializer.addMap(param, doc, getterTuple, (Map)value);
            } else {
                param.setTextContent(valString);
            }
            if (paramsElement == null) {
                paramsElement = doc.createElement("params");
                root.appendChild(paramsElement);
            }
            paramsElement.appendChild(param);
        }
    }

    private static void addMap(Element param, Document doc, MethodTuple getterTuple, Map<String, String> object) {
        for (Map.Entry<String, String> e : new TreeMap<String, String>(object).entrySet()) {
            Element element = doc.createElement("string");
            element.setAttribute("key", e.getKey());
            element.setAttribute("value", e.getValue());
            param.appendChild(element);
        }
    }

    private static void addList(Element param, Document doc, MethodTuple getterTuple, List<String> list) {
        for (String s : list) {
            Element element = doc.createElement("string");
            element.setTextContent(s);
            param.appendChild(element);
        }
    }

    private static Method findGetter(MethodTuple setter, Object object) {
        Matcher m = Pattern.compile("\\A(?:get|is)([A-Z].+)\\Z").matcher("");
        for (Method method : object.getClass().getMethods()) {
            Class<?> returnType;
            String paramName;
            if (object.getClass().getName().contains("PDF")) {
                System.out.println(method.getName());
            }
            if (!m.reset(method.getName()).find()) continue;
            if (object.getClass().getName().contains("PDF")) {
                System.out.println("2: " + method.getName());
            }
            if (!setter.name.equals(paramName = m.group(1)) || !setter.singleParam.equals(returnType = method.getReturnType())) continue;
            return method;
        }
        return null;
    }

    private static MethodTuple pickBestSetter(Set<MethodTuple> tuples) {
        Iterator<MethodTuple> iterator = tuples.iterator();
        if (iterator.hasNext()) {
            MethodTuple t = iterator.next();
            return t;
        }
        return null;
    }

    static {
        PRIMITIVES.put(Integer.class, "int");
        PRIMITIVES.put(Integer.TYPE, "int");
        PRIMITIVES.put(String.class, "string");
        PRIMITIVES.put(Boolean.class, "bool");
        PRIMITIVES.put(Boolean.TYPE, "bool");
        PRIMITIVES.put(Float.class, "float");
        PRIMITIVES.put(Float.TYPE, "float");
        PRIMITIVES.put(Double.class, "double");
        PRIMITIVES.put(Double.TYPE, "double");
        PRIMITIVES.put(Long.class, "long");
        PRIMITIVES.put(Long.TYPE, "long");
        PRIMITIVES.put(Map.class, "map");
        PRIMITIVES.put(List.class, "list");
    }

    public static enum Mode {
        MINIMAL,
        CURRENT,
        STATIC,
        STATIC_FULL;

    }

    private static class MethodTuple {
        String name;
        Method method;
        Class singleParam;

        public MethodTuple(String name, Method method, Class singleParam) {
            this.name = name;
            this.method = method;
            this.singleParam = singleParam;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MethodTuple that = (MethodTuple)o;
            return this.name.equals(that.name) && this.method.equals(that.method) && this.singleParam.equals(that.singleParam);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.method, this.singleParam);
        }
    }

    private static class MethodTuples {
        Map<String, Set<MethodTuple>> tuples = new TreeMap<String, Set<MethodTuple>>();

        private MethodTuples() {
        }

        public void add(MethodTuple tuple) {
            Set<MethodTuple> set = this.tuples.get(tuple.name);
            if (set == null) {
                set = new HashSet<MethodTuple>();
                this.tuples.put(tuple.name, set);
            }
            set.add(tuple);
        }

        public int getSize() {
            return this.tuples.size();
        }
    }
}


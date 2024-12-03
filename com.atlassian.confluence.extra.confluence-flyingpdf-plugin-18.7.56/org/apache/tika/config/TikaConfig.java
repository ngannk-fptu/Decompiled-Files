/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tika.concurrent.ConfigurableThreadPoolExecutor;
import org.apache.tika.concurrent.SimpleThreadPoolExecutor;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.LoadErrorHandler;
import org.apache.tika.config.Param;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.CompositeEncodingDetector;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.translate.DefaultTranslator;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.metadata.filter.CompositeMetadataFilter;
import org.apache.tika.metadata.filter.DefaultMetadataFilter;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.apache.tika.parser.AbstractEncodingDetectorParser;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.utils.AnnotationUtils;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TikaConfig {
    protected static AtomicInteger TIMES_INSTANTIATED = new AtomicInteger();
    private final ServiceLoader serviceLoader;
    private final CompositeParser parser;
    private final CompositeDetector detector;
    private final Translator translator;
    private final MimeTypes mimeTypes;
    private final ExecutorService executorService;
    private final EncodingDetector encodingDetector;
    private final MetadataFilter metadataFilter;

    private static MimeTypes getDefaultMimeTypes(ClassLoader loader) {
        return MimeTypes.getDefaultMimeTypes(loader);
    }

    protected static CompositeDetector getDefaultDetector(MimeTypes types, ServiceLoader loader) {
        return new DefaultDetector(types, loader);
    }

    protected static CompositeEncodingDetector getDefaultEncodingDetector(ServiceLoader loader) {
        return new DefaultEncodingDetector(loader);
    }

    private static CompositeParser getDefaultParser(MimeTypes types, ServiceLoader loader, EncodingDetector encodingDetector) {
        return new DefaultParser(types.getMediaTypeRegistry(), loader, encodingDetector);
    }

    private static Translator getDefaultTranslator(ServiceLoader loader) {
        return new DefaultTranslator(loader);
    }

    private static ConfigurableThreadPoolExecutor getDefaultExecutorService() {
        return new SimpleThreadPoolExecutor();
    }

    private static MetadataFilter getDefaultMetadataFilter(ServiceLoader loader) {
        return new DefaultMetadataFilter(loader);
    }

    public TikaConfig(String file) throws TikaException, IOException, SAXException {
        this(Paths.get(file, new String[0]));
    }

    public TikaConfig(Path path) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(path));
    }

    public TikaConfig(Path path, ServiceLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(path), loader);
    }

    public TikaConfig(File file) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(file.toPath()));
    }

    public TikaConfig(File file, ServiceLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(file.toPath()), loader);
    }

    public TikaConfig(URL url) throws TikaException, IOException, SAXException {
        this(url, ServiceLoader.getContextClassLoader());
    }

    public TikaConfig(URL url, ClassLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(url.toString()).getDocumentElement(), loader);
    }

    public TikaConfig(URL url, ServiceLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(url.toString()).getDocumentElement(), loader);
    }

    public TikaConfig(InputStream stream) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(stream));
    }

    public TikaConfig(Document document) throws TikaException, IOException {
        this(document.getDocumentElement());
    }

    public TikaConfig(Document document, ServiceLoader loader) throws TikaException, IOException {
        this(document.getDocumentElement(), loader);
    }

    public TikaConfig(Element element) throws TikaException, IOException {
        this(element, TikaConfig.serviceLoaderFromDomElement(element, null));
    }

    public TikaConfig(Element element, ClassLoader loader) throws TikaException, IOException {
        this(element, TikaConfig.serviceLoaderFromDomElement(element, loader));
    }

    private TikaConfig(Element element, ServiceLoader loader) throws TikaException, IOException {
        DetectorXmlLoader detectorLoader = new DetectorXmlLoader();
        TranslatorXmlLoader translatorLoader = new TranslatorXmlLoader();
        ExecutorServiceXmlLoader executorLoader = new ExecutorServiceXmlLoader();
        EncodingDetectorXmlLoader encodingDetectorXmlLoader = new EncodingDetectorXmlLoader();
        MetadataFilterXmlLoader metadataFilterXmlLoader = new MetadataFilterXmlLoader();
        this.updateXMLReaderUtils(element);
        this.mimeTypes = TikaConfig.typesFromDomElement(element);
        this.detector = (CompositeDetector)detectorLoader.loadOverall(element, this.mimeTypes, loader);
        this.encodingDetector = (EncodingDetector)encodingDetectorXmlLoader.loadOverall(element, this.mimeTypes, loader);
        ParserXmlLoader parserLoader = new ParserXmlLoader(this.encodingDetector);
        this.parser = (CompositeParser)parserLoader.loadOverall(element, this.mimeTypes, loader);
        this.translator = (Translator)translatorLoader.loadOverall(element, this.mimeTypes, loader);
        this.executorService = (ExecutorService)executorLoader.loadOverall(element, this.mimeTypes, loader);
        this.metadataFilter = (MetadataFilter)metadataFilterXmlLoader.loadOverall(element, this.mimeTypes, loader);
        this.serviceLoader = loader;
        TIMES_INSTANTIATED.incrementAndGet();
    }

    public TikaConfig(ClassLoader loader) throws MimeTypeException, IOException {
        this.serviceLoader = new ServiceLoader(loader);
        this.mimeTypes = TikaConfig.getDefaultMimeTypes(loader);
        this.detector = TikaConfig.getDefaultDetector(this.mimeTypes, this.serviceLoader);
        this.encodingDetector = TikaConfig.getDefaultEncodingDetector(this.serviceLoader);
        this.parser = TikaConfig.getDefaultParser(this.mimeTypes, this.serviceLoader, this.encodingDetector);
        this.translator = TikaConfig.getDefaultTranslator(this.serviceLoader);
        this.executorService = TikaConfig.getDefaultExecutorService();
        this.metadataFilter = TikaConfig.getDefaultMetadataFilter(this.serviceLoader);
        TIMES_INSTANTIATED.incrementAndGet();
    }

    public TikaConfig() throws TikaException, IOException {
        String config = System.getProperty("tika.config");
        if (config == null || config.trim().equals("")) {
            config = System.getenv("TIKA_CONFIG");
        }
        if (config == null || config.trim().equals("")) {
            this.serviceLoader = new ServiceLoader();
            this.mimeTypes = TikaConfig.getDefaultMimeTypes(ServiceLoader.getContextClassLoader());
            this.encodingDetector = TikaConfig.getDefaultEncodingDetector(this.serviceLoader);
            this.parser = TikaConfig.getDefaultParser(this.mimeTypes, this.serviceLoader, this.encodingDetector);
            this.detector = TikaConfig.getDefaultDetector(this.mimeTypes, this.serviceLoader);
            this.translator = TikaConfig.getDefaultTranslator(this.serviceLoader);
            this.executorService = TikaConfig.getDefaultExecutorService();
            this.metadataFilter = TikaConfig.getDefaultMetadataFilter(this.serviceLoader);
        } else {
            ServiceLoader tmpServiceLoader = new ServiceLoader();
            try (InputStream stream = TikaConfig.getConfigInputStream(config, tmpServiceLoader);){
                Element element = XMLReaderUtils.buildDOM(stream).getDocumentElement();
                this.updateXMLReaderUtils(element);
                this.serviceLoader = TikaConfig.serviceLoaderFromDomElement(element, tmpServiceLoader.getLoader());
                DetectorXmlLoader detectorLoader = new DetectorXmlLoader();
                EncodingDetectorXmlLoader encodingDetectorLoader = new EncodingDetectorXmlLoader();
                TranslatorXmlLoader translatorLoader = new TranslatorXmlLoader();
                ExecutorServiceXmlLoader executorLoader = new ExecutorServiceXmlLoader();
                MetadataFilterXmlLoader metadataFilterXmlLoader = new MetadataFilterXmlLoader();
                this.mimeTypes = TikaConfig.typesFromDomElement(element);
                this.encodingDetector = (EncodingDetector)encodingDetectorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
                ParserXmlLoader parserLoader = new ParserXmlLoader(this.encodingDetector);
                this.parser = (CompositeParser)parserLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.detector = (CompositeDetector)detectorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.translator = (Translator)translatorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.executorService = (ExecutorService)executorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.metadataFilter = (MetadataFilter)metadataFilterXmlLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
            }
            catch (SAXException e) {
                throw new TikaException("Specified Tika configuration has syntax errors: " + config, e);
            }
        }
        TIMES_INSTANTIATED.incrementAndGet();
    }

    private void updateXMLReaderUtils(Element element) throws TikaException {
        Element child = TikaConfig.getChild(element, "xml-reader-utils");
        if (child == null) {
            return;
        }
        String attr = child.getAttribute("maxEntityExpansions");
        if (attr != null) {
            XMLReaderUtils.setMaxEntityExpansions(Integer.parseInt(attr));
        }
        if ((attr = child.getAttribute("poolSize")) != null) {
            XMLReaderUtils.setPoolSize(Integer.parseInt(attr));
        }
    }

    private static InputStream getConfigInputStream(String config, ServiceLoader serviceLoader) throws TikaException, IOException {
        Path file;
        InputStream stream = null;
        try {
            stream = new URL(config).openStream();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (stream == null) {
            stream = serviceLoader.getResourceAsStream(config);
        }
        if (stream == null && Files.isRegularFile(file = Paths.get(config, new String[0]), new LinkOption[0])) {
            stream = Files.newInputStream(file, new OpenOption[0]);
        }
        if (stream == null) {
            throw new TikaException("Specified Tika configuration not found: " + config);
        }
        return stream;
    }

    private static String getText(Node node) {
        if (node.getNodeType() == 3) {
            return node.getNodeValue();
        }
        if (node.getNodeType() == 1) {
            StringBuilder builder = new StringBuilder();
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); ++i) {
                builder.append(TikaConfig.getText(list.item(i)));
            }
            return builder.toString();
        }
        return "";
    }

    public Parser getParser(MediaType mimeType) {
        return this.parser.getParsers().get(mimeType);
    }

    public Parser getParser() {
        return this.parser;
    }

    public Detector getDetector() {
        return this.detector;
    }

    public EncodingDetector getEncodingDetector() {
        return this.encodingDetector;
    }

    public Translator getTranslator() {
        return this.translator;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public MimeTypes getMimeRepository() {
        return this.mimeTypes;
    }

    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.mimeTypes.getMediaTypeRegistry();
    }

    public ServiceLoader getServiceLoader() {
        return this.serviceLoader;
    }

    public MetadataFilter getMetadataFilter() {
        return this.metadataFilter;
    }

    public static TikaConfig getDefaultConfig() {
        try {
            return new TikaConfig();
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to read default configuration", e);
        }
        catch (TikaException e) {
            throw new RuntimeException("Unable to access default configuration", e);
        }
    }

    private static Element getChild(Element element, String name) {
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() != 1 || !name.equals(child.getNodeName())) continue;
            return (Element)child;
        }
        return null;
    }

    private static List<Element> getTopLevelElementChildren(Element element, String parentName, String childrenName) throws TikaException {
        NodeList nodes;
        Node parentNode = null;
        if (parentName != null) {
            nodes = element.getElementsByTagName(parentName);
            if (nodes.getLength() > 1) {
                throw new TikaException("Properties may not contain multiple " + parentName + " entries");
            }
            if (nodes.getLength() == 1) {
                parentNode = nodes.item(0);
            }
        } else {
            parentNode = element;
        }
        if (parentNode != null) {
            nodes = parentNode.getChildNodes();
            ArrayList<Element> elements = new ArrayList<Element>();
            for (int i = 0; i < nodes.getLength(); ++i) {
                Element nodeE;
                Node node = nodes.item(i);
                if (!(node instanceof Element) || !childrenName.equals((nodeE = (Element)node).getTagName())) continue;
                elements.add(nodeE);
            }
            return elements;
        }
        return Collections.emptyList();
    }

    private static MimeTypes typesFromDomElement(Element element) throws TikaException, IOException {
        Element mtr = TikaConfig.getChild(element, "mimeTypeRepository");
        if (mtr != null && mtr.hasAttribute("resource")) {
            return MimeTypesFactory.create(mtr.getAttribute("resource"));
        }
        return TikaConfig.getDefaultMimeTypes(null);
    }

    private static Set<MediaType> mediaTypesListFromDomElement(Element node, String tag) throws TikaException, IOException {
        HashSet<MediaType> types = null;
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Element cElement;
            Node cNode = children.item(i);
            if (!(cNode instanceof Element) || !tag.equals((cElement = (Element)cNode).getTagName())) continue;
            String mime = TikaConfig.getText(cElement);
            MediaType type = MediaType.parse(mime);
            if (type != null) {
                if (types == null) {
                    types = new HashSet<MediaType>();
                }
                types.add(type);
                continue;
            }
            throw new TikaException("Invalid media type name: " + mime);
        }
        if (types != null) {
            return types;
        }
        return Collections.emptySet();
    }

    private static ServiceLoader serviceLoaderFromDomElement(Element element, ClassLoader loader) throws TikaConfigException {
        ServiceLoader serviceLoader;
        Element serviceLoaderElement = TikaConfig.getChild(element, "service-loader");
        if (serviceLoaderElement != null) {
            boolean dynamic = Boolean.parseBoolean(serviceLoaderElement.getAttribute("dynamic"));
            LoadErrorHandler loadErrorHandler = LoadErrorHandler.IGNORE;
            String loadErrorHandleConfig = serviceLoaderElement.getAttribute("loadErrorHandler");
            if (LoadErrorHandler.WARN.toString().equalsIgnoreCase(loadErrorHandleConfig)) {
                loadErrorHandler = LoadErrorHandler.WARN;
            } else if (LoadErrorHandler.THROW.toString().equalsIgnoreCase(loadErrorHandleConfig)) {
                loadErrorHandler = LoadErrorHandler.THROW;
            }
            InitializableProblemHandler initializableProblemHandler = TikaConfig.getInitializableProblemHandler(serviceLoaderElement.getAttribute("initializableProblemHandler"));
            if (loader == null) {
                loader = ServiceLoader.getContextClassLoader();
            }
            serviceLoader = new ServiceLoader(loader, loadErrorHandler, initializableProblemHandler, dynamic);
        } else {
            serviceLoader = loader != null ? new ServiceLoader(loader) : new ServiceLoader();
        }
        return serviceLoader;
    }

    private static InitializableProblemHandler getInitializableProblemHandler(String initializableProblemHandler) throws TikaConfigException {
        if (initializableProblemHandler == null || initializableProblemHandler.length() == 0) {
            return InitializableProblemHandler.DEFAULT;
        }
        if (InitializableProblemHandler.IGNORE.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.IGNORE;
        }
        if (InitializableProblemHandler.INFO.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.INFO;
        }
        if (InitializableProblemHandler.WARN.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.WARN;
        }
        if (InitializableProblemHandler.THROW.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.THROW;
        }
        throw new TikaConfigException(String.format(Locale.US, "Couldn't parse non-null '%s'. Must be one of 'ignore', 'info', 'warn' or 'throw'", initializableProblemHandler));
    }

    private static class MetadataFilterXmlLoader
    extends XmlLoader<MetadataFilter, MetadataFilter> {
        private MetadataFilterXmlLoader() {
        }

        @Override
        boolean supportsComposite() {
            return true;
        }

        @Override
        String getParentTagName() {
            return "metadataFilters";
        }

        @Override
        String getLoaderTagName() {
            return "metadataFilter";
        }

        @Override
        Class<? extends MetadataFilter> getLoaderClass() {
            return MetadataFilter.class;
        }

        @Override
        boolean isComposite(MetadataFilter loaded) {
            return loaded instanceof CompositeMetadataFilter;
        }

        @Override
        boolean isComposite(Class<? extends MetadataFilter> loadedClass) {
            return CompositeMetadataFilter.class.isAssignableFrom(loadedClass);
        }

        @Override
        MetadataFilter preLoadOne(Class<? extends MetadataFilter> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
            return null;
        }

        @Override
        MetadataFilter createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
            return TikaConfig.getDefaultMetadataFilter(loader);
        }

        @Override
        MetadataFilter createComposite(List<MetadataFilter> loaded, MimeTypes mimeTypes, ServiceLoader loader) {
            return new DefaultMetadataFilter(loaded);
        }

        @Override
        MetadataFilter createComposite(Class<? extends MetadataFilter> metadataFilterClass, List<MetadataFilter> childMetadataFilters, Set<Class<? extends MetadataFilter>> excludeFilters, Map<String, Param> params, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            Constructor<? extends MetadataFilter> c;
            MetadataFilter metadataFilter = null;
            if (metadataFilter == null) {
                try {
                    c = metadataFilterClass.getConstructor(ServiceLoader.class, Collection.class);
                    metadataFilter = c.newInstance(loader, excludeFilters);
                }
                catch (NoSuchMethodException me) {
                    me.printStackTrace();
                }
            }
            if (metadataFilter == null) {
                try {
                    c = metadataFilterClass.getConstructor(List.class);
                    metadataFilter = c.newInstance(childMetadataFilters);
                }
                catch (NoSuchMethodException me) {
                    me.printStackTrace();
                }
            }
            return metadataFilter;
        }

        @Override
        MetadataFilter decorate(MetadataFilter created, Element element) {
            return created;
        }
    }

    private static class EncodingDetectorXmlLoader
    extends XmlLoader<EncodingDetector, EncodingDetector> {
        private EncodingDetectorXmlLoader() {
        }

        @Override
        boolean supportsComposite() {
            return true;
        }

        @Override
        String getParentTagName() {
            return "encodingDetectors";
        }

        @Override
        String getLoaderTagName() {
            return "encodingDetector";
        }

        @Override
        Class<? extends EncodingDetector> getLoaderClass() {
            return EncodingDetector.class;
        }

        @Override
        boolean isComposite(EncodingDetector loaded) {
            return loaded instanceof CompositeEncodingDetector;
        }

        @Override
        boolean isComposite(Class<? extends EncodingDetector> loadedClass) {
            return CompositeEncodingDetector.class.isAssignableFrom(loadedClass);
        }

        @Override
        EncodingDetector preLoadOne(Class<? extends EncodingDetector> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
            return null;
        }

        @Override
        EncodingDetector createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
            return TikaConfig.getDefaultEncodingDetector(loader);
        }

        @Override
        CompositeEncodingDetector createComposite(List<EncodingDetector> encodingDetectors, MimeTypes mimeTypes, ServiceLoader loader) {
            return new CompositeEncodingDetector(encodingDetectors);
        }

        @Override
        EncodingDetector createComposite(Class<? extends EncodingDetector> encodingDetectorClass, List<EncodingDetector> childEncodingDetectors, Set<Class<? extends EncodingDetector>> excludeDetectors, Map<String, Param> params, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            Constructor<? extends EncodingDetector> c;
            EncodingDetector encodingDetector = null;
            if (encodingDetector == null) {
                try {
                    c = encodingDetectorClass.getConstructor(ServiceLoader.class, Collection.class);
                    encodingDetector = c.newInstance(loader, excludeDetectors);
                }
                catch (NoSuchMethodException me) {
                    me.printStackTrace();
                }
            }
            if (encodingDetector == null) {
                try {
                    c = encodingDetectorClass.getConstructor(List.class);
                    encodingDetector = c.newInstance(childEncodingDetectors);
                }
                catch (NoSuchMethodException me) {
                    me.printStackTrace();
                }
            }
            return encodingDetector;
        }

        @Override
        EncodingDetector decorate(EncodingDetector created, Element element) {
            return created;
        }
    }

    private static class ExecutorServiceXmlLoader
    extends XmlLoader<ConfigurableThreadPoolExecutor, ConfigurableThreadPoolExecutor> {
        private ExecutorServiceXmlLoader() {
        }

        @Override
        ConfigurableThreadPoolExecutor createComposite(Class<? extends ConfigurableThreadPoolExecutor> compositeClass, List<ConfigurableThreadPoolExecutor> children, Set<Class<? extends ConfigurableThreadPoolExecutor>> excludeChildren, Map<String, Param> params, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            throw new InstantiationException("Only one executor service supported");
        }

        @Override
        ConfigurableThreadPoolExecutor createComposite(List<ConfigurableThreadPoolExecutor> loaded, MimeTypes mimeTypes, ServiceLoader loader) {
            return loaded.get(0);
        }

        @Override
        ConfigurableThreadPoolExecutor createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
            return TikaConfig.getDefaultExecutorService();
        }

        @Override
        ConfigurableThreadPoolExecutor decorate(ConfigurableThreadPoolExecutor created, Element element) throws IOException, TikaException {
            Element coreThreadElement;
            Element maxThreadElement = TikaConfig.getChild(element, "max-threads");
            if (maxThreadElement != null) {
                created.setMaximumPoolSize(Integer.parseInt(TikaConfig.getText(maxThreadElement)));
            }
            if ((coreThreadElement = TikaConfig.getChild(element, "core-threads")) != null) {
                created.setCorePoolSize(Integer.parseInt(TikaConfig.getText(coreThreadElement)));
            }
            return created;
        }

        @Override
        Class<? extends ConfigurableThreadPoolExecutor> getLoaderClass() {
            return ConfigurableThreadPoolExecutor.class;
        }

        @Override
        ConfigurableThreadPoolExecutor loadOne(Element element, MimeTypes mimeTypes, ServiceLoader loader) throws TikaException, IOException {
            return (ConfigurableThreadPoolExecutor)super.loadOne(element, mimeTypes, loader);
        }

        @Override
        boolean supportsComposite() {
            return false;
        }

        @Override
        String getParentTagName() {
            return null;
        }

        @Override
        String getLoaderTagName() {
            return "executor-service";
        }

        @Override
        boolean isComposite(ConfigurableThreadPoolExecutor loaded) {
            return false;
        }

        @Override
        boolean isComposite(Class<? extends ConfigurableThreadPoolExecutor> loadedClass) {
            return false;
        }

        @Override
        ConfigurableThreadPoolExecutor preLoadOne(Class<? extends ConfigurableThreadPoolExecutor> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
            return null;
        }
    }

    private static class TranslatorXmlLoader
    extends XmlLoader<Translator, Translator> {
        private TranslatorXmlLoader() {
        }

        @Override
        boolean supportsComposite() {
            return false;
        }

        @Override
        String getParentTagName() {
            return null;
        }

        @Override
        String getLoaderTagName() {
            return "translator";
        }

        @Override
        Class<? extends Translator> getLoaderClass() {
            return Translator.class;
        }

        @Override
        Translator preLoadOne(Class<? extends Translator> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
            return null;
        }

        @Override
        boolean isComposite(Translator loaded) {
            return false;
        }

        @Override
        boolean isComposite(Class<? extends Translator> loadedClass) {
            return false;
        }

        @Override
        Translator createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
            return TikaConfig.getDefaultTranslator(loader);
        }

        @Override
        Translator createComposite(List<Translator> loaded, MimeTypes mimeTypes, ServiceLoader loader) {
            return loaded.get(0);
        }

        @Override
        Translator createComposite(Class<? extends Translator> compositeClass, List<Translator> children, Set<Class<? extends Translator>> excludeChildren, Map<String, Param> params, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            throw new InstantiationException("Only one translator supported");
        }

        @Override
        Translator decorate(Translator created, Element element) {
            return created;
        }
    }

    private static class DetectorXmlLoader
    extends XmlLoader<CompositeDetector, Detector> {
        private DetectorXmlLoader() {
        }

        @Override
        boolean supportsComposite() {
            return true;
        }

        @Override
        String getParentTagName() {
            return "detectors";
        }

        @Override
        String getLoaderTagName() {
            return "detector";
        }

        @Override
        Class<? extends Detector> getLoaderClass() {
            return Detector.class;
        }

        @Override
        Detector preLoadOne(Class<? extends Detector> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
            if (MimeTypes.class.equals(loadedClass)) {
                return mimeTypes;
            }
            return null;
        }

        @Override
        boolean isComposite(Detector loaded) {
            return loaded instanceof CompositeDetector;
        }

        @Override
        boolean isComposite(Class<? extends Detector> loadedClass) {
            return CompositeDetector.class.isAssignableFrom(loadedClass);
        }

        @Override
        CompositeDetector createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
            return TikaConfig.getDefaultDetector(mimeTypes, loader);
        }

        @Override
        CompositeDetector createComposite(List<Detector> detectors, MimeTypes mimeTypes, ServiceLoader loader) {
            MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            return new CompositeDetector(registry, detectors);
        }

        @Override
        Detector createComposite(Class<? extends Detector> detectorClass, List<Detector> childDetectors, Set<Class<? extends Detector>> excludeDetectors, Map<String, Param> params, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            Constructor<? extends Detector> c;
            Detector detector = null;
            MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            if (detector == null) {
                try {
                    c = detectorClass.getConstructor(MimeTypes.class, ServiceLoader.class, Collection.class);
                    detector = c.newInstance(mimeTypes, loader, excludeDetectors);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (detector == null) {
                try {
                    c = detectorClass.getConstructor(MediaTypeRegistry.class, List.class, Collection.class);
                    detector = c.newInstance(registry, childDetectors, excludeDetectors);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (detector == null) {
                try {
                    c = detectorClass.getConstructor(MediaTypeRegistry.class, List.class);
                    detector = c.newInstance(registry, childDetectors);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (detector == null) {
                try {
                    c = detectorClass.getConstructor(List.class);
                    detector = c.newInstance(childDetectors);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            return detector;
        }

        @Override
        Detector decorate(Detector created, Element element) {
            return created;
        }
    }

    private static class ParserXmlLoader
    extends XmlLoader<CompositeParser, Parser> {
        private final EncodingDetector encodingDetector;

        @Override
        boolean supportsComposite() {
            return true;
        }

        @Override
        String getParentTagName() {
            return "parsers";
        }

        @Override
        String getLoaderTagName() {
            return "parser";
        }

        private ParserXmlLoader(EncodingDetector encodingDetector) {
            this.encodingDetector = encodingDetector;
        }

        @Override
        Class<? extends Parser> getLoaderClass() {
            return Parser.class;
        }

        @Override
        Parser preLoadOne(Class<? extends Parser> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
            if (AutoDetectParser.class.isAssignableFrom(loadedClass)) {
                throw new TikaException("AutoDetectParser not supported in a <parser> configuration element: " + classname);
            }
            return null;
        }

        @Override
        boolean isComposite(Parser loaded) {
            return loaded instanceof CompositeParser;
        }

        @Override
        boolean isComposite(Class<? extends Parser> loadedClass) {
            return CompositeParser.class.isAssignableFrom(loadedClass) || ParserDecorator.class.isAssignableFrom(loadedClass);
        }

        @Override
        CompositeParser createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
            return TikaConfig.getDefaultParser(mimeTypes, loader, this.encodingDetector);
        }

        @Override
        CompositeParser createComposite(List<Parser> parsers, MimeTypes mimeTypes, ServiceLoader loader) {
            MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            return new CompositeParser(registry, parsers);
        }

        @Override
        Parser createComposite(Class<? extends Parser> parserClass, List<Parser> childParsers, Set<Class<? extends Parser>> excludeParsers, Map<String, Param> params, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            Parser parser = null;
            Constructor<? extends Parser> c = null;
            MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, ServiceLoader.class, Collection.class, EncodingDetector.class);
                    parser = c.newInstance(registry, loader, excludeParsers, this.encodingDetector);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, ServiceLoader.class, Collection.class);
                    parser = c.newInstance(registry, loader, excludeParsers);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, List.class, Collection.class);
                    parser = c.newInstance(registry, childParsers, excludeParsers);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, Collection.class, Map.class);
                    parser = c.newInstance(registry, childParsers, params);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, List.class);
                    parser = c.newInstance(registry, childParsers);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (parser == null && ParserDecorator.class.isAssignableFrom(parserClass)) {
                try {
                    CompositeParser cp = null;
                    cp = childParsers.size() == 1 && excludeParsers.size() == 0 && childParsers.get(0) instanceof CompositeParser ? (CompositeParser)childParsers.get(0) : new CompositeParser(registry, childParsers, excludeParsers);
                    c = parserClass.getConstructor(Parser.class);
                    parser = c.newInstance(cp);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            return parser;
        }

        @Override
        Parser newInstance(Class<? extends Parser> loadedClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            if (AbstractEncodingDetectorParser.class.isAssignableFrom(loadedClass)) {
                Constructor<? extends Parser> ctor = loadedClass.getConstructor(EncodingDetector.class);
                return ctor.newInstance(this.encodingDetector);
            }
            return loadedClass.newInstance();
        }

        @Override
        Parser decorate(Parser created, Element element) throws IOException, TikaException {
            Set parserExclTypes;
            Parser parser = created;
            Set parserTypes = TikaConfig.mediaTypesListFromDomElement(element, "mime");
            if (!parserTypes.isEmpty()) {
                parser = ParserDecorator.withTypes(parser, parserTypes);
            }
            if (!(parserExclTypes = TikaConfig.mediaTypesListFromDomElement(element, "mime-exclude")).isEmpty()) {
                parser = ParserDecorator.withoutTypes(parser, parserExclTypes);
            }
            return parser;
        }
    }

    private static abstract class XmlLoader<CT, T> {
        protected static final String PARAMS_TAG_NAME = "params";

        private XmlLoader() {
        }

        abstract boolean supportsComposite();

        abstract String getParentTagName();

        abstract String getLoaderTagName();

        abstract Class<? extends T> getLoaderClass();

        abstract boolean isComposite(T var1);

        abstract boolean isComposite(Class<? extends T> var1);

        abstract T preLoadOne(Class<? extends T> var1, String var2, MimeTypes var3) throws TikaException;

        abstract CT createDefault(MimeTypes var1, ServiceLoader var2);

        abstract CT createComposite(List<T> var1, MimeTypes var2, ServiceLoader var3);

        abstract T createComposite(Class<? extends T> var1, List<T> var2, Set<Class<? extends T>> var3, Map<String, Param> var4, MimeTypes var5, ServiceLoader var6) throws InvocationTargetException, IllegalAccessException, InstantiationException;

        abstract T decorate(T var1, Element var2) throws IOException, TikaException;

        CT loadOverall(Element element, MimeTypes mimeTypes, ServiceLoader loader) throws TikaException, IOException {
            ArrayList<T> loaded = new ArrayList<T>();
            for (Element le : TikaConfig.getTopLevelElementChildren(element, this.getParentTagName(), this.getLoaderTagName())) {
                T loadedChild = this.loadOne(le, mimeTypes, loader);
                if (loadedChild == null) continue;
                loaded.add(loadedChild);
            }
            if (loaded.isEmpty()) {
                return this.createDefault(mimeTypes, loader);
            }
            if (loaded.size() == 1) {
                Object single = loaded.get(0);
                if (this.isComposite(single)) {
                    return (CT)single;
                }
            } else if (!this.supportsComposite()) {
                return (CT)loaded.get(0);
            }
            return this.createComposite(loaded, mimeTypes, loader);
        }

        T loadOne(Element element, MimeTypes mimeTypes, ServiceLoader loader) throws TikaException, IOException {
            String name = element.getAttribute("class");
            String initProbHandler = element.getAttribute("initializableProblemHandler");
            InitializableProblemHandler initializableProblemHandler = initProbHandler == null || initProbHandler.length() == 0 ? loader.getInitializableProblemHandler() : TikaConfig.getInitializableProblemHandler(initProbHandler);
            T loaded = null;
            try {
                Class<T> loadedClass = loader.getServiceClass(this.getLoaderClass(), name);
                loaded = this.preLoadOne(loadedClass, name, mimeTypes);
                if (loaded != null) {
                    return loaded;
                }
                Map<String, Param> params = null;
                try {
                    params = this.getParams(element);
                }
                catch (Exception e) {
                    throw new TikaConfigException(e.getMessage(), e);
                }
                if (this.isComposite(loadedClass)) {
                    ArrayList<T> children = new ArrayList<T>();
                    NodeList childNodes = element.getElementsByTagName(this.getLoaderTagName());
                    if (childNodes.getLength() > 0) {
                        for (int i = 0; i < childNodes.getLength(); ++i) {
                            T loadedChild = this.loadOne((Element)childNodes.item(i), mimeTypes, loader);
                            if (loadedChild == null) continue;
                            children.add(loadedChild);
                        }
                    }
                    HashSet<Class<T>> excludeChildren = new HashSet<Class<T>>();
                    NodeList excludeChildNodes = element.getElementsByTagName(this.getLoaderTagName() + "-exclude");
                    if (excludeChildNodes.getLength() > 0) {
                        for (int i = 0; i < excludeChildNodes.getLength(); ++i) {
                            Element excl = (Element)excludeChildNodes.item(i);
                            String exclName = excl.getAttribute("class");
                            excludeChildren.add(loader.getServiceClass(this.getLoaderClass(), exclName));
                        }
                    }
                    if ((loaded = (T)this.createComposite(loadedClass, children, excludeChildren, params, mimeTypes, loader)) == null) {
                        loaded = this.newInstance(loadedClass);
                    }
                } else {
                    loaded = this.newInstance(loadedClass);
                }
                AnnotationUtils.assignFieldParams(loaded, params);
                if (loaded instanceof Initializable) {
                    ((Initializable)loaded).initialize(params);
                    ((Initializable)loaded).checkInitialization(initializableProblemHandler);
                }
                loaded = this.decorate(loaded, element);
                return loaded;
            }
            catch (ClassNotFoundException e) {
                if (loader.getLoadErrorHandler() == LoadErrorHandler.THROW) {
                    throw new TikaException("Unable to find a " + this.getLoaderTagName() + " class: " + name, e);
                }
                loader.getLoadErrorHandler().handleLoadError(name, e);
                return null;
            }
            catch (IllegalAccessException e) {
                throw new TikaException("Unable to access a " + this.getLoaderTagName() + " class: " + name, e);
            }
            catch (InvocationTargetException e) {
                throw new TikaException("Unable to create a " + this.getLoaderTagName() + " class: " + name, e);
            }
            catch (InstantiationException e) {
                throw new TikaException("Unable to instantiate a " + this.getLoaderTagName() + " class: " + name, e);
            }
            catch (NoSuchMethodException e) {
                throw new TikaException("Unable to find the right constructor for " + this.getLoaderTagName() + " class: " + name, e);
            }
        }

        T newInstance(Class<? extends T> loadedClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            return loadedClass.newInstance();
        }

        Map<String, Param> getParams(Element el) {
            HashMap<String, Param> params = new HashMap<String, Param>();
            for (Node child = el.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (!PARAMS_TAG_NAME.equals(child.getNodeName())) continue;
                if (!child.hasChildNodes()) break;
                NodeList childNodes = child.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node item = childNodes.item(i);
                    if (item.getNodeType() != 1) continue;
                    Param param = Param.load(item);
                    params.put(param.getName(), param);
                }
                break;
            }
            return params;
        }
    }
}


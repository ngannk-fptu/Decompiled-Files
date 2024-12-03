/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.util.DefaultPathMapper
 *  com.atlassian.plugin.servlet.util.PathMapper
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.johnson.config;

import com.atlassian.johnson.Initable;
import com.atlassian.johnson.config.ConfigurationJohnsonException;
import com.atlassian.johnson.config.JohnsonConfig;
import com.atlassian.johnson.event.ApplicationEventCheck;
import com.atlassian.johnson.event.EventCheck;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;
import com.atlassian.johnson.event.RequestEventCheck;
import com.atlassian.johnson.setup.ContainerFactory;
import com.atlassian.johnson.setup.DefaultContainerFactory;
import com.atlassian.johnson.setup.DefaultSetupConfig;
import com.atlassian.johnson.setup.SetupConfig;
import com.atlassian.johnson.util.StringUtils;
import com.atlassian.plugin.servlet.util.DefaultPathMapper;
import com.atlassian.plugin.servlet.util.PathMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XmlJohnsonConfig
implements JohnsonConfig {
    public static final String DEFAULT_CONFIGURATION_FILE = "johnson-config.xml";
    private static final Logger LOG = LoggerFactory.getLogger(XmlJohnsonConfig.class);
    private final List<ApplicationEventCheck> applicationEventChecks;
    private final ContainerFactory containerFactory;
    private final String errorPath;
    private final List<EventCheck> eventChecks;
    private final Map<Integer, EventCheck> eventChecksById;
    private final Map<String, EventLevel> eventLevels;
    private final Map<String, EventType> eventTypes;
    private final PathMapper ignoreMapper;
    private final List<String> ignorePaths;
    private final Map<String, String> params;
    private final List<RequestEventCheck> requestEventChecks;
    private final SetupConfig setupConfig;
    private final String setupPath;

    private XmlJohnsonConfig(SetupConfig setupConfig, ContainerFactory containerFactory, List<EventCheck> eventChecks, Map<Integer, EventCheck> eventChecksById, Map<String, EventLevel> eventLevels, Map<String, EventType> eventTypes, List<String> ignorePaths, Map<String, String> params, String setupPath, String errorPath) {
        this.containerFactory = containerFactory;
        this.errorPath = errorPath;
        this.eventChecks = eventChecks;
        this.eventChecksById = eventChecksById;
        this.eventLevels = eventLevels;
        this.eventTypes = eventTypes;
        this.ignorePaths = ignorePaths;
        this.params = params;
        this.setupConfig = setupConfig;
        this.setupPath = setupPath;
        ImmutableList.Builder applicationBuilder = ImmutableList.builder();
        ImmutableList.Builder requestBuilder = ImmutableList.builder();
        for (EventCheck eventCheck : eventChecks) {
            if (eventCheck instanceof ApplicationEventCheck) {
                applicationBuilder.add((Object)((ApplicationEventCheck)eventCheck));
            }
            if (!(eventCheck instanceof RequestEventCheck)) continue;
            requestBuilder.add((Object)((RequestEventCheck)eventCheck));
        }
        this.applicationEventChecks = applicationBuilder.build();
        this.requestEventChecks = requestBuilder.build();
        this.ignoreMapper = new DefaultPathMapper();
        this.ignoreMapper.put(errorPath, errorPath);
        this.ignoreMapper.put(setupPath, setupPath);
        for (String path : ignorePaths) {
            this.ignoreMapper.put(path, path);
        }
    }

    @Nonnull
    public static XmlJohnsonConfig fromDocument(@Nonnull Document document) {
        Element root = ((Document)Preconditions.checkNotNull((Object)document, (Object)"document")).getDocumentElement();
        SetupConfig setupConfig = XmlJohnsonConfig.configureClass(root, "setup-config", SetupConfig.class, DefaultSetupConfig.class);
        ContainerFactory containerFactory = XmlJohnsonConfig.configureClass(root, "container-factory", ContainerFactory.class, DefaultContainerFactory.class);
        Map<String, EventLevel> eventLevels = XmlJohnsonConfig.configureEventConstants(root, "event-levels", EventLevel.class);
        Map<String, EventType> eventTypes = XmlJohnsonConfig.configureEventConstants(root, "event-types", EventType.class);
        Map<String, String> params = XmlJohnsonConfig.configureParameters(root);
        String setupPath = (String)Iterables.getOnlyElement(XmlJohnsonConfig.configurePaths(root, "setup"));
        String errorPath = (String)Iterables.getOnlyElement(XmlJohnsonConfig.configurePaths(root, "error"));
        List<String> ignorePaths = XmlJohnsonConfig.configurePaths(root, "ignore");
        ElementIterable elements = XmlJohnsonConfig.getElementsByTagName(root, "event-checks");
        ArrayList<EventCheck> checks = new ArrayList<EventCheck>(elements.size());
        HashMap<Integer, EventCheck> checksById = new HashMap<Integer, EventCheck>(elements.size());
        if (!elements.isEmpty()) {
            elements = XmlJohnsonConfig.getElementsByTagName((Node)Iterables.getOnlyElement((Iterable)elements), "event-check");
            for (Element element : elements) {
                EventCheck check = XmlJohnsonConfig.parseEventCheck(element);
                checks.add(check);
                String id = element.getAttribute("id");
                if (StringUtils.isBlank(id)) continue;
                try {
                    if (checksById.put(Integer.parseInt(id), check) == null) continue;
                    throw new ConfigurationJohnsonException("EventCheck ID [" + id + "] is not unique");
                }
                catch (NumberFormatException e) {
                    throw new ConfigurationJohnsonException("EventCheck ID [" + id + "] is not a number", e);
                }
            }
        }
        return new XmlJohnsonConfig(setupConfig, containerFactory, (List<EventCheck>)ImmutableList.copyOf(checks), (Map<Integer, EventCheck>)ImmutableMap.copyOf(checksById), eventLevels, eventTypes, ignorePaths, params, setupPath, errorPath);
    }

    @Nonnull
    public static XmlJohnsonConfig fromFile(@Nonnull String fileName) {
        URL url = XmlJohnsonConfig.getResource((String)Preconditions.checkNotNull((Object)fileName, (Object)"fileName"), XmlJohnsonConfig.class);
        if (url != null) {
            LOG.debug("Loading {} from classpath at {}", (Object)fileName, (Object)url);
            fileName = url.toString();
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fileName);
            return XmlJohnsonConfig.fromDocument(document);
        }
        catch (IOException e) {
            throw new ConfigurationJohnsonException("Failed to parse [" + fileName + "]; the file could not be read", e);
        }
        catch (ParserConfigurationException e) {
            throw new ConfigurationJohnsonException("Failed to parse [" + fileName + "]; JVM configuration is invalid", e);
        }
        catch (SAXException e) {
            throw new ConfigurationJohnsonException("Failed to parse [" + fileName + "]; XML is not well-formed", e);
        }
    }

    @Override
    @Nonnull
    public List<ApplicationEventCheck> getApplicationEventChecks() {
        return this.applicationEventChecks;
    }

    @Override
    @Nonnull
    public ContainerFactory getContainerFactory() {
        return this.containerFactory;
    }

    @Override
    @Nonnull
    public String getErrorPath() {
        return this.errorPath;
    }

    @Override
    public EventCheck getEventCheck(int id) {
        return this.eventChecksById.get(id);
    }

    @Override
    @Nonnull
    public List<EventCheck> getEventChecks() {
        return this.eventChecks;
    }

    @Override
    public EventLevel getEventLevel(@Nonnull String level) {
        return this.eventLevels.get(Preconditions.checkNotNull((Object)level, (Object)"level"));
    }

    @Override
    public EventType getEventType(@Nonnull String type) {
        return this.eventTypes.get(Preconditions.checkNotNull((Object)type, (Object)"type"));
    }

    @Override
    @Nonnull
    public List<String> getIgnorePaths() {
        return this.ignorePaths;
    }

    @Override
    @Nonnull
    public Map<String, String> getParams() {
        return this.params;
    }

    @Override
    @Nonnull
    public List<RequestEventCheck> getRequestEventChecks() {
        return this.requestEventChecks;
    }

    @Override
    @Nonnull
    public SetupConfig getSetupConfig() {
        return this.setupConfig;
    }

    @Override
    @Nonnull
    public String getSetupPath() {
        return this.setupPath;
    }

    @Override
    public boolean isIgnoredPath(@Nonnull String uri) {
        return this.ignoreMapper.get((String)Preconditions.checkNotNull((Object)uri, (Object)"uri")) != null;
    }

    private static <T> Map<String, T> configureEventConstants(Element root, String tagName, Class<T> childClass) {
        Constructor<T> constructor;
        try {
            constructor = childClass.getConstructor(String.class, String.class);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class [" + childClass.getName() + "] requires a String, String constructor");
        }
        ElementIterable elements = XmlJohnsonConfig.getElementsByTagName(root, tagName);
        if (elements.isEmpty()) {
            return Collections.emptyMap();
        }
        elements = XmlJohnsonConfig.getElementsByTagName((Node)Iterables.getOnlyElement((Iterable)elements), tagName.substring(0, tagName.length() - 1));
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Element element : elements) {
            String key = element.getAttribute("key");
            String description = XmlJohnsonConfig.getContainedText(element, "description");
            try {
                builder.put((Object)key, constructor.newInstance(key, description));
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Constructor [" + constructor.getName() + "] must be public");
            }
            catch (InstantiationException e) {
                throw new IllegalArgumentException("Class [" + childClass.getName() + "] may not be abstract");
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new UndeclaredThrowableException(cause);
            }
        }
        return builder.build();
    }

    private static List<String> configurePaths(Element root, String tagname) {
        ElementIterable elements = XmlJohnsonConfig.getElementsByTagName(root, tagname);
        if (elements.isEmpty()) {
            return Collections.emptyList();
        }
        elements = XmlJohnsonConfig.getElementsByTagName((Node)Iterables.getOnlyElement((Iterable)elements), "path");
        return (List)StreamSupport.stream(elements.spliterator(), false).map(element -> (Text)element.getFirstChild()).map(CharacterData::getData).map(String::trim).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    private static Map<String, String> configureParameters(Element root) {
        NodeList list = root.getElementsByTagName("parameters");
        if (XmlJohnsonConfig.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Element element = (Element)list.item(0);
        return XmlJohnsonConfig.getInitParameters(element);
    }

    @Nonnull
    private static <T> T configureClass(Element root, String tagname, Class<T> expectedClass, Class<? extends T> defaultClass) {
        ElementIterable elements = XmlJohnsonConfig.getElementsByTagName(root, tagname);
        if (elements.isEmpty()) {
            try {
                return defaultClass.newInstance();
            }
            catch (Exception e) {
                throw new ConfigurationJohnsonException("Default [" + expectedClass.getName() + "], [" + defaultClass.getName() + "] is not valid", e);
            }
        }
        Element element = (Element)Iterables.getOnlyElement((Iterable)elements);
        String className = element.getAttribute("class");
        try {
            Class clazz = XmlJohnsonConfig.loadClass(className, XmlJohnsonConfig.class);
            if (!expectedClass.isAssignableFrom(clazz)) {
                throw new ConfigurationJohnsonException("The class specified by " + tagname + " (" + className + ") is required to implement [" + expectedClass.getName() + "]");
            }
            T instance = expectedClass.cast(clazz.newInstance());
            if (instance instanceof Initable) {
                Map<String, String> params = XmlJohnsonConfig.getInitParameters(element);
                ((Initable)instance).init(params);
            }
            return instance;
        }
        catch (Exception e) {
            throw new ConfigurationJohnsonException("Could not create: " + tagname, e);
        }
    }

    private static Map<String, String> getInitParameters(Element root) {
        ElementIterable elements = new ElementIterable(root.getElementsByTagName("init-param"));
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Element element : elements) {
            String paramName = XmlJohnsonConfig.getContainedText(element, "param-name");
            String paramValue = XmlJohnsonConfig.getContainedText(element, "param-value");
            builder.put((Object)paramName, (Object)paramValue);
        }
        return builder.build();
    }

    private static String getContainedText(Node parent, String childTagName) {
        try {
            Node tag = ((Element)parent).getElementsByTagName(childTagName).item(0);
            return ((Text)tag.getFirstChild()).getData();
        }
        catch (Exception e) {
            return null;
        }
    }

    private static ElementIterable getElementsByTagName(Node parent, String tagName) {
        Element element = (Element)parent;
        NodeList list = element.getElementsByTagName(tagName);
        if (XmlJohnsonConfig.isEmpty(list) && tagName.contains("-")) {
            list = element.getElementsByTagName(tagName.replace("-", ""));
        }
        return new ElementIterable(list);
    }

    @Nullable
    private static URL getResource(@Nonnull String resourceName, @Nonnull Class callingClass) {
        ClassLoader callingLoader;
        Objects.requireNonNull(resourceName, "resourceName");
        Objects.requireNonNull(callingClass, "callingClass");
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = XmlJohnsonConfig.class.getResource(resourceName);
        }
        if (url == null && (callingLoader = callingClass.getClassLoader()) != null) {
            url = callingLoader.getResource(resourceName);
        }
        if (url == null && resourceName.charAt(0) != '/') {
            return XmlJohnsonConfig.getResource("/" + resourceName, callingClass);
        }
        return url;
    }

    private static boolean isEmpty(NodeList list) {
        return list == null || list.getLength() == 0;
    }

    private static Class loadClass(@Nonnull String className, @Nonnull Class callingClass) throws ClassNotFoundException {
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(className, "className");
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException ignored) {
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException ignored2) {
                try {
                    return XmlJohnsonConfig.class.getClassLoader().loadClass(className);
                }
                catch (ClassNotFoundException ignored3) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
        }
    }

    private static EventCheck parseEventCheck(Element element) {
        Object o;
        String className = element.getAttribute("class");
        if (StringUtils.isBlank(className)) {
            throw new ConfigurationJohnsonException("event-check element with bad class attribute");
        }
        try {
            LOG.trace("Loading class [{}]", (Object)className);
            Class eventCheckClazz = XmlJohnsonConfig.loadClass(className, XmlJohnsonConfig.class);
            LOG.trace("Instantiating [{}]", (Object)className);
            o = eventCheckClazz.newInstance();
        }
        catch (ClassNotFoundException e) {
            LOG.error("Failed to load EventCheck class [" + className + "]", (Throwable)e);
            throw new ConfigurationJohnsonException("Could not load EventCheck: " + className, e);
        }
        catch (IllegalAccessException e) {
            LOG.error("Missing public nullary constructor for EventCheck class [" + className + "]", (Throwable)e);
            throw new ConfigurationJohnsonException("Could not instantiate EventCheck: " + className, e);
        }
        catch (InstantiationException e) {
            LOG.error("Could not instantiate EventCheck class [" + className + "]", (Throwable)e);
            throw new ConfigurationJohnsonException("Could not instantiate EventCheck: " + className, e);
        }
        if (!(o instanceof EventCheck)) {
            throw new ConfigurationJohnsonException(className + " does not implement EventCheck");
        }
        LOG.debug("Adding EventCheck of class: " + className);
        EventCheck eventCheck = (EventCheck)o;
        if (eventCheck instanceof Initable) {
            ((Initable)((Object)eventCheck)).init(XmlJohnsonConfig.getInitParameters(element));
        }
        return eventCheck;
    }

    private static class ElementIterable
    implements Iterable<Element> {
        private final NodeList list;

        private ElementIterable(NodeList list) {
            this.list = list;
        }

        @Override
        @Nonnull
        public Iterator<Element> iterator() {
            return new Iterator<Element>(){
                private int index;

                @Override
                public boolean hasNext() {
                    return this.index < list.getLength();
                }

                @Override
                public Element next() {
                    if (this.hasNext()) {
                        return (Element)list.item(this.index++);
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public boolean isEmpty() {
            return this.list == null || this.list.getLength() == 0;
        }

        public int size() {
            return this.list == null ? 0 : this.list.getLength();
        }
    }
}


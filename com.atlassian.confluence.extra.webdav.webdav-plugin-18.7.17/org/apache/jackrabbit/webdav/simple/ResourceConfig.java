/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.server.io.CopyMoveHandler;
import org.apache.jackrabbit.server.io.CopyMoveManager;
import org.apache.jackrabbit.server.io.CopyMoveManagerImpl;
import org.apache.jackrabbit.server.io.DefaultIOManager;
import org.apache.jackrabbit.server.io.DeleteManager;
import org.apache.jackrabbit.server.io.DeleteManagerImpl;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyManager;
import org.apache.jackrabbit.server.io.PropertyManagerImpl;
import org.apache.jackrabbit.webdav.simple.DefaultItemFilter;
import org.apache.jackrabbit.webdav.simple.ItemFilter;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.tika.detect.Detector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ResourceConfig {
    private static Logger log = LoggerFactory.getLogger(ResourceConfig.class);
    private static final String ELEMENT_IOMANAGER = "iomanager";
    private static final String ELEMENT_IOHANDLER = "iohandler";
    private static final String ELEMENT_PROPERTYMANAGER = "propertymanager";
    private static final String ELEMENT_PROPERTYHANDLER = "propertyhandler";
    private static final String ELEMENT_COPYMOVEMANAGER = "copymovemanager";
    private static final String ELEMENT_COPYMOVEHANDLER = "copymovehandler";
    private static final String ELEMENT_CLASS = "class";
    private static final String ELEMENT_PARAM = "param";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private final Detector detector;
    private ItemFilter itemFilter;
    private IOManager ioManager;
    private CopyMoveManager cmManager;
    private PropertyManager propManager;
    private DeleteManager deleteManager;
    private String[] nodetypeNames = new String[0];
    private boolean collectionNames = false;

    public ResourceConfig(Detector detector) {
        this.detector = detector;
    }

    public void parse(URL configURL) {
        try {
            this.parse(configURL.openStream());
        }
        catch (IOException e) {
            log.debug("Invalid resource configuration: " + e.getMessage());
        }
    }

    public void parse(InputStream stream) {
        try {
            Object handler;
            Element iohEl;
            ElementIterator iohElements;
            Object inst;
            Element config = DomUtil.parseDocument(stream).getDocumentElement();
            if (config == null) {
                log.warn("Mandatory 'config' element is missing.");
                return;
            }
            Element el = DomUtil.getChildElement(config, ELEMENT_IOMANAGER, null);
            if (el != null) {
                inst = ResourceConfig.buildClassFromConfig(el);
                if (inst != null && inst instanceof IOManager) {
                    this.ioManager = (IOManager)inst;
                    this.ioManager.setDetector(this.detector);
                    iohElements = DomUtil.getChildren(el, ELEMENT_IOHANDLER, null);
                    while (iohElements.hasNext()) {
                        iohEl = iohElements.nextElement();
                        inst = ResourceConfig.buildClassFromConfig(iohEl);
                        if (inst != null && inst instanceof IOHandler) {
                            handler = (IOHandler)inst;
                            ResourceConfig.setParameters(handler, iohEl);
                            this.ioManager.addIOHandler((IOHandler)handler);
                            continue;
                        }
                        log.warn("Not a valid IOHandler : " + ResourceConfig.getClassName(iohEl));
                    }
                } else {
                    log.warn("'iomanager' element does not define a valid IOManager.");
                }
            } else {
                log.warn("'iomanager' element is missing.");
            }
            el = DomUtil.getChildElement(config, ELEMENT_PROPERTYMANAGER, null);
            if (el != null) {
                inst = ResourceConfig.buildClassFromConfig(el);
                if (inst != null && inst instanceof PropertyManager) {
                    this.propManager = (PropertyManager)inst;
                    iohElements = DomUtil.getChildren(el, ELEMENT_PROPERTYHANDLER, null);
                    while (iohElements.hasNext()) {
                        iohEl = iohElements.nextElement();
                        inst = ResourceConfig.buildClassFromConfig(iohEl);
                        if (inst != null && inst instanceof PropertyHandler) {
                            handler = (PropertyHandler)inst;
                            ResourceConfig.setParameters(handler, iohEl);
                            this.propManager.addPropertyHandler((PropertyHandler)handler);
                            continue;
                        }
                        log.warn("Not a valid PropertyHandler : " + ResourceConfig.getClassName(iohEl));
                    }
                } else {
                    log.warn("'propertymanager' element does not define a valid PropertyManager.");
                }
            } else {
                log.debug("'propertymanager' element is missing.");
            }
            el = DomUtil.getChildElement(config, ELEMENT_COPYMOVEMANAGER, null);
            if (el != null) {
                inst = ResourceConfig.buildClassFromConfig(el);
                if (inst != null && inst instanceof CopyMoveManager) {
                    this.cmManager = (CopyMoveManager)inst;
                    iohElements = DomUtil.getChildren(el, ELEMENT_COPYMOVEHANDLER, null);
                    while (iohElements.hasNext()) {
                        iohEl = iohElements.nextElement();
                        inst = ResourceConfig.buildClassFromConfig(iohEl);
                        if (inst != null && inst instanceof CopyMoveHandler) {
                            handler = (CopyMoveHandler)inst;
                            ResourceConfig.setParameters(handler, iohEl);
                            this.cmManager.addCopyMoveHandler((CopyMoveHandler)handler);
                            continue;
                        }
                        log.warn("Not a valid CopyMoveHandler : " + ResourceConfig.getClassName(iohEl));
                    }
                } else {
                    log.warn("'copymovemanager' element does not define a valid CopyMoveManager.");
                }
            } else {
                log.debug("'copymovemanager' element is missing.");
            }
            el = DomUtil.getChildElement(config, "collection", null);
            if (el != null) {
                this.nodetypeNames = ResourceConfig.parseNodeTypesEntry(el);
                this.collectionNames = true;
            } else {
                el = DomUtil.getChildElement(config, "noncollection", null);
                if (el != null) {
                    this.nodetypeNames = ResourceConfig.parseNodeTypesEntry(el);
                    this.collectionNames = false;
                }
            }
            el = DomUtil.getChildElement(config, "filter", null);
            if (el != null) {
                inst = ResourceConfig.buildClassFromConfig(el);
                if (inst != null && inst instanceof ItemFilter) {
                    this.itemFilter = (ItemFilter)inst;
                }
                if (this.itemFilter != null) {
                    this.itemFilter.setFilteredNodetypes(ResourceConfig.parseNodeTypesEntry(el));
                    this.parseNamespacesEntry(el);
                }
            } else {
                log.debug("No 'filter' element specified.");
            }
            el = DomUtil.getChildElement(config, "mimetypeproperties", null);
            if (el != null) {
                log.warn("Ignoring deprecated mimetypeproperties settings");
            }
        }
        catch (IOException e) {
            log.debug("Invalid resource configuration: " + e.getMessage());
        }
        catch (ParserConfigurationException e) {
            log.warn("Failed to parse resource configuration: " + e.getMessage());
        }
        catch (SAXException e) {
            log.warn("Failed to parse resource configuration: " + e.getMessage());
        }
    }

    private void parseNamespacesEntry(Element parent) {
        Element namespaces = DomUtil.getChildElement(parent, "namespaces", null);
        if (namespaces != null) {
            ArrayList<String> l = new ArrayList<String>();
            ElementIterator it = DomUtil.getChildren(namespaces, "prefix", null);
            while (it.hasNext()) {
                Element e = it.nextElement();
                l.add(DomUtil.getText(e));
            }
            String[] prefixes = l.toArray(new String[l.size()]);
            l.clear();
            it = DomUtil.getChildren(namespaces, "uri", null);
            while (it.hasNext()) {
                Element e = it.nextElement();
                l.add(DomUtil.getText(e));
            }
            String[] uris = l.toArray(new String[l.size()]);
            this.itemFilter.setFilteredPrefixes(prefixes);
            this.itemFilter.setFilteredURIs(uris);
        }
    }

    private static String[] parseNodeTypesEntry(Element parent) {
        String[] ntNames;
        Element nodetypes = DomUtil.getChildElement(parent, "nodetypes", null);
        if (nodetypes != null) {
            ArrayList<String> l = new ArrayList<String>();
            ElementIterator it = DomUtil.getChildren(nodetypes, "nodetype", null);
            while (it.hasNext()) {
                Element e = it.nextElement();
                l.add(DomUtil.getText(e));
            }
            ntNames = l.toArray(new String[l.size()]);
        } else {
            ntNames = new String[]{};
        }
        return ntNames;
    }

    private static Object buildClassFromConfig(Element parent) {
        Object instance = null;
        Element classElem = DomUtil.getChildElement(parent, ELEMENT_CLASS, null);
        if (classElem != null) {
            try {
                String className = DomUtil.getAttribute(classElem, ATTR_NAME, null);
                if (className != null) {
                    Class<?> c = Class.forName(className);
                    instance = c.newInstance();
                } else {
                    log.error("Invalid configuration: missing 'class' element");
                }
            }
            catch (Exception e) {
                log.error("Error while create class instance: " + e.getMessage());
            }
        }
        return instance;
    }

    private static String getClassName(Element parent) {
        String className = null;
        Element classElem = DomUtil.getChildElement(parent, ELEMENT_CLASS, null);
        if (classElem != null) {
            className = DomUtil.getAttribute(classElem, ATTR_NAME, null);
        }
        return className == null ? "" : className;
    }

    private static void setParameters(Object instance, Element xmlElement) {
        Map<String, Method> setters;
        ElementIterator paramElems = DomUtil.getChildren(xmlElement, ELEMENT_PARAM, Namespace.EMPTY_NAMESPACE);
        if (paramElems.hasNext() && !(setters = ResourceConfig.getSetters(instance.getClass())).isEmpty()) {
            while (paramElems.hasNext()) {
                Element parameter = paramElems.next();
                String name = DomUtil.getAttribute(parameter, ATTR_NAME, null);
                String value = DomUtil.getAttribute(parameter, ATTR_VALUE, null);
                if (name == null || value == null) {
                    log.error("Parameter name or value missing -> ignore.");
                    continue;
                }
                Method setter = setters.get(name);
                if (setter == null) continue;
                Class<Object> type = setter.getParameterTypes()[0];
                try {
                    if (type.isAssignableFrom(String.class) || type.isAssignableFrom(Object.class)) {
                        setter.invoke(instance, value);
                        continue;
                    }
                    if (type.isAssignableFrom(Boolean.TYPE) || type.isAssignableFrom(Boolean.class)) {
                        setter.invoke(instance, Boolean.valueOf(value));
                        continue;
                    }
                    if (type.isAssignableFrom(Integer.TYPE) || type.isAssignableFrom(Integer.class)) {
                        setter.invoke(instance, Integer.valueOf(value));
                        continue;
                    }
                    if (type.isAssignableFrom(Long.TYPE) || type.isAssignableFrom(Long.class)) {
                        setter.invoke(instance, Long.valueOf(value));
                        continue;
                    }
                    if (type.isAssignableFrom(Double.TYPE) || type.isAssignableFrom(Double.class)) {
                        setter.invoke(instance, Double.valueOf(value));
                        continue;
                    }
                    log.error("Cannot set configuration property " + name);
                }
                catch (Exception e) {
                    log.error("Invalid format (" + value + ") for property " + name + " of class " + instance.getClass().getName(), (Throwable)e);
                }
            }
        }
    }

    private static Map<String, Method> getSetters(Class<?> cl) {
        HashMap<String, Method> methods = new HashMap<String, Method>();
        for (Method method : cl.getMethods()) {
            String name = method.getName();
            if (!name.startsWith("set") || name.length() <= 3 || !Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || !Void.TYPE.equals(method.getReturnType()) || method.getParameterTypes().length != 1) continue;
            methods.put(name.substring(3, 4).toLowerCase() + name.substring(4), method);
        }
        return methods;
    }

    public IOManager getIOManager() {
        if (this.ioManager == null) {
            log.debug("Missing io-manager > building DefaultIOManager ");
            this.ioManager = new DefaultIOManager();
            this.ioManager.setDetector(this.detector);
        }
        return this.ioManager;
    }

    public PropertyManager getPropertyManager() {
        if (this.propManager == null) {
            log.debug("Missing property-manager > building default.");
            this.propManager = PropertyManagerImpl.getDefaultManager();
        }
        return this.propManager;
    }

    public CopyMoveManager getCopyMoveManager() {
        if (this.cmManager == null) {
            log.debug("Missing copymove-manager > building default.");
            this.cmManager = CopyMoveManagerImpl.getDefaultManager();
        }
        return this.cmManager;
    }

    public DeleteManager getDeleteManager() {
        if (this.deleteManager == null) {
            log.debug("Missing delete-manager > building default.");
            this.deleteManager = DeleteManagerImpl.getDefaultManager();
        }
        return this.deleteManager;
    }

    public boolean isCollectionResource(Item item) {
        if (item.isNode()) {
            boolean isCollection = true;
            Node n = (Node)item;
            try {
                for (int i = 0; i < this.nodetypeNames.length && isCollection; ++i) {
                    isCollection = this.collectionNames ? n.isNodeType(this.nodetypeNames[i]) : !n.isNodeType(this.nodetypeNames[i]);
                }
            }
            catch (RepositoryException e) {
                log.warn(e.getMessage());
            }
            return isCollection;
        }
        return false;
    }

    public ItemFilter getItemFilter() {
        if (this.itemFilter == null) {
            log.debug("Missing resource filter > building DefaultItemFilter ");
            this.itemFilter = new DefaultItemFilter();
        }
        return this.itemFilter;
    }

    public Detector getDetector() {
        return this.detector;
    }
}


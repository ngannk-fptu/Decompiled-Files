/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.factory.DefaultFactory;
import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;
import com.opensymphony.module.sitemesh.mapper.PathMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ConfigLoader {
    private volatile State state;
    private File configFile = null;
    private String configFileName = null;
    private Config config = null;

    public ConfigLoader(File configFile) throws ServletException {
        this.configFile = configFile;
        this.configFileName = configFile.getName();
        this.state = this.loadConfig();
    }

    public ConfigLoader(String configFileName, Config config) throws ServletException {
        this.config = config;
        this.configFileName = configFileName;
        if (config.getServletContext().getRealPath(configFileName) != null) {
            this.configFile = new File(config.getServletContext().getRealPath(configFileName));
        }
        this.state = this.loadConfig();
    }

    public Decorator getDecoratorByName(String name) throws ServletException {
        return (Decorator)this.refresh().decorators.get(name);
    }

    public String getMappedName(String path) throws ServletException {
        return this.refresh().pathMapper.get(path);
    }

    private State loadConfig() throws ServletException {
        State newState = new State();
        try {
            Document document;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            if (this.configFile != null && this.configFile.canRead()) {
                newState.lastModified = this.configFile.lastModified();
                document = builder.parse(this.configFile);
            } else {
                document = builder.parse(this.config.getServletContext().getResourceAsStream(this.configFileName));
            }
            this.parseConfig(newState, document);
            return newState;
        }
        catch (ParserConfigurationException e) {
            throw new ServletException("Could not get XML parser", (Throwable)e);
        }
        catch (IOException e) {
            throw new ServletException("Could not read the config file: " + this.configFileName, (Throwable)e);
        }
        catch (SAXException e) {
            throw new ServletException("Could not parse the config file: " + this.configFileName, (Throwable)e);
        }
        catch (IllegalArgumentException e) {
            throw new ServletException("Could not find the config file: " + this.configFileName, (Throwable)e);
        }
    }

    private void parseConfig(State newState, Document document) {
        Element root = document.getDocumentElement();
        String defaultDir = ConfigLoader.getAttribute(root, "defaultdir");
        if (defaultDir == null) {
            defaultDir = ConfigLoader.getAttribute(root, "defaultDir");
        }
        NodeList decoratorNodes = root.getElementsByTagName("decorator");
        for (int i = 0; i < decoratorNodes.getLength(); ++i) {
            String page;
            String name;
            String uriPath = null;
            String role = null;
            Element decoratorElement = (Element)decoratorNodes.item(i);
            if (ConfigLoader.getAttribute(decoratorElement, "name") != null) {
                name = ConfigLoader.getAttribute(decoratorElement, "name");
                page = ConfigLoader.getAttribute(decoratorElement, "page");
                uriPath = ConfigLoader.getAttribute(decoratorElement, "webapp");
                role = ConfigLoader.getAttribute(decoratorElement, "role");
                if (defaultDir != null && page != null && page.length() > 0 && !page.startsWith("/")) {
                    page = page.charAt(0) == '/' ? defaultDir + page : defaultDir + '/' + page;
                }
                if (uriPath != null && uriPath.length() > 0 && uriPath.charAt(0) != '/') {
                    uriPath = '/' + uriPath;
                }
                this.populatePathMapper(newState, decoratorElement.getElementsByTagName("pattern"), role, name);
                this.populatePathMapper(newState, decoratorElement.getElementsByTagName("url-pattern"), role, name);
            } else {
                name = ConfigLoader.getContainedText(decoratorNodes.item(i), "decorator-name");
                page = ConfigLoader.getContainedText(decoratorNodes.item(i), "resource");
                if (page == null) {
                    page = ConfigLoader.getContainedText(decoratorNodes.item(i), "jsp-file");
                }
            }
            HashMap<String, String> params = new HashMap<String, String>();
            NodeList paramNodes = decoratorElement.getElementsByTagName("init-param");
            for (int ii = 0; ii < paramNodes.getLength(); ++ii) {
                String paramName = ConfigLoader.getContainedText(paramNodes.item(ii), "param-name");
                String paramValue = ConfigLoader.getContainedText(paramNodes.item(ii), "param-value");
                params.put(paramName, paramValue);
            }
            this.storeDecorator(newState, new DefaultDecorator(name, page, uriPath, role, params));
        }
        NodeList mappingNodes = root.getElementsByTagName("decorator-mapping");
        for (int i = 0; i < mappingNodes.getLength(); ++i) {
            Element n = (Element)mappingNodes.item(i);
            String name = ConfigLoader.getContainedText(mappingNodes.item(i), "decorator-name");
            this.populatePathMapper(newState, n.getElementsByTagName("url-pattern"), null, name);
        }
    }

    private void populatePathMapper(State newState, NodeList patternNodes, String role, String name) {
        for (int j = 0; j < patternNodes.getLength(); ++j) {
            String pattern;
            Element p = (Element)patternNodes.item(j);
            Text patternText = (Text)p.getFirstChild();
            if (patternText == null || (pattern = patternText.getData().trim()) == null) continue;
            if (role != null) {
                newState.pathMapper.put(name + role, pattern);
                continue;
            }
            newState.pathMapper.put(name, pattern);
        }
    }

    private static String getAttribute(Element element, String name) {
        if (element != null && element.getAttribute(name) != null && element.getAttribute(name).trim() != "") {
            return element.getAttribute(name).trim();
        }
        return null;
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

    private void storeDecorator(State newState, Decorator d) {
        if (d.getRole() != null) {
            newState.decorators.put(d.getName() + d.getRole(), d);
        } else {
            newState.decorators.put(d.getName(), d);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private State refresh() throws ServletException {
        long oldLastModified;
        State currentState = this.state;
        if (this.configFile == null) {
            return currentState;
        }
        long current = System.currentTimeMillis();
        boolean check = false;
        State state = currentState;
        synchronized (state) {
            oldLastModified = currentState.lastModified;
            if (!currentState.checking && current >= currentState.lastModificationCheck + DefaultFactory.configCheckMillis) {
                currentState.lastModificationCheck = current;
                currentState.checking = true;
                check = true;
            }
        }
        if (check) {
            State newState = null;
            try {
                long currentLastModified = this.configFile.lastModified();
                if (currentLastModified != oldLastModified) {
                    this.state = newState = this.loadConfig();
                    State state2 = newState;
                    return state2;
                }
            }
            finally {
                if (newState == null) {
                    State state3 = currentState;
                    synchronized (state3) {
                        currentState.checking = false;
                    }
                }
            }
        }
        return currentState;
    }

    private static class State {
        long lastModificationCheck = System.currentTimeMillis();
        long lastModified;
        boolean checking = false;
        Map decorators = new HashMap();
        PathMapper pathMapper = new PathMapper();

        private State() {
        }
    }
}


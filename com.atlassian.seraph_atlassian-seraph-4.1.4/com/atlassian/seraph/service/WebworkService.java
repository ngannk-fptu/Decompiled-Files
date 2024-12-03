/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.service;

import com.atlassian.seraph.SecurityService;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.util.CachedPathMapper;
import com.opensymphony.util.ClassLoaderUtil;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WebworkService
implements SecurityService {
    private static final Logger log = LoggerFactory.getLogger(WebworkService.class);
    private static final String ROLES_REQUIRED_ATTR = "roles-required";
    private static final String ACTION_EXTENSION_INIT_PARAM = "action.extension";
    private static final String ACTIONS_XML_FILE_INIT_PARAM = "actions.xml.file";
    private static final String ACTION_EXTENSION_DEFAULT = "action";
    private static final String ACTIONS_XML_FILE_DEFAULT = "actions";
    private final CachedPathMapper actionMapper = new CachedPathMapper();
    private final Map<String, String> rolesMap = new ConcurrentHashMap<String, String>();

    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
        try {
            String actionsXmlFile;
            String extension = params.get(ACTION_EXTENSION_INIT_PARAM);
            if (extension == null) {
                extension = ACTION_EXTENSION_DEFAULT;
            }
            if ((actionsXmlFile = params.get(ACTIONS_XML_FILE_INIT_PARAM)) == null) {
                actionsXmlFile = ACTIONS_XML_FILE_DEFAULT;
            }
            this.configureActionMapper(extension, actionsXmlFile);
        }
        catch (RuntimeException e) {
            log.error("Failed to initialise WebworkService", (Throwable)e);
        }
    }

    private void configureActionMapper(String extension, String actionsXmlFile) {
        Document doc = this.parseActionsXmlFile(actionsXmlFile);
        NodeList actions = doc.getElementsByTagName(ACTION_EXTENSION_DEFAULT);
        String rootRolesRequired = this.overrideRoles(null, doc.getDocumentElement());
        HashMap<String, String> pathMaps = new HashMap<String, String>();
        for (int i = 0; i < actions.getLength(); ++i) {
            Element action = (Element)actions.item(i);
            String actionName = action.getAttribute("name");
            String actionAlias = action.getAttribute("alias");
            String actionRolesRequired = this.overrideRoles(rootRolesRequired, action);
            if (actionRolesRequired != null) {
                if (actionAlias != null) {
                    pathMaps.put(actionAlias, "/" + actionAlias + "." + extension);
                    this.rolesMap.put(actionAlias, actionRolesRequired);
                    pathMaps.put(actionAlias + "!*", "/" + actionAlias + "!*." + extension);
                    this.rolesMap.put(actionAlias + "!*", actionRolesRequired);
                }
                if (actionName != null) {
                    pathMaps.put(actionName, "/" + actionName + "." + extension);
                    this.rolesMap.put(actionName, actionRolesRequired);
                    pathMaps.put(actionName + "!*", "/" + actionName + "!*." + extension);
                    this.rolesMap.put(actionName + "!*", actionRolesRequired);
                }
            }
            NodeList commands = action.getElementsByTagName("command");
            for (int j = 0; j < commands.getLength(); ++j) {
                Element command = (Element)commands.item(j);
                String cmdRolesRequired = this.overrideRoles(actionRolesRequired, command);
                String commandAlias = command.getAttribute("alias");
                if (commandAlias == null || cmdRolesRequired == null) continue;
                pathMaps.put(commandAlias, "/" + commandAlias + "." + extension);
                this.rolesMap.put(commandAlias, cmdRolesRequired);
            }
        }
        this.actionMapper.set(pathMaps);
    }

    private Document parseActionsXmlFile(String actionsXmlFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        URL fileUrl = ClassLoaderUtil.getResource((String)(actionsXmlFile + ".xml"), this.getClass());
        if (fileUrl == null) {
            fileUrl = ClassLoaderUtil.getResource((String)("/" + actionsXmlFile + ".xml"), this.getClass());
        }
        if (fileUrl == null) {
            throw new IllegalArgumentException("No such XML file:/" + actionsXmlFile + ".xml");
        }
        try {
            return factory.newDocumentBuilder().parse(fileUrl.toString());
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private String overrideRoles(String rolesRequired, Element action) {
        if (action.hasAttribute(ROLES_REQUIRED_ATTR)) {
            return action.getAttribute(ROLES_REQUIRED_ATTR);
        }
        return rolesRequired;
    }

    @Override
    public void destroy() {
    }

    @Override
    public Set<String> getRequiredRoles(HttpServletRequest request) {
        HashSet<String> requiredRoles = new HashSet<String>();
        String currentURL = request.getRequestURI();
        int lastSlash = currentURL.lastIndexOf(47);
        String targetURL = lastSlash > -1 ? currentURL.substring(lastSlash) : currentURL;
        String actionMatch = this.actionMapper.get(targetURL);
        if (actionMatch != null) {
            String rolesStr = this.rolesMap.get(actionMatch);
            StringTokenizer st = new StringTokenizer(rolesStr, ", ");
            while (st.hasMoreTokens()) {
                requiredRoles.add(st.nextToken());
            }
        }
        return Collections.unmodifiableSet(requiredRoles);
    }
}


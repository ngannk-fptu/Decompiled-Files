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
import com.atlassian.seraph.util.XMLUtils;
import com.opensymphony.util.ClassLoaderUtil;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PathService
implements SecurityService {
    private static final Logger log = LoggerFactory.getLogger(PathService.class);
    static String CONFIG_FILE_PARAM_KEY = "config.file";
    String configFileLocation = "seraph-paths.xml";
    private final CachedPathMapper pathMapper = new CachedPathMapper();
    private final Map<String, String[]> paths = new ConcurrentHashMap<String, String[]>();

    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
        if (params.get(CONFIG_FILE_PARAM_KEY) != null) {
            this.configFileLocation = params.get(CONFIG_FILE_PARAM_KEY);
        }
        this.configurePathMapper();
    }

    private void configurePathMapper() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            URL fileUrl = ClassLoaderUtil.getResource((String)this.configFileLocation, this.getClass());
            if (fileUrl == null) {
                fileUrl = ClassLoaderUtil.getResource((String)("/" + this.configFileLocation), this.getClass());
            }
            if (fileUrl == null) {
                return;
            }
            Document doc = factory.newDocumentBuilder().parse(fileUrl.toString());
            Element root = doc.getDocumentElement();
            NodeList pathNodes = root.getElementsByTagName("path");
            HashMap<String, String> pathMaps = new HashMap<String, String>();
            for (int i = 0; i < pathNodes.getLength(); ++i) {
                Element path = (Element)pathNodes.item(i);
                String pathName = path.getAttribute("name");
                String roleNames = XMLUtils.getContainedText(path, "role-name");
                String urlPattern = XMLUtils.getContainedText(path, "url-pattern");
                if (roleNames == null || urlPattern == null) continue;
                String[] rolesArr = this.parseRoles(roleNames);
                this.paths.put(pathName, rolesArr);
                pathMaps.put(pathName, urlPattern);
            }
            this.pathMapper.set(pathMaps);
        }
        catch (Exception ex) {
            log.error("Failed to configure pathMapper", (Throwable)ex);
        }
    }

    protected String[] parseRoles(String roleNames) {
        StringTokenizer st = new StringTokenizer(roleNames, ",; \t\n", false);
        String[] roles = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            roles[i] = st.nextToken();
            ++i;
        }
        return roles;
    }

    @Override
    public void destroy() {
    }

    @Override
    public Set<String> getRequiredRoles(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return this.getRequiredRoles(servletPath);
    }

    public Set<String> getRequiredRoles(String servletPath) {
        HashSet<String> requiredRoles = new HashSet<String>();
        Collection<String> constraintMatches = this.pathMapper.getAll(servletPath);
        if (constraintMatches == null) {
            throw new RuntimeException("No constraints matched for path " + servletPath);
        }
        for (String constraint : constraintMatches) {
            String[] rolesForPath = this.paths.get(constraint);
            for (int i = 0; i < rolesForPath.length; ++i) {
                if (requiredRoles.contains(rolesForPath[i])) continue;
                requiredRoles.add(rolesForPath[i]);
            }
        }
        return Collections.unmodifiableSet(requiredRoles);
    }
}


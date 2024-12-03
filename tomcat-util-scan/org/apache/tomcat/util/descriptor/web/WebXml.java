/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletContext
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.descriptor.JspPropertyGroupDescriptor
 *  javax.servlet.descriptor.TaglibDescriptor
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.Constants;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;
import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.MessageDestination;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.MultipartDef;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.descriptor.web.SecurityRoleRef;
import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.SessionConfig;
import org.apache.tomcat.util.descriptor.web.TaglibDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.XmlEncodingBase;
import org.apache.tomcat.util.digester.DocumentProperties;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public class WebXml
extends XmlEncodingBase
implements DocumentProperties.Charset {
    protected static final String ORDER_OTHERS = "org.apache.catalina.order.others";
    private static final StringManager sm = StringManager.getManager((String)Constants.PACKAGE_NAME);
    private final Log log = LogFactory.getLog(WebXml.class);
    private boolean overridable = false;
    private final List<String> duplicates = new ArrayList<String>();
    private Set<String> absoluteOrdering = null;
    private final Set<String> after = new LinkedHashSet<String>();
    private final Set<String> before = new LinkedHashSet<String>();
    private String publicId = null;
    private boolean metadataComplete = false;
    private String name = null;
    private int majorVersion = 4;
    private int minorVersion = 0;
    private String displayName = null;
    private boolean distributable = false;
    private boolean denyUncoveredHttpMethods = false;
    private final Map<String, String> contextParams = new HashMap<String, String>();
    private final Map<String, FilterDef> filters = new LinkedHashMap<String, FilterDef>();
    private final Set<FilterMap> filterMaps = new LinkedHashSet<FilterMap>();
    private final Set<String> filterMappingNames = new HashSet<String>();
    private final Set<String> listeners = new LinkedHashSet<String>();
    private final Map<String, ServletDef> servlets = new HashMap<String, ServletDef>();
    private final Map<String, String> servletMappings = new HashMap<String, String>();
    private final Set<String> servletMappingNames = new HashSet<String>();
    private SessionConfig sessionConfig = new SessionConfig();
    private final Map<String, String> mimeMappings = new HashMap<String, String>();
    private boolean replaceWelcomeFiles = false;
    private boolean alwaysAddWelcomeFiles = true;
    private final Set<String> welcomeFiles = new LinkedHashSet<String>();
    private final Map<String, ErrorPage> errorPages = new HashMap<String, ErrorPage>();
    private final Map<String, String> taglibs = new HashMap<String, String>();
    private final Set<JspPropertyGroup> jspPropertyGroups = new LinkedHashSet<JspPropertyGroup>();
    private final Set<SecurityConstraint> securityConstraints = new HashSet<SecurityConstraint>();
    private LoginConfig loginConfig = null;
    private final Set<String> securityRoles = new HashSet<String>();
    private final Map<String, ContextEnvironment> envEntries = new HashMap<String, ContextEnvironment>();
    private final Map<String, ContextEjb> ejbRefs = new HashMap<String, ContextEjb>();
    private final Map<String, ContextLocalEjb> ejbLocalRefs = new HashMap<String, ContextLocalEjb>();
    private final Map<String, ContextService> serviceRefs = new HashMap<String, ContextService>();
    private final Map<String, ContextResource> resourceRefs = new HashMap<String, ContextResource>();
    private final Map<String, ContextResourceEnvRef> resourceEnvRefs = new HashMap<String, ContextResourceEnvRef>();
    private final Map<String, MessageDestinationRef> messageDestinationRefs = new HashMap<String, MessageDestinationRef>();
    private final Map<String, MessageDestination> messageDestinations = new HashMap<String, MessageDestination>();
    private final Map<String, String> localeEncodingMappings = new HashMap<String, String>();
    private Map<String, String> postConstructMethods = new HashMap<String, String>();
    private Map<String, String> preDestroyMethods = new HashMap<String, String>();
    private String requestCharacterEncoding;
    private String responseCharacterEncoding;
    private URL uRL = null;
    private String jarName = null;
    private boolean webappJar = true;
    private boolean delegate = false;
    private static final String INDENT2 = "  ";
    private static final String INDENT4 = "    ";
    private static final String INDENT6 = "      ";

    public boolean isOverridable() {
        return this.overridable;
    }

    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }

    public boolean isDuplicated() {
        return !this.duplicates.isEmpty();
    }

    @Deprecated
    public void setDuplicated(boolean duplicated) {
        if (duplicated) {
            this.duplicates.add("unknown");
        } else {
            this.duplicates.clear();
        }
    }

    public void addDuplicate(String duplicate) {
        this.duplicates.add(duplicate);
    }

    public List<String> getDuplicates() {
        return new ArrayList<String>(this.duplicates);
    }

    public void createAbsoluteOrdering() {
        if (this.absoluteOrdering == null) {
            this.absoluteOrdering = new LinkedHashSet<String>();
        }
    }

    public void addAbsoluteOrdering(String fragmentName) {
        this.createAbsoluteOrdering();
        this.absoluteOrdering.add(fragmentName);
    }

    public void addAbsoluteOrderingOthers() {
        this.createAbsoluteOrdering();
        this.absoluteOrdering.add(ORDER_OTHERS);
    }

    public Set<String> getAbsoluteOrdering() {
        return this.absoluteOrdering;
    }

    public void addAfterOrdering(String fragmentName) {
        this.after.add(fragmentName);
    }

    public void addAfterOrderingOthers() {
        if (this.before.contains(ORDER_OTHERS)) {
            throw new IllegalArgumentException(sm.getString("webXml.multipleOther"));
        }
        this.after.add(ORDER_OTHERS);
    }

    public Set<String> getAfterOrdering() {
        return this.after;
    }

    public void addBeforeOrdering(String fragmentName) {
        this.before.add(fragmentName);
    }

    public void addBeforeOrderingOthers() {
        if (this.after.contains(ORDER_OTHERS)) {
            throw new IllegalArgumentException(sm.getString("webXml.multipleOther"));
        }
        this.before.add(ORDER_OTHERS);
    }

    public Set<String> getBeforeOrdering() {
        return this.before;
    }

    public String getVersion() {
        StringBuilder sb = new StringBuilder(3);
        sb.append(this.majorVersion);
        sb.append('.');
        sb.append(this.minorVersion);
        return sb.toString();
    }

    public void setVersion(String version) {
        if (version == null) {
            return;
        }
        switch (version) {
            case "2.4": {
                this.majorVersion = 2;
                this.minorVersion = 4;
                break;
            }
            case "2.5": {
                this.majorVersion = 2;
                this.minorVersion = 5;
                break;
            }
            case "3.0": {
                this.majorVersion = 3;
                this.minorVersion = 0;
                break;
            }
            case "3.1": {
                this.majorVersion = 3;
                this.minorVersion = 1;
                break;
            }
            case "4.0": {
                this.majorVersion = 4;
                this.minorVersion = 0;
                break;
            }
            default: {
                this.log.warn((Object)sm.getString("webXml.version.unknown", new Object[]{version}));
            }
        }
    }

    public String getPublicId() {
        return this.publicId;
    }

    public void setPublicId(String publicId) {
        if (publicId == null) {
            return;
        }
        switch (publicId) {
            case "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN": {
                this.majorVersion = 2;
                this.minorVersion = 2;
                this.publicId = publicId;
                break;
            }
            case "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN": {
                this.majorVersion = 2;
                this.minorVersion = 3;
                this.publicId = publicId;
                break;
            }
            default: {
                this.log.warn((Object)sm.getString("webXml.unrecognisedPublicId", new Object[]{publicId}));
            }
        }
    }

    public boolean isMetadataComplete() {
        return this.metadataComplete;
    }

    public void setMetadataComplete(boolean metadataComplete) {
        this.metadataComplete = metadataComplete;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (ORDER_OTHERS.equalsIgnoreCase(name)) {
            this.log.warn((Object)sm.getString("webXml.reservedName", new Object[]{name}));
        } else {
            this.name = name;
        }
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDistributable() {
        return this.distributable;
    }

    public void setDistributable(boolean distributable) {
        this.distributable = distributable;
    }

    public boolean getDenyUncoveredHttpMethods() {
        return this.denyUncoveredHttpMethods;
    }

    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
        this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
    }

    public void addContextParam(String param, String value) {
        this.contextParams.put(param, value);
    }

    public Map<String, String> getContextParams() {
        return this.contextParams;
    }

    public void addFilter(FilterDef filter) {
        if (this.filters.containsKey(filter.getFilterName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateFilter", new Object[]{filter.getFilterName()}));
        }
        this.filters.put(filter.getFilterName(), filter);
    }

    public Map<String, FilterDef> getFilters() {
        return this.filters;
    }

    public void addFilterMapping(FilterMap filterMap) {
        filterMap.setCharset(this.getCharset());
        this.filterMaps.add(filterMap);
        this.filterMappingNames.add(filterMap.getFilterName());
    }

    public Set<FilterMap> getFilterMappings() {
        return this.filterMaps;
    }

    public void addListener(String className) {
        this.listeners.add(className);
    }

    public Set<String> getListeners() {
        return this.listeners;
    }

    public void addServlet(ServletDef servletDef) {
        this.servlets.put(servletDef.getServletName(), servletDef);
        if (this.overridable) {
            servletDef.setOverridable(this.overridable);
        }
    }

    public Map<String, ServletDef> getServlets() {
        return this.servlets;
    }

    public void addServletMapping(String urlPattern, String servletName) {
        this.addServletMappingDecoded(UDecoder.URLDecode((String)urlPattern, (Charset)this.getCharset()), servletName);
    }

    public void addServletMappingDecoded(String urlPattern, String servletName) {
        String oldServletName = this.servletMappings.put(urlPattern, servletName);
        if (oldServletName != null) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateServletMapping", new Object[]{oldServletName, servletName, urlPattern}));
        }
        this.servletMappingNames.add(servletName);
    }

    public Map<String, String> getServletMappings() {
        return this.servletMappings;
    }

    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    public SessionConfig getSessionConfig() {
        return this.sessionConfig;
    }

    public void addMimeMapping(String extension, String mimeType) {
        this.mimeMappings.put(extension, mimeType);
    }

    public Map<String, String> getMimeMappings() {
        return this.mimeMappings;
    }

    public void setReplaceWelcomeFiles(boolean replaceWelcomeFiles) {
        this.replaceWelcomeFiles = replaceWelcomeFiles;
    }

    public void setAlwaysAddWelcomeFiles(boolean alwaysAddWelcomeFiles) {
        this.alwaysAddWelcomeFiles = alwaysAddWelcomeFiles;
    }

    public void addWelcomeFile(String welcomeFile) {
        if (this.replaceWelcomeFiles) {
            this.welcomeFiles.clear();
            this.replaceWelcomeFiles = false;
        }
        this.welcomeFiles.add(welcomeFile);
    }

    public Set<String> getWelcomeFiles() {
        return this.welcomeFiles;
    }

    public void addErrorPage(ErrorPage errorPage) {
        errorPage.setCharset(this.getCharset());
        this.errorPages.put(errorPage.getName(), errorPage);
    }

    public Map<String, ErrorPage> getErrorPages() {
        return this.errorPages;
    }

    public void addTaglib(String uri, String location) {
        if (this.taglibs.containsKey(uri)) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateTaglibUri", new Object[]{uri}));
        }
        this.taglibs.put(uri, location);
    }

    public Map<String, String> getTaglibs() {
        return this.taglibs;
    }

    public void addJspPropertyGroup(JspPropertyGroup propertyGroup) {
        propertyGroup.setCharset(this.getCharset());
        this.jspPropertyGroups.add(propertyGroup);
    }

    public Set<JspPropertyGroup> getJspPropertyGroups() {
        return this.jspPropertyGroups;
    }

    public void addSecurityConstraint(SecurityConstraint securityConstraint) {
        securityConstraint.setCharset(this.getCharset());
        this.securityConstraints.add(securityConstraint);
    }

    public Set<SecurityConstraint> getSecurityConstraints() {
        return this.securityConstraints;
    }

    public void setLoginConfig(LoginConfig loginConfig) {
        loginConfig.setCharset(this.getCharset());
        this.loginConfig = loginConfig;
    }

    public LoginConfig getLoginConfig() {
        return this.loginConfig;
    }

    public void addSecurityRole(String securityRole) {
        this.securityRoles.add(securityRole);
    }

    public Set<String> getSecurityRoles() {
        return this.securityRoles;
    }

    public void addEnvEntry(ContextEnvironment envEntry) {
        if (this.envEntries.containsKey(envEntry.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateEnvEntry", new Object[]{envEntry.getName()}));
        }
        this.envEntries.put(envEntry.getName(), envEntry);
    }

    public Map<String, ContextEnvironment> getEnvEntries() {
        return this.envEntries;
    }

    public void addEjbRef(ContextEjb ejbRef) {
        this.ejbRefs.put(ejbRef.getName(), ejbRef);
    }

    public Map<String, ContextEjb> getEjbRefs() {
        return this.ejbRefs;
    }

    public void addEjbLocalRef(ContextLocalEjb ejbLocalRef) {
        this.ejbLocalRefs.put(ejbLocalRef.getName(), ejbLocalRef);
    }

    public Map<String, ContextLocalEjb> getEjbLocalRefs() {
        return this.ejbLocalRefs;
    }

    public void addServiceRef(ContextService serviceRef) {
        this.serviceRefs.put(serviceRef.getName(), serviceRef);
    }

    public Map<String, ContextService> getServiceRefs() {
        return this.serviceRefs;
    }

    public void addResourceRef(ContextResource resourceRef) {
        if (this.resourceRefs.containsKey(resourceRef.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateResourceRef", new Object[]{resourceRef.getName()}));
        }
        this.resourceRefs.put(resourceRef.getName(), resourceRef);
    }

    public Map<String, ContextResource> getResourceRefs() {
        return this.resourceRefs;
    }

    public void addResourceEnvRef(ContextResourceEnvRef resourceEnvRef) {
        if (this.resourceEnvRefs.containsKey(resourceEnvRef.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateResourceEnvRef", new Object[]{resourceEnvRef.getName()}));
        }
        this.resourceEnvRefs.put(resourceEnvRef.getName(), resourceEnvRef);
    }

    public Map<String, ContextResourceEnvRef> getResourceEnvRefs() {
        return this.resourceEnvRefs;
    }

    public void addMessageDestinationRef(MessageDestinationRef messageDestinationRef) {
        if (this.messageDestinationRefs.containsKey(messageDestinationRef.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateMessageDestinationRef", new Object[]{messageDestinationRef.getName()}));
        }
        this.messageDestinationRefs.put(messageDestinationRef.getName(), messageDestinationRef);
    }

    public Map<String, MessageDestinationRef> getMessageDestinationRefs() {
        return this.messageDestinationRefs;
    }

    public void addMessageDestination(MessageDestination messageDestination) {
        if (this.messageDestinations.containsKey(messageDestination.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateMessageDestination", new Object[]{messageDestination.getName()}));
        }
        this.messageDestinations.put(messageDestination.getName(), messageDestination);
    }

    public Map<String, MessageDestination> getMessageDestinations() {
        return this.messageDestinations;
    }

    public void addLocaleEncodingMapping(String locale, String encoding) {
        this.localeEncodingMappings.put(locale, encoding);
    }

    public Map<String, String> getLocaleEncodingMappings() {
        return this.localeEncodingMappings;
    }

    public void addPostConstructMethods(String clazz, String method) {
        if (!this.postConstructMethods.containsKey(clazz)) {
            this.postConstructMethods.put(clazz, method);
        }
    }

    public Map<String, String> getPostConstructMethods() {
        return this.postConstructMethods;
    }

    public void addPreDestroyMethods(String clazz, String method) {
        if (!this.preDestroyMethods.containsKey(clazz)) {
            this.preDestroyMethods.put(clazz, method);
        }
    }

    public Map<String, String> getPreDestroyMethods() {
        return this.preDestroyMethods;
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        if (this.jspPropertyGroups.isEmpty() && this.taglibs.isEmpty()) {
            return null;
        }
        ArrayList<JspPropertyGroupDescriptor> descriptors = new ArrayList<JspPropertyGroupDescriptor>(this.jspPropertyGroups.size());
        for (JspPropertyGroup jspPropertyGroup : this.jspPropertyGroups) {
            JspPropertyGroupDescriptorImpl descriptor = new JspPropertyGroupDescriptorImpl(jspPropertyGroup);
            descriptors.add(descriptor);
        }
        HashSet<TaglibDescriptor> tlds = new HashSet<TaglibDescriptor>(this.taglibs.size());
        for (Map.Entry<String, String> entry : this.taglibs.entrySet()) {
            TaglibDescriptorImpl descriptor = new TaglibDescriptorImpl(entry.getValue(), entry.getKey());
            tlds.add(descriptor);
        }
        return new JspConfigDescriptorImpl(descriptors, tlds);
    }

    public String getRequestCharacterEncoding() {
        return this.requestCharacterEncoding;
    }

    public void setRequestCharacterEncoding(String requestCharacterEncoding) {
        if (requestCharacterEncoding != null) {
            try {
                B2CConverter.getCharset((String)requestCharacterEncoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
        this.requestCharacterEncoding = requestCharacterEncoding;
    }

    public String getResponseCharacterEncoding() {
        return this.responseCharacterEncoding;
    }

    public void setResponseCharacterEncoding(String responseCharacterEncoding) {
        if (responseCharacterEncoding != null) {
            try {
                B2CConverter.getCharset((String)responseCharacterEncoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
        this.responseCharacterEncoding = responseCharacterEncoding;
    }

    public void setURL(URL url) {
        this.uRL = url;
    }

    public URL getURL() {
        return this.uRL;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getJarName() {
        return this.jarName;
    }

    public void setWebappJar(boolean webappJar) {
        this.webappJar = webappJar;
    }

    public boolean getWebappJar() {
        return this.webappJar;
    }

    public boolean getDelegate() {
        return this.delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(32);
        buf.append("Name: ");
        buf.append(this.getName());
        buf.append(", URL: ");
        buf.append(this.getURL());
        return buf.toString();
    }

    /*
     * WARNING - void declaration
     */
    public String toXml() {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (this.publicId != null) {
            sb.append("<!DOCTYPE web-app PUBLIC\n");
            sb.append("  \"");
            sb.append(this.publicId);
            sb.append("\"\n");
            sb.append("  \"");
            if ("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN".equals(this.publicId)) {
                sb.append("http://java.sun.com/dtd/web-app_2_2.dtd");
            } else {
                sb.append("http://java.sun.com/dtd/web-app_2_3.dtd");
            }
            sb.append("\">\n");
            sb.append("<web-app>");
        } else {
            void var3_9;
            String javaeeNamespace = null;
            Object var3_3 = null;
            String version = this.getVersion();
            if ("2.4".equals(version)) {
                javaeeNamespace = "http://java.sun.com/xml/ns/j2ee";
                String string = "http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd";
            } else if ("2.5".equals(version)) {
                javaeeNamespace = "http://java.sun.com/xml/ns/javaee";
                String string = "http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd";
            } else if ("3.0".equals(version)) {
                javaeeNamespace = "http://java.sun.com/xml/ns/javaee";
                String string = "http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd";
            } else if ("3.1".equals(version)) {
                javaeeNamespace = "http://xmlns.jcp.org/xml/ns/javaee";
                String string = "http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd";
            } else if ("4.0".equals(version)) {
                javaeeNamespace = "http://xmlns.jcp.org/xml/ns/javaee";
                String string = "http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd";
            }
            sb.append("<web-app xmlns=\"");
            sb.append(javaeeNamespace);
            sb.append("\"\n");
            sb.append("         xmlns:xsi=");
            sb.append("\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            sb.append("         xsi:schemaLocation=\"");
            sb.append(javaeeNamespace);
            sb.append(' ');
            sb.append((String)var3_9);
            sb.append("\"\n");
            sb.append("         version=\"");
            sb.append(this.getVersion());
            sb.append("\"");
            if ("2.4".equals(version)) {
                sb.append(">\n\n");
            } else {
                sb.append("\n         metadata-complete=\"true\">\n\n");
            }
        }
        WebXml.appendElement(sb, INDENT2, "display-name", this.displayName);
        if (this.isDistributable()) {
            sb.append("  <distributable/>\n\n");
        }
        for (Map.Entry<String, String> entry : this.contextParams.entrySet()) {
            sb.append("  <context-param>\n");
            WebXml.appendElement(sb, INDENT4, "param-name", entry.getKey());
            WebXml.appendElement(sb, INDENT4, "param-value", entry.getValue());
            sb.append("  </context-param>\n");
        }
        sb.append('\n');
        if (this.getMajorVersion() > 2 || this.getMinorVersion() > 2) {
            for (Map.Entry<String, Object> entry : this.filters.entrySet()) {
                String[] filterDef = (String[])entry.getValue();
                sb.append("  <filter>\n");
                WebXml.appendElement(sb, INDENT4, "description", filterDef.getDescription());
                WebXml.appendElement(sb, INDENT4, "display-name", filterDef.getDisplayName());
                WebXml.appendElement(sb, INDENT4, "filter-name", filterDef.getFilterName());
                WebXml.appendElement(sb, INDENT4, "filter-class", filterDef.getFilterClass());
                if (this.getMajorVersion() != 2) {
                    WebXml.appendElement(sb, INDENT4, "async-supported", filterDef.getAsyncSupported());
                }
                for (Map.Entry<String, String> param : filterDef.getParameterMap().entrySet()) {
                    sb.append("    <init-param>\n");
                    WebXml.appendElement(sb, INDENT6, "param-name", param.getKey());
                    WebXml.appendElement(sb, INDENT6, "param-value", param.getValue());
                    sb.append("    </init-param>\n");
                }
                sb.append("  </filter>\n");
            }
            sb.append('\n');
            for (FilterMap filterMap : this.filterMaps) {
                sb.append("  <filter-mapping>\n");
                WebXml.appendElement(sb, INDENT4, "filter-name", filterMap.getFilterName());
                if (filterMap.getMatchAllServletNames()) {
                    sb.append("    <servlet-name>*</servlet-name>\n");
                } else {
                    for (String servletName : filterMap.getServletNames()) {
                        WebXml.appendElement(sb, INDENT4, "servlet-name", servletName);
                    }
                }
                if (filterMap.getMatchAllUrlPatterns()) {
                    sb.append("    <url-pattern>*</url-pattern>\n");
                } else {
                    for (String urlPattern : filterMap.getURLPatterns()) {
                        WebXml.appendElement(sb, INDENT4, "url-pattern", this.encodeUrl(urlPattern));
                    }
                }
                if (this.getMajorVersion() > 2 || this.getMinorVersion() > 3) {
                    for (String dispatcher : filterMap.getDispatcherNames()) {
                        if (this.getMajorVersion() == 2 && DispatcherType.ASYNC.name().equals(dispatcher)) continue;
                        WebXml.appendElement(sb, INDENT4, "dispatcher", dispatcher);
                    }
                }
                sb.append("  </filter-mapping>\n");
            }
            sb.append('\n');
        }
        if (this.getMajorVersion() > 2 || this.getMinorVersion() > 2) {
            for (String string : this.listeners) {
                sb.append("  <listener>\n");
                WebXml.appendElement(sb, INDENT4, "listener-class", string);
                sb.append("  </listener>\n");
            }
            sb.append('\n');
        }
        for (Map.Entry entry : this.servlets.entrySet()) {
            MultipartDef multipartDef;
            ServletDef servletDef = (ServletDef)entry.getValue();
            sb.append("  <servlet>\n");
            WebXml.appendElement(sb, INDENT4, "description", servletDef.getDescription());
            WebXml.appendElement(sb, INDENT4, "display-name", servletDef.getDisplayName());
            WebXml.appendElement(sb, INDENT4, "servlet-name", (String)entry.getKey());
            WebXml.appendElement(sb, INDENT4, "servlet-class", servletDef.getServletClass());
            WebXml.appendElement(sb, INDENT4, "jsp-file", servletDef.getJspFile());
            for (Map.Entry<String, String> param : servletDef.getParameterMap().entrySet()) {
                sb.append("    <init-param>\n");
                WebXml.appendElement(sb, INDENT6, "param-name", param.getKey());
                WebXml.appendElement(sb, INDENT6, "param-value", param.getValue());
                sb.append("    </init-param>\n");
            }
            WebXml.appendElement(sb, INDENT4, "load-on-startup", servletDef.getLoadOnStartup());
            WebXml.appendElement(sb, INDENT4, "enabled", servletDef.getEnabled());
            if (this.getMajorVersion() != 2) {
                WebXml.appendElement(sb, INDENT4, "async-supported", servletDef.getAsyncSupported());
            }
            if ((this.getMajorVersion() > 2 || this.getMinorVersion() > 2) && servletDef.getRunAs() != null) {
                sb.append("    <run-as>\n");
                WebXml.appendElement(sb, INDENT6, "role-name", servletDef.getRunAs());
                sb.append("    </run-as>\n");
            }
            for (SecurityRoleRef roleRef : servletDef.getSecurityRoleRefs()) {
                sb.append("    <security-role-ref>\n");
                WebXml.appendElement(sb, INDENT6, "role-name", roleRef.getName());
                WebXml.appendElement(sb, INDENT6, "role-link", roleRef.getLink());
                sb.append("    </security-role-ref>\n");
            }
            if (this.getMajorVersion() != 2 && (multipartDef = servletDef.getMultipartDef()) != null) {
                sb.append("    <multipart-config>\n");
                WebXml.appendElement(sb, INDENT6, "location", multipartDef.getLocation());
                WebXml.appendElement(sb, INDENT6, "max-file-size", multipartDef.getMaxFileSize());
                WebXml.appendElement(sb, INDENT6, "max-request-size", multipartDef.getMaxRequestSize());
                WebXml.appendElement(sb, INDENT6, "file-size-threshold", multipartDef.getFileSizeThreshold());
                sb.append("    </multipart-config>\n");
            }
            sb.append("  </servlet>\n");
        }
        sb.append('\n');
        for (Map.Entry entry : this.servletMappings.entrySet()) {
            sb.append("  <servlet-mapping>\n");
            WebXml.appendElement(sb, INDENT4, "servlet-name", (String)entry.getValue());
            WebXml.appendElement(sb, INDENT4, "url-pattern", this.encodeUrl((String)entry.getKey()));
            sb.append("  </servlet-mapping>\n");
        }
        sb.append('\n');
        if (this.sessionConfig != null) {
            sb.append("  <session-config>\n");
            WebXml.appendElement(sb, INDENT4, "session-timeout", this.sessionConfig.getSessionTimeout());
            if (this.majorVersion >= 3) {
                sb.append("    <cookie-config>\n");
                WebXml.appendElement(sb, INDENT6, "name", this.sessionConfig.getCookieName());
                WebXml.appendElement(sb, INDENT6, "domain", this.sessionConfig.getCookieDomain());
                WebXml.appendElement(sb, INDENT6, "path", this.sessionConfig.getCookiePath());
                WebXml.appendElement(sb, INDENT6, "comment", this.sessionConfig.getCookieComment());
                WebXml.appendElement(sb, INDENT6, "http-only", this.sessionConfig.getCookieHttpOnly());
                WebXml.appendElement(sb, INDENT6, "secure", this.sessionConfig.getCookieSecure());
                WebXml.appendElement(sb, INDENT6, "max-age", this.sessionConfig.getCookieMaxAge());
                sb.append("    </cookie-config>\n");
                for (SessionTrackingMode sessionTrackingMode : this.sessionConfig.getSessionTrackingModes()) {
                    WebXml.appendElement(sb, INDENT4, "tracking-mode", sessionTrackingMode.name());
                }
            }
            sb.append("  </session-config>\n\n");
        }
        for (Map.Entry entry : this.mimeMappings.entrySet()) {
            sb.append("  <mime-mapping>\n");
            WebXml.appendElement(sb, INDENT4, "extension", (String)entry.getKey());
            WebXml.appendElement(sb, INDENT4, "mime-type", (String)entry.getValue());
            sb.append("  </mime-mapping>\n");
        }
        sb.append('\n');
        if (this.welcomeFiles.size() > 0) {
            sb.append("  <welcome-file-list>\n");
            for (String string : this.welcomeFiles) {
                WebXml.appendElement(sb, INDENT4, "welcome-file", string);
            }
            sb.append("  </welcome-file-list>\n\n");
        }
        for (ErrorPage errorPage : this.errorPages.values()) {
            String exceptionType = errorPage.getExceptionType();
            int errorCode = errorPage.getErrorCode();
            if (exceptionType == null && errorCode == 0 && this.getMajorVersion() == 2) continue;
            sb.append("  <error-page>\n");
            if (errorPage.getExceptionType() != null) {
                WebXml.appendElement(sb, INDENT4, "exception-type", exceptionType);
            } else if (errorPage.getErrorCode() > 0) {
                WebXml.appendElement(sb, INDENT4, "error-code", Integer.toString(errorCode));
            }
            WebXml.appendElement(sb, INDENT4, "location", errorPage.getLocation());
            sb.append("  </error-page>\n");
        }
        sb.append('\n');
        if (this.taglibs.size() > 0 || this.jspPropertyGroups.size() > 0) {
            if (this.getMajorVersion() > 2 || this.getMinorVersion() > 3) {
                sb.append("  <jsp-config>\n");
            }
            for (Map.Entry entry : this.taglibs.entrySet()) {
                sb.append("    <taglib>\n");
                WebXml.appendElement(sb, INDENT6, "taglib-uri", (String)entry.getKey());
                WebXml.appendElement(sb, INDENT6, "taglib-location", (String)entry.getValue());
                sb.append("    </taglib>\n");
            }
            if (this.getMajorVersion() > 2 || this.getMinorVersion() > 3) {
                for (JspPropertyGroup jspPropertyGroup : this.jspPropertyGroups) {
                    sb.append("    <jsp-property-group>\n");
                    for (String urlPattern : jspPropertyGroup.getUrlPatterns()) {
                        WebXml.appendElement(sb, INDENT6, "url-pattern", this.encodeUrl(urlPattern));
                    }
                    WebXml.appendElement(sb, INDENT6, "el-ignored", jspPropertyGroup.getElIgnored());
                    WebXml.appendElement(sb, INDENT6, "page-encoding", jspPropertyGroup.getPageEncoding());
                    WebXml.appendElement(sb, INDENT6, "scripting-invalid", jspPropertyGroup.getScriptingInvalid());
                    WebXml.appendElement(sb, INDENT6, "is-xml", jspPropertyGroup.getIsXml());
                    for (String prelude : jspPropertyGroup.getIncludePreludes()) {
                        WebXml.appendElement(sb, INDENT6, "include-prelude", prelude);
                    }
                    for (String coda : jspPropertyGroup.getIncludeCodas()) {
                        WebXml.appendElement(sb, INDENT6, "include-coda", coda);
                    }
                    WebXml.appendElement(sb, INDENT6, "deferred-syntax-allowed-as-literal", jspPropertyGroup.getDeferredSyntax());
                    WebXml.appendElement(sb, INDENT6, "trim-directive-whitespaces", jspPropertyGroup.getTrimWhitespace());
                    WebXml.appendElement(sb, INDENT6, "default-content-type", jspPropertyGroup.getDefaultContentType());
                    WebXml.appendElement(sb, INDENT6, "buffer", jspPropertyGroup.getBuffer());
                    WebXml.appendElement(sb, INDENT6, "error-on-undeclared-namespace", jspPropertyGroup.getErrorOnUndeclaredNamespace());
                    sb.append("    </jsp-property-group>\n");
                }
                sb.append("  </jsp-config>\n\n");
            }
        }
        if (this.getMajorVersion() > 2 || this.getMinorVersion() > 2) {
            for (ContextResourceEnvRef contextResourceEnvRef : this.resourceEnvRefs.values()) {
                sb.append("  <resource-env-ref>\n");
                WebXml.appendElement(sb, INDENT4, "description", contextResourceEnvRef.getDescription());
                WebXml.appendElement(sb, INDENT4, "resource-env-ref-name", contextResourceEnvRef.getName());
                WebXml.appendElement(sb, INDENT4, "resource-env-ref-type", contextResourceEnvRef.getType());
                WebXml.appendElement(sb, INDENT4, "mapped-name", contextResourceEnvRef.getProperty("mappedName"));
                for (InjectionTarget target : contextResourceEnvRef.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    WebXml.appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                    WebXml.appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                WebXml.appendElement(sb, INDENT4, "lookup-name", contextResourceEnvRef.getLookupName());
                sb.append("  </resource-env-ref>\n");
            }
            sb.append('\n');
        }
        for (ContextResource contextResource : this.resourceRefs.values()) {
            sb.append("  <resource-ref>\n");
            WebXml.appendElement(sb, INDENT4, "description", contextResource.getDescription());
            WebXml.appendElement(sb, INDENT4, "res-ref-name", contextResource.getName());
            WebXml.appendElement(sb, INDENT4, "res-type", contextResource.getType());
            WebXml.appendElement(sb, INDENT4, "res-auth", contextResource.getAuth());
            if (this.getMajorVersion() > 2 || this.getMinorVersion() > 2) {
                WebXml.appendElement(sb, INDENT4, "res-sharing-scope", contextResource.getScope());
            }
            WebXml.appendElement(sb, INDENT4, "mapped-name", contextResource.getProperty("mappedName"));
            for (InjectionTarget target : contextResource.getInjectionTargets()) {
                sb.append("    <injection-target>\n");
                WebXml.appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                WebXml.appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                sb.append("    </injection-target>\n");
            }
            WebXml.appendElement(sb, INDENT4, "lookup-name", contextResource.getLookupName());
            sb.append("  </resource-ref>\n");
        }
        sb.append('\n');
        for (SecurityConstraint securityConstraint : this.securityConstraints) {
            sb.append("  <security-constraint>\n");
            if (this.getMajorVersion() > 2 || this.getMinorVersion() > 2) {
                WebXml.appendElement(sb, INDENT4, "display-name", securityConstraint.getDisplayName());
            }
            for (SecurityCollection collection : securityConstraint.findCollections()) {
                sb.append("    <web-resource-collection>\n");
                WebXml.appendElement(sb, INDENT6, "web-resource-name", collection.getName());
                WebXml.appendElement(sb, INDENT6, "description", collection.getDescription());
                for (String urlPattern : collection.findPatterns()) {
                    WebXml.appendElement(sb, INDENT6, "url-pattern", this.encodeUrl(urlPattern));
                }
                for (String method : collection.findMethods()) {
                    WebXml.appendElement(sb, INDENT6, "http-method", method);
                }
                for (String method : collection.findOmittedMethods()) {
                    WebXml.appendElement(sb, INDENT6, "http-method-omission", method);
                }
                sb.append("    </web-resource-collection>\n");
            }
            if (securityConstraint.findAuthRoles().length > 0) {
                sb.append("    <auth-constraint>\n");
                for (String role : securityConstraint.findAuthRoles()) {
                    WebXml.appendElement(sb, INDENT6, "role-name", role);
                }
                sb.append("    </auth-constraint>\n");
            }
            if (securityConstraint.getUserConstraint() != null) {
                sb.append("    <user-data-constraint>\n");
                WebXml.appendElement(sb, INDENT6, "transport-guarantee", securityConstraint.getUserConstraint());
                sb.append("    </user-data-constraint>\n");
            }
            sb.append("  </security-constraint>\n");
        }
        sb.append('\n');
        if (this.loginConfig != null) {
            sb.append("  <login-config>\n");
            WebXml.appendElement(sb, INDENT4, "auth-method", this.loginConfig.getAuthMethod());
            WebXml.appendElement(sb, INDENT4, "realm-name", this.loginConfig.getRealmName());
            if (this.loginConfig.getErrorPage() != null || this.loginConfig.getLoginPage() != null) {
                sb.append("    <form-login-config>\n");
                WebXml.appendElement(sb, INDENT6, "form-login-page", this.loginConfig.getLoginPage());
                WebXml.appendElement(sb, INDENT6, "form-error-page", this.loginConfig.getErrorPage());
                sb.append("    </form-login-config>\n");
            }
            sb.append("  </login-config>\n\n");
        }
        for (String string : this.securityRoles) {
            sb.append("  <security-role>\n");
            WebXml.appendElement(sb, INDENT4, "role-name", string);
            sb.append("  </security-role>\n");
        }
        for (ContextEnvironment contextEnvironment : this.envEntries.values()) {
            sb.append("  <env-entry>\n");
            WebXml.appendElement(sb, INDENT4, "description", contextEnvironment.getDescription());
            WebXml.appendElement(sb, INDENT4, "env-entry-name", contextEnvironment.getName());
            WebXml.appendElement(sb, INDENT4, "env-entry-type", contextEnvironment.getType());
            WebXml.appendElement(sb, INDENT4, "env-entry-value", contextEnvironment.getValue());
            WebXml.appendElement(sb, INDENT4, "mapped-name", contextEnvironment.getProperty("mappedName"));
            for (InjectionTarget target : contextEnvironment.getInjectionTargets()) {
                sb.append("    <injection-target>\n");
                WebXml.appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                WebXml.appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                sb.append("    </injection-target>\n");
            }
            WebXml.appendElement(sb, INDENT4, "lookup-name", contextEnvironment.getLookupName());
            sb.append("  </env-entry>\n");
        }
        sb.append('\n');
        for (ContextEjb contextEjb : this.ejbRefs.values()) {
            sb.append("  <ejb-ref>\n");
            WebXml.appendElement(sb, INDENT4, "description", contextEjb.getDescription());
            WebXml.appendElement(sb, INDENT4, "ejb-ref-name", contextEjb.getName());
            WebXml.appendElement(sb, INDENT4, "ejb-ref-type", contextEjb.getType());
            WebXml.appendElement(sb, INDENT4, "home", contextEjb.getHome());
            WebXml.appendElement(sb, INDENT4, "remote", contextEjb.getRemote());
            WebXml.appendElement(sb, INDENT4, "ejb-link", contextEjb.getLink());
            WebXml.appendElement(sb, INDENT4, "mapped-name", contextEjb.getProperty("mappedName"));
            for (InjectionTarget target : contextEjb.getInjectionTargets()) {
                sb.append("    <injection-target>\n");
                WebXml.appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                WebXml.appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                sb.append("    </injection-target>\n");
            }
            WebXml.appendElement(sb, INDENT4, "lookup-name", contextEjb.getLookupName());
            sb.append("  </ejb-ref>\n");
        }
        sb.append('\n');
        if (this.getMajorVersion() > 2 || this.getMinorVersion() > 2) {
            for (ContextLocalEjb contextLocalEjb : this.ejbLocalRefs.values()) {
                sb.append("  <ejb-local-ref>\n");
                WebXml.appendElement(sb, INDENT4, "description", contextLocalEjb.getDescription());
                WebXml.appendElement(sb, INDENT4, "ejb-ref-name", contextLocalEjb.getName());
                WebXml.appendElement(sb, INDENT4, "ejb-ref-type", contextLocalEjb.getType());
                WebXml.appendElement(sb, INDENT4, "local-home", contextLocalEjb.getHome());
                WebXml.appendElement(sb, INDENT4, "local", contextLocalEjb.getLocal());
                WebXml.appendElement(sb, INDENT4, "ejb-link", contextLocalEjb.getLink());
                WebXml.appendElement(sb, INDENT4, "mapped-name", contextLocalEjb.getProperty("mappedName"));
                for (InjectionTarget target : contextLocalEjb.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    WebXml.appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                    WebXml.appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                WebXml.appendElement(sb, INDENT4, "lookup-name", contextLocalEjb.getLookupName());
                sb.append("  </ejb-local-ref>\n");
            }
            sb.append('\n');
        }
        if (this.getMajorVersion() > 2 || this.getMinorVersion() > 3) {
            for (ContextService contextService : this.serviceRefs.values()) {
                sb.append("  <service-ref>\n");
                WebXml.appendElement(sb, INDENT4, "description", contextService.getDescription());
                WebXml.appendElement(sb, INDENT4, "display-name", contextService.getDisplayname());
                WebXml.appendElement(sb, INDENT4, "service-ref-name", contextService.getName());
                WebXml.appendElement(sb, INDENT4, "service-interface", contextService.getInterface());
                WebXml.appendElement(sb, INDENT4, "service-ref-type", contextService.getType());
                WebXml.appendElement(sb, INDENT4, "wsdl-file", contextService.getWsdlfile());
                WebXml.appendElement(sb, INDENT4, "jaxrpc-mapping-file", contextService.getJaxrpcmappingfile());
                String qname = contextService.getServiceqnameNamespaceURI();
                if (qname != null) {
                    qname = qname + ":";
                }
                qname = qname + contextService.getServiceqnameLocalpart();
                WebXml.appendElement(sb, INDENT4, "service-qname", qname);
                Iterator<String> endpointIter = contextService.getServiceendpoints();
                while (endpointIter.hasNext()) {
                    String endpoint = endpointIter.next();
                    sb.append("    <port-component-ref>\n");
                    WebXml.appendElement(sb, INDENT6, "service-endpoint-interface", endpoint);
                    WebXml.appendElement(sb, INDENT6, "port-component-link", contextService.getProperty(endpoint));
                    sb.append("    </port-component-ref>\n");
                }
                Iterator<String> handlerIter = contextService.getHandlers();
                while (handlerIter.hasNext()) {
                    String handler = handlerIter.next();
                    sb.append("    <handler>\n");
                    ContextHandler ch = contextService.getHandler(handler);
                    WebXml.appendElement(sb, INDENT6, "handler-name", ch.getName());
                    WebXml.appendElement(sb, INDENT6, "handler-class", ch.getHandlerclass());
                    sb.append("    </handler>\n");
                }
                WebXml.appendElement(sb, INDENT4, "mapped-name", contextService.getProperty("mappedName"));
                for (InjectionTarget target : contextService.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    WebXml.appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                    WebXml.appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                WebXml.appendElement(sb, INDENT4, "lookup-name", contextService.getLookupName());
                sb.append("  </service-ref>\n");
            }
            sb.append('\n');
        }
        if (!this.postConstructMethods.isEmpty()) {
            for (Map.Entry entry : this.postConstructMethods.entrySet()) {
                sb.append("  <post-construct>\n");
                WebXml.appendElement(sb, INDENT4, "lifecycle-callback-class", (String)entry.getKey());
                WebXml.appendElement(sb, INDENT4, "lifecycle-callback-method", (String)entry.getValue());
                sb.append("  </post-construct>\n");
            }
            sb.append('\n');
        }
        if (!this.preDestroyMethods.isEmpty()) {
            for (Map.Entry entry : this.preDestroyMethods.entrySet()) {
                sb.append("  <pre-destroy>\n");
                WebXml.appendElement(sb, INDENT4, "lifecycle-callback-class", (String)entry.getKey());
                WebXml.appendElement(sb, INDENT4, "lifecycle-callback-method", (String)entry.getValue());
                sb.append("  </pre-destroy>\n");
            }
            sb.append('\n');
        }
        if (this.getMajorVersion() > 2 || this.getMinorVersion() > 3) {
            for (MessageDestinationRef messageDestinationRef : this.messageDestinationRefs.values()) {
                sb.append("  <message-destination-ref>\n");
                WebXml.appendElement(sb, INDENT4, "description", messageDestinationRef.getDescription());
                WebXml.appendElement(sb, INDENT4, "message-destination-ref-name", messageDestinationRef.getName());
                WebXml.appendElement(sb, INDENT4, "message-destination-type", messageDestinationRef.getType());
                WebXml.appendElement(sb, INDENT4, "message-destination-usage", messageDestinationRef.getUsage());
                WebXml.appendElement(sb, INDENT4, "message-destination-link", messageDestinationRef.getLink());
                WebXml.appendElement(sb, INDENT4, "mapped-name", messageDestinationRef.getProperty("mappedName"));
                for (InjectionTarget target : messageDestinationRef.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    WebXml.appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                    WebXml.appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                WebXml.appendElement(sb, INDENT4, "lookup-name", messageDestinationRef.getLookupName());
                sb.append("  </message-destination-ref>\n");
            }
            sb.append('\n');
            for (MessageDestination messageDestination : this.messageDestinations.values()) {
                sb.append("  <message-destination>\n");
                WebXml.appendElement(sb, INDENT4, "description", messageDestination.getDescription());
                WebXml.appendElement(sb, INDENT4, "display-name", messageDestination.getDisplayName());
                WebXml.appendElement(sb, INDENT4, "message-destination-name", messageDestination.getName());
                WebXml.appendElement(sb, INDENT4, "mapped-name", messageDestination.getProperty("mappedName"));
                WebXml.appendElement(sb, INDENT4, "lookup-name", messageDestination.getLookupName());
                sb.append("  </message-destination>\n");
            }
            sb.append('\n');
        }
        if ((this.getMajorVersion() > 2 || this.getMinorVersion() > 3) && this.localeEncodingMappings.size() > 0) {
            sb.append("  <locale-encoding-mapping-list>\n");
            for (Map.Entry entry : this.localeEncodingMappings.entrySet()) {
                sb.append("    <locale-encoding-mapping>\n");
                WebXml.appendElement(sb, INDENT6, "locale", (String)entry.getKey());
                WebXml.appendElement(sb, INDENT6, "encoding", (String)entry.getValue());
                sb.append("    </locale-encoding-mapping>\n");
            }
            sb.append("  </locale-encoding-mapping-list>\n");
            sb.append("\n");
        }
        if ((this.getMajorVersion() > 3 || this.getMajorVersion() == 3 && this.getMinorVersion() > 0) && this.denyUncoveredHttpMethods) {
            sb.append("  <deny-uncovered-http-methods/>");
            sb.append("\n");
        }
        if (this.getMajorVersion() >= 4) {
            WebXml.appendElement(sb, INDENT2, "request-character-encoding", this.requestCharacterEncoding);
            WebXml.appendElement(sb, INDENT2, "response-character-encoding", this.responseCharacterEncoding);
        }
        sb.append("</web-app>");
        return sb.toString();
    }

    private String encodeUrl(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static void appendElement(StringBuilder sb, String indent, String elementName, String value) {
        if (value == null) {
            return;
        }
        if (value.length() == 0) {
            sb.append(indent);
            sb.append('<');
            sb.append(elementName);
            sb.append("/>\n");
        } else {
            sb.append(indent);
            sb.append('<');
            sb.append(elementName);
            sb.append('>');
            sb.append(Escape.xml((String)value));
            sb.append("</");
            sb.append(elementName);
            sb.append(">\n");
        }
    }

    private static void appendElement(StringBuilder sb, String indent, String elementName, Object value) {
        if (value == null) {
            return;
        }
        WebXml.appendElement(sb, indent, elementName, value.toString());
    }

    /*
     * WARNING - void declaration
     */
    public boolean merge(Set<WebXml> fragments) {
        WebXml temp = new WebXml();
        for (WebXml webXml : fragments) {
            if (this.mergeMap(webXml.getContextParams(), this.contextParams, temp.getContextParams(), webXml, "Context Parameter")) continue;
            return false;
        }
        this.contextParams.putAll(temp.getContextParams());
        if (this.displayName == null) {
            for (WebXml webXml : fragments) {
                String string = webXml.getDisplayName();
                if (string == null) continue;
                if (temp.getDisplayName() == null) {
                    temp.setDisplayName(string);
                    continue;
                }
                this.log.error((Object)sm.getString("webXml.mergeConflictDisplayName", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            this.displayName = temp.getDisplayName();
        }
        if (!this.denyUncoveredHttpMethods) {
            for (WebXml webXml : fragments) {
                if (!webXml.getDenyUncoveredHttpMethods()) continue;
                this.denyUncoveredHttpMethods = true;
                break;
            }
        }
        if (this.requestCharacterEncoding == null) {
            for (WebXml webXml : fragments) {
                if (webXml.getRequestCharacterEncoding() == null) continue;
                this.requestCharacterEncoding = webXml.getRequestCharacterEncoding();
            }
        }
        if (this.responseCharacterEncoding == null) {
            for (WebXml webXml : fragments) {
                if (webXml.getResponseCharacterEncoding() == null) continue;
                this.responseCharacterEncoding = webXml.getResponseCharacterEncoding();
            }
        }
        if (this.distributable) {
            for (WebXml webXml : fragments) {
                if (webXml.isDistributable()) continue;
                this.distributable = false;
                break;
            }
        }
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getEjbLocalRefs(), this.ejbLocalRefs, temp.getEjbLocalRefs(), webXml)) continue;
            return false;
        }
        this.ejbLocalRefs.putAll(temp.getEjbLocalRefs());
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getEjbRefs(), this.ejbRefs, temp.getEjbRefs(), webXml)) continue;
            return false;
        }
        this.ejbRefs.putAll(temp.getEjbRefs());
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getEnvEntries(), this.envEntries, temp.getEnvEntries(), webXml)) continue;
            return false;
        }
        this.envEntries.putAll(temp.getEnvEntries());
        for (WebXml webXml : fragments) {
            if (this.mergeMap(webXml.getErrorPages(), this.errorPages, temp.getErrorPages(), webXml, "Error Page")) continue;
            return false;
        }
        this.errorPages.putAll(temp.getErrorPages());
        ArrayList<FilterMap> filterMapsToAdd = new ArrayList<FilterMap>();
        for (WebXml webXml : fragments) {
            for (FilterMap filterMap : webXml.getFilterMappings()) {
                if (this.filterMappingNames.contains(filterMap.getFilterName())) continue;
                filterMapsToAdd.add(filterMap);
            }
        }
        for (FilterMap filterMap : filterMapsToAdd) {
            this.addFilterMapping(filterMap);
        }
        for (WebXml webXml : fragments) {
            for (Map.Entry<String, FilterDef> entry : webXml.getFilters().entrySet()) {
                if (this.filters.containsKey(entry.getKey())) {
                    WebXml.mergeFilter(entry.getValue(), this.filters.get(entry.getKey()), false);
                    continue;
                }
                if (temp.getFilters().containsKey(entry.getKey())) {
                    if (WebXml.mergeFilter(entry.getValue(), temp.getFilters().get(entry.getKey()), true)) continue;
                    this.log.error((Object)sm.getString("webXml.mergeConflictFilter", new Object[]{entry.getKey(), webXml.getName(), webXml.getURL()}));
                    return false;
                }
                temp.getFilters().put(entry.getKey(), entry.getValue());
            }
        }
        this.filters.putAll(temp.getFilters());
        for (WebXml webXml : fragments) {
            for (JspPropertyGroup jspPropertyGroup : webXml.getJspPropertyGroups()) {
                this.addJspPropertyGroup(jspPropertyGroup);
            }
        }
        for (WebXml webXml : fragments) {
            for (String string : webXml.getListeners()) {
                this.addListener(string);
            }
        }
        for (WebXml webXml : fragments) {
            if (this.mergeMap(webXml.getLocaleEncodingMappings(), this.localeEncodingMappings, temp.getLocaleEncodingMappings(), webXml, "Locale Encoding Mapping")) continue;
            return false;
        }
        this.localeEncodingMappings.putAll(temp.getLocaleEncodingMappings());
        if (this.getLoginConfig() == null) {
            void var4_30;
            Object var4_29 = null;
            for (WebXml webXml : fragments) {
                LoginConfig loginConfig = webXml.loginConfig;
                if (loginConfig == null) continue;
                if (var4_30 == null || loginConfig.equals(var4_30)) {
                    LoginConfig loginConfig2 = loginConfig;
                    continue;
                }
                this.log.error((Object)sm.getString("webXml.mergeConflictLoginConfig", new Object[]{webXml.getName(), webXml.getURL()}));
            }
            this.loginConfig = var4_30;
        }
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getMessageDestinationRefs(), this.messageDestinationRefs, temp.getMessageDestinationRefs(), webXml)) continue;
            return false;
        }
        this.messageDestinationRefs.putAll(temp.getMessageDestinationRefs());
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getMessageDestinations(), this.messageDestinations, temp.getMessageDestinations(), webXml)) continue;
            return false;
        }
        this.messageDestinations.putAll(temp.getMessageDestinations());
        for (WebXml webXml : fragments) {
            if (this.mergeMap(webXml.getMimeMappings(), this.mimeMappings, temp.getMimeMappings(), webXml, "Mime Mapping")) continue;
            return false;
        }
        this.mimeMappings.putAll(temp.getMimeMappings());
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getResourceEnvRefs(), this.resourceEnvRefs, temp.getResourceEnvRefs(), webXml)) continue;
            return false;
        }
        this.resourceEnvRefs.putAll(temp.getResourceEnvRefs());
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getResourceRefs(), this.resourceRefs, temp.getResourceRefs(), webXml)) continue;
            return false;
        }
        this.resourceRefs.putAll(temp.getResourceRefs());
        for (WebXml webXml : fragments) {
            for (SecurityConstraint securityConstraint : webXml.getSecurityConstraints()) {
                this.addSecurityConstraint(securityConstraint);
            }
        }
        for (WebXml webXml : fragments) {
            for (String string : webXml.getSecurityRoles()) {
                this.addSecurityRole(string);
            }
        }
        for (WebXml webXml : fragments) {
            if (this.mergeResourceMap(webXml.getServiceRefs(), this.serviceRefs, temp.getServiceRefs(), webXml)) continue;
            return false;
        }
        this.serviceRefs.putAll(temp.getServiceRefs());
        ArrayList<Map.Entry<String, String>> arrayList = new ArrayList<Map.Entry<String, String>>();
        for (WebXml webXml : fragments) {
            for (Map.Entry<String, String> entry : webXml.getServletMappings().entrySet()) {
                if (this.servletMappingNames.contains(entry.getValue()) || this.servletMappings.containsKey(entry.getKey())) continue;
                arrayList.add(entry);
            }
        }
        for (Map.Entry entry : arrayList) {
            this.addServletMappingDecoded((String)entry.getKey(), (String)entry.getValue());
        }
        for (WebXml webXml : fragments) {
            for (Map.Entry<String, ServletDef> entry : webXml.getServlets().entrySet()) {
                if (this.servlets.containsKey(entry.getKey())) {
                    WebXml.mergeServlet(entry.getValue(), this.servlets.get(entry.getKey()), false);
                    continue;
                }
                if (temp.getServlets().containsKey(entry.getKey())) {
                    if (WebXml.mergeServlet(entry.getValue(), temp.getServlets().get(entry.getKey()), true)) continue;
                    this.log.error((Object)sm.getString("webXml.mergeConflictServlet", new Object[]{entry.getKey(), webXml.getName(), webXml.getURL()}));
                    return false;
                }
                temp.getServlets().put(entry.getKey(), entry.getValue());
            }
        }
        this.servlets.putAll(temp.getServlets());
        if (this.sessionConfig.getSessionTimeout() == null) {
            for (WebXml webXml : fragments) {
                Integer n = webXml.getSessionConfig().getSessionTimeout();
                if (n == null) continue;
                if (temp.getSessionConfig().getSessionTimeout() == null) {
                    temp.getSessionConfig().setSessionTimeout(n.toString());
                    continue;
                }
                if (n.equals(temp.getSessionConfig().getSessionTimeout())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionTimeout", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            if (temp.getSessionConfig().getSessionTimeout() != null) {
                this.sessionConfig.setSessionTimeout(temp.getSessionConfig().getSessionTimeout().toString());
            }
        }
        if (this.sessionConfig.getCookieName() == null) {
            for (WebXml webXml : fragments) {
                String string = webXml.getSessionConfig().getCookieName();
                if (string == null) continue;
                if (temp.getSessionConfig().getCookieName() == null) {
                    temp.getSessionConfig().setCookieName(string);
                    continue;
                }
                if (string.equals(temp.getSessionConfig().getCookieName())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionCookieName", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            this.sessionConfig.setCookieName(temp.getSessionConfig().getCookieName());
        }
        if (this.sessionConfig.getCookieDomain() == null) {
            for (WebXml webXml : fragments) {
                String string = webXml.getSessionConfig().getCookieDomain();
                if (string == null) continue;
                if (temp.getSessionConfig().getCookieDomain() == null) {
                    temp.getSessionConfig().setCookieDomain(string);
                    continue;
                }
                if (string.equals(temp.getSessionConfig().getCookieDomain())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionCookieDomain", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            this.sessionConfig.setCookieDomain(temp.getSessionConfig().getCookieDomain());
        }
        if (this.sessionConfig.getCookiePath() == null) {
            for (WebXml webXml : fragments) {
                String string = webXml.getSessionConfig().getCookiePath();
                if (string == null) continue;
                if (temp.getSessionConfig().getCookiePath() == null) {
                    temp.getSessionConfig().setCookiePath(string);
                    continue;
                }
                if (string.equals(temp.getSessionConfig().getCookiePath())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionCookiePath", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            this.sessionConfig.setCookiePath(temp.getSessionConfig().getCookiePath());
        }
        if (this.sessionConfig.getCookieComment() == null) {
            for (WebXml webXml : fragments) {
                String string = webXml.getSessionConfig().getCookieComment();
                if (string == null) continue;
                if (temp.getSessionConfig().getCookieComment() == null) {
                    temp.getSessionConfig().setCookieComment(string);
                    continue;
                }
                if (string.equals(temp.getSessionConfig().getCookieComment())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionCookieComment", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            this.sessionConfig.setCookieComment(temp.getSessionConfig().getCookieComment());
        }
        if (this.sessionConfig.getCookieHttpOnly() == null) {
            for (WebXml webXml : fragments) {
                Boolean bl = webXml.getSessionConfig().getCookieHttpOnly();
                if (bl == null) continue;
                if (temp.getSessionConfig().getCookieHttpOnly() == null) {
                    temp.getSessionConfig().setCookieHttpOnly(bl.toString());
                    continue;
                }
                if (bl.equals(temp.getSessionConfig().getCookieHttpOnly())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionCookieHttpOnly", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            if (temp.getSessionConfig().getCookieHttpOnly() != null) {
                this.sessionConfig.setCookieHttpOnly(temp.getSessionConfig().getCookieHttpOnly().toString());
            }
        }
        if (this.sessionConfig.getCookieSecure() == null) {
            for (WebXml webXml : fragments) {
                Boolean bl = webXml.getSessionConfig().getCookieSecure();
                if (bl == null) continue;
                if (temp.getSessionConfig().getCookieSecure() == null) {
                    temp.getSessionConfig().setCookieSecure(bl.toString());
                    continue;
                }
                if (bl.equals(temp.getSessionConfig().getCookieSecure())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionCookieSecure", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            if (temp.getSessionConfig().getCookieSecure() != null) {
                this.sessionConfig.setCookieSecure(temp.getSessionConfig().getCookieSecure().toString());
            }
        }
        if (this.sessionConfig.getCookieMaxAge() == null) {
            for (WebXml webXml : fragments) {
                Integer n = webXml.getSessionConfig().getCookieMaxAge();
                if (n == null) continue;
                if (temp.getSessionConfig().getCookieMaxAge() == null) {
                    temp.getSessionConfig().setCookieMaxAge(n.toString());
                    continue;
                }
                if (n.equals(temp.getSessionConfig().getCookieMaxAge())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionCookieMaxAge", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            if (temp.getSessionConfig().getCookieMaxAge() != null) {
                this.sessionConfig.setCookieMaxAge(temp.getSessionConfig().getCookieMaxAge().toString());
            }
        }
        if (this.sessionConfig.getSessionTrackingModes().size() == 0) {
            for (WebXml webXml : fragments) {
                EnumSet<SessionTrackingMode> enumSet = webXml.getSessionConfig().getSessionTrackingModes();
                if (enumSet.size() <= 0) continue;
                if (temp.getSessionConfig().getSessionTrackingModes().size() == 0) {
                    temp.getSessionConfig().getSessionTrackingModes().addAll(enumSet);
                    continue;
                }
                if (enumSet.equals(temp.getSessionConfig().getSessionTrackingModes())) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictSessionTrackingMode", new Object[]{webXml.getName(), webXml.getURL()}));
                return false;
            }
            this.sessionConfig.getSessionTrackingModes().addAll(temp.getSessionConfig().getSessionTrackingModes());
        }
        for (WebXml webXml : fragments) {
            if (this.mergeMap(webXml.getTaglibs(), this.taglibs, temp.getTaglibs(), webXml, "Taglibs")) continue;
            return false;
        }
        this.taglibs.putAll(temp.getTaglibs());
        for (WebXml webXml : fragments) {
            if (!webXml.alwaysAddWelcomeFiles && this.welcomeFiles.size() != 0) continue;
            for (String string : webXml.getWelcomeFiles()) {
                this.addWelcomeFile(string);
            }
        }
        if (this.postConstructMethods.isEmpty()) {
            for (WebXml webXml : fragments) {
                if (this.mergeLifecycleCallback(webXml.getPostConstructMethods(), temp.getPostConstructMethods(), webXml, "Post Construct Methods")) continue;
                return false;
            }
            this.postConstructMethods.putAll(temp.getPostConstructMethods());
        }
        if (this.preDestroyMethods.isEmpty()) {
            for (WebXml webXml : fragments) {
                if (this.mergeLifecycleCallback(webXml.getPreDestroyMethods(), temp.getPreDestroyMethods(), webXml, "Pre Destroy Methods")) continue;
                return false;
            }
            this.preDestroyMethods.putAll(temp.getPreDestroyMethods());
        }
        return true;
    }

    private <T extends ResourceBase> boolean mergeResourceMap(Map<String, T> fragmentResources, Map<String, T> mainResources, Map<String, T> tempResources, WebXml fragment) {
        for (ResourceBase resource : fragmentResources.values()) {
            String resourceName = resource.getName();
            if (mainResources.containsKey(resourceName)) {
                ((ResourceBase)mainResources.get(resourceName)).getInjectionTargets().addAll(resource.getInjectionTargets());
                continue;
            }
            ResourceBase existingResource = (ResourceBase)tempResources.get(resourceName);
            if (existingResource != null) {
                if (existingResource.equals(resource)) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictResource", new Object[]{resourceName, fragment.getName(), fragment.getURL()}));
                return false;
            }
            tempResources.put(resourceName, resource);
        }
        return true;
    }

    private <T> boolean mergeMap(Map<String, T> fragmentMap, Map<String, T> mainMap, Map<String, T> tempMap, WebXml fragment, String mapName) {
        for (Map.Entry<String, T> entry : fragmentMap.entrySet()) {
            String key = entry.getKey();
            if (mainMap.containsKey(key)) continue;
            T value = entry.getValue();
            if (tempMap.containsKey(key)) {
                if (value == null || value.equals(tempMap.get(key))) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictString", new Object[]{mapName, key, fragment.getName(), fragment.getURL()}));
                return false;
            }
            tempMap.put(key, value);
        }
        return true;
    }

    private static boolean mergeFilter(FilterDef src, FilterDef dest, boolean failOnConflict) {
        if (dest.getAsyncSupported() == null) {
            dest.setAsyncSupported(src.getAsyncSupported());
        } else if (src.getAsyncSupported() != null && failOnConflict && !src.getAsyncSupported().equals(dest.getAsyncSupported())) {
            return false;
        }
        if (dest.getFilterClass() == null) {
            dest.setFilterClass(src.getFilterClass());
        } else if (src.getFilterClass() != null && failOnConflict && !src.getFilterClass().equals(dest.getFilterClass())) {
            return false;
        }
        for (Map.Entry<String, String> srcEntry : src.getParameterMap().entrySet()) {
            if (dest.getParameterMap().containsKey(srcEntry.getKey())) {
                if (!failOnConflict || dest.getParameterMap().get(srcEntry.getKey()).equals(srcEntry.getValue())) continue;
                return false;
            }
            dest.addInitParameter(srcEntry.getKey(), srcEntry.getValue());
        }
        return true;
    }

    private static boolean mergeServlet(ServletDef src, ServletDef dest, boolean failOnConflict) {
        if (dest.getServletClass() != null && dest.getJspFile() != null) {
            return false;
        }
        if (src.getServletClass() != null && src.getJspFile() != null) {
            return false;
        }
        if (dest.getServletClass() == null && dest.getJspFile() == null) {
            dest.setServletClass(src.getServletClass());
            dest.setJspFile(src.getJspFile());
        } else if (failOnConflict) {
            if (!(src.getServletClass() == null || dest.getJspFile() == null && src.getServletClass().equals(dest.getServletClass()))) {
                return false;
            }
            if (!(src.getJspFile() == null || dest.getServletClass() == null && src.getJspFile().equals(dest.getJspFile()))) {
                return false;
            }
        }
        for (SecurityRoleRef securityRoleRef : src.getSecurityRoleRefs()) {
            dest.addSecurityRoleRef(securityRoleRef);
        }
        if (dest.getLoadOnStartup() == null) {
            if (src.getLoadOnStartup() != null) {
                dest.setLoadOnStartup(src.getLoadOnStartup().toString());
            }
        } else if (src.getLoadOnStartup() != null && failOnConflict && !src.getLoadOnStartup().equals(dest.getLoadOnStartup())) {
            return false;
        }
        if (dest.getEnabled() == null) {
            if (src.getEnabled() != null) {
                dest.setEnabled(src.getEnabled().toString());
            }
        } else if (src.getEnabled() != null && failOnConflict && !src.getEnabled().equals(dest.getEnabled())) {
            return false;
        }
        for (Map.Entry entry : src.getParameterMap().entrySet()) {
            if (dest.getParameterMap().containsKey(entry.getKey())) {
                if (!failOnConflict || dest.getParameterMap().get(entry.getKey()).equals(entry.getValue())) continue;
                return false;
            }
            dest.addInitParameter((String)entry.getKey(), (String)entry.getValue());
        }
        if (dest.getMultipartDef() == null) {
            dest.setMultipartDef(src.getMultipartDef());
        } else if (src.getMultipartDef() != null) {
            return WebXml.mergeMultipartDef(src.getMultipartDef(), dest.getMultipartDef(), failOnConflict);
        }
        if (dest.getAsyncSupported() == null) {
            if (src.getAsyncSupported() != null) {
                dest.setAsyncSupported(src.getAsyncSupported().toString());
            }
        } else if (src.getAsyncSupported() != null && failOnConflict && !src.getAsyncSupported().equals(dest.getAsyncSupported())) {
            return false;
        }
        return true;
    }

    private static boolean mergeMultipartDef(MultipartDef src, MultipartDef dest, boolean failOnConflict) {
        if (dest.getLocation() == null) {
            dest.setLocation(src.getLocation());
        } else if (src.getLocation() != null && failOnConflict && !src.getLocation().equals(dest.getLocation())) {
            return false;
        }
        if (dest.getFileSizeThreshold() == null) {
            dest.setFileSizeThreshold(src.getFileSizeThreshold());
        } else if (src.getFileSizeThreshold() != null && failOnConflict && !src.getFileSizeThreshold().equals(dest.getFileSizeThreshold())) {
            return false;
        }
        if (dest.getMaxFileSize() == null) {
            dest.setMaxFileSize(src.getMaxFileSize());
        } else if (src.getMaxFileSize() != null && failOnConflict && !src.getMaxFileSize().equals(dest.getMaxFileSize())) {
            return false;
        }
        if (dest.getMaxRequestSize() == null) {
            dest.setMaxRequestSize(src.getMaxRequestSize());
        } else if (src.getMaxRequestSize() != null && failOnConflict && !src.getMaxRequestSize().equals(dest.getMaxRequestSize())) {
            return false;
        }
        return true;
    }

    private boolean mergeLifecycleCallback(Map<String, String> fragmentMap, Map<String, String> tempMap, WebXml fragment, String mapName) {
        for (Map.Entry<String, String> entry : fragmentMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (tempMap.containsKey(key)) {
                if (value == null || value.equals(tempMap.get(key))) continue;
                this.log.error((Object)sm.getString("webXml.mergeConflictString", new Object[]{mapName, key, fragment.getName(), fragment.getURL()}));
                return false;
            }
            tempMap.put(key, value);
        }
        return true;
    }

    public static Set<WebXml> orderWebFragments(WebXml application, Map<String, WebXml> fragments, ServletContext servletContext) {
        return application.orderWebFragments(fragments, servletContext);
    }

    private Set<WebXml> orderWebFragments(Map<String, WebXml> fragments, ServletContext servletContext) {
        LinkedHashSet<WebXml> orderedFragments = new LinkedHashSet<WebXml>();
        boolean absoluteOrdering = this.getAbsoluteOrdering() != null;
        boolean orderingPresent = false;
        if (absoluteOrdering) {
            orderingPresent = true;
            Iterator<WebXml> requestedOrder = this.getAbsoluteOrdering();
            Iterator iterator = requestedOrder.iterator();
            while (iterator.hasNext()) {
                String requestedName = (String)iterator.next();
                if (ORDER_OTHERS.equals(requestedName)) {
                    for (Map.Entry<String, WebXml> entry : fragments.entrySet()) {
                        WebXml fragment;
                        if (requestedOrder.contains(entry.getKey()) || (fragment = entry.getValue()) == null) continue;
                        orderedFragments.add(fragment);
                    }
                    continue;
                }
                WebXml fragment = fragments.get(requestedName);
                if (fragment != null) {
                    orderedFragments.add(fragment);
                    continue;
                }
                this.log.warn((Object)sm.getString("webXml.wrongFragmentName", new Object[]{requestedName}));
            }
        } else {
            for (WebXml fragment : fragments.values()) {
                if (!fragment.isDuplicated()) continue;
                List<String> duplicates = fragment.getDuplicates();
                duplicates.add(0, fragment.getURL().toString());
                throw new IllegalArgumentException(sm.getString("webXml.duplicateFragment", new Object[]{fragment.getName(), duplicates}));
            }
            for (WebXml fragment : fragments.values()) {
                Iterator<String> before = fragment.getBeforeOrdering().iterator();
                while (before.hasNext()) {
                    orderingPresent = true;
                    String beforeEntry = before.next();
                    if (beforeEntry.equals(ORDER_OTHERS)) continue;
                    WebXml beforeFragment = fragments.get(beforeEntry);
                    if (beforeFragment == null) {
                        before.remove();
                        continue;
                    }
                    beforeFragment.addAfterOrdering(fragment.getName());
                }
                Iterator<Object> after = fragment.getAfterOrdering().iterator();
                while (after.hasNext()) {
                    orderingPresent = true;
                    String afterEntry = after.next();
                    if (afterEntry.equals(ORDER_OTHERS)) continue;
                    WebXml afterFragment = fragments.get(afterEntry);
                    if (afterFragment == null) {
                        after.remove();
                        continue;
                    }
                    afterFragment.addBeforeOrdering(fragment.getName());
                }
            }
            for (WebXml fragment : fragments.values()) {
                if (fragment.getBeforeOrdering().contains(ORDER_OTHERS)) {
                    WebXml.makeBeforeOthersExplicit(fragment.getAfterOrdering(), fragments);
                }
                if (!fragment.getAfterOrdering().contains(ORDER_OTHERS)) continue;
                WebXml.makeAfterOthersExplicit(fragment.getBeforeOrdering(), fragments);
            }
            HashSet<WebXml> beforeSet = new HashSet<WebXml>();
            HashSet othersSet = new HashSet();
            HashSet<WebXml> afterSet = new HashSet<WebXml>();
            for (WebXml fragment : fragments.values()) {
                if (fragment.getBeforeOrdering().contains(ORDER_OTHERS)) {
                    beforeSet.add(fragment);
                    fragment.getBeforeOrdering().remove(ORDER_OTHERS);
                    continue;
                }
                if (fragment.getAfterOrdering().contains(ORDER_OTHERS)) {
                    afterSet.add(fragment);
                    fragment.getAfterOrdering().remove(ORDER_OTHERS);
                    continue;
                }
                othersSet.add(fragment);
            }
            WebXml.decoupleOtherGroups(beforeSet);
            WebXml.decoupleOtherGroups(othersSet);
            WebXml.decoupleOtherGroups(afterSet);
            WebXml.orderFragments(orderedFragments, beforeSet);
            WebXml.orderFragments(orderedFragments, othersSet);
            WebXml.orderFragments(orderedFragments, afterSet);
        }
        LinkedHashSet<WebXml> containerFragments = new LinkedHashSet<WebXml>();
        for (WebXml fragment : fragments.values()) {
            if (fragment.getWebappJar()) continue;
            containerFragments.add(fragment);
            orderedFragments.remove(fragment);
        }
        if (servletContext != null) {
            ArrayList<String> orderedJarFileNames = null;
            if (orderingPresent) {
                orderedJarFileNames = new ArrayList<String>();
                for (WebXml fragment : orderedFragments) {
                    orderedJarFileNames.add(fragment.getJarName());
                }
            }
            servletContext.setAttribute("javax.servlet.context.orderedLibs", orderedJarFileNames);
        }
        if (containerFragments.size() > 0) {
            LinkedHashSet<WebXml> result = new LinkedHashSet<WebXml>();
            if (((WebXml)containerFragments.iterator().next()).getDelegate()) {
                result.addAll(containerFragments);
                result.addAll(orderedFragments);
            } else {
                result.addAll(orderedFragments);
                result.addAll(containerFragments);
            }
            return result;
        }
        return orderedFragments;
    }

    private static void decoupleOtherGroups(Set<WebXml> group) {
        HashSet<String> names = new HashSet<String>();
        for (WebXml fragment : group) {
            names.add(fragment.getName());
        }
        for (WebXml fragment : group) {
            fragment.getAfterOrdering().removeIf(entry -> !names.contains(entry));
        }
    }

    private static void orderFragments(Set<WebXml> orderedFragments, Set<WebXml> unordered) {
        HashSet<WebXml> addedThisRound = new HashSet<WebXml>();
        HashSet<WebXml> addedLastRound = new HashSet<WebXml>();
        while (unordered.size() > 0) {
            Iterator<WebXml> source = unordered.iterator();
            while (source.hasNext()) {
                WebXml fragment = source.next();
                for (WebXml toRemove : addedLastRound) {
                    fragment.getAfterOrdering().remove(toRemove.getName());
                }
                if (!fragment.getAfterOrdering().isEmpty()) continue;
                addedThisRound.add(fragment);
                orderedFragments.add(fragment);
                source.remove();
            }
            if (addedThisRound.size() == 0) {
                throw new IllegalArgumentException(sm.getString("webXml.mergeConflictOrder"));
            }
            addedLastRound.clear();
            addedLastRound.addAll(addedThisRound);
            addedThisRound.clear();
        }
    }

    private static void makeBeforeOthersExplicit(Set<String> beforeOrdering, Map<String, WebXml> fragments) {
        for (String before : beforeOrdering) {
            WebXml webXml;
            if (before.equals(ORDER_OTHERS) || (webXml = fragments.get(before)).getBeforeOrdering().contains(ORDER_OTHERS)) continue;
            webXml.addBeforeOrderingOthers();
            WebXml.makeBeforeOthersExplicit(webXml.getAfterOrdering(), fragments);
        }
    }

    private static void makeAfterOthersExplicit(Set<String> afterOrdering, Map<String, WebXml> fragments) {
        for (String after : afterOrdering) {
            WebXml webXml;
            if (after.equals(ORDER_OTHERS) || (webXml = fragments.get(after)).getAfterOrdering().contains(ORDER_OTHERS)) continue;
            webXml.addAfterOrderingOthers();
            WebXml.makeAfterOthersExplicit(webXml.getBeforeOrdering(), fragments);
        }
    }
}


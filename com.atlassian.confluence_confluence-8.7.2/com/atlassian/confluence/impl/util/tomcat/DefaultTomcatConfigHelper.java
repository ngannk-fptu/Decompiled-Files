/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  com.google.common.base.Joiner
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.ReflectionUtils
 */
package com.atlassian.confluence.impl.util.tomcat;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.util.NumberUtil;
import com.atlassian.confluence.impl.util.OptionalUtils;
import com.atlassian.confluence.internal.util.reflection.ReflectionUtil;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.security.xml.SecureXmlParserFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Internal
public class DefaultTomcatConfigHelper
implements TomcatConfigHelper {
    private static final Logger log = LoggerFactory.getLogger(DefaultTomcatConfigHelper.class);
    private static final String CATALINA_HOME = System.getProperty("catalina.base", System.getProperty("catalina.home"));
    private static final String CONF = "conf";
    private static final String SERVER_XML = "server.xml";
    private static final String CONTEXT_XML = "context.xml";
    private static final String META_INF = "META-INF";
    private static final String WEBAPPS = "webapps";
    private static final String[] XML_FILE_LIST = new String[]{"server.xml", "context.xml"};
    static final QueryExp PROTOCOL_BASED_ON_HTTP = Query.or(Query.initialSubString(Query.attr("protocol"), Query.value("HTTP")), Query.eq(Query.attr("protocol"), Query.value("org.apache.coyote.http11.Http11Nio2Protocol")));
    private final MBeanServer mBeanServer;

    public DefaultTomcatConfigHelper(MBeanServer mBeanServer) {
        this.mBeanServer = Objects.requireNonNull(mBeanServer);
    }

    public Optional<Integer> getProxyPort() {
        File serverXmlFile = DefaultTomcatConfigHelper.fromCatalinaHome(CONF, SERVER_XML);
        String proxyPort = Strings.emptyToNull((String)DefaultTomcatConfigHelper.getAttributeFromXmlFile(serverXmlFile, "proxyPort", "//Connector[@proxyName]"));
        if (proxyPort == null) {
            log.info("No proxyPort is found in {}", (Object)serverXmlFile.getAbsolutePath());
        } else {
            log.info("proxyPort {} is found in {}", (Object)proxyPort, (Object)serverXmlFile.getAbsolutePath());
        }
        log.info("Checking if Tomcat is running behind a reverse proxy in {}...", (Object)serverXmlFile.getAbsolutePath());
        return Optional.ofNullable(proxyPort).flatMap(NumberUtil::parseInteger);
    }

    public Optional<String> getConnectorPort() {
        File serverXmlFile = DefaultTomcatConfigHelper.fromCatalinaHome(CONF, SERVER_XML);
        String connectorPort = Strings.emptyToNull((String)DefaultTomcatConfigHelper.getAttributeFromXmlFile(serverXmlFile, "port", "//Connector[@port]"));
        log.info("Tomcat connector port: ", (Object)connectorPort);
        return Optional.ofNullable(connectorPort);
    }

    public List<File> getPotentialDatasourceLocations() {
        ArrayList<File> locations = new ArrayList<File>();
        for (String fileName : XML_FILE_LIST) {
            locations.add(DefaultTomcatConfigHelper.fromCatalinaHome(CONF, fileName));
        }
        String docBase = DefaultTomcatConfigHelper.getConfluenceDocBase();
        if (StringUtils.isNotBlank((CharSequence)docBase)) {
            locations.add(DefaultTomcatConfigHelper.fromCatalinaHome(docBase, META_INF, CONTEXT_XML));
            locations.add(DefaultTomcatConfigHelper.fromCatalinaHome(WEBAPPS, docBase, META_INF, CONTEXT_XML));
        }
        return locations;
    }

    public Optional<Pair<String, String>> getDatasourceCredentials() {
        return this.getPotentialDatasourceLocations().stream().map(DefaultTomcatConfigHelper::getCredentialsFromFile).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    private static String getConfluenceDocBase() {
        File serverXmlFile = DefaultTomcatConfigHelper.fromCatalinaHome(CONF, SERVER_XML);
        return DefaultTomcatConfigHelper.getAttributeFromXmlFile(serverXmlFile, "docBase", "//Engine/Host/Context[@path='']").replace("../", "");
    }

    private static Optional<Pair<String, String>> getCredentialsFromFile(File xmlFile) {
        log.debug("Searching for JNDI Datasource credentials in [{}]", (Object)xmlFile.getAbsolutePath());
        String username = DefaultTomcatConfigHelper.getAttributeFromXmlFile(xmlFile, "username", "//Resource[@type='javax.sql.DataSource' and @username]");
        String password = DefaultTomcatConfigHelper.getAttributeFromXmlFile(xmlFile, "password", "//Resource[@type='javax.sql.DataSource' and @password]");
        if (StringUtils.isNotBlank((CharSequence)username)) {
            log.debug("Found JNDI Datasource credentials in [{}]", (Object)xmlFile.getAbsolutePath());
            return Optional.of(ImmutablePair.of((Object)username, (Object)StringUtils.defaultString((String)password)));
        }
        return Optional.empty();
    }

    public Optional<String> getDatasourceUrl(File xmlFile) {
        log.debug("Searching for JNDI Datasource url in [{}]", (Object)xmlFile.getAbsolutePath());
        String url = DefaultTomcatConfigHelper.getAttributeFromXmlFile(xmlFile, "url", "//Resource[@type='javax.sql.DataSource' and @url]");
        if (StringUtils.isNotBlank((CharSequence)url)) {
            log.debug("Found JNDI Datasource url in [{}]", (Object)xmlFile.getAbsolutePath());
            return Optional.of(url);
        }
        return Optional.empty();
    }

    private static String getAttributeFromXmlFile(File xmlFile, String attributeName, String expression) {
        log.debug("Retrieving attribute '{}' with expression '{}' in file [{}]", new Object[]{attributeName, expression, xmlFile.getAbsolutePath()});
        if (!xmlFile.exists()) {
            log.debug("Failed to retrieve attribute '{}'. The file [{}] does not exist.", (Object)attributeName, (Object)xmlFile.getAbsolutePath());
            return "";
        }
        try {
            Node resource;
            Element element;
            String attributeValue;
            DocumentBuilderFactory factory = SecureXmlParserFactory.newDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            XPathFactory xPathfactory = XPathFactory.newInstance("http://java.sun.com/jaxp/xpath/dom", "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl", ClassLoader.getSystemClassLoader());
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(expression);
            NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
            if (nodeList.getLength() == 0) {
                log.debug("Failed to retrieve attribute '{}'. No node found for the xpath expression '{}' in file [{}]", new Object[]{attributeName, expression, xmlFile.getAbsolutePath()});
                return "";
            }
            if (nodeList.getLength() > 1) {
                log.debug("Found multiple matches for attribute '{}' with expression '{}' in file [{}]. Using only the first match.", new Object[]{attributeName, expression, xmlFile.getAbsolutePath()});
            }
            if (StringUtils.startsWith((CharSequence)(attributeValue = (element = (Element)(resource = nodeList.item(0))).getAttribute(attributeName)), (CharSequence)"${") && StringUtils.endsWith((CharSequence)attributeValue, (CharSequence)"}")) {
                String substitutionKey = attributeValue.replace("${", "").replace("}", "");
                String substitutionValue = System.getProperty(substitutionKey);
                if (StringUtils.isNotBlank((CharSequence)substitutionValue)) {
                    log.debug("Performing System property substitution '{}'->'{}' for attribute '{}' in file [{}]", new Object[]{attributeValue, substitutionKey.contains("password") ? "*******" : substitutionValue, attributeName, xmlFile.getAbsolutePath()});
                    return substitutionValue;
                }
                log.debug("System property '{}' not found for attribute '{}' with value '{}' in file [{}]", new Object[]{substitutionKey, attributeName, attributeValue, xmlFile.getAbsolutePath()});
            }
            return attributeValue;
        }
        catch (Exception e) {
            log.warn("Failed to retrieve the attribute '{}' using expression '{}' from file [{}] : {}", new Object[]{attributeName, expression, xmlFile.getAbsolutePath(), e.getMessage()});
            log.debug("", (Throwable)e);
            return "";
        }
    }

    private static File fromCatalinaHome(String ... segment) {
        return new File(CATALINA_HOME, Joiner.on((char)File.separatorChar).join((Object[])segment));
    }

    public String getJavaRuntimeDirectory() {
        String javaRuntimeDirectory = System.getProperty("java.home");
        if (StringUtils.isBlank((CharSequence)javaRuntimeDirectory)) {
            javaRuntimeDirectory = StringUtils.defaultString((String)System.getenv("JRE_HOME"), (String)System.getenv("JAVA_HOME"));
        }
        if (StringUtils.isBlank((CharSequence)javaRuntimeDirectory)) {
            throw new Error("Both JRE_HOME and JAVA_HOME are not defined!");
        }
        return javaRuntimeDirectory;
    }

    public boolean isStandardPort(int port) {
        return port == 80 || port == 443;
    }

    public List<Optional<Integer>> getAllMaxHttpThreads() {
        return this.getHttpConnectors().stream().map(connector -> OptionalUtils.first(Optional.of(connector), this::getMaxExecutorThreads, this::getMaxHttpThreads)).collect(Collectors.toList());
    }

    @Deprecated
    public Optional<Integer> getMaxHttpThreads() {
        return OptionalUtils.first(this.getFirstHttpConnector(), this::getMaxExecutorThreads, this::getMaxHttpThreads);
    }

    private Optional<ObjectName> getFirstHttpConnector() {
        try {
            Set<ObjectName> httpConnectors = this.mBeanServer.queryNames(new ObjectName("*:type=Connector,*"), PROTOCOL_BASED_ON_HTTP);
            if (httpConnectors.size() == 1) {
                return httpConnectors.stream().findFirst();
            }
            log.warn("Expected exactly one HTTP connector in Tomcat configuration, but found {}", httpConnectors);
            return Optional.empty();
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalStateException(e);
        }
    }

    private Collection<ObjectName> getHttpConnectors() {
        try {
            return this.mBeanServer.queryNames(new ObjectName("*:type=Connector,*"), PROTOCOL_BASED_ON_HTTP);
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<Integer> getMaxHttpThreads(ObjectName objectName) {
        Optional<Integer> maxThreadsAttribute = this.getAttribute("maxThreads", objectName);
        return maxThreadsAttribute.filter(maxThreads -> maxThreads > 0);
    }

    private Optional<Integer> getMaxExecutorThreads(ObjectName httpConnector) {
        return this.getAttribute("executor", httpConnector).flatMap(executor -> OptionalUtils.zip(ReflectionUtil.findMethod(executor.getClass(), "getMaxThreads"), Optional.of(executor))).map(methodAndExecutor -> (Integer)ReflectionUtils.invokeMethod((Method)((Method)methodAndExecutor.left()), (Object)methodAndExecutor.right()));
    }

    private <T> Optional<T> getAttribute(String attributeName, ObjectName objectName) {
        try {
            return Optional.ofNullable(this.mBeanServer.getAttribute(objectName, attributeName));
        }
        catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            log.warn(String.format("Could not read %s attribute of %s", attributeName, objectName), (Throwable)e);
            return Optional.empty();
        }
    }
}


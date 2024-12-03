/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractXmlConfigBuilder;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.internal.util.XmlUtil;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class AbstractXmlConfigHelper {
    private static final ILogger LOGGER = Logger.getLogger(AbstractXmlConfigHelper.class);
    protected boolean domLevel3 = true;
    final String xmlns = "http://www.hazelcast.com/schema/" + this.getNamespaceType();
    private final String hazelcastSchemaLocation;

    public AbstractXmlConfigHelper() {
        this.hazelcastSchemaLocation = this.getConfigType().name + "-config-" + this.getReleaseVersion() + ".xsd";
    }

    public String getNamespaceType() {
        return this.getConfigType().name.equals("hazelcast") ? "config" : "client-config";
    }

    protected AbstractXmlConfigBuilder.ConfigType getConfigType() {
        return AbstractXmlConfigBuilder.ConfigType.SERVER;
    }

    protected void schemaValidation(Document doc) throws Exception {
        String[] xsdLocations;
        ArrayList<StreamSource> schemas = new ArrayList<StreamSource>();
        InputStream inputStream = null;
        String schemaLocation = doc.getDocumentElement().getAttribute("xsi:schemaLocation");
        schemaLocation = schemaLocation.replaceAll("^ +| +$| (?= )", "");
        for (String xsdLocation : xsdLocations = schemaLocation.split("(?<!\\G\\S+)\\s")) {
            if (xsdLocation.isEmpty()) continue;
            String namespace = xsdLocation.split('[' + StringUtil.LINE_SEPARATOR + " ]+")[0];
            String uri = xsdLocation.split('[' + StringUtil.LINE_SEPARATOR + " ]+")[1];
            if (namespace.equals(this.xmlns) && !uri.endsWith(this.hazelcastSchemaLocation)) {
                LOGGER.warning("Name of the hazelcast schema location is incorrect, using default");
            }
            if (namespace.equals(this.xmlns)) continue;
            inputStream = this.loadSchemaFile(uri);
            schemas.add(new StreamSource(inputStream));
        }
        schemas.add(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(this.hazelcastSchemaLocation)));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DOMSource xmlSource = new DOMSource(doc);
        StreamResult outputTarget = new StreamResult(outputStream);
        TransformerFactory transformerFactory = XmlUtil.getTransformerFactory();
        transformerFactory.newTransformer().transform(xmlSource, outputTarget);
        ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        SchemaFactory schemaFactory = XmlUtil.getSchemaFactory();
        Schema schema = schemaFactory.newSchema(schemas.toArray(new Source[0]));
        Validator validator = schema.newValidator();
        try {
            SAXSource source = new SAXSource(new InputSource(is));
            validator.validate(source);
        }
        catch (Exception e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
        finally {
            for (StreamSource source : schemas) {
                IOUtil.closeResource(source.getInputStream());
            }
            IOUtil.closeResource(inputStream);
        }
    }

    protected InputStream loadSchemaFile(String schemaLocation) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaLocation);
        if (inputStream == null) {
            try {
                inputStream = new URL(schemaLocation).openStream();
            }
            catch (Exception e) {
                throw new InvalidConfigurationException("Your xsd schema couldn't be loaded");
            }
        }
        return inputStream;
    }

    protected String getReleaseVersion() {
        BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();
        String[] versionTokens = StringUtil.tokenizeVersionString(buildInfo.getVersion());
        return versionTokens[0] + "." + versionTokens[1];
    }

    protected String xmlToJavaName(String name) {
        String javaRefName = this.xmlRefToJavaName(name);
        if (javaRefName != null) {
            return javaRefName;
        }
        StringBuilder builder = new StringBuilder();
        char[] charArray = name.toCharArray();
        boolean dash = false;
        StringBuilder token = new StringBuilder();
        for (char aCharArray : charArray) {
            if (aCharArray == '-') {
                this.appendToken(builder, token);
                dash = true;
                continue;
            }
            token.append(dash ? Character.toUpperCase(aCharArray) : aCharArray);
            dash = false;
        }
        this.appendToken(builder, token);
        return builder.toString();
    }

    private String xmlRefToJavaName(String name) {
        if (name.equals("quorum-ref")) {
            return "quorumName";
        }
        return null;
    }

    protected void appendToken(StringBuilder builder, StringBuilder token) {
        String string = token.toString();
        if ("Jvm".equals(string)) {
            string = "JVM";
        }
        builder.append(string);
        token.setLength(0);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.hibernate.boot.xsd.XsdDescriptor;
import org.hibernate.internal.util.xml.XsdException;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

public class LocalXsdResolver {
    public static String latestJpaVerison() {
        return "2.2";
    }

    public static boolean isValidJpaVersion(String version) {
        switch (version) {
            case "1.0": 
            case "2.0": 
            case "2.1": 
            case "2.2": 
            case "3.0": {
                return true;
            }
        }
        return false;
    }

    public static URL resolveLocalXsdUrl(String resourceName) {
        URL url2;
        try {
            url2 = LocalXsdResolver.class.getClassLoader().getResource(resourceName);
            if (url2 != null) {
                return url2;
            }
        }
        catch (Exception url2) {
            // empty catch block
        }
        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
            try {
                url2 = LocalXsdResolver.class.getClassLoader().getResource(resourceName);
                if (url2 != null) {
                    return url2;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        try {
            return new URL(resourceName);
        }
        catch (Exception exception) {
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Schema resolveLocalXsdSchema(String schemaResourceName) {
        URL url = LocalXsdResolver.resolveLocalXsdUrl(schemaResourceName);
        if (url == null) {
            throw new XsdException("Unable to locate schema [" + schemaResourceName + "] via classpath", schemaResourceName);
        }
        try {
            InputStream schemaStream = url.openStream();
            try {
                StreamSource source = new StreamSource(url.openStream());
                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                Schema schema = schemaFactory.newSchema(source);
                return schema;
            }
            catch (IOException | SAXException e) {
                throw new XsdException("Unable to load schema [" + schemaResourceName + "]", e, schemaResourceName);
            }
            finally {
                try {
                    schemaStream.close();
                }
                catch (IOException e) {
                    Logger.getLogger(LocalXsdResolver.class).debugf("Problem closing schema stream [%s]", (Object)e.toString());
                }
            }
        }
        catch (IOException e) {
            throw new XsdException("Stream error handling schema url [" + url.toExternalForm() + "]", schemaResourceName);
        }
    }

    public static XsdDescriptor buildXsdDescriptor(String resourceName, String version, String namespaceUri) {
        return new XsdDescriptor(resourceName, LocalXsdResolver.resolveLocalXsdSchema(resourceName), version, namespaceUri);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.jaxb.internal.stax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.hibernate.boot.jaxb.internal.stax.XmlInfrastructureException;
import org.jboss.logging.Logger;

public class LocalSchemaLocator {
    private static final Logger log = Logger.getLogger(LocalSchemaLocator.class);

    private LocalSchemaLocator() {
    }

    public static URL resolveLocalSchemaUrl(String schemaResourceName) {
        URL url = LocalSchemaLocator.class.getClassLoader().getResource(schemaResourceName);
        if (url == null) {
            throw new XmlInfrastructureException("Unable to locate schema [" + schemaResourceName + "] via classpath");
        }
        return url;
    }

    public static Schema resolveLocalSchema(String schemaName) {
        return LocalSchemaLocator.resolveLocalSchema(LocalSchemaLocator.resolveLocalSchemaUrl(schemaName));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Schema resolveLocalSchema(URL schemaUrl) {
        try {
            InputStream schemaStream = schemaUrl.openStream();
            try {
                StreamSource source = new StreamSource(schemaUrl.openStream());
                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                Schema schema = schemaFactory.newSchema(source);
                return schema;
            }
            catch (Exception e) {
                throw new XmlInfrastructureException("Unable to load schema [" + schemaUrl.toExternalForm() + "]", e);
            }
            finally {
                try {
                    schemaStream.close();
                }
                catch (IOException e) {
                    log.debugf("Problem closing schema stream - %s", (Object)e.toString());
                }
            }
        }
        catch (IOException e) {
            throw new XmlInfrastructureException("Stream error handling schema url [" + schemaUrl.toExternalForm() + "]");
        }
    }
}


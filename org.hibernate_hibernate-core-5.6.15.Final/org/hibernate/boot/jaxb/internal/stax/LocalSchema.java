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

@Deprecated
public enum LocalSchema {
    ORM("http://www.hibernate.org/xsd/orm/mapping", "org/hibernate/jpa/orm_2_1.xsd", "2.1"),
    HBM("http://www.hibernate.org/xsd/orm/hbm", "org/hibernate/xsd/mapping/legacy-mapping-4.0.xsd", "4.0"),
    CFG("http://www.hibernate.org/xsd/orm/cfg", "org/hibernate/hibernate-configuration-4.0.xsd", "4.0");

    private static final Logger log;
    private final String namespaceUri;
    private final String localResourceName;
    private final String currentVersion;
    private final Schema schema;

    private LocalSchema(String namespaceUri, String localResourceName, String currentVersion) {
        this.namespaceUri = namespaceUri;
        this.localResourceName = localResourceName;
        this.currentVersion = currentVersion;
        this.schema = LocalSchema.resolveLocalSchema(localResourceName);
    }

    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public String getCurrentVersion() {
        return this.currentVersion;
    }

    public Schema getSchema() {
        return this.schema;
    }

    private static Schema resolveLocalSchema(String schemaName) {
        return LocalSchema.resolveLocalSchema(LocalSchema.resolveLocalSchemaUrl(schemaName));
    }

    private static URL resolveLocalSchemaUrl(String schemaName) {
        URL url = LocalSchema.class.getClassLoader().getResource(schemaName);
        if (url == null) {
            throw new XmlInfrastructureException("Unable to locate schema [" + schemaName + "] via classpath");
        }
        return url;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Schema resolveLocalSchema(URL schemaUrl) {
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

    static {
        log = Logger.getLogger(LocalSchema.class);
    }
}


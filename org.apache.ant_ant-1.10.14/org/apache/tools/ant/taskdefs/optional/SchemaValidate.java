/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.XMLValidateTask;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class SchemaValidate
extends XMLValidateTask {
    public static final String ERROR_SAX_1 = "SAX1 parsers are not supported";
    public static final String ERROR_NO_XSD_SUPPORT = "Parser does not support Xerces or JAXP schema features";
    public static final String ERROR_TOO_MANY_DEFAULT_SCHEMAS = "Only one of defaultSchemaFile and defaultSchemaURL allowed";
    public static final String ERROR_PARSER_CREATION_FAILURE = "Could not create parser";
    public static final String MESSAGE_ADDING_SCHEMA = "Adding schema ";
    public static final String ERROR_DUPLICATE_SCHEMA = "Duplicate declaration of schema ";
    private Map<String, SchemaLocation> schemaLocations = new HashMap<String, SchemaLocation>();
    private boolean fullChecking = true;
    private boolean disableDTD = false;
    private SchemaLocation anonymousSchema;

    @Override
    public void init() throws BuildException {
        super.init();
        this.setLenient(false);
    }

    public boolean enableXercesSchemaValidation() {
        try {
            this.setFeature("http://apache.org/xml/features/validation/schema", true);
            this.setNoNamespaceSchemaProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
        }
        catch (BuildException e) {
            this.log(e.toString(), 3);
            return false;
        }
        return true;
    }

    private void setNoNamespaceSchemaProperty(String property) {
        String anonSchema = this.getNoNamespaceSchemaURL();
        if (anonSchema != null) {
            this.setProperty(property, anonSchema);
        }
    }

    public boolean enableJAXP12SchemaValidation() {
        try {
            this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            this.setNoNamespaceSchemaProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
        }
        catch (BuildException e) {
            this.log(e.toString(), 3);
            return false;
        }
        return true;
    }

    public void addConfiguredSchema(SchemaLocation location) {
        this.log("adding schema " + location, 4);
        location.validateNamespace();
        SchemaLocation old = this.schemaLocations.get(location.getNamespace());
        if (old != null && !old.equals(location)) {
            throw new BuildException(ERROR_DUPLICATE_SCHEMA + location);
        }
        this.schemaLocations.put(location.getNamespace(), location);
    }

    public void setFullChecking(boolean fullChecking) {
        this.fullChecking = fullChecking;
    }

    protected void createAnonymousSchema() {
        if (this.anonymousSchema == null) {
            this.anonymousSchema = new SchemaLocation();
        }
        this.anonymousSchema.setNamespace("(no namespace)");
    }

    public void setNoNamespaceURL(String defaultSchemaURL) {
        this.createAnonymousSchema();
        this.anonymousSchema.setUrl(defaultSchemaURL);
    }

    public void setNoNamespaceFile(File defaultSchemaFile) {
        this.createAnonymousSchema();
        this.anonymousSchema.setFile(defaultSchemaFile);
    }

    public void setDisableDTD(boolean disableDTD) {
        this.disableDTD = disableDTD;
    }

    @Override
    protected void initValidator() {
        super.initValidator();
        if (this.isSax1Parser()) {
            throw new BuildException(ERROR_SAX_1);
        }
        this.setFeature("http://xml.org/sax/features/namespaces", true);
        if (!this.enableXercesSchemaValidation() && !this.enableJAXP12SchemaValidation()) {
            throw new BuildException(ERROR_NO_XSD_SUPPORT);
        }
        this.setFeature("http://apache.org/xml/features/validation/schema-full-checking", this.fullChecking);
        this.setFeatureIfSupported("http://apache.org/xml/features/disallow-doctype-decl", this.disableDTD);
        this.addSchemaLocations();
    }

    @Override
    protected XMLReader createDefaultReader() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        XMLReader reader = null;
        try {
            SAXParser saxParser = factory.newSAXParser();
            reader = saxParser.getXMLReader();
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new BuildException(ERROR_PARSER_CREATION_FAILURE, e);
        }
        return reader;
    }

    protected void addSchemaLocations() {
        if (!this.schemaLocations.isEmpty()) {
            String joinedValue = this.schemaLocations.values().stream().map(SchemaLocation::getURIandLocation).peek(tuple -> this.log(MESSAGE_ADDING_SCHEMA + tuple, 3)).collect(Collectors.joining(" "));
            this.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", joinedValue);
        }
    }

    protected String getNoNamespaceSchemaURL() {
        return this.anonymousSchema == null ? null : this.anonymousSchema.getSchemaLocationURL();
    }

    protected void setFeatureIfSupported(String feature, boolean value) {
        try {
            this.getXmlReader().setFeature(feature, value);
        }
        catch (SAXNotRecognizedException e) {
            this.log("Not recognized: " + feature, 3);
        }
        catch (SAXNotSupportedException e) {
            this.log("Not supported: " + feature, 3);
        }
    }

    @Override
    protected void onSuccessfulValidation(int fileProcessed) {
        this.log(fileProcessed + " file(s) have been successfully validated.", 3);
    }

    public static class SchemaLocation {
        private String namespace;
        private File file;
        private String url;
        public static final String ERROR_NO_URI = "No namespace URI";
        public static final String ERROR_TWO_LOCATIONS = "Both URL and File were given for schema ";
        public static final String ERROR_NO_FILE = "File not found: ";
        public static final String ERROR_NO_URL_REPRESENTATION = "Cannot make a URL of ";
        public static final String ERROR_NO_LOCATION = "No file or URL supplied for the schema ";

        public String getNamespace() {
            return this.namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSchemaLocationURL() {
            boolean hasFile = this.file != null;
            boolean hasURL = this.isSet(this.url);
            if (!hasFile && !hasURL) {
                throw new BuildException(ERROR_NO_LOCATION + this.namespace);
            }
            if (hasFile && hasURL) {
                throw new BuildException(ERROR_TWO_LOCATIONS + this.namespace);
            }
            String schema = this.url;
            if (hasFile) {
                if (!this.file.exists()) {
                    throw new BuildException(ERROR_NO_FILE + this.file);
                }
                try {
                    schema = FileUtils.getFileUtils().getFileURL(this.file).toString();
                }
                catch (MalformedURLException e) {
                    throw new BuildException(ERROR_NO_URL_REPRESENTATION + this.file, e);
                }
            }
            return schema;
        }

        public String getURIandLocation() throws BuildException {
            this.validateNamespace();
            return this.namespace + ' ' + this.getSchemaLocationURL();
        }

        public void validateNamespace() {
            if (!this.isSet(this.getNamespace())) {
                throw new BuildException(ERROR_NO_URI);
            }
        }

        private boolean isSet(String property) {
            return property != null && !property.isEmpty();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SchemaLocation)) {
                return false;
            }
            SchemaLocation schemaLocation = (SchemaLocation)o;
            return (this.file == null ? schemaLocation.file == null : this.file.equals(schemaLocation.file)) && (this.namespace == null ? schemaLocation.namespace == null : this.namespace.equals(schemaLocation.namespace)) && (this.url == null ? schemaLocation.url == null : this.url.equals(schemaLocation.url));
        }

        public int hashCode() {
            int result = this.namespace == null ? 0 : this.namespace.hashCode();
            result = 29 * result + (this.file == null ? 0 : this.file.hashCode());
            result = 29 * result + (this.url == null ? 0 : this.url.hashCode());
            return result;
        }

        public String toString() {
            return (this.namespace == null ? "(anonymous)" : this.namespace) + (this.url == null ? "" : " " + this.url) + (this.file == null ? "" : " " + this.file.getAbsolutePath());
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.validation;

import java.io.File;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactoryFinder;
import javax.xml.validation.SecuritySupport;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class SchemaFactory {
    static /* synthetic */ Class class$javax$xml$validation$SchemaFactory;

    protected SchemaFactory() {
    }

    public static final SchemaFactory newInstance(String string) {
        SchemaFactory schemaFactory;
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader == null) {
            classLoader = (class$javax$xml$validation$SchemaFactory == null ? (class$javax$xml$validation$SchemaFactory = SchemaFactory.class$("javax.xml.validation.SchemaFactory")) : class$javax$xml$validation$SchemaFactory).getClassLoader();
        }
        if ((schemaFactory = new SchemaFactoryFinder(classLoader).newFactory(string)) == null) {
            throw new IllegalArgumentException(string);
        }
        return schemaFactory;
    }

    public static SchemaFactory newInstance(String string, String string2, ClassLoader classLoader) {
        SchemaFactory schemaFactory;
        if (string == null) {
            throw new NullPointerException();
        }
        if (string2 == null) {
            throw new IllegalArgumentException("factoryClassName cannot be null.");
        }
        if (classLoader == null) {
            classLoader = SecuritySupport.getContextClassLoader();
        }
        if ((schemaFactory = new SchemaFactoryFinder(classLoader).createInstance(string2)) == null || !schemaFactory.isSchemaLanguageSupported(string)) {
            throw new IllegalArgumentException(string);
        }
        return schemaFactory;
    }

    public abstract boolean isSchemaLanguageSupported(String var1);

    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(string);
    }

    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(string);
    }

    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(string);
    }

    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException("the name parameter is null");
        }
        throw new SAXNotRecognizedException(string);
    }

    public abstract void setErrorHandler(ErrorHandler var1);

    public abstract ErrorHandler getErrorHandler();

    public abstract void setResourceResolver(LSResourceResolver var1);

    public abstract LSResourceResolver getResourceResolver();

    public Schema newSchema(Source source) throws SAXException {
        return this.newSchema(new Source[]{source});
    }

    public Schema newSchema(File file) throws SAXException {
        return this.newSchema(new StreamSource(file));
    }

    public Schema newSchema(URL uRL) throws SAXException {
        return this.newSchema(new StreamSource(uRL.toExternalForm()));
    }

    public abstract Schema newSchema(Source[] var1) throws SAXException;

    public abstract Schema newSchema() throws SAXException;

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}


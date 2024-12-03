/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.validation;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class Validator {
    protected Validator() {
    }

    public abstract void reset();

    public void validate(Source source) throws SAXException, IOException {
        this.validate(source, null);
    }

    public abstract void validate(Source var1, Result var2) throws SAXException, IOException;

    public abstract void setErrorHandler(ErrorHandler var1);

    public abstract ErrorHandler getErrorHandler();

    public abstract void setResourceResolver(LSResourceResolver var1);

    public abstract LSResourceResolver getResourceResolver();

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
}


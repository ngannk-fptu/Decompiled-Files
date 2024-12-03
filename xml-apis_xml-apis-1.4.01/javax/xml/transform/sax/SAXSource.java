/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.sax;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXSource
implements Source {
    public static final String FEATURE = "http://javax.xml.transform.sax.SAXSource/feature";
    private XMLReader reader;
    private InputSource inputSource;

    public SAXSource() {
    }

    public SAXSource(XMLReader xMLReader, InputSource inputSource) {
        this.reader = xMLReader;
        this.inputSource = inputSource;
    }

    public SAXSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    public void setXMLReader(XMLReader xMLReader) {
        this.reader = xMLReader;
    }

    public XMLReader getXMLReader() {
        return this.reader;
    }

    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    public InputSource getInputSource() {
        return this.inputSource;
    }

    public void setSystemId(String string) {
        if (null == this.inputSource) {
            this.inputSource = new InputSource(string);
        } else {
            this.inputSource.setSystemId(string);
        }
    }

    public String getSystemId() {
        if (this.inputSource == null) {
            return null;
        }
        return this.inputSource.getSystemId();
    }

    public static InputSource sourceToInputSource(Source source) {
        if (source instanceof SAXSource) {
            return ((SAXSource)source).getInputSource();
        }
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource)source;
            InputSource inputSource = new InputSource(streamSource.getSystemId());
            inputSource.setByteStream(streamSource.getInputStream());
            inputSource.setCharacterStream(streamSource.getReader());
            inputSource.setPublicId(streamSource.getPublicId());
            return inputSource;
        }
        return null;
    }
}


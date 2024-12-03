/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util.transform;

import com.sun.xml.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.messaging.saaj.util.XMLDeclarationParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

public class EfficientStreamingTransformer
extends Transformer {
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private Transformer m_realTransformer = null;
    private Object m_fiDOMDocumentParser = null;
    private Object m_fiDOMDocumentSerializer = null;

    private EfficientStreamingTransformer() {
    }

    private void materialize() throws TransformerException {
        if (this.m_realTransformer == null) {
            this.m_realTransformer = this.transformerFactory.newTransformer();
        }
    }

    @Override
    public void clearParameters() {
        if (this.m_realTransformer != null) {
            this.m_realTransformer.clearParameters();
        }
    }

    @Override
    public ErrorListener getErrorListener() {
        try {
            this.materialize();
            return this.m_realTransformer.getErrorListener();
        }
        catch (TransformerException transformerException) {
            return null;
        }
    }

    @Override
    public Properties getOutputProperties() {
        try {
            this.materialize();
            return this.m_realTransformer.getOutputProperties();
        }
        catch (TransformerException transformerException) {
            return null;
        }
    }

    @Override
    public String getOutputProperty(String str) throws IllegalArgumentException {
        try {
            this.materialize();
            return this.m_realTransformer.getOutputProperty(str);
        }
        catch (TransformerException transformerException) {
            return null;
        }
    }

    @Override
    public Object getParameter(String str) {
        try {
            this.materialize();
            return this.m_realTransformer.getParameter(str);
        }
        catch (TransformerException transformerException) {
            return null;
        }
    }

    @Override
    public URIResolver getURIResolver() {
        try {
            this.materialize();
            return this.m_realTransformer.getURIResolver();
        }
        catch (TransformerException transformerException) {
            return null;
        }
    }

    @Override
    public void setErrorListener(ErrorListener errorListener) throws IllegalArgumentException {
        try {
            this.materialize();
            this.m_realTransformer.setErrorListener(errorListener);
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    @Override
    public void setOutputProperties(Properties properties) throws IllegalArgumentException {
        try {
            this.materialize();
            this.m_realTransformer.setOutputProperties(properties);
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    @Override
    public void setOutputProperty(String str, String str1) throws IllegalArgumentException {
        try {
            this.materialize();
            this.m_realTransformer.setOutputProperty(str, str1);
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    @Override
    public void setParameter(String str, Object obj) {
        try {
            this.materialize();
            this.m_realTransformer.setParameter(str, obj);
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    @Override
    public void setURIResolver(URIResolver uRIResolver) {
        try {
            this.materialize();
            this.m_realTransformer.setURIResolver(uRIResolver);
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    private InputStream getInputStreamFromSource(StreamSource s) throws TransformerException {
        InputStream stream = s.getInputStream();
        if (stream != null) {
            return stream;
        }
        if (s.getReader() != null) {
            return null;
        }
        String systemId = s.getSystemId();
        if (systemId != null) {
            try {
                String fileURL = systemId;
                if (systemId.startsWith("file:///")) {
                    String driveDesignatedPath;
                    String absolutePath = systemId.substring(7);
                    boolean hasDriveDesignator = absolutePath.indexOf(":") > 0;
                    fileURL = hasDriveDesignator ? (driveDesignatedPath = absolutePath.substring(1)) : absolutePath;
                }
                try {
                    return new FileInputStream(new File(new URI(fileURL)));
                }
                catch (URISyntaxException ex) {
                    throw new TransformerException(ex);
                }
            }
            catch (IOException e) {
                throw new TransformerException(e.toString());
            }
        }
        throw new TransformerException("Unexpected StreamSource object");
    }

    @Override
    public void transform(Source source, Result result) throws TransformerException {
        if (source instanceof StreamSource && result instanceof StreamResult) {
            block21: {
                try {
                    int num;
                    StreamSource streamSource = (StreamSource)source;
                    InputStream is = this.getInputStreamFromSource(streamSource);
                    OutputStream os = ((StreamResult)result).getOutputStream();
                    if (os == null) {
                        throw new TransformerException("Unexpected StreamResult object contains null OutputStream");
                    }
                    if (is != null) {
                        int num2;
                        if (is.markSupported()) {
                            is.mark(Integer.MAX_VALUE);
                        }
                        byte[] b = new byte[8192];
                        while ((num2 = is.read(b)) != -1) {
                            os.write(b, 0, num2);
                        }
                        if (is.markSupported()) {
                            is.reset();
                        }
                        return;
                    }
                    Reader reader = streamSource.getReader();
                    if (reader == null) break block21;
                    if (reader.markSupported()) {
                        reader.mark(Integer.MAX_VALUE);
                    }
                    PushbackReader pushbackReader = new PushbackReader(reader, 4096);
                    XMLDeclarationParser ev = new XMLDeclarationParser(pushbackReader);
                    try {
                        ev.parse();
                    }
                    catch (Exception ex) {
                        throw new TransformerException("Unable to run the JAXP transformer on a stream " + ex.getMessage());
                    }
                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    ev.writeTo(writer);
                    char[] ac = new char[8192];
                    while ((num = pushbackReader.read(ac)) != -1) {
                        ((Writer)writer).write(ac, 0, num);
                    }
                    ((Writer)writer).flush();
                    if (reader.markSupported()) {
                        reader.reset();
                    }
                    return;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new TransformerException(e.toString());
                }
            }
            throw new TransformerException("Unexpected StreamSource object");
        }
        if (FastInfosetReflection.isFastInfosetSource(source) && result instanceof DOMResult) {
            try {
                if (this.m_fiDOMDocumentParser == null) {
                    this.m_fiDOMDocumentParser = FastInfosetReflection.DOMDocumentParser_new();
                }
                FastInfosetReflection.DOMDocumentParser_parse(this.m_fiDOMDocumentParser, (Document)((DOMResult)result).getNode(), FastInfosetReflection.FastInfosetSource_getInputStream(source));
                return;
            }
            catch (Exception e) {
                throw new TransformerException(e);
            }
        }
        if (source instanceof DOMSource && FastInfosetReflection.isFastInfosetResult(result)) {
            try {
                if (this.m_fiDOMDocumentSerializer == null) {
                    this.m_fiDOMDocumentSerializer = FastInfosetReflection.DOMDocumentSerializer_new();
                }
                FastInfosetReflection.DOMDocumentSerializer_setOutputStream(this.m_fiDOMDocumentSerializer, FastInfosetReflection.FastInfosetResult_getOutputStream(result));
                FastInfosetReflection.DOMDocumentSerializer_serialize(this.m_fiDOMDocumentSerializer, ((DOMSource)source).getNode());
                return;
            }
            catch (Exception e) {
                throw new TransformerException(e);
            }
        }
        this.materialize();
        this.m_realTransformer.transform(source, result);
    }

    public static Transformer newTransformer() {
        return new EfficientStreamingTransformer();
    }
}


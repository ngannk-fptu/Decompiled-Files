/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.streaming;

import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.streaming.DOMStreamReader;
import com.sun.xml.ws.streaming.XMLReaderException;
import com.sun.xml.ws.util.FastInfosetUtil;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

public class SourceReaderFactory {
    public static XMLStreamReader createSourceReader(Source source, boolean rejectDTDs) {
        return SourceReaderFactory.createSourceReader(source, rejectDTDs, null);
    }

    public static XMLStreamReader createSourceReader(Source source, boolean rejectDTDs, String charsetName) {
        try {
            if (source instanceof StreamSource) {
                StreamSource streamSource = (StreamSource)source;
                InputStream is = streamSource.getInputStream();
                if (is != null) {
                    if (charsetName != null) {
                        return XMLStreamReaderFactory.create(source.getSystemId(), new InputStreamReader(is, charsetName), rejectDTDs);
                    }
                    return XMLStreamReaderFactory.create(source.getSystemId(), is, rejectDTDs);
                }
                Reader reader = streamSource.getReader();
                if (reader != null) {
                    return XMLStreamReaderFactory.create(source.getSystemId(), reader, rejectDTDs);
                }
                return XMLStreamReaderFactory.create(source.getSystemId(), new URL(source.getSystemId()).openStream(), rejectDTDs);
            }
            if (FastInfosetUtil.isFastInfosetSource(source)) {
                return FastInfosetUtil.createFIStreamReader(source);
            }
            if (source instanceof DOMSource) {
                DOMStreamReader dsr = new DOMStreamReader();
                dsr.setCurrentNode(((DOMSource)source).getNode());
                return dsr;
            }
            if (source instanceof SAXSource) {
                Transformer tx = XmlUtil.newTransformer();
                DOMResult domResult = new DOMResult();
                tx.transform(source, domResult);
                return SourceReaderFactory.createSourceReader(new DOMSource(domResult.getNode()), rejectDTDs);
            }
            throw new XMLReaderException("sourceReader.invalidSource", source.getClass().getName());
        }
        catch (Exception e) {
            throw new XMLReaderException(e);
        }
    }
}


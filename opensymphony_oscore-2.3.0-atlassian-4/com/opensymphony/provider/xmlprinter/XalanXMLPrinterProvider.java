/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider.xmlprinter;

import com.opensymphony.provider.ProviderConfigurationException;
import com.opensymphony.provider.XMLPrinterProvider;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class XalanXMLPrinterProvider
implements XMLPrinterProvider {
    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }

    @Override
    public void print(Document doc, Writer out) throws IOException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty("encoding", "UTF-8");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
            out.flush();
        }
        catch (Exception e) {
            throw new IOException("Error while serializing XML", e);
        }
    }
}


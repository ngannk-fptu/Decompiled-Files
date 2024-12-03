/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.xslt;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Transform {
    static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    static Map<URI, Templates> cache = new ConcurrentHashMap<URI, Templates>();

    public static void transform(TransformerFactory transformerFactory, URL xslt, InputStream in, OutputStream out) throws Exception {
        if (xslt == null) {
            throw new IllegalArgumentException("No source template specified");
        }
        Templates templates = cache.get(xslt.toURI());
        if (templates == null) {
            try (InputStream xsltIn = xslt.openStream();){
                templates = transformerFactory.newTemplates(new StreamSource(xsltIn));
                cache.put(xslt.toURI(), templates);
            }
        }
        StreamResult xmlResult = new StreamResult(out);
        StreamSource xmlSource = new StreamSource(in);
        Transformer t = templates.newTransformer();
        t.transform(xmlSource, xmlResult);
        out.flush();
    }

    public static void transform(URL xslt, InputStream in, OutputStream out) throws Exception {
        Transform.transform(transformerFactory, xslt, in, out);
    }
}


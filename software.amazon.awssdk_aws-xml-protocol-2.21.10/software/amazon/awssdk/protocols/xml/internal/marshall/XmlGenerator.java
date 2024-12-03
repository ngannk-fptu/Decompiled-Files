/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import java.io.StringWriter;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlWriter;

@SdkInternalApi
public final class XmlGenerator {
    private final StringWriter stringWriter;
    private final XmlWriter xmlWriter;

    private XmlGenerator(StringWriter stringWriter, XmlWriter xmlWriter) {
        this.stringWriter = stringWriter;
        this.xmlWriter = xmlWriter;
    }

    public static XmlGenerator create(String xmlns) {
        StringWriter stringWriter = new StringWriter();
        return new XmlGenerator(stringWriter, new XmlWriter(stringWriter, xmlns));
    }

    public XmlWriter xmlWriter() {
        return this.xmlWriter;
    }

    public StringWriter stringWriter() {
        return this.stringWriter;
    }

    public void startElement(String element) {
        this.xmlWriter.startElement(element);
    }

    public void startElement(String element, Map<String, String> attributes) {
        this.xmlWriter.startElement(element, attributes);
    }

    public void endElement() {
        this.xmlWriter.endElement();
    }
}


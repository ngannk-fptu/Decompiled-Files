/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public interface OMMetaFactory {
    public OMFactory getOMFactory();

    public SOAPFactory getSOAP11Factory();

    public SOAPFactory getSOAP12Factory();

    public OMXMLParserWrapper createStAXOMBuilder(OMFactory var1, XMLStreamReader var2);

    public OMXMLParserWrapper createOMBuilder(OMFactory var1, StAXParserConfiguration var2, InputSource var3);

    public OMXMLParserWrapper createOMBuilder(OMFactory var1, Source var2);

    public OMXMLParserWrapper createOMBuilder(OMFactory var1, Node var2, boolean var3);

    public OMXMLParserWrapper createOMBuilder(OMFactory var1, SAXSource var2, boolean var3);

    public OMXMLParserWrapper createOMBuilder(StAXParserConfiguration var1, OMFactory var2, InputSource var3, MimePartProvider var4);

    public SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader var1);

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration var1, InputSource var2);

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration var1, SOAPFactory var2, InputSource var3, MimePartProvider var4);
}


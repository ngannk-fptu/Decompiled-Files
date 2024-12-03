/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDJAXRPCHandlerInfo
extends WSDDElement {
    private String _classname;
    private Map _map;
    private QName[] _headers;

    public WSDDJAXRPCHandlerInfo() {
    }

    public WSDDJAXRPCHandlerInfo(Element e) throws WSDDException {
        super(e);
        String classnameStr = e.getAttribute("classname");
        if (classnameStr == null || classnameStr.equals("")) {
            throw new WSDDException(Messages.getMessage("noClassNameAttr00"));
        }
        this._classname = classnameStr;
        Element[] elements = this.getChildElements(e, "parameter");
        this._map = new HashMap();
        if (elements.length != 0) {
            for (int i = 0; i < elements.length; ++i) {
                Element param = elements[i];
                String pname = param.getAttribute("name");
                String value = param.getAttribute("value");
                this._map.put(pname, value);
            }
        }
        if ((elements = this.getChildElements(e, "header")).length != 0) {
            ArrayList<QName> headerList = new ArrayList<QName>();
            for (int i = 0; i < elements.length; ++i) {
                Element qElem = elements[i];
                String headerStr = qElem.getAttribute("qname");
                if (headerStr == null || headerStr.equals("")) {
                    throw new WSDDException(Messages.getMessage("noValidHeader"));
                }
                QName headerQName = XMLUtils.getQNameFromString(headerStr, qElem);
                if (headerQName == null) continue;
                headerList.add(headerQName);
            }
            QName[] headers = new QName[headerList.size()];
            this._headers = headerList.toArray(headers);
        }
    }

    protected QName getElementName() {
        return QNAME_JAXRPC_HANDLERINFO;
    }

    public String getHandlerClassName() {
        return this._classname;
    }

    public void setHandlerClassName(String classname) {
        this._classname = classname;
    }

    public Map getHandlerMap() {
        return this._map;
    }

    public void setHandlerMap(Map map) {
        this._map = map;
    }

    public QName[] getHeaders() {
        return this._headers;
    }

    public void setHeaders(QName[] headers) {
        this._headers = headers;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "classname", "classname", "CDATA", this._classname);
        context.startElement(WSDDConstants.QNAME_JAXRPC_HANDLERINFO, attrs);
        Map ht = this._map;
        if (ht != null) {
            Set keys = ht.keySet();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                String name = (String)iter.next();
                String value = (String)ht.get(name);
                attrs = new AttributesImpl();
                attrs.addAttribute("", "name", "name", "CDATA", name);
                attrs.addAttribute("", "value", "value", "CDATA", value);
                context.startElement(WSDDConstants.QNAME_PARAM, attrs);
                context.endElement();
            }
        }
        if (this._headers != null) {
            for (int i = 0; i < this._headers.length; ++i) {
                QName qname = this._headers[i];
                attrs = new AttributesImpl();
                attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(qname));
                context.startElement(WSDDConstants.QNAME_JAXRPC_HEADER, attrs);
                context.endElement();
            }
        }
        context.endElement();
    }
}


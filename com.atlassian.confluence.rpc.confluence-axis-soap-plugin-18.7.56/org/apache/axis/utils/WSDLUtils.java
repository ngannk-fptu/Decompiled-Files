/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Port
 *  javax.wsdl.extensions.UnknownExtensibilityElement
 *  javax.wsdl.extensions.soap.SOAPAddress
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.util.List;
import java.util.ListIterator;
import javax.wsdl.Port;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

public class WSDLUtils {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$WSDLUtils == null ? (class$org$apache$axis$utils$WSDLUtils = WSDLUtils.class$("org.apache.axis.utils.WSDLUtils")) : class$org$apache$axis$utils$WSDLUtils).getName());
    static /* synthetic */ Class class$org$apache$axis$utils$WSDLUtils;

    public static String getAddressFromPort(Port p) {
        List extensibilityList = p.getExtensibilityElements();
        ListIterator li = extensibilityList.listIterator();
        while (li.hasNext()) {
            UnknownExtensibilityElement unkElement;
            QName name;
            Object obj = li.next();
            if (obj instanceof SOAPAddress) {
                return ((SOAPAddress)obj).getLocationURI();
            }
            if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("address")) continue;
            return unkElement.getElement().getAttribute("location");
        }
        return null;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


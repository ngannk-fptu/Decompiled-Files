/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import com.ibm.wsdl.util.xml.DOM2Writer;
import java.io.PrintWriter;
import java.io.Serializable;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;

public class UnknownExtensionSerializer
implements ExtensionSerializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        UnknownExtensibilityElement unknownExt = (UnknownExtensibilityElement)extension;
        pw.print("    ");
        DOM2Writer.serializeAsXML(unknownExt.getElement(), def.getNamespaces(), pw);
        pw.println();
    }
}


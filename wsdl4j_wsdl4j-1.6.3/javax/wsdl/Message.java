/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.util.List;
import java.util.Map;
import javax.wsdl.Part;
import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

public interface Message
extends WSDLElement {
    public void setQName(QName var1);

    public QName getQName();

    public void addPart(Part var1);

    public Part getPart(String var1);

    public Part removePart(String var1);

    public Map getParts();

    public List getOrderedParts(List var1);

    public void setUndefined(boolean var1);

    public boolean isUndefined();
}


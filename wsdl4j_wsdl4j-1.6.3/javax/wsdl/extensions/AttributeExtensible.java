/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public interface AttributeExtensible {
    public static final int NO_DECLARED_TYPE = -1;
    public static final int STRING_TYPE = 0;
    public static final int QNAME_TYPE = 1;
    public static final int LIST_OF_STRINGS_TYPE = 2;
    public static final int LIST_OF_QNAMES_TYPE = 3;

    public void setExtensionAttribute(QName var1, Object var2);

    public Object getExtensionAttribute(QName var1);

    public Map getExtensionAttributes();

    public List getNativeAttributeNames();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.io.Serializable;

public interface Name
extends Comparable,
Cloneable,
Serializable {
    public static final String NS_EMPTY_PREFIX = "";
    public static final String NS_DEFAULT_URI = "";
    public static final String NS_REP_PREFIX = "rep";
    public static final String NS_REP_URI = "internal";
    public static final String NS_JCR_PREFIX = "jcr";
    public static final String NS_JCR_URI = "http://www.jcp.org/jcr/1.0";
    public static final String NS_NT_PREFIX = "nt";
    public static final String NS_NT_URI = "http://www.jcp.org/jcr/nt/1.0";
    public static final String NS_MIX_PREFIX = "mix";
    public static final String NS_MIX_URI = "http://www.jcp.org/jcr/mix/1.0";
    public static final String NS_SV_PREFIX = "sv";
    public static final String NS_SV_URI = "http://www.jcp.org/jcr/sv/1.0";
    public static final String NS_XML_PREFIX = "xml";
    public static final String NS_XML_URI = "http://www.w3.org/XML/1998/namespace";
    public static final String NS_XMLNS_PREFIX = "xmlns";
    public static final String NS_XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    public static final Name[] EMPTY_ARRAY = new Name[0];

    public String getLocalName();

    public String getNamespaceURI();
}


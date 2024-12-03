/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.search;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface SearchConstants {
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;
    public static final String BASICSEARCH = NAMESPACE.getPrefix() + "basicsearch";
    public static final String HEADER_DASL = "DASL";
    public static final String XML_QUERY_GRAMMAR = "supported-query-grammar";
    public static final String XML_GRAMMER = "grammar";
    public static final String XML_SEARCHREQUEST = "searchrequest";
    public static final String XML_QUERY_SCHEMA_DISCOVERY = "query-schema-discovery";
    public static final DavPropertyName QUERY_GRAMMER_SET = DavPropertyName.create("supported-query-grammar-set", NAMESPACE);
}


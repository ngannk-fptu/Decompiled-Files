/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Namespace;
import com.atlassian.confluence.content.render.xhtml.ThreadLocalTransformationDateFormats;
import com.atlassian.confluence.content.render.xhtml.TransformationDateFormats;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.xml.namespace.QName;

public final class XhtmlConstants {
    public static final String CONFLUENCE_XHTML_NAMESPACE_PREFIX = "atlassian-content";
    public static final String CONFLUENCE_XHTML_NAMESPACE_ALTERNATE_PREFIX = "ac";
    public static final String CONFLUENCE_XHTML_NAMESPACE_URI = "http://atlassian.com/content";
    public static final String CONFLUENCE_XHTML_XMLNS_DECLARATION = "xmlns:ac=\"http://atlassian.com/content\"";
    public static final String XHTML_NAMESPACE_URI = "http://www.w3.org/1999/xhtml";
    public static final String RESOURCE_IDENTIFIER_NAMESPACE_URI = "http://atlassian.com/resource/identifier";
    public static final String RESOURCE_IDENTIFIER_NAMESPACE_PREFIX = "ri";
    public static final Namespace CONFLUENCE_ALTERNATE_NAMESPACE = new Namespace("ac", "http://atlassian.com/content", false);
    public static final Namespace CONFLUENCE_NAMESPACE = new Namespace("atlassian-content", "http://atlassian.com/content", false);
    public static final Namespace XHTML_NAMESPACE = new Namespace(null, "http://www.w3.org/1999/xhtml", true);
    public static final Namespace RESOURCE_IDENTIFIER_NAMESPACE = new Namespace("ri", "http://atlassian.com/resource/identifier", false);
    public static final String TEMPLATE_NAMESPACE_PREFIX = "atlassian-template";
    public static final String TEMPLATE_NAMESPACE_ALTERNATE_PREFIX = "at";
    public static final String TEMPLATE_NAMESPACE_URI = "http://atlassian.com/template";
    public static final Namespace TEMPLATE_ALTERNATE_NAMESPACE = new Namespace("at", "http://atlassian.com/template", false);
    public static final Namespace TEMPLATE_NAMESPACE = new Namespace("atlassian-template", "http://atlassian.com/template", false);
    public static final List<Namespace> STORAGE_NAMESPACES = ImmutableList.of((Object)CONFLUENCE_NAMESPACE, (Object)CONFLUENCE_ALTERNATE_NAMESPACE, (Object)RESOURCE_IDENTIFIER_NAMESPACE, (Object)TEMPLATE_NAMESPACE, (Object)TEMPLATE_ALTERNATE_NAMESPACE, (Object)XHTML_NAMESPACE);
    public static final TransformationDateFormats DATE_FORMATS = new ThreadLocalTransformationDateFormats();

    public static final class Attribute {
        public static final QName CLASS = new QName("class");
    }
}


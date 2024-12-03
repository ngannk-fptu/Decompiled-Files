/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import javax.xml.namespace.QName;

public final class StorageLinkConstants {
    public static final String LINK_ELEMENT_NAME = "link";
    public static final String LINK_BODY_ELEMENT_NAME = "link-body";
    public static final String PLAIN_TEXT_LINK_BODY_ELEMENT_NAME = "plain-text-link-body";
    public static final String TOOLTIP_ATTRIBUTE_NAME = "tooltip";
    public static final String TARGET_ATTRIBUTE_NAME = "target";
    public static final String ANCHOR_ATTRIBUTE_NAME = "anchor";
    public static final String BODY_TYPE_ATTRIBUTE_NAME = "type";
    public static final String BODY_TYPE_PLAIN_TEXT_VALUE = "plain-text";
    public static final QName LINK_BODY_ELEMENT_QNAME = new QName("http://atlassian.com/content", "link-body", "ac");
    public static final QName PLAIN_TEXT_LINK_BODY_ELEMENT_QNAME = new QName("http://atlassian.com/content", "plain-text-link-body", "ac");
    public static final QName LINK_ELEMENT = new QName("http://atlassian.com/content", "link", "ac");
    public static final QName LINK_BODY_TYPE_ATTRIBUTE_QNAME = new QName("http://atlassian.com/content", "type", "ac");
}


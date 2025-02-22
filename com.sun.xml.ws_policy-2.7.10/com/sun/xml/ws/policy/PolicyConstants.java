/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import javax.xml.namespace.QName;

public final class PolicyConstants {
    public static final String SUN_POLICY_NAMESPACE_URI = "http://java.sun.com/xml/ns/wsit/policy";
    public static final String SUN_POLICY_NAMESPACE_PREFIX = "sunwsp";
    public static final QName VISIBILITY_ATTRIBUTE = new QName("http://java.sun.com/xml/ns/wsit/policy", "visibility");
    public static final String VISIBILITY_VALUE_PRIVATE = "private";
    public static final String WSU_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static final String WSU_NAMESPACE_PREFIX = "wsu";
    public static final QName WSU_ID = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
    public static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";
    public static final QName XML_ID = new QName("http://www.w3.org/XML/1998/namespace", "id");
    public static final String CLIENT_CONFIGURATION_IDENTIFIER = "client";
    public static final String SUN_MANAGEMENT_NAMESPACE = "http://java.sun.com/xml/ns/metro/management";

    private PolicyConstants() {
    }
}


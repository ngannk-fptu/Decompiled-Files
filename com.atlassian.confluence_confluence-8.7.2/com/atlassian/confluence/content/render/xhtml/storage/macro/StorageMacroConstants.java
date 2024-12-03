/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import javax.xml.namespace.QName;

public class StorageMacroConstants {
    public static final QName MACRO_ELEMENT = new QName("http://atlassian.com/content", "macro", "ac");
    public static final QName MACRO_V2_ELEMENT = new QName("http://atlassian.com/content", "structured-macro", "ac");
    public static final QName MACRO_PARAMETER_ELEMENT = new QName("http://atlassian.com/content", "parameter", "ac");
    public static final QName PLAIN_TEXT_BODY_PARAMETER_ELEMENT = new QName("http://atlassian.com/content", "plain-text-body", "ac");
    public static final QName RICH_TEXT_BODY_PARAMETER_ELEMENT = new QName("http://atlassian.com/content", "rich-text-body", "ac");
    public static final QName DEFAULT_PARAMETER_ELEMENT = new QName("http://atlassian.com/content", "default-parameter", "ac");
    public static final QName NAME_ATTRIBUTE = new QName("http://atlassian.com/content", "name", "ac");
    public static final QName MACRO_ID_ATTRIBUTE = new QName("http://atlassian.com/content", "macro-id", "ac");
    public static final QName MACRO_SCHEMA_VERSION_ATTRIBUTE = new QName("http://atlassian.com/content", "schema-version", "ac");
    public static final String MACRO_OUTPUT_TYPE_PARAMETER = "atlassian-macro-output-type";
}


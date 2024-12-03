/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.templates;

import javax.xml.namespace.QName;

public interface TemplateConstants {
    public static final String TEMPLATE_CONTENT_PARAMS = "com.atlassian.confluence.plugins.templates";
    public static final String TEMPLATE_DISABLE_INPUTS = "com.atlassian.confluence.plugins.templates.input.disable";
    public static final String EDITOR_DECLARATION_GROUP_TAG = "ul";
    public static final String EDITOR_DECLARATION_GROUP_ATTR = "data-variable-declarations";
    public static final String EDITOR_DECLARATION_VAR_TAG = "li";
    public static final String EDITOR_DECLARATION_VAR_NAME_ATTR_TAG = "data-variable-name";
    public static final String EDITOR_DECLARATION_VAR_TYPE_ATTR_TAG = "data-variable-type";
    public static final String EDITOR_DECLARATION_VAR_ROWS_ATTR_TAG = "data-variable-rows";
    public static final String EDITOR_DECLARATION_VAR_COLUMNS_ATTR_TAG = "data-variable-columns";
    public static final String EDITOR_DECLARATION_VAR_RAW_XHTML_ATTR_TAG = "data-variable-raw-xhtml";
    public static final String EDITOR_DECLARATION_LIST_GROUP_TAG = "ul";
    public static final String EDITOR_DECLARATION_LIST_OPTION_TAG = "li";
    public static final String EDITOR_DECLARATION_LIST_OPTION_VALUE_ATTR = "data-variable-option";
    public static final String EDITOR_USAGE_TAG = "img";
    public static final String EDITOR_USAGE_NAME_ATTR = "data-variable-name";
    public static final QName STORAGE_DECLARATION_GROUP_TAG = new QName("http://atlassian.com/template", "declarations", "at");
    public static final QName STORAGE_NAME_ATTR = new QName("http://atlassian.com/template", "name", "at");
    public static final QName STORAGE_KEY_ATTR = new QName("http://atlassian.com/template", "key", "at");
    public static final QName STORAGE_NOAUTOESCAPE_ATTR = new QName("http://atlassian.com/template", "noAutoescape", "at");
    public static final QName STORAGE_RAW_XHTML_ATTR = new QName("http://atlassian.com/template", "rawxhtml", "at");
    public static final QName STORAGE_STRING_VAR_TAG = new QName("http://atlassian.com/template", "string", "at");
    public static final QName STORAGE_LIST_VAR_TAG = new QName("http://atlassian.com/template", "list", "at");
    public static final QName STORAGE_LIST_OPTION_TAG = new QName("http://atlassian.com/template", "option", "at");
    public static final QName STORAGE_LIST_OPTION_VALUE_ATTR = new QName("http://atlassian.com/template", "value", "at");
    public static final QName STORAGE_TEXT_AREA_VAR_TAG = new QName("http://atlassian.com/template", "textarea", "at");
    public static final QName STORAGE_TEXT_AREA_ROWS_ATTR = new QName("http://atlassian.com/template", "rows", "at");
    public static final QName STORAGE_TEXT_AREA_COLUMNS_ATTR = new QName("http://atlassian.com/template", "columns", "at");
    public static final QName STORAGE_USAGE_VARIABLE = new QName("http://atlassian.com/template", "var", "at");
    public static final QName STORAGE_USAGE_I18N = new QName("http://atlassian.com/template", "i18n", "at");
    public static final String DEFAULT_VARIABLE_TYPE = "string";
    @Deprecated
    public static final QName STORAGE_OLD_RAW_XHTML_ATTR = new QName("http://atlassian.com/template", "rawXhtml", "at");
}


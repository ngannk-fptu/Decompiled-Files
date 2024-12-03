/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.pagelayouts;

import javax.xml.namespace.QName;

public class StoragePageLayoutConstants {
    public static final String PAGE_LAYOUT = "layout";
    public static final String PAGE_LAYOUT_SECTION = "layout-section";
    public static final String PAGE_LAYOUT_CELL = "layout-cell";
    public static final String PAGE_LAYOUT_SECTION_TYPE_ATTRIBUTE_NAME = "type";
    public static final String PAGE_LAYOUT_SECTION_TYPE_SINGLE = "single";
    public static final String PAGE_LAYOUT_SECTION_TYPE_TWO_EQUAL = "two_equal";
    public static final String PAGE_LAYOUT_SECTION_TYPE_TWO_LEFT_SIDEBAR = "two_left_sidebar";
    public static final String PAGE_LAYOUT_SECTION_TYPE_TWO_RIGHT_SIDEBAR = "two_right_sidebar";
    public static final String PAGE_LAYOUT_SECTION_TYPE_THREE_EQUAL = "three_equal";
    public static final String PAGE_LAYOUT_SECTION_TYPE_THREE_WITH_SIDEBARS = "three_with_sidebars";
    public static final QName PAGE_LAYOUT_ELEMENT_QNAME = new QName("http://atlassian.com/content", "layout", "ac");
    public static final QName PAGE_LAYOUT_SECTION_ELEMENT_QNAME = new QName("http://atlassian.com/content", "layout-section", "ac");
    public static final QName PAGE_LAYOUT_CELL_ELEMENT_QNAME = new QName("http://atlassian.com/content", "layout-cell", "ac");
    public static final QName PAGE_LAYOUT_SECTION_TYPE_ATTRIBUTE_QNAME = new QName("http://atlassian.com/content", "type", "ac");
    public static final String PAGE_LAYOUT_CELL_EMPTY_TEXT = "<p></p>";
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.inlinetask;

import javax.xml.namespace.QName;

public class ViewInlineTaskConstants {
    public static final String TASK_LIST_TAG_NAME = new String("ul");
    public static final String TASK_TAG_NAME = new String("li");
    public static final String TASK_LIST_IDENTIFYING_CSS_CLASS = new String("inline-task-list");
    public static final String TASK_LIST_CONTENT_ID_DATA_ATTRIBUTE = new String("data-inline-tasks-content-id");
    public static final String TASK_ID_DATA_ATTRIBUTE = new String("data-inline-task-id");
    public static final String EMPTY_TASK_BODY_STYLE_ATTRIBUTE = new String("background-image: none;");
    public static final String COMPLETED_TASK_CSS_CLASS = new String("checked");
    public static final String EMPTY_LIST_ITEM_ID = new String("-1");
    public static final QName TASK_LIST_TAG = new QName("http://www.w3.org/1999/xhtml", TASK_LIST_TAG_NAME);
    public static final QName TASK_TAG = new QName("http://www.w3.org/1999/xhtml", TASK_TAG_NAME);
    public static final String CONTENT_PROPERTY_LAST_SEQUENCE_ID = "confluence.inline.tasks.sequence.last";
    public static final String[] TAGS_TO_IGNORE_IN_TASK_TITLE = new String[]{"address", "blockquote", "button", "dd", "div", "dl", "dt", "fieldset", "form", "hr", "li", "map", "noscript", "object", "ol", "script", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "ul"};
    public static final String DIFF_TASK_LIST_TAG_NAME = "inline-task-list-ul";
    public static final String DIFF_TASK_TAG_NAME = "inline-task-list-li";
    public static final String DIFF_TASK_EMPTY_BODY_CSS_CLASS = "empty-body";
    public static final String DIFF_IMAGE_TAG_NAME = "inline-task-check-marker";
    public static final String DIFF_IMAGE_CSS_BASE = "inline-task";
    public static final String DIFF_IMAGE_CSS_CHECKED = "inline-task checked";
    public static final String TASK_LIST_DIFF_IDENTIFYING_CSS_CLASS = "inline-task-list diff-inline-task-list";
}


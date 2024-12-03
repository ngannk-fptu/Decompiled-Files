/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.inlinetask;

import javax.xml.namespace.QName;

public class StorageInlineTaskConstants {
    public static final String TASK_LIST_ELEMENT_NAME = new String("task-list");
    public static final String TASK_ELEMENT_NAME = new String("task");
    public static final String TASK_ID_ELEMENT_NAME = new String("task-id");
    public static final String TASK_BODY_ELEMENT_NAME = new String("task-body");
    public static final String TASK_STATUS_ELEMENT_NAME = new String("task-status");
    public static final String TASK_STATUS_COMPLETE = new String("complete");
    public static final String TASK_STATUS_INCOMPLETE = new String("incomplete");
    public static final QName TASK_LIST_ELEMENT = new QName("http://atlassian.com/content", TASK_LIST_ELEMENT_NAME, "ac");
    public static final QName TASK_ELEMENT = new QName("http://atlassian.com/content", TASK_ELEMENT_NAME, "ac");
    public static final QName TASK_ID_ELEMENT = new QName("http://atlassian.com/content", TASK_ID_ELEMENT_NAME, "ac");
    public static final QName TASK_BODY_ELEMENT = new QName("http://atlassian.com/content", TASK_BODY_ELEMENT_NAME, "ac");
    public static final QName TASK_STATUS_ELEMENT = new QName("http://atlassian.com/content", TASK_STATUS_ELEMENT_NAME, "ac");
}


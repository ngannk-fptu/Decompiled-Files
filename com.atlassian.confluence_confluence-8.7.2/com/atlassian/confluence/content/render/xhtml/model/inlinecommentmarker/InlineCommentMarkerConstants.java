/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker;

import javax.xml.namespace.QName;

public class InlineCommentMarkerConstants {
    public static final String INLINE_COMMENT_MARKER_HTML_TAG_NAME = new String("span");
    public static final String INLINE_COMMENT_MARKER_STORAGE_TAG_NAME = new String("inline-comment-marker");
    public static final String INLINE_COMMENT_MARKER_STORAGE_REF_ATTR_NAME = new String("ref");
    public static final String INLINE_COMMENT_MARKER_CLASS = new String("inline-comment-marker");
    public static final String INLINE_COMMENT_MARKER_HTML_REF_ATTR_NAME = new String("data-ref");
    public static final QName INLINE_COMMENT_MARKER_STORAGE_TAG = new QName("http://atlassian.com/content", INLINE_COMMENT_MARKER_STORAGE_TAG_NAME, "ac");
    public static final QName INLINE_COMMENT_MARKER_STORAGE_REF_ATTR = new QName("http://atlassian.com/content", INLINE_COMMENT_MARKER_STORAGE_REF_ATTR_NAME, "ac");
    public static final QName INLINE_COMMENT_MARKER_HTML_TAG = new QName("http://www.w3.org/1999/xhtml", INLINE_COMMENT_MARKER_HTML_TAG_NAME);
    public static final QName INLINE_COMMENT_MARKER_HTML_REF_ATTR = new QName("http://www.w3.org/1999/xhtml", INLINE_COMMENT_MARKER_HTML_REF_ATTR_NAME);
}


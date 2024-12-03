/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.pages.Comment
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.tinymceplugin.service;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.tinymceplugin.rest.entities.CommentResult;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamException;

public interface CommentRenderService {
    public CommentResult render(Comment var1, boolean var2, HttpServletRequest var3) throws XMLStreamException, XhtmlException;

    public CommentResult render(Comment var1, boolean var2, HttpServletRequest var3, int var4, boolean var5) throws XMLStreamException, XhtmlException;
}


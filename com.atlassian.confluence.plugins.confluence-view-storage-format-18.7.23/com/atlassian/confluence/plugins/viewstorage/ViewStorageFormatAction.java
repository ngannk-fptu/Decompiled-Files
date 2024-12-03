/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.actions.AbstractPageAwareAction
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.viewstorage;

import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ViewStorageFormatAction
extends AbstractPageAwareAction {
    private static Pattern BLOCK_ELEMENTS = Pattern.compile("<(p|h[1-6]|table|tbody|thead|tfoot|tr|td|th|ul|ol|li|dl|dt|dd|div|pre|blockquote|address|hr)(\\s|>)", 2);
    private CommentManager commentManager;
    private InputStream inputStream;
    private long commentId;

    @PermittedMethods(value={HttpMethod.GET})
    public String executeForPage() throws Exception {
        AbstractPage page = this.getPage();
        String pageSource = "";
        if (!page.getBodyContents().isEmpty()) {
            BodyContent bodyContent = (BodyContent)page.getBodyContents().get(0);
            pageSource = bodyContent.getBody();
            if (bodyContent.getBodyType() == BodyType.XHTML) {
                pageSource = this.formatXhtml(bodyContent.getBody());
            }
        }
        this.inputStream = new ByteArrayInputStream(pageSource.getBytes(this.settingsManager.getGlobalSettings().getDefaultEncoding()));
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String executeForComment() throws Exception {
        String commentSource = this.formatXhtml(this.commentManager.getComment(this.commentId).getBodyAsString());
        this.inputStream = new ByteArrayInputStream(commentSource.getBytes(this.settingsManager.getGlobalSettings().getDefaultEncoding()));
        return "success";
    }

    public boolean isLatestVersionRequired() {
        return false;
    }

    private String formatXhtml(String xhtml) {
        if (StringUtils.isEmpty((CharSequence)xhtml)) {
            return xhtml;
        }
        Matcher matcher = BLOCK_ELEMENTS.matcher(xhtml);
        matcher.region(1, xhtml.length());
        return matcher.replaceAll("\n$0");
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public long getCommentId() {
        return this.commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public void setCommentManager(CommentManager commentManager) {
        this.commentManager = commentManager;
    }
}


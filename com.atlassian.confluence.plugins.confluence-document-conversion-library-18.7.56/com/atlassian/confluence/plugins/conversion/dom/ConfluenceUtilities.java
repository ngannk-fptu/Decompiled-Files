/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.PageManager
 */
package com.atlassian.confluence.plugins.conversion.dom;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;

public class ConfluenceUtilities {
    public static Attachment findImage(PageManager pageManager, AttachmentManager attachmentManager, AbstractPage currentPage, String spaceKey, String page, String imgName) {
        Object imgParent = page != null ? (spaceKey != null ? pageManager.getPage(spaceKey, page) : pageManager.getPage(currentPage.getSpaceKey(), page)) : currentPage;
        if (imgParent != null && imgName != null) {
            return attachmentManager.getAttachment((ContentEntityObject)imgParent, imgName);
        }
        return null;
    }
}


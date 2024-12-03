/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.actions.AbstractEditPageAction;

public class EditBlogPostAction
extends AbstractEditPageAction {
    @Override
    protected void validateDuplicatePageTitle() {
        BlogPost post = this.pageManager.getBlogPost(this.getSpaceKey(), this.getTitle(), BlogPost.toCalendar(this.getPostingDate()));
        if (post != null && post.getId() != this.getBlogPost().getId()) {
            this.addActionError(this.getText("news.title.exists"));
        }
    }
}


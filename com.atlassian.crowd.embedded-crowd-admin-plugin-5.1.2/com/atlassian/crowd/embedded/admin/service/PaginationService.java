/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 */
package com.atlassian.crowd.embedded.admin.service;

import com.atlassian.crowd.embedded.admin.dto.PageLink;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaginationService {
    private static final int TRAILING_AND_LEADING_PAGE_LINKS_COUNT_LIMIT = 3;
    private final I18nResolver i18nResolver;

    @Autowired
    public PaginationService(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public int getPagesCount(int totalCount, int pageSize) {
        int pagesCount = (int)Math.ceil((double)totalCount / (double)pageSize);
        return Math.max(1, pagesCount);
    }

    public List<PageLink> getPageLinks(int totalCount, int pageSize, int currentPageNumber) {
        int i;
        int pageCount = this.getPagesCount(totalCount, pageSize);
        ArrayList<PageLink> result = new ArrayList<PageLink>();
        if (pageCount == 1) {
            return result;
        }
        if (currentPageNumber > 1) {
            String previousString = this.i18nResolver.getText("embedded.crowd.directory.users.preview.pagination.previous");
            result.add(new PageLink(previousString, currentPageNumber - 1));
        }
        boolean delimiterAlreadyPrinted = false;
        for (i = 1; i < currentPageNumber; ++i) {
            if (i == 1 || currentPageNumber - i < 3) {
                result.add(new PageLink(String.valueOf(i), i));
                continue;
            }
            if (delimiterAlreadyPrinted) continue;
            result.add(new PageLink("...", 0));
            delimiterAlreadyPrinted = true;
        }
        result.add(new PageLink(String.valueOf(currentPageNumber), 0, true));
        delimiterAlreadyPrinted = false;
        for (i = currentPageNumber + 1; i <= pageCount; ++i) {
            if (i == pageCount || i - currentPageNumber < 3) {
                result.add(new PageLink(String.valueOf(i), i));
                continue;
            }
            if (delimiterAlreadyPrinted) continue;
            result.add(new PageLink("...", 0));
            delimiterAlreadyPrinted = true;
        }
        if (currentPageNumber < pageCount) {
            String nextString = this.i18nResolver.getText("embedded.crowd.directory.users.preview.pagination.next");
            result.add(new PageLink(nextString, currentPageNumber + 1));
        }
        return result;
    }
}


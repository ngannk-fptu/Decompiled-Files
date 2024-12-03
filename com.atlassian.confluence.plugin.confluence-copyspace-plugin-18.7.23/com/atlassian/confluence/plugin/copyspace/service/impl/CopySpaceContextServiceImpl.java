/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.LogProgressMeterWrapper
 *  com.atlassian.core.util.ProgressMeter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.CopySpaceContextService;
import com.atlassian.confluence.plugin.copyspace.service.SpaceService;
import com.atlassian.confluence.plugin.copyspace.service.StatisticsService;
import com.atlassian.confluence.util.LogProgressMeterWrapper;
import com.atlassian.core.util.ProgressMeter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="copySpaceContextServiceImpl")
public class CopySpaceContextServiceImpl
implements CopySpaceContextService {
    private final SpaceService spaceService;
    private final StatisticsService statisticsService;
    private static final Logger log = LoggerFactory.getLogger(CopySpaceContextServiceImpl.class);

    @Autowired
    public CopySpaceContextServiceImpl(SpaceService spaceService, StatisticsService statisticsService) {
        this.spaceService = spaceService;
        this.statisticsService = statisticsService;
    }

    @Override
    public CopySpaceContext createContext(CopySpaceRequest request) {
        int pagesCount = this.statisticsService.getTotalAmountOfPages(request.getOldKey());
        int blogsCount = this.statisticsService.getTotalAmountOfBlogs(request.getOldKey());
        int commentsCount = this.statisticsService.getTotalAmountOfComments(request.getOldKey());
        int attachmentsCount = this.statisticsService.getTotalAmountOfAttachments(request.getOldKey());
        int totalOperationsCount = 0;
        totalOperationsCount += request.isCopyPages() ? pagesCount : 0;
        int n = request.isCopyBlogPosts() ? blogsCount : 0;
        log.info("The total number of operations consists of {} pages and {} blog posts", (Object)pagesCount, (Object)blogsCount);
        ProgressMeter progressMeter = new ProgressMeter();
        progressMeter.setCurrentCount(0);
        progressMeter.setTotalObjects(this.getTotalObjects(totalOperationsCount += n));
        return new CopySpaceContext.Builder(request.getOldKey(), request.getNewKey()).copySpaceRequest(request).originalSpaceId(this.spaceService.getSpaceId(request.getOldKey()).orElse(0L)).pagesCount(pagesCount).commentsCount(commentsCount).blogPostsCount(blogsCount).attachmentsCount(attachmentsCount).progressMeter((ProgressMeter)new LogProgressMeterWrapper(progressMeter)).build();
    }

    private int getTotalObjects(int totalOperationsCount) {
        int totalObjects = (int)((float)totalOperationsCount * 1.1f);
        return totalObjects == totalOperationsCount ? totalObjects + 1 : totalObjects;
    }
}


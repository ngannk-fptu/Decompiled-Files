/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.core.persistence.SearchableDao;
import com.atlassian.confluence.internal.index.ReIndexer;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.util.DefaultProgress;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.Progress;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ReIndexTask
implements Runnable {
    private final ReIndexer reIndexer;
    private final SearchableDao searchableDao;
    private final EnumSet<ReIndexOption> options;
    private final Optional<SearchQuery> searchQuery;
    private volatile boolean finishedReindexing = false;
    private long startTime;
    private long stopTime;
    private final int jobID;
    private final Progress progress;
    private final List<String> spaceKeys;

    public ReIndexTask(ReIndexer reIndexer, SearchableDao searchableDao, EnumSet<ReIndexOption> options, int jobID) {
        this(reIndexer, searchableDao, options, Optional.empty(), jobID);
    }

    public ReIndexTask(ReIndexer reIndexer, SearchableDao searchableDao, EnumSet<ReIndexOption> options, Optional<SearchQuery> searchQuery, int jobID) {
        this(reIndexer, searchableDao, Collections.emptyList(), options, searchQuery, jobID);
    }

    public ReIndexTask(ReIndexer reIndexer, SearchableDao searchableDao, List<String> spaceKeys, EnumSet<ReIndexOption> options, int jobID) {
        this(reIndexer, searchableDao, spaceKeys, options, Optional.empty(), jobID);
    }

    public ReIndexTask(ReIndexer reIndexer, SearchableDao searchableDao, List<String> spaceKeys, EnumSet<ReIndexOption> options, Optional<SearchQuery> searchQuery, int jobID) {
        this.reIndexer = reIndexer;
        this.searchableDao = searchableDao;
        this.spaceKeys = spaceKeys;
        this.options = options;
        this.searchQuery = searchQuery;
        this.jobID = jobID;
        this.progress = new DefaultProgress(this.getTotal(this.options));
    }

    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        try {
            if (this.searchQuery.isPresent()) {
                this.reIndexer.reIndex(this.options, this.searchQuery.get(), this.progress);
            } else if (this.spaceKeys != null && !this.spaceKeys.isEmpty()) {
                this.reIndexer.reIndex(this.options, this.spaceKeys, this.progress);
            } else {
                this.reIndexer.reIndex(this.options, this.progress);
            }
        }
        finally {
            this.finishedReindexing = true;
        }
        this.stopTime = System.currentTimeMillis();
    }

    public String getName() {
        return "Rebuilding Search Index";
    }

    public String getCompactElapsedTime() {
        return GeneralUtil.getCompactDuration((this.stopTime == 0L ? System.currentTimeMillis() : this.stopTime) - this.startTime);
    }

    public Progress getProgress() {
        return this.progress;
    }

    public boolean isFinishedReindexing() {
        return this.finishedReindexing;
    }

    private int getTotal(Set<ReIndexOption> options) {
        if (this.searchQuery.isPresent() && this.spaceKeys.isEmpty()) {
            return -1;
        }
        if (this.spaceKeys.isEmpty()) {
            if (options == null || options.isEmpty() || ReIndexOption.isFullReindex(options)) {
                return this.searchableDao.getCountOfLatestSearchables();
            }
            if (options.size() == 1 && options.contains((Object)ReIndexOption.ATTACHMENT_ONLY)) {
                return this.searchableDao.getCountOfLatestSearchables(Attachment.class);
            }
            if (options.size() == 1 && options.contains((Object)ReIndexOption.USER_ONLY)) {
                return this.searchableDao.getCountOfLatestSearchables(PersonalInformation.class);
            }
            if (options.size() == 1 && options.contains((Object)ReIndexOption.CONTENT_ONLY)) {
                return this.searchableDao.getCountOfLatestSearchables() - this.searchableDao.getCountOfLatestSearchables(Attachment.class) - this.searchableDao.getCountOfLatestSearchables(PersonalInformation.class);
            }
            throw new UnsupportedOperationException();
        }
        return this.spaceKeys.stream().mapToInt(this.searchableDao::getCountOfLatestSearchables).sum();
    }

    public int getJobID() {
        return this.jobID;
    }

    public long getStartTime() {
        return this.startTime;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.confluence.pages.ancestors.AncestorsDao;
import com.atlassian.confluence.pages.ancestors.PageWithAncestors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageRepairWorker
implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PageRepairWorker.class);
    private static final int QUEUE_SIZE = 1000;
    private static final int BATCH_PROCESSING_SIZE = 10;
    private final BlockingQueue<PageWithAncestors> brokenPageQueue = new LinkedBlockingQueue<PageWithAncestors>(1000);
    private final AncestorsDao ancestorsDao;
    private static final PageWithAncestors STOP_PROCESSING = new PageWithAncestors(null, null);

    PageRepairWorker(AncestorsDao ancestorsDao) {
        this.ancestorsDao = ancestorsDao;
    }

    @Override
    public void run() {
        log.debug("Repair worker started repairing");
        try {
            boolean stopProcessing = false;
            while (!stopProcessing) {
                try {
                    ArrayList<PageWithAncestors> pagesToFix = new ArrayList<PageWithAncestors>();
                    for (int i = 0; i < 10; ++i) {
                        PageWithAncestors pageToFix = this.brokenPageQueue.take();
                        if (pageToFix == STOP_PROCESSING) {
                            log.debug("Received stop signal");
                            stopProcessing = true;
                            break;
                        }
                        pagesToFix.add(pageToFix);
                    }
                    if (pagesToFix.size() <= 0) continue;
                    this.fixPages(pagesToFix);
                }
                catch (InterruptedException e) {
                    throw e;
                }
                catch (Exception e) {
                    log.error("Exception: " + e.getMessage(), (Throwable)e);
                }
            }
        }
        catch (InterruptedException e) {
            log.info("PageRepairWorker was interrupted");
        }
        log.debug("Repair worker finished processing");
    }

    void addPageId(Long pageId, List<Long> ancestors) throws InterruptedException {
        this.brokenPageQueue.put(new PageWithAncestors(pageId, ancestors));
    }

    void noMoreBrokenPagesAreExpected() throws InterruptedException {
        this.brokenPageQueue.put(STOP_PROCESSING);
    }

    private void fixPages(List<PageWithAncestors> pagesToFix) {
        try {
            this.ancestorsDao.fixPages(pagesToFix);
            log.info("These pages have been repaired: " + pagesToFix.stream().map(page -> page.getPageId().toString()).collect(Collectors.joining(", ")));
        }
        catch (Exception e) {
            String idList = pagesToFix.stream().map(page -> Long.toString(page.getPageId())).collect(Collectors.joining(", "));
            log.warn("Pages " + idList + " were not repaired due to " + e.getMessage());
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content;

import com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao.RealContentAndPermissionsDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

class NextContentIdBatchGetter
implements Callable<List<SimpleContent>> {
    private final PlatformTransactionManager platformTransactionManager;
    private final RealContentAndPermissionsDao realContentAndPermissionsDao;
    private volatile Long lastProcessedContentId = null;
    private volatile boolean processingFinished = false;
    private final int processingLimit;

    public NextContentIdBatchGetter(PlatformTransactionManager platformTransactionManager, RealContentAndPermissionsDao realContentAndPermissionsDao, int processingLimit) {
        this.platformTransactionManager = platformTransactionManager;
        this.realContentAndPermissionsDao = realContentAndPermissionsDao;
        this.processingLimit = processingLimit;
    }

    @Override
    public List<SimpleContent> call() {
        if (this.processingFinished) {
            throw new IllegalStateException("NextContentGetter has already processed all the pages");
        }
        TransactionTemplate template = new TransactionTemplate(this.platformTransactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
        template.setReadOnly(true);
        return (List)template.execute(session -> {
            List<SimpleContent> pageList = this.realContentAndPermissionsDao.getSimplePageListWithIdGreaterThen(this.lastProcessedContentId, this.processingLimit);
            if (pageList.size() < this.processingLimit) {
                this.processingFinished = true;
            }
            if (pageList.size() > 0) {
                this.lastProcessedContentId = pageList.get(pageList.size() - 1).getId();
            }
            return pageList;
        });
    }

    public boolean hasNext() {
        return !this.processingFinished;
    }
}


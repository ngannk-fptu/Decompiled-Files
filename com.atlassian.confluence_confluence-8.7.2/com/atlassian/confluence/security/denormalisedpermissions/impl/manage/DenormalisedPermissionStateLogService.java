/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.manage;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.dao.DenormalisedChangeLogDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.dao.DenormalisedServiceStateDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateChangeLog;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

@Internal
public class DenormalisedPermissionStateLogService {
    private final DenormalisedServiceStateDao denormalisedServiceStateDao;
    private final PlatformTransactionManager transactionManager;
    private final DenormalisedChangeLogDao denormalisedChangeLogDao;

    public DenormalisedPermissionStateLogService(DenormalisedServiceStateDao denormalisedServiceStateDao, PlatformTransactionManager transactionManager, DenormalisedChangeLogDao denormalisedChangeLogDao) {
        this.denormalisedServiceStateDao = denormalisedServiceStateDao;
        this.transactionManager = transactionManager;
        this.denormalisedChangeLogDao = denormalisedChangeLogDao;
    }

    public DenormalisedPermissionServiceState getServiceState(DenormalisedServiceStateRecord.ServiceType serviceType) {
        DenormalisedServiceStateRecord record = this.getServiceStateRecord(serviceType);
        return record != null ? record.getState() : DenormalisedPermissionServiceState.DISABLED;
    }

    public DenormalisedServiceStateRecord getServiceStateRecord(DenormalisedServiceStateRecord.ServiceType serviceType) {
        return this.denormalisedServiceStateDao.getRecord(serviceType);
    }

    public List<DenormalisedServiceStateRecord> getAllStateRecords() {
        return this.denormalisedServiceStateDao.getAllRecords();
    }

    public void changeState(DenormalisedServiceStateRecord stateRecord, DenormalisedPermissionServiceState newState, StateChangeInformation.MessageLevel messageLevel, String message) {
        this.addMessageToStateLog(messageLevel, message);
        stateRecord.setState(newState);
        this.denormalisedServiceStateDao.saveRecord(stateRecord);
    }

    public void changeState(DenormalisedServiceStateRecord.ServiceType serviceType, DenormalisedPermissionServiceState newState, StateChangeInformation.MessageLevel messageLevel, String message, Long lastUpToDateTimestamp) {
        DenormalisedServiceStateRecord stateRecord = this.denormalisedServiceStateDao.getRecord(serviceType);
        this.addMessageToStateLog(messageLevel, message);
        stateRecord.setState(newState);
        if (lastUpToDateTimestamp != null) {
            stateRecord.setLastUpToDateTimestamp(lastUpToDateTimestamp);
        }
    }

    public void addMessageToStateLog(StateChangeInformation.MessageLevel messageLevel, String message) {
        this.denormalisedChangeLogDao.addMessage(messageLevel, message);
    }

    public List<StateChangeInformation> getStateChangeLog(int limit) {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        return (List)new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition).execute(status -> {
            List<DenormalisedServiceStateChangeLog> records = this.denormalisedChangeLogDao.getLastRecords(limit);
            return records.stream().map(r -> new StateChangeInformation(r.getId(), r.getMessage(), this.convertToMessageLevel(r.getMessageLevel()), r.getTimestamp())).collect(Collectors.toList());
        });
    }

    private StateChangeInformation.MessageLevel convertToMessageLevel(String messageLevelAsString) {
        try {
            return StateChangeInformation.MessageLevel.valueOf(messageLevelAsString);
        }
        catch (IllegalArgumentException e) {
            return StateChangeInformation.MessageLevel.ERROR;
        }
    }

    public void clearHistory() {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
        new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition).execute(status -> this.denormalisedChangeLogDao.clearHistory());
    }

    public void updateLastUpToDateTimeStamp(DenormalisedServiceStateRecord.ServiceType serviceType, long time) {
        DenormalisedServiceStateRecord stateRecord = this.denormalisedServiceStateDao.getRecord(serviceType);
        stateRecord.setLastUpToDateTimestamp(time);
    }
}


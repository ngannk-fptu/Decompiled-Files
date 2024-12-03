/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.migration.agent.dto.DuplicateEmailsConfigDto;
import com.atlassian.migration.agent.dto.InvalidEmailsConfigDto;
import com.atlassian.migration.agent.dto.ScanSummaryDto;
import com.atlassian.migration.agent.entity.ScanStatus;
import com.atlassian.migration.agent.entity.UserBaseScan;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.store.UserBaseScanStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.util.Optional;

public class UserBaseScanService {
    private final UserBaseScanStore userBaseScanStore;
    private final PluginTransactionTemplate ptx;
    private final GlobalEmailFixesConfigService globalEmailFixesConfigService;
    private final ClusterLockService clusterLockService;

    public UserBaseScanService(UserBaseScanStore userBaseScanStore, PluginTransactionTemplate ptx, GlobalEmailFixesConfigService globalEmailFixesConfigService, ClusterLockService clusterLockService) {
        this.userBaseScanStore = userBaseScanStore;
        this.ptx = ptx;
        this.globalEmailFixesConfigService = globalEmailFixesConfigService;
        this.clusterLockService = clusterLockService;
    }

    public void saveUserBaseScan(UserBaseScan userBaseScan) {
        this.ptx.write(() -> this.userBaseScanStore.save(userBaseScan));
    }

    public void updateStatus(String scanId, ScanStatus status) {
        this.ptx.write(() -> this.userBaseScanStore.updateStatus(scanId, status));
    }

    public void updateStatusAndCounts(String scanId, ScanStatus status, long invalidUsersCount, long duplicateUsersCount) {
        this.ptx.write(() -> this.userBaseScanStore.updateStatusAndCounts(scanId, status, invalidUsersCount, duplicateUsersCount));
    }

    public Optional<UserBaseScan> get(String scanId) {
        return this.userBaseScanStore.get(scanId);
    }

    public Optional<UserBaseScan> getLatestStarted() {
        return this.userBaseScanStore.getLatestStarted();
    }

    public ScanSummaryDto getScanSummary() {
        Optional<UserBaseScan> latestStartedScan = this.getLatestStartedScan();
        DuplicateEmailsConfigDto duplicateEmailsConfig = this.globalEmailFixesConfigService.getDuplicateEmailsConfig();
        InvalidEmailsConfigDto invalidEmailsConfig = this.globalEmailFixesConfigService.getInvalidEmailsConfig();
        if (latestStartedScan.isPresent()) {
            UserBaseScan latestScan = latestStartedScan.get();
            return new ScanSummaryDto(latestScan.getStatus(), latestScan.getStarted(), latestScan.getFinished(), latestScan.getInvalidUsers(), latestScan.getDuplicateUsers(), latestScan.getId(), duplicateEmailsConfig, invalidEmailsConfig);
        }
        return new ScanSummaryDto(duplicateEmailsConfig, invalidEmailsConfig);
    }

    private Optional<UserBaseScan> getLatestStartedScan() {
        Optional<UserBaseScan> latestStarted = this.getLatestStarted();
        return latestStarted.map(this::validateScanStatus);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UserBaseScan validateScanStatus(UserBaseScan scan) {
        ClusterLock lock;
        if (scan.getStatus() == ScanStatus.IN_PROGRESS && (lock = this.clusterLockService.getLockForName("com.atlassian.migration.userbase.scan")).tryLock()) {
            try {
                UserBaseScan stateWithLock = this.get(scan.getId()).orElseThrow(() -> new IllegalStateException("Could not find userBase scan of id: " + scan.getId()));
                if (stateWithLock.getStatus() == ScanStatus.IN_PROGRESS) {
                    stateWithLock.setStatus(ScanStatus.FAILED);
                }
                UserBaseScan userBaseScan = stateWithLock;
                return userBaseScan;
            }
            finally {
                lock.unlock();
            }
        }
        return scan;
    }
}


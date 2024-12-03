/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.email.EmailCheckContext
 *  com.atlassian.cmpt.check.email.EmailData
 *  com.atlassian.cmpt.check.email.EmailDuplicate
 *  com.atlassian.cmpt.check.email.EmailDuplicateChecker
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.email.EmailCheckContext;
import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.cmpt.check.email.EmailDuplicate;
import com.atlassian.cmpt.check.email.EmailDuplicateChecker;
import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.agent.entity.ScanStatus;
import com.atlassian.migration.agent.entity.UserBaseScan;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.email.EmailsSource;
import com.atlassian.migration.agent.service.email.IncorrectEmailService;
import com.atlassian.migration.agent.service.email.InvalidEmailValidator;
import com.atlassian.migration.agent.service.email.MostFrequentDomainService;
import com.atlassian.migration.agent.service.email.NoValidEmailsException;
import com.atlassian.migration.agent.service.email.UserBaseScanService;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class UserBaseScanRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserBaseScanRunner.class);
    public static final String USER_BASE_SCAN_LOCK = "com.atlassian.migration.userbase.scan";
    private final UserGroupExtractFacade userGroupExtractFacade;
    private final IncorrectEmailService incorrectEmailService;
    private final ClusterLockService clusterLockService;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final UserBaseScanService userBaseScanService;
    private final InvalidEmailValidator invalidEmailValidator;
    private final MostFrequentDomainService mostFrequentDomainService;

    public UserBaseScanRunner(UserGroupExtractFacade userGroupExtractFacade, IncorrectEmailService incorrectEmailService, ClusterLockService clusterLockService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, UserBaseScanService userBaseScanService, InvalidEmailValidator invalidEmailValidator, MostFrequentDomainService mostFrequentDomainService) {
        this.userGroupExtractFacade = userGroupExtractFacade;
        this.incorrectEmailService = incorrectEmailService;
        this.clusterLockService = clusterLockService;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.userBaseScanService = userBaseScanService;
        this.invalidEmailValidator = invalidEmailValidator;
        this.mostFrequentDomainService = mostFrequentDomainService;
    }

    public void startUserBaseScan(String cloudId) {
        new Thread(this.scanUserBaseWithClusterLock(cloudId)).start();
    }

    private Runnable scanUserBaseWithClusterLock(String cloudId) {
        return () -> {
            log.debug("Attempting to start a user base scan");
            ClusterLock lock = this.clusterLockService.getLockForName(USER_BASE_SCAN_LOCK);
            if (lock.tryLock()) {
                String scanId = UUID.randomUUID().toString();
                log.debug("Obtained a cluster lock for {}", (Object)scanId);
                try {
                    this.scanUserBaseForIncorrectEmails(scanId, cloudId);
                }
                catch (Exception ex) {
                    if (this.isExceptionNoValidEmailsException(ex)) {
                        log.info("No valid emails found in the user base for scanId: {}", (Object)scanId, (Object)ex);
                        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserBaseScanErrorEvent(ex.getMessage(), cloudId, scanId));
                        this.userBaseScanService.updateStatus(scanId, ScanStatus.NO_VALID_EMAILS);
                    } else {
                        log.error("Exception: {} during a user base scan: {}", (Object)ex, (Object)scanId);
                        this.userBaseScanService.updateStatus(scanId, ScanStatus.FAILED);
                        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserBaseScanErrorEvent(ex.getMessage(), cloudId, scanId));
                    }
                }
                finally {
                    lock.unlock();
                }
            }
        };
    }

    void scanUserBaseForIncorrectEmails(String scanId, String cloudId) {
        log.info("Started user base scan: {}", (Object)scanId);
        long startTime = System.nanoTime();
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserBaseScanStartedEvent(scanId));
        this.userBaseScanService.saveUserBaseScan(new UserBaseScan(scanId, ScanStatus.IN_PROGRESS, 0L, 0L, Instant.now()));
        List<MigrationUser> allUsers = this.userGroupExtractFacade.getAllUsers();
        if (!this.mostFrequentDomainService.isMostFrequentDomainNameCached()) {
            this.mostFrequentDomainService.getMostFrequentDomainName(() -> allUsers.stream().map(MigrationUser::getEmail).collect(Collectors.toList()), EmailsSource.PREFETCHED, cloudId);
        }
        EmailCheckContext emailCheckContext = this.extractUsersToValidate(allUsers);
        log.info("Extracted :{} users for {} scan", (Object)emailCheckContext.emails.size(), (Object)scanId);
        List<EmailData> invalidEmails = this.invalidEmailValidator.getInvalidEmails(scanId, cloudId, allUsers);
        log.info("Found {} invalid emails for {} scan", (Object)invalidEmails.size(), (Object)scanId);
        List<EmailDuplicate> duplicateEmails = this.getDuplicateEmails(emailCheckContext, invalidEmails);
        log.info("Found {} duplicate emails for {} scan", (Object)duplicateEmails.size(), (Object)scanId);
        Map<String, String> totalUserNameToEmailMap = allUsers.stream().collect(Collectors.toMap(MigrationUser::getUsername, MigrationUser::getEmail));
        Map<String, String> totalUserNameToUserkeyMap = allUsers.stream().collect(Collectors.toMap(MigrationUser::getUsername, MigrationUser::getUserKey));
        this.incorrectEmailService.replaceDuplicateAndInvalidEmails(scanId, invalidEmails, duplicateEmails, totalUserNameToEmailMap, totalUserNameToUserkeyMap, cloudId);
        int totalDuplicates = duplicateEmails.stream().mapToInt(email -> email.ids.size()).sum();
        this.userBaseScanService.updateStatusAndCounts(scanId, ScanStatus.FINISHED, invalidEmails.size(), totalDuplicates);
        long endTime = System.nanoTime();
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserBaseScanFinishedEvent(scanId, emailCheckContext.emails.size(), invalidEmails.size(), totalDuplicates, (endTime - startTime) / 1000000L));
        log.info("Finished user base scan: {}", (Object)scanId);
    }

    private EmailCheckContext extractUsersToValidate(List<MigrationUser> allUsers) {
        List emailData = allUsers.stream().map(user -> new EmailData(user.getUsername(), IdentityAcceptedEmailValidator.cleanse((String)user.getEmail()))).collect(Collectors.toList());
        return new EmailCheckContext(emailData);
    }

    private List<EmailDuplicate> getDuplicateEmails(EmailCheckContext checkContext, List<EmailData> invalidEmails) {
        CheckResult checkResult = new EmailDuplicateChecker().check(checkContext);
        List duplicateEmails = EmailDuplicateChecker.retrieveEmailDuplicates((Map)checkResult.details);
        Set mappedInvalidEmails = invalidEmails.stream().map(emailData -> emailData.email).collect(Collectors.toSet());
        return duplicateEmails.stream().filter(duplicate -> !mappedInvalidEmails.contains(duplicate.email)).collect(Collectors.toList());
    }

    private boolean isExceptionNoValidEmailsException(Exception ex) {
        return ExceptionUtils.getRootCause((Throwable)ex) instanceof NoValidEmailsException;
    }
}


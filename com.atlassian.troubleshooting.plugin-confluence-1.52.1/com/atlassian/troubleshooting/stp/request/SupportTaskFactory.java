/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.audit.Auditor;
import com.atlassian.troubleshooting.stp.persistence.ZipConfiguration;
import com.atlassian.troubleshooting.stp.persistence.ZipConfigurationRepository;
import com.atlassian.troubleshooting.stp.request.CreateSupportRequestMonitor;
import com.atlassian.troubleshooting.stp.request.CreateSupportRequestTask;
import com.atlassian.troubleshooting.stp.request.SupportRequestCreationRequest;
import com.atlassian.troubleshooting.stp.request.SupportZipCreationRequest;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.security.UserService;
import com.atlassian.troubleshooting.stp.spi.HostApplication;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipTask;
import com.atlassian.troubleshooting.stp.zip.SupportZipFileNameGenerator;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class SupportTaskFactory {
    private final EventPublisher eventPublisher;
    private final HostApplication hostApplication;
    private final MailUtility mailUtility;
    private final SupportApplicationInfo applicationInfo;
    private final SupportZipFileNameGenerator fileNameGenerator;
    private final UserService userService;
    private final TimeZoneManager timeZoneManager;
    private final ZipConfigurationRepository zipConfigurationRepository;
    private final Auditor auditor;

    @Autowired
    public SupportTaskFactory(@Nonnull SupportApplicationInfo applicationInfo, @Nonnull HostApplication hostApplication, @Nonnull MailUtility mailUtility, @Nonnull EventPublisher eventPublisher, @Nonnull SupportZipFileNameGenerator fileNameGenerator, @Nonnull UserService userService, @Nonnull Auditor auditor, @Nonnull TimeZoneManager timeZoneManager, @Nonnull ZipConfigurationRepository zipConfigurationRepository) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.hostApplication = Objects.requireNonNull(hostApplication);
        this.mailUtility = Objects.requireNonNull(mailUtility);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.fileNameGenerator = Objects.requireNonNull(fileNameGenerator);
        this.userService = Objects.requireNonNull(userService);
        this.auditor = Objects.requireNonNull(auditor);
        this.timeZoneManager = Objects.requireNonNull(timeZoneManager);
        this.zipConfigurationRepository = Objects.requireNonNull(zipConfigurationRepository);
    }

    @Nonnull
    public CreateSupportRequestTask createSupportRequestTask(SupportRequestCreationRequest request) {
        return new CreateSupportRequestTask(request, this.applicationInfo, this.hostApplication, this.mailUtility, this.eventPublisher, new CreateSupportRequestMonitor(), this.createSupportZipTask(request.getSupportZipCreationRequest()), this.userService.getUsername().orElse(null));
    }

    @Nonnull
    private CreateSupportZipTask createSupportZipTask(SupportZipCreationRequest request) {
        this.zipConfigurationRepository.saveConfiguration(ZipConfiguration.from(request));
        return new CreateSupportZipTask(request, this.fileNameGenerator, this.applicationInfo, this.hostApplication, new CreateSupportZipMonitor(), this.userService.getUsername().orElse(null), this.auditor, this.timeZoneManager.getDefaultTimeZone());
    }

    @Nonnull
    public CreateSupportZipTask createSupportZipTask(SupportZipRequest supportZipRequest) {
        return this.createSupportZipTask(this.asSupportZipCreationRequest(supportZipRequest));
    }

    private SupportZipCreationRequest asSupportZipCreationRequest(SupportZipRequest supportZipRequest) {
        Iterable bundles = supportZipRequest.getItems().stream().map(this::getBundle).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        return SupportZipCreationRequest.builder().bundles(bundles).limitFileSizes(supportZipRequest.isLimitFileSizes()).fileConstraintSize(supportZipRequest.getFileConstraintSize()).fileConstraintLastModified(supportZipRequest.getFileConstraintLastModified()).withClusterTaskId(supportZipRequest.getClusterTaskId()).build();
    }

    private Optional<SupportZipBundle> getBundle(String bundleKey) {
        return this.applicationInfo.getSupportZipBundles().stream().filter(bundle -> bundle.getKey().equals(bundleKey)).findFirst();
    }
}


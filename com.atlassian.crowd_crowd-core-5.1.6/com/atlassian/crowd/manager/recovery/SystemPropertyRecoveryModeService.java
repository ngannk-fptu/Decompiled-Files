/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.StringSystemProperty
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.recovery.RecoveryModeActivatedEvent
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Suppliers
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.recovery;

import com.atlassian.crowd.common.properties.StringSystemProperty;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.recovery.RecoveryModeActivatedEvent;
import com.atlassian.crowd.manager.recovery.RecoveryModeDirectory;
import com.atlassian.crowd.manager.recovery.RecoveryModeService;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyRecoveryModeService
implements RecoveryModeService {
    private static final Logger logger = LoggerFactory.getLogger(SystemPropertyRecoveryModeService.class);
    static final String RECOVERY_USERNAME = "recovery_admin";
    static final String RECOVERY_DISPLAY_NAME = "Recovery Admin User";
    static final String RECOVERY_EMAIL = "@";
    @VisibleForTesting
    public static final StringSystemProperty PROP_RECOVERY_PASSWORD = new StringSystemProperty("atlassian.recovery.password", "");
    private final DirectoryInstanceLoader loader;
    private final EventPublisher eventPublisher;
    private final AtomicBoolean recoveryModeEventRaised = new AtomicBoolean();
    private final Supplier<String> recoveryPasswordRef = Suppliers.memoize(() -> ((StringSystemProperty)PROP_RECOVERY_PASSWORD).getValue());
    private final Supplier<RecoveryModeDirectory> recoveryDirectoryRef = Suppliers.memoize(() -> new RecoveryModeDirectory(RECOVERY_USERNAME, this.recoveryPasswordRef.get()));

    public SystemPropertyRecoveryModeService(DirectoryInstanceLoader loader, EventPublisher eventPublisher) {
        this.loader = (DirectoryInstanceLoader)Preconditions.checkNotNull((Object)loader, (Object)"loader");
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher, (Object)"eventPublisher");
    }

    @Override
    public boolean isRecoveryModeOn() {
        boolean recoveryModeOn;
        boolean bl = recoveryModeOn = this.isRecoveryFeatureOn() && StringUtils.isNotBlank((CharSequence)this.getRecoveryPassword());
        if (this.recoveryModeEventRaised.compareAndSet(false, true)) {
            if (recoveryModeOn) {
                logger.info("Recovery mode is ON");
                this.eventPublisher.publish((Object)new RecoveryModeActivatedEvent(RECOVERY_USERNAME, this.getRecoveryDirectory()));
            } else if (StringUtils.isNotBlank((CharSequence)this.getRecoveryPassword()) && !this.isRecoveryFeatureOn()) {
                logger.warn("Attempt has been made to activate recovery mode, but the host application does not support it yet");
            }
        }
        return recoveryModeOn;
    }

    @Override
    public Directory getRecoveryDirectory() {
        Preconditions.checkState((boolean)this.isRecoveryModeOn(), (Object)"Recovery Mode is not ON");
        return this.recoveryDirectoryRef.get();
    }

    @Override
    public String getRecoveryUsername() {
        Preconditions.checkState((boolean)this.isRecoveryModeOn(), (Object)"Recovery Mode is not ON");
        return RECOVERY_USERNAME;
    }

    @Override
    public boolean isRecoveryDirectory(Directory directory) {
        return directory.getId().equals(-2L);
    }

    private boolean isRecoveryFeatureOn() {
        return this.loader.canLoad(RecoveryModeDirectory.class.getName());
    }

    private String getRecoveryPassword() {
        return this.recoveryPasswordRef.get();
    }
}


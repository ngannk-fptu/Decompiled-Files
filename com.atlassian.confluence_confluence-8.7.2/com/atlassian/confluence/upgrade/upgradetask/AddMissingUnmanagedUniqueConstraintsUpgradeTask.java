/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.event.events.plugin.AsyncPluginFrameworkStartedEvent;
import com.atlassian.confluence.internal.upgrade.constraint.ConstraintChecker;
import com.atlassian.confluence.internal.upgrade.constraint.ConstraintCreator;
import com.atlassian.confluence.internal.upgrade.constraint.MissingConstraintsRemediationEvent;
import com.atlassian.confluence.internal.upgrade.constraint.UniqueConstraintAddition;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.Deduper;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.JustFailDedupeStrategy;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.KeepBiggestIdDedupeStrategy;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.KeepSmallestIdDedupeStrategy;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.MergeToSmallestIdDedupeStrategy;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddMissingUnmanagedUniqueConstraintsUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(AddMissingUnmanagedUniqueConstraintsUpgradeTask.class);
    private static final String BUILD_NUMBER = "8301";
    private final ConstraintChecker constraintChecker;
    private final Deduper deduper;
    private final ConstraintCreator constraintCreator;
    private final EventPublisher eventPublisher;
    private final AtomicReference<MissingConstraintsRemediationEvent> eventHolder = new AtomicReference();
    private final AtomicBoolean hasPluginFrameworkStarted = new AtomicBoolean(false);

    public AddMissingUnmanagedUniqueConstraintsUpgradeTask(ConstraintChecker constraintChecker, Deduper deduper, ConstraintCreator constraintCreator, EventPublisher eventPublisher) {
        this.constraintChecker = Objects.requireNonNull(constraintChecker);
        this.deduper = Objects.requireNonNull(deduper);
        this.constraintCreator = Objects.requireNonNull(constraintCreator);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public String getShortDescription() {
        return "Add unmanaged unique constraints missing in CONFSERVER-38706 and CONFSERVER-58261";
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void cleanup() {
        this.eventPublisher.unregister((Object)this);
    }

    public void doUpgrade() throws Exception {
        log.info("Finding missing unique constraints");
        int existingCount = 0;
        int addedCount = 0;
        int errorCount = 0;
        for (UniqueConstraintAddition uniqueConstraintAddition : this.findAllConstraints()) {
            try {
                if (uniqueConstraintAddition.addIfMissing(this.constraintChecker, this.deduper, this.constraintCreator)) {
                    ++addedCount;
                    continue;
                }
                ++existingCount;
            }
            catch (UpgradeException ue) {
                ++errorCount;
                log.warn("Error fixing a missing constraint [{}]", (Object)uniqueConstraintAddition.toString(), (Object)ue);
            }
        }
        if (this.hasPluginFrameworkStarted.get()) {
            this.eventPublisher.publish((Object)new MissingConstraintsRemediationEvent(existingCount, addedCount, errorCount, "A"));
        } else {
            this.eventHolder.set(new MissingConstraintsRemediationEvent(existingCount, addedCount, errorCount, "U"));
        }
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(AsyncPluginFrameworkStartedEvent ignored) {
        this.hasPluginFrameworkStarted.set(true);
        MissingConstraintsRemediationEvent event = this.eventHolder.getAndSet(null);
        if (event != null) {
            this.eventPublisher.publish((Object)event);
        }
    }

    private List<UniqueConstraintAddition> findAllConstraints() {
        ArrayList<UniqueConstraintAddition> uniqueConstraints = new ArrayList<UniqueConstraintAddition>();
        uniqueConstraints.add(new UniqueConstraintAddition("unq_lwr_username", "user_mapping", Collections.singletonList("lower_username"), "user_key", new JustFailDedupeStrategy()));
        uniqueConstraints.add(new UniqueConstraintAddition("cwd_group_name_dir_id", "cwd_group", Arrays.asList("lower_group_name", "directory_id"), "id", new MergeToSmallestIdDedupeStrategy("cwd_group", "id", Arrays.asList(new MergeToSmallestIdDedupeStrategy.ReferencedTable("cwd_group_attribute", "group_id"), new MergeToSmallestIdDedupeStrategy.ReferencedTable("cwd_membership", "parent_id"), new MergeToSmallestIdDedupeStrategy.ReferencedTable("cwd_membership", "child_group_id")))));
        uniqueConstraints.add(new UniqueConstraintAddition("cwd_user_name_dir_id", "cwd_user", Arrays.asList("lower_user_name", "directory_id"), "id", new JustFailDedupeStrategy()));
        if (this.deduper.multipleNullsNotAllowed()) {
            uniqueConstraints.add(new UniqueConstraintAddition("cwd_unique_membership", "cwd_membership", Arrays.asList("parent_id", "child_group_id", "child_user_id"), "id", new KeepSmallestIdDedupeStrategy("cwd_membership", "id")));
        } else {
            uniqueConstraints.add(new UniqueConstraintAddition("cwd_unique_user_membership", "cwd_membership", Arrays.asList("parent_id", "child_user_id"), "id", new KeepSmallestIdDedupeStrategy("cwd_membership", "id")));
            uniqueConstraints.add(new UniqueConstraintAddition("cwd_unique_group_membership", "cwd_membership", Arrays.asList("parent_id", "child_group_id"), "id", new KeepSmallestIdDedupeStrategy("cwd_membership", "id")));
        }
        uniqueConstraints.add(new UniqueConstraintAddition("cwd_unique_grp_attr", "cwd_group_attribute", Arrays.asList("directory_id", "group_id", "attribute_name", "attribute_lower_value"), "id", new KeepSmallestIdDedupeStrategy("cwd_group_attribute", "id")));
        uniqueConstraints.add(new UniqueConstraintAddition("cwd_unique_usr_attr", "cwd_user_attribute", Arrays.asList("directory_id", "user_id", "attribute_name", "attribute_lower_value"), "id", new KeepSmallestIdDedupeStrategy("cwd_user_attribute", "id")));
        uniqueConstraints.add(new UniqueConstraintAddition("cps_unique_type", "CONTENT_PERM_SET", Arrays.asList("CONTENT_ID", "CONT_PERM_TYPE"), "ID", new MergeToSmallestIdDedupeStrategy("CONTENT_PERM_SET", "ID", Collections.singletonList(new MergeToSmallestIdDedupeStrategy.ReferencedTable("CONTENT_PERM", "CPS_ID")))));
        if (this.deduper.multipleNullsNotAllowed()) {
            uniqueConstraints.add(new UniqueConstraintAddition("cp_unique_user_groups", "CONTENT_PERM", Arrays.asList("CPS_ID", "CP_TYPE", "USERNAME", "GROUPNAME"), "ID", new KeepSmallestIdDedupeStrategy("CONTENT_PERM", "ID")));
        } else {
            uniqueConstraints.add(new UniqueConstraintAddition("cp_unique_user", "CONTENT_PERM", Arrays.asList("CPS_ID", "CP_TYPE", "USERNAME"), "ID", new KeepSmallestIdDedupeStrategy("CONTENT_PERM", "ID")));
            uniqueConstraints.add(new UniqueConstraintAddition("cp_unique_group", "CONTENT_PERM", Arrays.asList("CPS_ID", "CP_TYPE", "GROUPNAME"), "ID", new KeepSmallestIdDedupeStrategy("CONTENT_PERM", "ID")));
        }
        uniqueConstraints.add(new UniqueConstraintAddition("bandana_unique_key", "BANDANA", Arrays.asList("BANDANACONTEXT", "BANDANAKEY"), "BANDANAID", new KeepBiggestIdDedupeStrategy("BANDANA", "BANDANAID")));
        return uniqueConstraints;
    }
}


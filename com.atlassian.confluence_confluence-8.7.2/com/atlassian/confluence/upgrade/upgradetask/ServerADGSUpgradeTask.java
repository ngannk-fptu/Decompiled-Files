/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.setup.bandana.BandanaPersisterSupport;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaRecord;
import com.atlassian.confluence.setup.bandana.persistence.dao.hibernate.HibernateConfluenceBandanaRecordDao;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.core.util.ClassLoaderUtils;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerADGSUpgradeTask
extends AbstractDeferredRunUpgradeTask
implements DatabaseUpgradeTask {
    private static Logger logger = LoggerFactory.getLogger(ServerADGSUpgradeTask.class);
    private static final String THREAHOLD_PROP = "confluence.upgrade.ADGS.threahold";
    private static final String SKIP_UPGRADE_PROP = "confluence.upgrade.ADGS.skip";
    private int deferUpgradeThreahold;
    private final BandanaPersisterSupport bandanaPersisterSupport;
    private final HibernateConfluenceBandanaRecordDao hibernateConfluenceBandanaRecordDao;
    private final Properties oldAUIColourProperties;
    private final Properties newAUIColourProperties;
    private MigrateStatistic statistic;

    public ServerADGSUpgradeTask(BandanaPersisterSupport bandanaPersisterSupport, HibernateConfluenceBandanaRecordDao hibernateConfluenceBandanaRecordDao) throws IOException {
        this.bandanaPersisterSupport = bandanaPersisterSupport;
        this.hibernateConfluenceBandanaRecordDao = hibernateConfluenceBandanaRecordDao;
        this.statistic = new MigrateStatistic();
        this.deferUpgradeThreahold = Integer.getInteger(THREAHOLD_PROP, 1000);
        this.newAUIColourProperties = new Properties();
        this.newAUIColourProperties.load(ClassLoaderUtils.getResourceAsStream((String)"aui-default-colours.properties", ServerADGSUpgradeTask.class));
        this.oldAUIColourProperties = new Properties();
        this.oldAUIColourProperties.load(ClassLoaderUtils.getResourceAsStream((String)"aui-default-colours-old.properties", ServerADGSUpgradeTask.class));
    }

    @VisibleForTesting
    public MigrateStatistic getStatistic() {
        return this.statistic;
    }

    @VisibleForTesting
    public Properties getOldAUIColourProperties() {
        return this.oldAUIColourProperties;
    }

    @VisibleForTesting
    public Properties getNewAUIColourProperties() {
        return this.newAUIColourProperties;
    }

    public String getBuildNumber() {
        return "7601";
    }

    public String getShortDescription() {
        return "Upgrade custom colour scheme to ADGS";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        long numberOfItemToUpgrade = this.hibernateConfluenceBandanaRecordDao.countWithKey("atlassian.confluence.colour.scheme");
        if (numberOfItemToUpgrade > (long)this.deferUpgradeThreahold) {
            logger.info("Defer ADGS migration due number of item in DB is too big: {}", (Object)numberOfItemToUpgrade);
            logger.info("Notice we could run upgrading task manually via : /admin/do-force-upgrade.action");
            this.statistic.setDeferMigrate(true);
            this.setUpgradeRequired(true);
            return;
        }
        logger.info("ADGS migration with number of item in DB: {}", (Object)numberOfItemToUpgrade);
        this.upgrade();
    }

    public void doDeferredUpgrade() throws Exception {
        this.upgrade();
    }

    private boolean shouldRun() {
        boolean shouldSkip = Boolean.getBoolean(SKIP_UPGRADE_PROP);
        return !shouldSkip;
    }

    private void upgrade() {
        logger.info("ADGS migrating ....");
        if (!this.shouldRun()) {
            logger.warn("ServerADGSUpgradeTask was skipped");
            return;
        }
        Iterable<ConfluenceBandanaRecord> records = this.hibernateConfluenceBandanaRecordDao.findAllWithKey("atlassian.confluence.colour.scheme");
        Stream<ConfluenceBandanaRecord> streamRecord = StreamSupport.stream(records.spliterator(), false);
        streamRecord.forEach(bandanaRecord -> {
            if (bandanaRecord == null) {
                logger.debug("Skip a record because it is null");
                return;
            }
            try {
                StringReader stringReader = new StringReader(bandanaRecord.getValue());
                BaseColourScheme baseColourScheme = (BaseColourScheme)this.bandanaPersisterSupport.getSerializer(null).deserialize(stringReader);
                this.migrateBandanaRecord((ConfluenceBandanaRecord)bandanaRecord, baseColourScheme);
            }
            catch (IOException e) {
                String errorMsg = String.format("Could not load colour scheme for an record name %s at context %s and key %s", bandanaRecord.getValue(), bandanaRecord.getContext(), bandanaRecord.getKey());
                logger.error(errorMsg, (Throwable)e);
            }
        });
        logger.info("ADGS migrating ....Done");
    }

    private void migrateBandanaRecord(ConfluenceBandanaRecord bandanaRecord, BaseColourScheme baseColourScheme) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Migrate for record[context:{}, key:{}] is migrate", (Object)bandanaRecord.getContext(), (Object)bandanaRecord.getKey());
        }
        if (baseColourScheme == null) {
            logger.debug("Skip migration because 'baseColourScheme' is null");
            return;
        }
        Enumeration<?> propertyNames = this.newAUIColourProperties.propertyNames();
        boolean needSave = false;
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String)propertyNames.nextElement();
            String value = baseColourScheme.get(propertyName);
            if (value == null) {
                logger.debug("There some new colour scheme which does not exist in old colour scheme will skip it");
                continue;
            }
            String oldValue = this.oldAUIColourProperties.getProperty(propertyName);
            if (!value.equalsIgnoreCase(oldValue)) continue;
            logger.debug("Colour with key {} in record[context:{}, key:{}] is migrate", new Object[]{propertyName, bandanaRecord.getContext(), bandanaRecord.getKey()});
            baseColourScheme.set(propertyName, this.newAUIColourProperties.getProperty(propertyName));
            needSave = true;
        }
        if (needSave) {
            logger.debug("Saving bandana record with new value");
            StringWriter writer = new StringWriter();
            this.bandanaPersisterSupport.getSerializer(null).serialize(baseColourScheme, writer);
            bandanaRecord.setValue(writer.toString());
            this.hibernateConfluenceBandanaRecordDao.saveOrUpdate(bandanaRecord);
            this.statistic.increaseMigrateItem();
        }
    }

    public static class MigrateStatistic {
        private boolean isDeferMigrate;
        private long migratedItems;

        long increaseMigrateItem() {
            ++this.migratedItems;
            return this.migratedItems;
        }

        public long getMigratedItems() {
            return this.migratedItems;
        }

        public boolean isDeferMigrate() {
            return this.isDeferMigrate;
        }

        public void setDeferMigrate(boolean deferMigrate) {
            this.isDeferMigrate = deferMigrate;
        }
    }
}


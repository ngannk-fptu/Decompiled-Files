/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.HealthCheckMessage;
import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.health.checks.DbHealthCheckHelper;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbConnectionHealthCheck
extends HealthCheckTemplate {
    private static final Logger log = LoggerFactory.getLogger(DbConnectionHealthCheck.class);
    private final SingleConnectionProvider databaseHelper;
    private final HibernateConfig hibernateConfig;
    private final DbHealthCheckHelper dbHealthCheckHelper;

    protected DbConnectionHealthCheck(SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig, DbHealthCheckHelper dbHealthCheckHelper) {
        super(Collections.emptyList());
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.dbHealthCheckHelper = Objects.requireNonNull(dbHealthCheckHelper);
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return Collections.singleton(LifecyclePhase.BOOTSTRAP_END);
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        if (this.isHibernateSetUp(this.hibernateConfig)) {
            List<HealthCheckResult> list;
            block9: {
                Connection c = this.databaseHelper.getConnection(this.hibernateConfig.getHibernateProperties());
                try {
                    list = Collections.emptyList();
                    if (c == null) break block9;
                }
                catch (Throwable throwable) {
                    try {
                        if (c != null) {
                            try {
                                c.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (SQLException e) {
                        HealthCheckMessage message = this.getErrorMessage();
                        return HealthCheckResult.fail(this, new Event(JohnsonEventType.DATABASE.eventType(), "Database connection failed", message.asHtml(), EventLevel.get((String)"fatal")), UrlBuilder.createURL("https://confluence.atlassian.com/x/KZHPO"), "database-connection-failed", message.asText());
                    }
                }
                c.close();
            }
            return list;
        }
        return Collections.emptyList();
    }

    private boolean isHibernateSetUp(HibernateConfig hibernateConfig) {
        return Optional.ofNullable((String)hibernateConfig.getHibernateProperties().get("hibernate.setup")).map("true"::equals).orElse(false);
    }

    private HealthCheckMessage getErrorMessage() {
        HealthCheckMessage.Builder msgBuilder = new HealthCheckMessage.Builder();
        msgBuilder.append("Confluence failed to establish a connection to your database.").lineBreak();
        msgBuilder.append("This could be because:").lineBreak();
        msgBuilder.appendList("Your database isn't running", "The configuration of your " + this.dbHealthCheckHelper.getJDBCUrlConfigLocationDescription() + " file is incorrect (user, password, or database URL etc.)", "There is a network issue between Confluence and your database (e.g. firewall, database doesn't allow remote access etc.)");
        msgBuilder.lineBreak();
        msgBuilder.append("There are several other solutions you can try, review our documentation and see what works for you.").lineBreak();
        return msgBuilder.build();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.pac;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.Cost;
import com.atlassian.marketplace.client.api.EnumWithKey;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.pac.AvailableAddonSummaryWithVersion;
import com.atlassian.upm.pac.AvailableAddonWithVersionBase;
import com.atlassian.upm.pac.PacClient;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonMarketplaceQueries {
    private static final Logger logger = LoggerFactory.getLogger(AddonMarketplaceQueries.class);
    private final PacClient pacClient;
    private final SysPersisted sysPersisted;

    public AddonMarketplaceQueries(PacClient pacClient, SysPersisted sysPersisted) {
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
    }

    public PacClient getClient() {
        return this.pacClient;
    }

    public AvailableAddonSummaries getAvailableAddonSummaries(AddonQuery.View view, int offset, String category, String costKey) {
        return this.getAvailableAddonSummaries(view, offset, category, costKey, Option.none(String.class));
    }

    public AvailableAddonSummaries getAvailableAddonSummaries(AddonQuery.View view, int offset, String category, String costKey, Option<String> searchText) {
        if (this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
            return AvailableAddonSummaries.disabled();
        }
        AddonQuery.Builder query = AddonQuery.builder().bounds(QueryBounds.offset(offset)).searchText(UpmFugueConverters.toJavaOptional(searchText)).withVersion(true);
        if (StringUtils.isNotBlank((CharSequence)category)) {
            query.categoryNames(Collections.singletonList(category));
        }
        if (!searchText.isDefined()) {
            query.view(Optional.of(view));
        }
        if (StringUtils.isNotBlank((CharSequence)costKey)) {
            query.cost((Optional)EnumWithKey.Parser.forType(Cost.class).safeValueForKey(costKey));
        }
        try {
            Page<AddonSummary> addons = this.getClient().findPlugins(query.build());
            return AvailableAddonSummaries.successfulResult(addons);
        }
        catch (MpacException e) {
            logger.warn("Marketplace product query failed: " + e);
            logger.debug(e.toString(), (Throwable)e);
            return AvailableAddonSummaries.unreachable();
        }
    }

    public static class AvailableAddonSummaries {
        public final Iterable<AvailableAddonWithVersionBase> addons;
        public final Page<AddonSummary> sourceAddons;
        public final boolean mpacDisabled;
        public final boolean mpacUnreachable;

        private AvailableAddonSummaries(Page<AddonSummary> sourceAddons, boolean mpacDisabled, boolean mpacUnreachable) {
            this.addons = Collections.unmodifiableList(StreamSupport.stream(sourceAddons.spliterator(), false).map(AvailableAddonSummaryWithVersion::fromAddonSummary).filter(Objects::nonNull).collect(Collectors.toList()));
            this.sourceAddons = sourceAddons;
            this.mpacDisabled = mpacDisabled;
            this.mpacUnreachable = mpacUnreachable;
        }

        static AvailableAddonSummaries successfulResult(Page<AddonSummary> addons) {
            return new AvailableAddonSummaries(addons, false, false);
        }

        static AvailableAddonSummaries disabled() {
            return new AvailableAddonSummaries(Page.empty(AddonSummary.class), true, false);
        }

        static AvailableAddonSummaries unreachable() {
            return new AvailableAddonSummaries(Page.empty(AddonSummary.class), false, true);
        }
    }
}


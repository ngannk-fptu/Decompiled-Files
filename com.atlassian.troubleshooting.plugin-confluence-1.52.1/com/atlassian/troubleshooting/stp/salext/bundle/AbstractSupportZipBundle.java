/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.atlassian.troubleshooting.stp.salext.bundle.BundlePriority;
import java.util.Objects;
import javax.annotation.Nonnull;

@Deprecated
public abstract class AbstractSupportZipBundle
implements SupportZipBundle {
    private final String titleKey;
    private final String descriptionKey;
    private final I18nResolver i18nResolver;
    private final BundleManifest bundle;
    private String applicabilityReason = "";
    private boolean selected = true;
    private boolean applicable = true;

    public AbstractSupportZipBundle(I18nResolver i18nResolver, @Nonnull BundleManifest bundle, String titleKey, String descriptionKey, Boolean applicable, String applicabilityReason) {
        this(i18nResolver, bundle, titleKey, descriptionKey);
        this.applicable = applicable;
        this.applicabilityReason = applicabilityReason;
        if (!applicable.booleanValue()) {
            this.selected = false;
        }
    }

    public AbstractSupportZipBundle(I18nResolver i18nResolver, @Nonnull BundleManifest bundle, String titleKey, String descriptionKey) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
        this.bundle = Objects.requireNonNull(bundle);
        this.titleKey = Objects.requireNonNull(titleKey);
        this.descriptionKey = Objects.requireNonNull(descriptionKey);
        this.selected = BundleManifest.getDefaults().contains((Object)bundle);
    }

    @Override
    public String getTitle() {
        return this.i18nResolver.getText(this.titleKey);
    }

    @Override
    public String getDescription() {
        return this.i18nResolver.getText(this.descriptionKey);
    }

    @Override
    public String getKey() {
        return this.bundle.getKey();
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public boolean isApplicable() {
        return this.applicable;
    }

    @Override
    public String getApplicabilityReason() {
        return this.applicabilityReason;
    }

    @Override
    public boolean isRequired() {
        return this.bundle.getPriority().equals((Object)BundlePriority.REQUIRED);
    }
}


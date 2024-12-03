/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.rest.representations.AbstractLicenseDetailsRepresentation;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class HostLicenseDetailsRepresentation
extends AbstractLicenseDetailsRepresentation {
    @JsonProperty
    private final Boolean subscriptionAnnual;

    @JsonCreator
    public HostLicenseDetailsRepresentation(@JsonProperty(value="valid") Boolean valid, @JsonProperty(value="evaluation") Boolean evaluation, @JsonProperty(value="maximumNumberOfUsers") Integer maximumNumberOfUsers, @JsonProperty(value="licenseType") LicenseType licenseType, @JsonProperty(value="creationDateString") String creationDateString, @JsonProperty(value="expiryDate") Date expiryDate, @JsonProperty(value="expiryDateString") String expiryDateString, @JsonProperty(value="pluginSupportEntitlementNumber") String supportEntitlementNumber, @JsonProperty(value="organizationName") String organizationName, @JsonProperty(value="contactEmail") String contactEmail, @JsonProperty(value="enterprise") Boolean enterprise, @JsonProperty(value="subscription") Boolean subscription, @JsonProperty(value="subscriptionAnnual") Boolean subscriptionAnnual, @JsonProperty(value="active") Boolean active, @JsonProperty(value="autoRenewal") Boolean autoRenewal, @JsonProperty(value="dataCenter") Boolean dataCenter) {
        super(valid, evaluation, maximumNumberOfUsers, licenseType.name(), creationDateString, expiryDate, expiryDateString, supportEntitlementNumber, organizationName, contactEmail, enterprise, dataCenter, subscription, active, autoRenewal);
        this.subscriptionAnnual = subscriptionAnnual;
    }

    public HostLicenseDetailsRepresentation(HostApplicationLicenseAttributes attrs, UpmHostApplicationInformation appInfo, LicenseDateFormatter dateFormatter) {
        this(!HostApplicationLicenseAttributes.isExpired(attrs), attrs.isEvaluation(), attrs.getEdition().getOrElse(-1), attrs.getLicenseType(), null, HostApplicationLicenseAttributes.getExpiryDate(attrs).getOrElse((Date)null), HostApplicationLicenseAttributes.getExpiryDateString(attrs, dateFormatter).getOrElse((String)null), attrs.getSen().getOrElse((String)null), null, null, false, attrs.getSubscriptionPeriod().isDefined(), attrs.getSubscriptionPeriod().isDefined() ? Boolean.valueOf(attrs.getSubscriptionPeriod().equals(Option.some(SubscriptionPeriod.ANNUAL))) : null, true, attrs.isAutoRenewal(), appInfo.isHostDataCenterEnabled());
    }
}


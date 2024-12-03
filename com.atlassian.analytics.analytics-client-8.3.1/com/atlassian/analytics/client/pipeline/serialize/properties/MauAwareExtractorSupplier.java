/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.serialize.properties;

import com.atlassian.analytics.client.pipeline.serialize.properties.ExtractionSupplier;
import com.atlassian.analytics.client.pipeline.serialize.properties.dto.ForExtractionDTO;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.MetaPropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.MauAwarePropertyExtractor;
import com.atlassian.analytics.event.RawEvent;
import java.util.function.Supplier;

public class MauAwareExtractorSupplier
implements ExtractionSupplier {
    private final MetaPropertyExtractor metaPropertyExtractor;
    private final MauAwarePropertyExtractor mauAwarePropertyExtractor;

    public MauAwareExtractorSupplier(MetaPropertyExtractor metaPropertyExtractor, MauAwarePropertyExtractor mauAwarePropertyExtractor) {
        this.metaPropertyExtractor = metaPropertyExtractor;
        this.mauAwarePropertyExtractor = mauAwarePropertyExtractor;
    }

    @Override
    public Supplier<RawEvent> toExtractionSupplier(ForExtractionDTO forExtractionDTO) {
        return ExtractionSupplier.createTimedSupplier(this::oldInstant, this::oldDelayed, forExtractionDTO);
    }

    private RawEvent.Builder oldInstant(RawEvent.Builder withRawBuilder, ForExtractionDTO forExtractionDTO) {
        return withRawBuilder.name(this.metaPropertyExtractor.getEventName(forExtractionDTO.getEvent(), forExtractionDTO.getProperties())).product(this.metaPropertyExtractor.getProduct()).subproduct(this.metaPropertyExtractor.getSubProduct(forExtractionDTO.getEvent())).server(this.metaPropertyExtractor.getServer()).receivedTime(this.metaPropertyExtractor.getCurrentTime()).clientTime(this.metaPropertyExtractor.getClientTime(forExtractionDTO.getEvent())).user(this.mauAwarePropertyExtractor.getUserButSuppressForMau(forExtractionDTO.getEvent(), forExtractionDTO.getProperties())).appAccess(this.metaPropertyExtractor.getApplicationAccess()).requestCorrelationId(this.mauAwarePropertyExtractor.getRequestCorrelationIdButSuppressForMau(forExtractionDTO.getRequestInfo(), forExtractionDTO.getEvent())).session(this.metaPropertyExtractor.getSessionId(forExtractionDTO.getSessionId())).version(this.metaPropertyExtractor.getVersion()).sourceIP(this.metaPropertyExtractor.getSourceIp(forExtractionDTO.getRequestInfo())).atlPath(this.metaPropertyExtractor.extractAtlPathFromRequestInfo(forExtractionDTO.getRequestInfo()));
    }

    private RawEvent.Builder oldDelayed(RawEvent.Builder withRawBuilder, ForExtractionDTO forExtractionDTO) {
        return withRawBuilder.properties(this.mauAwarePropertyExtractor.getEventPropertiesWithHashedEmail(forExtractionDTO.getEvent(), forExtractionDTO.getProperties())).sen(this.metaPropertyExtractor.getSen());
    }
}


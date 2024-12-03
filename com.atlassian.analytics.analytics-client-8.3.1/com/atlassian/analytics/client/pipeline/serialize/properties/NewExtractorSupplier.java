/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.serialize.properties;

import com.atlassian.analytics.client.pipeline.serialize.properties.ExtractionSupplier;
import com.atlassian.analytics.client.pipeline.serialize.properties.dto.ForExtractionDTO;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.MetaPropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.v2.NewMetaExtractor;
import com.atlassian.analytics.event.RawEvent;
import java.util.function.Supplier;

public class NewExtractorSupplier
implements ExtractionSupplier {
    private final MetaPropertyExtractor metaPropertyExtractor;
    private final NewMetaExtractor newMetaExtractor;

    public NewExtractorSupplier(MetaPropertyExtractor metaPropertyExtractor, NewMetaExtractor newMetaExtractor) {
        this.metaPropertyExtractor = metaPropertyExtractor;
        this.newMetaExtractor = newMetaExtractor;
    }

    @Override
    public Supplier<RawEvent> toExtractionSupplier(ForExtractionDTO forExtractionDTO) {
        return ExtractionSupplier.createTimedSupplier(this::oldInstant, this::oldDelayed, forExtractionDTO);
    }

    private RawEvent.Builder oldInstant(RawEvent.Builder withRawBuilder, ForExtractionDTO forExtractionDTO) {
        return withRawBuilder.name(this.newMetaExtractor.getEventName(forExtractionDTO.getEvent())).product(this.metaPropertyExtractor.getProduct()).subproduct(this.metaPropertyExtractor.getSubProduct(forExtractionDTO.getEvent())).server(this.metaPropertyExtractor.getServer()).receivedTime(this.metaPropertyExtractor.getCurrentTime()).clientTime(this.metaPropertyExtractor.getClientTime(forExtractionDTO.getEvent())).user(this.metaPropertyExtractor.getUser(forExtractionDTO.getEvent(), forExtractionDTO.getProperties())).appAccess(this.metaPropertyExtractor.getApplicationAccess()).requestCorrelationId(this.metaPropertyExtractor.getRequestCorrelationId(forExtractionDTO.getRequestInfo())).session(this.metaPropertyExtractor.getSessionId(forExtractionDTO.getSessionId())).version(this.metaPropertyExtractor.getVersion()).sourceIP(this.metaPropertyExtractor.getSourceIp(forExtractionDTO.getRequestInfo())).atlPath(this.metaPropertyExtractor.extractAtlPathFromRequestInfo(forExtractionDTO.getRequestInfo()));
    }

    private RawEvent.Builder oldDelayed(RawEvent.Builder withRawBuilder, ForExtractionDTO forExtractionDTO) {
        return withRawBuilder.properties(this.metaPropertyExtractor.getEventPropertiesWithClientEventDecorating(forExtractionDTO.getEvent(), forExtractionDTO.getProperties())).sen(this.metaPropertyExtractor.getSen());
    }
}


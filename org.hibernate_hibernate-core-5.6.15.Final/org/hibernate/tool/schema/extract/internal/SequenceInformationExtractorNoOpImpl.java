/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.sql.SQLException;
import java.util.Collections;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;

public class SequenceInformationExtractorNoOpImpl
implements SequenceInformationExtractor {
    public static final SequenceInformationExtractorNoOpImpl INSTANCE = new SequenceInformationExtractorNoOpImpl();

    @Override
    public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException {
        return Collections.emptyList();
    }
}


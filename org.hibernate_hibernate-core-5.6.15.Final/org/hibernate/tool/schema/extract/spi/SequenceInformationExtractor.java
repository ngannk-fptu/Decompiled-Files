/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import java.sql.SQLException;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;

public interface SequenceInformationExtractor {
    public Iterable<SequenceInformation> extractMetadata(ExtractionContext var1) throws SQLException;
}


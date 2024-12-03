/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import org.codehaus.stax2.validation.DTDValidationSchema;

public interface DTDInfo {
    public Object getProcessedDTD();

    public String getDTDRootName();

    public String getDTDSystemId();

    public String getDTDPublicId();

    public String getDTDInternalSubset();

    public DTDValidationSchema getProcessedDTDSchema();
}


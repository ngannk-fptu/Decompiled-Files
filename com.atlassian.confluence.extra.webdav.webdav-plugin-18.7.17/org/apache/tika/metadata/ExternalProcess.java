/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public interface ExternalProcess {
    public static final String PREFIX_EXTERNAL_META = "external-process";
    public static final Property STD_OUT = Property.externalText("external-process:stdout");
    public static final Property STD_ERR = Property.externalText("external-process:stderr");
    public static final Property STD_OUT_IS_TRUNCATED = Property.externalBoolean("external-process:stdout-truncated");
    public static final Property STD_ERR_IS_TRUNCATED = Property.externalBoolean("external-process:stderr-truncated");
    public static final Property STD_OUT_LENGTH = Property.externalReal("external-process:stdout-length");
    public static final Property STD_ERR_LENGTH = Property.externalReal("external-process:stderr-length");
    public static final Property EXIT_VALUE = Property.externalInteger("external-process:exit-value");
    public static final Property IS_TIMEOUT = Property.externalBoolean("external-process:timeout");
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import org.codehaus.stax2.validation.XMLValidationSchema;

public interface DTDValidationSchema
extends XMLValidationSchema {
    public int getEntityCount();

    public int getNotationCount();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import org.apache.xalan.processor.ProcessorInclude;

public class ProcessorImport
extends ProcessorInclude {
    static final long serialVersionUID = -8247537698214245237L;

    @Override
    protected int getStylesheetType() {
        return 3;
    }

    @Override
    protected String getStylesheetInclErr() {
        return "ER_IMPORTING_ITSELF";
    }
}


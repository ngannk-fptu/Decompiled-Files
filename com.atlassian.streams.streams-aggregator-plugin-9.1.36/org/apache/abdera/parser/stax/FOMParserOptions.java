/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.parser.stax.FOMException;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.util.AbstractParserOptions;

public class FOMParserOptions
extends AbstractParserOptions {
    public FOMParserOptions(Factory factory) {
        this.factory = factory;
        this.detect = true;
    }

    protected void initFactory() {
        if (this.factory == null) {
            this.factory = new FOMFactory();
        }
    }

    protected void checkFactory(Factory factory) {
        if (!(factory instanceof FOMFactory)) {
            throw new FOMException(Localizer.sprintf("WRONG.PARSER.INSTANCE", FOMFactory.class.getName()));
        }
    }
}


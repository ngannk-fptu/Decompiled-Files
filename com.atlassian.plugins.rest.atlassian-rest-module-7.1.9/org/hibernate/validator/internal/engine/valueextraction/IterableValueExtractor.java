/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.valueextraction.ValueExtractor
 *  javax.validation.valueextraction.ValueExtractor$ValueReceiver
 */
package org.hibernate.validator.internal.engine.valueextraction;

import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;

class IterableValueExtractor
implements ValueExtractor<Iterable<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new IterableValueExtractor());

    private IterableValueExtractor() {
    }

    public void extractValues(Iterable<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (Object object : originalValue) {
            receiver.iterableValue("<iterable element>", object);
        }
    }
}


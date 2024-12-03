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

class FloatArrayValueExtractor
implements ValueExtractor<float[]> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new FloatArrayValueExtractor());

    private FloatArrayValueExtractor() {
    }

    public void extractValues(float[] originalValue, ValueExtractor.ValueReceiver receiver) {
        for (int i = 0; i < originalValue.length; ++i) {
            receiver.indexedValue("<iterable element>", i, (Object)Float.valueOf(originalValue[i]));
        }
    }
}


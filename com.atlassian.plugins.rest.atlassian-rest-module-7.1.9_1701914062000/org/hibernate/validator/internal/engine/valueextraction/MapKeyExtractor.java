/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.valueextraction.ValueExtractor
 *  javax.validation.valueextraction.ValueExtractor$ValueReceiver
 */
package org.hibernate.validator.internal.engine.valueextraction;

import java.util.Map;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;

class MapKeyExtractor
implements ValueExtractor<Map<?, ?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new MapKeyExtractor());

    private MapKeyExtractor() {
    }

    public void extractValues(Map<?, ?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (Map.Entry<?, ?> entry : originalValue.entrySet()) {
            receiver.keyedValue("<map key>", entry.getKey(), entry.getKey());
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javafx.beans.property.MapProperty
 *  javax.validation.valueextraction.ValueExtractor
 *  javax.validation.valueextraction.ValueExtractor$ValueReceiver
 */
package org.hibernate.validator.internal.engine.valueextraction;

import java.util.Map;
import javafx.beans.property.MapProperty;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;

@IgnoreForbiddenApisErrors(reason="Usage of JavaFX classes")
class MapPropertyValueExtractor
implements ValueExtractor<MapProperty<?, ?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new MapPropertyValueExtractor());

    private MapPropertyValueExtractor() {
    }

    public void extractValues(MapProperty<?, ?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (Map.Entry entry : originalValue.entrySet()) {
            receiver.keyedValue("<map value>", entry.getKey(), entry.getValue());
        }
    }
}


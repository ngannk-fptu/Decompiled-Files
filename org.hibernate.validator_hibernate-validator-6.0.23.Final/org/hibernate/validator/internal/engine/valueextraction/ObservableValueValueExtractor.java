/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javafx.beans.value.ObservableValue
 *  javax.validation.valueextraction.UnwrapByDefault
 *  javax.validation.valueextraction.ValueExtractor
 *  javax.validation.valueextraction.ValueExtractor$ValueReceiver
 */
package org.hibernate.validator.internal.engine.valueextraction;

import javafx.beans.value.ObservableValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;

@UnwrapByDefault
@IgnoreForbiddenApisErrors(reason="Usage of JavaFX classes")
class ObservableValueValueExtractor
implements ValueExtractor<ObservableValue<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new ObservableValueValueExtractor());

    private ObservableValueValueExtractor() {
    }

    public void extractValues(ObservableValue<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.getValue());
    }
}


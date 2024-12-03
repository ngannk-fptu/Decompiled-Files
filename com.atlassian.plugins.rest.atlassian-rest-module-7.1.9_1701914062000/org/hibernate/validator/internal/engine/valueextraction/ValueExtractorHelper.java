/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ValidationException
 *  javax.validation.valueextraction.ValueExtractor
 *  javax.validation.valueextraction.ValueExtractor$ValueReceiver
 */
package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ValueExtractorHelper {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    private ValueExtractorHelper() {
    }

    public static Set<Class<? extends ValueExtractor>> toValueExtractorClasses(Set<ValueExtractorDescriptor> valueExtractorDescriptors) {
        return valueExtractorDescriptors.stream().map(valueExtractorDescriptor -> valueExtractorDescriptor.getValueExtractor().getClass()).collect(Collectors.toSet());
    }

    public static void extractValues(ValueExtractorDescriptor valueExtractorDescriptor, Object containerValue, ValueExtractor.ValueReceiver valueReceiver) {
        ValueExtractor<?> valueExtractor = valueExtractorDescriptor.getValueExtractor();
        try {
            valueExtractor.extractValues(containerValue, valueReceiver);
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            throw LOG.getErrorWhileExtractingValuesInValueExtractorException(valueExtractor.getClass(), e);
        }
    }
}


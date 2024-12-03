/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.aggregated.rule;

import org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;

public class ParallelMethodsMustNotDefineGroupConversionForCascadedReturnValue
extends MethodConfigurationRule {
    @Override
    public void apply(ConstrainedExecutable method, ConstrainedExecutable otherMethod) {
        boolean hasGroupConversions;
        boolean isCascaded = method.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements() || otherMethod.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements();
        boolean bl = hasGroupConversions = method.getCascadingMetaDataBuilder().hasGroupConversionsOnAnnotatedObjectOrContainerElements() || otherMethod.getCascadingMetaDataBuilder().hasGroupConversionsOnAnnotatedObjectOrContainerElements();
        if (this.isDefinedOnParallelType(method, otherMethod) && isCascaded && hasGroupConversions) {
            throw LOG.getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException(method.getExecutable(), otherMethod.getExecutable());
        }
    }
}


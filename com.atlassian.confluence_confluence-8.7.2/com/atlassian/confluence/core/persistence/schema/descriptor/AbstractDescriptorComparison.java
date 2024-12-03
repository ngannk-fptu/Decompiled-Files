/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.google.common.base.Preconditions
 *  net.jcip.annotations.ThreadSafe
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import com.atlassian.confluence.core.persistence.schema.api.SchemaElementComparison;
import com.atlassian.confluence.core.persistence.schema.descriptor.DescriptorComparator;
import com.atlassian.fugue.Maybe;
import com.google.common.base.Preconditions;
import java.util.Optional;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.Nullable;

@ThreadSafe
abstract class AbstractDescriptorComparison<T extends DescriptorComparator<T>>
implements SchemaElementComparison<String> {
    private final @Nullable T expected;
    private final @Nullable T actual;

    protected AbstractDescriptorComparison(Maybe<T> expected, Maybe<T> actual) {
        this.expected = (DescriptorComparator)((Maybe)Preconditions.checkNotNull(expected)).getOrNull();
        this.actual = (DescriptorComparator)((Maybe)Preconditions.checkNotNull(actual)).getOrNull();
    }

    @Override
    public Optional<String> expected() {
        return AbstractDescriptorComparison.toString(this.expected);
    }

    @Override
    public Optional<String> actual() {
        return AbstractDescriptorComparison.toString(this.actual);
    }

    private static <T> Optional<String> toString(@Nullable T element) {
        return Optional.ofNullable(element).map(Object::toString);
    }

    @Override
    public SchemaElementComparison.ComparisonResult getResult() {
        if (this.actual != null && this.expected != null) {
            return this.expected.matches(this.actual) ? SchemaElementComparison.ComparisonResult.MATCH : SchemaElementComparison.ComparisonResult.MISMATCH;
        }
        if (this.expected != null) {
            return SchemaElementComparison.ComparisonResult.ACTUAL_ELEMENT_MISSING;
        }
        return SchemaElementComparison.ComparisonResult.EXPECTED_ELEMENT_MISSING;
    }
}


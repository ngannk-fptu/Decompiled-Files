/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;

public interface TriggerBinding {
    public FullBinding createBinding(SourceBinding var1, TargetBinding var2);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import org.codehaus.groovy.binding.ClosureSourceBinding;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.SwingTimerFullBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class SwingTimerTriggerBinding
implements TriggerBinding {
    @Override
    public FullBinding createBinding(SourceBinding source, TargetBinding target) {
        return new SwingTimerFullBinding((ClosureSourceBinding)source, target);
    }
}


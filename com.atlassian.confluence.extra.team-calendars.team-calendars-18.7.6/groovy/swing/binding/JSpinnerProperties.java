/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.JSpinnerValueBinding;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JSpinner;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class JSpinnerProperties {
    public static Map<String, TriggerBinding> getSyntheticProperties() {
        HashMap<String, TriggerBinding> result = new HashMap<String, TriggerBinding>();
        result.put(JSpinner.class.getName() + "#value", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JSpinnerValueBinding((PropertyBinding)source, target);
            }
        });
        return result;
    }
}


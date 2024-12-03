/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.JScrollBarValueBinding;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JScrollBar;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class JScrollBarProperties {
    public static Map<String, TriggerBinding> getSyntheticProperties() {
        HashMap<String, TriggerBinding> result = new HashMap<String, TriggerBinding>();
        result.put(JScrollBar.class.getName() + "#value", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JScrollBarValueBinding((PropertyBinding)source, target);
            }
        });
        return result;
    }
}


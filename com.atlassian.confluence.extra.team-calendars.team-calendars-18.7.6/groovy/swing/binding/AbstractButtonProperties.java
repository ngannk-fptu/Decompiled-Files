/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractButtonSelectedBinding;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class AbstractButtonProperties {
    public static Map<String, TriggerBinding> getSyntheticProperties() {
        HashMap<String, TriggerBinding> result = new HashMap<String, TriggerBinding>();
        result.put(AbstractButton.class.getName() + "#selected", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractButtonSelectedBinding((PropertyBinding)source, target);
            }
        });
        return result;
    }
}


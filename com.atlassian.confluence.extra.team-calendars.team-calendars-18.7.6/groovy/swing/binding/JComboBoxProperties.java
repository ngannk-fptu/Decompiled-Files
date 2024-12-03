/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.JComboBoxElementsBinding;
import groovy.swing.binding.JComboBoxSelectedElementBinding;
import groovy.swing.binding.JComboBoxSelectedIndexBinding;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class JComboBoxProperties {
    public static Map<String, TriggerBinding> getSyntheticProperties() {
        HashMap<String, TriggerBinding> result = new HashMap<String, TriggerBinding>();
        result.put(JComboBox.class.getName() + "#selectedItem", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JComboBoxSelectedElementBinding((PropertyBinding)source, target, "selectedItem");
            }
        });
        result.put(JComboBox.class.getName() + "#selectedElement", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JComboBoxSelectedElementBinding((PropertyBinding)source, target, "selectedElement");
            }
        });
        result.put(JComboBox.class.getName() + "#selectedIndex", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JComboBoxSelectedIndexBinding((PropertyBinding)source, target);
            }
        });
        result.put(JComboBox.class.getName() + "#elements", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JComboBoxElementsBinding((PropertyBinding)source, target);
            }
        });
        return result;
    }
}


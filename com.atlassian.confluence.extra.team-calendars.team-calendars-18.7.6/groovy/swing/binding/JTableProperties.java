/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.JTableElementsBinding;
import groovy.swing.binding.JTableSelectedElementBinding;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class JTableProperties {
    public static Map<String, TriggerBinding> getSyntheticProperties() {
        HashMap<String, TriggerBinding> result = new HashMap<String, TriggerBinding>();
        result.put(JTable.class.getName() + "#elements", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JTableElementsBinding((PropertyBinding)source, target);
            }
        });
        result.put(JTable.class.getName() + "#selectedElement", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JTableSelectedElementBinding((PropertyBinding)source, target, "selectedElement");
            }
        });
        result.put(JTable.class.getName() + "#selectedElements", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new JTableSelectedElementBinding((PropertyBinding)source, target, "selectedElements");
            }
        });
        return result;
    }
}


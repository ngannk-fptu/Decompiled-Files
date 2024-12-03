/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractJComponentBinding;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class JComponentProperties {
    public static Map<String, TriggerBinding> getSyntheticProperties() {
        HashMap<String, TriggerBinding> result = new HashMap<String, TriggerBinding>();
        result.put(JComponent.class.getName() + "#size", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractJComponentBinding((PropertyBinding)source, target, "size"){

                    @Override
                    public void componentResized(ComponentEvent event) {
                        this.update();
                    }
                };
            }
        });
        result.put(JComponent.class.getName() + "#width", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractJComponentBinding((PropertyBinding)source, target, "width"){

                    @Override
                    public void componentResized(ComponentEvent event) {
                        this.update();
                    }
                };
            }
        });
        result.put(JComponent.class.getName() + "#height", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractJComponentBinding((PropertyBinding)source, target, "height"){

                    @Override
                    public void componentResized(ComponentEvent event) {
                        this.update();
                    }
                };
            }
        });
        result.put(JComponent.class.getName() + "#bounds", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractJComponentBinding((PropertyBinding)source, target, "bounds"){

                    @Override
                    public void componentResized(ComponentEvent event) {
                        this.update();
                    }

                    @Override
                    public void componentMoved(ComponentEvent event) {
                        this.update();
                    }
                };
            }
        });
        result.put(JComponent.class.getName() + "#x", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractJComponentBinding((PropertyBinding)source, target, "x"){

                    @Override
                    public void componentMoved(ComponentEvent event) {
                        this.update();
                    }
                };
            }
        });
        result.put(JComponent.class.getName() + "#y", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractJComponentBinding((PropertyBinding)source, target, "y"){

                    @Override
                    public void componentMoved(ComponentEvent event) {
                        this.update();
                    }
                };
            }
        });
        result.put(JComponent.class.getName() + "#visible", new TriggerBinding(){

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                return new AbstractJComponentBinding((PropertyBinding)source, target, "visible"){

                    @Override
                    public void componentHidden(ComponentEvent event) {
                        this.update();
                    }

                    @Override
                    public void componentShown(ComponentEvent event) {
                        this.update();
                    }
                };
            }
        });
        return result;
    }
}


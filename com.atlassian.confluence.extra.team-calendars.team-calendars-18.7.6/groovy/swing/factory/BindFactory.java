/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.swing.SwingBuilder;
import groovy.swing.binding.AbstractButtonProperties;
import groovy.swing.binding.JComboBoxProperties;
import groovy.swing.binding.JComponentProperties;
import groovy.swing.binding.JListProperties;
import groovy.swing.binding.JScrollBarProperties;
import groovy.swing.binding.JSliderProperties;
import groovy.swing.binding.JSpinnerProperties;
import groovy.swing.binding.JTableProperties;
import groovy.swing.binding.JTextComponentProperties;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.binding.AggregateBinding;
import org.codehaus.groovy.binding.BindingUpdatable;
import org.codehaus.groovy.binding.ClosureSourceBinding;
import org.codehaus.groovy.binding.ClosureTriggerBinding;
import org.codehaus.groovy.binding.EventTriggerBinding;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.MutualPropertyBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class BindFactory
extends AbstractFactory
implements GroovyObject {
    public static final String CONTEXT_DATA_KEY = "BindFactoryData";
    private final Map<String, TriggerBinding> syntheticBindings;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BindFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Object object = callSiteArray[0].callConstructor(HashMap.class);
        this.syntheticBindings = (Map)ScriptBytecodeAdapter.castToType(object, Map.class);
        callSiteArray[1].call(this.syntheticBindings, callSiteArray[2].callGetProperty(JTextComponentProperties.class));
        callSiteArray[3].call(this.syntheticBindings, callSiteArray[4].callGetProperty(AbstractButtonProperties.class));
        callSiteArray[5].call(this.syntheticBindings, callSiteArray[6].callGetProperty(JSliderProperties.class));
        callSiteArray[7].call(this.syntheticBindings, callSiteArray[8].callGetProperty(JScrollBarProperties.class));
        callSiteArray[9].call(this.syntheticBindings, callSiteArray[10].callGetProperty(JComboBoxProperties.class));
        callSiteArray[11].call(this.syntheticBindings, callSiteArray[12].callGetProperty(JListProperties.class));
        callSiteArray[13].call(this.syntheticBindings, callSiteArray[14].callGetProperty(JSpinnerProperties.class));
        callSiteArray[15].call(this.syntheticBindings, callSiteArray[16].callGetProperty(JTableProperties.class));
        callSiteArray[17].call(this.syntheticBindings, callSiteArray[18].callGetProperty(JComponentProperties.class));
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        var5_5 = BindFactory.$getCallSiteArray();
        source = var5_5[19].call((Object)attributes, "source");
        target = var5_5[20].call((Object)attributes, "target");
        update = var5_5[21].call((Object)attributes, "update");
        var9_9 = var5_5[22].call(var5_5[23].callGroovyObjectGetProperty(builder), BindFactory.CONTEXT_DATA_KEY);
        bindContext = (Map)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(var9_9) != false ? var9_9 : ScriptBytecodeAdapter.createMap(new Object[0]), Map.class);
        if (DefaultTypeTransformation.booleanUnbox(var5_5[24].call(bindContext))) {
            var5_5[25].call(var5_5[26].callGroovyObjectGetProperty(builder), BindFactory.CONTEXT_DATA_KEY, bindContext);
        }
        tb = null;
        if (ScriptBytecodeAdapter.compareNotEqual(target, null)) {
            var12_12 = var5_5[27].call((Object)attributes, "targetProperty");
            targetProperty = DefaultTypeTransformation.booleanUnbox(var12_12) != false ? var12_12 : value;
            if (!BytecodeInterface8.isOrigZ() || BindFactory.__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (targetProperty instanceof CharSequence == false) {
                    throw (Throwable)var5_5[28].callConstructor(IllegalArgumentException.class, var5_5[29].call(var5_5[30].call((Object)"Invalid value for targetProperty: (or node value).", " Value for this attribute must be a String but it is "), ScriptBytecodeAdapter.compareNotEqual(targetProperty, null) != false ? var5_5[31].call(var5_5[32].call(targetProperty)) : null));
                }
            } else if (targetProperty instanceof CharSequence == false) {
                throw (Throwable)var5_5[33].callConstructor(IllegalArgumentException.class, var5_5[34].call(var5_5[35].call((Object)"Invalid value for targetProperty: (or node value).", " Value for this attribute must be a String but it is "), ScriptBytecodeAdapter.compareNotEqual(targetProperty, null) != false ? var5_5[36].call(var5_5[37].call(targetProperty)) : null));
            }
            var14_14 = var5_5[38].callConstructor(PropertyBinding.class, target, var5_5[39].call(targetProperty), update);
            tb = (TargetBinding)ScriptBytecodeAdapter.castToType(var14_14, TargetBinding.class);
            if (ScriptBytecodeAdapter.compareEqual(source, null)) {
                result = null;
                if (DefaultTypeTransformation.booleanUnbox(var5_5[40].call((Object)attributes, "mutual"))) {
                    result = var16_16 = var5_5[41].callConstructor(MutualPropertyBinding.class, null, null, tb, ScriptBytecodeAdapter.getMethodPointer(this, "getTriggerBinding"));
                } else {
                    var17_17 = tb;
                    result = var17_17;
                }
                newAttributes = ScriptBytecodeAdapter.createMap(new Object[0]);
                var5_5[42].call((Object)newAttributes, attributes);
                var5_5[43].call(bindContext, result, newAttributes);
                var5_5[44].call(attributes);
                return result;
            }
        }
        fb = null;
        sea = DefaultTypeTransformation.booleanUnbox(var5_5[45].call((Object)attributes, "sourceEvent"));
        sva = DefaultTypeTransformation.booleanUnbox(var5_5[46].call((Object)attributes, "sourceValue"));
        spa = 0;
        spa = !BytecodeInterface8.isOrigZ() || BindFactory.__$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (var23_23 = DefaultTypeTransformation.booleanUnbox(var5_5[47].call((Object)attributes, "sourceProperty")) != false || DefaultTypeTransformation.booleanUnbox(value) != false ? 1 : 0) : (var24_24 = DefaultTypeTransformation.booleanUnbox(var5_5[48].call((Object)attributes, "sourceProperty")) != false || DefaultTypeTransformation.booleanUnbox(value) != false ? 1 : 0);
        if (BytecodeInterface8.isOrigZ() && !BindFactory.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) ** GOTO lbl125
        if ((sea != false && sva != false) != false && spa == 0 != false) {
            queryValue = (Closure)ScriptBytecodeAdapter.castToType(var5_5[49].call((Object)attributes, "sourceValue"), Closure.class);
            csb = (ClosureSourceBinding)ScriptBytecodeAdapter.castToType(var5_5[50].callConstructor(ClosureSourceBinding.class, queryValue), ClosureSourceBinding.class);
            trigger = ShortTypeHandling.castToString(var5_5[51].call((Object)attributes, "sourceEvent"));
            etb = (EventTriggerBinding)ScriptBytecodeAdapter.castToType(var5_5[52].callConstructor(EventTriggerBinding.class, source, trigger), EventTriggerBinding.class);
            var29_29 = var5_5[53].call(etb, csb, tb);
            fb = (FullBinding)ScriptBytecodeAdapter.castToType(var29_29, FullBinding.class);
        } else if (spa != 0 && (sea != false && sva != false) == false != false) {
            var30_30 = var5_5[54].call((Object)attributes, "sourceProperty");
            property = DefaultTypeTransformation.booleanUnbox(var30_30) != false ? var30_30 : value;
            if (property instanceof CharSequence == false) {
                throw (Throwable)var5_5[55].callConstructor(IllegalArgumentException.class, var5_5[56].call(var5_5[57].call((Object)"Invalid value for sourceProperty: (or node value). ", "Value for this attribute must be a String but it is "), ScriptBytecodeAdapter.compareNotEqual(property, null) != false ? var5_5[58].call(var5_5[59].call(property)) : null));
            }
            if (ScriptBytecodeAdapter.compareEqual(source, null)) {
                throw (Throwable)var5_5[60].callConstructor(IllegalArgumentException.class, var5_5[61].call((Object)"Missing value for source: even though sourceProperty: (or node value) ", "was specified. Please check you didn't write bind(model.someProperty) instead of bind{ model.someProperty }"));
            }
            pb = (PropertyBinding)ScriptBytecodeAdapter.castToType(var5_5[62].callConstructor(PropertyBinding.class, source, var5_5[63].call(property), update), PropertyBinding.class);
            trigger = null;
            if (sea) {
                triggerName = ShortTypeHandling.castToString(var5_5[64].call((Object)attributes, "sourceEvent"));
                var35_35 = var5_5[65].callConstructor(EventTriggerBinding.class, source, triggerName);
                trigger = (TriggerBinding)ScriptBytecodeAdapter.castToType(var35_35, TriggerBinding.class);
            } else {
                var36_36 = var5_5[66].callCurrent((GroovyObject)this, pb);
                trigger = (TriggerBinding)ScriptBytecodeAdapter.castToType(var36_36, TriggerBinding.class);
            }
            sb /* !! */  = null;
            if (sva) {
                queryValue = (Closure)ScriptBytecodeAdapter.castToType(var5_5[67].call((Object)attributes, "sourceValue"), Closure.class);
                var39_39 = var5_5[68].callConstructor(ClosureSourceBinding.class, queryValue);
                sb /* !! */  = (SourceBinding)ScriptBytecodeAdapter.castToType(var39_39, SourceBinding.class);
            } else {
                sb /* !! */  = var40_40 = pb;
            }
            if (DefaultTypeTransformation.booleanUnbox(var5_5[69].call((Object)attributes, "mutual"))) {
                var41_41 = var5_5[70].callConstructor(MutualPropertyBinding.class, trigger, sb /* !! */ , tb, ScriptBytecodeAdapter.getMethodPointer(this, "getTriggerBinding"));
                fb = (FullBinding)ScriptBytecodeAdapter.castToType(var41_41, FullBinding.class);
            } else {
                var42_42 = var5_5[71].call(trigger, sb /* !! */ , tb);
                fb = (FullBinding)ScriptBytecodeAdapter.castToType(var42_42, FullBinding.class);
            }
        } else {
            if (((sea != false || sva != false) != false || spa != 0) == false) {
                newAttributes = ScriptBytecodeAdapter.createMap(new Object[0]);
                var5_5[72].call((Object)newAttributes, attributes);
                ctb = var5_5[73].callConstructor(ClosureTriggerBinding.class, this.syntheticBindings);
                var5_5[74].call(bindContext, ctb, newAttributes);
                var5_5[75].call(attributes);
                return ctb;
            }
            throw (Throwable)var5_5[76].callConstructor(RuntimeException.class, "Both sourceEvent: and sourceValue: cannot be specified along with sourceProperty: or a value argument");
lbl125:
            // 1 sources

            if ((sea != false && sva != false) != false && spa == 0 != false) {
                queryValue = (Closure)ScriptBytecodeAdapter.castToType(var5_5[77].call((Object)attributes, "sourceValue"), Closure.class);
                csb = (ClosureSourceBinding)ScriptBytecodeAdapter.castToType(var5_5[78].callConstructor(ClosureSourceBinding.class, queryValue), ClosureSourceBinding.class);
                trigger = ShortTypeHandling.castToString(var5_5[79].call((Object)attributes, "sourceEvent"));
                etb = (EventTriggerBinding)ScriptBytecodeAdapter.castToType(var5_5[80].callConstructor(EventTriggerBinding.class, source, trigger), EventTriggerBinding.class);
                var49_49 = var5_5[81].call(etb, csb, tb);
                fb = (FullBinding)ScriptBytecodeAdapter.castToType(var49_49, FullBinding.class);
            } else if (spa != 0 && (sea != false && sva != false) == false != false) {
                var50_50 = var5_5[82].call((Object)attributes, "sourceProperty");
                property = DefaultTypeTransformation.booleanUnbox(var50_50) != false ? var50_50 : value;
                if (property instanceof CharSequence == false) {
                    throw (Throwable)var5_5[83].callConstructor(IllegalArgumentException.class, var5_5[84].call(var5_5[85].call((Object)"Invalid value for sourceProperty: (or node value). ", "Value for this attribute must be a String but it is "), ScriptBytecodeAdapter.compareNotEqual(property, null) != false ? var5_5[86].call(var5_5[87].call(property)) : null));
                }
                if (ScriptBytecodeAdapter.compareEqual(source, null)) {
                    throw (Throwable)var5_5[88].callConstructor(IllegalArgumentException.class, var5_5[89].call((Object)"Missing value for source: even though sourceProperty: (or node value) ", "was specified. Please check you didn't write bind(model.someProperty) instead of bind{ model.someProperty }"));
                }
                pb = (PropertyBinding)ScriptBytecodeAdapter.castToType(var5_5[90].callConstructor(PropertyBinding.class, source, var5_5[91].call(property), update), PropertyBinding.class);
                trigger = null;
                if (sea) {
                    triggerName = ShortTypeHandling.castToString(var5_5[92].call((Object)attributes, "sourceEvent"));
                    var55_55 = var5_5[93].callConstructor(EventTriggerBinding.class, source, triggerName);
                    trigger = (TriggerBinding)ScriptBytecodeAdapter.castToType(var55_55, TriggerBinding.class);
                } else {
                    var56_56 = var5_5[94].callCurrent((GroovyObject)this, pb);
                    trigger = (TriggerBinding)ScriptBytecodeAdapter.castToType(var56_56, TriggerBinding.class);
                }
                sb /* !! */  = null;
                if (sva) {
                    queryValue = (Closure)ScriptBytecodeAdapter.castToType(var5_5[95].call((Object)attributes, "sourceValue"), Closure.class);
                    var59_59 = var5_5[96].callConstructor(ClosureSourceBinding.class, queryValue);
                    sb /* !! */  = (SourceBinding)ScriptBytecodeAdapter.castToType(var59_59, SourceBinding.class);
                } else {
                    sb /* !! */  = var60_60 = pb;
                }
                if (DefaultTypeTransformation.booleanUnbox(var5_5[97].call((Object)attributes, "mutual"))) {
                    var61_61 = var5_5[98].callConstructor(MutualPropertyBinding.class, trigger, sb /* !! */ , tb, ScriptBytecodeAdapter.getMethodPointer(this, "getTriggerBinding"));
                    fb = (FullBinding)ScriptBytecodeAdapter.castToType(var61_61, FullBinding.class);
                } else {
                    var62_62 = var5_5[99].call(trigger, sb /* !! */ , tb);
                    fb = (FullBinding)ScriptBytecodeAdapter.castToType(var62_62, FullBinding.class);
                }
            } else {
                if (((sea != false || sva != false) != false || spa != 0) == false) {
                    newAttributes = ScriptBytecodeAdapter.createMap(new Object[0]);
                    var5_5[100].call((Object)newAttributes, attributes);
                    ctb = var5_5[101].callConstructor(ClosureTriggerBinding.class, this.syntheticBindings);
                    var5_5[102].call(bindContext, ctb, newAttributes);
                    var5_5[103].call(attributes);
                    return ctb;
                }
                throw (Throwable)var5_5[104].callConstructor(RuntimeException.class, "Both sourceEvent: and sourceValue: cannot be specified along with sourceProperty: or a value argument");
            }
        }
        if (DefaultTypeTransformation.booleanUnbox(var5_5[105].call((Object)attributes, "value"))) {
            var5_5[106].call(bindContext, fb, ScriptBytecodeAdapter.createMap(new Object[]{"value", var5_5[107].call((Object)attributes, "value")}));
        }
        var5_5[108].call(var5_5[109].call(bindContext, fb, ScriptBytecodeAdapter.createMap(new Object[0])), "update", update);
        o = var5_5[110].call((Object)attributes, "bind");
        if (!BytecodeInterface8.isOrigZ() || BindFactory.__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if ((ScriptBytecodeAdapter.compareEqual(o, null) != false && DefaultTypeTransformation.booleanUnbox(var5_5[111].call((Object)attributes, "group")) == false != false) != false || (o instanceof Boolean != false && DefaultTypeTransformation.booleanUnbox(var5_5[112].call((Boolean)ScriptBytecodeAdapter.castToType(o, Boolean.class))) != false) != false) {
                var5_5[113].call(fb);
            }
        } else if ((ScriptBytecodeAdapter.compareEqual(o, null) != false && DefaultTypeTransformation.booleanUnbox(var5_5[114].call((Object)attributes, "group")) == false != false) != false || (o instanceof Boolean != false && DefaultTypeTransformation.booleanUnbox(var5_5[115].call((Boolean)ScriptBytecodeAdapter.castToType(o, Boolean.class))) != false) != false) {
            var5_5[116].call(fb);
        }
        if (var5_5[117].callGetProperty(attributes) instanceof AggregateBinding != false && fb instanceof BindingUpdatable != false) {
            var5_5[118].call(var5_5[119].call((Object)attributes, "group"), fb);
        }
        var5_5[120].call((Object)builder, ScriptBytecodeAdapter.getMethodPointer(fb, "unbind"));
        return fb;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractFactory.class, this, "onNodeCompleted", new Object[]{builder, parent, node});
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (node instanceof FullBinding && DefaultTypeTransformation.booleanUnbox(callSiteArray[121].callGetProperty(node)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[122].callGetProperty(node))) {
                try {
                    callSiteArray[123].call(node);
                }
                catch (Exception ignored) {
                }
                try {
                    callSiteArray[124].call(node);
                }
                catch (Exception ignored) {
                }
            }
        } else if (node instanceof FullBinding && DefaultTypeTransformation.booleanUnbox(callSiteArray[125].callGetProperty(node)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[126].callGetProperty(node))) {
            try {
                callSiteArray[127].call(node);
            }
            catch (Exception ignored) {
            }
            try {
                callSiteArray[128].call(node);
            }
            catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        callSiteArray[129].call((Object)attributes, "update");
        return true;
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        return false;
    }

    @Override
    public boolean isHandlesNodeChildren() {
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        if (node instanceof FullBinding && ScriptBytecodeAdapter.compareEqual(callSiteArray[130].callGetProperty(node), null)) {
            Closure closure = childContent;
            ScriptBytecodeAdapter.setProperty(closure, null, node, "converter");
            return false;
        }
        if (node instanceof ClosureTriggerBinding) {
            Closure closure = childContent;
            ScriptBytecodeAdapter.setProperty(closure, null, node, "closure");
            return false;
        }
        if (node instanceof TriggerBinding) {
            Object object = callSiteArray[131].call(callSiteArray[132].call(callSiteArray[133].callGroovyObjectGetProperty(builder), CONTEXT_DATA_KEY), node);
            Object bindAttrs = DefaultTypeTransformation.booleanUnbox(object) ? object : ScriptBytecodeAdapter.createMap(new Object[0]);
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[134].call(bindAttrs, "converter"))) {
                Closure closure = childContent;
                callSiteArray[135].call(bindAttrs, "converter", closure);
                return false;
            }
        }
        throw (Throwable)callSiteArray[136].callConstructor(RuntimeException.class, "Binding nodes do not accept child content when a converter is already specified");
    }

    public TriggerBinding getTriggerBinding(PropertyBinding psb) {
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        String property = ShortTypeHandling.castToString(callSiteArray[137].callGetProperty(psb));
        Class currentClass = ShortTypeHandling.castToClass(callSiteArray[138].call(callSiteArray[139].callGetProperty(psb)));
        while (ScriptBytecodeAdapter.compareNotEqual(currentClass, null)) {
            TriggerBinding trigger = (TriggerBinding)ScriptBytecodeAdapter.castToType(callSiteArray[140].call(this.syntheticBindings, ScriptBytecodeAdapter.createPojoWrapper((String)ScriptBytecodeAdapter.asType(new GStringImpl(new Object[]{callSiteArray[141].callGetProperty(currentClass), property}, new String[]{"", "#", ""}), String.class), String.class)), TriggerBinding.class);
            if (ScriptBytecodeAdapter.compareNotEqual(trigger, null)) {
                return (TriggerBinding)ScriptBytecodeAdapter.castToType(trigger, TriggerBinding.class);
            }
            Object object = callSiteArray[142].call(currentClass);
            currentClass = ShortTypeHandling.castToClass(object);
        }
        return psb;
    }

    public Object bindingAttributeDelegate(FactoryBuilderSupport builder, Object node, Object attributes) {
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        Iterator iter = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[143].call(callSiteArray[144].call(attributes)), Iterator.class);
        Object object = callSiteArray[145].call(callSiteArray[146].callGroovyObjectGetProperty(builder), CONTEXT_DATA_KEY);
        Map bindContext = (Map)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(object) ? object : ScriptBytecodeAdapter.createMap(new Object[0]), Map.class);
        while (DefaultTypeTransformation.booleanUnbox(callSiteArray[147].call(iter))) {
            Map.Entry entry = (Map.Entry)ScriptBytecodeAdapter.castToType(callSiteArray[148].call(iter), Map.Entry.class);
            String property = ShortTypeHandling.castToString(callSiteArray[149].call(callSiteArray[150].callGetProperty(entry)));
            Object value = callSiteArray[151].callGetProperty(entry);
            Object object2 = callSiteArray[152].call((Object)bindContext, value);
            Object bindAttrs = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : ScriptBytecodeAdapter.createMap(new Object[0]);
            Object object3 = callSiteArray[153].call((Object)builder, callSiteArray[154].callGetProperty(SwingBuilder.class));
            Object idAttr = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : callSiteArray[155].callGetProperty(SwingBuilder.class);
            Object id = callSiteArray[156].call(bindAttrs, idAttr);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[157].call(bindAttrs, "value"))) {
                Object object4 = callSiteArray[158].call(bindAttrs, "value");
                ScriptBytecodeAdapter.setProperty(object4, null, node, ShortTypeHandling.castToString(new GStringImpl(new Object[]{property}, new String[]{"", ""})));
            }
            Object update = callSiteArray[159].call(bindAttrs, "update");
            FullBinding fb = null;
            if (value instanceof MutualPropertyBinding) {
                FullBinding fullBinding;
                fb = fullBinding = (FullBinding)ScriptBytecodeAdapter.castToType(value, FullBinding.class);
                PropertyBinding psb = (PropertyBinding)ScriptBytecodeAdapter.castToType(callSiteArray[160].callConstructor(PropertyBinding.class, node, property, update), PropertyBinding.class);
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[161].callGetProperty(fb), null)) {
                    PropertyBinding propertyBinding = psb;
                    ScriptBytecodeAdapter.setProperty(propertyBinding, null, fb, "sourceBinding");
                    callSiteArray[162].callCurrent(this, fb, builder, bindAttrs, id);
                } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[163].callGetProperty(fb), null)) {
                    PropertyBinding propertyBinding = psb;
                    ScriptBytecodeAdapter.setProperty(propertyBinding, null, fb, "targetBinding");
                }
            } else if (value instanceof FullBinding) {
                FullBinding fullBinding;
                fb = fullBinding = (FullBinding)ScriptBytecodeAdapter.castToType(value, FullBinding.class);
                Object object5 = callSiteArray[164].callConstructor(PropertyBinding.class, node, property, update);
                ScriptBytecodeAdapter.setProperty(object5, null, fb, "targetBinding");
            } else if (value instanceof TargetBinding) {
                PropertyBinding psb = (PropertyBinding)ScriptBytecodeAdapter.castToType(callSiteArray[165].callConstructor(PropertyBinding.class, node, property, update), PropertyBinding.class);
                Object object6 = callSiteArray[166].call(callSiteArray[167].callCurrent((GroovyObject)this, psb), psb, value);
                fb = (FullBinding)ScriptBytecodeAdapter.castToType(object6, FullBinding.class);
                callSiteArray[168].callCurrent(this, fb, builder, bindAttrs, id);
            } else {
                if (!(value instanceof ClosureTriggerBinding)) continue;
                PropertyBinding psb = (PropertyBinding)ScriptBytecodeAdapter.castToType(callSiteArray[169].callConstructor(PropertyBinding.class, node, property, update), PropertyBinding.class);
                Object object7 = callSiteArray[170].call(value, value, psb);
                fb = (FullBinding)ScriptBytecodeAdapter.castToType(object7, FullBinding.class);
                callSiteArray[171].callCurrent(this, fb, builder, bindAttrs, id);
            }
            try {
                callSiteArray[172].call(fb);
            }
            catch (Exception e) {
            }
            try {
                callSiteArray[173].call(fb);
            }
            catch (Exception e) {
            }
            callSiteArray[174].call(iter);
        }
        return null;
    }

    private Object finishContextualBinding(FullBinding fb, FactoryBuilderSupport builder, Object bindAttrs, Object id) {
        Reference<FullBinding> fb2 = new Reference<FullBinding>(fb);
        CallSite[] callSiteArray = BindFactory.$getCallSiteArray();
        callSiteArray[175].call(bindAttrs, "update");
        Object bindValue = callSiteArray[176].call(bindAttrs, "bind");
        Reference<List> propertiesToBeSkipped = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[]{"group"}));
        public class _finishContextualBinding_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference propertiesToBeSkipped;
            private /* synthetic */ Reference fb;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _finishContextualBinding_closure1(Object _outerInstance, Object _thisObject, Reference propertiesToBeSkipped, Reference fb) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _finishContextualBinding_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.propertiesToBeSkipped = reference2 = propertiesToBeSkipped;
                this.fb = reference = fb;
            }

            public Object doCall(Object k, Object v) {
                CallSite[] callSiteArray = _finishContextualBinding_closure1.$getCallSiteArray();
                if (!ScriptBytecodeAdapter.isCase(k, this.propertiesToBeSkipped.get())) {
                    Object object = v;
                    ScriptBytecodeAdapter.setProperty(object, null, this.fb.get(), ShortTypeHandling.castToString(new GStringImpl(new Object[]{k}, new String[]{"", ""})));
                    return object;
                }
                return null;
            }

            public Object call(Object k, Object v) {
                CallSite[] callSiteArray = _finishContextualBinding_closure1.$getCallSiteArray();
                return callSiteArray[0].callCurrent(this, k, v);
            }

            public List getPropertiesToBeSkipped() {
                CallSite[] callSiteArray = _finishContextualBinding_closure1.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.propertiesToBeSkipped.get(), List.class);
            }

            public FullBinding getFb() {
                CallSite[] callSiteArray = _finishContextualBinding_closure1.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(this.fb.get(), FullBinding.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _finishContextualBinding_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "doCall";
                return new CallSiteArray(_finishContextualBinding_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _finishContextualBinding_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[177].call(bindAttrs, new _finishContextualBinding_closure1(this, this, propertiesToBeSkipped, fb2));
        if (callSiteArray[178].callGetProperty(bindAttrs) instanceof AggregateBinding && fb2.get() instanceof BindingUpdatable) {
            callSiteArray[179].call(callSiteArray[180].callGetProperty(bindAttrs), fb2.get());
        }
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(bindValue, null) || bindValue instanceof Boolean && DefaultTypeTransformation.booleanUnbox(callSiteArray[181].call((Boolean)ScriptBytecodeAdapter.castToType(bindValue, Boolean.class)))) {
                callSiteArray[182].call(fb2.get());
            }
        } else if (ScriptBytecodeAdapter.compareEqual(bindValue, null) || bindValue instanceof Boolean && DefaultTypeTransformation.booleanUnbox(callSiteArray[183].call((Boolean)ScriptBytecodeAdapter.castToType(bindValue, Boolean.class)))) {
            callSiteArray[184].call(fb2.get());
        }
        callSiteArray[185].call((Object)builder, ScriptBytecodeAdapter.getMethodPointer(fb2.get(), "unbind"));
        if (DefaultTypeTransformation.booleanUnbox(id)) {
            return callSiteArray[186].call(builder, id, fb2.get());
        }
        return null;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BindFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public final Map<String, TriggerBinding> getSyntheticBindings() {
        return this.syntheticBindings;
    }

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ boolean super$2$onNodeChildren(FactoryBuilderSupport factoryBuilderSupport, Object object, Closure closure) {
        return super.onNodeChildren(factoryBuilderSupport, object, closure);
    }

    public /* synthetic */ boolean super$2$onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object object, Map map) {
        return super.onHandleNodeAttributes(factoryBuilderSupport, object, map);
    }

    public /* synthetic */ boolean super$2$isHandlesNodeChildren() {
        return super.isHandlesNodeChildren();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "putAll";
        stringArray[2] = "syntheticProperties";
        stringArray[3] = "putAll";
        stringArray[4] = "syntheticProperties";
        stringArray[5] = "putAll";
        stringArray[6] = "syntheticProperties";
        stringArray[7] = "putAll";
        stringArray[8] = "syntheticProperties";
        stringArray[9] = "putAll";
        stringArray[10] = "syntheticProperties";
        stringArray[11] = "putAll";
        stringArray[12] = "syntheticProperties";
        stringArray[13] = "putAll";
        stringArray[14] = "syntheticProperties";
        stringArray[15] = "putAll";
        stringArray[16] = "syntheticProperties";
        stringArray[17] = "putAll";
        stringArray[18] = "syntheticProperties";
        stringArray[19] = "remove";
        stringArray[20] = "remove";
        stringArray[21] = "get";
        stringArray[22] = "get";
        stringArray[23] = "context";
        stringArray[24] = "isEmpty";
        stringArray[25] = "put";
        stringArray[26] = "context";
        stringArray[27] = "remove";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "plus";
        stringArray[30] = "plus";
        stringArray[31] = "getName";
        stringArray[32] = "getClass";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "plus";
        stringArray[35] = "plus";
        stringArray[36] = "getName";
        stringArray[37] = "getClass";
        stringArray[38] = "<$constructor$>";
        stringArray[39] = "toString";
        stringArray[40] = "remove";
        stringArray[41] = "<$constructor$>";
        stringArray[42] = "putAll";
        stringArray[43] = "put";
        stringArray[44] = "clear";
        stringArray[45] = "containsKey";
        stringArray[46] = "containsKey";
        stringArray[47] = "containsKey";
        stringArray[48] = "containsKey";
        stringArray[49] = "remove";
        stringArray[50] = "<$constructor$>";
        stringArray[51] = "remove";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "createBinding";
        stringArray[54] = "remove";
        stringArray[55] = "<$constructor$>";
        stringArray[56] = "plus";
        stringArray[57] = "plus";
        stringArray[58] = "getName";
        stringArray[59] = "getClass";
        stringArray[60] = "<$constructor$>";
        stringArray[61] = "plus";
        stringArray[62] = "<$constructor$>";
        stringArray[63] = "toString";
        stringArray[64] = "remove";
        stringArray[65] = "<$constructor$>";
        stringArray[66] = "getTriggerBinding";
        stringArray[67] = "remove";
        stringArray[68] = "<$constructor$>";
        stringArray[69] = "remove";
        stringArray[70] = "<$constructor$>";
        stringArray[71] = "createBinding";
        stringArray[72] = "putAll";
        stringArray[73] = "<$constructor$>";
        stringArray[74] = "put";
        stringArray[75] = "clear";
        stringArray[76] = "<$constructor$>";
        stringArray[77] = "remove";
        stringArray[78] = "<$constructor$>";
        stringArray[79] = "remove";
        stringArray[80] = "<$constructor$>";
        stringArray[81] = "createBinding";
        stringArray[82] = "remove";
        stringArray[83] = "<$constructor$>";
        stringArray[84] = "plus";
        stringArray[85] = "plus";
        stringArray[86] = "getName";
        stringArray[87] = "getClass";
        stringArray[88] = "<$constructor$>";
        stringArray[89] = "plus";
        stringArray[90] = "<$constructor$>";
        stringArray[91] = "toString";
        stringArray[92] = "remove";
        stringArray[93] = "<$constructor$>";
        stringArray[94] = "getTriggerBinding";
        stringArray[95] = "remove";
        stringArray[96] = "<$constructor$>";
        stringArray[97] = "remove";
        stringArray[98] = "<$constructor$>";
        stringArray[99] = "createBinding";
        stringArray[100] = "putAll";
        stringArray[101] = "<$constructor$>";
        stringArray[102] = "put";
        stringArray[103] = "clear";
        stringArray[104] = "<$constructor$>";
        stringArray[105] = "containsKey";
        stringArray[106] = "put";
        stringArray[107] = "remove";
        stringArray[108] = "put";
        stringArray[109] = "get";
        stringArray[110] = "remove";
        stringArray[111] = "containsKey";
        stringArray[112] = "booleanValue";
        stringArray[113] = "bind";
        stringArray[114] = "containsKey";
        stringArray[115] = "booleanValue";
        stringArray[116] = "bind";
        stringArray[117] = "group";
        stringArray[118] = "addBinding";
        stringArray[119] = "remove";
        stringArray[120] = "addDisposalClosure";
        stringArray[121] = "sourceBinding";
        stringArray[122] = "targetBinding";
        stringArray[123] = "update";
        stringArray[124] = "rebind";
        stringArray[125] = "sourceBinding";
        stringArray[126] = "targetBinding";
        stringArray[127] = "update";
        stringArray[128] = "rebind";
        stringArray[129] = "remove";
        stringArray[130] = "converter";
        stringArray[131] = "getAt";
        stringArray[132] = "get";
        stringArray[133] = "context";
        stringArray[134] = "containsKey";
        stringArray[135] = "putAt";
        stringArray[136] = "<$constructor$>";
        stringArray[137] = "propertyName";
        stringArray[138] = "getClass";
        stringArray[139] = "bean";
        stringArray[140] = "get";
        stringArray[141] = "name";
        stringArray[142] = "getSuperclass";
        stringArray[143] = "iterator";
        stringArray[144] = "entrySet";
        stringArray[145] = "get";
        stringArray[146] = "context";
        stringArray[147] = "hasNext";
        stringArray[148] = "next";
        stringArray[149] = "toString";
        stringArray[150] = "key";
        stringArray[151] = "value";
        stringArray[152] = "get";
        stringArray[153] = "getAt";
        stringArray[154] = "DELEGATE_PROPERTY_OBJECT_ID";
        stringArray[155] = "DEFAULT_DELEGATE_PROPERTY_OBJECT_ID";
        stringArray[156] = "remove";
        stringArray[157] = "containsKey";
        stringArray[158] = "remove";
        stringArray[159] = "get";
        stringArray[160] = "<$constructor$>";
        stringArray[161] = "sourceBinding";
        stringArray[162] = "finishContextualBinding";
        stringArray[163] = "targetBinding";
        stringArray[164] = "<$constructor$>";
        stringArray[165] = "<$constructor$>";
        stringArray[166] = "createBinding";
        stringArray[167] = "getTriggerBinding";
        stringArray[168] = "finishContextualBinding";
        stringArray[169] = "<$constructor$>";
        stringArray[170] = "createBinding";
        stringArray[171] = "finishContextualBinding";
        stringArray[172] = "update";
        stringArray[173] = "rebind";
        stringArray[174] = "remove";
        stringArray[175] = "remove";
        stringArray[176] = "remove";
        stringArray[177] = "each";
        stringArray[178] = "group";
        stringArray[179] = "addBinding";
        stringArray[180] = "group";
        stringArray[181] = "booleanValue";
        stringArray[182] = "bind";
        stringArray[183] = "booleanValue";
        stringArray[184] = "bind";
        stringArray[185] = "addDisposalClosure";
        stringArray[186] = "setVariable";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[187];
        BindFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BindFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BindFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.script.ScriptEventWrapper
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.ContextAction
 *  org.mozilla.javascript.Function
 *  org.mozilla.javascript.NativeJavaObject
 *  org.mozilla.javascript.NativeObject
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 *  org.mozilla.javascript.Undefined
 */
package org.apache.batik.bridge;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.batik.bridge.RhinoInterpreter;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.script.ScriptEventWrapper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

class EventTargetWrapper
extends NativeJavaObject {
    protected static WeakHashMap mapOfListenerMap;
    public static final String ADD_NAME = "addEventListener";
    public static final String ADDNS_NAME = "addEventListenerNS";
    public static final String REMOVE_NAME = "removeEventListener";
    public static final String REMOVENS_NAME = "removeEventListenerNS";
    protected RhinoInterpreter interpreter;

    EventTargetWrapper(Scriptable scope, EventTarget object, RhinoInterpreter interpreter) {
        super(scope, (Object)object, null);
        this.interpreter = interpreter;
    }

    public Object get(String name, Scriptable start) {
        Object method = super.get(name, start);
        if (name.equals(ADD_NAME)) {
            method = new FunctionAddProxy(this.interpreter, (Function)method, this.initMap());
        } else if (name.equals(REMOVE_NAME)) {
            method = new FunctionRemoveProxy((Function)method, this.initMap());
        } else if (name.equals(ADDNS_NAME)) {
            method = new FunctionAddNSProxy(this.interpreter, (Function)method, this.initMap());
        } else if (name.equals(REMOVENS_NAME)) {
            method = new FunctionRemoveNSProxy((Function)method, this.initMap());
        }
        return method;
    }

    public Map initMap() {
        WeakHashMap map = null;
        if (mapOfListenerMap == null) {
            mapOfListenerMap = new WeakHashMap(10);
        }
        if ((map = (WeakHashMap)mapOfListenerMap.get(this.unwrap())) == null) {
            map = new WeakHashMap(2);
            mapOfListenerMap.put(this.unwrap(), map);
        }
        return map;
    }

    static class FunctionRemoveNSProxy
    extends FunctionProxy {
        protected Map listenerMap;

        FunctionRemoveNSProxy(Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
        }

        public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[2] instanceof Function) {
                SoftReference sr = (SoftReference)this.listenerMap.get(args[2]);
                if (sr == null) {
                    return Undefined.instance;
                }
                EventListener el = (EventListener)sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                Class[] paramTypes = new Class[]{String.class, String.class, Function.class, Boolean.TYPE};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                AbstractNode target = (AbstractNode)njo.unwrap();
                target.removeEventListenerNS((String)args[0], (String)args[1], el, ((Boolean)args[3]).booleanValue());
                return Undefined.instance;
            }
            if (args[2] instanceof NativeObject) {
                SoftReference sr = (SoftReference)this.listenerMap.get(args[2]);
                if (sr == null) {
                    return Undefined.instance;
                }
                EventListener el = (EventListener)sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                Class[] paramTypes = new Class[]{String.class, String.class, Scriptable.class, Boolean.TYPE};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                AbstractNode target = (AbstractNode)njo.unwrap();
                target.removeEventListenerNS((String)args[0], (String)args[1], el, ((Boolean)args[3]).booleanValue());
                return Undefined.instance;
            }
            return this.delegate.call(ctx, scope, thisObj, args);
        }
    }

    static class FunctionAddNSProxy
    extends FunctionProxy {
        protected Map listenerMap;
        protected RhinoInterpreter interpreter;

        FunctionAddNSProxy(RhinoInterpreter interpreter, Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
            this.interpreter = interpreter;
        }

        public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[2] instanceof Function) {
                FunctionEventListener evtListener = new FunctionEventListener((Function)args[2], this.interpreter);
                this.listenerMap.put(args[2], new SoftReference<FunctionEventListener>(evtListener));
                Class[] paramTypes = new Class[]{String.class, String.class, Function.class, Boolean.TYPE, Object.class};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                AbstractNode target = (AbstractNode)njo.unwrap();
                target.addEventListenerNS((String)args[0], (String)args[1], (EventListener)evtListener, ((Boolean)args[3]).booleanValue(), args[4]);
                return Undefined.instance;
            }
            if (args[2] instanceof NativeObject) {
                HandleEventListener evtListener = new HandleEventListener((Scriptable)args[2], this.interpreter);
                this.listenerMap.put(args[2], new SoftReference<HandleEventListener>(evtListener));
                Class[] paramTypes = new Class[]{String.class, String.class, Scriptable.class, Boolean.TYPE, Object.class};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                AbstractNode target = (AbstractNode)njo.unwrap();
                target.addEventListenerNS((String)args[0], (String)args[1], (EventListener)evtListener, ((Boolean)args[3]).booleanValue(), args[4]);
                return Undefined.instance;
            }
            return this.delegate.call(ctx, scope, thisObj, args);
        }
    }

    static class FunctionRemoveProxy
    extends FunctionProxy {
        public Map listenerMap;

        FunctionRemoveProxy(Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
        }

        public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                SoftReference sr = (SoftReference)this.listenerMap.get(args[1]);
                if (sr == null) {
                    return Undefined.instance;
                }
                EventListener el = (EventListener)sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                Class[] paramTypes = new Class[]{String.class, Function.class, Boolean.TYPE};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).removeEventListener((String)args[0], el, (Boolean)args[2]);
                return Undefined.instance;
            }
            if (args[1] instanceof NativeObject) {
                SoftReference sr = (SoftReference)this.listenerMap.get(args[1]);
                if (sr == null) {
                    return Undefined.instance;
                }
                EventListener el = (EventListener)sr.get();
                if (el == null) {
                    return Undefined.instance;
                }
                Class[] paramTypes = new Class[]{String.class, Scriptable.class, Boolean.TYPE};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).removeEventListener((String)args[0], el, (Boolean)args[2]);
                return Undefined.instance;
            }
            return this.delegate.call(ctx, scope, thisObj, args);
        }
    }

    static class FunctionAddProxy
    extends FunctionProxy {
        protected Map listenerMap;
        protected RhinoInterpreter interpreter;

        FunctionAddProxy(RhinoInterpreter interpreter, Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
            this.interpreter = interpreter;
        }

        public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                EventListener evtListener = null;
                SoftReference sr = (SoftReference)this.listenerMap.get(args[1]);
                if (sr != null) {
                    evtListener = (EventListener)sr.get();
                }
                if (evtListener == null) {
                    evtListener = new FunctionEventListener((Function)args[1], this.interpreter);
                    this.listenerMap.put(args[1], new SoftReference<EventListener>(evtListener));
                }
                Class[] paramTypes = new Class[]{String.class, Function.class, Boolean.TYPE};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).addEventListener((String)args[0], evtListener, (Boolean)args[2]);
                return Undefined.instance;
            }
            if (args[1] instanceof NativeObject) {
                EventListener evtListener = null;
                SoftReference sr = (SoftReference)this.listenerMap.get(args[1]);
                if (sr != null) {
                    evtListener = (EventListener)sr.get();
                }
                if (evtListener == null) {
                    evtListener = new HandleEventListener((Scriptable)args[1], this.interpreter);
                    this.listenerMap.put(args[1], new SoftReference<EventListener>(evtListener));
                }
                Class[] paramTypes = new Class[]{String.class, Scriptable.class, Boolean.TYPE};
                for (int i = 0; i < args.length; ++i) {
                    args[i] = Context.jsToJava((Object)args[i], (Class)paramTypes[i]);
                }
                ((EventTarget)njo.unwrap()).addEventListener((String)args[0], evtListener, (Boolean)args[2]);
                return Undefined.instance;
            }
            return this.delegate.call(ctx, scope, thisObj, args);
        }
    }

    static abstract class FunctionProxy
    implements Function {
        protected Function delegate;

        public FunctionProxy(Function delegate) {
            this.delegate = delegate;
        }

        public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
            return this.delegate.construct(cx, scope, args);
        }

        public String getClassName() {
            return this.delegate.getClassName();
        }

        public Object get(String name, Scriptable start) {
            return this.delegate.get(name, start);
        }

        public Object get(int index, Scriptable start) {
            return this.delegate.get(index, start);
        }

        public boolean has(String name, Scriptable start) {
            return this.delegate.has(name, start);
        }

        public boolean has(int index, Scriptable start) {
            return this.delegate.has(index, start);
        }

        public void put(String name, Scriptable start, Object value) {
            this.delegate.put(name, start, value);
        }

        public void put(int index, Scriptable start, Object value) {
            this.delegate.put(index, start, value);
        }

        public void delete(String name) {
            this.delegate.delete(name);
        }

        public void delete(int index) {
            this.delegate.delete(index);
        }

        public Scriptable getPrototype() {
            return this.delegate.getPrototype();
        }

        public void setPrototype(Scriptable prototype) {
            this.delegate.setPrototype(prototype);
        }

        public Scriptable getParentScope() {
            return this.delegate.getParentScope();
        }

        public void setParentScope(Scriptable parent) {
            this.delegate.setParentScope(parent);
        }

        public Object[] getIds() {
            return this.delegate.getIds();
        }

        public Object getDefaultValue(Class hint) {
            return this.delegate.getDefaultValue(hint);
        }

        public boolean hasInstance(Scriptable instance) {
            return this.delegate.hasInstance(instance);
        }
    }

    static class HandleEventListener
    implements EventListener {
        public static final String HANDLE_EVENT = "handleEvent";
        public Scriptable scriptable;
        public Object[] array = new Object[1];
        public RhinoInterpreter interpreter;

        HandleEventListener(Scriptable s, RhinoInterpreter interpreter) {
            this.scriptable = s;
            this.interpreter = interpreter;
        }

        @Override
        public void handleEvent(Event evt) {
            this.array[0] = evt instanceof ScriptEventWrapper ? ((ScriptEventWrapper)evt).getEventObject() : evt;
            ContextAction handleEventAction = new ContextAction(){

                public Object run(Context cx) {
                    ScriptableObject.callMethod((Scriptable)scriptable, (String)HandleEventListener.HANDLE_EVENT, (Object[])array);
                    return null;
                }
            };
            this.interpreter.call(handleEventAction);
        }
    }

    static class FunctionEventListener
    implements EventListener {
        protected Function function;
        protected RhinoInterpreter interpreter;

        FunctionEventListener(Function f, RhinoInterpreter i) {
            this.function = f;
            this.interpreter = i;
        }

        @Override
        public void handleEvent(Event evt) {
            Object event = evt instanceof ScriptEventWrapper ? ((ScriptEventWrapper)evt).getEventObject() : evt;
            this.interpreter.callHandler(this.function, event);
        }
    }
}


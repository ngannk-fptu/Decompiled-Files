/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.w3c.dom.Location
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.Function
 *  org.mozilla.javascript.ImporterTopLevel
 *  org.mozilla.javascript.NativeObject
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 */
package org.apache.batik.bridge;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.batik.bridge.RhinoInterpreter;
import org.apache.batik.bridge.Window;
import org.apache.batik.w3c.dom.Location;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class WindowWrapper
extends ImporterTopLevel {
    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
    protected RhinoInterpreter interpreter;
    protected Window window;

    public WindowWrapper(Context context) {
        super(context);
        String[] names = new String[]{"setInterval", "setTimeout", "clearInterval", "clearTimeout", "parseXML", "printNode", "getURL", "postURL", "alert", "confirm", "prompt"};
        this.defineFunctionProperties(names, WindowWrapper.class, 2);
        this.defineProperty("location", WindowWrapper.class, 4);
    }

    public String getClassName() {
        return "Window";
    }

    public String toString() {
        return "[object Window]";
    }

    public static Object setInterval(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError((String)"invalid argument count");
        }
        long to = (Long)Context.jsToJava((Object)args[1], Long.TYPE);
        if (args[0] instanceof Function) {
            RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
            FunctionWrapper fw = new FunctionWrapper(interp, (Function)args[0], EMPTY_ARGUMENTS);
            return Context.toObject((Object)window.setInterval(fw, to), (Scriptable)thisObj);
        }
        String script = (String)Context.jsToJava((Object)args[0], String.class);
        return Context.toObject((Object)window.setInterval(script, to), (Scriptable)thisObj);
    }

    public static Object setTimeout(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError((String)"invalid argument count");
        }
        long to = (Long)Context.jsToJava((Object)args[1], Long.TYPE);
        if (args[0] instanceof Function) {
            RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
            FunctionWrapper fw = new FunctionWrapper(interp, (Function)args[0], EMPTY_ARGUMENTS);
            return Context.toObject((Object)window.setTimeout(fw, to), (Scriptable)thisObj);
        }
        String script = (String)Context.jsToJava((Object)args[0], String.class);
        return Context.toObject((Object)window.setTimeout(script, to), (Scriptable)thisObj);
    }

    public static void clearInterval(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            window.clearInterval(Context.jsToJava((Object)args[0], Object.class));
        }
    }

    public static void clearTimeout(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            window.clearTimeout(Context.jsToJava((Object)args[0], Object.class));
        }
    }

    public static Object parseXML(Context cx, Scriptable thisObj, final Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError((String)"invalid argument count");
        }
        RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
        AccessControlContext acc = interp.getAccessControlContext();
        PrivilegedAction pa = new PrivilegedAction(){

            public Object run() {
                return window.parseXML((String)Context.jsToJava((Object)args[0], String.class), (Document)Context.jsToJava((Object)args[1], Document.class));
            }
        };
        Object ret = acc != null ? AccessController.doPrivileged(pa, acc) : AccessController.doPrivileged(pa);
        return Context.toObject(ret, (Scriptable)thisObj);
    }

    public static Object printNode(Context cx, Scriptable thisObj, final Object[] args, Function funObj) {
        if (args.length != 1) {
            throw Context.reportRuntimeError((String)"invalid argument count");
        }
        WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        AccessControlContext acc = ((RhinoInterpreter)window.getInterpreter()).getAccessControlContext();
        Object ret = AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return window.printNode((Node)Context.jsToJava((Object)args[0], Node.class));
            }
        }, acc);
        return Context.toString(ret);
    }

    public static void getURL(Context cx, Scriptable thisObj, final Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError((String)"invalid argument count");
        }
        RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
        final String uri = (String)Context.jsToJava((Object)args[0], String.class);
        Window.URLResponseHandler urlHandler = null;
        urlHandler = args[1] instanceof Function ? new GetURLFunctionWrapper(interp, (Function)args[1], ww) : new GetURLObjectWrapper(interp, (ScriptableObject)((NativeObject)args[1]), ww);
        final Window.URLResponseHandler fw = urlHandler;
        AccessControlContext acc = ((RhinoInterpreter)window.getInterpreter()).getAccessControlContext();
        if (len == 2) {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    window.getURL(uri, fw);
                    return null;
                }
            }, acc);
        } else {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    window.getURL(uri, fw, (String)Context.jsToJava((Object)args[2], String.class));
                    return null;
                }
            }, acc);
        }
    }

    public static void postURL(Context cx, Scriptable thisObj, final Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 3) {
            throw Context.reportRuntimeError((String)"invalid argument count");
        }
        RhinoInterpreter interp = (RhinoInterpreter)window.getInterpreter();
        final String uri = (String)Context.jsToJava((Object)args[0], String.class);
        final String content = (String)Context.jsToJava((Object)args[1], String.class);
        Window.URLResponseHandler urlHandler = null;
        urlHandler = args[2] instanceof Function ? new GetURLFunctionWrapper(interp, (Function)args[2], ww) : new GetURLObjectWrapper(interp, (ScriptableObject)((NativeObject)args[2]), ww);
        final Window.URLResponseHandler fw = urlHandler;
        AccessControlContext acc = interp.getAccessControlContext();
        switch (len) {
            case 3: {
                AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        window.postURL(uri, content, fw);
                        return null;
                    }
                }, acc);
                break;
            }
            case 4: {
                AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        window.postURL(uri, content, fw, (String)Context.jsToJava((Object)args[3], String.class));
                        return null;
                    }
                }, acc);
                break;
            }
            default: {
                AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        window.postURL(uri, content, fw, (String)Context.jsToJava((Object)args[3], String.class), (String)Context.jsToJava((Object)args[4], String.class));
                        return null;
                    }
                }, acc);
            }
        }
    }

    public static void alert(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            String message = (String)Context.jsToJava((Object)args[0], String.class);
            window.alert(message);
        }
    }

    public static Object confirm(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            String message = (String)Context.jsToJava((Object)args[0], String.class);
            if (window.confirm(message)) {
                return Context.toObject((Object)Boolean.TRUE, (Scriptable)thisObj);
            }
            return Context.toObject((Object)Boolean.FALSE, (Scriptable)thisObj);
        }
        return Context.toObject((Object)Boolean.FALSE, (Scriptable)thisObj);
    }

    public static Object prompt(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        String result;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        switch (args.length) {
            case 0: {
                result = "";
                break;
            }
            case 1: {
                String message = (String)Context.jsToJava((Object)args[0], String.class);
                result = window.prompt(message);
                break;
            }
            default: {
                String message = (String)Context.jsToJava((Object)args[0], String.class);
                String defVal = (String)Context.jsToJava((Object)args[1], String.class);
                result = window.prompt(message, defVal);
            }
        }
        if (result == null) {
            return null;
        }
        return Context.toString((Object)result);
    }

    public Location getLocation() {
        return this.window.getLocation();
    }

    public void setLocation(Object val) {
        String url = (String)Context.jsToJava((Object)val, String.class);
        this.window.getLocation().assign(url);
    }

    static class GetURLDoneArgBuilder
    implements RhinoInterpreter.ArgumentsBuilder {
        boolean success;
        String mime;
        String content;
        WindowWrapper windowWrapper;

        public GetURLDoneArgBuilder(boolean success, String mime, String content, WindowWrapper ww) {
            this.success = success;
            this.mime = mime;
            this.content = content;
            this.windowWrapper = ww;
        }

        @Override
        public Object[] buildArguments() {
            NativeObject so = new NativeObject();
            so.put("success", (Scriptable)so, (Object)(this.success ? Boolean.TRUE : Boolean.FALSE));
            if (this.mime != null) {
                so.put("contentType", (Scriptable)so, (Object)Context.toObject((Object)this.mime, (Scriptable)this.windowWrapper));
            }
            if (this.content != null) {
                so.put("content", (Scriptable)so, (Object)Context.toObject((Object)this.content, (Scriptable)this.windowWrapper));
            }
            return new Object[]{so};
        }
    }

    private static class GetURLObjectWrapper
    implements Window.URLResponseHandler {
        private RhinoInterpreter interpreter;
        private ScriptableObject object;
        private WindowWrapper windowWrapper;
        private static final String COMPLETE = "operationComplete";

        public GetURLObjectWrapper(RhinoInterpreter ri, ScriptableObject obj, WindowWrapper ww) {
            this.interpreter = ri;
            this.object = obj;
            this.windowWrapper = ww;
        }

        @Override
        public void getURLDone(boolean success, String mime, String content) {
            this.interpreter.callMethod(this.object, COMPLETE, new GetURLDoneArgBuilder(success, mime, content, this.windowWrapper));
        }
    }

    protected static class GetURLFunctionWrapper
    implements Window.URLResponseHandler {
        protected RhinoInterpreter interpreter;
        protected Function function;
        protected WindowWrapper windowWrapper;

        public GetURLFunctionWrapper(RhinoInterpreter ri, Function fct, WindowWrapper ww) {
            this.interpreter = ri;
            this.function = fct;
            this.windowWrapper = ww;
        }

        @Override
        public void getURLDone(boolean success, String mime, String content) {
            this.interpreter.callHandler(this.function, new GetURLDoneArgBuilder(success, mime, content, this.windowWrapper));
        }
    }

    protected static class FunctionWrapper
    implements Runnable {
        protected RhinoInterpreter interpreter;
        protected Function function;
        protected Object[] arguments;

        public FunctionWrapper(RhinoInterpreter ri, Function f, Object[] args) {
            this.interpreter = ri;
            this.function = f;
            this.arguments = args;
        }

        @Override
        public void run() {
            this.interpreter.callHandler(this.function, this.arguments);
        }
    }
}


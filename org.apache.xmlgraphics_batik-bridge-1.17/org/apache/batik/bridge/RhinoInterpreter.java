/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.script.ImportInfo
 *  org.apache.batik.script.Interpreter
 *  org.apache.batik.script.InterpreterException
 *  org.apache.batik.script.rhino.BatikSecurityController
 *  org.apache.batik.script.rhino.RhinoClassLoader
 *  org.apache.batik.script.rhino.RhinoClassShutter
 *  org.mozilla.javascript.ClassCache
 *  org.mozilla.javascript.ClassShutter
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.ContextAction
 *  org.mozilla.javascript.ContextFactory
 *  org.mozilla.javascript.Function
 *  org.mozilla.javascript.JavaScriptException
 *  org.mozilla.javascript.Script
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 *  org.mozilla.javascript.SecurityController
 *  org.mozilla.javascript.WrapFactory
 *  org.mozilla.javascript.WrappedException
 */
package org.apache.batik.bridge;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.batik.bridge.BatikWrapFactory;
import org.apache.batik.bridge.EventTargetWrapper;
import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.bridge.RhinoInterpreterFactory;
import org.apache.batik.bridge.Window;
import org.apache.batik.bridge.WindowWrapper;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.rhino.BatikSecurityController;
import org.apache.batik.script.rhino.RhinoClassLoader;
import org.apache.batik.script.rhino.RhinoClassShutter;
import org.mozilla.javascript.ClassCache;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SecurityController;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.WrappedException;
import org.w3c.dom.events.EventTarget;

public class RhinoInterpreter
implements Interpreter {
    private static final int MAX_CACHED_SCRIPTS = 32;
    public static final String SOURCE_NAME_SVG = "<SVG>";
    public static final String BIND_NAME_WINDOW = "window";
    protected static List contexts = new LinkedList();
    protected Window window;
    protected ScriptableObject globalObject = null;
    protected LinkedList compiledScripts = new LinkedList();
    protected WrapFactory wrapFactory = new BatikWrapFactory(this);
    protected ClassShutter classShutter = new RhinoClassShutter();
    protected RhinoClassLoader rhinoClassLoader;
    protected SecurityController securityController = new BatikSecurityController();
    protected ContextFactory contextFactory = new Factory();
    protected Context defaultContext;

    public RhinoInterpreter(URL documentURL) {
        this.init(documentURL, null);
    }

    public RhinoInterpreter(URL documentURL, ImportInfo imports) {
        this.init(documentURL, imports);
    }

    protected void init(URL documentURL, final ImportInfo imports) {
        try {
            this.rhinoClassLoader = new RhinoClassLoader(documentURL, this.getClass().getClassLoader());
        }
        catch (SecurityException se) {
            this.rhinoClassLoader = null;
        }
        ContextAction initAction = new ContextAction(){

            public Object run(Context cx) {
                ScriptableObject scriptable = cx.initStandardObjects(null, false);
                RhinoInterpreter.this.defineGlobalWrapperClass((Scriptable)scriptable);
                RhinoInterpreter.this.globalObject = RhinoInterpreter.this.createGlobalObject(cx);
                ClassCache cache = ClassCache.get((Scriptable)RhinoInterpreter.this.globalObject);
                cache.setCachingEnabled(RhinoInterpreter.this.rhinoClassLoader != null);
                ImportInfo ii = imports;
                if (ii == null) {
                    ii = ImportInfo.getImports();
                }
                StringBuffer sb = new StringBuffer();
                Iterator iter = ii.getPackages();
                while (iter.hasNext()) {
                    String pkg = (String)iter.next();
                    sb.append("importPackage(Packages.");
                    sb.append(pkg);
                    sb.append(");");
                }
                iter = ii.getClasses();
                while (iter.hasNext()) {
                    String cls = (String)iter.next();
                    sb.append("importClass(Packages.");
                    sb.append(cls);
                    sb.append(");");
                }
                cx.evaluateString((Scriptable)RhinoInterpreter.this.globalObject, sb.toString(), null, 0, (Object)RhinoInterpreter.this.rhinoClassLoader);
                return null;
            }
        };
        this.contextFactory.call(initAction);
    }

    public String[] getMimeTypes() {
        return RhinoInterpreterFactory.RHINO_MIMETYPES;
    }

    public Window getWindow() {
        return this.window;
    }

    public ContextFactory getContextFactory() {
        return this.contextFactory;
    }

    protected void defineGlobalWrapperClass(Scriptable global) {
        try {
            ScriptableObject.defineClass((Scriptable)global, WindowWrapper.class);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected ScriptableObject createGlobalObject(Context ctx) {
        return new WindowWrapper(ctx);
    }

    public AccessControlContext getAccessControlContext() {
        if (this.rhinoClassLoader == null) {
            return null;
        }
        return this.rhinoClassLoader.getAccessControlContext();
    }

    protected ScriptableObject getGlobalObject() {
        return this.globalObject;
    }

    public Object evaluate(Reader scriptreader) throws IOException {
        return this.evaluate(scriptreader, SOURCE_NAME_SVG);
    }

    public Object evaluate(final Reader scriptReader, final String description) throws IOException {
        ContextAction evaluateAction = new ContextAction(){

            public Object run(Context cx) {
                try {
                    return cx.evaluateReader((Scriptable)RhinoInterpreter.this.globalObject, scriptReader, description, 1, (Object)RhinoInterpreter.this.rhinoClassLoader);
                }
                catch (IOException ioe) {
                    throw new WrappedException((Throwable)ioe);
                }
            }
        };
        try {
            return this.contextFactory.call(evaluateAction);
        }
        catch (JavaScriptException e) {
            Object value = e.getValue();
            Throwable ex = value instanceof Exception ? (Exception)value : e;
            throw new InterpreterException((Exception)ex, ex.getMessage(), -1, -1);
        }
        catch (WrappedException we) {
            Throwable w = we.getWrappedException();
            if (w instanceof Exception) {
                throw new InterpreterException((Exception)w, w.getMessage(), -1, -1);
            }
            throw new InterpreterException(w.getMessage(), -1, -1);
        }
        catch (InterruptedBridgeException ibe) {
            throw ibe;
        }
        catch (RuntimeException re) {
            throw new InterpreterException((Exception)re, re.getMessage(), -1, -1);
        }
    }

    public Object evaluate(final String scriptStr) {
        ContextAction evalAction = new ContextAction(){

            public Object run(final Context cx) {
                Script script = null;
                Entry entry = null;
                Iterator it = RhinoInterpreter.this.compiledScripts.iterator();
                while (it.hasNext()) {
                    entry = (Entry)it.next();
                    if (!entry.str.equals(scriptStr)) continue;
                    script = entry.script;
                    it.remove();
                    break;
                }
                if (script == null) {
                    PrivilegedAction compile = new PrivilegedAction(){

                        public Object run() {
                            try {
                                return cx.compileReader((Reader)new StringReader(scriptStr), RhinoInterpreter.SOURCE_NAME_SVG, 1, (Object)RhinoInterpreter.this.rhinoClassLoader);
                            }
                            catch (IOException ioEx) {
                                throw new RuntimeException(ioEx.getMessage());
                            }
                        }
                    };
                    script = (Script)AccessController.doPrivileged(compile);
                    if (RhinoInterpreter.this.compiledScripts.size() + 1 > 32) {
                        RhinoInterpreter.this.compiledScripts.removeFirst();
                    }
                    RhinoInterpreter.this.compiledScripts.addLast(new Entry(scriptStr, script));
                } else {
                    RhinoInterpreter.this.compiledScripts.addLast(entry);
                }
                return script.exec(cx, (Scriptable)RhinoInterpreter.this.globalObject);
            }
        };
        try {
            return this.contextFactory.call(evalAction);
        }
        catch (InterpreterException ie) {
            throw ie;
        }
        catch (JavaScriptException e) {
            Object value = e.getValue();
            Throwable ex = value instanceof Exception ? (Exception)value : e;
            throw new InterpreterException((Exception)ex, ex.getMessage(), -1, -1);
        }
        catch (WrappedException we) {
            Throwable w = we.getWrappedException();
            if (w instanceof Exception) {
                throw new InterpreterException((Exception)w, w.getMessage(), -1, -1);
            }
            throw new InterpreterException(w.getMessage(), -1, -1);
        }
        catch (RuntimeException re) {
            throw new InterpreterException((Exception)re, re.getMessage(), -1, -1);
        }
    }

    public void dispose() {
        if (this.rhinoClassLoader != null) {
            ClassCache cache = ClassCache.get((Scriptable)this.globalObject);
            cache.setCachingEnabled(false);
        }
    }

    public void bindObject(final String name, final Object object) {
        this.contextFactory.call(new ContextAction(){

            public Object run(Context cx) {
                Object o = object;
                if (name.equals(RhinoInterpreter.BIND_NAME_WINDOW) && object instanceof Window) {
                    ((WindowWrapper)RhinoInterpreter.this.globalObject).window = (Window)object;
                    RhinoInterpreter.this.window = (Window)object;
                    o = RhinoInterpreter.this.globalObject;
                }
                Scriptable jsObject = Context.toObject((Object)o, (Scriptable)RhinoInterpreter.this.globalObject);
                RhinoInterpreter.this.globalObject.put(name, (Scriptable)RhinoInterpreter.this.globalObject, (Object)jsObject);
                return null;
            }
        });
    }

    void callHandler(final Function handler, final Object arg) {
        this.contextFactory.call(new ContextAction(){

            public Object run(Context cx) {
                Scriptable a = Context.toObject((Object)arg, (Scriptable)RhinoInterpreter.this.globalObject);
                Object[] args = new Object[]{a};
                handler.call(cx, (Scriptable)RhinoInterpreter.this.globalObject, (Scriptable)RhinoInterpreter.this.globalObject, args);
                return null;
            }
        });
    }

    void callMethod(final ScriptableObject obj, final String methodName, final ArgumentsBuilder ab) {
        this.contextFactory.call(new ContextAction(){

            public Object run(Context cx) {
                ScriptableObject.callMethod((Scriptable)obj, (String)methodName, (Object[])ab.buildArguments());
                return null;
            }
        });
    }

    void callHandler(final Function handler, final Object[] args) {
        this.contextFactory.call(new ContextAction(){

            public Object run(Context cx) {
                handler.call(cx, (Scriptable)RhinoInterpreter.this.globalObject, (Scriptable)RhinoInterpreter.this.globalObject, args);
                return null;
            }
        });
    }

    void callHandler(final Function handler, final ArgumentsBuilder ab) {
        this.contextFactory.call(new ContextAction(){

            public Object run(Context cx) {
                Object[] args = ab.buildArguments();
                handler.call(cx, handler.getParentScope(), (Scriptable)RhinoInterpreter.this.globalObject, args);
                return null;
            }
        });
    }

    Object call(ContextAction action) {
        return this.contextFactory.call(action);
    }

    Scriptable buildEventTargetWrapper(EventTarget obj) {
        return new EventTargetWrapper((Scriptable)this.globalObject, obj, this);
    }

    public void setOut(Writer out) {
    }

    public Locale getLocale() {
        return null;
    }

    public void setLocale(Locale locale) {
    }

    public String formatMessage(String key, Object[] args) {
        return null;
    }

    protected class Factory
    extends ContextFactory {
        protected Factory() {
        }

        protected Context makeContext() {
            Context cx = super.makeContext();
            cx.setWrapFactory(RhinoInterpreter.this.wrapFactory);
            cx.setSecurityController(RhinoInterpreter.this.securityController);
            cx.setClassShutter(RhinoInterpreter.this.classShutter);
            if (RhinoInterpreter.this.rhinoClassLoader == null) {
                cx.setOptimizationLevel(-1);
            }
            return cx;
        }
    }

    protected static class Entry {
        public String str;
        public Script script;

        public Entry(String str, Script script) {
            this.str = str;
            this.script = script;
        }
    }

    public static interface ArgumentsBuilder {
        public Object[] buildArguments();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.ContextFactory
 *  org.mozilla.javascript.EvaluatorException
 *  org.mozilla.javascript.Function
 *  org.mozilla.javascript.JavaScriptException
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 */
package com.atlassian.lesscss;

import com.atlassian.lesscss.LessCompilationException;
import com.atlassian.lesscss.LessCompiler;
import com.atlassian.lesscss.LessSyntaxException;
import com.atlassian.lesscss.Loader;
import com.atlassian.lesscss.NotifyingContextFactory;
import com.atlassian.lesscss.ScriptableCreationListener;
import com.atlassian.lesscss.UnresolvableImportException;
import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoLessCompiler
implements LessCompiler {
    private final Scriptable sharedScope;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RhinoLessCompiler() {
        NotifyingContextFactory cf = new NotifyingContextFactory();
        ScriptableObjectSealer sealer = new ScriptableObjectSealer();
        cf.addListener(sealer);
        Context cx = cf.enterContext();
        try {
            ScriptableObject scope = cx.initStandardObjects();
            cx.setOptimizationLevel(9);
            try {
                this.loadJs((Scriptable)scope, cx, "/js/less/less-rhino.js");
                this.loadJs((Scriptable)scope, cx, "/js/less/less-patches.js");
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
            sealer.sealAll();
            cf.removeListener(sealer);
            this.sharedScope = scope;
        }
        finally {
            Context.exit();
        }
    }

    private void loadJs(Scriptable topScope, Context cx, String name) throws IOException {
        InputStream in = this.getClass().getResourceAsStream(name);
        if (in == null) {
            throw new FileNotFoundException("Could not find JS resource " + name);
        }
        InputStreamReader reader = new InputStreamReader(in, "UTF-8");
        cx.evaluateReader(topScope, (Reader)reader, name, 1, null);
    }

    @Override
    public String compile(Loader loader, URI uri, CharSequence content, boolean compress) {
        ContextFactory cf = new ContextFactory();
        Context cx = cf.enterContext();
        try {
            Function runLessRun = (Function)this.sharedScope.get("runLessRun", this.sharedScope);
            Scriptable newScope = cx.newObject(this.sharedScope);
            newScope.setPrototype(this.sharedScope);
            newScope.setParentScope(null);
            try {
                Object[] args = new Object[]{uri, loader, content, compress};
                Object result = runLessRun.call(cx, newScope, newScope, args);
                String string = Context.toString((Object)result);
                return string;
            }
            catch (JavaScriptException e) {
                throw this.newLessException(e);
            }
        }
        finally {
            Context.exit();
        }
    }

    private LessCompilationException newLessException(JavaScriptException e) {
        if (e.getValue() instanceof Scriptable) {
            Scriptable value = (Scriptable)e.getValue();
            String type = String.valueOf(ScriptableObject.getProperty((Scriptable)value, (String)"type"));
            String message = String.valueOf(ScriptableObject.getProperty((Scriptable)value, (String)"message"));
            if ("Syntax".equals(type)) {
                return new LessSyntaxException(message, e);
            }
            if ("Unresolvable Import".equals(type)) {
                return new UnresolvableImportException(message, e);
            }
        }
        return new LessCompilationException(RhinoLessCompiler.describeJsObject(e.getValue()), e);
    }

    private static String describeJsObject(Object jsObject) {
        try {
            Map map = (Map)Context.jsToJava((Object)jsObject, Map.class);
            return new LinkedHashMap(map).toString();
        }
        catch (EvaluatorException e) {
            return Context.toString((Object)jsObject);
        }
    }

    private static class ScriptableObjectSealer
    implements ScriptableCreationListener {
        private final List<ScriptableObject> objects = Lists.newArrayList();

        private ScriptableObjectSealer() {
        }

        @Override
        public void onNewScriptable(Scriptable scriptable) {
            if (scriptable instanceof ScriptableObject) {
                this.objects.add((ScriptableObject)scriptable);
            }
        }

        public void sealAll() {
            for (ScriptableObject object : this.objects) {
                if (object.isSealed()) continue;
                object.sealObject();
            }
        }
    }
}


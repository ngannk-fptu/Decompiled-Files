/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.scripting.support.StandardScriptEvalException
 *  org.springframework.scripting.support.StandardScriptUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.FileCopyUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.view.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.scripting.support.StandardScriptEvalException;
import org.springframework.scripting.support.StandardScriptUtils;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.script.RenderingContext;
import org.springframework.web.servlet.view.script.ScriptTemplateConfig;

public class ScriptTemplateView
extends AbstractUrlBasedView {
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String DEFAULT_RESOURCE_LOADER_PATH = "classpath:";
    private static final ThreadLocal<Map<Object, ScriptEngine>> enginesHolder = new NamedThreadLocal("ScriptTemplateView engines");
    @Nullable
    private ScriptEngine engine;
    @Nullable
    private Supplier<ScriptEngine> engineSupplier;
    @Nullable
    private String engineName;
    @Nullable
    private Boolean sharedEngine;
    @Nullable
    private String[] scripts;
    @Nullable
    private String renderObject;
    @Nullable
    private String renderFunction;
    @Nullable
    private Charset charset;
    @Nullable
    private String[] resourceLoaderPaths;
    @Nullable
    private volatile ScriptEngineManager scriptEngineManager;

    public ScriptTemplateView() {
        this.setContentType(null);
    }

    public ScriptTemplateView(String url) {
        super(url);
        this.setContentType(null);
    }

    public void setEngine(ScriptEngine engine) {
        this.engine = engine;
    }

    public void setEngineSupplier(Supplier<ScriptEngine> engineSupplier) {
        this.engineSupplier = engineSupplier;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public void setSharedEngine(Boolean sharedEngine) {
        this.sharedEngine = sharedEngine;
    }

    public void setScripts(String ... scripts) {
        this.scripts = scripts;
    }

    public void setRenderObject(String renderObject) {
        this.renderObject = renderObject;
    }

    public void setRenderFunction(String functionName) {
        this.renderFunction = functionName;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setResourceLoaderPath(String resourceLoaderPath) {
        String[] paths = StringUtils.commaDelimitedListToStringArray((String)resourceLoaderPath);
        this.resourceLoaderPaths = new String[paths.length + 1];
        this.resourceLoaderPaths[0] = "";
        for (int i2 = 0; i2 < paths.length; ++i2) {
            String path = paths[i2];
            if (!path.endsWith("/") && !path.endsWith(":")) {
                path = path + "/";
            }
            this.resourceLoaderPaths[i2 + 1] = path;
        }
    }

    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        ScriptTemplateConfig viewConfig = this.autodetectViewConfig();
        if (this.engine == null && viewConfig.getEngine() != null) {
            this.engine = viewConfig.getEngine();
        }
        if (this.engineSupplier == null && viewConfig.getEngineSupplier() != null) {
            this.engineSupplier = viewConfig.getEngineSupplier();
        }
        if (this.engineName == null && viewConfig.getEngineName() != null) {
            this.engineName = viewConfig.getEngineName();
        }
        if (this.scripts == null && viewConfig.getScripts() != null) {
            this.scripts = viewConfig.getScripts();
        }
        if (this.renderObject == null && viewConfig.getRenderObject() != null) {
            this.renderObject = viewConfig.getRenderObject();
        }
        if (this.renderFunction == null && viewConfig.getRenderFunction() != null) {
            this.renderFunction = viewConfig.getRenderFunction();
        }
        if (this.getContentType() == null) {
            this.setContentType(viewConfig.getContentType() != null ? viewConfig.getContentType() : DEFAULT_CONTENT_TYPE);
        }
        if (this.charset == null) {
            Charset charset = this.charset = viewConfig.getCharset() != null ? viewConfig.getCharset() : DEFAULT_CHARSET;
        }
        if (this.resourceLoaderPaths == null) {
            String resourceLoaderPath = viewConfig.getResourceLoaderPath();
            this.setResourceLoaderPath(resourceLoaderPath != null ? resourceLoaderPath : DEFAULT_RESOURCE_LOADER_PATH);
        }
        if (this.sharedEngine == null && viewConfig.isSharedEngine() != null) {
            this.sharedEngine = viewConfig.isSharedEngine();
        }
        int engineCount = 0;
        if (this.engine != null) {
            ++engineCount;
        }
        if (this.engineSupplier != null) {
            ++engineCount;
        }
        if (this.engineName != null) {
            ++engineCount;
        }
        Assert.isTrue((engineCount == 1 ? 1 : 0) != 0, (String)"You should define either 'engine', 'engineSupplier', or 'engineName'.");
        if (Boolean.FALSE.equals(this.sharedEngine)) {
            Assert.isTrue((this.engine == null ? 1 : 0) != 0, (String)"When 'sharedEngine' is set to false, you should specify the script engine using 'engineName' or 'engineSupplier', not 'engine'.");
        } else if (this.engine != null) {
            this.loadScripts(this.engine);
        } else if (this.engineName != null) {
            this.setEngine(this.createEngineFromName(this.engineName));
        } else {
            this.setEngine(this.createEngineFromSupplier());
        }
        if (this.renderFunction != null && this.engine != null) {
            Assert.isInstanceOf(Invocable.class, (Object)this.engine, (String)"ScriptEngine must implement Invocable when 'renderFunction' is specified");
        }
    }

    protected ScriptEngine getEngine() {
        if (Boolean.FALSE.equals(this.sharedEngine)) {
            Map<Object, ScriptEngine> engines = enginesHolder.get();
            if (engines == null) {
                engines = new HashMap<Object, ScriptEngine>(4);
                enginesHolder.set(engines);
            }
            String name = this.engineName != null ? this.engineName : "";
            Object engineKey = !ObjectUtils.isEmpty((Object[])this.scripts) ? new EngineKey(name, this.scripts) : name;
            ScriptEngine engine = engines.get(engineKey);
            if (engine == null) {
                engine = this.engineName != null ? this.createEngineFromName(this.engineName) : this.createEngineFromSupplier();
                engines.put(engineKey, engine);
            }
            return engine;
        }
        Assert.state((this.engine != null ? 1 : 0) != 0, (String)"No shared engine available");
        return this.engine;
    }

    protected ScriptEngine createEngineFromName(String engineName) {
        ScriptEngineManager scriptEngineManager = this.scriptEngineManager;
        if (scriptEngineManager == null) {
            this.scriptEngineManager = scriptEngineManager = new ScriptEngineManager(this.obtainApplicationContext().getClassLoader());
        }
        ScriptEngine engine = StandardScriptUtils.retrieveEngineByName((ScriptEngineManager)scriptEngineManager, (String)engineName);
        this.loadScripts(engine);
        return engine;
    }

    private ScriptEngine createEngineFromSupplier() {
        Assert.state((this.engineSupplier != null ? 1 : 0) != 0, (String)"No engine supplier available");
        ScriptEngine engine = this.engineSupplier.get();
        if (this.renderFunction != null) {
            Assert.isInstanceOf(Invocable.class, (Object)engine, (String)"ScriptEngine must implement Invocable when 'renderFunction' is specified");
        }
        this.loadScripts(engine);
        return engine;
    }

    protected void loadScripts(ScriptEngine engine) {
        if (!ObjectUtils.isEmpty((Object[])this.scripts)) {
            for (String script : this.scripts) {
                Resource resource = this.getResource(script);
                if (resource == null) {
                    throw new IllegalStateException("Script resource [" + script + "] not found");
                }
                try {
                    engine.eval(new InputStreamReader(resource.getInputStream()));
                }
                catch (Throwable ex) {
                    throw new IllegalStateException("Failed to evaluate script [" + script + "]", ex);
                }
            }
        }
    }

    @Nullable
    protected Resource getResource(String location) {
        if (this.resourceLoaderPaths != null) {
            for (String path : this.resourceLoaderPaths) {
                Resource resource = this.obtainApplicationContext().getResource(path + location);
                if (!resource.exists()) continue;
                return resource;
            }
        }
        return null;
    }

    protected ScriptTemplateConfig autodetectViewConfig() throws BeansException {
        try {
            return (ScriptTemplateConfig)BeanFactoryUtils.beanOfTypeIncludingAncestors((ListableBeanFactory)this.obtainApplicationContext(), ScriptTemplateConfig.class, (boolean)true, (boolean)false);
        }
        catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Expected a single ScriptTemplateConfig bean in the current Servlet web application context or the parent root context: ScriptTemplateConfigurer is the usual implementation. This bean may have any name.", (Throwable)ex);
        }
    }

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        String url = this.getUrl();
        Assert.state((url != null ? 1 : 0) != 0, (String)"'url' not set");
        return this.getResource(url) != null;
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        super.prepareResponse(request, response);
        this.setResponseContentType(request, response);
        if (this.charset != null) {
            response.setCharacterEncoding(this.charset.name());
        }
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            Object html;
            ScriptEngine engine = this.getEngine();
            String url = this.getUrl();
            Assert.state((url != null ? 1 : 0) != 0, (String)"'url' not set");
            String template = this.getTemplate(url);
            Function<String, String> templateLoader = path -> {
                try {
                    return this.getTemplate((String)path);
                }
                catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            };
            Locale locale = RequestContextUtils.getLocale(request);
            RenderingContext context = new RenderingContext(this.obtainApplicationContext(), locale, templateLoader, url);
            if (this.renderFunction == null) {
                SimpleBindings bindings = new SimpleBindings();
                bindings.putAll((Map<? extends String, ? extends Object>)model);
                model.put("renderingContext", context);
                html = engine.eval(template, (Bindings)bindings);
            } else if (this.renderObject != null) {
                Object thiz = engine.eval(this.renderObject);
                html = ((Invocable)((Object)engine)).invokeMethod(thiz, this.renderFunction, template, model, context);
            } else {
                html = ((Invocable)((Object)engine)).invokeFunction(this.renderFunction, template, model, context);
            }
            response.getWriter().write(String.valueOf(html));
        }
        catch (ScriptException ex) {
            throw new ServletException("Failed to render script template", (Throwable)new StandardScriptEvalException(ex));
        }
    }

    protected String getTemplate(String path) throws IOException {
        Resource resource = this.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Template resource [" + path + "] not found");
        }
        InputStreamReader reader = this.charset != null ? new InputStreamReader(resource.getInputStream(), this.charset) : new InputStreamReader(resource.getInputStream());
        return FileCopyUtils.copyToString((Reader)reader);
    }

    private static class EngineKey {
        private final String engineName;
        private final String[] scripts;

        public EngineKey(String engineName, String[] scripts) {
            this.engineName = engineName;
            this.scripts = scripts;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof EngineKey)) {
                return false;
            }
            EngineKey otherKey = (EngineKey)other;
            return this.engineName.equals(otherKey.engineName) && Arrays.equals(this.scripts, otherKey.scripts);
        }

        public int hashCode() {
            return this.engineName.hashCode() * 29 + Arrays.hashCode(this.scripts);
        }
    }
}


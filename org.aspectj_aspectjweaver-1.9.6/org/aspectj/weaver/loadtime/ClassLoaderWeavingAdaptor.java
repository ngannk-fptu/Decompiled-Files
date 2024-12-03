/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.Lint;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelWeakClassLoaderReference;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.loadtime.Aj;
import org.aspectj.weaver.loadtime.ConcreteAspectCodeGen;
import org.aspectj.weaver.loadtime.DefaultWeavingContext;
import org.aspectj.weaver.loadtime.IWeavingContext;
import org.aspectj.weaver.loadtime.Options;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.loadtime.definition.DocumentParser;
import org.aspectj.weaver.ltw.LTWWorld;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.tools.GeneratedClassHandler;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.WeavingAdaptor;
import org.aspectj.weaver.tools.cache.WeavedClassCache;
import sun.misc.Unsafe;

public class ClassLoaderWeavingAdaptor
extends WeavingAdaptor {
    private static final String AOP_XML = "META-INF/aop.xml;META-INF/aop-ajc.xml;org/aspectj/aop.xml";
    private boolean initialized;
    private List<TypePattern> dumpTypePattern = new ArrayList<TypePattern>();
    private boolean dumpBefore = false;
    private boolean dumpDirPerClassloader = false;
    private boolean hasExcludes = false;
    private List<TypePattern> excludeTypePattern = new ArrayList<TypePattern>();
    private List<String> excludeStartsWith = new ArrayList<String>();
    private List<String> excludeStarDotDotStar = new ArrayList<String>();
    private List<String> excludeExactName = new ArrayList<String>();
    private List<String> excludeEndsWith = new ArrayList<String>();
    private List<String[]> excludeSpecial = new ArrayList<String[]>();
    private boolean hasIncludes = false;
    private List<TypePattern> includeTypePattern = new ArrayList<TypePattern>();
    private List<String> includeStartsWith = new ArrayList<String>();
    private List<String> includeExactName = new ArrayList<String>();
    private boolean includeStar = false;
    private List<TypePattern> aspectExcludeTypePattern = new ArrayList<TypePattern>();
    private List<String> aspectExcludeStartsWith = new ArrayList<String>();
    private List<TypePattern> aspectIncludeTypePattern = new ArrayList<TypePattern>();
    private List<String> aspectIncludeStartsWith = new ArrayList<String>();
    private StringBuffer namespace;
    private IWeavingContext weavingContext;
    private List<ConcreteAspectCodeGen> concreteAspects = new ArrayList<ConcreteAspectCodeGen>();
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(ClassLoaderWeavingAdaptor.class);
    private Unsafe unsafe;
    private static Method bindTo_Method;
    private static Method invokeWithArguments_Method;
    private static Object defineClassMethodHandle;
    private static Boolean initializedForJava11;
    static Method defineClassMethod;
    private static String lock;

    public ClassLoaderWeavingAdaptor() {
        if (trace.isTraceEnabled()) {
            trace.enter("<init>", this);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("<init>");
        }
    }

    @Deprecated
    public ClassLoaderWeavingAdaptor(ClassLoader deprecatedLoader, IWeavingContext deprecatedContext) {
        if (trace.isTraceEnabled()) {
            trace.enter("<init>", (Object)this, new Object[]{deprecatedLoader, deprecatedContext});
        }
        if (trace.isTraceEnabled()) {
            trace.exit("<init>");
        }
    }

    public void initialize(ClassLoader classLoader, IWeavingContext context) {
        if (this.initialized) {
            return;
        }
        boolean success = true;
        this.weavingContext = context;
        if (this.weavingContext == null) {
            this.weavingContext = new DefaultWeavingContext(classLoader);
        }
        this.createMessageHandler();
        this.generatedClassHandler = new SimpleGeneratedClassHandler(classLoader);
        List<Definition> definitions = this.weavingContext.getDefinitions(classLoader, this);
        if (definitions.isEmpty()) {
            this.disable();
            if (trace.isTraceEnabled()) {
                trace.exit("initialize", definitions);
            }
            return;
        }
        this.bcelWorld = new LTWWorld(classLoader, this.weavingContext, this.getMessageHandler(), null);
        this.weaver = new BcelWeaver(this.bcelWorld);
        success = this.registerDefinitions(this.weaver, classLoader, definitions);
        if (success) {
            this.weaver.prepareForWeave();
            this.enable();
            success = this.weaveAndDefineConceteAspects();
        }
        if (success) {
            this.enable();
        } else {
            this.disable();
            this.bcelWorld = null;
            this.weaver = null;
        }
        if (WeavedClassCache.isEnabled()) {
            this.initializeCache(classLoader, this.getAspectClassNames(definitions), this.generatedClassHandler, this.getMessageHandler());
        }
        this.initialized = true;
        if (trace.isTraceEnabled()) {
            trace.exit("initialize", this.isEnabled());
        }
    }

    List<String> getAspectClassNames(List<Definition> definitions) {
        LinkedList<String> aspects = new LinkedList<String>();
        for (Definition def : definitions) {
            List<String> defAspects = def.getAspectClassNames();
            if (defAspects == null) continue;
            aspects.addAll(defAspects);
        }
        return aspects;
    }

    List<Definition> parseDefinitions(ClassLoader loader) {
        if (trace.isTraceEnabled()) {
            trace.enter("parseDefinitions", this);
        }
        ArrayList<Definition> definitions = new ArrayList<Definition>();
        try {
            String file;
            this.info("register classloader " + this.getClassLoaderName(loader));
            if (loader.equals(ClassLoader.getSystemClassLoader()) && (file = System.getProperty("aj5.def", null)) != null) {
                this.info("using (-Daj5.def) " + file);
                definitions.add(DocumentParser.parse(new File(file).toURI().toURL()));
            }
            String resourcePath = System.getProperty("org.aspectj.weaver.loadtime.configuration", AOP_XML);
            if (trace.isTraceEnabled()) {
                trace.event("parseDefinitions", (Object)this, resourcePath);
            }
            StringTokenizer st = new StringTokenizer(resourcePath, ";");
            while (st.hasMoreTokens()) {
                String nextDefinition = st.nextToken();
                if (nextDefinition.startsWith("file:")) {
                    try {
                        String fpath = new URL(nextDefinition).getFile();
                        File configFile = new File(fpath);
                        if (!configFile.exists()) {
                            this.warn("configuration does not exist: " + nextDefinition);
                            continue;
                        }
                        definitions.add(DocumentParser.parse(configFile.toURI().toURL()));
                    }
                    catch (MalformedURLException mue) {
                        this.error("malformed definition url: " + nextDefinition);
                    }
                    continue;
                }
                Enumeration<URL> xmls = this.weavingContext.getResources(nextDefinition);
                HashSet<URL> seenBefore = new HashSet<URL>();
                while (xmls.hasMoreElements()) {
                    URL xml = xmls.nextElement();
                    if (trace.isTraceEnabled()) {
                        trace.event("parseDefinitions", (Object)this, xml);
                    }
                    if (!seenBefore.contains(xml)) {
                        this.info("using configuration " + this.weavingContext.getFile(xml));
                        definitions.add(DocumentParser.parse(xml));
                        seenBefore.add(xml);
                        continue;
                    }
                    this.debug("ignoring duplicate definition: " + xml);
                }
            }
            if (definitions.isEmpty()) {
                this.info("no configuration found. Disabling weaver for class loader " + this.getClassLoaderName(loader));
            }
        }
        catch (Exception e) {
            definitions.clear();
            this.warn("parse definitions failed", e);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("parseDefinitions", definitions);
        }
        return definitions;
    }

    private boolean registerDefinitions(BcelWeaver weaver, ClassLoader loader, List<Definition> definitions) {
        if (trace.isTraceEnabled()) {
            trace.enter("registerDefinitions", (Object)this, definitions);
        }
        boolean success = true;
        try {
            this.registerOptions(weaver, loader, definitions);
            this.registerAspectExclude(weaver, loader, definitions);
            this.registerAspectInclude(weaver, loader, definitions);
            success = this.registerAspects(weaver, loader, definitions);
            this.registerIncludeExclude(weaver, loader, definitions);
            this.registerDump(weaver, loader, definitions);
        }
        catch (Exception ex) {
            trace.error("register definition failed", ex);
            success = false;
            this.warn("register definition failed", ex instanceof AbortException ? null : ex);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("registerDefinitions", success);
        }
        return success;
    }

    private String getClassLoaderName(ClassLoader loader) {
        return this.weavingContext.getClassLoaderName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void registerOptions(BcelWeaver weaver, ClassLoader loader, List<Definition> definitions) {
        StringBuffer allOptions = new StringBuffer();
        for (Definition definition : definitions) {
            allOptions.append(definition.getWeaverOptions()).append(' ');
        }
        Options.WeaverOption weaverOption = Options.parse(allOptions.toString(), loader, this.getMessageHandler());
        World world = weaver.getWorld();
        this.setMessageHandler(weaverOption.messageHandler);
        world.setXlazyTjp(weaverOption.lazyTjp);
        world.setXHasMemberSupportEnabled(weaverOption.hasMember);
        world.setTiming(weaverOption.timers, true);
        world.setOptionalJoinpoints(weaverOption.optionalJoinpoints);
        world.setPinpointMode(weaverOption.pinpoint);
        weaver.setReweavableMode(weaverOption.notReWeavable);
        if (weaverOption.loadersToSkip != null && weaverOption.loadersToSkip.length() > 0) {
            Aj.loadersToSkip = LangUtil.anySplit(weaverOption.loadersToSkip, ",");
        }
        if (Aj.loadersToSkip != null) {
            MessageUtil.info(world.getMessageHandler(), "no longer creating weavers for these classloaders: " + Aj.loadersToSkip);
        }
        world.performExtraConfiguration(weaverOption.xSet);
        world.setXnoInline(weaverOption.noInline);
        world.setBehaveInJava5Way(LangUtil.is15VMOrGreater());
        world.setAddSerialVerUID(weaverOption.addSerialVersionUID);
        this.bcelWorld.getLint().loadDefaultProperties();
        this.bcelWorld.getLint().adviceDidNotMatch.setKind(null);
        if (weaverOption.lintFile != null) {
            InputStream resource = null;
            try {
                resource = loader.getResourceAsStream(weaverOption.lintFile);
                IOException failure = null;
                if (resource != null) {
                    try {
                        Properties properties = new Properties();
                        properties.load(resource);
                        world.getLint().setFromProperties(properties);
                    }
                    catch (IOException e) {
                        failure = e;
                    }
                }
                if (failure != null || resource == null) {
                    this.warn("Cannot access resource for -Xlintfile:" + weaverOption.lintFile, failure);
                }
            }
            finally {
                try {
                    resource.close();
                }
                catch (Throwable throwable) {}
            }
        }
        if (weaverOption.lint != null) {
            if (weaverOption.lint.equals("default")) {
                this.bcelWorld.getLint().loadDefaultProperties();
            } else {
                this.bcelWorld.getLint().setAll(weaverOption.lint);
                if (weaverOption.lint.equals("ignore")) {
                    this.bcelWorld.setAllLintIgnored();
                }
            }
        }
    }

    private void registerAspectExclude(BcelWeaver weaver, ClassLoader loader, List<Definition> definitions) {
        String fastMatchInfo = null;
        for (Definition definition : definitions) {
            for (String exclude : definition.getAspectExcludePatterns()) {
                TypePattern excludePattern = new PatternParser(exclude).parseTypePattern();
                this.aspectExcludeTypePattern.add(excludePattern);
                fastMatchInfo = this.looksLikeStartsWith(exclude);
                if (fastMatchInfo == null) continue;
                this.aspectExcludeStartsWith.add(fastMatchInfo);
            }
        }
    }

    private void registerAspectInclude(BcelWeaver weaver, ClassLoader loader, List<Definition> definitions) {
        String fastMatchInfo = null;
        for (Definition definition : definitions) {
            for (String include : definition.getAspectIncludePatterns()) {
                TypePattern includePattern = new PatternParser(include).parseTypePattern();
                this.aspectIncludeTypePattern.add(includePattern);
                fastMatchInfo = this.looksLikeStartsWith(include);
                if (fastMatchInfo == null) continue;
                this.aspectIncludeStartsWith.add(fastMatchInfo);
            }
        }
    }

    protected void lint(String name, String[] infos) {
        Lint lint = this.bcelWorld.getLint();
        Lint.Kind kind = lint.getLintKind(name);
        kind.signal(infos, null, null);
    }

    @Override
    public String getContextId() {
        return this.weavingContext.getId();
    }

    private boolean registerAspects(BcelWeaver weaver, ClassLoader loader, List<Definition> definitions) {
        if (trace.isTraceEnabled()) {
            trace.enter("registerAspects", (Object)this, new Object[]{weaver, loader, definitions});
        }
        boolean success = true;
        for (Definition definition : definitions) {
            for (String aspectClassName : definition.getAspectClassNames()) {
                if (this.acceptAspect(aspectClassName)) {
                    String definedScope;
                    this.info("register aspect " + aspectClassName);
                    String requiredType = definition.getAspectRequires(aspectClassName);
                    if (requiredType != null) {
                        ((BcelWorld)weaver.getWorld()).addAspectRequires(aspectClassName, requiredType);
                    }
                    if ((definedScope = definition.getScopeForAspect(aspectClassName)) != null) {
                        ((BcelWorld)weaver.getWorld()).addScopedAspect(aspectClassName, definedScope);
                    }
                    weaver.addLibraryAspect(aspectClassName);
                    if (this.namespace == null) {
                        this.namespace = new StringBuffer(aspectClassName);
                        continue;
                    }
                    this.namespace = this.namespace.append(";").append(aspectClassName);
                    continue;
                }
                this.lint("aspectExcludedByConfiguration", new String[]{aspectClassName, this.getClassLoaderName(loader)});
            }
        }
        block2: for (Definition definition : definitions) {
            for (Definition.ConcreteAspect concreteAspect : definition.getConcreteAspects()) {
                if (!this.acceptAspect(concreteAspect.name)) continue;
                this.info("define aspect " + concreteAspect.name);
                ConcreteAspectCodeGen gen = new ConcreteAspectCodeGen(concreteAspect, weaver.getWorld());
                if (!gen.validate()) {
                    this.error("Concrete-aspect '" + concreteAspect.name + "' could not be registered");
                    success = false;
                    continue block2;
                }
                ((BcelWorld)weaver.getWorld()).addSourceObjectType(Utility.makeJavaClass(concreteAspect.name, gen.getBytes()), true);
                this.concreteAspects.add(gen);
                weaver.addLibraryAspect(concreteAspect.name);
                if (this.namespace == null) {
                    this.namespace = new StringBuffer(concreteAspect.name);
                    continue;
                }
                this.namespace = this.namespace.append(";" + concreteAspect.name);
            }
        }
        if (!success) {
            this.warn("failure(s) registering aspects. Disabling weaver for class loader " + this.getClassLoaderName(loader));
        } else if (this.namespace == null) {
            success = false;
            this.info("no aspects registered. Disabling weaver for class loader " + this.getClassLoaderName(loader));
        }
        if (trace.isTraceEnabled()) {
            trace.exit("registerAspects", success);
        }
        return success;
    }

    private boolean weaveAndDefineConceteAspects() {
        if (trace.isTraceEnabled()) {
            trace.enter("weaveAndDefineConceteAspects", (Object)this, this.concreteAspects);
        }
        boolean success = true;
        for (ConcreteAspectCodeGen gen : this.concreteAspects) {
            String name = gen.getClassName();
            byte[] bytes = gen.getBytes();
            try {
                byte[] newBytes = this.weaveClass(name, bytes, true);
                this.generatedClassHandler.acceptClass(name, bytes, newBytes);
            }
            catch (IOException ex) {
                trace.error("weaveAndDefineConceteAspects", ex);
                this.error("exception weaving aspect '" + name + "'", ex);
            }
        }
        if (trace.isTraceEnabled()) {
            trace.exit("weaveAndDefineConceteAspects", success);
        }
        return success;
    }

    private void registerIncludeExclude(BcelWeaver weaver, ClassLoader loader, List<Definition> definitions) {
        String fastMatchInfo = null;
        for (Definition definition : definitions) {
            Iterator<String> iterator1 = definition.getIncludePatterns().iterator();
            while (iterator1.hasNext()) {
                this.hasIncludes = true;
                String include = iterator1.next();
                fastMatchInfo = this.looksLikeStartsWith(include);
                if (fastMatchInfo != null) {
                    this.includeStartsWith.add(fastMatchInfo);
                    continue;
                }
                if (include.equals("*")) {
                    this.includeStar = true;
                    continue;
                }
                fastMatchInfo = this.looksLikeExactName(include);
                if (fastMatchInfo != null) {
                    this.includeExactName.add(fastMatchInfo);
                    continue;
                }
                TypePattern includePattern = new PatternParser(include).parseTypePattern();
                this.includeTypePattern.add(includePattern);
            }
            iterator1 = definition.getExcludePatterns().iterator();
            while (iterator1.hasNext()) {
                this.hasExcludes = true;
                String exclude = iterator1.next();
                fastMatchInfo = this.looksLikeStartsWith(exclude);
                if (fastMatchInfo != null) {
                    this.excludeStartsWith.add(fastMatchInfo);
                    continue;
                }
                fastMatchInfo = this.looksLikeStarDotDotStarExclude(exclude);
                if (fastMatchInfo != null) {
                    this.excludeStarDotDotStar.add(fastMatchInfo);
                    continue;
                }
                fastMatchInfo = this.looksLikeExactName(exclude);
                if (fastMatchInfo != null) {
                    this.excludeExactName.add(exclude);
                    continue;
                }
                fastMatchInfo = this.looksLikeEndsWith(exclude);
                if (fastMatchInfo != null) {
                    this.excludeEndsWith.add(fastMatchInfo);
                    continue;
                }
                if (exclude.equals("org.codehaus.groovy..* && !org.codehaus.groovy.grails.web.servlet.mvc.SimpleGrailsController*")) {
                    this.excludeSpecial.add(new String[]{"org.codehaus.groovy.", "org.codehaus.groovy.grails.web.servlet.mvc.SimpleGrailsController"});
                    continue;
                }
                TypePattern excludePattern = new PatternParser(exclude).parseTypePattern();
                this.excludeTypePattern.add(excludePattern);
            }
        }
    }

    private String looksLikeStarDotDotStarExclude(String typePattern) {
        if (!typePattern.startsWith("*..*")) {
            return null;
        }
        if (!typePattern.endsWith("*")) {
            return null;
        }
        String subPattern = typePattern.substring(4, typePattern.length() - 1);
        if (this.hasStarDot(subPattern, 0)) {
            return null;
        }
        return subPattern.replace('$', '.');
    }

    private String looksLikeExactName(String typePattern) {
        if (this.hasSpaceAnnotationPlus(typePattern, 0) || typePattern.indexOf("*") != -1) {
            return null;
        }
        return typePattern.replace('$', '.');
    }

    private String looksLikeEndsWith(String typePattern) {
        if (typePattern.charAt(0) != '*') {
            return null;
        }
        if (this.hasSpaceAnnotationPlus(typePattern, 1) || this.hasStarDot(typePattern, 1)) {
            return null;
        }
        return typePattern.substring(1).replace('$', '.');
    }

    private boolean hasSpaceAnnotationPlus(String string, int pos) {
        int max = string.length();
        for (int i = pos; i < max; ++i) {
            char ch = string.charAt(i);
            if (ch != ' ' && ch != '@' && ch != '+') continue;
            return true;
        }
        return false;
    }

    private boolean hasStarDot(String string, int pos) {
        int max = string.length();
        for (int i = pos; i < max; ++i) {
            char ch = string.charAt(i);
            if (ch != '*' && ch != '.') continue;
            return true;
        }
        return false;
    }

    private String looksLikeStartsWith(String typePattern) {
        if (this.hasSpaceAnnotationPlus(typePattern, 0) || typePattern.charAt(typePattern.length() - 1) != '*') {
            return null;
        }
        int length = typePattern.length();
        if (typePattern.endsWith("..*") && length > 3 && typePattern.indexOf("..") == length - 3 && typePattern.indexOf(42) == length - 1) {
            return typePattern.substring(0, length - 2).replace('$', '.');
        }
        return null;
    }

    private void registerDump(BcelWeaver weaver, ClassLoader loader, List<Definition> definitions) {
        for (Definition definition : definitions) {
            for (String dump : definition.getDumpPatterns()) {
                TypePattern pattern = new PatternParser(dump).parseTypePattern();
                this.dumpTypePattern.add(pattern);
            }
            if (definition.shouldDumpBefore()) {
                this.dumpBefore = true;
            }
            if (!definition.createDumpDirPerClassloader()) continue;
            this.dumpDirPerClassloader = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean accept(String className, byte[] bytes) {
        boolean bl;
        if (!this.hasExcludes && !this.hasIncludes) {
            return true;
        }
        String fastClassName = className.replace('/', '.');
        for (String string : this.excludeStartsWith) {
            if (!fastClassName.startsWith(string)) continue;
            return false;
        }
        if (!this.excludeStarDotDotStar.isEmpty()) {
            for (String string : this.excludeStarDotDotStar) {
                int index;
                if (fastClassName.indexOf(string, (index = fastClassName.lastIndexOf(46)) + 1) == -1) continue;
                return false;
            }
        }
        fastClassName = fastClassName.replace('$', '.');
        if (!this.excludeEndsWith.isEmpty()) {
            for (String string : this.excludeEndsWith) {
                if (!fastClassName.endsWith(string)) continue;
                return false;
            }
        }
        if (!this.excludeExactName.isEmpty()) {
            for (String string : this.excludeExactName) {
                if (!fastClassName.equals(string)) continue;
                return false;
            }
        }
        if (!this.excludeSpecial.isEmpty()) {
            for (String[] stringArray : this.excludeSpecial) {
                String excludeThese = stringArray[0];
                Iterator<String> exceptThese = stringArray[1];
                if (!fastClassName.startsWith(excludeThese) || fastClassName.startsWith((String)((Object)exceptThese))) continue;
                return false;
            }
        }
        boolean didSomeIncludeMatching = false;
        if (this.excludeTypePattern.isEmpty()) {
            if (this.includeStar) {
                return true;
            }
            if (!this.includeExactName.isEmpty()) {
                didSomeIncludeMatching = true;
                for (String exactname : this.includeExactName) {
                    if (!fastClassName.equals(exactname)) continue;
                    return true;
                }
            }
            boolean bl2 = false;
            for (int i = 0; i < this.includeStartsWith.size(); ++i) {
                didSomeIncludeMatching = true;
                boolean bl3 = fastClassName.startsWith(this.includeStartsWith.get(i));
                if (!bl3) continue;
                return true;
            }
            if (this.includeTypePattern.isEmpty()) {
                return !didSomeIncludeMatching;
            }
        }
        try {
            this.ensureDelegateInitialized(className, bytes);
            ReferenceType classInfo = this.delegateForCurrentClass.getResolvedTypeX();
            for (TypePattern typePattern : this.excludeTypePattern) {
                if (!typePattern.matchesStatically(classInfo)) continue;
                boolean bl4 = false;
                return bl4;
            }
            if (this.includeStar) {
                boolean exceptThese = true;
                return exceptThese;
            }
            if (!this.includeExactName.isEmpty()) {
                didSomeIncludeMatching = true;
                for (String exactname : this.includeExactName) {
                    if (!fastClassName.equals(exactname)) continue;
                    boolean bl5 = true;
                    return bl5;
                }
            }
            for (int i = 0; i < this.includeStartsWith.size(); ++i) {
                didSomeIncludeMatching = true;
                boolean fastaccept = fastClassName.startsWith(this.includeStartsWith.get(i));
                if (!fastaccept) continue;
                boolean bl6 = true;
                return bl6;
            }
            boolean bl7 = !didSomeIncludeMatching;
            for (TypePattern typePattern : this.includeTypePattern) {
                bl = typePattern.matchesStatically(classInfo);
                if (!bl) continue;
                break;
            }
        }
        finally {
            this.bcelWorld.demote();
        }
        return bl;
    }

    private boolean acceptAspect(String aspectClassName) {
        TypePattern typePattern;
        int i;
        if (this.aspectExcludeTypePattern.isEmpty() && this.aspectIncludeTypePattern.isEmpty()) {
            return true;
        }
        String fastClassName = aspectClassName.replace('/', '.').replace('.', '$');
        for (i = 0; i < this.aspectExcludeStartsWith.size(); ++i) {
            if (!fastClassName.startsWith(this.aspectExcludeStartsWith.get(i))) continue;
            return false;
        }
        for (i = 0; i < this.aspectIncludeStartsWith.size(); ++i) {
            if (!fastClassName.startsWith(this.aspectIncludeStartsWith.get(i))) continue;
            return true;
        }
        ResolvedType classInfo = this.weaver.getWorld().resolve(UnresolvedType.forName(aspectClassName), true);
        for (TypePattern typePattern2 : this.aspectExcludeTypePattern) {
            if (!typePattern2.matchesStatically(classInfo)) continue;
            return false;
        }
        boolean accept = true;
        Iterator<TypePattern> iterator = this.aspectIncludeTypePattern.iterator();
        while (iterator.hasNext() && !(accept = (typePattern = iterator.next()).matchesStatically(classInfo))) {
        }
        return accept;
    }

    @Override
    protected boolean shouldDump(String className, boolean before) {
        if (before && !this.dumpBefore) {
            return false;
        }
        if (this.dumpTypePattern.isEmpty()) {
            return false;
        }
        ResolvedType classInfo = this.weaver.getWorld().resolve(UnresolvedType.forName(className), true);
        for (TypePattern typePattern : this.dumpTypePattern) {
            if (!typePattern.matchesStatically(classInfo)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected String getDumpDir() {
        if (this.dumpDirPerClassloader) {
            StringBuffer dir = new StringBuffer();
            dir.append("_ajdump").append(File.separator).append(this.weavingContext.getId());
            return dir.toString();
        }
        return super.getDumpDir();
    }

    public String getNamespace() {
        if (this.namespace == null) {
            return "";
        }
        return new String(this.namespace);
    }

    public boolean generatedClassesExistFor(String className) {
        if (className == null) {
            return !this.generatedClasses.isEmpty();
        }
        return this.generatedClasses.containsKey(className);
    }

    public void flushGeneratedClasses() {
        this.generatedClasses = new HashMap();
    }

    public void flushGeneratedClassesFor(String className) {
        try {
            String dottedClassName = className.replace('/', '.');
            String dottedClassNameDollar = dottedClassName + "$";
            Iterator iter = this.generatedClasses.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry next = iter.next();
                String existingGeneratedName = (String)next.getKey();
                if (!existingGeneratedName.equals(dottedClassName) && !existingGeneratedName.startsWith(dottedClassNameDollar)) continue;
                iter.remove();
            }
        }
        catch (Throwable t) {
            new RuntimeException("Unexpected problem tidying up generated classes for " + className, t).printStackTrace();
        }
    }

    private Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        if (this.unsafe == null) {
            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            return (Unsafe)theUnsafeField.get(null);
        }
        return this.unsafe;
    }

    private static synchronized void initializeForJava11() {
        if (initializedForJava11.booleanValue()) {
            return;
        }
        try {
            Class<?> methodType_Class = Class.forName("java.lang.invoke.MethodType");
            Method methodTypeMethodOnMethodTypeClass = methodType_Class.getDeclaredMethod("methodType", Class.class, Class[].class);
            methodTypeMethodOnMethodTypeClass.setAccessible(true);
            Object defineClassMethodType = methodTypeMethodOnMethodTypeClass.invoke(null, Class.class, new Class[]{String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class});
            Class<?> methodHandles_Class = Class.forName("java.lang.invoke.MethodHandles");
            Method lookupMethodOnMethodHandlesClass = methodHandles_Class.getDeclaredMethod("lookup", new Class[0]);
            lookupMethodOnMethodHandlesClass.setAccessible(true);
            Object methodHandlesLookup = lookupMethodOnMethodHandlesClass.invoke(null, new Object[0]);
            Class<?> methodHandlesLookup_Class = Class.forName("java.lang.invoke.MethodHandles$Lookup");
            Method privateLookupMethodOnMethodHandlesClass = methodHandles_Class.getDeclaredMethod("privateLookupIn", Class.class, methodHandlesLookup_Class);
            privateLookupMethodOnMethodHandlesClass.setAccessible(true);
            Object lookup = privateLookupMethodOnMethodHandlesClass.invoke(null, ClassLoader.class, methodHandlesLookup);
            Method findVirtual_Method = methodHandlesLookup_Class.getDeclaredMethod("findVirtual", Class.class, String.class, methodType_Class);
            findVirtual_Method.setAccessible(true);
            defineClassMethodHandle = findVirtual_Method.invoke(lookup, ClassLoader.class, "defineClass", defineClassMethodType);
            Class<?> methodHandle_Class = Class.forName("java.lang.invoke.MethodHandle");
            bindTo_Method = methodHandle_Class.getDeclaredMethod("bindTo", Object.class);
            invokeWithArguments_Method = methodHandle_Class.getDeclaredMethod("invokeWithArguments", Object[].class);
            initializedForJava11 = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void defineClass(ClassLoader loader, String name, byte[] bytes, ProtectionDomain protectionDomain) {
        if (trace.isTraceEnabled()) {
            trace.enter("defineClass", (Object)this, new Object[]{loader, name, bytes});
        }
        Object clazz = null;
        this.debug("generating class '" + name + "'");
        if (LangUtil.is11VMOrGreater()) {
            try {
                if (!initializedForJava11.booleanValue()) {
                    ClassLoaderWeavingAdaptor.initializeForJava11();
                }
                Object o = bindTo_Method.invoke(defineClassMethodHandle, loader);
                clazz = invokeWithArguments_Method.invoke(o, new Object[]{new Object[]{name, bytes, 0, bytes.length, protectionDomain}});
            }
            catch (Throwable t) {
                t.printStackTrace(System.err);
                this.warn("define generated class failed", t);
            }
        } else {
            try {
                if (defineClassMethod == null) {
                    String t = lock;
                    synchronized (t) {
                        this.getUnsafe();
                        defineClassMethod = Unsafe.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ClassLoader.class, ProtectionDomain.class);
                    }
                }
                defineClassMethod.setAccessible(true);
                clazz = defineClassMethod.invoke((Object)this.getUnsafe(), name, bytes, 0, bytes.length, loader, protectionDomain);
            }
            catch (LinkageError le) {
                le.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace(System.err);
                this.warn("define generated class failed", e);
            }
        }
        if (trace.isTraceEnabled()) {
            trace.exit("defineClass", clazz);
        }
    }

    private void defineClass(ClassLoader loader, String name, byte[] bytes) {
        this.defineClass(loader, name, bytes, null);
    }

    static {
        invokeWithArguments_Method = null;
        defineClassMethodHandle = null;
        initializedForJava11 = false;
        lock = "lock";
    }

    class SimpleGeneratedClassHandler
    implements GeneratedClassHandler {
        private BcelWeakClassLoaderReference loaderRef;

        SimpleGeneratedClassHandler(ClassLoader loader) {
            this.loaderRef = new BcelWeakClassLoaderReference(loader);
        }

        @Override
        public void acceptClass(String name, byte[] originalBytes, byte[] wovenBytes) {
            try {
                if (ClassLoaderWeavingAdaptor.this.shouldDump(name.replace('/', '.'), false)) {
                    ClassLoaderWeavingAdaptor.this.dump(name, wovenBytes, false);
                }
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (ClassLoaderWeavingAdaptor.this.activeProtectionDomain != null) {
                ClassLoaderWeavingAdaptor.this.defineClass(this.loaderRef.getClassLoader(), name, wovenBytes, ClassLoaderWeavingAdaptor.this.activeProtectionDomain);
            } else {
                ClassLoaderWeavingAdaptor.this.defineClass(this.loaderRef.getClassLoader(), name, wovenBytes);
            }
        }
    }
}


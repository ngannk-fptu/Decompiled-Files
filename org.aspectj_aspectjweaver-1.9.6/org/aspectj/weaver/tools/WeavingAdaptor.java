/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageContext;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.IMessageHolder;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.bridge.MessageWriter;
import org.aspectj.bridge.Version;
import org.aspectj.bridge.WeaveMessage;
import org.aspectj.util.FileUtil;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.IClassFileProvider;
import org.aspectj.weaver.IUnwovenClassFile;
import org.aspectj.weaver.IWeaveRequestor;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.aspectj.weaver.tools.GeneratedClassHandler;
import org.aspectj.weaver.tools.ISupportsMessageContext;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.WeavingClassLoader;
import org.aspectj.weaver.tools.cache.CachedClassEntry;
import org.aspectj.weaver.tools.cache.CachedClassReference;
import org.aspectj.weaver.tools.cache.SimpleCache;
import org.aspectj.weaver.tools.cache.SimpleCacheFactory;
import org.aspectj.weaver.tools.cache.WeavedClassCache;

public class WeavingAdaptor
implements IMessageContext {
    public static final String WEAVING_ADAPTOR_VERBOSE = "aj.weaving.verbose";
    public static final String SHOW_WEAVE_INFO_PROPERTY = "org.aspectj.weaver.showWeaveInfo";
    public static final String TRACE_MESSAGES_PROPERTY = "org.aspectj.tracing.messages";
    private static final String ASPECTJ_BASE_PACKAGE = "org.aspectj.";
    private static final String PACKAGE_INITIAL_CHARS = "org.aspectj.".charAt(0) + "sj";
    private boolean enabled = false;
    protected boolean verbose = WeavingAdaptor.getVerbose();
    protected BcelWorld bcelWorld;
    protected BcelWeaver weaver;
    private IMessageHandler messageHandler;
    private WeavingAdaptorMessageHolder messageHolder;
    private boolean abortOnError = false;
    protected GeneratedClassHandler generatedClassHandler;
    protected Map<String, IUnwovenClassFile> generatedClasses = new HashMap<String, IUnwovenClassFile>();
    public BcelObjectType delegateForCurrentClass;
    protected ProtectionDomain activeProtectionDomain;
    private boolean haveWarnedOnJavax = false;
    protected WeavedClassCache cache;
    private int weavingSpecialTypes = 0;
    private static final int INITIALIZED = 1;
    private static final int WEAVE_JAVA_PACKAGE = 2;
    private static final int WEAVE_JAVAX_PACKAGE = 4;
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(WeavingAdaptor.class);
    private ThreadLocal<Boolean> weaverRunning = new ThreadLocal<Boolean>(){

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    protected WeavingAdaptor() {
    }

    public WeavingAdaptor(WeavingClassLoader loader) {
        this.generatedClassHandler = loader;
        this.init((ClassLoader)((Object)loader), this.getFullClassPath((ClassLoader)((Object)loader)), this.getFullAspectPath((ClassLoader)((Object)loader)));
    }

    public WeavingAdaptor(GeneratedClassHandler handler, URL[] classURLs, URL[] aspectURLs) {
        this.generatedClassHandler = handler;
        this.init(null, FileUtil.makeClasspath(classURLs), FileUtil.makeClasspath(aspectURLs));
    }

    protected List<String> getFullClassPath(ClassLoader loader) {
        LinkedList<String> list = new LinkedList<String>();
        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader)loader).getURLs();
                list.addAll(0, FileUtil.makeClasspath(urls));
            } else {
                this.warn("cannot determine classpath");
            }
            loader = loader.getParent();
        }
        if (LangUtil.is19VMOrGreater()) {
            list.add(0, LangUtil.getJrtFsFilePath());
            List<String> javaClassPathEntries = WeavingAdaptor.makeClasspath(System.getProperty("java.class.path"));
            for (int i = javaClassPathEntries.size() - 1; i >= 0; --i) {
                String javaClassPathEntry = javaClassPathEntries.get(i);
                if (list.contains(javaClassPathEntry)) continue;
                list.add(0, javaClassPathEntry);
            }
        }
        list.addAll(0, WeavingAdaptor.makeClasspath(System.getProperty("sun.boot.class.path")));
        return list;
    }

    private List<String> getFullAspectPath(ClassLoader loader) {
        LinkedList<String> list = new LinkedList<String>();
        while (loader != null) {
            if (loader instanceof WeavingClassLoader) {
                URL[] urls = ((WeavingClassLoader)((Object)loader)).getAspectURLs();
                list.addAll(0, FileUtil.makeClasspath(urls));
            }
            loader = loader.getParent();
        }
        return list;
    }

    private static boolean getVerbose() {
        try {
            return Boolean.getBoolean(WEAVING_ADAPTOR_VERBOSE);
        }
        catch (Throwable t) {
            return false;
        }
    }

    private void init(ClassLoader loader, List<String> classPath, List<String> aspectPath) {
        this.abortOnError = true;
        this.createMessageHandler();
        this.info("using classpath: " + classPath);
        this.info("using aspectpath: " + aspectPath);
        this.bcelWorld = new BcelWorld(classPath, this.messageHandler, null);
        this.bcelWorld.setXnoInline(false);
        this.bcelWorld.getLint().loadDefaultProperties();
        if (LangUtil.is15VMOrGreater()) {
            this.bcelWorld.setBehaveInJava5Way(true);
        }
        this.weaver = new BcelWeaver(this.bcelWorld);
        this.registerAspectLibraries(aspectPath);
        this.initializeCache(loader, aspectPath, null, this.getMessageHandler());
        this.enabled = true;
    }

    protected void initializeCache(ClassLoader loader, List<String> aspects, GeneratedClassHandler existingClassHandler, IMessageHandler myMessageHandler) {
        if (WeavedClassCache.isEnabled()) {
            this.cache = WeavedClassCache.createCache(loader, aspects, existingClassHandler, myMessageHandler);
            if (this.cache != null) {
                this.generatedClassHandler = this.cache.getCachingClassHandler();
            }
        }
    }

    protected void createMessageHandler() {
        this.messageHolder = new WeavingAdaptorMessageHolder(new PrintWriter(System.err));
        this.messageHandler = this.messageHolder;
        if (this.verbose) {
            this.messageHandler.dontIgnore(IMessage.INFO);
        }
        if (Boolean.getBoolean(SHOW_WEAVE_INFO_PROPERTY)) {
            this.messageHandler.dontIgnore(IMessage.WEAVEINFO);
        }
        this.info("AspectJ Weaver Version " + Version.getText() + " built on " + Version.getTimeText());
    }

    protected IMessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    public IMessageHolder getMessageHolder() {
        return this.messageHolder;
    }

    protected void setMessageHandler(IMessageHandler mh) {
        if (mh instanceof ISupportsMessageContext) {
            ISupportsMessageContext smc = (ISupportsMessageContext)((Object)mh);
            smc.setMessageContext(this);
        }
        if (mh != this.messageHolder) {
            this.messageHolder.setDelegate(mh);
        }
        this.messageHolder.flushMessages();
    }

    protected void disable() {
        if (trace.isTraceEnabled()) {
            trace.enter("disable", this);
        }
        this.enabled = false;
        this.messageHolder.flushMessages();
        if (trace.isTraceEnabled()) {
            trace.exit("disable");
        }
    }

    protected void enable() {
        this.enabled = true;
        this.messageHolder.flushMessages();
    }

    protected boolean isEnabled() {
        return this.enabled;
    }

    public void addURL(URL url) {
        File libFile = new File(url.getPath());
        try {
            this.weaver.addLibraryJarFile(libFile);
        }
        catch (IOException ex) {
            this.warn("bad library: '" + libFile + "'");
        }
    }

    public byte[] weaveClass(String name, byte[] bytes) throws IOException {
        return this.weaveClass(name, bytes, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] weaveClass(String name, byte[] bytes, boolean mustWeave) throws IOException {
        if (trace == null) {
            System.err.println("AspectJ Weaver cannot continue to weave, static state has been cleared.  Are you under Tomcat? In order to weave '" + name + "' during shutdown, 'org.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false' must be set (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=231945).");
            return bytes;
        }
        if (this.weaverRunning.get().booleanValue()) {
            return bytes;
        }
        try {
            this.weaverRunning.set(true);
            if (trace.isTraceEnabled()) {
                trace.enter("weaveClass", (Object)this, new Object[]{name, bytes});
            }
            if (!this.enabled) {
                if (trace.isTraceEnabled()) {
                    trace.exit("weaveClass", false);
                }
                byte[] byArray = bytes;
                return byArray;
            }
            boolean debugOn = !this.messageHandler.isIgnoring(Message.DEBUG);
            try {
                this.delegateForCurrentClass = null;
                name = name.replace('/', '.');
                if (this.couldWeave(name, bytes)) {
                    if (this.accept(name, bytes)) {
                        CachedClassEntry entry;
                        CachedClassReference cacheKey = null;
                        byte[] original_bytes = bytes;
                        if (this.cache != null && !mustWeave && (entry = this.cache.get(cacheKey = this.cache.createCacheKey(name, original_bytes), original_bytes)) != null) {
                            if (entry.isIgnored()) {
                                byte[] byArray = bytes;
                                return byArray;
                            }
                            byte[] byArray = entry.getBytes();
                            return byArray;
                        }
                        if (debugOn) {
                            this.debug("weaving '" + name + "'");
                        }
                        bytes = this.getWovenBytes(name, bytes);
                        if (cacheKey != null) {
                            if (Arrays.equals(original_bytes, bytes)) {
                                this.cache.ignore(cacheKey, original_bytes);
                            } else {
                                this.cache.put(cacheKey, original_bytes, bytes);
                            }
                        }
                    } else if (debugOn) {
                        this.debug("not weaving '" + name + "'");
                    }
                } else if (debugOn) {
                    this.debug("cannot weave '" + name + "'");
                }
            }
            finally {
                this.delegateForCurrentClass = null;
            }
            if (trace.isTraceEnabled()) {
                trace.exit("weaveClass", bytes);
            }
            byte[] byArray = bytes;
            return byArray;
        }
        finally {
            this.weaverRunning.remove();
        }
    }

    private boolean couldWeave(String name, byte[] bytes) {
        return !this.generatedClasses.containsKey(name) && this.shouldWeaveName(name);
    }

    protected boolean accept(String name, byte[] bytes) {
        return true;
    }

    protected boolean shouldDump(String name, boolean before) {
        return false;
    }

    private boolean shouldWeaveName(String name) {
        if (PACKAGE_INITIAL_CHARS.indexOf(name.charAt(0)) != -1) {
            if ((this.weavingSpecialTypes & 1) == 0) {
                this.weavingSpecialTypes |= 1;
                Properties p = this.weaver.getWorld().getExtraConfiguration();
                if (p != null) {
                    boolean b = p.getProperty("weaveJavaPackages", "false").equalsIgnoreCase("true");
                    if (b) {
                        this.weavingSpecialTypes |= 2;
                    }
                    if (b = p.getProperty("weaveJavaxPackages", "false").equalsIgnoreCase("true")) {
                        this.weavingSpecialTypes |= 4;
                    }
                }
            }
            if (name.startsWith(ASPECTJ_BASE_PACKAGE)) {
                return false;
            }
            if (name.startsWith("sun.reflect.")) {
                return false;
            }
            if (name.startsWith("javax.")) {
                if ((this.weavingSpecialTypes & 4) != 0) {
                    return true;
                }
                if (!this.haveWarnedOnJavax) {
                    this.haveWarnedOnJavax = true;
                    this.warn("javax.* types are not being woven because the weaver option '-Xset:weaveJavaxPackages=true' has not been specified");
                }
                return false;
            }
            if (name.startsWith("java.")) {
                return (this.weavingSpecialTypes & 2) != 0;
            }
        }
        return true;
    }

    private boolean shouldWeaveAnnotationStyleAspect(String name, byte[] bytes) {
        if (this.delegateForCurrentClass == null) {
            this.ensureDelegateInitialized(name, bytes);
        }
        return this.delegateForCurrentClass.isAnnotationStyleAspect();
    }

    protected void ensureDelegateInitialized(String name, byte[] bytes) {
        if (this.delegateForCurrentClass == null) {
            BcelWorld world = (BcelWorld)this.weaver.getWorld();
            this.delegateForCurrentClass = world.addSourceObjectType(name, bytes, false);
        }
    }

    private byte[] getWovenBytes(String name, byte[] bytes) throws IOException {
        WeavingClassFileProvider wcp = new WeavingClassFileProvider(name, bytes);
        this.weaver.weave(wcp);
        return wcp.getBytes();
    }

    private byte[] getAtAspectJAspectBytes(String name, byte[] bytes) throws IOException {
        WeavingClassFileProvider wcp = new WeavingClassFileProvider(name, bytes);
        wcp.setApplyAtAspectJMungersOnly();
        this.weaver.weave(wcp);
        return wcp.getBytes();
    }

    private void registerAspectLibraries(List aspectPath) {
        for (String libName : aspectPath) {
            this.addAspectLibrary(libName);
        }
        this.weaver.prepareForWeave();
    }

    private void addAspectLibrary(String aspectLibraryName) {
        File aspectLibrary = new File(aspectLibraryName);
        if (aspectLibrary.isDirectory() || FileUtil.isZipFile(aspectLibrary)) {
            try {
                this.info("adding aspect library: '" + aspectLibrary + "'");
                this.weaver.addLibraryJarFile(aspectLibrary);
            }
            catch (IOException ex) {
                this.error("exception adding aspect library: '" + ex + "'");
            }
        } else {
            this.error("bad aspect library: '" + aspectLibrary + "'");
        }
    }

    private static List<String> makeClasspath(String cp) {
        ArrayList<String> ret = new ArrayList<String>();
        if (cp != null) {
            StringTokenizer tok = new StringTokenizer(cp, File.pathSeparator);
            while (tok.hasMoreTokens()) {
                ret.add(tok.nextToken());
            }
        }
        return ret;
    }

    protected boolean debug(String message) {
        return MessageUtil.debug(this.messageHandler, message);
    }

    protected boolean info(String message) {
        return MessageUtil.info(this.messageHandler, message);
    }

    protected boolean warn(String message) {
        return MessageUtil.warn(this.messageHandler, message);
    }

    protected boolean warn(String message, Throwable th) {
        return this.messageHandler.handleMessage(new Message(message, IMessage.WARNING, th, null));
    }

    protected boolean error(String message) {
        return MessageUtil.error(this.messageHandler, message);
    }

    protected boolean error(String message, Throwable th) {
        return this.messageHandler.handleMessage(new Message(message, IMessage.ERROR, th, null));
    }

    @Override
    public String getContextId() {
        return "WeavingAdaptor";
    }

    protected void dump(String name, byte[] b, boolean before) {
        String className;
        String dirName = this.getDumpDir();
        if (before) {
            dirName = dirName + File.separator + "_before";
        }
        File dir = (className = name.replace('.', '/')).indexOf(47) > 0 ? new File(dirName + File.separator + className.substring(0, className.lastIndexOf(47))) : new File(dirName);
        dir.mkdirs();
        String fileName = dirName + File.separator + className + ".class";
        try {
            FileOutputStream os = new FileOutputStream(fileName);
            os.write(b);
            os.close();
        }
        catch (IOException ex) {
            this.warn("unable to dump class " + name + " in directory " + dirName, ex);
        }
    }

    protected String getDumpDir() {
        return "_ajdump";
    }

    public void setActiveProtectionDomain(ProtectionDomain protectionDomain) {
        this.activeProtectionDomain = protectionDomain;
    }

    private class WeavingClassFileProvider
    implements IClassFileProvider {
        private final UnwovenClassFile unwovenClass;
        private final List<UnwovenClassFile> unwovenClasses = new ArrayList<UnwovenClassFile>();
        private IUnwovenClassFile wovenClass;
        private boolean isApplyAtAspectJMungersOnly = false;

        public WeavingClassFileProvider(String name, byte[] bytes) {
            WeavingAdaptor.this.ensureDelegateInitialized(name, bytes);
            this.unwovenClass = new UnwovenClassFile(name, WeavingAdaptor.this.delegateForCurrentClass.getResolvedTypeX().getName(), bytes);
            this.unwovenClasses.add(this.unwovenClass);
            if (WeavingAdaptor.this.shouldDump(name.replace('/', '.'), true)) {
                WeavingAdaptor.this.dump(name, bytes, true);
            }
        }

        public void setApplyAtAspectJMungersOnly() {
            this.isApplyAtAspectJMungersOnly = true;
        }

        @Override
        public boolean isApplyAtAspectJMungersOnly() {
            return this.isApplyAtAspectJMungersOnly;
        }

        public byte[] getBytes() {
            if (this.wovenClass != null) {
                return this.wovenClass.getBytes();
            }
            return this.unwovenClass.getBytes();
        }

        @Override
        public Iterator<UnwovenClassFile> getClassFileIterator() {
            return this.unwovenClasses.iterator();
        }

        @Override
        public IWeaveRequestor getRequestor() {
            return new IWeaveRequestor(){

                @Override
                public void acceptResult(IUnwovenClassFile result) {
                    if (WeavingClassFileProvider.this.wovenClass == null) {
                        WeavingClassFileProvider.this.wovenClass = result;
                        String name = result.getClassName();
                        if (WeavingAdaptor.this.shouldDump(name.replace('/', '.'), false)) {
                            WeavingAdaptor.this.dump(name, result.getBytes(), false);
                        }
                    } else {
                        String className = result.getClassName();
                        byte[] resultBytes = result.getBytes();
                        if (SimpleCacheFactory.isEnabled()) {
                            SimpleCache lacache = SimpleCacheFactory.createSimpleCache();
                            lacache.put(result.getClassName(), WeavingClassFileProvider.this.wovenClass.getBytes(), result.getBytes());
                            lacache.addGeneratedClassesNames(WeavingClassFileProvider.this.wovenClass.getClassName(), WeavingClassFileProvider.this.wovenClass.getBytes(), result.getClassName());
                        }
                        WeavingAdaptor.this.generatedClasses.put(className, result);
                        WeavingAdaptor.this.generatedClasses.put(WeavingClassFileProvider.this.wovenClass.getClassName(), result);
                        WeavingAdaptor.this.generatedClassHandler.acceptClass(className, null, resultBytes);
                    }
                }

                @Override
                public void processingReweavableState() {
                }

                @Override
                public void addingTypeMungers() {
                }

                @Override
                public void weavingAspects() {
                }

                @Override
                public void weavingClasses() {
                }

                @Override
                public void weaveCompleted() {
                    if (WeavingAdaptor.this.delegateForCurrentClass != null) {
                        WeavingAdaptor.this.delegateForCurrentClass.weavingCompleted();
                    }
                }
            };
        }
    }

    protected class WeavingAdaptorMessageWriter
    extends MessageWriter {
        private final Set<IMessage.Kind> ignoring;
        private final IMessage.Kind failKind;

        public WeavingAdaptorMessageWriter(PrintWriter writer) {
            super(writer, true);
            this.ignoring = new HashSet<IMessage.Kind>();
            this.ignore(IMessage.WEAVEINFO);
            this.ignore(IMessage.DEBUG);
            this.ignore(IMessage.INFO);
            this.failKind = IMessage.ERROR;
        }

        @Override
        public boolean handleMessage(IMessage message) throws AbortException {
            super.handleMessage(message);
            if (WeavingAdaptor.this.abortOnError && 0 <= message.getKind().compareTo(this.failKind)) {
                throw new AbortException(message);
            }
            return true;
        }

        @Override
        public boolean isIgnoring(IMessage.Kind kind) {
            return null != kind && this.ignoring.contains(kind);
        }

        @Override
        public void ignore(IMessage.Kind kind) {
            if (null != kind && !this.ignoring.contains(kind)) {
                this.ignoring.add(kind);
            }
        }

        @Override
        public void dontIgnore(IMessage.Kind kind) {
            if (null != kind) {
                this.ignoring.remove(kind);
            }
        }

        @Override
        protected String render(IMessage message) {
            return "[" + WeavingAdaptor.this.getContextId() + "] " + super.render(message);
        }
    }

    protected class WeavingAdaptorMessageHolder
    extends MessageHandler {
        private IMessageHandler delegate;
        private List<IMessage> savedMessages;
        protected boolean traceMessages = Boolean.getBoolean("org.aspectj.tracing.messages");

        public WeavingAdaptorMessageHolder(PrintWriter writer) {
            this.delegate = new WeavingAdaptorMessageWriter(writer);
            super.dontIgnore(IMessage.WEAVEINFO);
        }

        private void traceMessage(IMessage message) {
            if (message instanceof WeaveMessage) {
                trace.debug(this.render(message));
            } else if (message.isDebug()) {
                trace.debug(this.render(message));
            } else if (message.isInfo()) {
                trace.info(this.render(message));
            } else if (message.isWarning()) {
                trace.warn(this.render(message), message.getThrown());
            } else if (message.isError()) {
                trace.error(this.render(message), message.getThrown());
            } else if (message.isFailed()) {
                trace.fatal(this.render(message), message.getThrown());
            } else if (message.isAbort()) {
                trace.fatal(this.render(message), message.getThrown());
            } else {
                trace.error(this.render(message), message.getThrown());
            }
        }

        protected String render(IMessage message) {
            return "[" + WeavingAdaptor.this.getContextId() + "] " + message.toString();
        }

        public void flushMessages() {
            if (this.savedMessages == null) {
                this.savedMessages = new ArrayList<IMessage>();
                this.savedMessages.addAll(super.getUnmodifiableListView());
                this.clearMessages();
                for (IMessage message : this.savedMessages) {
                    this.delegate.handleMessage(message);
                }
            }
        }

        public void setDelegate(IMessageHandler messageHandler) {
            this.delegate = messageHandler;
        }

        @Override
        public boolean handleMessage(IMessage message) throws AbortException {
            if (this.traceMessages) {
                this.traceMessage(message);
            }
            super.handleMessage(message);
            if (WeavingAdaptor.this.abortOnError && 0 <= message.getKind().compareTo(IMessage.ERROR)) {
                throw new AbortException(message);
            }
            if (this.savedMessages != null) {
                this.delegate.handleMessage(message);
            }
            return true;
        }

        @Override
        public boolean isIgnoring(IMessage.Kind kind) {
            return this.delegate.isIgnoring(kind);
        }

        @Override
        public void dontIgnore(IMessage.Kind kind) {
            if (null != kind && this.delegate != null) {
                this.delegate.dontIgnore(kind);
            }
        }

        @Override
        public void ignore(IMessage.Kind kind) {
            if (null != kind && this.delegate != null) {
                this.delegate.ignore(kind);
            }
        }

        @Override
        public List<IMessage> getUnmodifiableListView() {
            ArrayList<IMessage> allMessages = new ArrayList<IMessage>();
            allMessages.addAll(this.savedMessages);
            allMessages.addAll(super.getUnmodifiableListView());
            return allMessages;
        }
    }
}


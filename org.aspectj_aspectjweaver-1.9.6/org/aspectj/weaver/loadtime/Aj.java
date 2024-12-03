/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor;
import org.aspectj.weaver.loadtime.ClassPreProcessor;
import org.aspectj.weaver.loadtime.IWeavingContext;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.WeavingAdaptor;
import org.aspectj.weaver.tools.cache.SimpleCache;
import org.aspectj.weaver.tools.cache.SimpleCacheFactory;

public class Aj
implements ClassPreProcessor {
    private IWeavingContext weavingContext;
    public static SimpleCache laCache = SimpleCacheFactory.createSimpleCache();
    private static ReferenceQueue adaptorQueue = new ReferenceQueue();
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(Aj.class);
    private static final String deleLoader = "sun.reflect.DelegatingClassLoader";
    private static final String deleLoader2 = "jdk.internal.reflect.DelegatingClassLoader";
    public static List<String> loadersToSkip = null;

    public Aj() {
        this(null);
    }

    public Aj(IWeavingContext context) {
        if (trace.isTraceEnabled()) {
            trace.enter("<init>", (Object)this, new Object[]{context, this.getClass().getClassLoader()});
        }
        this.weavingContext = context;
        if (trace.isTraceEnabled()) {
            trace.exit("<init>");
        }
    }

    @Override
    public void initialize() {
    }

    /*
     * Exception decompiling
     */
    @Override
    public byte[] preProcess(String className, byte[] bytes, ClassLoader loader, ProtectionDomain protectionDomain) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int removeStaleAdaptors(boolean displayProgress) {
        int removed = 0;
        Map<AdaptorKey, ExplicitlyInitializedClassLoaderWeavingAdaptor> map = WeaverContainer.weavingAdaptors;
        synchronized (map) {
            if (displayProgress) {
                System.err.println("Weaver adaptors before queue processing:");
                Map<AdaptorKey, ExplicitlyInitializedClassLoaderWeavingAdaptor> m = WeaverContainer.weavingAdaptors;
                Set<AdaptorKey> keys = m.keySet();
                for (AdaptorKey object : keys) {
                    System.err.println(object + " = " + WeaverContainer.weavingAdaptors.get(object));
                }
            }
            Reference o = adaptorQueue.poll();
            while (o != null) {
                AdaptorKey wo;
                boolean didit;
                if (displayProgress) {
                    System.err.println("Processing referencequeue entry " + o);
                }
                boolean bl = didit = WeaverContainer.weavingAdaptors.remove(wo = (AdaptorKey)o) != null;
                if (didit) {
                    ++removed;
                } else {
                    throw new RuntimeException("Eh?? key=" + wo);
                }
                if (displayProgress) {
                    System.err.println("Removed? " + didit);
                }
                o = adaptorQueue.poll();
            }
            if (displayProgress) {
                System.err.println("Weaver adaptors after queue processing:");
                Map<AdaptorKey, ExplicitlyInitializedClassLoaderWeavingAdaptor> m = WeaverContainer.weavingAdaptors;
                Set<AdaptorKey> keys = m.keySet();
                for (AdaptorKey object : keys) {
                    System.err.println(object + " = " + WeaverContainer.weavingAdaptors.get(object));
                }
            }
        }
        return removed;
    }

    public static int getActiveAdaptorCount() {
        return WeaverContainer.weavingAdaptors.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void checkQ() {
        ReferenceQueue referenceQueue = adaptorQueue;
        synchronized (referenceQueue) {
            Reference o = adaptorQueue.poll();
            while (o != null) {
                AdaptorKey wo = (AdaptorKey)o;
                WeaverContainer.weavingAdaptors.remove(wo);
                o = adaptorQueue.poll();
            }
        }
    }

    public String getNamespace(ClassLoader loader) {
        ClassLoaderWeavingAdaptor weavingAdaptor = (ClassLoaderWeavingAdaptor)WeaverContainer.getWeaver(loader, this.weavingContext);
        return weavingAdaptor.getNamespace();
    }

    public boolean generatedClassesExist(ClassLoader loader) {
        return ((ClassLoaderWeavingAdaptor)WeaverContainer.getWeaver(loader, this.weavingContext)).generatedClassesExistFor(null);
    }

    public void flushGeneratedClasses(ClassLoader loader) {
        ((ClassLoaderWeavingAdaptor)WeaverContainer.getWeaver(loader, this.weavingContext)).flushGeneratedClasses();
    }

    @Override
    public void prepareForRedefinition(ClassLoader loader, String className) {
        ((ClassLoaderWeavingAdaptor)WeaverContainer.getWeaver(loader, this.weavingContext)).flushGeneratedClassesFor(className);
    }

    static {
        new ExplicitlyInitializedClassLoaderWeavingAdaptor(new ClassLoaderWeavingAdaptor());
        try {
            String loadersToSkipProperty = System.getProperty("aj.weaving.loadersToSkip", "");
            StringTokenizer st = new StringTokenizer(loadersToSkipProperty, ",");
            if (loadersToSkipProperty != null && loadersToSkip == null) {
                if (st.hasMoreTokens()) {
                    loadersToSkip = new ArrayList<String>();
                }
                while (st.hasMoreTokens()) {
                    String nextLoader = st.nextToken();
                    loadersToSkip.add(nextLoader);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static class ExplicitlyInitializedClassLoaderWeavingAdaptor {
        private final ClassLoaderWeavingAdaptor weavingAdaptor;
        private boolean isInitialized;

        public ExplicitlyInitializedClassLoaderWeavingAdaptor(ClassLoaderWeavingAdaptor weavingAdaptor) {
            this.weavingAdaptor = weavingAdaptor;
            this.isInitialized = false;
        }

        private void initialize(ClassLoader loader, IWeavingContext weavingContext) {
            if (!this.isInitialized) {
                this.isInitialized = true;
                this.weavingAdaptor.initialize(loader, weavingContext);
            }
        }

        public ClassLoaderWeavingAdaptor getWeavingAdaptor(ClassLoader loader, IWeavingContext weavingContext) {
            this.initialize(loader, weavingContext);
            return this.weavingAdaptor;
        }
    }

    static class WeaverContainer {
        static final Map<AdaptorKey, ExplicitlyInitializedClassLoaderWeavingAdaptor> weavingAdaptors = Collections.synchronizedMap(new HashMap());
        private static final ClassLoader myClassLoader = WeavingAdaptor.class.getClassLoader();
        private static ExplicitlyInitializedClassLoaderWeavingAdaptor myClassLoaderAdaptor;

        WeaverContainer() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        static WeavingAdaptor getWeaver(ClassLoader loader, IWeavingContext weavingContext) {
            ExplicitlyInitializedClassLoaderWeavingAdaptor adaptor = null;
            AdaptorKey adaptorKey = new AdaptorKey(loader);
            String loaderClassName = loader.getClass().getName();
            Map<AdaptorKey, ExplicitlyInitializedClassLoaderWeavingAdaptor> map = weavingAdaptors;
            synchronized (map) {
                Aj.checkQ();
                adaptor = loader.equals(myClassLoader) ? myClassLoaderAdaptor : weavingAdaptors.get(adaptorKey);
                if (adaptor == null) {
                    ClassLoaderWeavingAdaptor weavingAdaptor = new ClassLoaderWeavingAdaptor();
                    adaptor = new ExplicitlyInitializedClassLoaderWeavingAdaptor(weavingAdaptor);
                    if (myClassLoaderAdaptor == null && loader.equals(myClassLoader)) {
                        myClassLoaderAdaptor = adaptor;
                    } else {
                        weavingAdaptors.put(adaptorKey, adaptor);
                    }
                }
            }
            return adaptor.getWeavingAdaptor(loader, weavingContext);
        }
    }

    private static class AdaptorKey
    extends WeakReference {
        private final int loaderHashCode;
        private final int sysHashCode;
        private final int hashValue;
        private final String loaderClass;

        public AdaptorKey(ClassLoader loader) {
            super(loader, adaptorQueue);
            this.loaderHashCode = loader.hashCode();
            this.sysHashCode = System.identityHashCode(loader);
            this.loaderClass = loader.getClass().getName();
            this.hashValue = this.loaderHashCode + this.sysHashCode + this.loaderClass.hashCode();
        }

        public ClassLoader getClassLoader() {
            ClassLoader instance = (ClassLoader)this.get();
            return instance;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AdaptorKey)) {
                return false;
            }
            AdaptorKey other = (AdaptorKey)obj;
            return other.loaderHashCode == this.loaderHashCode && other.sysHashCode == this.sysHashCode && this.loaderClass.equals(other.loaderClass);
        }

        public int hashCode() {
            return this.hashValue;
        }
    }
}


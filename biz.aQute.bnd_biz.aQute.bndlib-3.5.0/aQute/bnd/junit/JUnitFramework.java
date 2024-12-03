/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.launch.Framework
 *  org.osgi.framework.launch.FrameworkFactory
 *  org.osgi.util.tracker.ServiceTracker
 */
package aQute.bnd.junit;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.Run;
import aQute.bnd.build.Workspace;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.JarResource;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.URLResource;
import aQute.bnd.service.Strategy;
import aQute.bnd.version.VersionRange;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.io.IO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class JUnitFramework
implements AutoCloseable {
    ExecutorService executor = Executors.newCachedThreadPool();
    public final List<ServiceTracker<?, ?>> trackers = new ArrayList();
    public final Jar bin_test;
    public final Framework framework;
    public final BundleContext context;
    public final File projectDir;
    public Workspace workspace;
    public Project project;
    static AtomicInteger n = new AtomicInteger();

    public JUnitFramework() {
        this(IO.work);
    }

    public JUnitFramework(File projectDir) {
        this.projectDir = projectDir.getAbsoluteFile();
        try {
            Project p = this.getProject();
            File bin_test = p.getTestOutput();
            this.bin_test = new Jar(bin_test);
            String extra = this.getExtra();
            HashMap<String, String> props = new HashMap<String, String>();
            props.put("org.osgi.framework.system.packages.extra", extra);
            File storage = IO.getFile(p.getTarget(), "fw");
            IO.delete(storage);
            props.put("org.osgi.framework.storage", storage.getAbsolutePath());
            props.put("org.osgi.framework.storage.clean", "onFirstInit");
            FrameworkFactory factory = this.getFactory();
            this.framework = factory.newFramework(props);
            this.framework.init();
            this.framework.start();
            this.context = this.framework.getBundleContext();
        }
        catch (Exception e) {
            throw Exceptions.duck(e);
        }
    }

    private String getExtra() throws Exception {
        try (Analyzer a = new Analyzer();){
            VersionRange vr;
            String v;
            for (Container c : this.getProject().getBuildpath()) {
                assert (c.getError() == null);
                a.addClasspath(c.getFile());
            }
            for (Container c : this.getProject().getTestpath()) {
                assert (c.getError() == null);
                a.addClasspath(c.getFile());
            }
            a.setJar(this.bin_test);
            a.removeClose(this.bin_test);
            a.calcManifest();
            StringBuilder extra = new StringBuilder();
            String del = "";
            for (Map.Entry<Descriptors.PackageRef, Attrs> e : a.getImports().entrySet()) {
                extra.append(del);
                extra.append(e.getKey().getFQN());
                v = e.getValue().getVersion();
                if (v != null) {
                    vr = VersionRange.parseOSGiVersionRange(v);
                    extra.append(";version=").append(vr.getLow());
                }
                del = ",";
            }
            for (Map.Entry<Descriptors.PackageRef, Attrs> e : a.getContained().entrySet()) {
                extra.append(del);
                extra.append(e.getKey().getFQN());
                v = e.getValue().getVersion();
                if (v != null) {
                    vr = VersionRange.parseOSGiVersionRange(v);
                    extra.append(";version=").append(vr.getLow());
                }
                del = ",";
            }
            String string = extra.toString();
            return string;
        }
    }

    @Override
    public void close() throws Exception {
        for (ServiceTracker<?, ?> st : this.trackers) {
            st.close();
        }
        this.framework.stop();
        this.framework.waitForStop(10000L);
        this.executor.shutdownNow();
        this.bin_test.close();
    }

    public BundleContext getBundleContext() {
        return this.context;
    }

    public <T> List<T> getServices(Class<T> class1) throws InvalidSyntaxException {
        Collection refs = this.context.getServiceReferences(class1, null);
        ArrayList<Object> result = new ArrayList<Object>();
        for (ServiceReference ref : refs) {
            Object service = this.context.getService(ref);
            if (service == null) continue;
            result.add(service);
        }
        return result;
    }

    public <T> T getService(Class<T> class1) throws Exception {
        List<T> services = this.getServices(class1);
        assert (1 == services.size());
        return services.get(0);
    }

    public <T> Promise<T> waitForService(final Class<T> class1, final long timeoutInMs) throws Exception {
        final Deferred deferred = new Deferred();
        this.executor.execute(new Runnable(){

            @Override
            public void run() {
                ServiceTracker tracker = new ServiceTracker(JUnitFramework.this.context, class1, null);
                tracker.open();
                try {
                    Object s = tracker.waitForService(timeoutInMs);
                    if (s != null) {
                        deferred.resolve(s);
                    } else {
                        deferred.fail(new Exception("No service object " + class1));
                    }
                }
                catch (InterruptedException e) {
                    deferred.fail(e);
                }
            }
        });
        return deferred.getPromise();
    }

    public BundleBuilder bundle() throws IOException {
        BundleBuilder bundleBuilder = new BundleBuilder();
        bundleBuilder.addClasspath(this.bin_test);
        bundleBuilder.removeClose(this.bin_test);
        return bundleBuilder;
    }

    public void addBundles(String bndrun) throws Exception {
        this.addBundles(IO.getFile(bndrun));
    }

    public void addBundles(File bndrun) throws Exception {
        Run run = Run.createRun(this.getWorkspace(), bndrun);
        ArrayList<Bundle> bundles = new ArrayList<Bundle>();
        for (Container c : run.getRunbundles()) {
            assert (c.getError() == null);
            Bundle bundle = this.context.installBundle(c.getFile().toURI().toString());
            bundles.add(bundle);
        }
        this.startAll(bundles);
    }

    public Workspace getWorkspace() throws Exception {
        if (this.workspace == null) {
            this.workspace = Workspace.getWorkspace(this.projectDir.getParentFile());
        }
        return this.workspace;
    }

    public Project getProject() throws Exception {
        if (this.project == null) {
            this.project = this.getWorkspace().getProjectFromFile(this.projectDir);
            assert (this.project.check(new String[0]));
        }
        return this.project;
    }

    public void startAll(List<Bundle> bundles) throws BundleException {
        for (Bundle b : bundles) {
            b.start();
        }
    }

    public List<Bundle> addBundle(String spec) throws Exception {
        Parameters p = new Parameters(spec);
        ArrayList<Bundle> bundles = new ArrayList<Bundle>();
        for (Map.Entry<String, Attrs> e : p.entrySet()) {
            Container c = this.getProject().getBundle(e.getKey(), e.getValue().get("version"), Strategy.HIGHEST, e.getValue());
            assert (c.getError() == null);
            Bundle bundle = this.context.installBundle(c.getFile().toURI().toString());
            bundles.add(bundle);
        }
        this.startAll(bundles);
        return bundles;
    }

    private FrameworkFactory getFactory() throws Exception {
        ServiceLoader<FrameworkFactory> sl = ServiceLoader.load(FrameworkFactory.class);
        Iterator<FrameworkFactory> i$ = sl.iterator();
        if (i$.hasNext()) {
            FrameworkFactory ff = i$.next();
            return ff;
        }
        throw new FileNotFoundException("No Framework found on classpath");
    }

    public class BundleBuilder
    extends Builder {
        Map<String, Resource> additionalResources = new HashMap<String, Resource>();

        BundleBuilder() {
            this.setBundleSymbolicName("test-" + n.incrementAndGet());
        }

        public BundleBuilder addResource(String path, URL url) {
            return this.addResource(path, new URLResource(url));
        }

        public BundleBuilder addResource(String path, Resource resource) {
            this.additionalResources.put(path, resource);
            return this;
        }

        /*
         * Loose catch block
         */
        public Bundle install() throws Exception {
            try {
                Jar jar = this.build();
                for (Map.Entry<String, Resource> e : this.additionalResources.entrySet()) {
                    jar.putResource(e.getKey(), e.getValue());
                }
                try (JarResource j = new JarResource(jar);){
                    Bundle bundle = JUnitFramework.this.context.installBundle("generated " + jar.getBsn(), j.openInputStream());
                    return bundle;
                }
                {
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                }
            }
            finally {
                this.close();
            }
        }

        @Override
        public void close() throws IOException {
            this.getClasspath().remove(JUnitFramework.this.bin_test);
            super.close();
        }
    }
}


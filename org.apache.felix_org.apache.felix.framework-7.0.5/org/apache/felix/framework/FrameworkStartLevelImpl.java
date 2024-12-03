/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.List;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.ServiceRegistry;
import org.apache.felix.framework.StartLevelImpl;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.startlevel.StartLevel;

class FrameworkStartLevelImpl
implements FrameworkStartLevel,
Runnable {
    static final String THREAD_NAME = "FelixStartLevel";
    private final Felix m_felix;
    private final ServiceRegistry m_registry;
    private final List<StartLevelRequest> m_requests = new ArrayList<StartLevelRequest>();
    private ServiceRegistration<StartLevel> m_slReg;
    private Thread m_thread = null;

    FrameworkStartLevelImpl(Felix felix, ServiceRegistry registry) {
        this.m_felix = felix;
        this.m_registry = registry;
    }

    void start() {
        this.m_slReg = this.m_registry.registerService(this.m_felix, new String[]{StartLevel.class.getName()}, new StartLevelImpl(this.m_felix), null);
    }

    private void startThread() {
        if (this.m_thread == null) {
            this.m_thread = new Thread((Runnable)this, THREAD_NAME);
            this.m_thread.setDaemon(true);
            this.m_thread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void stop() {
        List<StartLevelRequest> list = this.m_requests;
        synchronized (list) {
            if (this.m_thread != null) {
                this.m_thread = null;
                this.m_requests.notifyAll();
            }
        }
    }

    @Override
    public Bundle getBundle() {
        return this.m_felix;
    }

    @Override
    public int getStartLevel() {
        return this.m_felix.getActiveStartLevel();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setStartLevel(int startlevel, FrameworkListener ... listeners) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_felix, "startlevel"));
        }
        if (startlevel <= 0) {
            throw new IllegalArgumentException("Start level must be greater than zero.");
        }
        List<StartLevelRequest> list = this.m_requests;
        synchronized (list) {
            if (this.m_thread == null) {
                throw new IllegalStateException("No inital startlevel yet");
            }
            this.m_requests.add(new StartLevelRequest(null, startlevel, listeners));
            this.m_requests.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setStartLevelAndWait(int startlevel) {
        StartLevelRequest request;
        StartLevelRequest startLevelRequest = request = new StartLevelRequest(null, startlevel, new FrameworkListener[0]);
        synchronized (startLevelRequest) {
            List<StartLevelRequest> list = this.m_requests;
            synchronized (list) {
                this.startThread();
                this.m_requests.add(request);
                this.m_requests.notifyAll();
            }
            try {
                request.wait();
            }
            catch (InterruptedException ex) {
                this.m_felix.getLogger().log(2, "Wait for start level change during shutdown interrupted.", ex);
            }
        }
    }

    @Override
    public int getInitialBundleStartLevel() {
        return this.m_felix.getInitialBundleStartLevel();
    }

    @Override
    public void setInitialBundleStartLevel(int startlevel) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_felix, "startlevel"));
        }
        this.m_felix.setInitialBundleStartLevel(startlevel);
    }

    BundleStartLevel createBundleStartLevel(BundleImpl bundle) {
        return new BundleStartLevelImpl(bundle);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        StartLevelRequest previousRequest = null;
        while (true) {
            StartLevelRequest request = null;
            Object object = this.m_requests;
            synchronized (object) {
                while (this.m_requests.isEmpty()) {
                    if (this.m_thread == null) {
                        return;
                    }
                    try {
                        this.m_requests.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                request = this.m_requests.remove(0);
            }
            if (request.getBundle() == null) {
                try {
                    this.m_felix.setActiveStartLevel(request.getStartLevel(), request.getListeners());
                }
                catch (IllegalStateException ise) {
                    if (previousRequest == request) {
                        this.m_felix.getLogger().log(1, "Unexpected problem setting active start level to " + request, ise);
                    }
                    List<StartLevelRequest> list = this.m_requests;
                    synchronized (list) {
                        this.m_requests.add(0, request);
                        previousRequest = request;
                    }
                }
                catch (Exception ex) {
                    this.m_felix.getLogger().log(1, "Unexpected problem setting active start level to " + request, ex);
                }
            } else {
                this.m_felix.setBundleStartLevel(request.getBundle(), request.getStartLevel());
            }
            object = request;
            synchronized (object) {
                request.notifyAll();
            }
        }
    }

    private static final class StartLevelRequest {
        private final Bundle m_bundle;
        private final int m_startLevel;
        private final FrameworkListener[] m_listeners;

        private StartLevelRequest(Bundle bundle, int startLevel, FrameworkListener ... listeners) {
            this.m_bundle = bundle;
            this.m_startLevel = startLevel;
            this.m_listeners = listeners;
        }

        private Bundle getBundle() {
            return this.m_bundle;
        }

        private int getStartLevel() {
            return this.m_startLevel;
        }

        public FrameworkListener[] getListeners() {
            return this.m_listeners;
        }
    }

    class BundleStartLevelImpl
    implements BundleStartLevel {
        private BundleImpl m_bundle;

        private BundleStartLevelImpl(BundleImpl bundle) {
            this.m_bundle = bundle;
        }

        @Override
        public Bundle getBundle() {
            return this.m_bundle;
        }

        @Override
        public int getStartLevel() {
            return FrameworkStartLevelImpl.this.m_felix.getBundleStartLevel(this.m_bundle);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setStartLevel(int startlevel) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(new AdminPermission(this.m_bundle, "execute"));
            }
            if (this.m_bundle.getBundleId() == 0L) {
                throw new IllegalArgumentException("Cannot change system bundle start level.");
            }
            if (startlevel <= 0) {
                throw new IllegalArgumentException("Start level must be greater than zero.");
            }
            List list = FrameworkStartLevelImpl.this.m_requests;
            synchronized (list) {
                FrameworkStartLevelImpl.this.startThread();
                this.m_bundle.setStartLevel(startlevel);
                FrameworkStartLevelImpl.this.m_requests.add(new StartLevelRequest((Bundle)this.m_bundle, startlevel, new FrameworkListener[0]));
                FrameworkStartLevelImpl.this.m_requests.notifyAll();
            }
        }

        @Override
        public boolean isPersistentlyStarted() {
            return FrameworkStartLevelImpl.this.m_felix.isBundlePersistentlyStarted(this.m_bundle);
        }

        @Override
        public boolean isActivationPolicyUsed() {
            return FrameworkStartLevelImpl.this.m_felix.isBundleActivationPolicyUsed(this.m_bundle);
        }
    }
}


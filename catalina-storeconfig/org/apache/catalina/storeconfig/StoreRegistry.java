/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.CredentialHandler
 *  org.apache.catalina.LifecycleListener
 *  org.apache.catalina.Manager
 *  org.apache.catalina.Realm
 *  org.apache.catalina.Valve
 *  org.apache.catalina.WebResourceRoot
 *  org.apache.catalina.WebResourceSet
 *  org.apache.catalina.ha.CatalinaCluster
 *  org.apache.catalina.ha.ClusterDeployer
 *  org.apache.catalina.ha.ClusterListener
 *  org.apache.catalina.tribes.Channel
 *  org.apache.catalina.tribes.ChannelInterceptor
 *  org.apache.catalina.tribes.ChannelReceiver
 *  org.apache.catalina.tribes.ChannelSender
 *  org.apache.catalina.tribes.Member
 *  org.apache.catalina.tribes.MembershipService
 *  org.apache.catalina.tribes.MessageListener
 *  org.apache.catalina.tribes.transport.DataSender
 *  org.apache.coyote.UpgradeProtocol
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.http.CookieProcessor
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.storeconfig;

import java.util.HashMap;
import java.util.Map;
import javax.naming.directory.DirContext;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.ha.ClusterListener;
import org.apache.catalina.storeconfig.IStoreFactory;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipService;
import org.apache.catalina.tribes.MessageListener;
import org.apache.catalina.tribes.transport.DataSender;
import org.apache.coyote.UpgradeProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.res.StringManager;

public class StoreRegistry {
    private static Log log = LogFactory.getLog(StoreRegistry.class);
    private static StringManager sm = StringManager.getManager(StoreRegistry.class);
    private Map<String, StoreDescription> descriptors = new HashMap<String, StoreDescription>();
    private String encoding = "UTF-8";
    private String name;
    private String version;
    private static Class<?>[] interfaces = new Class[]{CatalinaCluster.class, ChannelSender.class, ChannelReceiver.class, Channel.class, MembershipService.class, ClusterDeployer.class, Realm.class, Manager.class, DirContext.class, LifecycleListener.class, Valve.class, ClusterListener.class, MessageListener.class, DataSender.class, ChannelInterceptor.class, Member.class, WebResourceRoot.class, WebResourceSet.class, CredentialHandler.class, UpgradeProtocol.class, CookieProcessor.class};

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public StoreDescription findDescription(String id) {
        StoreDescription desc;
        if (log.isDebugEnabled()) {
            log.debug((Object)("search descriptor " + id));
        }
        if ((desc = this.descriptors.get(id)) == null) {
            Class<?> aClass = null;
            try {
                aClass = Class.forName(id, true, this.getClass().getClassLoader());
            }
            catch (ClassNotFoundException e) {
                log.error((Object)sm.getString("registry.loadClassFailed", new Object[]{id}), (Throwable)e);
            }
            if (aClass != null) {
                desc = this.descriptors.get(aClass.getName());
                for (int i = 0; desc == null && i < interfaces.length; ++i) {
                    if (!interfaces[i].isAssignableFrom(aClass)) continue;
                    desc = this.descriptors.get(interfaces[i].getName());
                }
            }
        }
        if (log.isDebugEnabled()) {
            if (desc != null) {
                log.debug((Object)("find descriptor " + id + "#" + desc.getTag() + "#" + desc.getStoreFactoryClass()));
            } else {
                log.debug((Object)("Can't find descriptor for key " + id));
            }
        }
        return desc;
    }

    public StoreDescription findDescription(Class<?> aClass) {
        return this.findDescription(aClass.getName());
    }

    public IStoreFactory findStoreFactory(String aClassName) {
        StoreDescription desc = this.findDescription(aClassName);
        if (desc != null) {
            return desc.getStoreFactory();
        }
        return null;
    }

    public IStoreFactory findStoreFactory(Class<?> aClass) {
        return this.findStoreFactory(aClass.getName());
    }

    public void registerDescription(StoreDescription desc) {
        String key = desc.getId();
        if (key == null || key.isEmpty()) {
            key = desc.getTagClass();
        }
        this.descriptors.put(key, desc);
        if (log.isDebugEnabled()) {
            log.debug((Object)("register store descriptor " + key + "#" + desc.getTag() + "#" + desc.getTagClass()));
        }
    }

    public StoreDescription unregisterDescription(StoreDescription desc) {
        String key = desc.getId();
        if (key == null || "".equals(key)) {
            key = desc.getTagClass();
        }
        return this.descriptors.remove(key);
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }
}


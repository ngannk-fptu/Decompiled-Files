/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Contained
 *  org.apache.catalina.Container
 *  org.apache.catalina.Context
 *  org.apache.catalina.Engine
 *  org.apache.catalina.Host
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.LifecycleState
 *  org.apache.catalina.Manager
 *  org.apache.catalina.Valve
 *  org.apache.catalina.tribes.Channel
 *  org.apache.catalina.tribes.ChannelInterceptor
 *  org.apache.catalina.tribes.ChannelListener
 *  org.apache.catalina.tribes.Member
 *  org.apache.catalina.tribes.MembershipListener
 *  org.apache.catalina.tribes.group.GroupChannel
 *  org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor
 *  org.apache.catalina.tribes.group.interceptors.TcpFailureDetector
 *  org.apache.catalina.util.LifecycleMBeanBase
 *  org.apache.catalina.util.ToStringUtil
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.tcp;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.ObjectName;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Valve;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.ha.ClusterListener;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.ha.session.ClusterSessionListener;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.session.JvmRouteBinderValve;
import org.apache.catalina.ha.tcp.ReplicationValve;
import org.apache.catalina.ha.tcp.SendMessageData;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.group.GroupChannel;
import org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class SimpleTcpCluster
extends LifecycleMBeanBase
implements CatalinaCluster,
MembershipListener,
ChannelListener {
    public static final Log log = LogFactory.getLog(SimpleTcpCluster.class);
    public static final String BEFORE_MEMBERREGISTER_EVENT = "before_member_register";
    public static final String AFTER_MEMBERREGISTER_EVENT = "after_member_register";
    public static final String BEFORE_MANAGERREGISTER_EVENT = "before_manager_register";
    public static final String AFTER_MANAGERREGISTER_EVENT = "after_manager_register";
    public static final String BEFORE_MANAGERUNREGISTER_EVENT = "before_manager_unregister";
    public static final String AFTER_MANAGERUNREGISTER_EVENT = "after_manager_unregister";
    public static final String BEFORE_MEMBERUNREGISTER_EVENT = "before_member_unregister";
    public static final String AFTER_MEMBERUNREGISTER_EVENT = "after_member_unregister";
    public static final String SEND_MESSAGE_FAILURE_EVENT = "send_message_failure";
    public static final String RECEIVE_MESSAGE_FAILURE_EVENT = "receive_message_failure";
    protected Channel channel = new GroupChannel();
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.ha.tcp");
    protected String clusterName;
    protected boolean heartbeatBackgroundEnabled = false;
    protected Container container = null;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected final Map<String, ClusterManager> managers = new HashMap<String, ClusterManager>();
    protected ClusterManager managerTemplate = new DeltaManager();
    private final List<Valve> valves = new ArrayList<Valve>();
    private ClusterDeployer clusterDeployer;
    private ObjectName onameClusterDeployer;
    protected final List<ClusterListener> clusterListeners = new ArrayList<ClusterListener>();
    private boolean notifyLifecycleListenerOnFailure = false;
    private int channelSendOptions = 8;
    private int channelStartOptions = 15;
    private final Map<Member, ObjectName> memberOnameMap = new ConcurrentHashMap<Member, ObjectName>();
    protected boolean hasMembers = false;

    public boolean isHeartbeatBackgroundEnabled() {
        return this.heartbeatBackgroundEnabled;
    }

    public void setHeartbeatBackgroundEnabled(boolean heartbeatBackgroundEnabled) {
        this.heartbeatBackgroundEnabled = heartbeatBackgroundEnabled;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterName() {
        if (this.clusterName == null && this.container != null) {
            return this.container.getName();
        }
        return this.clusterName;
    }

    public void setContainer(Container container) {
        Container oldContainer = this.container;
        this.container = container;
        this.support.firePropertyChange("container", oldContainer, this.container);
    }

    public Container getContainer() {
        return this.container;
    }

    public boolean isNotifyLifecycleListenerOnFailure() {
        return this.notifyLifecycleListenerOnFailure;
    }

    public void setNotifyLifecycleListenerOnFailure(boolean notifyListenerOnFailure) {
        boolean oldNotifyListenerOnFailure = this.notifyLifecycleListenerOnFailure;
        this.notifyLifecycleListenerOnFailure = notifyListenerOnFailure;
        this.support.firePropertyChange("notifyLifecycleListenerOnFailure", oldNotifyListenerOnFailure, this.notifyLifecycleListenerOnFailure);
    }

    @Override
    public void addValve(Valve valve) {
        if (valve instanceof ClusterValve && !this.valves.contains(valve)) {
            this.valves.add(valve);
        }
    }

    @Override
    public Valve[] getValves() {
        return this.valves.toArray(new Valve[0]);
    }

    public ClusterListener[] findClusterListeners() {
        return this.clusterListeners.toArray(new ClusterListener[0]);
    }

    @Override
    public void addClusterListener(ClusterListener listener) {
        if (listener != null && !this.clusterListeners.contains(listener)) {
            this.clusterListeners.add(listener);
            listener.setCluster(this);
        }
    }

    @Override
    public void removeClusterListener(ClusterListener listener) {
        if (listener != null) {
            this.clusterListeners.remove(listener);
            listener.setCluster(null);
        }
    }

    @Override
    public ClusterDeployer getClusterDeployer() {
        return this.clusterDeployer;
    }

    @Override
    public void setClusterDeployer(ClusterDeployer clusterDeployer) {
        this.clusterDeployer = clusterDeployer;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setManagerTemplate(ClusterManager managerTemplate) {
        this.managerTemplate = managerTemplate;
    }

    public void setChannelSendOptions(int channelSendOptions) {
        this.channelSendOptions = channelSendOptions;
    }

    public void setChannelSendOptions(String channelSendOptions) {
        int value = Channel.parseSendOptions((String)channelSendOptions);
        if (value > 0) {
            this.setChannelSendOptions(value);
        }
    }

    @Override
    public boolean hasMembers() {
        return this.hasMembers;
    }

    @Override
    public Member[] getMembers() {
        return this.channel.getMembers();
    }

    @Override
    public Member getLocalMember() {
        return this.channel.getLocalMember(true);
    }

    @Override
    public Map<String, ClusterManager> getManagers() {
        return this.managers;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    public ClusterManager getManagerTemplate() {
        return this.managerTemplate;
    }

    public int getChannelSendOptions() {
        return this.channelSendOptions;
    }

    public String getChannelSendOptionsName() {
        return Channel.getSendOptionsAsString((int)this.channelSendOptions);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Manager createManager(String name) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Creating ClusterManager for context " + name + " using class " + this.getManagerTemplate().getClass().getName()));
        }
        ClusterManager manager = null;
        try {
            manager = this.managerTemplate.cloneFromTemplate();
            manager.setName(name);
        }
        catch (Exception x) {
            log.error((Object)sm.getString("simpleTcpCluster.clustermanager.cloneFailed"), (Throwable)x);
            manager = new DeltaManager();
        }
        finally {
            if (manager != null) {
                manager.setCluster(this);
            }
        }
        return manager;
    }

    public void registerManager(Manager manager) {
        if (!(manager instanceof ClusterManager)) {
            log.warn((Object)sm.getString("simpleTcpCluster.clustermanager.notImplement", new Object[]{manager}));
            return;
        }
        ClusterManager cmanager = (ClusterManager)manager;
        this.fireLifecycleEvent(BEFORE_MANAGERREGISTER_EVENT, manager);
        String clusterName = this.getManagerName(cmanager.getName(), manager);
        cmanager.setName(clusterName);
        cmanager.setCluster(this);
        this.managers.put(clusterName, cmanager);
        this.fireLifecycleEvent(AFTER_MANAGERREGISTER_EVENT, manager);
    }

    public void removeManager(Manager manager) {
        if (manager instanceof ClusterManager) {
            ClusterManager cmgr = (ClusterManager)manager;
            this.fireLifecycleEvent(BEFORE_MANAGERUNREGISTER_EVENT, manager);
            this.managers.remove(this.getManagerName(cmgr.getName(), manager));
            cmgr.setCluster(null);
            this.fireLifecycleEvent(AFTER_MANAGERUNREGISTER_EVENT, manager);
        }
    }

    @Override
    public String getManagerName(String name, Manager manager) {
        Context context;
        Container host;
        String clusterName = name;
        if (clusterName == null) {
            clusterName = manager.getContext().getName();
        }
        if (this.getContainer() instanceof Engine && (host = (context = manager.getContext()).getParent()) instanceof Host && clusterName != null && !clusterName.startsWith(host.getName() + "#")) {
            clusterName = host.getName() + "#" + clusterName;
        }
        return clusterName;
    }

    @Override
    public Manager getManager(String name) {
        return this.managers.get(name);
    }

    public void backgroundProcess() {
        if (this.clusterDeployer != null) {
            this.clusterDeployer.backgroundProcess();
        }
        if (this.isHeartbeatBackgroundEnabled() && this.channel != null) {
            this.channel.heartbeat();
        }
        this.fireLifecycleEvent("periodic", null);
    }

    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.clusterDeployer != null) {
            StringBuilder name = new StringBuilder("type=Cluster");
            Container container = this.getContainer();
            if (container != null) {
                name.append(container.getMBeanKeyProperties());
            }
            name.append(",component=Deployer");
            this.onameClusterDeployer = this.register(this.clusterDeployer, name.toString());
        }
    }

    protected void startInternal() throws LifecycleException {
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("simpleTcpCluster.start"));
        }
        this.channel.setUtilityExecutor(Container.getService((Container)this.getContainer()).getServer().getUtilityExecutor());
        try {
            this.checkDefaults();
            this.registerClusterValve();
            this.channel.addMembershipListener((MembershipListener)this);
            this.channel.addChannelListener((ChannelListener)this);
            this.channel.setName(this.getClusterName() + "-Channel");
            this.channel.start(this.channelStartOptions);
            if (this.clusterDeployer != null) {
                this.clusterDeployer.start();
            }
            this.registerMember(this.channel.getLocalMember(false));
        }
        catch (Exception x) {
            log.error((Object)sm.getString("simpleTcpCluster.startUnable"), (Throwable)x);
            throw new LifecycleException((Throwable)x);
        }
        this.setState(LifecycleState.STARTING);
    }

    protected void checkDefaults() {
        if (this.clusterListeners.size() == 0 && this.managerTemplate instanceof DeltaManager) {
            this.addClusterListener(new ClusterSessionListener());
        }
        if (this.valves.size() == 0) {
            this.addValve(new JvmRouteBinderValve());
            this.addValve(new ReplicationValve());
        }
        if (this.clusterDeployer != null) {
            this.clusterDeployer.setCluster(this);
        }
        if (this.channel == null) {
            this.channel = new GroupChannel();
        }
        if (this.channel instanceof GroupChannel && !((GroupChannel)this.channel).getInterceptors().hasNext()) {
            this.channel.addInterceptor((ChannelInterceptor)new MessageDispatchInterceptor());
            this.channel.addInterceptor((ChannelInterceptor)new TcpFailureDetector());
        }
        if (this.heartbeatBackgroundEnabled) {
            this.channel.setHeartbeat(false);
        }
    }

    protected void registerClusterValve() {
        if (this.container != null) {
            for (Valve v : this.valves) {
                ClusterValve valve = (ClusterValve)v;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Invoking addValve on " + this.getContainer() + " with class=" + valve.getClass().getName()));
                }
                if (valve == null) continue;
                this.container.getPipeline().addValve((Valve)valve);
                valve.setCluster(this);
            }
        }
    }

    protected void unregisterClusterValve() {
        for (Valve v : this.valves) {
            ClusterValve valve = (ClusterValve)v;
            if (log.isDebugEnabled()) {
                log.debug((Object)("Invoking removeValve on " + this.getContainer() + " with class=" + valve.getClass().getName()));
            }
            if (valve == null) continue;
            this.container.getPipeline().removeValve((Valve)valve);
            valve.setCluster(null);
        }
    }

    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        this.unregisterMember(this.channel.getLocalMember(false));
        if (this.clusterDeployer != null) {
            this.clusterDeployer.stop();
        }
        this.managers.clear();
        try {
            if (this.clusterDeployer != null) {
                this.clusterDeployer.setCluster(null);
            }
            this.channel.stop(this.channelStartOptions);
            this.channel.removeChannelListener((ChannelListener)this);
            this.channel.removeMembershipListener((MembershipListener)this);
            this.unregisterClusterValve();
        }
        catch (Exception x) {
            log.error((Object)sm.getString("simpleTcpCluster.stopUnable"), (Throwable)x);
        }
        this.channel.setUtilityExecutor(null);
    }

    protected void destroyInternal() throws LifecycleException {
        if (this.onameClusterDeployer != null) {
            this.unregister(this.onameClusterDeployer);
            this.onameClusterDeployer = null;
        }
        super.destroyInternal();
    }

    public String toString() {
        return ToStringUtil.toString((Contained)this);
    }

    @Override
    public void send(ClusterMessage msg) {
        this.send(msg, null);
    }

    @Override
    public void send(ClusterMessage msg, Member dest) {
        this.send(msg, dest, this.channelSendOptions);
    }

    @Override
    public void send(ClusterMessage msg, Member dest, int sendOptions) {
        try {
            msg.setAddress(this.getLocalMember());
            if (dest != null) {
                if (!this.getLocalMember().equals(dest)) {
                    this.channel.send(new Member[]{dest}, (Serializable)msg, sendOptions);
                } else {
                    log.error((Object)sm.getString("simpleTcpCluster.unableSend.localMember", new Object[]{msg}));
                }
            } else {
                Member[] destmembers = this.channel.getMembers();
                if (destmembers.length > 0) {
                    this.channel.send(destmembers, (Serializable)msg, sendOptions);
                } else if (log.isDebugEnabled()) {
                    log.debug((Object)("No members in cluster, ignoring message:" + msg));
                }
            }
        }
        catch (Exception x) {
            log.error((Object)sm.getString("simpleTcpCluster.sendFailed"), (Throwable)x);
        }
    }

    public void memberAdded(Member member) {
        try {
            this.hasMembers = this.channel.hasMembers();
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("simpleTcpCluster.member.added", new Object[]{member}));
            }
            this.fireLifecycleEvent(BEFORE_MEMBERREGISTER_EVENT, member);
            this.registerMember(member);
            this.fireLifecycleEvent(AFTER_MEMBERREGISTER_EVENT, member);
        }
        catch (Exception x) {
            log.error((Object)sm.getString("simpleTcpCluster.member.addFailed"), (Throwable)x);
        }
    }

    public void memberDisappeared(Member member) {
        try {
            this.hasMembers = this.channel.hasMembers();
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("simpleTcpCluster.member.disappeared", new Object[]{member}));
            }
            this.fireLifecycleEvent(BEFORE_MEMBERUNREGISTER_EVENT, member);
            this.unregisterMember(member);
            this.fireLifecycleEvent(AFTER_MEMBERUNREGISTER_EVENT, member);
        }
        catch (Exception x) {
            log.error((Object)sm.getString("simpleTcpCluster.member.removeFailed"), (Throwable)x);
        }
    }

    public boolean accept(Serializable msg, Member sender) {
        return msg instanceof ClusterMessage;
    }

    public void messageReceived(Serializable message, Member sender) {
        ClusterMessage fwd = (ClusterMessage)message;
        fwd.setAddress(sender);
        this.messageReceived(fwd);
    }

    public void messageReceived(ClusterMessage message) {
        if (log.isDebugEnabled() && message != null) {
            log.debug((Object)("Assuming clocks are synched: Replication for " + message.getUniqueId() + " took=" + (System.currentTimeMillis() - message.getTimestamp()) + " ms."));
        }
        boolean accepted = false;
        if (message != null) {
            for (ClusterListener listener : this.clusterListeners) {
                if (!listener.accept(message)) continue;
                accepted = true;
                listener.messageReceived(message);
            }
            if (!accepted && this.notifyLifecycleListenerOnFailure) {
                Member dest = message.getAddress();
                this.fireLifecycleEvent(RECEIVE_MESSAGE_FAILURE_EVENT, new SendMessageData(message, dest, null));
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Message " + message.toString() + " from type " + message.getClass().getName() + " transferred but no listener registered"));
                }
            }
        }
    }

    public int getChannelStartOptions() {
        return this.channelStartOptions;
    }

    public void setChannelStartOptions(int channelStartOptions) {
        this.channelStartOptions = channelStartOptions;
    }

    protected String getDomainInternal() {
        Container container = this.getContainer();
        if (container == null) {
            return null;
        }
        return container.getDomain();
    }

    protected String getObjectNameKeyProperties() {
        StringBuilder name = new StringBuilder("type=Cluster");
        Container container = this.getContainer();
        if (container != null) {
            name.append(container.getMBeanKeyProperties());
        }
        return name.toString();
    }

    private void registerMember(Member member) {
        StringBuilder name = new StringBuilder("type=Cluster");
        Container container = this.getContainer();
        if (container != null) {
            name.append(container.getMBeanKeyProperties());
        }
        name.append(",component=Member,name=");
        name.append(ObjectName.quote(member.getName()));
        ObjectName oname = this.register(member, name.toString());
        this.memberOnameMap.put(member, oname);
    }

    private void unregisterMember(Member member) {
        if (member == null) {
            return;
        }
        ObjectName oname = this.memberOnameMap.remove(member);
        if (oname != null) {
            this.unregister(oname);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.tipis;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Heartbeat;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.group.Response;
import org.apache.catalina.tribes.group.RpcCallback;
import org.apache.catalina.tribes.group.RpcChannel;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.tipis.ReplicatedMapEntry;
import org.apache.catalina.tribes.util.Arrays;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class AbstractReplicatedMap<K, V>
implements Map<K, V>,
Serializable,
RpcCallback,
ChannelListener,
MembershipListener,
Heartbeat {
    private static final long serialVersionUID = 1L;
    protected static final StringManager sm = StringManager.getManager(AbstractReplicatedMap.class);
    private final Log log = LogFactory.getLog(AbstractReplicatedMap.class);
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected final ConcurrentMap<K, MapEntry<K, V>> innerMap;
    protected transient long rpcTimeout = 5000L;
    protected transient Channel channel;
    protected transient RpcChannel rpcChannel;
    protected transient byte[] mapContextName;
    protected transient boolean stateTransferred = false;
    protected final transient Object stateMutex = new Object();
    protected final transient HashMap<Member, Long> mapMembers = new HashMap();
    protected transient int channelSendOptions = 2;
    protected transient MapOwner mapOwner;
    protected transient ClassLoader[] externalLoaders;
    protected transient int currentNode = 0;
    protected transient long accessTimeout = 5000L;
    protected transient String mapname = "";
    private volatile transient State state = State.NEW;

    protected abstract int getStateMessageType();

    protected abstract int getReplicateMessageType();

    public AbstractReplicatedMap(MapOwner owner, Channel channel, long timeout, String mapContextName, int initialCapacity, float loadFactor, int channelSendOptions, ClassLoader[] cls, boolean terminate) {
        this.innerMap = new ConcurrentHashMap<K, MapEntry<K, V>>(initialCapacity, loadFactor, 15);
        this.init(owner, channel, mapContextName, timeout, channelSendOptions, cls, terminate);
    }

    protected Member[] wrap(Member m) {
        if (m == null) {
            return new Member[0];
        }
        return new Member[]{m};
    }

    protected void init(MapOwner owner, Channel channel, String mapContextName, long timeout, int channelSendOptions, ClassLoader[] cls, boolean terminate) {
        long start;
        block5: {
            start = System.currentTimeMillis();
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("abstractReplicatedMap.init.start", mapContextName));
            }
            this.mapOwner = owner;
            this.externalLoaders = cls;
            this.channelSendOptions = channelSendOptions;
            this.channel = channel;
            this.rpcTimeout = timeout;
            this.mapname = mapContextName;
            this.mapContextName = mapContextName.getBytes(StandardCharsets.ISO_8859_1);
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)("Created Lazy Map with name:" + mapContextName + ", bytes:" + Arrays.toString(this.mapContextName)));
            }
            this.rpcChannel = new RpcChannel(this.mapContextName, channel, this);
            this.channel.addChannelListener(this);
            this.channel.addMembershipListener(this);
            try {
                this.broadcast(8, true);
                this.transferState();
                this.broadcast(6, true);
            }
            catch (ChannelException x) {
                this.log.warn((Object)sm.getString("abstractReplicatedMap.unableSend.startMessage"));
                if (!terminate) break block5;
                this.breakdown();
                throw new RuntimeException(sm.getString("abstractReplicatedMap.unableStart"), x);
            }
        }
        this.state = State.INITIALIZED;
        long complete = System.currentTimeMillis() - start;
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)sm.getString("abstractReplicatedMap.init.completed", mapContextName, Long.toString(complete)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void ping(long timeout) throws ChannelException {
        MapMessage msg = new MapMessage(this.mapContextName, 13, false, null, null, null, this.channel.getLocalMember(false), null);
        if (this.channel.getMembers().length > 0) {
            try {
                Response[] resp;
                for (Response response : resp = this.rpcChannel.send(this.channel.getMembers(), msg, 3, this.channelSendOptions, (int)this.accessTimeout)) {
                    MapMessage mapMsg = (MapMessage)response.getMessage();
                    try {
                        mapMsg.deserialize(this.getExternalLoaders());
                        Member member = response.getSource();
                        State state = (State)((Object)mapMsg.getValue());
                        if (state.isAvailable()) {
                            this.memberAlive(member);
                            continue;
                        }
                        if (state == State.STATETRANSFERRED) {
                            HashMap<Member, Long> hashMap = this.mapMembers;
                            synchronized (hashMap) {
                                if (this.log.isInfoEnabled()) {
                                    this.log.info((Object)sm.getString("abstractReplicatedMap.ping.stateTransferredMember", member));
                                }
                                if (this.mapMembers.containsKey(member)) {
                                    this.mapMembers.put(member, System.currentTimeMillis());
                                }
                                continue;
                            }
                        }
                        if (!this.log.isInfoEnabled()) continue;
                        this.log.info((Object)sm.getString("abstractReplicatedMap.mapMember.unavailable", member));
                    }
                    catch (IOException | ClassNotFoundException e) {
                        this.log.error((Object)sm.getString("abstractReplicatedMap.unable.deserialize.MapMessage"), (Throwable)e);
                    }
                }
            }
            catch (ChannelException ce) {
                ChannelException.FaultyMember[] faultyMembers;
                for (ChannelException.FaultyMember faultyMember : faultyMembers = ce.getFaultyMembers()) {
                    this.memberDisappeared(faultyMember.getMember());
                }
                throw ce;
            }
        }
        HashMap<Member, Long> hashMap = this.mapMembers;
        synchronized (hashMap) {
            Member[] members = this.mapMembers.keySet().toArray(new Member[0]);
            long now = System.currentTimeMillis();
            for (Member member : members) {
                long access = this.mapMembers.get(member);
                if (now - access <= timeout) continue;
                this.log.warn((Object)sm.getString("abstractReplicatedMap.ping.timeout", member, this.mapname));
                this.memberDisappeared(member);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void memberAlive(Member member) {
        this.mapMemberAdded(member);
        HashMap<Member, Long> hashMap = this.mapMembers;
        synchronized (hashMap) {
            this.mapMembers.put(member, System.currentTimeMillis());
        }
    }

    protected void broadcast(int msgtype, boolean rpc) throws ChannelException {
        Member[] members = this.channel.getMembers();
        if (members.length == 0) {
            return;
        }
        MapMessage msg = new MapMessage(this.mapContextName, msgtype, false, null, null, null, this.channel.getLocalMember(false), null);
        if (rpc) {
            Response[] resp = this.rpcChannel.send(members, msg, 1, this.channelSendOptions, this.rpcTimeout);
            if (resp.length > 0) {
                for (Response response : resp) {
                    this.mapMemberAdded(response.getSource());
                    this.messageReceived(response.getMessage(), response.getSource());
                }
            } else {
                this.log.warn((Object)sm.getString("abstractReplicatedMap.broadcast.noReplies"));
            }
        } else {
            this.channel.send(this.channel.getMembers(), msg, this.channelSendOptions);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void breakdown() {
        this.state = State.DESTROYED;
        if (this.rpcChannel != null) {
            this.rpcChannel.breakdown();
        }
        if (this.channel != null) {
            try {
                this.broadcast(7, false);
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.channel.removeChannelListener(this);
            this.channel.removeMembershipListener(this);
        }
        this.rpcChannel = null;
        this.channel = null;
        HashMap<Member, Long> hashMap = this.mapMembers;
        synchronized (hashMap) {
            this.mapMembers.clear();
        }
        this.innerMap.clear();
        this.stateTransferred = false;
        this.externalLoaders = null;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.mapContextName);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractReplicatedMap)) {
            return false;
        }
        if (!o.getClass().equals(this.getClass())) {
            return false;
        }
        AbstractReplicatedMap other = (AbstractReplicatedMap)o;
        return Arrays.equals(this.mapContextName, other.mapContextName);
    }

    public Member[] getMapMembers(HashMap<Member, Long> members) {
        return members.keySet().toArray(new Member[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Member[] getMapMembers() {
        HashMap<Member, Long> hashMap = this.mapMembers;
        synchronized (hashMap) {
            return this.getMapMembers(this.mapMembers);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Member[] getMapMembersExcl(Member[] exclude) {
        if (exclude == null) {
            return null;
        }
        HashMap<Member, Long> hashMap = this.mapMembers;
        synchronized (hashMap) {
            HashMap list = (HashMap)this.mapMembers.clone();
            for (Member member : exclude) {
                list.remove(member);
            }
            return this.getMapMembers(list);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void replicate(Object key, boolean complete) {
        MapEntry entry;
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Replicate invoked on key:" + key));
        }
        if ((entry = (MapEntry)this.innerMap.get(key)) == null) {
            return;
        }
        if (!entry.isSerializable()) {
            return;
        }
        if (entry.isPrimary() && entry.getBackupNodes() != null && entry.getBackupNodes().length > 0) {
            boolean repl;
            ReplicatedMapEntry rentry = null;
            if (entry.getValue() instanceof ReplicatedMapEntry) {
                rentry = (ReplicatedMapEntry)entry.getValue();
            }
            boolean isDirty = rentry != null && rentry.isDirty();
            boolean isAccess = rentry != null && rentry.isAccessReplicate();
            boolean bl = repl = complete || isDirty || isAccess;
            if (!repl) {
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("Not replicating:" + key + ", no change made"));
                }
                return;
            }
            MapMessage msg = null;
            if (rentry != null && rentry.isDiffable() && (isDirty || complete)) {
                rentry.lock();
                try {
                    msg = new MapMessage(this.mapContextName, this.getReplicateMessageType(), true, (Serializable)entry.getKey(), null, rentry.getDiff(), entry.getPrimary(), entry.getBackupNodes());
                    rentry.resetDiff();
                }
                catch (IOException x) {
                    this.log.error((Object)sm.getString("abstractReplicatedMap.unable.diffObject"), (Throwable)x);
                }
                finally {
                    rentry.unlock();
                }
            }
            if (msg == null && complete) {
                msg = new MapMessage(this.mapContextName, this.getReplicateMessageType(), false, (Serializable)entry.getKey(), (Serializable)entry.getValue(), null, entry.getPrimary(), entry.getBackupNodes());
            }
            if (msg == null) {
                msg = new MapMessage(this.mapContextName, 11, false, (Serializable)entry.getKey(), null, null, entry.getPrimary(), entry.getBackupNodes());
            }
            try {
                if (this.channel != null && entry.getBackupNodes() != null && entry.getBackupNodes().length > 0) {
                    if (rentry != null) {
                        rentry.setLastTimeReplicated(System.currentTimeMillis());
                    }
                    this.channel.send(entry.getBackupNodes(), msg, this.channelSendOptions);
                }
            }
            catch (ChannelException x) {
                this.log.error((Object)sm.getString("abstractReplicatedMap.unable.replicate"), (Throwable)x);
            }
        }
    }

    public void replicate(boolean complete) {
        for (Map.Entry e : this.innerMap.entrySet()) {
            this.replicate(e.getKey(), complete);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void transferState() {
        block7: {
            try {
                Member backup;
                Member[] members = this.getMapMembers();
                Member member = backup = members.length > 0 ? members[0] : null;
                if (backup == null) break block7;
                MapMessage msg = new MapMessage(this.mapContextName, this.getStateMessageType(), false, null, null, null, null, null);
                Response[] resp = this.rpcChannel.send(new Member[]{backup}, msg, 1, this.channelSendOptions, this.rpcTimeout);
                if (resp.length > 0) {
                    Object object = this.stateMutex;
                    synchronized (object) {
                        msg = (MapMessage)resp[0].getMessage();
                        msg.deserialize(this.getExternalLoaders());
                        ArrayList list = (ArrayList)msg.getValue();
                        for (Object o : list) {
                            this.messageReceived((Serializable)o, resp[0].getSource());
                        }
                    }
                    this.stateTransferred = true;
                    break block7;
                }
                this.log.warn((Object)sm.getString("abstractReplicatedMap.transferState.noReplies"));
            }
            catch (IOException | ClassNotFoundException | ChannelException x) {
                this.log.error((Object)sm.getString("abstractReplicatedMap.unable.transferState"), (Throwable)x);
            }
        }
        this.state = State.STATETRANSFERRED;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Serializable replyRequest(Serializable msg, Member sender) {
        if (!(msg instanceof MapMessage)) {
            return null;
        }
        MapMessage mapmsg = (MapMessage)msg;
        if (mapmsg.getMsgType() == 8) {
            mapmsg.setPrimary(this.channel.getLocalMember(false));
            return mapmsg;
        }
        if (mapmsg.getMsgType() == 6) {
            mapmsg.setPrimary(this.channel.getLocalMember(false));
            this.mapMemberAdded(sender);
            return mapmsg;
        }
        if (mapmsg.getMsgType() == 2) {
            MapEntry entry = (MapEntry)this.innerMap.get(mapmsg.getKey());
            if (entry == null || !entry.isSerializable()) {
                return null;
            }
            mapmsg.setValue((Serializable)entry.getValue());
            return mapmsg;
        }
        if (mapmsg.getMsgType() == 5 || mapmsg.getMsgType() == 10) {
            Object object = this.stateMutex;
            synchronized (object) {
                ArrayList<MapMessage> list = new ArrayList<MapMessage>();
                for (Map.Entry e : this.innerMap.entrySet()) {
                    MapEntry entry = (MapEntry)this.innerMap.get(e.getKey());
                    if (entry == null || !entry.isSerializable()) continue;
                    boolean copy = mapmsg.getMsgType() == 10;
                    MapMessage me = new MapMessage(this.mapContextName, copy ? 9 : 3, false, (Serializable)entry.getKey(), copy ? (Serializable)entry.getValue() : null, null, entry.getPrimary(), entry.getBackupNodes());
                    list.add(me);
                }
                mapmsg.setValue(list);
                return mapmsg;
            }
        }
        if (mapmsg.getMsgType() == 13) {
            mapmsg.setValue((Serializable)((Object)this.state));
            mapmsg.setPrimary(this.channel.getLocalMember(false));
            return mapmsg;
        }
        return null;
    }

    @Override
    public void leftOver(Serializable msg, Member sender) {
        if (!(msg instanceof MapMessage)) {
            return;
        }
        MapMessage mapmsg = (MapMessage)msg;
        try {
            mapmsg.deserialize(this.getExternalLoaders());
            if (mapmsg.getMsgType() == 6) {
                this.mapMemberAdded(mapmsg.getPrimary());
            } else if (mapmsg.getMsgType() == 8) {
                this.memberAlive(mapmsg.getPrimary());
            } else if (mapmsg.getMsgType() == 13) {
                State state;
                Member member = mapmsg.getPrimary();
                if (this.log.isInfoEnabled()) {
                    this.log.info((Object)sm.getString("abstractReplicatedMap.leftOver.pingMsg", member));
                }
                if ((state = (State)((Object)mapmsg.getValue())).isAvailable()) {
                    this.memberAlive(member);
                }
            } else if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("abstractReplicatedMap.leftOver.ignored", mapmsg.getTypeDesc()));
            }
        }
        catch (IOException | ClassNotFoundException x) {
            this.log.error((Object)sm.getString("abstractReplicatedMap.unable.deserialize.MapMessage"), (Throwable)x);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void messageReceived(Serializable msg, Member sender) {
        MapEntry entry;
        if (!(msg instanceof MapMessage)) {
            return;
        }
        MapMessage mapmsg = (MapMessage)msg;
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Map[" + this.mapname + "] received message:" + mapmsg));
        }
        try {
            mapmsg.deserialize(this.getExternalLoaders());
        }
        catch (IOException | ClassNotFoundException x) {
            this.log.error((Object)sm.getString("abstractReplicatedMap.unable.deserialize.MapMessage"), (Throwable)x);
            return;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Map message received from:" + sender.getName() + " msg:" + mapmsg));
        }
        if (mapmsg.getMsgType() == 6) {
            this.mapMemberAdded(mapmsg.getPrimary());
        }
        if (mapmsg.getMsgType() == 7) {
            this.memberDisappeared(mapmsg.getPrimary());
        }
        if (mapmsg.getMsgType() == 3) {
            MapEntry old;
            entry = (MapEntry)this.innerMap.get(mapmsg.getKey());
            if (entry == null && (old = this.innerMap.putIfAbsent((entry = new MapEntry(mapmsg.getKey(), mapmsg.getValue())).getKey(), entry)) != null) {
                entry = old;
            }
            entry.setProxy(true);
            entry.setBackup(false);
            entry.setCopy(false);
            entry.setBackupNodes(mapmsg.getBackupNodes());
            entry.setPrimary(mapmsg.getPrimary());
        }
        if (mapmsg.getMsgType() == 4) {
            this.innerMap.remove(mapmsg.getKey());
        }
        if (mapmsg.getMsgType() == 1 || mapmsg.getMsgType() == 9) {
            entry = (MapEntry)this.innerMap.get(mapmsg.getKey());
            if (entry == null) {
                entry = new MapEntry(mapmsg.getKey(), mapmsg.getValue());
                entry.setBackup(mapmsg.getMsgType() == 1);
                entry.setProxy(false);
                entry.setCopy(mapmsg.getMsgType() == 9);
                entry.setBackupNodes(mapmsg.getBackupNodes());
                entry.setPrimary(mapmsg.getPrimary());
                if (mapmsg.getValue() instanceof ReplicatedMapEntry) {
                    ((ReplicatedMapEntry)mapmsg.getValue()).setOwner(this.getMapOwner());
                }
            } else {
                entry.setBackup(mapmsg.getMsgType() == 1);
                entry.setProxy(false);
                entry.setCopy(mapmsg.getMsgType() == 9);
                entry.setBackupNodes(mapmsg.getBackupNodes());
                entry.setPrimary(mapmsg.getPrimary());
                if (entry.getValue() instanceof ReplicatedMapEntry) {
                    ReplicatedMapEntry diff = (ReplicatedMapEntry)entry.getValue();
                    if (mapmsg.isDiff()) {
                        diff.lock();
                        try {
                            diff.applyDiff(mapmsg.getDiffValue(), 0, mapmsg.getDiffValue().length);
                        }
                        catch (Exception x) {
                            this.log.error((Object)sm.getString("abstractReplicatedMap.unableApply.diff", entry.getKey()), (Throwable)x);
                        }
                        finally {
                            diff.unlock();
                        }
                    } else if (mapmsg.getValue() != null) {
                        if (mapmsg.getValue() instanceof ReplicatedMapEntry) {
                            ReplicatedMapEntry re = (ReplicatedMapEntry)mapmsg.getValue();
                            re.setOwner(this.getMapOwner());
                            entry.setValue((Serializable)re);
                        } else {
                            entry.setValue((Serializable)mapmsg.getValue());
                        }
                    } else {
                        ((ReplicatedMapEntry)entry.getValue()).setOwner(this.getMapOwner());
                    }
                } else if (mapmsg.getValue() instanceof ReplicatedMapEntry) {
                    ReplicatedMapEntry re = (ReplicatedMapEntry)mapmsg.getValue();
                    re.setOwner(this.getMapOwner());
                    entry.setValue((Serializable)re);
                } else if (mapmsg.getValue() != null) {
                    entry.setValue((Serializable)mapmsg.getValue());
                }
            }
            this.innerMap.put(entry.getKey(), entry);
        }
        if (mapmsg.getMsgType() == 11 && (entry = (MapEntry)this.innerMap.get(mapmsg.getKey())) != null) {
            entry.setBackupNodes(mapmsg.getBackupNodes());
            entry.setPrimary(mapmsg.getPrimary());
            if (entry.getValue() instanceof ReplicatedMapEntry) {
                ((ReplicatedMapEntry)entry.getValue()).accessEntry();
            }
        }
        if (mapmsg.getMsgType() == 12 && (entry = (MapEntry)this.innerMap.get(mapmsg.getKey())) != null) {
            entry.setBackupNodes(mapmsg.getBackupNodes());
            entry.setPrimary(mapmsg.getPrimary());
            if (entry.getValue() instanceof ReplicatedMapEntry) {
                ((ReplicatedMapEntry)entry.getValue()).accessEntry();
            }
        }
    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        boolean result = false;
        if (msg instanceof MapMessage) {
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)("Map[" + this.mapname + "] accepting...." + msg));
            }
            result = Arrays.equals(this.mapContextName, ((MapMessage)msg).getMapId());
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)("Msg[" + this.mapname + "] accepted[" + result + "]...." + msg));
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void mapMemberAdded(Member member) {
        if (member.equals(this.getChannel().getLocalMember(false))) {
            return;
        }
        boolean memberAdded = false;
        Member mapMember = this.getChannel().getMember(member);
        if (mapMember == null) {
            this.log.warn((Object)sm.getString("abstractReplicatedMap.mapMemberAdded.nullMember", member));
            return;
        }
        Object object = this.mapMembers;
        synchronized (object) {
            if (!this.mapMembers.containsKey(mapMember)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info((Object)sm.getString("abstractReplicatedMap.mapMemberAdded.added", mapMember));
                }
                this.mapMembers.put(mapMember, System.currentTimeMillis());
                memberAdded = true;
            }
        }
        if (memberAdded) {
            object = this.stateMutex;
            synchronized (object) {
                for (Map.Entry e : this.innerMap.entrySet()) {
                    MapEntry entry = (MapEntry)this.innerMap.get(e.getKey());
                    if (entry == null || !entry.isPrimary() || entry.getBackupNodes() != null && entry.getBackupNodes().length != 0) continue;
                    try {
                        Member[] backup = this.publishEntryInfo(entry.getKey(), entry.getValue());
                        entry.setBackupNodes(backup);
                        entry.setPrimary(this.channel.getLocalMember(false));
                    }
                    catch (ChannelException x) {
                        this.log.error((Object)sm.getString("abstractReplicatedMap.unableSelect.backup"), (Throwable)x);
                    }
                }
            }
        }
    }

    public boolean inSet(Member m, Member[] set) {
        if (set == null) {
            return false;
        }
        boolean result = false;
        for (Member member : set) {
            if (!m.equals(member)) continue;
            result = true;
            break;
        }
        return result;
    }

    public Member[] excludeFromSet(Member[] mbrs, Member[] set) {
        ArrayList<Member> result = new ArrayList<Member>();
        for (Member member : set) {
            boolean include = true;
            for (Member mbr : mbrs) {
                if (!mbr.equals(member)) continue;
                include = false;
                break;
            }
            if (!include) continue;
            result.add(member);
        }
        return result.toArray(new Member[0]);
    }

    @Override
    public void memberAdded(Member member) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void memberDisappeared(Member member) {
        boolean removed = false;
        HashMap<Member, Long> hashMap = this.mapMembers;
        synchronized (hashMap) {
            boolean bl = removed = this.mapMembers.remove(member) != null;
            if (!removed) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Member[" + member + "] disappeared, but was not present in the map."));
                }
                return;
            }
        }
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)sm.getString("abstractReplicatedMap.member.disappeared", member));
        }
        long start = System.currentTimeMillis();
        Iterator i = this.innerMap.entrySet().iterator();
        while (i.hasNext()) {
            Member[] backup;
            Map.Entry e = i.next();
            MapEntry entry = (MapEntry)this.innerMap.get(e.getKey());
            if (entry == null) continue;
            if (entry.isPrimary() && this.inSet(member, entry.getBackupNodes())) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"[1] Primary choosing a new backup");
                }
                try {
                    backup = this.publishEntryInfo(entry.getKey(), entry.getValue());
                    entry.setBackupNodes(backup);
                    entry.setPrimary(this.channel.getLocalMember(false));
                }
                catch (ChannelException x) {
                    this.log.error((Object)sm.getString("abstractReplicatedMap.unable.relocate", entry.getKey()), (Throwable)x);
                }
            } else if (member.equals(entry.getPrimary())) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"[2] Primary disappeared");
                }
                entry.setPrimary(null);
            }
            if (entry.isProxy() && entry.getPrimary() == null && entry.getBackupNodes() != null && entry.getBackupNodes().length == 1 && entry.getBackupNodes()[0].equals(member)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"[3] Removing orphaned proxy");
                }
                i.remove();
                continue;
            }
            if (entry.getPrimary() != null || !entry.isBackup() || entry.getBackupNodes() == null || entry.getBackupNodes().length != 1 || !entry.getBackupNodes()[0].equals(this.channel.getLocalMember(false))) continue;
            try {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"[4] Backup becoming primary");
                }
                entry.setPrimary(this.channel.getLocalMember(false));
                entry.setBackup(false);
                entry.setProxy(false);
                entry.setCopy(false);
                backup = this.publishEntryInfo(entry.getKey(), entry.getValue());
                entry.setBackupNodes(backup);
                if (this.mapOwner == null) continue;
                this.mapOwner.objectMadePrimary(entry.getKey(), entry.getValue());
            }
            catch (ChannelException x) {
                this.log.error((Object)sm.getString("abstractReplicatedMap.unable.relocate", entry.getKey()), (Throwable)x);
            }
        }
        long complete = System.currentTimeMillis() - start;
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)sm.getString("abstractReplicatedMap.relocate.complete", Long.toString(complete)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNextBackupIndex() {
        HashMap<Member, Long> hashMap = this.mapMembers;
        synchronized (hashMap) {
            int node;
            int size = this.mapMembers.size();
            if (this.mapMembers.size() == 0) {
                return -1;
            }
            if ((node = this.currentNode++) >= size) {
                node = 0;
                this.currentNode = 1;
            }
            return node;
        }
    }

    public Member getNextBackupNode() {
        Member[] members = this.getMapMembers();
        int node = this.getNextBackupIndex();
        if (members.length == 0 || node == -1) {
            return null;
        }
        if (node >= members.length) {
            node = 0;
        }
        return members[node];
    }

    protected abstract Member[] publishEntryInfo(Object var1, Object var2) throws ChannelException;

    @Override
    public void heartbeat() {
        try {
            if (this.state.isAvailable()) {
                this.ping(this.accessTimeout);
            }
        }
        catch (Exception x) {
            this.log.error((Object)sm.getString("abstractReplicatedMap.heartbeat.failed"), (Throwable)x);
        }
    }

    @Override
    public V remove(Object key) {
        return this.remove(key, true);
    }

    public V remove(Object key, boolean notify) {
        MapEntry entry = (MapEntry)this.innerMap.remove(key);
        try {
            if (this.getMapMembers().length > 0 && notify) {
                MapMessage msg = new MapMessage(this.getMapContextName(), 4, false, (Serializable)key, null, null, null, null);
                this.getChannel().send(this.getMapMembers(), msg, this.getChannelSendOptions());
            }
        }
        catch (ChannelException x) {
            this.log.error((Object)sm.getString("abstractReplicatedMap.unable.remove"), (Throwable)x);
        }
        return entry != null ? (V)entry.getValue() : null;
    }

    public MapEntry<K, V> getInternal(Object key) {
        return (MapEntry)this.innerMap.get(key);
    }

    @Override
    public V get(Object key) {
        MapEntry entry = (MapEntry)this.innerMap.get(key);
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Requesting id:" + key + " entry:" + entry));
        }
        if (entry == null) {
            return null;
        }
        if (!entry.isPrimary()) {
            try {
                Member[] backup = null;
                MapMessage msg = null;
                if (entry.isBackup()) {
                    backup = this.publishEntryInfo(key, entry.getValue());
                } else if (entry.isProxy()) {
                    msg = new MapMessage(this.getMapContextName(), 2, false, (Serializable)key, null, null, null, null);
                    Response[] resp = this.getRpcChannel().send(entry.getBackupNodes(), msg, 1, this.getChannelSendOptions(), this.getRpcTimeout());
                    if (resp == null || resp.length == 0 || resp[0].getMessage() == null) {
                        this.log.warn((Object)sm.getString("abstractReplicatedMap.unable.retrieve", key));
                        return null;
                    }
                    msg = (MapMessage)resp[0].getMessage();
                    msg.deserialize(this.getExternalLoaders());
                    backup = entry.getBackupNodes();
                    if (msg.getValue() != null) {
                        entry.setValue(msg.getValue());
                    }
                    msg = new MapMessage(this.getMapContextName(), 12, false, (Serializable)entry.getKey(), null, null, this.channel.getLocalMember(false), backup);
                    if (backup != null && backup.length > 0) {
                        this.getChannel().send(backup, msg, this.getChannelSendOptions());
                    }
                    msg = new MapMessage(this.getMapContextName(), 3, false, (Serializable)key, null, null, this.channel.getLocalMember(false), backup);
                    Member[] dest = this.getMapMembersExcl(backup);
                    if (dest != null && dest.length > 0) {
                        this.getChannel().send(dest, msg, this.getChannelSendOptions());
                    }
                    if (entry.getValue() instanceof ReplicatedMapEntry) {
                        ReplicatedMapEntry val = (ReplicatedMapEntry)entry.getValue();
                        val.setOwner(this.getMapOwner());
                    }
                } else if (entry.isCopy() && (backup = this.getMapMembers()).length > 0) {
                    msg = new MapMessage(this.getMapContextName(), 12, false, (Serializable)key, null, null, this.channel.getLocalMember(false), backup);
                    this.getChannel().send(backup, msg, this.getChannelSendOptions());
                }
                entry.setPrimary(this.channel.getLocalMember(false));
                entry.setBackupNodes(backup);
                entry.setBackup(false);
                entry.setProxy(false);
                entry.setCopy(false);
                if (this.getMapOwner() != null) {
                    this.getMapOwner().objectMadePrimary(key, entry.getValue());
                }
            }
            catch (IOException | ClassNotFoundException | RuntimeException | ChannelException x) {
                this.log.error((Object)sm.getString("abstractReplicatedMap.unable.get"), (Throwable)x);
                return null;
            }
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Requesting id:" + key + " result:" + entry.getValue()));
        }
        return entry.getValue();
    }

    protected void printMap(String header) {
        block4: {
            try {
                System.out.println("\nDEBUG MAP:" + header);
                System.out.println("Map[" + new String(this.mapContextName, StandardCharsets.ISO_8859_1) + ", Map Size:" + this.innerMap.size());
                Member[] mbrs = this.getMapMembers();
                for (int i = 0; i < mbrs.length; ++i) {
                    System.out.println("Mbr[" + (i + 1) + "=" + mbrs[i].getName());
                }
                Iterator i = this.innerMap.entrySet().iterator();
                int cnt = 0;
                while (i.hasNext()) {
                    Map.Entry e = i.next();
                    System.out.println(++cnt + ". " + this.innerMap.get(e.getKey()));
                }
                System.out.println("EndMap]\n\n");
            }
            catch (Exception ignore) {
                if (!this.log.isTraceEnabled()) break block4;
                this.log.trace((Object)"Error printing map", (Throwable)ignore);
            }
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return this.innerMap.containsKey(key);
    }

    @Override
    public V put(K key, V value) {
        return this.put(key, value, true);
    }

    public V put(K key, V value, boolean notify) {
        MapEntry<K, V> entry = new MapEntry<K, V>(key, value);
        entry.setBackup(false);
        entry.setProxy(false);
        entry.setCopy(false);
        entry.setPrimary(this.channel.getLocalMember(false));
        V old = null;
        if (this.containsKey(key)) {
            old = this.remove(key);
        }
        try {
            if (notify) {
                Member[] backup = this.publishEntryInfo(key, value);
                entry.setBackupNodes(backup);
            }
        }
        catch (ChannelException x) {
            this.log.error((Object)sm.getString("abstractReplicatedMap.unable.put"), (Throwable)x);
        }
        this.innerMap.put(key, entry);
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Iterator<Map.Entry<K, V>> iterator = m.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> value;
            Map.Entry<K, V> entry = value = iterator.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.clear(true);
    }

    public void clear(boolean notify) {
        if (notify) {
            for (K k : this.keySet()) {
                this.remove(k);
            }
        } else {
            this.innerMap.clear();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        Objects.requireNonNull(value);
        for (Map.Entry e : this.innerMap.entrySet()) {
            MapEntry entry = (MapEntry)this.innerMap.get(e.getKey());
            if (entry == null || !entry.isActive() || !value.equals(entry.getValue())) continue;
            return true;
        }
        return false;
    }

    public Set<Map.Entry<K, MapEntry<K, V>>> entrySetFull() {
        return this.innerMap.entrySet();
    }

    public Set<K> keySetFull() {
        return this.innerMap.keySet();
    }

    public int sizeFull() {
        return this.innerMap.size();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        LinkedHashSet<MapEntry> set = new LinkedHashSet<MapEntry>(this.innerMap.size());
        for (Map.Entry e : this.innerMap.entrySet()) {
            Object key = e.getKey();
            MapEntry entry = (MapEntry)this.innerMap.get(key);
            if (entry == null || !entry.isActive()) continue;
            set.add(entry);
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Set<K> keySet() {
        LinkedHashSet set = new LinkedHashSet(this.innerMap.size());
        for (Map.Entry e : this.innerMap.entrySet()) {
            Object key = e.getKey();
            MapEntry entry = (MapEntry)this.innerMap.get(key);
            if (entry == null || !entry.isActive()) continue;
            set.add(key);
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public int size() {
        int counter = 0;
        Iterator it = this.innerMap.entrySet().iterator();
        while (it != null && it.hasNext()) {
            MapEntry entry;
            Map.Entry e = it.next();
            if (e == null || (entry = (MapEntry)this.innerMap.get(e.getKey())) == null || !entry.isActive() || entry.getValue() == null) continue;
            ++counter;
        }
        return counter;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public Collection<V> values() {
        ArrayList values = new ArrayList();
        for (Map.Entry e : this.innerMap.entrySet()) {
            MapEntry entry = (MapEntry)this.innerMap.get(e.getKey());
            if (entry == null || !entry.isActive() || entry.getValue() == null) continue;
            values.add(entry.getValue());
        }
        return Collections.unmodifiableCollection(values);
    }

    public Channel getChannel() {
        return this.channel;
    }

    public byte[] getMapContextName() {
        return this.mapContextName;
    }

    public RpcChannel getRpcChannel() {
        return this.rpcChannel;
    }

    public long getRpcTimeout() {
        return this.rpcTimeout;
    }

    public Object getStateMutex() {
        return this.stateMutex;
    }

    public boolean isStateTransferred() {
        return this.stateTransferred;
    }

    public MapOwner getMapOwner() {
        return this.mapOwner;
    }

    public ClassLoader[] getExternalLoaders() {
        return this.externalLoaders;
    }

    public int getChannelSendOptions() {
        return this.channelSendOptions;
    }

    public long getAccessTimeout() {
        return this.accessTimeout;
    }

    public void setMapOwner(MapOwner mapOwner) {
        this.mapOwner = mapOwner;
    }

    public void setExternalLoaders(ClassLoader[] externalLoaders) {
        this.externalLoaders = externalLoaders;
    }

    public void setChannelSendOptions(int channelSendOptions) {
        this.channelSendOptions = channelSendOptions;
    }

    public void setAccessTimeout(long accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

    private static enum State {
        NEW(false),
        STATETRANSFERRED(false),
        INITIALIZED(true),
        DESTROYED(false);

        private final boolean available;

        private State(boolean available) {
            this.available = available;
        }

        public boolean isAvailable() {
            return this.available;
        }
    }

    public static interface MapOwner {
        public void objectMadePrimary(Object var1, Object var2);
    }

    public static class MapMessage
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = 1L;
        public static final int MSG_BACKUP = 1;
        public static final int MSG_RETRIEVE_BACKUP = 2;
        public static final int MSG_PROXY = 3;
        public static final int MSG_REMOVE = 4;
        public static final int MSG_STATE = 5;
        public static final int MSG_START = 6;
        public static final int MSG_STOP = 7;
        public static final int MSG_INIT = 8;
        public static final int MSG_COPY = 9;
        public static final int MSG_STATE_COPY = 10;
        public static final int MSG_ACCESS = 11;
        public static final int MSG_NOTIFY_MAPMEMBER = 12;
        public static final int MSG_PING = 13;
        private final byte[] mapId;
        private final int msgtype;
        private final boolean diff;
        private transient Serializable key;
        private transient Serializable value;
        private byte[] valuedata;
        private byte[] keydata;
        private final byte[] diffvalue;
        private final Member[] nodes;
        private Member primary;

        public String toString() {
            StringBuilder buf = new StringBuilder("MapMessage[context=");
            buf.append(new String(this.mapId));
            buf.append("; type=");
            buf.append(this.getTypeDesc());
            buf.append("; key=");
            buf.append(this.key);
            buf.append("; value=");
            buf.append(this.value);
            buf.append(']');
            return buf.toString();
        }

        public String getTypeDesc() {
            switch (this.msgtype) {
                case 1: {
                    return "MSG_BACKUP";
                }
                case 2: {
                    return "MSG_RETRIEVE_BACKUP";
                }
                case 3: {
                    return "MSG_PROXY";
                }
                case 4: {
                    return "MSG_REMOVE";
                }
                case 5: {
                    return "MSG_STATE";
                }
                case 6: {
                    return "MSG_START";
                }
                case 7: {
                    return "MSG_STOP";
                }
                case 8: {
                    return "MSG_INIT";
                }
                case 10: {
                    return "MSG_STATE_COPY";
                }
                case 9: {
                    return "MSG_COPY";
                }
                case 11: {
                    return "MSG_ACCESS";
                }
                case 12: {
                    return "MSG_NOTIFY_MAPMEMBER";
                }
                case 13: {
                    return "MSG_PING";
                }
            }
            return "UNKNOWN";
        }

        public MapMessage(byte[] mapId, int msgtype, boolean diff, Serializable key, Serializable value, byte[] diffvalue, Member primary, Member[] nodes) {
            this.mapId = mapId;
            this.msgtype = msgtype;
            this.diff = diff;
            this.key = key;
            this.value = value;
            this.diffvalue = diffvalue;
            this.nodes = nodes;
            this.primary = primary;
            this.setValue(value);
            this.setKey(key);
        }

        public void deserialize(ClassLoader[] cls) throws IOException, ClassNotFoundException {
            this.key(cls);
            this.value(cls);
        }

        public int getMsgType() {
            return this.msgtype;
        }

        public boolean isDiff() {
            return this.diff;
        }

        public Serializable getKey() {
            try {
                return this.key(null);
            }
            catch (Exception x) {
                throw new RuntimeException(sm.getString("mapMessage.deserialize.error.key"), x);
            }
        }

        public Serializable key(ClassLoader[] cls) throws IOException, ClassNotFoundException {
            if (this.key != null) {
                return this.key;
            }
            if (this.keydata == null || this.keydata.length == 0) {
                return null;
            }
            this.key = XByteBuffer.deserialize(this.keydata, 0, this.keydata.length, cls);
            this.keydata = null;
            return this.key;
        }

        public byte[] getKeyData() {
            return this.keydata;
        }

        public Serializable getValue() {
            try {
                return this.value(null);
            }
            catch (Exception x) {
                throw new RuntimeException(sm.getString("mapMessage.deserialize.error.value"), x);
            }
        }

        public Serializable value(ClassLoader[] cls) throws IOException, ClassNotFoundException {
            if (this.value != null) {
                return this.value;
            }
            if (this.valuedata == null || this.valuedata.length == 0) {
                return null;
            }
            this.value = XByteBuffer.deserialize(this.valuedata, 0, this.valuedata.length, cls);
            this.valuedata = null;
            return this.value;
        }

        public byte[] getValueData() {
            return this.valuedata;
        }

        public byte[] getDiffValue() {
            return this.diffvalue;
        }

        public Member[] getBackupNodes() {
            return this.nodes;
        }

        public Member getPrimary() {
            return this.primary;
        }

        private void setPrimary(Member m) {
            this.primary = m;
        }

        public byte[] getMapId() {
            return this.mapId;
        }

        public void setValue(Serializable value) {
            try {
                if (value != null) {
                    this.valuedata = XByteBuffer.serialize(value);
                }
                this.value = value;
            }
            catch (IOException x) {
                throw new RuntimeException(x);
            }
        }

        public void setKey(Serializable key) {
            try {
                if (key != null) {
                    this.keydata = XByteBuffer.serialize(key);
                }
                this.key = key;
            }
            catch (IOException x) {
                throw new RuntimeException(x);
            }
        }

        public MapMessage clone() {
            try {
                return (MapMessage)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class MapEntry<K, V>
    implements Map.Entry<K, V> {
        private boolean backup;
        private boolean proxy;
        private boolean copy;
        private Member[] backupNodes;
        private Member primary;
        private K key;
        private V value;

        public MapEntry(K key, V value) {
            this.setKey(key);
            this.setValue(value);
        }

        public boolean isKeySerializable() {
            return this.key == null || this.key instanceof Serializable;
        }

        public boolean isValueSerializable() {
            return this.value == null || this.value instanceof Serializable;
        }

        public boolean isSerializable() {
            return this.isKeySerializable() && this.isValueSerializable();
        }

        public boolean isBackup() {
            return this.backup;
        }

        public void setBackup(boolean backup) {
            this.backup = backup;
        }

        public boolean isProxy() {
            return this.proxy;
        }

        public boolean isPrimary() {
            return !this.proxy && !this.backup && !this.copy;
        }

        public boolean isActive() {
            return !this.proxy;
        }

        public void setProxy(boolean proxy) {
            this.proxy = proxy;
        }

        public boolean isCopy() {
            return this.copy;
        }

        public void setCopy(boolean copy) {
            this.copy = copy;
        }

        public boolean isDiffable() {
            return this.value instanceof ReplicatedMapEntry && ((ReplicatedMapEntry)this.value).isDiffable();
        }

        public void setBackupNodes(Member[] nodes) {
            this.backupNodes = nodes;
        }

        public Member[] getBackupNodes() {
            return this.backupNodes;
        }

        public void setPrimary(Member m) {
            this.primary = m;
        }

        public Member getPrimary() {
            return this.primary;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        public K setKey(K key) {
            K old = this.key;
            this.key = key;
            return old;
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return this.key.equals(o);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void apply(byte[] data, int offset, int length, boolean diff) throws IOException, ClassNotFoundException {
            if (this.isDiffable() && diff) {
                ReplicatedMapEntry rentry = (ReplicatedMapEntry)this.value;
                rentry.lock();
                try {
                    rentry.applyDiff(data, offset, length);
                }
                finally {
                    rentry.unlock();
                }
            } else if (length == 0) {
                this.value = null;
                this.proxy = true;
            } else {
                this.value = XByteBuffer.deserialize(data, offset, length);
            }
        }

        public String toString() {
            StringBuilder buf = new StringBuilder("MapEntry[key:");
            buf.append(this.getKey()).append("; ");
            buf.append("value:").append(this.getValue()).append("; ");
            buf.append("primary:").append(this.isPrimary()).append("; ");
            buf.append("backup:").append(this.isBackup()).append("; ");
            buf.append("proxy:").append(this.isProxy()).append(";]");
            return buf.toString();
        }
    }
}


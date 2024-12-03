/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Cluster
 *  org.apache.catalina.Manager
 *  org.apache.catalina.Valve
 *  org.apache.catalina.tribes.Channel
 *  org.apache.catalina.tribes.Member
 */
package org.apache.catalina.ha;

import java.util.Map;
import org.apache.catalina.Cluster;
import org.apache.catalina.Manager;
import org.apache.catalina.Valve;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.ha.ClusterListener;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.Member;

public interface CatalinaCluster
extends Cluster {
    public void send(ClusterMessage var1);

    public void send(ClusterMessage var1, Member var2);

    public void send(ClusterMessage var1, Member var2, int var3);

    public boolean hasMembers();

    public Member[] getMembers();

    public Member getLocalMember();

    public void addValve(Valve var1);

    public void addClusterListener(ClusterListener var1);

    public void removeClusterListener(ClusterListener var1);

    public void setClusterDeployer(ClusterDeployer var1);

    public ClusterDeployer getClusterDeployer();

    public Map<String, ClusterManager> getManagers();

    public Manager getManager(String var1);

    public String getManagerName(String var1, Manager var2);

    public Valve[] getValves();

    public void setChannel(Channel var1);

    public Channel getChannel();
}


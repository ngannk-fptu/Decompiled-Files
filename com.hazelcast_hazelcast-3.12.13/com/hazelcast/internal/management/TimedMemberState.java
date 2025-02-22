/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.monitor.impl.MemberStateImpl;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public final class TimedMemberState
implements Cloneable,
JsonSerializable {
    long time;
    MemberStateImpl memberState;
    List<String> memberList;
    boolean master;
    String clusterName;
    boolean sslEnabled;
    boolean lite;
    boolean socketInterceptorEnabled;
    boolean scriptingEnabled;

    public List<String> getMemberList() {
        return this.memberList;
    }

    public void setMemberList(List<String> memberList) {
        this.memberList = memberList;
    }

    public boolean isMaster() {
        return this.master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public String getClusterName() {
        return this.clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public MemberStateImpl getMemberState() {
        return this.memberState;
    }

    public void setMemberState(MemberStateImpl memberState) {
        this.memberState = memberState;
    }

    public boolean isSslEnabled() {
        return this.sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public boolean isLite() {
        return this.lite;
    }

    public void setLite(boolean lite) {
        this.lite = lite;
    }

    public boolean isSocketInterceptorEnabled() {
        return this.socketInterceptorEnabled;
    }

    public void setSocketInterceptorEnabled(boolean socketInterceptorEnabled) {
        this.socketInterceptorEnabled = socketInterceptorEnabled;
    }

    public boolean isScriptingEnabled() {
        return this.scriptingEnabled;
    }

    public void setScriptingEnabled(boolean scriptingEnabled) {
        this.scriptingEnabled = scriptingEnabled;
    }

    public TimedMemberState clone() throws CloneNotSupportedException {
        TimedMemberState state = (TimedMemberState)super.clone();
        state.setTime(this.time);
        state.setMemberState(this.memberState);
        state.setMemberList(this.memberList);
        state.setMaster(this.master);
        state.setClusterName(this.clusterName);
        state.setSslEnabled(this.sslEnabled);
        state.setLite(this.lite);
        state.setSocketInterceptorEnabled(this.socketInterceptorEnabled);
        state.setScriptingEnabled(this.scriptingEnabled);
        return state;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("master", this.master);
        root.add("time", this.time);
        root.add("clusterName", this.clusterName);
        if (this.memberList != null) {
            JsonArray members = new JsonArray();
            for (String member : this.memberList) {
                members.add(member);
            }
            root.add("memberList", members);
        }
        root.add("memberState", this.memberState.toJson());
        root.add("sslEnabled", this.sslEnabled);
        root.add("lite", this.lite);
        root.add("socketInterceptorEnabled", this.socketInterceptorEnabled);
        root.add("scriptingEnabled", this.scriptingEnabled);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.time = JsonUtil.getLong(json, "time");
        this.master = JsonUtil.getBoolean(json, "master");
        this.clusterName = JsonUtil.getString(json, "clusterName");
        JsonArray jsonMemberList = JsonUtil.getArray(json, "memberList");
        this.memberList = new ArrayList<String>(jsonMemberList.size());
        for (JsonValue member : jsonMemberList.values()) {
            this.memberList.add(member.asString());
        }
        JsonObject jsonMemberState = JsonUtil.getObject(json, "memberState");
        this.memberState = new MemberStateImpl();
        this.memberState.fromJson(jsonMemberState);
        this.sslEnabled = JsonUtil.getBoolean(json, "sslEnabled", false);
        this.lite = JsonUtil.getBoolean(json, "lite");
        this.socketInterceptorEnabled = JsonUtil.getBoolean(json, "socketInterceptorEnabled");
        this.scriptingEnabled = JsonUtil.getBoolean(json, "scriptingEnabled");
    }

    public String toString() {
        return "TimedMemberState{" + StringUtil.LINE_SEPARATOR + '\t' + this.memberState + StringUtil.LINE_SEPARATOR + "}";
    }
}


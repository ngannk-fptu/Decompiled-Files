/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.restricted.SoyMsg;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;

public class SoyMsgBundleImpl
implements SoyMsgBundle {
    private final String localeString;
    private final Map<Long, SoyMsg> msgMap;

    public SoyMsgBundleImpl(@Nullable String localeString, List<SoyMsg> msgs) {
        this.localeString = localeString;
        TreeMap tempMsgMap = Maps.newTreeMap();
        for (SoyMsg msg : msgs) {
            Preconditions.checkArgument((boolean)Objects.equal((Object)msg.getLocaleString(), (Object)localeString));
            long msgId = msg.getId();
            if (!tempMsgMap.containsKey(msgId)) {
                tempMsgMap.put(msgId, msg);
                continue;
            }
            SoyMsg existingMsg = (SoyMsg)tempMsgMap.get(msgId);
            for (String source : msg.getSourcePaths()) {
                existingMsg.addSourcePath(source);
            }
        }
        this.msgMap = ImmutableMap.copyOf((Map)tempMsgMap);
    }

    @Override
    public String getLocaleString() {
        return this.localeString;
    }

    @Override
    public SoyMsg getMsg(long msgId) {
        return this.msgMap.get(msgId);
    }

    @Override
    public int getNumMsgs() {
        return this.msgMap.size();
    }

    @Override
    public Iterator<SoyMsg> iterator() {
        return this.msgMap.values().iterator();
    }
}


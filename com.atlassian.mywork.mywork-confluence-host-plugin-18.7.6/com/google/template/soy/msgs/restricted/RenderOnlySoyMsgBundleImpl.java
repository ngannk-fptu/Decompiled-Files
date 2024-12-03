/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.restricted.MsgPartUtils;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import javax.annotation.Nullable;

final class RenderOnlySoyMsgBundleImpl
implements SoyMsgBundle {
    private final String localeString;
    private final long[] idArray;
    private final Object[] valueArray;

    public RenderOnlySoyMsgBundleImpl(@Nullable String localeString, Iterable<SoyMsg> msgs) {
        this.localeString = localeString;
        TreeMap partsMap = Maps.newTreeMap();
        for (SoyMsg msg : msgs) {
            Preconditions.checkArgument((boolean)Objects.equal((Object)msg.getLocaleString(), (Object)localeString));
            Preconditions.checkArgument((msg.getAltId() < 0L ? 1 : 0) != 0, (Object)"RenderOnlySoyMsgBundleImpl doesn't support alternate ID's.");
            long msgId = msg.getId();
            Preconditions.checkArgument((!partsMap.containsKey(msgId) ? 1 : 0) != 0, (Object)"Duplicate messages are not permitted in the render-only impl.");
            ImmutableList<SoyMsgPart> parts = msg.getParts();
            Preconditions.checkArgument((MsgPartUtils.hasPlrselPart(parts) == msg.isPlrselMsg() ? 1 : 0) != 0, (Object)"Message's plural/select status is inconsistent -- internal compiler bug.");
            if (parts.size() == 1) {
                partsMap.put(msgId, parts.get(0));
                continue;
            }
            partsMap.put(msgId, ImmutableList.copyOf(parts));
        }
        this.idArray = new long[partsMap.size()];
        this.valueArray = new Object[partsMap.size()];
        int index = 0;
        for (Map.Entry entry : partsMap.entrySet()) {
            this.idArray[index] = (Long)entry.getKey();
            this.valueArray[index] = entry.getValue();
            ++index;
        }
        Preconditions.checkState((index == partsMap.size() ? 1 : 0) != 0);
    }

    private SoyMsg resurrectMsg(long id, Object value) {
        ImmutableList parts = value instanceof SoyMsgPart ? ImmutableList.of((Object)((SoyMsgPart)value)) : (ImmutableList)value;
        return new SoyMsg(id, this.localeString, MsgPartUtils.hasPlrselPart((List<SoyMsgPart>)parts), (List<SoyMsgPart>)parts);
    }

    @Override
    public String getLocaleString() {
        return this.localeString;
    }

    @Override
    public SoyMsg getMsg(long msgId) {
        int index = Arrays.binarySearch(this.idArray, msgId);
        return index >= 0 ? this.resurrectMsg(msgId, this.valueArray[index]) : null;
    }

    @Override
    public int getNumMsgs() {
        return this.idArray.length;
    }

    @Override
    public Iterator<SoyMsg> iterator() {
        return new Iterator<SoyMsg>(){
            int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < RenderOnlySoyMsgBundleImpl.this.idArray.length;
            }

            @Override
            public SoyMsg next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                SoyMsg result = RenderOnlySoyMsgBundleImpl.this.resurrectMsg(RenderOnlySoyMsgBundleImpl.this.idArray[this.index], RenderOnlySoyMsgBundleImpl.this.valueArray[this.index]);
                ++this.index;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Iterator is immutable");
            }
        };
    }
}


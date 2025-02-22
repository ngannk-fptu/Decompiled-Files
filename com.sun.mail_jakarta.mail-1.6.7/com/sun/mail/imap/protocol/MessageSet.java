/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap.protocol;

import java.util.ArrayList;

public class MessageSet {
    public int start;
    public int end;

    public MessageSet() {
    }

    public MessageSet(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int size() {
        return this.end - this.start + 1;
    }

    public static MessageSet[] createMessageSets(int[] msgs) {
        ArrayList<MessageSet> v = new ArrayList<MessageSet>();
        for (int i = 0; i < msgs.length; ++i) {
            int j;
            MessageSet ms = new MessageSet();
            ms.start = msgs[i];
            for (j = i + 1; j < msgs.length && msgs[j] == msgs[j - 1] + 1; ++j) {
            }
            ms.end = msgs[j - 1];
            v.add(ms);
            i = j - 1;
        }
        return v.toArray(new MessageSet[v.size()]);
    }

    public static String toString(MessageSet[] msgsets) {
        if (msgsets == null || msgsets.length == 0) {
            return null;
        }
        int i = 0;
        StringBuilder s = new StringBuilder();
        int size = msgsets.length;
        while (true) {
            int start;
            int end;
            if ((end = msgsets[i].end) > (start = msgsets[i].start)) {
                s.append(start).append(':').append(end);
            } else {
                s.append(start);
            }
            if (++i >= size) break;
            s.append(',');
        }
        return s.toString();
    }

    public static int size(MessageSet[] msgsets) {
        int count = 0;
        if (msgsets == null) {
            return 0;
        }
        for (int i = 0; i < msgsets.length; ++i) {
            count += msgsets[i].size();
        }
        return count;
    }
}


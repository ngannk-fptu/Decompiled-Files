/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.log;

import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.SnapshotEntry;
import com.hazelcast.ringbuffer.impl.ArrayRingbuffer;
import com.hazelcast.ringbuffer.impl.Ringbuffer;
import java.util.ArrayList;
import java.util.List;

public class RaftLog {
    private final Ringbuffer<LogEntry> logs;
    private SnapshotEntry snapshot = new SnapshotEntry();

    public RaftLog(int capacity) {
        this.logs = new ArrayRingbuffer<LogEntry>(capacity);
    }

    public long lastLogOrSnapshotIndex() {
        return this.lastLogOrSnapshotEntry().index();
    }

    public int lastLogOrSnapshotTerm() {
        return this.lastLogOrSnapshotEntry().term();
    }

    public LogEntry lastLogOrSnapshotEntry() {
        return !this.logs.isEmpty() ? this.logs.read(this.logs.tailSequence()) : this.snapshot;
    }

    public boolean containsLogEntry(long entryIndex) {
        long sequence = this.toSequence(entryIndex);
        return sequence >= this.logs.headSequence() && sequence <= this.logs.tailSequence();
    }

    public LogEntry getLogEntry(long entryIndex) {
        if (entryIndex < 1L) {
            throw new IllegalArgumentException("Illegal index: " + entryIndex + ". Index starts from 1.");
        }
        if (!this.containsLogEntry(entryIndex)) {
            return null;
        }
        LogEntry logEntry = this.logs.read(this.toSequence(entryIndex));
        assert (logEntry.index() == entryIndex) : "Expected: " + entryIndex + ", Entry: " + logEntry;
        return logEntry;
    }

    public List<LogEntry> truncateEntriesFrom(long entryIndex) {
        if (entryIndex <= this.snapshotIndex()) {
            throw new IllegalArgumentException("Illegal index: " + entryIndex + ", snapshot index: " + this.snapshotIndex());
        }
        if (entryIndex > this.lastLogOrSnapshotIndex()) {
            throw new IllegalArgumentException("Illegal index: " + entryIndex + ", last log index: " + this.lastLogOrSnapshotIndex());
        }
        long startSequence = this.toSequence(entryIndex);
        assert (startSequence >= this.logs.headSequence()) : "Entry index: " + entryIndex + ", Head Seq: " + this.logs.headSequence();
        ArrayList<LogEntry> truncated = new ArrayList<LogEntry>();
        for (long ix = startSequence; ix <= this.logs.tailSequence(); ++ix) {
            truncated.add(this.logs.read(ix));
        }
        this.logs.setTailSequence(startSequence - 1L);
        return truncated;
    }

    public int availableCapacity() {
        return (int)(this.logs.getCapacity() - this.logs.size());
    }

    public boolean checkAvailableCapacity(int requestedCapacity) {
        return this.availableCapacity() >= requestedCapacity;
    }

    public void appendEntries(LogEntry ... newEntries) {
        int lastTerm = this.lastLogOrSnapshotTerm();
        long lastIndex = this.lastLogOrSnapshotIndex();
        if (!this.checkAvailableCapacity(newEntries.length)) {
            throw new IllegalStateException("Not enough capacity! Capacity: " + this.logs.getCapacity() + ", Size: " + this.logs.size() + ", New entries: " + newEntries.length);
        }
        for (LogEntry entry : newEntries) {
            if (entry.term() < lastTerm) {
                throw new IllegalArgumentException("Cannot append " + entry + " since its term is lower than last log term: " + lastTerm);
            }
            if (entry.index() != lastIndex + 1L) {
                throw new IllegalArgumentException("Cannot append " + entry + " since its index is bigger than (lastLogIndex + 1): " + (lastIndex + 1L));
            }
            this.logs.add(entry);
            ++lastIndex;
            lastTerm = Math.max(lastTerm, entry.term());
        }
    }

    public LogEntry[] getEntriesBetween(long fromEntryIndex, long toEntryIndex) {
        if (fromEntryIndex > toEntryIndex) {
            throw new IllegalArgumentException("Illegal from entry index: " + fromEntryIndex + ", to entry index: " + toEntryIndex);
        }
        if (!this.containsLogEntry(fromEntryIndex)) {
            throw new IllegalArgumentException("Illegal from entry index: " + fromEntryIndex);
        }
        if (fromEntryIndex > this.lastLogOrSnapshotIndex()) {
            throw new IllegalArgumentException("Illegal from entry index: " + fromEntryIndex + ", last log index: " + this.lastLogOrSnapshotIndex());
        }
        if (toEntryIndex > this.lastLogOrSnapshotIndex()) {
            throw new IllegalArgumentException("Illegal to entry index: " + toEntryIndex + ", last log index: " + this.lastLogOrSnapshotIndex());
        }
        assert ((int)(toEntryIndex - fromEntryIndex) >= 0) : "Int overflow! From: " + fromEntryIndex + ", to: " + toEntryIndex;
        LogEntry[] entries = new LogEntry[(int)(toEntryIndex - fromEntryIndex + 1L)];
        long offset = this.toSequence(fromEntryIndex);
        for (int i = 0; i < entries.length; ++i) {
            entries[i] = this.logs.read(offset + (long)i);
        }
        return entries;
    }

    public int setSnapshot(SnapshotEntry snapshot) {
        return this.setSnapshot(snapshot, snapshot.index());
    }

    public int setSnapshot(SnapshotEntry snapshot, long truncateUpToIndex) {
        if (snapshot.index() <= this.snapshotIndex()) {
            throw new IllegalArgumentException("Illegal index: " + snapshot.index() + ", current snapshot index: " + this.snapshotIndex());
        }
        long newHeadSeq = this.toSequence(truncateUpToIndex) + 1L;
        long newTailSeq = Math.max(this.logs.tailSequence(), newHeadSeq - 1L);
        long prevSize = this.logs.size();
        for (long seq = this.logs.headSequence(); seq < newHeadSeq; ++seq) {
            this.logs.set(seq, null);
        }
        this.logs.setHeadSequence(newHeadSeq);
        this.logs.setTailSequence(newTailSeq);
        this.snapshot = snapshot;
        return (int)(prevSize - this.logs.size());
    }

    public long snapshotIndex() {
        return this.snapshot.index();
    }

    public SnapshotEntry snapshot() {
        return this.snapshot;
    }

    private long toSequence(long entryIndex) {
        return entryIndex - 1L;
    }
}


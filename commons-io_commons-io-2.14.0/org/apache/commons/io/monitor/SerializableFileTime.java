/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.monitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.file.attribute.FileTimes;

class SerializableFileTime
implements Serializable {
    static final SerializableFileTime EPOCH = new SerializableFileTime(FileTimes.EPOCH);
    private static final long serialVersionUID = 1L;
    private FileTime fileTime;

    public SerializableFileTime(FileTime fileTime) {
        this.fileTime = Objects.requireNonNull(fileTime);
    }

    public int compareTo(FileTime other) {
        return this.fileTime.compareTo(other);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SerializableFileTime)) {
            return false;
        }
        SerializableFileTime other = (SerializableFileTime)obj;
        return Objects.equals(this.fileTime, other.fileTime);
    }

    public int hashCode() {
        return this.fileTime.hashCode();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        this.fileTime = FileTime.from((Instant)ois.readObject());
    }

    long to(TimeUnit unit) {
        return this.fileTime.to(unit);
    }

    Instant toInstant() {
        return this.fileTime.toInstant();
    }

    long toMillis() {
        return this.fileTime.toMillis();
    }

    public String toString() {
        return this.fileTime.toString();
    }

    FileTime unwrap() {
        return this.fileTime;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.fileTime.toInstant());
    }
}


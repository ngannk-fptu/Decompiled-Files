/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.util.Internal;

@Internal
public final class SavedByEntry {
    private String userName;
    private String saveLocation;

    public SavedByEntry(String userName, String saveLocation) {
        this.userName = userName;
        this.saveLocation = saveLocation;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getSaveLocation() {
        return this.saveLocation;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SavedByEntry)) {
            return false;
        }
        SavedByEntry that = (SavedByEntry)other;
        return that.userName.equals(this.userName) && that.saveLocation.equals(this.saveLocation);
    }

    public int hashCode() {
        return Objects.hash(this.userName, this.saveLocation);
    }

    public String toString() {
        return "SavedByEntry[userName=" + this.getUserName() + ",saveLocation=" + this.getSaveLocation() + "]";
    }
}


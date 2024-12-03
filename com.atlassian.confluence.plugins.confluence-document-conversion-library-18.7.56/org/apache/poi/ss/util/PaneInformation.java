/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.util;

import java.util.Objects;
import org.apache.poi.ss.usermodel.PaneType;

public class PaneInformation {
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_RIGHT = 1;
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_UPPER_LEFT = 3;
    private final short x;
    private final short y;
    private final short topRow;
    private final short leftColumn;
    private final byte activePane;
    private final boolean frozen;

    public PaneInformation(short x, short y, short top, short left, byte active, boolean frozen) {
        this.x = x;
        this.y = y;
        this.topRow = top;
        this.leftColumn = left;
        this.activePane = active;
        this.frozen = frozen;
    }

    public short getVerticalSplitPosition() {
        return this.x;
    }

    public short getHorizontalSplitPosition() {
        return this.y;
    }

    public short getHorizontalSplitTopRow() {
        return this.topRow;
    }

    public short getVerticalSplitLeftColumn() {
        return this.leftColumn;
    }

    public byte getActivePane() {
        return this.activePane;
    }

    public PaneType getActivePaneType() {
        switch (this.activePane) {
            case 0: {
                return PaneType.LOWER_RIGHT;
            }
            case 1: {
                return PaneType.UPPER_RIGHT;
            }
            case 2: {
                return PaneType.LOWER_LEFT;
            }
            case 3: {
                return PaneType.UPPER_LEFT;
            }
        }
        return null;
    }

    public boolean isFreezePane() {
        return this.frozen;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaneInformation)) {
            return false;
        }
        PaneInformation that = (PaneInformation)o;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (this.topRow != that.topRow) {
            return false;
        }
        if (this.leftColumn != that.leftColumn) {
            return false;
        }
        if (this.activePane != that.activePane) {
            return false;
        }
        return this.frozen == that.frozen;
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y, this.topRow, this.leftColumn, this.activePane, this.frozen);
    }
}


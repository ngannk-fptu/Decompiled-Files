/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.varia;

import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class LevelRangeFilter
extends Filter {
    boolean acceptOnMatch;
    Level levelMin;
    Level levelMax;

    @Override
    public int decide(LoggingEvent event) {
        if (this.levelMin != null && !event.getLevel().isGreaterOrEqual(this.levelMin)) {
            return -1;
        }
        if (this.levelMax != null && event.getLevel().toInt() > this.levelMax.toInt()) {
            return -1;
        }
        if (this.acceptOnMatch) {
            return 1;
        }
        return 0;
    }

    public boolean getAcceptOnMatch() {
        return this.acceptOnMatch;
    }

    public Level getLevelMax() {
        return this.levelMax;
    }

    public Level getLevelMin() {
        return this.levelMin;
    }

    public void setAcceptOnMatch(boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }

    public void setLevelMax(Level levelMax) {
        this.levelMax = levelMax;
    }

    public void setLevelMin(Level levelMin) {
        this.levelMin = levelMin;
    }
}


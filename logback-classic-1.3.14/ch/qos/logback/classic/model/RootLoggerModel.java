/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.processor.PhaseIndicator
 *  ch.qos.logback.core.model.processor.ProcessingPhase
 */
package ch.qos.logback.classic.model;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.PhaseIndicator;
import ch.qos.logback.core.model.processor.ProcessingPhase;
import java.util.Objects;

@PhaseIndicator(phase=ProcessingPhase.SECOND)
public class RootLoggerModel
extends Model {
    private static final long serialVersionUID = -2811453129653502831L;
    String level;

    protected RootLoggerModel makeNewInstance() {
        return new RootLoggerModel();
    }

    protected void mirror(Model that) {
        RootLoggerModel actual = (RootLoggerModel)that;
        super.mirror((Model)actual);
        this.level = actual.level;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Objects.hash(this.level);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (((Object)((Object)this)).getClass() != obj.getClass()) {
            return false;
        }
        RootLoggerModel other = (RootLoggerModel)((Object)obj);
        return Objects.equals(this.level, other.level);
    }
}


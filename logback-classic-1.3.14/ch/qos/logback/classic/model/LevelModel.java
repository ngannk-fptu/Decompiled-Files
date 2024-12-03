/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.model.Model
 */
package ch.qos.logback.classic.model;

import ch.qos.logback.core.model.Model;
import java.util.Objects;

public class LevelModel
extends Model {
    private static final long serialVersionUID = -7287549849308062148L;
    String value;

    protected LevelModel makeNewInstance() {
        return new LevelModel();
    }

    protected void mirror(Model that) {
        LevelModel actual = (LevelModel)that;
        super.mirror((Model)actual);
        this.value = actual.value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Objects.hash(this.value);
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
        LevelModel other = (LevelModel)((Object)obj);
        return Objects.equals(this.value, other.value);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Term;
import java.util.HashMap;
import java.util.Map;

public interface TermNumeric<T extends Number>
extends Term<T> {
    public Unit getUnit();

    public TermNumeric<T> setUnit(Unit var1);

    public TermNumeric<T> setZero();

    public static enum Unit {
        none("", Type.none),
        em("em", Type.length),
        ex("ex", Type.length),
        ch("ch", Type.length),
        rem("rem", Type.length),
        vw("vw", Type.length),
        vh("vh", Type.length),
        vmin("vmin", Type.length),
        vmax("vmax", Type.length),
        cm("cm", Type.length),
        mm("mm", Type.length),
        q("q", Type.length),
        in("in", Type.length),
        pt("pt", Type.length),
        pc("pc", Type.length),
        px("px", Type.length),
        fr("fr", Type.length),
        deg("deg", Type.angle),
        rad("rad", Type.angle),
        grad("grad", Type.angle),
        turn("turn", Type.angle),
        ms("ms", Type.time),
        s("s", Type.time),
        hz("hz", Type.frequency),
        khz("khz", Type.frequency),
        dpi("dpi", Type.resolution),
        dpcm("dpcm", Type.resolution),
        dppx("dppx", Type.resolution);

        private static final Map<String, Unit> map;
        private String value;
        private Type type;

        private Unit(String value, Type type) {
            this.value = value;
            this.type = type;
        }

        public String value() {
            return this.value;
        }

        public Type getType() {
            return this.type;
        }

        public static Unit findByValue(String value) {
            return map.get(value);
        }

        public boolean isAngle() {
            return this.getType() == Type.angle;
        }

        public boolean isLength() {
            return this.getType() == Type.length;
        }

        public boolean isTime() {
            return this.getType() == Type.time;
        }

        public boolean isFrequency() {
            return this.getType() == Type.frequency;
        }

        public boolean isResolution() {
            return this.getType() == Type.resolution;
        }

        static {
            map = new HashMap<String, Unit>(Unit.values().length);
            for (Unit u : Unit.values()) {
                map.put(u.value, u);
            }
        }

        public static enum Type {
            angle,
            length,
            time,
            frequency,
            resolution,
            none;

        }
    }
}


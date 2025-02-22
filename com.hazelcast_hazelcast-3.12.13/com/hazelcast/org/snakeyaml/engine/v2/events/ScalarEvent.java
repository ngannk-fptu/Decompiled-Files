/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.common.CharConstants;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.events.ImplicitTuple;
import com.hazelcast.org.snakeyaml.engine.v2.events.NodeEvent;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ScalarEvent
extends NodeEvent {
    private static final Map<Character, Integer> ESCAPES_TO_PRINT = CharConstants.ESCAPES.entrySet().stream().filter(entry -> ((Character)entry.getKey()).charValue() != '\"').collect(Collectors.toMap(e -> (Character)e.getKey(), e -> (Integer)e.getValue()));
    private final Optional<String> tag;
    private final ScalarStyle style;
    private final String value;
    private final ImplicitTuple implicit;

    public ScalarEvent(Optional<Anchor> anchor, Optional<String> tag, ImplicitTuple implicit, String value, ScalarStyle style, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(anchor, startMark, endMark);
        Objects.requireNonNull(tag, "Tag must be provided.");
        this.tag = tag;
        this.implicit = implicit;
        Objects.requireNonNull(value, "Value must be provided.");
        this.value = value;
        Objects.requireNonNull(style, "Style must be provided.");
        this.style = style;
    }

    public ScalarEvent(Optional<Anchor> anchor, Optional<String> tag, ImplicitTuple implicit, String value, ScalarStyle style) {
        this(anchor, tag, implicit, value, style, Optional.empty(), Optional.empty());
    }

    public Optional<String> getTag() {
        return this.tag;
    }

    public ScalarStyle getScalarStyle() {
        return this.style;
    }

    public String getValue() {
        return this.value;
    }

    public ImplicitTuple getImplicit() {
        return this.implicit;
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.Scalar;
    }

    public boolean isPlain() {
        return this.style == ScalarStyle.PLAIN;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("=VAL");
        this.getAnchor().ifPresent(a -> builder.append(" &" + a));
        if (this.implicit.bothFalse()) {
            this.getTag().ifPresent(theTag -> builder.append(" <" + theTag + ">"));
        }
        builder.append(" ");
        builder.append(this.getScalarStyle().toString());
        builder.append(this.escapedValue());
        return builder.toString();
    }

    private String escape(Character ch) {
        if (ESCAPES_TO_PRINT.containsKey(ch)) {
            Integer i = ESCAPES_TO_PRINT.get(ch);
            Character c = Character.valueOf((char)i.intValue());
            return "\\" + c.toString();
        }
        return ch.toString();
    }

    public String escapedValue() {
        return this.value.codePoints().filter(i -> i < 65535).mapToObj(c -> Character.valueOf((char)c)).map(this::escape).collect(Collectors.joining(""));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.Event;

public class MessageEvent
extends Event {
    private String text;
    public static int WARNING = 0;
    public static int ERROR = 1;

    public MessageEvent(Object object) {
        super(object);
    }

    public MessageEvent(Object object, int n, String string) {
        super(object);
        this.setValues(n, string);
    }

    public String getText() {
        return this.text;
    }

    void setText(String string) {
        this.text = string;
    }

    void setValues(int n, String string) {
        super.setValues(n);
        this.setText(string);
    }

    public String toString() {
        return "ParserMessageEvent [" + (this.getType() == WARNING ? "warning," : "error,") + this.getText() + "]";
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

public interface ContentHandler {
    public void startCalendar();

    public void endCalendar() throws IOException;

    public void startComponent(String var1);

    public void endComponent(String var1);

    public void startProperty(String var1);

    public void propertyValue(String var1) throws URISyntaxException, ParseException, IOException;

    public void endProperty(String var1) throws URISyntaxException, ParseException, IOException;

    public void parameter(String var1, String var2) throws URISyntaxException;
}


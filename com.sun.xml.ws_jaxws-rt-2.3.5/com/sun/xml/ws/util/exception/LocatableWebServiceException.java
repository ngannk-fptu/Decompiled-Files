/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.util.exception;

import com.sun.istack.NotNull;
import com.sun.xml.ws.resources.UtilMessages;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class LocatableWebServiceException
extends WebServiceException {
    private final Locator[] location;

    public LocatableWebServiceException(String message, Locator ... location) {
        this(message, (Throwable)null, location);
    }

    public LocatableWebServiceException(String message, Throwable cause, Locator ... location) {
        super(LocatableWebServiceException.appendLocationInfo(message, location), cause);
        this.location = location;
    }

    public LocatableWebServiceException(Throwable cause, Locator ... location) {
        this(cause.toString(), cause, location);
    }

    public LocatableWebServiceException(String message, XMLStreamReader locationSource) {
        this(message, LocatableWebServiceException.toLocation(locationSource));
    }

    public LocatableWebServiceException(String message, Throwable cause, XMLStreamReader locationSource) {
        this(message, cause, LocatableWebServiceException.toLocation(locationSource));
    }

    public LocatableWebServiceException(Throwable cause, XMLStreamReader locationSource) {
        this(cause, LocatableWebServiceException.toLocation(locationSource));
    }

    @NotNull
    public List<Locator> getLocation() {
        return Arrays.asList(this.location);
    }

    private static String appendLocationInfo(String message, Locator[] location) {
        StringBuilder buf = new StringBuilder(message);
        for (Locator loc : location) {
            buf.append('\n').append(UtilMessages.UTIL_LOCATION(loc.getLineNumber(), loc.getSystemId()));
        }
        return buf.toString();
    }

    private static Locator toLocation(XMLStreamReader xsr) {
        LocatorImpl loc = new LocatorImpl();
        Location in = xsr.getLocation();
        loc.setSystemId(in.getSystemId());
        loc.setPublicId(in.getPublicId());
        loc.setLineNumber(in.getLineNumber());
        loc.setColumnNumber(in.getColumnNumber());
        return loc;
    }
}


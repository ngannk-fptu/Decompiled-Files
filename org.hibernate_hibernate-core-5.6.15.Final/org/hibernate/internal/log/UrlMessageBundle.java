/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.Cause
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.internal.log;

import java.net.URISyntaxException;
import java.net.URL;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=10000001, max=10001000)
public interface UrlMessageBundle {
    public static final UrlMessageBundle URL_LOGGER = (UrlMessageBundle)Logger.getMessageLogger(UrlMessageBundle.class, (String)"org.hibernate.orm.url");

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Malformed URL: %s", id=10000001)
    public void logMalformedUrl(URL var1, @Cause URISyntaxException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="File or directory named by URL [%s] could not be found.  URL will be ignored", id=10000002)
    public void logUnableToFindFileByUrl(URL var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="File or directory named by URL [%s] did not exist.  URL will be ignored", id=10000003)
    public void logFileDoesNotExist(URL var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Expecting resource named by URL [%s] to be a directory, but it was not.  URL will be ignored", id=10000004)
    public void logFileIsNotDirectory(URL var1);

    @Message(value="File [%s] referenced by given URL [%s] does not exist", id=10000005)
    public String fileDoesNotExist(String var1, URL var2);
}


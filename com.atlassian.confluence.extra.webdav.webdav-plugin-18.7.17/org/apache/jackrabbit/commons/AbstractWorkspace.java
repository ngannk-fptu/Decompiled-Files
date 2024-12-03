/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.io.IOException;
import java.io.InputStream;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import org.apache.jackrabbit.commons.xml.ParsingContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class AbstractWorkspace
implements Workspace {
    @Override
    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException, InvalidSerializedDataException, RepositoryException {
        try {
            ContentHandler handler = this.getImportContentHandler(parentAbsPath, uuidBehavior);
            new ParsingContentHandler(handler).parse(in);
        }
        catch (SAXException e) {
            Exception exception = e.getException();
            if (exception instanceof RepositoryException) {
                throw (RepositoryException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new InvalidSerializedDataException("XML parse error", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
    }
}


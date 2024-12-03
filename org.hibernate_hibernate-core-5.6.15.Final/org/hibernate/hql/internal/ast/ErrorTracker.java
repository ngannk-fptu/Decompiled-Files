/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.internal.ast;

import antlr.RecognitionException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.ParseErrorHandler;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class ErrorTracker
implements ParseErrorHandler {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)ErrorTracker.class.getName());
    private final String hql;
    private List<String> errorList = new ArrayList<String>();
    private List<RecognitionException> recognitionExceptions = new ArrayList<RecognitionException>();

    public ErrorTracker() {
        this(null);
    }

    public ErrorTracker(String hql) {
        this.hql = hql;
    }

    @Override
    public void reportError(RecognitionException e) {
        this.reportError(e.toString());
        this.recognitionExceptions.add(e);
        LOG.error(e.toString(), e);
    }

    @Override
    public void reportError(String message) {
        LOG.error(message);
        this.errorList.add(message);
    }

    @Override
    public int getErrorCount() {
        return this.errorList.size();
    }

    @Override
    public void reportWarning(String message) {
        LOG.debug(message);
    }

    private String getErrorString() {
        StringBuilder buf = new StringBuilder();
        Iterator<String> iterator = this.errorList.iterator();
        while (iterator.hasNext()) {
            buf.append(iterator.next());
            if (!iterator.hasNext()) continue;
            buf.append("\n");
        }
        return buf.toString();
    }

    @Override
    public void throwQueryException() throws QueryException {
        if (this.getErrorCount() > 0) {
            if (this.recognitionExceptions.size() > 0) {
                throw QuerySyntaxException.convert(this.recognitionExceptions.get(0), this.hql);
            }
            throw new QueryException(this.getErrorString(), this.hql);
        }
        LOG.debug("throwQueryException() : no errors");
    }
}


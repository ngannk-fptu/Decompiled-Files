/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.w3c.dom.DocumentFragment;

public class CleanResults {
    private List<String> errorMessages;
    private Callable<String> cleanHTML;
    private long startOfScan;
    private long elapsedScan;
    private DocumentFragment cleanXMLDocumentFragment;

    public CleanResults() {
        this.errorMessages = new ArrayList<String>();
    }

    public CleanResults(long startOfScan, final String cleanHTML, DocumentFragment XMLDocumentFragment, List<String> errorMessages) {
        this(startOfScan, new Callable<String>(){

            @Override
            public String call() throws Exception {
                return cleanHTML;
            }
        }, XMLDocumentFragment, errorMessages);
    }

    public CleanResults(long startOfScan, Callable<String> cleanHTML, DocumentFragment XMLDocumentFragment, List<String> errorMessages) {
        this.startOfScan = startOfScan;
        this.elapsedScan = System.currentTimeMillis() - startOfScan;
        this.cleanXMLDocumentFragment = XMLDocumentFragment;
        this.cleanHTML = cleanHTML;
        this.errorMessages = Collections.unmodifiableList(errorMessages);
    }

    public DocumentFragment getCleanXMLDocumentFragment() {
        return this.cleanXMLDocumentFragment;
    }

    public String getCleanHTML() {
        try {
            return this.cleanHTML.call();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getErrorMessages() {
        return this.errorMessages;
    }

    public double getScanTime() {
        return (double)this.elapsedScan / 1000.0;
    }

    public int getNumberOfErrors() {
        return this.errorMessages.size();
    }

    public long getStartOfScan() {
        return this.startOfScan;
    }
}


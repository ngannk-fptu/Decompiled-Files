/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.scan.AntiSamyDOMScanner;
import org.owasp.validator.html.scan.AntiSamySAXScanner;

public class AntiSamy {
    public static final int DOM = 0;
    public static final int SAX = 1;
    private Policy policy = null;

    public AntiSamy() {
    }

    public AntiSamy(Policy policy) {
        this.policy = policy;
    }

    public CleanResults scan(String taintedHTML) throws ScanException, PolicyException {
        return this.scan(taintedHTML, this.policy, 1);
    }

    public CleanResults scan(String taintedHTML, int scanType) throws ScanException, PolicyException {
        return this.scan(taintedHTML, this.policy, scanType);
    }

    public CleanResults scan(String taintedHTML, Policy policy) throws ScanException, PolicyException {
        return this.scan(taintedHTML, policy, 0);
    }

    public CleanResults scan(String taintedHTML, Policy policy, int scanType) throws ScanException, PolicyException {
        if (policy == null) {
            throw new PolicyException("No policy loaded");
        }
        if (scanType == 0) {
            return new AntiSamyDOMScanner(policy).scan(taintedHTML);
        }
        return new AntiSamySAXScanner(policy).scan(taintedHTML);
    }

    public CleanResults scan(Reader reader, Writer writer, Policy policy) throws ScanException {
        return new AntiSamySAXScanner(policy).scan(reader, writer);
    }

    public CleanResults scan(String taintedHTML, String filename) throws ScanException, PolicyException {
        Policy policy = Policy.getInstance(filename);
        return this.scan(taintedHTML, policy);
    }

    public CleanResults scan(String taintedHTML, File policyFile) throws ScanException, PolicyException {
        Policy policy = Policy.getInstance(policyFile);
        return this.scan(taintedHTML, policy);
    }
}


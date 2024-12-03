/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.MappedSisyphusPatternSource;
import com.atlassian.sisyphus.ReloadableSisyphusPatternSource;
import com.atlassian.sisyphus.SisyphusPatternPersister;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class RemoteXmlPatternSource
extends MappedSisyphusPatternSource
implements ReloadableSisyphusPatternSource {
    private final URL url;
    private final Proxy proxy;
    private final SisyphusPatternPersister patternPersister = new SisyphusPatternPersister();

    public RemoteXmlPatternSource(URL url) throws IOException, ClassNotFoundException {
        this.url = url;
        this.proxy = null;
        this.reload();
    }

    public RemoteXmlPatternSource(URL url, Proxy proxy) throws IOException, ClassNotFoundException {
        this.url = url;
        this.proxy = proxy;
        final String httpuser = System.getProperty("http.proxyUser");
        final String httppassword = System.getProperty("http.proxyPassword");
        final String httpsuser = System.getProperty("https.proxyUser");
        final String httpspassword = System.getProperty("https.proxyPassword");
        if (StringUtils.isNotEmpty((CharSequence)httpuser) && StringUtils.isNotEmpty((CharSequence)httppassword)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(httpuser, httppassword.toCharArray());
                }
            });
        } else if (StringUtils.isNotEmpty((CharSequence)httpsuser) && StringUtils.isNotEmpty((CharSequence)httpspassword)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(httpsuser, httpspassword.toCharArray());
                }
            });
        }
        this.reload();
    }

    @Override
    public void reload() throws IOException, ClassNotFoundException {
        InputStreamReader reader = null;
        reader = this.proxy != null ? new InputStreamReader(this.url.openConnection(this.proxy).getInputStream()) : new InputStreamReader(this.url.openStream());
        this.regexMap = this.patternPersister.readPatternsIn(reader);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.axis.client.Call;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class Options {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$Options == null ? (class$org$apache$axis$utils$Options = Options.class$("org.apache.axis.utils.Options")) : class$org$apache$axis$utils$Options).getName());
    String[] args = null;
    Vector usedArgs = null;
    URL defaultURL = null;
    static /* synthetic */ Class class$org$apache$axis$utils$Options;

    public Options(String[] _args) throws MalformedURLException {
        if (_args == null) {
            _args = new String[]{};
        }
        this.args = _args;
        this.usedArgs = null;
        this.defaultURL = new URL("http://localhost:8080/axis/servlet/AxisServlet");
        try {
            this.getURL();
        }
        catch (MalformedURLException e) {
            log.error((Object)Messages.getMessage("cantDoURL00"));
            throw e;
        }
        this.getUser();
        this.getPassword();
    }

    public void setDefaultURL(String url) throws MalformedURLException {
        this.defaultURL = new URL(url);
    }

    public void setDefaultURL(URL url) {
        this.defaultURL = url;
    }

    public int isFlagSet(char optChar) {
        int i;
        int loop;
        int value = 0;
        for (loop = 0; this.usedArgs != null && loop < this.usedArgs.size(); ++loop) {
            String arg = (String)this.usedArgs.elementAt(loop);
            if (arg.charAt(0) != '-') continue;
            for (i = 0; i < arg.length(); ++i) {
                if (arg.charAt(i) != optChar) continue;
                ++value;
            }
        }
        for (loop = 0; loop < this.args.length; ++loop) {
            if (this.args[loop] == null || this.args[loop].length() == 0 || this.args[loop].charAt(0) != '-') continue;
            while (this.args[loop] != null && (i = this.args[loop].indexOf(optChar)) != -1) {
                this.args[loop] = this.args[loop].substring(0, i) + this.args[loop].substring(i + 1);
                if (this.args[loop].length() == 1) {
                    this.args[loop] = null;
                }
                ++value;
                if (this.usedArgs == null) {
                    this.usedArgs = new Vector();
                }
                this.usedArgs.add("-" + optChar);
            }
        }
        return value;
    }

    public String isValueSet(char optChar) {
        int loop;
        String value = null;
        for (loop = 0; this.usedArgs != null && loop < this.usedArgs.size(); ++loop) {
            String arg = (String)this.usedArgs.elementAt(loop);
            if (arg.charAt(0) != '-' || arg.charAt(1) != optChar) continue;
            value = arg.substring(2);
            if (loop + 1 >= this.usedArgs.size()) continue;
            value = (String)this.usedArgs.elementAt(++loop);
        }
        for (loop = 0; loop < this.args.length; ++loop) {
            int i;
            if (this.args[loop] == null || this.args[loop].length() == 0 || this.args[loop].charAt(0) != '-' || (i = this.args[loop].indexOf(optChar)) != 1) continue;
            if (i != this.args[loop].length() - 1) {
                value = this.args[loop].substring(i + 1);
                this.args[loop] = this.args[loop].substring(0, i);
            } else {
                this.args[loop] = this.args[loop].substring(0, i);
                if (loop + 1 < this.args.length && this.args[loop + 1] != null && this.args[loop + 1].charAt(0) != '-') {
                    value = this.args[loop + 1];
                    this.args[loop + 1] = null;
                }
            }
            if (this.args[loop].length() != 1) continue;
            this.args[loop] = null;
        }
        if (value != null) {
            if (this.usedArgs == null) {
                this.usedArgs = new Vector();
            }
            this.usedArgs.add("-" + optChar);
            if (value.length() > 0) {
                this.usedArgs.add(value);
            }
        }
        return value;
    }

    public String getRemainingFlags() {
        StringBuffer sb = null;
        for (int loop = 0; loop < this.args.length; ++loop) {
            if (this.args[loop] == null || this.args[loop].length() == 0 || this.args[loop].charAt(0) != '-') continue;
            if (sb == null) {
                sb = new StringBuffer();
            }
            sb.append(this.args[loop].substring(1));
        }
        return sb == null ? null : sb.toString();
    }

    public String[] getRemainingArgs() {
        int loop;
        ArrayList<String> al = null;
        for (loop = 0; loop < this.args.length; ++loop) {
            if (this.args[loop] == null || this.args[loop].length() == 0 || this.args[loop].charAt(0) == '-') continue;
            if (al == null) {
                al = new ArrayList<String>();
            }
            al.add(this.args[loop]);
        }
        if (al == null) {
            return null;
        }
        String[] a = new String[al.size()];
        for (loop = 0; loop < al.size(); ++loop) {
            a[loop] = (String)al.get(loop);
        }
        return a;
    }

    public String getURL() throws MalformedURLException {
        String host = null;
        String port = null;
        String servlet = null;
        String protocol = null;
        URL url = null;
        Call.initialize();
        String tmp = this.isValueSet('l');
        if (tmp != null) {
            url = new URL(tmp);
            host = url.getHost();
            port = "" + url.getPort();
            servlet = url.getFile();
            protocol = url.getProtocol();
        }
        if ((tmp = this.isValueSet('f')) != null) {
            host = "";
            port = "-1";
            servlet = tmp;
            protocol = "file";
        }
        tmp = this.isValueSet('h');
        if (host == null) {
            host = tmp;
        }
        tmp = this.isValueSet('p');
        if (port == null) {
            port = tmp;
        }
        tmp = this.isValueSet('s');
        if (servlet == null) {
            servlet = tmp;
        }
        if (host == null) {
            host = this.defaultURL.getHost();
        }
        if (port == null) {
            port = "" + this.defaultURL.getPort();
        }
        if (servlet == null) {
            servlet = this.defaultURL.getFile();
        } else if (servlet.length() > 0 && servlet.charAt(0) != '/') {
            servlet = "/" + servlet;
        }
        if (url == null) {
            if (protocol == null) {
                protocol = this.defaultURL.getProtocol();
            }
            tmp = protocol + "://" + host;
            if (port != null && !port.equals("-1")) {
                tmp = tmp + ":" + port;
            }
            if (servlet != null) {
                tmp = tmp + servlet;
            }
        } else {
            tmp = url.toString();
        }
        log.debug((Object)Messages.getMessage("return02", "getURL", tmp));
        return tmp;
    }

    public String getHost() {
        try {
            URL url = new URL(this.getURL());
            return url.getHost();
        }
        catch (Exception exp) {
            return "localhost";
        }
    }

    public int getPort() {
        try {
            URL url = new URL(this.getURL());
            return url.getPort();
        }
        catch (Exception exp) {
            return -1;
        }
    }

    public String getUser() {
        return this.isValueSet('u');
    }

    public String getPassword() {
        return this.isValueSet('w');
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


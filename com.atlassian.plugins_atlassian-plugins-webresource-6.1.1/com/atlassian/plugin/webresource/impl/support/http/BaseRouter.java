/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl.support.http;

import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.support.http.BaseController;
import com.atlassian.plugin.webresource.impl.support.http.Request;
import com.atlassian.plugin.webresource.impl.support.http.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public abstract class BaseRouter<Controller extends BaseController> {
    protected final Globals globals;
    protected final List<Route> routes;
    protected final boolean useAbsoluteUrl;

    public BaseRouter(Globals globals) {
        this.globals = globals;
        this.routes = new ArrayList<Route>();
        this.useAbsoluteUrl = false;
    }

    protected BaseRouter(Globals globals, List<Route> routes, boolean useAbsoluteUrl) {
        this.globals = globals;
        this.routes = routes;
        this.useAbsoluteUrl = useAbsoluteUrl;
    }

    public static String buildUrl(String path, Map<String, String> params) {
        try {
            StringBuilder buff = new StringBuilder();
            buff.append(BaseRouter.encodePath(path));
            boolean isFirst = true;
            params = new TreeMap<String, String>(params);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().isEmpty() || entry.getValue().isEmpty()) continue;
                if (isFirst) {
                    buff.append("?");
                    isFirst = false;
                } else {
                    buff.append("&");
                }
                buff.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                buff.append("=");
                buff.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return buff.toString();
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildUrl(String path) {
        return BaseRouter.buildUrl(path, new HashMap<String, String>());
    }

    public static List<String> parseWithRe(String string, String re) {
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(string);
        ArrayList<String> results = new ArrayList<String>();
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); ++i) {
                results.add(matcher.group(i));
            }
        }
        return results;
    }

    public static Pattern routeToRe(String route) {
        String optionalParam = "\\((.*?)\\)";
        String namedParam = "(\\(\\?)?:\\w+";
        String splatParam = "\\*\\w+";
        String escapeRegExp = "[\\-{}\\[\\]+?.,\\\\\\^$|#\\s]";
        String routeRe = route.replaceAll(escapeRegExp, "\\\\$0").replaceAll(optionalParam, "(?:$1)?").replaceAll(namedParam, "([^/?]+)").replaceAll(splatParam, "([^?]*?)") + "$";
        return Pattern.compile(routeRe);
    }

    public static String interpolate(String string, String ... args) {
        return String.format(string.replaceAll(":[A-Za-z0-9]+", "%s"), args);
    }

    public static String joinWithSlashWithoutEmpty(String ... parts) {
        StringBuilder b = new StringBuilder();
        boolean isFirst = true;
        for (int i = 0; i < parts.length; ++i) {
            if (!StringUtils.isEmpty((CharSequence)parts[i])) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    b.append("/");
                }
                b.append(parts[i]);
                continue;
            }
            if (!isFirst) continue;
            b.append("/");
        }
        return b.toString();
    }

    private static String encodePath(String unencodedPath) {
        if (unencodedPath == null || unencodedPath.isEmpty()) {
            return unencodedPath;
        }
        try {
            if (unencodedPath.startsWith("/")) {
                return new URI(null, null, null, -1, unencodedPath, null, null).toASCIIString();
            }
            return new URI(null, null, null, -1, "/".concat(unencodedPath), null, null).toASCIIString().substring(1);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Controller createController(Globals var1, Request var2, Response var3);

    public void addRoute(String route, Handler handler) {
        this.routes.add(new Route(BaseRouter.routeToRe(route), handler));
    }

    @Deprecated
    public boolean canDispatch(String path) {
        if (!this.isUrlTooLong(path)) {
            for (Route route : this.routes) {
                if (!route.routeRe.matcher(path).find()) continue;
                return true;
            }
        }
        return false;
    }

    public void dispatch(Request request, Response response) {
        String path = request.getPath();
        if (!this.isUrlTooLong(path)) {
            for (Route route : this.routes) {
                Matcher matcher = route.routeRe.matcher(path);
                if (!matcher.find()) continue;
                ArrayList<String> matches = new ArrayList<String>();
                for (int i = 1; i <= matcher.groupCount(); ++i) {
                    matches.add(matcher.group(i));
                }
                String[] args = matches.toArray(new String[matches.size()]);
                Controller controller = this.createController(this.globals, request, response);
                this.callHandler(route.handler, controller, request, response, args);
                return;
            }
        }
        throw new RuntimeException("no route for " + path);
    }

    protected boolean isUrlTooLong(String url) {
        return url.length() > 8192;
    }

    protected void callHandler(Handler handler, Controller controller, Request request, Response response, String ... args) {
        if (args.length == 0) {
            handler.apply(controller);
        } else if (args.length == 1) {
            handler.apply(controller, args[0]);
        } else if (args.length == 2) {
            handler.apply(controller, args[0], args[1]);
        } else if (args.length == 3) {
            handler.apply(controller, args[0], args[1], args[2]);
        }
        handler.apply(request, response, controller, args);
    }

    protected class Route {
        Pattern routeRe;
        Handler handler;

        public Route(Pattern routeRe, Handler handler) {
            this.routeRe = routeRe;
            this.handler = handler;
        }
    }

    public abstract class Handler {
        public void apply(Controller controller) {
        }

        public void apply(Controller controller, String arg) {
        }

        public void apply(Controller controller, String arg1, String arg2) {
        }

        public void apply(Controller controller, String arg1, String arg2, String arg3) {
        }

        public void apply(Request request, Response response, Controller controller, String[] arguments) {
        }
    }
}


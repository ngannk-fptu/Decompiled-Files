/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.velocity.tools.view;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.tools.generic.ValueParser;
import org.apache.velocity.tools.view.ServletUtils;

public class LinkTool
extends org.apache.velocity.tools.generic.LinkTool {
    public static final String INCLUDE_REQUEST_PARAMS_KEY = "includeRequestParams";
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected boolean includeRequestParams = false;

    @Override
    protected void configure(ValueParser props) {
        super.configure(props);
        this.request = (HttpServletRequest)props.getValue("request");
        Boolean incParams = props.getBoolean(INCLUDE_REQUEST_PARAMS_KEY);
        if (incParams != null) {
            this.setIncludeRequestParams(incParams);
        }
        this.response = (HttpServletResponse)props.getValue("response");
        this.setCharacterEncoding(this.response.getCharacterEncoding());
        this.setFromRequest(this.request);
    }

    protected void setFromRequest(HttpServletRequest request) {
        this.setScheme(request.getScheme());
        this.setPort(request.getServerPort());
        this.setHost(request.getServerName());
        String ctx = request.getContextPath();
        String pth = ServletUtils.getPath(request);
        this.setPath(this.combinePath(ctx, pth));
        if (this.includeRequestParams) {
            this.setQuery(request.getParameterMap());
        }
    }

    public void setIncludeRequestParams(boolean includeRequestParams) {
        this.includeRequestParams = includeRequestParams;
    }

    public LinkTool addRequestParams(Object ... butOnlyThese) {
        return this.addRequestParams(false, butOnlyThese);
    }

    public LinkTool addRequestParamsExcept(Object ... ignoreThese) {
        return this.addRequestParams(true, ignoreThese);
    }

    public LinkTool addMissingRequestParams(Object ... ignoreThese) {
        Object[] these;
        if (this.query != null && !this.query.isEmpty()) {
            int i;
            Set keys = this.query.keySet();
            int size = keys.size();
            if (null != ignoreThese) {
                size += ignoreThese.length;
            }
            these = new Object[size];
            if (null != ignoreThese) {
                for (i = 0; i < ignoreThese.length; ++i) {
                    these[i] = ignoreThese[i];
                }
            }
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                these[i] = String.valueOf(iter.next());
                ++i;
            }
        } else {
            these = ignoreThese;
        }
        return this.addRequestParams(true, these);
    }

    private LinkTool addRequestParams(boolean ignore, Object ... special) {
        LinkTool copy = (LinkTool)this.duplicate(true);
        Map reqParams = this.request.getParameterMap();
        boolean noSpecial = special == null || special.length == 0;
        Iterator iterator = reqParams.entrySet().iterator();
        while (iterator.hasNext()) {
            boolean isSpecial;
            Map.Entry e;
            Map.Entry entry = e = iterator.next();
            String key = String.valueOf(entry.getKey());
            boolean bl = isSpecial = !noSpecial && this.contains(special, key);
            if (!noSpecial && (!ignore || isSpecial) && (ignore || !isSpecial)) continue;
            copy.setParam(key, entry.getValue(), this.appendParams);
        }
        return copy;
    }

    private boolean contains(Object[] set, String name) {
        for (Object i : set) {
            if (!name.equals(i)) continue;
            return true;
        }
        return false;
    }

    protected boolean isPathChanged() {
        if (this.path == this.self.getPath()) {
            return false;
        }
        if (this.path == null) {
            return true;
        }
        return !this.path.equals(this.self.getPath());
    }

    @Override
    public String getContextPath() {
        if (!this.isPathChanged()) {
            return this.request.getContextPath();
        }
        if (this.path == null || this.opaque) {
            return null;
        }
        int firstInternalSlash = this.path.indexOf(47, 1);
        if (firstInternalSlash <= 0) {
            return this.path;
        }
        return this.path.substring(0, firstInternalSlash);
    }

    public String getRequestPath() {
        if (this.path == null || this.opaque) {
            return null;
        }
        if (!this.isPathChanged()) {
            return ServletUtils.getPath(this.request);
        }
        int firstInternalSlash = this.path.indexOf(47, 1);
        if (firstInternalSlash <= 0) {
            return this.path;
        }
        return this.path.substring(firstInternalSlash, this.path.length());
    }

    public String getContextURL() {
        LinkTool copy = (LinkTool)this.duplicate();
        copy.setQuery(null);
        copy.setFragment(null);
        copy.setPath(this.getContextPath());
        return copy.toString();
    }

    @Override
    public String toString() {
        String str = super.toString();
        if (str.length() == 0) {
            return str;
        }
        return this.response.encodeURL(str);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.view;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.PagerTool;

@DefaultKey(value="search")
@InvalidScope(value={"application", "session"})
public abstract class AbstractSearchTool
extends PagerTool {
    public static final String DEFAULT_CRITERIA_KEY = "find";
    protected static final String STORED_RESULTS_KEY = StoredResults.class.getName();
    protected Log LOG;
    private String criteriaKey = "find";
    private Object criteria;

    public void setLog(Log log) {
        if (log == null) {
            throw new NullPointerException("log should not be set to null");
        }
        this.LOG = log;
    }

    @Override
    public void setup(HttpServletRequest request) {
        super.setup(request);
        String findMe = request.getParameter(this.getCriteriaKey());
        if (findMe != null) {
            this.setCriteria(findMe);
        }
    }

    public void setCriteriaKey(String key) {
        this.criteriaKey = key;
    }

    public String getCriteriaKey() {
        return this.criteriaKey;
    }

    @Override
    public void reset() {
        super.reset();
        this.setCriteria(null);
    }

    public void setCriteria(Object criteria) {
        this.criteria = criteria;
    }

    public Object getCriteria() {
        return this.criteria;
    }

    @Override
    public List getItems() {
        Object findMe = this.getCriteria();
        if (findMe == null) {
            return Collections.EMPTY_LIST;
        }
        List list = super.getItems();
        assert (list != null);
        if (list.isEmpty()) {
            block6: {
                try {
                    list = this.executeQuery(findMe);
                }
                catch (Throwable t) {
                    if (this.LOG == null) break block6;
                    this.LOG.error((Object)("AbstractSearchTool: executeQuery(" + findMe + ") failed"), t);
                }
            }
            if (list == null) {
                list = Collections.EMPTY_LIST;
            }
            this.setItems(list);
        }
        return list;
    }

    @Override
    protected List getStoredItems() {
        StoredResults sr = this.getStoredResults();
        if (sr != null && this.getCriteria().equals(sr.getCriteria())) {
            return sr.getList();
        }
        return null;
    }

    @Override
    protected void setStoredItems(List items) {
        this.setStoredResults(new StoredResults(this.getCriteria(), items));
    }

    protected abstract List executeQuery(Object var1);

    protected StoredResults getStoredResults() {
        if (this.session != null) {
            return (StoredResults)this.session.getAttribute(STORED_RESULTS_KEY);
        }
        return null;
    }

    protected void setStoredResults(StoredResults results) {
        if (this.session != null) {
            this.session.setAttribute(STORED_RESULTS_KEY, (Object)results);
        }
    }

    public static class StoredResults
    implements Serializable {
        private static final long serialVersionUID = 4503130168585978169L;
        private final transient Object crit;
        private final transient List list;

        public StoredResults(Object crit, List list) {
            this.crit = crit;
            this.list = list;
        }

        public Object getCriteria() {
            return this.crit;
        }

        public List getList() {
            return this.list;
        }
    }
}


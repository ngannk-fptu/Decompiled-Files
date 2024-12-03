/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.apache.velocity.tools.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.ParameterTool;

@DefaultKey(value="pager")
@InvalidScope(value={"application", "session"})
public class PagerTool {
    public static final String DEFAULT_NEW_ITEMS_KEY = "new.items";
    public static final String DEFAULT_INDEX_KEY = "index";
    public static final String DEFAULT_ITEMS_PER_PAGE_KEY = "show";
    public static final String DEFAULT_SLIP_SIZE_KEY = "slipSize";
    public static final int DEFAULT_ITEMS_PER_PAGE = 10;
    public static final int DEFAULT_SLIP_SIZE = 20;
    protected static final String STORED_ITEMS_KEY = PagerTool.class.getName();
    private String newItemsKey = "new.items";
    private String indexKey = "index";
    private String itemsPerPageKey = "show";
    private String slipSizeKey = "slipSize";
    private boolean createSession = false;
    private List items;
    private int index = 0;
    private int slipSize = 20;
    private int itemsPerPage = 10;
    protected HttpSession session;

    public void setRequest(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("request should not be null");
        }
        this.session = request.getSession(this.getCreateSession());
        this.setup(request);
    }

    public void setup(HttpServletRequest request) {
        List newItems;
        int slipSize;
        int show;
        ParameterTool params = new ParameterTool((ServletRequest)request);
        int index = params.getInt(this.getIndexKey(), -1);
        if (index >= 0) {
            this.setIndex(index);
        }
        if ((show = params.getInt(this.getItemsPerPageKey(), 0)) > 0) {
            this.setItemsPerPage(show);
        }
        if ((slipSize = params.getInt(this.getSlipSizeKey(), 0)) > 0) {
            this.setSlipSize(slipSize);
        }
        if ((newItems = (List)request.getAttribute(this.getNewItemsKey())) != null) {
            this.setItems(newItems);
        }
    }

    public void setNewItemsKey(String key) {
        this.newItemsKey = key;
    }

    public String getNewItemsKey() {
        return this.newItemsKey;
    }

    public void setIndexKey(String key) {
        this.indexKey = key;
    }

    public String getIndexKey() {
        return this.indexKey;
    }

    public void setItemsPerPageKey(String key) {
        this.itemsPerPageKey = key;
    }

    public String getItemsPerPageKey() {
        return this.itemsPerPageKey;
    }

    public void setSlipSizeKey(String key) {
        this.slipSizeKey = key;
    }

    public String getSlipSizeKey() {
        return this.slipSizeKey;
    }

    public void setCreateSession(boolean createSession) {
        this.createSession = createSession;
    }

    public boolean getCreateSession() {
        return this.createSession;
    }

    public void reset() {
        this.items = null;
        this.index = 0;
        this.itemsPerPage = 10;
    }

    public void setItems(List items) {
        this.items = items;
        this.setStoredItems(items);
    }

    public void setIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        this.index = index;
    }

    public void setItemsPerPage(int itemsPerPage) {
        if (itemsPerPage < 1) {
            itemsPerPage = 10;
        }
        this.itemsPerPage = itemsPerPage;
    }

    public void setSlipSize(int slipSize) {
        if (slipSize < 2) {
            slipSize = 20;
        }
        this.slipSize = slipSize;
    }

    public int getItemsPerPage() {
        return this.itemsPerPage;
    }

    public int getSlipSize() {
        return this.slipSize;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean hasItems() {
        return !this.getItems().isEmpty();
    }

    public List getItems() {
        if (this.items == null) {
            this.items = this.getStoredItems();
        }
        return this.items != null ? this.items : Collections.EMPTY_LIST;
    }

    public Integer getLastIndex() {
        if (!this.hasItems()) {
            return null;
        }
        return Math.min(this.getTotal() - 1, this.index + this.itemsPerPage - 1);
    }

    public Integer getNextIndex() {
        int next = this.index + this.itemsPerPage;
        if (next < this.getTotal()) {
            return next;
        }
        return null;
    }

    public Integer getFirstIndex() {
        if (!this.hasItems()) {
            return null;
        }
        return Math.min(this.getTotal() - 1, this.index);
    }

    public Integer getPrevIndex() {
        int prev = Math.min(this.index, this.getTotal()) - this.itemsPerPage;
        if (this.index > 0) {
            return Math.max(0, prev);
        }
        return null;
    }

    public int getPagesAvailable() {
        return (int)Math.ceil((double)this.getTotal() / (double)this.itemsPerPage);
    }

    public List getPage() {
        if (!this.hasItems()) {
            return null;
        }
        int start = this.getFirstIndex();
        int end = this.getLastIndex() + 1;
        return this.getItems().subList(start, end);
    }

    public Integer getPageNumber(int i) {
        if (!this.hasItems()) {
            return null;
        }
        return 1 + i / this.itemsPerPage;
    }

    public Integer getPageNumber() {
        return this.getPageNumber(this.index);
    }

    public int getTotal() {
        if (!this.hasItems()) {
            return 0;
        }
        return this.getItems().size();
    }

    public String getPageDescription() {
        int total;
        if (!this.hasItems()) {
            return "0 of 0";
        }
        StringBuilder out = new StringBuilder();
        int first = this.getFirstIndex() + 1;
        if (first >= (total = this.getTotal())) {
            out.append(total);
            out.append(" of ");
            out.append(total);
        } else {
            int last = this.getLastIndex() + 1;
            out.append(first);
            out.append(" - ");
            out.append(last);
            out.append(" of ");
            out.append(total);
        }
        return out.toString();
    }

    public List getSlip() {
        int totalPgs = this.getPagesAvailable();
        if (totalPgs <= 1) {
            return Collections.EMPTY_LIST;
        }
        int curPg = this.getPageNumber() - 1;
        int slipStart = Math.max(0, curPg - this.slipSize / 2);
        int slipEnd = Math.min(totalPgs, slipStart + this.slipSize);
        if (slipEnd - slipStart < this.slipSize) {
            slipStart = Math.max(0, slipEnd - this.slipSize);
        }
        ArrayList<Integer> slip = new ArrayList<Integer>(slipEnd - slipStart);
        for (int i = slipStart; i < slipEnd; ++i) {
            slip.add(i * this.itemsPerPage);
        }
        return slip;
    }

    protected List getStoredItems() {
        if (this.session != null) {
            return (List)this.session.getAttribute(STORED_ITEMS_KEY);
        }
        return null;
    }

    protected void setStoredItems(List items) {
        if (this.session != null) {
            this.session.setAttribute(STORED_ITEMS_KEY, (Object)items);
        }
    }
}


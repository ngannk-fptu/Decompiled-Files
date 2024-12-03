/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPropertyManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mail.MailException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.eventmacro;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.compat.setup.xstream.XStreamManagerCompat;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.extra.calendar3.eventmacro.DuplicateReplyException;
import com.atlassian.confluence.extra.calendar3.eventmacro.Reply;
import com.atlassian.confluence.extra.calendar3.eventmacro.ReplyDetailsPermissionException;
import com.atlassian.confluence.extra.calendar3.eventmacro.events.WaitingAttendantPromoted;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mail.MailException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EventMacroManager {
    private static final Logger log = LoggerFactory.getLogger(EventMacroManager.class);
    private static final String KEY = "event";
    private static final String KEY_WAITING_LIST = "event.waiting_list";
    static final String KEY_ALLOW_ANON = "event.allow_anon";
    static final String KEY_REPLY_LIMIT = "event.reply_limit";
    static final String KEY_HIDE_REPLIES = "event.hide_replies";
    static final String KEY_OCCURRENCE = "event.occurrence";
    static final String KEY_ENABLE_WAITING_LIST = "event.enable_waiting_list";
    static final String KEY_TITLE = "event.title";
    private final ContentPropertyManager contentPropertyManager;
    private final PermissionManager permissionManager;
    private final ClusterLockService clusterLockService;
    private final EventPublisher eventPublisher;
    private final XStreamManagerCompat xStreamManagerCompat;
    private static final String CLUSTER_LOCK_KEY = "RSVP-Plugin.executionlock";

    @Autowired
    public EventMacroManager(@ComponentImport ContentPropertyManager contentPropertyManager, @ComponentImport PermissionManager permissionManager, @ComponentImport ClusterLockService clusterLockService, @ComponentImport EventPublisher eventPublisher, @Qualifier(value="xStreamManagerCompat") XStreamManagerCompat xStreamManagerCompat) {
        this.contentPropertyManager = contentPropertyManager;
        this.permissionManager = permissionManager;
        this.clusterLockService = clusterLockService;
        this.eventPublisher = eventPublisher;
        this.xStreamManagerCompat = xStreamManagerCompat;
    }

    public List<Reply> checkedGetReplyList(ContentEntityObject contentObject, String occurrence) throws ReplyDetailsPermissionException {
        if (!this.canSeeReplyDetails(contentObject, occurrence)) {
            throw new ReplyDetailsPermissionException("You are not permitted to see the reply details.");
        }
        return this.getReplyList(contentObject, occurrence, ReplyType.ATTENDANCE);
    }

    private boolean canSeeReplyDetails(ContentEntityObject contentObject, String occurrence) {
        return !BooleanUtils.toBoolean(this.contentPropertyManager.getStringProperty(contentObject, EventMacroManager.getKeyForOccurrence(KEY_HIDE_REPLIES, occurrence))) || this.isMacroAdmin(contentObject);
    }

    public List<Reply> getReplyList(ContentEntityObject contentObject, String occurrence, ReplyType replyType) {
        String listAsXml = this.contentPropertyManager.getTextProperty(contentObject, this.getKeyForOccurrence(occurrence, replyType));
        if (listAsXml == null) {
            return new ArrayList<Reply>(Collections.EMPTY_LIST);
        }
        return (List)this.xStreamManagerCompat.fromXML(listAsXml);
    }

    public Reply checkedGetReply(ContentEntityObject contentObject, String occurrence, long id) throws ReplyDetailsPermissionException {
        List<Reply> replies = this.checkedGetReplyList(contentObject, occurrence);
        for (Reply reply : replies) {
            if (id != reply.getId()) continue;
            return reply;
        }
        return null;
    }

    public Reply getReply(ContentEntityObject contentObject, String occurrence, long id) {
        List<Reply> replies = this.getReplyList(contentObject, occurrence, ReplyType.ATTENDANCE);
        for (Reply reply : replies) {
            if (id != reply.getId()) continue;
            return reply;
        }
        return null;
    }

    private String getKeyForOccurrence(String occurrence, ReplyType replyType) {
        return "default".equals(occurrence) ? replyType.key() : replyType.key() + ":" + occurrence;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Reply updateReply(ContentEntityObject contentEntityObject, long id, String name, String email, boolean isConfirm, String occurrence) {
        ClusterLock lock = this.clusterLockService.getLockForName(CLUSTER_LOCK_KEY);
        try {
            lock.lock();
            List<Reply> replies = this.getReplyList(contentEntityObject, occurrence, ReplyType.ATTENDANCE);
            Reply reply = this.getReplyFromListById(id, replies);
            if (reply == null) {
                Reply reply2 = null;
                return reply2;
            }
            if (email != null) {
                reply.setEmail(email);
            }
            reply.setConfirm(isConfirm);
            if (name != null) {
                reply.setName(name);
            }
            this.saveReplyList(contentEntityObject, replies, occurrence, ReplyType.ATTENDANCE);
            Reply reply3 = reply;
            return reply3;
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private void saveReplyList(ContentEntityObject contentObject, List<Reply> replyList, String occurrence, ReplyType replyType) {
        String objAsXml = this.xStreamManagerCompat.toXML(replyList);
        this.contentPropertyManager.setTextProperty(contentObject, this.getKeyForOccurrence(occurrence, replyType), objAsXml);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Reply removeFromList(ContentEntityObject entity, long id, String occurrence) throws Exception {
        ClusterLock lock = this.clusterLockService.getLockForName(CLUSTER_LOCK_KEY);
        try {
            Reply waiting;
            List<Reply> waitingList;
            lock.lock();
            Reply promotedReply = null;
            boolean waitingListEnabled = this.isWaitingListEnabled(entity, occurrence);
            List<Reply> replyList = this.getReplyList(entity, occurrence, ReplyType.ATTENDANCE);
            Reply reply = this.getReplyFromListById(id, replyList);
            if (reply != null) {
                this.checkPermissionToRemoveReply(entity, reply);
                replyList.remove(reply);
                if (waitingListEnabled && !(waitingList = this.getReplyList(entity, occurrence, ReplyType.WAITING_LIST)).isEmpty()) {
                    promotedReply = waitingList.remove(0);
                    promotedReply.setInWaitingList(false);
                    replyList.add(promotedReply);
                    this.saveReplyList(entity, waitingList, occurrence, ReplyType.WAITING_LIST);
                }
                this.saveReplyList(entity, replyList, occurrence, ReplyType.ATTENDANCE);
            } else if (waitingListEnabled && (waiting = this.getReplyFromListById(id, waitingList = this.getReplyList(entity, occurrence, ReplyType.WAITING_LIST))) != null) {
                this.checkPermissionToRemoveReply(entity, waiting);
                waitingList.remove(waiting);
                this.saveReplyList(entity, waitingList, occurrence, ReplyType.WAITING_LIST);
            }
            if (promotedReply != null) {
                this.notifyPromotedResponders(entity, occurrence, (List<Reply>)ImmutableList.of(promotedReply));
            }
            Reply reply2 = promotedReply;
            return reply2;
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private Reply getReplyFromListById(long id, List<Reply> replyList) {
        return (Reply)Iterables.find(replyList, reply -> reply.getId() == id, null);
    }

    private Reply getReplyFromListByNameAndEmail(String name, String email, List<Reply> replyList) {
        return (Reply)Iterables.find(replyList, reply -> this.getRawEmailAddress((Reply)reply).equals(email) && reply.getName().equals(name), null);
    }

    private void checkPermissionToRemoveReply(ContentEntityObject entity, Reply reply) throws Exception {
        if (!(this.isMacroAdmin(entity) || reply.getUserName() != null && reply.getUserName().equals(AuthenticatedUserThreadLocal.getUsername()))) {
            throw new Exception("not_authorised");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void toggleConfirm(ContentEntityObject entity, long id, String occurrence) throws Exception {
        if (!this.isMacroAdmin(entity)) {
            throw new Exception("not_admin");
        }
        ClusterLock lock = this.clusterLockService.getLockForName(CLUSTER_LOCK_KEY);
        try {
            lock.lock();
            List<Reply> replyList = this.getReplyList(entity, occurrence, ReplyType.ATTENDANCE);
            Reply reply = replyList.get(replyList.indexOf(new Reply(id)));
            reply.setConfirm(!reply.isConfirm());
            this.saveReplyList(entity, replyList, occurrence, ReplyType.ATTENDANCE);
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Reply> getAndExpandReplyList(ContentEntityObject content, int replyLimit, Boolean waitingListEnabled, String occurrence) {
        ClusterLock lock = this.clusterLockService.getLockForName(CLUSTER_LOCK_KEY);
        try {
            List<Reply> waitingList;
            lock.lock();
            List<Reply> replyList = this.getReplyList(content, occurrence, ReplyType.ATTENDANCE);
            if (replyLimit <= 0 || !waitingListEnabled.booleanValue()) {
                List<Reply> list = replyList;
                return list;
            }
            if (replyLimit <= replyList.size()) {
                List<Reply> list = replyList;
                return list;
            }
            int spotsAvailable = replyLimit - replyList.size();
            if (spotsAvailable > 0 && !(waitingList = this.getReplyList(content, occurrence, ReplyType.WAITING_LIST)).isEmpty()) {
                ArrayList promotedReplies = Lists.newArrayListWithCapacity((int)spotsAvailable);
                while (spotsAvailable > 0 && !waitingList.isEmpty()) {
                    Reply reply = waitingList.remove(0);
                    reply.setInWaitingList(false);
                    promotedReplies.add(reply);
                    --spotsAvailable;
                }
                if (!promotedReplies.isEmpty()) {
                    replyList.addAll(promotedReplies);
                    this.saveReplyList(content, replyList, occurrence, ReplyType.ATTENDANCE);
                    this.saveReplyList(content, waitingList, occurrence, ReplyType.WAITING_LIST);
                    try {
                        this.notifyPromotedResponders(content, occurrence, promotedReplies);
                    }
                    catch (MailException e) {
                        log.error("Unable to notify promoted responders due to :" + e.getMessage(), (Throwable)e);
                    }
                }
            }
            List<Reply> list = replyList;
            return list;
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Reply addReply(ContentEntityObject entity, String name, String personalUrl, int guests, String comment, Map<String, String> customColumnsMap, Map<String, Boolean> customCheckboxesMap, String occurrence) throws Exception {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        assert (customColumnsMap != null);
        boolean allowAnon = Boolean.valueOf(this.contentPropertyManager.getStringProperty(entity, EventMacroManager.getKeyForOccurrence(KEY_ALLOW_ANON, occurrence)));
        if (user == null && !allowAnon) {
            throw new Exception("login");
        }
        String replyLimitString = this.contentPropertyManager.getStringProperty(entity, EventMacroManager.getKeyForOccurrence(KEY_REPLY_LIMIT, occurrence));
        int replyLimit = NumberUtils.toInt(replyLimitString, -1);
        ClusterLock lock = this.clusterLockService.getLockForName(CLUSTER_LOCK_KEY);
        try {
            Reply existingReply;
            lock.lock();
            long id = System.currentTimeMillis();
            ReplyType currentReplyType = ReplyType.ATTENDANCE;
            List<Reply> replyList = this.getReplyList(entity, occurrence, ReplyType.ATTENDANCE);
            List<Reply> waitingList = null;
            int currentNumReplies = replyList.size();
            if (replyLimit != -1 && currentNumReplies >= replyLimit) {
                boolean isWaitingListEnabled = this.isWaitingListEnabled(entity, occurrence);
                if (!isWaitingListEnabled) {
                    throw new Exception("over_reply_limit");
                }
                currentReplyType = ReplyType.WAITING_LIST;
                waitingList = this.getReplyList(entity, occurrence, ReplyType.WAITING_LIST);
            }
            if ((existingReply = this.getReplyFromListByNameAndEmail(name, personalUrl, replyList)) == null && waitingList != null) {
                existingReply = this.getReplyFromListByNameAndEmail(name, personalUrl, waitingList);
            }
            if (existingReply != null) {
                throw new DuplicateReplyException();
            }
            Reply newReply = new Reply(id, name, personalUrl, guests, comment, customColumnsMap, customCheckboxesMap, user, currentReplyType == ReplyType.WAITING_LIST);
            if (currentReplyType == ReplyType.ATTENDANCE) {
                replyList.add(newReply);
                this.saveReplyList(entity, replyList, occurrence, currentReplyType);
            } else {
                waitingList.add(newReply);
                this.saveReplyList(entity, waitingList, occurrence, currentReplyType);
            }
            Reply reply = newReply;
            return reply;
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        try {
            new InternetAddress(email, true);
        }
        catch (AddressException e) {
            return false;
        }
        return true;
    }

    public String extractEmails(ContentEntityObject contentObject, String occurrence) throws Exception {
        if (!this.isMacroAdmin(contentObject)) {
            throw new Exception("not_admin");
        }
        List<Reply> replyList = this.getReplyList(contentObject, occurrence, ReplyType.ATTENDANCE);
        StringBuilder emails = new StringBuilder();
        for (Reply reply : replyList) {
            String email = reply.getEmail();
            if (!this.isValidEmail(email)) continue;
            emails.append(email).append(",");
        }
        return emails.toString();
    }

    public void getCSVText(ContentEntityObject entity, String occurrence, Writer writer) throws Exception {
        if (!this.isMacroAdmin(entity)) {
            throw new Exception("not_admin");
        }
        List<Reply> replyList = this.getReplyList(entity, occurrence, ReplyType.ATTENDANCE);
        writer.append("Name,Guests,Email,Comment");
        if (replyList.size() < 1) {
            return;
        }
        for (String customValueName : replyList.get(0).getCustomValues().keySet()) {
            writer.append("," + customValueName);
        }
        for (String customCheckboxName : replyList.get(0).getCustomCheckboxes().keySet()) {
            writer.append("," + customCheckboxName);
        }
        writer.append("\n");
        for (Reply reply : replyList) {
            writer.append(reply.getName());
            writer.append("," + reply.getGuests());
            writer.append("," + this.getRawEmailAddress(reply));
            writer.append((CharSequence)(reply.getComment() == null ? "" : "," + reply.getComment()));
            for (String customValue : reply.getCustomValues().values()) {
                writer.append("," + customValue);
            }
            Iterator<Object> iterator = reply.getCustomCheckboxes().values().iterator();
            while (iterator.hasNext()) {
                boolean checkboxValue = (Boolean)iterator.next();
                writer.append("," + String.valueOf(checkboxValue));
            }
            writer.append("\n");
        }
    }

    public String getRawEmailAddress(Reply reply) {
        return this.getRawEmailAddress(reply.getEmail());
    }

    public String getRawEmailAddress(String personalUrl) {
        if (StringUtils.isNotBlank(personalUrl) && personalUrl.length() > 7 && personalUrl.substring(0, 7).equals("mailto:")) {
            return personalUrl.substring(7);
        }
        return personalUrl;
    }

    public boolean isMacroAdmin(ContentEntityObject entity) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)entity);
    }

    public int getNumResponders(ContentEntityObject page, String occurrence) {
        return this.getReplyList(page, occurrence, ReplyType.ATTENDANCE).size();
    }

    public static String getKeyForOccurrence(String keyType, String occurrence) {
        return keyType + ":" + occurrence;
    }

    public void setAllowAnonymous(ContentEntityObject contentEntityObject, String occurrence, boolean allowAnonymous) {
        this.contentPropertyManager.setStringProperty(contentEntityObject, EventMacroManager.getKeyForOccurrence(KEY_ALLOW_ANON, occurrence), String.valueOf(allowAnonymous));
    }

    public void setReplyLimit(ContentEntityObject contentEntityObject, String occurrence, int replyLimit) {
        if (replyLimit >= 0) {
            this.contentPropertyManager.setStringProperty(contentEntityObject, EventMacroManager.getKeyForOccurrence(KEY_REPLY_LIMIT, occurrence), String.valueOf(replyLimit));
        }
    }

    public void setHideReplies(ContentEntityObject contentEntityObject, String occurrence, boolean hideReplies) {
        this.contentPropertyManager.setStringProperty(contentEntityObject, EventMacroManager.getKeyForOccurrence(KEY_HIDE_REPLIES, occurrence), String.valueOf(hideReplies));
    }

    public void setWaitingListEnabled(ContentEntityObject contentEntityObject, String occurrence, boolean waitingListEnabled) {
        this.contentPropertyManager.setStringProperty(contentEntityObject, EventMacroManager.getKeyForOccurrence(KEY_ENABLE_WAITING_LIST, occurrence), String.valueOf(waitingListEnabled));
    }

    public void setEventTitle(ContentEntityObject contentEntityObject, String occurrence, String title) {
        this.contentPropertyManager.setStringProperty(contentEntityObject, EventMacroManager.getKeyForOccurrence(KEY_TITLE, occurrence), String.valueOf(title));
    }

    private Boolean isWaitingListEnabled(ContentEntityObject entity, String occurrence) {
        return Boolean.valueOf(this.contentPropertyManager.getStringProperty(entity, EventMacroManager.getKeyForOccurrence(KEY_ENABLE_WAITING_LIST, occurrence)));
    }

    private void notifyPromotedResponders(ContentEntityObject contentEntityObject, String occurrence, List<Reply> promotedReplies) throws MailException {
        String eventTitle = this.contentPropertyManager.getStringProperty(contentEntityObject, EventMacroManager.getKeyForOccurrence(KEY_TITLE, occurrence));
        this.eventPublisher.publish((Object)new WaitingAttendantPromoted(eventTitle, contentEntityObject.getUrlPath(), promotedReplies));
    }

    public static enum ReplyType {
        ATTENDANCE("event"),
        WAITING_LIST("event.waiting_list");

        private final String key;

        private ReplyType(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }
    }
}


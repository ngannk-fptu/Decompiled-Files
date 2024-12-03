/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.json.JSONParser
 */
package org.apache.catalina.tribes.membership.cloud;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.catalina.tribes.membership.cloud.CertificateStreamProvider;
import org.apache.catalina.tribes.membership.cloud.CloudMembershipProvider;
import org.apache.catalina.tribes.membership.cloud.CloudMembershipService;
import org.apache.catalina.tribes.membership.cloud.TokenStreamProvider;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.json.JSONParser;

public class KubernetesMembershipProvider
extends CloudMembershipProvider {
    private static final Log log = LogFactory.getLog(KubernetesMembershipProvider.class);

    @Override
    public void start(int level) throws Exception {
        String ver;
        if ((level & 4) == 0) {
            return;
        }
        super.start(level);
        String namespace = this.getNamespace();
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("Namespace [%s] set; clustering enabled", namespace));
        }
        String protocol = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_MASTER_PROTOCOL", "KUBERNETES_MASTER_PROTOCOL");
        String masterHost = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_MASTER_HOST", "KUBERNETES_SERVICE_HOST");
        String masterPort = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_MASTER_PORT", "KUBERNETES_SERVICE_PORT");
        String clientCertificateFile = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_CLIENT_CERT_FILE", "KUBERNETES_CLIENT_CERTIFICATE_FILE");
        String caCertFile = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_CA_CERT_FILE", "KUBERNETES_CA_CERTIFICATE_FILE");
        if (caCertFile == null) {
            caCertFile = "/var/run/secrets/kubernetes.io/serviceaccount/ca.crt";
        }
        if (clientCertificateFile == null) {
            String saTokenFile;
            if (protocol == null) {
                protocol = "https";
            }
            if ((saTokenFile = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_SA_TOKEN_FILE", "SA_TOKEN_FILE")) == null) {
                saTokenFile = "/var/run/secrets/kubernetes.io/serviceaccount/token";
            }
            try {
                byte[] bytes = Files.readAllBytes(FileSystems.getDefault().getPath(saTokenFile, new String[0]));
                this.streamProvider = new TokenStreamProvider(new String(bytes, StandardCharsets.US_ASCII), caCertFile);
            }
            catch (IOException e) {
                log.error((Object)sm.getString("kubernetesMembershipProvider.streamError"), (Throwable)e);
            }
        } else {
            String clientKeyFile;
            if (protocol == null) {
                protocol = "http";
            }
            if ((clientKeyFile = KubernetesMembershipProvider.getEnv("KUBERNETES_CLIENT_KEY_FILE")) == null) {
                log.error((Object)sm.getString("kubernetesMembershipProvider.noKey"));
                return;
            }
            String clientKeyPassword = KubernetesMembershipProvider.getEnv("KUBERNETES_CLIENT_KEY_PASSWORD");
            String clientKeyAlgo = KubernetesMembershipProvider.getEnv("KUBERNETES_CLIENT_KEY_ALGO");
            if (clientKeyAlgo == null) {
                clientKeyAlgo = "RSA";
            }
            this.streamProvider = new CertificateStreamProvider(clientCertificateFile, clientKeyFile, clientKeyPassword, clientKeyAlgo, caCertFile);
        }
        if ((ver = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_API_VERSION", "KUBERNETES_API_VERSION")) == null) {
            ver = "v1";
        }
        String labels = KubernetesMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_LABELS", "KUBERNETES_LABELS");
        namespace = URLEncoder.encode(namespace, "UTF-8");
        labels = labels == null ? null : URLEncoder.encode(labels, "UTF-8");
        this.url = String.format("%s://%s:%s/api/%s/namespaces/%s/pods", protocol, masterHost, masterPort, ver, namespace);
        if (labels != null && labels.length() > 0) {
            this.url = this.url + "?labelSelector=" + labels;
        }
        this.heartbeat();
    }

    @Override
    public boolean stop(int level) throws Exception {
        try {
            boolean bl = super.stop(level);
            return bl;
        }
        finally {
            this.streamProvider = null;
        }
    }

    @Override
    protected Member[] fetchMembers() {
        if (this.streamProvider == null) {
            return new Member[0];
        }
        ArrayList<MemberImpl> members = new ArrayList<MemberImpl>();
        try (InputStream stream = this.streamProvider.openStream(this.url, this.headers, this.connectionTimeout, this.readTimeout);
             InputStreamReader reader = new InputStreamReader(stream, "UTF-8");){
            this.parsePods(reader, members);
        }
        catch (IOException e) {
            log.error((Object)sm.getString("kubernetesMembershipProvider.streamError"), (Throwable)e);
        }
        return members.toArray(new Member[0]);
    }

    protected void parsePods(Reader reader, List<MemberImpl> members) {
        JSONParser parser = new JSONParser(reader);
        try {
            LinkedHashMap json = parser.object();
            Object itemsObject = json.get("items");
            if (!(itemsObject instanceof List)) {
                log.error((Object)sm.getString("kubernetesMembershipProvider.invalidPodsList", "no items"));
                return;
            }
            List items = (List)itemsObject;
            for (Object podObject : items) {
                String uid;
                if (!(podObject instanceof LinkedHashMap)) {
                    log.warn((Object)sm.getString("kubernetesMembershipProvider.invalidPod", "item"));
                    continue;
                }
                LinkedHashMap pod = (LinkedHashMap)podObject;
                Object podKindObject = pod.get("kind");
                if (podKindObject != null && !"Pod".equals(podKindObject)) continue;
                Object metadataObject = pod.get("metadata");
                if (!(metadataObject instanceof LinkedHashMap)) {
                    log.warn((Object)sm.getString("kubernetesMembershipProvider.invalidPod", "metadata"));
                    continue;
                }
                LinkedHashMap metadata = (LinkedHashMap)metadataObject;
                Object nameObject = metadata.get("name");
                if (nameObject == null) {
                    log.warn((Object)sm.getString("kubernetesMembershipProvider.invalidPod", "name"));
                    continue;
                }
                Object objectUid = metadata.get("uid");
                Object creationTimestampObject = metadata.get("creationTimestamp");
                if (creationTimestampObject == null) {
                    log.warn((Object)sm.getString("kubernetesMembershipProvider.invalidPod", "uid"));
                    continue;
                }
                Object statusObject = pod.get("status");
                if (!(statusObject instanceof LinkedHashMap)) {
                    log.warn((Object)sm.getString("kubernetesMembershipProvider.invalidPod", "status"));
                    continue;
                }
                LinkedHashMap status = (LinkedHashMap)statusObject;
                if (!"Running".equals(status.get("phase"))) continue;
                Object podIPObject = status.get("podIP");
                if (podIPObject == null) {
                    log.warn((Object)sm.getString("kubernetesMembershipProvider.invalidPod", "podIP"));
                    continue;
                }
                String podIP = podIPObject.toString();
                String string = uid = objectUid == null ? podIP : objectUid.toString();
                if (podIP.equals(this.localIp)) {
                    Member localMember = this.service.getLocalMember(false);
                    if (localMember.getUniqueId() != CloudMembershipService.INITIAL_ID || !(localMember instanceof MemberImpl)) continue;
                    byte[] id = this.md5.digest(uid.getBytes(StandardCharsets.US_ASCII));
                    ((MemberImpl)localMember).setUniqueId(id);
                    continue;
                }
                long aliveTime = Duration.between(Instant.parse(creationTimestampObject.toString()), this.startTime).toMillis();
                MemberImpl member = null;
                try {
                    member = new MemberImpl(podIP, this.port, aliveTime);
                }
                catch (IOException e) {
                    log.error((Object)sm.getString("kubernetesMembershipProvider.memberError"), (Throwable)e);
                    continue;
                }
                byte[] id = this.md5.digest(uid.getBytes(StandardCharsets.US_ASCII));
                member.setUniqueId(id);
                members.add(member);
            }
        }
        catch (Exception e) {
            log.error((Object)sm.getString("kubernetesMembershipProvider.jsonError"), (Throwable)e);
        }
    }
}


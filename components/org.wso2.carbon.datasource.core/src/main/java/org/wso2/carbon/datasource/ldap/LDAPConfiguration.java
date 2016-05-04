package org.wso2.carbon.datasource.ldap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 */
@XmlRootElement(name = "configuration")
public class LDAPConfiguration {


    private String url;
    private String username;
    private Password passwordPersist;
    private long connectionTimeout;
    private long readTimeout;
    private boolean connectionPoolingEnabled;
    private String referral;
    private String binaryAttribute;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        if (this.getPasswordPersist() == null) {
            this.passwordPersist = new Password();
        }
        this.passwordPersist.setValue(password);
    }

    @XmlTransient
    public String getPassword() {
        if (this.getPasswordPersist() != null) {
            return this.getPasswordPersist().getValue();
        } else {
            return null;
        }
    }

    @XmlElement(name = "password")
    public Password getPasswordPersist() {
        return passwordPersist;
    }

    public void setPasswordPersist(Password passwordPersist) {
        this.passwordPersist = passwordPersist;
    }

    /**
     * Bean class holding password.
     */
    @XmlRootElement(name = "password")
    public static class Password {

        private boolean encrypted = true;

        private String value;

        @XmlAttribute(name = "encrypted")
        public boolean isEncrypted() {
            return encrypted;
        }

        public void setEncrypted(boolean encrypted) {
            this.encrypted = encrypted;
        }

        @XmlValue
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    @XmlElement(name = "connectionTimeout")
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @XmlElement(name = "readTimeout")
    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    @XmlElement(name = "connectionPoolingEnabled")
    public boolean isConnectionPoolingEnabled() {
        return connectionPoolingEnabled;
    }

    public void setConnectionPoolingEnabled(boolean connectionPoolingEnabled) {
        this.connectionPoolingEnabled = connectionPoolingEnabled;
    }

    @XmlElement(name = "referral")
    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    @XmlElement(name = "binaryAttribute")
    public String getBinaryAttribute() {
        return binaryAttribute;
    }

    public void setBinaryAttribute(String binaryAttribute) {
        this.binaryAttribute = binaryAttribute;
    }
}

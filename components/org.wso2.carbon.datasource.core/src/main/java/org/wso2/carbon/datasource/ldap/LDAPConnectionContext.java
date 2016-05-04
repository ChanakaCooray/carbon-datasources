/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.datasource.ldap;

import org.osgi.service.jndi.JNDIContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.datasource.core.DataHolder;

import java.util.Hashtable;
import java.util.SortedMap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

/**
 *
 */
public class LDAPConnectionContext {

    private static Logger log = LoggerFactory.getLogger(LDAPConnectionContext.class);
    JNDIContextManager manager;
    @SuppressWarnings("rawtypes")
    private Hashtable environment;
    private SortedMap<Integer, SRVRecord> dcMap;
    //    private Hashtable environmentForDNS;

    //    private String DNSDomainName;
    private boolean readOnly = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public LDAPConnectionContext(LDAPConfiguration config) {

        manager = DataHolder.getDataHolderInstance().getJndiContextManager();

        //if DNS is enabled, populate DC Map
        //        String DNSUrl = config.getUserStoreProperty(LDAPConstants.DNS_URL);
        //        if (DNSUrl != null) {
        //            DNSDomainName = config.getUserStoreProperty(LDAPConstants.DNS_DOMAIN_NAME);
        //            if (DNSDomainName == null) {
        //                throw new DataSourceException("DNS is enabled, but DNS domain name not provided.");
        //            } else {
        //                environmentForDNS = new Hashtable();
        //                environmentForDNS.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        //                environmentForDNS.put("java.naming.provider.url", DNSUrl);
        //                populateDCMap();
        //            }
        //            //need to keep track of if the user store config is read only
        //                    String readOnlyString = config
        //                            .getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_READ_ONLY);
        //            if (readOnlyString != null) {
        //                readOnly = Boolean.parseBoolean(readOnlyString);
        //            }
        //        }

        String rawConnectionURL = config.getUrl();
        String connectionURL = config.getUrl();
        //if DNS enabled in AD case, this can be null
        //        if (rawConnectionURL != null) {
        //            String portInfo = rawConnectionURL.split(":")[2];
        //
        //            String port = null;
        //
        //            // if the port contains a template string that refers to carbon.xml
        //            if ((portInfo.contains("${")) && (portInfo.contains("}"))) {
        //                port = Integer.toString(CarbonUtils.getPortFromServerConfig(portInfo));
        //            }
        //
        //            if (port != null) {
        //                connectionURL = rawConnectionURL.replace(portInfo, port);
        //            } else {
        //                // if embedded-ldap is not enabled,
        //                connectionURL = config.getUserStoreProperty(LDAPConstants.CONNECTION_URL);
        //            }
        //        }

        String connectionName = config.getUsername();
        String connectionPassword = config.getPassword();

        if (log.isDebugEnabled()) {
            log.debug("Connection Name :: " + connectionName + ", Connection URL :: " + connectionURL);
        }

        environment = new Hashtable();

        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");

        /**
         * In carbon JNDI context we need to by pass specific tenant context and we need the base
         * context for LDAP operations.
         */
        environment.put(LDAPConstants.REQUEST_BASE_CONTEXT, "true");

        if (connectionName != null) {
            environment.put(Context.SECURITY_PRINCIPAL, connectionName);
        }

        if (connectionPassword != null) {
            environment.put(Context.SECURITY_CREDENTIALS, connectionPassword);
        }

        if (connectionURL != null) {
            environment.put(Context.PROVIDER_URL, connectionURL);
        }

        // Enable connection pooling if property is set in user-mgt.xml
        boolean isLDAPConnectionPoolingEnabled = config.isConnectionPoolingEnabled();

        environment.put("com.sun.jndi.ldap.connect.pool", isLDAPConnectionPoolingEnabled ? "true" : "false");

        // set referral status if provided in configuration.
        if (config.getReferral() != null) {
            environment.put("java.naming.referral", config.getReferral());
        }

        String binaryAttribute = config.getBinaryAttribute();

        if (binaryAttribute != null) {
            environment.put(LDAPConstants.LDAP_ATTRIBUTES_BINARY, binaryAttribute);
        }

        //Set connect timeout if provided in configuration. Otherwise set default value
        long connectTimeout = config.getConnectionTimeout();
        long readTimeout = config.getReadTimeout();
        if (connectTimeout != 0) {
            environment.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(connectTimeout));
        } else {
            environment.put("com.sun.jndi.ldap.connect.timeout", LDAPConstants.IDLE_TIME_OUT);
        }

        if (readTimeout != 0) {
            environment.put("com.sun.jndi.ldap.read.timeout", String.valueOf(readTimeout));
        }

        //pooling properties
//                System.setProperty("com.sun.jndi.ldap.connect.pool.authentication", "none");
//                System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "fine");
//                System.setProperty("com.sun.jndi.ldap.connect.pool.initsize", "10");
//                System.setProperty("com.sun.jndi.ldap.connect.pool.prefsize", "20");
//                System.setProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain");
//                System.setProperty("com.sun.jndi.ldap.connect.pool.timeout", "300000");
//                System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", "1");
    }

    public DirContext getContext() {
        DirContext context = null;
        //if dcMap is not populated, it is not DNS case
        if (dcMap == null) {
            try {
                context = (DirContext) manager.newInitialContext(environment);

            } catch (NamingException e) {
                log.error("Error obtaining connection. " + e.getMessage(), e);
            }
        } else if (dcMap != null && dcMap.size() != 0) {
            try {
                //first try the first entry in dcMap, if it fails, try iteratively
                Integer firstKey = dcMap.firstKey();
                SRVRecord firstRecord = dcMap.get(firstKey);
                //compose the connection URL
                environment.put(Context.PROVIDER_URL, getLDAPURLFromSRVRecord(firstRecord));
                context = (DirContext) manager.newInitialContext(environment);

            } catch (NamingException e) {
                log.error("Error obtaining connection to first Domain Controller." + e.getMessage(), e);
                log.info("Trying to connect with other Domain Controllers");

                for (Integer integer : dcMap.keySet()) {
                    try {
                        SRVRecord srv = dcMap.get(integer);
                        environment.put(Context.PROVIDER_URL, getLDAPURLFromSRVRecord(srv));
                        context = (DirContext) manager.newInitialContext(environment);
                        break;
                    } catch (NamingException e1) {
                        if (integer == (dcMap.lastKey())) {
                            log.error("Error obtaining connection for all " + integer + " Domain Controllers." + e
                                    .getMessage(), e);
                        }
                    }
                }
            }
        }
        return (context);

    }

    //    private void populateDCMap() throws DataSourceException {
    //        try {
    //            //get the directory context for DNS
    //            DirContext dnsContext = new InitialDirContext(environmentForDNS);
    //            //compose the DNS service to be queried
    //            String DNSServiceName = LDAPConstants.ACTIVE_DIRECTORY_DOMAIN_CONTROLLER_SERVICE + DNSDomainName;
    //            //query the DNS
    //            Attributes attributes = dnsContext
    //                    .getAttributes(DNSServiceName, new String[] { LDAPConstants.SRV_ATTRIBUTE_NAME });
    //            Attribute srvRecords = attributes.get(LDAPConstants.SRV_ATTRIBUTE_NAME);
    //            //there can be multiple records with same domain name - get them all
    //            NamingEnumeration srvValues = srvRecords.getAll();
    //            dcMap = new TreeMap<Integer, SRVRecord>();
    //            //extract all SRV Records for _ldap._tcp service under the specified domain and populate dcMap
    //            //int forcedPriority = 0;
    //            while (srvValues.hasMore()) {
    //                String value = srvValues.next().toString();
    //                SRVRecord srvRecord = new SRVRecord();
    //                String valueItems[] = value.split(" ");
    //                String priority = valueItems[0];
    //                if (priority != null) {
    //                    int priorityInt = Integer.parseInt(priority);
    //
    //                    /*if ((priorityInt == forcedPriority) || (priorityInt < forcedPriority)) {
    //                        forcedPriority++;
    //                        priorityInt = forcedPriority;
    //                    }*/
    //                    srvRecord.setPriority(priorityInt);
    //                }/* else {
    //                    forcedPriority++;
    //                    srvRecord.setPriority(forcedPriority);
    //                }*/
    //                String weight = valueItems[1];
    //                if (weight != null) {
    //                    srvRecord.setWeight(Integer.parseInt(weight));
    //                }
    //                String port = valueItems[2];
    //                if (port != null) {
    //                    srvRecord.setPort(Integer.parseInt(port));
    //                }
    //                String host = valueItems[3];
    //                if (host != null) {
    //                    srvRecord.setHostName(host);
    //                }
    //                //we index dcMap on priority basis, therefore, priorities must be different
    //                dcMap.put(srvRecord.getPriority(), srvRecord);
    //            }
    //            //iterate over the SRVRecords for Active Directory Domain Controllers and figure out the
    //            //host records for that
    //            for (SRVRecord srvRecord : dcMap.values()) {
    //                    Attributes hostAttributes = dnsContext
    //                            .getAttributes(srvRecord.getHostName(), new String[]
    //                                    { LDAPConstants.A_RECORD_ATTRIBUTE_NAME });
    //                Attribute hostRecord = hostAttributes.get(LDAPConstants.A_RECORD_ATTRIBUTE_NAME);
    //                //we know there is only one IP value for a given host. So we do just get, not getAll
    //                srvRecord.setHostIP((String) hostRecord.get());
    //            }
    //        } catch (NamingException e) {
    //            log.error("Error obtaining information from DNS Server" + e.getMessage(), e);
    //            throw new DataSourceException("Error obtaining information from DNS Server " + e.getMessage(), e);
    //        }
    //    }

    private String getLDAPURLFromSRVRecord(SRVRecord srvRecord) {
        String ldapURL = null;
        if (readOnly) {
            ldapURL = "ldap://" + srvRecord.getHostIP() + ":" + srvRecord.getPort();
        } else {
            ldapURL = "ldaps://" + srvRecord.getHostIP() + ":" + srvRecord.getPort();
        }
        return ldapURL;
    }
}

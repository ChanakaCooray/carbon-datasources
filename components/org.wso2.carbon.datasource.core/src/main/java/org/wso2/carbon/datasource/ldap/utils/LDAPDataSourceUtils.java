package org.wso2.carbon.datasource.ldap.utils;

import org.wso2.carbon.datasource.core.exception.DataSourceException;
import org.wso2.carbon.datasource.ldap.LDAPConfiguration;
import org.wso2.carbon.datasource.utils.DataSourceUtils;

/**
 *
 */
public class LDAPDataSourceUtils {

    /**
     * Generate the configuration bean by reading the xml configuration.
     *
     * @param xmlConfiguration String
     * @return {@code }
     * @throws DataSourceException
     */
    public static LDAPConfiguration buildConfiguration(String xmlConfiguration) throws DataSourceException {
        try {
            LDAPConfiguration config = DataSourceUtils.loadJAXBConfiguration(xmlConfiguration, LDAPConfiguration.class);

            //            Hashtable environment = new Hashtable();
            //            environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            //            environment.put(Context.SECURITY_AUTHENTICATION, "simple");
            //
            //            /**
            //             * In carbon JNDI context we need to by pass specific tenant context and we need the base
            //             * context for LDAP operations.
            //             */
            //            environment.put(LDAPConstants.REQUEST_BASE_CONTEXT, "true");
            //            String connectionURL = config.getUrl();
            //            String connectionName = config.getUsername();
            //            String connectionPassword = config.getPassword();
            //
            //            if (connectionName != null) {
            //                environment.put(Context.SECURITY_PRINCIPAL, connectionName);
            //            }
            //
            //            if (connectionPassword != null) {
            //                environment.put(Context.SECURITY_CREDENTIALS, connectionPassword);
            //            }
            //
            //            if (connectionURL != null) {
            //                environment.put(Context.PROVIDER_URL, connectionURL);
            //            }
            //
            //            // Enable connection pooling if property is set in user-mgt.xml
//                        boolean isLDAPConnectionPoolingEnabled = config.isConnectionPoolingEnabled();
//
//                        environment.put("com.sun.jndi.ldap.connect.pool",
//                                isLDAPConnectionPoolingEnabled ? "true" : "false");
//
//                        // set referral status if provided in configuration.
            //            if (config.getReferral() != null) {
            //                environment.put("java.naming.referral",
            //                        config.getReferral());
            //            }
            //
            //            String binaryAttribute = config.getBinaryAttribute();
            //
            //            if (binaryAttribute != null) {
            //                environment.put(LDAPConstants.LDAP_ATTRIBUTES_BINARY, binaryAttribute);
            //            }
            //
            //            //Set connect timeout if provided in configuration. Otherwise set default value
            //            long connectTimeout = config.getConnectionTimeout();
            //            long readTimeout = config.getReadTimeout();
            //            if (connectTimeout != 0) {
            //                environment.put("com.sun.jndi.ldap.connect.timeout", connectTimeout);
            //            } else {
            //                environment.put("com.sun.jndi.ldap.connect.timeout", LDAPConstants.IDLE_TIME_OUT);
            //            }
            //
            //            if(readTimeout != 0){
            //                environment.put("com.sun.jndi.ldap.read.timeout",readTimeout);
            //            }

            return config;
        } catch (DataSourceException e) {
            throw new DataSourceException("Error in loading LDAP configuration: " + e.getMessage(), e);
        }
    }

}

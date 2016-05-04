/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.datasource.ldap;

/**
 *
 */
public class LDAPConstants {

    private LDAPConstants(){

    }

    public static final String DRIVER_NAME = "com.sun.jndi.ldap.LdapCtxFactory";

    public static final String LDAP_ATTRIBUTES_BINARY = "java.naming.ldap.attributes.binary";
    public static final String REQUEST_BASE_CONTEXT = "org.wso2.carbon.context.RequestBaseContext";

    //DNS related constants
    public static final String ACTIVE_DIRECTORY_DOMAIN_CONTROLLER_SERVICE = "_ldap._tcp.";
    public static final String SRV_ATTRIBUTE_NAME = "SRV";
    public static final String A_RECORD_ATTRIBUTE_NAME = "A";
    public static final String DNS_URL = "URLOfDNS";
    public static final String DNS_DOMAIN_NAME = "DNSDomainName";
    public static final String CONNECTION_POOLING_ENABLED = "ConnectionPoolingEnabled";

    public static final String IDLE_TIME_OUT = "5000";

    public static final String LDAP_DATASOURCE_TYPE = "LDAP";
}

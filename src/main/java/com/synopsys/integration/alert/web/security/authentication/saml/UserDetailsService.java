/**
 * blackduck-alert
 *
 * Copyright (c) 2019 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
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
package com.synopsys.integration.alert.web.security.authentication.saml;

public class UserDetailsService {
    // TODO enable SAML support
    /*implements SAMLUserDetailsService {
    @Override
    public Object loadUserBySAML(final SAMLCredential credential) throws UsernameNotFoundException {
        final String userName = credential.getAttributeAsString("Name");
        final String emailAddress = credential.getAttributeAsString("Email");
        final String[] alertRoles = credential.getAttributeAsStringArray("AlertRoles");
        Set<String> roles = Arrays.stream(alertRoles).collect(Collectors.toSet());

        final UserModel userModel = UserModel.of(userName, "", emailAddress, roles);
        return new UserPrincipal(userModel);
    } */
}
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

public class SAMLManager {
    // TODO enable SAML support
    /*
    public static Logger logger = LoggerFactory.getLogger(SAMLManager.class);
    private final SAMLContext samlContext;
    private final ParserPool parserPool;
    private final ExtendedMetadata extendedMetadata;
    private final MetadataManager metadataManager;

    public SAMLManager(final SAMLContext samlContext, final ParserPool parserPool, final ExtendedMetadata extendedMetadata, final MetadataManager metadataManager) {
        this.samlContext = samlContext;
        this.parserPool = parserPool;
        this.extendedMetadata = extendedMetadata;
        this.metadataManager = metadataManager;
    }

    public void initializeSAML() {
        try {
            final ConfigurationModel currentConfiguration = samlContext.getCurrentConfiguration();
            final boolean samlEnabled = samlContext.isSAMLEnabled(currentConfiguration);
            final String metadataURL = samlContext.getFieldValueOrEmpty(currentConfiguration, SettingsDescriptor.KEY_SAML_METADATA_URL);
            if (samlEnabled) {
                setupMetadataManager(metadataURL);
            }
        } catch (final AlertDatabaseConstraintException | AlertLDAPConfigurationException | MetadataProviderException e) {
            logger.error("Error adding the SAML identity provider.", e);
        }
    }

    public void updateSAMLConfiguration(final boolean samlEnabled, final String metadataURL) {
        try {
            if (samlEnabled) {
                if (StringUtils.isNotBlank(metadataURL)) {
                    setupMetadataManager(metadataURL);
                }
            } else {
                final List<ExtendedMetadataDelegate> currentProviders = metadataManager.getAvailableProviders();
                currentProviders.forEach(ExtendedMetadataDelegate::destroy);
                metadataManager.setProviders(List.of());
                metadataManager.setDefaultIDP(null);
                metadataManager.setHostedSPName(null);
                metadataManager.afterPropertiesSet();
            }
        } catch (final MetadataProviderException e) {
            logger.error("Error updating the SAML identity provider.", e);
        }
    }

    private void setupMetadataManager(final String metadataURL) throws MetadataProviderException {
        final Timer backgroundTaskTimer = new Timer(true);

        // The URL can not end in a '/' because it messes with the paths for saml
        final String correctedMetadataURL = StringUtils.removeEnd(metadataURL, "/");
        final HTTPMetadataProvider httpMetadataProvider;
        httpMetadataProvider = new HTTPMetadataProvider(backgroundTaskTimer, new HttpClient(), correctedMetadataURL);
        httpMetadataProvider.setParserPool(parserPool);

        final ExtendedMetadataDelegate idpMetadata = new ExtendedMetadataDelegate(httpMetadataProvider, extendedMetadata);
        idpMetadata.setMetadataTrustCheck(true);
        idpMetadata.setMetadataRequireSignature(false);
        metadataManager.setProviders(List.of(idpMetadata));
        metadataManager.afterPropertiesSet();
    }*/

}
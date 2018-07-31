/**
 * blackduck-alert
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.alert.channel.slack.descriptor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.alert.channel.DistributionChannelManager;
import com.blackducksoftware.integration.alert.channel.event.ChannelEvent;
import com.blackducksoftware.integration.alert.channel.slack.SlackChannel;
import com.blackducksoftware.integration.alert.common.descriptor.config.DescriptorConfig;
import com.blackducksoftware.integration.alert.common.descriptor.config.UIComponent;
import com.blackducksoftware.integration.alert.database.channel.slack.SlackDistributionConfigEntity;
import com.blackducksoftware.integration.alert.database.channel.slack.SlackDistributionRepositoryAccessor;
import com.blackducksoftware.integration.alert.database.entity.DatabaseEntity;
import com.blackducksoftware.integration.alert.web.channel.model.SlackDistributionConfig;
import com.blackducksoftware.integration.alert.web.model.Config;
import com.blackducksoftware.integration.exception.IntegrationException;

@Component
public class SlackDistributionDescriptorConfig extends DescriptorConfig {
    private final DistributionChannelManager distributionChannelManager;
    private final SlackChannel slackChannel;

    @Autowired
    public SlackDistributionDescriptorConfig(final SlackDistributionContentConverter databaseContentConverter, final SlackDistributionRepositoryAccessor repositoryAccessor, final DistributionChannelManager distributionChannelManager,
            final SlackChannel slackChannel) {
        super(databaseContentConverter, repositoryAccessor);
        this.distributionChannelManager = distributionChannelManager;
        this.slackChannel = slackChannel;
    }

    @Override
    public UIComponent getUiComponent() {
        return new UIComponent("Slack", "slack", "SlackJobConfiguration");
    }

    @Override
    public void validateConfig(final Config restModel, final Map<String, String> fieldErrors) {
        final SlackDistributionConfig slackRestModel = (SlackDistributionConfig) restModel;

        if (StringUtils.isBlank(slackRestModel.getWebhook())) {
            fieldErrors.put("webhook", "A webhook is required.");
        }
        if (StringUtils.isBlank(slackRestModel.getChannelName())) {
            fieldErrors.put("channelName", "A channel name is required.");
        }
    }

    @Override
    public void testConfig(final DatabaseEntity entity) throws IntegrationException {
        final SlackDistributionConfigEntity slackEntity = (SlackDistributionConfigEntity) entity;
        final ChannelEvent event = distributionChannelManager.createChannelEvent(SlackChannel.COMPONENT_NAME);
        slackChannel.sendAuditedMessage(event, slackEntity);
    }

}

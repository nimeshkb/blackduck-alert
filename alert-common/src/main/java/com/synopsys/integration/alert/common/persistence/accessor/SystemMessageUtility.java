/**
 * alert-common
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.alert.common.persistence.accessor;

import java.time.OffsetDateTime;
import java.util.List;

import com.synopsys.integration.alert.common.enumeration.SystemMessageSeverity;
import com.synopsys.integration.alert.common.enumeration.SystemMessageType;
import com.synopsys.integration.alert.common.message.model.DateRange;
import com.synopsys.integration.alert.common.persistence.model.SystemMessageModel;

public interface SystemMessageUtility {

    void addSystemMessage(String message, SystemMessageSeverity severity, SystemMessageType messageType);

    void removeSystemMessagesByType(SystemMessageType messageType);

    List<SystemMessageModel> getSystemMessages();

    List<SystemMessageModel> getSystemMessagesAfter(OffsetDateTime date);

    List<SystemMessageModel> getSystemMessagesBefore(OffsetDateTime date);

    List<SystemMessageModel> findBetween(DateRange dateRange);

    void deleteSystemMessages(List<SystemMessageModel> messagesToDelete);
}

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
package com.synopsys.integration.alert.channel.jira.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.alert.common.channel.issuetracker.IssueConfig;
import com.synopsys.integration.alert.common.exception.AlertFieldException;
import com.synopsys.integration.alert.common.persistence.accessor.FieldAccessor;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.jira.common.model.components.ProjectComponent;
import com.synopsys.integration.jira.common.model.response.IssueTypeResponseModel;
import com.synopsys.integration.jira.common.rest.service.IssueMetaDataService;
import com.synopsys.integration.jira.common.rest.service.IssueTypeService;

public abstract class JiraIssueConfigValidator {
    private static final String CONNECTION_ERROR_FORMAT_STRING = "There was a problem getting the %s from Jira. Please ensure the server is configured correctly.";

    private final IssueTypeService issueTypeService;
    private final IssueMetaDataService issueMetaDataService;

    public JiraIssueConfigValidator(IssueTypeService issueTypeService, IssueMetaDataService issueMetaDataService) {
        this.issueTypeService = issueTypeService;
        this.issueMetaDataService = issueMetaDataService;
    }

    public abstract String getProjectFieldKey();

    public abstract String getIssueTypeFieldKey();

    public abstract String getIssueCreatorFieldKey();

    public abstract String getAddCommentsFieldKey();

    public abstract String getResolveTransitionFieldKey();

    public abstract String getOpenTransitionFieldKey();

    public abstract String getDefaultIssueCreatorFieldKey();

    public abstract Collection<ProjectComponent> getProjectsByName(String jiraProjectName) throws IntegrationException;

    public abstract boolean isUserValid(String issueCreator) throws IntegrationException;

    public IssueConfig validate(FieldAccessor fieldAccessor) throws AlertFieldException {
        IssueConfig jiraIssueConfig = new IssueConfig();
        Map<String, String> fieldErrors = new HashMap<>();

        ProjectComponent projectComponent = validateProject(fieldAccessor, fieldErrors);
        if (projectComponent != null) {
            jiraIssueConfig.setProjectId(projectComponent.getId());
            jiraIssueConfig.setProjectKey(projectComponent.getKey());
            jiraIssueConfig.setProjectName(projectComponent.getName());
        }

        String issueCreator = validateIssueCreator(fieldAccessor, fieldErrors);
        jiraIssueConfig.setIssueCreator(issueCreator);

        String issueType = validateIssueType(fieldAccessor, fieldErrors);
        jiraIssueConfig.setIssueType(issueType);

        Boolean commentOnIssue = fieldAccessor.getBooleanOrFalse(getAddCommentsFieldKey());
        jiraIssueConfig.setCommentOnIssues(commentOnIssue);

        String resolveTransition = fieldAccessor.getStringOrNull(getResolveTransitionFieldKey());
        jiraIssueConfig.setResolveTransition(resolveTransition);

        String openTransition = fieldAccessor.getStringOrNull(getOpenTransitionFieldKey());
        jiraIssueConfig.setOpenTransition(openTransition);

        if (fieldErrors.isEmpty()) {
            return jiraIssueConfig;
        } else {
            throw new AlertFieldException(fieldErrors);
        }
    }

    private ProjectComponent validateProject(FieldAccessor fieldAccessor, Map<String, String> fieldErrors) {
        String projectNameFieldKey = getProjectFieldKey();
        Optional<String> optionalProjectName = fieldAccessor.getString(projectNameFieldKey);
        if (optionalProjectName.isPresent()) {
            String jiraProjectName = optionalProjectName.get();
            try {
                Collection<ProjectComponent> projectsResponseModel = getProjectsByName(jiraProjectName);
                Optional<ProjectComponent> optionalProject = projectsResponseModel
                                                                 .stream()
                                                                 .filter(project -> jiraProjectName.equals(project.getName()) || jiraProjectName.equals(project.getKey()))
                                                                 .findAny();
                if (optionalProject.isPresent()) {
                    return optionalProject.get();
                } else {
                    fieldErrors.put(projectNameFieldKey, String.format("No project named '%s' was found", jiraProjectName));
                }
            } catch (IntegrationException e) {
                fieldErrors.put(projectNameFieldKey, String.format(CONNECTION_ERROR_FORMAT_STRING, "projects"));
            }
        } else {
            requireField(fieldErrors, projectNameFieldKey);
        }
        return null;
    }

    private String validateIssueCreator(FieldAccessor fieldAccessor, Map<String, String> fieldErrors) {
        String issueCreatorFieldKey = getIssueCreatorFieldKey();
        Optional<String> optionalIssueCreator = fieldAccessor.getString(issueCreatorFieldKey)
                                                    .filter(StringUtils::isNotBlank)
                                                    .or(() -> fieldAccessor.getString(getDefaultIssueCreatorFieldKey()));
        if (optionalIssueCreator.isPresent()) {
            String issueCreator = optionalIssueCreator.get();
            try {
                if (isUserValid(issueCreator)) {
                    return issueCreator;
                } else {
                    fieldErrors.put(issueCreatorFieldKey, String.format("The username '%s' is not associated with any valid Jira users.", issueCreator));
                }
            } catch (IntegrationException e) {
                fieldErrors.put(issueCreatorFieldKey, String.format(CONNECTION_ERROR_FORMAT_STRING, "users"));
            }
        } else {
            requireField(fieldErrors, issueCreatorFieldKey);
        }
        return null;
    }

    private String validateIssueType(FieldAccessor fieldAccessor, Map<String, String> fieldErrors) {
        String projectNameFieldKey = getProjectFieldKey();
        String issueTypeFieldKey = getIssueTypeFieldKey();
        Optional<String> optionalProjectName = fieldAccessor.getString(projectNameFieldKey);
        String issueType = fieldAccessor.getString(issueTypeFieldKey).orElse(JiraConstants.DEFAULT_ISSUE_TYPE);
        try {
            boolean isValidIssueType = issueTypeService.getAllIssueTypes()
                                           .stream()
                                           .map(IssueTypeResponseModel::getName)
                                           .anyMatch(issueType::equals);
            if (isValidIssueType) {
                if (optionalProjectName.isPresent()) {
                    String projectName = optionalProjectName.get();
                    boolean isValidForProject = issueMetaDataService.doesProjectContainIssueType(projectName, issueType);
                    if (isValidForProject) {
                        return issueType;
                    } else {
                        fieldErrors.put(issueTypeFieldKey, String.format("The issue type '%s' not assigned to project '%s'", issueType, projectName));
                    }
                }
            } else {
                fieldErrors.put(issueTypeFieldKey, String.format("The issue type '%s' could not be found", issueType));
            }
        } catch (IntegrationException e) {
            fieldErrors.put(issueTypeFieldKey, String.format(CONNECTION_ERROR_FORMAT_STRING, "issue types"));
        }
        return null;
    }

    private void requireField(Map<String, String> fieldErrors, String key) {
        fieldErrors.put(key, "This field is required");
    }
}
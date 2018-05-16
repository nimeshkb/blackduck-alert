package com.blackducksoftware.integration.hub.alert.processor.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.alert.config.GlobalProperties;
import com.blackducksoftware.integration.hub.alert.datasource.entity.NotificationCategoryEnum;
import com.blackducksoftware.integration.hub.alert.hub.model.NotificationModel;
import com.blackducksoftware.integration.hub.alert.mock.notification.NotificationGeneratorUtils;
import com.blackducksoftware.integration.hub.alert.processor.NotificationProcessingModel;
import com.blackducksoftware.integration.hub.alert.processor.NotificationProcessingRule;
import com.blackducksoftware.integration.hub.api.generated.enumeration.NotificationType;
import com.blackducksoftware.integration.hub.api.generated.view.NotificationView;
import com.blackducksoftware.integration.hub.api.view.CommonNotificationState;
import com.blackducksoftware.integration.hub.notification.NotificationContentDetailResults;
import com.blackducksoftware.integration.hub.notification.NotificationResults;
import com.blackducksoftware.integration.hub.notification.content.ComponentVersionStatus;
import com.blackducksoftware.integration.hub.notification.content.PolicyInfo;
import com.blackducksoftware.integration.hub.notification.content.PolicyOverrideNotificationContent;
import com.blackducksoftware.integration.hub.notification.content.RuleViolationClearedNotificationContent;
import com.blackducksoftware.integration.hub.notification.content.RuleViolationNotificationContent;
import com.blackducksoftware.integration.hub.notification.content.detail.NotificationContentDetail;
import com.blackducksoftware.integration.hub.service.bucket.HubBucket;

public class PolicyNotificationTypeProcessorTest {
    private static final String COMPONENT_ISSUE_URL = "issuesLink";
    private static final String COMPONENT_VERSION_URL = "component version url";
    private static final String COMPONENT_URL = "component url";
    private static final String POLICY_URL = "policyUrl";
    private static final String POLICY_NAME = "PolicyViolation";
    private static final String PROJECT_VERSION_URL = "policy url";
    private static final String PROJECT_NAME = "PolicyProject";
    private static final String COMPONENT_VERSION_NAME = "1.2.3";
    private static final String COMPONENT_NAME = "notification test component";
    private PolicyNotificationTypeProcessor processor;

    @Before
    public void initProcessor() {
        final GlobalProperties globalProperties = Mockito.mock(GlobalProperties.class);
        processor = new PolicyNotificationTypeProcessor(globalProperties);
    }

    @Test
    public void testRuleCollection() {
        final Collection<NotificationProcessingRule<NotificationProcessingModel>> rules = processor.getProcessingRules();
        assertEquals(3, rules.size());
    }

    @Test
    public void testPolicyViolation() {
        final List<CommonNotificationState> notificationContentItems = new ArrayList<>();
        final CommonNotificationState notificationContentItem = createPolicyViolationNotification();
        notificationContentItems.add(notificationContentItem);

        final NotificationResults notificationResults = NotificationGeneratorUtils.createNotificationResults(notificationContentItems);
        final HubBucket bucket = notificationResults.getHubBucket();
        final NotificationContentDetailResults detailResults = notificationResults.getNotificationContentDetails();
        notificationResults.getCommonNotificationStates().forEach(commonNotificationState -> {
            processor.process(commonNotificationState, bucket, detailResults);
        });

        final List<NotificationModel> modelList = processor.getModels(bucket);

        assertEquals(1, modelList.size());
        final NotificationModel model = modelList.get(0);
        final List<NotificationContentDetail> detailList = detailResults.getDetails(notificationContentItem.getContent());

        final NotificationContentDetail detail = detailList.get(0);
        assertEquals(detail.getContentDetailKey(), model.getEventKey());
        assertEquals(NotificationCategoryEnum.POLICY_VIOLATION, model.getNotificationType());
        assertEquals(detail.getProjectName(), model.getProjectName());
        assertEquals(detail.getProjectVersionName(), model.getProjectVersion());
        assertEquals(detail.getComponentName().get(), model.getComponentName());
        assertEquals(detail.getComponentVersionName().get(), model.getComponentVersion());
        assertEquals(detail.getPolicyName().get(), model.getPolicyRuleName());
    }

    @Test
    public void testPolicyCleared() {
        final List<CommonNotificationState> notificationContentItems = new ArrayList<>();
        final CommonNotificationState notificationContentItem = createPolicyClearedNotification();
        notificationContentItems.add(notificationContentItem);

        final NotificationResults notificationResults = NotificationGeneratorUtils.createNotificationResults(notificationContentItems);
        final HubBucket bucket = notificationResults.getHubBucket();
        final NotificationContentDetailResults detailResults = notificationResults.getNotificationContentDetails();
        notificationResults.getCommonNotificationStates().forEach(commonNotificationState -> {
            processor.process(commonNotificationState, bucket, detailResults);
        });

        final List<NotificationModel> modelList = processor.getModels(bucket);

        assertEquals(1, modelList.size());
        final NotificationModel model = modelList.get(0);
        final List<NotificationContentDetail> detailList = detailResults.getDetails(notificationContentItem.getContent());

        final NotificationContentDetail detail = detailList.get(0);
        assertEquals(detail.getContentDetailKey(), model.getEventKey());
        assertEquals(NotificationCategoryEnum.POLICY_VIOLATION_CLEARED, model.getNotificationType());
        assertEquals(detail.getProjectName(), model.getProjectName());
        assertEquals(detail.getProjectVersionName(), model.getProjectVersion());
        assertEquals(detail.getComponentName().get(), model.getComponentName());
        assertEquals(detail.getComponentVersionName().get(), model.getComponentVersion());
        assertEquals(detail.getPolicyName().get(), model.getPolicyRuleName());
    }

    @Test
    public void testPolicyOverride() {
        final List<CommonNotificationState> notificationContentItems = new ArrayList<>();
        final CommonNotificationState notificationContentItem = createPolicyOverrideNotification();
        notificationContentItems.add(notificationContentItem);

        final NotificationResults notificationResults = NotificationGeneratorUtils.createNotificationResults(notificationContentItems);
        final HubBucket bucket = notificationResults.getHubBucket();
        final NotificationContentDetailResults detailResults = notificationResults.getNotificationContentDetails();
        notificationResults.getCommonNotificationStates().forEach(commonNotificationState -> {
            processor.process(commonNotificationState, bucket, detailResults);
        });

        final List<NotificationModel> modelList = processor.getModels(bucket);

        assertEquals(1, modelList.size());
        final NotificationModel model = modelList.get(0);
        final List<NotificationContentDetail> detailList = detailResults.getDetails(notificationContentItem.getContent());

        final NotificationContentDetail detail = detailList.get(0);
        assertEquals(detail.getContentDetailKey(), model.getEventKey());
        assertEquals(NotificationCategoryEnum.POLICY_VIOLATION_OVERRIDE, model.getNotificationType());
        assertEquals(detail.getProjectName(), model.getProjectName());
        assertEquals(detail.getProjectVersionName(), model.getProjectVersion());
        assertEquals(detail.getComponentName().get(), model.getComponentName());
        assertEquals(detail.getComponentVersionName().get(), model.getComponentVersion());
        assertEquals(detail.getPolicyName().get(), model.getPolicyRuleName());
    }

    @Test
    public void testPolicyClearedCancel() {
        final CommonNotificationState violation = createPolicyViolationNotification();
        final CommonNotificationState cleared = createPolicyClearedNotification();
        final List<CommonNotificationState> notificationContentItems = Arrays.asList(violation, cleared);

        final NotificationResults notificationResults = NotificationGeneratorUtils.createNotificationResults(notificationContentItems);
        final HubBucket bucket = notificationResults.getHubBucket();
        final NotificationContentDetailResults detailResults = notificationResults.getNotificationContentDetails();
        notificationResults.getCommonNotificationStates().forEach(commonNotificationState -> {
            processor.process(commonNotificationState, bucket, detailResults);
        });

        final List<NotificationModel> modelList = processor.getModels(bucket);

        assertTrue(modelList.isEmpty());
    }

    @Test
    public void testPolicyOverrideCancel() {
        final CommonNotificationState violation = createPolicyViolationNotification();
        final CommonNotificationState override = createPolicyOverrideNotification();
        final List<CommonNotificationState> notificationContentItems = Arrays.asList(violation, override);

        final NotificationResults notificationResults = NotificationGeneratorUtils.createNotificationResults(notificationContentItems);
        final HubBucket bucket = notificationResults.getHubBucket();
        final NotificationContentDetailResults detailResults = notificationResults.getNotificationContentDetails();
        notificationResults.getCommonNotificationStates().forEach(commonNotificationState -> {
            processor.process(commonNotificationState, bucket, detailResults);
        });

        final List<NotificationModel> modelList = processor.getModels(bucket);

        assertTrue(modelList.isEmpty());
    }

    @Test
    public void testDuplicatePolicyClearedAdded() {
        final CommonNotificationState violation = createPolicyViolationNotification();
        final CommonNotificationState cleared = createPolicyClearedNotification();
        final CommonNotificationState duplicateCleared = createPolicyClearedNotification();
        final List<CommonNotificationState> notificationContentItems = Arrays.asList(violation, cleared, duplicateCleared);

        final NotificationResults notificationResults = NotificationGeneratorUtils.createNotificationResults(notificationContentItems);
        final HubBucket bucket = notificationResults.getHubBucket();
        final NotificationContentDetailResults detailResults = notificationResults.getNotificationContentDetails();
        notificationResults.getCommonNotificationStates().forEach(commonNotificationState -> {
            processor.process(commonNotificationState, bucket, detailResults);
        });

        final List<NotificationModel> modelList = processor.getModels(bucket);

        assertEquals(1, modelList.size());
        final NotificationModel model = modelList.get(0);
        final List<NotificationContentDetail> detailList = detailResults.getDetails(duplicateCleared.getContent());

        final NotificationContentDetail detail = detailList.get(0);
        assertEquals(detail.getContentDetailKey(), model.getEventKey());
        assertEquals(NotificationCategoryEnum.POLICY_VIOLATION_CLEARED, model.getNotificationType());
        assertEquals(detail.getProjectName(), model.getProjectName());
        assertEquals(detail.getProjectVersionName(), model.getProjectVersion());
        assertEquals(detail.getComponentName().get(), model.getComponentName());
        assertEquals(detail.getComponentVersionName().get(), model.getComponentVersion());
        assertEquals(detail.getPolicyName().get(), model.getPolicyRuleName());
    }

    @Test
    public void testDuplicateOverrideAdded() {
        final CommonNotificationState violation = createPolicyViolationNotification();
        final CommonNotificationState override = createPolicyOverrideNotification();
        final CommonNotificationState duplicateOverride = createPolicyOverrideNotification();
        final List<CommonNotificationState> notificationContentItems = Arrays.asList(violation, override, duplicateOverride);

        final NotificationResults notificationResults = NotificationGeneratorUtils.createNotificationResults(notificationContentItems);
        final HubBucket bucket = notificationResults.getHubBucket();
        final NotificationContentDetailResults detailResults = notificationResults.getNotificationContentDetails();
        notificationResults.getCommonNotificationStates().forEach(commonNotificationState -> {
            processor.process(commonNotificationState, bucket, detailResults);
        });

        final List<NotificationModel> modelList = processor.getModels(bucket);

        assertEquals(1, modelList.size());
        final NotificationModel model = modelList.get(0);
        final List<NotificationContentDetail> detailList = detailResults.getDetails(duplicateOverride.getContent());

        final NotificationContentDetail detail = detailList.get(0);
        assertEquals(detail.getContentDetailKey(), model.getEventKey());
        assertEquals(NotificationCategoryEnum.POLICY_VIOLATION_OVERRIDE, model.getNotificationType());
        assertEquals(detail.getProjectName(), model.getProjectName());
        assertEquals(detail.getProjectVersionName(), model.getProjectVersion());
        assertEquals(detail.getComponentName().get(), model.getComponentName());
        assertEquals(detail.getComponentVersionName().get(), model.getComponentVersion());
        assertEquals(detail.getPolicyName().get(), model.getPolicyRuleName());
    }

    private CommonNotificationState createPolicyViolationNotification() {
        final NotificationView view = NotificationGeneratorUtils.createNotificationView(NotificationType.RULE_VIOLATION);
        final RuleViolationNotificationContent content = new RuleViolationNotificationContent();
        content.projectName = PROJECT_NAME;
        content.projectVersionName = COMPONENT_VERSION_NAME;
        content.projectVersion = PROJECT_VERSION_URL;
        content.componentVersionsInViolation = 1;

        final PolicyInfo policyInfo = new PolicyInfo();
        policyInfo.policyName = POLICY_NAME;
        policyInfo.policy = POLICY_URL;
        content.policyInfos = Arrays.asList(policyInfo);

        final ComponentVersionStatus componentVersionStatus = new ComponentVersionStatus();
        componentVersionStatus.componentName = COMPONENT_NAME;
        componentVersionStatus.componentVersionName = COMPONENT_VERSION_NAME;
        componentVersionStatus.component = COMPONENT_URL;
        componentVersionStatus.componentVersion = COMPONENT_VERSION_URL;
        componentVersionStatus.componentIssueLink = COMPONENT_ISSUE_URL;
        componentVersionStatus.policies = Arrays.asList(policyInfo.policy);
        componentVersionStatus.bomComponentVersionPolicyStatus = "IN_VIOLATION";
        content.componentVersionStatuses = Arrays.asList(componentVersionStatus);
        final CommonNotificationState notificationContentItem = NotificationGeneratorUtils.createCommonNotificationState(view, content);
        return notificationContentItem;
    }

    private CommonNotificationState createPolicyClearedNotification() {
        final NotificationView view = NotificationGeneratorUtils.createNotificationView(NotificationType.RULE_VIOLATION_CLEARED);

        final RuleViolationClearedNotificationContent content = new RuleViolationClearedNotificationContent();
        content.projectName = PROJECT_NAME;
        content.projectVersionName = COMPONENT_VERSION_NAME;
        content.projectVersion = PROJECT_VERSION_URL;
        content.componentVersionsCleared = 1;

        final PolicyInfo policyInfo = new PolicyInfo();
        policyInfo.policyName = POLICY_NAME;
        policyInfo.policy = POLICY_URL;
        content.policyInfos = Arrays.asList(policyInfo);

        final ComponentVersionStatus componentVersionStatus = new ComponentVersionStatus();
        componentVersionStatus.componentName = COMPONENT_NAME;
        componentVersionStatus.componentVersionName = COMPONENT_VERSION_NAME;
        componentVersionStatus.component = COMPONENT_URL;
        componentVersionStatus.componentVersion = COMPONENT_VERSION_URL;
        componentVersionStatus.componentIssueLink = COMPONENT_ISSUE_URL;
        componentVersionStatus.policies = Arrays.asList(policyInfo.policy);
        componentVersionStatus.bomComponentVersionPolicyStatus = "VIOLATION_CLEARED";
        content.componentVersionStatuses = Arrays.asList(componentVersionStatus);
        final CommonNotificationState notificationContentItem = NotificationGeneratorUtils.createCommonNotificationState(view, content);
        return notificationContentItem;
    }

    private CommonNotificationState createPolicyOverrideNotification() {
        final NotificationView view = NotificationGeneratorUtils.createNotificationView(NotificationType.POLICY_OVERRIDE);
        final PolicyInfo policyInfo = new PolicyInfo();
        policyInfo.policyName = POLICY_NAME;
        policyInfo.policy = POLICY_URL;

        final PolicyOverrideNotificationContent content = new PolicyOverrideNotificationContent();
        content.projectName = PROJECT_NAME;
        content.projectVersionName = COMPONENT_VERSION_NAME;
        content.projectVersion = PROJECT_VERSION_URL;
        content.componentName = COMPONENT_NAME;
        content.componentVersionName = COMPONENT_VERSION_NAME;
        content.componentVersion = COMPONENT_VERSION_URL;
        content.policyInfos = Arrays.asList(policyInfo);
        content.policies = Arrays.asList(policyInfo.policy);
        content.bomComponentVersionPolicyStatus = "POLICY_OVERRIDE";
        final CommonNotificationState notificationContentItem = NotificationGeneratorUtils.createCommonNotificationState(view, content);
        return notificationContentItem;
    }
}

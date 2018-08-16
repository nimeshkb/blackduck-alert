package com.synopsys.integration.alert.web.controller;

import java.nio.charset.Charset;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.synopsys.integration.alert.TestProperties;
import com.synopsys.integration.alert.TestPropertyKey;
import com.synopsys.integration.test.annotation.ExternalConnectionTest;
import com.synopsys.integration.alert.Application;
import com.synopsys.integration.alert.common.AlertProperties;
import com.synopsys.integration.alert.database.DatabaseDataSource;
import com.synopsys.integration.alert.mock.model.MockLoginRestModel;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProperties;

@Category(ExternalConnectionTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class, DatabaseDataSource.class })
@TestPropertySource(locations = "classpath:spring-test.properties")
@Transactional
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class LoginControllerTestIT {

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected BlackDuckProperties blackDuckProperties;
    @Autowired
    protected AlertProperties alertProperties;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private MockMvc mockMvc;
    private final String loginUrl = BaseController.BASE_PATH + "/login";
    private final String logoutUrl = BaseController.BASE_PATH + "/logout";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testLogout() throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(logoutUrl).with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"));
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testLogin() throws Exception {

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(loginUrl);
        final TestProperties testProperties = new TestProperties();
        final MockLoginRestModel mockLoginRestModel = new MockLoginRestModel();
        mockLoginRestModel.setBlackDuckUsername(testProperties.getProperty(TestPropertyKey.TEST_USERNAME));
        mockLoginRestModel.setBlackDuckPassword(testProperties.getProperty(TestPropertyKey.TEST_PASSWORD));

        ReflectionTestUtils.setField(blackDuckProperties, "blackDuckUrl", testProperties.getProperty(TestPropertyKey.TEST_HUB_SERVER_URL));
        ReflectionTestUtils.setField(alertProperties, "alertTrustCertificate", Boolean.valueOf(testProperties.getProperty(TestPropertyKey.TEST_TRUST_HTTPS_CERT)));
        final String restModel = mockLoginRestModel.getRestModelJson();
        request.content(restModel);
        request.contentType(contentType);
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

}
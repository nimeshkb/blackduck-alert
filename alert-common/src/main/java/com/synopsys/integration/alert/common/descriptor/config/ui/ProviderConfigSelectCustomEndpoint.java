package com.synopsys.integration.alert.common.descriptor.config.ui;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.synopsys.integration.alert.common.action.CustomEndpointManager;
import com.synopsys.integration.alert.common.descriptor.DescriptorKey;
import com.synopsys.integration.alert.common.descriptor.DescriptorMap;
import com.synopsys.integration.alert.common.descriptor.ProviderDescriptor;
import com.synopsys.integration.alert.common.descriptor.config.field.LabelValueSelectOption;
import com.synopsys.integration.alert.common.descriptor.config.field.endpoint.SelectCustomEndpoint;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.accessor.FieldAccessor;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationModel;
import com.synopsys.integration.alert.common.rest.ResponseFactory;
import com.synopsys.integration.alert.common.rest.model.FieldModel;

@Component
public class ProviderConfigSelectCustomEndpoint extends SelectCustomEndpoint {
    private final ConfigurationAccessor configurationAccessor;
    private final DescriptorMap descriptorMap;

    @Autowired
    public ProviderConfigSelectCustomEndpoint(CustomEndpointManager customEndpointManager, ResponseFactory responseFactory, Gson gson, ConfigurationAccessor configurationAccessor, DescriptorMap descriptorMap) throws AlertException {
        super(ProviderDescriptor.KEY_PROVIDER_CONFIG_NAME, customEndpointManager, responseFactory, gson);
        this.configurationAccessor = configurationAccessor;
        this.descriptorMap = descriptorMap;
    }

    @Override
    protected List<LabelValueSelectOption> createData(FieldModel fieldModel) throws AlertException {
        String providerName = fieldModel.getDescriptorName();
        Optional<DescriptorKey> descriptorKey = descriptorMap.getDescriptorKey(providerName);
        if (descriptorKey.isPresent()) {
            List<ConfigurationModel> configurationModels = configurationAccessor.getConfigurationByDescriptorKeyAndContext(descriptorKey.get(), ConfigContextEnum.GLOBAL);
            return configurationModels.stream()
                       .map(ConfigurationModel::getCopyOfKeyToFieldMap)
                       .map(FieldAccessor::new)
                       .map(accessor -> accessor.getString(ProviderDescriptor.KEY_PROVIDER_CONFIG_NAME))
                       .flatMap(Optional::stream)
                       .map(LabelValueSelectOption::new)
                       .collect(Collectors.toList());
        }
        return List.of();
    }
}

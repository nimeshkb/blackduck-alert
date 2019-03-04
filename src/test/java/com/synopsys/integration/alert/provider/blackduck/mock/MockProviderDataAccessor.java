package com.synopsys.integration.alert.provider.blackduck.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.synopsys.integration.alert.common.exception.AlertDatabaseConstraintException;
import com.synopsys.integration.alert.common.persistence.model.ProviderProject;
import com.synopsys.integration.alert.database.api.ProviderDataAccessor;
import com.synopsys.integration.alert.database.provider.user.ProviderUserEntity;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProvider;
import com.synopsys.integration.alert.provider.polaris.PolarisProvider;

public final class MockProviderDataAccessor extends ProviderDataAccessor {
    private final Map<String, Set<ProviderProject>> providerProjectMap;
    private final Set<ProviderUserEntity> users;
    private Set<String> expectedEmailAddresses = Set.of();

    public MockProviderDataAccessor() {
        super(null, null, null);
        providerProjectMap = new HashMap<>();
        providerProjectMap.put(BlackDuckProvider.COMPONENT_NAME, new HashSet<>());
        providerProjectMap.put(PolarisProvider.COMPONENT_NAME, new HashSet<>());
        users = new HashSet<>();
    }

    @Override
    public ProviderProject saveProject(final String providerName, final ProviderProject providerProject) {
        providerProjectMap.get(providerName).add(providerProject);
        return providerProject;
    }

    @Override
    public List<ProviderProject> deleteAndSaveAllProjects(final String providerName, final Collection<ProviderProject> providerProjects) {
        providerProjectMap.put(providerName, new HashSet<>(providerProjects));
        return new ArrayList<>(providerProjects);
    }

    @Override
    public void deleteByHref(final String href) {
        providerProjectMap.values()
            .stream()
            .flatMap(Collection::stream)
            .filter(providerProject -> href.equals(providerProject.getHref()))
            .findFirst();
    }

    @Override
    public Set<String> getEmailAddressesForProjectHref(final String href) {
        return expectedEmailAddresses;
    }

    @Override
    public void mapUsersToProjectByEmail(final String href, final Collection<String> emailAddresses) throws AlertDatabaseConstraintException {
        // Implement if needed
    }

    @Override
    public Optional<ProviderProject> findFirstByName(final String name) {
        return providerProjectMap.values()
                   .stream()
                   .flatMap(Collection::stream)
                   .filter(providerProject -> name.equals(providerProject.getName()))
                   .findFirst();
    }

    @Override
    public List<ProviderProject> findByProviderName(final String providerName) {
        return new ArrayList<>(providerProjectMap.get(providerName));
    }

    public void setExpectedEmailAddresses(final Set<String> expectedEmailAddresses) {
        this.expectedEmailAddresses = expectedEmailAddresses;
    }

    @Override
    public List<ProviderUserEntity> getAllUsers(final String providerName) {
        return new ArrayList<>(users);
    }

    @Override
    public List<ProviderUserEntity> deleteAndSaveAllUsers(final Iterable<ProviderUserEntity> userEntitiesToDelete, final Iterable<ProviderUserEntity> userEntitiesToAdd) {
        for (final ProviderUserEntity user : userEntitiesToDelete) {
            users.remove(user);
        }
        for (final ProviderUserEntity user : userEntitiesToAdd) {
            users.add(user);
        }
        return new ArrayList<>(users);
    }
}

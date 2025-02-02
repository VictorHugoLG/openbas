package io.openbas.executors.tanium.service;

import io.openbas.database.model.Asset;
import io.openbas.database.model.Endpoint;
import io.openbas.database.model.Injector;
import io.openbas.executors.tanium.client.TaniumExecutorClient;
import io.openbas.executors.tanium.config.TaniumExecutorConfig;
import jakarta.validation.constraints.NotNull;
import lombok.extern.java.Log;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Objects;

@Log
@Service
public class TaniumExecutorContextService {
    private TaniumExecutorConfig taniumExecutorConfig;

    private TaniumExecutorClient taniumExecutorClient;

    @Autowired
    public void setTaniumExecutorConfig(TaniumExecutorConfig taniumExecutorConfig) {
        this.taniumExecutorConfig = taniumExecutorConfig;
    }

    @Autowired
    public void setTaniumExecutorClient(TaniumExecutorClient taniumExecutorClient) {
        this.taniumExecutorClient = taniumExecutorClient;
    }

    public void launchExecutorSubprocess(@NotNull final Injector injector, @NotNull final Asset asset) {
        Endpoint.PLATFORM_TYPE platform = Objects.equals(asset.getType(), "Endpoint") ? ((Endpoint) Hibernate.unproxy(asset)).getPlatform(): null;
        if( platform == null ) {
            throw new RuntimeException("Unsupported platform: " + platform);
        }
        switch (platform ) {
            case Endpoint.PLATFORM_TYPE.Windows -> {
                String command = injector.getExecutorCommands().get(Endpoint.PLATFORM_TYPE.Windows.name()).replace("\"#{location}\"", "$PWD.Path");
                this.taniumExecutorClient.executeAction(asset.getExternalReference(), this.taniumExecutorConfig.getWindowsPackageId(), Base64.getEncoder().encodeToString(command.getBytes()));
            }
            case Endpoint.PLATFORM_TYPE.Linux -> {
                String command = injector.getExecutorCommands().get(Endpoint.PLATFORM_TYPE.Linux.name()).replace("\"#{location}\"", "$(pwd)");
                this.taniumExecutorClient.executeAction(asset.getExternalReference(), this.taniumExecutorConfig.getUnixPackageId(), Base64.getEncoder().encodeToString(command.getBytes()));
            }
            case Endpoint.PLATFORM_TYPE.MacOS -> {
                String command = injector.getExecutorCommands().get(Endpoint.PLATFORM_TYPE.MacOS.name()).replace("\"#{location}\"", "$(pwd)");
                this.taniumExecutorClient.executeAction(asset.getExternalReference(), this.taniumExecutorConfig.getUnixPackageId(), Base64.getEncoder().encodeToString(command.getBytes()));
            }
            default -> throw new RuntimeException("Unsupported platform: " + platform);
        };
    }

    public void launchExecutorClear(@NotNull final Injector injector, @NotNull final Asset asset) {

    }
}

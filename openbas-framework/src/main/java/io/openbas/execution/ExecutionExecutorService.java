package io.openbas.execution;

import io.openbas.asset.AssetGroupService;
import io.openbas.database.model.Asset;
import io.openbas.database.model.Executor;
import io.openbas.database.model.Inject;
import io.openbas.database.model.Injector;
import io.openbas.executors.caldera.config.CalderaExecutorConfig;
import io.openbas.executors.caldera.service.CalderaExecutorContextService;
import io.openbas.executors.tanium.config.TaniumExecutorConfig;
import io.openbas.executors.tanium.service.TaniumExecutorContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Log
public class ExecutionExecutorService {
    private AssetGroupService assetGroupService;

    private CalderaExecutorConfig calderaExecutorConfig;

    private CalderaExecutorContextService calderaExecutorContextService;

    private TaniumExecutorConfig taniumExecutorConfig;

    private TaniumExecutorContextService taniumExecutorContextService;

    @Autowired
    public void setAssetGroupService(AssetGroupService assetGroupService) {
        this.assetGroupService = assetGroupService;
    }

    @Autowired
    public void setCalderaExecutorConfig(CalderaExecutorConfig calderaExecutorConfig) {
        this.calderaExecutorConfig = calderaExecutorConfig;
    }

    @Autowired
    public void setCalderaExecutorContextService(CalderaExecutorContextService calderaExecutorContextService) {
        this.calderaExecutorContextService = calderaExecutorContextService;
    }

    @Autowired
    public void setTaniumExecutorConfig(TaniumExecutorConfig taniumExecutorConfig) {
        this.taniumExecutorConfig = taniumExecutorConfig;
    }

    @Autowired
    public void setTaniumExecutorContextService(TaniumExecutorContextService taniumExecutorContextService) {
        this.taniumExecutorContextService = taniumExecutorContextService;
    }

    public ExecutableInject launchExecutorContext(ExecutableInject executableInject, Inject inject) throws InterruptedException {
        // First, get the assets of this injects
        List<Asset> assets = Stream.concat(
                inject.getAssets().stream(),
                inject.getAssetGroups().stream().flatMap(assetGroup -> this.assetGroupService.assetsFromAssetGroup(assetGroup.getId()).stream())
        ).toList();
        assets.forEach(asset -> {
            launchExecutorContextForAsset(inject.getInjectorContract().getInjector(), asset);
        });
        return executableInject;
    }

    private void launchExecutorContextForAsset(Injector injector, Asset asset) {
        Executor executor = asset.getExecutor();
        if (executor == null) {
            log.log(Level.SEVERE, "Cannot find the executor for the asset " + asset.getName());
        } else {
            switch (executor.getType()) {
                case "openbas_caldera" -> {
                    if (!this.calderaExecutorConfig.isEnable()) {
                        throw new RuntimeException("Fatal error: Caldera executor is not enabled");
                    }
                    this.calderaExecutorContextService.launchExecutorSubprocess(injector, asset);
                }
                case "openbas_tanium" -> {
                    if (!this.taniumExecutorConfig.isEnable()) {
                        throw new RuntimeException("Fatal error: Tanium executor is not enabled");
                    }
                    this.taniumExecutorContextService.launchExecutorSubprocess(injector, asset);
                }
                default -> {
                    throw new RuntimeException("Fatal error: Unsupported executor " + executor.getType());
                }
            }
        }
    }
}

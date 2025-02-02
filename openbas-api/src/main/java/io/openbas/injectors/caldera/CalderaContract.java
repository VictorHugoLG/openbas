package io.openbas.injectors.caldera;

import io.openbas.injector_contract.*;
import io.openbas.injector_contract.fields.ContractAsset;
import io.openbas.injector_contract.fields.ContractAssetGroup;
import io.openbas.injector_contract.fields.ContractExpectations;
import io.openbas.injector_contract.fields.ContractSelect;
import io.openbas.database.model.Endpoint;
import io.openbas.helper.SupportedLanguage;
import io.openbas.injectors.caldera.client.model.Ability;
import io.openbas.injectors.caldera.config.CalderaInjectorConfig;
import io.openbas.injectors.caldera.model.Obfuscator;
import io.openbas.injectors.caldera.service.CalderaInjectorService;
import io.openbas.model.inject.form.Expectation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.openbas.executors.caldera.service.CalderaExecutorService.toPlatform;
import static io.openbas.injector_contract.Contract.executableContract;
import static io.openbas.injector_contract.ContractCardinality.Multiple;
import static io.openbas.injector_contract.ContractDef.contractBuilder;
import static io.openbas.injector_contract.fields.ContractAsset.assetField;
import static io.openbas.injector_contract.fields.ContractAssetGroup.assetGroupField;
import static io.openbas.injector_contract.fields.ContractExpectations.expectationsField;
import static io.openbas.injector_contract.fields.ContractSelect.selectFieldWithDefault;
import static io.openbas.database.model.InjectExpectation.EXPECTATION_TYPE.DETECTION;
import static io.openbas.database.model.InjectExpectation.EXPECTATION_TYPE.PREVENTION;
import static io.openbas.helper.SupportedLanguage.en;
import static io.openbas.helper.SupportedLanguage.fr;

@Component
@RequiredArgsConstructor
public class CalderaContract extends Contractor {

    public static final String TYPE = "openbas_caldera";

    private final CalderaInjectorConfig config;
    private final CalderaInjectorService injectorCalderaService;

    @Override
    public boolean isExpose() {
        return this.config.isEnable();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ContractConfig getConfig() {
        Map<SupportedLanguage, String> labels = Map.of(en, "Caldera", fr, "Caldera");
        return new ContractConfig(TYPE, labels, "#8b0000", "#8b0000", "/img/icon-caldera.png", isExpose());
    }

    @Override
    public List<Contract> contracts() {
        if (this.config.isEnable()) {
            ContractConfig contractConfig = getConfig();
            // Add contract based on abilities
            return new ArrayList<>(abilityContracts(contractConfig));
        }
        return List.of();
    }

    // -- PRIVATE --

    private ContractSelect obfuscatorField() {
        List<Obfuscator> obfuscators = this.injectorCalderaService.obfuscators();
        Map<String, String> obfuscatorChoices = obfuscators.stream()
                .collect(Collectors.toMap(Obfuscator::getName, Obfuscator::getName));
        return selectFieldWithDefault(
                "obfuscator",
                "Obfuscators",
                obfuscatorChoices,
                "base64"
        );
    }

    private ContractExpectations expectations() {
        // Prevention
        Expectation preventionExpectation = new Expectation();
        preventionExpectation.setType(PREVENTION);
        preventionExpectation.setName("Expect inject to be prevented");
        preventionExpectation.setScore(0);
        // Detection
        Expectation detectionExpectation = new Expectation();
        detectionExpectation.setType(DETECTION);
        detectionExpectation.setName("Expect inject to be detected");
        detectionExpectation.setScore(0);

        return expectationsField(
                "expectations", "Expectations", List.of(preventionExpectation, detectionExpectation)
        );
    }

    private List<Contract> abilityContracts(@NotNull final ContractConfig contractConfig) {
        // Fields
        ContractSelect obfuscatorField = obfuscatorField();
        ContractAsset assetField = assetField("assets", "Assets", Multiple);
        ContractAssetGroup assetGroupField = assetGroupField("assetgroups", "Asset groups",
                Multiple);
        // Expectations
        ContractExpectations expectationsField = expectations();

        List<Ability> abilities = this.injectorCalderaService.abilities();
        // Build contracts
        return abilities.stream().map((ability -> {
            ContractDef builder = contractBuilder();
            builder.mandatoryGroup(assetField, assetGroupField);
            builder.optional(obfuscatorField);
            builder.optional(expectationsField);
            List<String> platforms = new ArrayList<>();
            ability.getExecutors().forEach(executor -> {
                if (!executor.getPlatform().equals("unknown")) {
                    String platform = toPlatform(executor.getPlatform()).name();
                    if (!platforms.contains(platform)) {
                        platforms.add(platform);
                    }
                } else {
                    if (executor.getName().equals("psh")) {
                        if (!platforms.contains(Endpoint.PLATFORM_TYPE.Windows.name())) {
                            platforms.add(Endpoint.PLATFORM_TYPE.Windows.name());
                        }
                    } else if (executor.getName().equals("sh")) {
                        if (!platforms.contains(Endpoint.PLATFORM_TYPE.Linux.name())) {
                            platforms.add(Endpoint.PLATFORM_TYPE.Linux.name());
                        }
                    } else if (executor.getName().equals("cmd")) {
                        if (!platforms.contains(Endpoint.PLATFORM_TYPE.Windows.name())) {
                            platforms.add(Endpoint.PLATFORM_TYPE.Windows.name());
                        }
                    }
                }
            });
            Contract contract = executableContract(
                    contractConfig,
                    ability.getAbility_id(),
                    Map.of(en, ability.getName(), fr, ability.getName()),
                    builder.build(),
                    platforms,
                    true
            );
            contract.addAttackPattern(ability.getTechnique_id());
            return contract;
        })).collect(Collectors.toList());
    }

    @Override
    public ContractorIcon getIcon() {
        InputStream iconStream = getClass().getResourceAsStream("/img/icon-caldera.png");
        return new ContractorIcon(iconStream);
    }
}

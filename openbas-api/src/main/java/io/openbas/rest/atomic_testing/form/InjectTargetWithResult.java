package io.openbas.rest.atomic_testing.form;

import io.openbas.database.model.Endpoint;
import io.openbas.utils.AtomicTestingMapper.ExpectationResultsByType;
import io.openbas.atomic_testing.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class InjectTargetWithResult {

  private final TargetType targetType;
  private final Endpoint.PLATFORM_TYPE platformType;
  @NotBlank
  private final String id;
  private final String name;
  private final List<ExpectationResultsByType> expectationResultsByTypes;
  private final List<InjectTargetWithResult> children = new ArrayList<>();

  public InjectTargetWithResult(@NotNull TargetType targetType, @NotNull String id, @NotNull String name, @NotNull List<ExpectationResultsByType> expectationResultsByTypes,
      @NotNull List<InjectTargetWithResult> children,  Endpoint.PLATFORM_TYPE platformType) {
    this.targetType = targetType;
    this.platformType = platformType;
    this.id = id;
    this.name = name;
    this.expectationResultsByTypes = expectationResultsByTypes;
    this.children.addAll(children);
  }

  public InjectTargetWithResult(@NotNull TargetType targetType, @NotNull String id, @NotNull String name, @NotNull List<ExpectationResultsByType> expectationResultsByTypes,  Endpoint.PLATFORM_TYPE platformType) {
    this.targetType = targetType;
    this.platformType = platformType;
    this.id = id;
    this.name = name;
    this.expectationResultsByTypes = expectationResultsByTypes;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

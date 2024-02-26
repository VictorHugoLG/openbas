package io.openex.rest.team.form;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static io.openex.config.AppConfig.MANDATORY_MESSAGE;

@Data
public class TeamCreateInput {

  @NotBlank(message = MANDATORY_MESSAGE)
  @JsonProperty("team_name")
  private String name;

  @JsonProperty("team_description")
  private String description;

  @JsonProperty("team_organization")
  private String organizationId;

  @JsonProperty("team_tags")
  private List<String> tagIds = new ArrayList<>();

  @JsonProperty("team_exercises")
  private List<String> exerciseIds = new ArrayList<>();

  @JsonProperty("team_scenarios")
  private List<String> scenarioIds = new ArrayList<>();

  @JsonProperty("team_contextual")
  private Boolean contextual = false;
}

package io.openex.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openex.database.model.Challenge;
import io.openex.database.model.Exercise;
import io.openex.database.model.Inject;
import io.openex.database.model.Scenario;
import io.openex.database.repository.ChallengeRepository;
import io.openex.database.repository.ExerciseRepository;
import io.openex.database.repository.InjectRepository;
import io.openex.injects.challenge.model.ChallengeContent;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static io.openex.helper.StreamHelper.fromIterable;
import static io.openex.injects.challenge.ChallengeContract.CHALLENGE_PUBLISH;

@Service
public class ChallengeService {

  @Resource
  protected ObjectMapper mapper;

  private ExerciseRepository exerciseRepository;
  private ChallengeRepository challengeRepository;
  private InjectRepository injectRepository;

  @Autowired
  public void setInjectRepository(InjectRepository injectRepository) {
    this.injectRepository = injectRepository;
  }

  @Autowired
  public void setChallengeRepository(ChallengeRepository challengeRepository) {
    this.challengeRepository = challengeRepository;
  }

  @Autowired
  public void setExerciseRepository(ExerciseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }

  public Challenge enrichChallengeWithExercisesOrScenarios(@NotNull Challenge challenge) {
    List<Inject> injects = fromIterable(this.injectRepository.findAllForChallengeId("%" + challenge.getId() + "%"));
    List<String> exerciseIds = injects.stream().filter(i -> i.getExercise() != null).map(i -> i.getExercise().getId()).distinct().toList();
    challenge.setExerciseIds(exerciseIds);
    List<String> scenarioIds = injects.stream().filter(i -> i.getScenario() != null).map(i -> i.getScenario().getId()).distinct().toList();
    challenge.setScenarioIds(scenarioIds);
    return challenge;
  }

  public Iterable<Challenge> getExerciseChallenges(@NotBlank final String exerciseId) {
    Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();
    return resolveChallenges(exercise.getInjects())
        .map(this::enrichChallengeWithExercisesOrScenarios)
        .toList();
  }

  public Iterable<Challenge> getScenarioChallenges(@NotNull final Scenario scenario) {
    return resolveChallenges(scenario.getInjects())
        .map(this::enrichChallengeWithExercisesOrScenarios)
        .toList();
  }

  // -- PRIVATE --

  private Stream<Challenge> resolveChallenges(@NotNull final List<Inject> injects) {
    List<String> challenges = injects.stream()
        .filter(inject -> inject.getContract().equals(CHALLENGE_PUBLISH))
        .filter(inject -> inject.getContent() != null)
        .flatMap(inject -> {
          try {
            ChallengeContent content = this.mapper.treeToValue(inject.getContent(), ChallengeContent.class);
            return content.getChallenges().stream();
          } catch (JsonProcessingException e) {
            return Stream.empty();
          }
        })
        .distinct().toList();
    return fromIterable(this.challengeRepository.findAllById(challenges)).stream();
  }
}

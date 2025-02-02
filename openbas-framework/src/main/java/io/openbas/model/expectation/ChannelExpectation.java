package io.openbas.model.expectation;

import io.openbas.database.model.Article;
import io.openbas.database.model.InjectExpectation;
import io.openbas.model.Expectation;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ChannelExpectation implements Expectation {

  private Integer score;
  private Article article;

  public ChannelExpectation(Integer score, Article article) {
    setScore(Objects.requireNonNullElse(score, 100));
    setArticle(article);
  }

  @Override
  public InjectExpectation.EXPECTATION_TYPE type() {
    return InjectExpectation.EXPECTATION_TYPE.ARTICLE;
  }

}

package io.openex.helper;

import io.openex.database.model.Grant;
import io.openex.database.model.User;

import java.util.List;

import static java.util.Arrays.asList;

public class UserHelper {

  public static List<User> getUsersByType(List<Grant> grants, Grant.GRANT_TYPE... types) {
    return grants.stream()
        .filter(grant -> asList(types).contains(grant.getName()))
        .map(Grant::getGroup)
        .flatMap(group -> group.getUsers().stream())
        .distinct()
        .toList();
  }

}

package io.openex.rest.exercise.exports;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openex.database.model.*;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFileExport {

    @JsonProperty("export_version")
    private int version;

    @JsonProperty("exercise_information")
    private Exercise exercise;

    @JsonProperty("exercise_audiences")
    private List<Audience> audiences = new ArrayList<>();

    @JsonProperty("exercise_objectives")
    private List<Objective> objectives = new ArrayList<>();

    @JsonProperty("exercise_users")
    private List<User> users = new ArrayList<>();

    @JsonProperty("exercise_organizations")
    private List<Organization> organizations = new ArrayList<>();

    @JsonProperty("exercise_injects")
    private List<Inject> injects = new ArrayList<>();

    @JsonProperty("exercise_tags")
    private List<Tag> tags = new ArrayList<>();

    @JsonProperty("exercise_documents")
    private List<Document> documents = new ArrayList<>();

    @JsonProperty("exercise_medias")
    private List<Media> medias = new ArrayList<>();

    @JsonProperty("exercise_articles")
    private List<Article> articles = new ArrayList<>();

    @JsonProperty("exercise_challenges")
    private List<Challenge> challenges = new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<Audience> getAudiences() {
        return audiences;
    }

    public void setAudiences(List<Audience> audiences) {
        this.audiences = audiences;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<Objective> objectives) {
        this.objectives = objectives;
    }

    public List<Inject> getInjects() {
        return injects;
    }

    public void setInjects(List<Inject> injects) {
        this.injects = injects;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }
}

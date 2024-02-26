package io.openex.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openex.database.audit.ModelBaseListener;
import io.openex.helper.MultiIdDeserializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "documents")
@EntityListeners(ModelBaseListener.class)
public class Document implements Base {

    @Id
    @Column(name = "document_id")
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @JsonProperty("document_id")
    @NotBlank
    private String id;

    @Column(name = "document_name")
    @JsonProperty("document_name")
    private String name;

    @Column(name = "document_target")
    @JsonProperty("document_target")
    private String target;

    @Column(name = "document_description")
    @JsonProperty("document_description")
    private String description;

    @Column(name = "document_type")
    @JsonProperty("document_type")
    private String type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "documents_tags",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonSerialize(using = MultiIdDeserializer.class)
    @JsonProperty("document_tags")
    private List<Tag> tags = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "exercises_documents",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id"))
    @JsonSerialize(using = MultiIdDeserializer.class)
    @JsonProperty("document_exercises")
    private List<Exercise> exercises = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "scenarios_documents",
        joinColumns = @JoinColumn(name = "document_id"),
        inverseJoinColumns = @JoinColumn(name = "scenario_id"))
    @JsonSerialize(using = MultiIdDeserializer.class)
    @JsonProperty("document_scenarios")
    private List<Scenario> scenarios = new ArrayList<>();

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InjectDocument> injectDocuments = new ArrayList<>();

    @Override
    public boolean isUserHasAccess(User user) {
        return exercises.stream().anyMatch(exercise -> exercise.isUserHasAccess(user));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !Base.class.isAssignableFrom(o.getClass())) return false;
        Base base = (Base) o;
        return id.equals(base.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

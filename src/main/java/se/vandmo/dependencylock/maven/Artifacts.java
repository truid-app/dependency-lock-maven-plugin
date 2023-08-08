package se.vandmo.dependencylock.maven;

import org.apache.maven.project.MavenProject;

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class Artifacts implements Iterable<Artifact> {

  public final List<Artifact> artifacts;

  private Artifacts(Collection<Artifact> artifacts) {
    ArrayList<Artifact> copy = new ArrayList<>(artifacts);
    sort(copy);
    this.artifacts = unmodifiableList(copy);
  }

  private static Boolean matchArtifact(org.apache.maven.artifact.Artifact a, MavenProject p) {
    return (
      a.getArtifactId().equals(p.getModel().getArtifactId()) &&
      a.getGroupId().equals(p.getModel().getGroupId())
    );
  }

  public static Artifacts fromMavenArtifacts(
    Collection<org.apache.maven.artifact.Artifact> artifacts,
    Collection<MavenProject> reactorProjects,
    Boolean excludeReactor
  ) {
    if (excludeReactor) {
      return new Artifacts(
        artifacts.stream()
          .filter(a -> reactorProjects.stream().noneMatch(p -> matchArtifact(a, p)))
          .map(a -> Artifact.from(a))
          .collect(toList())
      );
    } else {
      return new Artifacts(artifacts.stream().map(a -> Artifact.from(a)).collect(toList()));
    }
  }

  public static Artifacts fromArtifacts(Collection<Artifact> artifacts) {
    return new Artifacts(artifacts);
  }

  public Optional<Artifact> by(ArtifactIdentifier identifier) {
    for (Artifact artifact : artifacts) {
      if (identifier.equals(artifact.identifier)) {
        return Optional.of(artifact);
      }
    }
    return Optional.empty();
  }

  @Override
  public Iterator<Artifact> iterator() {
    return artifacts.iterator();
  }
}

package com.example.yaml_parser;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Merger {

  public Props intersection(List<Props> props) {

    if (props.isEmpty()) {
      throw new RuntimeException("Empty props");
    }

    Props lead = props.get(0);

    String newName =
        props.stream().map(Props::getName).collect(Collectors.joining("-")) + "-intersection";

    return new Props(
        lead.getFlatten().entrySet().stream()
            .filter(
                entry ->
                    props.stream()
                        .allMatch(prop -> entry.getValue().equals(prop.find(entry.getKey()))))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
        newName);
  }

  public Props left(Props left, Props right) {
    return new Props(
        left.getFlatten().entrySet().stream()
            .filter(leftEntry -> !leftEntry.getValue().equals(right.find(leftEntry.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
        left.getName() + "-spec");
  }

  public Set<String> leftKeys(Props left, Props right) {
    return left.getFlatten().keySet().stream()
        .map(Object::toString)
        .filter(leftKey -> right.find(leftKey) == null)
        .collect(Collectors.toSet());
  }
}

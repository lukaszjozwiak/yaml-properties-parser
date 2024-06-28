package com.example.yaml_parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
@RequiredArgsConstructor
public class YamlReader {

  private final FileUtils fileUtils;

  public List<Map<Object, Object>> readYaml(String fileName) {
    return readAllDocuments(split(fileUtils.readFromFile(fileName)));
  }

  private List<Map<Object, Object>> readAllDocuments(String[] documents) {
    return Arrays.stream(documents).map(this::readDocument).toList();
  }

  private Map<Object, Object> readDocument(String data) {
    Yaml yaml = new Yaml();
    return yaml.load(data);
  }

  private String[] split(String data) {
    return Arrays.stream(data.split("---"))
        .filter(doc -> !doc.trim().isEmpty())
        .toArray(String[]::new);
  }
}

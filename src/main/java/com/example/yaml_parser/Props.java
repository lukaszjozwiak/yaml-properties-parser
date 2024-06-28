package com.example.yaml_parser;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Getter
public class Props {

  private final String name;
  private final Map<Object, Object> flatten;

  public Props(Map<Object, Object> data, String defaultName) {
    this.flatten = flatten(data);
    this.name = initName(defaultName);
  }

  public void add(Object key, Object value) {
    flatten.put(key, value);
  }

  public Object find(Object key) {
    return flatten.get(key);
  }

  public Map<Object, Object> getData() {
    return unflatten();
  }

  private Map<Object, Object> flatten(Map<Object, Object> data) {
    Map<Object, Object> flatMap = new TreeMap<>();
    flatten(data, "", flatMap);
    return flatMap;
  }

  private void flatten(Map<Object, Object> nestedMap, String prefix, Map<Object, Object> flatMap) {
    for (Map.Entry<Object, Object> entry : nestedMap.entrySet()) {
      String keyText = entry.getKey().toString();
      String key = prefix.isEmpty() ? keyText : prefix + "." + keyText;
      if (entry.getValue() instanceof Map) {
        flatten((Map<Object, Object>) entry.getValue(), key, flatMap);
      } else {
        flatMap.put(key, entry.getValue());
      }
    }
  }

  public Map<Object, Object> unflatten() {
    Map<Object, Object> nestedMap = new TreeMap<>();
    for (Map.Entry<Object, Object> entry : flatten.entrySet()) {
      String[] keys = entry.getKey().toString().split("\\.");
      Map<Object, Object> currentMap = nestedMap;
      for (int i = 0; i < keys.length - 1; i++) {
        currentMap =
            (Map<Object, Object>) currentMap.computeIfAbsent(keys[i], k -> new TreeMap<>());
      }
      currentMap.put(keys[keys.length - 1], entry.getValue());
    }
    return nestedMap;
  }

  private String initName(String defaultName) {
    Object profileName = flatten.get("spring.config.activate.on-profile");
    Object groupName = flatten.get("spring.profiles.group.local-env");

    String finalName;

    if (profileName != null) {
      finalName = profileName.toString().replaceAll("\\s", "").replaceAll(",", "-");
    } else if (groupName != null) {
      finalName = groupName.toString().replaceAll("\\s", "").replaceAll(",", "-");
    } else {
      finalName = defaultName;
    }

    return finalName;
  }

  public static Props findByName(List<Props> props, String name) {
    return props.stream().filter(prop -> prop.getName().equals(name)).findAny().orElseThrow();
  }

  @Override
  public String toString() {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setIndent(2);
    options.setPrettyFlow(true);
    Yaml yaml = new Yaml(options);
    String rawYaml = yaml.dump(unflatten());

    String[] lines = rawYaml.split("\n");
    StringBuilder formated = new StringBuilder();

    for (int i = 0; i < lines.length; i++) {

      if (i != 0 && lines[i].matches("^\\S.*")) {
        formated.append("\n").append(lines[i]).append("\n");
      } else {
        formated.append(lines[i]).append("\n");
      }
    }

    return formated.toString();
  }
}

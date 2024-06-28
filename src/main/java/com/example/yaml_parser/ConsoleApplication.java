package com.example.yaml_parser;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsoleApplication implements ApplicationRunner {

  private final YamlReader yamlReader;
  private final Merger merger;
  private final FileUtils fileUtils;

  @Override
  public void run(ApplicationArguments args) {
    Props common =
        yamlReader.readYaml("inputs/common.yaml").stream()
            .map(doc -> new Props(doc, "common"))
            .filter(prop -> prop.getName().equals("common"))
            .findAny()
            .orElseThrow();

    Props uat =
        yamlReader.readYaml("inputs/uat.yaml").stream()
            .map(doc -> new Props(doc, "uat"))
            .findAny()
            .orElseThrow();

    Props prod =
        yamlReader.readYaml("inputs/prod.yaml").stream()
            .map(doc -> new Props(doc, "prod"))
            .findAny()
            .orElseThrow();

    Set<String> commonNotProd = merger.leftKeys(common, prod);
    Set<String> commonNotUat = merger.leftKeys(common, uat);
    Set<String> commonNotCommon = merger.leftKeys(uat, common);

    log.info("Properties from common, missing in prod:\n{}", commonNotProd);
    log.info("Properties from common, missing in uat:\n{}", commonNotUat);

    Props intersection = merger.intersection(List.of(uat, prod, common));
    Props uatSpecific = merger.left(uat, common);
    Props prodSpecific = merger.left(prod, common);

    fileUtils.writeToFile(intersection.getName() + ".yaml", intersection.toString());
    fileUtils.writeToFile(uatSpecific.getName() + ".yaml", uatSpecific.toString());
    fileUtils.writeToFile(prodSpecific.getName() + ".yaml", prodSpecific.toString());
  }
}

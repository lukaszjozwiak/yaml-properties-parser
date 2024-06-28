package com.example.yaml_parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUtils {

  private final ApplicationContext context;

  @SneakyThrows
  public String readFromFile(String fileName) {
    Resource resource = context.getResource("classpath:" + fileName);
    File file = resource.getFile();
    return org.apache.commons.io.FileUtils.readFileToString(file, "UTF-8");
  }

  @SneakyThrows
  public void writeToFile(String fileName, String content) {

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      writer.write(content);
    }
  }
}

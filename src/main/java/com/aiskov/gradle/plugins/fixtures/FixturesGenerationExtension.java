package com.aiskov.gradle.plugins.fixtures;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FixturesGenerationExtension {
    String outputDir;
    List<String> targetClasses;
    Map<String, String> valueProviderSnippets;
}

package com.cq.sandbox.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum LanguageImageEnum {

    JAVA("java", "openjdk:8-alpine"),
    ;

    private final String language;
    private final String image;

    /**
     * 获取值列表
     */
    public static List<String> getImages() {
        return Arrays.stream(values()).map(item -> item.image).collect(Collectors.toList());
    }

}

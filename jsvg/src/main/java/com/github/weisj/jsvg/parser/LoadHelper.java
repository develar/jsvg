package com.github.weisj.jsvg.parser;

import com.github.weisj.jsvg.attributes.AttributeParser;
import org.jetbrains.annotations.NotNull;

public interface LoadHelper {
    @NotNull
    AttributeParser attributeParser();

    @NotNull
    ResourceLoader resourceLoader();
}

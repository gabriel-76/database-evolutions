package com.evolutions.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

@Service
public class EvolutionsReader {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private Config config;

    private Stream<Resource> loadResources(String pattern) throws IOException {
        return Arrays.stream(ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern));
    }

    Stream<Resource> loadEvolutions() throws IOException {
        return loadResources(String.format("classpath*:%1$s/*.sql", config.getPath()))
                .sorted((o1, o2) -> valueOf(name(o1))
                .compareTo(parseInt(name(o2))));
    }

    private String name(Resource resource){
        return Objects.requireNonNull(resource.getFilename()).split("\\.sql")[0];
    }

}

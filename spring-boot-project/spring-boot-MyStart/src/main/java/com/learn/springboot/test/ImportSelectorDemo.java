package com.learn.springboot.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author Wang danning
 * @since 2020-03-21 22:56
 **/
@Component
public class ImportSelectorDemo implements ImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		System.out.println("ImportSelectorDemo..selectImports");
		return new String[]{"com.learn.springboot.test.Red"};
	}
}

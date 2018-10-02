package com.spring.demo.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.springframework.integration.file.filters.AbstractFileListFilter;

public class DemoFileFilter extends AbstractFileListFilter<File>{

	private final Object monitor = new Object();

    public final static Set<String> processedFile  = new HashSet<>();
    
    
    @Override
	public boolean accept(File file) {
        synchronized (this.monitor) {
        	return !processedFile.contains(file.getName());
        }
    }
}

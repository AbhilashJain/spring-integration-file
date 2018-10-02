package com.spring.demo.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.messaging.MessageChannel;

import com.spring.demo.service.DemoFileProcessor;

@Configuration
public class SpringIntegrationFileConfig {

	@Value("${demo.file.location}")
	private String directory;
	
	@Bean
	public DemoFileProcessor fileProcessor() {
		return new DemoFileProcessor();
	}
	
	@Bean
	public DemoFileFilter fileFilter() {
		return new DemoFileFilter();
	}
	
	@Bean
	public IntegrationFlow processFileFlow() {
		return IntegrationFlows
				.from("fileInputChannel")
				.handle("fileProcessor", "process").get();
	}

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

	@Bean
	@InboundChannelAdapter(value = "fileInputChannel",autoStartup="true", poller = @Poller(fixedDelay = "1000"))
	public MessageSource<File> fileReadingMessageSource() {
		CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
		filters.addFilter(new SimplePatternFileListFilter("*.xlsx"));
		filters.addFilter(fileFilter());
		FileReadingMessageSource source = new FileReadingMessageSource();
		source.setAutoCreateDirectory(true);
		source.setDirectory(new File(directory));
		source.setFilter(filters);

		return source;
	}

	
}

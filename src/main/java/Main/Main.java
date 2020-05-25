package Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import Main.Model.User;
import Main.Services.GitHubService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class Main implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	@Autowired
	private GitHubService gitHubLookupService;
	
	public static void main(String[] args) {
		// close the application context to shut down the custom ExecutorService
		SpringApplication.run(Main.class, args).close();;
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("GithubLookup-");
		executor.initialize();
		return executor;
	}

	@Override
	public void run(String... args) throws Exception {
		
		CompletableFuture<User> page1 = gitHubLookupService.findUser("itqpleyva");
		CompletableFuture<User> page2 = gitHubLookupService.findUser("r3ello");
		CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");
		CompletableFuture.allOf(page1,page2,page3).join();
		logger.info("--> " + page1.get());
		logger.info("--> " + page2.get());
		logger.info("--> " + page3.get());

	}


}

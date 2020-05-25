package Main.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import Main.Model.User;

import java.util.concurrent.CompletableFuture;

@Service
public class GitHubService {

	  private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

	  private final RestTemplate restTemplate;

	  public GitHubService(RestTemplateBuilder restTemplateBuilder) {
	    this.restTemplate = restTemplateBuilder.build();
	  }

	@Async
	public CompletableFuture<User> findUser(String user) throws InterruptedException {

		logger.info("Searching..: " + user);
		String url = String.format("https://api.github.com/users/%s", user);
		User results = restTemplate.getForObject(url, User.class);
		return CompletableFuture.completedFuture(results);
	}

}

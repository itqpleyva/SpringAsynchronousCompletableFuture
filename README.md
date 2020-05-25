# SpringAsynchronousCompletableFuture
<h3>Spring Boot example of the execution of asynchronous methods using @Async and CompletableFuture</h3>
<br>
This example consumes the service provided by Github to obtain account information (https://api.github.com/users/). In the run method of the main class, the data of three accounts are requested, for this, three requests of the gitHubLookupService.findUser() service are executed asynchronously.
<br>
Main dependencies:
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
<br>
Creating User Model:

      @JsonIgnoreProperties(ignoreUnknown=true)
      public class User {

        private String name;
        private String blog;

        public String getName() {
          return name;
        }

        public void setName(String name) {
          this.name = name;
        }

        public String getBlog() {
          return blog;
        }

        public void setBlog(String blog) {
          this.blog = blog;
        }

        @Override
        public String toString() {
          return "User [name=" + name + ", blog=" + blog + "]";
        }

      }

<br>
Creating Github Service:

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
      <br>
Consuming Github Service:

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

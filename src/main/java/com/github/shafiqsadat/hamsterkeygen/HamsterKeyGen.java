package com.github.shafiqsadat.hamsterkeygen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class HamsterKeyGen {
    private static final Logger logger = LogManager.getLogger(HamsterKeyGen.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<Integer, Game> games = new LinkedHashMap<>();

    static {
        games.put(1, new Game("Riding Extreme 3D", "d28721be-fd2d-4b45-869e-9f253b554e50", "43e35910-c168-4634-ad4f-52fd764a843f", 25000, 25));
        games.put(2, new Game("Chain Cube 2048", "d1690a07-3780-4068-810f-9b5bbf2931b2", "b4170868-cef0-424f-8eb9-be0622e8e8e3", 25000, 20));
        games.put(3, new Game("My Clone Army", "74ee0b5b-775e-4bee-974f-63e7f4d5bacb", "fe693b26-b342-4159-8808-15e3ff7f8767", 180000, 30));
        games.put(4, new Game("Train Miner", "82647f43-3f87-402d-88dd-09a90025313f", "c4480ac7-e178-4973-8061-9ed5b2e17954", 20000, 15));
        games.put(5, new Game("Merge Away", "8d1cc2ad-e097-4b86-90ef-7a27e19fb833", "dc128d28-c45b-411c-98ff-ac7726fbaea4", 20000, 25));
        games.put(6, new Game("Twerk Race 3D", "61308365-9d16-4040-8bb0-2f4a4c69074c", "61308365-9d16-4040-8bb0-2f4a4c69074c", 20000, 20));
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a game:");
        games.forEach((key, value) -> System.out.println(key + ": " + value.name));
        int gameChoice = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter the number of keys to generate: ");
        int keyCount = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter the proxy file path (leave empty to use 'proxy.txt'): ");
        String proxyFilePath = scanner.nextLine().isEmpty() ? "proxy.txt" : scanner.nextLine();

        List<String> proxies = loadProxies(proxyFilePath);

        logger.info("Generating {} key(s) for {} using proxies from {}",
                keyCount, games.get(gameChoice).name, proxies.isEmpty() ? "no proxies" : proxyFilePath);

        ExecutorService executor = Executors.newFixedThreadPool(keyCount);
        List<CompletableFuture<String>> tasks = new ArrayList<>();

        for (int i = 0; i < keyCount; i++) {
            tasks.add(CompletableFuture.supplyAsync(() -> generateKeyProcess(games.get(gameChoice), proxies), executor));
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
        allOf.get();

        List<String> keys = new ArrayList<>();
        for (CompletableFuture<String> task : tasks) {
            keys.add(task.get());
        }

        executor.shutdown();

        String fileName = games.get(gameChoice).name.replace(" ", "_").toLowerCase() + "_keys.txt";
        Files.write(Paths.get(fileName), keys);

        logger.info("Generated Key(s) were successfully saved to {}.", fileName);
        System.out.println("Press enter to exit");
        scanner.nextLine();
    }

    private static List<String> loadProxies(String filePath) {
        try {
            List<String> proxies = Files.readAllLines(Paths.get(filePath));
            Collections.shuffle(proxies);
            return proxies;
        } catch (IOException e) {
            logger.error("Error reading proxy file {}: {}", filePath, e.getMessage());
            return Collections.emptyList();
        }
    }

    private static String generateKeyProcess(Game game, List<String> proxies) {
        try {
            String clientId = generateClientId();
            logger.info("Generated client ID: {}", clientId);

            String clientToken = login(clientId, game.appToken, proxies);
            if (clientToken == null) {
                logger.error("Failed to generate client token for client ID: {}", clientId);
                return null;
            }

            for (int i = 0; i < game.attempts; i++) {
                logger.info("Emulating progress event {}/{} for client ID: {}", i + 1, game.attempts, clientId);
                boolean hasCode = emulateProgress(clientToken, game.promoId, proxies, game.attempts, game.timing, i);
                if (hasCode) {
                    logger.info("Progress event triggered key generation for client ID: {}", clientId);
                    break;
                }
                Thread.sleep(game.timing);
            }

            return generateKey(clientToken, game.promoId, proxies);
        } catch (Exception e) {
            logger.error("Error during key generation: {}", e.getMessage());
            return null;
        }
    }

    private static String generateClientId() {
        long timestamp = Instant.now().toEpochMilli();
        String randomNumbers = ThreadLocalRandom.current().ints(0, 10)
                .limit(19)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return timestamp + "-" + randomNumbers;
    }

    private static String login(String clientId, String appToken, List<String> proxies) throws IOException, InterruptedException {
        String proxy = proxies.isEmpty() ? null : proxies.get(ThreadLocalRandom.current().nextInt(proxies.size()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.gamepromo.io/promo/login-client"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"appToken\":\"" + appToken + "\",\"clientId\":\"" + clientId + "\",\"clientOrigin\":\"deviceid\"}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            var data = objectMapper.readValue(response.body(), Map.class);
            logger.info("Login successful for client ID: {}", clientId);
            return (String) data.get("clientToken");
        } else {
            logger.error("Failed to login: {}", response.body());
            return null;
        }
    }

    private static boolean emulateProgress(String clientToken, String promoId, List<String> proxies, int attempts, int timeOut, int currentAttempt) throws IOException, InterruptedException {
        String proxy = proxies.isEmpty() ? null : proxies.get(ThreadLocalRandom.current().nextInt(proxies.size()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.gamepromo.io/promo/register-event"))
                .header("Authorization", "Bearer " + clientToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"promoId\":\"" + promoId + "\",\"eventId\":\"" + UUID.randomUUID() + "\",\"eventOrigin\":\"undefined\"}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var data = objectMapper.readValue(response.body(), Map.class);
        if (response.statusCode() == 200) {
            System.out.println(data);
            return (Boolean) data.get("hasCode");
        } else if (response.body().contains("TooManyRegister")) {
            logger.error("Failed to emulate progress (attempt {}/{}}): {}", currentAttempt, attempts, response.body());
            Thread.sleep(timeOut); // Wait 30 seconds before retrying
        } else {
            logger.error("Failed to emulate progress: {}", response.body());
            return false;
        }

        return false;
    }

    private static String generateKey(String clientToken, String promoId, List<String> proxies) throws IOException, InterruptedException {
        String proxy = proxies.isEmpty() ? null : proxies.get(ThreadLocalRandom.current().nextInt(proxies.size()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.gamepromo.io/promo/create-code"))
                .header("Authorization", "Bearer " + clientToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"promoId\":\"" + promoId + "\"}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            var data = objectMapper.readValue(response.body(), Map.class);
            String key = (String) data.get("promoCode");
            logger.info("Generated key: {}", key);
            return key;
        } else {
            logger.error("Failed to generate key: {}", response.body());
            return null;
        }
    }

    static class Game {
        String name;
        String appToken;
        String promoId;
        int timing;
        int attempts;

        Game(String name, String appToken, String promoId, int timing, int attempts) {
            this.name = name;
            this.appToken = appToken;
            this.promoId = promoId;
            this.timing = timing;
            this.attempts = attempts;
        }
    }
}


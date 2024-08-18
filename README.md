
# HamsterKeyGenJava

![image](https://github.com/user-attachments/assets/18ed1781-bd34-45ff-84a8-4c96043bdda7)

This is a Java application to generate Hamster Kombat Keys.

## Web Version

- [HamsterKeyGenWeb](https://github.com/ShafiqSadat/HamsterKeyGenWeb)

## Python Version

- [HamsterKeyGen](https://github.com/ShafiqSadat/HamsterKeyGen)

## ⭐ 💹 Need bulk keys? 
- Contact me: [Telegram](https://t.me/Shafiq)

## Features
- Generate unique client IDs.
- Authenticate and obtain client tokens.
- Emulate user progress and register events.
- Generate and retrieve promo keys.
- Optional proxy support.
- Save keys into a file.

## Requirements
- Java 11 or higher
- Maven (optional for dependency management)

## Installation

### Option 1: Running with Maven

1. **Clone the repository:**
    ```sh
    git clone https://github.com/ShafiqSadat/HamsterKeyGenJava.git
    cd HamsterKeyGenJava
    ```

2. **Build the project with Maven:**
    ```sh
    mvn clean install
    ```

3. **Run the application:**
    ```sh
    mvn exec:java -Dexec.mainClass="com.github.shafiqsadat.hamsterkeygen.HamsterKeyGen"
    ```

### Option 2: Running without Maven

1. **Clone the repository:**
    ```sh
    git clone https://github.com/ShafiqSadat/HamsterKeyGenJava.git
    cd HamsterKeyGenJava
    ```

2. **Compile the Java files:**
    ```sh
    javac -d out -sourcepath src src/com/github/shafiqsadat/hamsterkeygen/HamsterKeyGen.java
    ```

3. **Run the application:**
    ```sh
    java -cp out com.github.shafiqsadat.hamsterkeygen.HamsterKeyGen
    ```

## Usage

1. Open a terminal and navigate to the directory containing the compiled Java files.
2. Run the application using one of the above methods.
3. Follow the prompts to enter the game number and the number of keys you want to generate.

### Using Proxies

You can optionally use a proxy by specifying a proxy file. If no proxy file is provided, the script will look for `proxy.txt` in the same directory.

1. Create a file named `proxy.txt` in the same directory as the compiled Java files.
2. Add your proxy URL to the `proxy.txt` file. For example:

#### HTTP Proxy
```
http://proxy-server:8080
```

#### HTTPS Proxy
```
https://proxy-server:8080
```

#### SOCKS5 Proxy
```
socks5://proxy-server:1080
```

#### SOCKS4 Proxy
```
socks4://proxy-server:1080
```

When running the application, the program will attempt to use `proxy.txt` by default if no other proxy file is specified.

### Application Details

- **generateClientId**: Generates a unique client ID.
- **login**: Authenticates using the client ID and returns a client token.
- **emulateProgress**: Simulates user progress and registers an event.
- **generateKey**: Generates a promo key using the client token.
- **generateKeyProcess**: Orchestrates the process of generating a key.
- **main**: Main function to generate multiple keys concurrently.

## Stargazers over time
[![Stargazers over time](https://starchart.cc/ShafiqSadat/HamsterKeyGenJava.svg?variant=adaptive)](https://starchart.cc/ShafiqSadat/HamsterKeyGenJava)

## License
This project is licensed under the GPL-3.0 license.

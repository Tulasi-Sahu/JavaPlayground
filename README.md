**JavaPlayground**

**JavaPlayground** is a small collection of Java desktop applications showcasing interactive and fun projects built with **Java Swing**. The repository currently includes two projects:

1. **Weather Dashboard** – Displays real-time weather information with animated visuals.
2. **Snake Game** – A classic snake game with color-changing mechanics and increasing difficulty.

## 1. Weather Dashboard
### What it does
The Weather Dashboard lets you check the current weather and hourly forecast for popular cities. The application:
* Fetches live weather data using the **OpenWeatherMap API**.
* Displays temperature, humidity, wind speed, sunrise/sunset time, and a short description.
* Animates the background depending on weather (sun, clouds, rain, or night).
* Offers a **dark/light mode toggle** for better readability.

### Requirements
* **Java SE 8** or higher.
* **Internet connection** to fetch weather data.
* **JSON library** (`org.json`) for parsing API responses.
* An **OpenWeatherMap API key** (replace `API_KEY` in the code with your own).

### How to run
1. Clone this repository:
git clone https://github.com/yourusername/JavaPlayground.git
cd JavaPlayground
2. Add the JSON library to your classpath.
3. Compile the code:
javac -cp .;json-20230618.jar WeatherApp.java
4. Run the application:
java -cp .;json-20230618.jar WeatherApp

### How to use
* Select a city from the dropdown menu at the top.
* View the current temperature, humidity, wind speed, sunrise/sunset time, and a short weather description.
* Scroll through the hourly forecast at the bottom.
* Click the **Toggle Dark/Light Mode** button to switch themes.
* Observe animated weather effects in the background that match the current weather.
  
## 2. Snake Game
### What it does
The Snake Game is a classic arcade game implemented in Java:
* Control the snake using **arrow keys**.
* Eat food to grow the snake and increase your score.
* Each food eaten changes the color of the snake dynamically.
* The game speeds up slightly as you progress.
* The game ends if the snake collides with the walls or itself.
  
### Requirements
* **Java SE 8** or higher.
* No internet connection or external libraries required.

### How to run
1. Compile the game:
javac SnakeGame.java
2. Run the game:
java SnakeGame

### How to play
* Use the **arrow keys** to move the snake up, down, left, or right.
* Eat colored food that appears randomly on the grid to grow.
* Avoid hitting the walls or your own tail.
* When the game ends, click **RESTART** to play again or **EXIT** to close the game.

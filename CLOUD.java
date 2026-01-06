import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.*;
import org.json.JSONObject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;
import java.util.Timer;
import java.util.HashMap;
import java.util.Map;

public class WeatherForecastGUI extends JFrame {

    private static final String API_KEY = "5b7b9c09fb40ff7d9c731f9e40473176"; // Replace with your OpenWeatherMap API key
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

    private JTextField cityField;
    private JTextArea resultArea;
    private JLabel weatherIconLabel, overallWeatherLabel;
    private JLabel temperatureLabel, humidityLabel, windSpeedLabel, pressureLabel, sunriseLabel, sunsetLabel;
    private JLabel dateTimeLabel;  // New date and time label
    private BackgroundPanel mainPanel; // Make mainPanel an instance variable to access it later

    // Map to hold weather condition to background image mappings
    private Map<String, String> weatherBackgroundMap;

    public WeatherForecastGUI() {
        setTitle("Weather Forecast");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Initialize the weather background map
        initializeWeatherBackgroundMap();

        // Set the application icon
        setIconImage(new ImageIcon("C:/JavaProject/3d-weather-icon-sun-and-wind-free-png.png").getImage()); // Set your own icon path

        // Custom panel with background image
        mainPanel = new BackgroundPanel(new ImageIcon("C:\\JavaProject\\default-background.jpg").getImage()); // Set your default background image path
        mainPanel.setLayout(new BorderLayout());

        // Panel to hold input field and button
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setOpaque(false); // Set panel transparent to see the background

        JLabel cityLabel = new JLabel("Enter City: ");
        cityLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\city-icon.png"), 52, 52)); // Add a city icon, replace path with your city icon path
        cityField = new JTextField(15);
        JButton fetchButton = new JButton("Check Weather");

        // Set an icon for the button
        fetchButton.setIcon(new ImageIcon(new ImageIcon("C:/JavaProject/fetch_icon.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH))); // Set your own fetch button icon path

        // Action listener to handle button click
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText().trim();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a city name.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Adding components to panel
        panel.add(cityLabel);
        panel.add(cityField);
        panel.add(fetchButton);

        // Text area to display weather data
        resultArea = new JTextArea(10, 35);
        resultArea.setEditable(false); // Read-only
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        // Main panel for weather details and icons
        JPanel weatherDetailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        weatherDetailsPanel.setOpaque(false); // Set panel transparent to see the background

        weatherIconLabel = new JLabel();
        weatherIconLabel.setHorizontalAlignment(JLabel.CENTER);

        // Initialize labels with default icons, resized to 32x32 pixels
        temperatureLabel = new JLabel("Temperature: ");
        temperatureLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\temperature-icon.png"), 100, 100));

        humidityLabel = new JLabel("Humidity: ");
        humidityLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\humidity-icon.png"), 100, 100));

        windSpeedLabel = new JLabel("Wind Speed: ");
        windSpeedLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\wind-icon.png"), 100, 100));

        pressureLabel = new JLabel("Pressure: ");
        pressureLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\pressure-icon.png"), 100, 100));

        sunriseLabel = new JLabel("Sunrise: ");
        sunriseLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\sunrise-icon.png"), 100, 100));

        sunsetLabel = new JLabel("Sunset: ");
        sunsetLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\sunset-icon.png"), 100, 100));

        // Label for displaying overall weather
        overallWeatherLabel = new JLabel("Weather: ");
        overallWeatherLabel.setIcon(resizeIcon(new ImageIcon("C:\\JavaProject\\weather-icon.png"), 100, 100)); // Example icon, adjust path accordingly

        // Add labels to the panel
        weatherDetailsPanel.add(overallWeatherLabel);
        weatherDetailsPanel.add(temperatureLabel);
        weatherDetailsPanel.add(humidityLabel);
        weatherDetailsPanel.add(windSpeedLabel);
        weatherDetailsPanel.add(pressureLabel);
        weatherDetailsPanel.add(sunriseLabel);
        weatherDetailsPanel.add(sunsetLabel);
        weatherDetailsPanel.add(weatherIconLabel); // Optionally add the weather icon

        // Date and time label at the center
        dateTimeLabel = new JLabel();
        dateTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 30));  // Set bold and larger font size
        dateTimeLabel.setForeground(Color.WHITE); // Set color to white for contrast
        updateDateTime();  // Initialize date and time

        // Schedule date and time update every second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateDateTime();
            }
        }, 0, 1000);  // Update every second

        // Create a new panel to stack dateTimeLabel and weatherDetailsPanel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);  // Transparent panel to show background

        // Add dateTimeLabel and weatherDetailsPanel to the center panel
        centerPanel.add(dateTimeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some spacing
        centerPanel.add(weatherDetailsPanel);

        // Adding components to the main panel
        mainPanel.add(panel, BorderLayout.NORTH);
        //mainPanel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);  // Add centerPanel to the center

        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        // Revalidate and repaint to ensure components are updated
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Initializes the mapping between weather conditions and background image paths.
     * Make sure to replace the image paths with your actual image locations.
     */
    private void initializeWeatherBackgroundMap() {
        weatherBackgroundMap = new HashMap<>();
        // Example mappings. You should add more mappings based on the weather conditions you want to handle.
        weatherBackgroundMap.put("Clear", "C:\\JavaProject\\backgrounds\\clear.jpg");
        weatherBackgroundMap.put("Clouds", "C:\\JavaProject\\backgrounds\\cloudy.jpg");
        weatherBackgroundMap.put("Rain", "C:\\JavaProject\\backgrounds\\rain.jpg");
        weatherBackgroundMap.put("Drizzle", "C:\\JavaProject\\backgrounds\\drizzle.jpg");
        weatherBackgroundMap.put("Thunderstorm", "C:\\JavaProject\\backgrounds\\thunderstorm.jpg");
        weatherBackgroundMap.put("Snow", "C:\\JavaProject\\backgrounds\\snow.jpg");
        weatherBackgroundMap.put("Mist", "C:\\JavaProject\\backgrounds\\mist.jpg");
        // Add more conditions as needed
        weatherBackgroundMap.put("Default", "C:\\JavaProject\\backgrounds\\default-background.jpg");
    }

    private void updateDateTime() {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy HH:mm:ss");  // Format for day, date, and time
        String formattedDateTime = now.format(formatter);
        dateTimeLabel.setText(formattedDateTime);  // Update label text
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void getWeatherData(String city) {
        try {
            String urlString = BASE_URL + city + "&appid=" + API_KEY + "&units=metric";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                parseAndDisplayWeatherData(response.body());
            } else {
                JOptionPane.showMessageDialog(this, "Error: Unable to fetch weather data. Check the city name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void parseAndDisplayWeatherData(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        String cityName = jsonObject.getString("name");

        JSONObject main = jsonObject.getJSONObject("main");
        double temperature = main.getDouble("temp");
        double humidity = main.getDouble("humidity");
        double pressure = main.getDouble("pressure");

        JSONObject wind = jsonObject.getJSONObject("wind");
        double windSpeed = wind.getDouble("speed");

        JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
        String description = weather.getString("description");
        String iconCode = weather.getString("icon");  // Get icon code from API
        String mainWeather = weather.getString("main"); // Get main weather condition

        JSONObject sys = jsonObject.getJSONObject("sys");
        long sunriseTimestamp = sys.getLong("sunrise");
        long sunsetTimestamp = sys.getLong("sunset");

        LocalDateTime sunrise = LocalDateTime.ofInstant(Instant.ofEpochSecond(sunriseTimestamp), ZoneId.systemDefault());
        LocalDateTime sunset = LocalDateTime.ofInstant(Instant.ofEpochSecond(sunsetTimestamp), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Update labels with data
        temperatureLabel.setText("Temperature: " + temperature + "°C");
        humidityLabel.setText("Humidity: " + humidity + "%");
        windSpeedLabel.setText("Wind Speed: " + windSpeed + " m/s");
        pressureLabel.setText("Pressure: " + pressure + " hPa");
        sunriseLabel.setText("Sunrise: " + sunrise.format(formatter));
        sunsetLabel.setText("Sunset: " + sunset.format(formatter));
        overallWeatherLabel.setText("Weather: " + description);

        // Display the weather information in the result area
        resultArea.setText("City: " + cityName);

        // Display weather icon based on icon code
        setWeatherIcon(iconCode);

        // Update the background image based on the main weather condition
        updateBackgroundImage(mainWeather);
    }

    private void setWeatherIcon(String iconCode) {
        // Icons can be downloaded from OpenWeatherMap or use your own icon set
        String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
        ImageIcon weatherIcon = new ImageIcon(new ImageIcon(iconUrl).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        weatherIconLabel.setIcon(weatherIcon);
    }

    /**
     * Updates the background image based on the main weather condition.
     * @param mainWeather The main weather condition (e.g., Clear, Clouds, Rain).
     */
    private void updateBackgroundImage(String mainWeather) {
        String imagePath = weatherBackgroundMap.getOrDefault(mainWeather, weatherBackgroundMap.get("Default"));
        Image newBackground = new ImageIcon(imagePath).getImage();
        mainPanel.setBackgroundImage(newBackground);
        mainPanel.repaint();
    }

    // Custom JPanel class for the background image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
            if (backgroundImage == null) {
                System.out.println("Error: Background image not loaded.");
            }
        }

        /**
         * Sets a new background image and repaints the panel.
         * @param image The new background image.
         */
        public void setBackgroundImage(Image image) {
            this.backgroundImage = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the background image to fill the entire panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WeatherForecastGUI().setVisible(true);
        });
    }
}

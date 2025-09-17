import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import org.json.*;
import java.nio.charset.StandardCharsets;

public class WeatherApp extends JFrame {
    private JComboBox<String> cityBox;
    private JLabel tempLabel, feelsLabel, humidityLabel, windLabel, descLabel, iconLabel,
            sunriseLabel, sunsetLabel, errorLabel, dateTimeLabel;
    private JPanel hourlyPanel, mainPanel, cardPanel;
    private final String API_KEY = "532ad55bfe3f336b51d3643309b4d49b";
    private final String[] cities = {"New York,US","London,UK","Tokyo,JP","Delhi,IN","Sydney,AU"};
    private final int HOURLY_COUNT = 8;
    private boolean darkMode = false;
    private String weatherMain = "Clear";
    private java.util.List<Cloud> clouds = new ArrayList<>();
    private java.util.List<RainDrop> rain = new ArrayList<>();
    private int sunX = 50;

    public WeatherApp() {
        setTitle("Weather Dashboard");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupTopPanel();
        setupMainPanel();
        setupHourlyPanel();

        new javax.swing.Timer(1000, new DateTimeUpdater()).start();
        new javax.swing.Timer(50, new AnimationUpdater()).start();

        setVisible(true);
        fetchWeather((String) cityBox.getSelectedItem());
    }

    private void setupTopPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        top.setOpaque(false);

        cityBox = new JComboBox<>(cities);
        cityBox.addActionListener(e -> fetchWeather((String) cityBox.getSelectedItem()));
        cityBox.setFont(new Font("Arial", Font.BOLD, 14));

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton themeBtn = new JButton("Toggle Dark/Light Mode");
        themeBtn.addActionListener(e -> { darkMode = !darkMode; refreshTheme(); });

        top.add(new JLabel("Select City:"));
        top.add(cityBox);
        top.add(dateTimeLabel);
        top.add(themeBtn);
        add(top, BorderLayout.NORTH);
    }

    private void setupMainPanel() {
        mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setPreferredSize(new Dimension(900, 220));
        cardPanel.setBackground(darkMode ? new Color(50, 50, 50) : Color.WHITE);
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);

        tempLabel = createLabel("Temp: N/A", 28, new Color(70, 130, 180));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        cardPanel.add(tempLabel, c);

        feelsLabel = createLabel("Feels Like: N/A", 16, null);
        c.gridy = 1; c.gridwidth = 1;
        cardPanel.add(feelsLabel, c);

        humidityLabel = createLabel("Humidity: N/A", 16, null);
        c.gridx = 1;
        cardPanel.add(humidityLabel, c);

        windLabel = createLabel("Wind: N/A", 16, null);
        c.gridx = 0; c.gridy = 2;
        cardPanel.add(windLabel, c);

        sunriseLabel = createLabel("Sunrise: N/A", 16, null);
        c.gridx = 1;
        cardPanel.add(sunriseLabel, c);

        sunsetLabel = createLabel("Sunset: N/A", 16, null);
        c.gridx = 0; c.gridy = 3;
        cardPanel.add(sunsetLabel, c);

        descLabel = createLabel("Description: N/A", 16, null);
        c.gridx = 1;
        cardPanel.add(descLabel, c);

        iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(120, 120));
        c.gridx = 2; c.gridy = 0; c.gridheight = 4;
        cardPanel.add(iconLabel, c);

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(cardPanel, gbc);

        errorLabel = createLabel("", 14, Color.RED);
        gbc.gridy = 1;
        mainPanel.add(errorLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupHourlyPanel() {
        hourlyPanel = new JPanel();
        hourlyPanel.setLayout(new BoxLayout(hourlyPanel, BoxLayout.X_AXIS));

        JScrollPane scroll = new JScrollPane(hourlyPanel);
        scroll.setPreferredSize(new Dimension(950, 150));
        scroll.setBorder(null);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text, int size, Color c) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, size));
        lbl.setForeground(c == null ? (darkMode ? Color.WHITE : Color.DARK_GRAY) : c);
        return lbl;
    }

    private JLabel createHourLabel(String text) {
        return createLabel(text, 12, null);
    }

    private void refreshTheme() {
        Color bg = darkMode ? new Color(50, 50, 50) : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.DARK_GRAY;

        mainPanel.setBackground(bg);
        cardPanel.setBackground(bg);

        for (JLabel l : new JLabel[]{tempLabel, feelsLabel, humidityLabel, windLabel, descLabel, sunriseLabel, sunsetLabel, errorLabel})
            l.setForeground(l == errorLabel ? Color.RED : fg);

        for (Component c : hourlyPanel.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(bg);
                for (Component in : ((JPanel) c).getComponents())
                    if (in instanceof JLabel) ((JLabel) in).setForeground(fg);
            }
        }

        mainPanel.repaint();
        hourlyPanel.repaint();
    }

    private void animateBackground() {
        sunX += 1;
        if (sunX > getWidth()) sunX = -100;
        for (Cloud cl : clouds) cl.x += cl.speed;
        for (RainDrop r : rain) {
            r.y += r.speed;
            if (r.y > getHeight()) r.y = -10;
        }
    }

    private void drawBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (weatherMain.contains("Rain")) {
            g2.setColor(new Color(180, 200, 255));
            g2.fillRect(0, 0, getWidth(), getHeight());
            for (RainDrop r : rain) g2.drawLine(r.x, r.y, r.x, r.y + 5);
        } else if (weatherMain.contains("Cloud")) {
            g2.setColor(new Color(200, 220, 255));
            g2.fillRect(0, 0, getWidth(), getHeight());
            for (Cloud c : clouds) g2.fillOval(c.x, c.y, c.width, c.height);
        } else if (weatherMain.contains("Clear")) {
            g2.setColor(new Color(255, 250, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.ORANGE);
            g2.fillOval(sunX, 50, 80, 80);
        } else {
            g2.setColor(new Color(30, 30, 60));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private class DateTimeUpdater implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            dateTimeLabel.setText(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(new Date()));
        }
    }

    private class AnimationUpdater implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            animateBackground();
            mainPanel.repaint();
        }
    }

    private class Cloud { int x, y, width, height, speed; Cloud(int x,int y,int w,int h,int s){this.x=x;this.y=y;width=w;height=h;speed=s;} }
    private class RainDrop { int x, y; double speed; RainDrop(int x,int y,double s){this.x=x;this.y=y;speed=s;} }

    public void fetchWeather(String city) {
        errorLabel.setText("");
        new Thread(new WeatherFetcher(city)).start();
    }

    private class WeatherFetcher implements Runnable {
        private String city;
        WeatherFetcher(String city){ this.city = city; }
        public void run() {
            try {
                String enc = URLEncoder.encode(city, StandardCharsets.UTF_8);
                String currentUrl = "https://api.openweathermap.org/data/2.5/weather?q="+enc+"&appid="+API_KEY+"&units=metric";
                String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?q="+enc+"&appid="+API_KEY+"&units=metric";

                HttpURLConnection con = (HttpURLConnection)new URL(currentUrl).openConnection();
                con.setRequestMethod("GET");
                if(con.getResponseCode()!=200){ errorLabel.setText(readStream(con.getErrorStream())); return; }
                JSONObject currentJson = new JSONObject(readStream(con.getInputStream()));

                HttpURLConnection fCon = (HttpURLConnection)new URL(forecastUrl).openConnection();
                fCon.setRequestMethod("GET");
                if(fCon.getResponseCode()!=200){ errorLabel.setText(readStream(fCon.getErrorStream())); return; }
                JSONObject forecastJson = new JSONObject(readStream(fCon.getInputStream()));

                SwingUtilities.invokeLater(() -> new WeatherUIUpdater(currentJson, forecastJson).run());
            } catch(Exception ex){ ex.printStackTrace(); errorLabel.setText("Error fetching weather"); }
        }
    }

    private class WeatherUIUpdater implements Runnable {
        private JSONObject currentJson, forecastJson;
        WeatherUIUpdater(JSONObject c, JSONObject f){ currentJson=c; forecastJson=f; }
        public void run() {
            try {
                JSONObject main = currentJson.getJSONObject("main");
                double temp = main.getDouble("temp"), feels = main.getDouble("feels_like");
                int humidity = main.getInt("humidity");
                double windSpeed = currentJson.getJSONObject("wind").getDouble("speed");
                JSONObject weather = currentJson.getJSONArray("weather").getJSONObject(0);
                String desc = weather.getString("description"), icon = weather.getString("icon");
                weatherMain = weather.getString("main");
                long sunrise = currentJson.getJSONObject("sys").getLong("sunrise")*1000L;
                long sunset = currentJson.getJSONObject("sys").getLong("sunset")*1000L;

                tempLabel.setText(String.format("%.1f°C", temp));
                feelsLabel.setText("Feels Like: "+String.format("%.1f°C", feels));
                humidityLabel.setText("Humidity: "+humidity+"%");
                windLabel.setText("Wind: "+windSpeed+" m/s");
                descLabel.setText(desc.substring(0,1).toUpperCase()+desc.substring(1));
                sunriseLabel.setText("Sunrise: "+new SimpleDateFormat("HH:mm").format(new Date(sunrise)));
                sunsetLabel.setText("Sunset: "+new SimpleDateFormat("HH:mm").format(new Date(sunset)));

                try { iconLabel.setIcon(new ImageIcon(new URL("https://openweathermap.org/img/wn/"+icon+"@2x.png"))); } catch(Exception e){}

                initWeatherAnimation();
                updateHourlyForecast();
            } catch(Exception e){ e.printStackTrace(); }
        }
    }

    private void initWeatherAnimation() {
        clouds.clear(); rain.clear();
        if(weatherMain.contains("Cloud")) 
            for(int i=0;i<5;i++) clouds.add(new Cloud(-100*i,50+30*i,100,50,1+i));
        else if(weatherMain.contains("Rain")) 
            for(int i=0;i<50;i++) rain.add(new RainDrop((int)(Math.random()*getWidth()),(int)(Math.random()*getHeight()),2+Math.random()*3));
    }

    private void updateHourlyForecast() {
        hourlyPanel.removeAll();
        JSONArray list = null;
        try {
            list = new JSONObject(readStream(new URL(
                "https://api.openweathermap.org/data/2.5/forecast?q="+
                URLEncoder.encode((String)cityBox.getSelectedItem(),StandardCharsets.UTF_8)+
                "&appid="+API_KEY+"&units=metric").openStream())).getJSONArray("list");
        } catch(Exception e){}

        if(list == null) return;

        for(int i=0; i<Math.min(HOURLY_COUNT,list.length()); i++){
            JSONObject obj = list.getJSONObject(i);
            JSONObject fMain = obj.getJSONObject("main");
            JSONObject fWeather = obj.getJSONArray("weather").getJSONObject(0);

            double hTemp = fMain.getDouble("temp");
            int hHum = fMain.getInt("humidity");
            String hIcon = fWeather.getString("icon");
            String hDesc = fWeather.getString("description");
            String time = obj.getString("dt_txt").split(" ")[1].substring(0,5);

            JPanel hourCard = new JPanel(new GridLayout(4,1));
            hourCard.setPreferredSize(new Dimension(100,120));
            hourCard.setBackground(darkMode?new Color(70,70,70):Color.WHITE);
            hourCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            hourCard.add(createHourLabel(time));
            hourCard.add(createHourLabel(String.format("%.1f°C",hTemp)));
            hourCard.add(createHourLabel("Hum: "+hHum+"%"));

            JLabel hIconLabel = new JLabel();
            try{ hIconLabel.setIcon(new ImageIcon(new URL("https://openweathermap.org/img/wn/"+hIcon+"@2x.png"))); } catch(Exception ex){}
            hIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            hourCard.add(hIconLabel);
            hourCard.setToolTipText(hDesc.substring(0,1).toUpperCase()+hDesc.substring(1));

            hourlyPanel.add(hourCard);
            hourlyPanel.add(Box.createRigidArea(new Dimension(10,0)));
        }
        hourlyPanel.revalidate();
        hourlyPanel.repaint();
    }

    private String readStream(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line=in.readLine())!=null) sb.append(line);
        in.close();
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WeatherApp::new);
    }
}

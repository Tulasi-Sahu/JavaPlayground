import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class SnakeGame extends JPanel implements KeyListener, Runnable {
    private final int box = 20, w = 30, h = 20;
    private final java.util.List<Point> snake = new ArrayList<>();
    private final java.util.List<Color> colors = new ArrayList<>();
    private Point food; 
    private Color foodColor;
    private int dx = 1, dy = 0, speed = 180, foodCount = 0;
    private Thread thread; 
    private boolean running = false, gameOver = false;
    private final int speedStep = 15;
    private JButton restartButton, exitButton, startButton;
    private int lastScore = 0;

    public SnakeGame() {
        setPreferredSize(new Dimension(w*box, h*box));
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        startButton = new JButton("START");
        startButton.setBounds(w*box/2-50, h*box/2-25, 100, 50);
        add(startButton);
        startButton.addActionListener(e -> { remove(startButton); initGame(); repaint(); });
    }

    private void initGame() {
        snake.clear(); colors.clear();
        snake.add(new Point(5,5)); colors.add(Color.GREEN);
        spawnFood(); running = true; speed = 180; foodCount = 0;
        gameOver = false;
        removeButtons();
        if(thread == null || !thread.isAlive()){ thread = new Thread(this); thread.start(); }
    }

    private void spawnFood() {
        Random r = new Random();
        food = new Point(r.nextInt(w), r.nextInt(h));
        foodColor = new Color(r.nextInt(156)+50, r.nextInt(156)+50, r.nextInt(156)+50);
    }

    private void removeButtons() {
        if(restartButton != null) remove(restartButton);
        if(exitButton != null) remove(exitButton);
    }

    private void showGameOver() {
        gameOver = true;
        lastScore = snake.size()-1;

        restartButton = new JButton("RESTART");
        exitButton = new JButton("EXIT");
        restartButton.setBounds(w*box/2-110, h*box/2-25, 100, 50);
        exitButton.setBounds(w*box/2+10, h*box/2-25, 100, 50);

        restartButton.addActionListener(e -> { remove(restartButton); remove(exitButton); initGame(); repaint(); });
        exitButton.addActionListener(e -> System.exit(0));

        add(restartButton); add(exitButton);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(220, 240, 255)); // light plain background

        // Draw snake
        for(int i=0; i<snake.size(); i++){
            Point p = snake.get(i);
            if(i==0){ 
                g.setColor(Color.WHITE); 
                g.fillRect(p.x*box, p.y*box, box, box);
                g.setColor(colors.get(i));
                g.fillRect(p.x*box+2, p.y*box+2, box-4, box-4);
            } else {
                g.setColor(colors.get(i));
                g.fillRect(p.x*box, p.y*box, box, box);
            }
        }

        // Draw food
        g.setColor(foodColor);
        g.fillOval(food.x*box+2, food.y*box+2, box-4, box-4);

        // Draw Game Over overlay
        if(gameOver) {
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0,getWidth(),getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! Score: "+lastScore, w*box/2-130, h*box/2-50);
        }
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x + dx, head.y + dy);

        if(newHead.x < 0 || newHead.y < 0 || newHead.x >= w || newHead.y >= h || snake.contains(newHead)) {
            running = false;
            showGameOver();
            return;
        }

        snake.add(0, newHead);

        if(newHead.equals(food)) {
            colors.add(0, foodColor);
            spawnFood();
            foodCount++;
            if(speed > 50) speed -= speedStep;
        } else {
            colors.add(0, colors.get(0));
            snake.remove(snake.size()-1);
            colors.remove(colors.size()-1);
        }

        for(int i=1; i<colors.size(); i++)
            colors.set(i, blend(colors.get(i-1), colors.get(i), 0.2f));
    }

    private Color blend(Color c1, Color c2, float ratio) {
        int r = (int)(c1.getRed()*ratio + c2.getRed()*(1-ratio));
        int g = (int)(c1.getGreen()*ratio + c2.getGreen()*(1-ratio));
        int b = (int)(c1.getBlue()*ratio + c2.getBlue()*(1-ratio));
        return new Color(r, g, b);
    }

    @Override
    public void run() {
        while(true) {
            if(running) { move(); repaint(); }
            try { Thread.sleep(speed); } catch(Exception e) {}
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP: if(dy==0){dx=0;dy=-1;} break;
            case KeyEvent.VK_DOWN: if(dy==0){dx=0;dy=1;} break;
            case KeyEvent.VK_LEFT: if(dx==0){dx=-1;dy=0;} break;
            case KeyEvent.VK_RIGHT: if(dx==0){dx=1;dy=0;} break;
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame f = new JFrame("Snake Game");
        SnakeGame g = new SnakeGame();
        f.add(g);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}

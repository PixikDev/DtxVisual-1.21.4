package pixikdev.ru.dtxvisual.client.managers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThemeManager {
    private static ThemeManager instance;
    private Theme currentTheme;
    private final List<ThemeChangeListener> listeners = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ThemeManager() {
        this.currentTheme = new LightTheme();
        startGradientUpdateTask();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        Color bg = theme.getBackgroundColor();
        System.out.println("Theme changed to: " + theme.getName() +
                " | BackgroundColor: " + bg.getRed() + ", " + bg.getGreen() + ", " + bg.getBlue() + ", " + bg.getAlpha());
        notifyListeners();
    }

    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(currentTheme);
        }
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public Color getThemeColor() {
        Color color = currentTheme.getBackgroundColor();
        return color;
    }

    public Theme[] getAvailableThemes() {
        return new Theme[]{
                new TwilightGradientTheme(),
                new EmeraldGlowTheme(),
                new SunsetBlazeTheme(),
                new OceanBreezeTheme(),
                new AuroraBorealisTheme(),
                new CyberNeonTheme(),
                new LavaCoreTheme(),
                new RGBTheme(),
                new CrystalRoseTheme(),
                new ElectricLimeTheme(),
                new GoldenSunsetTheme(),
                new MysticPurpleTheme(),
                new ArcticIceTheme()
        };
    }

    private void startGradientUpdateTask() {
        scheduler.scheduleAtFixedRate(() -> {
            if (currentTheme instanceof GradientTheme) {
                notifyListeners();
            }
        }, 0, 25, TimeUnit.MILLISECONDS);
    }

    public interface Theme {
        Color getBackgroundColor();
        Color getBorderColor();
        Color getTextColor();
        Color getAccentColor();
        Color getSecondaryBackgroundColor();
        String getName();
    }

    public interface ThemeChangeListener {
        void onThemeChanged(Theme theme);
    }

    private static Color interpolateColor(Color start, Color end, float t) {
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * t);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * t);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * t);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * t);
        return new Color(
                Math.max(0, Math.min(255, r)),
                Math.max(0, Math.min(255, g)),
                Math.max(0, Math.min(255, b)),
                Math.max(0, Math.min(255, a))
        );
    }

    private static abstract class GradientTheme implements Theme {
        protected final Color[] backgroundColors;
        protected final Color[] borderColors;
        protected final Color[] accentColors;
        protected final Color[] secondaryBackgroundColors;
        private static final long TRANSITION_DURATION = 1000;

        protected GradientTheme(Color[] backgroundColors, Color[] borderColors,
                                Color[] accentColors, Color[] secondaryBackgroundColors) {
            this.backgroundColors = backgroundColors;
            this.borderColors = borderColors;
            this.accentColors = accentColors;
            this.secondaryBackgroundColors = secondaryBackgroundColors;
        }

        protected float getInterpolationFactor() {
            long currentTime = System.currentTimeMillis();
            float phase = (float) (currentTime % (TRANSITION_DURATION * backgroundColors.length)) / TRANSITION_DURATION;
            int index = (int) phase;
            float t = phase - index;
            t = (float) (Math.sin(t * Math.PI / 2));
            return t;
        }

        protected int getCurrentIndex() {
            long currentTime = System.currentTimeMillis();
            return (int) ((currentTime % (TRANSITION_DURATION * backgroundColors.length)) / TRANSITION_DURATION);
        }

        @Override
        public Color getBackgroundColor() {
            int currentIndex = getCurrentIndex();
            int nextIndex = (currentIndex + 1) % backgroundColors.length;
            return interpolateColor(backgroundColors[currentIndex], backgroundColors[nextIndex], getInterpolationFactor());
        }

        @Override
        public Color getBorderColor() {
            int currentIndex = getCurrentIndex();
            int nextIndex = (currentIndex + 1) % borderColors.length;
            return interpolateColor(borderColors[currentIndex], borderColors[nextIndex], getInterpolationFactor());
        }

        @Override
        public Color getAccentColor() {
            int currentIndex = getCurrentIndex();
            int nextIndex = (currentIndex + 1) % accentColors.length;
            return interpolateColor(accentColors[currentIndex], accentColors[nextIndex], getInterpolationFactor());
        }

        @Override
        public Color getSecondaryBackgroundColor() {
            int currentIndex = getCurrentIndex();
            int nextIndex = (currentIndex + 1) % secondaryBackgroundColors.length;
            return interpolateColor(secondaryBackgroundColors[currentIndex], secondaryBackgroundColors[nextIndex], getInterpolationFactor());
        }
    }

    public static class LightTheme implements Theme {
        @Override
        public Color getBackgroundColor() {
            return new Color(255,255,255, 100);
        }

        @Override
        public Color getBorderColor() {
            return new Color(255,255,255, 100);
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public Color getAccentColor() {
            return new Color(255,255,255, 100);
        }

        @Override
        public Color getSecondaryBackgroundColor() {
            return new Color(255,255,255, 100);
        }

        @Override
        public String getName() {
            return "White";
        }
    }

    public static class DarkTheme implements Theme {
        @Override
        public Color getBackgroundColor() {
            return new Color(30, 30, 30, 175);
        }

        @Override
        public Color getBorderColor() {
            return new Color(200, 200, 200, 100);
        }

        @Override
        public Color getTextColor() {
            return new Color(220, 220, 220);
        }

        @Override
        public Color getAccentColor() {
            return new Color(100, 100, 100, 100);
        }

        @Override
        public Color getSecondaryBackgroundColor() {
            return new Color(20, 20, 20, 200);
        }

        @Override
        public String getName() {
            return "Black";
        }
    }

    public static class TwilightGradientTheme extends GradientTheme {
        public TwilightGradientTheme() {
            super(
                    new Color[]{
                            new Color(80, 42, 195, 150),
                            new Color(156, 69, 211, 150),
                            new Color(179, 103, 250, 150),
                            new Color(148, 36, 255, 150)
                    },
                    new Color[]{
                            new Color(80, 42, 195, 150),
                            new Color(156, 69, 211, 150),
                            new Color(179, 103, 250, 150),
                            new Color(148, 36, 255, 150)
                    },
                    new Color[]{
                            new Color(80, 42, 195, 150),
                            new Color(156, 69, 211, 150),
                            new Color(179, 103, 250, 150),
                            new Color(148, 36, 255, 150)
                    },
                    new Color[]{
                            new Color(80, 42, 195, 150),
                            new Color(156, 69, 211, 150),
                            new Color(179, 103, 250, 150),
                            new Color(148, 36, 255, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Purple";
        }
    }

    public static class EmeraldGlowTheme extends GradientTheme {
        public EmeraldGlowTheme() {
            super(
                    new Color[]{
                            new Color(0, 204, 160, 150),
                            new Color(2, 204, 73, 150),
                            new Color(15, 227, 0, 150),
                            new Color(0, 170, 31, 150)
                    },
                    new Color[]{
                            new Color(0, 204, 160, 150),
                            new Color(2, 204, 73, 150),
                            new Color(15, 227, 0, 150),
                            new Color(0, 170, 31, 150)
                    },
                    new Color[]{
                            new Color(0, 204, 160, 150),
                            new Color(2, 204, 73, 150),
                            new Color(15, 227, 0, 150),
                            new Color(0, 170, 31, 150)
                    },
                    new Color[]{
                            new Color(0, 204, 160, 150),
                            new Color(2, 204, 73, 150),
                            new Color(15, 227, 0, 150),
                            new Color(0, 170, 31, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Emerald";
        }
    }

    public static class SunsetBlazeTheme extends GradientTheme {
        public SunsetBlazeTheme() {
            super(
                    new Color[]{
                            new Color(200, 60, 20, 150),
                            new Color(220, 100, 40, 150),
                            new Color(240, 140, 60, 150),
                            new Color(180, 40, 10, 150)
                    },
                    new Color[]{
                            new Color(200, 60, 20, 150),
                            new Color(220, 100, 40, 150),
                            new Color(240, 140, 60, 150),
                            new Color(180, 40, 10, 150)
                    },
                    new Color[]{
                            new Color(200, 60, 20, 150),
                            new Color(220, 100, 40, 150),
                            new Color(240, 140, 60, 150),
                            new Color(180, 40, 10, 150)
                    },
                    new Color[]{
                            new Color(200, 60, 20, 150),
                            new Color(220, 100, 40, 150),
                            new Color(240, 140, 60, 150),
                            new Color(180, 40, 10, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Orange";
        }
    }

    public static class OceanBreezeTheme extends GradientTheme {
        public OceanBreezeTheme() {
            super(
                    new Color[]{
                            new Color(20, 80, 120, 150),
                            new Color(40, 120, 160, 150),
                            new Color(60, 160, 200, 150),
                            new Color(10, 60, 100, 150)
                    },
                    new Color[]{
                            new Color(16, 64, 96, 150),
                            new Color(32, 96, 128, 150),
                            new Color(48, 128, 160, 150),
                            new Color(8, 48, 80, 150)
                    },
                    new Color[]{
                            new Color(16, 64, 96, 150),
                            new Color(32, 96, 128, 150),
                            new Color(48, 128, 160, 150),
                            new Color(8, 48, 80, 150)
                    },
                    new Color[]{
                            new Color(16, 64, 96, 150),
                            new Color(32, 96, 128, 150),
                            new Color(48, 128, 160, 150),
                            new Color(8, 48, 80, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Blue";
        }
    }

    public static class AuroraBorealisTheme extends GradientTheme {
        public AuroraBorealisTheme() {
            super(
                    new Color[]{
                            new Color(10, 80, 60, 150),
                            new Color(40, 180, 120, 150),
                            new Color(80, 220, 200, 150),
                            new Color(120, 100, 220, 150)
                    },
                    new Color[]{
                            new Color(8, 64, 48, 150),
                            new Color(32, 144, 96, 150),
                            new Color(64, 176, 160, 150),
                            new Color(96, 80, 176, 150)
                    },
                    new Color[]{
                            new Color(20, 120, 90, 150),
                            new Color(60, 200, 140, 150),
                            new Color(100, 240, 220, 150),
                            new Color(150, 130, 255, 150)
                    },
                    new Color[]{
                            new Color(16, 100, 80, 150),
                            new Color(50, 170, 130, 150),
                            new Color(90, 210, 190, 150),
                            new Color(130, 115, 240, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Aurora";
        }
    }

    public static class CyberNeonTheme extends GradientTheme {
        public CyberNeonTheme() {
            super(
                    new Color[]{
                            new Color(0, 255, 200, 150),
                            new Color(255, 0, 180, 150),
                            new Color(0, 140, 255, 150),
                            new Color(255, 255, 0, 150)
                    },
                    new Color[]{
                            new Color(0, 210, 165, 150),
                            new Color(210, 0, 150, 150),
                            new Color(0, 110, 210, 150),
                            new Color(210, 210, 0, 150)
                    },
                    new Color[]{
                            new Color(50, 255, 220, 150),
                            new Color(255, 50, 200, 150),
                            new Color(50, 170, 255, 150),
                            new Color(255, 255, 50, 150)
                    },
                    new Color[]{
                            new Color(0, 235, 190, 150),
                            new Color(235, 0, 170, 150),
                            new Color(0, 130, 235, 150),
                            new Color(235, 235, 0, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Neon";
        }
    }

    public static class LavaCoreTheme extends GradientTheme {
        public LavaCoreTheme() {
            super(
                    new Color[]{
                            new Color(120, 0, 0, 150),
                            new Color(200, 20, 0, 150),
                            new Color(255, 90, 0, 150),
                            new Color(255, 180, 0, 150)
                    },
                    new Color[]{
                            new Color(90, 0, 0, 150),
                            new Color(160, 16, 0, 150),
                            new Color(210, 75, 0, 150),
                            new Color(220, 150, 0, 150)
                    },
                    new Color[]{
                            new Color(160, 10, 0, 150),
                            new Color(230, 40, 0, 150),
                            new Color(255, 120, 0, 150),
                            new Color(255, 210, 0, 150)
                    },
                    new Color[]{
                            new Color(140, 5, 0, 150),
                            new Color(210, 30, 0, 150),
                            new Color(240, 100, 0, 150),
                            new Color(245, 190, 0, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Lava";
        }
    }

    public static class RGBTheme extends GradientTheme {
        public RGBTheme() {
            super(
                    new Color[]{
                            new Color(255, 0, 0, 150),
                            new Color(17, 255, 0, 150),
                            new Color(0, 5, 255, 150),
                            new Color(255, 0, 241, 150)
                    },
                    new Color[]{
                            new Color(255, 0, 0, 150),
                            new Color(17, 255, 0, 150),
                            new Color(0, 5, 255, 150),
                            new Color(255, 0, 241, 150)
                    },
                    new Color[]{
                            new Color(255, 0, 0, 150),
                            new Color(17, 255, 0, 150),
                            new Color(0, 5, 255, 150),
                            new Color(255, 0, 241, 150)
                    },
                    new Color[]{
                            new Color(255, 0, 0, 150),
                            new Color(17, 255, 0, 150),
                            new Color(0, 5, 255, 150),
                            new Color(255, 0, 241, 150)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "RGB";
        }
    }

    public static class CrystalRoseTheme extends GradientTheme {
        public CrystalRoseTheme() {
            super(
                    new Color[]{
                            new Color(255, 100, 150, 180),
                            new Color(255, 150, 200, 180),
                            new Color(255, 200, 220, 180),
                            new Color(255, 220, 240, 180)
                    },
                    new Color[]{
                            new Color(255, 100, 150, 180),
                            new Color(255, 150, 200, 180),
                            new Color(255, 200, 220, 180),
                            new Color(255, 220, 240, 180)
                    },
                    new Color[]{
                            new Color(255, 100, 150, 180),
                            new Color(255, 150, 200, 180),
                            new Color(255, 200, 220, 180),
                            new Color(255, 220, 240, 180)
                    },
                    new Color[]{
                            new Color(255, 120, 170, 180),
                            new Color(255, 170, 220, 180),
                            new Color(255, 220, 240, 180),
                            new Color(255, 240, 250, 180)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Crystal Rose";
        }
    }

    public static class ElectricLimeTheme extends GradientTheme {
        public ElectricLimeTheme() {
            super(
                    new Color[]{
                            new Color(100, 255, 100, 200),
                            new Color(150, 255, 150, 200),
                            new Color(200, 255, 200, 200),
                            new Color(180, 255, 180, 200)
                    },
                    new Color[]{
                            new Color(100, 255, 100, 200),
                            new Color(150, 255, 150, 200),
                            new Color(200, 255, 200, 200),
                            new Color(180, 255, 180, 200)
                    },
                    new Color[]{
                            new Color(100, 255, 100, 200),
                            new Color(150, 255, 150, 200),
                            new Color(200, 255, 200, 200),
                            new Color(180, 255, 180, 200)
                    },
                    new Color[]{
                            new Color(120, 255, 120, 200),
                            new Color(170, 255, 170, 200),
                            new Color(220, 255, 220, 200),
                            new Color(200, 255, 200, 200)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.BLACK;
        }

        @Override
        public String getName() {
            return "Electric Lime";
        }
    }

    public static class GoldenSunsetTheme extends GradientTheme {
        public GoldenSunsetTheme() {
            super(
                    new Color[]{
                            new Color(255, 230, 100, 200),
                            new Color(255, 200, 50, 200),
                            new Color(255, 180, 100, 200),
                            new Color(255, 220, 150, 200)
                    },
                    new Color[]{
                            new Color(255, 230, 100, 200),
                            new Color(255, 200, 50, 200),
                            new Color(255, 180, 100, 200),
                            new Color(255, 220, 150, 200)
                    },
                    new Color[]{
                            new Color(255, 230, 100, 200),
                            new Color(255, 200, 50, 200),
                            new Color(255, 180, 100, 200),
                            new Color(255, 220, 150, 200)
                    },
                    new Color[]{
                            new Color(255, 240, 120, 200),
                            new Color(255, 220, 80, 200),
                            new Color(255, 200, 120, 200),
                            new Color(255, 230, 170, 200)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.BLACK;
        }

        @Override
        public String getName() {
            return "Golden Sunset";
        }
    }

    public static class MysticPurpleTheme extends GradientTheme {
        public MysticPurpleTheme() {
            super(
                    new Color[]{
                            new Color(200, 100, 255, 200),
                            new Color(220, 150, 255, 200),
                            new Color(240, 180, 255, 200),
                            new Color(255, 200, 240, 200)
                    },
                    new Color[]{
                            new Color(200, 100, 255, 200),
                            new Color(220, 150, 255, 200),
                            new Color(240, 180, 255, 200),
                            new Color(255, 200, 240, 200)
                    },
                    new Color[]{
                            new Color(200, 100, 255, 200),
                            new Color(220, 150, 255, 200),
                            new Color(240, 180, 255, 200),
                            new Color(255, 200, 240, 200)
                    },
                    new Color[]{
                            new Color(210, 120, 255, 200),
                            new Color(230, 170, 255, 200),
                            new Color(250, 200, 255, 200),
                            new Color(255, 220, 245, 200)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.WHITE;
        }

        @Override
        public String getName() {
            return "Mystic Purple";
        }
    }

    public static class ArcticIceTheme extends GradientTheme {
        public ArcticIceTheme() {
            super(
                    new Color[]{
                            new Color(100, 200, 255, 200),
                            new Color(150, 220, 255, 200),
                            new Color(200, 240, 255, 200),
                            new Color(220, 250, 255, 200)
                    },
                    new Color[]{
                            new Color(100, 200, 255, 200),
                            new Color(150, 220, 255, 200),
                            new Color(200, 240, 255, 200),
                            new Color(220, 250, 255, 200)
                    },
                    new Color[]{
                            new Color(100, 200, 255, 200),
                            new Color(150, 220, 255, 200),
                            new Color(200, 240, 255, 200),
                            new Color(220, 250, 255, 200)
                    },
                    new Color[]{
                            new Color(150, 230, 255, 200),
                            new Color(180, 250, 255, 200),
                            new Color(220, 255, 255, 200),
                            new Color(240, 255, 255, 200)
                    }
            );
        }

        @Override
        public Color getTextColor() {
            return Color.BLACK;
        }

        @Override
        public String getName() {
            return "Arctic Ice";
        }
    }
}
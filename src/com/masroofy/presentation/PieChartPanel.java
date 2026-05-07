package com.masroofy.presentation;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * The type Pie chart panel.
 */
public class PieChartPanel extends JPanel {
    private Map<String, Double> categoryTotals;

    /**
     * Sets totals.
     *
     * @param totals the totals
     */
    public void setTotals(Map<String, Double> totals) {
        this.categoryTotals = totals;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (categoryTotals == null || categoryTotals.isEmpty()) {
            g.drawString("No expenses yet. Add an expense to see the chart!", 20, 30);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double totalAmount = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();

        int startAngle = 0;
        int ovalX = 20, ovalY = 20, ovalWidth = 150, ovalHeight = 150;

        Color[] colors = {
                new Color(255, 99, 132),  // Pink
                new Color(54, 162, 235),  // Blue
                new Color(255, 206, 86),  // Yellow
                new Color(75, 192, 192),  // Teal
                new Color(153, 102, 255), // Purple
                new Color(255, 159, 64)   // Orange
        };

        int colorIndex = 0;
        int legendX = 190;
        int legendY = 40;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double amount = entry.getValue();
            int arcAngle = (int) Math.round((amount / totalAmount) * 360.0);

            g2d.setColor(colors[colorIndex % colors.length]);
            g2d.fillArc(ovalX, ovalY, ovalWidth, ovalHeight, startAngle, arcAngle);

            g2d.fillRect(legendX, legendY, 12, 12);

            g2d.setColor(Color.BLACK);
            String legendText = entry.getKey() + ": " + String.format("%.2f", amount);
            g2d.drawString(legendText, legendX + 20, legendY + 11);

            startAngle += arcAngle;
            legendY += 25;
            colorIndex++;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Ensure the layout manager gives the chart enough room
        return new Dimension(380, 200);
    }
}
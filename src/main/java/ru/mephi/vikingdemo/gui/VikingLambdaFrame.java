package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.controller.VikingListener;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingLambdaService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;


public class VikingLambdaFrame extends JFrame {

    private final VikingLambdaService lambdaService;
    private final VikingService vikingService;
    private final VikingListener vikingListener;
    private final JTextArea resultArea;

    public VikingLambdaFrame(VikingLambdaService lambdaService,
                             VikingService vikingService,
                             VikingListener vikingListener) {
        this.lambdaService = lambdaService;
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;

        setTitle("Viking Lambda Analytics (Lab 5)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel header = new JLabel("Viking Lambda Analytics", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel countPanel = new JPanel();
        countPanel.setLayout(new BoxLayout(countPanel, BoxLayout.Y_AXIS));
        countPanel.setBorder(new TitledBorder("1. Count"));

        addButton(countPanel, "Age > 30", () -> {
            long count = lambdaService.countByAgeGreaterThan(30);
            showResult("Vikings older than 30: " + count);
        });
        addButton(countPanel, "Age < 25", () -> {
            long count = lambdaService.countByAgeLessThan(25);
            showResult("Vikings younger than 25: " + count);
        });
        addButton(countPanel, "Age in [20, 35]", () -> {
            long count = lambdaService.countByAgeInRange(20, 35);
            showResult("Vikings aged 20-35: " + count);
        });
        addButton(countPanel, "Age NOT in [20, 35]", () -> {
            long count = lambdaService.countByAgeOutOfRange(20, 35);
            showResult("Vikings NOT aged 20-35: " + count);
        });
        addButton(countPanel, "BRAIDED + Red hair", () -> {
            long count = lambdaService.countByBeardStyleAndHairColor(BeardStyle.BRAIDED, HairColor.Red);
            showResult("Vikings with BRAIDED beard and Red hair: " + count);
        });
        addButton(countPanel, "1 or 2 Axes (bearded)", () -> {
            long count = lambdaService.countWithOneOrTwoAxes();
            showResult("Bearded vikings with 1 or 2 axes: " + count);
        });

        buttonPanel.add(countPanel);

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBorder(new TitledBorder("2. Display"));

        addButton(displayPanel, "Random tall (>180)", () -> {
            Optional<Viking> v = lambdaService.getRandomVikingTallerThan180();
            showResult(v.map(this::formatViking)
                    .orElse("No vikings taller than 180 cm found."));
        });
        addButton(displayPanel, "Legendary equipment", () -> {
            List<Viking> list = lambdaService.getVikingsWithLegendaryEquipment();
            showResult("Vikings with Legendary equipment (" + list.size() + "):\n"
                    + list.stream().map(this::formatViking).collect(Collectors.joining("\n")));
        });
        addButton(displayPanel, "Red-bearded by age", () -> {
            List<Viking> list = lambdaService.getRedBeardedVikingsSortedByAge();
            showResult("Red-bearded vikings sorted by age (" + list.size() + "):\n"
                    + list.stream().map(this::formatViking).collect(Collectors.joining("\n")));
        });

        buttonPanel.add(displayPanel);

        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));
        idPanel.setBorder(new TitledBorder("3. ID Array ops"));

        addButton(idPanel, "Max ID (last record)", () -> {
            OptionalInt maxId = lambdaService.findMaxId();
            showResult(maxId.isPresent()
                    ? "Max ID (last record): " + maxId.getAsInt()
                    : "No vikings in database.");
        });
        addButton(idPanel, "Even IDs + Vikings", () -> {
            Integer[] evenIds = lambdaService.getEvenIds();
            List<Viking> vikings = lambdaService.getVikingsWithEvenIds();
            showResult("Even IDs: " + Arrays.toString(evenIds) + "\n\nVikings with even IDs ("
                    + vikings.size() + "):\n"
                    + vikings.stream().map(this::formatViking).collect(Collectors.joining("\n")));
        });

        buttonPanel.add(idPanel);

        JPanel genPanel = new JPanel();
        genPanel.setLayout(new BoxLayout(genPanel, BoxLayout.Y_AXIS));
        genPanel.setBorder(new TitledBorder("Bulk Generate"));

        addButton(genPanel, "Generate 10 Vikings", () -> {
            List<Viking> created = vikingService.bulkGenerateVikings(10);
            // точечно добавляем каждого нового викинга в основную таблицу
            vikingListener.onVikingsBulkAdded(created);
            showResult("Bulk generated " + created.size() + " vikings:\n"
                    + created.stream().map(this::formatViking).collect(Collectors.joining("\n")));
        });

        buttonPanel.add(genPanel);

        JScrollPane btnScroll = new JScrollPane(buttonPanel);
        btnScroll.setPreferredSize(new Dimension(250, 0));
        add(btnScroll, BorderLayout.WEST);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultArea.setText("Click a button to see results...");
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 30));
        button.addActionListener(e -> action.run());
        panel.add(button);
        panel.add(Box.createVerticalStrut(3));
    }

    private void showResult(String text) {
        SwingUtilities.invokeLater(() -> {
            resultArea.setText(text);
            resultArea.setCaretPosition(0);
        });
    }

    private String formatViking(Viking v) {
        String equipment = v.equipment().stream()
                .map(e -> e.name() + " [" + e.quality() + "]")
                .collect(Collectors.joining(", "));
        return String.format("[ID:%d] %s, age %d, %dcm, %s, %s | %s",
                v.id(), v.name(), v.age(), v.heightCm(),
                v.hairColor(), v.beardStyle(), equipment);
    }
}

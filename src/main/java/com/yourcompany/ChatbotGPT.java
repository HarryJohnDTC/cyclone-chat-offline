package com.yourcompany;

import okhttp3.*;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ChatbotGPT {
    private static final String API_URL = "http://localhost:11434/api/generate";
    private static JTextPane chatArea;
    private static JTextField inputField;
    private static JButton sendButton;
    private static JButton saveButton; // Nouveau bouton pour sauvegarder l'historique
    private static JFrame frame;
    private static StringBuilder conversationHistory = new StringBuilder();
    private static JLabel loadingLabel;

    // Définition des couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(183, 28, 28); // Rouge très foncé
    private static final Color SECONDARY_COLOR = new Color(198, 40, 40); // Rouge foncé
    private static final Color ACCENT_COLOR = new Color(255, 87, 34); // Orange foncé
    private static final Color BACKGROUND_COLOR = new Color(38, 38, 38); // Gris très foncé
    private static final Color TEXT_COLOR = new Color(255, 255, 255); // Blanc
    private static final Color BORDER_COLOR = new Color(66, 66, 66); // Gris foncé
    private static final Color BUTTON_TEXT_COLOR = new Color(255, 255, 255); // Blanc
    private static final Color BUTTON_HOVER_COLOR = new Color(211, 47, 47); // Rouge hover

    // Définition des styles
    private static final String STYLE_USER = "user";
    private static final String STYLE_ASSISTANT = "assistant";
    private static final String STYLE_SYSTEM = "system";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Création de la fenêtre principale avec un thème moderne
        frame = new JFrame("Chatbot Spéciale Cyclone | Urgence : 118");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // Configuration du style moderne pour les composants
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));

        // Zone de chat avec styles modernes
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(BACKGROUND_COLOR);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Définition des styles de texte modernes
        StyledDocument doc = chatArea.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style userStyle = doc.addStyle(STYLE_USER, def);
        StyleConstants.setForeground(userStyle, SECONDARY_COLOR);
        StyleConstants.setBold(userStyle, true);
        StyleConstants.setFontFamily(userStyle, "Segoe UI");

        Style assistantStyle = doc.addStyle(STYLE_ASSISTANT, def);
        StyleConstants.setForeground(assistantStyle, ACCENT_COLOR);
        StyleConstants.setBold(assistantStyle, true);
        StyleConstants.setFontFamily(assistantStyle, "Segoe UI");

        Style systemStyle = doc.addStyle(STYLE_SYSTEM, def);
        StyleConstants.setForeground(systemStyle, TEXT_COLOR);
        StyleConstants.setItalic(systemStyle, true);
        StyleConstants.setFontFamily(systemStyle, "Segoe UI");

        // Création d'un JScrollPane personnalisé
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        // Zone de saisie moderne
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Boutons modernes avec effet hover
        sendButton = new JButton("Envoyer") {
            {
                setContentAreaFilled(false);
                setOpaque(true);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else if (getModel().isRollover()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g.setColor(PRIMARY_COLOR);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        sendButton.setForeground(BUTTON_TEXT_COLOR);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Nouveau bouton pour sauvegarder l'historique
        saveButton = new JButton("Sauvegarder l'historique") {
            {
                setContentAreaFilled(false);
                setOpaque(true);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else if (getModel().isRollover()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g.setColor(PRIMARY_COLOR);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        saveButton.setForeground(BUTTON_TEXT_COLOR);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveConversationHistory());

        JButton newChatButton = new JButton("Nouvelle Conversation") {
            {
                setContentAreaFilled(false);
                setOpaque(true);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else if (getModel().isRollover()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g.setColor(PRIMARY_COLOR);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        newChatButton.setForeground(BUTTON_TEXT_COLOR);
        newChatButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        newChatButton.setFocusPainted(false);
        newChatButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newChatButton.addActionListener(e -> startNewConversation());

        // Création de l'indicateur de chargement
        loadingLabel = new JLabel("Génération de la réponse...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadingLabel.setForeground(ACCENT_COLOR);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setVisible(false);

        // Panel pour l'indicateur de chargement
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(BACKGROUND_COLOR);
        loadingPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        loadingPanel.add(loadingLabel, BorderLayout.CENTER);

        // Panel pour les boutons avec un layout moderne
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(newChatButton);
        buttonPanel.add(saveButton); // Ajouter le bouton de sauvegarde

        // Panel pour la zone de saisie avec un layout moderne
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Panel du bas avec un layout moderne
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(loadingPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(inputPanel, BorderLayout.SOUTH);

        // Ajouter les composants à la fenêtre
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Gestionnaires d'événements
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // Menu moderne
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JMenu helpMenu = new JMenu("Aide");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        // Tester la connexion
        testConnection();

        // Afficher la fenêtre
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Méthode pour sauvegarder l'historique des conversations
    private static void saveConversationHistory() {
        try {
            File file = new File(System.getProperty("user.home") + "/Desktop/conversation_history.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(conversationHistory.toString());
            writer.close();
            appendToChatArea("Historique des conversations sauvegardé avec succès.\n", STYLE_SYSTEM);
        } catch (IOException e) {
            appendToChatArea("Erreur lors de la sauvegarde de l'historique : " + e.getMessage() + "\n", STYLE_SYSTEM);
        }
    }

    // Classe pour la barre de défilement moderne
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private final Color thumbColorModern = SECONDARY_COLOR;
        private final Color trackColorModern = BACKGROUND_COLOR;

        @Override
        protected void configureScrollBarColors() {
            super.configureScrollBarColors();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColorModern);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColorModern);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }

    private static void startNewConversation() {
        chatArea.setText("");
        conversationHistory = new StringBuilder();
        appendToChatArea("Nouvelle conversation démarrée.\n", STYLE_SYSTEM);
        appendToChatArea("Expert cyclones à votre service. En cas d'urgence : 118\n\n", STYLE_SYSTEM);
        inputField.setText("");
        inputField.requestFocus();
    }

    private static void startOllama() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", "start ollama serve");
            } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
                pb = new ProcessBuilder("bash", "-c", "ollama serve &");
            } else {
                appendToChatArea("Système d'exploitation non supporté pour le démarrage automatique d'Ollama.\n",
                        STYLE_SYSTEM);
                return;
            }

            pb.start();
            // Attendre que le serveur démarre
            Thread.sleep(2000);
            appendToChatArea("Ollama démarré automatiquement.\n", STYLE_SYSTEM);
        } catch (IOException | InterruptedException e) {
            appendToChatArea("Impossible de démarrer Ollama automatiquement.\n" +
                    "Veuillez le démarrer manuellement.\n", STYLE_SYSTEM);
        }
    }

    private static void testConnection() {
        try {
            getChatbotResponse("test");
            appendToChatArea("Connexion établie. Expert cyclones prêt. Urgence : 118\n\n", STYLE_SYSTEM);
        } catch (IOException e) {
            appendToChatArea("Tentative de démarrage d'Ollama...\n", STYLE_SYSTEM);
            startOllama();
            // Réessayer la connexion après le démarrage
            try {
                Thread.sleep(3000);
                getChatbotResponse("test");
                appendToChatArea("Connexion établie. Expert cyclones prêt. Urgence : 118\n\n", STYLE_SYSTEM);
            } catch (IOException | InterruptedException ex) {
                appendToChatArea("Erreur de connexion à Ollama.\n" +
                        "1. Vérifiez l'installation (ollama.ai)\n" +
                        "2. Lancez Ollama manuellement\n" +
                        "3. Installez llama3.2\n", STYLE_SYSTEM);
            }
        }
    }

    private static void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty())
            return;

        appendToChatArea("Vous: " + message + "\n", STYLE_USER);
        inputField.setText("");
        sendButton.setEnabled(false);
        inputField.setEnabled(false);
        loadingLabel.setVisible(true);

        new Thread(() -> {
            try {
                String response = getChatbotResponse(message);
                SwingUtilities.invokeLater(() -> {
                    loadingLabel.setVisible(false);
                    appendToChatArea("\nAssistant: ", STYLE_ASSISTANT);
                    appendToChatArea(response + "\n\n", STYLE_ASSISTANT);
                    sendButton.setEnabled(true);
                    inputField.setEnabled(true);
                    inputField.requestFocus();
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    loadingLabel.setVisible(false);
                    appendToChatArea("Erreur: " + e.getMessage() + "\n", STYLE_SYSTEM);
                    sendButton.setEnabled(true);
                    inputField.setEnabled(true);
                });
            }
        }).start();
    }

    private static void appendToChatArea(String message, String style) {
        StyledDocument doc = chatArea.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), message, chatArea.getStyle(style));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        chatArea.setCaretPosition(doc.getLength());
    }

    private static String getChatbotResponse(String userInput) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        // Ajouter l'entrée de l'utilisateur à l'historique
        conversationHistory.append("Humain: ").append(userInput).append("\n\n");

        JSONObject json = new JSONObject();
        json.put("model", "llama3.2");
        json.put("stream", false);

        // Paramètres du modèle
        json.put("temperature", 0.1);
        json.put("top_p", 0.8);
        json.put("repeat_penalty", 1.1);
        json.put("num_ctx", 2048);
        json.put("num_thread", 4);

        // Instructions et contexte
        json.put("prompt",
                "Instructions pour l'assistant :\n" +
                        "Tu es un assistant spécialisé dans les cyclones qui aide à protéger la population.\n" +
                        "RÈGLES OBLIGATOIRES :\n" +
                        "1. Réponds UNIQUEMENT en français\n" +
                        "2. Reste TOUJOURS focalisé sur les cyclones et la météo\n" +
                        "3. Sois TRÈS bref et direct dans tes réponses\n" +
                        "4. Ne mentionne le 118 QUE si la situation est dangereuse\n" +
                        "5. Pour un simple 'bonjour', réponds uniquement 'Bonjour, comment puis-je vous aider ?'\n" +
                        "6. Donne des détails UNIQUEMENT si on te pose une question précise\n\n" +
                        conversationHistory.toString() +
                        "Assistant: ");

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Réponse inattendue: " + response);
            }

            JSONObject responseObject = new JSONObject(response.body().string());
            String assistantResponse = responseObject.getString("response");

            // Ajouter la réponse à l'historique
            conversationHistory.append("Assistant: ").append(assistantResponse).append("\n\n");

            return assistantResponse;
        }
    }

    private static void showAboutDialog() {
        JDialog aboutDialog = new JDialog(frame, "À propos", true);
        aboutDialog.setLayout(new BorderLayout());
        aboutDialog.setBackground(BACKGROUND_COLOR);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Chatbot Cyclone - Votre assistant sécurité");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea infoArea = new JTextArea(
                "• Posez vos questions sur les cyclones\n" +
                        "• Obtenez des conseils de sécurité\n" +
                        "• En cas d'urgence, appelez le 118\n\n" +
                        "Développé pour la sécurité de tous.");
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoArea.setBackground(BACKGROUND_COLOR);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(infoArea);

        JButton closeButton = new JButton("Fermer") {
            {
                setContentAreaFilled(false);
                setOpaque(true);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else if (getModel().isRollover()) {
                    g.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g.setColor(PRIMARY_COLOR);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        closeButton.setForeground(BUTTON_TEXT_COLOR);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> aboutDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(closeButton);

        aboutDialog.add(contentPanel, BorderLayout.CENTER);
        aboutDialog.add(buttonPanel, BorderLayout.SOUTH);
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(frame);
        aboutDialog.setVisible(true);
    }
}

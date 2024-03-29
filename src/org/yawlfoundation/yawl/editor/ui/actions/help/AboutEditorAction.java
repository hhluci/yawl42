/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.actions.help;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.BuildProperties;
import org.yawlfoundation.yawl.editor.ui.util.ResourceLoader;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;


public class AboutEditorAction extends YAWLBaseAction {


    {
        putValue(Action.SHORT_DESCRIPTION, "About the YAWL Editor");
        putValue(Action.NAME, "About...");
        putValue(Action.LONG_DESCRIPTION, "About the YAWL Editor");
        putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
    }

    public void actionPerformed(ActionEvent event) {
        new AboutEditorDialog().setVisible(true);
    }
}

class AboutEditorDialog extends JDialog {

    private static final Color BACK_COLOUR = new Color(254,254,240);
    private static final String FORUM_URL = "http://yawlfoundation.org/forum";
    private static final String ISSUES_URL = "https://github.com/yawlfoundation/yawl/issues";


    public AboutEditorDialog() {
        super(YAWLEditor.getInstance());
        setUndecorated(true);
        //setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setContentPane(getAboutPanel());
        setSize(430, 200);
        addKeyListener(this);
        addClickOutsideListener();
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }

    private JPanel getAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY),
                new EmptyBorder(7,7,7,7)));
        panel.setBackground(BACK_COLOUR);
        panel.add(getLogoPanel(), BorderLayout.WEST);
        panel.add(getTextPanel(), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getLogoPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setBackground(BACK_COLOUR);
        JLabel logo = new JLabel(ResourceLoader.getIcon("yawlEditorLogo.png"));
        panel.add(logo);
        addMouseListener(logo);
        addMouseListener(panel);
        return panel;
    }

    private JPanel getTextPanel() {
        JPanel panel = new JPanel(new GridLayout(0,1,3,3));
        panel.setBorder(new EmptyBorder(5,10,20,5));
        panel.setBackground(BACK_COLOUR);

        JLabel nameLabel = new JLabel("The YAWL Process Editor");
        Font font = nameLabel.getFont();
        font = font.deriveFont(Font.BOLD);
        nameLabel.setFont(font.deriveFont(18f));
        nameLabel.setForeground(Color.DARK_GRAY);
        addMouseListener(nameLabel);
        panel.add(nameLabel);

        BuildProperties buildProperties = new BuildProperties();

        JLabel versionLabel = new JLabel(getVersionText(buildProperties));
        versionLabel.setForeground(Color.DARK_GRAY);
        addMouseListener(versionLabel);
        panel.add(versionLabel);

        JLabel buildLabel = new JLabel(getBuildDateText(buildProperties));
        buildLabel.setForeground(Color.DARK_GRAY);
        addMouseListener(buildLabel);
        panel.add(buildLabel);

        JLabel copyLabel = new JLabel(getCopyrightText(buildProperties));
        copyLabel.setForeground(Color.DARK_GRAY);
        addMouseListener(copyLabel);
        panel.add(copyLabel);

        panel.add(createLink("Ask a question on the YAWL Forum", FORUM_URL));
        panel.add(createLink("Report a bug to the YAWL Issues List", ISSUES_URL));

        addMouseListener(panel);
        return panel;
    }


    private String getVersionText(BuildProperties buildProperties) {
        String version = buildProperties.getVersion();
        String buildNumber = buildProperties.getBuild();
        String result = "Version " + (version != null ? version : YAWLEditor.DEFAULT_VERSION);
        if (buildNumber != null) {
            result += " (build " + buildNumber + ")";
        }
        return result;
    }


    private String getBuildDateText(BuildProperties buildProperties) {
        String buildDate = buildProperties.getTimestamp();
        return "Build Date: " + (buildDate != null ? buildDate : "");
    }


    private String getCopyrightText(BuildProperties buildProperties) {
        String buildDate = buildProperties.getTimestamp();
        String year = buildDate != null ? buildDate.substring(0,4) : getDefaultYear();
        return "\u00a9 " + year + " The YAWL Foundation";
    }


    private JButton createLink(String text, final String url) {
        JButton button = new JButton();
        button.setText(showAsLink(text));
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    openWebPage(url);
                }
                catch (Exception e) {
                    MessageDialog.error(e.getMessage(), "Error browsing to page");
                }
            }
        });
        return button;
    }


    private String showAsLink(String text) {
        StringBuilder s = new StringBuilder(50);
        s.append("<HTML><FONT color=\"#000099\"><U>");
        s.append(text);
        s.append("</U></FONT></HTML >");
        return s.toString();
    }


    private void openWebPage(String url) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
                return;
            }
        }
        throw new IOException("Cannot launch browser from editor on this platform");
    }


    private String getDefaultYear() {
        return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }

    private void addMouseListener(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                setVisible(false);
            }
        });
    }


    private void addKeyListener(Component c) {
        c.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                setVisible(false);
            }
        });
    }


    private void addClickOutsideListener() {
        final AboutEditorDialog dialog = this;
        dialog.addWindowFocusListener(new WindowFocusListener() {

            public void windowGainedFocus(WindowEvent e) {
                //do nothing
            }

            public void windowLostFocus(WindowEvent e) {
                dialog.setVisible(false);
            }
        });
    }
}


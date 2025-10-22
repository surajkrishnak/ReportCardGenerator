import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;

/**
 * A Java Swing application to create, view, and manage student report cards.
 * Data is stored in a MySQL database.
 * @author Suraj Krishna K
 */
public class ReportCardGenerator extends JFrame implements ActionListener {

    // --- GUI Components ---
    JTextField tfRollNo, tfName, tfFatherName, tfMotherName;
    JTextField tfMaths, tfComputerScience, tfPhysics, tfChemistry, tfGeography, tfHistory, tfHindi;
    JButton generateButton, clearButton, viewButton;

    // --- Constructor ---
    public ReportCardGenerator() {
        setTitle("Student Report Card Generator - By Suraj Krishna K");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- UI Initialization ---
        JLabel heading = new JLabel("Enter Student Details and Marks");
        heading.setBounds(0, 20, 900, 30);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        add(heading);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        // --- Student Details Section ---
        add(createLabel("Roll Number", 50, 80, labelFont));
        tfRollNo = createTextField(200, 80, fieldFont); add(tfRollNo);

        add(createLabel("Name", 50, 130, labelFont));
        tfName = createTextField(200, 130, fieldFont); add(tfName);

        add(createLabel("Father's Name", 50, 180, labelFont));
        tfFatherName = createTextField(200, 180, fieldFont); add(tfFatherName);

        add(createLabel("Mother's Name", 50, 230, labelFont));
        tfMotherName = createTextField(200, 230, fieldFont); add(tfMotherName);

        // --- Subject Marks Section ---
        String[] subjects = {"Mathematics", "Computer Science", "Physics", "Chemistry", "Geography", "History", "Hindi"};
        for (int i = 0; i < subjects.length; i++) {
            add(createLabel(subjects[i], 450, 80 + (i * 50), labelFont));
        }
        
        tfMaths = createTextField(620, 80, fieldFont); add(tfMaths);
        tfComputerScience = createTextField(620, 130, fieldFont); add(tfComputerScience);
        tfPhysics = createTextField(620, 180, fieldFont); add(tfPhysics);
        tfChemistry = createTextField(620, 230, fieldFont); add(tfChemistry);
        tfGeography = createTextField(620, 280, fieldFont); add(tfGeography);
        tfHistory = createTextField(620, 330, fieldFont); add(tfHistory);
        tfHindi = createTextField(620, 380, fieldFont); add(tfHindi);

        // --- Action Buttons ---
        generateButton = createButton("Generate Report", 200, 470, 180, 45, new Color(0, 123, 255)); add(generateButton);
        clearButton = createButton("Clear", 400, 470, 120, 45, new Color(108, 117, 125)); add(clearButton);
        viewButton = createButton("View All Reports", 540, 470, 180, 45, new Color(40, 167, 69)); add(viewButton);

        setVisible(true);
    }

    // Helper methods to create UI components cleanly
    private JLabel createLabel(String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 170, 25);
        label.setFont(font);
        return label;
    }

    private JTextField createTextField(int x, int y, Font font) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, 200, 30);
        textField.setFont(font);
        return textField;
    }
    
    private JButton createButton(String text, int x, int y, int w, int h, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, w, h);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) handleGenerate();
        else if (e.getSource() == clearButton) handleClear();
        else if (e.getSource() == viewButton) viewAllReports();
    }

    private void handleGenerate() {
        try {
            // Validate all inputs before proceeding
            String rollNo = tfRollNo.getText();
            String name = tfName.getText();
            String fatherName = tfFatherName.getText();
            String motherName = tfMotherName.getText();
            
            if (rollNo.isEmpty() || name.isEmpty() || fatherName.isEmpty() || motherName.isEmpty()) {
                throw new Exception("All student details fields are required.");
            }

            int[] marks = {
                parseMark(tfMaths.getText(), "Mathematics"),
                parseMark(tfComputerScience.getText(), "Computer Science"),
                parseMark(tfPhysics.getText(), "Physics"),
                parseMark(tfChemistry.getText(), "Chemistry"),
                parseMark(tfGeography.getText(), "Geography"),
                parseMark(tfHistory.getText(), "History"),
                parseMark(tfHindi.getText(), "Hindi")
            };

            // Calculate total, percentage, and grade
            int total = 0;
            for (int m : marks) total += m;
            double percentage = total / 7.0;

            String grade, result;
            boolean passed = true;
            for (int m : marks) if (m < 33) { passed = false; break; }
            
            if (percentage >= 33 && passed) {
                result = "PASS";
                if (percentage >= 90) grade = "A+";
                else if (percentage >= 80) grade = "A";
                else if (percentage >= 70) grade = "B+";
                else if (percentage >= 60) grade = "B";
                else if (percentage >= 50) grade = "C";
                else grade = "D";
            } else {
                result = "FAIL";
                grade = "F";
            }

            // Save the data to the database
            saveToDatabase(rollNo, name, fatherName, motherName, marks, total, percentage, grade, result);
            
            // Show the generated report card in a new window
            displayReportCard(rollNo, name, fatherName, motherName, marks, total, percentage, grade, result);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseMark(String markText, String subjectName) throws Exception {
        if (markText.isEmpty()) throw new Exception("Mark for " + subjectName + " cannot be empty.");
        try {
            int mark = Integer.parseInt(markText);
            if (mark < 0 || mark > 100) throw new Exception("Mark for " + subjectName + " must be between 0 and 100.");
            return mark;
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number format for " + subjectName + "'s mark.");
        }
    }

    private void saveToDatabase(String rollNo, String name, String fatherName, String motherName, int[] marks, int total, double percentage, String grade, String result) {
        // This SQL query will INSERT a new record, or UPDATE the existing record if the roll_no already exists.
        String sql = "INSERT INTO report_cards VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE student_name=?, father_name=?, mother_name=?, " +
                     "maths=?, computer_science=?, physics=?, chemistry=?, geography=?, history=?, hindi=?, " +
                     "total_marks=?, percentage=?, grade=?, result=?";

        // 'try-with-resources' ensures the database connection is always closed automatically.
        // This line now calls the separate DatabaseConnection class.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Parameters for the INSERT part
            ps.setString(1, rollNo); ps.setString(2, name); ps.setString(3, fatherName); ps.setString(4, motherName);
            for (int i = 0; i < marks.length; i++) ps.setInt(5 + i, marks[i]);
            ps.setInt(12, total); ps.setDouble(13, percentage); ps.setString(14, grade); ps.setString(15, result);

            // Parameters for the UPDATE part
            ps.setString(16, name); ps.setString(17, fatherName); ps.setString(18, motherName);
            for (int i = 0; i < marks.length; i++) ps.setInt(19 + i, marks[i]);
            ps.setInt(26, total); ps.setDouble(27, percentage); ps.setString(28, grade); ps.setString(29, result);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Report for Roll No '" + rollNo + "' saved/updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleClear() {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all fields?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            tfRollNo.setText(""); tfName.setText(""); tfFatherName.setText(""); tfMotherName.setText("");
            tfMaths.setText(""); tfComputerScience.setText(""); tfPhysics.setText(""); tfChemistry.setText("");
            tfGeography.setText(""); tfHistory.setText(""); tfHindi.setText("");
        }
    }

    private void displayReportCard(String rollNo, String name, String fatherName, String motherName, int[] marks, int total, double percentage, String grade, String result) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("\tSTUDENT REPORT CARD\n");
        sb.append("=========================================\n\n");
        sb.append(" Roll No: ").append(rollNo).append("\n");
        sb.append(" Name:    ").append(name).append("\n");
        sb.append(" Father:  ").append(fatherName).append("\n");
        sb.append(" Mother:  ").append(motherName).append("\n\n");
        sb.append("-----------------------------------------\n");
        sb.append(String.format(" %-20s : %s\n", "SUBJECT", "MARKS"));
        sb.append("-----------------------------------------\n");
        String[] subjects = {"Mathematics", "Computer Science", "Physics", "Chemistry", "Geography", "History", "Hindi"};
        for (int i = 0; i < subjects.length; i++) sb.append(String.format(" %-20s : %d\n", subjects[i], marks[i]));
        sb.append("=========================================\n");
        sb.append("\n Total Marks: ").append(total).append(" / 700");
        sb.append("\n Percentage:  ").append(String.format("%.2f%%", percentage));
        sb.append("\n Grade:       ").append(grade);
        sb.append("\n Result:      ").append(result).append("\n");
        sb.append("=========================================\n");

        JTextArea reportArea = new JTextArea(sb.toString());
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setEditable(false);

        // --- Add Print and Save buttons to the report card window ---
        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> {
            try { reportArea.print(); } 
            catch (PrinterException ex) { JOptionPane.showMessageDialog(null, "Could not print the report: " + ex.getMessage()); }
        });
        
        JButton saveButton = new JButton("Save to Text File");
        saveButton.addActionListener(e -> {
            try (FileWriter writer = new FileWriter(name + "_" + rollNo + "_Report.txt")) {
                writer.write(reportArea.getText());
                JOptionPane.showMessageDialog(null, "✅ Report saved to text file successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error saving file: " + ex.getMessage());
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(printButton);
        buttonPanel.add(saveButton);

        // --- Show the report in a proper dialog window ---
        JDialog dialog = new JDialog(this, "Report Card - " + name, true);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void viewAllReports() {
        // This line now calls the separate DatabaseConnection class.
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM report_cards")) {

            JTable table = new JTable(buildTableModel(rs));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Good for many columns
            
            JDialog dialog = new JDialog(this, "All Saved Report Cards", true);
            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(this);
            dialog.add(new JScrollPane(table));
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error displaying reports: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i).replace("_", " ").toUpperCase());
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int i = 1; i <= columnCount; i++) vector.add(rs.getObject(i));
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
    
    public static void main(String[] args) {
        // Swing applications should be run on the Event Dispatch Thread for safety.
        SwingUtilities.invokeLater(ReportCardGenerator::new);
    }
}


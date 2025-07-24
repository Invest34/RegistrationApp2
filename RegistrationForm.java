import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RegistrationForm extends JFrame {

    // GUI Components
    JTextField nameField, mobileField;
    JRadioButton maleBtn, femaleBtn;
    JComboBox<String> dayBox, monthBox, yearBox;
    JTextArea addressArea;
    JCheckBox termsCheck;
    JButton submitBtn, resetBtn;
    JTable dataTable;
    DefaultTableModel tableModel;

    public RegistrationForm() {
        setTitle("Registration Form");
        setLayout(new BorderLayout());
        setSize(900, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Left Panel - Form
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("User Information"));

        nameField = new JTextField();
        mobileField = new JTextField();
        maleBtn = new JRadioButton("Male");
        femaleBtn = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleBtn);
        genderGroup.add(femaleBtn);

        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) days[i - 1] = String.valueOf(i);
        dayBox = new JComboBox<>(days);

        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        monthBox = new JComboBox<>(months);

        String[] years = new String[100];
        for (int i = 0; i < 100; i++) years[i] = String.valueOf(2024 - i);
        yearBox = new JComboBox<>(years);

        addressArea = new JTextArea(3, 20);
        termsCheck = new JCheckBox("Accept Terms and Conditions");

        submitBtn = new JButton("Submit");
        resetBtn = new JButton("Reset");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Mobile:"));
        formPanel.add(mobileField);
        formPanel.add(new JLabel("Gender:"));

        JPanel genderPanel = new JPanel();
        genderPanel.add(maleBtn);
        genderPanel.add(femaleBtn);
        formPanel.add(genderPanel);

        formPanel.add(new JLabel("DOB:"));

        JPanel dobPanel = new JPanel();
        dobPanel.add(dayBox);
        dobPanel.add(monthBox);
        dobPanel.add(yearBox);
        formPanel.add(dobPanel);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(new JScrollPane(addressArea));

        formPanel.add(termsCheck);
        formPanel.add(new JLabel()); // spacer

        formPanel.add(submitBtn);
        formPanel.add(resetBtn);

        // Right Panel - Table
        String[] columns = { "ID", "Name", "Mobile", "Gender", "DOB", "Address", "Agreed" };
        tableModel = new DefaultTableModel(columns, 0);
        dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Registered Users"));

        // Add both panels to main frame
        add(formPanel, BorderLayout.WEST);
        add(tableScrollPane, BorderLayout.CENTER);

        // Button Actions
        submitBtn.addActionListener(e -> insertData());
        resetBtn.addActionListener(e -> resetForm());

        // Load data on start
        fetchData();

        setVisible(true);
    }

    private void insertData() {
        String name = nameField.getText();
        String mobile = mobileField.getText();
        String gender = maleBtn.isSelected() ? "Male" : (femaleBtn.isSelected() ? "Female" : "");
        String dob = yearBox.getSelectedItem() + "-" +
                     (monthBox.getSelectedIndex() + 1) + "-" +
                     dayBox.getSelectedItem();
        String address = addressArea.getText();
        boolean agreed = termsCheck.isSelected();

        if (name.isEmpty() || mobile.isEmpty() || gender.isEmpty() || address.isEmpty() || !agreed) {
            JOptionPane.showMessageDialog(this, "Please complete all fields and accept terms.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (name, mobile, gender, dob, address, agreed) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, mobile);
            ps.setString(3, gender);
            ps.setString(4, dob);
            ps.setString(5, address);
            ps.setBoolean(6, agreed);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User registered successfully!");
            fetchData();
            resetForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void resetForm() {
        nameField.setText("");
        mobileField.setText("");
        genderGroup.clearSelection();
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        addressArea.setText("");
        termsCheck.setSelected(false);
    }

    private void fetchData() {
        tableModel.setRowCount(0); // Clear old data
        try (Connection con = DBConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("mobile"),
                    rs.getString("gender"),
                    rs.getString("dob"),
                    rs.getString("address"),
                    rs.getBoolean("agreed")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ButtonGroup genderGroup = new ButtonGroup();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistrationForm::new);
    }
}
